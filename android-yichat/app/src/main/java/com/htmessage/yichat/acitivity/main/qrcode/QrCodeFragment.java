package com.htmessage.yichat.acitivity.main.qrcode;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.htmessage.update.data.UserManager;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.R;
import com.htmessage.yichat.utils.CommonUtils;

/**
 * 项目名称：yichat0504
 * 类描述：QrCodeFragment 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/10 10:52
 * 邮箱:814326663@qq.com
 */
public class QrCodeFragment extends Fragment implements QrCodeView {
    private ImageView imageView,iv_avatar;
    private QrCodePrester prester;
    private TextView tv_nick;

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
        tv_nick.setText(HTApp.getInstance().getUserNick());
        UserManager.get().loadUserAvatar(getActivity(),UserManager.get().getMyAvatar(),iv_avatar);
     }

    private void getData() {
        prester.CreateQrCode();
    }

    private void initView(View view) {
        imageView = (ImageView) view.findViewById(R.id.code_image);
        iv_avatar = (ImageView) view.findViewById(R.id.iv_avatar);
        tv_nick = (TextView) view.findViewById(R.id.tv_nick);
    }

    @Override
    public void showQrCode(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void showError(String error) {
        CommonUtils.showToastShort(getBaseContext(),error);
    }

    @Override
    public void setPresenter(QrCodePrester presenter) {
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
