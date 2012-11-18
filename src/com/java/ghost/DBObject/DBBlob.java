package com.java.ghost.DBObject;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.sql.BLOB;
import oracle.sql.NUMBER;

import org.apache.log4j.Logger;

import com.java.ghost.Exceptions.GhostRuntimeException;
import com.java.ghost.MetaTableTypes.IMetaField;
import com.java.ghost.MetaTableTypes.IMetaGhostVariableTable;
import com.java.ghost.MetaTableTypes.Tables.MetaTables;
import com.java.ghost.utils.GhostCRUD;
import com.java.ghost.utils.GhostCRUDOperationType;
import com.java.ghost.utils.GhostDBProcedureList;
import com.java.ghost.utils.GhostDBStaticVariables;
import com.java.ghost.utils.GhostJDBCUtils;
import com.java.ghost.utils.GhostParameterArray;
import com.java.ghost.utils.GhostStaticVariables;

@SuppressWarnings({ "unused" })
public class DBBlob extends AbstractDBObject<java.sql.Blob, BLOB, java.sql.Blob, Long, IMetaGhostVariableTable> {
	private static Logger logger = Logger.getLogger("DBBlob");

	private static final IMetaField _vmInsertColumn = MetaTables.GHOST_VM.getBlobValue();	
	private long _length;
	
	private static enum DbProcedureTypes{
		isEqual, getLength, CreateEmptyBlob
	}
	protected static GhostDBProcedureList gProcList = new GhostDBProcedureList();

	private static final String GETLENGTH_Statement = "SELECT LENGTH(" + MetaTables.GHOST_VM.getBlobValue() + 
                                                            ") FROM " + MetaTables.GHOST_VM.getTableName() + 
                                                            " WHERE " + MetaTables.GHOST_VM.getUidvm() + 
                                                               " = ?";
	
	private static final String CREATEEMPTYBLOB_Statement = "UPDATE " + MetaTables.GHOST_VM.getTableName() +
														     " SET " + MetaTables.GHOST_VM.getBlobValue() + " = EMPTY_BLOB()" +  
														   " WHERE " + MetaTables.GHOST_VM.getUidvm() + " = ?";
	
	private static final String ISEQUAL_Statement = GhostDBStaticVariables.DB_FUNCTION_CALL_RETURN + GhostDBStaticVariables.GHOST_BLOB_UTIL_PKG + 
			                                       GhostDBStaticVariables.PERIOD + GhostDBStaticVariables.GHOST_BLOB_UTIL_ISEQUAL + 
	                                                               GhostDBStaticVariables.GHOST_BLOB_UTIL_ISEQUAL_ARGS + GhostDBStaticVariables.CLOSE_BRACKET;
	
//	private static final String CREATE_Statement = "INSERT INTO " + MetaTables.GHOST_VM.getTableName() +
//    " (" + MetaTables.GHOST_VM.getUidvm() +
//    ", " + GhostDbStaticVariables.GHOST_VM_POINTER_COLUMN +
//    ", " + GhostDbStaticVariables.GHOST_VM_POINTER_TABLE_COLUMN +
//    ", " + GhostDbStaticVariables.GHOST_VM_POINTER_FIELD_COLUMN + 
//    "," + MetaTables.GHOST_VM.getBlobValue() + ") VALUES (?, ?, ?, ?, EMPTY_BLOB())";

//	private static final String CREATE_Statement = "INSERT INTO " + MetaTables.GHOST_VM.getTableName() +
//														    " (" + MetaTables.GHOST_VM.getUidvm() + 
//														    "," + MetaTables.GHOST_VM.getBlobValue() + ") VALUES (?, EMPTY_BLOB())";
	
	private static final String LOCK_Statement = "SELECT " + MetaTables.GHOST_VM.getBlobValue() +
	                                             " FROM " + MetaTables.GHOST_VM.getTableName() + 
	                                            " WHERE " + MetaTables.GHOST_VM.getUidvm() + " = ? FOR UPDATE";
	
static{
		
		try {
            Class<com.java.ghost.DBObject.DBBlob> thisClass = com.java.ghost.DBObject.DBBlob.class;
            Method privateMethodSetArgument = null;
			Method privateGetReturnObject = null;
			
			//CreateEmptyBlob
			privateMethodSetArgument = thisClass.getDeclaredMethod("setCEBStatementArguments", GhostStaticVariables.oraclePreparedParams);			
			privateMethodSetArgument.setAccessible(true);
			
			gProcList.put(DbProcedureTypes.CreateEmptyBlob, CREATEEMPTYBLOB_Statement, 
				      true,
				      false,
				      false,
				      null,
				      privateMethodSetArgument,
				      null);
			
			//isEqual
			privateMethodSetArgument = thisClass.getDeclaredMethod("setEqualStatementArguments", GhostStaticVariables.oracleCallableParams);			
			privateMethodSetArgument.setAccessible(true);
			
			privateGetReturnObject = thisClass.getDeclaredMethod("getEqualReturnObject", GhostStaticVariables.oracleCallableParams);			
			privateGetReturnObject.setAccessible(true);
			
			gProcList.put(DbProcedureTypes.isEqual, ISEQUAL_Statement, 
				      false,
				      false,
				      false,
				      null,
				      privateMethodSetArgument,
				      privateGetReturnObject);
			
			
			//getLength
			privateMethodSetArgument = thisClass.getDeclaredMethod("setLengthStatementArguments", GhostStaticVariables.oraclePreparedParams);			
			privateMethodSetArgument.setAccessible(true);
			
			privateGetReturnObject = thisClass.getDeclaredMethod("getLengthReturnObject", GhostStaticVariables.oracleResultSetParams);			
			privateGetReturnObject.setAccessible(true);
			
			gProcList.put(DbProcedureTypes.getLength, GETLENGTH_Statement,
				      false,
				      false,
				      false,
				      null,
				      privateMethodSetArgument,
				      privateGetReturnObject);
			
		} catch (SecurityException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		} catch (NoSuchMethodException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
	    }
	}
		
    public DBBlob() {
	  super(MetaTables.GHOST_VM.getBlobValue());
    }

    public DBBlob(IMetaGhostVariableTable table, IMetaField valueColumn, DBID livesWithId){
    	super(table, valueColumn, livesWithId);
        };
    
    public DBBlob(IMetaGhostVariableTable table, IMetaField valueColumn,DBID livesWithId, java.sql.Blob blob){
    	super(table, valueColumn, livesWithId, blob);
    	};
    
    public DBBlob(IMetaGhostVariableTable table, IMetaField valueColumn, DBID livesWithId, DBID uidvm){
        	super(table, valueColumn, livesWithId, uidvm);
    };
    	
//    public DBBlob(IMetaTable table, IMetaField valueColumn, BigDecimal livesWithId, boolean b){//TODO: b serves no purpose other then to change signature
//        	super(table, valueColumn, true, livesWithId);
//        };
    

	protected GhostCRUD getOverrideGCRUD(){
//    	HashMap<SQLOperationType, String> gCRUDMap = new HashMap<SQLOperationType, String>();
    	GhostCRUD gCRUD = new GhostCRUD();
    	//gCRUD.put(GhostCRUDOperationType.Create, CREATE_Statement);
    	gCRUD.put(GhostCRUDOperationType.Lock, LOCK_Statement);
    	return gCRUD;
    	};
    	
    @Override
    protected BLOB convertToOracleObject(java.sql.Blob blob){
		return (BLOB) blob;
    }
    
    private void setCEBStatementArguments(OraclePreparedStatement ocs, GhostParameterArray paramArray){
    	try {
    		ocs.setNUMBER(1, (NUMBER) paramArray.getParameter(1));
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
    }
    
	private void setEqualStatementArguments(OracleCallableStatement ocs, GhostParameterArray paramArray){
    	try {
    		ocs.registerOutParameter(1, Types.BIGINT);
			ocs.setNUMBER(2, (NUMBER) paramArray.getParameter(1));
			ocs.setNUMBER(3, (NUMBER) paramArray.getParameter(2));
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
    }

    private Object getEqualReturnObject(OracleCallableStatement ocs, GhostParameterArray paramArray){
    	try {
			return  Integer.valueOf(ocs.getInt(1));
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			throw new GhostRuntimeException(e);
		}
    }
    
    private void setLengthStatementArguments(OraclePreparedStatement ops, GhostParameterArray paramArray){
    	try {
    		//ocs.registerOutParameter(1, Types.BIGINT);
			ops.setNUMBER(1, (NUMBER) paramArray.getParameter(1));
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
    }

    private Object getLengthReturnObject(OracleResultSet orset, GhostParameterArray paramArray){
    	try {
			return  orset.getLong(1);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			throw new GhostRuntimeException(e);
		}
    }
    
    public boolean equalsSameValue(DBBlob dbBlob){
    	if((isEmpty()) && (dbBlob.isEmpty())){
    		return true;
    	}
    	GhostParameterArray paramArray = new GhostParameterArray();
    	paramArray.putParameter(1, this.getOracleKey());
    	paramArray.putParameter(2, dbBlob.getOracleKey());
    	return ((Integer) executeDBFunctionStatement(gProcList, DbProcedureTypes.isEqual, paramArray)).intValue()==0?true:false;
	}    
    
    
    protected void setToEmptyBlob(){
    	//Update object and set blob column to empty blob
    	GhostParameterArray paramArray = new GhostParameterArray();
    	paramArray.putParameter(1, this.getOracleKey());
    	executeDBStatement(gProcList, DbProcedureTypes.CreateEmptyBlob, paramArray);
    	setIsEmpty(false);
    }
    
//    public DBBlob add(DBBlob dbBlob) throws ClassCastException{
//    	GhostParameterArray paramArray = new GhostParameterArray();
//    	paramArray.putParameter(1, this.getOracleKey());
//    	paramArray.putParameter(2, dbBlob.getOracleKey());
//    	DBBlob returnValue = new DBBlob((Integer) executeDBFunctionStatement(gProcList, DbProcedureTypes.add, paramArray));
//    	return returnValue;
//	}
    
//    public int compareTo(DBBlob arg0) throws ClassCastException{
//    	long result = 0;
//    	try {
//    		result = arg0.getValue().length() - super.executeDBStatement(GhostCRUDOperationType.Read).length();
//    		
//			if (result < 0){
//				result = -1;
//			}else if(result > 0 ){
//				result = 1;
//			}
//				
//		} catch (SQLException e) {
//			logger.error(e.getMessage(),e);
//		}
//		
//		return (int) result;
//	}
    
	@Override
	protected void setOPC(OraclePreparedStatement opc, int pos, Object o) throws SQLException {
		opc.setBLOB(pos, (BLOB) o);
	}	
	
	@Override
	public void setValue(java.sql.Blob blob) {
		if(isEmpty()){setToEmptyBlob();}
    	try {
			_length = blob.length();
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
//		executeDBStatement(GhostCRUDOperationType.Lock);
    	executeDBStatement(GhostCRUDOperationType.Update, convertToOracleObject(blob));
	}
	
	public byte[] getBytes(long pos, int length){
		    if(isEmpty()){setToEmptyBlob();}
			java.sql.Blob jBlob = executeDBStatement(GhostCRUDOperationType.Read);
			try {
				return jBlob.getBytes(pos, length);
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
				throw new GhostRuntimeException(e);
			}
	}
	
		
	public void setBytes(long pos, byte[] ba) {
    	_length = (pos-1) + ba.length;
    	
    	if(isEmpty()){setToEmptyBlob();}
    	
	    try{
	    	Connection conn = GhostJDBCUtils.getConnection();
	    	java.sql.Blob jBlob = executeDBStatement(GhostCRUDOperationType.Read, conn);
	    	executeDBStatement(GhostCRUDOperationType.Lock,conn);
	    	jBlob.setBytes(pos,ba);
	    	executeDBStatement(GhostCRUDOperationType.Update, convertToOracleObject(jBlob), conn);
	    	conn.close();	    	
		} catch (SQLException e) {
			logger.error(e.getMessage(),e); //TODO: This is inefficent because I can't call Update with Bytes Type becasue of generics setting
			throw new GhostRuntimeException(e);
		}
    	
    	
//    	byte[] blobBytes;
//		try {
//			blobBytes = jBlob.getBytes(1, (int) jBlob.length());
//			if(blobBytes.length < ba.length){
//				blobBytes = new byte[(int) (pos+((int)ba.length))-1];
//			}
//			int intPos = ((int) pos) -1;
//	    	for(int x = 0 ;x<ba.length; x++){
//	    		blobBytes[intPos + x] = ba[x];
//	    	}
//	    	BLOB b = convertToOracleObject(jBlob);
//	    	b.setBytes(blobBytes);
//	    	executeDBStatement(GhostCRUDOperationType.Update, b);
//		} catch (SQLException e) {
//			logger.error(e.getMessage(),e);
//		}
	}
	
	public long getLengthInBytes(){
		if(isEmpty()){setToEmptyBlob();}
    	GhostParameterArray paramArray = new GhostParameterArray();
    	paramArray.putParameter(1, this.getOracleKey());
    	return ((Long) executeDBStatement(gProcList, DbProcedureTypes.getLength, paramArray));
	}
	
	@Override
	public IMetaField getVMInsertColumn() {
		return _vmInsertColumn;
	}

	@Override
	public Long size() {
		return getLengthInBytes();
	}

	@Override
	protected boolean hasOverrideGCRUD() {
		return true;
	}
	
}
