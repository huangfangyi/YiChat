package com.htmessage.fanxinht.acitivity.main.find.recentlypeople;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.widget.swipyrefresh.SwipyRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：yichat0504
 * 类描述：PeopleRecentlyFragment 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/5 16:54
 * 邮箱:814326663@qq.com
 */
public class PeopleRecentlyFragment extends Fragment implements PeopleRecentlyView,AdapterView.OnItemClickListener ,SwipyRefreshLayout.OnRefreshListener{
    private PeopleRecentlyPrestener prestener;
    private Dialog diallog;
    private SwipyRefreshLayout swipyrefresh;
    private ListView listview_people_recently;
    private List<JSONObject> peoples = new ArrayList<>();
    private PeopleRecentlyAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        diallog = HTApp.getInstance().createLoadingDialog(getBaseContext(),getString(R.string.loading));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View timeview = inflater.inflate(R.layout.activity_people_recently, container, false);
        initView(timeview);
        initData();
        setListener();
        return timeview;
    }

    private void setListener() {
        listview_people_recently.setOnItemClickListener(this);
        swipyrefresh.setOnRefreshListener(this);
    }

    private void initData() {
        adapter = new PeopleRecentlyAdapter(getBaseContext(), peoples);
        listview_people_recently.setAdapter(adapter);
        onRefresh(1);
    }

    private void initView(View timeview) {
        swipyrefresh = (SwipyRefreshLayout)timeview.findViewById(R.id.swipyrefresh);
        listview_people_recently = (ListView) timeview.findViewById(R.id.listview_people_recently);
    }

    @Override
    public void showLoadingDialog() {
        if (diallog!=null){
            diallog.show();
        }
    }

    @Override
    public void hideLoadingDialog() {
        if (diallog!=null){
            diallog.dismiss();
        }
        swipyrefresh.setRefreshing(false);
    }

    @Override
    public void onRequestSuccess(List<JSONObject> peopleList) {
        swipyrefresh.setRefreshing(false);
        peoples.addAll(peopleList);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onRequestFailed(String errorMsg) {
        swipyrefresh.setRefreshing(false);
        Toast.makeText(getBaseContext(), errorMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setPresenter(PeopleRecentlyPrestener presenter) {
        this.prestener = presenter;
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
        JSONObject item = adapter.getItem(position);
        prestener.onListClickListener(item);
    }

    @Override
    public void onRefresh(int index) {
        peoples.clear();
        index = 1;
        prestener.requestData(index,20,false);
    }

    @Override
    public void onLoad(int index) {
        index++;
        prestener.requestData(index,20,true);
    }

    @Override
    public void onDestroy() {
        prestener.onDestory();
        super.onDestroy();
    }
}
