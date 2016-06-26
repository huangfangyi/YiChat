package com.fanxin.app.main.utils;



import android.app.ProgressDialog;
import android.content.Context;


import okhttp3.Callback;
import okhttp3.Response;


/**
 * 本地实现的CallBack
 *
 * @author wangxy
 *
 */
public abstract class BaseCallBack implements Callback {

    private ProgressDialog progressDialog;

    private boolean displayDialog;

    private Context context;

    public BaseCallBack(Context context) {
        this(context, true);
    }

    public BaseCallBack(Context context, boolean displayDialog) {
        if (displayDialog) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("正在加载...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
    }


    public void inProgress(float progress) {
        if (displayDialog) {
            progressDialog.show();
        }
    }


    public void onResponse(Object o) {

    }


    public String parseNetworkResponse(Response response) throws Exception {
        return this.initResponse(response);
    }

    public String initResponse (Response response) {
        if (displayDialog) {
            progressDialog.dismiss();
        }
        String result = null;
        try {
            result = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
