package com.java.ghost.MetaTableTypes;


public interface IMetaChildTable extends IMetaTable {
   public boolean hasInsertHeaderOnFailure();
   public IMetaParentTable getParentTable();
   public IMetaField getKeyField();
   public boolean doIMBTColumnsUseParentTable();
   
}
