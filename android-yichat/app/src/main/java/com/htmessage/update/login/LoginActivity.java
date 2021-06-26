package com.htmessage.update.login;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;

import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.runtimepermissions.PermissionsManager;
import com.htmessage.yichat.runtimepermissions.PermissionsResultAction;
import com.htmessage.update.data.SettingsManager;
import com.htmessage.update.register.PreRegisterActivity;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;

/**
 * Login screen
 */
public class LoginActivity extends BaseActivity {

    LoginFragment loginFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setTitle(R.string.login_by_mobile);
        showRightTextView("注册", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, PreRegisterActivity.class));
            }
        });


        loginFragment= new LoginFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.contentFrame, loginFragment);
        transaction.commit();
        //被另一台设备顶下时，要给个提示语
        loginFragment.setArguments(getIntent().getExtras());
        LoginPresenter presenter = new LoginPresenter(loginFragment);

        boolean permission = (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE));
        if (permission) {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                SettingsManager.getInstance().savaDeviceId(tm.getDeviceId());
            }
        }

        //初始化第三方登录的工具类
        requestPermissions();

    }



    @TargetApi(23)
    private void requestPermissions() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
            }

            @Override
            public void onDenied(String permission) {
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       // Log.d("QQLoginWatcher--->4", data.toString());
        if (requestCode == Constants.REQUEST_LOGIN || requestCode == Constants.REQUEST_APPBAR) {
            loginFragment.showDialog("登录中");
            Tencent.onActivityResultData(requestCode, resultCode, data, loginFragment.loginWatcher);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
