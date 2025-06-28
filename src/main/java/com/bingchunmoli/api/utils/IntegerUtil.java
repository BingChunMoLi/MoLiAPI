package com.bingchunmoli.api.utils;

/**
 * @author bingchunmoli
 */
public class IntegerUtil {
    /**
     * 是否是正整数
     *
     * @param str 可能是数字的字符串
     */
    public static Boolean isInteger(String str) {
        for (int i = 0; i < str.length(); i++) {
            int c = str.charAt(i);
            if (c < 48 || c > 57) {
                return false;
            }
        }
        return true;
    }
}
