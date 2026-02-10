package com.weather.report.reports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.weather.report.model.entities.Measurement;

public class SensorReportImplementation implements SensorReport{

    private String code;
    private String startDate, endDate;
    private long numberOfMeasurements;
    private double mean;
    private double variance;
    private double stdDev;
    private double minimumMeasuredValue;
    private double maximumMeasuredValue;
    private List<Measurement> outliers;
    private SortedMap<Range<Double>, Long> histogram;

    
    public SensorReportImplementation(String code, String startDate, String endDate, Collection<Measurement> misure) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.code = code;
        calculateVariables(misure);
    }

    private void calculateVariables(Collection<Measurement> measurements){
        final int NBUCKETS = 20;

        this.outliers = new ArrayList<>();
        this.histogram = new TreeMap<>(); 
        if (measurements == null || measurements.isEmpty()) {
            this.mean = 0.0;
            this.variance = 0.0;
            this.stdDev = 0.0;
            this.numberOfMeasurements=0;
            this.minimumMeasuredValue = 0.0;
            this.maximumMeasuredValue = 0.0;
            return;
        }
        this.numberOfMeasurements = measurements.size();
        double sum = 0.0;
        double min = Double.MAX_VALUE;       
        double max = -Double.MAX_VALUE;    
        
        for (Measurement m : measurements) {
            double v = m.getValue(); 
            sum += v;
            if (v < min) min = v;
            if (v > max)  max = v;
        }
        this.minimumMeasuredValue = min;
        this.maximumMeasuredValue = max;
        this.mean = sum / numberOfMeasurements;  

        double sumSquaredDiffs = 0.0;

        for (Measurement m : measurements) {
            double v = m.getValue();
            sumSquaredDiffs += Math.pow(v - this.mean, 2);
        }
        if(numberOfMeasurements<2){
            this.variance=0.0;
            this.stdDev=0.0;
        }else{
            this.variance = sumSquaredDiffs / (numberOfMeasurements-1);
            this.stdDev = Math.sqrt(this.variance);
            for (Measurement m : measurements) {
                if(Math.abs(m.getValue()-this.mean)>=2*this.stdDev) this.outliers.add(m);
            }
            calculateHistogram(measurements, this.outliers,NBUCKETS);
        }

        
    }

    private void calculateHistogram(Collection<Measurement> measurements, List<Measurement> outliers, int nBucket) {
        
        List<Double> validValues=measurements.stream().filter(m->!outliers.contains(m)).map(m->m.getValue()).toList();
       
        if (validValues.isEmpty()) return;

        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        
        for (Double v : validValues) {
            if (v < min) min = v;
            if (v > max) max = v;
        }

        double step = (max-min)/nBucket;

        
        double inizio = min;
        double end=0;
        boolean isLast=false;

        for(int i=0; i<nBucket; i++){
            if(i==nBucket-1){ end = max; isLast=true;}
            else end = inizio+step;
            Range<Double> range = new RangeImplementation<>(inizio, end, isLast);
            long count = validValues.stream().filter(d -> range.contains(d)).count();
            this.histogram.put(range, count);
            inizio=end;
        }
        

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
    public double getMean() {
       return mean;
    }

    @Override
    public double getVariance() {
        return variance;
    }

    @Override
    public double getStdDev() {
        return stdDev;
    }

    @Override
    public double getMinimumMeasuredValue() {
        return minimumMeasuredValue;
    }

    @Override
    public double getMaximumMeasuredValue() {
        return maximumMeasuredValue;
    }

    @Override
    public List<Measurement> getOutliers() {
        return outliers;
    }

    @Override
    public SortedMap<Range<Double>, Long> getHistogram() {
       return histogram;
    }

}
