package com.htmessage.fanxinht.acitivity.main.profile.info.profile;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
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
import com.htmessage.fanxinht.IMAction;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.utils.OkHttpUtils;
import com.htmessage.fanxinht.utils.Param;
import com.htmessage.fanxinht.widget.HTAlertDialog;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 项目名称：HTOpen
 * 类描述：ProfilePrester 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/7 10:49
 * 邮箱:814326663@qq.com
 */
public class ProfilePrester implements ProfileBasePrester {
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private static final int UPDATE_REGION = 7;
    private String imageName;
    private String dirPath;
    private JSONObject userJson;
    private ProfileView profileView;
    private InfoChangedListener listener;

    public ProfilePrester(ProfileView profileView) {
        this.profileView = profileView;
        this.profileView.setPresenter(this);
        dirPath = HTApp.getInstance().getDirFilePath();
        userJson =  profileView.getUserJson();
    }

    @Override
    public void resgisteRecivier() {
        IntentFilter intent = new IntentFilter(IMAction.ACTION_UPDATE_INFO);
        listener = new InfoChangedListener();
        LocalBroadcastManager.getInstance(profileView.getBaseContext()).registerReceiver(listener, intent);
    }

    private void updateInfo(final String key, final String value) {
        if (TextUtils.isEmpty(key) && TextUtils.isEmpty(value)){
            return;
        }
        final Dialog progressDialog = HTApp.getInstance().createLoadingDialog(profileView.getBaseContext(), profileView.getBaseContext().getString(R.string.are_uploading));
        progressDialog.show();
        List<Param> params = new ArrayList<Param>();
        params.add(new Param(key, value));
        params.add(new Param("userId", HTApp.getInstance().getUsername()));
        new OkHttpUtils(profileView.getBaseContext()).post(params, HTConstant.URL_UPDATE, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                progressDialog.dismiss();
                int code = jsonObject.getIntValue("code");
                if (code == 1) {
                    //update ui
                    if (key.equals(HTConstant.JSON_KEY_SEX)) {
                        switch (value) {
                            case "1":
                                profileView.onSexUpdate(R.string.male,true);
                                break;
                            case "0":
                                profileView.onSexUpdate(R.string.female,true);
                                break;
                        }
                    }
                    userJson.put(key, value);
                    HTApp.getInstance().setUserJson(userJson);
                    profileView.onUpdateSuccess(profileView.getBaseContext().getString(R.string.update_success));
                } else {
                    profileView.onUpdateFailed(profileView.getBaseContext().getString(R.string.upload_failed)+code);
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                profileView.onUpdateFailed(errorMsg);
                progressDialog.dismiss();
            }
        });

    }

    private void updateRegion(final String province,final String city) {
        if (TextUtils.isEmpty(province) && TextUtils.isEmpty(city)){
            return;
        }
        final Dialog progressDialog = HTApp.getInstance().createLoadingDialog(profileView.getBaseContext(), profileView.getBaseContext().getString(R.string.are_uploading));
        progressDialog.show();
        final List<Param> params = new ArrayList<Param>();
        params.add(new Param(HTConstant.JSON_KEY_PROVINCE, province));
        params.add(new Param(HTConstant.JSON_KEY_CITY, city));
        params.add(new Param("userId", HTApp.getInstance().getUsername()));
        new OkHttpUtils(profileView.getBaseContext()).post(params, HTConstant.URL_UPDATE, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                progressDialog.dismiss();
                int code = jsonObject.getIntValue("code");
                if (code == 1) {
                    //update ui
                    userJson.put(HTConstant.JSON_KEY_PROVINCE, province);
                    userJson.put(HTConstant.JSON_KEY_CITY, city);
                    HTApp.getInstance().setUserJson(userJson);
                    profileView.onRegionUpdate(province +" "+city,true);
                    profileView.onUpdateSuccess(profileView.getBaseContext().getString(R.string.update_success));
                } else {
                    profileView.onUpdateFailed(profileView.getBaseContext().getString(R.string.upload_failed) + code);
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                profileView.onUpdateFailed(errorMsg);
                progressDialog.dismiss();
            }
        });
    }

    private void updateAvatar(final String key,final String value){
        if (TextUtils.isEmpty(key) && TextUtils.isEmpty(value)){
            return;
        }
        final Dialog progressDialog = HTApp.getInstance().createLoadingDialog(profileView.getBaseContext(), profileView.getBaseContext().getString(R.string.are_uploading));
        progressDialog.show();
        final String fileName = value.substring(value.lastIndexOf("/") + 1);
        new UploadFileUtils(profileView.getBaseContext(), fileName, value).asyncUploadFile(new UploadFileUtils.a() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {

            }

            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                final String url = HTConstant.baseImgUrl + fileName;
                profileView.getBaseActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UpLoadAvator(value, key, url, progressDialog);
                    }
                });
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                profileView.getBaseActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }
    private void UpLoadAvator(final String filePath, final String key, final String value, final Dialog progressDialog) {
        List<Param> params = new ArrayList<Param>();
        params.add(new Param(key, value));
        new OkHttpUtils(profileView.getBaseContext()).post(params, HTConstant.URL_UPDATE, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                progressDialog.dismiss();
                int code = jsonObject.getIntValue("code");
                switch (code) {
                    case 1:
                        if (key.equals(HTConstant.JSON_KEY_AVATAR)) {
                            userJson.put(HTConstant.JSON_KEY_AVATAR, value);
                            HTApp.getInstance().setUserJson(userJson);
                            profileView.onAvatarUpdate(filePath,true);
                            LocalBroadcastManager.getInstance(profileView.getBaseContext()).sendBroadcast(new Intent(IMAction.ACTION_UPDATE_INFO).putExtra(HTConstant.JSON_KEY_AVATAR, value).putExtra(HTConstant.KEY_CHANGE_TYPE, HTConstant.JSON_KEY_AVATAR));
                        }
                        profileView.onUpdateSuccess(profileView.getBaseContext().getString(R.string.update_success));
                        break;
                    default:
                        String info = jsonObject.getString("info");
                        profileView.onUpdateFailed(info);
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                profileView.onUpdateFailed(errorMsg);
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void showPhotoDialog() {
        HTAlertDialog HTAlertDialog = new HTAlertDialog(profileView.getBaseContext(), null, new String[]{profileView.getBaseContext().getString(R.string.attach_take_pic), profileView.getBaseContext().getString(R.string.image_manager)});
        HTAlertDialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        imageName = getNowTime() + ".png";
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // 指定调用相机拍照后照片的储存路径
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(new File(dirPath, imageName)));
                        profileView.getBaseActivity().startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                        break;
                    case 1:
                        imageName = getNowTime() + ".png";
                        Crop.pickImage(profileView.getBaseActivity(), PHOTO_REQUEST_GALLERY);
                        break;
                }
            }
        });
    }

    @Override
    public void showSexDialog() {
        String title = profileView.getBaseContext().getString(R.string.sex);
        HTAlertDialog HTAlertDialog = new HTAlertDialog(profileView.getBaseContext(), title, new String[]{profileView.getBaseContext().getString(R.string.male), profileView.getBaseContext().getString(R.string.female)});
        HTAlertDialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        updateInfo(HTConstant.JSON_KEY_SEX, "1");
                        break;
                    case 1:
                        updateInfo(HTConstant.JSON_KEY_SEX, "0");
                        break;
                }
            }
        });
    }

    @Override
    public void result(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PHOTO_REQUEST_TAKEPHOTO:
                    beginCrop(Uri.fromFile(new File(dirPath, imageName)));
                    break;

                case PHOTO_REQUEST_GALLERY:
                    if (intent != null)
                        beginCrop(intent.getData());
                    break;

                case PHOTO_REQUEST_CUT:
                    Uri output = Crop.getOutput(intent);
                    updateAvatar(HTConstant.JSON_KEY_AVATAR, output.getPath());
                    break;
                case UPDATE_REGION:
                    Log.d("slj","-----更新地区:"+UPDATE_REGION);
                    if (intent != null) {
                        String province = intent.getStringExtra("province");
                        String city = intent.getStringExtra("city");
                        updateRegion(province, city);
                    }
                    break;
            }
        }
    }
    private void unRegisterReciver(){
        if (listener != null){
            LocalBroadcastManager.getInstance(profileView.getBaseContext()).unregisterReceiver(listener);
        }
    }
    @Override
    public void onDestory() {
        unRegisterReciver();
        profileView = null;
    }

    @Override
    public void start() {

    }


    private class InfoChangedListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (IMAction.ACTION_UPDATE_INFO.equals(intent.getAction())) {
                String type = intent.getStringExtra(HTConstant.KEY_CHANGE_TYPE);
                if (HTConstant.JSON_KEY_SIGN.equals(type)) {
                    String sign = intent.getStringExtra(HTConstant.JSON_KEY_SIGN);
                    profileView.onSignUpdate(sign,true);
                } else if (HTConstant.JSON_KEY_FXID.equals(type)) {
                    String fxid = intent.getStringExtra(HTConstant.JSON_KEY_FXID);
                    profileView.onFxidUpdate(fxid,true);
                } else if (HTConstant.JSON_KEY_NICK.equals(type)) {
                    String nick = intent.getStringExtra(HTConstant.JSON_KEY_NICK);
                    profileView.onNickUpdate(nick,true);
                }
            }
        }
    }
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(dirPath, imageName));
        Crop.of(source, destination).asSquare().start(profileView.getBaseActivity(), PHOTO_REQUEST_CUT);
    }

    private String getNowTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
        return dateFormat.format(date);
    }
}
