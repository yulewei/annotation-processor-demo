package com.example.maker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yulewei
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface PlusOne {
}
