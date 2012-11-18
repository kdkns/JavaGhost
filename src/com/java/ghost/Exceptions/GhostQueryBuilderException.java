package com.java.ghost.Exceptions;

public class GhostQueryBuilderException extends GhostRuntimeException{
	private final static String  CONST_MESSAGE_HEADER = "Can't process Query : ";
	private static final long serialVersionUID = 1L;
	
	public GhostQueryBuilderException(){
		super("Query can't be created or executed.");
	}
	
	@SuppressWarnings("static-access")
	public GhostQueryBuilderException(String message){
		super(CONST_MESSAGE_HEADER + message);
		super._message = super.CONST_MESSAGE_HEADER + CONST_MESSAGE_HEADER + message;
    }
}
