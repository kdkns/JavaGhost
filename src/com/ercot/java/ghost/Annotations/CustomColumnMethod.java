package com.ercot.java.ghost.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ercot.java.ghost.GhostAttributes.GhostCustomAttributeEnum;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CustomColumnMethod {
       GhostCustomAttributeEnum attribute();
}
