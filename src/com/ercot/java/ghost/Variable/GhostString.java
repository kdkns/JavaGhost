package com.ercot.java.ghost.Variable;

import com.ercot.java.ghost.Annotations.PossessConstructor;
import com.ercot.java.ghost.DBObject.DBID;
import com.ercot.java.ghost.DBObject.DBString;
import com.ercot.java.ghost.MetaTableTypes.IMetaField;
import com.ercot.java.ghost.MetaTableTypes.IMetaGhostVariableTable;

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
