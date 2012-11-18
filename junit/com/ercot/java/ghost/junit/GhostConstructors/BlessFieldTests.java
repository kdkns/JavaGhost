package com.ercot.java.ghost.junit.GhostConstructors;

import static com.ercot.java.ghost.junit.TestClasses.MetaTables.DUMMYTABLE;
import static com.ercot.java.ghost.junit.TestClasses.MetaTables.DUMMYTABLE2;
import static com.ercot.java.ghost.utils.GhostStaticVariables.BLESS;
import static com.ercot.java.ghost.utils.GhostStaticVariables.POSSESS;
import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

import com.ercot.java.ghost.Exceptions.GhostCollectionIsEmptyException;
import com.ercot.java.ghost.Exceptions.GhostCollectionIsNotSingleElementException;
import com.ercot.java.ghost.Exceptions.GhostFieldRequiredForInsertException;
import com.ercot.java.ghost.GhostAttributes.GhostAttributeEnum;
import com.ercot.java.ghost.GhostAttributes.GhostCustomAttributeEnum;
import com.ercot.java.ghost.GhostFieldMaps.GhostFieldMap;
import com.ercot.java.ghost.GhostFieldMaps.GhostFieldMapObject;
import com.ercot.java.ghost.GhostFieldMaps.GhostHeaderMapping;
import com.ercot.java.ghost.QueryConstructors.GhostQuery;
import com.ercot.java.ghost.QueryConstructors.GhostQueryConstructor;
import com.ercot.java.ghost.QueryConstructors.GhostTableFilter;
import com.ercot.java.ghost.Variable.GhostCollection;
import com.ercot.java.ghost.Variable.GhostNumber;

public class BlessFieldTests extends TestCase{
	
	private static Logger logger = Logger.getLogger("BlessFieldTests");
	
	public BlessFieldTests(String name) {
	    super(name);
	    PropertyConfigurator.configure("properties/log4j.properties");
	 }
	
	public void testBlessFieldMapConcatValues(){
		GhostFieldMap bfm = new GhostFieldMap();
		bfm.put(0,DUMMYTABLE.getTotal(), new GhostFieldMapObject(132, GhostFieldMapObject.GhostFieldMapObjectTypes.Number));
		String test = bfm.getConcatValues(",");
		assertTrue(test.equals("132"));
	}
	
	public void testBlessInsertFieldCheck(){
		
		GhostFieldMap bfm = new GhostFieldMap();
		bfm.put(0,DUMMYTABLE.getTotal(), new GhostFieldMapObject(132, GhostFieldMapObject.GhostFieldMapObjectTypes.Number));
		bfm.put(1,DUMMYTABLE.getIamstringcolumn(), new GhostFieldMapObject("test", GhostFieldMapObject.GhostFieldMapObjectTypes.String));
		bfm.put(2,DUMMYTABLE.getSpi(), new GhostFieldMapObject(3600, GhostFieldMapObject.GhostFieldMapObjectTypes.Number));
		try{
			@SuppressWarnings("unused")
			GhostQuery gq = GhostQueryConstructor.insertQuery(DUMMYTABLE, bfm);
			fail();
		}catch(GhostFieldRequiredForInsertException e){
			
		}
	}
	
	
	@SuppressWarnings("static-access")
	@Test
	public void testBlessRaptorBlob(){
		GhostTableFilter tf = new GhostTableFilter();
		tf.addAndRownumField(1,1);
		
		try {
 			
			GhostCollection<GhostNumber> result = new GhostCollection<GhostNumber>();
			POSSESS.possess(result,DUMMYTABLE, tf);
			result.makeIterable();
			
			GhostNumber rb = result.getSingleGhostVariable();
//			rb.setCustomAttribute(savechannelAttribute, "saverecorder");
			
//			BLESS.save(rb, DUMMYTABLE2, null, null, 1, savechannelAttribute);
			GhostHeaderMapping ghm = new GhostHeaderMapping();
			ghm.addMappingToColumn(GhostAttributeEnum.saverecorder, GhostCustomAttributeEnum.custom_1);
			ghm.addMappingToColumn(GhostAttributeEnum.savechannel, 1);
			
			BLESS.save(result, DUMMYTABLE2, ghm);
			
			result.releaseObjects();
			
		
		} catch (GhostCollectionIsNotSingleElementException e) {
			logger.error(e.getMessage(),e);
			fail();
		} catch (GhostCollectionIsEmptyException e) {
			logger.error(e.getMessage(),e);
			fail();
		}
	}
}
