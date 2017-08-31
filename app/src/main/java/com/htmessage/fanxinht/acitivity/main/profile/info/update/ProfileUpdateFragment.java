package com.htmessage.fanxinht.acitivity.main.profile.info.update;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.htmessage.fanxinht.R;

/**
 * 项目名称：HTOpen
 * 类描述：ProfileUpdateFragment 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/7 16:20
 * 邮箱:814326663@qq.com
 */
public class ProfileUpdateFragment extends Fragment implements UpdateProfileView,View.OnClickListener{

    private String defaultStr;
    private TextView saveTV;
    private EditText infoET;
    private TextView titleTV;
    private int type;
    private UpdateProfilePrestener prestener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View updateView = inflater.inflate(R.layout.activity_update_info, container, false);
        getData();
        initView(updateView);
        initData();
        setListener();
        return updateView;
    }

    private void setListener() {
        saveTV.setOnClickListener(this);
    }

    private void initData() {
        String title = prestener.getTitle(type);
        titleTV.setText(title);
        if (defaultStr != null) {
            infoET.setText(defaultStr);
            infoET.setSelection(infoET.getText().length());
        }
    }

    private void initView(View updateView) {
        titleTV = (TextView) updateView.findViewById(R.id.tv_title);
        saveTV = (TextView) updateView.findViewById(R.id.tv_save);
        infoET = (EditText) updateView.findViewById(R.id.et_info);
    }

    private void getData() {
        defaultStr = getDefultString();
        type = getType();
    }

    @Override
    public String getDefultString() {
        return getBaseActivity().getIntent().getStringExtra("default");
    }

    @Override
    public int getType() {
        return  getBaseActivity().getIntent().getIntExtra("type", 0);
    }

    @Override
    public String getInputString() {
        return infoET.getText().toString().trim();
    }

    @Override
    public void onUpdateSuccess(String msg) {
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
        getBaseActivity().finish();
    }

    @Override
    public void onUpdateFailed(String msg) {
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setPresenter(UpdateProfilePrestener presenter) {
        this.prestener = presenter;
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_save:
                prestener.updateInfo(prestener.getKey(type),getInputString(),getDefultString());
                break;
        }
    }

    @Override
    public void onDestroy() {
        prestener.onDestory();
        super.onDestroy();
    }
}
