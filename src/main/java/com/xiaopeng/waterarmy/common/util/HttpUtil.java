package com.xiaopeng.waterarmy.common.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.xiaopeng.waterarmy.common.enums.CharsetEnum;
import org.apache.http.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

/**
 * 功能描述： Http请求通用类
 *
 * @author <a href="laowen.zjw@alibaba-inc.com">郑健文</a>
 * @version 1.0.0
 * @since 1.0.0 create on: 2017/3/13 9:48
 */
public class HttpUtil {

	static Logger logger = Logger.getLogger(HttpUtil.class);
	/**
	 * 默认建立连接超时时间
	 */
	public static final int DEFAULT_CONNECTION_TIMEOUT = 1500; 
	/**
	 * 默认读取应答超时时间
	 */
	public static final int DEFAULT_READ_TIMEOUT = 3000;
	
	/**
	 * 获取可关闭的HttpClient
	 * @author UC
	 * @return
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 */
	private static CloseableHttpClient getHttpClient() throws Exception {
		// CloseableHttpClient进行http请求的例子，但是就这几行代码已经内置对Cookie和SSL的处理。
		// 谈不上很安全，但至少对使用Cookie对Https的URL是可以处理取回内容的。
		X509TrustManager x509mgr = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] xcs, String string) {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] xcs, String string) {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		SSLContext sslContext = null;
		sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, new TrustManager[] { x509mgr }, null);
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
				SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		return HttpClients.custom().setSSLSocketFactory(sslsf).build();
	}

	// ------------------------------------------------------------------------

	/**
	 *  @author UC
	 *  
	 *  request配置超时时间
	 *
	 * @param connectionTimeout
	 *            连接超时时间 ms
	 * @param readTimeout
	 *            读取超时时间 ms
	 * @return
	 */
	private static RequestConfig getRequestConfig(int connectionTimeout, int readTimeout) {
		int tConnectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
		int tReadTimeout = DEFAULT_READ_TIMEOUT;

		if (connectionTimeout > 0) {
			tConnectionTimeout = connectionTimeout;
		}
		if (readTimeout > 0) {
			tReadTimeout = readTimeout;
		}

		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(tConnectionTimeout)
				.setSocketTimeout(tReadTimeout).build();
		return requestConfig;
	}

	/**
	 * 设置config
	 */
	private static void setRequestConfig(HttpRequestBase request, RequestConfig config) {
		request.setConfig(config);
	}

	// ------------------------------------------------------------------------

	/**
	 * @author UC
	 * 设置请求头header
	 */
	private static void setRequestHeader(HttpRequestBase request, Map<String, String> header) {
		if (CollectionUtil.isNotEmpty(header)) {
			Iterator<Map.Entry<String, String>> it = header.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> entry = it.next();
				request.setHeader(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * @author UC
	 * 设置requestParams
	 */
	private static void setRequestParams(HttpPost post, Map<String, String> requestParams, CharsetEnum charset)
			throws Exception {
		if (CollectionUtil.isNotEmpty(requestParams) && charset != null) {
			List<NameValuePair> tList = new ArrayList<NameValuePair>();

			Set<String> tKeySet = requestParams.keySet();
			for (String key : tKeySet) {
				BasicNameValuePair tPair = new BasicNameValuePair(key, requestParams.get(key));
				tList.add(tPair);
			}

			post.setEntity(new UrlEncodedFormEntity(tList, charset.getValue()));
		}
	}

	/**
	 * @author UC
	 * 设置requestBody
	 */
	private static void setRequestBody(HttpPost post, String requestBody, CharsetEnum charset) throws Exception {
		if (StringUtils.isNotEmpty(requestBody)) {
			StringEntity stringEntity = new StringEntity(requestBody, charset.getValue());
			post.setEntity(stringEntity);
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * @author UC
	 * 
	 * 从输入流获取String内容
	 *
	 * @param input
	 *            响应的输入流
	 * @param charset
	 *            字符集
	 * @return
	 * @throws IOException
	 */
	private static String getStringFromInputStream(InputStream input, CharsetEnum charset) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		int length = 1024;
		byte[] data = new byte[length];
		int count = -1;
		while ((count = input.read(data, 0, length)) != -1) {
			outStream.write(data, 0, count);
		}
		return new String(outStream.toByteArray(), charset.getValue());
	}

	/**
	 * 通用请求
	 *
	 * @param response
	 * @return
	 * @throws IOException
	 */
	private static HttpResult getResult(CloseableHttpResponse response, CharsetEnum charset) throws Exception {
		// 获取status
		int status = response.getStatusLine().getStatusCode();
		// 从应答实体中获取content
		HttpEntity entity = response.getEntity();
		String content = null;
		if (entity != null) {
			content = getStringFromInputStream(entity.getContent(), charset);
		}

		HttpResult result = new HttpResult();
		result.setStatus(status);
		result.setMessage(content);

		return result;
	}

	/**
	 * 关闭http连接
	 */
	private static void close(Closeable client) {
		if (client != null) {
			try {
				client.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// ------------------------------------------------------------------------------------------------------------------

	/**
	 * get请求，参数以header的形式传递
	 */
	public static HttpResult getByHeader(String url, Map<String, String> header, CharsetEnum charset) throws Exception {
		return get(url, header, null, charset);
	}

	/**
	 * get请求，参数以requestParams的形式传递
	 */
	public static HttpResult getByParams(String url, Map<String, String> requestParams, CharsetEnum charset)
			throws Exception {
		return get(url, null, requestParams, charset);
	}

	/**
	 * get请求，参数包括header和requestParams
	 */
	public static HttpResult get(String url, Map<String, String> header, Map<String, String> requestParams,
			CharsetEnum charset) throws Exception {
		return get(url, header, requestParams, charset, 100000, 100000);
	}

	/**
	 * GET 请求
	 *
	 * @param url
	 *            访问路径
	 * @param header
	 *            请求头
	 * @param requestParams
	 *            请求参数
	 * @param charset
	 *            字符集
	 * @param connectionTimeout
	 *            连接超时ms
	 * @param readTimeout
	 *            读取超时ms
	 * @return
	 * @throws Exception
	 */
	public static HttpResult get(String url, Map<String, String> header, Map<String, String> requestParams,
			CharsetEnum charset, int connectionTimeout, int readTimeout) throws Exception {
		if (Strings.isNullOrEmpty(url)) {
			throw new Exception("get method param url miss");
		}
		if (charset == null) {
			throw new Exception("get method param charset miss");
		}

		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;

		try {
			client = getHttpClient();

			// ////////////////////////////////get区域//////////////////////////////////////////////////////////
			// 将请求参数的集合转换为url的参数 默认
			String requestUrl = url; 
			if (CollectionUtil.isNotEmpty(requestParams)) {
				String tParamStr = Joiner.on("&").withKeyValueSeparator("=").join(requestParams);
				requestUrl = url + "?" + tParamStr;
			}

			HttpGet get = new HttpGet(requestUrl);
			// 设置config
			setRequestConfig(get, getRequestConfig(connectionTimeout, readTimeout)); 
			// 设置header
			setRequestHeader(get, header); 
			// ////////////////////////////////get区域//////////////////////////////////////////////////////////

			response = client.execute(get);
			HttpResult result = getResult(response, charset);

			return result;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			close(response);
			close(client);
		}
	}

	// ------------------------------------------------------------------------------------------------------------------

	/**
	 * post请求，参数以header的形式传递
	 */
	public static HttpResult postByHeader(String url, Map<String, String> header, CharsetEnum charset) throws Exception {
		return post(url, header, null, null, charset);
	}

	/**
	 * post请求，参数以requestParams的形式传递
	 */
	public static HttpResult postByParams(String url, Map<String, String> requestParams, CharsetEnum charset)
			throws Exception {
		return post(url, null, requestParams, null, charset);
	}

	/**
	 * post请求，参数以requestBody的形式传递
	 */
	public static HttpResult postByBody(String url, String requestBody, CharsetEnum charset) throws Exception {
		return post(url, null, null, requestBody, charset);
	}

	/**
	 * POST请求，参数包括header和requestParams
	 */
	public static HttpResult post(String url, Map<String, String> header, Map<String, String> requestParams,
			String requestBody, CharsetEnum charset) throws Exception {
		return post(url, header, requestParams, requestBody, charset, 180000, 180000);
	}

	/**
	 * POST 请求
	 *
	 * @param url
	 *            请求地址
	 * @param header
	 *            请求头
	 * @param requestParams
	 *            请求参数
	 * @param requestBody
	 *            请求体
	 * @param charset
	 *            字符集
	 * @param connectionTimeout
	 *            连接超时ms
	 * @param readTimeout
	 *            读取超时ms
	 * @return
	 * @throws Exception
	 */
	public static HttpResult post(String url, Map<String, String> header, Map<String, String> requestParams,
			String requestBody, CharsetEnum charset, int connectionTimeout, int readTimeout) throws Exception {
		if (Strings.isNullOrEmpty(url)) {
			throw new Exception("post method param url miss");
		}
		if (charset == null) {
			throw new Exception("post method param charset miss");
		}
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;

		try {
			client = getHttpClient();

			// ////////////////////////////////post区域//////////////////////////////////////////////////////////
			HttpPost post = new HttpPost(url);
			// 设置config
			setRequestConfig(post, getRequestConfig(connectionTimeout, readTimeout)); 
			// 设置header
			setRequestHeader(post, header); 
			// 设置请求参数
			setRequestParams(post, requestParams, charset); 
			// 设置requestBody
			setRequestBody(post, requestBody, charset); 
			// ////////////////////////////////post区域//////////////////////////////////////////////////////////
			// 执行
			response = client.execute(post); 
			HttpResult result = getResult(response, charset);
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		} finally {
			close(response);
			close(client);
		}
	}

	// -------------------------------------------------------------------------------------------------------------------------

	/**
	 * 保存Http响应的信息POJO，记录status和message</br>
	 */
	public static class HttpResult {
		/**
		 * Http响应状态码
		 */
		private int status;
		/**
		 * Http响应信息
		 */
		private String message;

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		@Override
		public String toString() {
			return "HttpResult [status=" + status + ", message=" + message + "]";
		}
	}

	// -------------------------------------------------------------------------------------------------------------------------

	/**
	 * 根据HttpResult的状态判断，如果不为200则抛异常</br> 注：</br> ①要考虑302的情况</br>
	 *
	 * @param httpResult
	 * @throws DataAccessException
	 */
	public static void throwException(HttpResult httpResult) throws Exception {
		if (httpResult == null) {
			return;
		}

		if (httpResult.getStatus() != HttpStatus.SC_OK) {
			throw new Exception("http response:[statue=" + httpResult.getStatus() + ", entity="
					+ httpResult.getMessage() + "]");
		}
	}
	
	/**
	 * 返回请求状态码 200表示成功，400表示失败
	 * 
	 * @param urlStr
	 * @return
	 * @throws Exception
	 */
	public static int doGetRespondCode(String urlStr) {
		int code=404;
		URL url = null;
		HttpURLConnection conn = null;
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(DEFAULT_READ_TIMEOUT);
			conn.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			code= conn.getResponseCode();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
			}
			try {
				if (baos != null)
					baos.close();
			} catch (IOException e) {
			}
			conn.disconnect();
		}

		return code;

	}

}