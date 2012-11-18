/* $Id: GhostList.java,v 1.2 2012/04/23 17:49:01 mackermann Exp $
 * ==============================================================================
 * Copyright(c) 2001-2012 ERCOT Inc., All rights reserved.
 *
 * THIS PROGRAM IS AN UNPUBLISHED  WORK AND TRADE SECRET OF THE COPYRIGHT HOLDER,
 * AND DISTRIBUTED ONLY UNDER RESTRICTION.
 *
 * No  part  of  this  program  may be used,  installed,  displayed,  reproduced,
 * distributed or modified  without the express written consent  of the copyright
 * holder.
 *
 * EXCEPT AS EXPLICITLY STATED  IN A WRITTEN  AGREEMENT BETWEEN  THE PARTIES, THE
 * SOFTWARE IS PROVIDED AS-IS, WITHOUT WARRANTIES OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING THE IMPLIED WARRANTIES OF MERCHANTABILITY,  FITNESS FOR A PARTICULAR
 * PURPOSE, NONINFRINGEMENT, PERFORMANCE, AND QUALITY.
 *
 * [ERCOT CS7.6],[CIP-003 R4] - ERCOT Restricted
 * ==============================================================================
 */
package com.ercot.java.ghost.utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * An extension of the {@link java.util.ArrayList} collection object, GhostList implements the
 * standard {@link List} interface and adds two convenience methods for adding object elements (E)
 * in bulk instead of repeating the <code>add()</code> method for every object.  GhostList
 * implements all optional list operations, and permits all elements, including null.<br><br>
 * <b>Examples:</b><br>
 * To create a new GhostList object to contain a list of tables (IMetaTables):
 * <pre>
 * GhostList&lt;{@link IMetaTable}&gt; myTableList = new GhostList&lt;{@link IMetaTable}&gt;(QSE, BILLDETERMINANT, RESOURCENODAL);</pre>
 * To create an empty GhostList object and add field objects seperately:
 * <pre>
 * GhostList&lt;{@link IMetaField}&gt; myFieldList = new GhostList&lt;{@link IMetaField}&gt;();
 * myFieldList.addMany(QSE.getQseCode(), RESOURCENODAL.getResourceUID());
 * ...
 * myFieldList.add(BILLDETERMINANT.getBillDeterminantName());</pre>
 * <HR>
 * @since 1.0
 * @see {@link Collection}, {@link List}, {@link java.util.ArrayList}
 */
public class GhostList<E> extends ArrayList<E> {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a GhostList object with one or more elements of the defined type (E).<br><br>
	 * <b>Example:</b><br>
	 * To create a new GhostList object to contain a list of tables (IMetaTables):
	 * <pre>
	 * GhostList&lt;{@link IMetaTable}&gt; myTableList = new GhostList&lt;{@link IMetaTable}&gt;(QSE, BILLDETERMINANT, RESOURCENODAL);</pre>
	 * 
     * @param values - One or more object elements to be added, which match the defined element type.
     * @since 1.0
	 */
	public GhostList(E... values) {
		this.addAll(Arrays.asList(values));
	}
	
	/**
	 * Adds one or more object elements to the GhostList instance.<br><br>
     * <b>Example:</b><pre>
     * ...
     * GhostList&lt;{@link IMetaField}&gt; myFieldList = new GhostList&lt;{@link IMetaField}&gt;();
     * myFieldList.addMany(QSE.getQseCode(), RESOURCENODAL.getResourceUID());</pre>
	 * 
	 * @param values - One or more object elements to be added, which match the type defined by the constructor.
	 * @return boolean - True when object elements are successfully added to the list instance, false otherwise.
	 */
	public boolean addMany(E... values) {
		return this.addAll(Arrays.asList(values));
	}
	
	/**
	 * Removes one or more object elements from the GhostList instance.<br><br>
     * <b>Example:</b><pre>
     * ...
     * GhostList&lt;{@link IMetaTable}&gt; myTableList = new GhostList&lt;{@link IMetaTable}&gt;(QSE, BILLDETERMINANT, RESOURCENODAL);
     * ...
     * myTableList.removeMany(BILLDETERMINANT, RESOURCENODAL);</pre>
	 * 
     * @param values - One or more object elements matching the type defined by the constructor.
     * @return boolean - True when object elements are successfully removed from the list instance, false otherwise.
     * @since 1.0
	 */
	public boolean removeMany(E... values) {
		return this.removeAll(Arrays.asList(values));
	}

}
