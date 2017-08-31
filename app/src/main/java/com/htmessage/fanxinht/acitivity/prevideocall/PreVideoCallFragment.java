package com.htmessage.fanxinht.acitivity.prevideocall;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.chat.PreVideoCallListAdapter;
import com.htmessage.fanxinht.domain.User;

import java.util.ArrayList;

/**
 * 项目名称：yichat0504
 * 类描述：PreVideoCallFragment 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/11 11:20
 * 邮箱:814326663@qq.com
 */
public class PreVideoCallFragment extends Fragment implements PreVideoVideoCallView, AdapterView.OnItemClickListener {
    private ListView list;
    private PreVideoCallListAdapter listAdapter;
    private LinearLayout ll_videList1, ll_videList2;
    private PreVideoCallPrestener presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View videoCallView = inflater.inflate(R.layout.activity_pre_voideocall, container, false);
        initView(videoCallView);
        initData();
        setListener();
        return videoCallView;
    }

    private void setListener() {
        list.setOnItemClickListener(this);
    }

    private void initData() {
        presenter.getGroupMembers(getGroupId());
    }

    private void initView(View videoCallView) {
        list = (ListView) videoCallView.findViewById(R.id.list);
        ll_videList1 = (LinearLayout) videoCallView.findViewById(R.id.ll_videList1);
        ll_videList2 = (LinearLayout) videoCallView.findViewById(R.id.ll_videList2);
    }

    @Override
    public String getGroupId() {
        return getActivity().getIntent().getStringExtra("groupId");
    }

    @Override
    public void reFreshView(ArrayList<User> exitUsers, ArrayList<String> userIds, ArrayList<User> checkUsers) {
        listAdapter = new PreVideoCallListAdapter(getBaseContext(), exitUsers, userIds);
        list.setAdapter(listAdapter);
        refreshView(checkUsers);
    }

    @Override
    public void setPresenter(PreVideoCallPrestener presenter) {
        this.presenter = presenter;
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        checkBox.toggle();
        PreVideoCallListAdapter.getIsSelected().put(position, checkBox.isChecked());//将CheckBox的选中状况记录下来
        if (listAdapter.getItem(position).getUsername().equals(HTApp.getInstance().getUsername())) {
            checkBox.setChecked(true);
            checkBox.setEnabled(false);
            return;
        }
        // 调整选定条目
        if (checkBox.isChecked() == true) {
            presenter.getUserIds().add(listAdapter.getItem(position).getUsername());
            presenter.getUsers().add(listAdapter.getItem(position));
            refreshView(presenter.getUsers());
        } else {
            presenter.getUserIds().remove(listAdapter.getItem(position).getUsername());
            presenter.getUsers().remove(listAdapter.getItem(position));
            refreshView(presenter.getUsers());
        }
        if (presenter.getUserIds().size() > 9 && presenter.getUsers().size() > 9) {
           presenter.showCheckedDialog();
            checkBox.setChecked(false);
            checkBox.setEnabled(false);
            presenter.getUserIds().remove(listAdapter.getItem(position).getUsername());
            presenter.getUsers().remove(listAdapter.getItem(position));
            refreshView(presenter.getUsers());
            return;
        }
        if (presenter.getUserIds().size() == 0 &&  presenter.getUsers().size() == 0) {
            getBaseActivity().finish();
        }
    }

    /**
     * 刷新Ui显示
     *
     * @param users
     */
    private void refreshView(ArrayList<User> users) {
        //为避免重复数据 故清空所有的view
        ll_videList1.removeAllViews();
        ll_videList2.removeAllViews();
        switch (users.size()) {
            case 1:
                ll_videList1.addView(presenter.getView(users.get(0)));
                break;
            case 2:
                ll_videList1.addView(presenter.getView(users.get(0)));
                ll_videList1.addView(presenter.getView(users.get(1)));
                break;
            case 3:
                ll_videList1.addView(presenter.getView(users.get(0)));
                ll_videList1.addView(presenter.getView(users.get(1)));
                ll_videList1.addView(presenter.getView(users.get(2)));
                break;
            case 4:
                ll_videList1.addView(presenter.getView(users.get(0)));
                ll_videList1.addView(presenter.getView(users.get(1)));
                ll_videList1.addView(presenter.getView(users.get(2)));
                ll_videList1.addView(presenter.getView(users.get(3)));
                break;
            case 5:
                ll_videList1.addView(presenter.getView(users.get(0)));
                ll_videList1.addView(presenter.getView(users.get(1)));
                ll_videList1.addView(presenter.getView(users.get(2)));
                ll_videList1.addView(presenter.getView(users.get(3)));
                ll_videList1.addView(presenter.getView(users.get(4)));
                break;
            case 6:
                ll_videList1.addView(presenter.setViewParams(users.get(0)));
                ll_videList1.addView(presenter.setViewParams(users.get(1)));
                ll_videList1.addView(presenter.setViewParams(users.get(2)));
                ll_videList1.addView(presenter.setViewParams(users.get(3)));
                ll_videList1.addView(presenter.setViewParams(users.get(4)));
                ll_videList2.addView(presenter.setViewParams(users.get(5)));
                break;
            case 7:
                ll_videList1.addView(presenter.setViewParams(users.get(0)));
                ll_videList1.addView(presenter.setViewParams(users.get(1)));
                ll_videList1.addView(presenter.setViewParams(users.get(2)));
                ll_videList1.addView(presenter.setViewParams(users.get(3)));
                ll_videList1.addView(presenter.setViewParams(users.get(4)));
                ll_videList2.addView(presenter.setViewParams(users.get(5)));
                ll_videList2.addView(presenter.setViewParams(users.get(6)));
                break;
            case 8:
                ll_videList1.addView(presenter.setViewParams(users.get(0)));
                ll_videList1.addView(presenter.setViewParams(users.get(1)));
                ll_videList1.addView(presenter.setViewParams(users.get(2)));
                ll_videList1.addView(presenter.setViewParams(users.get(3)));
                ll_videList1.addView(presenter.setViewParams(users.get(4)));
                ll_videList2.addView(presenter.setViewParams(users.get(5)));
                ll_videList2.addView(presenter.setViewParams(users.get(6)));
                ll_videList2.addView(presenter.setViewParams(users.get(7)));
                break;
            case 9:
                ll_videList1.addView(presenter.setViewParams(users.get(0)));
                ll_videList1.addView(presenter.setViewParams(users.get(1)));
                ll_videList1.addView(presenter.setViewParams(users.get(2)));
                ll_videList1.addView(presenter.setViewParams(users.get(3)));
                ll_videList1.addView(presenter.setViewParams(users.get(5)));
                ll_videList2.addView(presenter.setViewParams(users.get(6)));
                ll_videList2.addView(presenter.setViewParams(users.get(7)));
                ll_videList2.addView(presenter.setViewParams(users.get(8)));
                break;
        }
    }

}
