package com.java.ghost.utils;

import java.util.HashMap;


public class GhostCRUD {
//	private static Logger logger = Logger.getLogger("GhostCRUD");
	private HashMap<GhostCRUDOperationType, String> _hm = new HashMap<GhostCRUDOperationType, String>();
	
	public GhostCRUD(){}
	
//	public GhostCRUD(HashMap hm){
//		_hm = hm; 
//	}
	
//	protected HashMap<SQLOperationType, String> getHashMap(){
//		return _hm;
//	}
	
	public void mergeCRUD(GhostCRUD gCRUD){
		_hm.putAll(gCRUD._hm); 
	}
	
	public void put(GhostCRUDOperationType sqlType, String str){
//		logger.debug("SqlType:" + GhostVariableWrapper.wrapVariable(sqlType) + "Statment:" + GhostVariableWrapper.wrapVariable(str));
		_hm.put(sqlType, str);
	}
	
	public String get(GhostCRUDOperationType sqlType){
		return (String) _hm.get(sqlType);
	}
	
	public String getSQLStatment(GhostCRUDOperationType sqlType) {
		return (String) _hm.get(sqlType);
	}
}
