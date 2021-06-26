package com.htmessage.yichat.utils;

import android.net.Uri;

/**
 * 项目名称：KTZ
 * 类描述：AudioPlayListener 描述:
 * 创建人：songlijie
 * 创建时间：2018/5/15 16:47
 * 邮箱:814326663@qq.com
 */
public interface AudioPlayListener {
    void onStart(Uri var1);

    void onStop(Uri var1);

    void onComplete(Uri var1);

}
