package com.xiaopeng.waterarmy;

import com.sun.deploy.net.HttpUtils;
import com.xiaopeng.waterarmy.common.constants.RequestConsts;
import com.xiaopeng.waterarmy.common.enums.PlatformEnum;
import com.xiaopeng.waterarmy.common.enums.TaskEntryTypeEnum;
import com.xiaopeng.waterarmy.common.enums.TaskTypeEnum;
import com.xiaopeng.waterarmy.common.util.ExcelUtil;
import com.xiaopeng.waterarmy.handle.HandlerDispatcher;
import com.xiaopeng.waterarmy.handle.impl.AiKaHandler;
import com.xiaopeng.waterarmy.handle.impl.AutoHomeHandler;
import com.xiaopeng.waterarmy.handle.impl.TaiPingYangHandler;
import com.xiaopeng.waterarmy.handle.impl.YiCheHandler;
import com.xiaopeng.waterarmy.handle.param.Content;
import com.xiaopeng.waterarmy.handle.param.RequestContext;
import com.xiaopeng.waterarmy.model.dao.LinkInfo;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WaterarmyApplicationTests {

    public WaterarmyApplicationTests() {
    }

    @Test
    public void test2() {
        int max = 1;
        int min = 0;
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        System.out.println(s);
    }


    @Test
    public void test1() {
        String line = "11111111111/xiaopengqicheg3/2222222222";
        String pattern = "\\/.*\\/";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(line);
        List<String> results = new ArrayList<>();
        if (m.find()) {
            String s = m.group(0);
            results.add(s.substring(1, s.length() - 2));
        }
        for (String result : results) {
            System.out.println(result);
        }
    }

    @Test
    public void contextLoads() {
        System.out.println("去请求");
        File file = new File("E:/水军系统研发/link.xls");
        importData(file, LinkInfo.class);
    }

    /**
     * 导入excel数据
     *
     * @param file
     * @return
     */
    public static List<Object> importData(File file, Object object) {
        System.out.println("~~~~~~~~~~" + (object instanceof LinkInfo));
        Workbook wb = null;
        List<Object> datas = null;
        try {
            if (ExcelUtil.isExcel2007(file.getPath())) {
                wb = new XSSFWorkbook(new FileInputStream(file));
            } else {
                wb = new HSSFWorkbook(new FileInputStream(file));
            }
        } catch (IOException e) {
            //logger.error("importData error, ", e);
            return null;
        }
        Sheet sheet = wb.getSheetAt(0);//获取第一张表
        datas = getDatasBySheet(sheet, object);
        try {
            if (ObjectUtils.isEmpty(wb)) {
                wb.close();
            }
        } catch (IOException e) {
            //logger.error("importData close wb error, ", e);
        }
        return datas;
    }

    private static List<Object> getDatasBySheet(Sheet sheet, Object object) {
        List<Object> datas = new ArrayList<>();
        for (int i = 1; i < sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);//获取索引为i的行，以1开始
            if (object instanceof LinkInfo) {
                LinkInfo info = new LinkInfo();
                String link = row.getCell(0).getStringCellValue();//获取第i行的索引为0的单元格数据
                if (ObjectUtils.isEmpty(link)) {
                    break;
                }
                info.setLink(link);
                datas.add(info);
            }
        }
        return datas;
    }

    @Autowired
    private YiCheHandler yiCheHandler;

    @Autowired
    private TaiPingYangHandler taiPingYangHandler;

    @Autowired
    private AiKaHandler aiKaHandler;

    @Autowired
    private AutoHomeHandler autoHomeHandler;


    @Test
    public void testPublist() {
        RequestContext requestContext = new RequestContext();
        Content content = new Content();
        content.setText("这个车真的很帅");
        content.setTitle("改装");
        requestContext.setContent(content);
        requestContext.setUserId(3L);
        requestContext.setUserLoginId("15164577148");
        requestContext.setHandleType(TaskTypeEnum.POSIED);
        requestContext.setPlatform(PlatformEnum.YICHE);
        //requestContext.setHandleEntryType(TaskEntryTypeEnum.YICHECOMMENTPRAISE);
        //requestContext.setHandleEntryType(TaskEntryTypeEnum.YICHENEWSCOMMENT);
        //requestContext.setPrefixUrl("http://www.xcar.com.cn/bbs/forumdisplay.php?fid=1745");
        requestContext.setPrefixUrl("http://baa.bitauto.com/changancs35/");
        //requestContext.setPrefixUrl("http://www.xcar.com.cn/bbs/viewthread.php?tid=34241863");
        //requestContext.setPrefixUrl("http://news.bitauto.com/hao/wenzhang/963762");
       /* HashMap map = new HashMap();
        map.put(RequestConsts.COMMENT_ID,"257971069489512448");
        map.put(RequestConsts.COMMENT_CONTENT,"666");
        requestContext.setRequestParam(map);https://cmt.pcauto.com.cn/action/comment/create.jsp?urlHandle=1*/
        yiCheHandler.publish(requestContext);
    }


    @Test
    public void testTaiPinYangPublish() {
        try {
            RequestContext requestContext = new RequestContext();
            Content content = new Content();
            content.setText("明年再买吧，有点贵");
            content.setTitle("改装");
            requestContext.setContent(content);
            requestContext.setUserId(7L);
            requestContext.setUserLoginId("18482193356");
            requestContext.setHandleType(TaskTypeEnum.COMMENT);
            requestContext.setPlatform(PlatformEnum.PCAUTO);
            requestContext.setHandleEntryType(TaskEntryTypeEnum.TAIPINGYANGCHEZHUCOMMENT);
            requestContext.setPrefixUrl("https://bbs.pcauto.com.cn/forum-20095.html");
            HashMap<String, String> map = new HashMap<>();
            map.put(RequestConsts.COMMENT_ID, "32202092");
            map.put(RequestConsts.COMMENT_CONTENT, "666");
            requestContext.setRequestParam(map);
            taiPingYangHandler.publish(requestContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testXCARPublish() {
        try {
            RequestContext requestContext = new RequestContext();
            Content content = new Content();
            content.setText("明年再买吧，有点贵");
            content.setTitle("改装");
            requestContext.setContent(content);
            requestContext.setUserId(25L);
            requestContext.setUserLoginId("18927512986");
            requestContext.setHandleType(TaskTypeEnum.POSIED);
            requestContext.setPlatform(PlatformEnum.XCAR);
            requestContext.setPrefixUrl("http://www.xcar.com.cn/bbs/forumdisplay.php?fid=1604");
            requestContext.setHandleEntryType(TaskEntryTypeEnum.TAIPINGYANGCHEZHUCOMMENT);
            HashMap<String, String> map = new HashMap<>();
            requestContext.setRequestParam(map);
            aiKaHandler.publish(requestContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testQiCheZhiJiaPublish() {
        try {
            RequestContext requestContext = new RequestContext();
            Content content = new Content();
            content.setText("买车是买鸡头好还是凤尾好？");
            content.setTitle("比如低配版本的奥迪A3和高配版雅阁");
            requestContext.setContent(content);
            requestContext.setUserId(23L);
            requestContext.setUserLoginId("18992572253");
            requestContext.setHandleType(TaskTypeEnum.POSIED);
            requestContext.setPlatform(PlatformEnum.AUTOHOME);
            requestContext.setPrefixUrl("https://club.autohome.com.cn/bbs/forum-o-200325-1.html");
            requestContext.setHandleEntryType(TaskEntryTypeEnum.TAIPINGYANGCHEZHUCOMMENT);
            HashMap<String, String> map = new HashMap<>();
            requestContext.setRequestParam(map);
            autoHomeHandler.publish(requestContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testQiCheZhiJiaComment() {
        try {
            RequestContext requestContext = new RequestContext();
            Content content = new Content();
            content.setText("拒绝灌水，从我做起,哈哈哈哈");
            content.setTitle("想把咱家的车拿去改装下，有什么好的推荐");
            requestContext.setContent(content);
            requestContext.setUserId(23L);
            requestContext.setUserLoginId("18992572253");
            requestContext.setHandleType(TaskTypeEnum.COMMENT);
            requestContext.setPlatform(PlatformEnum.AUTOHOME);
            requestContext.setPrefixUrl("https://club.autohome.com.cn/bbs/thread/5c44fd06bfc6f993/77286829-1.html");
            HashMap<String, String> map = new HashMap<>();
            map.put("commentContent", "确实是一辆好车，我都想买很久了");
            requestContext.setRequestParam(map);
            autoHomeHandler.comment(requestContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 汽车之家新闻评论点赞
     */
    @Test
    public void testQiCheZhiJiaNewsCommentPraise() {
        try {
            String url = "https://www.autohome.com.cn/news/201811/924545.html#pvareaid=102624";
            String article = url.substring(url.lastIndexOf("/") + 1);
            String articleId = article.substring(0, article.indexOf("."));
                    RequestContext requestContext = new RequestContext();
            Content content = new Content();
            content.setText("文字精彩");
            requestContext.setContent(content);
            requestContext.setUserId(1L);
            requestContext.setUserLoginId("18927512986");
            requestContext.setHandleType(TaskTypeEnum.COMMENT);
            requestContext.setPlatform(PlatformEnum.AUTOHOME);
            requestContext.setPrefixUrl(url);
            requestContext.setHandleEntryType(TaskEntryTypeEnum.AUTOHOMENEWSCOMMENTPRAISE);
            Map requestParam = new HashMap<>();
            requestParam.put("articleId", articleId);
            requestContext.setRequestParam(requestParam);
            autoHomeHandler.praise(requestContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 汽车之家新闻评论
     */
    @Test
    public void testQiCheZhiJiaNewsComment() {
        try {
            String url = "https://www.autohome.com.cn/news/201811/924429.html#pvareaid=3311495";
            String obj = url.substring(url.lastIndexOf("/") + 1);
            String objid = obj.substring(0, obj.indexOf("."));
            RequestContext requestContext = new RequestContext();
            Content content = new Content();
            content.setText("沃尔沃豪车啊 ~~~~");
            requestContext.setContent(content);
            requestContext.setUserId(1L);
            requestContext.setUserLoginId("18927512986");
            requestContext.setHandleType(TaskTypeEnum.COMMENT);
            requestContext.setPlatform(PlatformEnum.AUTOHOME);
            requestContext.setPrefixUrl(url);
            requestContext.setHandleEntryType(TaskEntryTypeEnum.AUTOHOMENEWSCOMMENT);
            Map requestParam = new HashMap<>();
            requestParam.put("objid", objid);
            requestContext.setRequestParam(requestParam);
            autoHomeHandler.commentNews(requestContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 汽车之家新闻评论
     */
    @Test
    public void testAikaCommentPraise() {
        try {
            String url = "http://info.xcar.com.cn/201810/news_2027037_1.html";
            RequestContext requestContext = new RequestContext();
            Content content = new Content();
            content.setText("探岳330TSI两驱豪华型请问都是什么配置倒车影像这个级别有了吗");
            requestContext.setContent(content);
            requestContext.setUserId(4L);
            requestContext.setUserLoginId("18927512986");
            requestContext.setHandleType(TaskTypeEnum.LIKE);
            requestContext.setPlatform(PlatformEnum.XCAR);
            requestContext.setPrefixUrl(url);
            aiKaHandler.praise(requestContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}