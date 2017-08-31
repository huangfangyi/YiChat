package com.htmessage.fanxinht.acitivity.main.find.recentlypeople;

import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.main.details.UserDetailsActivity;
import com.htmessage.fanxinht.utils.OkHttpUtils;
import com.htmessage.fanxinht.utils.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：yichat0504
 * 类描述：PeopleRecentlyPrestener 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/5 16:34
 * 邮箱:814326663@qq.com
 */
public class PeopleRecentlyPrestener implements PeopleRecentlyBasePrestener {
    private PeopleRecentlyView timeView;

    public PeopleRecentlyPrestener(PeopleRecentlyView timeView) {
        this.timeView = timeView;
        this.timeView.setPresenter(this);
    }

    @Override
    public void requestData(int page, int pageSize, final boolean loadMore) {
        timeView.showLoadingDialog();
        List<Param> params = new ArrayList<>();
        params.add(new Param("currentPage",String.valueOf(page)));
        params.add(new Param("pageSize",String.valueOf(pageSize)));
        new OkHttpUtils(timeView.getBaseContext()).post(params, HTConstant.URL_GET_RECENTLY_PEOPLE, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                timeView.hideLoadingDialog();
                int code = jsonObject.getIntValue("code");
                switch (code){
                    case 1:
                        List<JSONObject> peoples = new ArrayList<JSONObject>();
                        JSONArray data = jsonObject.getJSONArray("data");
                        if (data!=null && data.size()!=0){
                            for (int i = 0; i <data.size() ; i++) {
                                JSONObject object = data.getJSONObject(i);
                                if (!peoples.contains(object)){
                                    peoples.add(object);
                                }
                            }
                            timeView.onRequestSuccess(peoples);
                        }else{
                            timeView.onRequestFailed(timeView.getBaseContext().getString(R.string.load_failed));
                        }
                        break;
                    default:
                        if (loadMore){
                            timeView.onRequestFailed(timeView.getBaseContext().getString(R.string.load_more_failed));
                        }else{
                            timeView.onRequestFailed(timeView.getBaseContext().getString(R.string.load_failed));
                        }
                        break;
                }
                Log.d("slj","----最近上线的人:"+jsonObject.toJSONString());
            }

            @Override
            public void onFailure(String errorMsg) {
                timeView.hideLoadingDialog();
                timeView.onRequestFailed(errorMsg);
            }
        });
    }

    @Override
    public void onListClickListener(JSONObject object) {
        timeView.getBaseActivity().startActivity(new Intent(timeView.getBaseActivity(), UserDetailsActivity.class).putExtra(HTConstant.KEY_USER_INFO,object.toJSONString()));
    }

    @Override
    public void onDestory() {
        timeView = null;
    }

    @Override
    public void start() {

    }
}
