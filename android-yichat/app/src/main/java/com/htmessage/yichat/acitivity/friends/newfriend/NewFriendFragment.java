package com.htmessage.yichat.acitivity.friends.newfriend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alibaba.fastjson.JSONArray;
import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.widget.HTAlertDialog;

/**
 * 项目名称：yichat0504
 * 类描述：NewFriendFragment 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/4 17:17
 * 邮箱:814326663@qq.com
 */
public class NewFriendFragment extends Fragment implements NewFriendView {
    private RecyclerView recyclerView;
    private NewFriendsAdapter adapter;
    private NewFriendPresenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View friendView = inflater.inflate(R.layout.activity_new_friends, container, false);
        return friendView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
        mPresenter.getData();
    }


    private void initView() {
        recyclerView = (RecyclerView) getView().findViewById(R.id.listview);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
//         设置adapter
        // adapter = new NewFriendsAdapter(getBaseContext(), message);
//         设置adapter
    }





    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void showToast(int resId) {

    }

    @Override
    public void initRecyclerView(JSONArray data) {
        adapter=new NewFriendsAdapter(getActivity(),data);
        recyclerView.setAdapter(adapter);
        adapter.setAgreeListener(new NewFriendsAdapter.AgreeListener() {
            @Override
            public void onClicked(String fid,int position,String userId) {
                mPresenter.agreeApply(fid,position,userId);
            }
        });
        adapter.setOnItemClickListener(new NewFriendsAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position1) {

                new HTAlertDialog(getActivity(),null,new String[]{"删除"}).init(new HTAlertDialog.OnItemClickListner() {
                    @Override
                    public void onClick(int position) {
                        if(position==0){
                            mPresenter.deleteItem(position1);
                        }
                    }
                });


                return true;
            }
        });

    }

    @Override
    public void refreshRecyclerView() {
        adapter.notifyDataSetChanged();

    }

    @Override
    public void changeAgreeButton(String fid) {
        Button button=recyclerView.findViewWithTag(fid);
        if(button!=null){
            button.setBackground(null);
            button.setTextColor(getResources().getColor(R.color.color_AAAAAA));
            button.setText(R.string.has_agreed);
            //type=1表示只是刷新好友列表，而不再提示有新好友申请的动态
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcastSync(new Intent(IMAction.ACTION_INVITE_MESSAGE)
                    .putExtra("type",1));
        }
    }


    @Override
    public void setPresenter(NewFriendPresenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public Context getBaseContext() {
        return null;
    }

    @Override
    public Activity getBaseActivity() {
        return null;
    }
}
