package com.fanxin.app.main.utils;

import com.fanxin.app.DemoApplication;

import okhttp3.OkHttpClient;

/**
 * Created by ustc on 2016/6/27.
 */
public class OkHttpUtils {

    private static OkHttpClient okHttpClient;


    public  static  OkHttpClient getInstance(){
        okHttpClient=DemoApplication.getInstance().okHttpClient;
        return  okHttpClient;
    }


    private void post(){


    }






}
