package com.ercot.java.ghost.Exceptions;

public class GhostExceptions extends Exception{
	protected String _message;
	protected final static String  CONST_MESSAGE_HEADER = "Ghost Error - ";	
	private static final long serialVersionUID = 1L;
	
	public GhostExceptions(){
		super();
	}
	
	public GhostExceptions(String message){
		super(message);
		_message = CONST_MESSAGE_HEADER + message;
    }
}
