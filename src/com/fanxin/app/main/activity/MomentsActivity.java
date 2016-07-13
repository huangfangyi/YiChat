package com.fanxin.app.main.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fanxin.app.DemoApplication;
import com.fanxin.app.DemoHelper;
import com.fanxin.app.R;
import com.fanxin.app.main.FXConstant;
import com.fanxin.app.main.adapter.MomentsAdapter;
import com.fanxin.app.main.utils.OkHttpManager;
import com.fanxin.app.main.utils.Param;
import com.fanxin.app.main.widget.MomentsView;
import com.fanxin.app.ui.BaseActivity;
import com.fanxin.easeui.domain.EaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huangfangyi on 2016/7/10.\
 * QQ:84543217
 */
public class MomentsActivity extends BaseActivity {

    private List<JSONObject> articles = new ArrayList<JSONObject>();

    // private JSONArray datas = new JSONArray();
    private MomentsAdapter adapter;

    private int page = 0;
    private List<String> sIDs = new ArrayList<String>();
    private String friendID;

    private MomentsView momentsView;
    private String userID = DemoHelper.getInstance().getCurrentUsernName();

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.fx_activity_moments);
        initView();
        friendID = this.getIntent().getStringExtra("friendID");
        // if(friendID==null){
        // finish();
        // return;
        // }
        TextView tv_title = (TextView) this.findViewById(R.id.tv_title);
        // 此处应该换成昵称

        String nick = friendID;
        if (friendID.equals(userID)) {
            nick = DemoApplication.getInstance().getUserJson().getString(FXConstant.JSON_KEY_NICK);

        } else {

            EaseUser user = DemoHelper.getInstance().getContactList()
                    .get(friendID);

            nick = user.getNick();

        }

        tv_title.setText(nick);
        initView();
    }

    private void initView() {

        adapter = new MomentsAdapter(this, articles);
        momentsView = (MomentsView) findViewById(R.id.mementsView);
        momentsView.setAdapter(adapter);

        momentsView.setOnRefreshListener(new MomentsView.OnRefreshListener() {
            @Override
            public void onRefresh() {

                page = 0;
                getData(page);

//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        momentsView.stopRefresh();
//                    }
//                }, 2000);
            }
        });
//        momentsView.setOnMoreListener(new MomentsView.OnMoreDataListener() {
//            @Override
//            public void onMoreData(ListView listView) {
//                page++;
//                getData(page);
//            }
//        });
        momentsView.startRefresh();

    }

    private void getData(final int page_num) {

        List<Param> params = new ArrayList<>();
        params.add(new Param("userID", userID));
        params.add(new Param("friendID", friendID));
        params.add(new Param("num", String.valueOf(page_num)));

        OkHttpManager.getInstance().post(params, FXConstant.URL_SOCIAL_FRIEND, new OkHttpManager.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getInteger("code");
                if (code == 1000) {
                    JSONArray users_temp = jsonObject.getJSONArray("data");
                    String time = jsonObject.getString("time");
                    DemoApplication.getInstance().setTime(time);
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
                        momentsView.stopRefresh();
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
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String errorMsg) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        getData(0);
    }
}