package com.htmessage.yichat.acitivity.main.qrcode;

import android.graphics.Bitmap;

import com.htmessage.yichat.acitivity.BaseView;

/**
 * 项目名称：yichat0504
 * 类描述：QrCodeView 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/10 10:44
 * 邮箱:814326663@qq.com
 */
public interface QrCodeView extends BaseView<QrCodePrester>{
    void showQrCode(Bitmap bitmap);
    void showError(String error);
}
