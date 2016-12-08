package com.telemarket.telemarketer.mvc.annotation;

import java.lang.annotation.*;

/**
 * Be careful.
 * Author: Hanson
 * Email: imyijie@outlook.com
 * Date: 2016/12/6
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface QueryParam {
    String value();

    boolean require() default false;
}
