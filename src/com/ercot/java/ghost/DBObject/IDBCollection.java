package com.ercot.java.ghost.DBObject;

import java.math.BigDecimal;
import java.util.Collection;

import oracle.sql.NUMBER;

import com.ercot.java.ghost.DBObject.DBCollection.MetaInfo;
import com.ercot.java.ghost.GhostAttributes.IGhostCustomAttribute;
import com.ercot.java.ghost.MetaTableTypes.IMetaField;
import com.ercot.java.ghost.MetaTableTypes.IMetaGhostVariableTable;
import com.ercot.java.ghost.QueryConstructors.GhostQueryInternal;
import com.ercot.java.ghost.Variable.IGhostVariable;
import com.ercot.java.ghost.utils.IGDate;

public interface IDBCollection <objectType extends IGhostVariable<?,?>>{

	public Long getCollectionId();
	
	public NUMBER getCollectionIdFromDB(objectType arg0);
	
	public NUMBER getDBConvertedCollectionId();
	
	public void addCollectionId(objectType arg0);
	
	public void removeCollectionId(objectType arg0);
	
	public void addBulkCollectionId(NUMBER oldCollectionId);
	
	public void removeBulkCollectionId();
	
	public void clear();

	public void addGhostMetaInfo(objectType arg0);
	
	public void addGhostMetaInfo(IMetaGhostVariableTable table, IMetaField column, IMetaField idColumn);

	public void removeGhostMetaInfo(objectType arg0);

	public void removeAllGhostMetaBlobInfo(Collection<?> arg0);
	
	public String getSQLQuery();
	
	public String getSQLQuery(String additionalColumns);

	public void addByBulkInsertId(BigDecimal bulkSaveId) ;

	public void setCustomAttribute(IGhostCustomAttribute attribute, String value);
	
	public void setCustomAttribute(IGhostCustomAttribute attribute, IGDate value);
	
	public GhostQueryInternal bulkAdd(NUMBER possesionId);

	public GhostQueryInternal bulkAddUsingCollectionId(NUMBER collectionId);	
	
	public IMetaGhostVariableTable lookupTable(String tableName);
	
	public void deleteAllObjects();
	
	public void removeAllObjects();
	
	public IMetaField getVMInsertColumn();
	
	public void setVMInsertColumn(IMetaField insertColumn);
	
	public void mergeMetaInfo(IDBCollection<?> collection);

	public MetaInfo getMetaInfo();	
		
}
