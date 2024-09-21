package com.hjdmmm.blog.annotation;

import com.hjdmmm.blog.enums.AuthTypeEnum;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auth {
    @AliasFor("value")
    AuthTypeEnum type() default AuthTypeEnum.PERMIT_ALL;

    @AliasFor("type")
    AuthTypeEnum value() default AuthTypeEnum.PERMIT_ALL;
}
