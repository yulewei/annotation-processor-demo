package com.example.maker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类似 Lombok 的 {@code @Getter}
 *
 * https://projectlombok.org/features/Getter
 *
 * @author yulewei
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface PlusOne {
}
