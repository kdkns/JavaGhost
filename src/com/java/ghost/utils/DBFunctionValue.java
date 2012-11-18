package com.java.ghost.utils;

public class DBFunctionValue implements IDBFunctionValue{

	private String _statment;
	private GhostDBStaticVariables.DBTypes _type;
	
	public DBFunctionValue(String statment, GhostDBStaticVariables.DBTypes type) {
		_statment = statment;
		_type = type;
	}

	@Override
	public String getStatment() {
		return _statment;
	}

	
	@Override
	public GhostDBStaticVariables.DBTypes getType() {
		return _type;
	}
}
