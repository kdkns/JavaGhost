package com.ercot.java.ghost.utils;


public interface IGhostDBList<objectType> {
	public String getStringForm(String seperator);
	public String getStringForm(String seperator, String leftWrapper, String rightWrapper);
	public int size();
//	public List<objectType> getListOfObjects();
	public boolean add(objectType obj);
	//public boolean remove(objectType obj);
	public boolean remove(objectType obj);
	
}

