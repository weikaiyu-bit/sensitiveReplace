package com.wky.sensitive.rule.Impl;

import com.wky.sensitive.rule.Rule;
import com.wky.sensitive.util.ReplaceUtils;

/**
 * 身份证默认脱敏规则
 *
 * @author weikaiyu
 * @version 1.0
 * @date 2022-02-11 10:51
 */
public class IdentityCardRuleImpl implements Rule {
    @Override
    public String processor(String param) {
        return ReplaceUtils.idEncrypt(param);
    }
}
