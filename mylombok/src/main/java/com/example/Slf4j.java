package com.example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类似 Lombok 的 {@code @Slf4j}
 *
 * https://projectlombok.org/features/log
 *
 * @author yulewei
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Slf4j {
}
