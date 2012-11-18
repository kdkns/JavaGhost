package com.ercot.java.ghost.junit.MetaTable;

import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.ercot.java.ghost.Exceptions.GhostAttributeDoesNotExistException;
import com.ercot.java.ghost.GhostAttributes.GhostAttributeEnum;
import com.ercot.java.ghost.junit.TestClasses.DummyTable;
import com.ercot.java.ghost.junit.TestClasses.MetaTables;
public class MetaTableTests extends TestCase {
	
	private static Logger logger = Logger.getLogger("MetaTableTests");
	
   public MetaTableTests(String name) {
      super(name);
      PropertyConfigurator.configure("properties/log4j.properties");
   }

//   public class supA{
//	   public final int a;
//	   
//	   public int getA(){return a;}
//	   public supA(int x){
//		   this.a = x;
//		   System.out.println(this.getA());
//	   }
//   }
//   
//   public class subB extends supA{
//
//	   public int a;
//	   
//	   public int getA(){return a+5;}
//	   public int getSuperA(){return super.getA();}
//	   
//	public subB(int x) {
//		super(x);		
//		a += 1;
//	}  
//   }
//   
//   public void testSSUB() {
//	 subB b = new subB(1);
//	 System.out.println("Super A :" +  b.getSuperA());
//	 System.out.println("Sub A :" +  b.getA());	 
//   }
   
   public void testCreation() {
	   try{
		   @SuppressWarnings("unused")
		   DummyTable dummy = MetaTables.DUMMYTABLE;
	   }catch(Exception e){
		   logger.error(e.getMessage(),e);
		   fail();
	   }
   }
   
   public void testAttribute() {
	   DummyTable dummy = MetaTables.DUMMYTABLE;
	   
	    dummy.getAttributeField(GhostAttributeEnum.stoptime);	    
	    
	    try {
		    dummy.getAttributeField(GhostAttributeEnum.crraccthldrcode);
		    fail();
	    } catch (GhostAttributeDoesNotExistException e) {
			logger.error(e.getMessage(),e);
        }
	    
	    Set<GhostAttributeEnum> ga = dummy.getAttributes();
	    assertEquals(ga.size(),2);
	    assertTrue(ga.contains(GhostAttributeEnum.stoptime));
	    assertTrue(ga.contains(GhostAttributeEnum.starttime));
	    
   }

}


