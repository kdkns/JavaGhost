package com.java.ghost.DBStatementExecutor;

import java.lang.reflect.InvocationTargetException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface ICoreStatementLogic{
		
	    public void setIsReturnTypeResultSet(boolean value);
		public boolean isReturnTypeResultSet();
		public void executeStatementLogic(Object callingClass, DBSEIDataStructure dBSEIDataStructure) throws SQLException,IllegalAccessException,InvocationTargetException;
		public PreparedStatement getPreparedStatement();
		public CallableStatement getCallableStatement(); 
		public Connection getConnection(); 
		public ResultSet getResultSet(); 
		public Object getReturnValue();

		public void setReturnValue(Object returnValue);
		public void setPreparedStatement(PreparedStatement preparedStatement);
		public void setCallableStatement(CallableStatement callableStatement);
		public void setConnection(Connection connection);
		public void setResultSet(ResultSet resultSet);
	}
	