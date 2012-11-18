package com.ercot.java.ghost.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ercot.java.ghost.utils.GhostDBStaticVariables.DBOwnerList;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface MetaTableInfo {
	String unqualifiedTableName();
	DBOwnerList schema();

}
