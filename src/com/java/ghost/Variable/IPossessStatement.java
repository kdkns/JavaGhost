package com.java.ghost.Variable;

import oracle.sql.NUMBER;

import com.java.ghost.MetaTableTypes.IMetaGhostVariableTable;

public interface IPossessStatement{
		public <tableType extends IMetaGhostVariableTable> void bulkAddObjects(IGhostCollection<?> ghostCollection, NUMBER possessionId, tableType mTable, Class<? extends IGhostVariable<?,?>> objectClass);
		public Object getReturnResult();
		public void setReturnResult(Object obj);
		public void preProcess(Object...objArray); 
}