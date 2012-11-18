package com.java.ghost.GhostAttributes;

import java.util.ArrayList;
import java.util.List;

import com.java.ghost.GhostFieldMaps.GhostFieldMapObject;
import com.java.ghost.GhostFieldMaps.GhostFieldMapObject.GhostFieldMapObjectTypes;
import com.java.ghost.MetaTableTypes.IMetaField;
import com.java.ghost.MetaTableTypes.Tables.MetaTables;
import com.java.ghost.utils.GhostDBStaticVariables;

public class GhostPossessMappingList {
		
	private List<GhostFieldMapObject> _fieldList = new ArrayList<GhostFieldMapObject>();
	private int _currentPosition = 0;
	
	public GhostPossessMappingList(){
	}
	
	public GhostPossessMappingList(IMetaField... fields){
		for(IMetaField imf : fields){
			this.add(imf);
		}
	}
	
	public GhostPossessMappingList(String prefix, IGhostCustomAttribute... fields){
		this.add(prefix);
		for(IGhostCustomAttribute imf : fields){
			this.add(imf);
		}
	}
	
	public GhostPossessMappingList(String prefix, IMetaField... fields){
		this.add(prefix);
		for(IMetaField imf : fields){
			this.add(imf);
		}
	}
	
	
	public GhostPossessMappingList(String... fields){
		for(String imf : fields){
			this.add(imf);
		}
	}
	
	public GhostPossessMappingList(String prefix, String... fields){
		this.add(prefix);
		for(String imf : fields){
			this.add(imf);
		}
	}
	
	
	public GhostPossessMappingList(Number... fields){
		for(Number imf : fields){
			this.add(imf);
		}
	}
	
	public GhostPossessMappingList(String prefix, Number... fields){
		this.add(prefix);
		for(Number imf : fields){
			this.add(imf);
		}
	}
	
	public void add(IGhostCustomAttribute field){
		_fieldList.add(_currentPosition,new GhostFieldMapObject(MetaTables.GHOST_VM.getCustomAttributeField(field)));
		_currentPosition++;    
    }
	
	public void add(IMetaField field){
			_fieldList.add(_currentPosition,new GhostFieldMapObject(field));
			_currentPosition++;    
	}
	
	public void add(String str){
			_fieldList.add(_currentPosition,new GhostFieldMapObject(GhostDBStaticVariables.wrapString(str)) );
			_currentPosition++;
	}
	
	public void add(Number number){
		_fieldList.add(_currentPosition,new GhostFieldMapObject(number) );
		_currentPosition++;
   }
	
	public String getStringForm(String fieldToken, String concatOperator){
		StringBuilder result =  new StringBuilder();
		GhostFieldMapObjectTypes previousType = null;
		String imfToken = GhostDBStaticVariables.EMPTY_STR;		
		String modifiedFieldToken = GhostDBStaticVariables.EMPTY_STR;
		
		if(fieldToken == null){
			modifiedFieldToken = GhostDBStaticVariables.EMPTY_STR;
		}else{
			modifiedFieldToken = fieldToken + concatOperator;
		}
		
		for(GhostFieldMapObject gfmo : _fieldList){
			if(gfmo.getType().equals(GhostFieldMapObjectTypes.String)){
				
				result.append((String) gfmo.getValue() + concatOperator);
				previousType = GhostFieldMapObjectTypes.String;
			}else if(gfmo.getType().equals(GhostFieldMapObjectTypes.Number)){
				
				result.append((Number) gfmo.getValue() + concatOperator);
				previousType = GhostFieldMapObjectTypes.Number;
			}
			else if( (gfmo.getType().equals(GhostFieldMapObjectTypes.IMetaField)) ||
					  (gfmo.getType().equals(GhostFieldMapObjectTypes.IMetaFieldConstant)) ){
				
				//Set the extra separator token only if between two IMetaFields
				if(previousType == GhostFieldMapObjectTypes.IMetaField){
					imfToken = modifiedFieldToken;
//					imfTailToken = concatOperator;
				}else{
					imfToken = GhostDBStaticVariables.EMPTY_STR;
//					imfTailToken = concatOperator; 
				}
				
				IMetaField imf = ((IMetaField)gfmo.getValue());
				if(!imf.isFunction()){
	                result.append(imfToken + imf.getAssociatedTableAlias() + GhostDBStaticVariables.PERIOD + imf.getColumnName() + GhostDBStaticVariables.SPACE + concatOperator);
			    }else{
						 result.append(imfToken + imf.getColumnName() + GhostDBStaticVariables.SPACE + concatOperator); 
					 }
				previousType = GhostFieldMapObjectTypes.IMetaField;
				
			}
				
		}
		
		return result.substring(0, result.length()-concatOperator.length());//TODO: WTF are you doing
		
	}
	
	
}
