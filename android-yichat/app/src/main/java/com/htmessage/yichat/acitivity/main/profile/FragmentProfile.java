package com.htmessage.yichat.acitivity.main.profile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.SettingsActivity;
import com.htmessage.yichat.acitivity.chat.file.util.MD5;
import com.htmessage.yichat.acitivity.main.MainActivity;
import com.htmessage.yichat.acitivity.main.profile.info.profile.ProfileActivity;
import com.htmessage.yichat.acitivity.main.qrcode.MyQrActivity;
import com.htmessage.yichat.acitivity.main.wallet.WalletActivity;
import com.htmessage.update.data.UserManager;
import com.htmessage.yichat.acitivity.moments.MomentsActivity;
import com.htmessage.yichat.acitivity.moments.MomentsFriendActivity;
 import com.yzq.zxinglibrary.android.CaptureActivity;


public class FragmentProfile extends Fragment implements View.OnClickListener {
    private InfoChangedListener listener;
    private ImageView ivAvatar;
    private TextView tvNick;
    private TextView tvFxid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();
        setListener();
        initView();
    }

    private void getData() {
        IntentFilter intent = new IntentFilter(IMAction.ACTION_UPDATE_INFO);
        listener = new InfoChangedListener();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(listener, intent);

    }

    private void initView() {
        ivAvatar = (ImageView) getView().findViewById(R.id.iv_avatar);
        tvNick = (TextView) getView().findViewById(R.id.tv_name);
        tvFxid = (TextView) getView().findViewById(R.id.tv_fxid);
        UserManager.get().loadUserAvatar(getContext(),UserManager.get().getMyAvatar(),ivAvatar);
        tvNick.setText(UserManager.get().getMyNick());
        if(UserManager.get().getMyUser()!=null){
            String appId=UserManager.get().getMyUser().getString("appId");
            tvFxid.setText("appId:"+(TextUtils.isEmpty(appId)?getString(R.string.not_set):appId));
        }


    }

    private void setListener() {
        getView().findViewById(R.id.re_myinfo).setOnClickListener(this);
        getView().findViewById(R.id.re_setting).setOnClickListener(this);
         getView().findViewById(R.id.rl_qrcode).setOnClickListener(this);
        getView().findViewById(R.id.rl_call_us).setOnClickListener(this);
         getView().findViewById(R.id.rl_wallet).setOnClickListener(this);
        getView().findViewById(R.id.iv_code).setOnClickListener(this);
        getView().findViewById(R.id.rl_friends).setOnClickListener(this);
        getView().findViewById(R.id.rl_xiangce).setOnClickListener(this);
      //  getView().findViewById(R.id.rl_wallet_old).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.re_myinfo:
                startActivity(new Intent(getActivity(), ProfileActivity.class));
                break;

            case R.id.re_setting:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;


            case R.id.rl_qrcode: //扫一扫
                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                getActivity().startActivityForResult(intent, MainActivity.REQUEST_CODE_SCAN);
                break;

//            case R.id.rl_wallet_old:
//                 JrmfWalletClient.intentWallet(getActivity(), HTApp.getInstance().getUsername(), getThirdToken(),HTApp.getInstance().getUserNick(),
//                         UserManager.get().getMyAvatar());
//                // startActivity(new Intent(getActivity(), WalletActivity.class));
//                break;

            case R.id.rl_wallet:

               startActivity(new Intent(getActivity(), WalletActivity.class));
                break;



            case R.id.iv_code: //我的二维码
                startActivity(new Intent(getActivity(), MyQrActivity.class));
                break;

            case R.id.rl_friends: //朋友圈
                startActivity(new Intent(getActivity(), MomentsActivity.class));
                break;

            case R.id.rl_xiangce: //我的相册
                startActivity(new Intent(getActivity(), MomentsFriendActivity.class).putExtra("userId",UserManager.get().getMyUserId()));
                break;


        }
    }

    public String getThirdToken() {

        return MD5.getMD5((HTApp.getInstance().getUsername()+HTConstant.MOFANG_SECRET).getBytes() );



    }

    private class InfoChangedListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (IMAction.ACTION_UPDATE_INFO.equals(intent.getAction())) {
                String type = intent.getStringExtra("type");
                if (HTConstant.JSON_KEY_AVATAR.equals(type)) {
                    String avatar = intent.getStringExtra(HTConstant.JSON_KEY_AVATAR);
                    Glide.with(context).load(avatar).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar).into(ivAvatar);
                } else if ("appId".equals(type)) {
                    String appId = intent.getStringExtra("appId");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tvFxid.setText(getString(R.string.mixin_number) + appId);

                        }
                    },300);
                } else if (HTConstant.JSON_KEY_NICK.equals(type)) {
                    String nick = intent.getStringExtra(HTConstant.JSON_KEY_NICK);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tvNick.setText(nick);

                        }
                    },300);

                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listener != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(listener);
        }
    }
}
