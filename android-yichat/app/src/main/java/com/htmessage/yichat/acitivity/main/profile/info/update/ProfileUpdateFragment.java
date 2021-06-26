package com.htmessage.yichat.acitivity.main.profile.info.update;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.Validator;

/**
 * 项目名称：HTOpen
 * 类描述：ProfileUpdateFragment 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/7 16:20
 * 邮箱:814326663@qq.com
 */
public class ProfileUpdateFragment extends Fragment implements UpdateProfileView, View.OnClickListener {

    private String defaultStr;
    private TextView saveTV;
    private EditText infoET;
    private TextView titleTV, tv_tips;
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
        if (title.equals(getString(R.string.change_nick))) {
            infoET.setInputType(InputType.TYPE_CLASS_TEXT);
            hintTips();
         } else if (title.equals(getString(R.string.change_appId))) {
            showTips(R.string.modify_id_tips);
            infoET.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            infoET.setInputType(InputType.TYPE_CLASS_TEXT);
            hintTips();
        }
        if (defaultStr != null) {
            infoET.setText(defaultStr);
            infoET.setSelection(infoET.getText().length());
        }
    }

    private void initView(View updateView) {
        titleTV = (TextView) updateView.findViewById(R.id.tv_title);
        saveTV = (TextView) updateView.findViewById(R.id.tv_save);
        infoET = (EditText) updateView.findViewById(R.id.et_info);
        tv_tips = (TextView) updateView.findViewById(R.id.tv_tips);
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
        return getBaseActivity().getIntent().getIntExtra("type", 0);
    }

    @Override
    public String getInputString() {
        return infoET.getText().toString().trim();
    }

    @Override
    public void onUpdateSuccess(String key,String value) {

        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(IMAction.ACTION_UPDATE_INFO).putExtra("type",key).putExtra(key,value));
        getBaseActivity().finish();
    }

    @Override
    public void onUpdateFailed(String msg) {
        CommonUtils.showToastShort(getActivity(), msg);
    }

    @Override
    public void showTips(int msg) {
        if (tv_tips != null) {
            tv_tips.setVisibility(View.VISIBLE);
            tv_tips.setText(msg);
        }
    }

    @Override
    public void hintTips() {
        if (tv_tips != null) {
            tv_tips.setVisibility(View.GONE);
            tv_tips.setText("");
        }
    }

    @Override
    public void show(int resId) {

    }

    @Override
    public void showDialog() {

    }

    @Override
    public void cancelDialog() {

    }


    @Override
    public void finish() {
        if( getActivity()!=null){
            getActivity().finish();
        }
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
        switch (v.getId()) {
            case R.id.tv_save:
                String inputString = getInputString();
                if(type == 1){
                    if(Validator.isChineseStr(inputString)){
                        onUpdateFailed(getString(R.string.mixin_can_not_has_chinese));
                        return;
                    }
                }
                prestener.updateInfo(prestener.getKey(type), inputString, getDefultString());
                break;
        }
    }

    @Override
    public void onDestroy() {
        prestener.onDestory();
        super.onDestroy();
    }
}
