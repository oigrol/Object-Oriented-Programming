package com.weather.report.repositories;

import com.weather.report.model.entities.Threshold;

public class ThresholdRepository extends CRUDRepository<Threshold, Long>{

      public ThresholdRepository() {
        super(Threshold.class);
  }

}
