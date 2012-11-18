package com.java.ghost.DBObject;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.SQLException;

import oracle.jdbc.OraclePreparedStatement;
import oracle.sql.NUMBER;

import org.apache.log4j.Logger;

import com.java.ghost.Exceptions.GhostRuntimeException;
import com.java.ghost.MetaTableTypes.IMetaField;
import com.java.ghost.MetaTableTypes.IMetaGhostVariableTable;
import com.java.ghost.MetaTableTypes.Tables.MetaTables;
import com.java.ghost.utils.GhostCRUDOperationType;
import com.java.ghost.utils.GhostDBProcedureList;
import com.java.ghost.utils.GhostParameterArray;
import com.java.ghost.utils.GhostStaticVariables;
import com.java.ghost.utils.GhostVariableWrapper;

public class DBNumber extends AbstractDBObject<java.lang.Number, NUMBER, BigDecimal, Integer, IMetaGhostVariableTable> implements Comparable<DBNumber>{
	private static Logger logger = Logger.getLogger("DBNumber");

	private static final IMetaField _vmInsertColumn = MetaTables.GHOST_VM.getNumberValue();	
    
	private static enum DbProcedureTypes{
		addValue, subtractValue, multiplyValue, divideValue,
		addTodbValue, subtractTodbValue, multiplyTodbValue, divideTodbValue
	}
	protected static GhostDBProcedureList gProcList = new GhostDBProcedureList();

	private static final String OPERATION_VALUE_Statement = "UPDATE " + MetaTables.GHOST_VM.getTableName() +
							                                 " SET " + MetaTables.GHOST_VM.getNumberValue() + " = " 
							                                         + MetaTables.GHOST_VM.getNumberValue() + " " + GhostStaticVariables.OPERATION_TOKEN + " ? " +  
											               " WHERE " + MetaTables.GHOST_VM.getUidvm() + " = ?";
	
	private static final String OPERATION_TODB_Statement = "UPDATE " + MetaTables.GHOST_VM.getTableName() + " a" +
												          " SET a." + MetaTables.GHOST_VM.getNumberValue() + " = (SELECT a." + MetaTables.GHOST_VM.getNumberValue()
														            + GhostStaticVariables.OPERATION_TOKEN + " b." + MetaTables.GHOST_VM.getNumberValue() 
														            + " FROM " + MetaTables.GHOST_VM.getTableName() + " b"
														            + " WHERE b." + MetaTables.GHOST_VM.getUidvm() + " = ? )" + 
													    " WHERE a." + MetaTables.GHOST_VM.getUidvm() + " = ?";
		
	
static{
		
		try {
            Class<com.java.ghost.DBObject.DBNumber> thisClass = com.java.ghost.DBObject.DBNumber.class;
            Method privateMethodSetArgument = null;
			
			//Add value
			privateMethodSetArgument = thisClass.getDeclaredMethod("setOperationValueStatementArguments", GhostStaticVariables.oraclePreparedParams);			
			privateMethodSetArgument.setAccessible(true);
			
			gProcList.put(DbProcedureTypes.addValue, OPERATION_VALUE_Statement.replace(GhostStaticVariables.OPERATION_TOKEN, 
					                                                                  GhostStaticVariables.CHAR_PLUS), 
				      true,
				      false,
				      false,
				      null,
				      privateMethodSetArgument,
				      null);
			
			//Subtract value
			privateMethodSetArgument = thisClass.getDeclaredMethod("setOperationValueStatementArguments", GhostStaticVariables.oraclePreparedParams);			
			privateMethodSetArgument.setAccessible(true);
			
			gProcList.put(DbProcedureTypes.subtractValue, OPERATION_VALUE_Statement.replace(GhostStaticVariables.OPERATION_TOKEN, 
					                                                                       GhostStaticVariables.CHAR_MINUS), 
				      true,
				      false,
				      false,
				      null,
				      privateMethodSetArgument,
				      null);
			
			//Multiply value
			privateMethodSetArgument = thisClass.getDeclaredMethod("setOperationValueStatementArguments", GhostStaticVariables.oraclePreparedParams);			
			privateMethodSetArgument.setAccessible(true);
			
			gProcList.put(DbProcedureTypes.multiplyValue, OPERATION_VALUE_Statement.replace(GhostStaticVariables.OPERATION_TOKEN, 
					                                                                       GhostStaticVariables.CHAR_MULTIPLY), 
				      true,
				      false,
				      false,
				      null,
				      privateMethodSetArgument,
				      null);
			
			//Divide value
			privateMethodSetArgument = thisClass.getDeclaredMethod("setOperationValueStatementArguments", GhostStaticVariables.oraclePreparedParams);			
			privateMethodSetArgument.setAccessible(true);
			
			gProcList.put(DbProcedureTypes.divideValue, OPERATION_VALUE_Statement.replace(GhostStaticVariables.OPERATION_TOKEN, 
					                                                                     GhostStaticVariables.CHAR_DIVIDE), 
				      true,
				      false,
				      false,
				      null,
				      privateMethodSetArgument,
				      null);
			
			
			//Add value from DBNumber
			privateMethodSetArgument = thisClass.getDeclaredMethod("setOperationValueStatementArguments", GhostStaticVariables.oraclePreparedParams);			
			privateMethodSetArgument.setAccessible(true);
			
			gProcList.put(DbProcedureTypes.addTodbValue, OPERATION_TODB_Statement.replace(GhostStaticVariables.OPERATION_TOKEN, 
					                                                                     GhostStaticVariables.CHAR_PLUS), 
				      true,
				      false,
				      false,
				      null,
				      privateMethodSetArgument,
				      null);
			
			//Subtract value from DBNumber
			privateMethodSetArgument = thisClass.getDeclaredMethod("setOperationValueStatementArguments", GhostStaticVariables.oraclePreparedParams);			
			privateMethodSetArgument.setAccessible(true);
			
			gProcList.put(DbProcedureTypes.subtractTodbValue, OPERATION_TODB_Statement.replace(GhostStaticVariables.OPERATION_TOKEN, 
				                                                                              GhostStaticVariables.CHAR_MINUS), 
				      true,
				      false,
				      false,
				      null,
				      privateMethodSetArgument,
				      null);

			//Multiply value from DBNumber
			privateMethodSetArgument = thisClass.getDeclaredMethod("setOperationValueStatementArguments", GhostStaticVariables.oraclePreparedParams);			
			privateMethodSetArgument.setAccessible(true);
			
			gProcList.put(DbProcedureTypes.multiplyTodbValue, OPERATION_TODB_Statement.replace(GhostStaticVariables.OPERATION_TOKEN, 
					                                                                          GhostStaticVariables.CHAR_MULTIPLY), 
				      true,
				      false,
				      false,
				      null,
				      privateMethodSetArgument,
				      null);
			
			
			//Divide value from DBNumber
			privateMethodSetArgument = thisClass.getDeclaredMethod("setOperationValueStatementArguments", GhostStaticVariables.oraclePreparedParams);			
			privateMethodSetArgument.setAccessible(true);
			
			gProcList.put(DbProcedureTypes.divideTodbValue, OPERATION_TODB_Statement.replace(GhostStaticVariables.OPERATION_TOKEN, 
					                                                                        GhostStaticVariables.CHAR_DIVIDE), 
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
	private void setOperationValueStatementArguments(OraclePreparedStatement ocs, GhostParameterArray paramArray){
    	try {
			ocs.setNUMBER(1, (NUMBER) paramArray.getParameter(1));
			ocs.setNUMBER(2, (NUMBER) paramArray.getParameter(2));
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
    }
	
	public DBNumber() {
		  super(MetaTables.GHOST_VM.getNumberValue());
	}
	
	public DBNumber(java.lang.Number number) {
		  super(MetaTables.GHOST_VM.getNumberValue(),number);
	}
	
    public DBNumber(IMetaGhostVariableTable table, IMetaField valueColumn, DBID livesWithId){
    	super(table, valueColumn, livesWithId);
        };
    
    public DBNumber(IMetaGhostVariableTable table, IMetaField valueColumn, DBID livesWithId, java.lang.Number number){
    	super(table, valueColumn, livesWithId, number);
    	};
    
    public DBNumber(IMetaGhostVariableTable table, IMetaField valueColumn,DBID livesWithId, DBID bd){//TODO: b serves no purpose other then to change signature
        	super(table, valueColumn, livesWithId, bd);
    };	
    	
    @Override
    protected NUMBER convertToOracleObject(java.lang.Number number){
    	NUMBER oracleNumber = null;
    	try {
	    	logger.debug("Value of Number to convert to Oracle Number:" + GhostVariableWrapper.wrapVariable(number.toString()));
			oracleNumber = new NUMBER(number);
			logger.debug("Value of Oralce Number:" + GhostVariableWrapper.wrapVariable(oracleNumber.bigDecimalValue().toPlainString()));    			
		    } catch (SQLException e) {
		    e.printStackTrace();
			logger.error(e.getMessage(),e);
		}
		    return oracleNumber;
    }
    
    public int compareTo(DBNumber arg0) throws ClassCastException{
		return super.executeDBStatement(GhostCRUDOperationType.Read).compareTo(arg0.getValue());
	}

	@Override
	protected void setOPC(OraclePreparedStatement opc, int pos, Object o) throws SQLException {
		opc.setNUMBER(pos, (NUMBER) o);
	}

//	@Override
//	protected BigDecimal getResultSetObject(OracleResultSet orset, int pos) throws SQLException{
//		return orset.getBigDecimal(pos);
//	}
	
	@Override
	public IMetaField getVMInsertColumn() {
		return _vmInsertColumn;
	}
	
	public void operationValue(java.lang.Number number, DbProcedureTypes dbProcedureType){
    	GhostParameterArray paramArray = new GhostParameterArray();
    	try {
			paramArray.putParameter(1, new NUMBER(number));
			paramArray.putParameter(2, this.getOracleKey());
	    	executeDBStatement(gProcList, dbProcedureType, paramArray);
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
    	
//    	if(!isEmpty()){
//    		paramArray.putParameter(1, new NUMBER(number));
//			paramArray.putParameter(2, this.getOracleKey());
//	    	executeDBStatement(gProcList, dbProcedureType, paramArray);
//    	}else if(isRemoteTable()){		   
//		   
//		   try {
//			   
//			    paramArray.putParameter(1, new NUMBER(number));
//				paramArray.putParameter(2, this.getOracleKey());
//		    	executeDBStatement(gProcList, dbProcedureType, paramArray);
//		    	
//			   paramArray.putParameter(3, new CHAR(getLocatorQuery(this,getTable()), GhostDBStaticVariables.dbCharacterSet));
//		       setIsEmpty(false);
//		       setIsRemoteTable(false);
//		   } catch (SQLException e) {
//		     logger.error(e.getMessage(), e);
//		     throw new GhostRuntimeException(e);
//		   }
//    	}else {
// 		   throw new GhostRuntimeException("Can't perform function on empty value!");
// 	   }
	}
	
	public void operationTodbValue(DBNumber dbNumber, DbProcedureTypes dbProcedureType){
    	GhostParameterArray paramArray = new GhostParameterArray();
		paramArray.putParameter(1, dbNumber.getOracleKey());
		paramArray.putParameter(2, this.getOracleKey());
    	executeDBStatement(gProcList, dbProcedureType, paramArray);    	
	}
	
	public void add(java.lang.Number number){
		operationValue(number,DbProcedureTypes.addValue);
	}
    
	public void subtract(java.lang.Number number){
		operationValue(number,DbProcedureTypes.subtractValue);
	}
	
	public void multiply(java.lang.Number number){
		operationValue(number,DbProcedureTypes.multiplyValue);
	}
	
	public void divide(java.lang.Number number){
		operationValue(number,DbProcedureTypes.divideValue);
	}
	
	public void add(DBNumber dbNumber){
		operationTodbValue(dbNumber,DbProcedureTypes.addTodbValue);
	}
    
	public void subtract(DBNumber dbNumber){
		operationTodbValue(dbNumber,DbProcedureTypes.subtractTodbValue);
	}
	
	public void multiply(DBNumber dbNumber){
		operationTodbValue(dbNumber,DbProcedureTypes.multiplyTodbValue);
	}
	
	public void divide(DBNumber dbNumber){
		operationTodbValue(dbNumber,DbProcedureTypes.divideTodbValue);
	}

	@Override
	public Integer size() {
		return isEmpty()? 0 : 1;
	}

	@Override
	protected boolean hasOverrideGCRUD() {
		return false;
	}

}
