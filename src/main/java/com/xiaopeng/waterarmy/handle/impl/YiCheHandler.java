package com.xiaopeng.waterarmy.handle.impl;

import com.alibaba.fastjson.JSONObject;
import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.constants.HttpConstants;
import com.xiaopeng.waterarmy.common.constants.ResultConstants;
import com.xiaopeng.waterarmy.common.enums.ResultCodeEnum;
import com.xiaopeng.waterarmy.handle.PlatformHandler;
import com.xiaopeng.waterarmy.handle.Util.FetchParamUtil;
import com.xiaopeng.waterarmy.handle.Util.ResultParamUtil;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.handle.param.SaveContext;
import com.xiaopeng.waterarmy.handle.result.HandlerResultDTO;
import com.xiaopeng.waterarmy.handle.result.LoginResultDTO;
import com.xiaopeng.waterarmy.model.dao.CommentInfo;
import com.xiaopeng.waterarmy.model.dao.PublishInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class YiCheHandler extends PlatformHandler {

    private static Logger logger = LoggerFactory.getLogger(YiCheHandler.class);

    private static final String TOPIC = "topic";

    private static final String ForumApp = "forumApp";

    private static final String TARGET_COMMENT_URL = "http://baa.bitauto.com/baoma3xi/Ajax/CreateReply.aspx";

    private static final String TARGET_PUBLISH_URL = "http://baa.bitauto.com/equinox/Ajax/CreateTopic.aspx";

    private static final String TARGET_PUBLISH_SUFFIX = "posttopic-0.html";

    @Autowired
    private YiCheLoginHandler yiCheLoginHandler;

    @Override
    public Result<HandlerResultDTO> publish(RequestContext requestContext) {
        Result<LoginResultDTO> resultDTOResult = yiCheLoginHandler.login(requestContext.getUserId());
        if (!resultDTOResult.getSuccess()) {
            logger.error("[TaiPingYangHandler.publish] requestContext" + requestContext);
            return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
        }
        try {
            LoginResultDTO loginResultDTO = resultDTOResult.getData();
            CloseableHttpClient httpClient = loginResultDTO.getHttpClient();
            HttpPost httpPost = createPublishHttpPost(requestContext, loginResultDTO);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = null;
            if (entity != null) {
                content = EntityUtils.toString(entity, "utf-8");
                JSONObject jsonObject = JSONObject.parseObject(content);
                Integer status = (Integer) jsonObject.get("tid");
                Integer tid = (Integer) jsonObject.get("pid");
                String returnUrl = (String) jsonObject.get("returnUrl");
                PublishInfo publishInfo = ResultParamUtil.createPublishInfo(requestContext, content, returnUrl);
                save(new SaveContext(publishInfo));
                HandlerResultDTO handlerResultDTO = ResultParamUtil.createHandlerResultDTO(requestContext, content, returnUrl);
                return new Result(handlerResultDTO);
            }
            return new Result<>(ResultCodeEnum.HANDLE_FAILED);
        } catch (Exception e) {
            logger.error("[TaiPingYangHandler.comment] error!", e);
            //处理失败的回复，把context记录下来，可以决定是否重新扫描,并且记录失败原因
            return new Result<>(ResultCodeEnum.HANDLE_FAILED);
        }
    }


    private String createPublishUrl(RequestContext requestContext, LoginResultDTO loginResultDTO) {
        //http://baa.bitauto.com/equinox/Ajax/CreateTopic.aspx?fid=0&fgid=8625&tid=0&pid=0&parentId=0
        try {
            String prefixUrl = null;
            if (requestContext.getPrefixUrl().endsWith("/")) {
                prefixUrl = requestContext.getPrefixUrl() + TARGET_PUBLISH_SUFFIX;
            } else {
                prefixUrl = requestContext.getPrefixUrl() + "/" + TARGET_PUBLISH_SUFFIX;
            }
            HttpGet httpGet = new HttpGet(prefixUrl);
            setHeader(httpGet);
            CloseableHttpClient client = loginResultDTO.getHttpClient();
            CloseableHttpResponse response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, "utf-8");

            String target = FetchParamUtil.getMatherStr(content, "\\{fid:.*\\}");
            JSONObject jsonObject = JSONObject.parseObject(target);
            int fid = (int) jsonObject.get("fid");
            int fgid = (int) jsonObject.get("fgid");
            int pid = (int) jsonObject.get("pid");
            int parentId = (int) jsonObject.get("parentId");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair("fid", String.valueOf(fid)));
            nameValuePairs.add(new BasicNameValuePair("fgid", String.valueOf(fgid)));
            nameValuePairs.add(new BasicNameValuePair("tid", "0"));
            nameValuePairs.add(new BasicNameValuePair("pid", String.valueOf(pid)));
            nameValuePairs.add(new BasicNameValuePair("parentId", String.valueOf(parentId)));

            String str = EntityUtils.toString(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            String url = TARGET_PUBLISH_URL + "?" + str;
            return url;
        } catch (Exception e) {
            logger.error("[YiCheHandler.createPublishUrl]", e);
        }
        return null;
    }

    private HttpPost createPublishHttpPost(RequestContext requestContext, LoginResultDTO loginResultDTO) {
        /**
         * target 需要发帖的栏目 http://baa.bitauto.com/cs55
         *  拼接后访问http://baa.bitauto.com/cs55/posttopic-0.html，获取fid:0,fgid:8775,tid:0,pid:0,parentId:0
         *  发帖target http://baa.bitauto.com/cs55/Ajax/CreateTopic.aspx?fid=0&fgid=8775&tid=0&pid=0&parentId=0
         */


        /**
         * attachments: []
         * hdnAttachmentList:
         * digest: 0
         * title: 易车上的价格都是真的吗
         * vote_option:
         * vote_option:
         * vote_option:
         * hideDeleteOptionId:
         * hdnCheckCode:
         * hdnautocomplete1:
         * message: <p>易车上的价格都是真的吗，4s店也是这个价格吗</p>
         * guid: 2910b008-fb7e-8e3d-51c4-b86e7c1e54ea
         * hasContent: true
         */

        //http://baa.bitauto.com/equinox/Ajax/CreateTopic.aspx?fid=0&fgid=8625&tid=0&pid=0&parentId=0
        String targetUrl = createPublishUrl(requestContext, loginResultDTO);
        if (targetUrl == null) {
            return null;
        }
        String guid = generateGuid();
        HttpPost httpPost = new HttpPost(targetUrl);
        setHeader(httpPost);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("attachments", "[]"));
        nameValuePairs.add(new BasicNameValuePair("hdnAttachmentList", null));
        nameValuePairs.add(new BasicNameValuePair("digest", "0"));
        nameValuePairs.add(new BasicNameValuePair("title", requestContext.getContent().getTitle()));
        nameValuePairs.add(new BasicNameValuePair("message", requestContext.getContent().getText()));
        nameValuePairs.add(new BasicNameValuePair("guid", guid));
        nameValuePairs.add(new BasicNameValuePair("hasContent", "true"));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (Exception e) {
            logger.error("[YiCheHandler.createCommentPost]createPublishHttpPost  UrlEncodedFormEntity error! nameValuePairs" + nameValuePairs);
            return null;
        }
        return httpPost;
    }


    @Override
    public Result<HandlerResultDTO> comment(RequestContext requestContext) {
        Result<LoginResultDTO> resultDTOResult = yiCheLoginHandler.login(requestContext.getUserId());
        if (!resultDTOResult.getSuccess()) {
            logger.error("[TaiPingYangHandler.comment] requestContext" + requestContext);
            return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
        }
        try {
            LoginResultDTO loginResultDTO = resultDTOResult.getData();
            CloseableHttpClient httpClient = loginResultDTO.getHttpClient();
            HttpPost httpPost = createCommentPost(requestContext);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = null;
            if (entity != null) {
                content = EntityUtils.toString(entity, "utf-8");
                if (content.contains("isReply")) {//评论成功
                    //评论成功
                    HandlerResultDTO handlerResultDTO = ResultParamUtil.createHandlerResultDTO(requestContext, content);
                    CommentInfo commentInfo = ResultParamUtil.createCommentInfo(requestContext, content);
                    save(new SaveContext(commentInfo));
                    return new Result(handlerResultDTO);
                }
            }
        } catch (Exception e) {
            logger.error("[TaiPingYangHandler.comment] error!", e);
        }
        return new Result<>(ResultCodeEnum.HANDLE_FAILED);
    }

    @Override
    public Result<HandlerResultDTO> read(RequestContext requestContext) {
        return null;
    }

    @Override
    public Result<HandlerResultDTO> praise(RequestContext requestContext) {
        return null;
    }

    private HttpPost createCommentPost(RequestContext requestContext) {
        /**
         * 自动评论的流程
         * 1.获取需要评论的帖子的url
         * 2.爬取该url，通过content获取相应参数
         * 3.组装页面参数及动态参数
         */


        //易车网评论需要的参数
        /**
         * attachments: []
         * title:
         * message: 哈哈，今天去看很赞
         * fid: 0
         * tid: 15742669
         * fgid: 0
         * forumApp: xiaopengqicheg3
         * type: 1
         * attachdesc:
         * txtCheckCode:
         * parentid:
         * floor:
         */

        String topic = null;
        String forumApp = null;
        if (requestContext.getRequestParam() != null) {
            Object object = requestContext.getRequestParam().get(TOPIC);
            if (object != null) {
                topic = (String) object;
            }

            Object object1 = requestContext.getRequestParam().get(ForumApp);
            if (object1 != null) {
                forumApp = (String) object1;
            }
        }

        if (topic == null) { //http://baa.bitauto.com/xiaopengqicheg3/thread-15742669.html
            topic = getTopic(requestContext.getPrefixUrl());
        }

        if (forumApp == null) { //http://baa.bitauto.com/xiaopengqicheg3/thread-15742669.html
            forumApp = getForumApp(requestContext.getPrefixUrl());
        }


        HttpPost httpPost = new HttpPost(TARGET_COMMENT_URL);
        setHeader(httpPost);
        List<NameValuePair> nameValuePairs = new ArrayList<>();


        nameValuePairs.add(new BasicNameValuePair("attachments", "[]"));
        nameValuePairs.add(new BasicNameValuePair("title", null));
        nameValuePairs.add(new BasicNameValuePair("message", requestContext.getContent().getText()));
        nameValuePairs.add(new BasicNameValuePair("fid", "0"));
        nameValuePairs.add(new BasicNameValuePair("tid", topic));
        nameValuePairs.add(new BasicNameValuePair("fgid", "0"));
        nameValuePairs.add(new BasicNameValuePair("forumApp", forumApp));
        nameValuePairs.add(new BasicNameValuePair("type", "1"));
        nameValuePairs.add(new BasicNameValuePair("attachdesc", null));
        nameValuePairs.add(new BasicNameValuePair("txtCheckCode", null));
        nameValuePairs.add(new BasicNameValuePair("parentid", null));
        nameValuePairs.add(new BasicNameValuePair("floor", null));


        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (Exception e) {
            logger.error("[TaiPingYangHandler.createCommentPost]createCommentPost  UrlEncodedFormEntity error! nameValuePairs" + nameValuePairs);
            return null;
        }
        return httpPost;
    }


    private void setHeader(HttpPost httpPost) {
        httpPost.setHeader("Origin", "http://baa.bitauto.com");
        httpPost.setHeader("Referer", "http://baa.bitauto.com");
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
        httpPost.setHeader("Host", "baa.bitauto.com");
        httpPost.setHeader("X-Prototype-Version", "1.7.1");
        httpPost.setHeader("X-Requested-With", "XMLHttpRequest");
        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpPost.setHeader("Connection", "keep-alive");
        httpPost.setHeader("Accept", "text/javascript, text/html, application/xml, text/xml, */*");
        httpPost.setHeader("Accept-Encoding", "gzip, deflate");
        httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
        httpPost.setHeader("Connection", "keep-alive");
        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
    }

    private void setHeader(HttpGet httpGet) {
        httpGet.setHeader("Origin", "http://baa.bitauto.com");
        httpGet.setHeader("Referer", "http://baa.bitauto.com");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
        httpGet.setHeader("Host", "baa.bitauto.com");
        httpGet.setHeader("X-Prototype-Version", "1.7.1");
        httpGet.setHeader("X-Requested-With", "XMLHttpRequest");
        httpGet.setHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Accept", "text/javascript, text/html, application/xml, text/xml, */*");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
    }


    private String getTopic(String url) {
        String pattern = "-(\\d+).html";
        String temp = FetchParamUtil.getMatherStr(url, pattern);
        return FetchParamUtil.getMatherStr(temp, "(\\d+)");
    }

    private String getForumApp(String url) {
        String pattern = ".com(\\/.*\\/)";
        String temp = FetchParamUtil.getMatherStr(url, pattern);
        temp = FetchParamUtil.getMatherStr(temp, "(/.*\\/)");
        return temp.replaceAll("/", "");
    }


    private static String generateGuid() {
        StringBuilder prefix = new StringBuilder();
        StringBuilder suffix = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 7; i++) {
            prefix.append(random.nextInt(9));
        }
        for (int i = 0; i < 7; i++) {
            suffix.append(random.nextInt(9));
        }
        return "a" + prefix.toString() + "-87bb-5d43-394f-e0423" + suffix.toString();
    }
}
