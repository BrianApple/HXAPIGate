package IOTGateConsole.controller.apigate;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import IOTGateConsole.controller.apigate.HttpClientResult;

/**
 * Description: httpClient工具类
 * 
 * @author JourWon
 * @date Created on 2018年4月19日
 */
public class HttpClientUtil {

	// 编码格式。发送编码格式统一用UTF-8
	private static final String ENCODING = "UTF-8";
	
	// 设置连接超时时间，单位毫秒。
	private static final int CONNECT_TIMEOUT = 6000;
	
	// 请求获取数据的超时时间(即响应时间)，单位毫秒。
	private static final int SOCKET_TIMEOUT = 6000;

	/**
	 * 发送get请求；不带请求头和请求参数
	 * 
	 * @param url 请求地址
	 * @return
	 * @throws Exception
	 */
	public static HttpClientResult doGet(String url) throws Exception {
		return doGet(url, null, null);
	}
	
	/**
	 * 发送get请求；带请求参数
	 * 
	 * @param url 请求地址
	 * @param params 请求参数集合
	 * @return
	 * @throws Exception
	 */
	public static HttpClientResult doGet(String url, Map<String, String> params) throws Exception {
		return doGet(url, null, params);
	}

	/**
	 * 发送get请求；带请求头和请求参数
	 * 
	 * @param url 请求地址
	 * @param headers 请求头集合
	 * @param params 请求参数集合
	 * @return
	 * @throws Exception
	 */
	public static HttpClientResult doGet(String url, Map<String, String> headers, Map<String, String> params) throws Exception {
		// 创建httpClient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();

		// 创建访问的地址
		URIBuilder uriBuilder = new URIBuilder(url);
		if (params != null) {
			Set<Entry<String, String>> entrySet = params.entrySet();
			for (Entry<String, String> entry : entrySet) {
				uriBuilder.setParameter(entry.getKey(), entry.getValue());
			}
		}

		// 创建http对象
		HttpGet httpGet = new HttpGet(uriBuilder.build());
		/**
		 * setConnectTimeout：设置连接超时时间，单位毫秒。
		 * setConnectionRequestTimeout：设置从connect Manager(连接池)获取Connection
		 * 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。
		 * setSocketTimeout：请求获取数据的超时时间(即响应时间)，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
		 */
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
		httpGet.setConfig(requestConfig);
		
		// 设置请求头
		packageHeader(headers, httpGet);

		// 创建httpResponse对象
		CloseableHttpResponse httpResponse = null;

		try {
			// 执行请求并获得响应结果
			return getHttpClientResult(httpResponse, httpClient, httpGet);
		} finally {
			// 释放资源
			release(httpResponse, httpClient);
		}
	}

	/**
	 * 发送post请求；不带请求头和请求参数
	 * 
	 * @param url 请求地址
	 * @return
	 * @throws Exception
	 */
	public static HttpClientResult doPost(String url) throws Exception {
		return doPost(url, null, null);
	}
	
	/**
	 * 发送post请求；带请求参数
	 * 
	 * @param url 请求地址
	 * @param params 参数集合
	 * @return
	 * @throws Exception
	 */
	public static HttpClientResult doPost(String url, Map<String, String> params) throws Exception {
		return doPost(url, null, params);
	}

	/**
	 * 以json格式访问服务端 并设置http请求超时时间
	 * @Title: doPostJson 
	 * @author yangcheng
	 * @param url
	 * @param token
	 * @param json
	 * @return
	 */
	public static HttpClientResult doPostJson(String url, String json,Integer timeOut) {
		RequestConfig defaultRequestConfig = RequestConfig.custom()
				  .setSocketTimeout(timeOut)
				  .setConnectTimeout(timeOut)
				  .setConnectionRequestTimeout(timeOut)
				  .build();
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		CloseableHttpResponse response = null;
		String result = null;
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			//Request不会继承客户端级别的请求配置，所以在自定义Request的时候，需要将客户端的默认配置拷贝过去：
			RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig)
				    .build();
			httpPost.setConfig(requestConfig);
			// 创建请求内容
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			entity.setContentType("application/json");
			entity.setContentEncoding("utf-8");
			httpPost.setEntity(entity);
			// 执行http请求
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			return new HttpClientResult(500);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(response == null){
					
				}else{
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return  new HttpClientResult(200,result);
	}
	/**
	 * 以json格式访问服务端 并设置http请求超时时间
	 * @Title: doPostJson 
	 * @author yangcheng
	 * @param url
	 * @param token
	 * @param json
	 * @return
	 */
	public static HttpClientResult doPostJson(String url,Map<String,String> headers ,String json,Integer timeOut) {
		RequestConfig defaultRequestConfig = RequestConfig.custom()
				.setSocketTimeout(timeOut)
				.setConnectTimeout(timeOut)
				.setConnectionRequestTimeout(timeOut)
				.build();
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		CloseableHttpResponse response = null;
		String result = null;
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			packageHeader(headers, httpPost);
			//Request不会继承客户端级别的请求配置，所以在自定义Request的时候，需要将客户端的默认配置拷贝过去：
			RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig)
					.build();
			httpPost.setConfig(requestConfig);
			// 创建请求内容
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			entity.setContentType("application/json");
			entity.setContentEncoding("utf-8");
			httpPost.setEntity(entity);
			// 执行http请求
			response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			return new HttpClientResult(500);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(response == null){
					
				}else{
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return  new HttpClientResult(200,result);
	}
	/**
	 * 发送post请求；带请求头和请求参数
	 * 
	 * @param url 请求地址
	 * @param headers 请求头集合
	 * @param params 请求参数集合
	 * @return
	 * @throws Exception
	 */
	public static HttpClientResult doPost(String url, Map<String, String> headers, Map<String, String> params) throws Exception {
		// 创建httpClient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();

		// 创建http对象
		HttpPost httpPost = new HttpPost(url);
		/**
		 * setConnectTimeout：设置连接超时时间，单位毫秒。
		 * setConnectionRequestTimeout：设置从connect Manager(连接池)获取Connection
		 * 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。
		 * setSocketTimeout：请求获取数据的超时时间(即响应时间)，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
		 */
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
		httpPost.setConfig(requestConfig);
		// 设置请求头
		/*httpPost.setHeader("Cookie", "");
		httpPost.setHeader("Connection", "keep-alive");
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
		httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
		httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");*/
		packageHeader(headers, httpPost);
		// 封装请求参数
		packageParam(params, httpPost);

		// 创建httpResponse对象
		CloseableHttpResponse httpResponse = null;

		try {
			// 执行请求并获得响应结果
			return getHttpClientResult(httpResponse, httpClient, httpPost);
		} finally {
			// 释放资源
			release(httpResponse, httpClient);
		}
	}

	/**
	 * 发送put请求；不带请求参数
	 * 
	 * @param url 请求地址
	 * @param params 参数集合
	 * @return
	 * @throws Exception
	 */
	public static HttpClientResult doPut(String url) throws Exception {
		return doPut(url);
	}

	/**
	 * 发送put请求；带请求参数
	 * 
	 * @param url 请求地址
	 * @param params 参数集合
	 * @return
	 * @throws Exception
	 */
	public static HttpClientResult doPut(String url, Map<String, String> params) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPut httpPut = new HttpPut(url);
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
		httpPut.setConfig(requestConfig);
		
		packageParam(params, httpPut);

		CloseableHttpResponse httpResponse = null;

		try {
			return getHttpClientResult(httpResponse, httpClient, httpPut);
		} finally {
			release(httpResponse, httpClient);
		}
	}
	/**
	 * 发送put请求；带请求参数和headers
	 * 
	 * @param url 请求地址
	 * @param params 参数集合
	 * @return
	 * @throws Exception
	 */
	public static HttpClientResult doPut(String url, Map<String, String> headers, Map<String, String> params) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPut httpPut = new HttpPut(url);
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
		httpPut.setConfig(requestConfig);
		packageHeader(headers, httpPut);
		packageParam(params, httpPut);
		
		CloseableHttpResponse httpResponse = null;
		
		try {
			return getHttpClientResult(httpResponse, httpClient, httpPut);
		} finally {
			release(httpResponse, httpClient);
		}
	}
	public static HttpClientResult doPutJson(String url, Map<String, String> headers, String jsonParam) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPut httpPut = new HttpPut(url);
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
		httpPut.setConfig(requestConfig);
		httpPut.setHeader("Content-Type","application/json; charset=UTF-8");
		
		packageHeader(headers, httpPut);
		
		StringEntity stringEntity=new StringEntity(jsonParam, "UTF-8");
		httpPut.setEntity(stringEntity);
		
		CloseableHttpResponse httpResponse = null;
		
		try {
			return getHttpClientResult(httpResponse, httpClient, httpPut);
		} finally {
			release(httpResponse, httpClient);
		}
	}
	/**
	 * 以json格式访问服务端 并设置http请求超时时间
	 * @Title: doPostJson 
	 * @author yangcheng
	 * @param url
	 * @param token
	 * @param json
	 * @return
	 */
	public static HttpClientResult doDeleteJson(String url, String json,Integer timeOut) {
		RequestConfig defaultRequestConfig = RequestConfig.custom()
				  .setSocketTimeout(timeOut)
				  .setConnectTimeout(timeOut)
				  .setConnectionRequestTimeout(timeOut)
				  .build();
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPut httpPut = new HttpPut(url);
			//Request不会继承客户端级别的请求配置，所以在自定义Request的时候，需要将客户端的默认配置拷贝过去：
			RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig)
				    .build();
			httpPut.setConfig(requestConfig);
			// 创建请求内容
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			entity.setContentType("application/json");
			entity.setContentEncoding("utf-8");
			httpPut.setEntity(entity);
			// 执行http请求
			response = httpClient.execute(httpPut);
			if (response.getStatusLine().getStatusCode() == 200) {
				resultString = EntityUtils.toString(response.getEntity(), "UTF-8");//处理返回结果  将返回结果直接转为String字符串
			}
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			return new HttpClientResult(500);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(response == null){
					
				}else{
					response.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return  new HttpClientResult(200,resultString);
	}

	/**
	 * 发送delete请求；不带请求参数
	 * 
	 * @param url 请求地址
	 * @param params 参数集合
	 * @return
	 * @throws Exception
	 */
	public static HttpClientResult doDelete(String url) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpDelete httpDelete = new HttpDelete(url);
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
		httpDelete.setConfig(requestConfig);

		CloseableHttpResponse httpResponse = null;
		try {
			return getHttpClientResult(httpResponse, httpClient, httpDelete);
		} finally {
			release(httpResponse, httpClient);
		}
	}

	/**
	 * 发送delete请求；带请求参数
	 * 
	 * @param url 请求地址
	 * @param params 参数集合
	 * @return
	 * @throws Exception
	 */
	public static HttpClientResult doDelete(String url, Map<String, String> headers, Map<String, String> params) throws Exception {
		if (params == null) {
			params = new HashMap<String, String>();
		}
		params.put("_method", "delete");
		return doPost(url,headers, params);
	}
	/**
	 * 发送delete请求；带请求参数
	 * 
	 * @param url 请求地址
	 * @param params 参数集合
	 * @return
	 * @throws Exception
	 */
	public static HttpClientResult doDelete(String url, Map<String, String> params) throws Exception {
		if (params == null) {
			params = new HashMap<String, String>();
		}
		
		params.put("_method", "delete");
		return doPost(url, params);
	}

	/**
	 * Description: 封装请求头
	 * @param params
	 * @param httpMethod
	 */
	public static void packageHeader(Map<String, String> params, HttpRequestBase httpMethod) {
		// 封装请求头
		if (params != null) {
			Set<Entry<String, String>> entrySet = params.entrySet();
			for (Entry<String, String> entry : entrySet) {
				// 设置到请求头到HttpRequestBase对象中
				httpMethod.setHeader(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * Description: 封装请求参数
	 * 
	 * @param params
	 * @param httpMethod
	 * @throws UnsupportedEncodingException
	 */
	public static void packageParam(Map<String, String> params, HttpEntityEnclosingRequestBase httpMethod)
			throws UnsupportedEncodingException {
		// 封装请求参数
		if (params != null) {
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			Set<Entry<String, String>> entrySet = params.entrySet();
			for (Entry<String, String> entry : entrySet) {
				nvps.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
			}

			// 设置到请求的http对象中
			httpMethod.setEntity(new UrlEncodedFormEntity(nvps, ENCODING));
		}
	}

	/**
	 * Description: 获得响应结果
	 * 
	 * @param httpResponse
	 * @param httpClient
	 * @param httpMethod
	 * @return
	 * @throws Exception
	 */
	public static HttpClientResult getHttpClientResult(CloseableHttpResponse httpResponse,
			CloseableHttpClient httpClient, HttpRequestBase httpMethod) throws Exception {
		// 执行请求
		httpResponse = httpClient.execute(httpMethod);

		// 获取返回结果
		if (httpResponse != null && httpResponse.getStatusLine() != null) {
			String content = "";
			if (httpResponse.getEntity() != null) {
				content = EntityUtils.toString(httpResponse.getEntity(), ENCODING);
			}
			return new HttpClientResult(httpResponse.getStatusLine().getStatusCode(), content);
		}
		return new HttpClientResult(HttpStatus.SC_INTERNAL_SERVER_ERROR);
	}

	/**
	 * Description: 释放资源
	 * 
	 * @param httpResponse
	 * @param httpClient
	 * @throws IOException
	 */
	public static void release(CloseableHttpResponse httpResponse, CloseableHttpClient httpClient) throws IOException {
		// 释放资源
		if (httpResponse != null) {
			httpResponse.close();
		}
		if (httpClient != null) {
			httpClient.close();
		}
	}

}
