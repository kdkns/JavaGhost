package com.java.ghost.GhostAttributes;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.java.ghost.utils.GhostDBStaticVariables;


public class AbstractGhostAttributeSet implements IGhostAttributeSet {
	
	private Set<GhostAttributeEnum> _getGhostChildAttributeSet = new HashSet<GhostAttributeEnum>();
	private Set<IGhostCustomAttribute> _getGhostCustomAttributeSet = new HashSet<IGhostCustomAttribute>();
	private Set<GhostAttributeEnum> _getGhostParentAttributeSet = new HashSet<GhostAttributeEnum>();
	
	
	protected Set<GhostAttributeEnum> getGhostChildAttributeSet(){
		return _getGhostChildAttributeSet;
	}
	
	protected Set<IGhostCustomAttribute> getGhostCustomAttributeSet(){
		return _getGhostCustomAttributeSet;
	}
	
	protected Set<GhostAttributeEnum> getGhostParentAttributeSet(){
		return _getGhostParentAttributeSet;
	}
	
	
	/* (non-Javadoc)
	 * @see com.ercot.java.ghost.GhostAttributes.IGhostAttributeSet#removeAttribute(com.ercot.java.ghost.GhostAttributes.GhostAttributeEnum)
	 */
	@Override
	public boolean removeAttribute(GhostAttributeEnum gae){
		return getGhostChildAttributeSet().remove(gae);
	}

	/* (non-Javadoc)
	 * @see com.ercot.java.ghost.GhostAttributes.IGhostAttributeSet#removeParentAttribute(com.ercot.java.ghost.GhostAttributes.GhostAttributeEnum)
	 */
	@Override
	public boolean removeParentAttribute(GhostAttributeEnum gae){
		return getGhostParentAttributeSet().remove(gae);
	}
	
	/* (non-Javadoc)
	 * @see com.ercot.java.ghost.GhostAttributes.IGhostAttributeSet#removeCustomAttribute(com.ercot.java.ghost.GhostAttributes.GhostCustomAttributeEnum)
	 */
	@Override
	public boolean removeCustomAttribute(IGhostCustomAttribute gae){
		return getGhostCustomAttributeSet().remove(gae);
	}

	/* (non-Javadoc)
	 * @see com.ercot.java.ghost.GhostAttributes.IGhostAttributeSet#getAttributes()
	 */
	@Override
	public Collection<? extends GhostAttributeEnum> getAttributes() {
		return getGhostChildAttributeSet();
	}

	/* (non-Javadoc)
	 * @see com.ercot.java.ghost.GhostAttributes.IGhostAttributeSet#getParentAttributes()
	 */
	@Override
	public Collection<? extends GhostAttributeEnum> getParentAttributes() {
		return getGhostParentAttributeSet();
	}

	/* (non-Javadoc)
	 * @see com.ercot.java.ghost.GhostAttributes.IGhostAttributeSet#getCustomAttributes()
	 */
	@Override
	public Collection<? extends IGhostCustomAttribute> getCustomAttributes() {
		return getGhostCustomAttributeSet();
	}
	
	/* (non-Javadoc)
	 * @see com.ercot.java.ghost.GhostAttributes.IGhostAttributeSet#areAttriubtesEmpty()
	 */
	@Override
	public boolean areAttriubtesEmpty(){
		return getGhostChildAttributeSet().isEmpty(); 
	}
	
	/* (non-Javadoc)
	 * @see com.ercot.java.ghost.GhostAttributes.IGhostAttributeSet#areParentAttriubtesEmpty()
	 */
	@Override
	public boolean areParentAttriubtesEmpty(){
		return getGhostParentAttributeSet().isEmpty(); 
	}
	
	/* (non-Javadoc)
	 * @see com.ercot.java.ghost.GhostAttributes.IGhostAttributeSet#areCustomAttriubtesEmpty()
	 */
	@Override
	public boolean areCustomAttriubtesEmpty(){
		return getGhostCustomAttributeSet().isEmpty(); 
	}
	
	public static String printAttributes(Set<? extends IGhostAttribute> attributes){
		StringBuilder result = new StringBuilder();
		for(IGhostAttribute igae: attributes){
			result.append(GhostDBStaticVariables.COMMA).append(igae);
		}
		return result.substring(1);
		
	}
	
	public static String getConcactAttributes(Collection<? extends IGhostAttribute> attributes, String seperator){
		StringBuilder result = new StringBuilder();
		for(IGhostAttribute igae: attributes){
			result.append(seperator).append(igae);
		}
		return result.substring(1);	
	}
	
	public static String printAttributes(Collection<? extends IGhostAttribute> attributes){
		return getConcactAttributes(attributes,GhostDBStaticVariables.COMMA);		
	}
	
}
