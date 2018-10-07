package com.xiaopeng.waterarmy.handle.impl;

import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xiaopeng.waterarmy.common.Result.Result;
import com.xiaopeng.waterarmy.common.enums.ResultCodeEnum;
import com.xiaopeng.waterarmy.handle.PlatformHandler;
import com.xiaopeng.waterarmy.handle.Util.FetchParamUtil;
import com.xiaopeng.waterarmy.handle.Util.ResultParamUtil;
import com.xiaopeng.waterarmy.handle.Util.WebClientFatory;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.handle.param.SaveContext;
import com.xiaopeng.waterarmy.handle.result.HandlerResultDTO;
import com.xiaopeng.waterarmy.handle.result.LoginResultDTO;
import com.xiaopeng.waterarmy.model.dao.CommentInfo;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AiKaHandler extends PlatformHandler {

    private static Logger logger = LoggerFactory.getLogger(AiKaHandler.class);

    private static final String TARGET_COMMENT_URL = "http://www.xcar.com.cn/bbs/post.php";

    @Autowired
    private AiKaLoginHandler aiKaLoginHandler;

    @Override
    public Result<HandlerResultDTO> publish(RequestContext requestContext) {
        return null;
    }

    @Override
    public Result<HandlerResultDTO> comment(RequestContext requestContext) {
        Result<LoginResultDTO> resultDTOResult = aiKaLoginHandler.login(requestContext.getUserId());
        if (!resultDTOResult.getSuccess()) {
            logger.error("[DiYiDianDongHandler.comment] requestContext" + requestContext);
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
                JSONObject jsonObject = JSONObject.parseObject(content);
                int stauts = (int) jsonObject.get("stat");
                if (stauts == 1) {
                    HandlerResultDTO handlerResultDTO = ResultParamUtil.createHandlerResultDTO(requestContext, content);
                    CommentInfo commentInfo = ResultParamUtil.createCommentInfo(requestContext, content);
                    save(new SaveContext(commentInfo));
                    return new Result(handlerResultDTO);
                }
                //评论成功
            }
        } catch (Exception e) {
            logger.error("[AiKaHandler.comment] error!", e);
        }
        return new Result<>(ResultCodeEnum.HANDLE_FAILED);

    }


    private HttpPost createCommentPost(RequestContext requestContext) {

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
            HashMap map = new HashMap();
            getParameters(requestContext, map);
            nameValuePairs.add(new BasicNameValuePair("tid", (String) map.get("tid")));
            nameValuePairs.add(new BasicNameValuePair("fid", (String) map.get("fid")));
            nameValuePairs.add(new BasicNameValuePair("action", "reply"));
            nameValuePairs.add(new BasicNameValuePair("mt", " 0.022039394939090906"));
            nameValuePairs.add(new BasicNameValuePair("land", "lord"));
            nameValuePairs.add(new BasicNameValuePair("message", URLEncoder.encode(requestContext.getContent().getText(), "UTF-8")));//"%E5%A5%BD%E6%83%B3%E4%B9%9F%E5%8E%BB%E5%81%9A%E4%B8%80%E4%B8%8B"
            nameValuePairs.add(new BasicNameValuePair("formhash", (String) map.get("formhash")));//edacfc30
            nameValuePairs.add(new BasicNameValuePair("usesig", "1"));
            nameValuePairs.add(new BasicNameValuePair("ssid", (String) map.get("ssid")));//1538824543
            nameValuePairs.add(new BasicNameValuePair("replysubmit", "yes"));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (Exception e) {
            logger.error("[AiKaHandler.createCommentPost]createCommentPost  UrlEncodedFormEntity error! nameValuePairs" + nameValuePairs,e);
            return null;
        }
        return httpPost;
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
                logger.error("getParameters",e);
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

        String tid = FetchParamUtil.getMatherStr(str1,"tid:.*?,");
        tid = FetchParamUtil.getMatherStr(tid,"\\d+");


        String fid = FetchParamUtil.getMatherStr(str1,"fid:.*?,");
        fid = FetchParamUtil.getMatherStr(fid,"\\d+");

        String ssid =  FetchParamUtil.getMatherStr(str1,"ssid:.*?,");
        ssid = FetchParamUtil.getMatherStr(ssid,"\\d+");

        String formhash = FetchParamUtil.getMatherStr(str1,"formhash:.*?,");
        formhash = FetchParamUtil.getMatherStr(formhash,"\\'.*\\'");
        formhash = formhash.replaceAll("'","");


        map.put("fid", fid);
        map.put("tid", tid);
        map.put("ssid", ssid);
        map.put("formhash", formhash);
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


    @Override
    public Result<HandlerResultDTO> read(RequestContext requestContext) {
        return null;
    }

    @Override
    public Result<HandlerResultDTO> praise(RequestContext requestContext) {
        return null;
    }
}
