package com.xiaopeng.waterarmy.handle.Util;

import com.baidu.aip.ocr.AipOcr;
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
