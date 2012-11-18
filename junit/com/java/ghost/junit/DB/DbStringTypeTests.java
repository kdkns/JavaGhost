package com.java.ghost.junit.DB;

import java.sql.SQLException;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.java.ghost.DBObject.DBString;
import com.java.ghost.utils.GhostVariableWrapper;

public class DbStringTypeTests extends TestCase {
	
	private static Logger logger = Logger.getLogger("DbStringTypeTests");
	
   public DbStringTypeTests(String name) {
      super(name);
      PropertyConfigurator.configure("properties/log4j.properties");
   }

  public void testDBStringConstructor(){
	  DBString str = new DBString("Hello World");
	  try {
			logger.debug("OracleKey value:" + GhostVariableWrapper.wrapVariable(str.getOracleKey().bigDecimalValue()));
		} catch (SQLException e) {
			logger.debug(e.getMessage(),e);
		}
		assertTrue(str.getValue().equalsIgnoreCase("Hello World"));
		str.clear();
	  }
  
  public void testDBStringSet(){
	  DBString str = new DBString();
	  str.setValue("Hello World");
	  try {
			logger.debug("OracleKey value:" + GhostVariableWrapper.wrapVariable(str.getOracleKey().bigDecimalValue()));
		} catch (SQLException e) {
			logger.debug(e.getMessage(),e);
		}
		assertTrue(str.getValue().equalsIgnoreCase("Hello World"));
		str.clear();
	  }
  
  public void testDBStringSetAndConstructor(){
	  DBString str = new DBString("Hello World");
	  str.setValue("Test");
	  try {
			logger.debug("OracleKey value:" + GhostVariableWrapper.wrapVariable(str.getOracleKey().bigDecimalValue()));
		} catch (SQLException e) {
			logger.debug(e.getMessage(),e);
		}
		assertTrue(str.getValue().equalsIgnoreCase("Test"));
		str.clear();
	  }
  
  
}


