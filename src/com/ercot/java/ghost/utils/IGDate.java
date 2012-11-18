package com.ercot.java.ghost.utils;

import java.util.Calendar;
import java.util.Date;

public interface IGDate {
   public Date getJavaDate();
   public Calendar getCalendar();
   public void setDate(Date date);
   public void setCalendar(Calendar calendar);
   
   public void set(int month, int day, int year);   
   public void set(int month, int year);
   public void set(int year);
   public void set(int month, int day, int year, int hour, int minutes, int seconds);   
   public void set(int month, int day, int year, int hour, int minutes);
   public void set(int month, int day, int year, int hour);
   public long getTimeInMillis();
   
   public void addYears(int numberOfYears);
   public void addMonths(int numberOfMonths);
   public void addDays(int numberOfDays);
   public void addHours(int numberOfHours);
   public void addMinutes(int numberOfMinutes);
   public void addSeconds(int numberOfSeconds);
   
   public int getMonth();
   public int getDate();
   public int getYear();
   public int getHours();
   public int getMinutes();
   public int getSeconds();
   
   public String toString();
   public void truncTime();
   public boolean compareTime(IGDate date);
   
}
