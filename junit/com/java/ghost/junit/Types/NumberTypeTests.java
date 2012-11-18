package com.java.ghost.junit.Types;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.java.ghost.Variable.GhostNumber;

public class NumberTypeTests extends TestCase{
	private static Logger logger = Logger.getLogger("NumberTypeTests");
	
	   public NumberTypeTests(String name) {
	      super(name);
	      PropertyConfigurator.configure("properties/log4j.properties");	      
	   }
	   
	   public void testNumberPrimitive() {
		  GhostNumber x = new GhostNumber(10);
		  assertEquals(x.getValue().intValue(),10);
		  x.clear();
	   }
	   
	   public void testAccessSqlQuery() {
			  GhostNumber x = new GhostNumber(10);
			  logger.info(x.getAccessSqlQuery());
		   }
	   
	   public void testNumberPrimitiveSet() {
			  GhostNumber x = new GhostNumber();
			  x.setValue(132234);
			  assertEquals(x.getValue().intValue(), 132234);
			  x.clear();
	   }
	      
	   public void testNumberInteger() {
			  GhostNumber x = new GhostNumber();
			  x.setValue(Integer.valueOf(3));
			  assertEquals(x.getValue().intValue(), 3);
			  x.clear();
	   }
		
	   public void testNumberFloat() {
			  GhostNumber x = new GhostNumber();
			  x.clear();
			  x = new GhostNumber(new Float(10.33));
			  assertEquals(x.getValue().floatValue(), new Float(10.33));
			  x.clear();
	   }
		  
	   public void testNumberDouble() {
			  GhostNumber x = new GhostNumber();
			  x.setValue(new Double(3.333));
			  assertEquals(x.getValue().doubleValue(), new Double(3.333));
			  x.clear();
	   }
	   
	   public void testAddValue() {
			  GhostNumber x = new GhostNumber(10);
			  x.add(3);
			  assertEquals(13, x.getValue().intValue());
			  x.clear();
	   }
	   
	   public void testSubtractValue() {
			  GhostNumber x = new GhostNumber(10);
			  x.subtract(3);
			  assertEquals(7, x.getValue().intValue());
			  x.clear();
	   }
	   
	   public void testMultiplyValue() {
			  GhostNumber x = new GhostNumber(10);
			  x.multiply(3);
			  assertEquals(30, x.getValue().intValue());
			  x.clear();
	   }
	   
	   public void testDivideValue() {
			  GhostNumber x = new GhostNumber(10);
			  x.divide(2);
			  assertEquals(5, x.getValue().intValue());
			  x.clear();
	   }
	   
	   public void testAddGhostNumber() {
			  GhostNumber x = new GhostNumber(10);
			  GhostNumber y = new GhostNumber(3);
			  x.add(y);
			  assertEquals(13, x.getValue().intValue());
			  x.clear();
			  y.clear();
	   }
	   
	   public void testSubtractGhostNumber() {
			  GhostNumber x = new GhostNumber(10);
			  GhostNumber y = new GhostNumber(3);
			  x.subtract(y);
			  assertEquals(7, x.getValue().intValue());
			  x.clear();
			  y.clear();
	   }
	   
	   public void testMultiplyGhostNumber() {
			  GhostNumber x = new GhostNumber(10);
			  GhostNumber y = new GhostNumber(3);
			  x.multiply(y);
			  assertEquals(30, x.getValue().intValue());
			  x.clear();
			  y.clear();
	   }
	   
	   public void testDivideGhostNumber() {
			  GhostNumber x = new GhostNumber(10);
			  GhostNumber y = new GhostNumber(2);
			  x.divide(y);
			  assertEquals(5, x.getValue().intValue());
			  x.clear();
			  y.clear();
	   }
}
