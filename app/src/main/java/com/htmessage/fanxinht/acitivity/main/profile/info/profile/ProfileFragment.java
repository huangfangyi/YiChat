package com.htmessage.fanxinht.acitivity.main.profile.info.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.main.qrcode.MyQrActivity;
import com.htmessage.fanxinht.acitivity.main.region.RegionActivity;
import com.htmessage.fanxinht.acitivity.main.profile.info.update.ProfileUpdateActivity;

/**
 * 项目名称：HTOpen
 * 类描述：ProfileFragment 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/7 11:47
 * 邮箱:814326663@qq.com
 */
public class ProfileFragment extends Fragment implements ProfileView, View.OnClickListener{
    private RelativeLayout re_avatar,re_name,re_fxid,re_sex,re_region,re_sign,re_qrcode;
    private ImageView iv_avatar;
    private TextView tv_name;
    private TextView tv_fxid;
    private TextView tv_sex;
    private TextView tv_sign, tv_region;
    private String sex;
    private ProfilePrester prester;
    private JSONObject userJson;
    private static final int UPDATE_REGION = 7;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_info, container, false);
        initView(view);
        initData();
        setListener();
        return view;
    }

    private void setListener() {
        //设置监听
        re_avatar.setOnClickListener(this);
        re_name.setOnClickListener(this);
        re_fxid.setOnClickListener(this);
        re_sex.setOnClickListener(this);
        re_region.setOnClickListener(this);
        re_sign.setOnClickListener(this);
        re_qrcode.setOnClickListener(this);
    }

    private void initData() {
        prester.resgisteRecivier();
        userJson = getUserJson();
        String nick = userJson.getString(HTConstant.JSON_KEY_NICK);
        String fxid = userJson.getString(HTConstant.JSON_KEY_FXID);
        sex = userJson.getString(HTConstant.JSON_KEY_SEX);
        String sign = userJson.getString(HTConstant.JSON_KEY_SIGN);
        String province = userJson.getString(HTConstant.JSON_KEY_PROVINCE);
        String city = userJson.getString(HTConstant.JSON_KEY_CITY);

        String avatarUrl = userJson.getString(HTConstant.JSON_KEY_AVATAR);
        if (!TextUtils.isEmpty(avatarUrl)) {
            if (!avatarUrl.contains("http:")) {
                avatarUrl = HTConstant.URL_AVATAR + avatarUrl;
            }
        }
        Glide.with(getBaseContext()).load(avatarUrl).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar).into(iv_avatar);
        tv_name.setText(nick);
        if (TextUtils.isEmpty(fxid)) {
            tv_fxid.setText(R.string.not_set);
        } else {
            tv_fxid.setText(fxid);
        }
        if (!TextUtils.isEmpty(sex)) {
            switch (sex) {
                case "1":
                case "男":
                    tv_sex.setText(R.string.male);
                    break;
                case "0":
                case "女":
                    tv_sex.setText(R.string.female);
                    break;
                default:
                    tv_sex.setText(R.string.not_set);
                    break;
            }
        } else {
            tv_sex.setText(R.string.not_set);
        }
        if (TextUtils.isEmpty(sign)) {
            tv_sign.setText(R.string.not_input);
        } else {
            tv_sign.setText(sign);
        }

        if (!TextUtils.isEmpty(province) && !TextUtils.isEmpty(city)) {
            tv_region.setText(province + " " + city);
        } else {
            tv_region.setText(R.string.not_set);
        }
    }

    private void initView(View view) {
        iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_fxid = (TextView) view.findViewById(R.id.tv_fxid);
        tv_sex = (TextView) view.findViewById(R.id.tv_sex);
        tv_sign = (TextView) view.findViewById(R.id.tv_sign);
        tv_region = (TextView) view.findViewById(R.id.tv_region);

        re_avatar = (RelativeLayout) view.findViewById(R.id.re_avatar);
        re_name= (RelativeLayout) view.findViewById(R.id.re_name);
        re_fxid = (RelativeLayout) view.findViewById(R.id.re_fxid);
        re_sex = (RelativeLayout) view.findViewById(R.id.re_sex);
        re_region= (RelativeLayout) view.findViewById(R.id.re_region);
        re_sign = (RelativeLayout) view.findViewById(R.id.re_sign);
        re_qrcode = (RelativeLayout) view.findViewById(R.id.re_qrcode);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.re_avatar:
                prester.showPhotoDialog();
                break;
            case R.id.re_name:
                startActivity(new Intent(getActivity(), ProfileUpdateActivity.class)
                        .putExtra("type", ProfileUpdateActivity.TYPE_NICK)
                        .putExtra("default", userJson.getString(HTConstant.JSON_KEY_NICK)));
                break;
            case R.id.re_fxid:
                if (TextUtils.isEmpty(userJson.getString(HTConstant.JSON_KEY_FXID))) {
                    startActivity(new Intent(getActivity(), ProfileUpdateActivity.class)
                            .putExtra("type", ProfileUpdateActivity.TYPE_FXID));
                }
                break;
            case R.id.re_sex:
                prester.showSexDialog();
                break;
            case R.id.re_region:
                getBaseActivity().startActivityForResult(new Intent(getBaseActivity(), RegionActivity.class), UPDATE_REGION);
                break;
            case R.id.re_sign:
                startActivity(new Intent(getActivity(), ProfileUpdateActivity.class)
                        .putExtra("type", ProfileUpdateActivity.TYPE_SIGN)
                        .putExtra("default", userJson.getString(HTConstant.JSON_KEY_SIGN)));
                break;
            case R.id.re_qrcode:
                startActivity(new Intent(getActivity(), MyQrActivity.class));
                break;

        }
    }

    @Override
    public void onNickUpdate(String nick, boolean isHang) {
        tv_name.setText(nick);
    }

    @Override
    public void onSexUpdate(int sex, boolean isHang) {
        tv_sex.setText(sex);
    }

    @Override
    public void onSignUpdate(String sign, boolean isHang) {
        tv_sign.setText(sign);
    }

    @Override
    public void onAvatarUpdate(String avatar, boolean isHang) {
        Glide.with(getBaseContext()).load(avatar).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar).error(R.drawable.default_avatar).into(iv_avatar);
    }

    @Override
    public void onRegionUpdate(String region, boolean isHang) {
        tv_region.setText(region);
    }

    @Override
    public void onFxidUpdate(String fxid, boolean isHang) {
        tv_fxid.setText(fxid);
    }

    @Override
    public void onUpdateSuccess(String msg) {
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpdateFailed(String error) {
        Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public JSONObject getUserJson() {
        return HTApp.getInstance().getUserJson();
    }

    @Override
    public void setPresenter(ProfilePrester presenter) {
        this.prester = presenter;
    }

    @Override
    public Context getBaseContext() {
        return getContext();
    }

    @Override
    public Activity getBaseActivity() {
        return getActivity();
    }

    @Override
    public void onDestroy() {
        prester.onDestory();
        super.onDestroy();
    }
}
