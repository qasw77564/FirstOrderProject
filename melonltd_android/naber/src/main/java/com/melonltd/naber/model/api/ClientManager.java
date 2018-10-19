package com.melonltd.naber.model.api;

import android.annotation.SuppressLint;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by evan on 2018/1/5.
 */

public class ClientManager {
//    private static final String TAG = ClientManager.class.getSimpleName();
    private static final ConnectionPool CONNECTION_POOL = new ConnectionPool();
    private static final HttpLoggingInterceptor LOGGING = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(25, TimeUnit.SECONDS)
            .readTimeout(25, TimeUnit.SECONDS)
            .writeTimeout(25, TimeUnit.SECONDS)
            .addInterceptor(LOGGING)
            .connectionPool(CONNECTION_POOL)
            .hostnameVerifier(new TrustAllHostnameVerifier())
            .sslSocketFactory(createSSLSocketFactory(), new TrustAllManager())
            .build();

    private static ClientManager CLIENT_MANAGER = new ClientManager();

    private final static String HEADER_KEY = "Authorization";
    private final static String PARAMETER = "data";

    public static ClientManager getInstance() {
        if (CLIENT_MANAGER == null) {
            CLIENT_MANAGER = new ClientManager();
        }
        return CLIENT_MANAGER;
    }

    public static Call post(String url, String data) {
        RequestBody formBody = new FormBody.Builder() .add(PARAMETER, data)  .build();
        Request request = new Request.Builder()  .url(url)  .post(formBody) .build();
        return CLIENT.newCall(request);
    }

    public static Call post(String url) {
        RequestBody formBody = new FormBody.Builder().build();
        Request request = new Request.Builder().url(url).post(formBody).build();
        return CLIENT.newCall(request);
    }

    public static Call postHeader(String url, String token, String data) {
        RequestBody formBody = new FormBody.Builder().add(PARAMETER, data).build();
        Request request = new Request.Builder().header(HEADER_KEY, token).url(url).post(formBody).build();
        return CLIENT.newCall(request);
    }

    public static Call postHeader(String url, String token) {
        RequestBody formBody = new FormBody.Builder()
                .build();
        Request request = new Request.Builder()
                .header(HEADER_KEY, token)
                .url(url)
                .post(formBody)
                .build();

        return CLIENT.newCall(request);
    }

    @SuppressLint("TrulyRandom")
    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory sSLSocketFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllManager()}, new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception e) {

        }
        return sSLSocketFactory;
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }

        public static TrustAllHostnameVerifier newInstance() {
            return new TrustAllHostnameVerifier();
        }
    }

    private static class TrustAllManager implements X509TrustManager {

        public static TrustAllManager newInstance() {
            return new TrustAllManager();
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

}
