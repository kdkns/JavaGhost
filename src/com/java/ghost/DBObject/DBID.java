package com.java.ghost.DBObject;

public class DBID {
	private String _id;

	public DBID(){};
	
	public DBID(Object obj){
		_id = obj.toString();
	}
	
	public String geID() {
		return _id;
	}

	public void setId(String id) {
		_id = id;
	}
	
	public String toString(){
		return _id;
	}
}
