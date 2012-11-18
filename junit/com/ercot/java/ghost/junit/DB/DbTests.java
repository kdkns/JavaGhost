package com.ercot.java.ghost.junit.DB;

import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.ercot.java.ghost.utils.GhostJDBCUtils;

public class DbTests extends TestCase {
	
	private static Logger logger = Logger.getLogger("DbTests");
	
   public DbTests(String name) {
      super(name);
      PropertyConfigurator.configure("properties/log4j.properties");
   }

   public void testConnection() {
	  
      Connection conn = null;
      try{
          conn = GhostJDBCUtils.getConnection();
          assertNotNull(conn);
      } catch (SQLException e){
    	  logger.error(e.getMessage());
      }
      finally{
    	  try{
    	      conn.close();
    	  }catch(SQLException e){
    		  logger.error(e.getMessage());
    	  }
      }
   }
}


