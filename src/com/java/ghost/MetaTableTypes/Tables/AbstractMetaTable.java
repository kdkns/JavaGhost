package com.java.ghost.MetaTableTypes.Tables;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.java.ghost.Annotations.AttributeMapping;
import com.java.ghost.Annotations.GetColumnMethod;
import com.java.ghost.Annotations.MetaTableInfo;
import com.java.ghost.Annotations.MetaTablePartitionColumn;
import com.java.ghost.Annotations.MetaTablePrimaryKeyColumn;
import com.java.ghost.Exceptions.GhostAttributeDoesNotExistException;
import com.java.ghost.Exceptions.GhostRuntimeException;
import com.java.ghost.GhostAttributes.GhostAttributeEnum;
import com.java.ghost.GhostAttributes.IGhostAttribute;
import com.java.ghost.GhostAttributes.IGhostCustomAttribute;
import com.java.ghost.MetaTableTypes.IMetaField;
import com.java.ghost.MetaTableTypes.IMetaTable;
import com.java.ghost.utils.GhostDBStaticVariables;

public abstract class AbstractMetaTable implements IMetaTable {
	private static Logger logger = Logger.getLogger("AbstractMetaTable");
	private String _tableName;
	private MetaFieldMultiField _primaryKey;
	private boolean _isPrimaryKeySet = false; 
	private IMetaField _partitionField;
	private String _alias;
	private int _columnSize = 0;
	private boolean _usePartitionField;
	private List<IMetaField> _requiredInsertFieldList = new ArrayList<IMetaField>();
	private List<IMetaField> _fieldList = new ArrayList<IMetaField>();
	private MetaFieldConstant _uidSequenceColumn;
//	private final Set<GhostAttributeEnum> _attributeSet = new HashSet<GhostAttributeEnum>();
	private Map<GhostAttributeEnum, IMetaField> _attributeMap = new HashMap<GhostAttributeEnum, IMetaField>();
	
	private final static IMetaField  _countField = GhostDBStaticVariables.COUNTFIELD;
	private final static IMetaField  _rownumField = GhostDBStaticVariables.ROWNUMFIELD;
	private final static IMetaField  _nullField = GhostDBStaticVariables.NULLFIELD;
	
	
	protected final void setTableName(String tableName) {
		_tableName = GhostDBStaticVariables.checkDBCharacterLength(tableName, GhostDBStaticVariables.DB_MAX_TABLE_NAME_LENGTH);
	}
	
	protected void setPrimaryKey(MetaFieldMultiField primaryKey) {
		_primaryKey = primaryKey;
	}

	protected void setIsPrimaryKeySet(boolean isPrimaryKeySet) {
		_isPrimaryKeySet = isPrimaryKeySet;
	}

	
	protected void setPartitionField(IMetaField partitionField) {
		_partitionField = partitionField;
	}

	
	protected void setAlias(String alias) {
		_alias = GhostDBStaticVariables.checkDBCharacterLength(alias, GhostDBStaticVariables.DB_MAX_TABLE_ALIAS_LENGTH);
	}

	protected void setColumnSize(int columnSize) {
		_columnSize = columnSize;
	}

	
	protected void setUsePartitionField(boolean usePartitionField) {
		_usePartitionField = usePartitionField;
	}

	protected void setRequiredInsertFieldList(List<IMetaField> requiredInsertFieldList) {
		_requiredInsertFieldList = requiredInsertFieldList;
	}

	
	protected void setUidSequenceColumn(MetaFieldConstant uidSequenceColumn) {
		_uidSequenceColumn = uidSequenceColumn;
	}
	
	protected void setAttributeMap(Map<GhostAttributeEnum, IMetaField> attributeMap) {
		_attributeMap = attributeMap;
	}

	protected void setFieldList(List<IMetaField> fieldList) {
		_fieldList = fieldList;		
	}

	protected final void setDataToTable(IMetaTable table) {
		setTableName(table.getTableName());
		setAlias(table.getAlias());
		setPrimaryKey((MetaFieldMultiField) table.getPrimaryKey());
		setIsPrimaryKeySet(table.isPrimaryKeySet());
		setPartitionField(table.getPartitionField());
		setColumnSize(table.numberOfColumns());
		setUsePartitionField(table.usePartitionField());
		setRequiredInsertFieldList(table.getRequiredInsertFieldList());
		setFieldList(table.getAllColumns());
		setUidSequenceColumn(new MetaFieldConstant (table.getUidSequenceColumn()));
		setAttributeMap(table.getAttributeMap());
	}	
	

	protected final void initMetaTableObject()  {
		initMetaTableObject(this.getClass());
	}
	
	protected final void initMetaTableObject(Class<?> classOfObject) {
		Method[] methodList = classOfObject.getDeclaredMethods();
		Object o;
		for(int x=0;x<methodList.length;x++){
			try {
				if(methodList[x].isAnnotationPresent(GetColumnMethod.class)){
				    o = methodList[x].invoke(this);
				    getFieldList().add((IMetaField) o);
					
					if(((IMetaField)o).isRequired()){
						getRequiredInsertFieldList().add((IMetaField) o);
					}
					//Copied here to not invoke method twice.
					if(methodList[x].isAnnotationPresent(AttributeMapping.class)){
						getAttributeMap().put(methodList[x].getAnnotation(AttributeMapping.class).value(),(IMetaField) o);
						
					}
					
				}else{
					if(methodList[x].isAnnotationPresent(AttributeMapping.class)){
					    o = methodList[x].invoke(this);
					    getAttributeMap().put(methodList[x].getAnnotation(AttributeMapping.class).value(),(IMetaField) o);
						
					}
				}
				
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
		if(!this.isPrimaryKeySet()){
			setPrimaryKey(new MetaFieldMultiField(this));
		}
		
		Field[] fields = classOfObject.getDeclaredFields();
		IMetaField tempField = null;
		for(Field field: fields){
			field.setAccessible(true);
			if(field.isAnnotationPresent(MetaTablePrimaryKeyColumn.class)){
				try {
					tempField = ((IMetaField) field.get(this));
//					_primaryKey.addToColumnName( tempField.getColumnName() );
					_primaryKey.addFieldThatConstructsField(tempField);
					setIsPrimaryKeySet(true);
				} catch (IllegalArgumentException e) {
					logger.error(e.getMessage(),e);
					throw new GhostRuntimeException(e);
				} catch (IllegalAccessException e) {
					logger.error(e.getMessage(),e);
					throw new GhostRuntimeException(e);
				}
			}
			
			if(field.isAnnotationPresent(MetaTablePartitionColumn.class)){
				try {
					setPartitionField((IMetaField) field.get(this));
					setUsePartitionField(true);
				} catch (IllegalArgumentException e) {
					logger.error(e.getMessage(),e);
					throw new GhostRuntimeException(e);
				} catch (IllegalAccessException e) {
					logger.error(e.getMessage(),e);
					throw new GhostRuntimeException(e);
				}
			}
		}
	}
	
	
	//Still used by GhostQueryTable
	protected AbstractMetaTable(String table, String alias, int columnSize) {		
		setTableName(table);
		setColumnSize(columnSize);
		setAlias(alias);
	}
	
//	protected AbstractMetaTable(String table, String alias, int columnSize) {
//		GhostDbStaticVariables.checkDBCharacterLength(table, GhostDbStaticVariables.DB_MAX_TABLE_NAME_LENGTH);
//		GhostDbStaticVariables.checkDBCharacterLength(alias, GhostDbStaticVariables.DB_MAX_TABLE_ALIAS_LENGTH);
//		_table = table;
////		_primaryKey = primaryKey;
//		_columnSize = columnSize;
////		_partitionField = partitionField;
//		_alias = alias;
//		_usePartitonField = true;
//	}
	
	
	protected void setTableInfo(Class<? extends AbstractMetaTable> classOfObject){
		
		if(classOfObject.isAnnotationPresent(MetaTableInfo.class)){
			MetaTableInfo metaTableInfo = classOfObject.getAnnotation(MetaTableInfo.class);			
			
			// Set column size but grabbing all the columns by annotations
			// Could move this to populateFieldLists but want to keep constructor
			// true to original method
			int columnSize = this.numberOfColumns();
			Method[] methodList = classOfObject.getDeclaredMethods();
			for(int x=0;x<methodList.length;x++){
				try {
					if(methodList[x].isAnnotationPresent(GetColumnMethod.class)){
						columnSize++;
					}
				} catch (IllegalArgumentException e) {
					logger.error(e.getMessage(),e);
					throw new GhostRuntimeException(e);
				}
			}
			
			setTableName(GhostDBStaticVariables.getFullyQualifiedName(metaTableInfo.unqualifiedTableName(),metaTableInfo.schema()));			
			setAlias(metaTableInfo.unqualifiedTableName());
			setColumnSize(columnSize);
			setUidSequenceColumn(new MetaFieldConstant(null, GhostDBStaticVariables.DBTypes.NUMBER, GhostDBStaticVariables.getSchemaName(metaTableInfo.schema()) +
					                                   GhostDBStaticVariables.PERIOD + GhostDBStaticVariables.DB_SEQUENCE_PREFIX + metaTableInfo.unqualifiedTableName() +
					                                   GhostDBStaticVariables.DB_SEQUENCE_POSTFIX, 0, 0, false, GhostDBStaticVariables.DB_SEQUENCE_ALIAS_CONSTANT,true));
			
		}
	}
	
	protected AbstractMetaTable() {
		this(null);
	}
	
	protected AbstractMetaTable(Class<? extends AbstractMetaTable> classOfObject){
		Class<? extends AbstractMetaTable> currentClass = classOfObject ==null? this.getClass():(Class<? extends AbstractMetaTable>) classOfObject;
		setTableInfo(currentClass);
		
//		Class<? extends AbstractMetaTable> currentClass = classOfObject ==null? this.getClass():(Class<? extends AbstractMetaTable>) classOfObject;
//		if(currentClass.isAnnotationPresent(MetaTableInfo.class)){
//			MetaTableInfo metaTableInfo = currentClass.getAnnotation(MetaTableInfo.class);			
//			
//			// Set column size but grabbing all the columns by annotations
//			// Could move this to populateFieldLists but want to keep constructor
//			// true to original method
//			int columnSize = this.numberOfColumns();
//			Method[] methodList = currentClass.getDeclaredMethods();
//			for(int x=0;x<methodList.length;x++){
//				try {
//					if(methodList[x].isAnnotationPresent(GetColumnMethod.class)){
//						columnSize++;
//					}
//				} catch (IllegalArgumentException e) {
//					logger.error(e.getMessage(),e);
//				}
//			}
//			
//			_table = GhostDbStaticVariables.getFullyQualifiedName(metaTableInfo.unqualifiedTableName(),metaTableInfo.schema());			
//			_alias = metaTableInfo.unqualifiedTableName();
//			_columnSize = columnSize;
//			_uidSequenceColumn = new MetaFieldConstant(null, GhostDbStaticVariables.DBTypes.NUMBER, GhostDbStaticVariables.DB_SEQUENCE_PREFIX + _alias + GhostDbStaticVariables.DB_SEQUENCE_POSTFIX, 0, 0, false, GhostDbStaticVariables.DB_SEQUENCE_ALIAS_CONSTANT,true);
//			
//		}
	}

	public final IMetaField getPrimaryKey() {
		return _primaryKey;
	}
	
//	protected void setPrimaryKey(IMetaField pk) {
//		_primaryKey = pk;
//		_isPrimaryKetSet = true;
//	}

	public final boolean isPrimaryKeySet() {
		return _isPrimaryKeySet;
	}
	
//	protected final void setPartitionField(IMetaField partitionField) {
//		_partitionField = partitionField;
//		_usePartitonField = true;
//	}
	
	@Override
	public String getTableName() {
		return _tableName;
	}

	protected final void setNumberOfColumns(int num) {
		_columnSize = num;
	}
	
	@Override
	public final int numberOfColumns() {
		return _columnSize;
	}
	
	public final List<IMetaField> getAllColumns(){
		List<IMetaField> l = new ArrayList<IMetaField>();
		l.addAll(_fieldList);
		return l;
	}

	public final String getPartitionFieldName() {
		return _partitionField.getColumnName();
	}
	
	public final IMetaField getPartitionField() {
		return _partitionField;
	}
	

	@Override
	public final boolean usePartitionField() {
		return _usePartitionField;
	}
	
	public final IMetaField getCountField(){
		return _countField;
	}
	
	public final IMetaField getRownumField(){
		return _rownumField;
	}
	
	public final IMetaField getNullField(){
		return _nullField;
	}
	
	
//	private final IMetaField getFunctionField(IMetaField mf, GhostDBStaticVariables.DBTypes dataType, String left, String right, String alias) {
//		return new MetaFieldConstant(null,dataType,
//				             GhostDBStaticVariables.SPACE + left + mf.getFullyQualifiedTableAliaisWithColumnName() + right
//				             ,0,0, false, alias + mf.getAssociatedTableAlias() + mf.getColumnName() ,true);
//	}
//	
//	public final IMetaField getSumField(IMetaField mf) {
//		return getFunctionField(mf, mf.getType(), GhostDBStaticVariables.FUNCTION_SUM + GhostDBStaticVariables.OPEN_PARENTHESES, GhostDBStaticVariables.CLOSE_PARENTHESES, "sum_");
//	}
//	
//	public final IMetaField getMaxField(IMetaField mf) {
//		return getFunctionField(mf, mf.getType(), GhostDBStaticVariables.FUNCTION_MAX + GhostDBStaticVariables.OPEN_PARENTHESES, GhostDBStaticVariables.CLOSE_PARENTHESES, "max_");
//	}
//	
//	public final IMetaField getMinField(IMetaField mf) {
//		return getFunctionField(mf, mf.getType(), GhostDBStaticVariables.FUNCTION_MIN + GhostDBStaticVariables.OPEN_PARENTHESES, GhostDBStaticVariables.CLOSE_PARENTHESES, "min_");
//	}
//	
//	public final IMetaField getAvgField(IMetaField mf) {
//		return getFunctionField(mf, mf.getType(), GhostDBStaticVariables.FUNCTION_AVG + GhostDBStaticVariables.OPEN_PARENTHESES, GhostDBStaticVariables.CLOSE_PARENTHESES, "avg_");
//	}
//	
//	public final IMetaField getModifiedDateField(IMetaField mf, Number seconds){
//		if(mf.getType() != DBTypes.DATE){
//			throw new GhostQueryBuilderException("Column isn't a date type! Can't modify column with DATE math! Column is " + mf.getType() + " type.");
//		}
//		return getFunctionField(mf, mf.getType(), GhostDBStaticVariables.EMPTRY_STR, GhostDBStaticVariables.FUNCTION_MODIFY_DATE_SECONDS + seconds + GhostDBStaticVariables.CLOSE_PARENTHESES, "md_");
//	}
//	
//	public final IMetaField getModifiedDateFieldPlus235959(IMetaField mf){
//		if(mf.getType() != DBTypes.DATE){
//			throw new GhostQueryBuilderException("Column isn't a date type! Can't modify column with DATE math! Column is " + mf.getType() + " type.");
//		}
//		return getFunctionField(mf, mf.getType(), GhostDBStaticVariables.EMPTRY_STR, GhostDBStaticVariables.FUNCTION_MODIFY_DATE_SECONDS_235959 , "md_");
//	}
//	
////	public IMetaField getNVLField(IMetaField mf, IMetaField value) {
////		try {
////			return getFunctionField(mf, mf.getDbType(), GhostDbStaticVariables.FUNCTION_NVL + GhostDbStaticVariables.OPEN_PARENTHESES, GhostDbStaticVariables.COMMA +
////					                                    GhostDbStaticVariables.wrapObject(value) + GhostDbStaticVariables.CLOSE_PARENTHESES, "nvl_");
////		} catch (GhostQueryBuilderException e) {
////			logger.error(e.getMessage(),e);
////			//This should never happen as IMetaField is part of the wrapObject class for now
////			return null;
////		}
////	}
//	
//	public final IMetaField getNVLField(IMetaField mf, Object value){
//		return getFunctionField(mf, mf.getType(), GhostDBStaticVariables.FUNCTION_NVL + GhostDBStaticVariables.OPEN_PARENTHESES, GhostDBStaticVariables.COMMA +
//				                                    GhostDBStaticVariables.wrapObject(value) + GhostDBStaticVariables.CLOSE_PARENTHESES, "nvl_");
//	}
	
	public final String getAlias() {
		return _alias;
	}
	
	public final List<IMetaField> getRequiredInsertFieldList() {
		return _requiredInsertFieldList;
	}

	protected void setTableAliasforFields(String tableAlias) {
		setAlias(getAlias() + tableAlias);
		for(IMetaField i : getFieldList()){
			i.setTableAlias(i.getAssociatedTableAlias()+tableAlias);
			i.setAlias(i.getAlias()+tableAlias);
		}
	}
	
	public final IMetaField getUidSequenceColumn() {
		return _uidSequenceColumn;
	}
	
	public final Set<GhostAttributeEnum> getAttributes() {
		return _attributeMap.keySet();
	}
	
	public Map<GhostAttributeEnum, IMetaField> getAttributeMap(){
		return _attributeMap;
	}
	
	public final IMetaField getAttributeField(IGhostAttribute attribute){
		IMetaField iMetaField = getAttributeMap().get(attribute);//TODO: Check if Custom Attribute and fail here?
		if(iMetaField!=null){
			return iMetaField;
		}else{
			if(attribute instanceof IGhostCustomAttribute){
				return MetaTables.GHOST_VM.getCustomAttributeField((IGhostCustomAttribute)attribute);
			}
			throw new GhostAttributeDoesNotExistException(attribute);
		}
				
	}
	
	protected final List<IMetaField> getFieldList(){
		return _fieldList;
	}
	
	protected final boolean addAllToFieldList(Collection<? extends IMetaField> fields){
		return _fieldList.addAll(fields);
	}
}
