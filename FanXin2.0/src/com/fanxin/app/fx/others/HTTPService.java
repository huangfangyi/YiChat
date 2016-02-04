package com.fanxin.app.fx.others;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
 

public class HTTPService {
    private static final String TAG = HTTPService.class.getSimpleName();
    private static HTTPService instance = null;
    private static final int CONNECTION_TIMEOUT = 30000;

    private HTTPService() {
    }

    public static HTTPService getInstance() {
        if (instance == null) {
            instance = new HTTPService();
        }

        return instance;
    }

    /**
     * 是否有可用网络
     */
    public boolean hasActiveNet(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }

        return false;
    }

    public static boolean hasWifi(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (networkInfo != null) {
            return networkInfo.isAvailable();
        }

        return false;
    }

    public InputStream getStream(String url) {
        HttpParams httpparams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpparams,
                CONNECTION_TIMEOUT);
        HttpClient client = new DefaultHttpClient(httpparams);
        HttpGet get = new HttpGet(url);

        try {
            HttpResponse response = client.execute(get);
            int status = response.getStatusLine().getStatusCode();

            if (200 == status) {
                return response.getEntity().getContent();
            }
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

        return null;
    }

    /**
     * Get 请求
     * 
     * @param url
     */
    public String[] getRequest(String url, String cookie) {
        HttpParams httpparams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpparams,
                CONNECTION_TIMEOUT);
        HttpClient client = new DefaultHttpClient(httpparams);
        HttpGet get = new HttpGet(url);

        if (cookie != null) {
            get.addHeader("Cookie", cookie);
        }

        try {
            HttpResponse response = client.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "utf-8");
            String message = "";

            if (statusCode == 200) {
                message = "ok";
            } else if (statusCode == 500) {
                message = "服务器内部错误";
            } else {
                if (result != null) {
                    JSONObject obj = JSON.parseObject(result);
                    if (obj != null && obj.containsKey("error")) {
                        message = obj.getString("error");
                    } else {
                        message = "发生未知错误";
                    }
                } else {
                    message = "发生未知错误";
                }
            }

            return new String[] { statusCode + "", result, message };
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

        return null;
    }

    /**
     * Post 请求
     */
    public String[] postRequest(String url, Map<String, String> headers,
            Map<String, String> params, String cookie) {
        HttpParams httpparams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpparams,
                CONNECTION_TIMEOUT);
        HttpClient client = new DefaultHttpClient(httpparams);
        HttpPost post = new HttpPost(url);

        // 添加 headers
        if (headers != null) {
            for (Entry<String, String> item : headers.entrySet()) {
                String key = item.getKey();
                String value = item.getValue();
                post.addHeader(key, value);
            }
        }

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        // 添加 form 参数
        if (params != null) {
            for (Entry<String, String> item : params.entrySet()) {
                String key = item.getKey();
                String value = item.getValue();
                BasicNameValuePair basic = new BasicNameValuePair(key, value);
                nvps.add(basic);
            }
        }

        // 如果有 cookie 则添加
        if (cookie != null) {
            post.addHeader("Cookie", cookie);
        }

        try {
            HttpEntity entity = new UrlEncodedFormEntity(nvps, HTTP.UTF_8);
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            String result = EntityUtils.toString(response.getEntity(), "utf-8");
            String message = "";

            if (statusCode == 201) {
                message = "ok";
            } else if (statusCode == 500) {
                message = "服务器内部错误";
            } else {
                if (result != null) {
                    JSONObject obj = JSON.parseObject(result);
                    if (obj != null && obj.containsKey("error")) {
                        message = obj.getString("error");
                    } else {
                        message = "发生未知错误";
                    }
                } else {
                    message = "发生未知错误";
                }
            }

            return new String[] { statusCode + "", result, message };
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

        return null;
    }
}
