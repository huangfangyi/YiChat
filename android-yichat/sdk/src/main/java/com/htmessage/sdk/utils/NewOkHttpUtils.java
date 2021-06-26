package com.htmessage.sdk.utils;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;


import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by huangfangyi on 2019/1/14.
 * qq 84543217
 */
public class NewOkHttpUtils {
    private OkHttpClient okHttpClient;
    public NewOkHttpUtils(){

        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5000L, TimeUnit.MILLISECONDS)
                .readTimeout(5000L, TimeUnit.MILLISECONDS)


                .build();
    }



    //纯粹键值对post请求
    public void post(List<Param> params, String url, HttpCallBack httpCallBack) {
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
        try {
            Response response=okHttpClient.newCall(request).execute();
            if(response.isSuccessful()){
                String responseStr=response.body().string();
                JSONObject jsonObject= JSONObject.parseObject(responseStr);
                httpCallBack.onResponse(jsonObject);
                response.body().close();
                Log.d("result---->",url+"---"+responseStr);
            }else {
                httpCallBack.onFailure("Response Error");
            }

        } catch (IOException e) {
            httpCallBack.onFailure("IO Error");

            e.printStackTrace();
        }catch (JSONException e){
            httpCallBack.onFailure("JSON Error");
            e.printStackTrace();
        }

//        okHttpClient.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    Log.d("result---->", call.request().url().toString()+"---error"+e);
//                    Message message = handler.obtainMessage();
//                    message.what = RESULT_ERROR;
//                    message.obj = "error";
//                    Bundle bundle=new Bundle();
//                    bundle.putString("url",call.request().url().toString());
//                    message.setData(bundle);
//                    message.sendToTarget();
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//
//                    String responseStr=response.body().string();
//                    Log.d("result---->",call.request().url().toString()+"---"+responseStr);
//                    Message message = handler.obtainMessage();
//                    message.what = RESULT_SUCESS;
//                    message.obj =responseStr;
//                    Bundle bundle=new Bundle();
//                    bundle.putString("url",call.request().url().toString());
//                    message.setData(bundle);
//                    message.sendToTarget();
//                }
//            });
//        } else {
//            CommonUtils.showToastShort(context, R.string.the_current_network);
//        }

    }


    public interface HttpCallBack {

        void onResponse(JSONObject jsonObject);

        void onFailure(String errorMsg);
    }



}
