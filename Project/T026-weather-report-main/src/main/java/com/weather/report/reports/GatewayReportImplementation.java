package com.weather.report.reports;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.weather.report.exceptions.InvalidInputDataException;
import com.weather.report.model.entities.Gateway;
import com.weather.report.model.entities.Measurement;
import com.weather.report.model.entities.Parameter;

public class GatewayReportImplementation implements GatewayReport{
    private String code;
    private String startDate;
    private String endDate;
    private long numberOfMeasurements;
    private Collection<String> mostActiveSensors;
    private Collection<String> leastActiveSensors;
    private Map<String, Double> sensorLoadRatio;
    private Collection<String> outlierSensors;
    private double batteryChargePercentage;
    private SortedMap<Range<Duration>, Long> histogram;

    public GatewayReportImplementation(Gateway gateway, String startDate, String endDate, List<Measurement> measurements) throws InvalidInputDataException {
            this.code = gateway.getCode();
            this.startDate = startDate;
            this.endDate = endDate;
            this.calculateReport(gateway, measurements);
        }

    /**
     * Calculates the report
     * @param gateway gateway on wich the report has to be calculated
     * @param measurements gateway's measurements
     */
    private void calculateReport(Gateway gateway, List<Measurement> measurements) throws InvalidInputDataException {

        
        this.mostActiveSensors = new ArrayList<>();
        this.leastActiveSensors = new ArrayList<>();
        this.sensorLoadRatio = new HashMap<>();
        this.outlierSensors = new ArrayList<>();
        this.histogram = new TreeMap<>();

        Parameter batteryChargePercentageP = gateway.getParameter(Parameter.BATTERY_CHARGE_PERCENTAGE_CODE);
        this.batteryChargePercentage = (batteryChargePercentageP != null) ? batteryChargePercentageP.getValue() : 0.0;

        /* se non ci sono misurazioni ritorno immediatamente liste e mappe vuote */
        if (measurements == null || measurements.isEmpty()) {
            this.numberOfMeasurements = 0;
            return;
        }

        this.numberOfMeasurements = measurements.size(); //numero totale di misurazioni del Gateway nell’intervallo richiesto

        setCollectionOfSensors(measurements, numberOfMeasurements, mostActiveSensors, leastActiveSensors, sensorLoadRatio);

        outlierSensors = getOutlierSensors(measurements, gateway);

        histogram = getHistogram(measurements, numberOfMeasurements);
    }


    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getStartDate() {
        return startDate;
    }

    @Override
    public String getEndDate() {
        return endDate;
    }

    @Override
    public long getNumberOfMeasurements() {
        return numberOfMeasurements;
    }

    @Override
    public Collection<String> getMostActiveSensors() {
        return mostActiveSensors;
    }

    @Override
    public Collection<String> getLeastActiveSensors() {
        return leastActiveSensors;
    }

    @Override
    public Map<String, Double> getSensorsLoadRatio() {
        return sensorLoadRatio;
    }

    @Override
    public Collection<String> getOutlierSensors() {
        return outlierSensors;
    }

    @Override
    public double getBatteryChargePercentage() {
        return batteryChargePercentage;
    }

    @Override
    public SortedMap<Range<Duration>, Long> getHistogram() {
        return histogram;
    }

    /**
     * Analyze sensor data and collect it into related collections according to requests
     * @param measurements gateway's measurement
     * @param numberOfMeasurements number of gateway's measurement
     * @param mostActiveSensors list containing the sensors with the highest number of measurements
     * @param leastActiveSensors list containing the sensors with the least number of measurements
     * @param sensorsLoadRatio map containing the sensors with the relative percentage of measurements taken by the single sensor compared to the total of the gateway
     */
    private void setCollectionOfSensors(List<Measurement> measurements, long numberOfMeasurements, Collection<String> mostActiveSensors, Collection<String> leastActiveSensors, Map<String, Double> sensorsLoadRatio) {
        //ricavo una map che raggruppa le misurazioni per sensore e le conta
        Map<String, Long> countMeasurementsForSensor = measurements.stream()
                .collect(Collectors.groupingBy(Measurement::getSensorCode, Collectors.counting()));

        //ricavo il massimo e il minimo numero di misurazioni per i sensori
        long maxCount = countMeasurementsForSensor.values().stream().max(Long::compareTo).orElse((long)0);
        long minCount = countMeasurementsForSensor.values().stream().min(Long::compareTo).orElse((long)0);

        countMeasurementsForSensor.forEach((sensorCode, measurementCount) -> {
            if (measurementCount == maxCount) mostActiveSensors.add(sensorCode);
            if (measurementCount == minCount) leastActiveSensors.add(sensorCode);
            double ratio = (double) measurementCount / numberOfMeasurements;
            sensorsLoadRatio.put(sensorCode, ratio);
        });
    }

    /**
     * Retrieve a list of sensorCodes whose average detected values ​​are anomalous, comparing the real average with the gateway's expected values
     * @param measurements gateway's measurement
     * @param gateway gateway
     * @return a list of outlier sensors
     */
    private Collection<String> getOutlierSensors(List<Measurement> measurements, Gateway gateway) {

        Parameter expectedMeanP = gateway.getParameter(Parameter.EXPECTED_MEAN_CODE);
        Parameter expectedStdDevP = gateway.getParameter(Parameter.EXPECTED_STD_DEV_CODE);

        if (expectedMeanP!=null && expectedStdDevP!= null) {
            double expectedMean = expectedMeanP.getValue();
            double expectedStdDev = expectedStdDevP.getValue();
            //raggruppo misurazioni per sensore e ne calcolo la media
            Map<String, Double> meanMeasurementsForSensor = measurements.stream()
                .collect(Collectors.groupingBy(Measurement::getSensorCode, Collectors.averagingDouble(Measurement::getValue)));

            meanMeasurementsForSensor.forEach((sensorCode, sensorMean) -> {
                if (checkIfOutlier(expectedMean, expectedStdDev, sensorMean)) outlierSensors.add(sensorCode);
            });
        }

        return outlierSensors;
    }

    /**
     * Check if a sensor code is considered outlier
     * @param expected_mean expected mean
     * @param expected_std_dev expected standard deviation
     * @param sensor_mean average of the values ​​detected by a sensor
     * @return true if the average of the values ​​detected by a sensor is anomalous
     */
    private boolean checkIfOutlier(double expected_mean, double expected_std_dev, double sensor_mean) {
        return Math.abs(sensor_mean - expected_mean) >= 2.0 * expected_std_dev;
    }

    /**
     * Retrieve the histogram of the inter-arrival times between consecutive gateway measurements in the requested interval.
     * @param measurements gateway's measurement
     * @param numberOfMeasurements number of gateway's measurement
     * @return histogram with the duration count for each bucket
     */
    private SortedMap<Range<Duration>, Long> getHistogram(List<Measurement> measurements, long numberOfMeasurements) {
        final int BUCKETS_NUMBER = 20;

        if (numberOfMeasurements < 2) return histogram;

        //ordino le misurazioni in ordine cronologico
        List<Measurement> sortedMeasurements = measurements.stream().sorted(Comparator.comparing(Measurement::getTimestamp)).toList();

        //calcolo tutte le differenze temporali tra misurazioni consecutive
        List<Duration> interArrivalDurations = new ArrayList<>();
        for (int i=0; i<numberOfMeasurements-1; i++) {
            LocalDateTime start = sortedMeasurements.get(i).getTimestamp();
            LocalDateTime end = sortedMeasurements.get(i+1).getTimestamp();
            interArrivalDurations.add(Duration.between(start, end));
        }

        //calcolo i 20 intervalli contigui in cui suddividere il range di Duration
        Duration minDuration = interArrivalDurations.stream().min(Duration::compareTo).orElse(Duration.ZERO);
        Duration maxDuration = interArrivalDurations.stream().max(Duration::compareTo).orElse(Duration.ZERO);
        //caso limite
        if (minDuration.equals(maxDuration)) minDuration = Duration.ZERO; // se min = max -> considero arbitrariamente minDuration uguale a 0 per calcolare i bucket come da specifiche del professore

        Duration range = maxDuration.minus(minDuration);

        if (range.isZero()) range = Duration.ofNanos(BUCKETS_NUMBER); //se anche maxDuration era 0, creo range fittizio da 20 ns

        Duration bucketRange = range.dividedBy(BUCKETS_NUMBER);
        
        if (bucketRange.isZero()) bucketRange = Duration.ofNanos(1); //se range < 20, la divisione intera restituisce 0, imposto ogni bucket da 1 ns per averli forzatamente

        for (int i=0; i<BUCKETS_NUMBER; i++) {
            //creo i 20 bucket impostando start e end di ogni bucket e un flag che dice se è l'ultimo bucket (end coincide con maxDuration)
            Duration start = minDuration.plus(bucketRange.multipliedBy(i)); //min + (bucketRange * i)
            boolean isLast = (i == BUCKETS_NUMBER - 1);
            Duration end = (isLast) ? maxDuration : minDuration.plus(bucketRange.multipliedBy(i+1)); //min + (bucketRange * (i+1)) se non è l'ultimo

            RangeImplementation<Duration> bucket = new RangeImplementation<>(start, end, isLast);

            //ogni bucket è un intervallo dell'istogramm -> count è il numero di duration di quel bucket
            long count = interArrivalDurations.stream().filter(d -> bucket.contains(d)).count();

            histogram.put(bucket, count);
        }
        
        return histogram;
    }

}
