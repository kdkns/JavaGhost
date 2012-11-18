package com.java.ghost.GhostFieldMaps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.java.ghost.MetaTableTypes.IMetaField;
import com.java.ghost.MetaTableTypes.Tables.MetaFieldMultiField;
import com.java.ghost.utils.GhostDBStaticVariables;
import com.java.ghost.utils.GhostPair;

public abstract class AbstractGhostFieldMap {
//	private static Logger logger = Logger.getLogger("GhostFieldMap");
	private HashMap<Integer,GhostPair<IMetaField,GhostFieldMapObject>> _fieldMap = new HashMap<Integer,GhostPair<IMetaField,GhostFieldMapObject>>();
		
	protected final Map<Integer,GhostPair<IMetaField,GhostFieldMapObject>> getFieldMap(){
		return _fieldMap;
	}
	
	protected final void setFieldMap(Map<Integer, GhostPair<IMetaField, GhostFieldMapObject>> map) {
		_fieldMap = (HashMap<Integer, GhostPair<IMetaField, GhostFieldMapObject>>) map;
	}
	
	public final GhostPair<IMetaField,GhostFieldMapObject> get(int pos){
		return _fieldMap.get(pos);
	}
	
	public final GhostFieldMapObject getData(int pos){
		return (_fieldMap.get(pos)).getRight();
	}
	
	public IMetaField getField(int pos){
		return (_fieldMap.get(pos)).getLeft();
	}

	public final GhostPair<IMetaField,GhostFieldMapObject> remove(int pos){
		return _fieldMap.remove(pos);
	}
	
	public void clear(){
		_fieldMap.clear();
	}
	
	public final int size(){
		return _fieldMap.size();
	}

	public final List<IMetaField> getFieldList() {
		List<IMetaField> fieldList = new ArrayList<IMetaField>();
		Set<Integer> s = _fieldMap.keySet();
		IMetaField field = null;
		for( Integer i: s){
			field = (_fieldMap.get(i)).getLeft();
			
			if(field instanceof MetaFieldMultiField){
				for(IMetaField pkField: ((MetaFieldMultiField)field).getFieldsThatConstructField() ){
					fieldList.add(pkField);	
				}
			}else{
				fieldList.add(field);	
			}
			
		}
		return fieldList;
	}
	
	public final Set<Integer> getKeySet(){
		return _fieldMap.keySet();
	}
	
	public final String getConcatValues(String concatOperator){
			 StringBuilder result =  new StringBuilder();
			 int listSize = _fieldMap.size() -1;

			 GhostFieldMapObject bfmo = null;
			 GhostPair<IMetaField,GhostFieldMapObject> gp = null;
			 
			 Set<Integer> s = _fieldMap.keySet();
			 Iterator<Integer> i = s.iterator();

			 if(listSize>=0){
				 for (int x=0; x<= listSize-1;x++){
					 gp = _fieldMap.get(i.next());
					 bfmo = gp.getRight();
					 if(bfmo.getType().equals(GhostFieldMapObject.GhostFieldMapObjectTypes.String)){
						 result.append(GhostDBStaticVariables.SINGLE_QUOTE + bfmo.getValue().toString() + GhostDBStaticVariables.SINGLE_QUOTE + GhostDBStaticVariables.SPACE +  concatOperator);	 
					 }else {
						 result.append(bfmo.getValue().toString() + concatOperator);
					 }
					 
				 }
				 gp = _fieldMap.get(i.next());
				 bfmo = gp.getRight();
				 if(bfmo.getType().equals(GhostFieldMapObject.GhostFieldMapObjectTypes.String)){
					 result.append(GhostDBStaticVariables.SINGLE_QUOTE + bfmo.getValue().toString() + GhostDBStaticVariables.SINGLE_QUOTE + GhostDBStaticVariables.SPACE);	 
				 }else {
					 result.append(bfmo.getValue().toString());
				 }
				 
			 }
			 return result.toString();
	}
	
	public final void addAllToEnd(AbstractGhostFieldMap abstractFieldMap){
		if( (abstractFieldMap!=null) && (abstractFieldMap.size()>0)){			
			int maxPosition = -1;
			int count=1;
			
			if(!getFieldMap().isEmpty()){
				maxPosition = Collections.max(getFieldMap().keySet());
			}
			
			for(Integer i : abstractFieldMap.getKeySet()){
				getFieldMap().put(maxPosition+count, abstractFieldMap.get(i));
				count++;
			}
		}
	}
	
	public final void resortMapWithNoGaps(){		
		if( !getFieldMap().isEmpty()){		
			GhostPair<IMetaField, GhostFieldMapObject> currentObject = null;
			int emptyPosition = 0;
			boolean gapFound = false;
			int maxPosition = Collections.max(getFieldMap().keySet());
			for(int i =0; i<= maxPosition;i++){
				currentObject = getFieldMap().get(i);
				if(currentObject==null){
					emptyPosition = i;
					gapFound = true;
				}else if(gapFound){
				    getFieldMap().put(emptyPosition, currentObject);
				    getFieldMap().remove(i);
				    gapFound = false;
				}				
			}
		}
	}

}
