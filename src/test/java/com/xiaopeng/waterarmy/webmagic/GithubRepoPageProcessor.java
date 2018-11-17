package com.xiaopeng.waterarmy.webmagic;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.RedisScheduler;

/**
 * Created by iason on 2018/10/18.
 */
public class GithubRepoPageProcessor implements PageProcessor {

    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    @Override
    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    public void process(Page page) {
        System.out.println("html---------------------------" + page.getHtml().get());
        page.addTargetRequests(page.getHtml().links().regex("http://news.bitauto\\.com/.*").all());//http://news.bitauto\.com/.*
        if (page.getUrl().toString().contains("pins")) {
            page.putField("img", page.getHtml().xpath("//div[@id='pin_img']/img/@src").toString());
        } else {
            page.getResultItems().setSkip(true);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {

//        Spider.create(new GithubRepoPageProcessor()).thread(5)
//                .scheduler(new RedisScheduler("http://news.bitauto.com/hao/wenzhang/972987"))
//                .pipeline(new FilePipeline("/data/webmagic/test/"))
//                .run();

        Spider.create(new GithubRepoPageProcessor())
                //从"https://github.com/code4craft"开始抓
                .addUrl("http://news.bitauto.com/hao/wenzhang/972987")
                //开启5个线程抓取
                .thread(10)
                //启动爬虫
                .run();
    }

}