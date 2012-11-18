package com.java.ghost.Exceptions;

public class GhostDBRestrictionException extends GhostRuntimeException{
	private final static String  CONST_MESSAGE_HEADER = "Because of Database restrictions, cannot perform operation : ";
	private static final long serialVersionUID = 1L;
	
	public GhostDBRestrictionException(){
		super("Object is null");
	}
	
	@SuppressWarnings("static-access")
	public GhostDBRestrictionException(String message){
		super(CONST_MESSAGE_HEADER + message);
		super._message = super.CONST_MESSAGE_HEADER + CONST_MESSAGE_HEADER + message;
    }
}
