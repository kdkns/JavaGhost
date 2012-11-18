package com.java.ghost.Exceptions;

public class GhostNullObjectException extends GhostRuntimeException{
	private final static String  CONST_MESSAGE_HEADER = "Null Object : ";
	private static final long serialVersionUID = 1L;
	
	public GhostNullObjectException(){
		super("Object is null");
	}
	
	@SuppressWarnings("static-access")
	public GhostNullObjectException(String message){
		super(CONST_MESSAGE_HEADER + message);
		super._message = super.CONST_MESSAGE_HEADER + CONST_MESSAGE_HEADER + message;
    }
}
