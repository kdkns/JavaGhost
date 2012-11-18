package com.ercot.java.ghost.DBObject;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.sql.CHAR;
import oracle.sql.NUMBER;
import oracle.sql.TIMESTAMP;

import com.ercot.java.ghost.DBStatementExecutor.DBStatementExecutor;
import com.ercot.java.ghost.Exceptions.GhostAttributeDoesNotExistException;
import com.ercot.java.ghost.Exceptions.GhostCollectionObjectMaxReachedException;
import com.ercot.java.ghost.Exceptions.GhostRuntimeException;
import com.ercot.java.ghost.GhostAttributes.AbstractGhostAttributeSet;
import com.ercot.java.ghost.GhostAttributes.GhostAttributeEnum;
import com.ercot.java.ghost.GhostAttributes.GhostAttributeMapping;
import com.ercot.java.ghost.GhostAttributes.GhostAttributeSet;
import com.ercot.java.ghost.GhostAttributes.IAttributeTableList;
import com.ercot.java.ghost.GhostAttributes.IGhostAttribute;
import com.ercot.java.ghost.GhostAttributes.IGhostAttributeSet;
import com.ercot.java.ghost.GhostAttributes.IGhostCustomAttribute;
import com.ercot.java.ghost.MetaTableTypes.IMetaChildTable;
import com.ercot.java.ghost.MetaTableTypes.IMetaField;
import com.ercot.java.ghost.MetaTableTypes.IMetaGhostVariableTable;
import com.ercot.java.ghost.MetaTableTypes.IMetaParentTable;
import com.ercot.java.ghost.MetaTableTypes.IMetaTable;
import com.ercot.java.ghost.MetaTableTypes.Tables.Ghost_VM;
import com.ercot.java.ghost.MetaTableTypes.Tables.MetaTables;
import com.ercot.java.ghost.QueryConstructors.GhostQueryInternal;
import com.ercot.java.ghost.Variable.IGhostVariable;
import com.ercot.java.ghost.utils.GhostDBProcedureList;
import com.ercot.java.ghost.utils.GhostDBStaticVariables;
import com.ercot.java.ghost.utils.GhostParameterArray;
import com.ercot.java.ghost.utils.GhostStaticVariables;
import com.ercot.java.ghost.utils.GhostVariableWrapper;
import com.ercot.java.ghost.utils.IGDate;


public class DBCollection<objectType extends IGhostVariable<?,?>> extends DBStatementExecutor implements IDBCollection<objectType>{
	
//	private static final long serialVersionUID = 1L;
	//private static Logger logger = Logger.getLogger("DBCollection");
	
	protected static final String KEYVALUE_ALIAS = " keyvalue";
	private IMetaField _vmInsertColumn;
	
	private static final int _maxCollectionObjectSize = 66;
	private static final HashMap<Integer,Object> _collectionIdMap = new HashMap<Integer, Object>();
	
	private int _rawCollectionId;
	private MetaInfo _metaInfo;
	
	protected static final int UNASSIGNED_COLLECTION_ID = 0;
	
	@SuppressWarnings("rawtypes")
	private static final Class<DBCollection> thisClass = com.ercot.java.ghost.DBObject.DBCollection.class;
	private static Method privateMethodSetArgumentSetCustomAttribute;
	private static Method privateMethodSetArgumentSetCustomDateAttribute;
	
	private static final String GRABPOSSESIDS_STATEMENT = GhostDBStaticVariables.SELECT_SELECT + GhostDBStaticVariables.SPACE + MetaTables.GHOST_VM.getUidvm() + 
			                                              GhostDBStaticVariables.COMMA + MetaTables.GHOST_VM.getGhostPointer() +
												          GhostDBStaticVariables.SPACE + GhostDBStaticVariables.SELECT_FROM + GhostDBStaticVariables.SPACE + MetaTables.GHOST_VM.getTableName() + 
												          GhostDBStaticVariables.SPACE + GhostDBStaticVariables.SELECT_WHERE + GhostDBStaticVariables.SPACE +
												          MetaTables.GHOST_VM.getGhostPossessionId() +
												          GhostDBStaticVariables.EQUALS + GhostDBStaticVariables.QUESTIONMARK;
	
	private static final String GRABCOLLECTIONIDS_STATEMENT_TOKEN = "<GIC>";
	private static final String GRABCOLLECTIONIDS_STATEMENT = GhostDBStaticVariables.SELECT_SELECT + GhostDBStaticVariables.SPACE + MetaTables.GHOST_VM.getUidvm() + 
			                                                  GhostDBStaticVariables.COMMA + MetaTables.GHOST_VM.getGhostPointer() + GhostDBStaticVariables.COMMA +
			                                                  MetaTables.GHOST_VM.getGhostPointerTable() + GhostDBStaticVariables.COMMA +
			                                                  GRABCOLLECTIONIDS_STATEMENT_TOKEN +
														      GhostDBStaticVariables.SPACE + GhostDBStaticVariables.SELECT_FROM + GhostDBStaticVariables.SPACE + MetaTables.GHOST_VM.getTableName() + 
														      GhostDBStaticVariables.SPACE + GhostDBStaticVariables.SELECT_WHERE + 
														      GhostDBStaticVariables.createCollectionClause(GhostDBStaticVariables.QUESTIONMARK);
	
	{
		_metaInfo = new MetaInfo();
		try{
			privateMethodSetArgumentSetCustomAttribute = thisClass.getDeclaredMethod("setSetCustomAttributeArguments", GhostStaticVariables.oraclePreparedParams);			
			privateMethodSetArgumentSetCustomAttribute.setAccessible(true);
			
			privateMethodSetArgumentSetCustomDateAttribute = thisClass.getDeclaredMethod("setSetCustomDateAttributeArguments", GhostStaticVariables.oraclePreparedParams);			
			privateMethodSetArgumentSetCustomDateAttribute.setAccessible(true);
			
		} catch (NoSuchMethodException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
	    }
	}
	
	@SuppressWarnings("unused")
	private void setPossessIDsStatementArguments(OraclePreparedStatement ops, GhostParameterArray paramArray){
		try {
			ops.setNUMBER(1, (NUMBER) paramArray.getParameter(1));
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
	}
	
	@SuppressWarnings("unused")
	private void setCollectionIDsStatementArguments(OraclePreparedStatement ops, GhostParameterArray paramArray){
		try {
			ops.setNUMBER(1, (NUMBER) paramArray.getParameter(1));
			ops.setNUMBER(2, (NUMBER) paramArray.getParameter(2));
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
	}
	
	@SuppressWarnings("unused")
	private void setSetCustomAttributeArguments(OraclePreparedStatement ocs, GhostParameterArray paramArray){
    	try {
			ocs.setCHAR(1, (CHAR) (paramArray.getParameter(1)));
			ocs.setNUMBER(2, (NUMBER) (paramArray.getParameter(2)));
			ocs.setNUMBER(3, (NUMBER) (paramArray.getParameter(2)));
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
    }
	
	@SuppressWarnings("unused")
	private void setSetCustomDateAttributeArguments(OraclePreparedStatement ocs, GhostParameterArray paramArray){
    	try {
			ocs.setTIMESTAMP(1, (TIMESTAMP) (paramArray.getParameter(1)));
			ocs.setNUMBER(2, (NUMBER) (paramArray.getParameter(2)));
			ocs.setNUMBER(3, (NUMBER) (paramArray.getParameter(2)));
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
    }
	
	private synchronized void setInstanceCollectionId(){
		boolean foundSlot = false;
		for(int x = 1; x <= _maxCollectionObjectSize; x++){
			if(!_collectionIdMap.containsKey(x)){
				_collectionIdMap.put(x, this);				
				_rawCollectionId = x;
				foundSlot = true;
				break;
			}
		}	
		if(!foundSlot){
			throw new GhostCollectionObjectMaxReachedException();
		}
	}
		
	public DBCollection(){
		setInstanceCollectionId();
	}
	
	private NUMBER convertCollecitonIdToDBCID() {
		//Creating a number in powers of 2, shifting x-1 bit over for mask
		// Ex.  x = 4
		//      0000 ( 4 bit number )
		//      0001 ( value of 1 ) 
		//  Shift 3,(x-1), bits to the left
		//      1000 ( value of 16)
		
		if(getRawCollectionId()==UNASSIGNED_COLLECTION_ID){
			setInstanceCollectionId();
		}
		
		return new NUMBER(1<<(getRawCollectionId()-1));
    }
	
	
	public MetaInfo getMetaInfo(){
		return _metaInfo;
	}
	
	protected HashMap<Integer, Object> getCollectionIdMap() {		
		return _collectionIdMap;
	}
	
	protected int getRawCollectionId(){
		return _rawCollectionId;
	}
	
	protected void setRawCollectionId(int rawCollectionId){
		_rawCollectionId = rawCollectionId;
	}
	
	//TODO: Make into a hash table lookup instead to improve performance?
	public IMetaGhostVariableTable lookupTable(String tableName){
		for(IMetaGhostVariableTable table :  getMetaInfo().getTables()){
			if(table.getTableName().equalsIgnoreCase(tableName)){
				return table;
			}
		}
		throw new GhostRuntimeException("No Meta Table matching string table name found for " + GhostVariableWrapper.wrapVariable(tableName) + " inside Collection's metaInfo");
	}
	
	protected interface ISQLQueryAdditionalInformation{
		public String getAdditionalColumns();
		public String getAdditonalTables();
		public String getAdditonalClasues();
	}
	
	
	protected class SQLQueryAdditionalInformation implements ISQLQueryAdditionalInformation{
		String _additionalColumns;
		String _additonalTables;
		String _additonalClasues;
		
		public SQLQueryAdditionalInformation(String additionalColumns,
				String additonalTables, String additonalClasues) {
			super();
			_additionalColumns = additionalColumns;
			_additonalTables = additonalTables;
			_additonalClasues = additonalClasues;
		}
		public String getAdditionalColumns() {
			return _additionalColumns;
		}
		public String getAdditonalTables() {
			return _additonalTables;
		}
		public String getAdditonalClasues() {
			return _additonalClasues;
		}
	}
	
	protected class MetaInfo{
		@SuppressWarnings("unused")
		private class MetaSQLDatum{
			private String _selectFieldQuery;
			private String _selectFromQuery;
			private String _selectWhereQuery;
			private IMetaGhostVariableTable _table;
			private IMetaField _column;
			private IMetaField _idColumn;
			
			public MetaSQLDatum(IMetaGhostVariableTable table, IMetaField column, IMetaField idColumn, IMetaField insertColumn){
				_table = table;
				_column = column;
				_idColumn = idColumn;
				//Changed query to use new NVL command to pick correct blob resutlt value. By default table sets
				// blob column value to null. If any aciton is performed on blob value, the result is stored in local
				// ghost vm table. At that point the selection of the blob goes from the pointer table value to local
				// blob value.
				
				String ghostVMInsertColumn = insertColumn.getColumnName();
				
				_selectFieldQuery =   GhostDBStaticVariables.SELECT_SELECT + GhostDBStaticVariables.SPACE + 
						              MetaTables.GHOST_VM.getRangeId() + GhostDBStaticVariables.SPACE + "r," +						              
//						              "DECODE( LENGTH" + GhostDBStaticVariables.OPEN_PARENTHESES + ghostVMInsertColumn + GhostDBStaticVariables.CLOSE_PARENTHESES + 
//				                      GhostDBStaticVariables.COMMA + "0" + GhostDBStaticVariables.COMMA + column + GhostDBStaticVariables.COMMA + GhostDBStaticVariables.NULL + GhostDBStaticVariables.COMMA +				                      
//        		                      column + GhostDBStaticVariables.COMMA + ghostVMInsertColumn + GhostDBStaticVariables.CLOSE_PARENTHESES +
                                      " NVL(" +ghostVMInsertColumn + "," + column +  GhostDBStaticVariables.CLOSE_PARENTHESES +
				                      GhostDBStaticVariables.SPACE + "b_value" + GhostDBStaticVariables.SPACE +  GhostDBStaticVariables.COMMA +
				                      MetaTables.GHOST_VM.getUidvm().getFullyQualifiedTableAliaisWithColumnName() +
				                      GhostDBStaticVariables.COMMA + ghostVMInsertColumn + GhostDBStaticVariables.SPACE + "gvm_insert_column";
				
				if(!(table instanceof Ghost_VM)){
					_selectFromQuery = GhostDBStaticVariables.SPACE + GhostDBStaticVariables.SELECT_FROM + GhostDBStaticVariables.SPACE + 
							           table.getTableName() + GhostDBStaticVariables.SPACE + table.getAlias() + GhostDBStaticVariables.COMMA +
                                       GhostDBStaticVariables.SPACE + MetaTables.GHOST_VM.getTableName() + GhostDBStaticVariables.SPACE + 
					                   MetaTables.GHOST_VM.getAlias();
					
					_selectWhereQuery =   GhostDBStaticVariables.SPACE + GhostDBStaticVariables.SELECT_WHERE + GhostDBStaticVariables.SPACE +
                                          GhostDBStaticVariables.SPACE + MetaTables.GHOST_VM.getAlias() +
					                      GhostDBStaticVariables.PERIOD + MetaTables.GHOST_VM.getGhostPointer() + GhostDBStaticVariables.SPACE +//TODO: Make a more permanent fix for STring to Number conversion issue
					                      GhostDBStaticVariables.EQUALS +  idColumn.getFullyQualifiedTableAliaisWithColumnName() + GhostDBStaticVariables.STRINGCONCAT + GhostDBStaticVariables.EMPTY_DB_STRING + //GhostDBStaticVariables.SPACE + table.getAlias() + GhostDBStaticVariables.PERIOD +
					                      GhostDBStaticVariables.SPACE + GhostDBStaticVariables.AND + GhostDBStaticVariables.SPACE + "BITAND" + 
					                      GhostDBStaticVariables.OPEN_PARENTHESES + MetaTables.GHOST_VM.getGhostCollectionId() + ", <CID>) = <CID>";
					//TODO: Fix this so we don't include the table unnecessarily
					if(table instanceof IMetaChildTable){
						IMetaChildTable imct = (IMetaChildTable) table;
						if(imct.doIMBTColumnsUseParentTable()){
							IMetaParentTable parentTable = imct.getParentTable();
							
							_selectFromQuery += GhostDBStaticVariables.COMMA + parentTable.getTableName() + GhostDBStaticVariables.SPACE + 
									            parentTable.getAlias();
							
							_selectWhereQuery += GhostDBStaticVariables.SPACE + GhostDBStaticVariables.AND + GhostDBStaticVariables.SPACE +
									             imct.getAlias() + GhostDBStaticVariables.PERIOD + imct.getKeyField() + GhostDBStaticVariables.SPACE + GhostDBStaticVariables.EQUALS +
									             parentTable.getAlias() + GhostDBStaticVariables.PERIOD + parentTable.getForeignKeyField();
									           
						}
					}
					
				   
				}else{
					
					_selectFromQuery = GhostDBStaticVariables.SPACE + GhostDBStaticVariables.SELECT_FROM + GhostDBStaticVariables.SPACE +
							           MetaTables.GHOST_VM.getTableName() + GhostDBStaticVariables.SPACE + 
					                   MetaTables.GHOST_VM.getAlias();
					
				   _selectWhereQuery = GhostDBStaticVariables.SPACE + GhostDBStaticVariables.SELECT_WHERE + GhostDBStaticVariables.SPACE +
						               MetaTables.GHOST_VM.getGhostPointerTable() + " IS NULL" +
					                   GhostDBStaticVariables.SPACE + GhostDBStaticVariables.AND + GhostDBStaticVariables.SPACE + "BITAND" + 
					                   GhostDBStaticVariables.OPEN_PARENTHESES + MetaTables.GHOST_VM.getGhostCollectionId() + ", <CID>) = <CID>";
				}
			    
				if(table.usePartitionField()){
			    	_selectWhereQuery += GhostDBStaticVariables.SPACE + GhostDBStaticVariables.AND + GhostDBStaticVariables.SPACE +
					                     MetaTables.GHOST_VM.getAlias() + GhostDBStaticVariables.PERIOD + MetaTables.GHOST_VM.getGhostPointerPartitionDate() + 
									     GhostDBStaticVariables.SPACE + GhostDBStaticVariables.EQUALS + GhostDBStaticVariables.SPACE + 
									     table.getAlias()+ GhostDBStaticVariables.PERIOD + table.getPartitionFieldName();
			    }
				
				logger.debug("MetaSqlQuery : " + _selectFieldQuery + _selectFromQuery + _selectWhereQuery);
			}
			
			public String getSQLQuery(){
				return _selectFieldQuery + _selectFromQuery + _selectWhereQuery;
			}
			
			public String getSQLQuery(String additionalColumns){
				return _selectFieldQuery + additionalColumns + _selectFromQuery + _selectWhereQuery;
			}
			
			public String getSQLQuery(String additionalColumns, String additonalTables){
				return _selectFieldQuery + additionalColumns + _selectFromQuery + additonalTables + _selectWhereQuery;
			}
			
			public String getSQLQuery(String additionalColumns, String additonalTables, String additonalClasues){
				return _selectFieldQuery + additionalColumns + _selectFromQuery + additonalTables + _selectWhereQuery + additonalClasues;
			}
			
			public IMetaGhostVariableTable getTable(){
				return _table;
			}
		}
		
		private HashMap<IMetaGhostVariableTable,MetaSQLDatum> _selectQueryMap = new HashMap<IMetaGhostVariableTable,MetaSQLDatum>();
		
		public void putInfo(IMetaGhostVariableTable table, IMetaField column, IMetaField idColumn){
			_selectQueryMap.put(table, new MetaSQLDatum(table, column, idColumn, getVMInsertColumn()));
		}
		
//		public void putInfo(GhostMetaBlob gmb){
//			IMetaBlobTable tmp = gmb.getTable();//.getTableName().concat(gmb.getBlobColumn()).toUpperCase();
//			if(!_selectQueryMap.containsKey(tmp)){
//				_selectQueryMap.put(tmp, new MetaBlobSQLDatum(gmb.getTable(), gmb.getLivesAtColumn(), gmb.getIdColumn()));
//			}
//		}
		
		public void putInfo(objectType gv){			
			if(!_selectQueryMap.containsKey(gv.getTable())){
				_selectQueryMap.put(gv.getTable(), new MetaSQLDatum(gv.getTable(), gv.getLivesAtColumn(), gv.getIdColumn(), getVMInsertColumn()));
			}
		}
		
//		public void putInfo(String table, String column, String idColumn){
//			_selectQueryMap.put(table.concat(column).toUpperCase(), new MetaBlobSQLDatum(table, column, idColumn));
//		}
		
//		public String getSQLQuery(String table, String column){
//			return _selectQueryMap.get(table.concat(column).toUpperCase()).getSQLQuery();
//		}
		
//		public String getSQLQuery(GhostMetaBlob gmb){
//			return _selectQueryMap.get(gmb.getTable().getTableName().concat(gmb.getBlobColumn()).toUpperCase()).getSQLQuery();
//		}
		
		public Set<IMetaGhostVariableTable> getTables(){
			return _selectQueryMap.keySet();
		}

		public String getSQLQuery(){
			StringBuffer finalQuery = new StringBuffer();
			Collection<MetaSQLDatum> _sqlCollection = _selectQueryMap.values();
			
			for(Iterator<MetaSQLDatum> x = _sqlCollection.iterator(); x.hasNext();){
				finalQuery.append( x.next().getSQLQuery() );
				if(x.hasNext()){
					finalQuery.append(" UNION ALL ");
				}
			}
			return finalQuery.toString().replaceAll("<CID>", convertCollecitonIdToDBCID().stringValue());
		}
		
//		public IMetaField getIMGVMetaField(){
//			return getVMInsertColumn();
////			for( IMetaGhostVariableTable imgvt : getTables()){
////			    return GhostDBStaticVariables.getInsertColumnBasedOnTableType(imgvt);
////			} 
////			throw new GhostRuntimeException("No tables exist in metaInfo for this collection. Unable to get ghost data column field!");
//		}
		
//		public String getSQLQuery(Map<IMetaTable, String> attributeClauseSet){
//			StringBuffer finalQuery = new StringBuffer();
//			Collection<MetaBlobSQLDatum> _sqlCollection = _selectQueryMap.values();
//			String attributeClause = GhostDbStaticVariables.EMPTRY_STR;
//			MetaBlobSQLDatum mbsd = null;
//			
//			for(Iterator<MetaBlobSQLDatum> x = _sqlCollection.iterator(); x.hasNext();){
//				mbsd = (MetaBlobSQLDatum)x.next();
//				attributeClause = attributeClauseSet.get(mbsd.getTable());
//				finalQuery.append( mbsd.getSQLQuery(attributeClause) );
//				if(x.hasNext()){
//					finalQuery.append(" UNION ALL ");
//				}
//			}
//			return finalQuery.toString().replaceAll("<CID>", convertCollecitonIdToDBCID().stringValue());
//		}
		
		public String getSQLQuery(Map<IMetaTable, ISQLQueryAdditionalInformation> attributeClauseSet){
			StringBuffer finalQuery = new StringBuffer();
			Collection<MetaSQLDatum> _sqlCollection = _selectQueryMap.values();
			MetaSQLDatum mbsd = null;
			
			for(Iterator<MetaSQLDatum> x = _sqlCollection.iterator(); x.hasNext();){
				mbsd = x.next();
				finalQuery.append( mbsd.getSQLQuery(attributeClauseSet.get(mbsd.getTable()).getAdditionalColumns(),
						                            attributeClauseSet.get(mbsd.getTable()).getAdditonalTables(),
						                            attributeClauseSet.get(mbsd.getTable()).getAdditonalClasues()) );
				if(x.hasNext()){
					finalQuery.append(" UNION ALL ");
				}
			}
			return finalQuery.toString().replaceAll("<CID>", convertCollecitonIdToDBCID().stringValue());
		}
		
		public void remove(objectType gmb) {
			_selectQueryMap.remove(gmb.getTable());
		}

		public void clear() {
			_selectQueryMap.clear();
		}

		public String getSQLQuery(String allTablesClause) {
			StringBuffer finalQuery = new StringBuffer();
			Collection<MetaSQLDatum> _sqlCollection = _selectQueryMap.values();
			MetaSQLDatum mbsd = null;
			
			for(Iterator<MetaSQLDatum> x = _sqlCollection.iterator(); x.hasNext();){
				mbsd = x.next();
				finalQuery.append( mbsd.getSQLQuery(allTablesClause) );
				if(x.hasNext()){
					finalQuery.append(" UNION ALL ");
				}
			}
			return finalQuery.toString().replaceAll("<CID>", convertCollecitonIdToDBCID().stringValue());
		}
	}
	
	private static enum DbFunctionTypes{
		getCollectionId, addCollectionId , bulkAddCollectionId, removeBulkCollectionId,
		 setBulkCollectionIdByFieldAndId, setCustomAttribute, possessIds, removeCollectionId, 
		bulkRemoveCollectionId, grabCollectionIds, deleteAllObjects
	}
	
	protected static GhostDBProcedureList gProcList = new GhostDBProcedureList();

	private static final String GETCID_Statement = "SELECT " + MetaTables.GHOST_VM.getGhostCollectionId() + 
                                                   " FROM " + MetaTables.GHOST_VM.getTableName() + 
                                                  " WHERE " + MetaTables.GHOST_VM.getUidvm() + " = ?";
	
	private static final String ADDCID_Statement = "UPDATE " + MetaTables.GHOST_VM.getTableName() +
	                                                " SET " + MetaTables.GHOST_VM.getGhostCollectionId() + 
	                                                  " = " + MetaTables.GHOST_VM.getGhostCollectionId() + 
	                                          " + ? WHERE " + MetaTables.GHOST_VM.getUidvm() + " = ?";
	
	private static final String REMOVECID_Statement = "UPDATE " + MetaTables.GHOST_VM.getTableName() +
	                                                   " SET " + MetaTables.GHOST_VM.getGhostCollectionId() + 
											             " = " + MetaTables.GHOST_VM.getGhostCollectionId() + 
											     " - ? WHERE " + MetaTables.GHOST_VM.getUidvm() + " = ?";

	private static final String BULKADDCID_Statement = "UPDATE " + MetaTables.GHOST_VM.getTableName() +
													    " SET " + MetaTables.GHOST_VM.getGhostCollectionId() + 
														  " = " + MetaTables.GHOST_VM.getGhostCollectionId() + " + ?" +
											   " WHERE BITAND(" + MetaTables.GHOST_VM.getGhostCollectionId() + ", ?) = ?";
	
	private static final String SETBULKCID_BYFIELDANDID_Statement = "UPDATE " + MetaTables.GHOST_VM.getTableName() +
																     " SET " + MetaTables.GHOST_VM.getGhostCollectionId() + 
																	   " = " + MetaTables.GHOST_VM.getGhostCollectionId() + " + ?" +
														           " WHERE " + MetaTables.GHOST_VM.getBulkInsertId() + " = ?";
	
	private static final String REMOVEBULKCID_Statement = "UPDATE " + MetaTables.GHOST_VM.getTableName() +
										                   " SET " + MetaTables.GHOST_VM.getGhostCollectionId() + 
														     " = " + MetaTables.GHOST_VM.getGhostCollectionId() + " - ?" +
											              " WHERE" + GhostDBStaticVariables.createCollectionClause(GhostDBStaticVariables.QUESTIONMARK);

	
	private static final String SETCUSTOMATTRIBUTE_Statement = "UPDATE " +  MetaTables.GHOST_VM.getTableName() + " SET <CATR>" +
                                                              " = ? WHERE" + 
                                                              GhostDBStaticVariables.createCollectionClause(GhostDBStaticVariables.QUESTIONMARK);
	
	private static final String DELETEALLOBJECTS_Statement = "DELETE" + GhostDBStaticVariables.SPACE + GhostDBStaticVariables.SELECT_FROM + 
			                                                 MetaTables.GHOST_VM.getTableName() + GhostDBStaticVariables.SPACE +
												             GhostDBStaticVariables.SELECT_WHERE +
												             GhostDBStaticVariables.createCollectionClause(GhostDBStaticVariables.QUESTIONMARK);
	
	
static{
		
		try {

			Method privateMethodSetArgument = null;
			Method privateGetReturnObject = null;
			//addCID
			privateMethodSetArgument = thisClass.getDeclaredMethod("setAddCIDStatementArguments", GhostStaticVariables.oraclePreparedParams);			
			privateMethodSetArgument.setAccessible(true);
			
			gProcList.put(DbFunctionTypes.addCollectionId, ADDCID_Statement,
				      true,
				      false,
				      false,
				      null,
				      privateMethodSetArgument,
				      null);
			
			//removeCID
			privateMethodSetArgument = thisClass.getDeclaredMethod("setRemoveCIDStatementArguments", GhostStaticVariables.oraclePreparedParams);			
			privateMethodSetArgument.setAccessible(true);
			
			gProcList.put(DbFunctionTypes.removeCollectionId, REMOVECID_Statement,
				      true,
				      false,
				      false,
				      null,
				      privateMethodSetArgument,
				      null);
			
			//setBulkCID
			privateMethodSetArgument = thisClass.getDeclaredMethod("setSetBulkCIDStatementArguments", GhostStaticVariables.oraclePreparedParams);			
			privateMethodSetArgument.setAccessible(true);
			
			gProcList.put(DbFunctionTypes.bulkAddCollectionId, BULKADDCID_Statement,
				      true,
				      false,
				      false,
				      null,
				      privateMethodSetArgument,
				      null);
			
			//setBulkCIDByFieldAndId
			privateMethodSetArgument = thisClass.getDeclaredMethod("setSetBulkCIDByFieldAndIdStatementArguments", GhostStaticVariables.oraclePreparedParams);			
			privateMethodSetArgument.setAccessible(true);
			
			gProcList.put(DbFunctionTypes.setBulkCollectionIdByFieldAndId, SETBULKCID_BYFIELDANDID_Statement,
				      true,
				      false,
				      false,
				      null,
				      privateMethodSetArgument,
				      null);
			
			
			//RemoveBulkCID
			privateMethodSetArgument = thisClass.getDeclaredMethod("setRemoveBulkCIDStatementArguments", GhostStaticVariables.oraclePreparedParams);			
			privateMethodSetArgument.setAccessible(true);
			
			gProcList.put(DbFunctionTypes.removeBulkCollectionId, REMOVEBULKCID_Statement,
				      true,
				      false,
				      false,
				      null,
				      privateMethodSetArgument,
				      null);
			
			//getCID
			privateMethodSetArgument = thisClass.getDeclaredMethod("setGetCIDStatementArguments", GhostStaticVariables.oraclePreparedParams);			
			privateMethodSetArgument.setAccessible(true);
			
			privateGetReturnObject = thisClass.getDeclaredMethod("getGetCIDReturnObject", GhostStaticVariables.oracleResultSetParams);			
			privateGetReturnObject.setAccessible(true);
			
			gProcList.put(DbFunctionTypes.getCollectionId, GETCID_Statement,
				      false,
				      false,
				      false,
				      null,
				      privateMethodSetArgument,
				      privateGetReturnObject);
			
			
			
		
			//PossessIds
			privateMethodSetArgument = thisClass.getDeclaredMethod("setPossessIDsStatementArguments", GhostStaticVariables.oraclePreparedParams);			
			privateMethodSetArgument.setAccessible(true);
			
			gProcList.put(DbFunctionTypes.possessIds, null,
				      false,
				      false,
				      false,
				      NUMBER.class,
				      privateMethodSetArgument,
				      null);
			
			
			//CollectionIds
			privateMethodSetArgument = thisClass.getDeclaredMethod("setCollectionIDsStatementArguments", GhostStaticVariables.oraclePreparedParams);			
			privateMethodSetArgument.setAccessible(true);
			
			gProcList.put(DbFunctionTypes.grabCollectionIds, null,
				      false,
				      false,
				      false,
				      NUMBER.class,
				      privateMethodSetArgument,
				      null);
			
			//DeleteAllObjects
			privateMethodSetArgument = thisClass.getDeclaredMethod("setDeleteAllObjectsStatementArguments", GhostStaticVariables.oraclePreparedParams);			
			privateMethodSetArgument.setAccessible(true);
			
			gProcList.put(DbFunctionTypes.deleteAllObjects, DELETEALLOBJECTS_Statement,
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
	private void setGetCIDStatementArguments(OraclePreparedStatement ops, GhostParameterArray paramArray){
		try {
			ops.setNUMBER(1, (NUMBER) paramArray.getParameter(1));
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
	}
	
	@SuppressWarnings("unused")
	private Object getGetCIDReturnObject(OracleResultSet orset, GhostParameterArray paramArray){
		try {
			return  orset.getNUMBER(1);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			throw new GhostRuntimeException(e);
		}
	}
	
	@SuppressWarnings("unused")
	private void setAddCIDStatementArguments(OraclePreparedStatement ops, GhostParameterArray paramArray){
		try {
			ops.setNUMBER(1, (NUMBER) paramArray.getParameter(1));
			ops.setNUMBER(2, (NUMBER) paramArray.getParameter(2));
			logger.debug("MetaBlob DB Number to be added to collection Id: " + GhostVariableWrapper.wrapVariable((NUMBER) paramArray.getParameter(1)));
			logger.debug("MetaBlob DB Number uidvm for collection Id update: " + GhostVariableWrapper.wrapVariable((NUMBER) paramArray.getParameter(2)));
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
	}
	
	@SuppressWarnings("unused")
	private void setRemoveCIDStatementArguments(OraclePreparedStatement ops, GhostParameterArray paramArray){
		try {
			ops.setNUMBER(1, (NUMBER) paramArray.getParameter(1));
			ops.setNUMBER(2, (NUMBER) paramArray.getParameter(2));
			logger.debug("MetaBlob DB Number to be removed from collection Id: " + GhostVariableWrapper.wrapVariable((NUMBER) paramArray.getParameter(1)));
			logger.debug("MetaBlob DB Number uidvm for collection Id update: " + GhostVariableWrapper.wrapVariable((NUMBER) paramArray.getParameter(2)));
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
	}
	
	@SuppressWarnings("unused")
	private void setSetBulkCIDStatementArguments(OraclePreparedStatement ops, GhostParameterArray paramArray){
		try {
			ops.setNUMBER(1, (NUMBER) paramArray.getParameter(1));
			ops.setNUMBER(2, (NUMBER) paramArray.getParameter(2));
			ops.setNUMBER(3, (NUMBER) paramArray.getParameter(3));
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
	}
	
	@SuppressWarnings("unused")
	private void setSetBulkCIDByFieldAndIdStatementArguments(OraclePreparedStatement ops, GhostParameterArray paramArray){
		try {
			ops.setNUMBER(1, (NUMBER) paramArray.getParameter(1));
			ops.setCHAR(2, (CHAR) paramArray.getParameter(2));
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
	}
	
	@SuppressWarnings("unused")
	private void setRemoveBulkCIDStatementArguments(OraclePreparedStatement ops, GhostParameterArray paramArray){
		try {
			ops.setNUMBER(1, (NUMBER) paramArray.getParameter(1));
			ops.setNUMBER(2, (NUMBER) paramArray.getParameter(2));
			ops.setNUMBER(3, (NUMBER) paramArray.getParameter(3));
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
	}
	
	@SuppressWarnings("unused")
	private void setDeleteAllObjectsStatementArguments(OraclePreparedStatement ops, GhostParameterArray paramArray){
		try {
			ops.setNUMBER(1, (NUMBER) paramArray.getParameter(1));
			ops.setNUMBER(2, (NUMBER) paramArray.getParameter(2));
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
	}
	
	public IMetaField getVMInsertColumn() {
		return _vmInsertColumn;
	}

	public void setVMInsertColumn(IMetaField insertColumn) {
		_vmInsertColumn = insertColumn;
	}

	public void addCollectionId(objectType arg0){
    	GhostParameterArray paramArray = new GhostParameterArray();
    	paramArray.putParameter(1, convertCollecitonIdToDBCID());
    	paramArray.putParameter(2, arg0.getDBKey());
    	executeDBStatement(gProcList, DbFunctionTypes.addCollectionId, paramArray);
	}
	
	public void removeCollectionId(objectType arg0){
    	GhostParameterArray paramArray = new GhostParameterArray();
    	paramArray.putParameter(1, convertCollecitonIdToDBCID());
    	paramArray.putParameter(2, arg0.getDBKey());
    	executeDBStatement(gProcList, DbFunctionTypes.removeCollectionId, paramArray);
	}
	
	public void addBulkCollectionId(NUMBER oldCollectionId){
    	GhostParameterArray paramArray = new GhostParameterArray();
    	paramArray.putParameter(1, convertCollecitonIdToDBCID());
    	paramArray.putParameter(2, oldCollectionId);
    	paramArray.putParameter(3, oldCollectionId);
    	executeDBStatement(gProcList, DbFunctionTypes.bulkAddCollectionId, paramArray);
	}
	
	public void removeBulkCollectionId(){
    	GhostParameterArray paramArray = new GhostParameterArray();
    	NUMBER cid = convertCollecitonIdToDBCID();
    	paramArray.putParameter(1, cid);
    	paramArray.putParameter(2, cid);
    	paramArray.putParameter(3, cid);
    	executeDBStatement(gProcList, DbFunctionTypes.removeBulkCollectionId, paramArray);
	}
	
	public void deleteAllObjects(){
    	GhostParameterArray paramArray = new GhostParameterArray();
    	NUMBER cid = convertCollecitonIdToDBCID();
    	paramArray.putParameter(1, cid);
    	paramArray.putParameter(2, cid);
    	executeDBStatement(gProcList, DbFunctionTypes.deleteAllObjects, paramArray);
	}
	
	public NUMBER getCollectionIdFromDB(objectType arg0){
    	GhostParameterArray paramArray = new GhostParameterArray();
    	paramArray.putParameter(1, arg0.getDBKey());
    	return ((NUMBER) executeDBStatement(gProcList, DbFunctionTypes.getCollectionId, paramArray));
	}
	
	public Long getCollectionId(){
    	try {
			return convertCollecitonIdToDBCID().longValue();
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
	}
	
	public void removeAllObjects(){
		removeBulkCollectionId();
		getMetaInfo().clear();
	}
	
	public void clear() {
		removeAllObjects();		
		getCollectionIdMap().remove(getRawCollectionId());
		setRawCollectionId(UNASSIGNED_COLLECTION_ID);
	}
	
	public NUMBER getDBConvertedCollectionId(){
		return convertCollecitonIdToDBCID();
	}

	
	public void addGhostMetaInfo(objectType arg0) {
		getMetaInfo().putInfo(arg0);
	}
	
	public void addGhostMetaInfo(IMetaGhostVariableTable table, IMetaField column, IMetaField idColumn) {
		getMetaInfo().putInfo(table,column,idColumn);
	}

	public void removeGhostMetaInfo(objectType arg0) {
		getMetaInfo().remove(arg0);
	}

	public void removeAllGhostMetaBlobInfo(Collection<?> arg0) {
		getMetaInfo().clear();	
	}
	
	protected Set<IMetaGhostVariableTable> getAllUsedTablesForObjectsInCollection(){
		return getMetaInfo().getTables();
	}
	
	public String getSQLQuery(){return getMetaInfo().getSQLQuery();}
	
	public String getSQLQuery(String additionalColumns) {return getMetaInfo().getSQLQuery(additionalColumns);}

	
	public void addByBulkInsertId(BigDecimal bulkSaveId) {
		try {
			GhostParameterArray paramArray = new GhostParameterArray();
	    	paramArray.putParameter(1, convertCollecitonIdToDBCID());
	    	paramArray.putParameter(2, new NUMBER(bulkSaveId));
	    	executeDBStatement(gProcList, DbFunctionTypes.setBulkCollectionIdByFieldAndId, paramArray);
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
			throw new GhostRuntimeException(e);
		}
	}


	public void setCustomAttribute(IGhostCustomAttribute attribute, String value){
		GhostParameterArray paramArray = new GhostParameterArray();
		switch(attribute.getDBType()){
		case VARCHAR: 
					try {
						paramArray.putParameter(1, new CHAR(value,GhostDBStaticVariables.dbCharacterSet));
						paramArray.putParameter(2, getDBConvertedCollectionId());
						
						String tmp = SETCUSTOMATTRIBUTE_Statement.replace("<CATR>",MetaTables.GHOST_VM.getCustomAttributeField(attribute).getColumnName());
						logger.debug("DBCollection - setCustomAttribute: " + GhostVariableWrapper.wrapVariable(tmp));
						executeDBStatement(tmp,paramArray,privateMethodSetArgumentSetCustomAttribute,true,null,null);
					} catch (SQLException e) {
						logger.error(e.getMessage(),e);
						throw new GhostRuntimeException(e," Possible conversion error in setting value <" + value + ">");
					}
					break;
		case DATE: throw new GhostRuntimeException("Cannot set attribute " + GhostVariableWrapper.wrapVariable(attribute) + " with passed in string value" + GhostVariableWrapper.wrapVariable(value) +  ". Mismatch in types." );
		default: throw new GhostRuntimeException("Unknown Attribute Type while setting Custom Date Attribute for DB Collection : " + GhostVariableWrapper.wrapVariable(attribute));
		}
	}
	
	public final void setCustomAttribute(IGhostCustomAttribute attribute, IGDate gDate){
		GhostParameterArray paramArray = new GhostParameterArray();
		switch(attribute.getDBType()){
		case DATE: 
					paramArray.putParameter(1, new TIMESTAMP(new java.sql.Timestamp(gDate.getTimeInMillis())));
					paramArray.putParameter(2, getDBConvertedCollectionId());
				
					String tmp = SETCUSTOMATTRIBUTE_Statement.replace("<CATR>",MetaTables.GHOST_VM.getCustomAttributeField(attribute).getColumnName());
					logger.debug("DBCollection - setCustomAttribute: " + GhostVariableWrapper.wrapVariable(tmp));
					executeDBStatement(tmp,paramArray,privateMethodSetArgumentSetCustomDateAttribute,true,null,null);
					break;
		case VARCHAR: throw new GhostRuntimeException("Cannot set attribute " + GhostVariableWrapper.wrapVariable(attribute) + " with passed in string value" + GhostVariableWrapper.wrapVariable(gDate) +  ". Mismatch in types." );
		default: throw new GhostRuntimeException("Unknown Attribute Type while setting Custom Date Attribute for DB Collection : " + GhostVariableWrapper.wrapVariable(attribute));
		}
	}
	
	
	protected Map<IMetaTable, ISQLQueryAdditionalInformation> checkAttributes(boolean useMappedValues, DBCollection<?> dbCollection, IGhostAttributeSet igas, String modifier, String columnModifier) {

			HashMap<IMetaTable, ISQLQueryAdditionalInformation> attributeClauseSet = new HashMap<IMetaTable,ISQLQueryAdditionalInformation>();			
			Set<IMetaGhostVariableTable> tables = dbCollection.getAllUsedTablesForObjectsInCollection();
			
			Set<GhostAttributeEnum> attributes = new HashSet<GhostAttributeEnum>();
			Set<GhostAttributeEnum> parentattributes = new HashSet<GhostAttributeEnum>();
			
			StringBuilder clause = new StringBuilder();// = GhostDbStaticVariables.EMPTRY_STR;
			StringBuilder tableClause =  new StringBuilder();
			StringBuilder whereClause =  new StringBuilder();
			
			String newColumnModifier = GhostDBStaticVariables.STRINGCONCAT;
			if(columnModifier != null){
				newColumnModifier = GhostDBStaticVariables.STRINGCONCAT + columnModifier + GhostDBStaticVariables.STRINGCONCAT;
			}
			
//			IAttributeTableList ial = null;
			IMetaField imf = null;
			
			for(IMetaTable imt : tables){
				if(!igas.areAttriubtesEmpty()){
					
					attributes.clear();
					attributes.addAll(imt.getAttributes());
						
						if(attributes.containsAll(igas.getAttributes())){
							for(GhostAttributeEnum atr : igas.getAttributes()){
									if(igas instanceof GhostAttributeSet){
										IGhostAttribute igae = ((GhostAttributeSet)igas).getMappedAttribute(atr);
										if( useMappedValues && (igae!=null) ){
											imf = imt.getAttributeField(igae);
										}else{
											imf = imt.getAttributeField(atr);
										}
									}else{
										imf = imt.getAttributeField(atr);
									}
								clause.append(newColumnModifier + GhostDBStaticVariables.SPACE +
								              imf.getAssociatedTableAlias() + GhostDBStaticVariables.PERIOD +
								              imf.getColumnName().toString() + GhostDBStaticVariables.SPACE);
								
							}
//							attributeClauseSet.put(imt, clause);
						}else{
							throw new GhostAttributeDoesNotExistException();
						}
				}	
//					}else{
//						throw new GhostAttributesDoNotExistException();
//					}
				
				if(!igas.areParentAttriubtesEmpty()){
					if(imt instanceof IMetaChildTable){
						    IMetaParentTable impt = ((IMetaChildTable) imt).getParentTable();
							parentattributes.clear();
							parentattributes.addAll(((IAttributeTableList) impt).getAttributes());
							//ial = (IAttributeTableList) impt;
							
							if(parentattributes.containsAll(igas.getParentAttributes())){
								
								for(GhostAttributeEnum atr : igas.getParentAttributes()){
//										imf = impt.getAttributeField(atr);
										if(igas instanceof GhostAttributeSet){
											IGhostAttribute igae = ((GhostAttributeSet)igas).getMappedAttribute(atr);
											if( useMappedValues && (igae!=null) ){
												imf = impt.getAttributeField(igae);
											}else{
												imf = impt.getAttributeField(atr);
											}
										}else{
											imf = impt.getAttributeField(atr);
										}
									clause.append(newColumnModifier + GhostDBStaticVariables.SPACE +
									              imf.getAssociatedTableAlias() + GhostDBStaticVariables.PERIOD +
									              imf.getColumnName().toString() + GhostDBStaticVariables.SPACE);
									
									tableClause.append(GhostDBStaticVariables.COMMA + impt.getTableName() + 
									                   GhostDBStaticVariables.SPACE + impt.getAlias());
									
									whereClause.append(GhostDBStaticVariables.SPACE + GhostDBStaticVariables.AND + GhostDBStaticVariables.SPACE +
									                   impt.getAlias() + GhostDBStaticVariables.PERIOD +
									                   impt.getForeignKeyField() + GhostDBStaticVariables.EQUALS +
									                   imt.getAlias() + GhostDBStaticVariables.PERIOD + ((IMetaChildTable) imt).getKeyField());
									
								}						
							}else{
								throw new GhostAttributeDoesNotExistException();
							}
					}else{
						throw new GhostAttributeDoesNotExistException();
					}
				}
			
			if(!igas.areCustomAttriubtesEmpty()){
				if(!useMappedValues){
					for(IGhostCustomAttribute atr : igas.getCustomAttributes()){
						clause.append(newColumnModifier + GhostDBStaticVariables.SPACE +
									  MetaTables.GHOST_VM.getAlias() + GhostDBStaticVariables.PERIOD +
									  atr.toString());
					}
				}else{
					
					attributes.clear();
					attributes.addAll(imt.getAttributes());
					for(IGhostCustomAttribute atr : igas.getCustomAttributes()){
							try {
								if(igas instanceof GhostAttributeSet){
									IGhostAttribute igae = ((GhostAttributeSet)igas).getMappedAttribute(atr);
									if( useMappedValues && (igae!=null) ){
										imf = imt.getAttributeField(igae);
									}else{
										imf = imt.getAttributeField(atr);
									}
								}else{
									imf = imt.getAttributeField(atr);
								}
							} catch (GhostAttributeDoesNotExistException e) {
									GhostAttributeMapping g = ((GhostAttributeMapping) igas);
									attributes.removeAll(g.getCustomMappedAttributes());
									logger.debug(AbstractGhostAttributeSet.printAttributes(g.getCustomMappedAttributes()));
								throw new GhostAttributeDoesNotExistException(AbstractGhostAttributeSet.printAttributes(attributes));
							}
							clause.append(newColumnModifier + GhostDBStaticVariables.SPACE +
							              imf.getAssociatedTableAlias() + GhostDBStaticVariables.PERIOD +
							              imf.getColumnName() + GhostDBStaticVariables.SPACE);
						
					}
			    }
			}
			
			SQLQueryAdditionalInformation tableQueryAttributes = new SQLQueryAdditionalInformation(modifier + GhostDBStaticVariables.COMMA + clause.substring(newColumnModifier.length()) + KEYVALUE_ALIAS,
																	                               tableClause.toString(),
																								   whereClause.toString());
//			List<String> tableQueryAttributes = new ArrayList<String>();
//			tableQueryAttributes.add(modifier + GhostDbStaticVariables.COMMA + clause.substring(2) + KEYVALUE_ALIAS);
//			tableQueryAttributes.add(tableClause.toString());
//			tableQueryAttributes.add(whereClause.toString());
			
			attributeClauseSet.put(imt, tableQueryAttributes);
			logger.debug("Attribute Table columns: " + GhostVariableWrapper.wrapVariable(imt.getTableName()) + " " + GhostVariableWrapper.wrapVariable(modifier + GhostDBStaticVariables.COMMA + clause.substring(2) + KEYVALUE_ALIAS));
//			logger.debug("clause: " + clause.toString());
//			logger.debug("modifier: " + modifier);
//			logger.debug("tableClause: " + tableClause.toString());
//			logger.debug("whereClause: " + whereClause.toString());
			}//End For Loop
			return attributeClauseSet;
    }
	
	
    
    @Override
	public GhostQueryInternal bulkAdd(NUMBER possesionID){
    	GhostParameterArray gParamArray = new GhostParameterArray();
    	gParamArray.putParameter(1, possesionID);    	
    	return  new GhostQueryInternal(executeResultSetDBStatement(GRABPOSSESIDS_STATEMENT,gParamArray, gProcList.getSetArgumentsMethod(DbFunctionTypes.possessIds)) );
    }
    
    @Override
	public GhostQueryInternal bulkAddUsingCollectionId(NUMBER collectionID){    	
    	String modifiedStatement = GRABCOLLECTIONIDS_STATEMENT.replace(GRABCOLLECTIONIDS_STATEMENT_TOKEN, getVMInsertColumn().getFullyQualifiedTableAliaisWithColumnName());
    	logger.debug("BulkAddUsingCollectionId Query: " + GhostVariableWrapper.wrapVariable(modifiedStatement));
    	GhostParameterArray gParamArray = new GhostParameterArray();
    	gParamArray.putParameter(1, collectionID);
    	gParamArray.putParameter(2, collectionID);
    	return  new GhostQueryInternal(executeResultSetDBStatement(modifiedStatement,gParamArray, gProcList.getSetArgumentsMethod(DbFunctionTypes.grabCollectionIds)) );
    }

	@Override
	public void mergeMetaInfo(IDBCollection<?> collection) {
         MetaInfo mi = collection.getMetaInfo();
         for(IMetaGhostVariableTable imgvt : mi.getTables()){
        	this.getMetaInfo().putInfo(imgvt, imgvt.getIMGVTValueColumn(), imgvt.getPrimaryKey());	 
         }
	}
}
