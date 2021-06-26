package com.htmessage.yichat.acitivity.main;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.acitivity.SearchAllHistoryActivity;
import com.htmessage.yichat.acitivity.chat.group.GroupAddMembersActivity;
import com.htmessage.yichat.acitivity.friends.addfriend.AddFriendsPreActivity;
import com.htmessage.yichat.acitivity.main.contacts.ContactsFragment;
import com.htmessage.yichat.acitivity.main.conversation.ConversationFragment;
import com.htmessage.yichat.acitivity.main.profile.FragmentProfile;
import com.htmessage.yichat.manager.NotifierManager;
import com.htmessage.yichat.runtimepermissions.PermissionsManager;
import com.htmessage.yichat.runtimepermissions.PermissionsResultAction;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.LoggerUtils;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.update.login.LoginActivity;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.lang.reflect.Method;


/**
 * Created by huangfangyi on 2017/6/24.
 * qq 84543217
 */

public class MainActivity extends BaseActivity implements MainView {
    private TabLayout mTablayout;
    private NoAnimViewPager mViewPager;

    private String[] mTitles;
    private int[] drawabls = new int[]{R.drawable.tab_chat_bg, R.drawable.tab_contact_list_bg, R.drawable.tab_profile_bg};
    private TabLayout.Tab[] tabs;
    private Fragment[] fragments;
    //新消息角标
    private TextView unreadLabel;
    // 新好友申请消息角标
    public TextView unreadInvitionLable;
    public boolean isConflict = false;
    private android.app.AlertDialog.Builder exceptionBuilder;
    private boolean isConflictDialogShow;
    private MainPrestener mainPrestener;
    private boolean isCheckShow = false;
    private TextView tvTopName;
    public static final int REQUEST_CODE_SCAN = 10000;

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
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        tvTopName = this.findViewById(R.id.top_name);
        mTitles = new String[]{getString(R.string.app_chat), getString(R.string.address_book), getString(R.string.me)};
        tvTopName.setText(mTitles[0]);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_search:
                        startActivity(new Intent(MainActivity.this, SearchAllHistoryActivity.class));
                         break;
                    case R.id.item_add_friend:
                        startActivity(new Intent(MainActivity.this, AddFriendsPreActivity.class));
//                        new HTAlertDialog(MainActivity.this, "邀请好友", new String[]{"邀请微信好友", "邀请QQ好友"}).init(new HTAlertDialog.OnItemClickListner() {
//                            @Override
//                            public void onClick(int position) {
//                                switch (position) {
//                                    case 0:
//
//                                        WXWebpageObject webpage = new WXWebpageObject();
//                                        webpage.webpageUrl = "https://fir.im/355";
//                                        WXMediaMessage msg = new WXMediaMessage(webpage);
//                                        msg.title = "清聊----开心就用清聊";
//                                        msg.description = "我邀请你一起使用清聊";
//                                        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.app_logo_ql);
//                                        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 120, 120, true);
//                                        bmp.recycle();
//                                        msg.thumbData = Bitmap2Bytes(thumbBmp);
//
//                                        SendMessageToWX.Req req = new SendMessageToWX.Req();
//                                        req.transaction = buildTransaction("webpage");
//                                        req.message = msg;
//                                        req.scene = mTargetScene;
//                                        iwxapi.sendReq(req);
//                                        break;
//
//                                    case 1:
//                                        shareToQQ();
//                                        break;
//
//
//                                }
//                            }
//                        });
                        break;
                    case R.id.item_add_group:
                        startActivity(new Intent(MainActivity.this, GroupAddMembersActivity.class));

                        break;
                    case R.id.item_scan:
                        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_SCAN);
                        break;

                }


                return true;
            }
        });

        if (getIntent().getBooleanExtra(IMAction.ACTION_CONFLICT, false) && !isConflictDialogShow) {
           // ApiUtis.getInstance().
            showConflicDialog();

        }

        initViews();
        requestPermissions();
        mainPrestener.checkVersion(getVersionCode());
    }

    private Bundle params;

//    private void shareToQQ() {
//        params = new Bundle();
//        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
//        params.putString(QQShare.SHARE_TO_QQ_TITLE, "清聊----开心就用清聊");// 标题
//        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "我邀请你一起使用清聊");// 摘要
//        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "https://fir.im/355");// 内容地址
//        // params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");// 网络图片地址params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "应用名称");// 应用名称
//        // params.putString(QQShare.SHARE_TO_QQ_EXT_INT, "其它附加功能");
//        // 分享操作要在主线程中完成
//        ThreadManager.getMainHandler().post(new Runnable() {
//            @Override
//            public void run() {
//                mTencent.shareToQQ(MainActivity.this, params, new IUiListener() {
//                    @Override
//                    public void onComplete(Object o) {
//
//                    }
//
//                    @Override
//                    public void onError(UiError uiError) {
//
//                    }
//
//                    @Override
//                    public void onCancel() {
//
//                    }
//                });
//            }
//        });
//    }

//    private static byte[] Bitmap2Bytes(Bitmap bm) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        bm.recycle();
//        return baos.toByteArray();
//    }
//
//    private int mTargetScene = SendMessageToWX.Req.WXSceneSession;

//    private String buildTransaction(final String type) {
//        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
//    }
//

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @SuppressLint("RestrictedApi")
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
        //
        fragments = new Fragment[]{new ConversationFragment(), new ContactsFragment(), new FragmentProfile()};
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


    private View getBottomView(final int index, int drawableRes) {
        View view = getLayoutInflater().inflate(R.layout.widget_main_button, null);

        Button button = (Button) view.findViewById(R.id.button);
        button.setText(mTitles[index]);
        button.setCompoundDrawablesWithIntrinsicBounds(0, drawableRes, 0, 0);
        //  button.setCompoundDrawables(null, dr, null, null);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tabs[index] != null) {
                    tabs[index].select();
                    tvTopName.setText(mTitles[index]);
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
        LoggerUtils.d("showConflicDialog---1");
        isConflictDialogShow = true;
        String st = getResources().getString(R.string.Logoff_notification);
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (exceptionBuilder == null) {
                    exceptionBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
                }
                exceptionBuilder.setTitle(st);
                exceptionBuilder.setMessage(R.string.connect_conflict);
                exceptionBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        exceptionBuilder = null;
                        isConflictDialogShow = false;
                        NotifierManager.getInstance().cancel();

                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

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
    public void showUpdateDialog(String message, final String url, final boolean isForce) {
         if(isCheckShow){
             return;
         }
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
        isCheckShow = true;
        if ( isForce) {
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
                isCheckShow = false;
                if (isForce) {
                    HTApp.getInstance().logoutApp(0);
                }
            }
        });
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCheckShow = false;
                if (!isForce) {
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
            LoggerUtils.d("showConflicDialog---3");

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
    public void showInvitionCount(int count) {
        if (unreadInvitionLable == null) {
            return;
        }
        if (count > 0) {
               unreadInvitionLable.setText(String.valueOf(count));
            unreadInvitionLable.setVisibility(View.VISIBLE);
        } else {
            unreadInvitionLable.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                CommonUtils.handleQRcodeResult(MainActivity.this, content);


                // result.setText("扫描结果为：" + content);
            }
        }
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
}
