package com.ercot.java.ghost.GhostAttributes;

import com.ercot.java.ghost.utils.GhostDBStaticVariables;

public enum GhostCustomAttributeEnum implements IGhostCustomAttribute {
	
	custom_1(GhostDBStaticVariables.DBTypes.VARCHAR),
	custom_2(GhostDBStaticVariables.DBTypes.VARCHAR),
	custom_3(GhostDBStaticVariables.DBTypes.VARCHAR),
	custom_4(GhostDBStaticVariables.DBTypes.VARCHAR),
	custom_5(GhostDBStaticVariables.DBTypes.VARCHAR),
	custom_6(GhostDBStaticVariables.DBTypes.VARCHAR),
	custom_7(GhostDBStaticVariables.DBTypes.VARCHAR),
	custom_8(GhostDBStaticVariables.DBTypes.VARCHAR),
	custom_9(GhostDBStaticVariables.DBTypes.VARCHAR),
	custom_10(GhostDBStaticVariables.DBTypes.VARCHAR),
	custom_11(GhostDBStaticVariables.DBTypes.VARCHAR),
	custom_12(GhostDBStaticVariables.DBTypes.VARCHAR),
	custom_13(GhostDBStaticVariables.DBTypes.VARCHAR),
	custom_14(GhostDBStaticVariables.DBTypes.VARCHAR),
	custom_15(GhostDBStaticVariables.DBTypes.VARCHAR),
	custom_16(GhostDBStaticVariables.DBTypes.VARCHAR),
	custom_17(GhostDBStaticVariables.DBTypes.VARCHAR),
	custom_18(GhostDBStaticVariables.DBTypes.VARCHAR),
	custom_19(GhostDBStaticVariables.DBTypes.VARCHAR),
	custom_20(GhostDBStaticVariables.DBTypes.VARCHAR),
	custom_date_1(GhostDBStaticVariables.DBTypes.DATE),
	custom_date_2(GhostDBStaticVariables.DBTypes.DATE),
	custom_date_3(GhostDBStaticVariables.DBTypes.DATE),
	custom_date_4(GhostDBStaticVariables.DBTypes.DATE),
	custom_date_5(GhostDBStaticVariables.DBTypes.DATE),
	custom_date_6(GhostDBStaticVariables.DBTypes.DATE),
	custom_date_7(GhostDBStaticVariables.DBTypes.DATE),
	custom_date_8(GhostDBStaticVariables.DBTypes.DATE),
	custom_date_9(GhostDBStaticVariables.DBTypes.DATE),
	custom_date_10(GhostDBStaticVariables.DBTypes.DATE);
	
	private GhostDBStaticVariables.DBTypes _dbType;
	
	private GhostCustomAttributeEnum(GhostDBStaticVariables.DBTypes dbType){
		setDBType(dbType);
	}
	
	private void setDBType(GhostDBStaticVariables.DBTypes dbType){
		_dbType = dbType;
	}
	
	public GhostDBStaticVariables.DBTypes getDBType(){
		return _dbType;
	}
	
}
