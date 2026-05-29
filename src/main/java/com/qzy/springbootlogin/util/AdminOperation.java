package com.qzy.springbootlogin.util;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AdminOperation {
    String value() default "";
    String module() default "系统管理";
}
