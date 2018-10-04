package com.xiaopeng.waterarmy.handle.Util;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;

@Component
public class HttpFactory {

    public CloseableHttpClient getHttpClient() {
        return HttpClients.createDefault();
    }
}
