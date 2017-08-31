package com.htmessage.fanxinht.acitivity.main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.htmessage.sdk.client.HTClient;
import com.htmessage.fanxinht.IMAction;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;
import com.htmessage.fanxinht.acitivity.chat.group.GroupAddMembersActivity;
import com.htmessage.fanxinht.acitivity.addfriends.add.pre.AddFriendsPreActivity;
import com.htmessage.fanxinht.acitivity.login.LoginActivity;
import com.htmessage.fanxinht.acitivity.main.contacts.FragmentContacts;
import com.htmessage.fanxinht.acitivity.main.conversation.ConversationFragment;
import com.htmessage.fanxinht.acitivity.main.find.FragmentFind;
import com.htmessage.fanxinht.acitivity.main.profile.FragmentProfile;
import com.htmessage.fanxinht.runtimepermissions.PermissionsManager;
import com.htmessage.fanxinht.runtimepermissions.PermissionsResultAction;
import com.htmessage.fanxinht.widget.zxing.activity.CaptureActivity;

import java.lang.reflect.Method;

/**
 * Created by huangfangyi on 2017/6/24.
 * qq 84543217
 */

public class MainActivity extends BaseActivity implements MainView {
    private TabLayout mTablayout;
    private NoAnimViewPager mViewPager;
    private String[] mTitles;
    private int[] drawabls = new int[]{R.drawable.tab_chat_bg, R.drawable.tab_contact_list_bg, R.drawable.tab_find_bg, R.drawable.tab_profile_bg};
    private TabLayout.Tab[] tabs;
    private Fragment[] fragments;
    //新消息角标
    private TextView unreadLabel;
    // 新好友申请消息角标
    public TextView unreadInvitionLable;
    //朋友圈通知
    public TextView unreadFriendLable;

    // user logged into another device
    public boolean isConflict = false;
    private android.app.AlertDialog.Builder exceptionBuilder;
    private boolean isConflictDialogShow;
    private MainPrestener mainPrestener;

    @Override
    protected void onCreate(Bundle arg0) {
        if (arg0 != null && arg0.getBoolean("isConflict", false)) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        super.onCreate(arg0);
        setContentView(R.layout.activity_base_main);
        mainPrestener = new MainPrestener(this);
        mTitles = new String[]{getString(R.string.app_chat), getString(R.string.address_book), getString(R.string.bottom_find), getString(R.string.me)};
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_search:
                        Toast.makeText(getApplicationContext(), "waiting to do somethings", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.item_add_friend:
                        startActivity(new Intent(MainActivity.this, AddFriendsPreActivity.class));

                        break;
                    case R.id.item_add_group:
                        startActivity(new Intent(MainActivity.this, GroupAddMembersActivity.class));

                        break;
                    case R.id.item_scan:
                        startActivity(new Intent(MainActivity.this, CaptureActivity.class));
                        break;

                }


                return true;
            }
        });


        if (getIntent().getBooleanExtra(IMAction.ACTION_CONFLICT, false) && !isConflictDialogShow) {
            showConflicDialog();
        }

        initViews();

        requestPermissions();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }


    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        //解决menu item设置的图标不显示
        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    //   Out.print(getClass().getSimpleName() + "onMenuOpened...unable to set icons for overflow menu" + e);
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }


    private void initViews() {

        mTablayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (NoAnimViewPager) findViewById(R.id.viewPager);
        fragments = new Fragment[]{new ConversationFragment(), new FragmentContacts(), new FragmentFind(), new FragmentProfile()};
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {

                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }
        });

        mTablayout.setupWithViewPager(mViewPager);
        tabs = new TabLayout.Tab[mTitles.length];
        for (int i = 0; i < mTitles.length; i++) {
            tabs[i] = mTablayout.getTabAt(i);
            tabs[i].setCustomView(getBottomView(i, drawabls[i]));
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mainPrestener.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (exceptionBuilder != null) {
            exceptionBuilder.create().dismiss();
            exceptionBuilder = null;
            isConflictDialogShow = false;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        mainPrestener.checkVersion();
    }


    private View getBottomView(final int index, int drawableRes) {
        View view = getLayoutInflater().inflate(R.layout.widget_main_button, null);
        Button button = (Button) view.findViewById(R.id.button);
        button.setCompoundDrawablesWithIntrinsicBounds(0, drawableRes, 0, 0);
        //  button.setCompoundDrawables(null, dr, null, null);
        button.setText(mTitles[index]);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tabs[index] != null) {
                    tabs[index].select();
                }
            }
        });
        if (isCompatible(Build.VERSION_CODES.LOLLIPOP)) {
            button.setStateListAnimator(null);
        }
        TextView textView = (TextView) view.findViewById(R.id.unread_msg_number);
        if (index == 0) {
            unreadLabel = textView;
        } else if (index == 1) {
            unreadInvitionLable = textView;
        }else if(index == 2){
            unreadFriendLable= textView;
        }
        return view;
    }


    @Override
    public void setPresenter(MainPrestener presenter) {
        mainPrestener = presenter;
    }

    @Override
    public Activity getBaseActivity() {
        return this;
    }

    @Override
    public void showConflicDialog() {
        isConflictDialogShow = true;
        //HTClientHelper.getInstance().logout(null);
        String st = getResources().getString(R.string.Logoff_notification);
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (exceptionBuilder == null)
                    exceptionBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
                exceptionBuilder.setTitle(st);
                exceptionBuilder.setMessage(R.string.connect_conflict);
                exceptionBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        exceptionBuilder = null;
                        isConflictDialogShow = false;
                        HTApp.getInstance().setUserJson(null);
                        finish();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    }
                });
                exceptionBuilder.setCancelable(false);
                exceptionBuilder.show();
                isConflict = true;
            } catch (Exception e) {
                Log.e(TAG, "---------color conflictBuilder error" + e.getMessage());
            }
        }
    }


    @Override
    public void showUpdateDialog(String message, final String url, final String isForce) {
        String title = getString(R.string.has_update);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View dialogView = View.inflate(MainActivity.this, R.layout.layout_alert_dialog_delete, null);
        TextView tv_delete_people = (TextView) dialogView.findViewById(R.id.tv_delete_people);
        View view_line_dialog = dialogView.findViewById(R.id.view_line_dialog);
        TextView tv_delete_title = (TextView) dialogView.findViewById(R.id.tv_delete_title);
        TextView tv_cancle = (TextView) dialogView.findViewById(R.id.tv_cancle);
        TextView tv_ok = (TextView) dialogView.findViewById(R.id.tv_ok);
        tv_delete_title.setText(title);
        tv_delete_people.setText(message);
        tv_cancle.setText(R.string.update_later);
        tv_ok.setText(R.string.update_now);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.show();
        if (isForce.equals("1")) {
            view_line_dialog.setVisibility(View.GONE);
            tv_cancle.setVisibility(View.GONE);
            tv_ok.setText(R.string.update_has);
            dialog.setCancelable(false);//点击屏幕外不取消  返回键也没用
            dialog.setCanceledOnTouchOutside(false); //点击屏幕外取消,返回键有用
        } else {
            dialog.setCancelable(true);//点击屏幕外取消  返回键也没用
            dialog.setCanceledOnTouchOutside(true); //点击屏幕外取消,返回键有用
        }
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (isForce.equals("1")) {
                    logout();
                }
            }
        });
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isForce.equals("1")) {
                    dialog.dismiss();
                }
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(url);
                intent.setData(uri);
                MainActivity.this.startActivity(intent);

            }
        });

    }

    @Override
    public void onUnReadMsgs(int count) {
        if (unreadLabel == null) {
            return;
        }
        if (count > 0) {

            unreadLabel.setText(String.valueOf(count));
            unreadLabel.setVisibility(View.VISIBLE);
        } else {
            unreadLabel.setVisibility(View.INVISIBLE);
        }

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra(IMAction.ACTION_CONFLICT, false) && !isConflictDialogShow) {
            showConflicDialog();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }


    private void logout() {
        HTClient.getInstance().logout(new HTClient.HTCallBack() {
            @Override
            public void onSuccess() {
                HTApp.getInstance().setUserJson(null);
                HTApp.getInstance().finishActivities();
                finish();
            }

            @Override
            public void onError() {
                HTApp.getInstance().setUserJson(null);
                HTApp.getInstance().finishActivities();
                finish();
            }
        });
    }


    @TargetApi(23)
    private void requestPermissions() {
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this, new PermissionsResultAction() {
            @Override
            public void onGranted() {
//				Toast.makeText(MainActivity.this, "All permissions have been granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDenied(String permission) {
                //Toast.makeText(MainActivity.this, "Permission " + permission + " has been denied", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void showInvitionCount(int count) {
        if (unreadInvitionLable == null) {
            return;
        }
        if (count > 0) {
            //   unreadInvitionLable.setText(String.valueOf(count));
            unreadInvitionLable.setVisibility(View.VISIBLE);
        } else {
            unreadInvitionLable.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onNewMonmentsNotice(int count) {
        //朋友圈
        if (unreadFriendLable ==null){
            return;
        }
        if (count > 0) {
            unreadFriendLable.setText(String.valueOf(count));
            unreadFriendLable.setVisibility(View.VISIBLE);
        } else {
            unreadFriendLable.setVisibility(View.INVISIBLE);
        }
    }




}
