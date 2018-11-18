package com.xiaopeng.waterarmy.common.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaopeng.waterarmy.common.enums.CharsetEnum;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengwenlong on 18/11/18.
 */
public class ZhiMaProxyIpUtil {

    private static Logger logger = LoggerFactory.getLogger(ZhiMaProxyIpUtil.class);

    private static String zhimaGetIpUrl
            = "http://webapi.http.zhimacangku.com/getip?num=50&type=2&pro=" +
            "&city=0&yys=0&port=1&time=1&ts=0&ys=0&cs=1&lb=1&sb=0&pb=4&mr=1&regions=";

    private List<ProxyHttpConfig> zhimaProxyIps = new ArrayList<>();

    private void initZhimaProxyIps() {
        if (ObjectUtils.isEmpty(zhimaProxyIps) || !getRequest(zhimaProxyIps.get(0).getReqConfig())) {
            zhimaProxyIps.clear();
            try {
                HttpUtil.HttpResult result = HttpUtil.getByParams(zhimaGetIpUrl, null, CharsetEnum.UTF_8);
                JSONObject jsonObject = JSONObject.parseObject(result.getMessage());

                if (!ObjectUtils.isEmpty(jsonObject)) {
                    JSONArray array = jsonObject.getJSONArray("data");
                    for(int i=0;i<array.size();i++){
                        JSONObject job = array.getJSONObject(i);
                        ProxyHttpConfig config = new ProxyHttpConfig();
                        config.setProxyHost(job.getString("ip"));
                        config.setProxyPort(job.getInteger("port"));
                        HttpHost proxy = new HttpHost(config.getProxyHost(), config.getProxyPort(), "http");
                        RequestConfig reqConfig = RequestConfig.custom().setConnectionRequestTimeout(5000)
                                .setConnectTimeout(10000) // 设置连接超时时间
                                .setSocketTimeout(10000) // 设置读取超时时间
                                .setExpectContinueEnabled(false).setProxy(proxy)
                                .setCircularRedirectsAllowed(true) // 允许多次重定向
                                .build();
                        config.setReqConfig(reqConfig);
                        zhimaProxyIps.add(config);
                    }
                }
            } catch (Exception e) {
                logger.error("初始化芝麻ip列表失败,", e);
            }
        }
    }

    private boolean getRequest(RequestConfig reqConfig) {
        // 目标地址
        String targetUrl = "http://httpbin.org/get";
        try {
            HttpGet httpGet = new HttpGet(targetUrl);
            boolean isSucceed = doRequest(httpGet, reqConfig);
            if (isSucceed) {
                return true;
            }
        } catch (Exception e) {
            logger.error("获取失败,", e);
        }
        return false;
    }

    /**
     * 执行请求
     *
     * @param httpReq
     * @return
     */
    private boolean doRequest(HttpRequestBase httpReq, RequestConfig reqConfig) {
        httpReq.setConfig(reqConfig);
        CloseableHttpResponse httpResp = null;
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClients.createDefault();
            // 执行请求
            httpResp = httpClient.execute(httpReq);

            // 获取http code
            int statusCode = httpResp.getStatusLine().getStatusCode();
            System.out.println(statusCode);
            HttpEntity entity = httpResp.getEntity();
            return true;
        } catch (Exception e) {
            logger.error("测试芝麻ip是否失效失败,", e);
        } finally {
            if (!ObjectUtils.isEmpty(httpResp)) {
                try {
                    httpResp.close();
                } catch (IOException e) {
                    logger.error("关闭 httpResp失败,", e);
                }
            }
            if (!ObjectUtils.isEmpty(httpClient)) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    logger.error("关闭 httpClient失败,", e);
                }
            }
            if (!ObjectUtils.isEmpty(httpReq)) {
                httpReq.abort();
            }
        }
        return false;
    }

    class ProxyHttpConfig {
        private RequestConfig reqConfig;
        // 代理服务器
        private String proxyHost;
        private Integer proxyPort;
        private boolean isUsed;

        public RequestConfig getReqConfig() {
            return reqConfig;
        }

        public void setReqConfig(RequestConfig reqConfig) {
            this.reqConfig = reqConfig;
        }

        public String getProxyHost() {
            return proxyHost;
        }

        public void setProxyHost(String proxyHost) {
            this.proxyHost = proxyHost;
        }

        public Integer getProxyPort() {
            return proxyPort;
        }

        public void setProxyPort(Integer proxyPort) {
            this.proxyPort = proxyPort;
        }

        public boolean isUsed() {
            return isUsed;
        }

        public void setUsed(boolean used) {
            isUsed = used;
        }
    }

    public List<ProxyHttpConfig> getZhimaProxyIps() {
        return zhimaProxyIps;
    }

    public void setZhimaProxyIps(List<ProxyHttpConfig> zhimaProxyIps) {
        initZhimaProxyIps();
        this.zhimaProxyIps = zhimaProxyIps;
    }
}
