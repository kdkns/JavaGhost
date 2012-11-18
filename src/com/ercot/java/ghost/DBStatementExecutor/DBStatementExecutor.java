package com.ercot.java.ghost.DBStatementExecutor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.OracleResultSet;

import org.apache.log4j.Logger;

import com.ercot.java.ghost.Exceptions.GhostRuntimeException;
import com.ercot.java.ghost.utils.GhostDBProcedureList;
import com.ercot.java.ghost.utils.GhostJDBCUtils;
import com.ercot.java.ghost.utils.GhostParameterArray;

@SuppressWarnings("rawtypes")
public class DBStatementExecutor {
	protected static Logger logger= Logger.getLogger("DBStatementExecutor");
	public int _numberOfUpdateRecords = 0;
	
	protected static enum StatementType{
		functionOrProcedure,
		stringQuery,
		queries,
		stringResultSetQuery
	};
	
	private static class CommonStatementWrapper{
		
		public static final Object executeStatement(Object callingClass, DBSEIDataStructure dBSEIDataStructure){
			ICoreStatementLogic coreStatementLogic =  dBSEIDataStructure.getICoreStatementLogic();
			try{
	    		        
	    		coreStatementLogic.executeStatementLogic(callingClass,
	    				                               dBSEIDataStructure);
	    		
	    	} catch (SQLException e){
		        logger.error(e.getMessage(),e);
		        throw new GhostRuntimeException(e);
		    } catch (IllegalArgumentException e) {
		       	logger.error(e.getMessage(),e);
		       	throw new GhostRuntimeException(e);
			} catch (IllegalAccessException e) {
				logger.error(e.getMessage(),e);
				throw new GhostRuntimeException(e);
			} catch (InvocationTargetException e) {
				logger.error(e.getMessage(),e);
				throw new GhostRuntimeException(e);
			}
	        finally{
	      	  try{
	      		  if(coreStatementLogic.getResultSet() != null){
		      		coreStatementLogic.getResultSet().close();
		          }
	      		  if(coreStatementLogic.getPreparedStatement() != null) {
	      			coreStatementLogic.getPreparedStatement().close();
	      		  }
	      		  if(coreStatementLogic.getCallableStatement() != null){
	      			coreStatementLogic.getCallableStatement().close();
	      		  }
	      		  if(coreStatementLogic.getConnection() != null){
	      			coreStatementLogic.getConnection().close();
	      			coreStatementLogic.setConnection(null);
	      	      }
	      	  }catch(SQLException e){
	      		  logger.error(e.getMessage(),e);
	      		  throw new GhostRuntimeException(e);
	      	  }
	        }
	    	
	    	return coreStatementLogic.getReturnValue();
		}
		
		public static final ICoreStatementLogic executeStatementNoClose(Object callingClass, DBSEIDataStructure dBSEIDataStructure){
			ICoreStatementLogic coreStatementLogic =  dBSEIDataStructure.getICoreStatementLogic();
			try{
	    		        
	    		coreStatementLogic.executeStatementLogic(callingClass,
	    				                               dBSEIDataStructure);
	    		
	    	} catch (SQLException e){
		        logger.error(e.getMessage(),e);
		        throw new GhostRuntimeException(e);
		    } catch (IllegalArgumentException e) {
		       	logger.error(e.getMessage(),e);
		       	throw new GhostRuntimeException(e);
			} catch (IllegalAccessException e) {
				logger.error(e.getMessage(),e);
				throw new GhostRuntimeException(e);
			} catch (InvocationTargetException e) {
				logger.error(e.getMessage(),e);
				throw new GhostRuntimeException(e);
			}
			
	    	return coreStatementLogic;
		}
	}
	
	protected static final ICoreStatementLogic getICoreStatementLogic(StatementType statementType){
		switch(statementType){
			case queries: return _cSLForQueries;
			case functionOrProcedure: return _cSLForProcedureOrFunction;
			case stringQuery: return _cSLForStringQueries;
			case stringResultSetQuery: return _cSLForStringResultSetQueries;
			default: throw new GhostRuntimeException("No Statement type defined for <" + statementType + "> in DBStatementExecutor class.");
		}
	}
	
	
	//For custom procedure or function calls
	private static final ICoreStatementLogic _cSLForProcedureOrFunction = new CoreStatementLogic(){
	    @Override
		public void executeStatementLogic(Object callingClass, DBSEIDataStructure dBSEIDataStructure) throws SQLException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
	    	   
	    	this.setConnection( GhostJDBCUtils.getConnection());	    	
	   	    this.setCallableStatement( this.getConnection().prepareCall(dBSEIDataStructure.getGProcList().getSQLStatement(dBSEIDataStructure.getOperationType())));
	   	    
	   	    if(dBSEIDataStructure.getGParamArray()!=null){
	   	    	if(dBSEIDataStructure.getGParamArray().size()!=0){
	   	    	    try {
	   					dBSEIDataStructure.getGProcList().getSetArgumentsMethod(dBSEIDataStructure.getOperationType()).invoke(callingClass,this.getCallableStatement(),dBSEIDataStructure.getGParamArray());
	   				} catch (IllegalArgumentException e) {
	   					logger.error(e.getMessage(),e);
	   					throw new GhostRuntimeException(e);
	   				} catch (IllegalAccessException e) {
	   					logger.error(e.getMessage(),e);
	   					throw new GhostRuntimeException(e);
	   				} catch (InvocationTargetException e) {
	   					logger.error(e.getMessage(),e);
	   					throw new GhostRuntimeException(e);
	   				}
	   	    	}
	   	    }
	   	    
	   	    // Due to the fact that a Query may return a primiative, and primatives are not Objects, 
	   	    // and the getObject method is random in choosing what Type of object it returns, 
	   	    // the inheritied class may override the return type if needed, else just specify the type.
	   	    if(dBSEIDataStructure.getGProcList().isProcedure(dBSEIDataStructure.getOperationType())){
	   	    	this.getCallableStatement().executeQuery();
	   	    }else{
	   	    	@SuppressWarnings("unused")
				int numberOfUpdateRecords = this.getCallableStatement().executeUpdate();
	       	    if(dBSEIDataStructure.getGProcList().isReturnObjectAnObject(dBSEIDataStructure.getOperationType())){
	       	    	this.setReturnValue((dBSEIDataStructure.getGProcList().getClassReturnType(dBSEIDataStructure.getOperationType())).cast(this.getCallableStatement().getObject(1)));
	       	    }else{
	       	    	this.setReturnValue(dBSEIDataStructure.getGProcList().getReturnObject(dBSEIDataStructure.getOperationType()).invoke(callingClass, this.getCallableStatement(), dBSEIDataStructure.getGParamArray()));
	       	    }
	   	    }
	    }
     };
	 
	
  protected Object executeDBFunctionStatement(GhostDBProcedureList gProcList, java.lang.Enum operationType, GhostParameterArray gParamArray){
	  return CommonStatementWrapper.executeStatement(this, new DBSEIDataStructure(StatementType.functionOrProcedure, gProcList ,operationType,gParamArray));
  }

	  
//For custom Queries but NOT procedure or function calls
  private static final ICoreStatementLogic _cSLForQueries = new CoreStatementLogic(){
	    @Override
		public void executeStatementLogic(Object callingClass, DBSEIDataStructure dBSEIDataStructure) throws SQLException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
	    	   
	    	this.setConnection( GhostJDBCUtils.getConnection());	    	
	   	    this.setPreparedStatement( this.getConnection().prepareStatement(dBSEIDataStructure.getGProcList().getSQLStatement(dBSEIDataStructure.getOperationType())));
	   	    
	   	    if(dBSEIDataStructure.getGParamArray()!=null){
	   	    	if(dBSEIDataStructure.getGParamArray().size()!=0){
	   	    	    try {
	   	    	    	dBSEIDataStructure.getGProcList().getSetArgumentsMethod(dBSEIDataStructure.getOperationType()).invoke(callingClass,this.getPreparedStatement(),dBSEIDataStructure.getGParamArray());
	   				} catch (IllegalArgumentException e) {
	   					logger.error(e.getMessage(),e);
	   					throw new GhostRuntimeException(e);
	   				} catch (IllegalAccessException e) {
	   					logger.error(e.getMessage(),e);
	   					throw new GhostRuntimeException(e);
	   				} catch (InvocationTargetException e) {
	   					logger.error(e.getMessage(),e);
	   					throw new GhostRuntimeException(e);
	   				}
	   	    	}
	   	    }
	   	    
	   	 if(dBSEIDataStructure.getGProcList().getIsUpdate(dBSEIDataStructure.getOperationType())){
 	    	int numberOfUpdateRecords = this.getPreparedStatement().executeUpdate();
 	    	this.setReturnValue(Integer.valueOf(numberOfUpdateRecords));
 	    }else{
 	    	this.setResultSet((OracleResultSet) this.getPreparedStatement().executeQuery());
 	    	this.getResultSet().next();
 	    	
 	    	if(dBSEIDataStructure.getGProcList().isReturnObjectAnObject(dBSEIDataStructure.getOperationType())){
 	    	 	this.setReturnValue((dBSEIDataStructure.getGProcList().getClassReturnType(dBSEIDataStructure.getOperationType())).cast(this.getResultSet().getObject(1)));
	    	}else{
	    	   	this.setReturnValue(dBSEIDataStructure.getGProcList().getReturnObject(dBSEIDataStructure.getOperationType()).invoke(callingClass, this.getResultSet(), dBSEIDataStructure.getGParamArray()));
	    	     }
 	    }	
	    }
   };
  
  
    //For custom Queries but NOT procedure or function calls
    protected Object executeDBStatement(GhostDBProcedureList gProcList, java.lang.Enum operationType, GhostParameterArray gParamArray){
	     return CommonStatementWrapper.executeStatement(this, new DBSEIDataStructure(StatementType.queries, gProcList ,operationType,gParamArray));
	}
    
    
    //For custom Queries but NOT procedure or function calls, but uses query in String format. Used for dynamically generated queries.
  	private static final ICoreStatementLogic _cSLForStringQueries = new CoreStatementLogic(){
  	    @Override
  		public void executeStatementLogic(Object callingClass, DBSEIDataStructure dBSEIDataStructure) throws SQLException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
  	    	   
  	    	this.setConnection( GhostJDBCUtils.getConnection());	    	
  	   	    this.setPreparedStatement( this.getConnection().prepareStatement(dBSEIDataStructure.getQuery()) );
  	   	    
  	   	    if(dBSEIDataStructure.getGParamArray()!=null){
  	   	    	if(dBSEIDataStructure.getGParamArray().size()!=0){
  	   	    	    try {
     	   	    	   //dBSEIDataStructure.getGProcList().getSetArgumentsMethod(dBSEIDataStructure.getOperationType()).invoke(callingClass,this.getCallableStatement(),dBSEIDataStructure.getGParamArray());
  	   	    	         dBSEIDataStructure.getSetArgumentMethod().invoke(callingClass, this.getPreparedStatement(), dBSEIDataStructure.getGParamArray());
  	   				} catch (IllegalArgumentException e) {
  	   					logger.error(e.getMessage(),e);
  	   				    throw new GhostRuntimeException(e);
  	   				} catch (IllegalAccessException e) {
  	   					logger.error(e.getMessage(),e);
  	   				    throw new GhostRuntimeException(e);
  	   				} catch (InvocationTargetException e) {
  	   					logger.error(e.getMessage(),e);
  	   			        throw new GhostRuntimeException(e);
  	   				}
  	   	    	}
  	   	    }
  	   	if(dBSEIDataStructure.getIsUpdate()){
	    	int numberOfUpdateRecords = this.getPreparedStatement().executeUpdate();
	    	this.setReturnValue(Integer.valueOf(numberOfUpdateRecords));
	    }else{
	    	this.setResultSet(this.getPreparedStatement().executeQuery());
	    	this.getResultSet().next();
	    	if(dBSEIDataStructure.getReturnClass() != null){
    	    	this.setReturnValue(dBSEIDataStructure.getReturnClass().cast(this.getResultSet().getObject(1)) );
    	    }else{
    	    	this.setReturnValue(dBSEIDataStructure.getReturnMethod().invoke(callingClass, this.getResultSet(), dBSEIDataStructure.getGParamArray()));
    	    }
  	     }
  	    }
  	};
    
    
  //For custom Queries but NOT procedure or function calls, but uses query in String format. Used for dynamically generated queries.
    protected Object executeDBStatement(String query, GhostParameterArray gParamArray, Method setArgumentMethod, boolean isUpdate, Class returnClass, Method returnMethod){
	     return CommonStatementWrapper.executeStatement(this, new DBSEIDataStructure(StatementType.stringQuery, query, gParamArray, setArgumentMethod, isUpdate, returnClass, returnMethod));
	}
  	

    
  //For custom Queries but NOT procedure or function calls, but uses query in String format and returns a Result Set
  	private static final ICoreStatementLogic _cSLForStringResultSetQueries = new CoreStatementLogic(){
  	    @Override
  		public void executeStatementLogic(Object callingClass, DBSEIDataStructure dBSEIDataStructure) throws SQLException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
  	    	   
  	    	this.setConnection( GhostJDBCUtils.getConnection());	    	
  	   	    this.setPreparedStatement( this.getConnection().prepareStatement(dBSEIDataStructure.getQuery()) );
  	   	    
  	   	    if(dBSEIDataStructure.getGParamArray()!=null){
	   	    	if(dBSEIDataStructure.getGParamArray().size()!=0){
	   	    	    try {
	   	    	         dBSEIDataStructure.getSetArgumentMethod().invoke(callingClass, this.getPreparedStatement(), dBSEIDataStructure.getGParamArray());
	   				} catch (IllegalArgumentException e) {
	   					logger.error(e.getMessage(),e);
	   					throw new GhostRuntimeException(e);
	   				} catch (IllegalAccessException e) {
	   					logger.error(e.getMessage(),e);
	   					throw new GhostRuntimeException(e);
	   				} catch (InvocationTargetException e) {
	   					logger.error(e.getMessage(),e);
	   					throw new GhostRuntimeException(e);
	   				}
	   	    	}
	   	    }
  	   	    
	    	this.setResultSet(this.getPreparedStatement().executeQuery());	    	
	    	this.setReturnValue(this.getResultSet());
  	    }
  	};
    
    protected ICoreStatementLogic executeResultSetDBStatement(String query, GhostParameterArray gParamArray, Method setArgumentMethod){
    	_cSLForStringResultSetQueries.setIsReturnTypeResultSet(true);    	
    	return CommonStatementWrapper.executeStatementNoClose(this, new DBSEIDataStructure(StatementType.stringResultSetQuery, query, gParamArray, setArgumentMethod, false, OracleResultSet.class, null) );
    }
    
    
    //Used when needing to use a passed in connection for GhostQuery object
    protected ResultSet executeResultSetDBStatement(Connection conn,PreparedStatement opc,GhostDBProcedureList gProcList, java.lang.Enum operationType, GhostParameterArray gParamArray){
    	try{
    	    if(gParamArray!=null){
    	    	if(gParamArray.size()!=0){
		    	    try {
						gProcList.getSetArgumentsMethod(operationType).invoke(this, opc, gParamArray);
					} catch (IllegalArgumentException e) {
						logger.error(e.getMessage(),e);
						throw new GhostRuntimeException(e);
					} catch (IllegalAccessException e) {
						logger.error(e.getMessage(),e);
						throw new GhostRuntimeException(e);
					} catch (InvocationTargetException e) {
						logger.error(e.getMessage(),e);
						throw new GhostRuntimeException(e);
					}
    	    	}
    	    }
    	    return opc.executeQuery();
    	} catch (SQLException e){
	        logger.error(e.getMessage(),e);
	        throw new GhostRuntimeException(e);
	    } catch (IllegalArgumentException e) {
	       	logger.error(e.getMessage(),e);
	       	throw new GhostRuntimeException(e);
		}
    }

    
}

