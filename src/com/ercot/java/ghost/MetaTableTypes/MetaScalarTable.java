package com.ercot.java.ghost.MetaTableTypes;

import com.ercot.java.ghost.MetaTableTypes.Tables.AbstractMetaTable;

public class MetaScalarTable extends AbstractMetaTable implements IMetaScalarTable {

	private IMetaField _valueColumn;
	
	
	public MetaScalarTable(IMetaTable table, IMetaField valueColumn) {
		super.setDataToTable(table);
		_valueColumn = valueColumn;
	}
	
	public MetaScalarTable(IMetaTable table, IMetaField valueColumn, String tableAlias) {
		this(table,valueColumn);
        super.setTableAliasforFields(tableAlias);
    }
	
	@Override
	public IMetaField getIMGVTValueColumn() {
		return _valueColumn;
	}
	
	protected IMetaField getValueColumn(){
		return _valueColumn;
	}

}
