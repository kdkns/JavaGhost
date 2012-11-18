package com.ercot.java.ghost.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import oracle.jdbc.pool.OracleConnectionCacheManager;
import oracle.jdbc.pool.OracleDataSource;

import org.apache.log4j.Logger;

public class GhostJDBCUtils {

//	public static Connection getConnection() throws SQLException {
//		return DatabaseConnection.getConnection();
//	}

	 private final static String CACHE_NAME = "MYCACHE";
	 private static OracleDataSource ods = null;
	 private static Logger logger= Logger.getLogger("JDBCUtils");
	
	 static {
//	 logger.debug("Initializing Connection...");
	 try {
	 ods = new OracleDataSource();
//		 ods = new OracleOCIConnectionPool();
	 // ods.setURL("jdbc:oracle:thin:@//10.37.4.137:1527/dv05ndl");
	 // ods.setURL("jdbc:oracle:thin:@//10.5.4.31:1527/dv11ndl");
	 // ods.setURL("jdbc:oracle:thin:@//10.5.1.113:1527/ts08ndl");
	
//	 ods.setURL("jdbc:oracle:thin:@//10.5.1.56:1527/dv02lod");
	 ods.setURL("jdbc:oracle:thin:@//10.5.1.56:1527/dv01rap");
	
	 // ods.setUser("smijar");
	 // ods.setPassword("smijar");
	
	 // ods.setUser("apxuser");
	 // ods.setPassword("a5tr05##");
	 ods.setUser("arohatgi");
	 ods.setPassword("Ercot!23");
	
	 // caching parms
	 ods.setConnectionCachingEnabled(true);
	 ods.setConnectionCacheName(CACHE_NAME);
	 Properties cacheProps = new Properties();
	 cacheProps.setProperty("MinLimit", "1");
	 cacheProps.setProperty("MaxLimit", "4");
	 cacheProps.setProperty("InitialLimit", "2");
	 cacheProps.setProperty("ConnectionWaitTimeout", "0");
	 cacheProps.setProperty("ValidateConnection", "true");
	
	 ods.setConnectionCacheProperties(cacheProps);
	
	 }
	 catch (SQLException e) {
//	 logger.error(e.getMessage());
		 e.printStackTrace();
	 }
	 }
	
	 /**
	 * private constructor for static class
	 */
	 private GhostJDBCUtils() { }
	
	 public static Connection getConnection() throws SQLException {
	 return getConnection("Environment Unspecified");
	 }
	
	
	 public static Connection getConnection(String env)
	 throws SQLException
	 {
	 //logger.info("Request connection for " + env);
	 if (ods == null) {
	 throw new SQLException("OracleDataSource is null.");
	 }
	 return ods.getConnection();
	 }
	
	 public static void closePooledConnections() throws SQLException{
		 if (ods != null ) {
			 ods.close();
		 }
	 }
	
	 public static void listCacheInfos() throws SQLException{
	 OracleConnectionCacheManager occm =
	 OracleConnectionCacheManager.getConnectionCacheManagerInstance();
	 logger.info
	 (occm.getNumberOfAvailableConnections(CACHE_NAME)
	 + " connections are available in cache " + CACHE_NAME);
	 logger.info
	 (occm.getNumberOfActiveConnections(CACHE_NAME)
	 + " connections are active");
	
	 }
}
