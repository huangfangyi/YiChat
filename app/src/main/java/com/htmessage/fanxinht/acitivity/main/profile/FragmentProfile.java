package com.htmessage.fanxinht.acitivity.main.profile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.IMAction;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.main.qrcode.MyQrActivity;
import com.htmessage.fanxinht.acitivity.SettingsActivity;
import com.htmessage.fanxinht.acitivity.moments.MomentsFriendActivity;
import com.htmessage.fanxinht.acitivity.main.profile.info.profile.ProfileActivity;

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
        initView(HTApp.getInstance().getUserJson());
    }

    private void getData() {
        IntentFilter intent = new IntentFilter(IMAction.ACTION_UPDATE_INFO);
        listener = new InfoChangedListener();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(listener, intent);

    }

    private void initView(JSONObject jsonObject) {

        ivAvatar = (ImageView) getView().findViewById(R.id.iv_avatar);
        tvNick = (TextView) getView().findViewById(R.id.tv_name);

        tvFxid = (TextView) getView().findViewById(R.id.tv_fxid);
        String avatarUrl = jsonObject.getString(HTConstant.JSON_KEY_AVATAR);
        Glide.with(getActivity()).load(avatarUrl).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar).into(ivAvatar);
        tvNick.setText(jsonObject.getString(HTConstant.JSON_KEY_NICK));
        String fxid = jsonObject.getString(HTConstant.JSON_KEY_FXID);
        if (!TextUtils.isEmpty(fxid)) {
            tvFxid.setText(getString(R.string.mixin_number) + fxid);
        } else {
            tvFxid.setText(getString(R.string.mixin_number) + getString(R.string.not_set));
        }
    }

    private void setListener() {
        getView().findViewById(R.id.re_myinfo).setOnClickListener(this);
        getView().findViewById(R.id.re_setting).setOnClickListener(this);
        getView().findViewById(R.id.re_xiangce).setOnClickListener(this);
        getView().findViewById(R.id.rl_qrcode).setOnClickListener(this);

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

            case R.id.re_xiangce:
                startActivity(new Intent(getActivity(), MomentsFriendActivity.class).putExtra("userId", HTApp.getInstance().getUsername()));
                break;

            case R.id.rl_qrcode: //我的二维码
                startActivity(new Intent(getActivity(), MyQrActivity.class));
                break;

        }
    }

    private class InfoChangedListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (IMAction.ACTION_UPDATE_INFO.equals(intent.getAction())) {
                String type = intent.getStringExtra(HTConstant.KEY_CHANGE_TYPE);
                if (HTConstant.JSON_KEY_AVATAR.equals(type)) {
                    String avatar = intent.getStringExtra(HTConstant.JSON_KEY_AVATAR);
                    Glide.with(context).load(avatar).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar).into(ivAvatar);
                } else if (HTConstant.JSON_KEY_FXID.equals(type)) {
                    String fxid = intent.getStringExtra(HTConstant.JSON_KEY_FXID);
                    tvFxid.setText(getString(R.string.mixin_number) + fxid);
                } else if (HTConstant.JSON_KEY_NICK.equals(type)) {
                    String nick = intent.getStringExtra(HTConstant.JSON_KEY_NICK);
                    tvNick.setText(nick);
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
