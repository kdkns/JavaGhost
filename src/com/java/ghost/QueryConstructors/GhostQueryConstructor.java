package com.java.ghost.QueryConstructors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.java.ghost.Exceptions.GhostFieldRequiredForInsertException;
import com.java.ghost.Exceptions.GhostMustIncludePartitionFilterException;
import com.java.ghost.Exceptions.GhostRuntimeException;
import com.java.ghost.GhostFieldMaps.GhostBindFieldMap;
import com.java.ghost.GhostFieldMaps.GhostFieldMap;
import com.java.ghost.GhostFieldMaps.GhostFieldMapObject;
import com.java.ghost.MetaTableTypes.GhostQueryTable;
import com.java.ghost.MetaTableTypes.IMetaField;
import com.java.ghost.MetaTableTypes.IMetaGhostVariableTable;
import com.java.ghost.MetaTableTypes.IMetaTable;
import com.java.ghost.MetaTableTypes.Tables.MetaTables;
import com.java.ghost.Variable.IGhostCollection;
import com.java.ghost.Variable.IGhostVariable;
import com.java.ghost.utils.GhostDBStaticVariables;
import com.java.ghost.utils.GhostPair;
import com.java.ghost.utils.GhostVariableWrapper;
import com.java.ghost.utils.IGDate;

public class GhostQueryConstructor {
	public static final String ORDERBYASC = GhostDBStaticVariables.ORDERBYASC;
	public static final String ORDERBYDESC = GhostDBStaticVariables.ORDERBYDESC;
	public static final boolean IS_DISTINCT = true;
	public static final boolean IS_NOT_DISTINCT = false;
	
	private static Logger logger = Logger.getLogger("GhostQueryConstructor");
	
//	 public static String getConcatTableString(List<IMetaTable> tables, String concatOperator) {
//		 String result= GhostDbStaticVariables.EMPTRY_STR;
//		 if(tables!=null){
//			 int listSize = tables.size() -1;
//			 
//			 for (int x=0; x<= listSize-1;x++){
//				 result += ((IMetaTable)tables.get(x)).getTableName() + GhostDbStaticVariables.SPACE + ((IMetaTable)tables.get(x)).getAlias() + concatOperator;
//			 }
//			 result += ((IMetaTable)tables.get(listSize)).getTableName() + GhostDbStaticVariables.SPACE + ((IMetaTable)tables.get(listSize)).getAlias();
//		 }
//		 return result;
//	}
	 
//	 public static String getConcatFieldString(List<IMetaField> fieldList, String concatOperator){
//		 String result= GhostDbStaticVariables.EMPTRY_STR;
//		 int listSize = fieldList.size() -1;
//		 IMetaField field = null;
//		 if(listSize>=0){
//			 for (int x=0; x<= listSize-1;x++){
//				 field = (IMetaField)fieldList.get(x);
//				 if(!field.isFunction()){
//                     result += field.getAssociatedTableAlias() + GhostDbStaticVariables.PERIOD + field.getColumnName() + GhostDbStaticVariables.SPACE + field.getAlias() + concatOperator;
//				 }else{
//					 result += field.getColumnName() + GhostDbStaticVariables.SPACE + field.getAlias() + concatOperator; 
//				 }
//			 }
//			 field = (IMetaField)fieldList.get(listSize);
//			 if(!field.isFunction()){
//                 result += field.getAssociatedTableAlias() + GhostDbStaticVariables.PERIOD + field.getColumnName() + GhostDbStaticVariables.SPACE + field.getAlias();
//			 }else{
//				 result += field.getColumnName() + GhostDbStaticVariables.SPACE + field.getAlias(); 
//			 }
//		 }
//		 return result;
//	 }
	
	private static int setGhostQueryTableBindVariables(List<IMetaTable> tables, GhostTableFilter tableFilter){
		int lastBindPosition = 1;
		for(IMetaTable imt : tables){
			if(imt instanceof GhostQueryTable){
					GhostQueryTable gqt = (GhostQueryTable) imt;
					if(!gqt.getTableFilter().getGhostBindFieldMap().isEmpty()){
						GhostBindFieldMap gbfm = tableFilter.getGhostBindFieldMap();
						gbfm.mergeBeforeMap(gqt.getTableFilter().getGhostBindFieldMap());
						tableFilter.setIsBindMapSet(true);
						lastBindPosition = gbfm.size();
					}
			}
		}
		return lastBindPosition;
	}
	
	private static int setGhostQueryTableBindVariables(IMetaTable table, GhostTableFilter tableFilter){
		List<IMetaTable> tableList = new ArrayList<IMetaTable>();
		return setGhostQueryTableBindVariables(tableList, tableFilter);
	}

	 public static String getConcatFieldsColumnName(List<IMetaField> fieldList, String concatOperator){
		 StringBuilder result = new StringBuilder();
		 int listSize = fieldList.size();
		 if(listSize>0){
			 for (int x=0; x< listSize-1;x++){
				 result.append(((IMetaField)fieldList.get(x)).getColumnName() + concatOperator);
			 }
			 result.append(((IMetaField)fieldList.get(listSize-1)).getColumnName());
		 }
		 
		 return result.toString();
	 }
	 
	 private static String getUpdateFields(GhostFieldMap gfm, String updateEquals) {
			
			StringBuilder result = new StringBuilder();
			Set<Integer> s = gfm.getKeySet();
			Iterator<Integer> i = s.iterator();
			int listSize = gfm.size()-1;
			
			GhostFieldMapObject bfmo = null;
			IMetaField field = null;
			GhostPair<IMetaField,GhostFieldMapObject> gp = null;
			
		    for( int x =0;x<listSize;x++){
		    	gp = gfm.get(i.next());
				field = gp.getLeft();
				bfmo = gp.getRight();
				
		    	if(bfmo.getType().equals(GhostFieldMapObject.GhostFieldMapObjectTypes.String)){
		    		result.append(field.getColumnName() + GhostDBStaticVariables.EQUALS + GhostDBStaticVariables.SINGLE_QUOTE +  bfmo.toString() + GhostDBStaticVariables.SINGLE_QUOTE + GhostDBStaticVariables.COMMA);	
		    	}else{
		    		result.append(field.getColumnName() + GhostDBStaticVariables.EQUALS + bfmo.toString() + GhostDBStaticVariables.COMMA);
		    	}
		    }
		    gp = gfm.get(i.next());
			field = gp.getLeft();
			bfmo = gp.getRight();
		    if(bfmo.getType().equals(GhostFieldMapObject.GhostFieldMapObjectTypes.String)){
	    		result.append(field.getColumnName() + GhostDBStaticVariables.EQUALS + GhostDBStaticVariables.SINGLE_QUOTE +  bfmo.toString() + GhostDBStaticVariables.SINGLE_QUOTE);
	    	}else{
	    		result.append(field.getColumnName() + GhostDBStaticVariables.EQUALS + bfmo.toString());
	    	}
		    
		    return result.toString(); 
		}
	 
	 private static String getConcatFieldStringFromBlessMap(GhostFieldMap gfm, String fieldSperator) {
			    StringBuilder result = new StringBuilder();
				Set<Integer> s = gfm.getKeySet();
				Iterator<Integer> i = s.iterator();
				int listSize = gfm.size()-1;
				
				GhostFieldMapObject bfmo = null;
				IMetaField field = null;
				GhostPair<IMetaField,GhostFieldMapObject> gp = null;

				//Basically loop through all elements but the last one.
				//On last one all same code but handles not adding extra COMMA
				if(listSize>=0){
					for (int x=0; x<= listSize-1;x++){
						 gp = gfm.get(i.next());
						 field = gp.getLeft();
						 bfmo = gp.getRight();
						
						 switch(bfmo.getType()){
						    case String: result.append(GhostDBStaticVariables.wrapString((String)bfmo.getValue()) + 
			                                      GhostDBStaticVariables.SPACE + field.getColumnName() + GhostDBStaticVariables.SPACE +  fieldSperator);
						                 break;
						    case Date: result.append(GhostDBStaticVariables.wrapDate((IGDate)bfmo.getValue()) + 
			                                      GhostDBStaticVariables.SPACE + field.getColumnName() + GhostDBStaticVariables.SPACE +  fieldSperator);
						                 break;
						    case IMetaFieldCustomColumn: result.append( ((IMetaField)bfmo.getValue()).getColumnName() + GhostDBStaticVariables.SPACE + field.getColumnName() + GhostDBStaticVariables.SPACE +  fieldSperator);
				                                         break;
						    case IMetaFieldMultiField: //result.append(((IMetaField)bfmo.getValue()).getAssociatedTableAlias() + GhostDBStaticVariables.PERIOD + bfmo.getValue().toString()+
								    	               result.append(((IMetaField)bfmo.getValue()).getFullyQualifiedTableAliaisWithColumnName() +
							                           GhostDBStaticVariables.SPACE + field.getColumnName() + GhostDBStaticVariables.SPACE +  fieldSperator);
								                       break;
						    case IMetaField: //result.append(((IMetaField)bfmo.getValue()).getAssociatedTableAlias() + GhostDBStaticVariables.PERIOD + bfmo.getValue().toString()+ 
						    	             result.append(((IMetaField)bfmo.getValue()).getFullyQualifiedTableAliaisWithColumnName() +
				                             GhostDBStaticVariables.SPACE + field.getColumnName() + GhostDBStaticVariables.SPACE +  fieldSperator);
						                     break;
	  					    default: result.append(bfmo.getValue().toString()+ 
                                     GhostDBStaticVariables.SPACE + field.getColumnName() + GhostDBStaticVariables.SPACE +  fieldSperator);
						        
						 }
					}
					gp = gfm.get(i.next());
					field = gp.getLeft();
					bfmo = gp.getRight();
					
					switch(bfmo.getType()){
				    case String: result.append(GhostDBStaticVariables.wrapString((String)bfmo.getValue()) + 
	                                      GhostDBStaticVariables.SPACE + field.getColumnName() + GhostDBStaticVariables.SPACE );
				                 break;
				    case Date: result.append(GhostDBStaticVariables.wrapDate((IGDate)bfmo.getValue()) +  
	                                      GhostDBStaticVariables.SPACE + field.getColumnName() + GhostDBStaticVariables.SPACE );
				                 break;
				    case IMetaFieldCustomColumn: result.append( bfmo.getValue().toString()+ GhostDBStaticVariables.SPACE + field.getColumnName() + GhostDBStaticVariables.SPACE );
		                                         break;
				    case IMetaFieldMultiField: //result.append(((IMetaField)bfmo.getValue()).getAssociatedTableAlias() + GhostDBStaticVariables.PERIOD + bfmo.getValue().toString()+
				    	             result.append(((IMetaField)bfmo.getValue()).getFullyQualifiedTableAliaisWithColumnName() +
			                         GhostDBStaticVariables.SPACE + field.getColumnName() + GhostDBStaticVariables.SPACE );
				                     break;
				    case IMetaField: //result.append(((IMetaField)bfmo.getValue()).getAssociatedTableAlias() + GhostDBStaticVariables.PERIOD + bfmo.getValue().toString()+
				    	             result.append(((IMetaField)bfmo.getValue()).getFullyQualifiedTableAliaisWithColumnName() +
		                             GhostDBStaticVariables.SPACE + field.getColumnName() + GhostDBStaticVariables.SPACE );
				                     break;
				                     
				    default: result.append(bfmo.getValue().toString()+ 
                             GhostDBStaticVariables.SPACE + field.getColumnName() + GhostDBStaticVariables.SPACE );
				        
				 }
				}
			 return result.toString();
		}
	 
	 //Select Query
	 private static GhostQuery selectQueryHelper(boolean isDistinct, IMetaTable table, List<IMetaField> selectFieldList, GhostTableFilter tf, GhostHavingFilter havingFilter,
         List<IMetaField> groupByFields, List<IMetaField> orderByFields, String orderByAttribute) {
		 List<IMetaTable> tabs = new ArrayList<IMetaTable>();
		 tabs.add(table);
		 return selectQueryHelper(isDistinct, tabs, selectFieldList,tf,havingFilter,groupByFields,orderByFields,orderByAttribute); 
	 }
	 
	 private static GhostQuery selectQueryHelper(boolean isDistinct,List<IMetaTable> tables, List<IMetaField> selectFieldList, GhostTableFilter tf, GhostHavingFilter havingFilter,
			                    List<IMetaField> groupByFields, List<IMetaField> orderByFields, String orderByAttribute) {
		 if(tables.size()<1){
			 throw new GhostRuntimeException("Table list object is empty! Please add tables to your list.");
		 }
		 if(selectFieldList.size()<1){
			 throw new GhostRuntimeException("List of selected fields is empty! Please add fields to your list.");
		 }
		 
		 if(groupByFields!=null && groupByFields.size()<1){
			 throw new GhostRuntimeException("List of group by fields is empty! Please add fields to your list.");
		 }
		 
		 if(orderByFields!=null && orderByFields.size()<1){
			 throw new GhostRuntimeException("List of order by fields is empty! Please add fields to your list.");
		 }
		 
		 //Check that partition join was included!
		 for(IMetaTable imt : tables){
			 if(imt.usePartitionField()){
				 if(!tf.getFields().contains(imt.getPartitionField())){
					 throw new GhostMustIncludePartitionFilterException(imt.getPartitionFieldName());
				 }
			 }
		 }
		 
		 GhostQuery gq = new GhostQuery();
		 String useDistinct = GhostDBStaticVariables.GHOST_DB_DISTINCT_CLAUSE;
		 String tfSQL = GhostDBStaticVariables.EMPTY_STR;
			if(tf!=null){
				tfSQL = tf.getFilterSQL();
				
			}
		 if(!isDistinct){
			 useDistinct = "";
		 }
		 String tmp = GhostDBStaticVariables.SELECT_SELECT + useDistinct + GhostDBStaticVariables.SPACE + GhostDBStaticVariables.getConcatFieldString(selectFieldList,GhostDBStaticVariables.COMMA) +
		              GhostDBStaticVariables.SELECT_FROM + GhostDBStaticVariables.getConcatTableString(tables,GhostDBStaticVariables.COMMA) +
		              GhostDBStaticVariables.SELECT_WHERE1E1 + tfSQL;
		 
		 if(havingFilter != null){
			 tmp += GhostDBStaticVariables.SELECT_HAVING + havingFilter.getFilterSQL();
		 }
		 
		 if(groupByFields != null){
			 tmp += GhostDBStaticVariables.SELECT_GROUPBY + GhostDBStaticVariables.getConcatFieldStringNoAlias(groupByFields,GhostDBStaticVariables.COMMA);
		 }
		 
		 if(orderByFields != null){
			 tmp += GhostDBStaticVariables.SELECT_ORDERBY + GhostDBStaticVariables.getConcatFieldStringNoAlias(orderByFields,GhostDBStaticVariables.COMMA) + orderByAttribute;
		 }
		 
		 gq.setFieldList(selectFieldList);
		 gq.setTableList(tables);
		 if(tf == null){
			 tf = new GhostTableFilter();
		 }
		 setGhostQueryTableBindVariables(tables,tf);
		 gq.setTableFilter(tf);
		 gq.setHavingFilter(havingFilter);
		 gq.setGroupByFields(groupByFields);
		 gq.setOrderByFields(orderByFields);
		 gq.setQuery(tmp, GhostQuery.SqlTypes.select); 
		 logger.debug("GhostQueryConstructor SelectQuery: " + GhostVariableWrapper.wrapVariable(tmp));
		 return gq;
	 }
	 
	 
	 
	 //Gets called in BlessGhost and others?
	 public static GhostQuery selectQuery(boolean isDistinct, List<IMetaTable> tables, GhostFieldMap selectFieldMap,GhostTableFilter tf) {
		 GhostQuery gq = new GhostQuery();
		 String useDistinct = GhostDBStaticVariables.GHOST_DB_DISTINCT_CLAUSE;
		 String tfSQL = GhostDBStaticVariables.EMPTY_STR;
		 
		if(tf!=null){
				tfSQL = tf.getFilterSQL();
				
		}
		 if(!isDistinct){
			 useDistinct = "";
		 }
		 String tmp = GhostDBStaticVariables.SELECT_SELECT + useDistinct + GhostDBStaticVariables.SPACE +
                      getConcatFieldStringFromBlessMap(selectFieldMap,GhostDBStaticVariables.COMMA) +
		              GhostDBStaticVariables.SELECT_FROM + GhostDBStaticVariables.getConcatTableString(tables,GhostDBStaticVariables.COMMA) +
		              GhostDBStaticVariables.SELECT_WHERE1E1 + tfSQL;
		 
		 
		 gq.setFieldList(selectFieldMap.getFieldList());
		 gq.setTableList(tables);
		 if(tf == null){
			 tf = new GhostTableFilter();
		 }
		 setGhostQueryTableBindVariables(tables,tf);
		 gq.setTableFilter(tf);
		 gq.setQuery(tmp, GhostQuery.SqlTypes.select);
		 logger.debug("GhostQueryConstructor:selectQuery with BlessFieldMap:  " + GhostVariableWrapper.wrapVariable(tmp));
		 return gq;
		}
	 
	 //Used by BlessGhost
	 public static GhostQuery selectQuery(boolean isDistinct, IMetaTable table, GhostFieldMap selectFieldMap,GhostTableFilter tf) {
		 List<IMetaTable> tables = new ArrayList<IMetaTable>();
		 tables.add(table);
		 return selectQuery( isDistinct,  tables, selectFieldMap, tf);
	 }
	 
	 protected static GhostQuery selectQuery(boolean isDistinct, List<IMetaTable> tables, GhostFieldMap selectFieldMap,GhostTableFilter tf, IGhostVariable<?,?> gmb, IMetaField blobField) {
		 GhostQuery gq = new GhostQuery();
		 String useDistinct = GhostDBStaticVariables.GHOST_DB_DISTINCT_CLAUSE;
		 String tfSQL = GhostDBStaticVariables.EMPTY_STR;
		 
		 String fields = GhostDBStaticVariables.EMPTY_STR;
		 
		 String remoteBlobField = GhostDBStaticVariables.EMPTY_STR;
		 String remoteFromTable = GhostDBStaticVariables.EMPTY_STR;
		 String remoteWhereClause = GhostDBStaticVariables.EMPTY_STR;
		 
		 
		 if(gmb.getTable() != MetaTables.GHOST_VM){
			    IMetaTable table = gmb.getTable();
				remoteBlobField = GhostDBStaticVariables.COMMA + gmb.getVMInsertColumn() +
                                  "DECODE( LENGTH" + GhostDBStaticVariables.OPEN_PARENTHESES + gmb.getVMInsertColumn() + 
                                  GhostDBStaticVariables.CLOSE_PARENTHESES + GhostDBStaticVariables.COMMA + "0" + GhostDBStaticVariables.COMMA + 
                                  blobField.getColumnName() + GhostDBStaticVariables.COMMA + "NULL" + GhostDBStaticVariables.COMMA +
                                  blobField.getColumnName() + GhostDBStaticVariables.COMMA + gmb.getVMInsertColumn() + 
                                  GhostDBStaticVariables.CLOSE_PARENTHESES +  GhostDBStaticVariables.SPACE + blobField.getColumnName();
				
				remoteFromTable = GhostDBStaticVariables.COMMA + table.getTableName() + GhostDBStaticVariables.SPACE + table.getAlias();
				
				remoteWhereClause = GhostDBStaticVariables.SPACE + GhostDBStaticVariables.AND + GhostDBStaticVariables.SPACE + MetaTables.GHOST_VM.getAlias() +
                                    GhostDBStaticVariables.PERIOD + MetaTables.GHOST_VM.getGhostPointer() + GhostDBStaticVariables.SPACE +
                                    GhostDBStaticVariables.EQUALS + GhostDBStaticVariables.SPACE + table.getAlias() + GhostDBStaticVariables.PERIOD + gmb.getIdColumn();
				
				if(table.usePartitionField()){
					remoteWhereClause += GhostDBStaticVariables.SPACE + GhostDBStaticVariables.AND + GhostDBStaticVariables.SPACE +
			    	                     MetaTables.GHOST_VM.getAlias() + GhostDBStaticVariables.PERIOD + MetaTables.GHOST_VM.getGhostPointerPartitionDate() + 
			    	                     GhostDBStaticVariables.SPACE + GhostDBStaticVariables.EQUALS + GhostDBStaticVariables.SPACE + 
			    	                     table.getAlias()+ GhostDBStaticVariables.PERIOD + table.getPartitionFieldName();
			    }
				
				fields = getConcatFieldStringFromBlessMap(selectFieldMap,GhostDBStaticVariables.COMMA);
			    IMetaField dummy = null;
				selectFieldMap.put(selectFieldMap.size(), blobField, dummy);
				
		 } else{
			 selectFieldMap.put(selectFieldMap.size(), blobField, MetaTables.GHOST_VM.getBlobValue());
			 fields = getConcatFieldStringFromBlessMap(selectFieldMap,GhostDBStaticVariables.COMMA);
		 }
		 
		if(tf!=null){
				tfSQL = tf.getFilterSQL();
		}
		 if(!isDistinct){
			 useDistinct = "";
		 }
		 String tmp = GhostDBStaticVariables.SELECT_SELECT + useDistinct + GhostDBStaticVariables.SPACE +
		              fields +
		              remoteBlobField +        
		              GhostDBStaticVariables.SELECT_FROM + GhostDBStaticVariables.getConcatTableString(tables,GhostDBStaticVariables.COMMA) +
		              remoteFromTable +
		              GhostDBStaticVariables.SELECT_WHERE1E1 + tfSQL +
		              remoteWhereClause;
		 
		 
		 gq.setFieldList(selectFieldMap.getFieldList());
		 gq.setTableList(tables);
		 if(tf == null){
			 tf = new GhostTableFilter();
		 }
		 setGhostQueryTableBindVariables(tables,tf);
		 gq.setTableFilter(tf);
		 gq.setQuery(tmp, GhostQuery.SqlTypes.select);
		 logger.debug("GhostQueryConstructor:selectQuery with BlessFieldMap:  " + GhostVariableWrapper.wrapVariable(tmp));
		 return gq;
		}
	 
	public static GhostQuery selectAllQuery(IMetaTable table) {
		 return selectQueryHelper(false,table, table.getAllColumns(),null,null,null,null,GhostDBStaticVariables.EMPTY_STR);
	 }

	public static GhostQuery selectQuery(boolean isDistinct,List<IMetaField> selectFieldList,IMetaTable table) {
		return selectQueryHelper(isDistinct,table, selectFieldList,null,null,null,null,GhostDBStaticVariables.EMPTY_STR);
	 }
	 
	 public static GhostQuery selectQuery(boolean isDistinct, List<IMetaField> selectFieldList,IMetaTable table, GhostTableFilter tf) {
		return selectQueryHelper(isDistinct,table, selectFieldList,tf,null,null,null,GhostDBStaticVariables.EMPTY_STR);
	 }
	 
	 public static GhostQuery selectQuery(boolean isDistinct, List<IMetaField> selectFieldList, IMetaTable table, GhostTableFilter tf, List<IMetaField> orderByFields, String orderByAttribute) {
			return selectQueryHelper(isDistinct,table, selectFieldList,tf,null,null,orderByFields, orderByAttribute);
		 }
	 
	 public static GhostQuery selectQuery(boolean isDistinct, List<IMetaField> selectFieldList, IMetaTable table, GhostTableFilter tf, List<IMetaField> groupByFields) {
		return selectQueryHelper(isDistinct,table, selectFieldList,tf,null,groupByFields,null,GhostDBStaticVariables.EMPTY_STR);
	 }
	 
	 public static GhostQuery selectQuery(boolean isDistinct, List<IMetaField> selectFieldList, IMetaTable table, GhostTableFilter tf, List<IMetaField> groupByFields, List<IMetaField> orderByFields, String orderByAttribute) {
			return selectQueryHelper(isDistinct,table, selectFieldList,tf,null,groupByFields,orderByFields, orderByAttribute);
		 }
	 
	 public static GhostQuery selectQuery(boolean isDistinct, List<IMetaField> selectFieldList, IMetaTable table, GhostTableFilter tf, GhostHavingFilter havingFilter,
             List<IMetaField> groupByFields, List<IMetaField> orderByFields, String orderByAttribute) {
		return selectQueryHelper(isDistinct,table, selectFieldList,tf,havingFilter,groupByFields,orderByFields, orderByAttribute);
	 }
	 
	 public static GhostQuery selectQuery(boolean isDistinct, List<IMetaField> selectFieldList, IMetaTable table, GhostTableFilter tf, GhostHavingFilter havingFilter,
             List<IMetaField> groupByFields) {
		return selectQueryHelper(isDistinct,table, selectFieldList,tf,havingFilter,groupByFields,null,null);
	 }
	 
	 public static GhostQuery selectQuery(boolean isDistinct, List<IMetaField> selectFieldList, List<IMetaTable> tables) {
			return selectQueryHelper(isDistinct,tables, selectFieldList,null,null,null,null,GhostDBStaticVariables.EMPTY_STR);
	 }
	 
	 public static GhostQuery selectQuery(boolean isDistinct, List<IMetaField> selectFieldList, List<IMetaTable> tables, GhostTableFilter tf) {
		return selectQueryHelper(isDistinct,tables, selectFieldList,tf,null,null,null,GhostDBStaticVariables.EMPTY_STR);
	 }
	 
	 public static GhostQuery selectQuery(boolean isDistinct, List<IMetaField> selectFieldList, List<IMetaTable> tables, GhostTableFilter tf, List<IMetaField> groupByFields) {
		return selectQueryHelper(isDistinct,tables, selectFieldList,tf,null,groupByFields,null,GhostDBStaticVariables.EMPTY_STR);
	 }
	 
	 public static GhostQuery selectQuery(boolean isDistinct, List<IMetaField> selectFieldList,List<IMetaTable> tables, GhostTableFilter tf, GhostHavingFilter havingFilter,
             List<IMetaField> groupByFields, List<IMetaField> orderByFields, String orderByAttribute) {
		return selectQueryHelper(isDistinct,tables, selectFieldList,tf,havingFilter,groupByFields,orderByFields, orderByAttribute);
	 }
	 
	 public static GhostQuery selectQuery(boolean isDistinct, List<IMetaField> selectFieldList, List<IMetaTable> tables, GhostTableFilter tf, 
             List<IMetaField> orderByFields, String orderByAttribute) {
		return selectQueryHelper(isDistinct,tables, selectFieldList,tf,null,null,orderByFields, orderByAttribute);
	 }
	 
	 //Insert query
	 private static GhostQuery insertQueryHelper(IMetaTable table, GhostFieldMap bfm){
			GhostQuery gq = new GhostQuery();
			if(!bfm.getFieldList().containsAll(table.getRequiredInsertFieldList())){
				throw new GhostFieldRequiredForInsertException(table);
			}
			String tmp = GhostDBStaticVariables.INSERT_INTO + table.getTableName() +
			             GhostDBStaticVariables.OPEN_PARENTHESES + getConcatFieldsColumnName(bfm.getFieldList(),GhostDBStaticVariables.COMMA) + GhostDBStaticVariables.INSERT_VALUES +
			             bfm.getConcatValues(GhostDBStaticVariables.COMMA)+ GhostDBStaticVariables.CLOSE_PARENTHESES;
			gq.setQuery(tmp, GhostQuery.SqlTypes.insert);
			return gq;
	}
	
	private static GhostQuery insertUsingSelectQueryHelper(GhostQuery selectQuery, IMetaTable table) {
			GhostQuery gq = new GhostQuery();
			if(!selectQuery.getFieldList().containsAll(table.getRequiredInsertFieldList())){
				table.getRequiredInsertFieldList().removeAll(selectQuery.getFieldList());
				throw new GhostFieldRequiredForInsertException(table);
			}
			String tmp = GhostDBStaticVariables.INSERT_INTO + table.getTableName() + GhostDBStaticVariables.OPEN_PARENTHESES +
			             getConcatFieldsColumnName(selectQuery.getFieldList(),GhostDBStaticVariables.COMMA) + GhostDBStaticVariables.CLOSE_PARENTHESES +
			             GhostDBStaticVariables.SPACE +
			             selectQuery.getQuery();
			gq.setQuery(tmp, GhostQuery.SqlTypes.insert);
			gq.setTableList(table);
			GhostTableFilter tf = selectQuery.getTableFilter();
			if(tf == null){
				 tf = new GhostTableFilter();
			 }
			setGhostQueryTableBindVariables(table,tf);
			gq.setTableFilter(selectQuery.getTableFilter());
			return gq;
	}
	
	public static GhostQuery insertQuery(IMetaTable table, GhostFieldMap bfm) {
			return insertQueryHelper(table, bfm);
	}
	
	public static GhostQuery insertUsingSelectQuery(GhostQuery selectQuery, IMetaTable table) {
		return insertUsingSelectQueryHelper(selectQuery, table);
    }
	
	public static GhostQuery insertUsingSelectQuery(GhostQuery selectQuery, IMetaTable table, String additionalColumns){
		return insertUsingSelectQueryHelper(selectQuery, table);
    }
	
	 
	//Update query 
	private static GhostQuery updateQueryHelper(IMetaTable table, GhostFieldMap bfm, GhostTableFilter tf){
			GhostQuery gq = new GhostQuery();
			
			String tfSQL = GhostDBStaticVariables.EMPTY_STR;
			if(tf!=null){
				tfSQL = tf.getFilterSQL();
			}
			
			String tmp = GhostDBStaticVariables.UPDATE_UPDATE + table.getTableName() +
			             GhostDBStaticVariables.UPDATE_SET + getUpdateFields(bfm,GhostDBStaticVariables.EQUALS) +
			             GhostDBStaticVariables.SELECT_WHERE1E1 + tfSQL;
			
			gq.setQuery(tmp, GhostQuery.SqlTypes.update);
			gq.setTableList(table);
			gq.setTableFilter(tf);
			return gq;
	}

	public static GhostQuery updateQuery(IMetaTable table, GhostFieldMap bfm, GhostTableFilter tf){
		return updateQueryHelper(table, bfm, tf);
    }
	
	public static GhostQuery updateQuery(IMetaTable table, GhostFieldMap bfm){
			return updateQueryHelper(table, bfm, null);
	}

	//Delete Query
	public static GhostQuery deleteQuery(IMetaTable table, GhostTableFilter tf) {
		GhostQuery gq = new GhostQuery();
		
		String tfSQL = GhostDBStaticVariables.EMPTY_STR;
		if(tf!=null){
			tfSQL = tf.getFilterSQL();
		}
		
		String tmp = GhostDBStaticVariables.DELETE_DELETE + table.getTableName() +
		             GhostDBStaticVariables.SELECT_WHERE1E1 + tfSQL;
		
		gq.setTableFilter(tf);
		gq.setTableList(table);
		gq.setQuery(tmp, GhostQuery.SqlTypes.delete); 
		return gq;
	}

	public static GhostQuery selectQueryBlessSave(boolean isDistinct, List<IMetaTable> tables, GhostFieldMap selectFieldMap,GhostTableFilter tf, IGhostVariable<?,?> gmb, IMetaField blobField, IMetaGhostVariableTable destTable){
		 GhostQuery gq = new GhostQuery();
		 IMetaField sequenceColun = destTable.getUidSequenceColumn();
		 String useDistinct = GhostDBStaticVariables.GHOST_DB_DISTINCT_CLAUSE;
		 String tfSQL = GhostDBStaticVariables.EMPTY_STR;
		 
		 String fields = GhostDBStaticVariables.EMPTY_STR;
		 
		 String remoteBlobField = GhostDBStaticVariables.EMPTY_STR;
		 String remoteFromTable = GhostDBStaticVariables.EMPTY_STR;
		 String remoteWhereClause = GhostDBStaticVariables.EMPTY_STR;
		 
		 
		 if(gmb.getTable() != MetaTables.GHOST_VM){
			    IMetaTable table = gmb.getTable();
				remoteBlobField = GhostDBStaticVariables.COMMA + 
                                 "DECODE( LENGTH" + GhostDBStaticVariables.OPEN_PARENTHESES + gmb.getVMInsertColumn() + 
                                 GhostDBStaticVariables.CLOSE_PARENTHESES + GhostDBStaticVariables.COMMA + "0" + GhostDBStaticVariables.COMMA + 
                                 blobField.getColumnName() + GhostDBStaticVariables.COMMA + "NULL" + GhostDBStaticVariables.COMMA +
                                 blobField.getColumnName() + GhostDBStaticVariables.COMMA + gmb.getVMInsertColumn() + 
                                 GhostDBStaticVariables.CLOSE_PARENTHESES +  GhostDBStaticVariables.SPACE + blobField.getColumnName();
				
				remoteFromTable = GhostDBStaticVariables.COMMA + table.getTableName() + GhostDBStaticVariables.SPACE + table.getAlias();
				
				remoteWhereClause = GhostDBStaticVariables.SPACE + GhostDBStaticVariables.AND + GhostDBStaticVariables.SPACE + MetaTables.GHOST_VM.getAlias() +
                                   GhostDBStaticVariables.PERIOD + MetaTables.GHOST_VM.getGhostPointer() + GhostDBStaticVariables.SPACE +
                                   GhostDBStaticVariables.EQUALS + GhostDBStaticVariables.SPACE + table.getAlias() + GhostDBStaticVariables.PERIOD + gmb.getIdColumn().getColumnName();
				
				if(table.usePartitionField()){
					remoteWhereClause += GhostDBStaticVariables.SPACE + GhostDBStaticVariables.AND + GhostDBStaticVariables.SPACE +
			    	                     MetaTables.GHOST_VM.getAlias() + GhostDBStaticVariables.PERIOD + MetaTables.GHOST_VM.getGhostPointerPartitionDate() + 
			    	                     GhostDBStaticVariables.SPACE + GhostDBStaticVariables.EQUALS + GhostDBStaticVariables.SPACE + 
			    	                     table.getAlias()+ GhostDBStaticVariables.PERIOD + table.getPartitionFieldName();
			    }
				
				fields = getConcatFieldStringFromBlessMap(selectFieldMap,GhostDBStaticVariables.COMMA);
			    IMetaField dummy = null;
				selectFieldMap.put(selectFieldMap.size(), blobField, dummy);
				
		 } else{
			 selectFieldMap.put(selectFieldMap.size(), blobField, MetaTables.GHOST_VM.getBlobValue());
			 fields = getConcatFieldStringFromBlessMap(selectFieldMap,GhostDBStaticVariables.COMMA);
		 }
		 
		if(tf!=null){
				tfSQL = tf.getFilterSQL();
		}
		 if(!isDistinct){
			 useDistinct = "";
		 }
		 //TODO: Fix to make multiple field primary keys work!
		 String tmp = GhostDBStaticVariables.SELECT_SELECT + useDistinct + GhostDBStaticVariables.SPACE +
		              fields +
		              remoteBlobField + GhostDBStaticVariables.COMMA +
		              sequenceColun + GhostDBStaticVariables.SPACE + destTable.getPrimaryKey().getColumnName() +
		              GhostDBStaticVariables.SELECT_FROM + GhostDBStaticVariables.getConcatTableString(tables,GhostDBStaticVariables.COMMA) +
		              remoteFromTable +
		              GhostDBStaticVariables.SELECT_WHERE1E1 + tfSQL +
		              remoteWhereClause;
		 
		 
		 selectFieldMap.put(selectFieldMap.size(), destTable.getPrimaryKey(), sequenceColun);
		 //selectFieldMap.put(selectFieldMap.size(), destTable.getIMBTBlobColumn(), blobField);
		 
		 gq.setFieldList(selectFieldMap.getFieldList());
		 gq.setTableList(tables);
		 gq.setTableFilter(tf);
		 gq.setQuery(tmp, GhostQuery.SqlTypes.select);
		 logger.debug("GhostQueryConstructor:selectQuery with BlessFieldMap:  " + GhostVariableWrapper.wrapVariable(tmp));
		 return gq;
	}
	
	protected static GhostQuery selectQueryBlessSave(boolean isDistinct, List<IMetaTable> tables, GhostFieldMap ghostFieldMap,GhostTableFilter tf, IGhostCollection<?> gmbc, IMetaField valueField,
                                                    String additionalColumns,List<String> baseJoinString, GhostFieldMap selectFieldRemapping, String topLevelRemappingColumns, IMetaGhostVariableTable destTable) {
		GhostQuery gq = new GhostQuery();
		IMetaField sequenceColumn = destTable.getUidSequenceColumn();
		String useDistinct = GhostDBStaticVariables.GHOST_DB_DISTINCT_CLAUSE;
		String tfSQL = GhostDBStaticVariables.EMPTY_STR;
		StringBuilder addJoinCondition = new StringBuilder();
		String additionalTables = GhostDBStaticVariables.getConcatTableString(tables,GhostDBStaticVariables.COMMA);
		String ghostFieldMapColumns = GhostDBStaticVariables.EMPTY_STR;
		
		if(baseJoinString != null){
			for(String bjstr: baseJoinString){
				addJoinCondition.append(GhostDBStaticVariables.SPACE);
				addJoinCondition.append(GhostDBStaticVariables.AND);
				addJoinCondition.append(GhostDBStaticVariables.SUBQUERY_BASE_TABLE_NAME);
				addJoinCondition.append(GhostDBStaticVariables.PERIOD);
				addJoinCondition.append(bjstr);
				addJoinCondition.append(GhostDBStaticVariables.SPACE);
			}
		}
		logger.debug("BlessGhost.Save Collection: " + GhostVariableWrapper.wrapVariable(gmbc.getSQLQuery()));
		
		if(tf!=null){
			tfSQL = tf.getFilterSQL();
		}
		if(!isDistinct){
			useDistinct = "";
		}
		
//		if(!topLevelRemappingColumns.isEmpty()){
//			topLevelRemappingColumns = topLevelRemappingColumns;
//		}
		
		if(!additionalTables.isEmpty()){
			additionalTables += GhostDBStaticVariables.COMMA;
		}
		if(ghostFieldMap.size()>0){
			ghostFieldMapColumns = getConcatFieldStringFromBlessMap(ghostFieldMap,GhostDBStaticVariables.COMMA) + GhostDBStaticVariables.COMMA;
		}
		
		//TODO: Fix to make multiple field primary keys work!
		String tmp = GhostDBStaticVariables.SELECT_SELECT + useDistinct + GhostDBStaticVariables.SPACE +
					 ghostFieldMapColumns +
					 topLevelRemappingColumns +
					 GhostDBStaticVariables.COMMA + sequenceColumn + GhostDBStaticVariables.SPACE + destTable.getPrimaryKey().getColumnName() +
					 GhostDBStaticVariables.COMMA + "b_value " + valueField.getColumnName() +         
					 GhostDBStaticVariables.SELECT_FROM + additionalTables +
					 GhostDBStaticVariables.OPEN_PARENTHESES +
				 	 gmbc.getSQLQuery(additionalColumns) + 
					 GhostDBStaticVariables.CLOSE_PARENTHESES + GhostDBStaticVariables.SPACE +
					 GhostDBStaticVariables.SUBQUERY_BASE_TABLE_NAME +
					 GhostDBStaticVariables.SELECT_WHERE1E1 + tfSQL + addJoinCondition;
		
		ghostFieldMap.addAllToEnd(selectFieldRemapping);
		ghostFieldMap.put(ghostFieldMap.size(), destTable.getPrimaryKey(), sequenceColumn);
		
		ghostFieldMap.put(ghostFieldMap.size(), valueField, gmbc.getVMInsertColumn());
		gq.setFieldList(ghostFieldMap.getFieldList());
		gq.setTableList(tables);
		gq.setTableFilter(tf);
		gq.setQuery(tmp, GhostQuery.SqlTypes.select);
		logger.debug("GhostQueryConstructor:selectQuery with BlessFieldMap:  " + GhostVariableWrapper.wrapVariable(tmp));
		return gq;
	}	

}


