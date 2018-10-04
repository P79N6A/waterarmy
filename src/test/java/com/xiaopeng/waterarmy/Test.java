package com.xiaopeng.waterarmy;

import com.xiaopeng.waterarmy.common.constants.HttpConstants;
import com.xiaopeng.waterarmy.handle.Util.FetchParamUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Test {
    public static void main(String []args) {
        try {
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
        }
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


}
