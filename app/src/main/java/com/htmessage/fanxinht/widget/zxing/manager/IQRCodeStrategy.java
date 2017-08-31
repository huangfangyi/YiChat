package com.htmessage.fanxinht.widget.zxing.manager;

import android.content.Intent;
import android.graphics.Bitmap;

/**
 * 二维码策略类
 * Created by HDL on 2017/6/28.
 */

abstract class IQRCodeStrategy {
    /**
     * 发起扫描
     * @param requestCode
     */
    abstract  void scanning(int requestCode);

    /**
     * 结果回调
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        数据
     */
    abstract void onActivityResult(int requestCode, int resultCode, Intent data);

    /**
     * 创建二维码（不带logo）
     *
     * @param content   二维码的内容
     * @param widthPix  二维码的宽
     * @param heightPix 二维码的高
     * @return
     */
    abstract   Bitmap createQRCode(String content, int widthPix, int heightPix);

    /**
     * 创建二维码（不带logo）
     *
     * @param content   二维码的内容
     * @param widthPix  二维码的宽
     * @param heightPix 二维码的高
     * @param logoBm    logo对应的bitmap对象
     * @return
     */
    abstract Bitmap createQRCode(String content, int widthPix, int heightPix, Bitmap logoBm);
}
