package com.xiaopeng.waterarmy.common.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class HtmlReadUtil {

    private static final Logger logger = LoggerFactory.getLogger(HtmlReadUtil.class);

    private static ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("HtmlReadUtil").build();

    private static final LinkedBlockingQueue<String> URLS = new LinkedBlockingQueue<>(100000);
    private static List<ChromeDriver> chromeDrivers = new ArrayList<>();

    static {
        for (int i = 0; i < 10; i++) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors");
            ChromeDriver chromeDriver = new ChromeDriver(options);
            new Thread(() -> {
                while (true) {
                    try {
                        String readUrl = URLS.take();
                        chromeDriver.get(readUrl);
                        logger.info("阅读：" + readUrl);
                        TimeUnit.SECONDS.sleep(1);
                    } catch (Exception e) {
                        logger.error("阅读失败", e);
                        chromeDriver.close();
                        chromeDrivers.remove(chromeDriver);
                    }
                }
            }).start();
            chromeDrivers.add(chromeDriver);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> chromeDrivers.forEach(RemoteWebDriver::close)));
    }

    public static void read(String url) {
        URLS.add(url);
    }
}
