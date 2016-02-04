package com.fanxin.app.comments;

import internal.org.apache.http.entity.mime.MultipartEntity;
import internal.org.apache.http.entity.mime.content.FileBody;
import internal.org.apache.http.entity.mime.content.StringBody;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SocialApiTask {

    private String url;
    private Map<String, String> map = null;
    Context context;

    public SocialApiTask(Context context, String url, Map<String, String> map) {

        this.url = url;
        this.map = map;
        // has_Array = false;
        this.context = context;
    }

    @SuppressLint("HandlerLeak")
    public void getData(final DataCallBack dataCallBack) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 111 && dataCallBack != null) {
                    JSONObject jsonObject = (JSONObject) msg.obj;

                    dataCallBack.onDataCallBack(jsonObject);

                } else {
                    dataCallBack.onDataCallBack(null);
                    Log.e("APIerrorCode:", String.valueOf(msg.what));

                }
            }
        };

        new Thread() {

            @SuppressWarnings("rawtypes")
            public void run() {
                HttpClient client = new DefaultHttpClient();

                MultipartEntity entity = new MultipartEntity();

                Set keys = map.keySet();
                if (keys != null) {
                    Iterator iterator = keys.iterator();
                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        String value = (String) map.get(key);

                        System.out.println("上传数据------->>>>>>>>" + key + ":"
                                + value);

                        if (key.equals("file")) {
                            File file = new File(value);
                            entity.addPart(key, new FileBody(file));
                        } // 大图.....
                        else if (key.equals("file_big")) {
                            File file = new File(value);
                            entity.addPart(key, new FileBody(file));
                        } else {
                            try {
                                entity.addPart(key, new StringBody(value,
                                        Charset.forName("UTF-8")));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }

                        System.out.println("上传数据------->>>>>>>>" + key + ":"
                                + value);
                    }

                }

                client.getParams().setParameter(
                        CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
                // 请求超时
                client.getParams().setParameter(
                        CoreConnectionPNames.SO_TIMEOUT, 30000);
                HttpPost post = new HttpPost(url);
                post.setEntity(entity);
                StringBuilder builder = new StringBuilder();
                try {
                    HttpResponse response = client.execute(post);

                    if (response.getStatusLine().getStatusCode() == 200) {
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(response.getEntity()
                                        .getContent(), Charset.forName("UTF-8")));
                        for (String s = reader.readLine(); s != null; s = reader
                                .readLine()) {
                            builder.append(s);
                        }
                        String builder_BOM = jsonTokener(builder.toString());
                        System.out.println("返回数据是------->>>>>>>>"
                                + builder.toString());
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject = JSONObject.parseObject(builder_BOM);
                            Message msg = handler.obtainMessage();
                            msg.what = 111;
                            msg.obj = jsonObject;
                            handler.sendMessage(msg);
                        } catch (JSONException e) {
                            Message msg = handler.obtainMessage();
                            msg.what = 222;
                            msg.obj = null;
                            handler.sendMessage(msg);
                        }

                    } else {
                        Log.e("response.getStatusLine().getStatusCode() ----》》",
                                String.valueOf(response.getStatusLine()
                                        .getStatusCode()));
                        Message msg = handler.obtainMessage();
                        msg.what = 333;
                        msg.obj = null;
                        handler.sendMessage(msg);
                    }

                } catch (ClientProtocolException e) {
                    Message msg = handler.obtainMessage();
                    msg.what = 444;
                    msg.obj = null;
                    handler.sendMessage(msg);

                } catch (IOException e) {
                    Message msg = handler.obtainMessage();
                    msg.what = 555;
                    msg.obj = null;
                    handler.sendMessage(msg);
                }

            }
        }.start();

    }

    private String jsonTokener(String in) {
        // consume an optional byte order mark (BOM) if it exists
        if (in != null && in.startsWith("\ufeff")) {
            in = in.substring(1);
        }
        return in;
    }

    /**
     * 网路访问调接口
     * 
     */
    public interface DataCallBack {
        void onDataCallBack(JSONObject data);
    }

}
