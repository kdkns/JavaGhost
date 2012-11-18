package com.java.ghost.junit.Types;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.java.ghost.Exceptions.GhostCollectionIsEmptyException;
import com.java.ghost.Exceptions.GhostCollectionIsNotSingleElementException;
import com.java.ghost.Exceptions.GhostRuntimeException;
import com.java.ghost.GhostAttributes.GhostCustomAttributeEnum;
import com.java.ghost.QueryConstructors.GhostTableFilter;
import com.java.ghost.Variable.GhostBlob;
import com.java.ghost.Variable.GhostCollection;
import com.java.ghost.junit.TestClasses.MetaTables;
import com.java.ghost.utils.GDate;
import com.java.ghost.utils.GhostStaticVariables;
import com.java.ghost.utils.GhostVariableWrapper;

public class MetaBlobTypeTests extends TestCase{
	private static Logger logger = Logger.getLogger("MetaBlobTypeTests");
	
	   public MetaBlobTypeTests(String name) {
	      super(name);
	      PropertyConfigurator.configure("properties/log4j.properties");	      
	   }
	   
	   public void testAccessSqlQuery(){
		      GhostBlob x = new GhostBlob();		      
			  logger.info(x.getAccessSqlQuery());
			  
			 
			  GhostTableFilter gtf = new GhostTableFilter();
			  gtf.addAndRownumField(0, 1);
			  GhostCollection<GhostBlob> gmbc = new GhostCollection<GhostBlob>();
              GhostStaticVariables.POSSESS.possess(gmbc,MetaTables.DUMMYTABLE, gtf);
			  
			  try {
				  
				logger.info(gmbc.getGhostVariable(0).getAccessSqlQuery());
			    GhostBlob result = gmbc.getSingleGhostVariable();
//				GhostBlob result = gmbc.getGhostVariable(0).addReturnMetaBlob(gmbc.getGhostVariable(1));
				logger.info(result.getAccessSqlQuery());			
			} catch (GhostCollectionIsNotSingleElementException e) {
				logger.error(e.getMessage(),e);
				fail();
			} catch (GhostCollectionIsEmptyException e) {
				logger.error(e.getMessage(),e);
				fail();
			}
		   }
	   
	   public void testIsEmpty() {
		  GhostBlob x = new GhostBlob();
		  assertTrue(x.isEmpty());
		  x.clear();
	   }
	   
	  
//	   public void testLength() {
//			  GhostBlob x = new GhostBlob();//new GDate(), 3600, new BigDecimal(1234), "MKTINPUTINTERVAL", "UIDMKTINPUTINTERVAL", "VALUECODES");
//			  byte[] byteArray = {127, 127, 127, 127};
//			  DBBlob o = new DBBlob();
//
//			  o.setBytes(1, byteArray);
//			  
//			  try {
//				  x.setValue(o.getValue());
//				  logger.debug("MetaBlob length:" + GhostVariableWrapper.wrapVariable(x.getLengthInBytes()));
//				  assertEquals(4,x.getLengthInBytes());
//				} catch (GhostObjectNotSetException e) {
//					logger.error(e.getMessage(),e);
//					fail();
//				} catch (GhostNullObjectException e) {
//					logger.error(e.getMessage(),e);
//					fail();
//				}
//				o.clear();
//				x.clear();
//	   }
//	   
	 
	   
	   
	   public void testSettingCustomAttribute() {
		   GDate gDate = new GDate();
		   GhostBlob x = new GhostBlob();
		   
		   x.setCustomAttribute(GhostCustomAttributeEnum.custom_3, "Test");
		   logger.debug("testSettingCustomAttribute Value: " + GhostVariableWrapper.wrapVariable(x.getCustomAttribute(GhostCustomAttributeEnum.custom_3)));		   
		   assertTrue("Test".equalsIgnoreCase(x.getCustomAttribute(GhostCustomAttributeEnum.custom_3)));
		   
		   try{
			   x.setCustomAttribute(GhostCustomAttributeEnum.custom_date_3, "Test");
			   fail();
		   }catch(GhostRuntimeException e){
			   logger.error(e.getMessage(),e);
		   }catch(Exception e){
			   logger.error(e.getMessage(),e);
			   fail();
		   }
		   
		   x.setCustomAttribute(GhostCustomAttributeEnum.custom_date_3, gDate);
		   assertTrue(gDate.compareTime(x.getCustomDateAttribute(GhostCustomAttributeEnum.custom_date_3)) );
		   
		   
		   try{
			   x.setCustomAttribute(GhostCustomAttributeEnum.custom_3, gDate);
			   fail();
		   }catch(GhostRuntimeException e){
			   logger.error(e.getMessage(),e);
		   }catch(Exception e){
			   logger.error(e.getMessage(),e);
			   fail();
		   }

		   x.clear();
	   }
	   
//	   
//	   public void testSize() {
//			  GhostBlob x = new GhostBlob();
//			  logger.debug("Blob size" + GhostVariableWrapper.wrapVariable(x.size()));
//			  assertEquals(0,x.size());
//	   }
//	   
//	   public void testEquals() {
//			  GhostBlob x = new GhostBlob();
//			  assertTrue(x.equals(new GhostBlob()));
//	   }
	   
}
