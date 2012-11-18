package com.ercot.java.ghost.QueryConstructors;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.driver.OracleConnection;

import com.ercot.java.ghost.Exceptions.GhostRuntimeException;
import com.ercot.java.ghost.MetaTableTypes.IMetaField;
import com.ercot.java.ghost.MetaTableTypes.IMetaTable;
import com.ercot.java.ghost.utils.GhostDBProcedureList;
import com.ercot.java.ghost.utils.GhostJDBCUtils;
import com.ercot.java.ghost.utils.GhostParameterArray;
import com.ercot.java.ghost.utils.GhostStaticVariables;

public class GhostQuery extends AbstractGhostQuery{

	private String _query = null;
	private SqlTypes _sqlType = null;
	private List<IMetaField> _fieldList = new ArrayList<IMetaField>();
	private List<IMetaTable> _tableList = new ArrayList<IMetaTable>();
	private GhostTableFilter _tableFilter = null;
	private GhostHavingFilter _havingFilter = null;
	private List<IMetaField> _groupByFields = null;
	private List<IMetaField> _orderByFields = null;
	
	private GhostParameterArray _paramArray = new GhostParameterArray();
	private Method privateMethodSetArgument = null;
	
	{
		Class<com.ercot.java.ghost.QueryConstructors.GhostQuery> thisClass = com.ercot.java.ghost.QueryConstructors.GhostQuery.class; 
		try {
			privateMethodSetArgument = thisClass.getDeclaredMethod("setParamsStatementArguments",GhostStaticVariables.oraclePreparedParams);
			privateMethodSetArgument.setAccessible(true);
		} catch (SecurityException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		} catch (NoSuchMethodException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
	    }
	}
	
	protected GhostQuery(){super();};
	
	protected GhostQuery(Connection conn, PreparedStatement opc, ResultSet rs) {
		super(conn, opc, rs);
	}
	
	@SuppressWarnings("unused")
	private void setParamsStatementArguments(OraclePreparedStatement ops, GhostParameterArray paramArray){
		paramArray.setBindParametersToStatement(0,ops);
	}
	
	@SuppressWarnings("rawtypes")
	private void initializeConnection(Enum operationType) throws SQLException{
		setConn((OracleConnection) GhostJDBCUtils.getConnection());	    	    
	    setOpc((OraclePreparedStatement) getConn().prepareStatement(gProcList.getSQLStatement(operationType)));
	}
	
	protected enum SqlTypes{
		select, insert, update, delete, merge
	}
	
	protected enum DbFunctionTypes{
	  executeSelectQuery, executeInsertQuery, executeUpdateQuery, executeDeleteQuery, executeMergeQuery
	}
	
	protected static GhostDBProcedureList gProcList = new GhostDBProcedureList();

	public void executeQuery() {
		switch(_sqlType){
		  case select:executeSelectQuery();break;
		  case insert:executeInsertQuery();break;
		  case update:executeUpdateQuery();break;
		  case delete:executeDeleteQuery();break;
		  case merge:executeMergeQuery();break;
		}
	}

	
	private void executeSelectQuery(){
		if(_tableFilter!=null){
			if(_tableFilter.isBindMapSet()){getParamArray().setBindParameters(1, getTableFilter());}
		}
		gProcList.put(DbFunctionTypes.executeSelectQuery,_query,
			      false,
			      false,
			      false,
			      null,
			      privateMethodSetArgument,
			      null);
		try {
			initializeConnection(DbFunctionTypes.executeSelectQuery);
		    setGrset(executeResultSetDBStatement(getConn(),getOpc(),gProcList, DbFunctionTypes.executeSelectQuery, getParamArray()));
    	    setCanCallNext(true);
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
	}
    
	private int executeInsertQuery() {
		if(getTableFilter()!=null){
			if(getTableFilter().isBindMapSet()){getParamArray().setBindParameters(1, getTableFilter());}
		}
		gProcList.put(DbFunctionTypes.executeInsertQuery,_query,
			      true,
			      false,
			      false,
			      null,
			      privateMethodSetArgument,
			      null);
  	    return (Integer) executeDBStatement(gProcList, DbFunctionTypes.executeInsertQuery, getParamArray());
	}
	
	private int executeUpdateQuery() {
		if(getTableFilter()!=null){
			if(getTableFilter().isBindMapSet()){getParamArray().setBindParameters(1, getTableFilter());}
		}
		gProcList.put(DbFunctionTypes.executeUpdateQuery,_query,
			      true,
			      false,
			      false,
			      null,
			      privateMethodSetArgument,
			      null);
  	    return (Integer) executeDBStatement(gProcList, DbFunctionTypes.executeUpdateQuery, getParamArray());
	}
	
	private int executeDeleteQuery() {
		if(getTableFilter().isBindMapSet()){getParamArray().setBindParameters(1, getTableFilter());}
		gProcList.put(DbFunctionTypes.executeDeleteQuery,_query,
			      true,
			      false,
			      false,
			      null,
			      privateMethodSetArgument,
			      null);
  	    return (Integer) executeDBStatement(gProcList, DbFunctionTypes.executeDeleteQuery, getParamArray());
	}
	
	private int executeMergeQuery() {
		if(getTableFilter()!=null){
			if(getTableFilter().isBindMapSet()){getParamArray().setBindParameters(1, getTableFilter());}
		}
		gProcList.put(DbFunctionTypes.executeMergeQuery,_query,
			      true,
			      false,
			      false,
			      null,
			      privateMethodSetArgument,
			      null);
  	    return (Integer) executeDBStatement(gProcList, DbFunctionTypes.executeMergeQuery, getParamArray());
	}
	
	/**
	 * @param _query the _query to set
	 */
	protected void setQuery(String query, SqlTypes sqlType) {
		_query = query;
		_sqlType = sqlType;
	}

	/**
	 * @return the _query
	 */
	public String getQuery() {
		return _query;
	}
	
	public boolean setFieldList(List<IMetaField> selectFieldList) {
		return _fieldList.addAll((List<IMetaField>) selectFieldList);
	}
	
	public boolean setTableList(List<IMetaTable> tableList) {
		return _tableList.addAll((List<IMetaTable>) tableList);
	}
	
	public boolean setTableList(IMetaTable table) {
		return _tableList.add(table);
	}
		
	public List<IMetaField> getFieldList() {
		return _fieldList;
	}
	
	public List<IMetaTable> getTableList() {
		return _tableList;
	}
	
	public boolean containsColumn(IMetaField metaField){
		   return _fieldList.contains(metaField);
	}

	protected void setTableFilter(GhostTableFilter tableFilter) {
		_tableFilter = tableFilter;
	}

	protected void setHavingFilter(GhostHavingFilter havingFilter) {
		_havingFilter = havingFilter;
	}

	protected void setGroupByFields(List<IMetaField> groupByFields) {
		_groupByFields = groupByFields;
	}
	
	public void setOrderByFields(List<IMetaField> orderByFields) {
		_orderByFields = orderByFields;
	}
	
	protected GhostParameterArray getParamArray(){
		return _paramArray;
	}
	
	public GhostTableFilter getTableFilter(){
		return _tableFilter;
	}
	
	public GhostHavingFilter getHavingFilter(){
		return _havingFilter;
	}
	
	public List<IMetaField> getGroupByList() {
		return _groupByFields;
	}
	
	public List<IMetaField> getOrderByList() {
		return _orderByFields;
	}
	
}
