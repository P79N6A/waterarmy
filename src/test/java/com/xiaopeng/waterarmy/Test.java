package com.xiaopeng.waterarmy;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaopeng.waterarmy.common.constants.HttpConstants;
import com.xiaopeng.waterarmy.handle.Util.FetchParamUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

public class Test {
    public static void main(String []args) {

        test();
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

        String content = "{fid:0,fgid:8775,tid:0,pid:0,parentId:0}";
        String url = FetchParamUtil.getMatherStr(content,"\\{fid:.*\\}");
        System.out.println();
    }


}
