package com.ercot.java.ghost.MetaTableTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ercot.java.ghost.MetaTableTypes.Tables.AbstractMetaTable;
import com.ercot.java.ghost.MetaTableTypes.Tables.MetaFieldConstant;
import com.ercot.java.ghost.QueryConstructors.GhostQuery;
import com.ercot.java.ghost.QueryConstructors.GhostTableFilter;
import com.ercot.java.ghost.utils.GhostDBStaticVariables;

public class GhostQueryTable extends AbstractMetaTable{
	private GhostQuery _gq;
	private HashMap<IMetaField,IMetaField> _aliasMap = new HashMap<IMetaField,IMetaField>();
	
	public GhostQueryTable(GhostQuery gq, String tableAlias) {
		super(tableAlias.toUpperCase(),tableAlias, gq.getFieldList().size());
		_gq = gq;
		setFieldList(gq.getFieldList());
		setTableAliasforFields(tableAlias);
	}
	
	public IMetaField getField(IMetaField mf){
//		return super.getFieldList().get(super.getFieldList().indexOf(mf));
		return _aliasMap.get(mf);
	}
	
	public String getTableName() {
		return GhostDBStaticVariables.OPEN_PARENTHESES+_gq.getQuery()+GhostDBStaticVariables.CLOSE_PARENTHESES;
	}
	
	public GhostQuery getGhostQuery() {
		return _gq;
	}
	
	public GhostTableFilter getTableFilter() {
		return _gq.getTableFilter();
	}
	
	public void setFieldList(List<IMetaField> fieldList){
//		(_table,_aliasName,GhostDbStaticVariables.GHOST_DB_DBTYPE_DATE,"METABLOB_STARTTIME",7,false)
		List<MetaField> l = new ArrayList<MetaField>();
		for(IMetaField i: fieldList){
			if(i instanceof MetaFieldConstant){
				MetaFieldConstant m = new MetaFieldConstant(i.getAssociatedTable(), i.getType(), i.getAlias(), i.getSize(), i.getScale(), false, i.getAlias());
				_aliasMap.put(i,m);
				l.add(m);
			}else{
				MetaField m = new MetaField(i.getAssociatedTable(), i.getType(), i.getAlias(), i.getSize(), i.getScale(), false,i.getAlias());
				_aliasMap.put(i,m);
				l.add(m);
			}
		    
		}
		addAllToFieldList(l);
	}
	
	protected void setTableAliasforFields(String tableAlias) {
		for(IMetaField i : getFieldList()){
			i.setTableAlias(tableAlias);
			i.setAlias(tableAlias+i.getAlias());
		}
	}	
}
