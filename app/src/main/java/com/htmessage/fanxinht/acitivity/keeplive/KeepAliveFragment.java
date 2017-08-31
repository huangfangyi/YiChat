package com.htmessage.fanxinht.acitivity.keeplive;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.manager.HTPreferenceManager;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.utils.SwitchButton;

/**
 * 项目名称：yichat0718
 * 类描述：KeepAliveFragment 描述:
 * 创建人：songlijie
 * 创建时间：2017/8/24 15:11
 * 邮箱:814326663@qq.com
 */
public class KeepAliveFragment extends Fragment implements View.OnClickListener {
    private RelativeLayout rl_switch_lisenter,rl_switch_notification;
    private SwitchButton switch_notification;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_keep_alive, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();
        initView();
        iniData();
        setListener();
    }

    private void setListener() {
        rl_switch_lisenter.setOnClickListener(this);
        switch_notification.setOnClickListener(this);
        rl_switch_notification.setOnClickListener(this);
    }

    private void iniData() {
        if (HTPreferenceManager.getInstance().getNotificationShow()) {
            switch_notification.openSwitch();
        } else {
            switch_notification.closeSwitch();
        }
    }

    private void initView() {
        rl_switch_lisenter = (RelativeLayout) getView().findViewById(R.id.rl_switch_lisenter);
        rl_switch_notification = (RelativeLayout) getView().findViewById(R.id.rl_switch_notification);
        switch_notification = (SwitchButton) getView().findViewById(R.id.switch_notification);
    }

    private void getData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_switch_lisenter:
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivity(intent);
                break;
            case R.id.rl_switch_notification:
            case R.id.switch_notification:
                if (switch_notification.isSwitchOpen()) {
                    switch_notification.closeSwitch();

                    HTClient.getInstance().setNotificationShow(false);
                } else {
                    switch_notification.openSwitch();
                    HTClient.getInstance().setNotificationShow(true);
                }
                break;
        }
    }
}
