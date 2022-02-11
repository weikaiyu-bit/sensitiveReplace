package com.wky.sensitive.rule.abstracts;

import com.wky.sensitive.rule.Rule;

/**
 * 前置处理器，交由使用者调用
 * @author weikaiyu
 * @version 1.0
 * @date 2022-02-11 16:09
 */
public abstract class BeforeSensitiveProcessor implements Rule {

    @Override
    public String processor(String param) {
        return null;
    }


    public abstract boolean bool();

}
