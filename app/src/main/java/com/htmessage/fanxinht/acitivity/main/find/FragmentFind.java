package com.htmessage.fanxinht.acitivity.main.find;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htmessage.fanxinht.IMAction;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.moments.MomentsActivity;
import com.htmessage.fanxinht.acitivity.main.find.recentlypeople.PeopleRecentlyActivity;
import com.htmessage.fanxinht.domain.MomentsMessageDao;
import com.htmessage.fanxinht.widget.zxing.activity.CaptureActivity;


public class FragmentFind extends Fragment implements View.OnClickListener {
    private RelativeLayout rl_friends, re_qrcode, rl_near;
    private MyBroadcastReceiver myBroadcastReceiver;
    private MomentsMessageDao momentsMessageDao;
    private OnMomentsMessageLisenter onMomentsMessageLisenter;
    private TextView tvUnredCount;

    public interface OnMomentsMessageLisenter {
        void onNewMonmentsNotice(int count);


    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMomentsMessageLisenter) {
            onMomentsMessageLisenter = (OnMomentsMessageLisenter) context;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (savedInstanceState != null
                && savedInstanceState.getBoolean("isConflict", false))
            return;
        super.onActivityCreated(savedInstanceState);
        momentsMessageDao=new MomentsMessageDao(getContext());
        initView();
        initData();
        setListener();
        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(IMAction.ACTION_MOMENTS);
        intentFilter.addAction(IMAction.ACTION_MOMENTS_READ);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myBroadcastReceiver, intentFilter);

    }

    private void setListener() {
        rl_friends.setOnClickListener(this);
        re_qrcode.setOnClickListener(this);
        rl_near.setOnClickListener(this);

    }

    private void initData() {
        initUnreadView();
    }

    private void initView() {
        rl_friends = (RelativeLayout) getView().findViewById(R.id.rl_friends);
        re_qrcode = (RelativeLayout) getView().findViewById(R.id.re_qrcode);
        tvUnredCount = (TextView) getView().findViewById(R.id.unread_msg_count);
        rl_near = (RelativeLayout) getView().findViewById(R.id.rl_near);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_friends://志工圈
                startActivity(new Intent(getActivity(), MomentsActivity.class));
                break;
            case R.id.re_qrcode: //扫一扫
                startActivity(new Intent(getActivity(), CaptureActivity.class));
                break;

            case R.id.rl_near://最近在线
                startActivity(new Intent(getActivity(), PeopleRecentlyActivity.class));
                break;
        }
    }


    class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (IMAction.ACTION_MOMENTS.equals(intent.getAction())) {
                initUnreadView();
            }else if(IMAction.ACTION_MOMENTS_READ.equals(intent.getAction())){
                initUnreadView();

            }
        }
    }

    private void initUnreadView() {

        int count = momentsMessageDao.getUnreadMoments();
        if (count > 0) {
            tvUnredCount.setVisibility(View.VISIBLE);
            tvUnredCount.setText(count + "");
        }else {
            tvUnredCount.setVisibility(View.GONE);
         }
        if (onMomentsMessageLisenter != null) {
            onMomentsMessageLisenter.onNewMonmentsNotice(count);
        }
    }

}
