package com.example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yulewei
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Builder {
}
