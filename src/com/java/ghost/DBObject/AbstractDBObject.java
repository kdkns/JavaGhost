package com.java.ghost.DBObject;

	import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.sql.CHAR;
import oracle.sql.NUMBER;
import oracle.sql.TIMESTAMP;

import org.apache.log4j.Logger;

import com.java.ghost.DBStatementExecutor.DBStatementExecutor;
import com.java.ghost.Exceptions.GhostRuntimeException;
import com.java.ghost.GhostAttributes.GhostAttributeEnum;
import com.java.ghost.GhostAttributes.IGhostCustomAttribute;
import com.java.ghost.MetaTableTypes.IMetaChildTable;
import com.java.ghost.MetaTableTypes.IMetaField;
import com.java.ghost.MetaTableTypes.IMetaGhostVariableTable;
import com.java.ghost.MetaTableTypes.IMetaTable;
import com.java.ghost.MetaTableTypes.Tables.MetaTables;
import com.java.ghost.utils.GDate;
import com.java.ghost.utils.GhostCRUD;
import com.java.ghost.utils.GhostCRUDOperationType;
import com.java.ghost.utils.GhostDBStaticVariables;
import com.java.ghost.utils.GhostHash;
import com.java.ghost.utils.GhostJDBCUtils;
import com.java.ghost.utils.GhostParameterArray;
import com.java.ghost.utils.GhostStaticVariables;
import com.java.ghost.utils.GhostVariableWrapper;
import com.java.ghost.utils.IGDate;

    @SuppressWarnings({"unchecked", "unused" })
	public abstract class AbstractDBObject<inputObjectType, outputObjectType, getValueType, sizeReturnType, tableType extends IMetaGhostVariableTable> extends DBStatementExecutor implements IDBObject<inputObjectType, getValueType, sizeReturnType, tableType>{
		
		private static Logger logger= Logger.getLogger("AbstractDBObject");
		private NUMBER _uidvm;
		private boolean _isEmpty = true;		
		private boolean _isRemoteTable = false;
		private tableType _table;
		private IMetaField _livesAtValueColumn;
		private IMetaField _livesAtIdColumn;
		private DBID _livesWithId;
		
		private static final GhostCRUD _gCRUD = new GhostCRUD();
		private String _accessQueryRemote;
		
		
		private static final String GETUIDVM_SEQUENCE_Statement = "SELECT " + GhostDBStaticVariables.GHOST_UID_VM_SEQUENCE + ".NEXTVAL FROM DUAL";
	    private static final String REMOVE_Statement = "DELETE FROM " + MetaTables.GHOST_VM.getTableName() + " WHERE " + MetaTables.GHOST_VM.getUidvm() + " = ?";
//	    private static final String CREATE_Statement = "INSERT INTO " + MetaTables.GHOST_VM.getTableName() +
//	                                                   " (" + MetaTables.GHOST_VM.getUidvm() +
//	                                                   ", " + GhostDbStaticVariables.GHOST_VM_POINTER_COLUMN +
//	                                                   ", " + GhostDbStaticVariables.GHOST_VM_POINTER_TABLE_COLUMN +
//	                                                   ", " + GhostDbStaticVariables.GHOST_VM_POINTER_FIELD_COLUMN + ") VALUES (?, ?, ?, ?)";

	    private static final String CREATE_Statement = "INSERT INTO " + MetaTables.GHOST_VM.getTableName() +
														        " (" + MetaTables.GHOST_VM.getUidvm() + ") VALUES (?)";


	    private static final String GETHEADERATTRIBUTE_Statement = "SELECT <F> FROM <T> WHERE <PK> = ? AND <J>";
	    
	    private static final String GETATTRIBUTE_Statement = "SELECT <F> FROM <T> WHERE <PK> = ?";
		
		private static final String SETCUSTOMATTRIBUTE_Statement = "UPDATE " +  MetaTables.GHOST_VM.getTableName() + " SET <CATR>" +
		                                                          " = ? WHERE uidvm = ?";
		
		private static final String GETCUSTOMATTRIBUTE_Statement = "SELECT <CATR> FROM " +  MetaTables.GHOST_VM.getTableName() +
                                                                  " WHERE " + MetaTables.GHOST_VM.getUidvm() + " = ?";
	    
		private void initializeGhostCRUD() {
			String READ_Statement = "SELECT " + this.getVMInsertColumn() + " FROM " + MetaTables.GHOST_VM.getTableName() + " WHERE " + MetaTables.GHOST_VM.getUidvm() + " = ?";
//		    String INSERT_Statement = "INSERT INTO " + MetaTables.GHOST_VM.getTableName() +
//									           " (" + MetaTables.GHOST_VM.getUidvm() +
//									           ", " + GhostDbStaticVariables.GHOST_VM_POINTER_COLUMN +
//									           ", " + GhostDbStaticVariables.GHOST_VM_POINTER_TABLE_COLUMN +
//									           ", " + GhostDbStaticVariables.GHOST_VM_POINTER_FIELD_COLUMN +
//									           ", " + this.getInsertColumnName() + ") VALUES (?, ?, ?, ?, ?)";
			
		    String INSERT_Statement = "INSERT INTO " + MetaTables.GHOST_VM.getTableName() +
	           " (" + MetaTables.GHOST_VM.getUidvm() +
	           ", " + this.getVMInsertColumn() + ") VALUES (?, ?)";		    
		    String UPDATE_Statement = "UPDATE " + MetaTables.GHOST_VM.getTableName() + " SET " + this.getVMInsertColumn() + " = ? WHERE " + MetaTables.GHOST_VM.getUidvm() + " = ?";
		    
		    _gCRUD.put(GhostCRUDOperationType.Insert,INSERT_Statement);
	    	_gCRUD.put(GhostCRUDOperationType.Update,UPDATE_Statement);
	    	_gCRUD.put(GhostCRUDOperationType.Read,READ_Statement);
			_gCRUD.put(GhostCRUDOperationType.Create,CREATE_Statement);
	    	_gCRUD.put(GhostCRUDOperationType.Get,GETUIDVM_SEQUENCE_Statement);
	    	_gCRUD.put(GhostCRUDOperationType.Delete,REMOVE_Statement);
	    	_gCRUD.put(GhostCRUDOperationType.Lock,READ_Statement + " FOR UPDATE");	    	
		}
		
		
		//Used for Attribute Functions
		@SuppressWarnings("rawtypes")
		private static final Class<com.java.ghost.DBObject.AbstractDBObject> thisClass = com.java.ghost.DBObject.AbstractDBObject.class;
		private static Method privateMethodSetArgumentSetCustomAttribute;
		private static Method privateMethodSetArgumentSetCustomDateAttribute;
		
		private static Method privateMethodSetArgumentSetGetCustomAttribute;	
		private static Method privateMethodGetReturnObjectGetCustomAttribute;
		private static Method privateMethodGetReturnObjectGetCustomDateAttribute;
		
		
		private static Method privateMethodSetArgumentSetGetHeaderAttribute;
		private static Method privateMethodGetReturnObjectGetHeaderAttribute;
		
		private static Method privateMethodSetArgumentSetGetAttribute;
		private static Method privateMethodGetReturnObjectGetAttribute;
		
		static
		{
			try {
				privateMethodSetArgumentSetGetHeaderAttribute = thisClass.getDeclaredMethod("setGetHeaderAttributeArguments", GhostStaticVariables.oraclePreparedParams);
				privateMethodSetArgumentSetGetHeaderAttribute.setAccessible(true);
				
				privateMethodGetReturnObjectGetHeaderAttribute = thisClass.getDeclaredMethod("getGetHeaderAttributeReturnObject", GhostStaticVariables.oracleResultSetParams);			
				privateMethodGetReturnObjectGetHeaderAttribute.setAccessible(true);
				
				privateMethodSetArgumentSetGetAttribute = thisClass.getDeclaredMethod("setGetAttributeArguments", GhostStaticVariables.oraclePreparedParams);
				privateMethodSetArgumentSetGetAttribute.setAccessible(true);
				
				privateMethodGetReturnObjectGetAttribute = thisClass.getDeclaredMethod("getGetAttributeReturnObject", GhostStaticVariables.oracleResultSetParams);			
				privateMethodGetReturnObjectGetAttribute.setAccessible(true);
				
				privateMethodSetArgumentSetCustomAttribute = thisClass.getDeclaredMethod("setCustomAttributeArguments", GhostStaticVariables.oraclePreparedParams);
				privateMethodSetArgumentSetCustomAttribute.setAccessible(true);
				
				privateMethodSetArgumentSetCustomDateAttribute = thisClass.getDeclaredMethod("setCustomDateAttributeArguments", GhostStaticVariables.oraclePreparedParams);
				privateMethodSetArgumentSetCustomDateAttribute.setAccessible(true);
				
				privateMethodSetArgumentSetGetCustomAttribute = thisClass.getDeclaredMethod("setGetCustomAttributeArguments", GhostStaticVariables.oraclePreparedParams);
				privateMethodSetArgumentSetGetCustomAttribute.setAccessible(true);
				
				privateMethodGetReturnObjectGetCustomAttribute = thisClass.getDeclaredMethod("getGetCustomAttributeReturnObject", GhostStaticVariables.oracleResultSetParams);			
				privateMethodGetReturnObjectGetCustomAttribute.setAccessible(true);
				
				privateMethodGetReturnObjectGetCustomDateAttribute = thisClass.getDeclaredMethod("getGetCustomDateAttributeReturnObject", GhostStaticVariables.oracleResultSetParams);			
				privateMethodGetReturnObjectGetCustomDateAttribute.setAccessible(true);
				
			} catch (SecurityException e) {
				logger.error(e.getMessage(),e);
				throw new GhostRuntimeException(e);
			} catch (NoSuchMethodException e) {
				logger.error(e.getMessage(),e);
				throw new GhostRuntimeException(e);
			}
			
		}
		
		
	private final void setGetAttributeArguments(OraclePreparedStatement ocs, GhostParameterArray paramArray){
        	try {
    			ocs.setNUMBER(1, (NUMBER) (paramArray.getParameter(1)));
    		} catch (SQLException e) {
    			logger.error(e.getMessage(),e);
    			throw new GhostRuntimeException(e);
    		}
    }
    
    private final Object getGetAttributeReturnObject(OracleResultSet orset, GhostParameterArray paramArray){
    	try {
			return  orset.getString(1);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			throw new GhostRuntimeException(e);
		}
    }	
		
	private final void setGetHeaderAttributeArguments(OraclePreparedStatement ocs, GhostParameterArray paramArray){
        	try {
    			ocs.setNUMBER(1, (NUMBER) (paramArray.getParameter(1)));
    		} catch (SQLException e) {
    			logger.error(e.getMessage(),e);
    			throw new GhostRuntimeException(e);
    		}
    }
    
    private final Object getGetHeaderAttributeReturnObject(OracleResultSet orset, GhostParameterArray paramArray){
    	try {
			return  orset.getString(1);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			throw new GhostRuntimeException(e);
		}
    }
    	
    private final void setCustomAttributeArguments(OraclePreparedStatement ocs, GhostParameterArray paramArray){
    	try {
			ocs.setCHAR(1, (CHAR) (paramArray.getParameter(1)));
			ocs.setNUMBER(2, (NUMBER) (paramArray.getParameter(2)));
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
    }
    
    private final void setGetCustomAttributeArguments(OraclePreparedStatement ocs, GhostParameterArray paramArray){
    	try {
			ocs.setNUMBER(1, (NUMBER) (paramArray.getParameter(1)));
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
    }
    
    
    private final void setCustomDateAttributeArguments(OraclePreparedStatement ocs, GhostParameterArray paramArray){
    	try {
			ocs.setTIMESTAMP(1, (TIMESTAMP) (paramArray.getParameter(1)));
			ocs.setNUMBER(2, (NUMBER) (paramArray.getParameter(2)));
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
    }
    
    
    
    private final Object getGetCustomAttributeReturnObject(OracleResultSet orset, GhostParameterArray paramArray){
    	try {
			return  orset.getString(1);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			throw new GhostRuntimeException(e);
		}
    }
    
    private final Object getGetCustomDateAttributeReturnObject(OracleResultSet orset, GhostParameterArray paramArray){
    	try {
			return  orset.getTimestamp(1);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			throw new GhostRuntimeException(e);
		}
    }
		
		protected GhostCRUD getOverrideGCRUD(){return null;};
		
		{
			initializeGhostCRUD();
		}
		
		
		private void initializeObject(tableType table, IMetaField valueColumn, DBID livesWithId){			
			setTable(table);	    	
			setLivesAtValueColumn(valueColumn);
			setLivesAtIdColumn(table.getPrimaryKey());
	    	setAccessQueryRemote(generateRemoteSQL());
	    	setLivesWithId(livesWithId);
		}
		
		
		public AbstractDBObject(IMetaField valueColumn) {
			executeDBStatement(GhostCRUDOperationType.Create);
			initializeObject((tableType) MetaTables.GHOST_VM, valueColumn, this.getKey());
		}
		
		public AbstractDBObject(IMetaField valueColumn, inputObjectType io){
			if(this.hasOverrideGCRUD()){
	    		_gCRUD.mergeCRUD(this.getOverrideGCRUD());
	    	}
	    	executeDBStatement(GhostCRUDOperationType.Insert, convertToOracleObject(io));
	    	initializeObject((tableType) MetaTables.GHOST_VM, valueColumn, this.getKey());
	    }
		
		
		public AbstractDBObject(tableType table, IMetaField valueColumn, DBID livesWithId){
			if(this.hasOverrideGCRUD()){
	    		_gCRUD.mergeCRUD(this.getOverrideGCRUD());
	    	}
	    	executeDBStatement(GhostCRUDOperationType.Create);
	    	initializeObject(table, valueColumn, livesWithId);
	    }
		
		
		
		public AbstractDBObject(tableType table, IMetaField valueColumn, DBID livesWithId, inputObjectType io){
			if(this.hasOverrideGCRUD()){
	    		_gCRUD.mergeCRUD(this.getOverrideGCRUD());
	    	}
	    	executeDBStatement(GhostCRUDOperationType.Insert, convertToOracleObject(io));
	    	initializeObject(table, valueColumn, livesWithId);
	    	setIsEmpty(false);
	    }
    
	    
	    //Used when you wish to create an object that overrides CRUD SQL and want to set _uidvm ( object already created
	    // on DB by function return result such as Add.
	    public AbstractDBObject(tableType table, IMetaField valueColumn, DBID livesWithId, DBID uidvm) {
	    	initializeGhostCRUD();
	    	if(this.hasOverrideGCRUD()){
	    		_gCRUD.mergeCRUD(this.getOverrideGCRUD());
	    	}
	    	try {
				_uidvm = new NUMBER(new BigDecimal(uidvm.geID()));
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
				throw new GhostRuntimeException(e);
			}
	    	//isEmpty might be set wrong!
//	    	setIsEmpty(false);
	    	initializeObject(table, valueColumn, livesWithId);
		}

	    protected abstract boolean hasOverrideGCRUD();			
		
		public abstract IMetaField getVMInsertColumn();
		protected abstract outputObjectType convertToOracleObject(inputObjectType s);
		protected abstract void setOPC(OraclePreparedStatement opc, int pos, Object o) throws SQLException;
		//protected abstract getValueType getResultSetObject(OracleResultSet oraset, int pos) throws SQLException;
		
		public final int getNumberOfUpdateRecords(){return _numberOfUpdateRecords;}
		
				
		public final void setIsEmpty(boolean value){
			_isEmpty = value;
		}
		
		public final void setIsRemoteTable(boolean value){
			_isRemoteTable = value;
		}
				
		public final boolean isEmpty(){return _isEmpty;}
		
		public final boolean isRemoteTable(){return _isRemoteTable;}
		
		
	    protected final String getSQLStatement(GhostCRUDOperationType sqlType){
	    	return _gCRUD.get(sqlType);
	    }
	    
	    public final NUMBER getOracleKey(){ return _uidvm;};
	    
	    public final DBID getKey() {
	    	
	    	try {
				return new DBID(_uidvm.bigDecimalValue());
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
				throw new GhostRuntimeException(e);
			}
	    	
	    };
	    
	    public final void setKey(DBID key){
	    	try {
				_uidvm = new NUMBER(new BigDecimal(key.geID()));
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
				throw new GhostRuntimeException(e);
			}
	    }
	    
	    
	    public final void setOracleKey(NUMBER uidvm){_uidvm = uidvm;};
	    
	    public void updateInfo(tableType table, IMetaField valueColumn) {
	    	setTable(table);
	    	setLivesAtValueColumn(valueColumn);
	    	setAccessQueryRemote(generateRemoteSQL());
		}
	    
	    protected void setAccessQueryRemote(String accessQueryRemote) {
	    	_accessQueryRemote = accessQueryRemote;			
		}

		protected void setLivesAtValueColumn(IMetaField valueColumn) {
	    	_livesAtValueColumn = valueColumn;			
		}

		public boolean equals(Object obj){
	    	if(this == obj){
				return true;
			}
			if(obj!=null && (obj instanceof IDBObject)){
				try {
					return getUidvm().bigDecimalValue().compareTo( ((IDBObject<inputObjectType, getValueType, sizeReturnType, tableType>)obj).getOracleKey().bigDecimalValue() ) == 0;
				} catch (SQLException e) {
					logger.error(e.getMessage(),e);
					throw new GhostRuntimeException(e);
				}	
			}
			return false;
	    }
	    
	    public int hashCode() {
	    	//TODO: hashcode function ok?
		    int[] keyArray = {_uidvm.hashCode(),31,_uidvm.hashCode()};
		    return GhostHash.hashFunction(keyArray , 0, 3, 11);
		  }
	    
	    //For queries that don't have a return object
	    protected final getValueType executeDBStatement(GhostCRUDOperationType typeOfStatement){
	    	outputObjectType oNull = null;
	    	return executeDBStatement(typeOfStatement, oNull);
	    }
	    
	    protected final getValueType executeDBStatement(GhostCRUDOperationType typeOfStatement, Connection conn){
	    	outputObjectType oNull = null;
	    	return executeDBStatement(typeOfStatement, oNull, conn);
	    }
	    
//	    //Used for Lock Statement or others that need same conneciton/session to work
//	    protected final OracleConnection executeLockDBStatement() throws SQLException{
//	    	OraclePreparedStatement opc = null;
//	    	OracleConnection conn = null;
//	    	OracleResultSet orset = null;	    	
//	    	
//	    	try{
//	    	    conn = (OracleConnection) GhostJDBCUtils.getConnection();
//	    	    opc = (OraclePreparedStatement) conn.prepareStatement(getSQLStatement(GhostCRUDOperationType.Lock));
//				opc.setNUMBER(1,_uidvm);
//				orset = (OracleResultSet) opc.executeQuery();
//				return conn;
//	    	    
//	    	} catch (SQLException e){
//	      	  logger.error(e.getMessage(),e);
//	      	  throw e;
//	        }
//	        finally{
//	      	  try{
//	      		  if(orset != null){
//	      			  orset.close();
//	      		  }
//	      		  if(opc != null){
//	      			  opc.close();
//	      		  }
////	      		  if(conn != null){
////	      	    	  conn.close();
////	      	      }
//	      	  }catch(SQLException e){
//	      		  logger.error(e.getMessage(),e);
//	      		  throw e;
//	      	  }
//	        }
//	    }

	    
	    
	    protected final getValueType executeDBStatement(GhostCRUDOperationType typeOfStatement, outputObjectType o, Connection conn){
	    	return executeDBStatement( typeOfStatement,  o, conn, false);
	    }
	    
	    protected final getValueType executeDBStatement(GhostCRUDOperationType typeOfStatement, outputObjectType o){
	    	try {
				return executeDBStatement( typeOfStatement,  o, GhostJDBCUtils.getConnection(), true);
			} catch (SQLException e) {
				 logger.error(e.getMessage(),e);
				 throw new GhostRuntimeException(e);
			}	    	
	    }
	    
	    //For queries that return an object and are part of CRUD
	    protected final getValueType executeDBStatement(GhostCRUDOperationType typeOfStatement, outputObjectType o, Connection conn, boolean closeConnection){
	    	OraclePreparedStatement opc = null;
//	    	OracleConnection conn = null;
	    	OracleResultSet orset = null;
	    	getValueType returnValue = null;
	    	
	    	try{
//    	    	conn = (OracleConnection) GhostJDBCUtils.getConnection();
	    	    
	    	    switch (typeOfStatement){
	    	    case       Create : opc = (OraclePreparedStatement) conn.prepareStatement(getSQLStatement(GhostCRUDOperationType.Get));
									orset = (OracleResultSet) opc.executeQuery();
									orset.next();
									_uidvm = orset.getNUMBER(1);
									opc = (OraclePreparedStatement) conn.prepareStatement(getSQLStatement(GhostCRUDOperationType.Create));
	    	                        opc.setNUMBER(1,_uidvm);
	    	                        _numberOfUpdateRecords = opc.executeUpdate();
	    	                        break;
	    	    
	    	    case       Insert : opc = (OraclePreparedStatement) conn.prepareStatement(getSQLStatement(GhostCRUDOperationType.Get));
									orset = (OracleResultSet) opc.executeQuery();
									orset.next();
									_uidvm = orset.getNUMBER(1);
									opc = (OraclePreparedStatement) conn.prepareStatement(getSQLStatement(GhostCRUDOperationType.Insert));
									opc.setNUMBER(1,_uidvm);
	    	                        this.setOPC(opc, 2, o);
	    	                        _numberOfUpdateRecords = opc.executeUpdate();
	    	                        break;
	    	                        
	    	    case       Update : opc = (OraclePreparedStatement) conn.prepareStatement(getSQLStatement(GhostCRUDOperationType.Update));
	    	                        this.setOPC(opc, 1, o);
						            opc.setNUMBER(2,_uidvm);
						            _numberOfUpdateRecords = opc.executeUpdate();
						            break;
						            
	    	    case         Read : 
	    	    	                String statement;	    	    	                
	    	    	                if(!isRemoteTable()){
	    	    	                	statement = getSQLStatement(GhostCRUDOperationType.Read);
	    	    	                }else{
	    	    	                	statement = _accessQueryRemote;
	    	    	                }
	    	    	                
	    	    	                opc = (OraclePreparedStatement) conn.prepareStatement(statement);	    	    	                
						            opc.setNUMBER(1,_uidvm);
						            orset = (OracleResultSet) opc.executeQuery();
						            if(orset.next()){
						            	returnValue = (getValueType)(orset.getObject(1)); // this.getResultSetObject(orset, 1);
						            }
						            break;
						            
	    	    case       Delete : opc = (OraclePreparedStatement) conn.prepareStatement(getSQLStatement(GhostCRUDOperationType.Delete));
						            opc.setNUMBER(1,_uidvm);
						            _numberOfUpdateRecords = opc.executeUpdate();
						            break;
			    
	    	    case         Lock : opc = (OraclePreparedStatement) conn.prepareStatement(getSQLStatement(GhostCRUDOperationType.Lock));
						            opc.setNUMBER(1,_uidvm);
						            orset = (OracleResultSet) opc.executeQuery();
						            break;
		
						            
	    	    }
	    	    
	    	} catch (SQLException e){
	      	  logger.error(e.getMessage(),e);
	      	  throw new GhostRuntimeException(e);	      	  
	        }
	        finally{
	      	  try{
	      		  if(orset != null){
	      			  orset.close();
	      		  }
	      		  if(opc != null){
	      			  opc.close();
	      		  }
	      		  if((closeConnection) && (conn != null)){
	      	    	  conn.close();
	      	      }
	      	  }catch(SQLException e){
	      		  logger.error(e.getMessage(),e);
	      		  throw new GhostRuntimeException(e);
	      	  }
	        }
	        
	        return returnValue;
	    }
	    
	    public final int getNumberOfRecordsUpdated(){return _numberOfUpdateRecords;}
	    
		@Override
		public getValueType getValue() {
			return (getValueType) executeDBStatement(GhostCRUDOperationType.Read);
		}
		
		@Override
		public void setValue(inputObjectType io) {
			executeDBStatement(GhostCRUDOperationType.Update, convertToOracleObject(io));
			_isEmpty = false;
			_isRemoteTable = false;
		}
				
		@Override
		public void clear() {
			executeDBStatement(GhostCRUDOperationType.Delete);
			_uidvm = null;
		}
		
		public final NUMBER getUidvm() {
			return _uidvm;
		}
		
		public final GhostCRUD getGCRUD() {
			return _gCRUD;
		}				
		
		public tableType getTable() {
			return _table;
		}

		public void setTable(tableType table) {
			_table = table;
		}
		
		public IMetaField getValueColumn() {
			return _livesAtValueColumn;
		}

		public void setLivesAtColumn(IMetaField valueColumn) {
			_livesAtValueColumn = valueColumn;
		}
		
		public IMetaField getLivesAtIdColumn() {
			return _livesAtIdColumn;
		}

		public void setLivesAtIdColumn(IMetaField idColumn) {
			_livesAtIdColumn = idColumn;
		}
				
		public DBID getLivesWithId() {
			return _livesWithId;
		}

		public void setLivesWithId(DBID id) {
			_livesWithId = id;
		}
		
		
		public final void setCustomAttribute(IGhostCustomAttribute attribute, String value){
			GhostParameterArray paramArray = new GhostParameterArray();
			switch(attribute.getDBType()){
			case VARCHAR: 
						try {
							paramArray.putParameter(1, new CHAR(value,GhostDBStaticVariables.dbCharacterSet));
							paramArray.putParameter(2, getOracleKey());
							
							String tmp = SETCUSTOMATTRIBUTE_Statement.replace("<CATR>",(MetaTables.GHOST_VM).getCustomAttributeField(attribute).getColumnName());
							logger.debug("SetCustomAttribute query: " + GhostVariableWrapper.wrapVariable(tmp));
							executeDBStatement(tmp,paramArray,privateMethodSetArgumentSetCustomAttribute,true,null,null);
						} catch (SQLException e) {
							logger.error(e.getMessage(),e);
							throw new GhostRuntimeException(e," Possible conversion error in setting value: " + GhostVariableWrapper.wrapVariable(value));
						}
						break;
			case DATE: throw new GhostRuntimeException("Cannot set attribute " + GhostVariableWrapper.wrapVariable(attribute) + " with passed in string value" + GhostVariableWrapper.wrapVariable(value) +  ". Mismatch in types." );
			default: throw new GhostRuntimeException("Unknown Attribute Type while setting Custom Attribute for DB Object : " + GhostVariableWrapper.wrapVariable(attribute));
			}
		}
		
		public final void setCustomAttribute(IGhostCustomAttribute attribute, IGDate gDate){
			GhostParameterArray paramArray = new GhostParameterArray();
			switch(attribute.getDBType()){
			case DATE: 
						paramArray.putParameter(1, new TIMESTAMP(new java.sql.Timestamp(gDate.getTimeInMillis())));
						paramArray.putParameter(2, getOracleKey());
					
						String tmp = SETCUSTOMATTRIBUTE_Statement.replace("<CATR>",(MetaTables.GHOST_VM).getCustomAttributeField(attribute).getColumnName());
						logger.debug("SetCustomDateAttribute query: " + GhostVariableWrapper.wrapVariable(tmp));
						executeDBStatement(tmp,paramArray,privateMethodSetArgumentSetCustomDateAttribute,true,null,null);
						break;
			case VARCHAR: throw new GhostRuntimeException("Cannot set attribute " + GhostVariableWrapper.wrapVariable(attribute) + " with passed in string value" + GhostVariableWrapper.wrapVariable(gDate) +  ". Mismatch in types." );
			default: throw new GhostRuntimeException("Unknown Attribute Type while setting Custom Date Attribute for DB Object : " + GhostVariableWrapper.wrapVariable(attribute));
			}
		}

		public final String getCustomAttribute(IGhostCustomAttribute attribute) {
			GhostParameterArray paramArray = new GhostParameterArray();
			paramArray.putParameter(1, getOracleKey());
			String tmp = GETCUSTOMATTRIBUTE_Statement.replace("<CATR>",(MetaTables.GHOST_VM).getCustomAttributeField(attribute).getColumnName());
			logger.debug("GetCustomAttribute query: " + GhostVariableWrapper.wrapVariable(tmp));
			//If coming from a Date Attribute it should return a date string?
			return (String)  executeDBStatement(tmp,paramArray,privateMethodSetArgumentSetGetCustomAttribute,false,null,privateMethodGetReturnObjectGetCustomAttribute);
		}
		
		public final IGDate getCustomDateAttribute(IGhostCustomAttribute attribute) {
			GhostParameterArray paramArray = new GhostParameterArray();
			switch(attribute.getDBType()){
			case DATE:
						paramArray.putParameter(1, getOracleKey());
						String tmp = GETCUSTOMATTRIBUTE_Statement.replace("<CATR>",(MetaTables.GHOST_VM).getCustomAttributeField(attribute).getColumnName());
						logger.debug("GetCustomDateAttribute query: " + GhostVariableWrapper.wrapVariable(tmp));
						return new GDate( (java.sql.Timestamp) executeDBStatement(tmp,paramArray,privateMethodSetArgumentSetGetCustomAttribute,false,null,privateMethodGetReturnObjectGetCustomDateAttribute) );
		    default: throw new GhostRuntimeException("Attribute Type does not contain Date datatype! : " + GhostVariableWrapper.wrapVariable(attribute));
			}
		}	
		
		
		public final String getHeaderAttribute(IMetaChildTable childTable, GhostAttributeEnum attribute) {
			GhostParameterArray paramArray = new GhostParameterArray();
			logger.debug("primary key: " + GhostVariableWrapper.wrapVariable(_livesWithId.toString()));
	    	try {//TODO: Assume primary key is number and single field
				paramArray.putParameter(1, new NUMBER(_livesWithId));
			
				List<IMetaTable> tables = new ArrayList<IMetaTable>();
				IMetaField attributeField = childTable.getParentTable().getAttributeField(attribute);
				
				String tmp = GETHEADERATTRIBUTE_Statement.replace("<F>", attributeField.getAssociatedTableAlias() + GhostDBStaticVariables.PERIOD + attributeField.getColumnName());
				
				IMetaTable currentTable = ((IMetaTable)childTable);
				
				tables.add(attributeField.getAssociatedTable());
				tables.add(currentTable);
				
				tmp = tmp.replace("<T>", GhostDBStaticVariables.getConcatTableString(tables, GhostDBStaticVariables.COMMA));		
				tmp = tmp.replace("<PK>", currentTable.getAlias() + GhostDBStaticVariables.PERIOD + currentTable.getPrimaryKey());
				
				tmp = tmp.replace("<J>", currentTable.getAlias() + GhostDBStaticVariables.PERIOD + childTable.getKeyField() + 
						           GhostDBStaticVariables.SPACE + GhostDBStaticVariables.EQUALS + GhostDBStaticVariables.SPACE +
						           ((IMetaTable)childTable.getParentTable()).getAlias() + GhostDBStaticVariables.PERIOD +
						           childTable.getParentTable().getForeignKeyField());
				
		        //"SELECT <F> FROM <T> WHERE <PK> = ? AND <J>"
				logger.debug("GetHeaderAttribute query: " + GhostVariableWrapper.wrapVariable(tmp));
		    	return (String)  executeDBStatement(tmp,paramArray,privateMethodSetArgumentSetGetHeaderAttribute,false,null,privateMethodGetReturnObjectGetHeaderAttribute);
	    	} catch (SQLException e) {
				logger.error(e.getMessage(),e);
				throw new GhostRuntimeException(e,"getHeaderAttribute method: Possible number conversion error for internal key");
			}
		}
		
		public final String getAttribute(GhostAttributeEnum attribute){
			GhostParameterArray paramArray = new GhostParameterArray();
//			logger.debug("primary key: " + GhostVariableWrapper.wrapVariable(primaryKey));
	    	try {//TODO: Assume primary key is number and single field
				paramArray.putParameter(1, new NUMBER(_livesWithId));
			
				//List<IMetaTable> tables = new ArrayList<IMetaTable>();
				
				IMetaField attributeField = _table.getAttributeField(attribute);
				
				String tmp = GETATTRIBUTE_Statement.replace("<F>", attributeField.getAssociatedTableAlias() + GhostDBStaticVariables.PERIOD + attributeField.getColumnName());
				
//				IMeta_table current_table = ((IMeta_table)child_table);				
//				_tables.add(attributeField.getAssociated_table());
//				_tables.add(_table);
				
				//tmp = tmp.replace("<T>", GhostDbStaticVariables.getConcat_tableString(_tables, GhostDbStaticVariables.COMMA));		
				tmp = tmp.replace("<T>", _table.getTableName() + GhostDBStaticVariables.SPACE + _table.getAlias());
				tmp = tmp.replace("<PK>", _table.getAlias() + GhostDBStaticVariables.PERIOD + _table.getPrimaryKey());
				
		        //"SELECT <F> FROM <T> WHERE <PK> = ? AND <J>"
				logger.debug("GetAttribute query: " + GhostVariableWrapper.wrapVariable(tmp));
		    	return (String)  executeDBStatement(tmp,paramArray,privateMethodSetArgumentSetGetAttribute,false,null,privateMethodGetReturnObjectGetAttribute);
	    	} catch (SQLException e) {
				logger.error(e.getMessage(),e);
				throw new GhostRuntimeException(e,"getAttribute method: Possible number conversion error for internal key");
			}
		}
		
		
		
	    protected String getLocatorQuery(IDBObject<inputObjectType, getValueType, sizeReturnType, tableType> dbObject, IMetaGhostVariableTable table){
	    	if(dbObject.isRemoteTable()){
				   String query = generateRemoteSQL(table);
				   logger.debug("DBMetaBlob operation blob locator Query: " +GhostVariableWrapper.wrapVariable(query));
				   return query;
	    	}
	    	return GhostDBStaticVariables.EMPTY_STR;
	    }
	    
	    
	    private String generateRemoteSQL(IMetaTable table, IMetaField valueColumn, String additionalColumns){
	    	String modifiedAdditionalColumns = GhostDBStaticVariables.EMPTY_STR;
	    	if(additionalColumns != null){
	    		modifiedAdditionalColumns = GhostDBStaticVariables.COMMA + additionalColumns;
	    	}
	    	return          GhostDBStaticVariables.SELECT_SELECT + GhostDBStaticVariables.SPACE +
					        table.getAlias() + GhostDBStaticVariables.PERIOD + valueColumn + GhostDBStaticVariables.SPACE +
					        modifiedAdditionalColumns +
					        GhostDBStaticVariables.SELECT_FROM + GhostDBStaticVariables.SPACE +
					        MetaTables.GHOST_VM.getTableName() + GhostDBStaticVariables.SPACE + MetaTables.GHOST_VM.getAlias() +
			        	    GhostDBStaticVariables.COMMA + GhostDBStaticVariables.SPACE + table.getTableName() + GhostDBStaticVariables.SPACE + table.getAlias() +
			                GhostDBStaticVariables.SELECT_WHERE + GhostDBStaticVariables.SPACE + MetaTables.GHOST_VM.getUidvm() +
					        GhostDBStaticVariables.EQUALS + GhostDBStaticVariables.DB_BIND_VALUE + GhostDBStaticVariables.SPACE +
					        GhostDBStaticVariables.AND + GhostDBStaticVariables.SPACE + MetaTables.GHOST_VM.getGhostPointer() + 
					        GhostDBStaticVariables.EQUALS + table.getPrimaryKey().getFullyQualifiedTableAliaisWithColumnName();			        
	    }
	    
	    protected String generateRemoteSQL(){
	        return generateRemoteSQL(_table,_livesAtValueColumn,null);
	    }
	    
	    protected String generateRemoteSQL(IMetaGhostVariableTable table){
	        return generateRemoteSQL(table,table.getIMGVTValueColumn(), null);
	    }
	    
	    protected String generateRemoteSQL(IMetaGhostVariableTable table, String additionalColumns){
	        return generateRemoteSQL(table,table.getIMGVTValueColumn(), additionalColumns);
	    }
	    
	    
				
}
