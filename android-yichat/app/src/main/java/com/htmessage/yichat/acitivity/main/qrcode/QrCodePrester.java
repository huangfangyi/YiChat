package com.htmessage.yichat.acitivity.main.qrcode;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.htmessage.update.data.UserManager;
import com.htmessage.update.uitls.AESUtils;

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
         JSONObject allobj = new JSONObject();
        allobj.put("type",2);
        JSONObject object =new JSONObject();
        object.put("userId",UserManager.get().getMyUserId());
        allobj.put("data",object);
         try {
            Bitmap bitmap = generateQRCode(new AESUtils("A286D372M63HFUQW").encryptData(allobj.toJSONString()));
            codeView.showQrCode(bitmap);
        } catch (WriterException e) {
            codeView.showError(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
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
