package com.htmessage.yichat.acitivity.moments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.update.Constant;
import com.htmessage.update.data.UserManager;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.widget.swipyrefresh.SwipyRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class MomentsFriendActivity extends BaseActivity implements SwipyRefreshLayout.OnRefreshListener {

    private SwipyRefreshLayout pull_refresh_list;
    private List<JSONObject> articles = new ArrayList<JSONObject>();
    private MomentsFriendAdapter adapter;
    private ListView actualListView;
    private String userId;
    private String avatar;
    private String userNick;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1000:
                    JSONObject data = (JSONObject) msg.obj;
                    int pageIndex = msg.arg1;
                   String  backgroundMoment = data.getString("background");
                    adapter.setBackgroud(backgroundMoment);

                    JSONArray jsonArray = data.getJSONArray("list");
                    if (data != null) {
                        List<JSONObject> list = JSONArray.parseArray(jsonArray.toJSONString(), JSONObject.class);

                        if (pageIndex == 1) {
                            articles.clear();
                        }
                        articles.addAll(list);
                        adapter.notifyDataSetChanged();
                    }

                    pull_refresh_list.setRefreshing(false);
                    break;

                case 1001:
                    pull_refresh_list.setRefreshing(false);
                    break;


            }
        }
    };


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
            avatar = UserManager.get().getMyAvatar();
//            ivNotice.setVisibility(View.VISIBLE);
//            ivNotice.setOnClickListener(new OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    startActivity(new Intent(MomentsFriendActivity.this, MomentsNoticeActivity.class));
//                }
//
//            });
        }
        tvTitle.setText(userNick);
        initView();
    }

    private void getIntentData() {
        userId = this.getIntent().getStringExtra("userId");
        userNick=UserManager.get().getUserNick(userId);
        avatar=UserManager.get().getUserAvatar(userId);
    }

    private void initView() {
        pull_refresh_list = (SwipyRefreshLayout) findViewById(R.id.pull_refresh_list);
        actualListView = (ListView) findViewById(R.id.refresh_list);
        pull_refresh_list.setOnRefreshListener(this);
        adapter = new MomentsFriendAdapter(MomentsFriendActivity.this, articles, avatar);
        actualListView.setAdapter(adapter);
        getData(userId, 1);
    }

    private void getData(String friendID, final int pageIndex) {


        JSONObject body = new JSONObject();
        body.put("userId", friendID);
        body.put("pageNo", pageIndex);
        body.put("trendId", 10);
        ApiUtis.getInstance().postJSON(body, Constant.URL_trend_list, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (handler == null) {
                    return;
                }
                String code = jsonObject.getString("code");
                if ("0".equals(code)) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    Message message = handler.obtainMessage();
                    message.what = 1000;
                    message.arg1 = pageIndex;
                    message.obj = data;
                    message.sendToTarget();
                } else {

                    Message message = handler.obtainMessage();
                    message.what = 1001;
                    message.obj = R.string.api_error_5;
                    message.sendToTarget();
                }
            }

            @Override
            public void onFailure(int errorCode) {
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.obj = errorCode;
                message.sendToTarget();
            }
        });


//
//        List<Param> params = new ArrayList<>();
//        params.add(new Param("currentPage", pageIndex + ""));
//        params.add(new Param("pageSize", 20 + ""));
//        params.add(new Param("pageSize", friendID));
////        params.add(new Param("isFriend", isFriend));
//        new OkHttpUtils(MomentsFriendActivity.this).post(params, HTConstant.URL_SOCIAL_FRIEND, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                LoggerUtils.e(jsonObject.toJSONString());
//                int code = jsonObject.getInteger("code");
//                switch (code) {
//                    case 1:
//                        backgroundMoment.clear();
//                        String background = jsonObject.getString("backgrounds");
//                        String time = jsonObject.getString("time");
//                        JSONArray data = jsonObject.getJSONArray("data");
//                        if (data != null) {
//                            List<JSONObject> list = JSONArray.parseArray(data.toJSONString(), JSONObject.class);
//                            if (pageIndex == 1) {
//                                articles.clear();
//                                MmvkManger.getIntance().putJSONArray(cacheKey, data);
//                                MmvkManger.getIntance().putString(cacheKeyBg, background);
//                                MmvkManger.getIntance().putString(cacheKeyTime, time);
//                            }
//                            articles.addAll(list);
//                            backgroundMoment.add(background);
//                            serviceTimes.add(time);
//                        }
//                        break;
//                    case -1:
//                        if (pageIndex == 1) {
//                            MmvkManger.getIntance().putJSONArray(cacheKey, null);
//                            articles.clear();
//                            serviceTimes.clear();
//                        }
//                        CommonUtils.showToastShort(MomentsFriendActivity.this, R.string.has_nothing);
//                        break;
//                    default:
//                        if (pageIndex == 1) {
//                            MmvkManger.getIntance().putJSONArray(cacheKey, null);
//                            MmvkManger.getIntance().putString(cacheKeyBg, null);
//                            MmvkManger.getIntance().putString(cacheKeyTime, null);
//                            articles.clear();
//                            serviceTimes.clear();
//                            backgroundMoment.clear();
//                        }
//                        CommonUtils.showToastShort(MomentsFriendActivity.this, R.string.has_nothing);
//                        break;
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                CommonUtils.showToastShort(MomentsFriendActivity.this, getString(R.string.request_failed_msg) + errorMsg);
//                if (pageIndex == 1) {
//                    MmvkManger.getIntance().putJSONArray(cacheKey, null);
//                    MmvkManger.getIntance().putString(cacheKeyBg, null);
//                    MmvkManger.getIntance().putString(cacheKeyTime, null);
//                    articles.clear();
//                    serviceTimes.clear();
//                    backgroundMoment.clear();
//                }
//                adapter.notifyDataSetChanged();
//            }
//        });
//        pull_refresh_list.setRefreshing(false);
    }


    @Override
    protected void onResume() {
        super.onResume();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler = null;
    }
}
