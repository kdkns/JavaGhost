package com.ercot.java.ghost.MetaTableTypes;

import com.ercot.java.ghost.utils.GhostDBStaticVariables;


public interface IMetaField extends Comparable<IMetaField>{
   public String getColumnName();
   public String getAlias();
   public IMetaTable getAssociatedTable();
   public String getAssociatedTableName();
   public String getAssociatedTableAlias();
   public String getFullyQualifiedTableAliasWithAliasName();
   public String getFullyQualifiedTableAliaisWithColumnName();
   public String getColumnNameAndAlias();
   public GhostDBStaticVariables.DBTypes getType();
   public String toString();
   public int getSize();
   public int getScale();
   public boolean isFunction();
   public boolean isRequired();
   public void setTableAlias(String tableAlias);
   public void setAlias(String alias);
   
   public IMetaField getSumField();
   public IMetaField getMaxField();
   public IMetaField getMinField();
   public IMetaField getAvgField();
   public IMetaField getModifiedDateField(Number seconds);
   public IMetaField getModifiedDateFieldPlus235959();
   public IMetaField getNVLField(IMetaField value);
   public IMetaField getNVLField(Object value);
   
}
