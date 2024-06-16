package com.huawei.ibooking.utils;

import org.json.JSONObject;

/**
 * @author yuqichen
 * @date 2022/5/22 1:10 下午
 */
public class CommonTestUtils {
    public static String getResponseCode(String content) {
        JSONObject obj = new JSONObject(content);
        return obj.getString("code");
    }
}
