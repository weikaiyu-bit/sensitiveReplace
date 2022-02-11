package com.wky.sensitive.rule.Impl;

import com.wky.sensitive.rule.Rule;
import com.wky.sensitive.util.ReplaceUtils;

/**
 * 车牌默认匹配规则
 * 车牌号 只显示前二后三
 *
 * @author weikaiyu
 * @version 1.0
 * @date 2022-02-11 11:02
 */
public class VehiclePlateRuleImpl implements Rule {
    @Override
    public String processor(String param) {
        if (param.length() > 2) return ReplaceUtils.replaceBetween(param, 2, param.length() - 3, "*");
        return param;
    }


}
