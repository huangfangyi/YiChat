package com.htmessage.yichat.acitivity.main.profile.info.profile;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.htmessage.update.Constant;
import com.htmessage.update.data.UserManager;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.widget.HTAlertDialog;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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
    private String imageName;
    private String dirPath;
    private JSONObject userJson;
    private ProfileView profileView;
    private InfoChangedListener listener;

    public ProfilePrester(ProfileView profileView) {
        this.profileView = profileView;
        this.profileView.setPresenter(this);
        dirPath = HTApp.getInstance().getImageDirFilePath();
        userJson = UserManager.get().getMyUser();
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (profileView == null) {
                return;
            }

            switch (msg.what) {
                case 1000:
                    //更新头像
                    CommonUtils.cencelDialog();

                    String remotPath = (String) msg.obj;
                    profileView.onAvatarUpdate(remotPath, true);
                    profileView.showToast(R.string.update_success);

                    break;
                case 1001:
                    //更新失败
                    CommonUtils.cencelDialog();

                    int resId = msg.arg1;
                    profileView.showToast(resId);
                    break;
                case 1002:
                    //更新性别
                    CommonUtils.cencelDialog();
                    profileView.showToast(R.string.update_success);

                    String gender = (String) msg.obj;
                    switch (gender) {
                        case "1":
                            profileView.onSexUpdate(R.string.male, true);
                            break;
                        case "0":
                            profileView.onSexUpdate(R.string.female, true);
                            break;
                    }

                    break;
            }
        }
    };

    @Override
    public void resgisteRecivier() {
        IntentFilter intent = new IntentFilter(IMAction.ACTION_UPDATE_INFO);
        listener = new InfoChangedListener();
        LocalBroadcastManager.getInstance(profileView.getBaseContext()).registerReceiver(listener, intent);
    }

    private void updateInfo(final String key, final String value) {
        if (TextUtils.isEmpty(key) && TextUtils.isEmpty(value)) {
            return;
        }

        CommonUtils.showDialogNumal(profileView.getBaseContext(), profileView.getBaseContext().getString(R.string.are_uploading));
        JSONObject data = new JSONObject();
        data.put(key, value);
        ApiUtis.getInstance().postJSON(data, Constant.URL_INFO_UPDATE, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONObject userJSON = UserManager.get().getMyUser();
                    userJSON.put(key, value);
                    UserManager.get().saveMyUser(userJSON);
                    Message message = handler.obtainMessage();
                    message.what = 1002;
                    message.obj = value;
                    message.sendToTarget();

                } else {
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.arg1 = R.string.update_fail;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.arg1 = errorCode;
                message.sendToTarget();
            }
        });


    }


    private void updateAvatar(final String key, final String value) {
        if (TextUtils.isEmpty(key) && TextUtils.isEmpty(value)) {
            return;
        }
        CommonUtils.showDialogNumal(profileView.getBaseContext(), profileView.getBaseContext().getString(R.string.are_uploading));
        final String fileName = value.substring(value.lastIndexOf("/") + 1);
        new UploadFileUtils(profileView.getBaseContext(), fileName, value).asyncUploadFile(new UploadFileUtils.UploadCallBack() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {

            }

            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                final String url = HTConstant.baseImgUrl + fileName;
                profileView.getBaseActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        UpLoadAvator(value, key, url);
                    }
                });
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                profileView.getBaseActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CommonUtils.cencelDialog();
                    }
                });
            }
        });
    }

    private void UpLoadAvator(final String filePath, final String key, final String value) {
        JSONObject data = new JSONObject();
        data.put(key, value);
        ApiUtis.getInstance().postJSON(data, Constant.URL_INFO_UPDATE, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    Log.d("value--->",value);
                    Log.d("value--->11",filePath);
                    JSONObject userJSON = UserManager.get().getMyUser();
                    userJSON.put(key, value);
                    UserManager.get().saveMyUser(userJSON);
                    Message message = handler.obtainMessage();
                    message.what = 1000;
                    message.obj = value;
                    message.sendToTarget();

                } else {
                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.arg1 = R.string.update_fail;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.arg1 = errorCode;
                message.sendToTarget();
            }
        });



    }

    @Override
    public void showPhotoDialog() {
        final Activity activity = profileView.getBaseActivity();
        HTAlertDialog dialog = new HTAlertDialog(activity, null, new String[]{profileView.getBaseContext().getString(R.string.attach_take_pic), profileView.getBaseContext().getString(R.string.image_manager)});
        dialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        if (!CommonUtils.checkPermission(activity, Manifest.permission.CAMERA)) {
                            CommonUtils.showToastShort(activity, R.string.open_camera_permission);
                            return;
                        }
                        imageName = getNowTime() + ".png";
                        File file = new File(dirPath, imageName);
                        Uri imgUri = null;
                        if (Build.VERSION.SDK_INT >= 24) {
                            //兼容android7.0 使用共享文件的形式
                            ContentValues contentValues = new ContentValues(1);
                            contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                            imgUri = activity.getApplication().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                        } else {
                            imgUri = Uri.fromFile(file);
                        }
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // 指定调用相机拍照后照片的储存路径
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                        activity.startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                        break;
                    case 1:
                        if (!CommonUtils.checkPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) || !CommonUtils.checkPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            CommonUtils.showToastShort(activity, R.string.open_sd_permission);
                            return;
                        }
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
        HTAlertDialog dialog = new HTAlertDialog(profileView.getBaseContext(), title, new String[]{profileView.getBaseContext().getString(R.string.male), profileView.getBaseContext().getString(R.string.female)});
        dialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        updateInfo("gender", "1");
                        break;
                    case 1:
                        updateInfo("gender", "0");
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

            }
        }
    }

    private void unRegisterReciver() {
        if (listener != null) {
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
                String type = intent.getStringExtra("type");
                 if ("appId".equals(type)) {
                    String appId = intent.getStringExtra("appId");
                    profileView.onFxidUpdate(appId, true);
                } else if (HTConstant.JSON_KEY_NICK.equals(type)) {
                    String nick = intent.getStringExtra(HTConstant.JSON_KEY_NICK);
                    profileView.onNickUpdate(nick, true);
                }
            }
        }
    }

    private void beginCrop(Uri source) {
        if (TextUtils.isEmpty(imageName)) {

            imageName = getNowTime() + ".png";
        }
        Uri destination = Uri.fromFile(new File(dirPath, imageName));
        Crop.of(source, destination).asSquare().start(profileView.getBaseActivity(), PHOTO_REQUEST_CUT);
    }

    private String getNowTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return dateFormat.format(date);
    }
}
