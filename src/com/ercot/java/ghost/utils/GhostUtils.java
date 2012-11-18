package com.ercot.java.ghost.utils;

import java.util.List;

import com.ercot.java.ghost.MetaTableTypes.IMetaField;
import com.ercot.java.ghost.MetaTableTypes.IMetaFieldConstant;

public class GhostUtils {

	public static String buildMetaFieldColumns(List<IMetaField> columns, String seperator){
		StringBuilder additionalColumnsBuilder = new StringBuilder(); 
		
		//In case there are no columns to process
		if(columns.isEmpty()){
			return GhostDBStaticVariables.EMPTY_STR;
		}
		for(IMetaField imf : columns){
			if(imf instanceof IMetaFieldConstant){
				additionalColumnsBuilder.append(imf + GhostDBStaticVariables.SPACE + imf.getAlias() + seperator);
			}else{
				if(!imf.isFunction()){
				    additionalColumnsBuilder.append(imf.getAssociatedTableAlias() + GhostDBStaticVariables.PERIOD + imf + GhostDBStaticVariables.SPACE + imf.getAlias()+ seperator);
				}else{
					additionalColumnsBuilder.append(imf.getColumnName() + GhostDBStaticVariables.SPACE + imf.getAlias() + seperator);
				}
			}
		}
		return additionalColumnsBuilder.substring(0, additionalColumnsBuilder.length()-seperator.length());
	}
	
	public static String buildMetaFieldColumnsNoAlias(List<IMetaField> columns, String seperator){
		StringBuilder additionalColumnsBuilder = new StringBuilder(); 
		
		//In case there are no columns to process
		if(columns.isEmpty()){
			return GhostDBStaticVariables.EMPTY_STR;
		}
		
		for(IMetaField imf : columns){
			if(imf instanceof IMetaFieldConstant){
				additionalColumnsBuilder.append(imf + seperator);
			}else{
				if(!imf.isFunction()){
				    additionalColumnsBuilder.append(imf.getAssociatedTableAlias() + GhostDBStaticVariables.PERIOD + imf + seperator);
				}else{
					additionalColumnsBuilder.append(imf.getColumnName() + seperator);
				}
			}
		}
		
		return additionalColumnsBuilder.substring(0, additionalColumnsBuilder.length()-seperator.length());
	}
	
	
	
	public static String buildMetaFieldColumnsPlain(List<IMetaField> columns, String seperator){
		StringBuilder additionalColumnsBuilder = new StringBuilder(); 
		
		//In case there are no columns to process
		if(columns.isEmpty()){
			return GhostDBStaticVariables.EMPTY_STR;
		}
		
		for(IMetaField imf : columns){
			if(imf instanceof IMetaFieldConstant){
				additionalColumnsBuilder.append(imf + seperator);
			}else{
				if(!imf.isFunction()){
				    additionalColumnsBuilder.append(imf + seperator);
				}else{
					additionalColumnsBuilder.append(imf.getColumnName() + seperator);
				}
			}
		}
		
		return additionalColumnsBuilder.substring(0, additionalColumnsBuilder.length()-seperator.length());
	}
	
//	 public static Class<?> getClass(Type type) {
//		    if (type instanceof Class) {
//		      return (Class) type;
//		    }
//		    else if (type instanceof ParameterizedType) {
//		      return getClass(((ParameterizedType) type).getRawType());
//		    }
//		    else if (type instanceof GenericArrayType) {
//		      Type componentType = ((GenericArrayType) type).getGenericComponentType();
//		      Class<?> componentClass = getClass(componentType);
//		      if (componentClass != null ) {
//		        return Array.newInstance(componentClass, 0).getClass();
//		      }
//		      else {
//		        return null;
//		      }
//		    }
//		    else {
//		      return null;
//		    }
//		  }
//	
//	public static <T> List<Class<?>> getTypeArguments(
//		    Class<T> baseClass, Class<? extends T> childClass) {
//		    Map<Type, Type> resolvedTypes = new HashMap<Type, Type>();
//		    Type type = childClass;
//		    // start walking up the inheritance hierarchy until we hit baseClass
//		    while (! getClass(type).equals(baseClass)) {
//		      if (type instanceof Class) {
//		        // there is no useful information for us in raw types, so just keep going.
//		        type = ((Class<?>) type).getGenericSuperclass();
//		      }
//		      else {
//		        ParameterizedType parameterizedType = (ParameterizedType) type;
//		        Class<?> rawType = (Class<?>) parameterizedType.getRawType();
//		  
//		        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
//		        TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
//		        for (int i = 0; i < actualTypeArguments.length; i++) {
//		          resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
//		        }
//		  
//		        if (!rawType.equals(baseClass)) {
//		          type = rawType.getGenericSuperclass();
//		        }
//		      }
//		    }
//		  
//		    // finally, for each actual type argument provided to baseClass, determine (if possible)
//		    // the raw class for that type argument.
//		    Type[] actualTypeArguments;
//		    if (type instanceof Class) {
//		      actualTypeArguments = ((Class<?>) type).getTypeParameters();
//		    }
//		    else {
//		      actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
//		    }
//		    List<Class<?>> typeArgumentsAsClasses = new ArrayList<Class<?>>();
//		    // resolve types by chasing down type variables.
//		    for (Type baseType: actualTypeArguments) {
//		      while (resolvedTypes.containsKey(baseType)) {
//		        baseType = resolvedTypes.get(baseType);
//		      }
//		      typeArgumentsAsClasses.add(getClass(baseType));
//		    }
//		    return typeArgumentsAsClasses;
//		  }
	
}
