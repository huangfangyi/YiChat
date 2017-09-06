package com.htmessage.fanxinht.acitivity.moments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;
import com.htmessage.fanxinht.utils.ACache;
import com.htmessage.fanxinht.utils.CommonUtils;
import com.htmessage.fanxinht.utils.OkHttpUtils;
import com.htmessage.fanxinht.utils.Param;
import com.htmessage.fanxinht.widget.swipyrefresh.SwipyRefreshLayout;
import java.util.ArrayList;
import java.util.List;

public class MomentsFriendActivity extends BaseActivity implements SwipyRefreshLayout.OnRefreshListener {

    private SwipyRefreshLayout pull_refresh_list;
    private List<JSONObject> articles = new ArrayList<JSONObject>();
    private MomentsFriendAdapter adapter;
    private ListView actualListView;
    private String isFriend = "0";//默认不经过是否是好友判断
    private String userId;
    private String avatar;
    private String userNick;
    private List<String> backgroundMoment = new ArrayList<>();
    private List<String> serviceTimes = new ArrayList<>();
    private String cacheKeyTime;
    private String cacheKey;
    private String cacheKeyBg;


    @Override
    protected void onCreate(Bundle arg0) {

        super.onCreate(arg0);
        setContentView(R.layout.activity_moments_me);
        getIntentData();
        if (userId == null) {
            finish();
            return;
        }
        TextView tvTitle = (TextView) this.findViewById(R.id.tv_title);
        ImageView ivNotice = (ImageView) this.findViewById(R.id.iv_notice);

        if (HTApp.getInstance().getUsername().equals(userId)) {
            userNick = getString(R.string.my_image_manager);
            avatar = HTApp.getInstance().getUserJson().getString(HTConstant.JSON_KEY_AVATAR);
            ivNotice.setVisibility(View.VISIBLE);
            ivNotice.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MomentsFriendActivity.this, MomentsNoticeActivity.class));
                }

            });
        }
        tvTitle.setText(userNick);
        initView();
    }

    private void getIntentData() {
        userId = this.getIntent().getStringExtra("userId");
        userNick = getIntent().getStringExtra("userNick");
        avatar = getIntent().getStringExtra("avatar");
        cacheKey = "mymoments" + userId;
        cacheKeyBg = userId + "mymomentbg";
        cacheKeyTime = userId + "cacheKeyTime";
        getMoments();
        getBackgroundMoment();
        getCatcheTime();
    }

    private void initView() {
        pull_refresh_list = (SwipyRefreshLayout) findViewById(R.id.pull_refresh_list);
        actualListView = (ListView) findViewById(R.id.refresh_list);
        pull_refresh_list.setOnRefreshListener(this);
        adapter = new MomentsFriendAdapter(MomentsFriendActivity.this, articles, avatar, backgroundMoment, serviceTimes);
        actualListView.setAdapter(adapter);
        getData(userId, 1);
    }

    private void getData(String friendID, final int pageIndex) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("currentPage", pageIndex + ""));
        params.add(new Param("pageSize", 20 + ""));
        params.add(new Param("userId", friendID));
//        params.add(new Param("isFriend", isFriend));
        new OkHttpUtils(MomentsFriendActivity.this).post(params, HTConstant.URL_SOCIAL_FRIEND, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getInteger("code");
                switch (code) {
                    case 1:
                        backgroundMoment.clear();
                        String background = jsonObject.getString("backgrounds");
                        String time = jsonObject.getString("time");
                        JSONArray data = jsonObject.getJSONArray("data");
                        if (data != null) {
                            List<JSONObject> list = JSONArray.parseArray(data.toJSONString(), JSONObject.class);
                            if (pageIndex == 1) {
                                articles.clear();
                                ACache.get(MomentsFriendActivity.this).put(cacheKey, data);
                                ACache.get(MomentsFriendActivity.this).put(cacheKeyBg, background);
                                ACache.get(MomentsFriendActivity.this).put(cacheKeyTime, time);
                            }
                            articles.addAll(list);
                            backgroundMoment.add(background);
                            serviceTimes.add(time);
                        }
                        break;
                    case -1:
                        if (pageIndex == 1) {
                            ACache.get(MomentsFriendActivity.this).put(cacheKey, "");
                            articles.clear();
                            serviceTimes.clear();
                        }
                        CommonUtils.showToastShort(MomentsFriendActivity.this, R.string.has_nothing);
                        break;
                    default:
                        if (pageIndex == 1) {
                            ACache.get(MomentsFriendActivity.this).put(cacheKey, "");
                            ACache.get(MomentsFriendActivity.this).put(cacheKeyBg, "");
                            ACache.get(MomentsFriendActivity.this).put(cacheKeyTime, "");
                            articles.clear();
                            serviceTimes.clear();
                            backgroundMoment.clear();
                        }
                        CommonUtils.showToastShort(MomentsFriendActivity.this, R.string.has_nothing);
                        break;
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMsg) {
                CommonUtils.showToastShort(MomentsFriendActivity.this, getString(R.string.request_failed_msg) + errorMsg);
                if (pageIndex == 1) {
                    ACache.get(MomentsFriendActivity.this).put(cacheKey, "");
                    ACache.get(MomentsFriendActivity.this).put(cacheKeyBg, "");
                    ACache.get(MomentsFriendActivity.this).put(cacheKeyTime, "");
                    articles.clear();
                    serviceTimes.clear();
                    backgroundMoment.clear();
                }
                adapter.notifyDataSetChanged();
            }
        });
        pull_refresh_list.setRefreshing(false);
    }

    private List<String> getBackgroundMoment() {
        backgroundMoment.clear();
        String bacground = ACache.get(MomentsFriendActivity.this).getAsString(cacheKeyBg);
        if (!TextUtils.isEmpty(bacground)) {
            backgroundMoment.add(bacground);
        }
        return backgroundMoment;
    }

    private List<JSONObject> getMoments() {
        JSONArray jsonArray = ACache.get(MomentsFriendActivity.this).getAsJSONArray(cacheKey);
        if (jsonArray != null) {
            List<JSONObject> list = JSONArray.parseArray(jsonArray.toJSONString(), JSONObject.class);
            articles.addAll(list);
        }
        return articles;
    }

    private List<String> getCatcheTime() {
        serviceTimes.clear();
        String time = ACache.get(MomentsFriendActivity.this).getAsString(cacheKeyTime);
        if (!TextUtils.isEmpty(time)) {
            serviceTimes.add(time);
        } else {
            serviceTimes.add(com.htmessage.fanxinht.utils.DateUtils.getStringTime(System.currentTimeMillis()));
        }
        return serviceTimes;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  getData(pageIndex, userId);
    }


    @Override
    public void onRefresh(int index) {
        index = 1;
        getData(userId, index);
    }

    @Override
    public void onLoad(int index) {
        index++;
        getData(userId, index);
    }
}
