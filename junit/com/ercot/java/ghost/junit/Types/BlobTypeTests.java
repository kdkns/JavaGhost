package com.ercot.java.ghost.junit.Types;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.ercot.java.ghost.Variable.GhostBlob;
import com.ercot.java.ghost.utils.GhostVariableWrapper;

public class BlobTypeTests extends TestCase{
	private static Logger logger = Logger.getLogger("BlobTypeTests");
	
	   public BlobTypeTests(String name) {
	      super(name);
	      PropertyConfigurator.configure("properties/log4j.properties");	      
	   }
	   
	   public void testIsNull() {
		  GhostBlob x = new GhostBlob();
		  assertTrue(x.isEmpty());		  
	   }
	   
	   public void testSize() {
			  GhostBlob x = new GhostBlob();
			  logger.debug("Blob size" + GhostVariableWrapper.wrapVariable(x.size()));
			  assertTrue(x.size().equals(Long.valueOf(0)));
	   }
	   
	   public void testEquals() {
			  GhostBlob x = new GhostBlob();
			  assertFalse(x.equals(new GhostBlob()));
	   }
	   
}
