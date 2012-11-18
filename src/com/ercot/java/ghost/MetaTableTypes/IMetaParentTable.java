package com.ercot.java.ghost.MetaTableTypes;

import com.ercot.java.ghost.GhostAttributes.IGhostAttribute;

public interface IMetaParentTable extends IMetaTable{
   public IMetaTable getTableObject();
   public IMetaField getForeignKeyField();
   public IMetaField getHeaderCheckColumn();
//   public IAttributeList getAttributeList();
   public IMetaField getAttributeField(IGhostAttribute attribute);
}
