package com.htmessage.fanxinht.acitivity.main.qrcode;

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
import android.widget.Toast;

import com.htmessage.fanxinht.R;

/**
 * 项目名称：yichat0504
 * 类描述：QrCodeFragment 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/10 10:52
 * 邮箱:814326663@qq.com
 */
public class QrCodeFragment extends Fragment implements QrCodeView {
    private ImageView imageView;
    private QrCodePrester prester;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_qrcode_generate, container, false);
        initView(view);
        getData();
        return view;
    }

    private void getData() {
        prester.CreateQrCode();
    }

    private void initView(View view) {
        imageView = (ImageView) view.findViewById(R.id.code_image);
    }

    @Override
    public void showQrCode(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void showError(String error) {
        Toast.makeText(getBaseContext(), error, Toast.LENGTH_SHORT).show();
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
