package com.java.ghost.GhostAttributes;

import java.util.Map;
import java.util.Set;

import com.java.ghost.MetaTableTypes.IMetaField;

public interface IAttributeTableList {
   public IMetaField getAttributeField(IGhostAttribute attribute);
   public Set<? extends GhostAttributeEnum> getAttributes();
   public Map<GhostAttributeEnum, IMetaField> getAttributeMap();
}
