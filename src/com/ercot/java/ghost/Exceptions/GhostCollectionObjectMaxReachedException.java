package com.ercot.java.ghost.Exceptions;

public class GhostCollectionObjectMaxReachedException extends GhostRuntimeException{
	private final static String  CONST_MESSAGE_HEADER = "Unable to create Ghost Collection Object : ";
	private static final long serialVersionUID = 1L;
	
	public GhostCollectionObjectMaxReachedException(){
		super("Number of instances of Ghost Collection objects reached max.");
	}
	
	@SuppressWarnings("static-access")
	public GhostCollectionObjectMaxReachedException(String message){
		super(CONST_MESSAGE_HEADER + message);
		super._message = super.CONST_MESSAGE_HEADER + CONST_MESSAGE_HEADER + message;
    }
}
