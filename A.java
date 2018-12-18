
package com.sp.ppms.console.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;

public class A {
    private static final String KEY_STORE_TYPE_JKS = "jks";
    private static final String SCHEME_HTTPS = "https";
    private static final int HTTPS_PORT = 5543;
    private static final String HTTPS_URL = "https://nmsim01.fste01.sap.local:5543/nmsim/services/disconnect";
//    private static final String HTTPS_URL = "https://nmsim01.fste01.sap.local:5543/nmsim/services/getStatus";
    private static final String KEYSTORE_PATH = "F:\\soft\\java\\1.8\\jre\\lib\\security\\nmsimserver.truststore";
    private static final String KEY_STORE_CLIENT_PATH = "F:\\soft\\ppms_CerPk12\\ppms.qa.jks";
    private static final String kEY_STORE_TRUST_PASSWORD = "changeit";
    private static final String KEY_STORE_PASSWORD = "NARI_$123";
    private static final String CHARSET = "utf-8";

    public static String doPost(String url, String messageStr) throws Exception {
        HttpClient httpClient = new DefaultHttpClient();
        String result = "";
        try {
            System.setProperty("javax.net.debug","all");
            InputStream ksin = null;
            InputStream tsin = null;
            KeyStore keyStore = KeyStore.getInstance("jks");
            KeyStore trustKeyStore = KeyStore.getInstance(KEY_STORE_TYPE_JKS);
            try {
                ksin = new FileInputStream(KEY_STORE_CLIENT_PATH);
                tsin = new FileInputStream(KEYSTORE_PATH);
                keyStore.load(ksin, KEY_STORE_PASSWORD.toCharArray());
                trustKeyStore.load(tsin, kEY_STORE_TRUST_PASSWORD.toCharArray());
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
            URL localurl = new URL(HTTPS_URL);
            InputStream lo1 = localurl.openStream();
            int returnStr = lo1.available();
            SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore, KEY_STORE_PASSWORD, trustKeyStore);
            Scheme sch = new Scheme(SCHEME_HTTPS, HTTPS_PORT, socketFactory);
            httpClient.getConnectionManager().getSchemeRegistry().register(sch);
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
                    result = EntityUtils.toString(resEntity, CHARSET);
                }
            }
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return result;
    }

    public static void main(String[] args) throws Exception {

        String result = A.doPost(HTTPS_URL, "{\"devices\":[{\"deviceID\":\"GA4700050\"}]}");
//        String result = A.doPost(HTTPS_URL, "{\"jobID\":2018111317073900122,\"deviceID\":[\"GA4700050\"]}");
    }
}