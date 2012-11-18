package com.ercot.java.ghost.junit.Types;

import java.io.Serializable;

import junit.framework.TestCase;

import org.apache.log4j.PropertyConfigurator;

import com.ercot.java.ghost.Variable.GhostCollection;
import com.ercot.java.ghost.Variable.GhostString;

public class CollectionTypeTests extends TestCase implements Serializable{
	private static final long serialVersionUID = 1L;
	
	   public CollectionTypeTests(String name) {
	      super(name);
	      PropertyConfigurator.configure("properties/log4j.properties");	      
	   }
	   
//	   public void testObjectCollectionId() {
//			  GhostString gStr = new GhostString("Hello World");
//			  try{
//				  GhostCollection x = new GhostCollection();//1
//				  x.add(gStr);
//				  assertEquals(new NUMBER(1), x.getCollectionIdOfObject(gStr));
//			  }catch(GhostCollectionObjectMaxReachedException e){
//			      logger.error(e.getMessage(),e);
//				  fail();
//		   }
//	   }
	   
	   public void testCollectionId() {
			  GhostString gStr = new GhostString("Hello World");
			  GhostString gStr2 = new GhostString("World");
			  GhostString gStr3 = new GhostString("!");
//			  GhostMetaBlob b = new GhostMetaBlob();
			  
			 
				  GhostCollection<GhostString> x = new GhostCollection<GhostString>();//1   1
				  GhostCollection<GhostString> y = new GhostCollection<GhostString>();//2   2
				  GhostCollection<GhostString> z = new GhostCollection<GhostString>();//4   3
				  GhostCollection<GhostString> l = new GhostCollection<GhostString>();//8   4
//				  GhostCollection i = new GhostCollection();//8
				  //GhostCollection j = new GhostCollection();//16
				  x.add(gStr);
				  x.add(gStr2);
				  y.add(gStr2);
				  
				  z.add(gStr3);
				  
//				  x.add(b);
//				  z.add(b);
//				  i.add(b);
//				  i.add(gStr2);
				  
				  // gStr = 9
//				  z.clear();
//				  GhostCollection w = new GhostCollection();//4
//				  w.add(gStr);
				  //gStr = 1 + 4  (x + w)
//				  x.setCustomAttribute(GhostCustomAttributeEnum.custom_3, "Test");
				  
	
		      //j.add(b); // should fail              
		   }
	   
	   public void testAdd() {		   
			  GhostCollection<GhostString> x = new GhostCollection<GhostString>();
			  GhostString gStr = new GhostString("Hello World");
			  x.add(gStr);
			  x.makeIterable();
			  assertEquals("Hello World",((GhostString)x.getGhostVariable(0)).getValue());
	   }
	   
}

