package com.htmessage.fanxinht.acitivity.main.qrcode;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.HTConstant;

/**
 * 项目名称：yichat0504
 * 类描述：QrCodePrester 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/10 10:44
 * 邮箱:814326663@qq.com
 */
public class QrCodePrester implements QrCodeBasePrester {
    private String TAG = QrCodePrester.class.getSimpleName();

    private QrCodeView codeView;

    public QrCodePrester(QrCodeView codeView) {
        this.codeView = codeView;
        this.codeView.setPresenter(this);
    }

    @Override
    public void onDestory() {
        codeView = null;
    }

    @Override
    public void CreateQrCode() {
        JSONObject userJson = HTApp.getInstance().getUserJson();
        JSONObject allobj = new JSONObject();
        allobj.put("codeType",2);
        JSONObject object =new JSONObject();
        object.put(HTConstant.JSON_KEY_HXID,HTApp.getInstance().getUsername());
        object.put(HTConstant.JSON_KEY_NICK,userJson.getString(HTConstant.JSON_KEY_NICK));
        object.put(HTConstant.JSON_KEY_TEL,userJson.getString(HTConstant.JSON_KEY_TEL));
        object.put(HTConstant.JSON_KEY_FXID,userJson.getString(HTConstant.JSON_KEY_FXID));
        object.put(HTConstant.JSON_KEY_SEX,userJson.getString(HTConstant.JSON_KEY_SEX));
        object.put(HTConstant.JSON_KEY_AVATAR,userJson.getString(HTConstant.JSON_KEY_AVATAR));
        object.put(HTConstant.JSON_KEY_PROVINCE,userJson.getString(HTConstant.JSON_KEY_PROVINCE));
        object.put(HTConstant.JSON_KEY_CITY,userJson.getString(HTConstant.JSON_KEY_CITY));
        object.put(HTConstant.JSON_KEY_SIGN,userJson.getString(HTConstant.JSON_KEY_SIGN));
        allobj.put("data",object.toJSONString());
        try {
            Bitmap bitmap = generateQRCode(allobj.toJSONString());
            codeView.showQrCode(bitmap);
        } catch (WriterException e) {
            codeView.showError(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void start() {

    }



    private Bitmap bitMatrix2Bitmap(BitMatrix matrix) {
        int w = matrix.getWidth();
        int h = matrix.getHeight();
        int[] rawData = new int[w * h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                int color = Color.WHITE;
                if (matrix.get(i, j)) {
                    color = Color.BLACK;
                }
                rawData[i + (j * w)] = color;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        bitmap.setPixels(rawData, 0, w, 0, 0, w, h);
        return bitmap;
    }

    private Bitmap generateQRCode(String content) throws WriterException {
            Log.d(TAG,"二维码:"+content);
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE,
                    500, 500);
            return bitMatrix2Bitmap(matrix);
    }
}
