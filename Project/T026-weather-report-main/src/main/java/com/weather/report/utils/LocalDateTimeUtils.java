package com.weather.report.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import com.weather.report.WeatherReport;
import com.weather.report.exceptions.InvalidInputDataException;

public class LocalDateTimeUtils {
    
    /**
   * Retrieve an instance of LocalDateTime from a text string using a specific formatter
   * @param date date with string format
   * @param defaultDate if date is null, there is no limit
   * @return date in LocalDateTime format
   * @throws InvalidInputDataException if date is not in correct format "yyyy-MM-dd HH:mm:ss"
   */
  public static LocalDateTime parseLocalDateTime(String date, LocalDateTime defaultDate) throws InvalidInputDataException {
      if (date == null) return defaultDate; //max ~ +inf | min ~ -inf
      try {
          return LocalDateTime.parse(date, WeatherReport.DATE_TIME_FORMATTER);
      } catch (DateTimeParseException e) {
          throw new InvalidInputDataException("Il formato della data deve essere: " + WeatherReport.DATE_TIME_FORMATTER);
      }
  }

}
