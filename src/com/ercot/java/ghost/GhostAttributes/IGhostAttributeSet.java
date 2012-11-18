package com.ercot.java.ghost.GhostAttributes;

import java.util.Collection;

public interface IGhostAttributeSet {
	
	public abstract boolean removeAttribute(GhostAttributeEnum gae);

	public abstract boolean removeParentAttribute(GhostAttributeEnum gae);

	public abstract boolean removeCustomAttribute(IGhostCustomAttribute gae);

	public abstract Collection<? extends GhostAttributeEnum> getAttributes();

	public abstract Collection<? extends GhostAttributeEnum> getParentAttributes();

	public abstract Collection<? extends IGhostCustomAttribute> getCustomAttributes();

	public abstract boolean areAttriubtesEmpty();

	public abstract boolean areParentAttriubtesEmpty();

	public abstract boolean areCustomAttriubtesEmpty();

}