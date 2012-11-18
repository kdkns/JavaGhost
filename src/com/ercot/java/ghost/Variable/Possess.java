package com.ercot.java.ghost.Variable;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import oracle.sql.NUMBER;

import org.apache.log4j.Logger;

import com.ercot.java.ghost.DBStatementExecutor.DBStatementExecutor;
import com.ercot.java.ghost.Exceptions.GhostRuntimeException;
import com.ercot.java.ghost.GhostAttributes.GhostPossessMapping;
import com.ercot.java.ghost.MetaTableTypes.IMetaGhostVariableTable;
import com.ercot.java.ghost.MetaTableTypes.IMetaTable;
import com.ercot.java.ghost.QueryConstructors.GhostTableFilter;
import com.ercot.java.ghost.utils.GhostDBStaticVariables;

public class Possess extends DBStatementExecutor{
	private static Logger logger = Logger.getLogger("Possess");
		
	private static final PossessWrapper possessWrapper = new PossessWrapper();
	
	
	private static final IPossessStatement normalPossessStatement = new PossessStatement(){
		public <tableType extends IMetaGhostVariableTable> void bulkAddObjects(IGhostCollection<?> ghostCollection,	NUMBER possessionId, tableType mTable,Class<? extends IGhostVariable<?, ?>> objectClass){
				ghostCollection.bulkAdd(possessionId,mTable, objectClass);
		}		
	};
	
	private static final IPossessStatement bulkOnlyPossessStatement = new PossessStatement(){
		public void setReturnResult(Object obj){
			try {
				this._returnResult = GhostDBStaticVariables.DB_UIDVM_JAVA_TYPE.cast(obj).bigDecimalValue();
			} catch (SQLException e) {
				logger.error(e.getMessage(),e);
				throw new GhostRuntimeException(e);
			}
		}
	};
	
	public final <tableType extends IMetaGhostVariableTable> void possess(IGhostCollection<?> ghostCollection, tableType mTable, List<IMetaTable> secondaryTables, GhostTableFilter filter, GhostPossessMapping gpm) {
		if(ghostCollection.isIterable()){
			possessWrapper.execute(normalPossessStatement, PossessWrapper.DbFunctionTypes.possess, ghostCollection, ghostCollection.getClassGenericObjectType(), mTable, secondaryTables, filter, gpm);
		}else{
			BigDecimal size = (BigDecimal) possessWrapper.execute(bulkOnlyPossessStatement, PossessWrapper.DbFunctionTypes.possessBulkOnly, ghostCollection, ghostCollection.getClassGenericObjectType(), mTable, secondaryTables, filter, gpm);
			ghostCollection.setSize(size);
		}
	}
	
	public final  <tableType extends IMetaGhostVariableTable> void possess(IGhostCollection<?> mbCollection, tableType mTable, GhostTableFilter filter) {	
		possess(mbCollection,mTable, null, filter,null);
	}
	
	public final  <tableType extends IMetaGhostVariableTable> void possess(IGhostCollection<?> mbCollection, tableType mTable, GhostTableFilter filter, GhostPossessMapping gpm) {
		possess(mbCollection,mTable, null, filter,gpm);
	}
	
	public final  <tableType extends IMetaGhostVariableTable> void possess(IGhostCollection<?> mbCollection, tableType mTable, List<IMetaTable> secondaryTables, GhostTableFilter filter) {
        possess(mbCollection,mTable, secondaryTables, filter,null);
	}
                
        
//	public final <tableType extends IMetaGhostVariableTable> void possessIterable(IGhostCollection<?> ghostCollection, tableType mTable, List<IMetaTable> secondaryTables, GhostTableFilter filter, GhostPossessMapping gpm){
//		possessWrapper.execute(normalPossessStatement, PossessWrapper.DbFunctionTypes.possess, ghostCollection, ghostCollection.getClassGenericObjectType(), mTable, secondaryTables, filter, gpm);
//	}
//	
//	public final  <tableType extends IMetaGhostVariableTable> void possessIterable(IGhostCollection<?> mbCollection, tableType mTable, GhostTableFilter filter) {
//		possessIterable(mbCollection,mTable, null, filter,null);
//	}
//	
//	public final  <tableType extends IMetaGhostVariableTable> void possessIterable(IGhostCollection<?> mbCollection, tableType mTable, GhostTableFilter filter, GhostPossessMapping gpm) {
//		possessIterable(mbCollection,mTable, null, filter,gpm);
//	}
//	
//	public final  <tableType extends IMetaGhostVariableTable> void possessIterable(IGhostCollection<?> mbCollection, tableType mTable, List<IMetaTable> secondaryTables, GhostTableFilter filter) {
//		possessIterable(mbCollection,mTable, secondaryTables, filter,null);
//	
//	}
		
}
