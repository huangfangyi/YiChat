package com.htmessage.yichat.acitivity.main.profile.info.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.main.profile.info.update.ProfileUpdateActivity;
import com.htmessage.yichat.acitivity.main.qrcode.MyQrActivity;
import com.htmessage.yichat.acitivity.password.PasswordResetActivity;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.update.data.UserManager;

/**
 * 项目名称：HTOpen
 * 类描述：ProfileFragment 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/7 11:47
 * 邮箱:814326663@qq.com
 */
public class ProfileFragment extends Fragment implements ProfileView, View.OnClickListener{
    private RelativeLayout re_avatar,re_name, re_appId,re_sex,re_qrcode,re_mobile;
    private ImageView iv_avatar;
    private TextView tv_name;
    private TextView tv_appId;
    private TextView tv_sex;
     private String gender;
    private String mobile;
    private ProfilePrester prester;
    private JSONObject userJson;
     private TextView  tv_temp_mobile;
     //当前手机号显示
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_info, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView(getView());
        initData();
        setListener();
    }

    private void setListener() {
        //设置监听
        re_avatar.setOnClickListener(this);
        re_name.setOnClickListener(this);
        re_appId.setOnClickListener(this);
        re_sex.setOnClickListener(this);
        re_qrcode.setOnClickListener(this);
        re_mobile.setOnClickListener(this);
    }

    private void initData() {
        prester.resgisteRecivier();
        userJson = UserManager.get().getMyUser();
        String nick = userJson.getString("nick");
        String fxid = userJson.getString("appId");
        gender = userJson.getString("gender");
        mobile=userJson.getString("mobile");

        String avatarUrl = userJson.getString("avatar");

        UserManager.get().loadUserAvatar(getActivity(),avatarUrl,iv_avatar);
        tv_name.setText(nick);
        if (TextUtils.isEmpty(fxid)) {
            tv_appId.setText(R.string.not_set);
        } else {
            tv_appId.setText(fxid);
        }
        if (!TextUtils.isEmpty(gender)) {
            switch (gender) {
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

        if(TextUtils.isEmpty(mobile)){
            tv_temp_mobile.setText("绑定手机号");
        }else {
            tv_temp_mobile.setText("更换手机号");
            TextView tv_mobile=getView().findViewById(R.id.tv_mobile);
            tv_mobile.setText(mobile);
        }


    }

    private void initView(View view) {
        iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_appId = (TextView) view.findViewById(R.id.tv_fxid);
        tv_sex = (TextView) view.findViewById(R.id.tv_sex);

        re_avatar = (RelativeLayout) view.findViewById(R.id.re_avatar);
        re_name= (RelativeLayout) view.findViewById(R.id.re_name);
        re_appId = (RelativeLayout) view.findViewById(R.id.re_appid);
        re_sex = (RelativeLayout) view.findViewById(R.id.re_sex);

        re_qrcode = (RelativeLayout) view.findViewById(R.id.re_qrcode);
        re_mobile= (RelativeLayout) view.findViewById(R.id.re_mobile);
        tv_temp_mobile= (TextView) view.findViewById(R.id.tv_temp_mobile);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.re_avatar:
                if (!CommonUtils.checkPermission(getActivity(),Manifest.permission.CAMERA)
                        && !CommonUtils.checkPermission(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)
                        && !CommonUtils.checkPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    CommonUtils.showToastShort(getActivity(), R.string.miss_permission_camera);
                    requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                    return;
                }
                prester.showPhotoDialog();
                break;
            case R.id.re_name:
                startActivity(new Intent(getActivity(), ProfileUpdateActivity.class)
                        .putExtra("type", ProfileUpdateActivity.TYPE_NICK)
                        .putExtra("default", userJson.getString(HTConstant.JSON_KEY_NICK)));
                break;
            case R.id.re_appid:
                if (TextUtils.isEmpty(userJson.getString("appId"))) {
                    startActivity(new Intent(getActivity(), ProfileUpdateActivity.class)
                            .putExtra("type", ProfileUpdateActivity.TYPE_APPID));
                }
                break;
            case R.id.re_sex:
                prester.showSexDialog();
                break;


            case R.id.re_qrcode:
                startActivity(new Intent(getActivity(), MyQrActivity.class));
                break;
            case R.id.re_mobile:
                if(TextUtils.isEmpty(mobile)){
                    startActivity(new Intent(getActivity(), PasswordResetActivity.class).putExtra("isBind",1));

                }else {
                    startActivity(new Intent(getActivity(), PasswordResetActivity.class).putExtra("isBind",2));
                }

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
    public void onAvatarUpdate(String avatar, boolean isHang) {
        UserManager.get().loadUserAvatar(getActivity(),avatar,iv_avatar);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(IMAction.ACTION_UPDATE_INFO).putExtra("type","avatar").putExtra("avatar",avatar));
    }


    @Override
    public void onFxidUpdate(String fxid, boolean isHang) {
        tv_appId.setText(fxid);
    }

    @Override
    public void onUpdateSuccess(String msg) {
        CommonUtils.showToastShort(getActivity(),msg);
    }

    @Override
    public void onUpdateFailed(String error) {
        CommonUtils.showToastShort(getActivity(),error);
    }

    @Override
    public void showToast(int resId) {

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
