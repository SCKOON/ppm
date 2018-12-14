package com.sp.ppms.console.util;

import com.sp.ppms.console.domain.SftpConfig;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.security.KeyStore;

public class HttpClientUtil {

	private static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

	/**
	 * 发送http请求
	 * @param requestUrl
	 *            　请求地址
	 * @param messageStr
	 *            提交的数据
	 * @return　JSONObject(通过JSONObject.get(key)的方式获取JSON对象的属性值)
	 * @throws IOException
	 * @author  help
	 */
	public static String httpPostRequestWithBasicAuth(String requestUrl,
													  String messageStr, SftpConfig sftpConfig) throws IOException {
		String tmp = null;
		CloseableHttpResponse response = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(requestUrl);
		if(!"1".equals(sftpConfig.getIsLocation())){
			httpPost.addHeader("Authorization","Basic " + sftpConfig.getBasicAuthUserNameAndPwd());
		}
		httpPost.addHeader("content-type", "application/json");
		httpPost.setEntity(new StringEntity(messageStr));
		try {
			response = httpClient.execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			if(code == 200){
				tmp = EntityUtils.toString(response.getEntity(),"UTF-8");
			}
		} catch (ClientProtocolException e) {
			logger.error(e.getMessage(),e);
		} finally {
			if (null != response) {
				response.close();
			}
			if (null != httpClient) {
				httpClient.close();
			}
		}
		return tmp;
	}

	/**
	 * 发送https请求
	 * @param requestUrl
	 *            　请求地址
	 * @param requestMethod
	 *            　请求方式(GET,POST)
	 * @param messageStr
	 *            提交的数据
	 * @return　JSONObject(通过JSONObject.get(key)的方式获取JSON对象的属性值)
	 * @throws IOException
	 */
	public static String httpsRequest(String requestUrl, String requestMethod,
									 String messageStr,SftpConfig sftpConfig) throws IOException {
		String tmp = null;
		BufferedReader bufferedReader = null;
		OutputStream outputStream = null;
		InputStream inputStream = null;
		HttpsURLConnection conn = null;
		InputStreamReader inputStreamReader = null;
		SSLContext   sslContext = null;
		try {
			sslContext=getSSLContext(sftpConfig.getKeyStoreTrustPassword(),sftpConfig.getKeyStorePassword(),sftpConfig.getKeyStoreClientPath(),sftpConfig.getKeyStoreTrustPath(),sftpConfig.getKeyStoreTypeP12(),sftpConfig.getKeyStoreTypeJks());
			if(sslContext!=null){
				HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
			}

			URL url = new URL(requestUrl);
			conn = (HttpsURLConnection) url.openConnection();
			// conn.setRequestProperty("content-type", "text/html");
			conn.setRequestProperty("content-type", "application/json");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setReadTimeout(1800000);
			conn.setConnectTimeout(1800000);
			// 设置请求方式(GET/POST)
			conn.setRequestMethod(requestMethod);

			if (messageStr != null) {
				outputStream = conn.getOutputStream();
				outputStream.write(messageStr.getBytes("UTF-8"));
				outputStream.close();
			}
			// 从输入流读取返回内容
			inputStream = conn.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			StringBuffer buffer = new StringBuffer();

			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			// 释放资源

			tmp = buffer.toString();
		} catch (ConnectException ce) {
			logger.error(ce.getMessage(),ce);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} finally {
			if (null != bufferedReader) {
				bufferedReader.close();
			}
			if (null != inputStreamReader) {
				inputStreamReader.close();
			}
			if (null != inputStream) {
				inputStream.close();
				inputStream = null;
			}
			if (null != conn) {
				conn.disconnect();
			}
		}
		return tmp;
	}

	public static KeyStore getKeyStore(String password,String keyStorePath,String type) throws Exception{
		//实例化密钥库
		KeyStore ks = KeyStore.getInstance(type);
		//获取密钥库文件流
		FileInputStream   is = new FileInputStream(keyStorePath);
		//加载密钥库
		ks.load(is,password.toCharArray());
		//关闭密钥库文件流
		is.close();
		return ks;
	}

	public  static SSLContext  getSSLContext(String keyStoreTrustPassword,String password,String keyStoreParh,String trustStorePath,String type1,String type2) throws Exception{
		//实例化密钥库
		KeyManagerFactory keyManagerFactory=KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		//获得密钥库
		KeyStore keyStore=getKeyStore(password,keyStoreParh,type1);
		//初始化密钥工厂
		keyManagerFactory.init(keyStore,password.toCharArray());


		//实例化信任库
		TrustManagerFactory trustManagerFactory=TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		//获得信任库
		KeyStore trustStore=getKeyStore(keyStoreTrustPassword,trustStorePath,type2);
		//初始化信任库
		trustManagerFactory.init(trustStore);

		//实例化ssl上下文
		SSLContext ctx=SSLContext.getInstance("SSL");
		//初始化ssl上下文
		ctx.init(keyManagerFactory.getKeyManagers(),trustManagerFactory.getTrustManagers(),null);

		return ctx;
	}

	public static String doPostHttps(String url, String messageStr, SftpConfig sftpConfig) throws Exception {
		HttpClient httpClient = new DefaultHttpClient();
		String result = "";
		try {
			InputStream ksin = null;
			InputStream tsin = null;
			KeyStore keyStore = KeyStore.getInstance(sftpConfig.getKeyStoreTypeJks());
			KeyStore trustKeyStore = KeyStore.getInstance(sftpConfig.getKeyStoreTypeJks());
			try {
				ksin = new FileInputStream(sftpConfig.getKeyStoreTrustPath());
				tsin = new FileInputStream(sftpConfig.getKeyStoreTrustPath());
				keyStore.load(ksin, sftpConfig.getKeyStoreTrustPassword().toCharArray());
				trustKeyStore.load(tsin, sftpConfig.getKeyStoreTrustPassword().toCharArray());
			} catch (Exception e) {
				throw e;
			} finally {
				if (ksin != null) {
					ksin.close();
				}
				if (tsin != null) {
					tsin.close();
				}
			}

			org.apache.http.conn.ssl.SSLSocketFactory socketFactory = new org.apache.http.conn.ssl.SSLSocketFactory(keyStore, sftpConfig.getKeyStoreTrustPassword(), trustKeyStore);
			String[] strs= url.split(":");
			if (strs.length>2) {
				int httpsPort = Integer.valueOf(strs[2].substring(0,4));
				Scheme sch = new Scheme("https", httpsPort, socketFactory);
				httpClient.getConnectionManager().getSchemeRegistry().register(sch);
			}
			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader("content-type", "application/json");
			// 设置参数
			if (messageStr != null) {
				httpPost.setEntity(new StringEntity(messageStr));
			}
			HttpResponse response = httpClient.execute(httpPost);
			if (response != null) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					result = EntityUtils.toString(resEntity, "utf-8");
				}
			}
		} catch (Exception e){
			logger.error("-------------doPostHttps"+e.getMessage());
			throw e;
		} finally {
			logger.info("---------------doPostHttps return result:"+result);
			httpClient.getConnectionManager().shutdown();
		}
		return result;
	}
}
