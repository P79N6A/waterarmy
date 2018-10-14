package com.xiaopeng.waterarmy.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by iason on 2018/10/13.
 */
public class IPUtil {

    private static Logger logger = LoggerFactory.getLogger(IPUtil.class);

    /**
     *
     * 获取公网ip
     *
     * @return
     */
    public static String getPublicIP() {
        URL url = null;
        URLConnection urlconn = null;
        BufferedReader br = null;
        try {
            url = new URL("http://2018.ip138.com/ic.asp");//爬取的网站是百度搜索ip时排名第一的那个
            urlconn = url.openConnection();
            br = new BufferedReader(new InputStreamReader(
                    urlconn.getInputStream()));
            String buf = null;
            String get= null;
            while ((buf = br.readLine()) != null) {
                get+=buf;
            }
            int where,end;
            for(where=0;where<get.length()&&get.charAt(where)!='[';where++);
            for(end=where;end<get.length()&&get.charAt(end)!=']';end++);
            get=get.substring(where+1,end);
            return get;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("获取公网ip失败，", e);
        } finally {
            try {
                if (!ObjectUtils.isEmpty(br)) {
                    br.close();
                }
            } catch (IOException e) {
                logger.error("获取公网ip失败，", e);
            }
        }
        return null;
    }

}
