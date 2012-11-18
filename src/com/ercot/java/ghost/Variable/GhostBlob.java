package com.ercot.java.ghost.Variable;

import com.ercot.java.ghost.Annotations.PossessConstructor;
import com.ercot.java.ghost.DBObject.DBBlob;
import com.ercot.java.ghost.DBObject.DBID;
import com.ercot.java.ghost.MetaTableTypes.IMetaField;
import com.ercot.java.ghost.MetaTableTypes.IMetaGhostVariableTable;
import com.ercot.java.ghost.MetaTableTypes.Tables.MetaTables;


public class GhostBlob extends AbstractGhostVariable<java.sql.Blob, IMetaGhostVariableTable, DBBlob, Long> implements IGhostVariable<java.sql.Blob, Long>{
	
	public GhostBlob(){
		super(new DBBlob() );
	}
	
	public GhostBlob(DBID uidvm){		
		super(new DBBlob(MetaTables.GHOST_VM,MetaTables.GHOST_VM.getBlobValue(),uidvm));		
	}
	
	public GhostBlob(java.sql.Blob o){
		super(new DBBlob());        
		getContent().setValue(o);
	}
	
	
	@PossessConstructor
	public GhostBlob(DBID uidvm, DBID livesWithId, IMetaGhostVariableTable livesAtTable, IMetaField livesAtColumn, boolean isRemoteTable ) {
		super(new DBBlob(livesAtTable,livesAtColumn,livesWithId,uidvm) );
		getContent().setIsRemoteTable(isRemoteTable);
    }

	
	public boolean equalsSameValue(GhostBlob gBlob) {
		return getContent().equalsSameValue(gBlob.getContent());
	}	
		
}
