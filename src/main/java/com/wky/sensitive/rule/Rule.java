package com.wky.sensitive.rule;

/**
 * @author weikaiyu
 * @version 1.0
 * @date 2022-02-11 9:15
 */

public interface Rule {
    /**
     * 过滤接口
     *
     * @param param
     * @return
     */
    String processor(String param);

}
