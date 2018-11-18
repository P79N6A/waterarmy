package com.xiaopeng.waterarmy.common.util;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * 播放
 */
public class HtmlPlayUtil {

    private static final Logger logger = LoggerFactory.getLogger(HtmlPlayUtil.class);

    private static ThreadLocal<ChromeDriver> chromeDriverThreadLocal
            = ThreadLocal.withInitial(() -> {
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


    public static boolean play(String url) {
        try {
            chromeDriverThreadLocal.get().navigate().to(url);
            chromeDriverThreadLocal.get().navigate().refresh();
            String title = chromeDriverThreadLocal.get().getTitle();
            logger.info("Play url " + title + "  " + url + " successful!!!!!!!!! ");
            return true;
        } catch (Exception e) {
            logger.error("play error!!!!!!!!!!!", e);
        }
        return false;
    }
}
