package com.wky.sensitive.enums;

/**
 * 数据类型。返回的可能是分页，可能是集合，可能是实体类
 *
 * @author weikaiyu
 * @version 1.0
 * @date 2022-02-10 17:14
 */
public enum DataTypeEnum {

    //    分页
    PAGE,
    //    集合
    LIST,
    //    实体
    ENTITY;

    private DataTypeEnum() {
    }

}
