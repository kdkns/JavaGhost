package com.ercot.java.ghost.junit.MetaField;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.ercot.java.ghost.MetaTableTypes.IMetaField;
import com.ercot.java.ghost.MetaTableTypes.Tables.MetaTables;
import com.ercot.java.ghost.utils.GhostDBStaticVariables;

public class MetaFieldTests {

	@Test
	public void testMetaField() {
		IMetaField imf = MetaTables.GHOST_VM.getGhostPointer();
		
		assertTrue(GhostDBStaticVariables.GHOST_VM_POINTER_COLUMN.equalsIgnoreCase(imf.getColumnName()));
		assertTrue(GhostDBStaticVariables.GHOST_VM_POINTER_COLUMN.equalsIgnoreCase(imf.getAlias()));
		
		assertTrue((GhostDBStaticVariables.GHOST_VM_POINTER_COLUMN+GhostDBStaticVariables.SPACE+GhostDBStaticVariables.GHOST_VM_POINTER_COLUMN).equalsIgnoreCase(imf.getColumnNameAndAlias()));
		
		assertTrue(MetaTables.GHOST_VM.equals(imf.getAssociatedTable()));
		assertTrue(MetaTables.GHOST_VM.getTableName().equalsIgnoreCase(imf.getAssociatedTableName()));
		assertTrue(MetaTables.GHOST_VM.getAlias().equalsIgnoreCase(imf.getAssociatedTableAlias()));
		
		assertTrue((MetaTables.GHOST_VM.getAlias()+GhostDBStaticVariables.PERIOD+GhostDBStaticVariables.GHOST_VM_POINTER_COLUMN).equalsIgnoreCase(imf.getFullyQualifiedTableAliaisWithColumnName()));
		assertTrue((MetaTables.GHOST_VM.getAlias()+GhostDBStaticVariables.PERIOD+GhostDBStaticVariables.GHOST_VM_POINTER_COLUMN).equalsIgnoreCase(imf.getFullyQualifiedTableAliasWithAliasName()));
		
		assertTrue((" SUM("+MetaTables.GHOST_VM.getAlias()+GhostDBStaticVariables.PERIOD+GhostDBStaticVariables.GHOST_VM_POINTER_COLUMN+")").equals(imf.getSumField().getColumnName()));
		String alias1 = imf.getSumField().getAlias();
		String alias2 = imf.getSumField().getAlias();
		
		assertFalse(alias1.equalsIgnoreCase(alias2));
		
	}

}
