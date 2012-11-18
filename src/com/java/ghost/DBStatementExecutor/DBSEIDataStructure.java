package com.java.ghost.DBStatementExecutor;

import java.lang.reflect.Method;

import com.java.ghost.DBStatementExecutor.DBStatementExecutor.StatementType;
import com.java.ghost.utils.GhostDBProcedureList;
import com.java.ghost.utils.GhostParameterArray;

@SuppressWarnings("rawtypes") 
public class DBSEIDataStructure {
		
	    private GhostDBProcedureList _gProcList;
	    private java.lang.Enum _operationType;
	    private GhostParameterArray _gParamArray;
	    private Object _returnValue;
	    private StatementType _statementType;
	    private ICoreStatementLogic _iCoreStatementLogic;
	    
	    
	    
	    private String _query;
	    private Method _setArgumentMethod;
	    boolean _isUpdate;
	    private Class _returnClass;
	    private Method _returnMethod;
	    
	    
	    
		
		
		protected DBSEIDataStructure(StatementType statementType,
				                  GhostDBProcedureList gProcList,
				                  java.lang.Enum operationType,
				                  GhostParameterArray gParamArray,
				                  String query,
				                  Method setArgumentMethod,
				                  boolean isUpdate,
				                  Class returnClass,
				                  Method returnMethod) {
			super();
			_gProcList = gProcList;
			_operationType = operationType;
			_gParamArray = gParamArray;
			_statementType= statementType;
			_query = query;
			_iCoreStatementLogic = DBStatementExecutor.getICoreStatementLogic(_statementType);
			_setArgumentMethod = setArgumentMethod;
			_isUpdate = isUpdate;
			_returnClass = returnClass;
			_returnMethod = returnMethod;
			
		}
		
		protected DBSEIDataStructure(StatementType statementType,
				                  GhostDBProcedureList gProcList,
				                  java.lang.Enum operationType,
				                  GhostParameterArray gParamArray) {
			this(statementType,gProcList,operationType,gParamArray,null,null,false,null,null);
		}
		
		protected DBSEIDataStructure(StatementType statementType,
				                     String query,
				                     GhostParameterArray gParamArray,
				                     Method setArgumentMethod,
					                 boolean isUpdate,
					                 Class returnClass,
					                 Method returnMethod) {
			this(statementType,null,null,gParamArray,query, setArgumentMethod,isUpdate,returnClass,returnMethod);
		}

		protected GhostDBProcedureList getGProcList() {
			return _gProcList;
		}
		
		protected java.lang.Enum getOperationType() {
			return _operationType;
		}
		protected GhostParameterArray getGParamArray() {
			return _gParamArray;
		}
		protected Object getReturnValue() {
			return _returnValue;
		}
		protected void setgProcList(GhostDBProcedureList gProcList) {
			_gProcList = gProcList;
		}
		protected void setOperationType(StatementType operationType) {
			_operationType = operationType;
		}
		protected void setgParamArray(GhostParameterArray gParamArray) {
			_gParamArray = gParamArray;
		}
		protected void setReturnValue(Object returnValue) {
			_returnValue = returnValue;
		}
		protected StatementType getStatementType() {
			return _statementType;
		}
		protected String getQuery() {
			return _query;
		}
		protected ICoreStatementLogic getICoreStatementLogic() {
			return _iCoreStatementLogic;
		}

		public Method getSetArgumentMethod() {
			return _setArgumentMethod;
		}

		public boolean getIsUpdate() {
			return _isUpdate;
		}

		public Class getReturnClass() {
			return _returnClass;
		}

		public Method getReturnMethod() {
			return _returnMethod;
		}

		public void setSetArgumentMethod(Method setArgumentMethod) {
			this._setArgumentMethod = setArgumentMethod;
		}

		public void setUpdate(boolean isUpdate) {
			_isUpdate = isUpdate;
		}

		public void setReturnClass(Class returnClass) {
			_returnClass = returnClass;
		}

		public void setReturnMethod(Method returnMethod) {
			_returnMethod = returnMethod;
		}
	}