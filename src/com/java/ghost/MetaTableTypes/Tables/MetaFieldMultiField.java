package com.java.ghost.MetaTableTypes.Tables;

import java.util.ArrayList;
import java.util.List;

import com.java.ghost.GhostAttributes.IGhostCustomAttribute;
import com.java.ghost.MetaTableTypes.IMetaField;
import com.java.ghost.MetaTableTypes.IMetaFieldMultiField;
import com.java.ghost.MetaTableTypes.IMetaTable;
import com.java.ghost.utils.GhostDBStaticVariables;

public class MetaFieldMultiField extends MetaFieldConstant implements IMetaFieldMultiField{
    
	private final List<IMetaField> _fieldsOfMultiField = new ArrayList<IMetaField>();
	private String _prefix = GhostDBStaticVariables.EMPTY_STR;
	private String _seperator = GhostDBStaticVariables.EMPTY_STR;
	private boolean _isPrefixAndSeperatorSet;
	private static final int STRING_CONCAT_LENGTH = GhostDBStaticVariables.STRINGCONCAT.length();
	
	private void setPrefix(String prefix){
		_prefix = prefix;
	}
	
	private void setSeperator(String seperator){
		_seperator = seperator;
	}
	
	private void setIsPrefixAndSeperatorSet(boolean value){
		_isPrefixAndSeperatorSet = value;
	}
	
	private String getPrefix(){return _prefix;}
	private String getSeperator(){return _seperator;}
	private boolean isPrefixAndSeperatorSet(){return _isPrefixAndSeperatorSet;}
	
//	private MetaFieldMultiField(IMetaField iMetaField){
//		this(iMetaField.getAssociatedTable(), iMetaField.getType(), iMetaField.getColumnName(), iMetaField.getSize(), iMetaField.getScale(), iMetaField.isRequired(), iMetaField.isFunction());
//	}
//	
//	private MetaFieldMultiField(IMetaTable table, GhostDBStaticVariables.DBTypes dbType, String columnName, int size, int scale, boolean isRequired) {
//		this(table, dbType, columnName, size, scale, isRequired, columnName, false);
//	}
//    
//	private MetaFieldMultiField(IMetaTable table, GhostDBStaticVariables.DBTypes dbType, String columnName, int size, int scale, boolean isRequired, String alias) {
//		this(table, dbType, columnName, size, scale, isRequired, alias, false);
//	}
//	
//	private MetaFieldMultiField(IMetaTable table, GhostDBStaticVariables.DBTypes dbType, String columnName, int size, int scale, boolean isRequired, boolean isFunction) {
//    	this(table, dbType, columnName, size, scale, isRequired, columnName, isFunction);
//	}
    
	public MetaFieldMultiField(IMetaTable table) {
		this(table, GhostDBStaticVariables.DBTypes.VARCHAR, GhostDBStaticVariables.EMPTY_STR, 0, 0, true, GhostDBStaticVariables.DB_PK_AIAS + table.getAlias(), true);
	}
    
	private MetaFieldMultiField(IMetaTable table, GhostDBStaticVariables.DBTypes dbType, String columnName, int size, int scale, boolean isRequired, String alias, boolean isFunction) {
    	super(table,dbType,columnName,size,scale,isRequired,alias,isFunction);    	
	}
    
    public MetaFieldMultiField(String prefix, String seperator, IGhostCustomAttribute...ghostCustomAttributesEnum)  {
		this(MetaTables.GHOST_VM, GhostDBStaticVariables.DBTypes.VARCHAR, GhostDBStaticVariables.EMPTY_STR, 0, 0, false, GhostDBStaticVariables.EMPTY_STR, true);

		setPrefix(GhostDBStaticVariables.SINGLE_QUOTE + prefix + GhostDBStaticVariables.SINGLE_QUOTE);
		setSeperator(GhostDBStaticVariables.SINGLE_QUOTE + seperator + GhostDBStaticVariables.SINGLE_QUOTE);
		setIsPrefixAndSeperatorSet(true);
		
		for(IGhostCustomAttribute gcae: ghostCustomAttributesEnum){
			addFieldThatConstructsField(MetaTables.GHOST_VM.getCustomAttributeField(gcae));
		}
		
		//These are being set here manually which is dangerous rather then following the constructor pattern
		// Because we cannot call the constructor Afterwards. We'd either have to make a new constructor
		// anyway or do this which at least still follows some pattern
//		this.setColumnNameNoLengthCheck(columnName.toString());
		
		//This is not the true column size in the database but it set to the maximum length of the newly created value
//		this.setSize(getColumnName().length()); 
//		this.setAlias(columnName.toString());
	}
    

	@Override
	public List<IMetaField> getFieldsThatConstructField() {
		return _fieldsOfMultiField;
	}

	@Override
	public boolean addFieldThatConstructsField(IMetaField field) {
	    Boolean result = getFieldsThatConstructField().add(field);
		setColumnNameNoLengthCheck(constructColumnNameUsingFieldsAlias());
		setAlias(GhostDBStaticVariables.generateUniqueString(getColumnName()));
		setSize(getColumnName().length()); 
		return result;
	}
	
	
	private String constructColumnNameUsingFieldsAlias(){
		StringBuilder result = new StringBuilder();
		String seperatorString = GhostDBStaticVariables.EMPTY_STR;
		int substringLength = STRING_CONCAT_LENGTH;
		
		if(isPrefixAndSeperatorSet()){
			result.append(getPrefix());
			seperatorString = GhostDBStaticVariables.STRINGCONCAT + getSeperator(); 
			substringLength = 0;
		}
		for(IMetaField imf: getFieldsThatConstructField()){
			result.append(seperatorString);
			result.append(GhostDBStaticVariables.STRINGCONCAT);
			result.append(imf.getAlias());			
		}		
		return result.substring(substringLength);
	}
	
	private String constructFullyQuaifiedTableAliasColumnNameUsingFields(){
		StringBuilder result = new StringBuilder();
		String seperatorString = GhostDBStaticVariables.EMPTY_STR;
		int substringLength = STRING_CONCAT_LENGTH;
		
		if(isPrefixAndSeperatorSet()){
			result.append(getPrefix());
			seperatorString = GhostDBStaticVariables.STRINGCONCAT + getSeperator();
			substringLength = 0;
		}
		for(IMetaField imf: getFieldsThatConstructField()){
			result.append(seperatorString);
			result.append(GhostDBStaticVariables.STRINGCONCAT);
			result.append(imf.getFullyQualifiedTableAliaisWithColumnName());			
		}		
		return result.substring(substringLength);
	}
	
	@Override
	public String getFullyQualifiedTableAliaisWithColumnName() {
		return constructFullyQuaifiedTableAliasColumnNameUsingFields();
	}
	
//	@Override
//	public String toString() {
//		return getColumnName();
////		if(getColumnName().length()>result.length()){
////			return getColumnName();
////		}
//	}
    
    

}

