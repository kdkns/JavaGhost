package com.java.ghost.DBObject;

import oracle.sql.NUMBER;

import com.java.ghost.GhostAttributes.GhostAttributeEnum;
import com.java.ghost.GhostAttributes.IGhostCustomAttribute;
import com.java.ghost.MetaTableTypes.IMetaChildTable;
import com.java.ghost.MetaTableTypes.IMetaField;
import com.java.ghost.MetaTableTypes.IMetaGhostVariableTable;
import com.java.ghost.utils.IGDate;

public interface IDBObject<inputObjectType, getValueType, sizeReturnType, tableType extends IMetaGhostVariableTable> {
   public getValueType getValue();
   public void setValue(inputObjectType o);
   public void clear();
   public void setCustomAttribute(IGhostCustomAttribute attribute, String value);
   public String getCustomAttribute(IGhostCustomAttribute attribute);
   public void setCustomAttribute(IGhostCustomAttribute attribute, IGDate value);
   public IGDate getCustomDateAttribute(IGhostCustomAttribute attribute);
   public String getHeaderAttribute(IMetaChildTable childTable, GhostAttributeEnum attribute);
   public String getAttribute(GhostAttributeEnum attribute);
   public NUMBER getOracleKey();
   public DBID getKey();
   public boolean equals(Object obj);
   public int hashCode();
   public sizeReturnType size();
   public boolean isRemoteTable();
   public boolean isEmpty();
   public tableType getTable();
   public void setTable(tableType table);
   public IMetaField getValueColumn();
   public void setLivesAtColumn(IMetaField valueColumn);
   public void updateInfo(tableType table, IMetaField valueColumn);
   public void setKey(DBID key);
   public IMetaField getLivesAtIdColumn();
   public void setLivesAtIdColumn(IMetaField idColumn);
   public DBID getLivesWithId();
   public void setLivesWithId(DBID id);
   public void setIsEmpty(boolean b);
   public void setIsRemoteTable(boolean b);
   public IMetaField getVMInsertColumn();
}
