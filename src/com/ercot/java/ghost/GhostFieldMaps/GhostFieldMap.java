package com.ercot.java.ghost.GhostFieldMaps;

import java.util.Map;

import com.ercot.java.ghost.GhostFieldMaps.GhostFieldMapObject.GhostFieldMapObjectTypes;
import com.ercot.java.ghost.MetaTableTypes.IMetaField;
import com.ercot.java.ghost.utils.GhostDBStaticVariables;
import com.ercot.java.ghost.utils.GhostPair;
import com.ercot.java.ghost.utils.IGDate;

public class GhostFieldMap extends AbstractGhostFieldMap {
	
	public GhostPair<IMetaField,GhostFieldMapObject> put(int pos, IMetaField column, IMetaField valueColumn){
		return getFieldMap().put(pos, new GhostPair<IMetaField,GhostFieldMapObject>(column, new GhostFieldMapObject(valueColumn)));
	}
	
	public GhostPair<IMetaField,GhostFieldMapObject> put(int pos, IMetaField column, java.lang.Number valueColumn){
		return getFieldMap().put(pos, new GhostPair<IMetaField,GhostFieldMapObject>(column, new GhostFieldMapObject(valueColumn)));
	}
	
	public GhostPair<IMetaField,GhostFieldMapObject> put(int pos, IMetaField column, String valueColumn){
		return getFieldMap().put(pos, new GhostPair<IMetaField,GhostFieldMapObject>(column, new GhostFieldMapObject(valueColumn)));
	}
	
	public GhostPair<IMetaField,GhostFieldMapObject> put(int pos, IMetaField column, IGDate valueColumn){
		return getFieldMap().put(pos, new GhostPair<IMetaField,GhostFieldMapObject>(column, new GhostFieldMapObject(GhostDBStaticVariables.wrapDate(valueColumn), GhostFieldMapObjectTypes.Date)));
	}
	
	public GhostPair<IMetaField,GhostFieldMapObject> put(int pos, IMetaField column, GhostFieldMapObject o){
		return getFieldMap().put(pos, new GhostPair<IMetaField,GhostFieldMapObject>(column, o));
	}
	
	public void putAll(Map<? extends Integer, ? extends GhostPair<IMetaField, GhostFieldMapObject>> fieldMap){
		getFieldMap().putAll(fieldMap);
	}

	public void addAll(GhostFieldMap gfm) {
		getFieldMap().putAll(gfm.getFieldMap());		
	}
}
