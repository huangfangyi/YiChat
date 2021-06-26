package com.htmessage.yichat.acitivity.chat.group.qrcode;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.R;
import com.htmessage.yichat.utils.CommonUtils;

/**
 * 项目名称：fanxinht
 * 类描述：GroupQrCodeFragment 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/10 10:52
 * 邮箱:814326663@qq.com
 */
public class GroupQrCodeFragment extends Fragment implements GroupQrCodeView {
    private ImageView imageView, iv_avatar;
    private GroupQrCodePrester prester;
    private TextView tv_nick, tv_tips;
    private String groupId, groupName, groupAvatar;
    private FrameLayout frl_qrcode;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_qrcode_generate, container, false);
        initView(view);
        getData();
        initData();
        return view;
    }

    private void initData() {
        tv_nick.setText(TextUtils.isEmpty(groupName) ? groupId : groupName);
        tv_tips.setText(R.string.group_qrcode_tips);
        prester.CreateQrCode();
        CommonUtils.loadGroupAvatar(getActivity(), TextUtils.isEmpty(groupAvatar) ? "false" : groupAvatar, iv_avatar);
    }

    private void getData() {
        JSONObject intenetData = getIntenetData();
        groupId = intenetData.getString("groupId");
        if (TextUtils.isEmpty(groupId)) {
            getActivity().finish();
            return;
        }
        groupAvatar = intenetData.getString("groupAvatar");
        groupName = intenetData.getString("groupName");
    }

    private void initView(View view) {
        imageView = (ImageView) view.findViewById(R.id.code_image);
        iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
        tv_nick = (TextView) view.findViewById(R.id.tv_nick);
        tv_tips = (TextView) view.findViewById(R.id.tv_tips);
        frl_qrcode = (FrameLayout) view.findViewById(R.id.frl_qrcode);
    }

    @Override
    public void showQrCode(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void showError(String error) {
        CommonUtils.showToastShort(getBaseContext(), error);
    }

    @Override
    public JSONObject getIntenetData() {
        String groupObj = getActivity().getIntent().getStringExtra("groupObj");
        JSONObject object = JSONObject.parseObject(groupObj);
        return object;
    }

    @Override
    public View getFrameLayout() {
        return frl_qrcode;
    }

    @Override
    public void setPresenter(GroupQrCodePrester presenter) {
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
