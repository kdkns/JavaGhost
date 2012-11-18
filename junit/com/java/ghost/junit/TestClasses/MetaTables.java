package com.java.ghost.junit.TestClasses;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.java.ghost.Exceptions.GhostRuntimeException;


public class MetaTables {
	private static Logger logger = Logger.getLogger("MetaTables");
	private static Properties props;
	private static String odsPropertiesFile = "ods.properties";
	
//	public static Ghost_VM GHOST_VM;
	public static DummyTable DUMMYTABLE;
	public static DummyTable2 DUMMYTABLE2;
	
	static {
		    initProperties();
			DUMMYTABLE = new DummyTable();
			DUMMYTABLE2 = new DummyTable2();
//			try{
//				Class<?> vmClass = Class.forName(props.getProperty("vm.table.class"));
//				GHOST_VM = (Ghost_VM) vmClass.newInstance();
//			} catch (ClassNotFoundException e) {
//				logger.error(e.getMessage(),e);
//				throw new GhostRuntimeException(e);
//			} catch (InstantiationException e) {
//				logger.error(e.getMessage(),e);
//				throw new GhostRuntimeException(e);
//			} catch (IllegalAccessException e) {
//				logger.error(e.getMessage(),e);
//				throw new GhostRuntimeException(e);
//			} catch(Exception e){
//				logger.error(e.getMessage(),e);
//				throw new GhostRuntimeException(e);
//			}
	}

	private static void initProperties() {
		try {
			props  = new Properties();
            InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(odsPropertiesFile);
            props.load(in);
            in.close();
            logger.debug("ODS Properties file "+odsPropertiesFile+" loaded for table properties.");
            
            System.setProperty("com.ercot.ndlstarOwner",props.getProperty("ods.app.ndlstar").toUpperCase());
            System.setProperty("com.ercot.lodstarOwner",props.getProperty("ods.app.lodstar").toUpperCase());
            System.setProperty("com.ercot.packageOwner",props.getProperty("ods.app.packageOwner").toUpperCase());
            
		} catch (FileNotFoundException e) {
        	throw new GhostRuntimeException(e,"Could not locate "+odsPropertiesFile+" file.");
        } catch (IOException e) {
        	throw new GhostRuntimeException(e,"An error occurred while opening file "+odsPropertiesFile+".");
        }
		
	}
}
