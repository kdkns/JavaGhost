package com.ercot.java.ghost.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class GDate implements IGDate{
	private static final long ONE_SECOND_IN_MILISECONDS = 1000;
	private Calendar _calendar = new GregorianCalendar();

	public GDate(IGDate starttime){
		set(starttime.getMonth(),
		    starttime.getDate(),
		    starttime.getYear(),
		    starttime.getHours(),
		    starttime.getMinutes(),
		    starttime.getSeconds());
	}
	
	public GDate(int month, int day, int year){
		set( month, day, year,0,0,0);
	}
	
	public GDate(int month, int year){
		set( month, year,0,0,0,0);
	}

	public GDate(int year){
		set(year,0,0,0,0,0);
	}
	
	public GDate(int month, int day, int year, int hour, int minutes, int seconds){
		set( month,  day,  year,  hour,  minutes,  seconds);
	}
	
	public GDate(int month, int day, int year, int hour, int minutes){
		set( month,  day,  year,  hour,  minutes, 0);
	}
	
	public GDate(int month, int day, int year, int hour){
		set( month,  day,  year,  hour, 0, 0);
	}
	
	public GDate(Date date) {
		setDate(date);
	}

	public GDate() {
		_calendar = new GregorianCalendar();
	}
	
	
	@Override
	public void set(int month, int day, int year) {
		getCalendar().set(year, month-1, day);
	}

	@Override
	public void set(int month, int year) {
		getCalendar().set(year, month-1, 1);
	}

	@Override
	public void set(int year) {
		getCalendar().set(year, 1, 1);
	}

	@Override
	public void set(int month, int day, int year, int hour, int minutes, int seconds) {
		getCalendar().set(year, month-1, day, hour, minutes, seconds);
	}

	@Override
	public void set(int month, int day, int year, int hour, int minutes) {
		getCalendar().set(year, month-1, day, hour, minutes, 0);
	}

	@Override
	public void set(int month, int day, int year, int hour) {
		getCalendar().set(year, month-1, day, hour, 0, 0);	
	}
	
	

	@Override
	public Date getJavaDate() {
		return getCalendar().getTime();
	}
	
	public long getTimeInMillis(){
		return getCalendar().getTimeInMillis();
	}

	@Override
	public Calendar getCalendar() {
		return _calendar;
	}

	@Override
	public void setDate(Date date) {
		getCalendar().setTime(date);		
	}

	@Override
	public void setCalendar(Calendar calendar) {
		_calendar = calendar;		
	}

	@Override
	public void addYears(int numberOfYears) {
       getCalendar().add(Calendar.YEAR, numberOfYears);		
	}

	@Override
	public void addMonths(int numberOfMonths) {
		getCalendar().add(Calendar.MONTH, numberOfMonths);
		
	}

	@Override
	public void addDays(int numberOfDays) {
		getCalendar().add(Calendar.DATE, numberOfDays);
		
	}

	@Override
	public void addHours(int numberOfHours) {
		getCalendar().add(Calendar.HOUR_OF_DAY, numberOfHours);
		
	}

	@Override
	public void addMinutes(int numberOfMinutes) {
		getCalendar().add(Calendar.MINUTE, numberOfMinutes);
		
	}

	@Override
	public void addSeconds(int numberOfSeconds) {
		getCalendar().add(Calendar.SECOND, numberOfSeconds);
		
	}
	
	@Override
	public String toString(){
		return GhostDBStaticVariables.ghostCalendarDateFormater(getJavaDate());
	}

	public int getMonth() {
		return getCalendar().get(Calendar.MONTH)+1;
	}
	
	public int getDate() {
		return getCalendar().get(Calendar.DATE);
	}
	
	public int getYear() {
		return getCalendar().get(Calendar.YEAR);
	}
	
	public int getHours() {
		return getCalendar().get(Calendar.HOUR_OF_DAY);
	}
	
	public int getMinutes() {
		return getCalendar().get(Calendar.MINUTE);
	}
	
	public int getSeconds() {
		return getCalendar().get(Calendar.SECOND);
	}

	@Override
	public void truncTime() {
		getCalendar().set(Calendar.HOUR_OF_DAY, 0);
		getCalendar().set(Calendar.MINUTE, 0);
		getCalendar().set(Calendar.SECOND, 0);
		getCalendar().set(Calendar.MILLISECOND, 0);
		//getCalendar().set(Calendar.AM_PM, Calendar.AM);
	}
	
	public boolean equals(Object obj){
		if(obj instanceof GDate){
		  GDate date = (GDate) obj;
		  return this.getCalendar().equals(date.getCalendar());
		}
		return false;
	}
	
	public int hashCode(){
		return this.hashCode();//TODO: Is this right? Should be since we want equals to check for time and hash for object
	}
	
	public boolean compareTime(IGDate date){
		//The granularity of time is set to be accurate up to a second.
		//Meaning, the timestamps must match to the second only. Anything less is ignored.
		return Math.abs((this.getCalendar().getTimeInMillis()-date.getCalendar().getTimeInMillis()))<ONE_SECOND_IN_MILISECONDS;
	}
	
}
