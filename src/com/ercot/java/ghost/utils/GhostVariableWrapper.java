package com.ercot.java.ghost.utils;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;

import org.apache.log4j.Logger;

@SuppressWarnings("rawtypes")
public class GhostVariableWrapper {
	protected static Logger logger= Logger.getLogger("GhostVariableWrapper");
	private static final String _leftWrap = "<";
	private static final String _rightWrap = ">";
	
	public static String wrapVariable(String str){
		return _leftWrap +  str + _rightWrap;
	}
	
	public static String wrapVariable(IGDate date){
		return _leftWrap +  GhostDBStaticVariables.ghostCalendarDateFormater(date.getJavaDate()) + _rightWrap;
	}
	
	public static String wrapVariable(BigDecimal bd){
		return _leftWrap +  bd + _rightWrap;
	}
	
	public static String wrapVariable(float f){
		return _leftWrap +  f + _rightWrap;
	}
	
	public static String wrapVariable(double d){
		return _leftWrap +  d + _rightWrap;
	}
	
	public static String wrapVariable(int i){
		return _leftWrap +  i + _rightWrap;
	}
	
	public static String wrapVariable(java.lang.Number number){
		return _leftWrap +  number + _rightWrap;
	}
	
	public static String wrapVariable(oracle.sql.NUMBER number){
		return _leftWrap +  number.stringValue() + _rightWrap;
	}
	
	public static String wrapVariable(oracle.sql.CHAR c){
		return _leftWrap +  c + _rightWrap;
	}
	
	public static String wrapVariable(GhostCRUDOperationType s){
		return _leftWrap +  s + _rightWrap;
	}
	
	public static String wrapVariable(byte [] ba){
		StringBuilder result = new StringBuilder(_leftWrap);
		for(int x = 0; x < ba.length; x++){
			result.append(ba[x]);
		}
		return result + _rightWrap;
	}

	public static String wrapVariable(Enum e) {
		return _leftWrap +  e + _rightWrap;
	}

	public static String wrapVariable(boolean b) {
		return _leftWrap +  b + _rightWrap;
	}

	public static String wrapVariable(Class c) {
		return _leftWrap +  c + _rightWrap;
	}

	public static String wrapVariable(Method m) {
		return _leftWrap +  m + _rightWrap;
	}
	
	public static String wrapVariable(Object obj) {
		return _leftWrap + obj.toString() + _rightWrap;
	}

	public static String wrapVariable(Blob b) {
		int length;
		try {//TODO: Will break if bigger than MAX int size, fix
			length = Integer.valueOf(Long.toString(b.length()));
			return wrapVariable(b.getBytes(1, length));
		} catch (NumberFormatException e) {
			logger.error(e.getMessage(),e);
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
		}
		return null;
	}

	
}
