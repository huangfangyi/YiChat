package com.htmessage.yichat.acitivity.main.pay.paymentdetails;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.R;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.widget.swipyrefresh.SwipyRefreshLayout;


/**
 * 项目名称：hanxuan
 * 类描述：PayMentListFragment 描述:
 * 创建人：songlijie
 * 创建时间：2017/11/28 16:07
 * 邮箱:814326663@qq.com
 */
public class PayMentListFragment extends Fragment implements PaymentListView, SwipyRefreshLayout.OnRefreshListener, PayMentListAdapter.OnPayMentListClickListener {
    private PayMentListPresenter presenter;
    private RecyclerView recyclerview;
    private SwipyRefreshLayout swipyrefresh;
    private PayMentListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_list, container, false);
    }

    @Override
    public void onAttach(Context context) {
        presenter = new PayMentListPresenter(this);
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();
        initView();
        initData();
        setListener();
    }

    private void getData() {

    }

    private void initView() {
        recyclerview = (RecyclerView) getView().findViewById(R.id.recyclerview);
        swipyrefresh = (SwipyRefreshLayout) getView().findViewById(R.id.swipyrefresh);
    }

    private void initData() {
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PayMentListAdapter(getActivity(), presenter.getPayMentList());
        recyclerview.setAdapter(adapter);
        onRefresh(1);
    }

    private void setListener() {
        swipyrefresh.setOnRefreshListener(this);
        adapter.setListClickListener(this);
    }

    @Override
    public void showToast(String msg) {
        CommonUtils.showToastShort(getActivity(), msg);
    }

    @Override
    public void showProgress() {
        CommonUtils.showDialogNumal(getActivity(), getString(R.string.loading));
    }

    @Override
    public void hintProgress() {
        CommonUtils.cencelDialog();
    }

    @Override
    public void refreshList() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void cancleRefresh() {
        if (swipyrefresh != null) {
            swipyrefresh.setRefreshing(false);
        }
    }

    @Override
    public void setPresenter(PayMentListPresenter presenter) {
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
        presenter.requestPayMentList(index);
    }

    @Override
    public void onLoad(int index) {
        if (presenter.getPayMentList().size() < 20) {
            onRefresh(1);
        } else {
            index = 1;
            index++;
            presenter.requestPayMentList(index);
        }
    }

    @Override
    public void onPaymentClick(int positon, JSONObject object) {

    }
}
