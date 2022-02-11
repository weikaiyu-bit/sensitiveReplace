package com.wky.sensitive.annotation;

import com.wky.sensitive.enums.DataTypeEnum;

import java.lang.annotation.*;

/**
 * @author weikaiyu
 * @version 1.0
 * @date 2022-02-11 10:03
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SensitiveReplace {
    /**
     * 需要脱敏属性字段（比如， name，phone，sex）
     *
     * @return
     */
    String[] params() default "";

    /**
     * 替换规则，
     * 提供如下四种：com.wky.sensitive.rule.Impl
     *
     * @return
     */
    String[] rulePath() default "";


    /**
     * 数据类型
     *
     * @return
     */
    DataTypeEnum dataType();
}
