package com.wky.sensitive.constant;

/**
 * 替换规则的实现类，权限定类名，替换时将通过反射获取
 *
 * @author weikaiyu
 * @version 1.0
 * @date 2022-02-11 11:07
 */
public class RuleTypesConstant {
    //    银行卡替换规则
    public final static String BANKCARD = "com.hgsoft.zengzhiyingyong.module.sensitive.rule.Impl.BankCardRuleImpl";
    //    身份证替换
    public final static String INDENTITYCARD = "com.hgsoft.zengzhiyingyong.module.sensitive.rule.Impl.IdentityCardRuleImpl";
    //     名字替换
    public final static String NAME = "com.hgsoft.zengzhiyingyong.module.sensitive.rule.Impl.NameRuleImpl";
    //    车牌号替换
    public final static String VEHICLEPLATE = "com.hgsoft.zengzhiyingyong.module.sensitive.rule.Impl.VehiclePlateRuleImpl";

}
