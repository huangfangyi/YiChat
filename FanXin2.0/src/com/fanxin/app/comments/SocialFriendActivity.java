package com.fanxin.app.comments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.fanxin.app.Constant;
import com.fanxin.app.MYApplication;
import com.fanxin.app.R;
import com.fanxin.app.comments.SocialApiTask.DataCallBack;
import com.fanxin.app.domain.User;
import com.fanxin.app.fx.others.LocalUserInfo;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class SocialFriendActivity extends BaseActivity {

    private PullToRefreshListView pull_refresh_list;
    private List<JSONObject> articles = new ArrayList<JSONObject>();

    // private JSONArray datas = new JSONArray();
    private SocialFriendAdapter adapter;
    private ListView actualListView;
    private int page = 0;

    String userID;
    List<String> sIDs = new ArrayList<String>();
    String friendID;

    @Override
    protected void onCreate(Bundle arg0) {

        super.onCreate(arg0);
        setContentView(R.layout.activity_social_friend);
        userID = MYApplication.getInstance().getUserName();

        System.out.println("上传数据------->>>>>>>>" + "userID" + ":" + userID);

        friendID = this.getIntent().getStringExtra("friendID");
        // if(friendID==null){
        // finish();
        // return;
        // }
        TextView tv_title = (TextView) this.findViewById(R.id.tv_title);
        // 此处应该换成昵称

        String nick_temp = friendID;
        if (friendID.equals(userID)) {
            nick_temp = LocalUserInfo.getInstance(getApplicationContext())
                    .getUserInfo("nick");

        } else {

            User user = MYApplication.getInstance().getContactList()
                    .get(friendID);
            if (user != null) {
                nick_temp = user.getNick();
            }
        }

        tv_title.setText(nick_temp);
        initView();
    }

    private void initView() {

        pull_refresh_list = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        pull_refresh_list.setMode(Mode.BOTH);

        pull_refresh_list
                .setOnRefreshListener(new OnRefreshListener<ListView>() {
                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        String label = DateUtils.formatDateTime(
                                SocialFriendActivity.this,
                                System.currentTimeMillis(),
                                DateUtils.FORMAT_SHOW_TIME
                                        | DateUtils.FORMAT_SHOW_DATE
                                        | DateUtils.FORMAT_ABBREV_ALL);

                        // Update the LastUpdatedLabel
                        refreshView.getLoadingLayoutProxy()
                                .setLastUpdatedLabel(label);

                        // Do work to refresh the list here.

                        if (pull_refresh_list.getCurrentMode() == Mode.PULL_FROM_START) {
                            page = 0;

                        } else if (pull_refresh_list.getCurrentMode() == Mode.PULL_FROM_END) {
                            page++;

                        }

                        getData(page);
                    }
                });

        actualListView = pull_refresh_list.getRefreshableView();
        adapter = new SocialFriendAdapter(SocialFriendActivity.this, articles);
        actualListView.setAdapter(adapter);
        actualListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                if (position != 1) {
                    Log.e("position----->>", String.valueOf(position));
                    JSONObject json = adapter.getJSONs().get(position - 2);
                    startActivity(new Intent(SocialFriendActivity.this,
                            SocialDetailActivity.class).putExtra("json",
                            json.toJSONString()));
                }
            }

        });
        getData(0);
        pull_refresh_list.setRefreshing(false);

    }

    private void getData(final int page_num) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("userID", userID);
        map.put("friendID", friendID);
        map.put("num", String.valueOf(page_num));
        SocialApiTask task = new SocialApiTask(SocialFriendActivity.this,
                Constant.URL_SOCIAL_FRIEND, map);
        task.getData(new DataCallBack() {
            @Override
            public void onDataCallBack(JSONObject data) {
                pull_refresh_list.onRefreshComplete();
                if (data == null) {

                    return;

                }
                int code = data.getInteger("code");
                if (code == 1000) {
                    JSONArray users_temp = data.getJSONArray("data");
                    String time = data.getString("time");
                    MYApplication.getInstance().setTime(time);
                    if (page_num == 0) {

                        // datas = users_temp;
                        articles.clear();
                        sIDs.clear();
                        for (int i = 0; i < users_temp.size(); i++) {
                            JSONObject json = users_temp.getJSONObject(i);
                            String sID = json.getString("sID");
                            sIDs.add(sID);
                            articles.add(json);
                        }

                    } else {

                        Map<String, JSONObject> map = new HashMap<String, JSONObject>();

                        for (int i = 0; i < users_temp.size(); i++) {
                            JSONObject json = users_temp.getJSONObject(i);
                            String sID = json.getString("sID");
                            if (!sIDs.contains(sID)) {
                                sIDs.add(sID);
                                articles.add(json);
                            }
                        }

                    }
                    // adapter = new
                    // SocialFriendAdapter(SocialMainActivity.this,
                    // datas, time);
                    // actualListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    // ACache.get(getActivity()).put("last_login", users);

                } else {
                    // ToastUtil.showMessage("服务器出错...");
                }
            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        getData(0);
    }

}
