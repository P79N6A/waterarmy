package com.xiaopeng.waterarmy;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
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
            requestContext.setUserId(1L);
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
            content.setText("今年最好看质量最好的国产SUV是啥");
            content.setTitle("今年最好看质量最好的国产SUV是啥？");
            requestContext.setContent(content);
            requestContext.setUserId(21L);
            requestContext.setUserLoginId("15246093860");
            requestContext.setHandleType(TaskTypeEnum.POSIED);
            requestContext.setPlatform(PlatformEnum.XCAR);
            requestContext.setPrefixUrl("https://club.autohome.com.cn/bbs/forum-o-200325-1.html");
            requestContext.setHandleEntryType(TaskEntryTypeEnum.TAIPINGYANGCHEZHUCOMMENT);
            HashMap<String, String> map = new HashMap<>();
            requestContext.setRequestParam(map);
            autoHomeHandler.publish(requestContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}