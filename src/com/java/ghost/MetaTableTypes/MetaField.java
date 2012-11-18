package com.java.ghost.MetaTableTypes;

import com.java.ghost.Exceptions.GhostQueryBuilderException;
import com.java.ghost.MetaTableTypes.Tables.MetaFieldConstant;
import com.java.ghost.utils.GhostDBStaticVariables;
import com.java.ghost.utils.GhostHash;
import com.java.ghost.utils.GhostDBStaticVariables.DBTypes;

public class MetaField implements IMetaField{
    protected String _tableName;
    protected IMetaTable _table; //TODO: This breaks if user gets alias through this method?
    protected String _tableNameAlias;
    protected GhostDBStaticVariables.DBTypes _dbType;
    protected String _columnName;
    protected String _alias;
    protected int _size;
    protected int _scale;
    protected boolean _isFunction;
    protected boolean _isRequired;
    
    protected MetaField(){};
    
    public MetaField(IMetaTable table, GhostDBStaticVariables.DBTypes dbType, String columnName, int size, int scale, boolean isRequired){
		this(table, dbType, columnName, size, scale, isRequired, columnName, false);
	}
    
    public MetaField(IMetaTable table, GhostDBStaticVariables.DBTypes dbType, String columnName, int size, int scale, boolean isRequired, String alias){
		this(table, dbType, columnName, size, scale, isRequired, alias, false);
	}
    
    public MetaField(IMetaTable table, GhostDBStaticVariables.DBTypes dbType, String columnName, int size, int scale, boolean isRequired, boolean isFunction){
    	this(table, dbType, columnName, size, scale, isRequired, columnName, isFunction);
	}
    
    public MetaField(IMetaTable table, GhostDBStaticVariables.DBTypes dbType, String columnName, int size, int scale, boolean isRequired, String alias, boolean isFunction) {
		setTable(table);
		setColumnName(columnName);
		setAlias(alias);
		setDbType(dbType);		
	    setSize(size);
	    setScale(scale);
	    setIsFunction(isFunction);
	    setIsRequired(isRequired);		
	}
    
    
    protected void setIsRequired(boolean isRequired) {
    	_isRequired = isRequired;		
	}

	protected void setIsFunction(boolean isFunction) {
    	_isFunction = isFunction;		
	}

	protected void setScale(int scale) {
		_scale = scale;		
	}
	
	protected void setSize(int size) {
		_size = size;		
	}

	protected void setDbType(DBTypes dbType) {
		_dbType = dbType;		
	}

	protected void setColumnName(String columnName) {
		_columnName = GhostDBStaticVariables.checkDBCharacterLength(columnName, GhostDBStaticVariables.DB_MAX_COLUMN_NAME_LENGTH);		
	}
	
	protected void setColumnNameNoLengthCheck(String columnName) {
		_columnName = columnName;		
	}

	protected void setTable(IMetaTable table) {
		_table  = table;
		//This is for custom metafields like COUNT and ROWNUM
		if(table != null){
			setTableName(table.getTableName());
			setTableAlias(table.getAlias());
		}		
	}

	protected void setTableName(String tableName) {
		_tableName = GhostDBStaticVariables.checkDBCharacterLength(tableName, GhostDBStaticVariables.DB_MAX_TABLE_NAME_LENGTH);
	}

	public String getAssociatedTableName() {
		return _tableName;
	}

	
	public String getColumnName() {
		return _columnName;
	}

	
	public int getSize() {
		return _size;
	}
	
	public int getScale() {
		return _scale;
	}
	
	public String getAlias() {
		return _alias;
	}

	
	public String getAssociatedTableAlias() {
		return _tableNameAlias;
	}
	
	@Override
	public String toString() {
		return _columnName;
	}

	
	public boolean isFunction() {
		return _isFunction;
	}

	
	public boolean isRequired() {
		return _isRequired;
	}

	
	public int compareTo(IMetaField arg0) {
		return (arg0.getColumnName()+ arg0.getAssociatedTableName()).compareTo(this.getColumnName()+ this.getAssociatedTableName());
	}
	
	public void setTableAlias(String tableAlias){
		_tableNameAlias = GhostDBStaticVariables.checkDBCharacterLength(tableAlias, GhostDBStaticVariables.DB_MAX_TABLE_ALIAS_LENGTH);		
	}
	
	public void setAlias(String alias){
		_alias = GhostDBStaticVariables.checkDBCharacterLength(alias, GhostDBStaticVariables.DB_MAX_COLUMN_ALIAS_LENGTH);		
	}

	
	public GhostDBStaticVariables.DBTypes getType() {
		return _dbType;
	}

	@Override
	public IMetaTable getAssociatedTable() {
		return _table;
	}

	@Override
	public String getFullyQualifiedTableAliasWithAliasName() {
		if(!isFunction()) {
			return getAssociatedTableAlias() + GhostDBStaticVariables.PERIOD + getAlias();
		}
		return getAlias();
	}

	@Override
	public String getFullyQualifiedTableAliaisWithColumnName() {
		if(!isFunction()) {
			return getAssociatedTableAlias() + GhostDBStaticVariables.PERIOD + getColumnName();
		}
		return getColumnName();
	}


	
	private final IMetaField getFunctionField(String left, String right, String alias) {
		return new MetaFieldConstant(null,this.getType(),
				             GhostDBStaticVariables.SPACE + left + this.getFullyQualifiedTableAliaisWithColumnName() + right
				             ,0,0, false, GhostDBStaticVariables.generateUniqueString(
				            		      alias + this.getAssociatedTableAlias() + this.getColumnName()) ,true);
	}
	
	@Override
	public final IMetaField getSumField() {
		return getFunctionField(GhostDBStaticVariables.FUNCTION_SUM + GhostDBStaticVariables.OPEN_PARENTHESES, GhostDBStaticVariables.CLOSE_PARENTHESES, "sum_");
	}
	
	@Override
	public final IMetaField getMaxField() {
		return getFunctionField(GhostDBStaticVariables.FUNCTION_MAX + GhostDBStaticVariables.OPEN_PARENTHESES, GhostDBStaticVariables.CLOSE_PARENTHESES, "max_");
	}
	
	@Override
	public final IMetaField getMinField() {
		return getFunctionField(GhostDBStaticVariables.FUNCTION_MIN + GhostDBStaticVariables.OPEN_PARENTHESES, GhostDBStaticVariables.CLOSE_PARENTHESES, "min_");
	}
	
	@Override
	public final IMetaField getAvgField() {
		return getFunctionField(GhostDBStaticVariables.FUNCTION_AVG + GhostDBStaticVariables.OPEN_PARENTHESES, GhostDBStaticVariables.CLOSE_PARENTHESES, "avg_");
	}
	
	@Override
	public final IMetaField getModifiedDateField(Number seconds){
		if(this.getType() != DBTypes.DATE){
			throw new GhostQueryBuilderException("Column isn't a date type! Can't modify column with DATE math! Column is " + this.getType() + " type.");
		}
		return getFunctionField(GhostDBStaticVariables.EMPTY_STR, GhostDBStaticVariables.FUNCTION_MODIFY_DATE_SECONDS + seconds + GhostDBStaticVariables.CLOSE_PARENTHESES, "md_");
	}
	
	@Override
	public final IMetaField getModifiedDateFieldPlus235959(){
		if(this.getType() != DBTypes.DATE){
			throw new GhostQueryBuilderException("Column isn't a date type! Can't modify column with DATE math! Column is " + this.getType() + " type.");
		}
		return getFunctionField(GhostDBStaticVariables.EMPTY_STR, GhostDBStaticVariables.FUNCTION_MODIFY_DATE_SECONDS_235959 , "md_");
	}
	
	@Override
	public IMetaField getNVLField(IMetaField value) {
    	return getFunctionField(GhostDBStaticVariables.FUNCTION_NVL + GhostDBStaticVariables.OPEN_PARENTHESES, GhostDBStaticVariables.COMMA +
	                                              GhostDBStaticVariables.wrapObject(value) + GhostDBStaticVariables.CLOSE_PARENTHESES, "nvl_");
	}
	
	@Override
	public final IMetaField getNVLField(Object value){
		return getFunctionField(GhostDBStaticVariables.FUNCTION_NVL + GhostDBStaticVariables.OPEN_PARENTHESES, GhostDBStaticVariables.COMMA +
				                                    GhostDBStaticVariables.wrapObject(value) + GhostDBStaticVariables.CLOSE_PARENTHESES, "nvl_");
	}
	
	
	public boolean equals(Object obj){
    	if(this == obj){
			return true;
		}
		if(obj!=null && (this.getClass() == obj.getClass())){
			MetaField mf = (MetaField) obj;
			return (getAssociatedTableName() + getColumnName() + getAlias()).equals(mf.getAssociatedTableName() + mf.getColumnName() + mf.getAlias());
		}
		return false;
    }
    
    public int hashCode() {
    	//TODO: hashcode function ok?
	    CharSequence s = getAssociatedTableName() + getColumnName() + getAlias();
	    return (Integer) GhostHash.hashCharFunction(s, 0, s.length(), 31);
	  }

	@Override
	public String getColumnNameAndAlias() {
		return getColumnName() + GhostDBStaticVariables.SPACE + getAlias();
	}
	
}

