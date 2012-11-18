package com.java.ghost.junit.Types;

import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.java.ghost.MetaTableTypes.IMetaField;
import com.java.ghost.junit.TestClasses.DummyTable;
import com.java.ghost.junit.TestClasses.MetaTables;

public class TableTypeTests extends TestCase{
	private static Logger logger = Logger.getLogger("BlobTypeTests");
	
	   public TableTypeTests(String name) {
	      super(name);
	      PropertyConfigurator.configure("properties/log4j.properties");	      
	   }
	   
	   public void testGetAllColumns() {
		  DummyTable dummy = MetaTables.DUMMYTABLE;
		  List<IMetaField> fields = dummy.getAllColumns();
		  for( IMetaField i : fields){
			  logger.debug("testGetAllColumns column name: " + i.getColumnName());
		  }
		  assertEquals(dummy.numberOfColumns(),fields.size());
	   }
	   
	   public void testGetRequiredInsertFields() {
		      DummyTable dummy = MetaTables.DUMMYTABLE;
			  List<IMetaField> fields = dummy.getRequiredInsertFieldList();
			  for( IMetaField i : fields){
				  logger.debug("testGetRequiredFields column name: " +i.getColumnName());
			  }
			  assertEquals(1,fields.size());
		   }
	   
}
