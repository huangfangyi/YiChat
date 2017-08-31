package com.htmessage.fanxinht.anyrtc.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Set;

/**
 * Created by Skyline on 2016/5/24.
 */
public class SharePrefUtil {
    static SharedPreferences prefs;

    public static void init(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isNight() {
        return prefs.getBoolean("isNight", false);
    }

    public static void setNight(Context context, boolean isNight) {
        prefs.edit().putBoolean("isNight", isNight).commit();
        /*if (context instanceof BaseActivity) {
            ((BaseActivity) context).reload();
        }*/
    }

    /**
     * Set a cookie String value in the preferences editor, to be written back once
     *
     * @param cookie
     */
    public static void setCookie(String cookie) {
        prefs.edit().putString("cookie", cookie).commit();
    }

    /**
     * get the cookie which store in the SharedPreferences
     *
     * @return default return ""
     */
    public static String getCookie() {
        return prefs.getString("cookie", "");
    }

    /**
     * clear the cookie in the SharedPreferences
     */
    public static void clearCookie() {
        prefs.edit().putString("cookie", "").commit();
    }

    /**
     * Set a String value in the preferences editor, to be written back once
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     */
    public static void putString(String key, String value) {
        prefs.edit().putString(key, value).commit();
    }

    /**
     * Retrieve a String value from the preferences.
     *
     * @param key
     * @return default return null
     */
    public static String getString(String key) {
        return prefs.getString(key, null);
    }

    /**
     * Set a set of String values in the preferences editor, to be written
     *
     * @param key    The name of the preference to modify.
     * @param values The set of new values for the preference.  Passing {@code null}
     *               for this argument is equivalent to calling {@link #remove(String)} with
     *               this key.
     */
    public static void putStringSet(String key, Set<String> values) {
        prefs.edit().putStringSet(key, values).commit();
    }

    /**
     * Retrieve a set of String values from the preferences.
     *
     * @param key
     * @return default return null
     */
    public static Set<String> getStringSet(String key) {
        return prefs.getStringSet(key, null);
    }

    /**
     * Set an int value in the preferences editor, to be written back once
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     */
    public static void putInt(String key, int value) {
        prefs.edit().putInt(key, value).commit();
    }

    /**
     * Retrieve an int value from the preferences.
     *
     * @param key
     * @return default return 0
     */
    public static int getInt(String key) {
        return prefs.getInt(key, 0);
    }

    /**
     * Set a boolean value in the preferences editor, to be written back
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     */
    public static void putBoolean(String key, boolean value) {
        prefs.edit().putBoolean(key, value).commit();
    }

    /**
     * Retrieve a boolean value from the preferences.
     *
     * @param key
     * @return default return false
     */
    public static boolean getBoolean(String key) {
        return prefs.getBoolean(key, false);
    }


    /**
     * Set a float value in the preferences editor, to be written back once
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     */
    public static void putFloat(String key, float value) {
        prefs.edit().putFloat(key, value).commit();
    }

    /**
     * Retrieve a float value from the preferences.
     *
     * @param key
     * @return default return 0
     */
    public static float getFloat(String key) {
        return prefs.getFloat(key, 0);
    }

    /**
     * Set a long value in the preferences editor, to be written back once
     *
     * @param key   The name of the preference to modify.
     * @param value The new value for the preference.
     */
    public static void putLong(String key, long value) {
        prefs.edit().putLong(key, value).commit();
    }

    /**
     * Retrieve a long value from the preferences.
     *
     * @param key
     * @return default return 0
     */
    public static long getLong(String key) {
        return prefs.getLong(key, 0);
    }


    /**
     * desc:保存对象
     *
     * @param key
     * @param obj 要保存的对象，只能保存实现了serializable的对象
     *            modified:
     */
    public static void saveObject(String key, Object obj) {
        try {
            // 保存对象
            //先将序列化结果写到byte缓存中，其实就分配一个内存空间
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bos);
            //将对象序列化写入byte缓存
            os.writeObject(obj);
            //将序列化的数据转为16进制保存
            String bytesToHexString = bytesToHexString(bos.toByteArray());
            //保存该16进制数组

            prefs.edit().putString(key, bytesToHexString).commit();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("", "保存obj失败");
        }
    }

    /**
     * desc:将数组转为16进制
     *
     * @param bArray
     * @return modified:
     */
    public static String bytesToHexString(byte[] bArray) {
        if (bArray == null) {
            return null;
        }
        if (bArray.length == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * desc:获取保存的Object对象
     *
     * @param key
     * @return modified:
     */
    public static Object readObject(String key) {
        try {
            if (prefs.contains(key)) {
                String string = prefs.getString(key, "");
                if (TextUtils.isEmpty(string)) {
                    return null;
                } else {
                    //将16进制的数据转为数组，准备反序列化
                    byte[] stringToBytes = StringToBytes(string);
                    ByteArrayInputStream bis = new ByteArrayInputStream(stringToBytes);
                    ObjectInputStream is = new ObjectInputStream(bis);
                    //返回反序列化得到的对象
                    Object readObject = is.readObject();
                    return readObject;
                }
            }
        } catch (StreamCorruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //所有异常返回null
        return null;

    }

    /**
     * desc:将16进制的数据转为数组
     * <p>创建人：聂旭阳 , 2014-5-25 上午11:08:33</p>
     *
     * @param data
     * @return modified:
     */
    public static byte[] StringToBytes(String data) {
        String hexString = data.toUpperCase().trim();
        if (hexString.length() % 2 != 0) {
            return null;
        }
        byte[] retData = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i++) {
            int int_ch;  // 两位16进制数转化后的10进制数
            char hex_char1 = hexString.charAt(i); ////两位16进制数中的第一位(高位*16)
            int int_ch1;
            if (hex_char1 >= '0' && hex_char1 <= '9')
                int_ch1 = (hex_char1 - 48) * 16;   //// 0 的Ascll - 48
            else if (hex_char1 >= 'A' && hex_char1 <= 'F')
                int_ch1 = (hex_char1 - 55) * 16; //// A 的Ascll - 65
            else
                return null;
            i++;
            char hex_char2 = hexString.charAt(i); ///两位16进制数中的第二位(低位)
            int int_ch2;
            if (hex_char2 >= '0' && hex_char2 <= '9')
                int_ch2 = (hex_char2 - 48); //// 0 的Ascll - 48
            else if (hex_char2 >= 'A' && hex_char2 <= 'F')
                int_ch2 = hex_char2 - 55; //// A 的Ascll - 65
            else
                return null;
            int_ch = int_ch1 + int_ch2;
            retData[i / 2] = (byte) int_ch;//将转化后的数放入Byte里
        }
        return retData;
    }


}
