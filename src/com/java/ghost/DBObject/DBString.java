package com.java.ghost.DBObject;

import java.lang.reflect.Method;
import java.sql.SQLException;

import oracle.jdbc.OraclePreparedStatement;
import oracle.sql.CHAR;
import oracle.sql.NUMBER;

import org.apache.log4j.Logger;

import com.java.ghost.Exceptions.GhostRuntimeException;
import com.java.ghost.MetaTableTypes.IMetaField;
import com.java.ghost.MetaTableTypes.IMetaGhostVariableTable;
import com.java.ghost.MetaTableTypes.Tables.MetaTables;
import com.java.ghost.utils.GhostCRUDOperationType;
import com.java.ghost.utils.GhostDBProcedureList;
import com.java.ghost.utils.GhostDBStaticVariables;
import com.java.ghost.utils.GhostParameterArray;
import com.java.ghost.utils.GhostStaticVariables;
import com.java.ghost.utils.GhostVariableWrapper;

public class DBString extends AbstractDBObject<String, CHAR, String, Integer, IMetaGhostVariableTable> implements Comparable<DBString>{
	private static Logger logger = Logger.getLogger("DBString");
    private int _length = 0;
    
    private static final IMetaField _vmInsertColumn = MetaTables.GHOST_VM.getStringValue();
    
    private static enum DbProcedureTypes{
		concatValue, concatToDb
	}
	protected static GhostDBProcedureList gProcList = new GhostDBProcedureList();

	private static final String CONCAT_VALUE_Statement = "UPDATE " + MetaTables.GHOST_VM.getTableName() +
							                              " SET " + MetaTables.GHOST_VM.getStringValue() + " = " 
							                                      + MetaTables.GHOST_VM.getStringValue() + " || ? " +  
											            " WHERE " + MetaTables.GHOST_VM.getUidvm() + " = ?";
	
	private static final String CONCAT_TODB_Statement = "UPDATE " + MetaTables.GHOST_VM.getTableName() + " a" +
										               " SET a." + MetaTables.GHOST_VM.getStringValue() + " = (SELECT a." + MetaTables.GHOST_VM.getStringValue()
											                     + " || b." + MetaTables.GHOST_VM.getStringValue() 
														         + " FROM " + MetaTables.GHOST_VM.getTableName() + " b"
														         + " WHERE b." + MetaTables.GHOST_VM.getUidvm() + " = ? )" + 
													 " WHERE a." + MetaTables.GHOST_VM.getUidvm() + " = ?";
		
	
static{
		
		try {
            Class<com.java.ghost.DBObject.DBString> thisClass = com.java.ghost.DBObject.DBString.class;
            Method privateMethodSetArgument = null;
			
			//Concat value
			privateMethodSetArgument = thisClass.getDeclaredMethod("setConcatValueStatementArguments", GhostStaticVariables.oraclePreparedParams);			
			privateMethodSetArgument.setAccessible(true);
			
			gProcList.put(DbProcedureTypes.concatValue, CONCAT_VALUE_Statement, 
				      true,
				      false,
				      false,
				      null,
				      privateMethodSetArgument,
				      null);
			
			//Concat value
			privateMethodSetArgument = thisClass.getDeclaredMethod("setConcatToDbValueStatementArguments", GhostStaticVariables.oraclePreparedParams);			
			privateMethodSetArgument.setAccessible(true);
			
			gProcList.put(DbProcedureTypes.concatToDb, CONCAT_TODB_Statement, 
				      true,
				      false,
				      false,
				      null,
				      privateMethodSetArgument,
				      null);
			
		} catch (SecurityException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		} catch (NoSuchMethodException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
	    }
	}
    
	@SuppressWarnings("unused")
	private void setConcatValueStatementArguments(OraclePreparedStatement ocs, GhostParameterArray paramArray){
		try {
			ocs.setCHAR(1, (CHAR) paramArray.getParameter(1));
			ocs.setNUMBER(2, (NUMBER) paramArray.getParameter(2));
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
	}

	@SuppressWarnings("unused")
	private void setConcatToDbValueStatementArguments(OraclePreparedStatement ocs, GhostParameterArray paramArray){
		try {
			ocs.setNUMBER(1, (NUMBER) paramArray.getParameter(1));
			ocs.setNUMBER(2, (NUMBER) paramArray.getParameter(2));
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
	}

	public DBString() {
		  super(MetaTables.GHOST_VM.getStringValue());
	}
	
	public DBString(String str) {
		  super(MetaTables.GHOST_VM.getStringValue(),str);
	}
	
  public DBString(IMetaGhostVariableTable table, IMetaField valueColumn, DBID livesWithId){
  	super(table, valueColumn, livesWithId);
      };
  
  public DBString(IMetaGhostVariableTable table, IMetaField valueColumn, DBID livesWithId, String str){
  	super(table, valueColumn, livesWithId, str);
  	};
  
  public DBString(IMetaGhostVariableTable table, IMetaField valueColumn,DBID livesWithId, DBID bd){
      	super(table, valueColumn, livesWithId, bd);
  };	
    
    @Override
    public IMetaField getVMInsertColumn() {
    		return _vmInsertColumn;
    	}
    	
    protected int getLength(){return _length;};
    
    @Override
    protected CHAR convertToOracleObject(String s){
    	CHAR oracleString = null;
    	//CharacterSet c = CharacterSet.make(CharacterSet.WE8ISO8859P1_CHARSET);
    	
    	try {
	    	logger.debug("Value of String to convert to Oracle String:" + GhostVariableWrapper.wrapVariable(s));
			oracleString = new CHAR(s, GhostDBStaticVariables.dbCharacterSet);
			logger.debug("Value of Oralce String:" + GhostVariableWrapper.wrapVariable(oracleString));    			
		    } catch (SQLException e) {
		    	logger.error(e.getMessage(),e);
		}
		    return oracleString;
    }
    
    @Override
	public String getValue() {
    	    String tmp = executeDBStatement(GhostCRUDOperationType.Read).toString();
    	    _length = tmp.length();
			return tmp;
	}
    
    @Override
	public void setValue(String str) {
    	_length = str.length();
    	setIsEmpty(false);
    	executeDBStatement(GhostCRUDOperationType.Update, convertToOracleObject(str));
	}
    
    public int compareTo(DBString arg0) throws ClassCastException{
		return super.executeDBStatement(GhostCRUDOperationType.Read).compareTo(arg0.getValue());
	}

	@Override
	protected void setOPC(OraclePreparedStatement opc, int pos, Object o) throws SQLException {
		opc.setCHAR(pos, (CHAR) o);
	}
	
//	@Override
//	protected String getResultSetObject(OracleResultSet orset, int pos) throws SQLException{
//		return orset.getString(pos);
//	}
	
	public void concatValue(String str){
    	GhostParameterArray paramArray = new GhostParameterArray();
    	try {
			paramArray.putParameter(1, new CHAR(str, GhostDBStaticVariables.dbCharacterSet));
			paramArray.putParameter(2, this.getOracleKey());
	    	executeDBStatement(gProcList, DbProcedureTypes.concatValue, paramArray);
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
    	_length += str.length();
	}
	
	public void concatToDbValue(DBString dbString){
    	GhostParameterArray paramArray = new GhostParameterArray();
		paramArray.putParameter(1, dbString.getOracleKey());
		paramArray.putParameter(2, this.getOracleKey());
    	executeDBStatement(gProcList, DbProcedureTypes.concatToDb, paramArray);
    	_length+=dbString.getLength();
	}

	@Override
	public Integer size() {
		return isEmpty()? 0 : getLength();
	}

	@Override
	protected boolean hasOverrideGCRUD() {
		return false;
	}
}
