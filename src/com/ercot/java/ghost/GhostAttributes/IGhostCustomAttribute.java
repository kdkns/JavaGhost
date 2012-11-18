package com.ercot.java.ghost.GhostAttributes;

import com.ercot.java.ghost.utils.GhostDBStaticVariables;

public interface IGhostCustomAttribute extends IGhostAttribute {
	public GhostDBStaticVariables.DBTypes getDBType();
}