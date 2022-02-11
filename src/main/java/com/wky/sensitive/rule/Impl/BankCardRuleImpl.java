package com.wky.sensitive.rule.Impl;


import com.wky.sensitive.rule.Rule;
import com.wky.sensitive.util.ReplaceUtils;

/**
 * 银行卡替换规则
 *
 * @author weikaiyu
 * @version 1.0
 * @date 2022-02-11 10:53
 */
public  class BankCardRuleImpl implements Rule {
    @Override
    public String processor(String param) {
        return ReplaceUtils.idEncrypts(param);
    }
}
