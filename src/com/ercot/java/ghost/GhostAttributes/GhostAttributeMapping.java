package com.ercot.java.ghost.GhostAttributes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class GhostAttributeMapping extends AbstractGhostAttributeSet {
	
	private Map<GhostAttributeEnum,IGhostCustomAttribute> _mapping = new HashMap<GhostAttributeEnum, IGhostCustomAttribute>();
	private Map<GhostAttributeEnum,IGhostCustomAttribute> _parentMapping = new HashMap<GhostAttributeEnum, IGhostCustomAttribute>();
	private Map<IGhostCustomAttribute,IGhostCustomAttribute> _customMapping = new HashMap<IGhostCustomAttribute, IGhostCustomAttribute>();

	
	protected Map<GhostAttributeEnum,IGhostCustomAttribute> getMapping(){
		return _mapping;
	}
	
	protected Map<GhostAttributeEnum,IGhostCustomAttribute> getParentMapping(){
		return _parentMapping;
	}
	
	protected Map<IGhostCustomAttribute,IGhostCustomAttribute> getCustomMapping(){
		return _customMapping;
	}
	
	public boolean addAttribute(GhostAttributeEnum gae, IGhostCustomAttribute mappedTogae){
		_mapping.put(gae,mappedTogae);
		return getGhostChildAttributeSet().add(gae);
	}

	public boolean addParentAttribute(GhostAttributeEnum gae, IGhostCustomAttribute mappedTogae){
		_parentMapping.put(gae,mappedTogae);
		return getGhostParentAttributeSet().add(gae);
	}
	
	public boolean addCustomAttribute(IGhostCustomAttribute gae){
		_customMapping.put(gae,gae);
		return getGhostCustomAttributeSet().add(gae);
	}
	
	public boolean addCustomAttribute(IGhostCustomAttribute gae, IGhostCustomAttribute mappedTogae){
		_customMapping.put(gae,mappedTogae);
		return getGhostCustomAttributeSet().add(gae);
	}	

	public boolean removeAttribute(GhostAttributeEnum gae){
		_mapping.remove(gae);
		return getGhostChildAttributeSet().remove(gae);
	}

	public boolean removeParentAttribute(GhostAttributeEnum gae){
		_parentMapping.remove(gae);
		return getGhostParentAttributeSet().remove(gae);
	}
	
	public boolean removeCustomAttribute(IGhostCustomAttribute gae){
		_customMapping.remove(gae);
		return getGhostCustomAttributeSet().remove(gae);
	}

	public IGhostAttribute getMappedAttribute(GhostAttributeEnum atr) {
		return _mapping.get(atr);
	}	
	
	public IGhostAttribute getMappedParentAttribute(GhostAttributeEnum atr) {
		return _parentMapping.get(atr);
	}
	
	public IGhostAttribute getMappedAttribute(IGhostCustomAttribute atr) {
		return _customMapping.get(atr);
	}

	public Collection<IGhostCustomAttribute> getCustomMappedAttributes() {
		return _customMapping.values();
	}
	
	public Collection<IGhostCustomAttribute> getMappedAttributes() {
		List<IGhostCustomAttribute> result = new ArrayList<IGhostCustomAttribute>();
		result.addAll(_mapping.values());
		result.addAll(_parentMapping.values());
		result.addAll(_customMapping.values());
		//!! This order is dependent on what order the checkAttributes Function, aka what order the driver query generates the columns in!
		return result;
	}
	
	public static String printAttributes(Set<IGhostAttribute> attributes){		
		return AbstractGhostAttributeSet.printAttributes(attributes);		
	}
	
	public static String printAttributes(Collection<IGhostAttribute> attributes){
		return AbstractGhostAttributeSet.printAttributes(attributes);
	}
}
