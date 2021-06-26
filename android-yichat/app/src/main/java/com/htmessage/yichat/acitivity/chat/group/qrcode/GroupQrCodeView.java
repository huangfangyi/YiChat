package com.htmessage.yichat.acitivity.chat.group.qrcode;

import android.graphics.Bitmap;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.acitivity.BaseView;

/**
 * 项目名称：fanxinht
 * 类描述：GroupQrCodeView 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/10 10:44
 * 邮箱:814326663@qq.com
 */
public interface GroupQrCodeView extends BaseView<GroupQrCodePrester>{
    void showQrCode(Bitmap bitmap);
    void showError(String error);
    JSONObject getIntenetData();
    View getFrameLayout();
}
