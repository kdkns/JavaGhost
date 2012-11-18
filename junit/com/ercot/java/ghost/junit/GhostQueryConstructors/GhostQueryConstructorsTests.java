package com.ercot.java.ghost.junit.GhostQueryConstructors;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.ercot.java.ghost.Exceptions.GhostDBRestrictionException;
import com.ercot.java.ghost.Exceptions.GhostFieldRequiredForInsertException;
import com.ercot.java.ghost.Exceptions.GhostQueryBuilderException;
import com.ercot.java.ghost.GhostFieldMaps.GhostFieldMap;
import com.ercot.java.ghost.GhostFieldMaps.GhostFieldMapObject;
import com.ercot.java.ghost.MetaTableTypes.GhostQueryTable;
import com.ercot.java.ghost.MetaTableTypes.IMetaField;
import com.ercot.java.ghost.MetaTableTypes.IMetaTable;
import com.ercot.java.ghost.QueryConstructors.GhostQuery;
import com.ercot.java.ghost.QueryConstructors.GhostQueryConstructor;
import com.ercot.java.ghost.QueryConstructors.GhostQueryFilter.FilterOperationTypes;
import com.ercot.java.ghost.QueryConstructors.GhostTableFilter;
import com.ercot.java.ghost.junit.TestClasses.DummyTable;
import com.ercot.java.ghost.junit.TestClasses.MetaTables;
import com.ercot.java.ghost.utils.GDate;
import com.ercot.java.ghost.utils.GhostDBStaticVariables;
import com.ercot.java.ghost.utils.GhostListNumber;
import com.ercot.java.ghost.utils.GhostListString;
import com.ercot.java.ghost.utils.GhostVariableWrapper;

public class GhostQueryConstructorsTests extends TestCase{
private static Logger logger = Logger.getLogger("GhostQueryConstructorTests");
	
	public GhostQueryConstructorsTests(String name) {
	    super(name);
	    PropertyConfigurator.configure("properties/log4j.properties");
	 }
	
//	public void testReflectionPerformance() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
//		Object object = new Object();
//		Class c = Object.class;
//
//		int loops = 100000;
//
//		long start = System.currentTimeMillis();
//		for( int i = 0; i < loops; i++ )
//		{
//		object.toString();
//		}
//		System.out.println( loops + " regular method calls:" + (System.currentTimeMillis() - start) + " milliseconds." );
//		java.lang.reflect.Method method = c.getMethod( "toString", null );
//
//		start = System.currentTimeMillis();
//		for( int i = 0; i < loops; i++ )
//		{
//		method.invoke( object, null );
//		}
//
//		System.out.println( loops + " reflective method calls without lookup:" + (System.currentTimeMillis() - start) + " milliseconds." );
//		start = System.currentTimeMillis();
//		for( int i = 0; i < loops; i++ )
//		{
//		method = c.getMethod( "toString", null );
//		method.invoke( object, null );
//		}
//		System.out.println( loops + " reflective method calls with lookup:" + (System.currentTimeMillis() - start) + " milliseconds." ); 
//	}
	
	public void testGQCSelectAllQuery(){
		DummyTable dt = MetaTables.DUMMYTABLE;
	    GhostQueryConstructor.selectAllQuery(dt);
	}
	
	public void testGQCFieldMapConcatValues(){
		DummyTable dt = MetaTables.DUMMYTABLE;
		GhostFieldMap bfm = new GhostFieldMap();
		bfm.put(1,dt.getIamnumbercolumn(), new GhostFieldMapObject(132, GhostFieldMapObject.GhostFieldMapObjectTypes.Number));
		
		String test = bfm.getConcatValues(",");
		logger.debug(GhostVariableWrapper.wrapVariable(test));
		assertTrue(test.equals("132"));
	}
	
	public void testGQCInsertFieldCheck(){
		DummyTable dt = MetaTables.DUMMYTABLE;
		
		GhostFieldMap bfm = new GhostFieldMap();
		//bfm.put(dt.getTotal(), new BlessFieldMapObject(132, BlessFieldMapObject.ObjectTypes.Number));
		//bfm.put(dt.getLsuser(), new BlessFieldMapObject("test", BlessFieldMapObject.ObjectTypes.String));
		//bfm.put(dt.getSpi(), new BlessFieldMapObject(3600, BlessFieldMapObject.ObjectTypes.Number));
	    try{	
			GhostQueryConstructor.insertQuery(dt, bfm);		
			fail();
		}catch(GhostFieldRequiredForInsertException e){
			
		}
	}
	
	public void testGQCSelect(){
		DummyTable dt = MetaTables.DUMMYTABLE;
        GhostTableFilter tf = new GhostTableFilter();
        GhostQuery gq = null;
        
        List<IMetaField> selectFieldList = new ArrayList<IMetaField>();
        selectFieldList.add(dt.getIamnumbercolumn());
        
        
			tf.addAndField(0,GhostTableFilter.FilterOperationTypes.EQUALS, dt.getIamnumbercolumn(), 27);
		        
			gq = GhostQueryConstructor.selectQuery(GhostQueryConstructor.IS_NOT_DISTINCT, selectFieldList, dt, tf);
		
		if(gq!=null){
			logger.debug(GhostVariableWrapper.wrapVariable(gq.getQuery()));
			assertTrue(gq.getQuery().equals("SELECT  DUMMY.IAMNUMBERCOLUMN IAMNUMBERCOLUMN FROM AROHATGI.DUMMY DUMMY WHERE 1=1  AND DUMMY.IAMNUMBERCOLUMN = 27"));
			gq.close();				
		}
		
		
	}
	
	public void testGQCInsert(){
		DummyTable dt = MetaTables.DUMMYTABLE;
		
		GhostFieldMap bfm = new GhostFieldMap();
		bfm.put(1,dt.getUidcolumn(), 123);
		bfm.put(2,dt.getIamnumbercolumn(),321);
		bfm.put(3,dt.getIamstringcolumn(),"test");
		
//		bfm.put(dt.getIamnumbercolumn(), new GhostFieldMapObject(132, GhostFieldMapObject.GhostFieldMapObjectTypes.Number));
//		bfm.put(dt.getIamnumbercolumn(), new GhostFieldMapObject("test", GhostFieldMapObject.GhostFieldMapObjectTypes.String));
		
//		bfm.put(, new BlessFieldMapObject(3600, BlessFieldMapObject.ObjectTypes.Number));
		
		
		GhostQuery gq = null;
		
		gq = GhostQueryConstructor.insertQuery(dt, bfm);
		
		logger.debug(GhostVariableWrapper.wrapVariable(gq.getQuery()));
		assertTrue(gq.getQuery().equals(" INSERT INTO AROHATGI.DUMMY(UIDCOLUMN,IAMNUMBERCOLUMN,IAMSTRINGCOLUMN)VALUES(123,321,'test' )"));
	}

	public void testGQCUpdate(){
		DummyTable dt = MetaTables.DUMMYTABLE;
		
		GhostFieldMap bfm = new GhostFieldMap();
		bfm.put(1,dt.getUidcolumn(), 123);
		bfm.put(2,dt.getIamnumbercolumn(),321);
		bfm.put(3,dt.getIamstringcolumn(),"test");
//		bfm.put(dt.getIamnumbercolumn(), new GhostFieldMapObject(132, GhostFieldMapObject.GhostFieldMapObjectTypes.Number));
//		bfm.put(dt.getIamstringcolumn(), new GhostFieldMapObject("test", GhostFieldMapObject.GhostFieldMapObjectTypes.String));
		
		GhostQuery gq = null;
		
			gq = GhostQueryConstructor.updateQuery(dt, bfm);
			logger.debug(GhostVariableWrapper.wrapVariable(gq.getQuery()));
		
			if(gq!=null){
				assertTrue(gq.getQuery().equals(" UPDATE AROHATGI.DUMMY SET UIDCOLUMN = 123,IAMNUMBERCOLUMN = 321,IAMSTRINGCOLUMN = 'test' WHERE 1=1 "));
				gq.close();
			}
		
		bfm.clear();
		bfm.put(1,dt.getUidcolumn(), 123);
		bfm.put(2,dt.getIamnumbercolumn(),321);
//		bfm.put(dt.getIamnumbercolumn(), new GhostFieldMapObject(132, GhostFieldMapObject.GhostFieldMapObjectTypes.Number));
		
		
			gq = GhostQueryConstructor.updateQuery(dt, bfm);
		
			if(gq!=null){
				logger.debug(GhostVariableWrapper.wrapVariable(gq.getQuery()));
				assertTrue(gq.getQuery().equals(" UPDATE AROHATGI.DUMMY SET UIDCOLUMN = 123,IAMNUMBERCOLUMN = 321 WHERE 1=1 "));
				gq.close();
			}
	}
	
	public void testGQCDelete(){
		DummyTable dt = MetaTables.DUMMYTABLE;
        GhostTableFilter tf = new GhostTableFilter();
        GhostQuery gq = null;
        
        
			tf.addAndField(0,GhostTableFilter.FilterOperationTypes.EQUALS, dt.getIamnumbercolumn(), 27);
		        
		gq = GhostQueryConstructor.deleteQuery(dt, tf);
		
		if(gq!=null){
				gq.close();
		}
		
		logger.debug(GhostVariableWrapper.wrapVariable(gq.getQuery()));
		assertTrue(gq.getQuery().equals("DELETE FROM AROHATGI.DUMMY WHERE 1=1  AND DUMMY.IAMNUMBERCOLUMN = 27"));
	}
	
	public void testGhostQueryBindVariable(){
		
			GDate d = new GDate(12,19,2011,13,28,22);
			
			List<IMetaField> selectFieldList = new ArrayList<IMetaField>();
			selectFieldList.add(MetaTables.DUMMYTABLE.getIamnumbercolumn());
			
			//Ordering should not matter
			GhostTableFilter tf = new GhostTableFilter();
	//		tf.addAndBindField(filterOperationTypes.equals,MetaTables.IDRSTAGING.getSegmentindex(),0);
			
			tf.addAndBindField(0,FilterOperationTypes.EQUALS,MetaTables.DUMMYTABLE.getIamdatecolumn(),d);
//			tf.addAndBindField(1,FilterOperationTypes.EQUALS,MetaTables.DUMMYTABLE.getIamstringcolumn(),"a");
			
//			GhostFieldMapObject t = new GhostFieldMapObject(null);
			
			//Ordering should not matter
	//		GhostBindFieldMap gbfm = new GhostBindFieldMap();
	//		gbfm.set(MetaTables.IDRSTAGING.getMethod(), "AMC");
	////		gbfm.set(MetaTables.IDRSTAGING.getSegmentindex(), 0);		
	//		gbfm.set(MetaTables.IDRSTAGING.getOperatingdate(), d);
			
			GhostQuery gq = GhostQueryConstructor.selectQuery(GhostQueryConstructor.IS_NOT_DISTINCT, selectFieldList, MetaTables.DUMMYTABLE, tf);
			gq.executeQuery();
			if(gq.next()){
	//			assertEquals(new BigDecimal(0),gq.getColumnByName(MetaTables.IDRSTAGING.getSegmentindex()));
				logger.debug("GhosQueryBindVariable columnValue: " + GhostVariableWrapper.wrapVariable(gq.getNumberColumnByName(MetaTables.DUMMYTABLE.getIamnumbercolumn())));
			}else{
				fail();
			}
			gq.close();
	}
	
	public void testGhostQueryInClause(){
		DummyTable dt = MetaTables.DUMMYTABLE;
		
        GhostTableFilter tf = new GhostTableFilter();
        
        //Testing Number
        GhostQuery gq = null;
        GhostListNumber ghostListNumber = new GhostListNumber();
        ghostListNumber.add(1);
        ghostListNumber.add(9);
        ghostListNumber.add(3);
        tf.addAndInField(0,dt.getIamnumbercolumn(), ghostListNumber);        
		gq = GhostQueryConstructor.deleteQuery(dt, tf);
		logger.debug("GhostQueryInClause query: " + GhostVariableWrapper.wrapVariable(gq.getQuery()));
		assertTrue(gq.getQuery().equals("DELETE FROM AROHATGI.DUMMY WHERE 1=1  AND DUMMY.IAMNUMBERCOLUMN IN (1,9,3)"));
		
		//Testing Strings
        GhostListString ghostList = new GhostListString();
        ghostList.add("sdsa");
        ghostList.add("asfasd");
        ghostList.add("3");
        
        tf.removeField(0);
        tf.addAndInField(1,dt.getIamnumbercolumn(), ghostList);        
		gq = GhostQueryConstructor.deleteQuery(dt, tf);
		logger.debug("GhostQueryInClause query: " + GhostVariableWrapper.wrapVariable(gq.getQuery()));
		assertTrue(gq.getQuery().equals("DELETE FROM AROHATGI.DUMMY WHERE 1=1  AND DUMMY.IAMNUMBERCOLUMN IN ('sdsa','asfasd','3')"));
		
	}
	
	public void testGhostQueryBetweenClause(){
		
		DummyTable dt = MetaTables.DUMMYTABLE;
		
		GDate starttime = new GDate(1,13,2010,23,59,59);
		GDate stoptime = new GDate(1,15,2010,23,59,59);
		
        GhostTableFilter tf = new GhostTableFilter();
        
		tf.addAndBetweenField(1,dt.getIamnumbercolumn(), 1, 5);
		
        //Testing Number
        GhostQuery gq = null;
		gq = GhostQueryConstructor.deleteQuery(dt, tf);
		logger.debug("GhostQueryInClause query: " + GhostVariableWrapper.wrapVariable(gq.getQuery()));
		assertTrue(gq.getQuery().equals("DELETE FROM AROHATGI.DUMMY WHERE 1=1  AND DUMMY.IAMNUMBERCOLUMN BETWEEN 1 AND 5"));
		
		
		tf.addAndBetweenField(1,dt.getIamnumbercolumn(), "A", "B");
		gq = GhostQueryConstructor.deleteQuery(dt, tf);
		logger.debug("GhostQueryInClause query: " + GhostVariableWrapper.wrapVariable(gq.getQuery()));
		assertTrue(gq.getQuery().equals("DELETE FROM AROHATGI.DUMMY WHERE 1=1  AND DUMMY.IAMNUMBERCOLUMN BETWEEN 'A' AND 'B'"));
		
		tf.addAndBetweenField(1,dt.getIamnumbercolumn(), dt.getUidcolumn(), dt.getIamnumbercolumn());
		gq = GhostQueryConstructor.deleteQuery(dt, tf);
		logger.debug("GhostQueryInClause query: " + GhostVariableWrapper.wrapVariable(gq.getQuery()));
		assertTrue(gq.getQuery().equals("DELETE FROM AROHATGI.DUMMY WHERE 1=1  AND DUMMY.IAMNUMBERCOLUMN BETWEEN DUMMY.UIDCOLUMN AND DUMMY.IAMNUMBERCOLUMN"));
		
		tf.addAndBetweenField(1,dt.getIamnumbercolumn(), starttime, stoptime);
		gq = GhostQueryConstructor.deleteQuery(dt, tf);
		logger.debug("GhostQueryInClause query: " + GhostVariableWrapper.wrapVariable(gq.getQuery()));
		assertTrue(gq.getQuery().equals("DELETE FROM AROHATGI.DUMMY WHERE 1=1  AND DUMMY.IAMNUMBERCOLUMN BETWEEN TO_DATE('01/13/2010 11:59:59 PM','MM/DD/YYYY HH:MI:SS AM') AND TO_DATE('01/15/2010 11:59:59 PM','MM/DD/YYYY HH:MI:SS AM')"));
		
	}
	
	public void testGhostQueryBetweenBindClause(){
		
			DummyTable dt = MetaTables.DUMMYTABLE;
			
	//		GDate starttime = new GDate(2010-1900,0,13,23,59,59);
	//		GDate stoptime = new GDate(2010-1900,0,15,23,59,59);
			
	        GhostTableFilter tf = new GhostTableFilter();
	        
				tf.addAndBetweenBindField(1,dt.getIamnumbercolumn(),1,3);
			
	        
	//         GhostBindFieldMap gbfm = new GhostBindFieldMap();
	//		gbfm.set(dt.getIamnumbercolumn(), 1);
	//		gbfm.set(dt.getIamnumbercolumn(), 2);
	        
	        //Testing Number
	        GhostQuery gq = null;
			gq = GhostQueryConstructor.deleteQuery(dt, tf);
			logger.debug("GhostQueryInClause query: " + GhostVariableWrapper.wrapVariable(gq.getQuery()));
			assertTrue(gq.getQuery().equals("DELETE FROM AROHATGI.DUMMY WHERE 1=1  AND DUMMY.IAMNUMBERCOLUMN BETWEEN ? AND ?"));		
	}
	
	public void testGhostQueryFunctions(){
		DummyTable dt = MetaTables.DUMMYTABLE;
		
		List<IMetaField> selectFieldList = new ArrayList<IMetaField>();
		try {
			selectFieldList.add(dt.getIamnumbercolumn().getMaxField());
		} catch (GhostDBRestrictionException e) {
			logger.error(e.getMessage(),e);
		}
		
        GhostQuery gq;
			gq = GhostQueryConstructor.selectQuery(GhostQueryConstructor.IS_NOT_DISTINCT, selectFieldList, dt);
			logger.debug("GhostQueryFunctions query: " + GhostVariableWrapper.wrapVariable(gq.getQuery()));
	        assertTrue(gq.getQuery().equals("SELECT   MAX(DUMMY.IAMNUMBERCOLUMN) max_DUMMYIAMNUMBERCOLUMN FROM AROHATGI.DUMMY DUMMY WHERE 1=1 "));
		
	}
	
	
	public void testGhostQueryMultipleSameTable(){
		DummyTable dt = MetaTables.DUMMYTABLE;
		DummyTable dt2;
		
			dt2 = new DummyTable("1");
			List<IMetaField> selectFieldList = new ArrayList<IMetaField>();
	//		selectFieldList.add(dt.getMaxField(dt.getIamnumbercolumn()));
	//		selectFieldList.add(dt.getMaxField(dt2.getIamstringcolumn()));
			selectFieldList.add(dt.getIamnumbercolumn());
			selectFieldList.add(dt2.getIamnumbercolumn());
			
			List<IMetaTable> tables = new ArrayList<IMetaTable>();
			tables.add(dt);
			tables.add(dt2);
			
	        GhostQuery gq;
				gq = GhostQueryConstructor.selectQuery(GhostQueryConstructor.IS_NOT_DISTINCT, selectFieldList, tables);
				logger.debug("QueryMultipleSameTable query: " + GhostVariableWrapper.wrapVariable(gq.getQuery()));
		        assertTrue(gq.getQuery().equals("SELECT  DUMMY.IAMNUMBERCOLUMN IAMNUMBERCOLUMN,DUMMY1.IAMNUMBERCOLUMN IAMNUMBERCOLUMN1 FROM AROHATGI.DUMMY DUMMY,AROHATGI.DUMMY DUMMY1 WHERE 1=1 "));
			
	        
		
	}
	
	public void testGQCSubTable(){
		DummyTable dt = MetaTables.DUMMYTABLE;
		    GhostQuery gq;
			gq = GhostQueryConstructor.selectAllQuery(dt);
		
			GhostQueryTable gqt;
			try {
				gqt = new GhostQueryTable(gq,"test");
			
				logger.debug(GhostVariableWrapper.wrapVariable(gqt.getAlias()));
				logger.debug(GhostVariableWrapper.wrapVariable(gqt.getTableName()));
				
				List <IMetaField> l = gqt.getAllColumns();
				for(IMetaField i: l){
					logger.debug(GhostVariableWrapper.wrapVariable(i.getColumnName() + " " + i.getAlias()));
				}
				
				logger.debug(GhostVariableWrapper.wrapVariable(gqt.getField(dt.getIamnumbercolumn()).getAlias()));
				
				
				List<IMetaField> selectFieldList = new ArrayList<IMetaField>();
				selectFieldList.add(dt.getUidcolumn());
				selectFieldList.add(gqt.getField(dt.getIamnumbercolumn()));
				
				List<IMetaTable> tables = new ArrayList<IMetaTable>();
				tables.add(dt);
				tables.add(gqt);
				
				GhostQuery testSubTable;
					testSubTable = GhostQueryConstructor.selectQuery(false, selectFieldList, tables);
					logger.debug(GhostVariableWrapper.wrapVariable(testSubTable.getQuery()) );                       
					assertTrue(testSubTable.getQuery().equalsIgnoreCase("SELECT  DUMMY.UIDCOLUMN UIDCOLUMN,test.IAMNUMBERCOLUMN testIAMNUMBERCOLUMN FROM AROHATGI.DUMMY DUMMY,(SELECT  DUMMY.IAMNUMBERCOLUMN IAMNUMBERCOLUMN,DUMMY.UIDCOLUMN UIDCOLUMN,DUMMY.IAMSTRINGCOLUMN IAMSTRINGCOLUMN,DUMMY.IAMDATECOLUMN IAMDATECOLUMN,DUMMY.STOPTIME STOPTIME,DUMMY.STARTTIME STARTTIME,DUMMY.SPI SPI,DUMMY.MIN MIN,DUMMY.MAX MAX,DUMMY.INTERVALCOUNT INTERVALCOUNT,DUMMY.IAMBLOBCOLUMN IAMBLOBCOLUMN,DUMMY.TOTAL TOTAL FROM AROHATGI.DUMMY DUMMY WHERE 1=1 ) test WHERE 1=1 "));
					                                                    
					                                          
				
			} catch (GhostDBRestrictionException e) {
				logger.error(e.getMessage(),e);
				fail();
			}
		
	}
	
	public void testGQCInClauseSubTable(){
		DummyTable dt = MetaTables.DUMMYTABLE;
		
		List<IMetaField> selectFieldList = new ArrayList<IMetaField>();
		selectFieldList.add(dt.getIamnumbercolumn());
		
		GhostQuery gq;
		
			gq = GhostQueryConstructor.selectQuery(GhostQueryConstructor.IS_NOT_DISTINCT, selectFieldList, dt);
		
			GhostTableFilter tf = new GhostTableFilter();
			
			GhostQueryTable gqt;
				gqt = new GhostQueryTable(gq,"test");
			
				List<IMetaField> selectFieldList2 = new ArrayList<IMetaField>();
				selectFieldList2.add(dt.getUidcolumn());
				
	//			tf.addAndInField(0, dt.getDate(), gqt);
				tf.addAndInField(0, dt.getIamnumbercolumn(), gqt);
				
				List<IMetaTable> tables = new ArrayList<IMetaTable>();
				tables.add(dt);			
				
				GhostQuery testSubTable = GhostQueryConstructor.selectQuery(false, selectFieldList2, tables, tf);
				assertTrue(testSubTable.getQuery().equals("SELECT  DUMMY.UIDCOLUMN UIDCOLUMN FROM AROHATGI.DUMMY DUMMY WHERE 1=1  AND DUMMY.IAMNUMBERCOLUMN IN (SELECT  DUMMY.IAMNUMBERCOLUMN IAMNUMBERCOLUMN FROM AROHATGI.DUMMY DUMMY WHERE 1=1 )"));
		
		
	}
	
	public void testGQCSelectMultipleSameColumnFilter(){
		DummyTable dt = MetaTables.DUMMYTABLE;
        GhostTableFilter tf = new GhostTableFilter();
        GhostQuery gq = null;
        
        List<IMetaField> selectFieldList = new ArrayList<IMetaField>();
        selectFieldList.add(dt.getIamnumbercolumn());
        
        
			tf.addAndField(0,GhostTableFilter.FilterOperationTypes.EQUALS, dt.getIamnumbercolumn(), 27);
			tf.addAndField(1,GhostTableFilter.FilterOperationTypes.GREATERTHAN, dt.getIamnumbercolumn(), 3);
		        
			gq = GhostQueryConstructor.selectQuery(GhostQueryConstructor.IS_NOT_DISTINCT, selectFieldList, dt, tf);
			
		
		if(gq!=null){
			    logger.debug(GhostVariableWrapper.wrapVariable(gq.getQuery()));
			    assertTrue(gq.getQuery().equals("SELECT  DUMMY.IAMNUMBERCOLUMN IAMNUMBERCOLUMN FROM AROHATGI.DUMMY DUMMY WHERE 1=1  AND DUMMY.IAMNUMBERCOLUMN = 27 AND DUMMY.IAMNUMBERCOLUMN > 3"));
				gq.close();
		}
		
	}
	
	
	public void testGQCGrouping() {
		DummyTable dt = MetaTables.DUMMYTABLE;
        GhostTableFilter tf = new GhostTableFilter();
        GhostQuery gq = null;
        
        List<IMetaField> selectFieldList = new ArrayList<IMetaField>();
        selectFieldList.add(dt.getIamnumbercolumn());
        
        
        	tf.addOrField(0,GhostTableFilter.FilterOperationTypes.GREATERTHAN, dt.getIamdatecolumn(), GhostDBStaticVariables.DB_CURRENT_TIME);
        	tf.addAndField(1,GhostTableFilter.FilterOperationTypes.LESSTHAN, dt.getIamnumbercolumn(), 27);
        	tf.addOrField(2,GhostTableFilter.FilterOperationTypes.GREATERTHAN, dt.getIamnumbercolumn(), 7);
			
        	tf.groupFilterCriteriaAnd(1,2);
		        
			gq = GhostQueryConstructor.selectQuery(GhostQueryConstructor.IS_NOT_DISTINCT, selectFieldList, dt, tf);
		
		
		if(gq!=null){
		   logger.debug(GhostVariableWrapper.wrapVariable(gq.getQuery()));
		   assertTrue(gq.getQuery().equals("SELECT  DUMMY.IAMNUMBERCOLUMN IAMNUMBERCOLUMN FROM AROHATGI.DUMMY DUMMY WHERE 1=1  OR DUMMY.IAMDATECOLUMN > SYSDATE AND ( DUMMY.IAMNUMBERCOLUMN < 27 OR DUMMY.IAMNUMBERCOLUMN > 7)"));
		   gq.close();
		}
		
		tf.removeField(1);
		tf.removeField(2);		
		
		tf.addOrField(1,GhostTableFilter.FilterOperationTypes.GREATERTHAN, dt.getIamnumbercolumn(), 7);
		tf.addAndField(2,GhostTableFilter.FilterOperationTypes.LESSTHAN, dt.getIamnumbercolumn(), 27);
			try{
        	tf.groupFilterCriteriaAnd(1,2);        	
        	fail();
			}catch(GhostQueryBuilderException e){
				
			}
			//tf.removeGroupFilterCriteria(1, 2);	        
			gq = GhostQueryConstructor.selectQuery(GhostQueryConstructor.IS_NOT_DISTINCT, selectFieldList, dt, tf);
		
		
		if(gq!=null){
			assertTrue(gq.getQuery().equals("SELECT  DUMMY.IAMNUMBERCOLUMN IAMNUMBERCOLUMN FROM AROHATGI.DUMMY DUMMY WHERE 1=1  OR DUMMY.IAMDATECOLUMN > SYSDATE OR DUMMY.IAMNUMBERCOLUMN > 7 AND DUMMY.IAMNUMBERCOLUMN < 27"));
			gq.close();
		}
		
		
	}
	
	public void testGQCBetweenDates(){
		GDate d = new GDate();
		
		DummyTable dt = MetaTables.DUMMYTABLE;
        GhostTableFilter tf = new GhostTableFilter();
        GhostQuery gq = null;
        List<IMetaField> selectFieldList = new ArrayList<IMetaField>();
        
            selectFieldList.add(dt.getIamnumbercolumn().getNVLField(dt. getIamnumbercolumn()));
			tf.addAndBetweenDatesField(0, dt.getIamdatecolumn(), dt.getIamdatecolumn(), d);
		
			gq = GhostQueryConstructor.selectQuery(GhostQueryConstructor.IS_NOT_DISTINCT, selectFieldList, dt, tf);
		
		
		if(gq!=null){
			    logger.debug(GhostVariableWrapper.wrapVariable(gq.getQuery()));
				gq.close();
		}
		
		//assertTrue(gq.getQuery().equals("SELECT  DUMMY.IAMNUMBERCOLUMN IAMNUMBERCOLUMN FROM AROHATGI.DUMMY DUMMY WHERE 1=1  AND DUMMY.IAMNUMBERCOLUMN = 27"));
	}
	
	
	public void testGQCSelectWithModififedDateField(){
		DummyTable dt = MetaTables.DUMMYTABLE;
        GhostTableFilter tf = new GhostTableFilter();
        GhostQuery gq = null;
        
        List<IMetaField> selectFieldList = new ArrayList<IMetaField>();
        selectFieldList.add(dt.getIamnumbercolumn());
        
        	tf.addAndField(0,GhostTableFilter.FilterOperationTypes.EQUALS, dt.getIamdatecolumn(), dt.getIamdatecolumn().getModifiedDateField(3600));
			tf.addAndField(1,GhostTableFilter.FilterOperationTypes.EQUALS, dt.getIamdatecolumn(), dt.getIamdatecolumn().getModifiedDateFieldPlus235959() );
		
        
            try{
	        	tf.addAndField(2,GhostTableFilter.FilterOperationTypes.EQUALS, dt.getIamdatecolumn(), dt.getIamnumbercolumn().getModifiedDateField(3600));
	        	fail();
            }catch(GhostQueryBuilderException e){
            	
            }
		  
            try{
	        	tf.addAndField(2,GhostTableFilter.FilterOperationTypes.EQUALS, dt.getIamdatecolumn(), dt.getIamnumbercolumn().getModifiedDateFieldPlus235959());
	        	fail();
		    }catch(GhostQueryBuilderException e){
		    	
		    }
        
			gq = GhostQueryConstructor.selectQuery(GhostQueryConstructor.IS_NOT_DISTINCT, selectFieldList, dt, tf);
		
		
		if(gq!=null){
			logger.debug(GhostVariableWrapper.wrapVariable(gq.getQuery()));
			assertTrue(gq.getQuery().equals("SELECT  DUMMY.IAMNUMBERCOLUMN IAMNUMBERCOLUMN FROM AROHATGI.DUMMY DUMMY WHERE 1=1  AND DUMMY.IAMDATECOLUMN =  DUMMY.IAMDATECOLUMN + ((1/86400) * 3600) AND DUMMY.IAMDATECOLUMN =  DUMMY.IAMDATECOLUMN + (86399/86400)"));
			gq.close();				
		}
		
		
	}
	
	
//	public void testGQCDecode(){
//		DummyTable dt = MetaTables.DUMMYTABLE;
//        GhostQuery gq = null;
//        
//        List<IMetaField> selectFieldList = new ArrayList<IMetaField>();
//        selectFieldList.add(dt.getIamnumbercolumn());
//        selectFieldList.add(dt.getIamstringcolumn());
//                
//		gq = GhostQueryConstructor.selectQuery(GhostQueryConstructor.IS_NOT_DISTINCT, dt, selectFieldList);
//		BigDecimal con = new BigDecimal(2);
//		BigDecimal n = null;
//		int x=3;
//		String s = "";
//		
//		gq.executeQuery();
//		try {
//			while(gq.next()){
//				n = gq.getIamnumbercolumnColumnByName(dt.getIamnumbercolumn());
//				s = gq.getIamstringcolumnColumnByName(dt.getIamstringcolumn());
////				if(s.equalsIgnoreCase("a")){
////					n = n.multiply(con);
////					x = x*2;
////				}
////				logger.info(n + " " + s);
//			}
//		} catch (GhostQueryBuilderException e) {
//			logger.error(e.getMessage(),e);
//			fail();
//		}
//	
//		if(gq!=null){
//				gq.close();
//		}
//	}
	
}
