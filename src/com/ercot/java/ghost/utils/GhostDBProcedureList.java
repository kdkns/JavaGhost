package com.ercot.java.ghost.utils;

import java.lang.reflect.Method;
import java.util.HashMap;

@SuppressWarnings("rawtypes")
public class GhostDBProcedureList {
	
//	private static Logger logger = Logger.getLogger("GhostDBProcedureList");
	
	@SuppressWarnings("unused")
	private class ProcTypesAndArguments{
		private String _Statement;
		private boolean _isUpdate = false;
		private Class _classReturnType;
		private Method _setArgumentsMethod;
		private Method _getReturnObject;
		private boolean _isReturnObjectAnObject;
		private boolean _isProcedure;
		
		
		public ProcTypesAndArguments(String Statement, boolean isUpdate, boolean isReturnObjectAnObject, boolean isProcedure, Class classReturnType, Method setArgumentsMethod, Method getReturnObject){
			_Statement = Statement;
			_isUpdate = isUpdate;
			_classReturnType = classReturnType;
			_setArgumentsMethod = setArgumentsMethod;
			_getReturnObject = getReturnObject;
			_isReturnObjectAnObject = isReturnObjectAnObject;
			_isProcedure = isProcedure;
		}
		
		public String getStatement() {
			return _Statement;
		}

		public void setStatement(String Statement) {
			_Statement = Statement;
		}

		public boolean getIsUpdate() {
			return _isUpdate;
		}

		public void setIsUpdate(boolean isUpdate) {
			_isUpdate = isUpdate;
		}

		public Class getClassReturnType() {
			return _classReturnType;
		}

		public void setClassReturnType(Class classReturnType) {
			_classReturnType = classReturnType;
		}
		
		public void setSetArgumentsMethod(Method setArgumentsMethod){
			_setArgumentsMethod = setArgumentsMethod;
		}
		
		public Method getSetArgumentsMethod(){
			return _setArgumentsMethod;
		}

		public Method getReturnObject() {
			return _getReturnObject;
		}
		
		public boolean getIsReturnObjectAnObject(){
			return _isReturnObjectAnObject;
		}
		
		public boolean getIsProcedure() {
			return _isProcedure;
		}

		public void setIsProcedure(boolean isProcedure) {
			_isProcedure = isProcedure;
		}
	}
		
	private HashMap<java.lang.Enum, ProcTypesAndArguments> _hm = new HashMap<java.lang.Enum, ProcTypesAndArguments>();
	
	public GhostDBProcedureList(){}
	
	public void mergeProcList(GhostDBProcedureList gCRUD){
		_hm.putAll(gCRUD._hm); 
	}
	
	public void put(java.lang.Enum operationType, String Statement, boolean isUpdate, boolean isReturnObjectAnObject, boolean isProcedure, Class classReturnType, Method setArgumentsMethod, Method getReturnObject ){
//		logger.debug("operationType:" + GhostVariableWrapper.wrapVariable(operationType) + "Statement:" + GhostVariableWrapper.wrapVariable(Statement));
//		logger.debug("operationType:" + GhostVariableWrapper.wrapVariable(operationType) + "isUpdate:" + GhostVariableWrapper.wrapVariable(isUpdate));
//		logger.debug("operationType:" + GhostVariableWrapper.wrapVariable(operationType) + "isReturnObjectAnObject:" + GhostVariableWrapper.wrapVariable(isReturnObjectAnObject));
//		logger.debug("operationType:" + GhostVariableWrapper.wrapVariable(operationType) + "isProcedure:" + GhostVariableWrapper.wrapVariable(isProcedure));
//		logger.debug("operationType:" + GhostVariableWrapper.wrapVariable(operationType) + "ClassReturnType:" + GhostVariableWrapper.wrapVariable(classReturnType));
//		logger.debug("operationType:" + GhostVariableWrapper.wrapVariable(operationType) + "SetArgumentsMethod:" + GhostVariableWrapper.wrapVariable(setArgumentsMethod));
//		logger.debug("operationType:" + GhostVariableWrapper.wrapVariable(operationType) + "GetReturnObject:" + GhostVariableWrapper.wrapVariable(getReturnObject));		
		_hm.put(operationType, new ProcTypesAndArguments(Statement,isUpdate, isReturnObjectAnObject, isProcedure, classReturnType, setArgumentsMethod, getReturnObject));
	}
	
	public Boolean getIsUpdate(java.lang.Enum operationType){
		return ((ProcTypesAndArguments) _hm.get(operationType)).getIsUpdate();
	}
	
	public String getSQLStatement(java.lang.Enum operationType) {
		return ((ProcTypesAndArguments) _hm.get(operationType)).getStatement();
	}
	
	public Class getClassReturnType(java.lang.Enum operationType) {
		return ((ProcTypesAndArguments) _hm.get(operationType)).getClassReturnType();
	}
	
	public Method getSetArgumentsMethod(java.lang.Enum operationType) {
		return ((ProcTypesAndArguments) _hm.get(operationType)).getSetArgumentsMethod();
	}

	public Method getReturnObject(Enum operationType) {
		return ((ProcTypesAndArguments) _hm.get(operationType)).getReturnObject();
	}

	public boolean isReturnObjectAnObject(Enum operationType) {
		return ((ProcTypesAndArguments) _hm.get(operationType)).getIsReturnObjectAnObject();
	}
	
	public boolean isProcedure(Enum operationType) {
		return ((ProcTypesAndArguments) _hm.get(operationType)).getIsProcedure();
	}

	public void setSQLStatement(Enum operationType, String Statement) {
		ProcTypesAndArguments praa = _hm.get(operationType);
		praa.setStatement(Statement);
	}

}
