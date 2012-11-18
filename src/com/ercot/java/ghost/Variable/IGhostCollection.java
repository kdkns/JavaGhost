package com.ercot.java.ghost.Variable;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;

import oracle.sql.NUMBER;

import com.ercot.java.ghost.Exceptions.GhostCollectionIsEmptyException;
import com.ercot.java.ghost.Exceptions.GhostCollectionIsNotSingleElementException;
import com.ercot.java.ghost.Exceptions.GhostExceptions;
import com.ercot.java.ghost.GhostAttributes.IGhostCustomAttribute;
import com.ercot.java.ghost.MetaTableTypes.IMetaField;
import com.ercot.java.ghost.MetaTableTypes.IMetaGhostVariableTable;

public interface IGhostCollection<objectType extends IGhostVariable<?,?>> extends Collection<objectType> {
	
	public NUMBER getLocalDBConvertedCollectionId();
	
	public boolean bulkAdd(NUMBER possesionId, IMetaGhostVariableTable mTable, Class<? extends IGhostVariable<?,?>> class1);
	
	public void bulkAddMetaInfo(IMetaGhostVariableTable table, IMetaField column, IMetaField idColumn);

	public boolean contains(Object arg0);

	public boolean containsAll(Collection<?> arg0);

	public boolean isEmpty();

	public Iterator<objectType> iterator();

	public boolean retainAll(Collection<?> arg0);

	public int size();

	public Object[] toArray();

	public <T> T[] toArray(T[] arg0);

	public void clear();

	public void releaseObjects();

	public void setCustomAttribute(IGhostCustomAttribute attribute,
			String value) throws GhostExceptions;

	public String getSQLQuery();

	public Long getCollectionId();

	public boolean add(objectType arg0);

	public boolean addAll(Collection<? extends objectType> arg0);

	public boolean remove(Object arg0);

	public boolean removeAll(Collection<?> arg0);

	public IGhostVariable<?, ?> getGhostVariable(int position);

	public IGhostVariable<?, ?> getSingleGhostVariable()
			throws GhostCollectionIsNotSingleElementException,
			GhostCollectionIsEmptyException;

	public void bulkAddMetaInfo();

	public String getSQLQuery(String additionalColumns);

	public void addByBulkInsertId(BigDecimal bulkSaveId);

	public void setSize(BigDecimal size);
	
	public Class<? extends IGhostVariable<?, ?>> getClassGenericObjectType();
	
	public boolean isIterable();
	
	public void makeIterable();
	
	public void makeUniterable();
	
	public IGhostVariable<?,?> getIGVariableObject();
	
	public IMetaField getVMInsertColumn();
	

}