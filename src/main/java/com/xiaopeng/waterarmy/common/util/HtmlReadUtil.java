package com.xiaopeng.waterarmy.common.util;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhangyong
 */
public class HtmlReadUtil {

    private static final Logger logger = LoggerFactory.getLogger(HtmlReadUtil.class);

    private static ThreadLocal<ChromeDriver> chromeDriverThreadLocal = ThreadLocal.withInitial(HtmlReadUtil::getChromDriver);
    private static ThreadLocal<AtomicInteger> counter = ThreadLocal.withInitial(AtomicInteger::new);


    public static boolean read(String url) {
        try {
            if (500 <= counter.get().get()) {
                if (chromeDriverThreadLocal.get() != null) {
                    chromeDriverThreadLocal.get().close();
                }
                chromeDriverThreadLocal.set(getChromDriver());
                counter.get().set(0);
                logger.info(" read 1000 次，关掉chrom重新来");
            }
            chromeDriverThreadLocal.get().navigate().to(url);
            chromeDriverThreadLocal.get().navigate().refresh();
            String title = chromeDriverThreadLocal.get().getTitle();
            logger.info(String.format("Read count: %s url %s  %s successful!!!!!!!!! ", new Object[]{counter.get(), title, url}));
            counter.get().incrementAndGet();
            return true;
        } catch (Exception e) {
            logger.error("read error!!!!!!!!!!!", e);
            if (chromeDriverThreadLocal.get() != null) {
                chromeDriverThreadLocal.get().close();
            }
            chromeDriverThreadLocal.set(getChromDriver());
            return false;
        }
    }


    private static ChromeDriver getChromDriver() {
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
    }
}
