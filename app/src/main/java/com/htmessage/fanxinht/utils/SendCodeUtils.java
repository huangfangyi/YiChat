package com.htmessage.fanxinht.utils;

import android.content.Context;

import com.htmessage.fanxinht.R;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.IOException;
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
    /**
     * 智能匹配模版发送接口的http地址
     */
    public String URI_SEND_SMS = "http://106.ihuyi.com/webservice/sms.php?method=Submit";
    public String URI_SEND_USERNAME = "C26846914";
    //短信验证的key
    public String SMSAPPKEY = "cc30ed16d5036ef486f931e25860dd17";
    //短信模板//设置您要发送的内容(内容必须和某个模板匹配。以下例子匹配的是系统提供的1号模板）
    public String SMSTEXT = "您的验证码是：【%s】。请不要把验证码泄露给其他人。如非本人操作，可不用理会！";

    private Context context;
    private OkHttpClient okHttpClient;
    private SmsCodeListener listener;
    public SendCodeUtils(Context context) {
        this.context = context;
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
    }

    public void sendCode(String mobile, final SmsCodeListener listener){
        this.listener = listener;
        //发送验证码
         final int smsCode = (int) (Math.random() * 9000 + 1000);
        FormBody.Builder bodyBulder = new FormBody.Builder();
        bodyBulder.add("account", URI_SEND_USERNAME);
        bodyBulder.add("password", SMSAPPKEY);
        bodyBulder.add("mobile", mobile);
//        bodyBulder.add("content",String.format(SMSTEXT,String.valueOf(smsCode)));
        bodyBulder.add("content",context.getString(R.string.smsCodeText_frount)+String.valueOf(smsCode)+context.getString(R.string.smsCodeText_after));
        bodyBulder.add("time", String.valueOf(System.currentTimeMillis()));
        RequestBody requestBody = bodyBulder.build();
        Request request = new Request.Builder()
                .url(URI_SEND_SMS)
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
                listener.onSuccess(recCode,recMsg, String.valueOf(smsCode));
            }
        });
    }
    public  interface  SmsCodeListener{
       void onSuccess(String recCode, String recMsg, String smsCode);
        void onFailure(IOException e);
    }
}
