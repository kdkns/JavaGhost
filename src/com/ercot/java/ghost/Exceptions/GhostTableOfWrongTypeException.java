package com.ercot.java.ghost.Exceptions;

public class GhostTableOfWrongTypeException extends GhostRuntimeException {
	private final static String  CONST_MESSAGE_HEADER = "Table object is of wrong type : ";
	private static final long serialVersionUID = 1L;
	
	public GhostTableOfWrongTypeException(){
		super(" Unable to perform operation, check that the correct type of table was passed.");
	}
	
	@SuppressWarnings("static-access")
	public GhostTableOfWrongTypeException(String message){
		super(CONST_MESSAGE_HEADER + message);
		super._message = super.CONST_MESSAGE_HEADER + CONST_MESSAGE_HEADER + message;
    }
}
