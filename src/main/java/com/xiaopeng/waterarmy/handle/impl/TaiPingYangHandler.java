package com.xiaopeng.waterarmy.handle.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.constants.RequestConsts;
import com.xiaopeng.waterarmy.common.enums.ResultCodeEnum;
import com.xiaopeng.waterarmy.common.enums.TaskEntryTypeEnum;
import com.xiaopeng.waterarmy.common.util.HtmlPlayUtil;
import com.xiaopeng.waterarmy.common.util.HtmlReadUtil;
import com.xiaopeng.waterarmy.handle.PlatformHandler;
import com.xiaopeng.waterarmy.handle.Util.FetchParamUtil;
import com.xiaopeng.waterarmy.handle.Util.ResolveUtil;
import com.xiaopeng.waterarmy.handle.Util.ResultParamUtil;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.handle.param.SaveContext;
import com.xiaopeng.waterarmy.handle.result.HandlerResultDTO;
import com.xiaopeng.waterarmy.handle.result.LoginResultDTO;
import com.xiaopeng.waterarmy.handle.result.TaiPingYangCommentResultDTO;
import com.xiaopeng.waterarmy.model.dao.CommentInfo;
import com.xiaopeng.waterarmy.model.dao.PraiseInfo;
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
import org.apache.http.impl.client.HttpClients;
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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    private static final String TARGET_COMMENT_NEWS = "https://cmt.pcauto.com.cn/action/comment/create.jsp";

    private static final String TARGET_COMMENT_NEWS_PRAISE = "https://cmt.pcauto.com.cn/action/comment/support.jsp";

    private static final String TARGET_COMMENT_CHEZHU_MESSAGE = "https://price.pcauto.com.cn/comment/interface/send_message_to_bip.jsp";

    private static final String TARGET_COMMENT_CHE_ZHU = "https://cmt.pcauto.com.cn/action/comment/create.jsp?urlHandle=1";

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

            if (requestContext.getImageInputStreams() != null && !requestContext.getImageInputStreams().isEmpty()) {
                TaiPingYangImage taiPingYangImage = taiPingYangImageUpload(httpClient, requestContext.getImageInputStreams().get(0));
                if (logger.isDebugEnabled()) {
                    logger.info("太平洋图片上传结果：" + taiPingYangImage);
                }
                requestContext.getContent().setText(requestContext.getContent().getText() + "[img=" + taiPingYangImage.width + "," + taiPingYangImage.height + ",5Y+v5re75YqgMjAw5a2X5Lul5YaF55qE5o+P6L+w]" + taiPingYangImage.url + "[/img]");
            }

            HttpPost httpPost = createPublishHttpPost(requestContext);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = null;
            if (entity != null) {
                content = EntityUtils.toString(entity, "utf-8");
                JSONObject jsonObject = JSONObject.parseObject(content);
                Integer status = (Integer) jsonObject.get("status");
                Integer tid = (Integer) jsonObject.get("tid");
                if (status.intValue() == 0 && tid > 0) {
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


    /**
     * 太平洋图片资源上传
     *
     * @param httpClient
     * @param inputStream
     * @return
     */
    public TaiPingYangImage taiPingYangImageUpload(HttpClient httpClient, InputStream inputStream) throws IOException {
        String uploadUrl = "https://upctemp.pcauto.com.cn/upload_quick.jsp?application=bbs6&keepSrc=true&readExif=true";//+ common_session_id;
        HttpPost httpPost = new HttpPost(uploadUrl);
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");

        HttpEntity entity = MultipartEntityBuilder.create()
                .addBinaryBody("file", inputStream, ContentType.create("application/octet-stream"), "file.png")
                .addTextBody("id", "WU_FILE_1")
                .addTextBody("name", "image.png")
                .build();
        httpPost.setEntity(entity);

        HttpResponse httpResponse = httpClient.execute(httpPost);
        String content = EntityUtils.toString(httpResponse.getEntity());
        JSONObject jsonObject = JSON.parseObject(content);
        int retCode = jsonObject.getInteger("retCode");
        if (retCode == 0) {
            return JSON.parseObject(jsonObject.getJSONArray("files").get(0).toString(), TaiPingYangImage.class);
        } else {
            throw new IOException("太平洋图片上传失败：" + content);
        }
    }

    public static class TaiPingYangImage {
        public String fileSize;
        public int height;
        public int rid;
        public int width;
        public String fileName;
        public String audit;
        public String orgFileName;
        public String isorg;
        public String url;

        @Override
        public String toString() {
            return "YiCheImage{" +
                    "fileSize='" + fileSize + '\'' +
                    ", height=" + height +
                    ", rid=" + rid +
                    ", width=" + width +
                    ", fileName='" + fileName + '\'' +
                    ", audit='" + audit + '\'' +
                    ", orgFileName='" + orgFileName + '\'' +
                    ", isorg='" + isorg + '\'' +
                    ", url='" + url + '\'' +
                    '}';
        }
    }

    private String generatePublishUrlByTopicId(Integer tid) {
        return TOPIC_PREFIX + tid + ".html";
    }

    @Override
    public Result<HandlerResultDTO> comment(RequestContext requestContext) {

        if (TaskEntryTypeEnum.TAIPINGYANGNEWSCOMMENT.equals(requestContext.getHandleEntryType())) {
            return commentNews(requestContext);
        }
        if (TaskEntryTypeEnum.TAIPINGYANGCHEZHUCOMMENT.equals(requestContext.getHandleEntryType())) {
            return commentChezhu(requestContext);
        }
        return commentForum(requestContext);
    }

    private Result<HandlerResultDTO> commentForum(RequestContext requestContext) {
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
                content = content.trim();
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
        if (TaskEntryTypeEnum.TAIPINGYANGNEWSCOMMENTPRAISE.equals(requestContext.getHandleEntryType())) {
            return commentNewsPraise(requestContext);
        }
        return new Result<>(ResultCodeEnum.HANDLER_NOT_FOUND);
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
            String temp = FetchParamUtil.getMatherStr(requestContext.getPrefixUrl(), pattern);
            fid = FetchParamUtil.getMatherStr(temp, "(\\d+)");
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


    private Result<HandlerResultDTO> commentNews(RequestContext requestContext) {


        Result<LoginResultDTO> resultDTOResult = taiPingYangLoginHandler.login(requestContext.getUserId());
        if (!resultDTOResult.getSuccess()) {
            logger.error("[TaiPingYangHandler.comment] requestContext" + requestContext);
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
                content = content.trim();
                TaiPingYangCommentResultDTO resultDTO = JSONObject.parseObject(content, TaiPingYangCommentResultDTO.class);
                if (resultDTO.getStatus().equals("0")) {
                    //新闻评论成功
                    if ((resultDTO.getResultCode()) == 0) {
                        //评论成功
                        HandlerResultDTO handlerResultDTO = ResultParamUtil.createHandlerResultDTO(requestContext, content);
                        CommentInfo commentInfo = ResultParamUtil.createCommentInfo(requestContext, content);
                        save(new SaveContext(commentInfo));
                        return new Result(handlerResultDTO);
                    } else {
                        //评论失败
                        return new Result(ResultCodeEnum.HANDLE_FAILED, content);
                    }
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

    private Result<HandlerResultDTO> commentChezhu(RequestContext requestContext) {
        Result<LoginResultDTO> resultDTOResult = taiPingYangLoginHandler.login(requestContext.getUserId());
        if (!resultDTOResult.getSuccess()) {
            logger.error("[TaiPingYangHandler.comment] requestContext" + requestContext);
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
                content = content.trim();
                TaiPingYangCommentResultDTO resultDTO = JSONObject.parseObject(content, TaiPingYangCommentResultDTO.class);
                if (resultDTO.getStatus().equals("0")) {
                    //新闻评论成功
                    if ((resultDTO.getResultCode()) == 0) {
                        //评论成功
                        //发起通知请求
                        /*HttpGet httpGet = createCheZhuCommentNoticeHttpGet(requestContext,httpClient,resultDTO);
                        CloseableHttpResponse response1 = httpClient.execute(httpGet);
                        String content1 = EntityUtils.toString(response1.getEntity(), "utf-8");*/

                        HandlerResultDTO handlerResultDTO = ResultParamUtil.createHandlerResultDTO(requestContext, content);
                        CommentInfo commentInfo = ResultParamUtil.createCommentInfo(requestContext, content);
                        save(new SaveContext(commentInfo));
                        return new Result(handlerResultDTO);
                    } else {
                        //评论失败
                        return new Result(ResultCodeEnum.HANDLE_FAILED, content);
                    }
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


    private HttpGet createCheZhuCommentNoticeHttpGet(RequestContext requestContext, CloseableHttpClient httpClient, TaiPingYangCommentResultDTO resultDTO) {
        /**
         * req-enc: utf-8
         * commentId: 881901
         * content: 今年的梦想
         * accountId: 47446648
         * sgId: 10349
         * modelId: 38952
         * account: eiaxal8142
         * replyId: 32202248
         * floor: 1
         */

        try {
            HttpGet httpGet = new HttpGet(requestContext.getPrefixUrl());
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String content = null;
            String mobileId = null;
            String sgId = null;
            if (entity != null) {
                content = EntityUtils.toString(entity, "utf-8");
                mobileId = FetchParamUtil.getMatherStr(content, "var modelId = \"\\d+");
                mobileId = FetchParamUtil.getMatherStr(mobileId, "\\d+");

                sgId = FetchParamUtil.getMatherStr(content, "var SGID='\\d+");
                sgId = FetchParamUtil.getMatherStr(sgId, "\\d+");
            }

            String commentId = FetchParamUtil.getMatherStr(requestContext.getPrefixUrl(), "view_\\d+.html");
            commentId = FetchParamUtil.getMatherStr(commentId, "\\d+");

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("req-enc", "utf-8"));
            nameValuePairs.add(new BasicNameValuePair("commentId", commentId));
            nameValuePairs.add(new BasicNameValuePair("content", requestContext.getContent().getText()));
            nameValuePairs.add(new BasicNameValuePair("accountId", String.valueOf(resultDTO.getUserId())));
            nameValuePairs.add(new BasicNameValuePair("sgId", sgId));
            nameValuePairs.add(new BasicNameValuePair("modelId", mobileId));
            nameValuePairs.add(new BasicNameValuePair("account", resultDTO.getShowName()));
            nameValuePairs.add(new BasicNameValuePair("replyId", String.valueOf(resultDTO.getCommentId())));
            nameValuePairs.add(new BasicNameValuePair("floor", String.valueOf(resultDTO.getFloor())));

            String str = EntityUtils.toString(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            String url = TARGET_COMMENT_CHEZHU_MESSAGE + "?" + str;
            HttpGet httpGet1 = new HttpGet(url);
            return httpGet1;
        } catch (Exception e) {
            logger.error("[TaiPingYangHandler.createCheZhuCommentNoticeHttpGet]", e);
            return null;
        }
    }

    private Result<HandlerResultDTO> commentNewsPraise(RequestContext requestContext) {
        Result<LoginResultDTO> resultDTOResult = taiPingYangLoginHandler.login(requestContext.getUserId());
        if (!resultDTOResult.getSuccess()) {
            logger.error("[TaiPingYangHandler.comment] requestContext" + requestContext);
            return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
        }
        try {
            LoginResultDTO loginResultDTO = resultDTOResult.getData();
            CloseableHttpClient httpClient = loginResultDTO.getHttpClient();
            HttpPost httpPost = createCommentNewsPraiseHttpPost(requestContext);
            setCommentNewsPraiseHeader(httpPost);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = null;
            if (entity != null) {
                content = EntityUtils.toString(entity, "utf-8");
                content = content.trim();
                TaiPingYangCommentResultDTO resultDTO = JSONObject.parseObject(content, TaiPingYangCommentResultDTO.class);
                if (resultDTO.getCode() == 1) {
                    HandlerResultDTO handlerResultDTO = ResultParamUtil.createHandlerResultDTO(requestContext, content);
                    PraiseInfo praiseInfo = ResultParamUtil.createPraiseInfo(requestContext, content);
                    save(new SaveContext(praiseInfo));
                    return new Result(handlerResultDTO);
                }
                return new Result(ResultCodeEnum.HANDLE_FAILED, content);
            }
            //处理失败的回复，把context记录下来，可以决定是否重新扫描,并且记录失败原因,这个在业务层处理，决定是否要记录未处理成功的数据
            return new Result<>(ResultCodeEnum.HANDLE_FAILED);
        } catch (Exception e) {
            logger.error("[TaiPingYangHandler.comment] error!", e);
            //处理失败的回复，把context记录下来，可以决定是否重新扫描,并且记录失败原因
            return new Result<>(ResultCodeEnum.HANDLE_FAILED);
        }
    }

    private HttpPost createCommentNewsPraiseHttpPost(RequestContext requestContext) {

        /**
         * version: 2
         * cid: 32202121
         * sp: 1
         * r: 0.24152560137723267
         */
        try {
            String cid = getTaiPinYangCommentIdByCommentContent(requestContext.getPrefixUrl(), requestContext.getContent().getText());
            HttpPost httpPost = new HttpPost(TARGET_COMMENT_NEWS_PRAISE);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("version", "2"));
            nameValuePairs.add(new BasicNameValuePair("cid", cid));
            nameValuePairs.add(new BasicNameValuePair("sp", "1"));
            nameValuePairs.add(new BasicNameValuePair("r", "0.24152560137723277"));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            return httpPost;
        } catch (Exception e) {
            logger.error("[TaiPingYangHandler.createCommentPost]createCommentNewsPraiseHttpPost  UrlEncodedFormEntity error! ");
            return null;
        }
    }


    private void setCommentNewsPraiseHeader(HttpPost httpPost) {
        /**
         * Host: cmt.pcauto.com.cn
         * Origin: https://www.pcauto.com.cn
         * Referer: https://www.pcauto.com.cn/nation/1323/13233103.html
         * User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36
         */
        httpPost.setHeader("Host", "cmt.pcauto.com.cn");
        httpPost.setHeader("Origin", "https://www.pcauto.com.cn");
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");


    }

    private HttpPost createCommentNewsHttpPost(RequestContext requestContext) {
        /**
         * isEncode: 1
         * content: %E7%A1%AE%E5%AE%9E%E5%BE%88%E9%9C%B8%E6%B0%94
         * captcha:
         * needCaptcha: 0
         * url: https://www.pcauto.com.cn/nation/1314/13145443.html
         * title: %E6%96%B0%E6%AC%BE%E6%96%AF%E6%9F%AF%E8%BE%BE%E6%9F%AF%E7%8F%9E%E5%85%8BScout/Sportline%E5%AE%98%E5%9B%BE_%E5%A4%AA%E5%B9%B3%E6%B4%8B%E6%B1%BD%E8%BD%A6%E7%BD%91
         * columnId:
         * area:
         * replyFloor2: 0
         * partId:
         * syncsites:
         */
        HttpPost httpPost = null;
        String url = requestContext.getPrefixUrl();
        if (TaskEntryTypeEnum.TAIPINGYANGCHEZHUCOMMENT.equals(requestContext.getHandleEntryType())) {
            url = url.replaceAll("https:", "");
            url = url.replaceAll("http:", "");
            httpPost = new HttpPost(TARGET_COMMENT_CHE_ZHU);
        } else {
            httpPost = new HttpPost(TARGET_COMMENT_NEWS);
        }
        String title = ResolveUtil.fetchTitle(requestContext.getPrefixUrl());
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("isEncode", "1"));
        nameValuePairs.add(new BasicNameValuePair("content", requestContext.getContent().getText()));
        nameValuePairs.add(new BasicNameValuePair("needCaptcha", "0"));


        nameValuePairs.add(new BasicNameValuePair("url", url));
        nameValuePairs.add(new BasicNameValuePair("title", title));
        nameValuePairs.add(new BasicNameValuePair("replyFloor2", "0"));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "GB2312"));
            return httpPost;
        } catch (Exception e) {
            logger.error("[TaiPingYangHandler.createCommentPost]createCommentPost  UrlEncodedFormEntity error! nameValuePairs" + nameValuePairs);
            return null;
        }
    }

    private HttpPost createCommentCheZhuHttpPost(RequestContext requestContext) {
        /**
         * isEncode: 1
         * content: %E5%AE%87%E5%AE%99%E7%A5%9E%E8%BD%A6%E7%9A%84%E6%84%9F%E8%A7%89
         * captcha:
         * needCaptcha: 0
         * url: //price.pcauto.com.cn/comment/sg21775/m79917/view_884063.html
         * title: %E9%A3%8E%E5%85%89S560%E6%80%8E%E4%B9%88%E6%A0%B7
         * columnId:
         * area:
         * replyFloor2: 0
         * partId:
         * syncsites:
         */
        HttpPost httpPost = new HttpPost(TARGET_COMMENT_NEWS);

        String title = ResolveUtil.fetchTitle(requestContext.getPrefixUrl());
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("isEncode", "1"));
        nameValuePairs.add(new BasicNameValuePair("content", requestContext.getContent().getText()));
        nameValuePairs.add(new BasicNameValuePair("needCaptcha", "0"));
        nameValuePairs.add(new BasicNameValuePair("url", requestContext.getPrefixUrl()));
        nameValuePairs.add(new BasicNameValuePair("title", title));
        nameValuePairs.add(new BasicNameValuePair("replyFloor2", "0"));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "GB2312"));
            return httpPost;
        } catch (Exception e) {
            logger.error("[TaiPingYangHandler.createCommentPost]createCommentPost  UrlEncodedFormEntity error! nameValuePairs" + nameValuePairs);
            return null;
        }
    }


    private void setCommentNewsHeader(HttpPost httpPost) {
        /**
         * Host: cmt.pcauto.com.cn
         * Origin: https://www.pcauto.com.cn
         * Referer: https://www.pcauto.com.cn/nation/1314/13145443.html
         * User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36
         */
        httpPost.setHeader("Host", "cmt.pcauto.com.cn");
        httpPost.setHeader("Origin", "https://www.pcauto.com.cn");
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");

    }

    /**
     * 通过文章url和评论获取评论的ID
     * https://www.pcauto.com.cn/nation/1355/13551303.html
     *
     * @param articleUrl     文章链接
     * @param commentContent 评论内容
     * @return
     */
    private String getTaiPinYangCommentIdByCommentContent(String articleUrl, String commentContent) {
        Pattern pattern = Pattern.compile("data-id=\"[0-9]+\"");
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String getData = "https://cmt.pcauto.com.cn/action/topic/get_data.jsp?url=" + articleUrl;
            HttpGet httpGetData = new HttpGet(getData);
            HttpResponse httpGetDataResponse = httpClient.execute(httpGetData);
            String replyUrlContent = EntityUtils.toString(httpGetDataResponse.getEntity());
            String url = JSON.parseObject(replyUrlContent).getString("url");
            for (int i = 1; ; i++) {
                String realUrl = url.replace("/p1/", "/p" + i + "/");
                System.out.println(realUrl);
                HttpGet httpGet = new HttpGet(realUrl);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                String res = EntityUtils.toString(httpResponse.getEntity(), "GB2312");
                Document document = Jsoup.parse(res);
                Element commentTableElement = document.getElementById("commentTable");
                Elements e = commentTableElement.getElementsByTag("li");
                for (Element liE : e) {
                    String liEString = liE.toString().replaceAll("(\r\n|\r|\n|\n\r)", "");
                    if (liEString.contains(commentContent)) {
                        Matcher matcher = pattern.matcher(liEString);
                        if (matcher.find()) {
                            String idS = matcher.group();
                            return idS.replace("data-id=", "").replace("\"", "");
                        }
                    }
                }
                if (!commentTableElement.toString().contains("下一页")) {
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}


