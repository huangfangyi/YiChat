package com.htmessage.sdk.manager;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.tencent.mmkv.MMKV;

import java.io.ByteArrayOutputStream;
import java.util.Set;

/**
 * Created by huangfangyi on 2018/9/24.
 * qq 84543217
 */
public class MmvkManger {

    private static MMKV mmkv;

    private static MmvkManger mmvkManger;




    public static MmvkManger getIntance() {

        if (mmvkManger == null) {
            mmvkManger = new MmvkManger();
        }

        return mmvkManger;
    }

    public MmvkManger() {
        mmkv = MMKV.defaultMMKV();
    }


    public void putBtimap(String key, Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] datas = baos.toByteArray();
            mmkv.encode(key, datas);
        } catch (Exception e) {
            e.printStackTrace();
        }
         
         }

    /**
     * 移除某个key
     *
     * @param key
     * @return 是否移除成功
     */
    public void remove(String key) {
        mmkv.remove(key).apply();
    }

    public Bitmap getBitmap(String key) {
        byte[] bytes = mmkv.decodeBytes(key);
        if (bytes==null||bytes.length == 0) {
            return null;
        }
        try {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            return null;
        }
    }


    public void putJSON(String key, JSONObject jsonObject) {
        if (jsonObject == null) {
            mmkv.encode(key, "");
            return;
        }
        mmkv.encode(key, jsonObject.toJSONString());
    }

    public void putString(String key, String message) {
        if (TextUtils.isEmpty(message)) {
            mmkv.encode(key, "");
            return;
        }
        mmkv.encode(key, message);
    }

    public void putStringSet(String key, Set<String> data) {

        mmkv.encode(key, data);
    }

    public Set<String> getStringSet(String key){
        return mmkv.getStringSet(key,null);
    }

    public String getAsString(String key) {
        return mmkv.decodeString(key);
    }

    public void putLong(String key, long value) {
        mmkv.encode(key, value);
    }

    public void putDouble(String key, Double value) {
        mmkv.encode(key, value);
    }
    public void putInt(String key, Integer value) {
        mmkv.encode(key, value);
    }

    public int getInt(String key,int defaultInt){
        return mmkv.getInt(key,defaultInt);

    }
    public Double getDouble(String key) {
       return mmkv.decodeDouble(key);
    }
    public long getAsLong(String key) {
        return mmkv.decodeLong(key,0);
    }

    public boolean getBoolean(String key,boolean defaultValue){
        return mmkv.getBoolean(key,defaultValue);
    }

    public void putBoolean(String key,boolean value){
        mmkv.encode(key,value);
    }


    public JSONObject getJSON(String key) {
        String string = mmkv.decodeString(key);
        if (string != null) {
            try {
                return JSONObject.parseObject(string);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public void putJSONArray(String key, JSONArray jsonArray) {
        if (jsonArray == null) {
            mmkv.encode(key, "");
            return;
        }
        mmkv.encode(key, jsonArray.toJSONString());
    }

    public JSONArray getJSONArray(String key) {

        String string = mmkv.decodeString(key);
        if (string != null) {
            try {
                return JSONArray.parseArray(string);
            } catch (JSONException e) {

                e.printStackTrace();
            }

        }

        return null;

    }

}
