package com.weather.report.reports;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.weather.report.exceptions.InvalidInputDataException;
import com.weather.report.model.entities.Measurement;
import com.weather.report.utils.LocalDateTimeUtils;

public class NetworkReportImplementation implements NetworkReport{

    private String code;                                      // il codice passato in input
    private String startDate;                                 // valore ricevuto (anche nullo!)
    private String endDate;                                   // valore ricevuto (anche nullo!)
    private long numberOfMeasurements;                        // numero totale di misurazioni della rete nell’intervallo
    private Collection<String> mostActiveGateways;            // gateway con il maggior numero di misurazioni
    private Collection<String> leastActiveGateways;           // gateway con il minor numero di misurazioni
    private Map<String, Double> gatewaysLoadRatio;            // (Double) ratio è la percentuale di misurazioni generate dal singolo gateway rispetto al totale della rete
    private SortedMap<Range<LocalDateTime>, Long> histogram;  // vedi commento sotto
    /* 
    La mappa:
        raggruppa le misurazioni della rete in sotto-intervalli temporali consecutivi (bucket), 
        la cui granularità può essere oraria o giornaliera a seconda della durata dell’intervallo richiesto o, 
        se assente, dell’intervallo effettivo delle misurazioni disponibili.
        Ogni chiave Range<LocalDateTime> contiene gli istanti esatti di inizio e fine del bucket e la relativa unità (HOUR oppure DAY).
        I bucket seguono la convenzione globale sugli istogrammi: 
            sono chiusi a sinistra e aperti a destra [start, end), 
            ad eccezione dell’ultimo bucket che è [start, end] in modo che il timestamp massimo risulti incluso.
        Il valore associato è il numero di misurazioni i cui timestamp ricadono nel bucket secondo questa convenzione 
            (start ≤ t < end, oppure start ≤ t ≤ end per l’ultimo bucket). 
        L'istogramma è rappresentato da una SortedMap: 
        i bucket coprono interamente l’intervallo considerato e sono restituiti in ordine crescente rispetto al loro istante di inizio.
    */

    public NetworkReportImplementation(String code, String startDate, String endDate, List<Measurement> measurements) throws InvalidInputDataException{
        this.code = code;
        this.startDate = startDate;
        this.endDate = endDate;
        mostActiveGateways = new ArrayList<String>();
        leastActiveGateways = new ArrayList<String>();
        gatewaysLoadRatio = new HashMap<String, Double>();
        histogram = new TreeMap<Range<LocalDateTime>, Long>();
        if(measurements == null || measurements.isEmpty()){
            numberOfMeasurements = 0L;            
            return;
        }     
        this.numberOfMeasurements = measurements.size();

        // mappo per ottenere <codice del gateway, numero di misurazioni del gateway>
        Map<String, Long> measurementCountPerGateway = measurements.stream().collect(Collectors.groupingBy(Measurement::getGatewayCode, Collectors.counting()));
        // calcolo massimo e minimo di misurazioni tra i gateway
        Long maxNumberOfMeasurementsOfAllGateways = measurementCountPerGateway.values().stream().max(Long::compareTo).orElse(Long.valueOf(0));
        Long minNumberOfMeasurementsOfAllGateways = measurementCountPerGateway.values().stream().min(Long::compareTo).orElse(Long.valueOf(0));
        // inserisco i dati nei valori che manderò in output
        measurementCountPerGateway.forEach((gateway, count) -> {
            if(count == maxNumberOfMeasurementsOfAllGateways){mostActiveGateways.add(gateway);}
            if(count == minNumberOfMeasurementsOfAllGateways){leastActiveGateways.add(gateway);}
            gatewaysLoadRatio.put(gateway, (count / (double)numberOfMeasurements));
        });
        // ottengo istogramma
        // calcolo limiti temporali
        LocalDateTime startOfHistogram = LocalDateTimeUtils.parseLocalDateTime(startDate, measurements.stream().map(Measurement::getTimestamp).min(LocalDateTime::compareTo).orElse(LocalDateTime.MIN));
        LocalDateTime endOfHistogram = LocalDateTimeUtils.parseLocalDateTime(endDate, measurements.stream().map(Measurement::getTimestamp).max(LocalDateTime::compareTo).orElse(LocalDateTime.MAX));

        // calcolo il range temporale totale dell'istogramma e scelgo la granularità
        long totalDurationInSeconds = Duration.between(startOfHistogram, endOfHistogram).toSeconds();
        long granularityTreshold = (48 * 60 * 60);
        ChronoUnit histogramGranularity = (totalDurationInSeconds > granularityTreshold) ? ChronoUnit.DAYS : ChronoUnit.HOURS;
        // itero su currentBucketStart muovendolo di una granularità alla volta
        LocalDateTime currentBucketStart = startOfHistogram;
        while(currentBucketStart.isBefore(endOfHistogram) || currentBucketStart.equals(endOfHistogram)){            
            LocalDateTime nextStep = currentBucketStart.plus(1, histogramGranularity);
            // controllo di non sforare oltre al massimo
            boolean isLastBucket = !nextStep.isBefore(endOfHistogram);
            LocalDateTime currentBucketEnd = isLastBucket ? endOfHistogram : nextStep;
            if (currentBucketEnd.equals(currentBucketStart) && !isLastBucket) {currentBucketEnd = nextStep;} // evito problemi in caso intervallo con un singolo punto
            RangeImplementation<LocalDateTime> range = new RangeImplementation<LocalDateTime>(currentBucketStart,currentBucketEnd,isLastBucket);
            long count = measurements.stream().filter(m -> range.contains(m.getTimestamp())).count();
            histogram.put(range, count);
            currentBucketStart = nextStep;
            if (isLastBucket || currentBucketStart.isAfter(endOfHistogram)) {break;}
        }
    }

    /**
     * Unique code of the reported element.
     *
     * @return element code
     */
    @Override
    public String getCode() {return code;}

    /**
     * Lower bound of the interval (inclusive), or {@code null} when no bound was
     * provided.
     *
     * @return start date string in {@code WeatherReport.DATE_FORMAT}, or
     *         {@code null}
     */
    @Override
    public String getStartDate() {return startDate;}

    /**
     * Upper bound of the interval (inclusive), or {@code null} when no bound was
     * provided.
     *
     * @return end date string in {@code WeatherReport.DATE_FORMAT}, or {@code null}
     */
    @Override
    public String getEndDate() {return endDate;}

    /**
     * Total measurements considered for the report within the requested interval.
     *
     * @return number of measurements
     */
    @Override
    public long getNumberOfMeasurements() {return numberOfMeasurements;}

    /**
     * Gateways with the highest number of measurements for the network.
     *
     * @return collection of gateway codes (ties are all included, order
     *         unspecified)
     */
    @Override
    public Collection<String> getMostActiveGateways() {return mostActiveGateways;}

    /**
     * Gateways with the lowest number of measurements for the network.
     *
     * @return collection of gateway codes (ties are all included, order
     *         unspecified)
     */
    @Override
    public Collection<String> getLeastActiveGateways() {return leastActiveGateways;}

    /**
     * Ratio between measurements of each gateway and the total measurements of the
     * network, expressed as a percentage.
     *
     * @return map {@code <gatewayCode, ratio>}
     */
    @Override
    public Map<String, Double> getGatewaysLoadRatio() {return gatewaysLoadRatio;}

    /**
     * Returns the number of measurements included in this network report,
     * grouped into consecutive time buckets.
     *
     * The method only considers measurements that belong to the current Network
     * and that fall within an effective interval [effectiveStart, effectiveEnd],
     * defined as follows:
     *
     * - if startDate is non-null, effectiveStart is the parsed value of startDate;
     * otherwise, effectiveStart is the timestamp of the earliest measurement
     * available for this Network (or null if no measurements exist);
     * - if endDate is non-null, effectiveEnd is the parsed value of endDate;
     * otherwise, effectiveEnd is the timestamp of the latest measurement
     * available for this Network (or null if no measurements exist).
     *
     * If either effectiveStart or effectiveEnd is null (i.e. there are no
     * measurements for this Network), the method returns an empty map.
     *
     * Once [effectiveStart, effectiveEnd] has been determined, the bucket
     * granularity is selected as follows:
     *
     * - if both effectiveStart and effectiveEnd are non-null and the duration
     * between them is less than or equal to 48 hours, the interval is
     * partitioned into hourly buckets;
     * - in all other cases, the interval is partitioned into daily buckets.
     *
     * Buckets always cover sub-intervals of [effectiveStart, effectiveEnd] and
     * are built by intersecting the logical hour/day units with the effective
     * interval:
     *
     * - for hourly buckets, each bucket normally covers a full hour
     * (yyyy-MM-dd HH:00:00 -> yyyy-MM-dd HH:59:59), except for the first and the
     * last bucket, whose bounds are truncated to effectiveStart and effectiveEnd
     * respectively;
     *
     * - for daily buckets, each bucket normally covers a full calendar day
     * (yyyy-MM-dd 00:00:00 -> yyyy-MM-dd 23:59:59), except for the first and the
     * last bucket, whose bounds are truncated to effectiveStart and effectiveEnd
     * respectively.
     *
     * Each entry of the returned map represents a single time bucket:
     *
     * - the key is a {@code Range<LocalDateTime>} instance that stores the exact
     * start and end instants of the bucket together with its logical unit
     * (HOUR or DAY);
     *
     * - the value is the total number of measurements whose timestamp falls
     * into that bucket according to the histogram convention:
     * all buckets except the last one are left-closed and right-open
     * [start, end), while the last bucket is [start, end] so that the
     * maximum timestamp is included.
     *
     * Buckets are contiguous and fully cover the [effectiveStart, effectiveEnd]
     * interval. The first and the last bucket may represent only partial hours
     * or days, depending on the actual effectiveStart / effectiveEnd values.
     *
     * The returned {@code SortedMap} is sorted by ascending bucket start time.
     *
     * @return a sorted map where each key is a {@code Range<LocalDateTime>}
     *         describing a time bucket and each value is the number of measurements
     *         in that bucket
     */
    @Override
    public SortedMap<Range<LocalDateTime>, Long> getHistogram() {return histogram;}

}
