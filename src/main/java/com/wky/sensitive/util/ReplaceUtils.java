package com.wky.sensitive.util;

import org.springframework.util.StringUtils;

/**
 * 替换工具类
 * @author weikaiyu
 * @version 1.0
 * @date 2022-02-11 10:46
 */
public  class ReplaceUtils {

    /**
     * 将字符串开始位置到结束位置之间的字符用指定字符替换
     *
     * @param sourceStr   待处理字符串
     * @param begin       开始位置
     * @param end         结束位置
     * @param replacement 替换字符
     * @return
     */
    public static String replaceBetween(String sourceStr, int begin, int end, String replacement) {
        if (sourceStr == null) {
            return "";
        }
        if (replacement == null) {
            replacement = "*";
        }
        int replaceLength = end - begin;
        if (org.apache.commons.lang3.StringUtils.isNotBlank(sourceStr) && replaceLength > 0) {
            StringBuilder sb = new StringBuilder(sourceStr);
            sb.replace(begin, end, org.apache.commons.lang3.StringUtils.repeat(replacement, replaceLength));
            return sb.toString();
        } else {
            return sourceStr;
        }
    }



    /**
     * 身份证前三后四脱敏
     *
     * @param id
     * @return
     */
    public static String idEncrypt(String id) {
        if (StringUtils.isEmpty(id) || (id.length() < 11)) {
            return id;
        }
        return id.replaceAll("(?<=\\w{6})\\w(?=\\w{4})", "*");
    }


    /**
     * 银行卡脱敏
     *
     * @param card
     * @return
     */
    public static String idEncrypts(String card) {

        return replaceBetween(card, 6, card.length() - 4, null);
    }
}

