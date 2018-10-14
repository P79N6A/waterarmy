package com.xiaopeng.waterarmy.handle.impl;

import com.alibaba.fastjson.JSONObject;
import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.constants.RequestConsts;
import com.xiaopeng.waterarmy.common.enums.ResultCodeEnum;
import com.xiaopeng.waterarmy.common.enums.TaskEntryTypeEnum;
import com.xiaopeng.waterarmy.common.util.HtmlPlayUtil;
import com.xiaopeng.waterarmy.common.util.HtmlReadUtil;
import com.xiaopeng.waterarmy.handle.PlatformHandler;
import com.xiaopeng.waterarmy.handle.Util.FetchParamUtil;
import com.xiaopeng.waterarmy.handle.Util.HttpFactory;
import com.xiaopeng.waterarmy.handle.Util.ResultParamUtil;
import com.xiaopeng.waterarmy.handle.Util.TranslateCodeUtil;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.handle.param.SaveContext;
import com.xiaopeng.waterarmy.handle.result.HandlerResultDTO;
import com.xiaopeng.waterarmy.handle.result.LoginResultDTO;
import com.xiaopeng.waterarmy.model.dao.CommentInfo;
import com.xiaopeng.waterarmy.model.dao.PraiseInfo;
import com.xiaopeng.waterarmy.model.dao.PublishInfo;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Component
public class YiCheHandler extends PlatformHandler {

    private static Logger logger = LoggerFactory.getLogger(YiCheHandler.class);

    private static final String TOPIC = "topic";

    private static final String ForumApp = "forumApp";

    private static final String TARGET_COMMENT_SUFFIX = "/Ajax/CreateReply.aspx";

    private static final String TARGET_COMMON_PREFIX = "http://baa.bitauto.com/";

    private static final String TARGET_PUBLISH_BASE_SUFFIX = "/Ajax/CreateTopic.aspx";

    private static final String TARGET_PUBLISH_CODE = "http://baa.bitauto.com/others/CheckCode.aspx?guid=";


    private static final String TARGET_PUBLISH_SUFFIX = "posttopic-0.html";

    private static final int MAX_RETRY_PUBLISH = 20;

    private static final String TARGET_CHEJIA_COMMENT = "http://newsapi.bitauto.com/comment/comment/sava";

    private static String getUserIdUrl1 = "http://news.bitauto.com/hao/wenzhang/963762";

    private static String TARGET_COMMENT_NEWS_PRAISE = "http://newsapi.bitauto.com/comment/like/update";

    @Autowired
    private YiCheLoginHandler yiCheLoginHandler;

    @Override
    public Result<HandlerResultDTO> publish(RequestContext requestContext) {
        Result<LoginResultDTO> resultDTOResult = yiCheLoginHandler.login(requestContext.getUserId());
        if (!resultDTOResult.getSuccess()) {
            logger.error("[YiCheHandler.publish] requestContext" + requestContext);
            return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
        }

        boolean needCode = false;
        String targetUrl = null;
        int retry = MAX_RETRY_PUBLISH;

        try {
            while (retry>0) {
                LoginResultDTO loginResultDTO = resultDTOResult.getData();
                CloseableHttpClient httpClient = loginResultDTO.getHttpClient();
                HttpPost httpPost = createPublishHttpPost(requestContext, loginResultDTO,targetUrl,needCode);
                CloseableHttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                String content = null;
                if (entity != null) {
                    content = EntityUtils.toString(entity, "utf-8");
                    if(content.contains("returnUrl")) {
                        JSONObject jsonObject = JSONObject.parseObject(content);
                        String returnUrl = (String) jsonObject.get("returnUrl");
                        PublishInfo publishInfo = ResultParamUtil.createPublishInfo(requestContext, content, returnUrl);
                        save(new SaveContext(publishInfo));
                        HandlerResultDTO handlerResultDTO = ResultParamUtil.createHandlerResultDTO(requestContext, content, returnUrl);
                        return new Result(handlerResultDTO);
                    }
                    //在这里处理验证码,需要验证码或者验证码处理失败
                    if (content.contains("验证码")) {
                        needCode = true;
                        retry--;
                        Thread.sleep(500);
                        continue;
                    }
                }
                return new Result<>(ResultCodeEnum.HANDLE_FAILED);
            }

        } catch (Exception e) {
            logger.error("[YiCheHandler.comment] error!", e);
            //处理失败的回复，把context记录下来，可以决定是否重新扫描,并且记录失败原因
            return new Result<>(ResultCodeEnum.HANDLE_FAILED);
        }
        return new Result<>(ResultCodeEnum.HANDLE_FAILED);
    }


    private String createPublishUrl(RequestContext requestContext, LoginResultDTO loginResultDTO) {
        String forumApp = null;
        if (requestContext.getRequestParam() != null) {
            Object object1 = requestContext.getRequestParam().get(ForumApp);
            if (object1 != null) {
                forumApp = (String) object1;
            }
        }

        if (forumApp == null) {
            forumApp = getForumApp(requestContext.getPrefixUrl());
        }

        String targetBaseUrl = TARGET_COMMON_PREFIX+forumApp+TARGET_PUBLISH_BASE_SUFFIX;

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

            String url = targetBaseUrl + "?" + str;
            return url;
        } catch (Exception e) {
            logger.error("[YiCheHandler.createPublishUrl]", e);
        }
        return null;
    }

    private HttpPost createPublishHttpPost(RequestContext requestContext, LoginResultDTO loginResultDTO,String targetUrl,boolean needCode) {
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


        if (targetUrl==null) {
            targetUrl = createPublishUrl(requestContext, loginResultDTO);
        }
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

        if (needCode) {
            String code = translatePublishCode(guid);
            nameValuePairs.add(new BasicNameValuePair("hdnCheckCode", code));
        }

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (Exception e) {
            logger.error("[YiCheHandler.createCommentPost]createPublishHttpPost  UrlEncodedFormEntity error! nameValuePairs" + nameValuePairs);
            return null;
        }
        return httpPost;
    }


    private String  translatePublishCode(String guid) {
        String url = TARGET_PUBLISH_CODE+guid+"&d=0.6218608967502508";
        String code = TranslateCodeUtil.getInstance().convertWithRegx(url,"[a-zA-Z]+");
        return code;
    }


    @Override
    public Result<HandlerResultDTO> comment(RequestContext requestContext) {

        //易车车家号评论
        if (TaskEntryTypeEnum.YICHENEWSCOMMENT.equals(requestContext.getHandleEntryType())) {
            return commentNews(requestContext);
        }
        if (TaskEntryTypeEnum.YICHEKOUBEICOMMENT.equals(requestContext.getHandleEntryType())) {
            return commentKoubei(requestContext);
        }
        return commentForum(requestContext);
    }

    /**
     * 论坛评论
     * @param requestContext
     * @return
     */
    private Result<HandlerResultDTO> commentForum(RequestContext requestContext) {
        //论坛评论
        Result<LoginResultDTO> resultDTOResult = yiCheLoginHandler.login(requestContext.getUserId());
        if (!resultDTOResult.getSuccess()) {
            logger.error("[YiCheHandler.commentForum] requestContext" + requestContext);
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
            logger.error("[YiCheHandler.commentForum] error!", e);
        }
        return new Result<>(ResultCodeEnum.HANDLE_FAILED);
    }


    /**
     * 车家号评论
     * @param requestContext
     * @return
     */
    private Result<HandlerResultDTO> commentNews(RequestContext requestContext) {
        Result<LoginResultDTO> resultDTOResult = yiCheLoginHandler.login(requestContext.getUserId());
        if (!resultDTOResult.getSuccess()) {
            logger.error("[YiCheHandler.commentNews] requestContext" + requestContext);
            return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
        }
        try {
            LoginResultDTO loginResultDTO = resultDTOResult.getData();
            CloseableHttpClient httpClient = loginResultDTO.getHttpClient();
            HttpPost httpPost = createCommentNewsPost(requestContext,loginResultDTO);
            setCommentNewsHeader(httpPost);
            httpPost.setHeader("Referer",requestContext.getPrefixUrl());
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = null;
            if (entity != null) {
                content = EntityUtils.toString(entity, "utf-8");
                if (content.contains("OK")) {//评论成功
                    //评论成功
                    HandlerResultDTO handlerResultDTO = ResultParamUtil.createHandlerResultDTO(requestContext, content);
                    CommentInfo commentInfo = ResultParamUtil.createCommentInfo(requestContext, content);
                    save(new SaveContext(commentInfo));
                    return new Result(handlerResultDTO);
                }
            }
        } catch (Exception e) {
            logger.error("[YiCheHandler.commentNews] error!", e);
        }
        return new Result<>(ResultCodeEnum.HANDLE_FAILED);



    }

    /**
     * 口碑评论
     * @param requestContext
     * @return
     */
    private Result<HandlerResultDTO> commentKoubei(RequestContext requestContext) {
        Result<LoginResultDTO> resultDTOResult = yiCheLoginHandler.login(requestContext.getUserId());
        if (!resultDTOResult.getSuccess()) {
            logger.error("[YiCheYangHandler.comment] requestContext" + requestContext);
            return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
        }
        try {
            LoginResultDTO loginResultDTO = resultDTOResult.getData();
            CloseableHttpClient httpClient = loginResultDTO.getHttpClient();
            HttpPost httpPost = createCommentKoubeiPost(requestContext,httpClient);
            setCommentKouBeiHeader(httpPost);
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = null;
            if (entity != null) {
                content = EntityUtils.toString(entity, "utf-8");
                if (content.contains("OK")) {//评论成功
                    //评论成功
                    HandlerResultDTO handlerResultDTO = ResultParamUtil.createHandlerResultDTO(requestContext, content);
                    CommentInfo commentInfo = ResultParamUtil.createCommentInfo(requestContext, content);
                    save(new SaveContext(commentInfo));
                    return new Result(handlerResultDTO);
                }
            }
        } catch (Exception e) {
            logger.error("[YiCheHandler.commentKoubei] error!", e);
        }
        return new Result<>(ResultCodeEnum.HANDLE_FAILED);



    }



    private HttpPost createCommentNewsPost(RequestContext requestContext, LoginResultDTO loginResultDTO) {
        try {
            String url = requestContext.getPrefixUrl();
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = loginResultDTO.getHttpClient().execute(httpGet);
            HttpEntity entity = response.getEntity();
            String content;
            if (entity!=null) {
                content = EntityUtils.toString(entity, "utf-8");
                String productId = FetchParamUtil.getMatherStr(content,"productId: \\d+,");
                productId = FetchParamUtil.getMatherStr(productId,"\\d+");

                String objectId = FetchParamUtil.getMatherStr(content,"objectId: \\'\\d+");
                objectId = FetchParamUtil.getMatherStr(objectId,"\\d+");

                Document doc = Jsoup.parse(content);
                String title = doc.title();

              /*  String title = FetchParamUtil.getMatherStr(content,"title: \\'.*\\',");
                title = FetchParamUtil.getMatherStr(title,"\\'.*\\'");
                title = title.replaceAll("'","");*/

                String createtime = FetchParamUtil.getMatherStr(content,"createtime: \\'.*\\',");
                createtime = FetchParamUtil.getMatherStr(createtime,"\\'.*\\'");
                createtime = createtime.replaceAll("'","");


              /*  String userid = FetchParamUtil.getMatherStr(content,"userid: \\d+,");
                if (userid!=null) {
                    userid = FetchParamUtil.getMatherStr(userid,"\\d+");
                }
                if (userid==null||userid.equals("0")) {
                    //其他页面拿不到userid，通过特定的url获取
                    HttpGet httpGet1 = new HttpGet(getUserIdUrl1);
                    CloseableHttpResponse response1 = loginResultDTO.getHttpClient().execute(httpGet1);
                    HttpEntity entity1 = response1.getEntity();
                    String content1 = EntityUtils.toString(entity1, "utf-8");
                    content = content1;
                    userid = FetchParamUtil.getMatherStr(content,"userid: \\d+,");
                }
                userid = FetchParamUtil.getMatherStr(userid,"\\d+");

                String source = FetchParamUtil.getMatherStr(content,"source: \\d+,");
                source = FetchParamUtil.getMatherStr(source,"\\d+");

              *//*  String type = FetchParamUtil.getMatherStr(content,"type: \\d+,");
                type = FetchParamUtil.getMatherStr(type,"\\d+");*//*

             *//*   String username = FetchParamUtil.getMatherStr(content,"nickname:'.*\\',");
                username = FetchParamUtil.getMatherStr(username,"\\'.*\\'");
                username = username.replaceAll("'","");*/


                String username= loginResultDTO.getOutUserName();
                String userid = loginResultDTO.getOutUserId();
                HttpPost httpPost = new HttpPost(TARGET_CHEJIA_COMMENT);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("productId", productId));
                nameValuePairs.add(new BasicNameValuePair("objectId", objectId));
                nameValuePairs.add(new BasicNameValuePair("title", title));
                nameValuePairs.add(new BasicNameValuePair("url", requestContext.getPrefixUrl()));
                nameValuePairs.add(new BasicNameValuePair("createtime", createtime));
                nameValuePairs.add(new BasicNameValuePair("parentId", "0"));
                nameValuePairs.add(new BasicNameValuePair("userId", userid));
                nameValuePairs.add(new BasicNameValuePair("userName", username));
                nameValuePairs.add(new BasicNameValuePair("content", requestContext.getContent().getText()));
                nameValuePairs.add(new BasicNameValuePair("client", "1"));
                nameValuePairs.add(new BasicNameValuePair("source", "0"));//source));

                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                } catch (Exception e) {
                    logger.error("[YiCheHandler.createCommentNewsPost]createCommentPost  UrlEncodedFormEntity error! nameValuePairs" + nameValuePairs);
                    return null;
                }
                return httpPost;
            }

            /**
             * productId: 8
             * objectId: 963762
             * objectuuId:
             * title: 除了穷，我还有7个不买后驱奥迪R8 RWS的理由
             * url: http://news.bitauto.com/hao/wenzhang/963762
             * url_m:
             * createtime: 2018-10-08 10:30:36
             * parentId: 0
             * userId: 34331790
             * userName: zdkipp577148
             * content: 这个车真心不错
             * client: 1
             * source: 0
             */

        }catch (Exception e) {
            return null;
        }
        return null;
    }


    private HttpPost createCommentKoubeiPost(RequestContext requestContext,CloseableHttpClient client) {
        try {
            String url = requestContext.getPrefixUrl();
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String content;
            if (entity!=null) {
                content = EntityUtils.toString(entity, "utf-8");
                String productId = FetchParamUtil.getMatherStr(content,"productId: \\d+,");
                productId = FetchParamUtil.getMatherStr(productId,"\\d+");

                String objectId = FetchParamUtil.getMatherStr(content,"objectId: \\d+,");
                objectId = FetchParamUtil.getMatherStr(objectId,"\\d+");

                String objectuuId = FetchParamUtil.getMatherStr(content,"objectuuId: \".*\",");
                objectuuId = FetchParamUtil.getMatherStr(objectuuId,"\".*\"");
                objectuuId = objectuuId.replaceAll("\"","");

                Document doc = Jsoup.parse(content);
                String title = doc.title();

               /* String title = FetchParamUtil.getMatherStr(content,"title: \".*\",");
                title = FetchParamUtil.getMatherStr(title,"\".*\"");
                title = title.replaceAll("\"","");*/

              /*  String url1 = FetchParamUtil.getMatherStr(content,"url: \".*\",");
                url1 = FetchParamUtil.getMatherStr(title,"\".*\"");
                url1 = url1.replaceAll("\"","");*/

                String urlm = FetchParamUtil.getMatherStr(content,"urlm: \".*\",");
                urlm = FetchParamUtil.getMatherStr(urlm,"\".*\"");
                urlm = urlm.replaceAll("\"","");

                String createtime = FetchParamUtil.getMatherStr(content,"createtime: \".*\",");
                createtime = FetchParamUtil.getMatherStr(createtime,"\".*\"");
                createtime = createtime.replaceAll("\"","");




                //其他页面拿不到userid，通过特定的url获取
                HttpGet httpGet1 = new HttpGet(getUserIdUrl1);
                CloseableHttpResponse response1 = client.execute(httpGet1);
                HttpEntity entity1 = response1.getEntity();
                String content1 = EntityUtils.toString(entity1, "utf-8");
                content = content1;

                String userid = FetchParamUtil.getMatherStr(content,"userid: \\d+,");
                userid = FetchParamUtil.getMatherStr(userid,"\\d+");

                String source = FetchParamUtil.getMatherStr(content,"source: \\d+,");
                source = FetchParamUtil.getMatherStr(source,"\\d+");


                String username = FetchParamUtil.getMatherStr(content,"username: \\'.*\\',");
                username = FetchParamUtil.getMatherStr(username,"\\'.*\\'");
                username = username.replaceAll("'","");


                /**
                 * productId: 6
                 * objectId: 968281
                 * objectuuId: e50af8bf-347f-4568-b3ad-95fe53fb2e3b
                 * title: 口碑968281
                 * url: http://car.bitauto.com/dibadaiyage/koubei/968281/
                 * url_m: http://car.m.yiche.com/dibadaiyage/koubei/968281/
                 * createtime: 2018-09-28 10:51:56
                 * parentId: 0
                 * userId: 34331669
                 * userName: onwcak849422
                 * content: 加油 买下它
                 * client: 1
                 * source: 0
                 */

                HttpPost httpPost = new HttpPost(TARGET_CHEJIA_COMMENT);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("productId", productId));
                nameValuePairs.add(new BasicNameValuePair("objectId", objectId));
                nameValuePairs.add(new BasicNameValuePair("objectuuId", objectuuId));
                nameValuePairs.add(new BasicNameValuePair("title", title));
                nameValuePairs.add(new BasicNameValuePair("url", requestContext.getPrefixUrl()));
                nameValuePairs.add(new BasicNameValuePair("url_m", urlm));
                nameValuePairs.add(new BasicNameValuePair("createtime", createtime));
                nameValuePairs.add(new BasicNameValuePair("parentId", "0"));
                nameValuePairs.add(new BasicNameValuePair("userId", userid));
                nameValuePairs.add(new BasicNameValuePair("userName", username));
                nameValuePairs.add(new BasicNameValuePair("content", requestContext.getContent().getText()));
                nameValuePairs.add(new BasicNameValuePair("client", "1"));
                nameValuePairs.add(new BasicNameValuePair("source", source));

                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                } catch (Exception e) {
                    logger.error("[YiCheHandler.createCommentNewsPost]createCommentPost  UrlEncodedFormEntity error! nameValuePairs" + nameValuePairs);
                    return null;
                }
                return httpPost;
            }

            /**
             * productId: 8
             * objectId: 963762
             * objectuuId:
             * title: 除了穷，我还有7个不买后驱奥迪R8 RWS的理由
             * url: http://news.bitauto.com/hao/wenzhang/963762
             * url_m:
             * createtime: 2018-10-08 10:30:36
             * parentId: 0
             * userId: 34331790
             * userName: zdkipp577148
             * content: 这个车真心不错
             * client: 1
             * source: 0
             */

        }catch (Exception e) {
            return null;
        }
        return null;
    }



    private Result<HandlerResultDTO> commentNewsPraise(RequestContext requestContext) {
        /**
         * callback: jQuery18006913486550558714_1539437102307
         * userid: 34331669
         * commentId: 257980449698086912
         * objectId: 8346394
         * productId: 1
         * _: 1539437118080
         */

        Result<LoginResultDTO> resultDTOResult = yiCheLoginHandler.login(requestContext.getUserId());
        if (!resultDTOResult.getSuccess()) {
            logger.error("[YiCheHandler.comment] requestContext" + requestContext);
            return new Result<>(ResultCodeEnum.LOGIN_FAILED.getIndex(), ResultCodeEnum.LOGIN_FAILED.getDesc());
        }
        try {
            LoginResultDTO loginResultDTO = resultDTOResult.getData();
            CloseableHttpClient httpClient = loginResultDTO.getHttpClient();
            HttpGet httpGet = createCommentNewsPraiseHttpGet(requestContext, httpClient);
            setCommentNewsHeader(httpGet);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String content = null;
            if (entity != null) {
                content = EntityUtils.toString(entity, "utf-8");
                if (content.contains("OK")) {
                    HandlerResultDTO handlerResultDTO = ResultParamUtil.createHandlerResultDTO(requestContext, content);
                    PraiseInfo praiseInfo = ResultParamUtil.createPraiseInfo(requestContext, content);
                    save(new SaveContext(praiseInfo));
                    return new Result(handlerResultDTO);
                }
            }
        }catch (Exception e) {
            logger.error("[YiCheHandler.commentNewsPraise]",e);
            return new Result<>(ResultCodeEnum.HANDLE_FAILED);
        }
        return new Result<>(ResultCodeEnum.HANDLE_FAILED);
    }


    private HttpGet createCommentNewsPraiseHttpGet(RequestContext requestContext,CloseableHttpClient client) {

        //参数校验
        if (requestContext.getRequestParam().get(RequestConsts.COMMENT_ID)==null) {
            return null;
        }

        try {
            String url = requestContext.getPrefixUrl();
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String content;
            if (entity!=null) {
                content = EntityUtils.toString(entity, "utf-8");
                String productId = FetchParamUtil.getMatherStr(content,"productId: \\d+,");
                productId = FetchParamUtil.getMatherStr(productId,"\\d+");

                String objectId = FetchParamUtil.getMatherStr(content,"objectId: \\'\\d+");
                objectId = FetchParamUtil.getMatherStr(objectId,"\\d+");


                HttpGet httpGet1 = new HttpGet(getUserIdUrl1);
                CloseableHttpResponse response1 = client.execute(httpGet1);
                HttpEntity entity1 = response1.getEntity();
                String content1 = EntityUtils.toString(entity1, "utf-8");
                content = content1;

                String userid = FetchParamUtil.getMatherStr(content,"userid: \\d+,");
                userid = FetchParamUtil.getMatherStr(userid,"\\d+");

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("callback", "jQuery18006913486550558714_"+new Date().getTime()));
                nameValuePairs.add(new BasicNameValuePair("userid", userid));
                nameValuePairs.add(new BasicNameValuePair("commentId", (String) requestContext.getRequestParam().get(RequestConsts.COMMENT_ID)));
                nameValuePairs.add(new BasicNameValuePair("objectId", objectId));
                nameValuePairs.add(new BasicNameValuePair("productId", productId));
                nameValuePairs.add(new BasicNameValuePair("_", String.valueOf(new Date().getTime())));
                String str = EntityUtils.toString(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                String target = TARGET_COMMENT_NEWS_PRAISE + "?" + str;
                HttpGet httpGet2 = new HttpGet(target);
                return httpGet2;
            }
        }catch (Exception e) {
            logger.error("[YiCheHandler.createCommentNewsPraisePost] error!",e);
            return null;
        }
        return null;
    }

    private void setCommentNewsHeader(HttpGet httpGet) {
        /**
         * Host: newsapi.bitauto.com
         * Origin: http://news.bitauto.com
         * Referer: http://news.bitauto.com/hao/wenzhang/963762
         */
        httpGet.setHeader("Host","newsapi.bitauto.com");
        httpGet.setHeader("Origin","http://news.bitauto.com");
        httpGet.setHeader("Referer","http://news.bitauto.com");
        httpGet.setHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
    }



    private void setCommentKouBeiHeader(HttpPost httpPost) {
        /**
         * Host: newsapi.bitauto.com
         * Origin: http://car.bitauto.com
         * Referer: http://car.bitauto.com/dibadaiyage/koubei/968281/
         * User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36
         */
        httpPost.setHeader("Host","newsapi.bitauto.com");
        httpPost.setHeader("Origin","http://car.bitauto.com");
        httpPost.setHeader("Referer","http://car.bitauto.com/dibadaiyage/koubei");
        httpPost.setHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");

    }
    private void setCommentNewsHeader(HttpPost httpPost) {
        /**
         * Host: newsapi.bitauto.com
         * Origin: http://news.bitauto.com
         * Referer: http://news.bitauto.com/hao/wenzhang/963762
         */
        httpPost.setHeader("Host","newsapi.bitauto.com");
        httpPost.setHeader("Origin","http://news.bitauto.com");
        httpPost.setHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
    }

    @Override
    public Result<HandlerResultDTO> read(RequestContext requestContext) {
        HtmlReadUtil.read(requestContext.getTargetUrl());
        HandlerResultDTO handlerResultDTO = ResultParamUtil.createHandlerResultDTO(requestContext, "{success:true,url:"+requestContext.getTargetUrl());
        return new Result(handlerResultDTO);
    }

    @Override
    public Result<HandlerResultDTO> play(RequestContext requestContext) {
        HtmlPlayUtil.play(requestContext.getTargetUrl());
        HandlerResultDTO handlerResultDTO = ResultParamUtil.createHandlerResultDTO(requestContext, "{success:true,url:"+requestContext.getTargetUrl());
        return new Result(handlerResultDTO);
    }

    @Override
    public Result<HandlerResultDTO> praise(RequestContext requestContext) {
        return commentNewsPraise(requestContext);
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

        if (topic == null) {
            topic = getTopic(requestContext.getPrefixUrl());
        }

        if (forumApp == null) {
            forumApp = getForumApp(requestContext.getPrefixUrl());
        }

        String targetCommentUrl = TARGET_COMMON_PREFIX+forumApp+TARGET_COMMENT_SUFFIX;

        HttpPost httpPost = new HttpPost(targetCommentUrl);
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
            logger.error("[YiCheHandler.createCommentPost]createCommentPost  UrlEncodedFormEntity error! nameValuePairs"
                    + nameValuePairs, e);
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
        try {
            String pattern = "-(\\d+).html";
            String temp = FetchParamUtil.getMatherStr(url, pattern);
            return FetchParamUtil.getMatherStr(temp, "(\\d+)");
        }catch (Exception e) {
            return getTopicV2(url);
        }

    }

    private String getTopicV2(String url) {
        String pattern = "-(\\d+).-";
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
