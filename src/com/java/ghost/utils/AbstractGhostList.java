package com.java.ghost.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class AbstractGhostList<objectType>  implements IGhostDBList<objectType>{
	private List<objectType> _list = new ArrayList<objectType>();
	
	public AbstractGhostList(objectType... values){
		this.addAll(Arrays.asList(values));
	}
	
	public boolean addMany(objectType... values){
		return this.addAll(Arrays.asList(values));
	}
	
	public boolean removeMany(objectType... values){
		return this.removeAll(Arrays.asList(values));
	}
	
	public String getStringForm(String seperator) {
		StringBuilder result = new StringBuilder();
		for(objectType i : _list){
			result.append(seperator + i.toString());
		}
		return result.substring(1);
	}
	
	public String getStringForm(String seperator, String leftWrapper,String rightWrapper) {
		StringBuilder result = new StringBuilder();
		for(objectType i : _list){
			result.append(seperator + leftWrapper + i.toString() + rightWrapper);
		}
		return result.substring(1);
	}
	
	public int size() {
		return _list.size();
	}
	
	public boolean add(objectType obj) {
		return _list.add(obj);
	}

	public boolean addAll(Collection<? extends objectType>  arg0){
		return _list.addAll(arg0);
	}
	
	public boolean remove(Object obj) {
		return _list.remove(obj);
	}
	
	public boolean removeAll(Collection<? extends objectType> arg0){
		return _list.removeAll(arg0);
	}
	
	public void clear(){
		_list.clear();
	}
	
	

	
	
	
	 

}
