package com.example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类似 Lombok 的 {@code @Data}
 *
 * https://projectlombok.org/features/Data
 *
 * @author yulewei
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Data {
}
