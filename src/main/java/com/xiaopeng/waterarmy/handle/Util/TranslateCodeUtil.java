package com.xiaopeng.waterarmy.handle.Util;

import com.baidu.aip.ocr.AipOcr;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslateCodeUtil {

    private static Logger logger = LoggerFactory.getLogger(TranslateCodeUtil.class);

    private static final String BAIDU_APP_ID = "14336197";

    private static final String BAIDU_API_KEY = "TTdtbnfd7hpfOE8Ee9ypiGeO";

    private static final String BAIDU_SECREET_KEY = "Ykh2GrcrEqZUCeg4YXNrvtoxvptdgie7";

    public static final String UserAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";

    private static TranslateCodeUtil instance;

    private static  AipOcr aipOcr;

    public static TranslateCodeUtil getInstance() {
        if (instance == null) {
            instance = new TranslateCodeUtil();
            aipOcr = new AipOcr(BAIDU_APP_ID,BAIDU_API_KEY,BAIDU_SECREET_KEY);
        }
        return instance;
    }

    public String convert(String url) {
        try {
            InputStream inputStream = fetchFileFromUrl(url);
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage,"PNG",bos);
            //BAIDU转换
            JSONObject res = aipOcr.basicGeneral(bos.toByteArray(),new HashMap<String, String>());
            JSONArray arr = res.getJSONArray("words_result");
            JSONObject object = (JSONObject) arr.get(0);
            String words = (String) object.get("words");
            return words;
        }catch (Exception e) {
            logger.error("TranslateCodeUtil.convert",e);
        }
        return null;
    }

    public String convertWithRegx(String url,String regx) {
        try {
            InputStream inputStream = fetchFileFromUrl(url);
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage,"PNG",bos);
            //BAIDU转换
            JSONObject res = aipOcr.basicGeneral(bos.toByteArray(),new HashMap<String, String>());
            JSONArray arr = res.getJSONArray("words_result");
            JSONObject object = (JSONObject) arr.get(0);
            String words = (String) object.get("words");
            Pattern pattern = Pattern.compile(regx);
            Matcher matcher = pattern.matcher(words);
            StringBuilder stringBuilder =  new StringBuilder();
            while (matcher.find()) {
                stringBuilder.append(matcher.group());
            }
            return stringBuilder.toString();
        }catch (Exception e) {
            logger.error("TranslateCodeUtil.convert",e);
        }
        return null;
    }


    public Util.HttpResp convertByFeiFei(String url,String pre_type) {
        try {
            InputStream inputStream = fetchFileFromUrl(url);
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage,"PNG",bos);
            ImageIO.write(bufferedImage,"PNG",new FileOutputStream("my.png"));
            FeiFeiApi feiFeiApi = FeiFeiApi.getInstance();
            Util.HttpResp resp = feiFeiApi.Predict(pre_type,bos.toByteArray());
            return resp;
        }catch (Exception e) {
            logger.error("[TranslateCodeUtil.convertByFeiFei] error!",e);
            return null;
        }
    }

    public void refund(String req_id) {
        // 识别的结果如果与预期不符，可以调用这个接口将预期不符的订单退款
        // 退款仅在正常识别出结果后，无法通过网站验证的情况，请勿非法或者滥用，否则可能进行封号处理
        try {
            int ret =  FeiFeiApi.getInstance().JusticeExtend(req_id); // 直接返回是否成功
            if (ret == 0 ) {
                logger.info("refund success, req_id is",req_id);
            }
        }catch (Exception e) {
            logger.error("[TranslateCodeUtil.refund] error!",e);
        }
    }

    public InputStream fetchFileFromUrl(String path) {
        URL url = null;
        InputStream is = null;
        try {
            url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", UserAgent);
            connection.setDoInput(true);
            connection.connect();
            is = connection.getInputStream();
        }catch (Exception e) {
            logger.error("fetchFileFromUrl",e);
        }
        return is;
    }


}
