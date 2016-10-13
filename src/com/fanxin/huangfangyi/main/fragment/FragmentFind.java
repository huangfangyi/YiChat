package com.fanxin.huangfangyi.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.fanxin.huangfangyi.DemoHelper;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.activity.ScanCaptureActivity;
import com.fanxin.huangfangyi.main.moments.SocialMainActivity;

public class FragmentFind extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().findViewById(R.id.re_friends).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String userID = DemoHelper.getInstance().getCurrentUsernName();
                if (!TextUtils.isEmpty(userID)) {

                    startActivity(new Intent(getActivity(), SocialMainActivity.class).putExtra("userID", userID));

                }
            }


        });
        getView().findViewById(R.id.re_qrcode).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), ScanCaptureActivity.class));
            }

        });

    }




}
