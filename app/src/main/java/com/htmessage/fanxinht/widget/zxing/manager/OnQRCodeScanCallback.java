package com.htmessage.fanxinht.widget.zxing.manager;

/**
 * 二维码扫描结果回调
 * Created by HDL on 2017/6/28.
 */

public interface OnQRCodeScanCallback {
    /**
     * 扫描完成的时候会回调此方法，结果存在于result中
     *
     * @param result 扫描结果
     */
    void onCompleted(String result);

    /**
     * 当扫描过程出错的时候会回调
     *
     * @param errorMsg 错误信息
     */
    void onError(Throwable errorMsg);

    /**
     * 当扫描被取消的时候回调
     */
    void onCancel();
}
