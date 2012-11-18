package com.java.ghost.junit.Possess;

import static com.java.ghost.junit.TestClasses.MetaTables.DUMMYTABLE;
import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.java.ghost.MetaTableTypes.MetaScalarTable;
import com.java.ghost.QueryConstructors.GhostTableFilter;
import com.java.ghost.Variable.GhostBlob;
import com.java.ghost.Variable.GhostCollection;
import com.java.ghost.Variable.GhostNumber;
import com.java.ghost.Variable.GhostString;
import com.java.ghost.utils.GhostStaticVariables;
import com.java.ghost.utils.GhostVariableWrapper;

public class PossessTests extends TestCase {
	private static Logger logger = Logger.getLogger("PossessTests");
	
	public PossessTests(String name) {
	    super(name);
	    PropertyConfigurator.configure("properties/log4j.properties");
	 }
	
	
	public void testPossesBlob(){
		GhostTableFilter tf = new GhostTableFilter();
		tf.addAndRownumField(1,10);
		GhostCollection<GhostBlob> result = new GhostCollection<GhostBlob>();
			
		//MetaScalarTable msst = new MetaScalarTable(DUMMYTABLE,DUMMYTABLE.getIamblobcolumn());
			
		GhostStaticVariables.POSSESS.possess(result,DUMMYTABLE, tf);
			
		for(GhostBlob gmsn : result){
				logger.info(GhostVariableWrapper.wrapVariable(gmsn.size()) );
		}
			
	
	}
	
	
	public void testPossesNumber(){
		GhostTableFilter tf = new GhostTableFilter();
		tf.addAndRownumField(1,10);
		
			
			GhostCollection<GhostNumber> result = new GhostCollection<GhostNumber>();
			
			MetaScalarTable msst = new MetaScalarTable(DUMMYTABLE,DUMMYTABLE.getIamnumbercolumn());
			
			GhostStaticVariables.POSSESS.possess(result, msst, tf);
			
			for(GhostNumber gmsn : result){
					logger.info(GhostVariableWrapper.wrapVariable(gmsn.getValue()) );
			}
	}
	
	
	public void possesstring(){
		GhostTableFilter tf = new GhostTableFilter();
		tf.addAndRownumField(1,10);

			
			GhostCollection<GhostString> result = new GhostCollection<GhostString>();
			
			MetaScalarTable msst = new MetaScalarTable(DUMMYTABLE,DUMMYTABLE.getIamstringcolumn());
			
			GhostStaticVariables.POSSESS.possess(result, msst, tf);
			
			for(GhostString gmsn : result){
					logger.info(GhostVariableWrapper.wrapVariable(gmsn.getValue()) );
			}
			
	}
	
	
}
