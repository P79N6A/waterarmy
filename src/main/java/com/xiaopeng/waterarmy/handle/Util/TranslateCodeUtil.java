package com.xiaopeng.waterarmy.handle.Util;

import com.baidu.aip.ocr.AipOcr;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class TranslateCodeUtil {

    private static Logger logger = LoggerFactory.getLogger(TranslateCodeUtil.class);

    private static final String BAIDU_APP_ID = "14336179";

    private static final String BAIDU_API_KEY = "RdBvKmGcvlHgu5MyaFe8mVoZ";

    private static final String BAIDU_SECREET_KEY = "tKce0g1lTp28pbwSKAnsqqUOeXfkO9ax";


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
            JSONObject res = aipOcr.basicGeneral(bos.toByteArray(),new HashMap<>());
            JSONArray arr = res.getJSONArray("words_result");
            JSONObject object = (JSONObject) arr.get(0);
            String words = (String) object.get("words");
            return words;
        }catch (Exception e) {
            logger.error("YiCheLoginHandler.convert",e);
        }
        return null;
    }

    public InputStream fetchFileFromUrl(String path) {
        URL url = null;
        InputStream is = null;
        try {
            url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            is = connection.getInputStream();
        }catch (Exception e) {
            logger.error("fetchFileFromUrl",e);
        }
        return is;
    }

}
