package com.htmessage.fanxinht.utils;

import android.content.Context;
import android.util.Log;

import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.R;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 项目名称：youyouRedpacket
 * 类描述：SendCodeUtils 描述:
 * 创建人：songlijie
 * 创建时间：2017/3/2 10:06
 * 邮箱:814326663@qq.com
 */
public class SendCodeUtils {
    private String TAG = SendCodeUtils.class.getSimpleName();


    private static Context context;
    private OkHttpClient okHttpClient;
    private SmsCodeListener listener;
    private static SendCodeUtils sendCodeUtils;

    /**
     * 获取单例对象
     *
     * @return
     */
    public static SendCodeUtils getIntence() {
        if (sendCodeUtils == null) {
            throw new RuntimeException("please init first in application!");
        }
        return sendCodeUtils;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public static void init(Context context) {
        sendCodeUtils = new SendCodeUtils(context);
    }

    /**
     * 初始化
     *
     * @param context
     */
    public SendCodeUtils(Context context) {
        this.context = context;
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
    }

    /**
     * 发送验证码
     *
     * @param mobile
     * @param listener
     */
    public void sendCode(String mobile, final SmsCodeListener listener) {
        this.listener = listener;
        //发送验证码
        final int smsCode = (int) (Math.random() * 900000 + 100000);
        FormBody.Builder bodyBulder = new FormBody.Builder();
        bodyBulder.add("account", HTConstant.URI_SEND_USERNAME);
        bodyBulder.add("password", HTConstant.SMSAPPKEY);
        bodyBulder.add("mobile", mobile);
        bodyBulder.add("content", String.format(HTConstant.SMSTEXT, String.valueOf(smsCode)));
        bodyBulder.add("time", String.valueOf(System.currentTimeMillis()));
        RequestBody requestBody = bodyBulder.build();
        Request request = new Request.Builder()
                .url(HTConstant.URI_SEND_SMS)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                listener.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Document doc = null;
                try {
                    doc = DocumentHelper.parseText(response.body().string());
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                Element root = doc.getRootElement();
                String recCode = root.elementText("code");
                String recMsg = root.elementText("msg");
                listener.onSuccess(recCode, recMsg, String.valueOf(smsCode));
                Log.d(TAG,"-----验证码:"+smsCode);
            }
        });
    }

    public  interface  SmsCodeListener{
       void onSuccess(String recCode, String recMsg, String smsCode);
        void onFailure(IOException e);
    }
}
