package com.java.ghost.MetaTableTypes;

import com.java.ghost.GhostAttributes.IGhostAttribute;

public interface IMetaParentTable extends IMetaTable{
   public IMetaTable getTableObject();
   public IMetaField getForeignKeyField();
   public IMetaField getHeaderCheckColumn();
//   public IAttributeList getAttributeList();
   public IMetaField getAttributeField(IGhostAttribute attribute);
}
