package com.fanxin.app.main.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fanxin.app.DemoApplication;
import com.fanxin.app.R;
import com.fanxin.app.main.activity.FXConstant;
import com.fanxin.app.main.activity.ProfileActivity;
import com.fanxin.app.main.activity.SettingsActivity;

public class FragmentProfile extends Fragment implements View.OnClickListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        setListener();
    }
    private void initView(){
        ImageView ivAvatar= (ImageView) getView().findViewById(R.id.iv_avatar);
        TextView tvNick= (TextView) getView().findViewById(R.id.tv_name);
        TextView tvFxid= (TextView) getView().findViewById(R.id.tv_fxid);

        JSONObject jsonObject=DemoApplication.getInstance().getUserJson();
        Glide.with(this).load(FXConstant.URL_AVATAR+ jsonObject.getString(FXConstant.JSON_KEY_AVATAR)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.fx_default_useravatar).into(ivAvatar);
        tvNick.setText(jsonObject.getString(FXConstant.JSON_KEY_NICK));
        String fxid=jsonObject.getString(FXConstant.JSON_KEY_FXID);
        if(TextUtils.isEmpty(fxid)){
            fxid="未设置";
        }
        fxid="微信号:"+fxid;
        tvFxid.setText(fxid);
    }
    private void setListener(){

        getView().findViewById(R.id.re_myinfo).setOnClickListener(this);
        getView().findViewById(R.id.re_setting).setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK){
            initView();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.re_myinfo:
                startActivityForResult(new Intent(getActivity(), ProfileActivity.class),0);
                break;

            case R.id.re_setting:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                break;

        }
    }
}
