package com.xiaopeng.waterarmy;

/**
 * Created by iason on 2018/10/13.
 */

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xuyh at 2017/11/6 14:03.
 */
public class HtmlUtilTest {

    @Test
    public void test1() {
        System.out.println(getV4IP());//getLocalAddress()  getWebIp()
    }

    public static String getWebIp() {
        try {

            URL url = new URL("http://iframe.ip138.com/ic.asp");

            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

            String s = "";

            StringBuffer sb = new StringBuffer("");

            String webContent = "";

            while ((s = br.readLine()) != null) {
                sb.append(s + "\r\n");

            }

            br.close();
            webContent = sb.toString();
            int start = webContent.indexOf("[")+1;
            int end = webContent.indexOf("]");
            webContent = webContent.substring(start,end);

            return webContent;

        } catch (Exception e) {
            e.printStackTrace();
            return "error";

        }
    }


    public static String getV4IP() {
        String ip = "";
        String chinaz = "http://ip.chinaz.com/";

        String inputLine = "";
        String read = "";
        try {
            URL url = new URL(chinaz);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while ((read = in.readLine()) != null) {
                inputLine += read;
            }
            System.out.println(inputLine);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Pattern p = Pattern.compile("\\<strong class\\=\"red\">(.*?)\\<\\/strong>");
        Matcher m = p.matcher(inputLine);
        if(m.find()){
            String ipstr = m.group(1);
            System.out.println(ipstr);
        }
        return ip;
    }

    public static String getLocalAddress() {
        String ip = "";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ip;
    }


    public String getRemortIP(HttpServletRequest request) {
        if (request.getHeader("x-forwarded-for") == null) {
            return request.getRemoteAddr();
        }
        return request.getHeader("x-forwarded-for");
    }

    @Test
    public void test() {
        String url = "http://vc.yiche.com/vplay/424665.html";
        final WebClient webClient = new WebClient(BrowserVersion.CHROME);
        //新建一个模拟谷歌Chrome浏览器的浏览器客户端对象

        webClient.getOptions().setThrowExceptionOnScriptError(false);
        //当JS执行出错的时候是否抛出异常, 这里选择不需要
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        //当HTTP的状态非200时是否抛出异常, 这里选择不需要
        webClient.getOptions().setActiveXNative(false);
        webClient.getOptions().setCssEnabled(false);
        //是否启用CSS, 因为不需要展现页面, 所以不需要启用
        webClient.getOptions().setJavaScriptEnabled(true);
        //很重要，启用JS
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        //很重要，设置支持AJAX

        HtmlPage page = null;
        try {
            page = webClient.getPage(url);
            //尝试加载上面图片例子给出的网页
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            webClient.close();
        }

        webClient.waitForBackgroundJavaScript(30000);
        //异步JS执行需要耗时,所以这里线程要阻塞30秒,等待异步JS执行结束

        String pageXml = page.asXml();
        //直接将加载完成的页面转换成xml格式的字符串

        //TODO 下面的代码就是对字符串的操作了,常规的爬虫操作,用到了比较好用的Jsoup库

        Document document = Jsoup.parse(pageXml);//获取html文档
        List<Element> infoListEle = document.getElementById("feedCardContent")
                .getElementsByAttributeValue("class", "feed-card-item");//获取元素节点等
        infoListEle.forEach(element -> {
            System.out.println(element.getElementsByTag("h2").first().getElementsByTag("a").text());
            System.out.println(element.getElementsByTag("h2").first().getElementsByTag("a").attr("href"));
        });
    }

    private static ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("HtmlUtilTest").build();

    private static ExecutorService threadPoolExecutor = new ThreadPoolExecutor(50, 200,
            100000L, TimeUnit.MINUTES,
            new LinkedBlockingQueue<Runnable>(100)
            , threadFactory, new ThreadPoolExecutor.AbortPolicy());

    @Test
    public void play() {
        String url = "http://vc.yiche.com/vplay/424665.html";
        for (int i = 0; i < 200; i++) {
//            threadPoolExecutor.execute(new Runnable() {
//                @Override
//                public void run() {
                    WebClient webClient = new WebClient(BrowserVersion.CHROME);
                    //新建一个模拟谷歌Chrome浏览器的浏览器客户端对象
                    webClient.getOptions().setThrowExceptionOnScriptError(false);
                    //当JS执行出错的时候是否抛出异常, 这里选择不需要
                    webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
                    //当HTTP的状态非200时是否抛出异常, 这里选择不需要
                    webClient.getOptions().setActiveXNative(false);
                    webClient.getOptions().setCssEnabled(false);
                    //是否启用CSS, 因为不需要展现页面, 所以不需要启用
                    webClient.getOptions().setJavaScriptEnabled(true);
                    //很重要，启用JS
                    webClient.setAjaxController(new NicelyResynchronizingAjaxController());
                    //很重要，设置支持AJAX
                    HtmlPage page = null;
                    try {
                        page = webClient.getPage(url);
                        //尝试加载上面图片例子给出的网页
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        webClient.close();
                    }
                    webClient.waitForBackgroundJavaScript(60000);
                    //异步JS执行需要耗时,所以这里线程要阻塞30秒,等待异步JS执行结束
                    String pageXml = page.asXml();
                    //直接将加载完成的页面转换成xml格式的字符串

//                    Document document = Jsoup.parse(pageXml);//获取html文档
//                    List<Element> infoListEle = document.getElementById("feedCardContent")
//                            .getElementsByAttributeValue("class", "feed-card-item");//获取元素节点等
//                    infoListEle.forEach(element -> {
//                        System.out.println(element.getElementsByTag("h2").first().getElementsByTag("a").text());
//                        System.out.println(element.getElementsByTag("h2").first().getElementsByTag("a").attr("href"));
//                    });

               // }
           // });

        }
    }

}