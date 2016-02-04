package com.fanxin.app.fx.others;

import internal.org.apache.http.entity.mime.MultipartEntity;
import internal.org.apache.http.entity.mime.content.FileBody;
import internal.org.apache.http.entity.mime.content.StringBody;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import android.widget.Toast;

/**
 * 图片异步加载类
 * 
 * @author Leslie.Fang
 * 
 */
public class LoadDataFromServer {

    private String url;
    private Map<String, String> map = null;
    private List<String> members = new ArrayList<String>();
    // 是否包含数组，默认是不包含
    private boolean has_Array = false;
    Context context;

    public LoadDataFromServer(Context context, String url,
            Map<String, String> map) {
        this.url = url;
        this.map = map;
        has_Array = false;
        this.context = context;
    }

    //
    public LoadDataFromServer(Context context, String url,
            Map<String, String> map, List<String> members) {
        this.url = url;
        this.map = map;
        this.members = members;
        has_Array = true;
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
                    Toast.makeText(context, "服务器访问失败!", Toast.LENGTH_LONG).show();
                    
                    return;
//                    JSONObject jsonObject = null;
//                    dataCallBack.onDataCallBack(jsonObject);
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
                        if (key.equals("file")) {
                            File file = new File(value);
                            entity.addPart(key, new FileBody(file));
                        } else {

                            try {
                                entity.addPart(key, new StringBody(value,
                                        Charset.forName("UTF-8")));

                                System.out.println("key---->>>>" + key
                                        + "------value---->>>>" + value);

                            } catch (UnsupportedEncodingException e) {

                                e.printStackTrace();
                                Message msg = new Message();
                                msg.what = 222;
                                msg.obj = null;
                                handler.sendMessage(msg);
                            }
                        }
                    }

                }
                // 如果包含数组，要把包含的数组放进去，项目目前只有members这个数组，所有固定键值，为了更灵活
                // 可以将传入自定义的键名......
                if (has_Array) {
                    for (int i = 0; i < members.size(); i++) {

                        try {
                            entity.addPart(
                                    "members[]",
                                    new StringBody(members.get(i), Charset
                                            .forName("UTF-8")));
                        } catch (UnsupportedEncodingException e) {
                            Message msg = new Message();
                            msg.what = 222;
                            msg.obj = null;
                            handler.sendMessage(msg);
                            e.printStackTrace();
                        }
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
                            Message msg = new Message();
                            msg.what = 111;
                            msg.obj = jsonObject;
                            handler.sendMessage(msg);
                        } catch (JSONException e) {
                            Message msg = new Message();
                            msg.what = 222;
                            msg.obj = null;
                            handler.sendMessage(msg);
                            e.printStackTrace();
                        }

                    }

                } catch (ClientProtocolException e) {
                    Message msg = new Message();
                    msg.what = 222;
                    msg.obj = null;
                    handler.sendMessage(msg);
                    e.printStackTrace();

                } catch (IOException e) {
                    Message msg = new Message();
                    msg.what = 222;
                    msg.obj = null;
                    handler.sendMessage(msg);
                    e.printStackTrace();
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
