package com.java.ghost.GhostFieldMaps;

import java.util.HashMap;
import java.util.Map;

import com.java.ghost.GhostFieldMaps.GhostFieldMapObject.GhostFieldMapObjectTypes;
import com.java.ghost.MetaTableTypes.IMetaField;
import com.java.ghost.utils.GhostDBStaticVariables;
import com.java.ghost.utils.GhostPair;
import com.java.ghost.utils.IGDate;

public class GhostBindFieldMap extends AbstractGhostFieldMap {

	public GhostPair<IMetaField,GhostFieldMapObject> set(int pos, IMetaField column, java.lang.Number valueColumn){
		return getFieldMap().put(pos, new GhostPair<IMetaField,GhostFieldMapObject>(column, new GhostFieldMapObject(valueColumn)));
	}
	
	public GhostPair<IMetaField,GhostFieldMapObject> set(int pos, IMetaField column, String valueColumn){
		return getFieldMap().put(pos, new GhostPair<IMetaField,GhostFieldMapObject>(column, new GhostFieldMapObject(valueColumn)));
	}
	
	public GhostPair<IMetaField,GhostFieldMapObject> set(int pos, IMetaField column, IGDate valueColumn){
		return getFieldMap().put(pos, new GhostPair<IMetaField,GhostFieldMapObject>(column, new GhostFieldMapObject(GhostDBStaticVariables.wrapBindDate(valueColumn), GhostFieldMapObjectTypes.Date)));
	}
	
	public GhostPair<IMetaField,GhostFieldMapObject> set(int pos, IMetaField column, GhostFieldMapObject o){
		return getFieldMap().put(pos, new GhostPair<IMetaField,GhostFieldMapObject>(column, o));
	}

	public void mergeBeforeMap(GhostBindFieldMap gbfm){
		 // Shift all the map bindings before criteria bindings
		 Map<Integer, GhostPair<IMetaField, GhostFieldMapObject>>  tmpMap = new HashMap<Integer, GhostPair<IMetaField, GhostFieldMapObject>>();
		 tmpMap.putAll(gbfm.getFieldMap());
		 int sizeOfMap = (Integer) tmpMap.keySet().toArray()[tmpMap.keySet().size()-1]+1;
		 for(Integer i: getFieldMap().keySet()){
			 tmpMap.put(sizeOfMap + i, getFieldMap().get(i));
		 }
		 setFieldMap(tmpMap);
	}

	public void updateValue(int i, Object value)  {
		GhostPair<IMetaField, GhostFieldMapObject> gp = getFieldMap().get(i);
		GhostFieldMapObject gfmo = gp.getRight();
		gfmo.setValue(value);
//		gp.setRight(gfmo);
		
//		getFieldMap().put(i, gp);		
	}

	public void mergeAfterMap(GhostBindFieldMap gbfm){
		 // Shift all the map bindings after current criteria bindings
		 Map<Integer, GhostPair<IMetaField, GhostFieldMapObject>>  tmpMap = new HashMap<Integer, GhostPair<IMetaField, GhostFieldMapObject>>();
		 
		 tmpMap.putAll(getFieldMap());
		 
		 int sizeOfMap = (Integer) tmpMap.keySet().toArray()[tmpMap.keySet().size()-1]+1;		 
		 for(Integer i: gbfm.getFieldMap().keySet()){
			 tmpMap.put(sizeOfMap + i, gbfm.getFieldMap().get(i));
		 }
		 
		 setFieldMap(tmpMap);
	}

	public boolean isEmpty() {
		if(getFieldMap()==null){
			return true;
		}
		return getFieldMap().size()>0?false:true;
	    }
}
