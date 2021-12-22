package com.bistu.weatherapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class RequestUtil {
    private static final String TAG = "RequestUtil";
    private static final javax.net.ssl.HttpsURLConnection HttpsURLConnection = null;



    static {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[] {};
            }
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        } };
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sc.init(null, trustAllCerts, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier( new HostnameVerifier(){
            public boolean verify(String string, SSLSession ssls) {
                return true;
            }
        });
    }

    public static String requestHttps(String path, String method, String data)
            throws IOException {
        URL url = null;
        url = new URL(path);
        Log.i(TAG, "路径    " + path + "\n方法" + method + "\n提交数据" + data);
        HttpsURLConnection httpURLConnection = (HttpsURLConnection) url
                .openConnection();
        httpURLConnection.setRequestMethod(method);
        httpURLConnection.setConnectTimeout(10000);
        httpURLConnection.setReadTimeout(10000);
        if(!TextUtils.isEmpty(data)){
            boolean isJson=false;
            try {
                JSONObject jsonObject=new JSONObject(data);
                isJson=true;
                jsonObject=null;
            } catch (JSONException e) {
            }
            if(!isJson) {
                httpURLConnection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
            }else {

                httpURLConnection.setRequestProperty("Content-Type",
                        "application/json");
            }
        }


        if(!method.equals("GET")){
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
        }

        if(!TextUtils.isEmpty(data)) {
            OutputStream outputStream = httpURLConnection.getOutputStream();

            outputStream.write(data.getBytes("utf-8"));
            outputStream.flush();
            outputStream.close();
        }



        BufferedInputStream bis = new BufferedInputStream(
                httpURLConnection.getInputStream());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int len;
        byte[] arr = new byte[1024];
        while ((len = bis.read(arr)) != -1) {
            bos.write(arr, 0, len);
            bos.flush();
        }

        bos.close();
        httpURLConnection.disconnect();
        String readData = bos.toString("utf-8");
        Log.i(TAG, "返回数据" + readData);
        return readData;
    }

}