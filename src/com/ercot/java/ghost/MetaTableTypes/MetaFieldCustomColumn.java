package com.ercot.java.ghost.MetaTableTypes;

import com.ercot.java.ghost.utils.GhostDBStaticVariables;

public class MetaFieldCustomColumn extends MetaField implements IMetaFieldCustomColumn{

	public MetaFieldCustomColumn(IMetaTable table, GhostDBStaticVariables.DBTypes dbType, String columnName, int size, int scale, boolean isRequired){
			super(table, dbType, columnName, size, scale, isRequired, columnName, false);
	}
}

