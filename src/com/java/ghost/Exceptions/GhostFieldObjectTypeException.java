package com.java.ghost.Exceptions;

public class GhostFieldObjectTypeException extends GhostRuntimeException{
	private final static String  CONST_MESSAGE_HEADER = "FieldMapObject : ";
	private static final long serialVersionUID = 1L;
	
	public GhostFieldObjectTypeException(){
		super("Object being set was of wrong type.");
	}
	
	@SuppressWarnings("static-access")
	public GhostFieldObjectTypeException(String message){
		super(CONST_MESSAGE_HEADER + message);
		super._message = super.CONST_MESSAGE_HEADER + CONST_MESSAGE_HEADER + message;
    }
}
