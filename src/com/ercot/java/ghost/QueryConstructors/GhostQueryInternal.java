package com.ercot.java.ghost.QueryConstructors;

import com.ercot.java.ghost.DBStatementExecutor.ICoreStatementLogic;

public class GhostQueryInternal extends AbstractGhostQuery{
	
	public GhostQueryInternal(ICoreStatementLogic ics){
		super(ics.getConnection(),ics.getPreparedStatement(), ics.getResultSet());
	}
	
}
