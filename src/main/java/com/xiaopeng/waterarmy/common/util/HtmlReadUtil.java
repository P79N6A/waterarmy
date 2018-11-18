package com.xiaopeng.waterarmy.common.util;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhangyong
 */
public class HtmlReadUtil {

    private static final Logger logger = LoggerFactory.getLogger(HtmlReadUtil.class);

    private static ThreadLocal<ChromeDriver> chromeDriverThreadLocal = ThreadLocal.withInitial(() -> {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-dev-shm-usage");
        try {
            return new ChromeDriver(options);
        } catch (Exception e) {
            logger.error("init ChromeDriver error!!!!!!!!!!!", e);
        }
        return null;
    });


    public static void read(String url) {
        try {
            chromeDriverThreadLocal.get().navigate().to(url);
            chromeDriverThreadLocal.get().navigate().refresh();
            String title = chromeDriverThreadLocal.get().getTitle();
            logger.info("Read url " + title + "  " + url + " successful!!!!!!!!! ");
        } catch (Exception e) {
            logger.error("read error!!!!!!!!!!!", e);
        }
    }
}
