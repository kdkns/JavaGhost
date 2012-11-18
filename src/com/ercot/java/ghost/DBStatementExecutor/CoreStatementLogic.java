package com.ercot.java.ghost.DBStatementExecutor;

import java.lang.reflect.InvocationTargetException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CoreStatementLogic implements ICoreStatementLogic {
		
		private Object _returnValue = null;
		private PreparedStatement _preparedStatement = null;
		private CallableStatement _callableStatement = null;
		private Connection _connection = null;
		private ResultSet _resultSet = null;
		private boolean _isReturnTypeResultSet = false;
		
		public void executeStatementLogic(Object callingClass, DBSEIDataStructure dBSEIDataStructure) throws SQLException,IllegalAccessException,InvocationTargetException {}
		public PreparedStatement getPreparedStatement(){return _preparedStatement;};
		public CallableStatement getCallableStatement(){return _callableStatement;}; 
		public Connection getConnection(){return _connection;}; 
		public ResultSet getResultSet(){return _resultSet;}; 
		public Object getReturnValue() {return _returnValue;}

		public void setReturnValue(Object returnValue) {
			_returnValue = returnValue;
		}
		public void setPreparedStatement(PreparedStatement preparedStatement) {
			_preparedStatement = preparedStatement;
		}
		public void setCallableStatement(CallableStatement callableStatement) {
			_callableStatement = callableStatement;
		}
		public void setConnection(Connection connection) {
			_connection = connection;
		}
		public void setResultSet(ResultSet resultSet) {
			_resultSet = resultSet;
		}
		
		public void setIsReturnTypeResultSet(boolean value){
			_isReturnTypeResultSet = value;
		}
		
		@Override
		public boolean isReturnTypeResultSet() {
			return _isReturnTypeResultSet;
		};
	}
	