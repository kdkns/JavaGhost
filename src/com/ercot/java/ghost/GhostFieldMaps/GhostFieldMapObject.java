package com.ercot.java.ghost.GhostFieldMaps;

import com.ercot.java.ghost.Exceptions.GhostFieldObjectTypeException;
import com.ercot.java.ghost.MetaTableTypes.IMetaField;
import com.ercot.java.ghost.MetaTableTypes.IMetaFieldConstant;
import com.ercot.java.ghost.MetaTableTypes.IMetaFieldCustomColumn;
import com.ercot.java.ghost.MetaTableTypes.IMetaFieldMultiField;
import com.ercot.java.ghost.utils.IDBFunctionValue;
import com.ercot.java.ghost.utils.IGDate;

public class GhostFieldMapObject {
	protected Object _value;
	protected GhostFieldMapObjectTypes _type;
	
	public static enum GhostFieldMapObjectTypes{
		Number,String,Date,IMetaField, IMetaFieldConstant, IMetaFieldCustomColumn, IMetaFieldMultiField
	}
	
	protected GhostFieldMapObject(){};
	
	private void setValueObject(Object o){
		if(o == null ){
			_value = null;
		}else{			
			if(o instanceof java.lang.Number){
				_type = GhostFieldMapObjectTypes.Number;
			}else if( (o instanceof String) || (o instanceof IDBFunctionValue)){
				_type = GhostFieldMapObjectTypes.String;
			}else if(o instanceof IGDate){
				_type = GhostFieldMapObjectTypes.Date;
			}else if(o instanceof IMetaFieldMultiField){
				_type = GhostFieldMapObjectTypes.IMetaFieldMultiField;
			}else if(o instanceof IMetaFieldCustomColumn){
				_type = GhostFieldMapObjectTypes.IMetaFieldCustomColumn;
			}else if(o instanceof IMetaFieldConstant){
				_type = GhostFieldMapObjectTypes.IMetaFieldConstant;
			}else if(o instanceof IMetaField){
				_type = GhostFieldMapObjectTypes.IMetaField;
			}else{
				throw new GhostFieldObjectTypeException();
			}
			_value = o;
		}
	}
	
	public GhostFieldMapObject(Object o) {
		setValueObject(o);
	}
	
	public GhostFieldMapObject(Object o, GhostFieldMapObjectTypes date) {
		_type = GhostFieldMapObjectTypes.Date;
		_value = o;
	}

	public Object getValue(){
		return _value;
	}
	
	public GhostFieldMapObjectTypes getType(){
		return _type;
	}
	
	public String toString(){
		return _value.toString();
	}

	public void setValue(Object value){
		setValueObject(value);
	}

}
