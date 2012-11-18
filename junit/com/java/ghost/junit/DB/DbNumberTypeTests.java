package com.java.ghost.junit.DB;

import java.sql.SQLException;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.java.ghost.DBObject.DBNumber;
import com.java.ghost.utils.GhostVariableWrapper;

public class DbNumberTypeTests extends TestCase {
	
	private static Logger logger = Logger.getLogger("DbNumberTypeTests");
	
   public DbNumberTypeTests(String name) {
      super(name);
      PropertyConfigurator.configure("properties/log4j.properties");
   }

  public void testDBNumberInteger(){
	   DBNumber number = new DBNumber();
	   number.setValue(10);
	  try {
			logger.debug("OracleKey value:" + GhostVariableWrapper.wrapVariable(number.getOracleKey().bigDecimalValue()));
		} catch (SQLException e) {
			logger.debug(e.getMessage(),e);
		}
	  assertEquals(number.getValue().intValue(),10);
	  number.clear();
	  }
  
  public void testDBNumberFloat(){
	  DBNumber number = new DBNumber(10.123);
	  try {
			logger.debug("OracleKey value:" + GhostVariableWrapper.wrapVariable(number.getOracleKey().bigDecimalValue()));
			logger.debug("DBNumber float value:" + GhostVariableWrapper.wrapVariable(number.getValue().floatValue()));
		} catch (SQLException e) {
			logger.debug(e.getMessage(),e);
		}
	  assertEquals(number.getValue().floatValue(),10.123f);
	  number.clear();
	  }
	
  public void testDBNumberDouble(){
	  DBNumber number = new DBNumber(10.1233333333333);
	  try {
			logger.debug("OracleKey value:" + GhostVariableWrapper.wrapVariable(number.getOracleKey().bigDecimalValue()));
			logger.debug("DBNumber double value:" + GhostVariableWrapper.wrapVariable(number.getValue().floatValue()));
		} catch (SQLException e) {
			logger.debug(e.getMessage());
		}
	  assertEquals(number.getValue().doubleValue(),10.1233333333333d);
	  number.clear();
	  }
  
//  public void testDBNumberBigDecimal(){
//	  BigDecimal bd = new BigDecimal(10.12333333333d);
//	  bd.setScale(38,BigDecimal.ROUND_HALF_UP);
//	  DBNumber number = new DBNumber(bd);
//	  
//	  try {
//			logger.debug("OracleKey value: " + number.getOracleKey().bigDecimalValue());
//			logger.debug("DBNumber BigDecimal value: " + number.getValue());
//			logger.debug("BigDecimal value: " + bd);
//		} catch (SQLException e) {
//			logger.debug(e.getMessage());
//		}
//	  assertEquals(number.getValue(),bd);
//	  }
}


