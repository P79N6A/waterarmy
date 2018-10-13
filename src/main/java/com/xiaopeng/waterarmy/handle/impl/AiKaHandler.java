package com.xiaopeng.waterarmy.handle.impl;

import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.enums.ResultCodeEnum;
import com.xiaopeng.waterarmy.common.util.HtmlPlayUtil;
import com.xiaopeng.waterarmy.common.util.HtmlReadUtil;
import com.xiaopeng.waterarmy.handle.PlatformHandler;
import com.xiaopeng.waterarmy.handle.Util.FetchParamUtil;
import com.xiaopeng.waterarmy.handle.Util.ResultParamUtil;
import com.xiaopeng.waterarmy.handle.Util.WebClientFatory;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.handle.param.SaveContext;
import com.xiaopeng.waterarmy.handle.result.HandlerResultDTO;
import com.xiaopeng.waterarmy.handle.result.LoginResultDTO;
import com.xiaopeng.waterarmy.model.dao.CommentInfo;
import com.xiaopeng.waterarmy.model.dao.PublishInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AiKaHandler extends PlatformHandler {

    private static Logger logger = LoggerFactory.getLogger(AiKaHandler.class);

    private static final String TARGET_COMMENT_URL = "http://www.xcar.com.cn/bbs/post.php";
    private static final String TARGET_PUBLISH_URL = "http://www.xcar.com.cn/bbs/post.php";

    @Autowired
    private AiKaLoginHandler aiKaLoginHandler;

    @Override
    public Result<HandlerResultDTO> publish(RequestContext requestContext) {

        /**
         * action: newthread
         * topicsubmit: yes
         * fromcard: 1
         * htmlon: 10
         * sign: travle
         * fid: 1046
         * extra:
         * cklid: 126007
         */

        /**
         * action: newthread
         * topicsubmit: yes
         * fromcard: 1
         * htmlon: 10
         * sign: travle
         * fid: 1604
         * extra:
         * cklid: 126007
         *
         */

        /**
         * subject: (unable to decode value)
         * message: (unable to decode value)
         * formhash: edacfc30
         */

        Result<LoginResultDTO> resultDTOResult = aiKaLoginHandler.login(requestContext.getUserId());
        if (!resultDTOResult.getSuccess()) {
            logger.error("[TaiPingYangHandler.publish] requestContext" + requestContext);
            return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
        }
        Map map = null;
        boolean needRetry = true;
        int retry = 3;
        try {

            while (needRetry&&retry>0) {
                retry -- ;
                LoginResultDTO loginResultDTO = resultDTOResult.getData();
                CloseableHttpClient httpClient = loginResultDTO.getHttpClient();
                map = createPublishHttpPost(requestContext,map);
                CloseableHttpResponse response = httpClient.execute((HttpPost)map.get("httpPost"));
                HttpEntity entity = response.getEntity();
                String content = null;
                if (entity != null) {
                    content = EntityUtils.toString(entity, "utf-8");
                    try {
                        String temp = FetchParamUtil.getMatherStr(content, "url=viewthread.php\\?tid=\\d+");
                        temp = FetchParamUtil.getMatherStr(temp, "\\d+");
                        if (StringUtils.isNotBlank(temp)) {
                            String url = "http://www.xcar.com.cn/bbs/viewthread.php?tid=" + temp;
                            PublishInfo publishInfo = ResultParamUtil.createPublishInfo(requestContext, content, url);
                            save(new SaveContext(publishInfo));
                            HandlerResultDTO handlerResultDTO = ResultParamUtil.createHandlerResultDTO(requestContext, content, url);
                            return new Result(handlerResultDTO);
                        }
                    }catch (Exception e) {
                        Document document = Jsoup.parse(content);
                        Elements elements = document.select("[name=formhash]");
                        String formhash = elements.get(0).val();

                        Elements elements1 = document.select("[name=ssid]");
                        String ssid = elements1.get(0).val();
                        if (formhash != null && ssid != null) {
                            map.put("formhash",formhash);
                            map.put("ssid",ssid);
                            needRetry = true;
                        }else{
                            needRetry = false;
                        }
                    }
                }
            }
            return new Result<>(ResultCodeEnum.HANDLE_FAILED);
        } catch (Exception e) {
            logger.error("[AiKaHandler.comment] error!", e);
            //处理失败的回复，把context记录下来，可以决定是否重新扫描,并且记录失败原因
            return new Result<>(ResultCodeEnum.HANDLE_FAILED);
        }
    }


    private Map createPublishHttpPost(RequestContext requestContext,Map map) {

        /**
         * subject: (unable to decode value)
         * message: (unable to decode value)
         * formhash: edacfc30
         */

        if (map == null) {
            map = new HashMap();
        }

        String pulishUrl = createPublisUrl(requestContext);

        if (pulishUrl == null) {
            return null;
        }

        HttpPost httpPost = new HttpPost(pulishUrl);
        setPublishHeader(httpPost);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        try {
            String temp = requestContext.getContent().getTitle();
            nameValuePairs.add(new BasicNameValuePair("subject", temp));
            nameValuePairs.add(new BasicNameValuePair("message", URLEncoder.encode("[textcard]" + requestContext.getContent().getText() + "[/textcard]", "GB2312")));//meesageBody
            if (map.get("formhash")!=null) {
                nameValuePairs.add(new BasicNameValuePair("formhash", (String) map.get("formhash")));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "GB2312"));
            map.put("httpPost",httpPost);
            return map;
        } catch (Exception e) {
            logger.error("[AiKaHandler.createPublishHttpPost]createPublishHttpPost  UrlEncodedFormEntity error! nameValuePairs" + nameValuePairs, e);
            return null;
        }
    }

    private String createPublisUrl(RequestContext requestContext) {
        /**
         *
         * http://www.xcar.com.cn/bbs/forumdisplay.php?fid=1604
         * action: newthread
         * topicsubmit: yes
         * fromcard: 1
         * htmlon: 10
         * sign: travle
         * fid: 1604
         * extra:
         * cklid: 126007
         */

        String tidStr = FetchParamUtil.getMatherStr(requestContext.getPrefixUrl(), "fid=\\d+");
        tidStr = FetchParamUtil.getMatherStr(tidStr, "\\d+");
        if (tidStr == null) {
            return null;
        }
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("action", "newthread"));
            nameValuePairs.add(new BasicNameValuePair("topicsubmit", "yes"));
            nameValuePairs.add(new BasicNameValuePair("fromcard", "1"));
            nameValuePairs.add(new BasicNameValuePair("htmlon", "10"));
            nameValuePairs.add(new BasicNameValuePair("sign", "travle"));
            nameValuePairs.add(new BasicNameValuePair("fid", tidStr));
            nameValuePairs.add(new BasicNameValuePair("extra", null));
            nameValuePairs.add(new BasicNameValuePair("cklid", "126007"));
            String str = EntityUtils.toString(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            return TARGET_PUBLISH_URL + "?" + str;
        } catch (Exception e) {
            logger.error("createPublisUrl", e);
            return null;
        }
    }


    @Override
    public Result<HandlerResultDTO> comment(RequestContext requestContext) {
        Result<LoginResultDTO> resultDTOResult = aiKaLoginHandler.login(requestContext.getUserId());
        if (!resultDTOResult.getSuccess()) {
            logger.error("[DiYiDianDongHandler.comment] requestContext" + requestContext);
            return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
        }

        int retry = 3;
        boolean needRetry = true;

        try {
            Map map = null;
            while (retry>0 && needRetry) {
                retry -- ;
                LoginResultDTO loginResultDTO = resultDTOResult.getData();
                CloseableHttpClient httpClient = loginResultDTO.getHttpClient();
                map = createCommentPost(requestContext, map);
                CloseableHttpResponse response = httpClient.execute((HttpPost) map.get("httpPost"));
                HttpEntity entity = response.getEntity();
                String content = null;
                if (entity != null) {
                    content = EntityUtils.toString(entity, "utf-8");
                    try {
                        JSONObject jsonObject = JSONObject.parseObject(content);
                        int stauts = (int) jsonObject.get("stat");
                        if (stauts == 1) {
                            HandlerResultDTO handlerResultDTO = ResultParamUtil.createHandlerResultDTO(requestContext, content);
                            CommentInfo commentInfo = ResultParamUtil.createCommentInfo(requestContext, content);
                            save(new SaveContext(commentInfo));
                            return new Result(handlerResultDTO);
                        }
                    } catch (Exception e) {
                        Document document = Jsoup.parse(content);
                        Elements elements = document.select("[name=formhash]");
                        String formhash = elements.get(0).val();

                        Elements elements1 = document.select("[name=ssid]");
                        String ssid = elements1.get(0).val();
                        if (formhash != null && ssid != null) {
                            map.put("formhash",formhash);
                            map.put("ssid",ssid);
                            needRetry = true;
                        }else{
                            needRetry = false;
                        }
                    }
                    //评论成功
                }
            }

        } catch (Exception e) {
            logger.error("[AiKaHandler.comment] error!", e);
        }
        return new Result<>(ResultCodeEnum.HANDLE_FAILED);

    }


    private Map createCommentPost(RequestContext requestContext, Map map) {

        /**
         * tid: 33934569
         * fid: 540
         * action: reply
         * mt: 0.022039394939090906
         * land: lord
         * message: %E8%B1%AA%E8%BD%A6
         * formhash: edacfc30
         * usesig: 1
         * ssid: 1538802571
         * replysubmit: yes
         */

        HttpPost httpPost = new HttpPost(TARGET_COMMENT_URL);
        setHeader(httpPost);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        try {
            if (map == null) {
                map = new HashMap();
            }
            if (map.get("tid") != null && map.get("fid") != null && map.get("formhash") != null && map.get("ssid") != null) {

            } else {
                getParameters(requestContext, map);
            }

            nameValuePairs.add(new BasicNameValuePair("tid", (String) map.get("tid")));
            nameValuePairs.add(new BasicNameValuePair("fid", (String) map.get("fid")));
            nameValuePairs.add(new BasicNameValuePair("action", "reply"));
            nameValuePairs.add(new BasicNameValuePair("mt", " 0.022039394939090906"));
            nameValuePairs.add(new BasicNameValuePair("land", "lord"));
            nameValuePairs.add(new BasicNameValuePair("message", URLEncoder.encode(requestContext.getContent().getText(), "UTF-8")));//URLEncoder.encode(requestContext.getContent().getText(), "UTF-8")));//"%E5%A5%BD%E6%83%B3%E4%B9%9F%E5%8E%BB%E5%81%9A%E4%B8%80%E4%B8%8B"
            if (map.get("formhash") != null) {
                nameValuePairs.add(new BasicNameValuePair("formhash", (String) map.get("formhash")));//"edacfc30"));//edacfc30//(String) map.get("formhash")
            }
            nameValuePairs.add(new BasicNameValuePair("usesig", "1"));
            if (map.get("ssid") != null) {
                nameValuePairs.add(new BasicNameValuePair("ssid", (String) map.get("ssid")));//1538824543 //(String) map.get("ssid")
            }
            nameValuePairs.add(new BasicNameValuePair("replysubmit", "yes"));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            map.put("httpPost", httpPost);
            return map;
        } catch (Exception e) {
            logger.error("[AiKaHandler.createCommentPost]createCommentPost  UrlEncodedFormEntity error! nameValuePairs" + nameValuePairs, e);
            return null;
        }
    }


    private void getParameters(RequestContext requestContext, Map map) {
        WebClient webClient = WebClientFatory.getInstance();
        HtmlPage page = null;
        //重试三次
        int count = 3;
        while (count > 0) {
            try {
                page = webClient.getPage(requestContext.getPrefixUrl());//尝试加载上面图片例子给出的网页
                break;
            } catch (Exception e) {
                logger.error("getParameters", e);
                e.printStackTrace();
                count--;
            } finally {
                webClient.close();
            }
        }
        webClient.waitForBackgroundJavaScript(500);//异步JS执行需要耗时,所以这里线程要阻塞30秒,等待异步JS执行结束

        String pageXml = page.asXml();//直接将加载完成的页面转换成xml格式的字符串
        Document document = Jsoup.parse(pageXml);//获取html文档
        String str = document.toString();
        String str1 = FetchParamUtil.getMatherStr(str, "\\{tid:.*ssid:.*\\}");

        String tid = FetchParamUtil.getMatherStr(str1, "tid:.*?,");
        tid = FetchParamUtil.getMatherStr(tid, "\\d+");


        String fid = FetchParamUtil.getMatherStr(str1, "fid:.*?,");
        fid = FetchParamUtil.getMatherStr(fid, "\\d+");

        String ssid = FetchParamUtil.getMatherStr(str1, "ssid:.*?,");
        ssid = FetchParamUtil.getMatherStr(ssid, "\\d+");

        String formhash = FetchParamUtil.getMatherStr(str1, "formhash:.*?,");
        formhash = FetchParamUtil.getMatherStr(formhash, "\\'.*\\'");
        formhash = formhash.replaceAll("'", "");


        map.put("fid", fid);
        map.put("tid", tid);
        //没登录的情况下拿到的数据不准确
        //map.put("ssid", ssid);
        //map.put("formhash", formhash);
    }

    /**
     * Host: www.xcar.com.cn
     * Origin: http://www.xcar.com.cn
     * Referer: http://www.xcar.com.cn/bbs/viewthread.php?tid=33934569
     * User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36
     * X-Request-Id: 1728;r=2589960
     * X-Requested-With: XMLHttpRequest
     *
     * @param httpPost
     */

    private void setHeader(HttpPost httpPost) {
        httpPost.setHeader("Host", "www.xcar.com.cn");
        httpPost.setHeader("Origin", "http://www.xcar.com.cn");
        httpPost.setHeader("Referer", "http://www.xcar.com.cn");
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        httpPost.setHeader(" X-Requested-With", "XMLHttpRequest");

    }

    private void setPublishHeader(HttpPost httpPost) {
        /**
         * Host: www.xcar.com.cn
         * Origin: http://www.xcar.com.cn
         * Referer: http://www.xcar.com.cn/bbs/post_card.php?a=newthread&fid=1604&extra=
         * Upgrade-Insecure-Requests: 1
         * User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36
         */
        httpPost.setHeader("Host", "www.xcar.com.cn");
        httpPost.setHeader("Origin", "http://www.xcar.com.cn");
        httpPost.setHeader("Referer", "http://www.xcar.com.cn/bbs/post_card.php?a=newthread&fid=1604&extra=");
        httpPost.setHeader("Upgrade-Insecure-Requests", "1");
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
    }


    @Override
    public Result<HandlerResultDTO> read(RequestContext requestContext) {
        HtmlReadUtil.read(requestContext.getTargetUrl());
        HandlerResultDTO handlerResultDTO = ResultParamUtil.createHandlerResultDTO(requestContext, "{success:true,url:" + requestContext.getTargetUrl());
        return new Result(handlerResultDTO);
    }

    @Override
    public Result<HandlerResultDTO> play(RequestContext requestContext) {
        HtmlPlayUtil.play(requestContext.getTargetUrl());
        HandlerResultDTO handlerResultDTO = ResultParamUtil.createHandlerResultDTO(requestContext, "{success:true,url:" + requestContext.getTargetUrl());
        return new Result(handlerResultDTO);
    }

    @Override
    public Result<HandlerResultDTO> praise(RequestContext requestContext) {
        return null;
    }
}
