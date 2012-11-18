package com.ercot.java.ghost.MetaTableTypes.Tables;

import com.ercot.java.ghost.MetaTableTypes.IMetaField;
import com.ercot.java.ghost.MetaTableTypes.IMetaFieldConstant;
import com.ercot.java.ghost.MetaTableTypes.IMetaTable;
import com.ercot.java.ghost.MetaTableTypes.MetaField;
import com.ercot.java.ghost.utils.GhostDBStaticVariables;

public class MetaFieldConstant extends MetaField implements IMetaFieldConstant{
    
	public MetaFieldConstant(IMetaField iMetaField)  {
		this(iMetaField.getAssociatedTable(), iMetaField.getType(), iMetaField.getColumnName(), iMetaField.getSize(), iMetaField.getScale(), iMetaField.isRequired(), GhostDBStaticVariables.generateUniqueString(iMetaField.getAlias()), iMetaField.isFunction());
	}
	
//	public MetaFieldConstant(String prefix, String seperator, IGhostCustomAttribute...ghostCustomAttributesEnum)  {
//		this(MetaTables.GHOST_VM, GhostDBStaticVariables.DBTypes.VARCHAR, GhostDBStaticVariables.EMPTY_STR, 0, 0, false, true);
//		
//		StringBuilder columnName = new StringBuilder();
//		columnName.append(GhostDBStaticVariables.SINGLE_QUOTE);
//		columnName.append(prefix);
//		columnName.append(GhostDBStaticVariables.SINGLE_QUOTE);
//		
//		for(IGhostCustomAttribute gcae: ghostCustomAttributesEnum){
//			columnName.append(GhostDBStaticVariables.STRINGCONCAT);
//			columnName.append(GhostDBStaticVariables.SINGLE_QUOTE);
//			columnName.append(seperator);
//			columnName.append(GhostDBStaticVariables.SINGLE_QUOTE);
//			columnName.append(GhostDBStaticVariables.STRINGCONCAT);			
//			columnName.append(MetaTables.GHOST_VM.getCustomAttributeField(gcae).getColumnName());
//		}
//		//These are being set here manually which is dangerous rather then following the consturctor pattern
//		// Because we cannot call the constructor Afterwards. We'd either have to make a new constructor
//		// anyway or do this which at least still follows some pattern
//		this.setColumnNameNoLengthCheck(columnName.toString());
//		//This is not the true column size in the database but it set to the maximum lenght of the newly created value
//		this.setSize(columnName.length()); 
//		this.setAlias(columnName.toString());
//	}
	
    public MetaFieldConstant(IMetaTable table, GhostDBStaticVariables.DBTypes dbType, String columnName, int size, int scale, boolean isRequired){
		this(table, dbType, columnName, size, scale, isRequired, columnName, false);
	}
    
    public MetaFieldConstant(IMetaTable table, GhostDBStaticVariables.DBTypes dbType, String columnName, int size, int scale, boolean isRequired, String alias){
		this(table, dbType, columnName, size, scale, isRequired, alias, false);
	}
    
    public MetaFieldConstant(IMetaTable table, GhostDBStaticVariables.DBTypes dbType, String columnName, int size, int scale, boolean isRequired, boolean isFunction){
    	this(table, dbType, columnName, size, scale, isRequired, columnName, isFunction);
	}
    
    protected MetaFieldConstant(IMetaTable table) {
		this(table, GhostDBStaticVariables.DBTypes.VARCHAR, GhostDBStaticVariables.EMPTY_STR, 0, 0, true, GhostDBStaticVariables.DB_PK_AIAS + table.getAlias(), false);
	}
    
    public MetaFieldConstant(IMetaTable table, GhostDBStaticVariables.DBTypes dbType, String columnName, int size, int scale, boolean isRequired, String alias, boolean isFunction) {

    	setTable(table);
		setDbType(dbType);
		setColumnNameNoLengthCheck(columnName);
		setAlias(alias);
		setSize(size);
		setScale(scale);
		setIsFunction(isFunction);
		setIsRequired(isRequired);
	}
    
    

//	protected void addToColumnName(String str){
//    	if(getSize()==0){
//    		setColumnNameNoLengthCheck(getColumnName()+ str);
//    	}else{
//    		setColumnNameNoLengthCheck(getColumnName()+ GhostDBStaticVariables.STRINGCONCAT + str);
//    	}
//    	setSize(getColumnName().length());
//    }
}

