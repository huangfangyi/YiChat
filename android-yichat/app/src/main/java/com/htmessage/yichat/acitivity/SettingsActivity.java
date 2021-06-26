package com.htmessage.yichat.acitivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.main.profile.NewMsgNoticeSettingActivity;
import com.htmessage.yichat.acitivity.password.PasswordResetActivity;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.widget.HTAlertDialog;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.update.Constant;


/**
 * Created by huangfangyi on 2016/7/4.\
 * QQ:84543217
 */
public class SettingsActivity extends BaseActivity implements View.OnClickListener {


    private Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1000:
                    CommonUtils.cencelDialog();
                    JSONObject data= (JSONObject) msg.obj;


              showUpdateDialog(SettingsActivity.this, getString(R.string.has_update), data.getString("memo"), data.getString("downloadUrl"));

                    break;
                case 1001:
                    CommonUtils.cencelDialog();
                   int resId=msg.arg1;
                    Toast.makeText(SettingsActivity.this,resId,Toast.LENGTH_SHORT).show();
                    break;
            }


        }
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.settings);
        initView();
    }

    private void initView() {
        this.findViewById(R.id.ll_change).setOnClickListener(this);
        this.findViewById(R.id.rl_logout).setOnClickListener(this);
        this.findViewById(R.id.rl_update).setOnClickListener(this);
        this.findViewById(R.id.re_resetpassword).setOnClickListener(this);

        TextView tv_version=findViewById(R.id.tv_version);
        tv_version.setText("当前版本号:"+getVersionCode()+" ("+getVersion()+")");
    }





    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_change: //新消息设置
                startActivity(new Intent(SettingsActivity.this, NewMsgNoticeSettingActivity.class));
                break;

            case R.id.rl_logout:
                 logOutDialog();
                break;

            case R.id.rl_update: //检查更新
                getAppUpdate();
                break;
             case R.id.re_resetpassword: //重置密码
                startActivity(new Intent(SettingsActivity.this, PasswordResetActivity.class).putExtra("isReset", true));
                break;

        }

    }

    private void logOutDialog() {
        HTAlertDialog dialog = new HTAlertDialog(this, null, new String[]{getString(R.string.exit_this_user)});
        dialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        logout();
                        break;
//                    case 1:
//
//                        break;
                }
            }
        });
    }


    void logout() {
        final ProgressDialog pd = new ProgressDialog(SettingsActivity.this);
        String st = getResources().getString(R.string.Are_logged_out);
        pd.setMessage(st);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        pd.dismiss();

        HTApp.getInstance().logoutApp(0);
//        HTClient.getInstance().logout(new HTClient.HTCallBack() {
//            @Override
//            public void onSuccess() {
//
//            }
//
//            @Override
//            public void onError() {
//                pd.dismiss();
//                CommonUtils.showToastShort(SettingsActivity.this, R.string.logout_failed);
//            }
//        });
    }

    public void back(View view) {
        finish();
    }

    /**
     * 获取VersionCode
     *
     * @return 当前应用的VersionCode
     */
    public int getVersionCode() {
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     *
     *
     * @return 当前应用的VersionCode
     */
    public String getVersion() {
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            String version = String.valueOf(info.versionName);
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    private void getAppUpdate() {
        CommonUtils.showDialogNumal(SettingsActivity.this,"");
        JSONObject data=new JSONObject();
        data.put("type",0);
        data.put("currentVersion",getVersionCode());
        ApiUtis.getInstance().postJSON(data, Constant.URL_VERSION, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(handler==null)
                    return;

                String code=jsonObject.getString("code");
                if("0".equals(code)){
                    JSONObject data=jsonObject.getJSONObject("data");

                    int version=data.getInteger("version");
                    if(version>getVersionCode()){

//                    data": {
//                    "version": "123123131",
//                            "memo": "12313131",
//                            "updateStatus": 0,
//                            "downloadUrl": "123131"
//                },

                        Message message=handler.obtainMessage();
                        message.what=1000;
                        message.obj=data;
                        message.sendToTarget();
                    }else {
                        Message message=handler.obtainMessage();
                        message.what=1001;
                        message.arg1=R.string.just_new_version;
                        message.sendToTarget();
                    }

                }else {
                    Message message=handler.obtainMessage();
                    message.what=1001;
                    message.arg1=R.string.api_error_5;
                    message.sendToTarget();

                }
            }

            @Override
            public void onFailure(int errorCode) {
                if(handler==null)
                    return;

                Message message=handler.obtainMessage();
                message.what=1001;
                message.arg1=R.string.api_error_5;
                message.sendToTarget();

            }
        });



//        final ProgressDialog dialog = new ProgressDialog(SettingsActivity.this);
//        dialog.setMessage(getString(R.string.are_checking_update));
//        dialog.show();
//        final String version = getVersionCode();
//        List<Param> params = new ArrayList<>();
//        params.add(new Param("system", "0"));
//        params.add(new Param("vid", version));
//        new OkHttpUtils(SettingsActivity.this).post(params, HTConstant.URL_CHECK_UPDATE, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                if (jsonObject != null) {
//                    String serviceVersion = jsonObject.getString("newVersion");
//                    String url = jsonObject.getString("url");
//                    String info = jsonObject.getString("info");
//                    String statue = jsonObject.getString("status");
//                    if(TextUtils.isEmpty(serviceVersion)){
//
//                        CommonUtils.showToastShort(SettingsActivity.this, R.string.just_new_version);
//                        return;
//                    }
//                    if (!version.equals(serviceVersion) && (Integer.valueOf(version) < Integer.valueOf(serviceVersion))) {
//                        showUpdateDialog(SettingsActivity.this, getString(R.string.has_update), info, url);
//                    } else {
//                        CommonUtils.showToastShort(SettingsActivity.this, R.string.just_new_version);
//                    }
//                }
//                dialog.dismiss();
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                dialog.dismiss();
//            }
//        });
    }

    private void showUpdateDialog(final Context context, String title, String message, final String url) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = View.inflate(context, R.layout.layout_alert_dialog_delete, null);
        TextView tv_delete_people = (TextView) dialogView.findViewById(R.id.tv_delete_people);
        TextView tv_delete_title = (TextView) dialogView.findViewById(R.id.tv_delete_title);
        TextView tv_cancle = (TextView) dialogView.findViewById(R.id.tv_cancle);
        TextView tv_ok = (TextView) dialogView.findViewById(R.id.tv_ok);
        tv_delete_title.setText(title);
        tv_delete_people.setText(message);
        tv_cancle.setText(R.string.update_later);
        tv_ok.setText(R.string.update_now);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.show();
//        dialog.setCancelable(false);//点击屏幕外不取消  返回键也没用
//        dialog.setCanceledOnTouchOutside(false); //点击屏幕外取消,返回键有用
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(url);
                intent.setData(uri);
                SettingsActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler=null;
    }
}
