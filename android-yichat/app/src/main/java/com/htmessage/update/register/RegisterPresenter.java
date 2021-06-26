package com.htmessage.update.register;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.DateUtils;
import com.htmessage.sdk.utils.UploadFileUtils;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.R;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.Constant;
import com.htmessage.update.uitls.GifSizeFilter;
import com.soundcloud.android.crop.Crop;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import java.io.File;
import java.util.List;

/**
 * Created by huangfangyi on 2017/6/23.
 * qq 84543217
 */

public class RegisterPresenter implements RegisterContract.Presenter {
    private RegisterContract.View registerView;
    private String cropImagePath = null;
    private    static final   int PHOTO_REQUEST_TAKEPHOTO=0;
    private  static final    int PHOTO_REQUEST_CUT=1;

    private Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(registerView==null){
                return;
            }
            switch (msg.what){
                case 1000:
                    //注册成功
                    registerView.showToast(R.string.register_succ);
                    registerView.onRegisterSucc();
                    break;
                case 1001:
                    //注册失败
                    int resId=msg.arg1;
                    registerView.showToast(resId);
                    registerView.cancelDialog();
                    break;
                case 1002:
                    //

            }
        }
    };
    public RegisterPresenter(RegisterContract.View view) {
        this.registerView = view;
        this.registerView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void registerInServer(String nickName, String mobile, String password) {
        registerView.showDialog();
        if (cropImagePath != null && new File(cropImagePath).exists()) {
            uploadAvatar(nickName, password, mobile, new File(cropImagePath).getAbsolutePath());
        } else {
            register(nickName, password, mobile, null);
        }
    }


    private void register(String usernick, String password, String usertel, String avatarUrl) {

        JSONObject jsonObject=new JSONObject();
        jsonObject.put("mobile",usertel);
        jsonObject.put("nick",usernick);

        jsonObject.put("password",password);
        jsonObject.put("platform","1");
        if(!TextUtils.isEmpty(avatarUrl)){
            jsonObject.put("avatar",avatarUrl);
        }
        ApiUtis.getInstance().postJSON(jsonObject, Constant.URL_REGISTER, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
               String code=jsonObject.getString("code");

                Message message=handler.obtainMessage();
               if(code.equals("0")){
                   //注册成功
                   message.what=1000;
               }else if(code.equals("102")){
                  //手机号已被注册
                   message.what=1001;
                   //默认错误码有4个
                   message.arg1=R.string.register_duplicate;
               }else {
                   //手机号已被注册
                   message.what=1001;
                   //默认错误码有4个
                   message.arg1=R.string.register_fail;
               }

                message.sendToTarget();
            }

            @Override
            public void onFailure(int errorCode) {
                Message message=handler.obtainMessage();
                message.what=1001;
                message.arg1=errorCode;
                message.sendToTarget();

            }
        });



    }

    private void uploadAvatar(final String usernick, final String password, final String usertel, String filePath) {
        final String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        new UploadFileUtils(registerView.getBaseActivity(), fileName, filePath).asyncUploadFile(new UploadFileUtils.UploadCallBack() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {

            }

            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                final String url = HTConstant.baseImgUrl + fileName;
                registerView.getBaseActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        register(usernick, password, usertel, url);
                    }
                });
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                registerView.getBaseActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        registerView.cancelDialog();
                    }
                });
            }
        });
    }


    @Override
    public void result(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {

                case PHOTO_REQUEST_TAKEPHOTO:
                    if (intent != null) {
                         List<Uri> uris = Matisse.obtainResult(intent);
                        if(uris!=null&&uris.size()>0){
                            beginCrop(uris.get(0) );
                        }
                    }


                    break;
                case PHOTO_REQUEST_CUT:
                    Uri output = Crop.getOutput(intent);
                    registerView.showAvatar(output.getPath());
                    break;
            }
        } else {
            //裁剪失败
            if (requestCode == PHOTO_REQUEST_CUT) {
                cropImagePath = null;
                registerView.showAvatar(null);
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void selectAvatar() {

        if (!CommonUtils.checkPermission(registerView.getBaseActivity(), Manifest.permission.CAMERA)
                || !CommonUtils.checkPermission(registerView.getBaseActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                || !CommonUtils.checkPermission(registerView.getBaseActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            CommonUtils.showToastShort(registerView.getBaseActivity(), R.string.miss_permission_camera);
            registerView.getBaseActivity().requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            return;
        }
        Matisse.from(registerView.getBaseActivity())
                .choose(MimeType.ofImage())
                .countable(true)
                .maxSelectable(1)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(registerView.getBaseActivity().getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(PHOTO_REQUEST_TAKEPHOTO);
    }


    private void beginCrop(Uri inputUri) {
        cropImagePath = HTApp.getInstance().getImageDirFilePath() + "mini_" + DateUtils.getyyMMddTime(System.currentTimeMillis()) + ".png";
        Uri outputUri = Uri.fromFile(new File(cropImagePath));
        Crop.of(inputUri, outputUri).asSquare().start(registerView.getBaseActivity(), PHOTO_REQUEST_CUT);
    }
}
