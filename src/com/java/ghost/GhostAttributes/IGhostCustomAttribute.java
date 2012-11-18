package com.java.ghost.GhostAttributes;

import com.java.ghost.utils.GhostDBStaticVariables;

public interface IGhostCustomAttribute extends IGhostAttribute {
	public GhostDBStaticVariables.DBTypes getDBType();
}