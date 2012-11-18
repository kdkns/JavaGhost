package com.ercot.java.ghost.Exceptions;

public class GhostRuntimeException extends RuntimeException{	
	protected String _message;
	protected final static String  CONST_MESSAGE_HEADER = "Ghost Error - ";	
	private static final long serialVersionUID = 1L;
	
	public GhostRuntimeException(Exception e){
		super(e.getMessage(),e);
    }
	
	public GhostRuntimeException(Exception e, String message){
		super(message,e);
		_message = CONST_MESSAGE_HEADER + message;
    }
	
	public GhostRuntimeException(String message){
		super(message);
		_message = CONST_MESSAGE_HEADER + message;
    }
	
}
