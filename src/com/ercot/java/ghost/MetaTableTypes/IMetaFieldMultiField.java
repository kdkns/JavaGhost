package com.ercot.java.ghost.MetaTableTypes;

import java.util.List;

public interface IMetaFieldMultiField extends IMetaFieldConstant{
  public List<IMetaField> getFieldsThatConstructField();
  public boolean addFieldThatConstructsField(IMetaField field);
}
