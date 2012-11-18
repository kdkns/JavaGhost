package com.java.ghost.Variable;

import com.java.ghost.Annotations.PossessConstructor;
import com.java.ghost.DBObject.DBID;
import com.java.ghost.DBObject.DBNumber;
import com.java.ghost.MetaTableTypes.IMetaField;
import com.java.ghost.MetaTableTypes.IMetaGhostVariableTable;


public class GhostNumber extends AbstractGhostVariable<java.lang.Number, IMetaGhostVariableTable, DBNumber, Integer> implements IGhostVariable<java.lang.Number, Integer>{
	
	public GhostNumber(){
		super(new DBNumber());
	}
	
	public GhostNumber(java.lang.Number o){
		super(new DBNumber() );
		getContent().setValue(o);
	}
	
	@PossessConstructor
	public GhostNumber(DBID uidvm, DBID livesWithId, IMetaGhostVariableTable livesAtTable, IMetaField livesAtColumn, boolean isRemoteTable ) {
		super(new DBNumber(livesAtTable,livesAtColumn,livesWithId,uidvm) );
		getContent().setIsRemoteTable(isRemoteTable);
    }
	
	public void add(java.lang.Number number){
		getContent().add(number);
	}
	
	public void subtract(java.lang.Number number){
		getContent().subtract(number);
	}
	
	public void multiply(java.lang.Number number){
		getContent().multiply(number);
	}
	
	public void divide(java.lang.Number number){
		getContent().divide(number);
	}
	
	public void add(GhostNumber ghostNumber){
		getContent().add(ghostNumber.getContent());
	}
	
	public void subtract(GhostNumber ghostNumber){
		getContent().subtract(ghostNumber.getContent());
	}
	
	public void multiply(GhostNumber ghostNumber){
		getContent().multiply(ghostNumber.getContent());
	}
	
	public void divide(GhostNumber ghostNumber){
		getContent().divide(ghostNumber.getContent());
	}
}
