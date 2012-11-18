package com.java.ghost.Exceptions;

import java.util.List;

import com.java.ghost.MetaTableTypes.IMetaField;
import com.java.ghost.MetaTableTypes.IMetaTable;
import com.java.ghost.QueryConstructors.GhostQueryConstructor;

public class GhostFieldRequiredForInsertException extends GhostRuntimeException{
	private final static String  CONST_MESSAGE_HEADER = " Field required: ";
	private static final long serialVersionUID = 1L;
	
//	public GhostFieldRequiredForInsertException(){
//		super("A field was missing that is required to be set when inserting into this table.");
//	}
	
	@SuppressWarnings("static-access")
	public GhostFieldRequiredForInsertException(String message){
		super(CONST_MESSAGE_HEADER + message);
		super._message = super.CONST_MESSAGE_HEADER + CONST_MESSAGE_HEADER + message;
    }

	public GhostFieldRequiredForInsertException(IMetaTable table) {
		super("Required fields are: " + GhostQueryConstructor.getConcatFieldsColumnName(table.getRequiredInsertFieldList(),","));
	}
	
	public GhostFieldRequiredForInsertException(List<IMetaField> fieldList) {
		super("Required fields are: " + GhostQueryConstructor.getConcatFieldsColumnName(fieldList,","));
	}
}
