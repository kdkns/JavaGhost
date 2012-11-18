package com.java.ghost.Exceptions;

import com.java.ghost.GhostAttributes.IGhostAttribute;

public class GhostAttributeDoesNotExistException extends GhostRuntimeException{
	private final static String  CONST_MESSAGE_HEADER = "Attribute does not exist : ";
	private static final long serialVersionUID = 1L;
	
	public GhostAttributeDoesNotExistException(){
		super("Attribute does not exist, please verify the table you are working with has this attribute.");
	}
	
	@SuppressWarnings("static-access")
	public GhostAttributeDoesNotExistException(String message){
		super(CONST_MESSAGE_HEADER + message);
		super._message = super.CONST_MESSAGE_HEADER + CONST_MESSAGE_HEADER + message;
    }
	
	public GhostAttributeDoesNotExistException(IGhostAttribute igae){
		super("Attribute does not exist, please verify the table you are working with has this attribute: " + igae);
    }
}
