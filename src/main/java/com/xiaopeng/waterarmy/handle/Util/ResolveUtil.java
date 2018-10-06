package com.xiaopeng.waterarmy.handle.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResolveUtil {

    private static Logger logger = LoggerFactory.getLogger(ResolveUtil.class);

    /**
     * 根据网络url 获取title
     * @param url
     * @return
     */
    public static String fetchTitle(String url) {
        int count = 3;
        while (count > 0) {
            try {
                Document doc = Jsoup.connect(url).timeout(2000).get();
                return doc.title();
            } catch (Exception e) {
                logger.error("fetchTitle error!" ,e);
                count--;
            }
        }
        return null;
    }

}
