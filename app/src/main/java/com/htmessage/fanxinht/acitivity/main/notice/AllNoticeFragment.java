package com.htmessage.fanxinht.acitivity.main.notice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.utils.CommonUtils;
import com.htmessage.fanxinht.widget.swipyrefresh.SwipyRefreshLayout;

/**
 * 项目名称：PersonalTailor
 * 类描述：AllNoticeFragment 描述:
 * 创建人：songlijie
 * 创建时间：2017/8/2 17:02
 * 邮箱:814326663@qq.com
 */
public class AllNoticeFragment extends Fragment implements AllNoticeView, SwipyRefreshLayout.OnRefreshListener, AllNoticeAdapter.OnItemClockListener {
    private SwipyRefreshLayout refreshview;
    private RecyclerView recyclerview;
    private AllNoticePresenter presenter;
    private AllNoticeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View noticeview = inflater.inflate(R.layout.fragment_all_notice, container, false);
        initView(noticeview);
        initData();
        setListener();
        return noticeview;
    }

    private void setListener() {
        refreshview.setOnRefreshListener(this);
        adapter.setListener(this);
    }

    private void initData() {
        onRefresh(1);
        adapter = new AllNoticeAdapter(presenter.getAllNotice(), getActivity());
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerview.setAdapter(adapter);
    }

    private void initView(View noticeview) {
        recyclerview = (RecyclerView) noticeview.findViewById(R.id.recyclerview);
        refreshview = (SwipyRefreshLayout) noticeview.findViewById(R.id.refreshview);
    }

    @Override
    public void showToast(String msg) {
        CommonUtils.showToastShort(getActivity(), msg);
    }

    @Override
    public void cancleRefresh() {
        refreshview.setRefreshing(false);
    }

    @Override
    public void RefreshList() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setPresenter(AllNoticePresenter presenter) {
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
    public void onRefresh(int index) {
        presenter.onRefresh();
    }

    @Override
    public void onLoad(int index) {
        index++;
        presenter.onLoadMore(index);
    }

    @Override
    public void onDestroy() {
        presenter.onDestory();
        super.onDestroy();
    }

    @Override
    public void onItemClock(View view, JSONObject object) {
        startActivity(new Intent(getBaseActivity(), AllNoticeDetailsActivity.class).putExtra("notice", object.toJSONString()));
    }
}
