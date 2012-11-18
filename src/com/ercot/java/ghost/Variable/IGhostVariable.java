package com.ercot.java.ghost.Variable;

import java.math.BigDecimal;
import java.util.List;

import com.ercot.java.ghost.Exceptions.GhostExceptions;
import com.ercot.java.ghost.GhostAttributes.GhostAttributeEnum;
import com.ercot.java.ghost.GhostAttributes.IGhostCustomAttribute;
import com.ercot.java.ghost.GhostFieldMaps.GhostFieldMap;
import com.ercot.java.ghost.MetaTableTypes.IMetaField;
import com.ercot.java.ghost.MetaTableTypes.IMetaGhostVariableTable;

public interface IGhostVariable<objectType, sizeReturnType> {
   public void setValue(objectType o) throws GhostExceptions;
   public objectType getValue();
   public sizeReturnType size();
   public void clear();
   public boolean isEmpty();
   public Object getDBKey();
   public BigDecimal getKey();
   public void setCustomAttribute(IGhostCustomAttribute attribute, String value);
   public String getCustomAttribute(IGhostCustomAttribute attribute);
   public String getHeaderAttribute(GhostAttributeEnum attribute);
   public String getAttribute(GhostAttributeEnum attribute);
   public String getAccessSqlQuery();
   public IMetaGhostVariableTable getTable();
   public IMetaField getLivesAtColumn();
   public IMetaField getIdColumn();
   public IMetaField getVMInsertColumn();
   public void blessColumnMappingToVMTable(int startPosition, GhostFieldMap ghostFieldMap, IMetaGhostVariableTable table);
   public int blessNumberOfColumnsForMappingToVMTable();
   public List<String> getBlessAdditionalColumns();
   public void setIsEmpty(boolean value);
   public void setIsRemoteTable(boolean value);
}
