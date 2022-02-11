package com.wky.sensitive.enums;

/**
 * 默认匹配规则
 *
 * @author weikaiyu
 * @version 1.0
 * @date 2022-02-11 11:05
 */
public enum RuleEnum {

    //    银行卡替换规则
    BANKCARD,
    //    身份证
    INDENTITYCARD,
    //     名字
    NAME,
    //     车牌号
    VEHICLEPLATE;

    private RuleEnum() {
    }

}
