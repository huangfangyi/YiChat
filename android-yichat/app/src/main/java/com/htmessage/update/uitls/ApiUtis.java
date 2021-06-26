package com.htmessage.update.uitls;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.update.data.UserManager;
import com.htmessage.yichat.R;
import com.htmessage.yichat.utils.LoggerUtils;
import com.htmessage.yichat.utils.Param;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by huangfangyi on 2019/7/21.
 * qq 84543217
 */
public class ApiUtis {
    private static ApiUtis apiUtis;
    private OkHttpClient okHttpClient;

    public static ApiUtis getInstance() {
        if (apiUtis == null) {
            apiUtis = new ApiUtis();
        }
        return apiUtis;
    }

    public ApiUtis() {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5000L, TimeUnit.MILLISECONDS)
                .readTimeout(5000L, TimeUnit.MILLISECONDS)
                .build();
    }

    public interface HttpCallBack {

        void onResponse(JSONObject jsonObject);

        void onFailure(int errorCode);
    }


    //纯粹键值对post请求
    public void postJSON(final JSONObject data, final String url, final HttpCallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Request.Builder builer = new Request.Builder()
                        .url(url)
                        .addHeader("Content-Type","application/json");
                if(data!=null){
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    RequestBody body = null;

                    try {
                         String encryptData=URLEncoder.encode(new AESUtils("A286D372M63HFUQW").encryptData(data.toJSONString()), "utf-8");
                        body = RequestBody.create(JSON,encryptData );
                        LoggerUtils.d("ApiUtis---url---" + url + "---data---" + data.toJSONString());

                    } catch (Exception e) {
                        callBack.onFailure(R.string.api_error_4);
                        LoggerUtils.d("ApiUtis---url---" + url + "---data---" + data.toJSONString() + "\n" + "result---" + e.getMessage());

                        return;
                    }

                    builer.post(body);
                } else{
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    builer.post(RequestBody.create(JSON,"{}" ));
                }

                String token= UserManager.get().getToken();
                if(!TextUtils.isEmpty(token)){
                    builer.addHeader("zf-token",token);

                    LoggerUtils.d("ApiUtis---url---" + url + "---zf-token---" + token);

                }
                builer.addHeader("None-AES","1");
                Request request=builer.build();

                try {
                    Response response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        response.body().close();
                        Log.d("responseStr---",responseStr);
                        responseStr=responseStr.substring(1,responseStr.length()-1);

                        responseStr=  new AESUtils("A286D372M63HFUQW").decryptData(responseStr);
                        JSONObject jsonObject = JSONObject.parseObject(responseStr);
                        String code=jsonObject.getString("code");


                        LoggerUtils.d("ApiUtis---url---" + url + "---data---"  + "\n" + "result---" + responseStr);
                        if("003".equals(code)){
                            //归类到SDK中的被提出的操作。
                            HTClient.getInstance().sendConfict();
                            return;
                        }
                        callBack.onResponse(jsonObject);
                    } else {
                        LoggerUtils.d("ApiUtis---url---" + url + "---data---" +   "\n" + "result---" + response.body().string()+"----"+response.code());
                        callBack.onFailure(R.string.api_error_1);
                    }

                } catch (IOException e) {
                    callBack.onFailure(R.string.api_error_2);
                    LoggerUtils.d("ApiUtis---url---" + url + "---data---"   + "\n" + "result---" + e.getMessage());

                } catch (JSONException e) {
                    callBack.onFailure(R.string.api_error_3);
                    LoggerUtils.d("ApiUtis---url---" + url + "---data---"   + "\n" + "result---" + e.getMessage());

                } catch (Exception e) {
                    callBack.onFailure(R.string.api_error_21);
                    e.printStackTrace();
                }

            }
        }).start();


    }


    public void postForm(final List<Param> params, final String url, final HttpCallBack httpCallBack) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                FormBody.Builder bodyBulder = new FormBody.Builder();

                for (Param param : params) {
                    if (!TextUtils.isEmpty(param.getValue())) {
                        bodyBulder.add(param.getKey(), param.getValue());
                        LoggerUtils.d("param.getKey()----->>" + param.getKey() + "----param.getValue()----->>" + param.getValue());
                    }

                }
                RequestBody requestBody = bodyBulder.build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        JSONObject jsonObject = JSONObject.parseObject(responseStr);
                        httpCallBack.onResponse(jsonObject);
                        response.body().close();
                        LoggerUtils.d("result---->" + url + "---" + responseStr);
                    } else {
                        httpCallBack.onFailure(R.string.api_error_1);
                    }

                } catch (IOException e) {
                    httpCallBack.onFailure(R.string.api_error_2);

                    e.printStackTrace();
                } catch (JSONException e) {
                    httpCallBack.onFailure(R.string.api_error_3);
                    e.printStackTrace();
                }
            }
        }).start();


    }

    public void get(final String url, final HttpCallBack httpCallBack) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                 Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        LoggerUtils.d("result---->" + url + "---" + responseStr);
                        JSONObject jsonObject = JSONObject.parseObject(responseStr.substring("callback(".length(),responseStr.length()-3));
                        httpCallBack.onResponse(jsonObject);
                        response.body().close();
                     } else {
                        httpCallBack.onFailure(R.string.api_error_1);
                    }

                } catch (IOException e) {
                    httpCallBack.onFailure(R.string.api_error_2);

                    e.printStackTrace();
                } catch (JSONException e) {
                    httpCallBack.onFailure(R.string.api_error_3);
                    e.printStackTrace();
                }
            }
        }).start();


    }


    public void loadFile(final String url, final String savePath, final HttpCallBack httpCallBack) {



         new Thread(new Runnable() {

             @Override
             public void run() {
                 if(TextUtils.isEmpty(url)){
                     httpCallBack.onFailure(R.string.api_error_22);
                     return;
                 }
                 Request request = new Request.Builder()
                         //下载地址
                         .url(url)
                         .build();
                 try {
                     Response response = okHttpClient.newCall(request).execute();
                     if (response.isSuccessful()&&response.body()!=null) {
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
                         httpCallBack.onResponse(new JSONObject());
                     } else {
                         httpCallBack.onFailure(R.string.api_error_1);
                     }

                 } catch (IOException e) {
                     httpCallBack.onFailure(R.string.api_error_2);

                     e.printStackTrace();
                 } catch (JSONException e) {
                     httpCallBack.onFailure(R.string.api_error_3);
                     e.printStackTrace();
                 }
             }
         }).start();

//        okHttpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                callBack.onFailure("error");
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//                callBack.onSuccess();
//            }
//        });
    }





}
