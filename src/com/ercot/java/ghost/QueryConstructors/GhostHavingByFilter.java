package com.ercot.java.ghost.QueryConstructors;

import com.ercot.java.ghost.MetaTableTypes.IMetaField;
import com.ercot.java.ghost.utils.GhostPair;

public class GhostHavingByFilter extends GhostQueryFilter {
	
	//HavingClause constructor
	public GhostPair<IMetaField, String> addHavingClause(int pos, FilterOperationTypes fot,IMetaField field, java.lang.Number value){
		return _filterSQL.put(pos, new GhostPair<IMetaField, String>(field, field.getColumnName() +  _operationMap.get(fot) +  value) );
	}	

}
