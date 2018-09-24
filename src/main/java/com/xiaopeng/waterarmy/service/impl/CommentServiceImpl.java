package com.xiaopeng.waterarmy.service.impl;

import com.xiaopeng.waterarmy.common.message.CodeEnum;
import com.xiaopeng.waterarmy.common.message.JsonMessage;
import com.xiaopeng.waterarmy.service.CommentService;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * * 功能描述：
 * <p> 版权所有：优视科技
 * <p> 未经本公司许可，不得以任何方式复制或使用本程序任何部分 <p>
 *
 * @author <a href="wenlong.cwl@alibaba-inc.com">成文龙</a>
 * @version 1.0.0
 * @since 1.0.0
 * create on: 2018/9/20
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Override
    public JsonMessage conmment(String userName, String passWord, String comment) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("https://passport3.pcauto.com.cn/passport3/passport/login.jsp");
            setHeader(httpPost);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("username", userName));//"18473837405"
            nameValuePairs.add(new BasicNameValuePair("password", passWord));//"rzvrxq71373"
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity, "utf-8");
            System.out.println(content);
            System.out.println(httpPost.getURI());
            System.out.println(response);

            //评论
            HttpPost httpPost1 = new HttpPost("https://bbs.pcauto.com.cn/action/post/create.ajax");
            List<NameValuePair> nameValuePairs1 = new ArrayList<NameValuePair>();
            nameValuePairs1.add(new BasicNameValuePair("wysiwyg", "1"));
            nameValuePairs1.add(new BasicNameValuePair("fid", "17965"));
            nameValuePairs1.add(new BasicNameValuePair("topicTitleMaxLength", "40"));
            nameValuePairs1.add(new BasicNameValuePair("topicContentMinLength", "1"));
            nameValuePairs1.add(new BasicNameValuePair("topicContentMaxLength", "500000"));
            nameValuePairs1.add(new BasicNameValuePair("uploadKeepSource", "false"));
            nameValuePairs1.add(new BasicNameValuePair("uploadMaxNumPerTime", "9999"));
            nameValuePairs1.add(new BasicNameValuePair("checkCategory", "0"));
            nameValuePairs1.add(new BasicNameValuePair("tid", "17338209"));
            nameValuePairs1.add(new BasicNameValuePair("category", "综合"));
            nameValuePairs1.add(new BasicNameValuePair("message"
                    , "[size=4]" + comment+ "[/size]"));//买车一个月，用起来真心不错，比想象的好很多
            nameValuePairs1.add(new BasicNameValuePair("upload2Album", "2982656"));
            nameValuePairs1.add(new BasicNameValuePair("albumId", "2982656"));
            nameValuePairs1.add(new BasicNameValuePair("sendMsg", "true"));
            httpPost1.setEntity(new UrlEncodedFormEntity(nameValuePairs1, "UTF-8"));
            CloseableHttpResponse response1 = httpClient.execute(httpPost1);
            HttpEntity entity1 = response1.getEntity();
            String content1 = EntityUtils.toString(entity1, "utf-8");
            System.out.println(content1);

            httpClient.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return JsonMessage.init().success(CodeEnum.SUCCESS);
    }

    private void setHeader(HttpPost httpPost) {
        httpPost.setHeader("referer", "https://passport3.pcauto.com.cn/passport3/passport/login2.jsp?status=1&return=http%3A%2F%2Fwww.pcauto.com.cn%2F&username=");
        httpPost.setHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.92 Safari/537.36");
        httpPost.setHeader("cookie", "u4ad=704s04971; UM_distinctid=163e80d0cdd3b0-01f26030b5adac-33697706-1fa400-163e80d0cde3a1; canWebp=1; pcsuv=1528606560209.ax.724221897; pcLocate=%7B%22proCode%22%3A%22330000%22%2C%22pro%22%3A%22%E6%B5%99%E6%B1%9F%E7%9C%81%22%2C%22cityCode%22%3A%22330100%22%2C%22city%22%3A%22%E6%9D%AD%E5%B7%9E%E5%B8%82%22%2C%22dataType%22%3A%22ipJson%22%2C%22expires%22%3A1529902563439%7D; pcautoLocate=%7B%22proId%22%3A19%2C%22cityId%22%3A217%2C%22url%22%3A%22http%3A%2F%2Fwww.pcauto.com.cn%2Fqcbj%2Fhz%2F%22%2C%22dataTypeAuto%22%3A%22region_ipArea%22%7D; cmu=icmvvr8061; common_session_id=52BD04F0F04CED50BBA1200DB53E9ADDBB32A0159A2EB408441EC2600085D7A22399AFE068FCFA23; pcuvdata=lastAccessTime=1537062590629|visits=5; channel=115; __PCautoMsg=%7B%22systemNoticeCount%22%3A%222%22%2C%22messageCount%22%3A%220%22%2C%22forumReplyCount%22%3A%220%22%2C%22dynaPraise%22%3A%220%22%2C%22commentReplyCount%22%3A%220%22%2C%22carReplyCount%22%3A%220%22%2C%22autoClubTaskCount%22%3A%221%22%2C%22lastUpdated%22%3A%221537081243975%22%7D");
        httpPost.setHeader("origin", "https://passport3.pcauto.com.cn");
    }


}
