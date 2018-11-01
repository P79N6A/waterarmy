package com.xiaopeng.waterarmy.handle.impl;

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
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
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

    private static final String TARGET_COMMENT_NEWS_PRAISE = "https://cmt.pcauto.com.cn/action/comment/support.jsp";

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
                String topicId = (String)jsonObject.get("result");
                if (topicId!=null) {
                    //发帖成功，拼帖子链接，注意这个帖子链接需要302跳转
                    //https://club.autohome.com.cn/bbs/thread-a-100022-77217156-1.html?036765385695959547
                    String prefixUrl = requestContext.getPrefixUrl().split(".")[0];
                    String targetUrl = prefixUrl+"-"+topicId+"-1.html?636765385695959547";
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
            HttpPost httpPost = createCommentPost(requestContext,loginResultDTO);
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

    private HttpPost createCommentPost(RequestContext requestContext,LoginResultDTO loginResultDTO) {
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
                String uniquePageId = FetchParamUtil.getMatherStr(content,"tz.uniquePageId = \".*\"");
                uniquePageId = FetchParamUtil.getMatherStr(uniquePageId,"\".*\"");
                uniquePageId = uniquePageId.replaceAll("\"","");

                String topicId = FetchParamUtil.getMatherStr(content,"tz.topicId=.*");
                topicId = FetchParamUtil.getMatherStr(topicId,"\\d+");

                String bbs = FetchParamUtil.getMatherStr(content,"tz.bbs=\".*\"");
                bbs = FetchParamUtil.getMatherStr(bbs,"\".*\"");
                bbs = bbs.replaceAll("\"","");

                String bbsId = FetchParamUtil.getMatherStr(content,"tz.bbsid=.*");
                bbsId = FetchParamUtil.getMatherStr(bbsId,"\\d+");

                String targetUrl = "https://club.autohome.com.cn/Detail/AddReply";
                HttpPost httpPost = new HttpPost(targetUrl);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("bbs", bbs));
                nameValuePairs.add(new BasicNameValuePair("bbsid", bbsId));
                nameValuePairs.add(new BasicNameValuePair("topicId", topicId));
                nameValuePairs.add(new BasicNameValuePair("uniquepageid", uniquePageId));
                nameValuePairs.add(new BasicNameValuePair("content", requestContext.getContent().getText()));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                setCreateCommentHeader(requestContext,httpPost,loginResultDTO.getCookieStore());
                return httpPost;
            }

        }catch (Exception e) {
            logger.error("createComment error!",e);
        }

        return null;
    }

    private void setCreateCommentHeader(RequestContext requestContext, HttpPost httpPost, BasicCookieStore cookieStore) {
        //Host: club.autohome.com.cn
        //Origin: https://club.autohome.com.cn
        //Referer: https://club.autohome.com.cn/bbs/thread/f0dd96c0c425b905/77251423-1.html
        //User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36
        //X-Requested-With: XMLHttpRequest
        httpPost.setHeader("Host","club.autohome.com.cn");
        httpPost.setHeader("Origin","https://club.autohome.com.cn");
        httpPost.setHeader("Referer",requestContext.getPrefixUrl());
        httpPost.setHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        httpPost.setHeader("X-Requested-With","XMLHttpRequest");
        //设置cookie
        StringBuilder stringBuilder = new StringBuilder();
        String s=null;
        for (Cookie cookie : cookieStore.getCookies()) {
            if ("clubUserShow".equalsIgnoreCase(cookie.getName())){
                s=cookie.getValue();
                break;
            }
        }
        httpPost.setHeader("Cookie", "clubUserShow=" + s);
    }

    private void setFetchTopicsHeader(HttpGet httpGet) {
        //Host: club.autohome.com.cn
        //Upgrade-Insecure-Requests: 1
        //User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36

        httpGet.setHeader("Host","club.autohome.com.cn");
        httpGet.setHeader("Upgrade-Insecure-Requests","1");
        httpGet.setHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
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
        }catch (Exception e) {
            logger.error("[createPublishHttpPost] error!",e);
            return  null;
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
        temp2 = FetchParamUtil.getMatherStr(temp2,"-.*-\\d+");
        temp2 = FetchParamUtil.getMatherStr(temp2,"-.*-");
        String type = temp2.replaceAll("-","");
        //type = type.replaceAll("-", "");
        String forumId = FetchParamUtil.getMatherStr(temp, "-\\d+-");
        forumId = forumId.replaceAll("-", "");
        //https://clubajax.autohome.com.cn/NewPost/CardPost?bbs=c&bbsId=2615&urlbbsId=2615&from_bj=0
        String reffer = "https://clubajax.autohome.com.cn/NewPost/CardPost?bbs=" + type + "&bbsId=" + forumId + "&urlbbsId=" + forumId + "&from_bj=0";
        httpPost.setHeader("Referer", reffer);
        httpPost.setHeader("Content-Type","application/x-www-form-urlencoded");

        //设置cookie
        StringBuilder stringBuilder = new StringBuilder();
        String s=null;
        for (Cookie cookie : cookieStore.getCookies()) {
            if ("clubUserShow".equalsIgnoreCase(cookie.getName())){
                s=cookie.getValue();
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
            String str = createTopicContent(memberId, type, forumId, requestContext.getContent().getTitle(), requestContext.getContent().getText());
            StringEntity entity = new StringEntity(str,"UTF-8");
            entity.setContentType("application/x-www-form-urlencoded");
            entity.setContentEncoding("UTF-8");
            httpPost.setEntity(entity);
            return httpPost;
        } catch (Exception e) {
            logger.error("create publish error!", e);
            return null;
}

    }


    private String createTopicContent(String memberId, String bbs, String bbsId, String title, String content) {
        //{"topicmain":{"topicid":0,"title":"有没有一起组团参加野外游的","source":"PC.CARD","memberid":"82819437","bbs":"a","bbsid":"100025","clientip":"{$realip$}","autohomeua":"","reply_notify_me":1,"informfriends":1},"topicext":{"lon":0,"lat":0,"postaddress":"","landmark":""},"topiccards":[{"ctype":2,"url":"","des":"想组团去成都自驾游，有没有可以一起的","otherattributes":{"linkurl":""}}]}
        String str = "{\"topicmain\":{\"topicid\":0,\"title\":\"" + title + "\",\"source\":\"PC.CARD\",\"memberid\":\"" + memberId + "\",\"bbs\":\"" + bbs + "\",\"bbsid\":\"" + bbsId + "\",\"clientip\":\"{$realip$}\",\"autohomeua\":\"\",\"reply_notify_me\":1,\"informfriends\":1},\"topicext\":{\"lon\":0,\"lat\":0,\"postaddress\":\"\",\"landmark\":\"\"},\"topiccards\":[{\"ctype\":2,\"url\":\"\",\"des\":\"" + content + "\",\"otherattributes\":{\"linkurl\":\"\"}}]}";
        return str;
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
            String cid = (String) requestContext.getRequestParam().get(RequestConsts.COMMENT_ID);
            if (cid == null) {
                return null;
            }
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


}


