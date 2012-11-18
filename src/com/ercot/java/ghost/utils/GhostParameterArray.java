package com.ercot.java.ghost.utils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;

import oracle.jdbc.OraclePreparedStatement;
import oracle.sql.CHAR;
import oracle.sql.NUMBER;

import org.apache.log4j.Logger;

import com.ercot.java.ghost.GhostFieldMaps.GhostBindFieldMap;
import com.ercot.java.ghost.GhostFieldMaps.GhostFieldMapObject;
import com.ercot.java.ghost.GhostFieldMaps.GhostFieldMapObject.GhostFieldMapObjectTypes;
import com.ercot.java.ghost.QueryConstructors.GhostTableFilter;

public class GhostParameterArray {
   private static Logger logger = Logger.getLogger("GhostParameterArray");
   private HashMap<Integer,Object> _objMap = new HashMap<Integer,Object>();
   private HashMap<Integer,GhostFieldMapObjectTypes> _bindMap = new HashMap<Integer,GhostFieldMapObjectTypes>();
	
   public void putParameter(int pos, Object parameter){
	   _objMap.put(pos, parameter);
   }
   
   public Object getParameter(int pos){
	   return _objMap.get(pos);
   }
   
   
   public void setBindParametersToStatement(int position, OraclePreparedStatement os){
		Set<Integer> bindFields = _bindMap.keySet();
		for(Integer i: bindFields){
			try{
				switch(_bindMap.get(i)){
				  case Number: os.setNUMBER(position+i, (NUMBER) _objMap.get(i));break;
				  default: os.setCHAR(position+i, (CHAR) _objMap.get(i));break;
				}
			}catch(SQLException e){
				logger.error(e.getMessage(), e);
			}
		}
   }
   
   public int setBindParameters(int position, GhostTableFilter tableFilter){
        GhostBindFieldMap gbfm = tableFilter.getGhostBindFieldMap();
        Set<Integer> s = gbfm.getKeySet();
        GhostFieldMapObject gfmo = null;
        int lastPos = position;
        for(Integer i : s){
			gfmo = gbfm.get(i).getRight();
			_bindMap.put(i+1,gfmo.getType());
			try{
				switch(gfmo.getType()){
				  case Number: putParameter(position+i, new NUMBER((java.lang.Number)gfmo.getValue()));break;
				  case String: putParameter(position+i, new CHAR((String)gfmo.getValue(), GhostDBStaticVariables.dbCharacterSet));break;
				  case Date: putParameter(position+i, new CHAR((String)gfmo.getValue(), GhostDBStaticVariables.dbCharacterSet));break;
				}
				lastPos++;
			}catch(SQLException e){
				logger.error(e.getMessage(), e);
			}
		}
        return lastPos;
   }
   
   public void setAndBuildBindString(int position, GhostTableFilter tableFilter){
	   StringBuilder result = new StringBuilder();
	   GhostBindFieldMap gbfm = tableFilter.getGhostBindFieldMap();
       Set<Integer> s = gbfm.getKeySet();
       if(s.size()>0){
	       GhostFieldMapObject gfmo = null;
	       for(Integer i : s){
	    	   gfmo = gbfm.get(i).getRight();
				_bindMap.put(i+1,gfmo.getType());
				switch(gfmo.getType()){
				  case Number: result.append(GhostDBStaticVariables.COMMA + (java.lang.Number)gfmo.getValue());break;
				  case String: result.append(GhostDBStaticVariables.COMMA + (String)gfmo.getValue());break;
				  case Date: result.append(GhostDBStaticVariables.COMMA + (String)gfmo.getValue());break;
				}
			}
			try {			
				putParameter(position, new CHAR(result.substring(1), GhostDBStaticVariables.dbCharacterSet));
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
       }
       else{
    	   try {
			putParameter(position, new CHAR("NULL", GhostDBStaticVariables.dbCharacterSet));
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
       }
    }

	public int size() {
		return _objMap.size();
	}
}
