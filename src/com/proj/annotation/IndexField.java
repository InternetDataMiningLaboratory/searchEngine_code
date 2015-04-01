package com.proj.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;  
import java.lang.annotation.ElementType;  

@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.FIELD)  
public @interface IndexField {  
  
	boolean indexed() default true;

	boolean stored() default false;

	boolean storeTermVectors() default true;

	boolean tokenized() default true;

	boolean storeTermVectorPositions() default true;

	boolean storeTermVectorOffsets() default true;  
}  