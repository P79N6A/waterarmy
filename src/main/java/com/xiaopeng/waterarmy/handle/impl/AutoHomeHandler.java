package com.xiaopeng.waterarmy.handle.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.constants.RequestConsts;
import com.xiaopeng.waterarmy.common.enums.CharsetEnum;
import com.xiaopeng.waterarmy.common.enums.ResultCodeEnum;
import com.xiaopeng.waterarmy.common.enums.TaskEntryTypeEnum;
import com.xiaopeng.waterarmy.common.util.HtmlPlayUtil;
import com.xiaopeng.waterarmy.common.util.HtmlReadUtil;
import com.xiaopeng.waterarmy.common.util.HttpUtil;
import com.xiaopeng.waterarmy.handle.PlatformHandler;
import com.xiaopeng.waterarmy.handle.Util.FetchParamUtil;
import com.xiaopeng.waterarmy.handle.Util.ResolveUtil;
import com.xiaopeng.waterarmy.handle.Util.ResultParamUtil;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.handle.param.SaveContext;
import com.xiaopeng.waterarmy.handle.result.*;
import com.xiaopeng.waterarmy.model.dao.CommentInfo;
import com.xiaopeng.waterarmy.model.dao.PraiseInfo;
import com.xiaopeng.waterarmy.model.dao.PublishInfo;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AutoHomeHandler extends PlatformHandler {

    private static Logger logger = LoggerFactory.getLogger(AutoHomeHandler.class);

    private static final String TOPIC = "topic";

    private static final String FID = "fid";

    private static final String TARGET_COMMENT_URL = "https://bbs.pcauto.com.cn/action/post/create.ajax";

    private static final String TARGET_PUBLISH_URL = "https://bbs.pcauto.com.cn/action/topic/create.ajax";

    private static final String CHECK_LOGIN = "https://my.pcauto.com.cn/intf/checkLogin.jsp?act=checkLogin";

    private static final String FETCH_TOPICS = "https://bbs.pcauto.com.cn/intf/user/_topics.jsp";

    private static final String TOPIC_PREFIX = "https://bbs.pcauto.com.cn/topic-";

    private static final String TARGET_COMMENT_NEWS = "https://cmt.pcauto.com.cn/action/comment/create.jsp";

    private static final String TARGET_COMMENT_NEWS_PRAISE = "https://www.autohome.com.cn/Ashx/AjaxUserOper.ashx";

    private static final String TARGET_COMMENT_CHEZHU_MESSAGE = "https://price.pcauto.com.cn/comment/interface/send_message_to_bip.jsp";

    private static final String TARGET_COMMENT_CHE_ZHU = "https://cmt.pcauto.com.cn/action/comment/create.jsp?urlHandle=1";


    @Autowired
    QiCheZhiJiaLoginHandler qiCheZhiJiaLoginHandler;


    @Override
    public Result<HandlerResultDTO> publish(RequestContext requestContext) {

        Result<LoginResultDTO> resultDTOResult = qiCheZhiJiaLoginHandler.login(requestContext.getUserId());
        if (!resultDTOResult.getSuccess()) {
            logger.error("[QiCheZhiJiaLoginHandler.publish] requestContext" + requestContext);
            return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
        }


        try {
            LoginResultDTO loginResultDTO = resultDTOResult.getData();
            CloseableHttpClient httpClient = loginResultDTO.getHttpClient();
            HttpPost httpPost = createPublishHttpPost(httpClient, requestContext, loginResultDTO.getCookieStore());
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = null;
            if (entity != null) {
                content = EntityUtils.toString(entity, "utf-8");
                JSONObject jsonObject = JSONObject.parseObject(content);

                String topicId = String.valueOf(jsonObject.get("result"));
                if (topicId != null) {
                    //发帖成功，拼帖子链接，注意这个帖子链接需要302跳转
                    //https://club.autohome.com.cn/bbs/thread-a-100022-77217156-1.html?036765385695959547
                    String prefixUrl = "";
                    if (!ObjectUtils.isEmpty(requestContext.getPrefixUrl())) {
                        if (ObjectUtils.isEmpty(requestContext.getPrefixUrl().contains("."))) {
                            prefixUrl = requestContext.getPrefixUrl().split(".")[0];
                        } else {
                            prefixUrl = requestContext.getPrefixUrl();
                        }

                    }
                    String targetUrl = prefixUrl + "-" + topicId + "-1.html?636765385695959547";
                    logger.info("汽车之家发帖成功，帖子地址：" + targetUrl);
                    PublishInfo publishInfo = ResultParamUtil.createPublishInfo(requestContext, content, targetUrl);
                    save(new SaveContext(publishInfo));
                    HandlerResultDTO handlerResultDTO = ResultParamUtil.createHandlerResultDTO(requestContext, content, targetUrl);
                    return new Result(handlerResultDTO);
                }
            }
            return new Result<>(ResultCodeEnum.HANDLE_FAILED);
        } catch (Exception e) {
            logger.error("[AutoHomeHandler.publish] error!", e);
            //处理失败的回复，把context记录下来，可以决定是否重新扫描,并且记录失败原因
            return new Result<>(ResultCodeEnum.HANDLE_FAILED);
        }
    }

    private String generatePublishUrlByTopicId(Integer tid) {
        return TOPIC_PREFIX + tid + ".html";
    }

    @Override
    public Result<HandlerResultDTO> comment(RequestContext requestContext) {
        return commentForum(requestContext);
    }

    private Result<HandlerResultDTO> commentForum(RequestContext requestContext) {
        Result<LoginResultDTO> resultDTOResult = qiCheZhiJiaLoginHandler.login(requestContext.getUserId());
        if (!resultDTOResult.getSuccess()) {
            logger.error("[TaiPingYangHandler.comment] requestContext" + requestContext);
            return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
        }
        try {
            LoginResultDTO loginResultDTO = resultDTOResult.getData();
            CloseableHttpClient httpClient = loginResultDTO.getHttpClient();

            HttpPost httpPost;
            if (requestContext.getRequestParam() != null && requestContext.getRequestParam().get("commentContent") != null) {
                httpPost = createReplyCommentPost(requestContext, loginResultDTO, (String) requestContext.getRequestParam().get("commentContent"));
            } else {
                httpPost = createCommentPost(requestContext, loginResultDTO);
            }

            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = null;
            if (entity != null) {
                content = EntityUtils.toString(entity, "utf-8");
                content = content.trim();
                AutoHomeCommentResultDTO resultDTO = JSONObject.parseObject(
                        content, AutoHomeCommentResultDTO.class);

                if (resultDTO.isSucceed()) {
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
        if (TaskEntryTypeEnum.AUTOHOMENEWSCOMMENTPRAISE.equals(requestContext.getHandleEntryType())) {
            return commentNewsPraise(requestContext);
        }
        return new Result<>(ResultCodeEnum.HANDLER_NOT_FOUND);
    }

    private HttpPost createCommentPost(RequestContext requestContext, LoginResultDTO loginResultDTO) {
        //汽车之家论坛评论
        //Accept: */*
        //Accept-Encoding: gzip, deflate, br
        //Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
        //Connection: keep-alive
        //Content-Length: 269
        //Content-Type: application/x-www-form-urlencoded; charset=UTF-8
        //Cookie: fvlid=1540687271470XXtsV13f28; __utmc=1; sessionid=DD4D08C1-E7A9-447F-AE49-05EA38F430C3%7C%7C2018-10-28+08%3A41%3A10.408%7C%7C0; ahpau=1; sessionuid=DD4D08C1-E7A9-447F-AE49-05EA38F430C3%7C%7C2018-10-28+08%3A41%3A10.408%7C%7C0; __ah_uuid=74F6512B-2EEC-4AB4-8B55-56A67AB69398; area=330104; __utma=1.766039677.1540687272.1540687272.1540911797.2; __utmz=1.1540911797.2.2.utmcsr=club.autohome.com.cn|utmccn=(referral)|utmcmd=referral|utmcct=/bbs/forum-o-200325-1.html; pcpopclub=3b413d61ab4f41a68a127df3afc6e5e204efcf08; clubUserShow=82824968|65|16|%e5%a6%99%e6%8b%a9%e7%81%b5%e9%ad%82%e7%a2%8e%e7%89%87|0|0|0||2018-10-30 23:36:41|0; autouserid=82824968; sessionuserid=82824968; pvidchain=101301; sessionlogin=b662df6756a5496d8258faaea62cd57604efcf08; sessionip=101.71.38.177; sessionvid=2422F8DF-9E14-4E7F-A9CC-F8A1D99A8FE7; autoac=37F17E9943879E2127D6351F17BCEC8D; autotc=2C465285DD24D4C0DBE4722C2CFD8AF8; historybbsName4=o-200325%7C%E9%9D%92%E5%B0%91%E5%B9%B4%2Ca-100025%7C%E5%9B%9B%E5%B7%9D%2Ca-100022%7C%E5%B1%B1%E8%A5%BF%2Cc-2288%7C%E9%98%BF%E5%B0%94%E6%B3%95%E7%BD%97%E5%AF%86%E6%AC%A7; ahpvno=6; ref=0%7C0%7C0%7C0%7C2018-11-01+20%3A13%3A45.518%7C2018-10-28+08%3A41%3A10.408; ahrlid=1541074437104SQi5RL2hNN-1541074487176
        //Host: club.autohome.com.cn
        //Origin: https://club.autohome.com.cn
        //Referer: https://club.autohome.com.cn/bbs/thread/f0dd96c0c425b905/77251423-1.html
        //User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36
        //X-Requested-With: XMLHttpRequest


        //request
        //bbs: c
        //bbsid: 2288
        //topicId: 77251423
        //content: 罗密欧这辆车确实不错，价格多少啊
        //uniquepageid: 0PipCFBvilX4cO64uAb72kGdCAuwUXyVslgmz9aApuM=
        //domain: autohome.com.cn


        //获取uniquepageid
        try {
            HttpGet httpGet = new HttpGet(requestContext.getPrefixUrl());
            setFetchTopicsHeader(httpGet);
            CloseableHttpResponse response = loginResultDTO.getHttpClient().execute(httpGet);
            HttpEntity entity = response.getEntity();
            String content = null;
            if (entity != null) {
                content = EntityUtils.toString(entity, "utf-8");
                String uniquePageId = FetchParamUtil.getMatherStr(content, "tz.uniquePageId = \".*\"");
                uniquePageId = FetchParamUtil.getMatherStr(uniquePageId, "\".*\"");
                uniquePageId = uniquePageId.replaceAll("\"", "");

                String topicId = FetchParamUtil.getMatherStr(content, "tz.topicId=.*");
                topicId = FetchParamUtil.getMatherStr(topicId, "\\d+");

                String bbs = FetchParamUtil.getMatherStr(content, "tz.bbs=\".*\"");
                bbs = FetchParamUtil.getMatherStr(bbs, "\".*\"");
                bbs = bbs.replaceAll("\"", "");

                String bbsId = FetchParamUtil.getMatherStr(content, "tz.bbsid=.*");
                bbsId = FetchParamUtil.getMatherStr(bbsId, "\\d+");

                String targetUrl = "https://club.autohome.com.cn/Detail/AddReply";
                HttpPost httpPost = new HttpPost(targetUrl);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("bbs", bbs));
                nameValuePairs.add(new BasicNameValuePair("bbsid", bbsId));
                nameValuePairs.add(new BasicNameValuePair("topicId", topicId));
                nameValuePairs.add(new BasicNameValuePair("uniquepageid", uniquePageId));
                nameValuePairs.add(new BasicNameValuePair("content", requestContext.getContent().getText()));
                /**
                 *获取被评论的内容，如果有就是要盖楼中楼
                 */
                if (requestContext.getRequestParam() != null && requestContext.getRequestParam().get("commentContent") != null) {
                    String commentId = getCommentIdByContent(requestContext.getPrefixUrl(), requestContext.getContent().getText());
                    nameValuePairs.add(new BasicNameValuePair("targetReplyid", commentId));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                setCreateCommentHeader(requestContext, httpPost, loginResultDTO.getCookieStore());
                return httpPost;
            }

        } catch (Exception e) {
            logger.error("createComment error!", e);
        }

        return null;
    }

    /**
     * 楼中楼回复
     *
     * @param requestContext
     * @param loginResultDTO
     * @param commentContent
     * @return
     */
    private HttpPost createReplyCommentPost(RequestContext requestContext, LoginResultDTO loginResultDTO, String commentContent) {
        try {
            HttpGet httpGet = new HttpGet(requestContext.getPrefixUrl());
            setFetchTopicsHeader(httpGet);
            CloseableHttpResponse response = loginResultDTO.getHttpClient().execute(httpGet);
            HttpEntity entity = response.getEntity();
            String content = null;
            if (entity != null) {
                content = EntityUtils.toString(entity, "utf-8");
                String uniquePageId = FetchParamUtil.getMatherStr(content, "tz.uniquePageId = \".*\"");
                uniquePageId = FetchParamUtil.getMatherStr(uniquePageId, "\".*\"");
                uniquePageId = uniquePageId.replaceAll("\"", "");

                String topicId = FetchParamUtil.getMatherStr(content, "tz.topicId=.*");
                topicId = FetchParamUtil.getMatherStr(topicId, "\\d+");

                String bbs = FetchParamUtil.getMatherStr(content, "tz.bbs=\".*\"");
                bbs = FetchParamUtil.getMatherStr(bbs, "\".*\"");
                bbs = bbs.replaceAll("\"", "");

                String bbsId = FetchParamUtil.getMatherStr(content, "tz.bbsid=.*");
                bbsId = FetchParamUtil.getMatherStr(bbsId, "\\d+");

                String targetUrl = "https://club.autohome.com.cn/Detail/AddReply";
                HttpPost httpPost = new HttpPost(targetUrl);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("bbs", bbs));
                nameValuePairs.add(new BasicNameValuePair("bbsid", bbsId));
                nameValuePairs.add(new BasicNameValuePair("topicId", topicId));
                nameValuePairs.add(new BasicNameValuePair("uniquepageid", uniquePageId));
                nameValuePairs.add(new BasicNameValuePair("content", requestContext.getContent().getText()));
                String commentId = getCommentIdByContent(requestContext.getPrefixUrl(), commentContent);
                nameValuePairs.add(new BasicNameValuePair("targetReplyid", commentId));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                setCreateCommentHeader(requestContext, httpPost, loginResultDTO.getCookieStore());
                return httpPost;
            }
        } catch (Exception e) {
            logger.error("createComment error!", e);
        }
        return null;
    }

    private void setCreateCommentHeader(RequestContext requestContext, HttpPost httpPost, BasicCookieStore cookieStore) {
        //Host: club.autohome.com.cn
        //Origin: https://club.autohome.com.cn
        //Referer: https://club.autohome.com.cn/bbs/thread/f0dd96c0c425b905/77251423-1.html
        //User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36
        //X-Requested-With: XMLHttpRequest
        httpPost.setHeader("Host", "club.autohome.com.cn");
        httpPost.setHeader("Origin", "https://club.autohome.com.cn");
        httpPost.setHeader("Referer", requestContext.getPrefixUrl());
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        httpPost.setHeader("X-Requested-With", "XMLHttpRequest");
        //设置cookie
        StringBuilder stringBuilder = new StringBuilder();
        String s = null;
        for (Cookie cookie : cookieStore.getCookies()) {
            if ("clubUserShow".equalsIgnoreCase(cookie.getName())) {
                s = cookie.getValue();
                break;
            }
        }
        httpPost.setHeader("Cookie", "clubUserShow=" + s);
    }

    private void setFetchTopicsHeader(HttpGet httpGet) {
        //Host: club.autohome.com.cn
        //Upgrade-Insecure-Requests: 1
        //User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36

        httpGet.setHeader("Host", "club.autohome.com.cn");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
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


    private HttpPost createPublishHttpPost(CloseableHttpClient httpClient, RequestContext requestContext, BasicCookieStore cookieStore) {
        try {
            String url = "https://clubajax.autohome.com.cn/ajax/corsagent?type=1&method=post";
            HttpPost httpPost = new HttpPost(url);
            httpPost = createPublish(httpClient, httpPost, requestContext, cookieStore);
            return httpPost;
        } catch (Exception e) {
            logger.error("[createPublishHttpPost] error!", e);
            return null;
        }
    }

    private HttpPost createPublish(CloseableHttpClient httpClient, HttpPost httpPost, RequestContext requestContext, BasicCookieStore cookieStore) {
        //Accept: application/json, text/plain, */*
        //Accept-Encoding: gzip, deflate, br
        //Accept-Language: zh-CN,zh;q=0.9,en;q=0.8
        //Connection: keep-alive
        //Host: clubajax.autohome.com.cn
        //Origin: https://clubajax.autohome.com.cn
        //Referer: https://clubajax.autohome.com.cn/NewPost/CardPost?bbs=a&bbsId=100025&urlbbsId=100025&from_bj=0
        //User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36

        httpPost.setHeader("Accept", "application/json, text/plain, */*");
        httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
        httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
        httpPost.setHeader("Host", "clubajax.autohome.com.cn");
        httpPost.setHeader("Origin", "https://clubajax.autohome.com.cn");

        String url = requestContext.getPrefixUrl();
        //https://club.autohome.com.cn/bbs/forum-c-2615-1.html
        String temp = FetchParamUtil.getMatherStr(url, "forum-.*-\\d+-\\d+.html");
        String temp2 = FetchParamUtil.getMatherStr(temp, "forum-.*-\\d+-");
        temp2 = FetchParamUtil.getMatherStr(temp2, "-.*-\\d+");
        temp2 = FetchParamUtil.getMatherStr(temp2, "-.*-");
        String type = temp2.replaceAll("-", "");
        //type = type.replaceAll("-", "");
        String forumId = FetchParamUtil.getMatherStr(temp, "-\\d+-");
        forumId = forumId.replaceAll("-", "");
        //https://clubajax.autohome.com.cn/NewPost/CardPost?bbs=c&bbsId=2615&urlbbsId=2615&from_bj=0
        String reffer = "https://clubajax.autohome.com.cn/NewPost/CardPost?bbs=" + type + "&bbsId=" + forumId + "&urlbbsId=" + forumId + "&from_bj=0";
        httpPost.setHeader("Referer", reffer);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

        //设置cookie
        StringBuilder stringBuilder = new StringBuilder();
        String s = null;
        for (Cookie cookie : cookieStore.getCookies()) {
            if ("clubUserShow".equalsIgnoreCase(cookie.getName())) {
                s = cookie.getValue();
                break;
            }
        }
        httpPost.setHeader("Cookie", "clubUserShow=" + s);
        String memberId = null;
        for (Cookie cookie : cookieStore.getCookies()) {
            if ("sessionuserid".equals(cookie.getName())) {
                memberId = cookie.getValue();
            }
        }
        try {
            String str;
            if (requestContext.getImageInputStreams() != null) {
                String imageContent = qichezhijiaImageUpload(httpClient, requestContext.getImageInputStreams());
                str = "{\"topicmain\":{\"topicid\":0,\"title\":\"" + requestContext.getContent().getTitle() + "\",\"source\":\"PC.CARD\",\"memberid\":\"" + memberId + "\",\"bbs\":\"" + type + "\",\"bbsid\":\"" + forumId + "\",\"clientip\":\"{$realip$}\",\"autohomeua\":\"\",\"reply_notify_me\":1,\"informfriends\":1},\"topicext\":{\"lon\":0,\"lat\":0,\"postaddress\":\"\",\"landmark\":\"\"},\"topiccards\":[  " + imageContent + ",   {\"ctype\":2,\"url\":\"\",\"des\":\"" + requestContext.getContent().getText() + "\",\"otherattributes\":{\"linkurl\":\"\"}}]}";
            } else {
                str = "{\"topicmain\":{\"topicid\":0,\"title\":\"" + requestContext.getContent().getTitle() + "\",\"source\":\"PC.CARD\",\"memberid\":\"" + memberId + "\",\"bbs\":\"" + type + "\",\"bbsid\":\"" + forumId + "\",\"clientip\":\"{$realip$}\",\"autohomeua\":\"\",\"reply_notify_me\":1,\"informfriends\":1},\"topicext\":{\"lon\":0,\"lat\":0,\"postaddress\":\"\",\"landmark\":\"\"},\"topiccards\":[{\"ctype\":2,\"url\":\"\",\"des\":\"" + requestContext.getContent().getText() + "\",\"otherattributes\":{\"linkurl\":\"\"}}]}";
            }
            StringEntity entity = new StringEntity(str, "UTF-8");
            entity.setContentType("application/x-www-form-urlencoded");
            entity.setContentEncoding("UTF-8");
            httpPost.setEntity(entity);
            return httpPost;
        } catch (Exception e) {
            logger.error("create publish error!", e);
            return null;
        }

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


        Result<LoginResultDTO> resultDTOResult = qiCheZhiJiaLoginHandler.login(requestContext.getUserId());
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
        Result<LoginResultDTO> resultDTOResult = qiCheZhiJiaLoginHandler.login(requestContext.getUserId());
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
        Result<LoginResultDTO> resultDTOResult = qiCheZhiJiaLoginHandler.login(requestContext.getUserId());
        if (!resultDTOResult.getSuccess()) {
            logger.error("[AutoHomeHandler.comment] requestContext" + requestContext);
            return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
        }
        try {
            LoginResultDTO loginResultDTO = resultDTOResult.getData();
            CloseableHttpClient httpClient = loginResultDTO.getHttpClient();
            HttpPost httpPost = createCommentNewsPraiseHttpPost(requestContext);
            setCommentNewsPraiseHeader(httpPost, loginResultDTO);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = null;
            if (entity != null) {
                content = EntityUtils.toString(entity, "gb2312");
                content = content.trim();
                AutoHomeCommentPraiseResultDTO resultDTO
                        = JSONObject.parseObject(content, AutoHomeCommentPraiseResultDTO.class);
                if (resultDTO.isStatus()) {
                    HandlerResultDTO handlerResultDTO
                            = ResultParamUtil.createHandlerResultDTO(requestContext, content);
                    PraiseInfo praiseInfo = ResultParamUtil.createPraiseInfo(requestContext, content);
                    save(new SaveContext(praiseInfo));
                    return new Result(handlerResultDTO);
                }
                return new Result(ResultCodeEnum.HANDLE_FAILED, content);
            }
            //处理失败的回复，把context记录下来，可以决定是否重新扫描,并且记录失败原因,这个在业务层处理，决定是否要记录未处理成功的数据
            return new Result<>(ResultCodeEnum.HANDLE_FAILED);
        } catch (Exception e) {
            logger.error("[AutoHomeHandler.comment] error!", e);
            //处理失败的回复，把context记录下来，可以决定是否重新扫描,并且记录失败原因
            return new Result<>(ResultCodeEnum.HANDLE_FAILED);
        }
    }

    private HttpPost createCommentNewsPraiseHttpPost(RequestContext requestContext) {

        /**
         * OperType: EvaltInsert
         * ArtId: 924429
         * EvaltTypeId: 1
         * SecondTypeId: 1
         * Content: 文字精彩
         */
        try {
            String articleId = String.valueOf(requestContext.getRequestParam().get("articleId"));//RequestConsts.COMMENT_ID
            if (articleId == null) {
                return null;
            }
            HttpPost httpPost = new HttpPost(TARGET_COMMENT_NEWS_PRAISE);

            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("OperType", "EvaltInsert"));
            nameValuePairs.add(new BasicNameValuePair("ArtId", articleId));
            nameValuePairs.add(new BasicNameValuePair("EvaltTypeId", "1"));
            nameValuePairs.add(new BasicNameValuePair("SecondTypeId", "1"));
            nameValuePairs.add(new BasicNameValuePair("Content"
                    , requestContext.getContent().getText()));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
            return httpPost;
        } catch (Exception e) {
            logger.error("[AutoHomeHandler.createCommentNewsPraiseHttpPost]" +
                    "createCommentNewsPraiseHttpPost  UrlEncodedFormEntity error! ", e);
            return null;
        }
    }


    private void setCommentNewsPraiseHeader(HttpPost httpPost, LoginResultDTO loginResultDTO) {
        /**
         *
         * Accept-Encoding: gzip, deflate, br
         * Accept-Language: zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7
         * Cache-Control: no-cache
         * Connection: keep-alive
         * Content-Length: 107
         * Content-Type: application/x-www-form-urlencoded; charset=UTF-8
         * Cookie: ASP.NET_SessionId=5ouvwb315qxmiolkqxnzxokg; __ah_uuid=AF1B4AEF-76B1-4BE3-875E-D855374CDF29; fvlid=1541225246038ETN0B4MM0j; __utmc=1; sessionip=183.236.71.86; sessionid=844C060E-A2A3-4468-9986-792ED3788C79%7C%7C2018-11-03+14%3A07%3A29.449%7C%7C0; ahpau=1; area=440106; sessionuid=844C060E-A2A3-4468-9986-792ED3788C79%7C%7C2018-11-03+14%3A07%3A29.449%7C%7C0; nice_id2d64d8c0-810a-11e8-aaa0-b1995d9e8c72=1fa27523-df48-11e8-9b50-6b73d98beb07; historybbsName4=c-3465%7C%E5%B8%9D%E8%B1%AAGS%2Co-200325%7C%E9%9D%92%E5%B0%91%E5%B9%B4; __utma=1.128045129.1541225249.1541238157.1541299668.4; __utmz=1.1541299668.4.4.utmcsr=club.autohome.com.cn|utmccn=(referral)|utmcmd=referral|utmcct=/bbs/thread/e5883eaca45cb512/77290014-1.html; pcpopclub=fabd0a7a9fc248edbd0db0679cfa2fe404557f37; clubUserShow=72711991|4691|6|iason2010|0|0|0||2018-11-04 11:02:30|0; autouserid=72711991; sessionuserid=72711991; sessionlogin=7b46bc479fe441299e8dac5232ab3c0904557f37; ahpvno=58; pvidchain=3311495; sessionvid=6CDD128A-E55F-4D7E-9966-8C5C7714E88D; _fmdata=bGzHBmBDGV%2BwPRpNFf5jRvfJuGaCaEAK975dzZZ%2BBjA6AOyatgUggVbu%2B51mQcBGVzRSZKLN%2BFSBYwsD3Sq6BQFvhIdry4h8jMqxXK4WzcQ%3D; ref=www.baidu.com%7C0%7C0%7C0%7C2018-11-04+12%3A06%3A44.129%7C2018-11-03+17%3A07%3A44.512; ahrlid=1541304401989S0azDBbwSA-1541304537256
         * Host: www.autohome.com.cn
         * Origin: https://www.autohome.com.cn
         * Pragma: no-cache
         * Referer: https://www.autohome.com.cn/news/201811/924429.html
         * User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36
         * X-Requested-With: XMLHttpRequest
         *
         */
        //httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
        //httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,zh-TW;q=0.7");
        //httpPost.setHeader("Cache-Control", "no-cache");
        //httpPost.setHeader("Connection", "keep-alive");
        //httpPost.setHeader("Content-Length", "107");
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        //httpPost.setHeader("Cookie", "ASP.NET_SessionId=5ouvwb315qxmiolkqxnzxokg; __ah_uuid=AF1B4AEF-76B1-4BE3-875E-D855374CDF29; fvlid=1541225246038ETN0B4MM0j; __utmc=1; sessionip=183.236.71.86; sessionid=844C060E-A2A3-4468-9986-792ED3788C79%7C%7C2018-11-03+14%3A07%3A29.449%7C%7C0; ahpau=1; area=440106; sessionuid=844C060E-A2A3-4468-9986-792ED3788C79%7C%7C2018-11-03+14%3A07%3A29.449%7C%7C0; nice_id2d64d8c0-810a-11e8-aaa0-b1995d9e8c72=1fa27523-df48-11e8-9b50-6b73d98beb07; historybbsName4=c-3465%7C%E5%B8%9D%E8%B1%AAGS%2Co-200325%7C%E9%9D%92%E5%B0%91%E5%B9%B4; __utma=1.128045129.1541225249.1541238157.1541299668.4; __utmz=1.1541299668.4.4.utmcsr=club.autohome.com.cn|utmccn=(referral)|utmcmd=referral|utmcct=/bbs/thread/e5883eaca45cb512/77290014-1.html; pcpopclub=fabd0a7a9fc248edbd0db0679cfa2fe404557f37; clubUserShow=72711991|4691|6|iason2010|0|0|0||2018-11-04 11:02:30|0; autouserid=72711991; sessionuserid=72711991; sessionlogin=7b46bc479fe441299e8dac5232ab3c0904557f37; ahpvno=58; pvidchain=3311495; sessionvid=6CDD128A-E55F-4D7E-9966-8C5C7714E88D; _fmdata=bGzHBmBDGV%2BwPRpNFf5jRvfJuGaCaEAK975dzZZ%2BBjA6AOyatgUggVbu%2B51mQcBGVzRSZKLN%2BFSBYwsD3Sq6BQFvhIdry4h8jMqxXK4WzcQ%3D; ref=www.baidu.com%7C0%7C0%7C0%7C2018-11-04+12%3A06%3A44.129%7C2018-11-03+17%3A07%3A44.512; ahrlid=1541304401989S0azDBbwSA-1541304537256");
        httpPost.setHeader("Host", "www.autohome.com.cn");
        httpPost.setHeader("Origin", "https://www.autohome.com.cn");
        //httpPost.setHeader("Pragma", "no-cache");
        httpPost.setHeader("Referer", "https://www.autohome.com.cn/news/201811/924429.html");
        //httpPost.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
        //httpPost.setHeader("X-Requested-With","XMLHttpRequest");

        //设置cookie
        StringBuilder stringBuilder = new StringBuilder();
        String s = null;
        for (Cookie cookie : loginResultDTO.getCookieStore().getCookies()) {
            if ("clubUserShow".equalsIgnoreCase(cookie.getName())) {
                s = cookie.getValue();
                break;
            }
        }
        httpPost.setHeader("Cookie", "clubUserShow=" + s);

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

    private String qichezhijiaImageUpload(HttpClient httpClient, List<InputStream> inputStreams) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < inputStreams.size(); ++i) {
            QiCheZhiJiaImage qiCheZhiJiaImage = qichezhijiaImageUpload(httpClient, inputStreams.get(i));
            Topiccard topiccard = new Topiccard();
            topiccard.cardid = (i + 1) + "";
            topiccard.url = "http://club2.autoimg.cn/album/" + qiCheZhiJiaImage.file;
            TopiccardOtherattribute otherattributes = new TopiccardOtherattribute(qiCheZhiJiaImage.width, qiCheZhiJiaImage.height);
            topiccard.otherattributes = otherattributes;
            if (i == 0) {
                stringBuilder.append(JSON.toJSONString(topiccard));
            } else {
                stringBuilder.append(",").append(JSON.toJSONString(topiccard));
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 汽车之家图片资源上传
     *
     * @param httpClient
     * @param inputStream
     * @return
     */
    public QiCheZhiJiaImage qichezhijiaImageUpload(HttpClient httpClient, InputStream inputStream) throws IOException {
        String uploadUrl = "https://clubajax.autohome.com.cn/Upload/UpImageOfBase64New?dir=image&cros=autohome.com.cn";
        HttpPost httpPost = new HttpPost(uploadUrl);

        HttpEntity entity = MultipartEntityBuilder.create()
                .addBinaryBody("file", inputStream, ContentType.create("application/octet-stream"), "file.png")
                .addTextBody("degree", "0")
                .addTextBody("CategoryId", "0")
                .addTextBody("id", "WU_FILE_1")
                .addTextBody("name", "image.png")
                .addTextBody("type", "image/png")
                .build();
        httpPost.setEntity(entity);

        HttpResponse httpResponse = httpClient.execute(httpPost);
        String content = EntityUtils.toString(httpResponse.getEntity());
        QiCheZhiJiaImage qiCheZhiJiaImage = JSON.parseObject(content, QiCheZhiJiaImage.class);
        JSONObject jsonObject = JSON.parseObject(content);
        JSONObject jsonObject1 = jsonObject.getJSONArray("tripsize").getJSONObject(0);
        qiCheZhiJiaImage.height = jsonObject1.getInteger("height");
        qiCheZhiJiaImage.width = jsonObject1.getInteger("width");
        return qiCheZhiJiaImage;
    }


    public static class QiCheZhiJiaImage {
        public String file;
        public int width;
        public int height;

        @Override
        public String toString() {
            return "QiCheZhiJiaImage{" +
                    "file='" + file + '\'' +
                    '}';
        }
    }

    public static class Topiccard {
        public String cardid;
        public String url;
        public String des = "";
        public int ctype = 1;
        public TopiccardOtherattribute otherattributes;
    }

    public static class TopiccardOtherattribute {
        public int width;
        public int height;
        public String linkurl;

        public TopiccardOtherattribute(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    /**
     * 通过评论获取评论的Id
     *
     * @param url
     * @param content
     * @return
     */
    private String getCommentIdByContent(String url, String content) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            String res = EntityUtils.toString(httpResponse.getEntity());
            String oneLine = res.replaceAll("(\r\n|\r|\n|\n\r)", "");
            String r = FetchParamUtil.getMatherStr(oneLine, "<div class=\"clearfix contstxt outer-section\".*?" + content);
            String[] ar = r.split("<div class=\"clearfix contstxt outer-section\"");
            String ars = ar[ar.length - 1];
            return FetchParamUtil.getMatherStr(ars, "pk=\"[0-9]+\"").replace("pk=\"", "").replace("\"", "");
        } catch (Exception e) {
            return null;
        }
    }
}


