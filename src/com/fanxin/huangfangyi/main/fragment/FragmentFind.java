package com.fanxin.huangfangyi.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.fanxin.huangfangyi.DemoHelper;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.activity.NearPeopleActivity;
import com.fanxin.huangfangyi.main.activity.ScanCaptureActivity;
import com.fanxin.huangfangyi.main.activity.ShakeActivity;
import com.fanxin.huangfangyi.main.moments.SocialMainActivity;

public class FragmentFind extends Fragment implements OnClickListener{
    private RelativeLayout re_friends,re_qrcode,re_fujin,re_piaoliuping,re_gouwu,re_youxi,re_yaoyiyao;
    private String userID;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userID = DemoHelper.getInstance().getCurrentUsernName();
        initView();
        setOnClick();
    }

    private void initView() {
        re_friends = (RelativeLayout) getView().findViewById(R.id.re_friends);
        re_qrcode = (RelativeLayout) getView().findViewById(R.id.re_qrcode);
        re_fujin = (RelativeLayout) getView().findViewById(R.id.re_fujin);
        re_piaoliuping = (RelativeLayout) getView().findViewById(R.id.re_piaoliuping);
        re_gouwu = (RelativeLayout) getView().findViewById(R.id.re_gouwu);
        re_youxi = (RelativeLayout) getView().findViewById(R.id.re_youxi);
        re_yaoyiyao = (RelativeLayout) getView().findViewById(R.id.re_yaoyiyao);
    }
    private void setOnClick() {
        re_friends.setOnClickListener(this);
        re_qrcode.setOnClickListener(this);
        re_fujin.setOnClickListener(this);
        re_piaoliuping.setOnClickListener(this);
        re_gouwu.setOnClickListener(this);
        re_youxi.setOnClickListener(this);
        re_yaoyiyao.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.re_friends:
                if (!TextUtils.isEmpty(userID)) {
                    startActivity(new Intent(getActivity(), SocialMainActivity.class).putExtra("userID", userID));
                }
            break;
            case R.id.re_qrcode:
                startActivity(new Intent(getActivity(), ScanCaptureActivity.class));
                break;
            case R.id.re_yaoyiyao:
                if (!TextUtils.isEmpty(userID)) {
                    startActivity(new Intent(getActivity(), ShakeActivity.class).putExtra("userID", userID));
                }
                break;
            case R.id.re_fujin:
                startActivity(new Intent(getActivity(), NearPeopleActivity.class));
                break;
            case R.id.re_piaoliuping:
                break;
            case R.id.re_gouwu:
                break;
            case R.id.re_youxi:
                break;

        }

    }
}
