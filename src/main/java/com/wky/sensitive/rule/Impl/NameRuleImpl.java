package com.wky.sensitive.rule.Impl;

import com.wky.sensitive.rule.Rule;
import com.wky.sensitive.util.ReplaceUtils;

/**
 * 默认的名字匹配规则
 *
 * @author weikaiyu
 * @version 1.0
 * @date 2022-02-11 10:40
 */
public class NameRuleImpl implements Rule {
    @Override
    public String processor(String param) {
        if (param.length() > 2) return ReplaceUtils.replaceBetween(param, 1, param.length() - 1, "*");
        return param;
    }
}
