package com.java.ghost.Variable;

import oracle.sql.NUMBER;

import com.java.ghost.MetaTableTypes.IMetaGhostVariableTable;

public class PossessStatement implements IPossessStatement{
		Object _returnResult = null;
	
		@Override
		public <tableType extends IMetaGhostVariableTable> void bulkAddObjects(IGhostCollection<?> ghostCollection, NUMBER possessionId, tableType mTable,Class<? extends IGhostVariable<?, ?>> objectClass){}

		@Override
		public Object getReturnResult() {return _returnResult;}

		@Override
		public void preProcess(Object...objArray) {}

		@Override
		public void setReturnResult(Object obj) {_returnResult= obj;}
		
	}