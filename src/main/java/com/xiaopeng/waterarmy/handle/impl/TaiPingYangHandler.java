package com.xiaopeng.waterarmy.handle.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.regexp.internal.RE;
import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.constants.ResultConstants;
import com.xiaopeng.waterarmy.common.enums.ResultCodeEnum;
import com.xiaopeng.waterarmy.common.util.HtmlReadUtil;
import com.xiaopeng.waterarmy.handle.PlatformHandler;
import com.xiaopeng.waterarmy.handle.Util.FetchParamUtil;
import com.xiaopeng.waterarmy.handle.Util.ResultParamUtil;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.handle.param.SaveContext;
import com.xiaopeng.waterarmy.handle.result.HandlerResultDTO;
import com.xiaopeng.waterarmy.handle.result.LoginResultDTO;
import com.xiaopeng.waterarmy.handle.result.TaiPingYangCommentResultDTO;
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
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TaiPingYangHandler extends PlatformHandler {

    private static Logger logger = LoggerFactory.getLogger(TaiPingYangHandler.class);

    private static final String TOPIC = "topic";

    private static final String FID = "fid";

    private static final String TARGET_COMMENT_URL = "https://bbs.pcauto.com.cn/action/post/create.ajax";

    private static final String TARGET_PUBLISH_URL = "https://bbs.pcauto.com.cn/action/topic/create.ajax";

    private static final String CHECK_LOGIN = "https://my.pcauto.com.cn/intf/checkLogin.jsp?act=checkLogin";

    private static final String FETCH_TOPICS = "https://bbs.pcauto.com.cn/intf/user/_topics.jsp";

    private static final String TOPIC_PREFIX = "https://bbs.pcauto.com.cn/topic-";


    @Autowired
    TaiPingYangLoginHandler taiPingYangLoginHandler;


    @Override
    public Result<HandlerResultDTO> publish(RequestContext requestContext) {

        Result<LoginResultDTO> resultDTOResult = taiPingYangLoginHandler.login(requestContext.getUserId());
        if (!resultDTOResult.getSuccess()) {
            logger.error("[TaiPingYangHandler.publish] requestContext" + requestContext);
            return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
        }
        try {
            LoginResultDTO loginResultDTO = resultDTOResult.getData();
            CloseableHttpClient httpClient = loginResultDTO.getHttpClient();
            HttpPost httpPost = createPublishHttpPost(requestContext);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = null;
            if (entity != null) {
                content = EntityUtils.toString(entity, "utf-8");
                JSONObject jsonObject = JSONObject.parseObject(content);
                Integer status = (Integer) jsonObject.get("status");
                Integer tid = (Integer) jsonObject.get("tid");
                if (status.intValue()==0 && tid >0) {
                    String url = generatePublishUrlByTopicId(tid);
                    PublishInfo publishInfo = ResultParamUtil.createPublishInfo(requestContext, content, url);
                    save(new SaveContext(publishInfo));
                    HandlerResultDTO handlerResultDTO = ResultParamUtil.createHandlerResultDTO(requestContext, content, url);
                    return new Result(handlerResultDTO);
                }
            }
            return new Result<>(ResultCodeEnum.HANDLE_FAILED);
        } catch (Exception e) {
            logger.error("[TaiPingYangHandler.comment] error!", e);
            //处理失败的回复，把context记录下来，可以决定是否重新扫描,并且记录失败原因
            return new Result<>(ResultCodeEnum.HANDLE_FAILED);
        }
    }

    private String generatePublishUrlByTopicId(Integer tid) {
        return TOPIC_PREFIX + tid + ".html";
    }

    @Override
    public Result<HandlerResultDTO> comment(RequestContext requestContext) {
        Result<LoginResultDTO> resultDTOResult = taiPingYangLoginHandler.login(requestContext.getUserId());
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
                TaiPingYangCommentResultDTO resultDTO = JSONObject.parseObject(content, TaiPingYangCommentResultDTO.class);
                if (resultDTO.getStatus().equals("0")) {
                    //评论成功
                    HandlerResultDTO handlerResultDTO = ResultParamUtil.createHandlerResultDTO(requestContext, content);
                    CommentInfo commentInfo = ResultParamUtil.createCommentInfo(requestContext, content);

                    save(new SaveContext(commentInfo));

                    return new Result(handlerResultDTO);
                }
            }
            //处理失败的回复，把context记录下来，可以决定是否重新扫描,并且记录失败原因,这个在业务层处理，决定是否要记录未处理成功的数据
            return new Result<>(ResultCodeEnum.HANDLE_FAILED);
        } catch (Exception e) {
            logger.error("[TaiPingYangHandler.comment] error!", e);
            //处理失败的回复，把context记录下来，可以决定是否重新扫描,并且记录失败原因
            return new Result<>(ResultCodeEnum.HANDLE_FAILED);
        }

    }

    @Override
    public Result<HandlerResultDTO> read(RequestContext requestContext) {
        HtmlReadUtil.read(requestContext.getTargetUrl());
        HandlerResultDTO handlerResultDTO = ResultParamUtil.createHandlerResultDTO(requestContext, "{success:true,url:"+requestContext.getTargetUrl());
        return new Result(handlerResultDTO);
    }

    @Override
    public Result<HandlerResultDTO> praise(RequestContext requestContext) {
        return null;
    }

    private HttpPost createCommentPost(RequestContext requestContext) {
        //太平洋评论需要的参数
        /**
         * tid: 16868614
         * fid: 24155
         * message: 今天取看过 不错
         * needCaptcha: false
         * captcha:
         * sendMsg: true
         * minContentLength: 1
         * maxContentLength: 500000
         */

        String topic = null;
        if (requestContext.getRequestParam() != null) {
            Object object = requestContext.getRequestParam().get(TOPIC);
            if (object != null) {
                topic = (String) object;
            }
        }
        if (topic == null) { //https://bbs.pcauto.com.cn/topic-16868614.html
            //太平洋的可以直接从url中截取
            String pattern = "(\\d+)";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(requestContext.getPrefixUrl());
            if (m.find()) {
                topic = m.group(0);
            }
        }

        String targetUrl = requestContext.getTargetUrl();
        if (StringUtils.isBlank(targetUrl)) {
            targetUrl = TARGET_COMMENT_URL;
        }
        HttpPost httpPost = new HttpPost(targetUrl);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("message", requestContext.getContent().getText()));
        nameValuePairs.add(new BasicNameValuePair("fid", this.getFid(requestContext.getPrefixUrl())));
        nameValuePairs.add(new BasicNameValuePair("tid", topic));
        nameValuePairs.add(new BasicNameValuePair("sendMsg", "true"));
        nameValuePairs.add(new BasicNameValuePair("minContentLength", "1"));
        nameValuePairs.add(new BasicNameValuePair("maxContentLength", "500000"));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (Exception e) {
            logger.error("[TaiPingYangHandler.createCommentPost]createCommentPost  UrlEncodedFormEntity error! nameValuePairs" + nameValuePairs);
            return null;
        }
        return httpPost;
    }

    private String getFid(String url) {

        int count = 3;
        while (count > 0) {
            try {
                Document doc = Jsoup.connect(url).timeout(2000).get();
                Element content = doc.getElementById("fixBox");
                String tid = content.attr("data-src");
                return tid;
            } catch (Exception e) {
                count--;
            }
        }
        return null;
    }





    private HttpPost createPublishHttpPost(RequestContext requestContext) {
        /**
         * target https://bbs.pcauto.com.cn/forum-20095.html 需要发帖的论坛
         */
        /**
         * editor: 1
         * sendMsg: true
         * fid: 20095
         * type:
         * topicTitleMaxLength: 40
         * category: 综合
         * uploadKeepSource: false
         * topicContentMinLength: 1
         * topicContentMaxLength: 500000
         * title: 国庆带着小白出行
         * message: 前几天陪媳妇开车去三门峡市办事，因为我主要是担任司机角色，她去忙她的，我就有时间在附近随便溜达了。未完待续
         * upload2Album: 2982656
         * file:
         * file:
         * albumId: 2982656
         */
        String fid = null;
        if (requestContext.getRequestParam() != null) {
            Object object = requestContext.getRequestParam().get(FID);
            if (object != null) {
                fid = (String) object;
            }
        }
        if (fid == null) { //https://bbs.pcauto.com.cn/topic-16868614.html
            //太平洋的可以直接从url中截取
            String pattern = "-(\\d+).html";
            String temp = FetchParamUtil.getMatherStr(requestContext.getPrefixUrl(),pattern);
            fid = FetchParamUtil.getMatherStr(temp,"(\\d+)");
        }

        String targetUrl = requestContext.getTargetUrl();
        if (StringUtils.isBlank(targetUrl)) {
            targetUrl = TARGET_PUBLISH_URL;
        }
        HttpPost httpPost = new HttpPost(targetUrl);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("editor", "1"));
        nameValuePairs.add(new BasicNameValuePair("sendMsg", "true"));
        nameValuePairs.add(new BasicNameValuePair("fid", fid));
        nameValuePairs.add(new BasicNameValuePair("topicTitleMaxLength", "40"));
        nameValuePairs.add(new BasicNameValuePair("category", "综合"));
        nameValuePairs.add(new BasicNameValuePair("uploadKeepSource", "false"));
        nameValuePairs.add(new BasicNameValuePair("topicContentMinLength", "1"));
        nameValuePairs.add(new BasicNameValuePair("topicContentMaxLength", "500000"));
        nameValuePairs.add(new BasicNameValuePair("title", requestContext.getContent().getTitle()));
        nameValuePairs.add(new BasicNameValuePair("message", requestContext.getContent().getText()));
        nameValuePairs.add(new BasicNameValuePair("upload2Album", "2982656")); //由于现在没有图片需求这里就写死相册好了
        nameValuePairs.add(new BasicNameValuePair("albumId", "2982656"));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (Exception e) {
            logger.error("[TaiPingYangHandler.createCommentPost]createCommentPost  UrlEncodedFormEntity error! nameValuePairs" + nameValuePairs);
            return null;
        }
        return httpPost;
    }


    //太平洋这一点比较坑，发帖成功后没有返回我帖子的id，只能通过请求帖子列表，来获取帖子的url
    //一般来说取第一个就好了

    /**
     * @param requestContext
     * @return
     */
    private String fetchPublisUrl(RequestContext requestContext, LoginResultDTO resultDTO) {
        /**
         * audited: false
         * userId: 47446648
         * callback: show
         * pageSize: 15
         * pageNo: 1
         */

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("audited", "false"));
            String userId = this.getThirdPlatformId(requestContext, resultDTO);
            nameValuePairs.add(new BasicNameValuePair("userId", userId));
            nameValuePairs.add(new BasicNameValuePair("pageSize", "1"));
            nameValuePairs.add(new BasicNameValuePair("pageNo", "1"));
            String str = EntityUtils.toString(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            String url = FETCH_TOPICS + "?" + str;
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = resultDTO.getHttpClient().execute(httpGet);
            HttpEntity entity = response.getEntity();
            String content = null;
            if (entity != null) {
                content = EntityUtils.toString(entity, "utf-8");
                JSONObject jsonObject = JSONObject.parseObject(content);
                JSONArray jsonArray = (JSONArray) jsonObject.get("list");
                JSONObject object = (JSONObject) jsonArray.get(0);
                String topicUrl = (String) object.get("url");
                String title = (String) object.get("title");
                if (requestContext.getContent().getTitle().equals(title)) {
                    return "https:" + topicUrl;
                }
            }
        } catch (Exception e) {
            logger.error("[TaiPingYangHandler.fetchPublisUrl] error!", e);
        }

        return null;
    }

    /**
     * 第三方平等的id
     *
     * @param requestContext
     * @param resultDTO
     * @return
     */
    private String getThirdPlatformId(RequestContext requestContext, LoginResultDTO resultDTO) {
        try {
            HttpGet httpGet = new HttpGet(CHECK_LOGIN);
            CloseableHttpResponse response = resultDTO.getHttpClient().execute(httpGet);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, "utf-8");
            String result = FetchParamUtil.getMatherStr(content, "\"id\":\"\\d+\"");
            result = FetchParamUtil.getMatherStr(result, "(\\d+)");
            return result;
        } catch (Exception e) {
            logger.error("[com.xiaopeng.waterarmy.handle.impl.TaiPingYangHandler.getThirdPlatformId],resultDTO " + resultDTO + "RequestContext " + requestContext);
            return null;
        }
    }
}


