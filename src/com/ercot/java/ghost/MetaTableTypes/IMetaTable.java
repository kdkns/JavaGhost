package com.ercot.java.ghost.MetaTableTypes;

import java.util.List;

import com.ercot.java.ghost.GhostAttributes.IAttributeTableList;

public interface IMetaTable extends IAttributeTableList{
	public String getTableName();
	public boolean isPrimaryKeySet();
	public IMetaField getPrimaryKey();
	public String getPartitionFieldName();
	public IMetaField getPartitionField();
	public IMetaField getNullField();
	public IMetaField getCountField();
    public IMetaField getRownumField();
	public int numberOfColumns();
	public boolean usePartitionField();
	public String getAlias();
	public List<IMetaField> getRequiredInsertFieldList();
	public List<IMetaField> getAllColumns();
	public IMetaField getUidSequenceColumn();	
}
