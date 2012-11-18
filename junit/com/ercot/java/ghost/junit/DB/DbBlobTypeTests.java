package com.ercot.java.ghost.junit.DB;

import java.sql.SQLException;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;

import com.ercot.java.ghost.DBObject.DBBlob;
import com.ercot.java.ghost.utils.GhostVariableWrapper;

public class DbBlobTypeTests extends TestCase {
	
	private static Logger logger = Logger.getLogger("DbBlobTypeTests");
	
   public DbBlobTypeTests(String name) {
      super(name);
      PropertyConfigurator.configure("properties/log4j.properties");
   }

   public void testDBBlobIsEqual(){
	   DBBlob blob = new DBBlob();
	   DBBlob blobCompare = new DBBlob();
	   logger.debug("DBBLobISEqual return value: " + GhostVariableWrapper.wrapVariable(blob.equals(blobCompare)));
	   assertFalse(blob.equals(blobCompare));
	   
	   byte[] byteArray = {127, 127, 127, 127};
	   blobCompare.setBytes(1, byteArray);
//	   logger.debug("DBBLobISEqual return value: " + GhostVariableWrapper.wrapVariable(blob.equals(blobCompare)));
//	   assertFalse(blob.equals(blobCompare));
//	   blob.clear();
//	   blobCompare.clear();
   }
   
   public void testDBBlobLength(){
	   DBBlob blobCompare = new DBBlob();
	   byte[] byteArray = {127, 127, 127, 127};
	   blobCompare.setBytes(1, byteArray);
	   
	   assertEquals(4, blobCompare.getLengthInBytes());
	   blobCompare.clear();
   }
      
//   public void testDBMetaBlobAdd(){
//	   DBMetaBlob blob = new DBMetaBlob();
//	   //DBMetaBlob blobResult = blob.add(BigDecimal.valueOf(2199), BigDecimal.valueOf(2200));
//	   blob.add(BigDecimal.valueOf(2199), BigDecimal.valueOf(2200));
//	   //assertNotNull(blobResult);
//   }
   
//   public void testDBMetaBlobAdd(){
//	   DBMetaBlob blob = new DBMetaBlob();
//	   DBMetaBlob blob2 = new DBMetaBlob();
//	   
//	   byte[] byteArray = {127, 127, 127, 127};
//	   blob.setBytes(1, byteArray);
//	   
//	   //DBBlob blobResult = blob.add(blob2);
//	   blob2.add(blob);
//	   //assertFalse(blob.equals(blobCompare));
//   }
   
   
  public void testDBBlobSetByteArray() throws SQLException{
	  byte[] byteArray = {127, 127, 127, 127};
	  
	  DBBlob blob = new DBBlob();
	  
	  try {
		   logger.debug("Byte Array Length:" + GhostVariableWrapper.wrapVariable(byteArray.length));
		   //blob.getValue().setBytes(byteArray.length, byteArray);
		   blob.setBytes(1,byteArray);
		   logger.debug("Blob Length:" + GhostVariableWrapper.wrapVariable(blob.getValue().length()));
		} catch (SQLException e) {
		   logger.error(e.getMessage(),e);
		}
	  	
	  logger.debug("Blob Byte Array:" + GhostVariableWrapper.wrapVariable(byteArray));
	  logger.debug("Blob Value:" + GhostVariableWrapper.wrapVariable(blob.getValue().getBytes(1, 1)));
	  
	  try {
		    logger.debug("DBBlob Byte Array:" + GhostVariableWrapper.wrapVariable(blob.getBytes(1,7)));
			logger.debug("OracleKey value:" + GhostVariableWrapper.wrapVariable(blob.getOracleKey().bigDecimalValue()));
		} catch (SQLException e) {
			logger.debug(e.getMessage(),e);
		}
	  try {
		  Assert.assertArrayEquals(byteArray,blob.getValue().getBytes(1,7));
		} catch (SQLException e) {
			logger.error(e.getMessage(),e);
		}
		blob.clear();		
	  }
  
}


