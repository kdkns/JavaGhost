package com.java.ghost.Variable;

import com.java.ghost.Annotations.PossessConstructor;
import com.java.ghost.DBObject.DBID;
import com.java.ghost.DBObject.DBString;
import com.java.ghost.MetaTableTypes.IMetaField;
import com.java.ghost.MetaTableTypes.IMetaGhostVariableTable;

public class GhostString extends AbstractGhostVariable<String, IMetaGhostVariableTable, DBString, Integer> implements IGhostVariable<String, Integer>{

	public GhostString(){
		super(new DBString() );
	}
	
	public GhostString(String o){
		super(new DBString() );
		getContent().setValue(o);
	}
	
	@PossessConstructor
	public GhostString(DBID uidvm, DBID livesWithId, IMetaGhostVariableTable livesAtTable, IMetaField livesAtColumn, boolean isRemoteTable ) {
		super(new DBString(livesAtTable,livesAtColumn,livesWithId,uidvm) );
		getContent().setIsRemoteTable(isRemoteTable);
    }
	
	public void concat(String str){
		getContent().concatValue(str); 
	}
	
	public void concat(GhostString gString){
		getContent().concatToDbValue(gString.getContent());
	}
	
}
