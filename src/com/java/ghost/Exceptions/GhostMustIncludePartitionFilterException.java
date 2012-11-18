package com.java.ghost.Exceptions;

import com.java.ghost.MetaTableTypes.IMetaField;

public class GhostMustIncludePartitionFilterException extends GhostRuntimeException{
	private final static String  CONST_MESSAGE_HEADER = "Table Filter Critera Missing : ";
	private static final long serialVersionUID = 1L;
	
	public GhostMustIncludePartitionFilterException(IMetaField field){
		super("Please include a filter critera for the partition field: " + field.getAssociatedTableName() + "." + field.getColumnName());
	}
	
	@SuppressWarnings("static-access")
	public GhostMustIncludePartitionFilterException(String message){
		super(CONST_MESSAGE_HEADER + message);
		super._message = super.CONST_MESSAGE_HEADER + CONST_MESSAGE_HEADER + message;
    }
}
