package com.ercot.java.ghost.GhostFieldMaps;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.ercot.java.ghost.Exceptions.GhostRuntimeException;
import com.ercot.java.ghost.GhostAttributes.GhostAttributeEnum;
import com.ercot.java.ghost.GhostAttributes.IGhostCustomAttribute;
import com.ercot.java.ghost.GhostFieldMaps.GhostFieldMapObject.GhostFieldMapObjectTypes;
import com.ercot.java.ghost.MetaTableTypes.IMetaField;
import com.ercot.java.ghost.MetaTableTypes.IMetaFieldMultiField;
import com.ercot.java.ghost.MetaTableTypes.IMetaTable;
import com.ercot.java.ghost.MetaTableTypes.Tables.MetaFieldConstant;
import com.ercot.java.ghost.MetaTableTypes.Tables.MetaTables;
import com.ercot.java.ghost.utils.GhostDBStaticVariables;
import com.ercot.java.ghost.utils.GhostVariableWrapper;
import com.ercot.java.ghost.utils.IGDate;

public class GhostHeaderMapping {
	
	private final Map<GhostAttributeEnum, GhostFieldMapObject> _attributeMap = new HashMap<GhostAttributeEnum,GhostFieldMapObject>();
	
	private Map<GhostAttributeEnum, GhostFieldMapObject> getAttributeMap(){
		return _attributeMap;
	}
	
	public void addMappingToColumn(GhostAttributeEnum attribute, IGDate date) {		
		getAttributeMap().put(attribute, new GhostFieldMapObject(date));
	}
	
	public void addMappingToColumn(GhostAttributeEnum attribute, Number number) {		
		getAttributeMap().put(attribute, new GhostFieldMapObject(number));
	}
	
	public void addMappingToColumn(GhostAttributeEnum attribute, String str) {		
		getAttributeMap().put(attribute, new GhostFieldMapObject(str));
	}
	
	public void addMappingToColumn(GhostAttributeEnum attribute, IMetaField iMetaField) {		
		getAttributeMap().put(attribute, new GhostFieldMapObject(iMetaField));
	}
	
	public void addMappingToColumn(GhostAttributeEnum attribute, IGhostCustomAttribute gcae) {		
		getAttributeMap().put(attribute, new GhostFieldMapObject(MetaTables.GHOST_VM.getCustomAttributeField(gcae)));
	}
	
	public void addMappingToColumn(IMetaField sequenceColumn) {
		getAttributeMap().put(GhostAttributeEnum.primaryKey, new GhostFieldMapObject(sequenceColumn));
	}
		
	public GhostFieldMapObject remove(GhostAttributeEnum attribute){
		return getAttributeMap().remove(attribute);
	}
	
	public void clear(){
		getAttributeMap().clear();
	}
	
	public void addAll(GhostHeaderMapping gfm) {
		getAttributeMap().putAll(gfm.getAttributeMap());		
	}

	public GhostFieldMap getGhostFieldMap(IMetaTable table){
		GhostFieldMap headerFieldMap = new GhostFieldMap();
		int x =0;
		for( GhostAttributeEnum attribute : getAttributeMap().keySet()){
			headerFieldMap.put(x, table.getAttributeField(attribute), getAttributeMap().get(attribute));
			x++;
		}
		
		return headerFieldMap;
	}

	public Set<? extends IMetaField> getFieldSet() {
		Set<IMetaField> fields = new TreeSet<IMetaField>();
		for(GhostAttributeEnum gae: getAttributeMap().keySet()){
			GhostFieldMapObject gfmo = getAttributeMap().get(gae);
			if((gfmo.getType() == GhostFieldMapObjectTypes.IMetaField) ||(gfmo.getType() == GhostFieldMapObjectTypes.IMetaFieldCustomColumn) ){
				fields.add((IMetaField)gfmo.getValue());
			}
			else if(gfmo.getType() == GhostFieldMapObjectTypes.IMetaFieldMultiField){
				fields.addAll(((IMetaFieldMultiField)gfmo.getValue()).getFieldsThatConstructField());
			}
		}
		return fields;
	}
	
	
	public IMetaField getMappedCustomAttributeForAttribute(GhostAttributeEnum attribute){
        GhostFieldMapObject gfmo = getAttributeMap().get(attribute);
        switch (gfmo.getType()){
		    case IMetaFieldMultiField: return (IMetaField) gfmo.getValue();
	        case IMetaFieldCustomColumn: return (IMetaField) gfmo.getValue();
	        case IMetaFieldConstant: return (IMetaField) gfmo.getValue();
	        case IMetaField: return (IMetaField) gfmo.getValue();
	        // Number & String have time added in case someone maps a hard-coded constant more then once like the number 1. The string would be the same without time?
	        case Number : return new MetaFieldConstant(null,GhostDBStaticVariables.DBTypes.CHAR,gfmo.getValue().toString(),0,0, false, GhostDBStaticVariables.generateUniqueString(gfmo.getValue().toString()),false);
	        case String : return new MetaFieldConstant(null,GhostDBStaticVariables.DBTypes.CHAR,gfmo.getValue().toString(),0,0, false, GhostDBStaticVariables.generateUniqueString(gfmo.getValue().toString()),false);
	        case Date :   return new MetaFieldConstant(null,GhostDBStaticVariables.DBTypes.DATE,gfmo.getValue().toString(),0,0, false, GhostDBStaticVariables.generateUniqueString(gfmo.getValue().toString()),false);
	        default: throw new GhostRuntimeException("Mapped column in GhostHeaderMapping object is of unknown type to be converted to a IMetaField! " + GhostVariableWrapper.wrapVariable(gfmo.getType()));
        }
  }
	

	
//	public String getSQLColumns() {
//		return getSQLColumns(null);
//	}
	
//	public String getSQLColumns() {
//		 String result= GhostDbStaticVariables.EMPTRY_STR;
//		 
//		 
//		 int listSize = _attributeMap.size() -1;
//		 TreeSet<GhostAttributeEnum> keyset = new TreeSet<GhostAttributeEnum>();
//		 keyset.addAll(_attributeMap.keySet());
//		 if(listSize>=0){
//			 GhostFieldMapObject gp = null;
//			 String tokenString = GhostDbStaticVariables.EMPTRY_STR;
//			 
//			 for(GhostAttributeEnum matr: keyset){
//				    gp = _attributeMap.get(matr);
//				    tokenString = GhostDbStaticVariables.STRINGCONCAT;
//					 
//					if(gp.getLeft() != null){
//					 tokenString += GhostDbStaticVariables.wrapString(gp.getLeft()) + GhostDbStaticVariables.STRINGCONCAT;
//					}
//					result+= GhostDbStaticVariables.COMMA + ((GhostPossessMappingList)gp.getRight()).getStringForm(GhostDbStaticVariables.wrapString(gp.getLeft()), GhostDbStaticVariables.STRINGCONCAT);
//				}
//		 }
//		 return result;
//	}
//	
//
//	public String getColumnOrderString(){
//		
//		    String result= GhostDbStaticVariables.EMPTRY_STR;
//		    TreeSet<GhostAttributeEnum> keyset = new TreeSet<GhostAttributeEnum>();
//			keyset.addAll(_attributeMap.keySet());
//
//			for(GhostAttributeEnum matr: keyset){
//				result+= GhostDbStaticVariables.COMMA + MetaTables.GHOST_VM.getCustomAttributeField(matr).getColumnName();
//			}
//			return result;
//	}
	
	
}
