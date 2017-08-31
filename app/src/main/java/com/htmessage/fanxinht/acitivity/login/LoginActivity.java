package com.htmessage.fanxinht.acitivity.login;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.runtimepermissions.PermissionsManager;
import com.htmessage.fanxinht.runtimepermissions.PermissionsResultAction;

/**
 * Login screen
 */
public class LoginActivity extends BaseActivity {
    private boolean isAuth = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setTitle(R.string.login_by_mobile);
        isAuth = getIntent().getBooleanExtra("isAuth", false);
        if (!isAuth) {
            hideBackView();
        } else {
            changeBackView(R.drawable.top_bar_back, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                }
            });
        }
        requestPermissions();
        LoginFragment loginFragment =
                (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (loginFragment == null) {
            // Create the fragment
            loginFragment = new LoginFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame, loginFragment);
            transaction.commit();
        }

        LoginPresenter presenter = new LoginPresenter(loginFragment);

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


}
