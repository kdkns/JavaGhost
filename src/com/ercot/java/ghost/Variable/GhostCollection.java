package com.ercot.java.ghost.Variable;

import java.math.BigDecimal;
import java.util.Collection;

import com.ercot.java.ghost.DBObject.DBCollection;

public class GhostCollection<objectType extends IGhostVariable<?,?>> extends AbstractGhostCollection<DBCollection<objectType>,objectType>{
	//private static Logger logger = Logger.getLogger("GhostCollection");
	 
	public GhostCollection(){
		super(new DBCollection<objectType>());
	}
	
	public GhostCollection(Collection<? extends objectType> arg0){
		super(new DBCollection<objectType>(),arg0);
	}

	@Override
	public void setSize(BigDecimal size) {
		super.setSize(size);		
	}
	
}
