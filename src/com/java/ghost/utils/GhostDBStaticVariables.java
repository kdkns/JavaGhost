package com.java.ghost.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import oracle.sql.CharacterSet;
import oracle.sql.NUMBER;

import org.apache.log4j.Logger;

import com.java.ghost.Exceptions.GhostDBOwnerDoesNotExistException;
import com.java.ghost.Exceptions.GhostQueryBuilderException;
import com.java.ghost.MetaTableTypes.IMetaField;
import com.java.ghost.MetaTableTypes.IMetaFieldConstant;
import com.java.ghost.MetaTableTypes.IMetaFieldMultiField;
import com.java.ghost.MetaTableTypes.IMetaTable;
import com.java.ghost.MetaTableTypes.MetaField;
import com.java.ghost.MetaTableTypes.Tables.MetaFieldConstant;
import com.java.ghost.MetaTableTypes.Tables.MetaTables;

public class GhostDBStaticVariables {
	private static Logger logger = Logger.getLogger("GhostDBStaticVariables");
	private static SecureRandom SECURE_RANDOM = new SecureRandom();

	
	public static final int DB_MAX_COLUMN_ALIAS_LENGTH  = 30;
	public static final int DB_MAX_COLUMN_NAME_LENGTH  = 30;
	
	public static final int DB_MAX_TABLE_ALIAS_LENGTH  = 30;
	public static final int DB_MAX_TABLE_NAME_LENGTH  = 30;
	
	public static final String DB_PK_AIAS = "PK_";
	public static final String DB_SEQUENCE_PREFIX = "LS_SEQ_";
	public static final String DB_SEQUENCE_POSTFIX = ".NEXTVAL";
	
	public static final CharacterSet dbCharacterSet = CharacterSet.make(CharacterSet.WE8ISO8859P1_CHARSET);
	
	public static final String GHOST_BLOB_UTIL_PKG = "ghost_blob_util";
	public static final int GHOST_BLOB_UTIL_BINARY_OPERATION_ADD = 0;
	public static final int GHOST_BLOB_UTIL_BINARY_OPERATION_SUBTRACT = 1;
	public static final int GHOST_BLOB_UTIL_BINARY_OPERATION_MULTIPLY = 2;
	public static final int GHOST_BLOB_UTIL_BINARY_OPERATION_DIVIDE = 3;
	
	public static final String GHOST_BLOB_UTIL_POSSESS_TYPE_METABLOB = "MB";
	public static final String GHOST_BLOB_UTIL_POSSESS_TYPE_METANUMBER = "MN";
	public static final String GHOST_BLOB_UTIL_POSSESS_TYPE_METASTRING = "MS";
	public static final String GHOST_BLOB_UTIL_POSSESS_TYPE_BLOB = "B";
	public static final String GHOST_BLOB_UTIL_POSSESS_TYPE_NUMBER = "N";
	public static final String GHOST_BLOB_UTIL_POSSESS_TYPE_STRING = "S";
	
	//DB Object Type Codes variable types
	public static enum DBObjectTypeCodes{
		NUMBER,
		STRING,
		BLOB,
		METANUMBER,
		METASTRING,		
		METABLOB
	}
	
	
	public static final Class<NUMBER> DB_UIDVM_JAVA_TYPE = NUMBER.class;
	
	public static final String GHOST_BLOB_UTIL_ISEQUAL = "is_equal_blob";
	public static final String GHOST_BLOB_UTIL_ISEQUAL_ARGS = "(? , ? )";

	public static final String GHOST_BLOB_UTIL_POSSESSBULK = "possess_bulk_meta_blob";
	public static final String GHOST_BLOB_UTIL_POSSESSBULK_ARGS = "(?, ?)";
	
	public static final String GHOST_BLOB_UTIL_POSSESS_RETURN_COUNT = "possess_return_count";
	public static final String GHOST_BLOB_UTIL_POSSESS_RETURN_COUNT_ARGS = "(?, ?, ?, ?, ?)";

	public static final String GHOST_BLOB_UTIL_POSSESS = "possess";
	public static final String GHOST_BLOB_UTIL_POSSESS_ARGS = "(?, ?, ?, ?, ?)";	

	public static final String GHOST_UID_VM_SEQUENCE = "Seq_GHOSTUIDVM";
	public static final String GHOST_TID_VM_SEQUENCE = "Seq_GHOSTTIDVM";
	
	//Ghost table fields
	public static final String GHOST_UIDVM_COLUMN = "uidvm";
	public static final String GHOST_CIDVM_COLUMN = "ghost_collection_id";
	public static final String GHOST_TIDVM_COLUMN = "ghost_transaction_id";
	public static final String GHOST_PIDVM_COLUMN = "ghost_possession_id";
	public static final String GHOST_VM_POINTER_COLUMN = "ghost_pointer";
	public static final String GHOST_VM_POINTER_PARTITION_COLUMN = "ghost_pointer_partition_date";
	public static final String GHOST_VM_BULK_DRIVE_FIELD_COLUMN = "ghost_bulk_drive_field";
	public static final String GHOST_VM_BULK_INSERT_ID_COLUMN = "bulk_insert_id";
	public static final String GHOST_VM_POINTER_TABLE_COLUMN = "ghost_pointer_table";
	public static final String GHOST_VM_POINTER_FIELD_COLUMN = "ghost_pointer_field";
	public static final String GHOST_VM_DATA_COLUMN = "ghost_data_column";
	public static final String GHOST_VM_RANGEID_FIELD = "range_id";
	
	
	// Custom Attribute fields
	public static final String GHOST_VM_CUSTOM_1 = "custom_1";
	public static final String GHOST_VM_CUSTOM_2 = "custom_2";
	public static final String GHOST_VM_CUSTOM_3 = "custom_3";
	public static final String GHOST_VM_CUSTOM_4 = "custom_4";
	public static final String GHOST_VM_CUSTOM_5 = "custom_5";
	public static final String GHOST_VM_CUSTOM_6 = "custom_6";
	public static final String GHOST_VM_CUSTOM_7 = "custom_7";
	public static final String GHOST_VM_CUSTOM_8 = "custom_8";
	public static final String GHOST_VM_CUSTOM_9 = "custom_9";
	public static final String GHOST_VM_CUSTOM_10 = "custom_10";
	public static final String GHOST_VM_CUSTOM_11 = "custom_11";
	public static final String GHOST_VM_CUSTOM_12 = "custom_12";
	public static final String GHOST_VM_CUSTOM_13 = "custom_13";
	public static final String GHOST_VM_CUSTOM_14 = "custom_14";
	public static final String GHOST_VM_CUSTOM_15 = "custom_15";
	public static final String GHOST_VM_CUSTOM_16 = "custom_16";
	public static final String GHOST_VM_CUSTOM_17 = "custom_17";
	public static final String GHOST_VM_CUSTOM_18 = "custom_18";
	public static final String GHOST_VM_CUSTOM_19 = "custom_19";
	public static final String GHOST_VM_CUSTOM_20 = "custom_20";
	
	public static final String GHOST_VM_CUSTOM_DATE_1 = "custom_date_1";
	public static final String GHOST_VM_CUSTOM_DATE_2 = "custom_date_2";
	public static final String GHOST_VM_CUSTOM_DATE_3 = "custom_date_3";
	public static final String GHOST_VM_CUSTOM_DATE_4 = "custom_date_4";
	public static final String GHOST_VM_CUSTOM_DATE_5 = "custom_date_5";
	public static final String GHOST_VM_CUSTOM_DATE_6 = "custom_date_6";
	public static final String GHOST_VM_CUSTOM_DATE_7 = "custom_date_7";
	public static final String GHOST_VM_CUSTOM_DATE_8 = "custom_date_8";
	public static final String GHOST_VM_CUSTOM_DATE_9 = "custom_date_9";
	public static final String GHOST_VM_CUSTOM_DATE_10 = "custom_date_10";
	
	//Columns to use for each datatype to insert into the vm
	public static final String GHOST_VM_NUMBER_INSERT = "number_value";
	public static final String GHOST_VM_STRING_INSERT = "string_value";
	public static final String GHOST_VM_BLOB_INSERT = "blob_value";

	public static final String GHOST_POSSESS_REPLACE_STRING = ":PID";
	public static final String GHOST_COLLECTION_REPLACE_STRING = ":CID";
	public static final String DB_BIND_VALUE = ":B";

	//Sql construction and filter constants
	public static final String GHOST_DB_ANDCLAUSE = " AND ";
	public static final String GHOST_DB_ORCLAUSE = " OR ";
	public static final String GHOST_DB_DISTINCT_CLAUSE = " DISTINCT ";
	public static final String GHOST_DB_CURRENT_TIME = "SYSDATE";
	public static final String GHOST_DB_DATEFORMAT_REPLACE_STRING = "<DATE>";
	public static final String GHOST_DB_DATEFORMAT = "TO_DATE('<DATE>','MM/DD/YYYY HH:MI:SS AM')";
	public static final String GHOST_CALENDER_DATEFORMAT_STRING = "MM/dd/yyyy hh:mm:ss a";
//	public static final SimpleDateFormat GHOST_CALENDER_DATEFORMATER = new SimpleDateFormat(GHOST_CALENDER_DATEFORMAT_STRING);
	
	public static final String GHOST_DB_FUNCTION_COUNT = " COUNT(0) ";
	public static final String GHOST_DB_FUNCTION_COUNT_ALAIS = "countSize";
	public static final String GHOST_DB_FUNCTION_ROWNUM = " ROWNUM ";
	public static final String GHOST_DB_FUNCTION_ROWNUM_ALAIS = "rownumSize";
		
    //DB variable types
	public static enum DBTypes{
		NUMBER,
		DATE,
		VARCHAR,
		CHAR,
		FLOAT,		
		BLOB
	}
	
	
	//Constant columns avialabile to all tables
	public static MetaField ROWNUMFIELD;
	public static MetaField COUNTFIELD;
	public static MetaField NULLFIELD;

	static{
			ROWNUMFIELD = new MetaFieldConstant(null, DBTypes.NUMBER, GHOST_DB_FUNCTION_ROWNUM, 0, 0, false, GHOST_DB_FUNCTION_ROWNUM_ALAIS,true);
			COUNTFIELD = new MetaFieldConstant(null,DBTypes.NUMBER,GHOST_DB_FUNCTION_COUNT,0, 0, false, GHOST_DB_FUNCTION_COUNT_ALAIS,true);
			NULLFIELD = new MetaFieldConstant(null,GhostDBStaticVariables.DBTypes.NUMBER,GhostDBStaticVariables.NULL,0,0,false);
	}
	
	public static final String COMMA = ",";
	public static final String SEMI_COLAN = ";";
	public static final String SINGLE_QUOTE = "'";
	public static final String OPEN_PARENTHESES = "(";
	public static final String CLOSE_PARENTHESES = ")";
	public static final String CLOSE_BRACKET = "}";
	public static final String OPEN_BRACKET = "{";
	public static final String SPACE = " ";
	public static final String PERIOD = ".";
	public static final String EMPTY_STR ="";
	
	public static final String EQUALS =" = ";
	public static final String NOTEQUALS =" != ";
	public static final String GREATERTHAN =" > ";
	public static final String GREATERTHANEQUALS =" >= ";
	public static final String LESSTHAN =" < ";
	public static final String LESSTHANEQUALS =" <= ";
	public static final String LIKE =" LIKE ";
	public static final String INCLAUSE =" IN ";
	public static final String NOTINCLAUSE =" NOT IN ";
	public static final String BETWEEN =" BETWEEN ";
	
	public static final String AND = "AND ";
	public static final String OR = "OR ";
	public static final String STRINGCONCAT = "||";
	public static final String QUESTIONMARK = "?";
	public static final String NULL = "NULL";	
	public static final String ISNULL = "IS " + NULL;
	public static final String EMPTY_DB_STRING = "''";
	
	public static final String DB_FUNCTION_CALL_RETURN = "{ ? = call ";
	public static final String DB_FUNCTION_CALL = "{ call ";
	public static final String DB_CUSTOM_COLUMN_SEPERATOR = "','";
	
	//Ghost Query Constructor constants
	public static final String SELECT_SELECT = "SELECT ";
	public static final String SELECT_FROM = " FROM ";
	public static final String SELECT_WHERE1E1 = " WHERE 1=1 ";
	public static final String SELECT_WHERE = " WHERE ";
	public static final String SELECT_HAVING = " HAVING  ";
	public static final String SELECT_GROUPBY = " GROUP BY  ";
	public static final String SELECT_ORDERBY = " ORDER BY  ";
	public static final String INSERT_INTO =" INSERT INTO ";
	public static final String INSERT_VALUES =")VALUES(";
	public static final String UPDATE_UPDATE =" UPDATE ";
	public static final String UPDATE_SET =" SET ";
//	public static final String UPDATE_EQUALS = " = ";
	public static final String EQUALS_PARAMETER = " = ? ";
	public static final String DELETE_DELETE = "DELETE FROM ";

	public static final String FUNCTION_SUM = "SUM";
	public static final String FUNCTION_AVG = "AVG";
	public static final String FUNCTION_MIN = "MIN";
	public static final String FUNCTION_MAX = "MAX";
	public static final String FUNCTION_NVL = "NVL";
	public static final String FUNCTION_MODIFY_DATE_SECONDS = " + ((1/86400) * ";
	public static final String FUNCTION_MODIFY_DATE_SECONDS_235959 = " + (86399/86400)";
	public static final String ORDERBYASC = " ASC";
	public static final String ORDERBYDESC = " DESC";

	private static final String bindDateReplacementString = SINGLE_QUOTE+GHOST_DB_DATEFORMAT_REPLACE_STRING+SINGLE_QUOTE;
	public static final IDBFunctionValue DB_CURRENT_TIME = new DBFunctionValue("SYSDATE", DBTypes.DATE);

	public static final String DB_SEQUENCE_ALIAS_CONSTANT = "PK_UIDSEQUENCE";
	public static final Object SUBQUERY_BASE_TABLE_NAME = "base";
	public static final String DB_OBJECT_VALUE_ALIAS = "b_value";
	public static final String DB_OUTER_JOIN = "(+)";	

	public static enum DBOwnerList {
		NDLSTAR,
		LODSTAR,
		OWNER
	};
	
//	public static ArrayDescriptor GHOST_RULES_TYPE_DESCRIPTOR;
//	
//	static {
//		try {
//			GHOST_RULES_TYPE_DESCRIPTOR = ArrayDescriptor.createDescriptor("GHOST_TAB_RULES",GhostJDBCUtils.getConnection());
//		} catch (SQLException e) {
//			logger.error(e.getMessage(), e);
//			throw new GhostRuntimeException(e);
//		}
//	}
	
	public static String ghostCalendarDateFormater(Date date){
		return (new SimpleDateFormat(GHOST_CALENDER_DATEFORMAT_STRING)).format(date);
	}
	
//	public static String wrapDate(Calendar c){
//		return GHOST_DB_DATEFORMAT.replaceFirst(GHOST_DB_DATEFORMAT_REPLACE_STRING,GHOST_CALENDER_DATEFORMATER.format(c));
//	}

//	public static String wrapDate(Date d){
//		return GHOST_DB_DATEFORMAT.replaceFirst(GHOST_DB_DATEFORMAT_REPLACE_STRING,GHOST_CALENDER_DATEFORMATER.format(d));
//	}
	
//	public static String wrapBindDate(Date d) {
//		return GHOST_CALENDER_DATEFORMATER.format(d);
//	}
	
	public static String wrapDate(IGDate date){
		return GHOST_DB_DATEFORMAT.replaceFirst(GHOST_DB_DATEFORMAT_REPLACE_STRING,ghostCalendarDateFormater(date.getJavaDate()));
	}
	
	public static Object wrapBindDate(IGDate date) {
		return ghostCalendarDateFormater(date.getJavaDate());
	}
	
	public static String wrapBindToDate(String formattedDate){
		return GHOST_DB_DATEFORMAT.replaceFirst(bindDateReplacementString,formattedDate);
	}
	
	public static String wrapString(String s){
		if(s == null){
			return null;
		}
		return SINGLE_QUOTE + s + SINGLE_QUOTE;
	}
	
	public static String generateUniqueString(String str){
		return "X" + String.valueOf(Math.abs((Integer) (GhostHash.hashCharFunction((new BigInteger(130,SECURE_RANDOM)).toString()+str, 0, str.length(), 31))) );
	}

	public static String checkDBCharacterLength(String value, int maxLength){
		if( value.length()>maxLength){
			    CharSequence s = value;
			    //TODO: Do I really want to do this?
			    return generateUniqueString(s.toString());
		}
		return value;
	}
	
	public static String getConcatStringList(List<String> listStrings, String concatOperator) {
		 StringBuilder result = new StringBuilder();
		 if((listStrings!=null) && listStrings.size()!=0){
			 int listSize = listStrings.size() -1;
			 
			 for (int x=0; x<= listSize-1;x++){
				 result.append(listStrings.get(x) + concatOperator);
			 }
			 result.append(listStrings.get(listSize));
		 }
		 return result.toString();
	}
	
	public static String getConcatTableString(List<IMetaTable> tables, String concatOperator) {
		 StringBuilder result = new StringBuilder();
		 if((tables!=null) && (tables.size()!=0)){
			 int listSize = tables.size() -1;
			 
			 for (int x=0; x<= listSize-1;x++){
				 result.append(((IMetaTable)tables.get(x)).getTableName() + GhostDBStaticVariables.SPACE + ((IMetaTable)tables.get(x)).getAlias() + concatOperator);
			 }
			 result.append(((IMetaTable)tables.get(listSize)).getTableName() + GhostDBStaticVariables.SPACE + ((IMetaTable)tables.get(listSize)).getAlias());
		 }
		 return result.toString();
	}
	
	public static String getConcatFieldString(List<IMetaField> fieldList, String concatOperator){
		 StringBuilder result = new StringBuilder();
		 int listSize = fieldList.size() -1;
		 IMetaField field = null;
		 if(listSize>=0){
			 for (int x=0; x<= listSize-1;x++){
				 field = (IMetaField)fieldList.get(x);
				 if(!field.isFunction()){
                    result.append(field.getAssociatedTableAlias() + GhostDBStaticVariables.PERIOD + field.getColumnName() + GhostDBStaticVariables.SPACE + field.getAlias() + concatOperator);
				 }else{
				    result.append(field.getColumnName() + GhostDBStaticVariables.SPACE + field.getAlias() + concatOperator); 
				 }
			 }
			 field = (IMetaField)fieldList.get(listSize);
			 if(!field.isFunction()){
                result.append(field.getAssociatedTableAlias() + GhostDBStaticVariables.PERIOD + field.getColumnName() + GhostDBStaticVariables.SPACE + field.getAlias());
			 }else{
				result.append(field.getColumnName() + GhostDBStaticVariables.SPACE + field.getAlias()); 
			 }
		 }
		 return result.toString();
	 }
	
	public static String getConcatFieldStringNoAlias(List<IMetaField> fieldList, String concatOperator){
		 StringBuilder result = new StringBuilder();
		 int listSize = fieldList.size() -1;
		 IMetaField field = null;
		 if(listSize>=0){
			 for (int x=0; x<= listSize-1;x++){
				 field = (IMetaField)fieldList.get(x);
				 if(!field.isFunction()){
                   result.append(field.getAssociatedTableAlias() + GhostDBStaticVariables.PERIOD + field.getColumnName() + GhostDBStaticVariables.SPACE + concatOperator);
				 }else{
					 result.append(field.getColumnName() + GhostDBStaticVariables.SPACE + concatOperator); 
				 }
			 }
			 field = (IMetaField)fieldList.get(listSize);
			 if(!field.isFunction()){
               result.append(field.getAssociatedTableAlias() + GhostDBStaticVariables.PERIOD + field.getColumnName() + GhostDBStaticVariables.SPACE);
			 }else{
				 result.append( field.getColumnName() + GhostDBStaticVariables.SPACE); 
			 }
		 }
		 return result.toString();
	 }

	public static String getFullyQualifiedName(String aliasName, DBOwnerList owner) {
		return getSchemaName(owner) + GhostDBStaticVariables.PERIOD + aliasName;
//		switch(owner){
//          case NDLSTAR : return System.getProperty("com.ercot.ndlstarOwner") + GhostDBStaticVariables.PERIOD + aliasName;
//          case LODSTAR : return System.getProperty("com.ercot.lodstarOwner") + GhostDBStaticVariables.PERIOD + aliasName;
//          case OWNER : return System.getProperty("com.ercot.packageOwner") + GhostDBStaticVariables.PERIOD + aliasName;
//          default: throw new GhostDBOwnerDoesNotExistException("Could not find fully qualified name in properties file");
//		}
	}
	
	public static String getSchemaName(DBOwnerList owner) {
		switch(owner){
          case NDLSTAR : return System.getProperty("com.ercot.ndlstarOwner");
          case LODSTAR : return System.getProperty("com.ercot.lodstarOwner");
          case OWNER : return System.getProperty("com.ercot.packageOwner");
          default: throw new GhostDBOwnerDoesNotExistException("Unknown owner name!: " + GhostVariableWrapper.wrapVariable(owner));
		}
		
	}
	
	public static String wrapObject(Object obj){
		if(obj instanceof java.lang.Number){
			return obj.toString();
		}else if(obj instanceof BigDecimal){
		    return ((BigDecimal)obj).toPlainString();
		}else if(obj instanceof String){
			return GhostDBStaticVariables.wrapString((String) obj);
		}else if(obj instanceof IMetaFieldMultiField){
			return ((IMetaFieldMultiField) obj).getColumnName();
		}else if(obj instanceof IMetaFieldConstant){
			  if(!((IMetaFieldConstant) obj).isFunction()){// Put in place to deal with MetaFieldConstants that are a literal string or a Function type such as SUM(someColumn)
				  return GhostDBStaticVariables.wrapString(((IMetaFieldConstant) obj).getColumnName());	  
			  }
			return ((IMetaFieldConstant) obj).getColumnName();
		}else if(obj instanceof IMetaField){
			return ((IMetaField) obj).getFullyQualifiedTableAliaisWithColumnName();
		}else if(obj instanceof IGDate){
			return GhostDBStaticVariables.wrapDate((IGDate) obj);
		}else if(obj instanceof IDBFunctionValue){
			return ((IDBFunctionValue) obj).getStatment();
		}else{
			throw new GhostQueryBuilderException(" Object type not suppored in filter critera!");
		}
	}

//	public static IMetaField getInsertColumnBasedOnTableType(IMetaGhostVariableTable table) {
//		
//		switch(table.getIMGVTValueColumn().getType()){
//			case NUMBER: return MetaTables.GHOST_VM.getNumberValue();
//			case DATE: return MetaTables.GHOST_VM.getStringValue();
//			case VARCHAR: return MetaTables.GHOST_VM.getStringValue();		
//			case CHAR: return MetaTables.GHOST_VM.getStringValue();
//			case FLOAT: return MetaTables.GHOST_VM.getNumberValue();		
//			case BLOB : return MetaTables.GHOST_VM.getBlobValue();
//            default: throw new GhostRuntimeException("Can't find matching Ghost table column for data type based on table where data lives.");
//		}
//	}

	
	public static String createCollectionClause(Long collectionID){
	    return createCollectionClause(collectionID.toString());
	}
	
	public static String createCollectionClause(String collectionIDMaker){
	    return " BITAND(" +  MetaTables.GHOST_VM.getGhostCollectionId() + GhostDBStaticVariables.COMMA + collectionIDMaker + " ) = " + collectionIDMaker;
	}
		
}