package com.ercot.java.ghost.GhostAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import com.ercot.java.ghost.MetaTableTypes.IMetaField;
import com.ercot.java.ghost.MetaTableTypes.Tables.MetaTables;
import com.ercot.java.ghost.utils.GhostDBStaticVariables;
import com.ercot.java.ghost.utils.GhostPair;

public class GhostPossessMapping {

	private final Map<IGhostCustomAttribute, GhostPair<String,GhostPossessMappingList>> _attributeMap = new HashMap<IGhostCustomAttribute,GhostPair<String,GhostPossessMappingList>>();
	
	public void addMappingToColumn(IGhostCustomAttribute attribute, IMetaField... columns) {		
		_attributeMap.put(attribute, new GhostPair<String,GhostPossessMappingList>(null,new GhostPossessMappingList(columns) ) );
	}
	
	public void addMappingToColumn(IGhostCustomAttribute attribute, String seperator, IMetaField... columns) {		
		_attributeMap.put(attribute, new GhostPair<String,GhostPossessMappingList>(seperator,new GhostPossessMappingList(columns)) );
	}	
		
	public void addMappingToColumn(IGhostCustomAttribute attribute, String value) {
		String[] valueList = { value };
		_attributeMap.put(attribute, new GhostPair<String,GhostPossessMappingList>(null,new GhostPossessMappingList(valueList) ) );
	}
	
	public void addMappingToColumn(IGhostCustomAttribute attribute, String seperator, String value) {
		String[] valueList = { value };
		_attributeMap.put(attribute, new GhostPair<String,GhostPossessMappingList>(seperator,new GhostPossessMappingList(valueList)) );
	}
		
	public void addMappingToColumn(IGhostCustomAttribute attribute, Number... columns) {		
		_attributeMap.put(attribute, new GhostPair<String,GhostPossessMappingList>(null,new GhostPossessMappingList(columns) ) );
	}
	
	public void addMappingToColumn(IGhostCustomAttribute attribute, String seperator, Number... columns) {		
		_attributeMap.put(attribute, new GhostPair<String,GhostPossessMappingList>(seperator,new GhostPossessMappingList(columns)) );
	}
	
	public void addMappingToColumn(IGhostCustomAttribute attribute, GhostPossessMappingList gpml) {		
		_attributeMap.put(attribute, new GhostPair<String,GhostPossessMappingList>(null,gpml) );
	}
	
	public void addMappingToColumn(IGhostCustomAttribute attribute, String separator, GhostPossessMappingList gpml) {		
		_attributeMap.put(attribute, new GhostPair<String,GhostPossessMappingList>(separator,gpml) );
	}
	
	
	public GhostPair<String,GhostPossessMappingList> remove(IGhostCustomAttribute attribute){
		return _attributeMap.remove(attribute);
	}
	
	public void clear(){
		_attributeMap.clear();
	}
	
//	public String getSQLColumns() {
//		return getSQLColumns(null);
//	}
	
	public String getSQLColumns() {
		 StringBuilder result = new StringBuilder();// GhostDbStaticVariables.EMPTRY_STR;
		 
		 
		 int listSize = _attributeMap.size() -1;
		 TreeSet<IGhostCustomAttribute> keyset = new TreeSet<IGhostCustomAttribute>();
		 keyset.addAll(_attributeMap.keySet());
		 if(listSize>=0){
			 GhostPair<String,GhostPossessMappingList> gp = null;
			 @SuppressWarnings("unused")
			 String tokenString = GhostDBStaticVariables.EMPTY_STR;
			 
			 for(IGhostCustomAttribute matr: keyset){
				    gp = _attributeMap.get(matr);
				    tokenString = GhostDBStaticVariables.STRINGCONCAT;
					 
					if(gp.getLeft() != null){
					 tokenString += GhostDBStaticVariables.wrapString(gp.getLeft()) + GhostDBStaticVariables.STRINGCONCAT;
					}
					result.append(GhostDBStaticVariables.COMMA + ((GhostPossessMappingList)gp.getRight()).getStringForm(GhostDBStaticVariables.wrapString(gp.getLeft()), GhostDBStaticVariables.STRINGCONCAT));
				}
		 }
		 return result.toString();
	}
	

	public String getColumnOrderString(){
		
		    StringBuilder result = new StringBuilder();// GhostDbStaticVariables.EMPTRY_STR;
		    TreeSet<IGhostCustomAttribute> keyset = new TreeSet<IGhostCustomAttribute>();
			keyset.addAll(_attributeMap.keySet());

			for(IGhostCustomAttribute matr: keyset){
				result.append(GhostDBStaticVariables.COMMA + MetaTables.GHOST_VM.getCustomAttributeField(matr).getColumnName());
			}
			return result.toString();
	}
	
}
