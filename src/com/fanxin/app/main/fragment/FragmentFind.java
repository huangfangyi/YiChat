package com.fanxin.app.main.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fanxin.app.DemoHelper;
import com.fanxin.app.R;
import com.fanxin.app.main.FXConstant;
import com.fanxin.app.main.activity.ChatActivity;
import com.fanxin.app.main.activity.ScanCaptureActivity;
import com.fanxin.app.main.moments.SocialMainActivity;
import com.fanxin.app.main.service.GroupService;
import com.fanxin.app.main.utils.OkHttpManager;
import com.fanxin.app.main.utils.Param;
import com.fanxin.easeui.EaseConstant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;

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
