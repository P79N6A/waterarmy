package com.xiaopeng.waterarmy.handle.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xiaopeng.waterarmy.common.Result.AiKaCommentList;
import com.xiaopeng.waterarmy.common.Result.AiKaCommentNews;
import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.enums.ResultCodeEnum;
import com.xiaopeng.waterarmy.common.enums.TaskEntryTypeEnum;
import com.xiaopeng.waterarmy.common.util.HtmlPlayUtil;
import com.xiaopeng.waterarmy.common.util.HtmlReadUtil;
import com.xiaopeng.waterarmy.handle.PlatformHandler;
import com.xiaopeng.waterarmy.handle.Util.FetchParamUtil;
import com.xiaopeng.waterarmy.handle.Util.ResultParamUtil;
import com.xiaopeng.waterarmy.handle.Util.WebClientFatory;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.handle.param.SaveContext;
import com.xiaopeng.waterarmy.handle.result.AiKaCommentResultDTO;
import com.xiaopeng.waterarmy.handle.result.HandlerResultDTO;
import com.xiaopeng.waterarmy.handle.result.LoginResultDTO;
import com.xiaopeng.waterarmy.model.dao.CommentInfo;
import com.xiaopeng.waterarmy.model.dao.PublishInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.xiaopeng.waterarmy.common.Result.AiKaComment;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;

@Component
public class AiKaHandler extends PlatformHandler {

    private static Logger logger = LoggerFactory.getLogger(AiKaHandler.class);

    private static final String TARGET_COMMENT_URL = "http://www.xcar.com.cn/bbs/post.php";
    private static final String TARGET_PUBLISH_URL = "http://www.xcar.com.cn/bbs/post.php";

    private static final String TARGET_COMMENT_NEWS = "http://comment.xcar.com.cn/icomment_news2.php?from=cms";

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

        Result<LoginResultDTO> resultDTOResult = aiKaLoginHandler.login(requestContext);
        if (!resultDTOResult.getSuccess()) {
            logger.error("[TaiPingYangHandler.publish] requestContext" + requestContext);
            return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
        }
        Map map = new HashMap();
        boolean needRetry = true;
        int retry = 3;
        try {

            while (needRetry && retry > 0) {
                retry--;
                LoginResultDTO loginResultDTO = resultDTOResult.getData();
                CloseableHttpClient httpClient = loginResultDTO.getHttpClient();

                if (requestContext.getImageInputStreams() != null && !requestContext.getImageInputStreams().isEmpty()) {
                    AiKaImage aiKaImage = aiKaImageUpload(httpClient, requestContext.getImageInputStreams().get(0));
                    if (logger.isDebugEnabled()) {
                        logger.info("爱卡图片上传结果：" + aiKaImage);
                    }
                    map.put("aiKaImage", aiKaImage);
                }

                map = createPublishHttpPost(requestContext, map);
                CloseableHttpResponse response = httpClient.execute((HttpPost) map.get("httpPost"));
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
                    } catch (Exception e) {
                        Document document = Jsoup.parse(content);
                        Elements elements = document.select("[name=formhash]");
                        String formhash = elements.get(0).val();

                        Elements elements1 = document.select("[name=ssid]");
                        String ssid = elements1.get(0).val();
                        if (formhash != null && ssid != null) {
                            map.put("formhash", formhash);
                            map.put("ssid", ssid);
                            needRetry = true;
                        } else {
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


    private Map createPublishHttpPost(RequestContext requestContext, Map map) {

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
            AiKaImage aiKaImage = (AiKaImage) map.get("aiKaImage");
            String msg;
            if (aiKaImage != null) {
                msg = URLEncoder.encode("[textcard]" + requestContext.getContent().getText() + "[/textcard][piccard][img=960,641]" + aiKaImage.src + "[/img][/piccard]", "GB2312");
            } else {
                msg = URLEncoder.encode("[textcard]" + requestContext.getContent().getText() + "[/textcard]", "GB2312");
            }
            nameValuePairs.add(new BasicNameValuePair("message", msg));//meesageBody
            if (map.get("formhash") != null) {
                nameValuePairs.add(new BasicNameValuePair("formhash", (String) map.get("formhash")));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "GB2312"));
            map.put("httpPost", httpPost);
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

        if (TaskEntryTypeEnum.AIKANEWSCOMMENT.equals(requestContext.getHandleEntryType())) {
            return createCommentNews(requestContext);
        }
        return commentForum(requestContext);
    }

    private Result<HandlerResultDTO> commentForum(RequestContext requestContext) {
        Result<LoginResultDTO> resultDTOResult = aiKaLoginHandler.login(requestContext);
        if (!resultDTOResult.getSuccess()) {
            logger.error("[DiYiDianDongHandler.comment] requestContext" + requestContext);
            return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
        }

        int retry = 3;
        boolean needRetry = true;

        try {
            Map map = null;
            while (retry > 0 && needRetry) {
                retry--;
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
                            map.put("formhash", formhash);
                            map.put("ssid", ssid);
                            needRetry = true;
                        } else {
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

    /**
     * 易车图片资源上传
     *
     * @param httpClient
     * @param inputStream
     * @return
     */
    public AiKaImage aiKaImageUpload(HttpClient httpClient, InputStream inputStream) throws IOException {
        String uploadUrl = "http://www.xcar.com.cn/bbs/post_card_ajax.php?a=upload_pic";
        HttpPost httpPost = new HttpPost(uploadUrl);

        HttpEntity entity = MultipartEntityBuilder.create()
                .addBinaryBody("Filedata", inputStream, ContentType.create("application/octet-stream"), "file.png")
                .build();
        httpPost.setEntity(entity);

        HttpResponse httpResponse = httpClient.execute(httpPost);
        String content = EntityUtils.toString(httpResponse.getEntity());
        System.out.println(content);
        return JSON.parseObject(content, AiKaImage.class);
    }


    public static class AiKaImage {
        public String status;
        public String msg;
        public String src;

        @Override
        public String toString() {
            return "YiCheImage{" +
                    "status='" + status + '\'' +
                    ", msg='" + msg + '\'' +
                    ", src='" + src + '\'' +
                    '}';
        }
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


        //tid: 80136066
        //fid: 223
        //action: reply
        //mt: 0.24824193206404255
        //land: lord
        //message: %5B%E5%9D%8F%E7%AC%91%5D%5B%E5%9D%8F%E7%AC%91%5D%5B%E5%9D%8F%E7%AC%91%5D%5B%E5%9D%8F%E7%AC%91%5D
        //formhash: b9894710
        //usesig: 1
        //ssid: 1541685415
        //repquote: 916757846
        //replysubmit: yes
        //repquote_authorid: 2271566

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


            /**
             *获取被评论的内容，如果有就是要盖楼中楼
             */
            if (requestContext.getRequestParam() != null && requestContext.getRequestParam().get("commentContent") != null) {
                Map map1 = new HashMap();
                getReplyCommentParameters(requestContext, map1);
                String uid = (String) map1.get("uid");
                String qutoId = (String) map1.get("qutoId");

                nameValuePairs.add(new BasicNameValuePair("repquote_authorid", uid));
                nameValuePairs.add(new BasicNameValuePair("repquote", qutoId));
            }

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            map.put("httpPost", httpPost);
            return map;
        } catch (Exception e) {
            logger.error("[AiKaHandler.createCommentPost]createCommentPost  UrlEncodedFormEntity error! nameValuePairs" + nameValuePairs, e);
            return null;
        }
    }


    private void getReplyCommentParameters(RequestContext requestContext,Map map) {

        //循环50页 获取数据
        int maxPage = 50;

        while (maxPage>0) {
            maxPage -- ;
            String url = requestContext.getPrefixUrl();
            if (maxPage<49) {
                url = url+"&page="+(50-maxPage);
            }
            WebClient webClient = WebClientFatory.getInstance();
            HtmlPage page = null;
            //重试三次
            int count = 3;
            while (count > 0) {
                try {
                    page = webClient.getPage(url);
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
            Elements tables = document.select("table[id]");
            Element element = null;
            for (Element tempElement:tables) {
                String str = tempElement.text();
                if (str.contains((String)requestContext.getRequestParam().get("commentContent"))) {
                    element = tempElement;
                    String uid = FetchParamUtil.getMatherStr(element.toString(),"uid=\\d+");
                    uid = FetchParamUtil.getMatherStr(uid,"\\d+");
                    map.put("uid",uid);

                    String qutoId = FetchParamUtil.getMatherStr(element.toString(),"id=\"table_\\d+\"");
                    qutoId = FetchParamUtil.getMatherStr(qutoId,"\\d+");
                    map.put("qutoId",qutoId);
                    break;
                }
            }

            if (element != null) {
                break;
            }
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
        Result<LoginResultDTO> resultDTOResult = aiKaLoginHandler.login(requestContext);
        if (!resultDTOResult.getSuccess()) {
            logger.error("[TaiPingYangHandler.publish] requestContext" + requestContext);
            return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
        }
        LoginResultDTO loginResultDTO = resultDTOResult.getData();
        CloseableHttpClient httpClient = loginResultDTO.getHttpClient();
        HttpGet httpGet = createPraiseParamHttpGet(requestContext);

        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String content = null;
            if (entity != null) {
                content = EntityUtils.toString(entity, "utf-8");
                AiKaCommentList aiKaCommentList = JSONObject.parseObject(content, AiKaCommentList.class);
                AiKaCommentNews aiKaCommentNews = aiKaCommentList.getNews();
                List<AiKaComment>  aiKaCommentList1 = aiKaCommentList.getList();
                AiKaComment comment = null;
                for (AiKaComment tempComment : aiKaCommentList1) {
                    if (tempComment.getConts().contains(requestContext.getContent().getText())) {
                        comment = tempComment;
                        break;
                    }
                }
                if (comment!=null) {
                    HttpGet httpGet1 = createPraiseHttpGet(requestContext,aiKaCommentNews.getId(),comment.getId(),aiKaCommentNews.getCid());
                    CloseableHttpResponse response1 = httpClient.execute(httpGet1);
                    HttpEntity entity1 = response1.getEntity();
                    String content1 = null;
                    if (entity1 != null) {
                        content1 = EntityUtils.toString(entity1, "utf-8");
                        HandlerResultDTO handlerResultDTO = ResultParamUtil.createHandlerResultDTO(requestContext, content);
                        return new Result(handlerResultDTO);
                    }
                }else {
                    logger.error("AiKapraise error!");
                }
            }
        } catch (Exception e) {
            logger.error("AiKaHandler.praise error", e);
        }


        return new Result<>(ResultCodeEnum.HANDLE_FAILED);
    }


    private HttpGet createPraiseHttpGet(RequestContext requestContext, String newsId,String commentId,String newsCid) {
        //http://comment.xcar.com.cn/interface/newsd_api.php?jsonpCallback=jQuery1124008292429965515535_1541595874336&cid=28976776&cmid=1125645&ctype=0&nid=2027320&action=agree&_=1541595874340
        String cid = commentId;
        String cmid = newsId;
        String nid = newsCid;

        String ctype = "0";
        String action = "agree";
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String jsonpCallback = "jQuery1124008292429965515535_" + String.valueOf(System.currentTimeMillis());

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("jsonpCallback", jsonpCallback));
            nameValuePairs.add(new BasicNameValuePair("cid", cid));
            nameValuePairs.add(new BasicNameValuePair("cmid", cmid));
            nameValuePairs.add(new BasicNameValuePair("ctype", ctype));
            nameValuePairs.add(new BasicNameValuePair("nid", nid));
            nameValuePairs.add(new BasicNameValuePair("action", action));
            nameValuePairs.add(new BasicNameValuePair("_", timeStamp));

            String str = EntityUtils.toString(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            String url = "http://comment.xcar.com.cn/interface/newsd_api.php?" + str;
            HttpGet httpGet = new HttpGet(url);
            setPraiseHttpGet(httpGet);
            return httpGet;
        }catch (Exception e) {
            logger.error("createPraiseHttpGet error!",e);
        }

        return null;
    }

    private void setPraiseHttpGet(HttpGet httpGet) {
        //Host: comment.xcar.com.cn
        //Referer: http://comment.xcar.com.cn/comments_news.php?cid=2027320&ctype=0&curl=http://newcar.xcar.com.cn/201810/news_2027320_1.html
        //User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36
        //X-Requested-With: XMLHttpRequest

        httpGet.setHeader("Host","comment.xcar.com.cn");
        httpGet.setHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        httpGet.setHeader("X-Requested-With","XMLHttpRequest");
    }

    private HttpGet createPraiseParamHttpGet(RequestContext requestContext) {
        String cid = FetchParamUtil.getMatherStr(requestContext.getPrefixUrl(), "_\\d+_");
        cid = FetchParamUtil.getMatherStr(cid, "\\d+");
        String commentsJsonTargetUrl = "http://comment.xcar.com.cn/interface/index.php?iact=CommentLevel&cid=" + cid + "&action=getNewsComment&sort=ups&ctype=0&page=1&limit=10000&_=1541592906003";
        HttpGet httpGet = new HttpGet(commentsJsonTargetUrl);
        setCommentListHeader(httpGet);
        return httpGet;
    }

    private void setCommentListHeader(HttpGet httpGet) {
        httpGet.setHeader("Host", "comment.xcar.com.cn");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
    }

    private Result<HandlerResultDTO> createCommentNews(RequestContext requestContext) {
        Result<LoginResultDTO> resultDTOResult = aiKaLoginHandler.login(requestContext);
        if (!resultDTOResult.getSuccess()) {
            logger.error("[DiYiDianDongHandler.comment] requestContext" + requestContext);
            return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
        }
        try {
            LoginResultDTO loginResultDTO = resultDTOResult.getData();
            CloseableHttpClient httpClient = loginResultDTO.getHttpClient();
            HttpPost httpPost = createCommentNewsHttpPost(requestContext);
            setCommentNewsHeader(httpPost);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = null;
            if (entity != null) {
                content = EntityUtils.toString(entity, "utf-8");
                AiKaCommentResultDTO aiKaCommentResultDTO = JSONObject.parseObject(content, AiKaCommentResultDTO.class);
                if ("0".equals(aiKaCommentResultDTO.getMsg())) {
                    HandlerResultDTO handlerResultDTO = ResultParamUtil.createHandlerResultDTO(requestContext, content);
                    CommentInfo commentInfo = ResultParamUtil.createCommentInfo(requestContext, content);
                    save(new SaveContext(commentInfo));
                    return new Result(handlerResultDTO);
                }
            }
            return new Result<>(ResultCodeEnum.HANDLE_FAILED);
        } catch (Exception e) {
            logger.error("[AiKaHandler.createCommentNews]", e);
            return new Result<>(ResultCodeEnum.HANDLE_FAILED);
        }
    }


    private HttpPost createCommentNewsHttpPost(RequestContext requestContext) {

        /**
         * action: Dresponse
         * cid: 2020939
         * content: 车是人生新的起点
         * ctype: 0
         * curl: http://info.xcar.com.cn/201808/news_2020939_1.html
         * istoeditor: 0
         * date: 1539495313382
         */
        HttpPost httpPost = new HttpPost(TARGET_COMMENT_NEWS);
        setCommentNewsHeader(httpPost);
        List<NameValuePair> nameValuePairs = new ArrayList<>();

        String cid = FetchParamUtil.getMatherStr(requestContext.getPrefixUrl(), "_\\d+_");
        cid = cid.replaceAll("_", "");

        try {
            nameValuePairs.add(new BasicNameValuePair("action", "Dresponse"));
            nameValuePairs.add(new BasicNameValuePair("cid", cid));
            nameValuePairs.add(new BasicNameValuePair("content", requestContext.getContent().getText()));
            nameValuePairs.add(new BasicNameValuePair("ctype", "0"));
            nameValuePairs.add(new BasicNameValuePair("curl", requestContext.getPrefixUrl()));
            nameValuePairs.add(new BasicNameValuePair("istoeditor", "0"));
            nameValuePairs.add(new BasicNameValuePair("date", String.valueOf(new Date().getTime())));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            return httpPost;
        } catch (Exception e) {
            logger.error("[AiKaHandler.createCommentNewsHttpPost]", e);
            return null;
        }

    }


    private void setCommentNewsHeader(HttpPost httpPost) {
        /**
         * Host: comment.xcar.com.cn
         * Origin: http://comment.xcar.com.cn
         * Referer: http://comment.xcar.com.cn/comments_news.php?cid=2020939&ctype=0&curl=http://info.xcar.com.cn/201808/news_2020939_1.html
         * User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36
         * X-Requested-With: XMLHttpRequest
         */
        httpPost.setHeader("Host", "comment.xcar.com.cn");
        httpPost.setHeader("Origin", "http://comment.xcar.com.cn");
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        httpPost.setHeader("Origin", "http://comment.xcar.com.cn");
    }
}
