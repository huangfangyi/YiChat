package com.htmessage.fanxinht.widget.zxing.manager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.htmessage.fanxinht.widget.zxing.activity.CaptureActivity;
import com.htmessage.fanxinht.widget.zxing.encoding.EncodingUtils;


/**
 * 二维码管理器
 * Created by HDL on 2017/6/28.
 */

public class QRCodeManager extends IQRCodeStrategy {
    /**
     * 扫描请求码
     */
    private static final int SCAN_REQUEST_CODE = 410;
    private static QRCodeManager mQRCodeManager;
    private Activity context;
    private OnQRCodeScanCallback callback;
    /**
     * 当前的请求码
     */
    private int curRequestCode = SCAN_REQUEST_CODE;
    /**
     * 请求的类型，默认为0
     */
    private int requestType = 0;

    private QRCodeManager() {
    }

    public static QRCodeManager getInstance() {
        synchronized (QRCodeManager.class) {
            if (mQRCodeManager == null) {
                mQRCodeManager = new QRCodeManager();
            }
        }
        return mQRCodeManager;
    }

    /**
     * 关联调用类
     *
     * @param context
     * @return
     */
    public QRCodeManager with(Activity context) {
        this.context = context;
        return this;
    }

    /**
     * 设置请求类型
     *
     * @param reqeustType
     * @return
     */
    public QRCodeManager setReqeustType(int reqeustType) {
        this.requestType = reqeustType;
        return this;
    }

    /**
     * <p>扫描二维码.</p>
     * 带回调的，一般表示结果由本管理器来处理onActivityResult方法，结果通过callback拿到。
     * <br/>
     * 此时，需要在activity/fragment的onActivityResult方法中注册{@link QRCodeManager#onActivityResult(int, int, Intent)}方法
     *
     * @return
     */
    public QRCodeManager scanningQRCode(OnQRCodeScanCallback callback) {
        this.callback = callback;
        scanning(SCAN_REQUEST_CODE);
        return this;
    }

    /**
     * <p>扫描二维码.</p>
     * 不带回调的，一般表示自己处理onActivityResult方法
     *
     * @return
     */
    public QRCodeManager scanningQRCode(int requestCode) {
        scanning(requestCode);
        return this;
    }

    /**
     * 发起扫描
     *
     * @param requestCode
     */
    @Override
    void scanning(int requestCode) {
        this.curRequestCode = requestCode;
        Intent intent = new Intent(context, CaptureActivity.class);
        intent.putExtra("type", requestType);
        context.startActivityForResult(intent, SCAN_REQUEST_CODE);
    }

    /**
     * 结果回调
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        数据
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (callback == null) {
            return;
        }
        if (requestCode == curRequestCode && resultCode == Activity.RESULT_OK) {//成功
            String result = data.getStringExtra("result");
            if (TextUtils.isEmpty(result)) {
                callback.onError(new Throwable("result is null"));
            } else {
                callback.onCompleted(result);
            }
        } else if (requestCode == curRequestCode && resultCode == Activity.RESULT_CANCELED) {//取消
            callback.onCancel();
        }
    }

    /**
     * 创建二维码（不带logo）
     *
     * @param content   二维码的内容
     * @param widthPix  二维码的宽
     * @param heightPix 二维码的高
     * @return
     */
    @Override
    public Bitmap createQRCode(String content, int widthPix, int heightPix) {
        return EncodingUtils.createQRCode(content, widthPix, heightPix, null);
    }

    /**
     * 创建二维码（不带logo）
     *
     * @param content   二维码的内容
     * @param widthPix  二维码的宽
     * @param heightPix 二维码的高
     * @param logoBm    logo对应的bitmap对象
     * @return
     */
    @Override
    public Bitmap createQRCode(String content, int widthPix, int heightPix, Bitmap logoBm) {
        return EncodingUtils.createQRCode(content, widthPix, heightPix, logoBm);
    }
}
