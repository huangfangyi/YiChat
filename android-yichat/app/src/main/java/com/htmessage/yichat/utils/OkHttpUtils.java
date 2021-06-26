package com.htmessage.yichat.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.HTClientHelper;
import com.htmessage.yichat.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by huangfangyi on 2016/10/27.
 * qq 84543217
 */

public class OkHttpUtils {
    private Context context;
    private OkHttpClient okHttpClient;
    private static final int RESULT_ERROR = 1000;
    private static final int RESULT_SUCESS = 2000;
    // private HttpCallBack httpCallBack;

    private HttpCallBack callBackMap ;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int reusltCode = msg.what;
            String url=msg.getData().getString("url");

            if(TextUtils.isEmpty(url)||callBackMap==null){
                return;
            }
            switch (reusltCode) {
                case RESULT_ERROR:


                    callBackMap.onFailure((""));
                  //  Log.d("result----->", (String) msg.obj);
                    break;
                case RESULT_SUCESS:
                    String result = (String) msg.obj;
                     try {
                        JSONObject jsonObject = JSONObject.parseObject(result);
                        callBackMap.onResponse(jsonObject);
                        if (jsonObject != null && jsonObject.containsKey("code") && jsonObject.containsKey("message")) {
                            if (jsonObject.getInteger("code") == 0 && jsonObject.getString("message").contains("session")) {
                                HTClientHelper.getInstance().notifyConflict(context);
                            }
                        }
                    } catch (JSONException e) {
                        callBackMap.onFailure("error");
                    }

                    break;
            }

        }
    };

    public OkHttpUtils(Context context) {
        this.context = context;
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5000L, TimeUnit.MILLISECONDS)
                .readTimeout(5000L, TimeUnit.MILLISECONDS)
                //其他配置
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .build();
    }


    //纯粹键值对post请求
    public void post(List<Param> params, String url, HttpCallBack httpCallBack) {
        this.callBackMap=httpCallBack;
        FormBody.Builder bodyBulder = new FormBody.Builder();




        for (Param param : params) {
            if(!TextUtils.isEmpty(param.getValue())){
                bodyBulder.add(param.getKey(), param.getValue());
                Log.d("param.getKey()----->>", param.getKey()+"----param.getValue()----->>"+param.getValue());
             }

        }
        RequestBody requestBody = bodyBulder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        startRequest(request);

    }

    //键值对+文件 post请求
    public void post(List<Param> params, List<File> files, String url, HttpCallBack httpCallBack) {
         this.callBackMap=httpCallBack;

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        for (Param param : params) {

            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.getKey() + "\""), RequestBody.create(MediaType.parse(guessMimeType(param.getKey())), param.getValue()));
            Log.d("param.getKey()----->>", param.getKey());
            Log.d("param.getValue()----->>", param.getValue());
        }
        for (File file : files) {
            if (file != null && file.exists()) {

                //TODO-本项目固化文件的键名为“file”
                builder.addPart(Headers.of("Content-Disposition",
                        "form-data; name=\"" + "file" + "\"; filename=\"" + file.getName() + "\""),
                        RequestBody.create(MediaType.parse(guessMimeType(file.getName())), file));

                Log.d("file.getName()----->>", file.getName());
            }

        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        startRequest(request);

    }

    private void startRequest(Request request) {


        if (CommonUtils.isNetWorkConnected(context)) {
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("result---->", call.request().url().toString()+"---error"+e);
                    Message message = handler.obtainMessage();
                    message.what = RESULT_ERROR;
                    message.obj = "error";
                    Bundle bundle=new Bundle();
                    bundle.putString("url",call.request().url().toString());
                    message.setData(bundle);
                    message.sendToTarget();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String responseStr=response.body().string();
                    Log.d("result---->",call.request().url().toString()+"---"+responseStr);
                    Message message = handler.obtainMessage();
                    message.what = RESULT_SUCESS;
                    message.obj =responseStr;
                    Bundle bundle=new Bundle();
                    bundle.putString("url",call.request().url().toString());
                    message.setData(bundle);
                    message.sendToTarget();
                    response.body().close();
                }
            });
        } else {
            CommonUtils.showToastShort(context, R.string.the_current_network);
        }
    }

    public interface HttpCallBack {

        void onResponse(JSONObject jsonObject);

        void onFailure(String errorMsg);
    }

    /**
     * 下载不带进度
     */
    public interface DownloadCallBack {
        void onSuccess();

        void onFailure(String message);
    }


    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    public void loadFile(String url, final String savePath, final DownloadCallBack callBack) {
        Request request = new Request.Builder()
                //下载地址
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onFailure("error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int len;
                byte[] buf = new byte[2048];
                InputStream inputStream = response.body().byteStream();
                //可以在这里自定义路径
                File file1 = new File(savePath);
                FileOutputStream fileOutputStream = new FileOutputStream(file1);
                while ((len = inputStream.read(buf)) != -1) {
                    fileOutputStream.write(buf, 0, len);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                inputStream.close();
                response.body().close();
                callBack.onSuccess();
            }
        });
    }

    /**
     * 下载的带进度的callback
     */
    public interface ProgressDownloadCallBack {
        void onSuccess();

        void onProgress(int progress);

        void onFailure();
    }

    /**
     * 下载文件带进度
     *
     * @param url      下载地址
     * @param savePath 保存地址
     * @param callBack ProgressDownloadCallBack
     */
    public void loadFileHasProgress(String url, final String savePath, final ProgressDownloadCallBack callBack) {
        Request request = new Request.Builder()
                //下载地址
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int len;
                byte[] buf = new byte[2048];
                InputStream inputStream = response.body().byteStream();
                long requestLength = response.body().contentLength();
                long total = 0;
                //可以在这里自定义路径
                File file1 = new File(savePath);
                FileOutputStream fileOutputStream = new FileOutputStream(file1);
                while ((len = inputStream.read(buf)) != -1) {
                    total += len;
                    // publishing the progress....
                    if (requestLength > 0) // only if total length is known
                        callBack.onProgress((int) (total * 100 / requestLength));
                    fileOutputStream.write(buf, 0, len);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                inputStream.close();
                callBack.onSuccess();
            }
        });
    }

    /**
     *  Json数据请求数据
     * @param url   地址
     * @param object    Josn数据
     * @param callBack  回调
     */
    public void postByJSONObject(String url, JSONObject object, HttpCallBack callBack) {
        this.callBackMap=callBack;

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, object.toJSONString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        startRequest(request);
    }

    /**
     * Json数据格式的字符串请求。
     * @param url  地址
     * @param objString  json数据格式的字符串
     * @param callBack   回调监听
     */
    public void postByJsonString(String url, String objString, HttpCallBack callBack) {
        this.callBackMap=callBack;
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, objString);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        startRequest(request);
    }
}
