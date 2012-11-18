package com.java.ghost.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.java.ghost.GhostAttributes.GhostAttributeEnum;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AttributeMapping {
	GhostAttributeEnum value();
}
