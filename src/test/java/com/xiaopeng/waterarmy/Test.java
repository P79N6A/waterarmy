package com.xiaopeng.waterarmy;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.xiaopeng.waterarmy.common.constants.HttpConstants;
import com.xiaopeng.waterarmy.handle.Util.FetchParamUtil;
import com.xiaopeng.waterarmy.handle.Util.ResolveUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String []args) {

        //test();
        math();
       /* try {
            //String url = "http://www.xcar.com.cn/bbs/viewthread.php?tid=32920945";
            String url = "https://www.d1ev.com/carnews/xinche/77669";
            Document doc = Jsoup.connect(url).timeout(2000).get();
            String a = null;
            String b = null;
            String c = null;
            getNewId(doc.toString(),a,b,c);

            Elements elements = doc.select("[name=_token]") ;
            String token = elements.get(0).val();
            Element element = doc.tagName("input");

            doc.tagName("");
            Elements contents = doc.getElementsByAttribute("_token");
            //Element token = contents.get(0);
            //String token1 =  token.val();
            String tid = contents.attr("data-src");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }


    private static void getNewId(String  body ,String targetId,String targetType,String targetUserId) {

        //获取targetid
        String targetIdPattern = "targetId:.*(\\d+),";
        String targetTypePattern = "targetType:.*(\\d+),";
        String userIdPattern = "https://www.d1ev.com/user/(\\d+)";

        targetId = FetchParamUtil.getMatherStr(body,targetIdPattern);
        targetType = FetchParamUtil.getMatherStr(body,targetTypePattern);
        targetUserId = FetchParamUtil.getMatherStr(body,userIdPattern);

        String commonPattern = "(\\d+)";
        FetchParamUtil.getMatherStr(targetId,commonPattern);
        FetchParamUtil.getMatherStr(targetType,commonPattern);
        FetchParamUtil.getMatherStr(targetUserId,commonPattern);

        System.out.println("123");
    }

    private static void test() {
       /* String str = "{\"list\":[{\"editor\":1,\"fid\":16488,\"floor\":1,\"forumName\":\"新奥拓论坛\",\"forumUrl\":\"//bbs.pcauto.com.cn/forum-16488.html\",\"image\":\"\",\"isContainImage\":false,\"isNew\":true,\"lastPostAt\":\"18-10-04 22:50\",\"message\":\"邻居说，轮胎蹭大梁，花了100大洋去做四轮定位回来……拿着两桶清洗剂说，准备自己清洗积碳！我也是醉了，就两桶清洗剂自己就搞了……说话中，邻居停车完毕，就打算自己操作了！\\n带大家欣赏一下这个奇葩的清洗积碳吧……\",\"pick\":0,\"pick1\":0,\"pid\":156977580,\"replyCount\":0,\"title\":\"国庆遇到巨坑\",\"topicCreateAt\":1538664652000,\"topicId\":17512037,\"url\":\"//bbs.pcauto.com.cn/topic-17512037.html\",\"userName\":\"eiaxal8142\",\"userUrl\":\"//my.pcauto.com.cn/47446648\",\"viewCount\":10}],\"listCount\":1}";
        JSONObject jsonObject = JSONObject.parseObject(str);
        JSONArray jsonArray = (JSONArray) jsonObject.get("list");
        JSONObject object = (JSONObject)jsonArray.get(0);
        String url = (String) object.get("url");*/

       /* String pattern = "(\\d+).html";
        String str = FetchParamUtil.getMatherStr("http://baa.bitauto.com/baoma3xi/koubei-15758125.html",pattern);
        System.out.println(str);*/
/*
        String url = "http://baa.bitauto.com/xiaopengqicheg3/thread-15742669.html";
        String pattern = ".com(\\/.*\\/)";
        String temp = FetchParamUtil.getMatherStr(url,pattern);
        String temp2 = FetchParamUtil.getMatherStr(temp,"\\/(.*)\\/");
        temp2 = temp2.replaceAll("/","");
        String temp3 = FetchParamUtil.getMatherStr(temp2,"([0-9a-zA-Z]*)");
        System.out.println("");*/
/*
        String content = "{fid:0,fgid:8775,tid:0,pid:0,parentId:0}";
        String url = FetchParamUtil.getMatherStr(content,"\\{fid:.*\\}");
        System.out.println();*/

       /* String title = ResolveUtil.fetchTitle("http://baa.bitauto.com/drive/thread-15745850.html");
        System.out.println();*/

       try {

           String url = "https://a.xcar.com.cn/bbs/thread-32981769-0.html";
           CookieStore cookieStore = new BasicCookieStore();
           CloseableHttpClient closeableHttpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
           HttpGet httpGet = new HttpGet(url);
           CloseableHttpResponse response = closeableHttpClient.execute(httpGet);
           HttpEntity entity = response.getEntity();
           StringBuffer tmpcookies = new StringBuffer();

           cookieStore.getCookies();

           List<Cookie> cookies = cookieStore.getCookies();
            String content = EntityUtils.toString(entity, "utf-8");
            System.out.println(content);


          /* Document doc = Jsoup.connect(url).timeout(2000).get();
           Element element = doc.body();
           String str = element.val();
           Elements elements = doc.select("[name=_token]") ;*/



           final WebClient webClient = new WebClient(BrowserVersion.CHROME);//新建一个模拟谷歌Chrome浏览器的浏览器客户端对象

           webClient.getOptions().setThrowExceptionOnScriptError(false);//当JS执行出错的时候是否抛出异常, 这里选择不需要
           webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);//当HTTP的状态非200时是否抛出异常, 这里选择不需要
           webClient.getOptions().setActiveXNative(false);
           webClient.getOptions().setCssEnabled(false);//是否启用CSS, 因为不需要展现页面, 所以不需要启用
           webClient.getOptions().setJavaScriptEnabled(true); //很重要，启用JS
           //webClient.setAjaxController(new NicelyResynchronizingAjaxController());//很重要，设置支持AJAX

           HtmlPage page = null;
           try {
               page = webClient.getPage(url);//尝试加载上面图片例子给出的网页
           } catch (Exception e) {
               e.printStackTrace();
           }finally {
               webClient.close();
           }

           webClient.waitForBackgroundJavaScript(3000);//异步JS执行需要耗时,所以这里线程要阻塞30秒,等待异步JS执行结束



           String pageXml = page.asXml();//直接将加载完成的页面转换成xml格式的字符串
           //TODO 下面的代码就是对字符串的操作了,常规的爬虫操作,用到了比较好用的Jsoup库
           Document document = Jsoup.parse(pageXml);//获取html文档

           String str = document.toString();
           String str1 = FetchParamUtil.getMatherStr(str,"\\{tid:.*ssid:.*\\}");

           System.out.println();




       }catch (Exception e) {
           e.printStackTrace();
       }


    }


    private static void  math() {
        String match = "{tid:33934569,fid:540,action:'reply',mt:Math.random(),land:'lord',message:encodeURIComponent(message),formhash:'092c41da',usesig:1,ssid:1538828909,repquote:pid,replysubmit:'yes',repquote_authorid:post_author_id}";


        String str = match;
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

        return;
    }

}
