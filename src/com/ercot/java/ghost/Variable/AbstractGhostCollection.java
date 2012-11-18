package com.ercot.java.ghost.Variable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import oracle.sql.NUMBER;

import org.apache.log4j.Logger;

import com.ercot.java.ghost.Annotations.PossessConstructor;
import com.ercot.java.ghost.DBObject.DBID;
import com.ercot.java.ghost.DBObject.IDBCollection;
import com.ercot.java.ghost.Exceptions.GhostCollectionIsEmptyException;
import com.ercot.java.ghost.Exceptions.GhostCollectionIsNotSingleElementException;
import com.ercot.java.ghost.Exceptions.GhostRuntimeException;
import com.ercot.java.ghost.GhostAttributes.IGhostCustomAttribute;
import com.ercot.java.ghost.MetaTableTypes.IMetaField;
import com.ercot.java.ghost.MetaTableTypes.IMetaGhostVariableTable;
import com.ercot.java.ghost.MetaTableTypes.Tables.MetaTables;
import com.ercot.java.ghost.QueryConstructors.GhostQueryInternal;
import com.ercot.java.ghost.utils.GhostDBStaticVariables;
import com.ercot.java.ghost.utils.IGDate;

public abstract class AbstractGhostCollection <collectionType extends IDBCollection<objectType>, objectType extends IGhostVariable<?,?>> implements IGhostCollection<objectType>{		
		private static Logger logger = Logger.getLogger("AbstractGhostCollection");
		private List<objectType> _ghostCollection;
		private collectionType _content; 
		private BigDecimal _newSize;
		private boolean _useNewSize = false;
		private boolean _isIterable = false;
		
		{
			_ghostCollection  = new ArrayList<objectType>();
//			_content = GhostDBCollectionFactory.getGhostCollection(this);
		}
		
		public AbstractGhostCollection(collectionType content) {
			super();
			_content = content;			
		}
		
		public AbstractGhostCollection(collectionType content, Collection<? extends objectType> arg0){
			this(content);
			this.addAll(arg0);
		}
		
		protected final Collection<objectType> getGhostCollection(){
			return _ghostCollection;
		}
		
		public final collectionType getContent(){
			return _content;
		}
		
		protected final NUMBER getCollectionIdOfObject(objectType o){
			return getContent().getCollectionIdFromDB((objectType)o);
		}
		
		
		public final NUMBER getLocalDBConvertedCollectionId(){
			return getContent().getDBConvertedCollectionId();
		}
		
		
		public void setSize(BigDecimal size) {
			_newSize = size;
			_useNewSize = true;			
		}
		
		public IMetaField getVMInsertColumn(){
			return getContent().getVMInsertColumn();
		}
		
		protected void setAllObjectsAsLocal(){
			if(isIterable()){				
				for(IGhostVariable<?,?> gmo : getGhostCollection() ){
					gmo.setIsEmpty(false);
					gmo.setIsRemoteTable(false);
				}
			}
		}
		
		
//		protected final int getCollectionId(){
//			return _content.getCollectionId();
//		}
			
		
		public final Long getCollectionId(){
			return getContent().getCollectionId();
	    }
		
		/* (non-Javadoc)
		 * @see com.ercot.java.ghost.Variable.IGhostCollection2#add(objectType)
		 */
		
		
//		public boolean add(objectType arg0) {
//			_content.updateCollectionId((IGhostVariable<?,?>) arg0);
//			return _ghostCollection.add(arg0);
//		}
//
//		/* (non-Javadoc)
//		 * @see com.ercot.java.ghost.Variable.IGhostCollection2#addAll(java.util.Collection)
//		 */
//		
//		
//		public boolean addAll(Collection<? extends objectType> arg0) {
//			_content.bulkupdateCollectionId(( (AbstractGhostCollection<?,?>)arg0).getLocalDBConvertedCollectionId());
//			return _ghostCollection.addAll(arg0);
//		}

		/* (non-Javadoc)
		 * @see com.ercot.java.ghost.Variable.IGhostCollection2#contains(java.lang.Object)
		 */
		
		
		
		public boolean isIterable() {
			return _isIterable;
		}

		protected void setIsIterable(boolean isIterable) {
			_isIterable = isIterable;
		}

		public final boolean contains(Object arg0) {		
			return getGhostCollection().contains(arg0);
		}

		/* (non-Javadoc)
		 * @see com.ercot.java.ghost.Variable.IGhostCollection2#containsAll(java.util.Collection)
		 */
		
		
		
		public final boolean containsAll(Collection<?> arg0) {
			return getGhostCollection().containsAll(arg0);
		}

		/* (non-Javadoc)
		 * @see com.ercot.java.ghost.Variable.IGhostCollection2#isEmpty()
		 */
		
		
		
		public final boolean isEmpty() {
			return getGhostCollection().isEmpty();
		}

		/* (non-Javadoc)
		 * @see com.ercot.java.ghost.Variable.IGhostCollection2#iterator()
		 */
		
		
		
		public final Iterator<objectType> iterator() {
			if(isIterable()){
				return getGhostCollection().iterator();
			}
			throw new GhostRuntimeException("Collection is not iteratable! Call correct makeIterable() first!");
		}

		/* (non-Javadoc)
		 * @see com.ercot.java.ghost.Variable.IGhostCollection2#remove(java.lang.Object)
		 */
		
		
//		public boolean remove(Object arg0) {
//			return _ghostCollection.remove(arg0);
//		}
//
//		/* (non-Javadoc)
//		 * @see com.ercot.java.ghost.Variable.IGhostCollection2#removeAll(java.util.Collection)
//		 */
//		
//		
//		public boolean removeAll(Collection<?> arg0) {
//			return _ghostCollection.removeAll(arg0);
//		}

		/* (non-Javadoc)
		 * @see com.ercot.java.ghost.Variable.IGhostCollection2#retainAll(java.util.Collection)
		 */
		
		
		
		public boolean retainAll(Collection<?> arg0) {
			return getGhostCollection().retainAll(arg0);
		}

		/* (non-Javadoc)
		 * @see com.ercot.java.ghost.Variable.IGhostCollection2#size()
		 */
		
		
		
		public final int size() {
			if(!_useNewSize){
				return getGhostCollection().size();
			}else{
				return Integer.valueOf(_newSize.toPlainString());
			}
		}

		/* (non-Javadoc)
		 * @see com.ercot.java.ghost.Variable.IGhostCollection2#toArray()
		 */
		
		
		
		public final Object[] toArray() {
			return getGhostCollection().toArray();
		}

		/* (non-Javadoc)
		 * @see com.ercot.java.ghost.Variable.IGhostCollection2#toArray(T[])
		 */
		
		 
		
		public final <T> T[] toArray(T[] arg0) {
			return getGhostCollection().toArray(arg0);
		}
		
		/* (non-Javadoc)
		 * @see com.ercot.java.ghost.Variable.IGhostCollection2#clear()
		 */
		
		
		
		public void clear() {
			getGhostCollection().clear();
			getContent().clear();
		}
		
		/* (non-Javadoc)
		 * @see com.ercot.java.ghost.Variable.IGhostCollection2#releaseObjects()
		 */
		
		
		public void releaseObjects(){
			if(isIterable()){
				for(objectType go: getGhostCollection()){
		 	    	go.clear();
		 	    }
			}else{
				getContent().deleteAllObjects();
				
			}
			this.clear();
		}
				
		
		public void removeAllObjects(){
			getContent().removeAllObjects();
		}
		
		@SuppressWarnings("unchecked")
		protected objectType getObject(int position) {//TODO: Make this more effecient?			
			if( ( !isIterable() || (position < 0) || getGhostCollection().isEmpty() || (position > getGhostCollection().size()) )){
				throw new GhostRuntimeException("No object exists at positon: " + position);
			}
			return (objectType) getGhostCollection().toArray()[position];
		}
		
		/* (non-Javadoc)
		 * @see com.ercot.java.ghost.Variable.IGhostCollection2#setCustomAttribute(com.ercot.java.ghost.GhostAttributes.GhostCustomAttributeEnum, java.lang.String)
		 */
		
		
		public void setCustomAttribute(IGhostCustomAttribute attribute, String value) {
		    getContent().setCustomAttribute(attribute, value);
		}
		
		public void setCustomAttribute(IGhostCustomAttribute attribute, IGDate value) {
		    getContent().setCustomAttribute(attribute, value);
		}
		
		
		public String getSQLQuery(){
			return getContent().getSQLQuery();
		}
		
		
//		public int getCollectionId(){
//			return getContent().getCollectionId();
//		}
		
		
		
		public boolean add(objectType arg0){
			boolean result = false;
			setSize(new BigDecimal(size()+1));
			if(!arg0.isEmpty()){
				getContent().addCollectionId((objectType) arg0);
				result = getGhostCollection().add(arg0);
//				logger.debug("GhostMetaBlobInfo added to GhostCollection");
//				logger.debug("Collection ID: " + GhostVariableWrapper.wrapVariable(getContent().getCurrentCollectionId()));
				getContent().addGhostMetaInfo(arg0);
			}else{				
				throw new GhostRuntimeException("GhostMetaVariable object being added has not been set with Meta Information!");				
			}			
			return result;
		}

		
		
		public boolean addAll(Collection<? extends objectType> arg0) {
			boolean result = false;
			AbstractGhostCollection<?,?> abc = ( (AbstractGhostCollection<?,?>) arg0);
			setSize(new BigDecimal(size() + arg0.size()));
			result = getGhostCollection().addAll(arg0);
			getContent().addBulkCollectionId(abc.getLocalDBConvertedCollectionId());
			
//			if(isIterable()){
//				for(objectType i: getGhostCollection()){
//					getContent().addGhostMetaInfo(i);
//				}
//			}else{
				getContent().mergeMetaInfo(abc.getContent());
//			}
			
			return result;
		}

		
		
		@SuppressWarnings("unchecked")
		public boolean remove(Object arg0) {
			getContent().removeGhostMetaInfo((objectType)arg0);
			getContent().removeCollectionId((objectType) arg0);
			return getGhostCollection().remove(arg0);
		}

		
		
		public boolean removeAll(Collection<?> arg0) {
			getContent().removeAllGhostMetaBlobInfo(arg0);
			getContent().removeBulkCollectionId();
			return getGhostCollection().removeAll(arg0);
		}

		
		
		public objectType getGhostVariable(int position){
			return this.getObject(position);
		}
		
		
		public objectType getSingleGhostVariable() throws GhostCollectionIsNotSingleElementException, GhostCollectionIsEmptyException{
			if(isIterable()){
				int size = size();
				
				if(size == 1){
						return this.getObject(0);				
				}else if(size==0){
					throw new GhostCollectionIsEmptyException();	
				}
			throw new GhostCollectionIsNotSingleElementException();
			}else{
				throw new GhostRuntimeException("Collection is not iteratable! Call correct makeIterable() first!");
			}
		}
		
//		
//		public boolean bulkAdd(objectType ghostVariable) {
////			boolean result = false;
////			result = getGhostCollection().add(ghostVariable);
////			//getContent().addGhostMetaBlobInfo(GhostMetaScalarNumber);
////			return result;
//			return getGhostCollection().add(ghostVariable);
//		}
		
		public void makeIterable(){
			if(!isIterable()){
				//Wipe out any previously added objects before it was made iterable
				getGhostCollection().clear();
				@SuppressWarnings("unchecked")
				Class<? extends IGhostVariable<?, ?>> objectClass = (Class<? extends IGhostVariable<?,?>>)((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
				bulkAddCollectionId(this.getLocalDBConvertedCollectionId(),objectClass);
				setIsIterable(true);
			}
		}
		
		
		public void makeUniterable(){
			if(isIterable()){
				//Wipe out any previously added objects before it was made iterable
				getGhostCollection().clear();
				setIsIterable(false);
			}
		}
		
		@SuppressWarnings("unchecked")
		public IGhostVariable<?,?> getIGVariableObject(){
			IGhostVariable<?,?> result = null;
			
				Class<? extends IGhostVariable<?, ?>> objectClass = (Class<? extends IGhostVariable<?,?>>)((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];				
				GhostQueryInternal gqi = getContent().bulkAddUsingCollectionId(this.getLocalDBConvertedCollectionId());
				
				Constructor<?> constructorWithAnnotation = null;
				 
	    		try {
	    				Constructor<?>[] constructors = objectClass.getConstructors();
	    				for(Constructor<?> c :constructors){
	    					if(c.isAnnotationPresent(PossessConstructor.class)){
	    						 constructorWithAnnotation = c;
	    						 break;
	    					}
	    				}
	    				
	    			if(constructorWithAnnotation == null){
	    				throw new GhostRuntimeException("PossessConstructor annotation is missing!");
	    			}
	    			
	    			Object newGhostVariableObject = null;
	    			Object dataColumnValue = null;
	    			
	    			  
    				String tableName = GhostDBStaticVariables.EMPTY_STR;    				
    				if(gqi.next()){	    					    
    					    tableName = gqi.getStringColumnByPosition(3);
    					    dataColumnValue = gqi.getObjectColumnByPosition(4);
    					    
    					    if(tableName == null){
    					    	newGhostVariableObject =  constructorWithAnnotation.newInstance(new DBID(gqi.getNumberColumnByPosition(1)),
							                  new DBID(gqi.getStringColumnByPosition(1)),
							                  MetaTables.GHOST_VM,
							                  getContent().getVMInsertColumn(),
							                  true);
    					    	result =((objectType) newGhostVariableObject);
    					    }else{
    					    	IMetaGhostVariableTable baseTable = ((IDBCollection<objectType>) getContent()).lookupTable(gqi.getStringColumnByPosition(3));
    					    	newGhostVariableObject =  constructorWithAnnotation.newInstance(new DBID(gqi.getNumberColumnByPosition(1)),
																                  new DBID(gqi.getStringColumnByPosition(2)),
																                  baseTable,
																                  baseTable.getIMGVTValueColumn(),
																                  true);
								
    					    	result = ((objectType) newGhostVariableObject);
	    					    
    					    }
    					    
    					    if(dataColumnValue == null){
    		    				((objectType) newGhostVariableObject).setIsRemoteTable(true);
    			    			((objectType) newGhostVariableObject).setIsEmpty(true);	
    		    			}else{
    		    				((objectType) newGhostVariableObject).setIsRemoteTable(false);
    			    			((objectType) newGhostVariableObject).setIsEmpty(false);
    		    			}
    					    
    					    result = ((objectType) newGhostVariableObject);
		           }
	    		} catch (InstantiationException e) {
					logger.error(e.getMessage(), e);
					throw new GhostRuntimeException(e);
				} catch (IllegalAccessException e) {
					logger.error(e.getMessage(), e);
					throw new GhostRuntimeException(e);
				} catch (IllegalArgumentException e) {
					logger.error(e.getMessage(), e);
					throw new GhostRuntimeException(e);
				} catch (InvocationTargetException e) {
					logger.error(e.getMessage(), e);
					throw new GhostRuntimeException(e);
				}
	    		gqi.close();
    				
	    		return result;
		}
		
		
		public boolean bulkAdd(NUMBER possesionId, IMetaGhostVariableTable mTable, Class<? extends IGhostVariable<?,?>> objectClass){
			return bulkAddHelper(getContent().bulkAdd(possesionId),objectClass,mTable);
		}
		
		public boolean bulkAddCollectionId(NUMBER collectionId, Class<? extends IGhostVariable<?,?>> objectClass){
			return bulkAddHelper(getContent().bulkAddUsingCollectionId(collectionId),objectClass,null);
		}
		
		@SuppressWarnings("unchecked")
		public boolean bulkAddHelper(GhostQueryInternal gqi,
				                     Class<? extends IGhostVariable<?,?>> objectClass,
				                     IMetaGhostVariableTable mTable){
			 
			 //GhostQueryInternal gqi = getContent().bulkAdd(possesionId);    
			 Constructor<?> constructorWithAnnotation = null;
			 
	    		try {
	    				Constructor<?>[] constructors = objectClass.getConstructors();
	    				for(Constructor<?> c :constructors){
	    					if(c.isAnnotationPresent(PossessConstructor.class)){
	    						 constructorWithAnnotation = c;
	    						 break;
	    					}
	    				}
	    				
	    			if(constructorWithAnnotation == null){
	    				throw new GhostRuntimeException("PossessConstructor annotation is missing!");
	    			}
	    			
	    			Object newGhostVariableObject = null;
	    			Object dataColumnValue = null;
	    			
	    			if(mTable == null){	  
	    				String tableName = GhostDBStaticVariables.EMPTY_STR;
	    				
	    				while(gqi.next()){	    					    
	    					    tableName = gqi.getStringColumnByPosition(3);
	    					    dataColumnValue = gqi.getObjectColumnByPosition(4);
	    					    
	    					    if(tableName == null){
	    					    	newGhostVariableObject =  constructorWithAnnotation.newInstance(new DBID(gqi.getNumberColumnByPosition(1)),
								                  new DBID(gqi.getStringColumnByPosition(1)),
								                  MetaTables.GHOST_VM,
								                  MetaTables.GHOST_VM.getIMGVTValueColumn(),
								                  true);
		    					     getGhostCollection().add((objectType) newGhostVariableObject);
	    					    }else{
	    					    	IMetaGhostVariableTable baseTable = ((IDBCollection<objectType>) getContent()).lookupTable(gqi.getStringColumnByPosition(3));
	    					    	newGhostVariableObject =  constructorWithAnnotation.newInstance(new DBID(gqi.getNumberColumnByPosition(1)),
																	                  new DBID(gqi.getStringColumnByPosition(2)),
																	                  baseTable,
																	                  baseTable.getIMGVTValueColumn(),
																	                  true);
									
									getGhostCollection().add((objectType) newGhostVariableObject);
		    					    
	    					    }
	    					    
	    					    if(dataColumnValue == null){
	    		    				((objectType) newGhostVariableObject).setIsRemoteTable(true);
	    			    			((objectType) newGhostVariableObject).setIsEmpty(true);	
	    		    			}else{
	    		    				((objectType) newGhostVariableObject).setIsRemoteTable(false);
	    			    			((objectType) newGhostVariableObject).setIsEmpty(false);
	    		    			}
						}
	    			}else{
	    				while(gqi.next()){
	    					newGhostVariableObject =  constructorWithAnnotation.newInstance(new DBID(gqi.getNumberColumnByPosition(1)),
															                  new DBID(gqi.getStringColumnByPosition(2)),
															                  mTable,
															                  mTable.getIMGVTValueColumn(),
															                  true);
							
							getGhostCollection().add((objectType) newGhostVariableObject );
							
//							if(dataColumnValue == null){
			    				((objectType) newGhostVariableObject).setIsRemoteTable(true);
				    			((objectType) newGhostVariableObject).setIsEmpty(true);	
//			    			}
//							else{
//			    				((objectType) newGhostVariableObject).setIsRemoteTable(false);
//				    			((objectType) newGhostVariableObject).setIsEmpty(false);
//			    			}
						}
	    				
	    			}
	    							
				} catch (InstantiationException e) {
					logger.error(e.getMessage(), e);
					throw new GhostRuntimeException(e);
				} catch (IllegalAccessException e) {
					logger.error(e.getMessage(), e);
					throw new GhostRuntimeException(e);
				} catch (IllegalArgumentException e) {
					logger.error(e.getMessage(), e);
					throw new GhostRuntimeException(e);
				} catch (InvocationTargetException e) {
					logger.error(e.getMessage(), e);
					throw new GhostRuntimeException(e);
				}
		    	gqi.close();
		    	return true;
	    }
		
		public void bulkAddMetaInfo(){
			if(getGhostCollection().size()>0){
				getContent().addGhostMetaInfo(((List<objectType>)getGhostCollection()).get(0));
			}
		}		
		
		public String getSQLQuery(String additionalColumns) {
			return getContent().getSQLQuery(additionalColumns);
		}
		
		
		public void bulkAddMetaInfo(IMetaGhostVariableTable table, IMetaField column, IMetaField idColumn){
				getContent().addGhostMetaInfo(table,column,idColumn);
		}

		
		public void addByBulkInsertId(BigDecimal bulkSaveId) {
			getContent().addByBulkInsertId(bulkSaveId);
		}
		
		@SuppressWarnings("unchecked")
		public Class<? extends IGhostVariable<?, ?>> getClassGenericObjectType() {
			return (Class<? extends IGhostVariable<?,?>>)((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
		}
		
}
