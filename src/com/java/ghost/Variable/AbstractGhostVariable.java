package com.java.ghost.Variable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import oracle.sql.NUMBER;

import org.apache.log4j.Logger;

import com.java.ghost.DBObject.DBID;
import com.java.ghost.DBObject.IDBObject;
import com.java.ghost.Exceptions.GhostRuntimeException;
import com.java.ghost.GhostAttributes.GhostAttributeEnum;
import com.java.ghost.GhostAttributes.IGhostCustomAttribute;
import com.java.ghost.GhostFieldMaps.GhostFieldMap;
import com.java.ghost.MetaTableTypes.IMetaChildTable;
import com.java.ghost.MetaTableTypes.IMetaField;
import com.java.ghost.MetaTableTypes.IMetaGhostVariableTable;
import com.java.ghost.MetaTableTypes.Tables.MetaTables;
import com.java.ghost.utils.GhostDBStaticVariables;
import com.java.ghost.utils.IGDate;


@SuppressWarnings("rawtypes")
public abstract class AbstractGhostVariable<objectReturnType,objectTableType extends IMetaGhostVariableTable, objectDbContentType extends IDBObject, sizeReturnType> implements IGhostVariable<objectReturnType,sizeReturnType>{
	private static Logger logger = Logger.getLogger("AbstractGhostVariable");
	
	private objectDbContentType _content;
	private List<String> _blessAdditionalColumns = new ArrayList<String>();
	//private objectTableType _livesAtTable;
	//private IMetaField _livesAtColumn;
//	private IMetaField _livesAtIdColumn;
//	private BigDecimal _livesWithId;
	
	
	protected AbstractGhostVariable(objectDbContentType superContent){
		abstractGhostVariableHelper(superContent);
	}
	
	protected void abstractGhostVariableHelper(objectDbContentType superContent){
		_content = superContent;		
	} 
	
	public void clear() {
        _content.clear();
	}
	
	@SuppressWarnings("unchecked")
	//TODO: This is odd because they are separate classes not linked
	public sizeReturnType size(){return (sizeReturnType) _content.size();}
	
	protected objectDbContentType getContent(){return _content;}

	@SuppressWarnings("unchecked")
	protected void setTable(objectTableType table){_content.setTable(table);}
	
	protected void setLivesAtColumn(IMetaField column){_content.setLivesAtColumn(column);}
	
	protected void setLivesAtIdColumn(IMetaField column){_content.setLivesAtIdColumn(column);}
	
	protected void setLivesWithId(DBID id){_content.setLivesWithId(id);} 
	
//	@SuppressWarnings("unchecked")
//	public void setLivesAtInfo(IMetaGhostVariableTable table,
//			                   IMetaField livesAtColumn,
//			                   BigDecimal livesWithId,
//			                   BigDecimal id,
//			                   boolean isRemote){		
//		_content.setTable(table);
//		_content.setLivesAtColumn(livesAtColumn);		
//		_content.setLivesWithId(id);
//		_content.setKey(id);
//		_content.setIsRemoteTable(isRemote);
//	}
//	
	public boolean isEmpty() {
		return _content.isEmpty();
	}
	
	public NUMBER getDBKey() {
		return _content.getOracleKey();
	}
	
	@SuppressWarnings("unchecked")
	public objectReturnType getValue() {
		return (objectReturnType) _content.getValue();
    }
	
	public BigDecimal getKey() {
		return new BigDecimal(_content.getOracleKey().stringValue());
	}
	
	public IMetaField getVMInsertColumn(){
		return getContent().getVMInsertColumn();
	}
	
	@SuppressWarnings("unchecked")
	public objectTableType getTable(){ return (objectTableType)_content.getTable();}
	
	public IMetaField getLivesAtColumn(){ return _content.getValueColumn();}
	
	public IMetaField getIdColumn(){ return _content.getLivesAtIdColumn();}
	
	public DBID getId(){ return _content.getKey();}
	
	protected void addBlessAdditionalColumns(String str){
		_blessAdditionalColumns.add(str);
	}
	
	public List<String> getBlessAdditionalColumns(){
		return _blessAdditionalColumns;
	}
	
	@SuppressWarnings("unchecked")
	public void setValue(objectReturnType o){
		if(o == null){
			throw new GhostRuntimeException("Object being set is null!");
		}
		_content.setValue(o);	
	}	
	
	public void setCustomAttribute(IGhostCustomAttribute attribute, String value){
		_content.setCustomAttribute(attribute, value);
	}
	
	public void setCustomAttribute(IGhostCustomAttribute attribute, IGDate gDate){
		_content.setCustomAttribute(attribute, gDate);
	}

	public String getCustomAttribute(IGhostCustomAttribute attribute){
		return _content.getCustomAttribute(attribute);
	}
	
	public IGDate getCustomDateAttribute(IGhostCustomAttribute attribute){
		return _content.getCustomDateAttribute(attribute);
	}

	public String getHeaderAttribute(GhostAttributeEnum attribute){
		try{
			IMetaChildTable childTable = (IMetaChildTable) _content.getTable();
			return _content.getHeaderAttribute(childTable, attribute);
		} catch(ClassCastException e){
			logger.error(e.getMessage(),e);			
			throw new GhostRuntimeException("The table that the meta lives at is not a child table that links to a parent table that has header attributes.");
		}	
//		}catch (GhostExceptions e) {
//			logger.error(e.getMessage(),e);
//			throw new GhostRuntimeExceptionMessage("Fatal error: Possible conversion of key id to database number failed.");
//		}
	}

	public String getAttribute(GhostAttributeEnum attribute) {
			return _content.getAttribute(attribute);
	}
	
	public boolean equals(Object obj) {
		if(this == obj){
			return true;
		}
		if(obj!=null && (this.getClass() == obj.getClass())){
			return getContent().equals(((AbstractGhostVariable)obj).getContent());	
		}
		return false;
	}
	
	public void blessColumnMappingToVMTable(int startPosition, GhostFieldMap ghostFieldMap, IMetaGhostVariableTable table){}
	 
	public int blessNumberOfColumnsForMappingToVMTable(){return 0;}
	
	public int hashCode() {
	    return getContent().hashCode();
	  }
	
	public String getAccessSqlQuery(){
		return getAccessSqlQuery(GhostDBStaticVariables.EMPTY_STR);
	}
	
	public String getAccessSqlQuery(String additionalColumns){
		String selectFieldQuery;
		String selectFromQuery;
		String selectWhereQuery;
		
		selectFieldQuery = this.getAccessSqlQuerySelectFieldString() + GhostDBStaticVariables.COMMA +
				           MetaTables.GHOST_VM.getUidvm().getFullyQualifiedTableAliaisWithColumnName() +
				           additionalColumns;
		
		if(this.getTable() != MetaTables.GHOST_VM){
		   selectFromQuery =     GhostDBStaticVariables.SPACE + GhostDBStaticVariables.SELECT_FROM + GhostDBStaticVariables.SPACE + this.getTable().getTableName() + 
				                 GhostDBStaticVariables.SPACE + this.getTable().getAlias() + GhostDBStaticVariables.COMMA +
			                     GhostDBStaticVariables.SPACE + MetaTables.GHOST_VM.getTableName() + GhostDBStaticVariables.SPACE + 
			                     MetaTables.GHOST_VM.getAlias();
		   
		   selectWhereQuery =    GhostDBStaticVariables.SPACE + GhostDBStaticVariables.SELECT_WHERE + GhostDBStaticVariables.SPACE + 
				                 GhostDBStaticVariables.SPACE + MetaTables.GHOST_VM.getAlias() +
			                     GhostDBStaticVariables.PERIOD + MetaTables.GHOST_VM.getGhostPointer() + GhostDBStaticVariables.SPACE +
			                     GhostDBStaticVariables.EQUALS + GhostDBStaticVariables.SPACE + this.getTable().getAlias() + GhostDBStaticVariables.PERIOD + this.getIdColumn() +
			                     GhostDBStaticVariables.SPACE + GhostDBStaticVariables.AND + GhostDBStaticVariables.SPACE +  
				                 MetaTables.GHOST_VM.getUidvm() + GhostDBStaticVariables.EQUALS + this.getKey();
		}else{
			
			selectFromQuery =   GhostDBStaticVariables.SPACE + GhostDBStaticVariables.SELECT_FROM + GhostDBStaticVariables.SPACE + MetaTables.GHOST_VM.getTableName() + GhostDBStaticVariables.SPACE + 
			                    MetaTables.GHOST_VM.getAlias();
		   selectWhereQuery =   GhostDBStaticVariables.SPACE + GhostDBStaticVariables.SELECT_WHERE + GhostDBStaticVariables.SPACE + 
				                MetaTables.GHOST_VM.getGhostPointerTable() + GhostDBStaticVariables.SPACE + GhostDBStaticVariables.ISNULL +
			                    GhostDBStaticVariables.SPACE + GhostDBStaticVariables.AND + GhostDBStaticVariables.SPACE +  
			                    MetaTables.GHOST_VM.getUidvm() + GhostDBStaticVariables.EQUALS + this.getKey();
		}
		
		if(this.getTable().usePartitionField()){
			selectWhereQuery += GhostDBStaticVariables.SPACE + GhostDBStaticVariables.AND + GhostDBStaticVariables.SPACE +
			                     MetaTables.GHOST_VM.getAlias() + GhostDBStaticVariables.PERIOD + MetaTables.GHOST_VM.getGhostPointerPartitionDate() + 
							     GhostDBStaticVariables.SPACE + GhostDBStaticVariables.EQUALS + GhostDBStaticVariables.SPACE + 
							     this.getTable().getAlias()+ GhostDBStaticVariables.PERIOD + this.getTable().getPartitionFieldName();
		}
		
		return  selectFieldQuery + selectFromQuery + selectWhereQuery;
	}

	protected String getAccessSqlQuerySelectFieldString() {
		return GhostDBStaticVariables.SELECT_SELECT + GhostDBStaticVariables.SPACE + this.getLivesAtColumn();
	}
	
	//Return empty list by default
//	@AdditionalPossessColumnsMethod
//	public List<IMetaField> buildIMBTColumnList(IMetaGhostVariableTable table) throws GhostTableOfWrongTypeException{
//		return new ArrayList<IMetaField>();
//	}
	
	public final void setIsEmpty(boolean value){
		getContent().setIsEmpty(value);
	}
	
	public final void setIsRemoteTable(boolean value){
		getContent().setIsRemoteTable(value);
	}
	
}
