package com.java.ghost.Exceptions;

public class GhostCollectionIsEmptyException extends GhostExceptions {
	private final static String  CONST_MESSAGE_HEADER = "Can't perform operation : ";
	private static final long serialVersionUID = 1L;
	
	public GhostCollectionIsEmptyException(){
		super(" Unable to perform operation, the collection is empty!");
	}
	
	@SuppressWarnings("static-access")
	public GhostCollectionIsEmptyException(String message){
		super(CONST_MESSAGE_HEADER + message);
		super._message = super.CONST_MESSAGE_HEADER + CONST_MESSAGE_HEADER + message;
    }
}
