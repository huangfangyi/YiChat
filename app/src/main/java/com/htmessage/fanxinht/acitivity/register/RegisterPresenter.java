package com.htmessage.fanxinht.acitivity.register;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.htmessage.sdk.utils.UploadFileUtils;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.utils.OkHttpUtils;
import com.htmessage.fanxinht.utils.Param;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangfangyi on 2017/6/23.
 * qq 84543217
 */

public class RegisterPresenter implements RegisterContract.Presenter {
    private RegisterContract.View registerView;
    private String cropImagePath = null;

    public RegisterPresenter(RegisterContract.View view) {
        registerView = view;
        registerView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void registerInServer(String nickName, String mobile, String password) {
        registerView.showDialog();
        if(cropImagePath!=null&&new File(cropImagePath).exists()){
            uploadAvatar(nickName, password, mobile, new File(cropImagePath).getAbsolutePath());
        }else {
            register(nickName, password, mobile, null);
        }
    }


    private void register(String usernick, String password, String usertel, String imageName) {
        registerView.showDialog();
        List<Param> params = new ArrayList<Param>();
        params.add(new Param("usertel", usertel));
        params.add(new Param("password", password));
        params.add(new Param("usernick", usernick));
        if (!TextUtils.isEmpty(imageName)) {
            params.add(new Param("avatar", imageName));
        }
        new OkHttpUtils(registerView.getBaseActivity()).post(params, HTConstant.URL_REGISTER, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int status = jsonObject.getInteger("code");
                switch (status) {
                    case 1:
                        JSONObject user = jsonObject.getJSONObject("user");
                        if (user != null) {
                            registerView.getBaseActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    registerView.cancelDialog();
                                    registerView.showToast(R.string.Registered_successfully);
                                    registerView.getBaseActivity().finish();
                                }
                            });

//                            HTClient.getInstance().register(user.getString(HTConstant.JSON_KEY_HXID), user.getString(HTConstant.JSON_KEY_PASSWORD), new HTClient.HTCallBack() {
//                                @Override
//                                public void onSuccess() {
//
//                                }
//
//                                @Override
//                                public void onError() {
//                                    registerView.getBaseActivity().runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            registerView.cancelDialog();
//                                            registerView.showToast(R.string.Registration_failed);
//
//                                        }
//                                    });
//
//                                }
//                            });
                        }
                        break;
                    case -1:
                        registerView.cancelDialog();
                        registerView.showToast(R.string.mobile_is_register);
                         break;
                    case -2:
                        registerView.cancelDialog();
                        registerView.showToast(R.string.Incorrect_phone_number_format);

                         break;
                    default:
                        registerView.cancelDialog();
                        registerView.showToast(R.string.Server_busy);
                         break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                registerView.cancelDialog();
                registerView.showToast(R.string.Server_busy);
             }
        });

    }

    private void uploadAvatar(final String usernick, final String password, final String usertel, String filePath) {
        final String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        new UploadFileUtils(registerView.getBaseActivity(), fileName, filePath).asyncUploadFile(new UploadFileUtils.a() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {

            }

            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                final String url = HTConstant.baseImgUrl + fileName;
                registerView.getBaseActivity(). runOnUiThread(new Runnable() {
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
                case RegisterContract.PHOTO_REQUEST_GALLERY:
                    if (intent != null)
                        beginCrop(intent.getData());

                    break;



                case RegisterContract.PHOTO_REQUEST_TAKEPHOTO:
                    beginCrop(Uri.fromFile(new File(registerView.getOriginImagePath())));
                    break;

                case RegisterContract.PHOTO_REQUEST_CUT:
                    Uri output = Crop.getOutput(intent);
                    Log.d("output---->",output.getPath());
                    registerView.showAvatar(output.getPath());
                    break;

            }
        }else {
            //裁剪失败
            if(requestCode==RegisterContract.PHOTO_REQUEST_CUT){
                cropImagePath=null;
                registerView.showAvatar(null);
            }

        }
    }

    private void beginCrop(Uri inputUri) {
        cropImagePath= HTApp.getInstance().getDirFilePath()  + "crop_" + System.currentTimeMillis() + ".png";
        Uri outputUri = Uri.fromFile(new File(cropImagePath));
        Crop.of(inputUri, outputUri).asSquare().start(registerView.getBaseActivity(), RegisterContract.PHOTO_REQUEST_CUT);
    }
}
