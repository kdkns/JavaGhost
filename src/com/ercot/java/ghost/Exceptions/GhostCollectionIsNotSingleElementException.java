package com.ercot.java.ghost.Exceptions;

public class GhostCollectionIsNotSingleElementException extends GhostExceptions {
	private final static String  CONST_MESSAGE_HEADER = "Can't perform operation : ";
	private static final long serialVersionUID = 1L;
	
	public GhostCollectionIsNotSingleElementException(){
		super(" Unable to perform operation, the collection does not have only one element in it.");
	}
	
	@SuppressWarnings("static-access")
	public GhostCollectionIsNotSingleElementException(String message){
		super(CONST_MESSAGE_HEADER + message);
		super._message = super.CONST_MESSAGE_HEADER + CONST_MESSAGE_HEADER + message;
    }
}
