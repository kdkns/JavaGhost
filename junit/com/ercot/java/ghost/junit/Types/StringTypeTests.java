package com.ercot.java.ghost.junit.Types;

import junit.framework.TestCase;

import org.apache.log4j.PropertyConfigurator;

import com.ercot.java.ghost.Variable.GhostString;

public class StringTypeTests extends TestCase{
//	private static Logger logger = Logger.getLogger("StringTypeTests");
	
	   public StringTypeTests(String name) {
	      super(name);
	      PropertyConfigurator.configure("properties/log4j.properties");	      
	   }
	   
	   public void testStringAssignment() {
		  GhostString x = new GhostString("Hello World");
		  assertTrue(x.getValue().equalsIgnoreCase("Hello World"));
		  x.clear();
	   }
	   
	   public void testStringConcatValue() {
			  GhostString x = new GhostString("Hello World");
			  x.concat("TEST");
			  assertTrue(x.getValue().equalsIgnoreCase("Hello WorldTEST"));
			  x.clear();
	   }
	   
	   public void testStringConcatGhostString() {
			  GhostString x = new GhostString("Hello World");
			  GhostString y = new GhostString("TEST");
			  x.concat(y);
			  assertTrue(x.getValue().equalsIgnoreCase("Hello WorldTEST"));
			  x.clear();
			  y.clear();
	   }
	   
}
