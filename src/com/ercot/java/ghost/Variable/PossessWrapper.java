package com.ercot.java.ghost.Variable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import oracle.jdbc.OracleCallableStatement;
import oracle.sql.CHAR;
import oracle.sql.NUMBER;

import org.apache.log4j.Logger;

import com.ercot.java.ghost.Annotations.AdditionalPossessColumnsMethod;
import com.ercot.java.ghost.DBStatementExecutor.DBStatementExecutor;
import com.ercot.java.ghost.Exceptions.GhostRuntimeException;
import com.ercot.java.ghost.GhostAttributes.GhostPossessMapping;
import com.ercot.java.ghost.MetaTableTypes.IMetaField;
import com.ercot.java.ghost.MetaTableTypes.IMetaGhostVariableTable;
import com.ercot.java.ghost.MetaTableTypes.IMetaTable;
import com.ercot.java.ghost.QueryConstructors.GhostTableFilter;
import com.ercot.java.ghost.utils.GhostDBProcedureList;
import com.ercot.java.ghost.utils.GhostDBStaticVariables;
import com.ercot.java.ghost.utils.GhostPair;
import com.ercot.java.ghost.utils.GhostParameterArray;
import com.ercot.java.ghost.utils.GhostStaticVariables;
import com.ercot.java.ghost.utils.GhostUtils;
import com.ercot.java.ghost.utils.GhostVariableWrapper;

public class PossessWrapper extends DBStatementExecutor{
	private static Logger logger = Logger.getLogger("PossessWrapper");
	
	protected enum DbFunctionTypes{
		possess, possessBulkOnly//, possessBulkMetaBlob,
		
	}
	protected static GhostDBProcedureList gProcList = new GhostDBProcedureList();


	private static final String POSSESS_RETURNCOUNT_STATMENT = GhostDBStaticVariables.DB_FUNCTION_CALL_RETURN + GhostDBStaticVariables.GHOST_BLOB_UTIL_PKG + 
			                                                   GhostDBStaticVariables.PERIOD + GhostDBStaticVariables.GHOST_BLOB_UTIL_POSSESS_RETURN_COUNT + 
															   GhostDBStaticVariables.GHOST_BLOB_UTIL_POSSESS_RETURN_COUNT_ARGS + GhostDBStaticVariables.CLOSE_BRACKET;
	
//	private static final String POSSESSBULKMETABLOB_STATMENT = GhostDBStaticVariables.DB_FUNCTION_CALL_RETURN + GhostDBStaticVariables.GHOST_BLOB_UTIL_PKG + 
//			                                                                   GhostDBStaticVariables.PERIOD + GhostDBStaticVariables.GHOST_BLOB_UTIL_POSSESSBULK + 
//                                                                               GhostDBStaticVariables.GHOST_BLOB_UTIL_POSSESSBULK_ARGS + GhostDBStaticVariables.CLOSE_BRACKET;


	private static final String POSSESS_STATMENT = GhostDBStaticVariables.DB_FUNCTION_CALL_RETURN + GhostDBStaticVariables.GHOST_BLOB_UTIL_PKG + 
                                                       GhostDBStaticVariables.PERIOD + GhostDBStaticVariables.GHOST_BLOB_UTIL_POSSESS + 
                                                       GhostDBStaticVariables.GHOST_BLOB_UTIL_POSSESS_ARGS + GhostDBStaticVariables.CLOSE_BRACKET;

 static{
		
		try {
            Class<com.ercot.java.ghost.Variable.PossessWrapper> thisClass = com.ercot.java.ghost.Variable.PossessWrapper.class;        
             
			Method privateMethodSetArgument = null;
			Method privateGetReturnObject = null;
			
			//PossessMetaBlobReturnCount
			privateMethodSetArgument = thisClass.getDeclaredMethod("setPossessReturnCountStatmentArguments", GhostStaticVariables.oracleCallableParams);			
			privateMethodSetArgument.setAccessible(true);
			
			privateGetReturnObject = thisClass.getDeclaredMethod("getPossessReturnCountReturnObject", GhostStaticVariables.oracleCallableParams);			
			privateGetReturnObject.setAccessible(true);
			
			gProcList.put(DbFunctionTypes.possessBulkOnly, POSSESS_RETURNCOUNT_STATMENT,
				      false,
				      false,
				      false,
				      NUMBER.class,
				      privateMethodSetArgument,
				      privateGetReturnObject);
			
//			//PossessBulkMetaBlob
//			privateMethodSetArgument = thisClass.getDeclaredMethod("setPossessBulkMetaBlobStatmentArguments", GhostStaticVariables.oracleCallableParams);			
//			privateMethodSetArgument.setAccessible(true);
//			
//			privateGetReturnObject = thisClass.getDeclaredMethod("getPossessBulkMetaBlobReturnObject", GhostStaticVariables.oracleCallableParams);			
//			privateGetReturnObject.setAccessible(true);
//			
//			gProcList.put(DbFunctionTypes.possessBulkMetaBlob, POSSESSBULKMETABLOB_STATMENT,
//				      false,
//				      false,
//				      false,
//				      NUMBER.class,
//				      privateMethodSetArgument,
//				      privateGetReturnObject);
			
			
			//Possess
			privateMethodSetArgument = thisClass.getDeclaredMethod("setPossessStatmentArguments",GhostStaticVariables.oracleCallableParams);			
			privateMethodSetArgument.setAccessible(true);
			
			privateGetReturnObject = thisClass.getDeclaredMethod("getPossessReturnObject", GhostStaticVariables.oracleCallableParams);			
			privateGetReturnObject.setAccessible(true);
			
			gProcList.put(DbFunctionTypes.possess, POSSESS_STATMENT,
				      false,
				      false,
				      false,
				      NUMBER.class,
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

 
//	@SuppressWarnings("unused")
//	private void setPossessMetaBlobStatmentArguments(OracleCallableStatement ops, GhostParameterArray paramArray){
//		try {
//			ops.registerOutParameter(1, Types.BIGINT);
//			ops.setCHAR(2,(CHAR) paramArray.getParameter(1));
//			ops.setNUMBER(3,(NUMBER) paramArray.getParameter(2));
//			ops.setCHAR(4,(CHAR) paramArray.getParameter(3));
//			ops.setCHAR(5,(CHAR) paramArray.getParameter(4));
//			ops.setCHAR(6,(CHAR) paramArray.getParameter(5));
//		} catch (SQLException e) {
//			logger.error(e.getMessage(),e);
//		}
//	}
	
		
//	@SuppressWarnings("unused")
//	private Object getPossessMetaBlobReturnObject(OracleCallableStatement ocs, GhostParameterArray paramArray){
//		try {
//			return  ocs.getNUMBER(1);
//		} catch (SQLException e) {
//			logger.error(e.getMessage(), e);
//		}
//		return null;
//	}
	
	
	@SuppressWarnings("unused")
	private void setPossessStatmentArguments(OracleCallableStatement ops, GhostParameterArray paramArray){
		try {
			ops.registerOutParameter(1, Types.BIGINT);
			ops.setCHAR(2,(CHAR) paramArray.getParameter(1));
			ops.setNUMBER(3,(NUMBER) paramArray.getParameter(2));
			ops.setCHAR(4,(CHAR) paramArray.getParameter(3));
			ops.setCHAR(5,(CHAR) paramArray.getParameter(4));
			ops.setCHAR(6,(CHAR) paramArray.getParameter(5));
//			ops.setCHAR(7,(CHAR) paramArray.getParameter(6));
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
	}
	
	@SuppressWarnings("unused")
	private Object getPossessReturnObject(OracleCallableStatement ocs, GhostParameterArray paramArray){
		try {
			return  ocs.getNUMBER(1);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			throw new GhostRuntimeException(e);
		}
	}
	
	@SuppressWarnings("unused")
	private void setPossessReturnCountStatmentArguments(OracleCallableStatement ops, GhostParameterArray paramArray){
		try {
			ops.registerOutParameter(1, Types.BIGINT);
			ops.setCHAR(2,(CHAR) paramArray.getParameter(1));
			ops.setNUMBER(3,(NUMBER) paramArray.getParameter(2));
			ops.setCHAR(4,(CHAR) paramArray.getParameter(3));
			ops.setCHAR(5,(CHAR) paramArray.getParameter(4));
			ops.setCHAR(6,(CHAR) paramArray.getParameter(5));
			//paramArray.setBindParametersToStatment(4,3, ops);
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
	}
	
	@SuppressWarnings("unused")
	private Object getPossessReturnCountReturnObject(OracleCallableStatement ocs, GhostParameterArray paramArray){
		try {
			return  ocs.getNUMBER(1);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			throw new GhostRuntimeException(e);
		}
	}
	
//	@SuppressWarnings("unused")
//	private void setPossessBulkMetaBlobStatmentArguments(OracleCallableStatement ops, GhostParameterArray paramArray){
//		try {
//			ops.registerOutParameter(1, Types.BIGINT);
//			ops.setCHAR(2,(CHAR) paramArray.getParameter(1));
//			ops.setNUMBER(3,(NUMBER) paramArray.getParameter(2));
//		} catch (SQLException e) {
//			logger.error(e.getMessage(),e);
//		}
//	}
//	
//	@SuppressWarnings("unused")
//	private Object getPossessBulkMetaBlobReturnObject(OracleCallableStatement ocs, GhostParameterArray paramArray){
//		try {
//			return  ocs.getNUMBER(1);
//		} catch (SQLException e) {
//			logger.error(e.getMessage(), e);
//		}
//		return null;
//	}
	
	
	
	@SuppressWarnings("unchecked")
	private <tableType extends IMetaGhostVariableTable> GhostPair<List<IMetaField>, List<IMetaField>> getAdditionalPossessColumnsList(Class<? extends IGhostVariable<?,?>> objectClass, tableType mTable){
		Method[] methodList = objectClass.getDeclaredMethods();
		Object o = null;
		for(int x=0;x<methodList.length;x++){
				if(methodList[x].isAnnotationPresent(AdditionalPossessColumnsMethod.class)){
				    try {
						o = methodList[x].invoke(null,mTable);
						break;
					} catch (IllegalAccessException e) {
						logger.error(e.getMessage(),e);
						throw new GhostRuntimeException(e);
					} catch (IllegalArgumentException e) {
						logger.error(e.getMessage(),e);
						throw new GhostRuntimeException(e);
					} catch (InvocationTargetException e) {
						logger.error(e.getMessage(),e);
						throw new GhostRuntimeException(e);
					}
				}
		}
		if(o==null){
			o = new GhostPair<List<IMetaField>,List<IMetaField>>(new ArrayList<IMetaField>(), new ArrayList<IMetaField>());
		}		
		return (GhostPair<List<IMetaField>,List<IMetaField>>) o;
	}
	

	
		public <tableType extends IMetaGhostVariableTable> Object execute(IPossessStatement possessStatement,
				                                                          DbFunctionTypes dbFunctionTypes,
				                                                          IGhostCollection<?> ghostCollection,
				                                                          Class<? extends IGhostVariable<?,?>> objectClass,
				                                                          tableType mTable, List<IMetaTable> secondaryTables,
				                                                          GhostTableFilter filter,
				                                                          GhostPossessMapping gpm) {
			//collectionType mbCollection = null;
			Object result = null;
	    	String partitionField = GhostDBStaticVariables.NULL;
	    	String addComma = GhostDBStaticVariables.EMPTY_STR;
	    	String alias= mTable.getAlias() + GhostDBStaticVariables.PERIOD;
	    	String customAttributes = GhostDBStaticVariables.EMPTY_STR;
	    	String customColumns = GhostDBStaticVariables.EMPTY_STR;
	    	
	    	String valueColumnName =  mTable.getIMGVTValueColumn().getColumnName();
	    	
	    	if(gpm != null){
	    		customColumns = gpm.getColumnOrderString();
	    		customAttributes = gpm.getSQLColumns();
	    	}
	    	
	    	if(secondaryTables!=null){
	    		addComma = GhostDBStaticVariables.COMMA;
	    	}
	    	
	    	if(mTable.usePartitionField()){
	    		partitionField = alias + mTable.getPartitionFieldName();
	    	}
	    	
	    	GhostPair<List<IMetaField>,List<IMetaField>> possessAdditonalColumns = getAdditionalPossessColumnsList(objectClass,mTable);
	    	String builtPossessColumns = GhostUtils.buildMetaFieldColumnsNoAlias(possessAdditonalColumns.getLeft() , GhostDBStaticVariables.COMMA);
	    	String vmTableAdditionalPossessColumns = GhostUtils.buildMetaFieldColumnsPlain(possessAdditonalColumns.getRight() , GhostDBStaticVariables.COMMA);
	    	
	    	//Add comma if columns exist
	    	if(!builtPossessColumns.isEmpty()){
	    		builtPossessColumns = GhostDBStaticVariables.COMMA + builtPossessColumns;
	    	}
	    	
	    	if(!vmTableAdditionalPossessColumns.isEmpty()){
	    		vmTableAdditionalPossessColumns = GhostDBStaticVariables.COMMA + vmTableAdditionalPossessColumns;
	    	}
	    	
	    	// Changed to use get Associated Table Alias in case column is null or comes from different table
//	    	TODO: Fix this alias issue to work properly in case table isn't in table list?
	    	String query = GhostDBStaticVariables.SELECT_SELECT +GhostDBStaticVariables.SPACE + GhostDBStaticVariables.GHOST_UID_VM_SEQUENCE + ".NEXTVAL, "
							         + mTable.getPrimaryKey().getFullyQualifiedTableAliaisWithColumnName() + GhostDBStaticVariables.COMMA
							         + partitionField + GhostDBStaticVariables.COMMA
							         + GhostDBStaticVariables.SINGLE_QUOTE + mTable.getTableName() + GhostDBStaticVariables.SINGLE_QUOTE + GhostDBStaticVariables.COMMA
							         + GhostDBStaticVariables.SINGLE_QUOTE + mTable.getPrimaryKey() + GhostDBStaticVariables.SINGLE_QUOTE + GhostDBStaticVariables.COMMA
							         + GhostDBStaticVariables.SINGLE_QUOTE + valueColumnName + GhostDBStaticVariables.SINGLE_QUOTE + GhostDBStaticVariables.COMMA
							         + GhostDBStaticVariables.GHOST_COLLECTION_REPLACE_STRING  + GhostDBStaticVariables.COMMA
							         + GhostDBStaticVariables.GHOST_POSSESS_REPLACE_STRING  
							         + builtPossessColumns
							         + customAttributes
							         + GhostDBStaticVariables.SPACE + GhostDBStaticVariables.SELECT_FROM + GhostDBStaticVariables.SPACE
							         + mTable.getTableName() + GhostDBStaticVariables.SPACE + mTable.getAlias()+ addComma
							         + GhostDBStaticVariables.getConcatTableString(secondaryTables,GhostDBStaticVariables.COMMA)
							         + GhostDBStaticVariables.SPACE + GhostDBStaticVariables.SELECT_WHERE1E1 + GhostDBStaticVariables.SPACE
	     					         + filter.getFilterSQL();
	     	
	    	//Fix bind values to be used by DB function
	    	query = query.replaceAll("\\"+GhostStaticVariables.CHAR_QUESTION_MARK, GhostStaticVariables.CHAR_COLAN+"B");
	    	
	    	logger.debug("Posses Query: " + GhostVariableWrapper.wrapVariable(query));
	    	
	    			
			GhostParameterArray paramArray = new GhostParameterArray();
	    	try {
	    		//mbCollection = (collectionType) collectionTypeClass.newInstance();//GhostCollectionFactory.getGhostCollection();
				paramArray.putParameter(1, new CHAR(query, GhostDBStaticVariables.dbCharacterSet));
				paramArray.putParameter(2, ghostCollection.getLocalDBConvertedCollectionId());
				paramArray.setAndBuildBindString(3, filter);
				paramArray.putParameter(4, new CHAR(vmTableAdditionalPossessColumns, GhostDBStaticVariables.dbCharacterSet));
				paramArray.putParameter(5, new CHAR(customColumns, GhostDBStaticVariables.dbCharacterSet));
//						paramArray.putParameter(6, new CHAR(dbObjectType, GhostDBStaticVariables.dbCharacterSet));
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
				throw new GhostRuntimeException(e,"A SQL Exception occured when posessing objects. Please check previous error in log.");
			}
	    	
	    	String myFormat = "yyyy-MM-dd HH:mm:ss.SSS";
			SimpleDateFormat formatter = new SimpleDateFormat(myFormat);
	    	logger.debug("Start possess: " + formatter.format(new Date()));
	    	NUMBER returnValue = (NUMBER) executeDBFunctionStatement(gProcList, dbFunctionTypes, paramArray);
//	    	NUMBER returnValue = new NUMBER(1);
	    	logger.debug("End possess: " + formatter.format(new Date()));
	    	
	    	logger.debug("Possession ID: " + GhostVariableWrapper.wrapVariable(returnValue));
	    	
//			    	ghostCollection.bulkAdd(possessionId,mTable, objectClass);
	    	possessStatement.bulkAddObjects(ghostCollection,returnValue, mTable, objectClass);
	    	
	    	possessStatement.preProcess(returnValue);
	    	
	    	ghostCollection.bulkAddMetaInfo(mTable,mTable.getIMGVTValueColumn(),mTable.getPrimaryKey());
	    	
	    	possessStatement.setReturnResult(returnValue);
	    	
	    	result = possessStatement.getReturnResult();
			    	
	    	return result;
		}
		
	}