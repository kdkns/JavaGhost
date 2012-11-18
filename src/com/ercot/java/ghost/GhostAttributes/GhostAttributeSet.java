package com.ercot.java.ghost.GhostAttributes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class GhostAttributeSet extends AbstractGhostAttributeSet {
	
	private Map<GhostAttributeEnum,IGhostAttribute> _mapping = new HashMap<GhostAttributeEnum, IGhostAttribute>();
	private Map<GhostAttributeEnum,IGhostAttribute> _parentMapping = new HashMap<GhostAttributeEnum, IGhostAttribute>();
	private Map<IGhostCustomAttribute,IGhostAttribute> _customMapping = new HashMap<IGhostCustomAttribute, IGhostAttribute>();

	
	protected Map<GhostAttributeEnum,IGhostAttribute> getMapping(){
		return _mapping;
	}
	
	protected Map<GhostAttributeEnum,IGhostAttribute> getParentMapping(){
		return _parentMapping;
	}
	
	protected Map<IGhostCustomAttribute,IGhostAttribute> getCustomMapping(){
		return _customMapping;
	}
	
	
	public void clear(){
		getMapping().clear();
		getParentMapping().clear();
		getCustomMapping().clear();		
		getGhostChildAttributeSet().clear();
		getGhostParentAttributeSet().clear();		
		getGhostCustomAttributeSet().clear();
	}
	
	public boolean addAttribute(GhostAttributeEnum gae){		
		return getGhostChildAttributeSet().add(gae);
	}

	public boolean addParentAttribute(GhostAttributeEnum gae){
		return getGhostParentAttributeSet().add(gae);
	}
	
	public boolean addCustomAttribute(IGhostCustomAttribute gae){
		return getGhostCustomAttributeSet().add(gae);
	}
	
	public boolean addAttribute(GhostAttributeEnum gae, IGhostAttribute mappedTogae){
		getMapping().put(gae,mappedTogae);
		return getGhostChildAttributeSet().add(gae);
	}

	public boolean addParentAttribute(GhostAttributeEnum gae, IGhostAttribute mappedTogae){
		getParentMapping().put(gae,mappedTogae);
		return getGhostParentAttributeSet().add(gae);
	}
	
	public boolean addCustomAttribute(IGhostCustomAttribute gae, IGhostAttribute mappedTogae){
		getCustomMapping().put(gae,mappedTogae);
		return getGhostCustomAttributeSet().add(gae);
	}
	

	public boolean removeAttribute(GhostAttributeEnum gae){
		getMapping().remove(gae);
		return getGhostChildAttributeSet().remove(gae);
	}

	public boolean removeParentAttribute(GhostAttributeEnum gae){
		getParentMapping().remove(gae);
		return getGhostParentAttributeSet().remove(gae);
	}
	
	public boolean removeCustomAttribute(IGhostCustomAttribute gae){
		getCustomMapping().remove(gae);
		return getGhostCustomAttributeSet().remove(gae);
	}

	public IGhostAttribute getMappedAttribute(GhostAttributeEnum atr) {
		return getMapping().get(atr);
	}	
	
	public IGhostAttribute getMappedParentAttribute(GhostAttributeEnum atr) {
		return getParentMapping().get(atr);
	}
	
	public IGhostAttribute getMappedAttribute(IGhostCustomAttribute atr) {
		return getCustomMapping().get(atr);
	}

	public Collection<IGhostAttribute> getCustomMappedAttributes() {
		return _customMapping.values();
	}
	
	public static String printAttributes(Set<IGhostAttribute> attributes){		
		return AbstractGhostAttributeSet.printAttributes(attributes);		
	}
	
	public static String printAttributes(Collection<IGhostAttribute> attributes){
		return AbstractGhostAttributeSet.printAttributes(attributes);
	}
}
