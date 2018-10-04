package com.xiaopeng.waterarmy.handle.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FetchParamUtil {
    public static String getMatherStr(String url, String pattern) {
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(url);
        if (m.find()) {
            return m.group(0);
        }
        return null;
    }
}
