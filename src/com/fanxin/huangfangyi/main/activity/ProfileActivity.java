package com.fanxin.huangfangyi.main.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fanxin.huangfangyi.DemoApplication;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.huangfangyi.main.utils.OkHttpManager;
import com.fanxin.huangfangyi.main.utils.Param;
import com.fanxin.huangfangyi.main.widget.FXAlertDialog;
import com.fanxin.huangfangyi.ui.BaseActivity;
import com.hyphenate.util.PathUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProfileActivity extends BaseActivity implements View.OnClickListener {
    private ImageView iv_avatar;
    private TextView tv_name;
    private TextView tv_fxid;
    private TextView tv_sex;
    private TextView tv_sign;
    private TextView tv_region;
    private String imageName;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private static final int UPDATE_FXID = 4;
    private static final int UPDATE_NICK = 5;
    private static final int UPDATE_SIGN = 6;
    private static final int UPDATE_REGION=7;
    private JSONObject userJson;
    //头像，昵称，凡信号是否发生变化
    private boolean hasChange=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fx_activity_myinfo);
        userJson = DemoApplication.getInstance().getUserJson();
        initView();
    }

    private void initView() {
        iv_avatar = (ImageView) this.findViewById(R.id.iv_avatar);
        tv_name = (TextView) this.findViewById(R.id.tv_name);
        tv_fxid = (TextView) this.findViewById(R.id.tv_fxid);
        tv_sex = (TextView) this.findViewById(R.id.tv_sex);
        tv_sign = (TextView) this.findViewById(R.id.tv_sign);
        tv_region = (TextView) this.findViewById(R.id.tv_region);
        String nick = userJson.getString(FXConstant.JSON_KEY_NICK);
        String fxid = userJson.getString(FXConstant.JSON_KEY_FXID);
        String sex = userJson.getString(FXConstant.JSON_KEY_SEX);
        String sign = userJson.getString(FXConstant.JSON_KEY_SIGN);
        String avatarUrl = FXConstant.URL_AVATAR + userJson.getString(FXConstant.JSON_KEY_AVATAR);
        Glide.with(ProfileActivity.this).load(avatarUrl).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(iv_avatar);
        tv_name.setText(nick);
        tv_region.setText(userJson.getString(FXConstant.JSON_KEY_PROVINCE)+" "+userJson.getString(FXConstant.JSON_KEY_CITY));
        if (TextUtils.isEmpty(fxid)) {
            tv_fxid.setText("未设置");

        } else {
            tv_fxid.setText(fxid);
        }
        tv_sex.setText(sex);
        if (TextUtils.isEmpty(sign)) {
            tv_sign.setText("未填写");
        } else {
            tv_sign.setText(sign);
        }
        //设置监听
        this.findViewById(R.id.re_avatar).setOnClickListener(this);
        this.findViewById(R.id.re_name).setOnClickListener(this);
        this.findViewById(R.id.re_fxid).setOnClickListener(this);
        this.findViewById(R.id.re_sex).setOnClickListener(this);
        this.findViewById(R.id.re_region).setOnClickListener(this);
        this.findViewById(R.id.re_sign).setOnClickListener(this);
        this.findViewById(R.id.re_qrcode).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.re_avatar:
                showPhotoDialog();
                break;
            case R.id.re_name:
                startActivityForResult(new Intent(ProfileActivity.this,
                        ProfileUpdateActivity.class).putExtra("type", ProfileUpdateActivity.TYPE_NICK).putExtra("default", userJson.getString(FXConstant.JSON_KEY_NICK)), UPDATE_NICK);
                break;
            case R.id.re_fxid:
                if (TextUtils.isEmpty(userJson.getString(FXConstant.JSON_KEY_FXID))
                        ) {
                    startActivityForResult(new Intent(ProfileActivity.this,
                            ProfileUpdateActivity.class).putExtra("type", ProfileUpdateActivity.TYPE_FXID), UPDATE_FXID);
                }
                break;
            case R.id.re_sex:
                showSexDialog();
                break;
            case R.id.re_region:
                setRegion();
                break;
            case R.id.re_sign:
                startActivityForResult(new Intent(ProfileActivity.this,
                        ProfileUpdateActivity.class).putExtra("type", ProfileUpdateActivity.TYPE_SIGN).putExtra("default", userJson.getString(FXConstant.JSON_KEY_SIGN)), UPDATE_SIGN);
                break;
            case R.id.re_qrcode:
                startActivity(new Intent(ProfileActivity.this,MyQrActivity.class));
                break;

        }

    }


    private void showPhotoDialog() {

        List<String> items = new ArrayList<String>();
        items.add("拍照");
        items.add("相册");
        FXAlertDialog fxAlertDialog = new FXAlertDialog(ProfileActivity.this, null, items);
        fxAlertDialog.init(new FXAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        imageName = getNowTime() + ".png";
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // 指定调用相机拍照后照片的储存路径
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(new File(PathUtil.getInstance().getImagePath(), imageName)));
                        startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                        break;
                    case 1:
                        imageName = getNowTime() + ".png";
                        Intent intent2 = new Intent(Intent.ACTION_PICK, null);
                        intent2.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent2, PHOTO_REQUEST_GALLERY);
                        break;
                }
            }
        });
    }

    private void showSexDialog() {
        String title = "性别";
        List<String> items = new ArrayList<String>();
        items.add("男");
        items.add("女");
        FXAlertDialog fxAlertDialog = new FXAlertDialog(ProfileActivity.this, title, items);
        fxAlertDialog.init(new FXAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        if (!userJson.getString(FXConstant.JSON_KEY_SEX).equals("男")) {

                            updateInServer(FXConstant.JSON_KEY_SEX, "男",false);
                        }
                        break;
                    case 1:
                        if (!userJson.getString(FXConstant.JSON_KEY_SEX).equals("女")) {

                            updateInServer(FXConstant.JSON_KEY_SEX, "女",false);
                        }
                        break;
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_REQUEST_TAKEPHOTO:

                    startPhotoZoom(
                            Uri.fromFile(new File(PathUtil.getInstance().getImagePath(), imageName)),
                            480);
                    break;

                case PHOTO_REQUEST_GALLERY:
                    if (data != null)
                        startPhotoZoom(data.getData(), 480);
                    break;

                case PHOTO_REQUEST_CUT:
                    updateInServer(FXConstant.JSON_KEY_AVATAR, imageName,false);
                    break;
                case UPDATE_FXID:
                    String fxid = data.getStringExtra("value");
                    if (fxid != null) {
                        tv_fxid.setText(fxid);
                        hasChange=true;
                    }

                    break;
                case UPDATE_NICK:
                    String nick = data.getStringExtra("value");
                    if (nick != null) {
                        tv_name.setText(nick);
                        hasChange=true;
                    }
                    break;
                case UPDATE_SIGN:
                    String sign = data.getStringExtra("value");
                    if (sign != null) {
                        tv_sign.setText(sign);

                    }
                    break;
                case UPDATE_REGION:
                    if (data != null) {
                        String province = data.getStringExtra("province");
                        String   city = data.getStringExtra("city");
                        boolean isRegion=true;
                        tv_region.setText(province+" "+city);
                        updateInServer(province,city,isRegion);
                    }
                    break;
            }
            super.onActivityResult(requestCode, resultCode, data);

        }
    }

    private void startPhotoZoom(Uri uri1, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri1, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", false);

        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(PathUtil.getInstance().getImagePath(), imageName)));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    private String getNowTime() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmssSS");
        return dateFormat.format(date);
    }

    private void updateInServer(final String key, final String value, final boolean isRegion) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            return;
        }
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在更新...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        List<Param> params = new ArrayList<Param>();

        if(!isRegion){

            params.add(new Param("key", key));
            params.add(new Param("value", value));
            params.add(new Param("hxid", userJson.getString(FXConstant.JSON_KEY_HXID)));

        }else {
            params.add(new Param("key", "province"));
            params.add(new Param("value", key));
            params.add(new Param("key2", "city"));
            params.add(new Param("value2", value));
            params.add(new Param("hxid", userJson.getString(FXConstant.JSON_KEY_HXID)));
        }

        List<File> files = new ArrayList<File>();
        File file=null;
        if (key == FXConstant.JSON_KEY_AVATAR) {
            file= new File( PathUtil.getInstance().getImagePath(), value);
            if (file.exists()) {
                files.add(file);
            }
        }

        final File finalFile = file;
        OkHttpManager.getInstance().post(params, files, FXConstant.URL_UPDATE, new OkHttpManager.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                progressDialog.dismiss();
                int code = jsonObject.getIntValue("code");
                if (code == 1000) {
                    //update ui
                    if (key.equals(FXConstant.JSON_KEY_AVATAR)) {
                        if(finalFile.exists()){
                            Bitmap bitmap = BitmapFactory.decodeFile(finalFile.getPath());
                            iv_avatar.setImageBitmap(bitmap);
                            hasChange=true;
                        }

                    }  else if (key.equals(FXConstant.JSON_KEY_SEX)) {
                        tv_sex.setText(value);
                    }
                    //
                    if(!isRegion){
                        userJson.put(key, value);
                    }else {
                        userJson.put(FXConstant.JSON_KEY_PROVINCE,key);
                        userJson.put(FXConstant.JSON_KEY_CITY,value);
                    }
                    DemoApplication.getInstance().setUserJson(userJson);
                } else {

                    Toast.makeText(getApplicationContext(), "更新失败,code:" + code, Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(String errorMsg) {
                progressDialog.dismiss();
            }
        });


    }
    @Override
    public  void back(View view){
        ckeckChange();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            ckeckChange();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void ckeckChange(){

        if( hasChange){
            setResult(RESULT_OK);
        }
        finish();
    }


    private void setRegion(){
        startActivityForResult(new Intent(ProfileActivity.this,
                RegionActivity.class), UPDATE_REGION);

    }
}
