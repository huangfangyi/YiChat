package com.htmessage.fanxinht.acitivity.moments.details;

import android.os.Bundle;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.utils.OkHttpUtils;
import com.htmessage.fanxinht.utils.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangfangyi on 2017/7/22.
 * qq 84543217
 */

public class MomentsPresenter implements MomentsContract.Presenter {

    private  JSONObject data;
    private  MomentsContract.View detailView;
    public MomentsPresenter(MomentsContract.View detailView){
        this.detailView=detailView;
        detailView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    public void initData(Bundle bundle){

        String dataStr=bundle.getString("data");
        if(dataStr==null){
            String mid=bundle.getString("mid");
             getDataFromServer(mid);
        }else {
            String timeStamp=bundle.getString("time");
            data=JSONObject.parseObject(dataStr);
            detailView.initMomentView(data,timeStamp);
        }

    }

    @Override
    public void setGood(String aid) {

        // 更新后台
        List<Param> params = new ArrayList<>();
        params.add(new Param("tid", aid));
        new OkHttpUtils(detailView.getBaseContext()).post(params, HTConstant.URL_SOCIAL_GOOD, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getIntValue("code");
                switch (code) {
                    case 1:
                        JSONArray praises = jsonObject.getJSONArray("praises");
                        detailView.updateGoodView( praises);

                        break;
                    case -1:
                        Toast.makeText(detailView.getBaseContext(), R.string.praise_failed, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(detailView.getBaseContext(), R.string.praise_failed, Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(detailView.getBaseContext(), R.string.praise_failed_msg + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

    }



    @Override
    public void cancelGood(String gid) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("pid", gid));
        new OkHttpUtils(detailView.getBaseContext()).post(params, HTConstant.URL_SOCIAL_GOOD_CANCEL, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getIntValue("code");
                switch (code) {
                    case 1:
                        JSONArray praises = jsonObject.getJSONArray("praises");
                        detailView.updateGoodView( praises);
                        break;

                    default:
                        Toast.makeText(detailView.getBaseContext(), R.string.praise_cancle_fail, Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(detailView.getBaseContext(), R.string.praise_cancle_fail_msg + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void deleteComment(String cid) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("cid", cid));
        new OkHttpUtils(detailView.getBaseContext()).post(params, HTConstant.URL_SOCIAL_DELETE_COMMENT, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getIntValue("code");
                switch (code) {
                    case 1:
                        JSONArray comments = jsonObject.getJSONArray("comments");
                        detailView.updateCommentView( comments);
                        break;
                    default:
                        Toast.makeText(detailView.getBaseContext(), R.string.delete_comment_failed, Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(detailView.getBaseContext(), R.string.delete_comment_failed_msg + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void delete(String aid) {
        final List<Param> params = new ArrayList<>();
        params.add(new Param("tid", aid));
        new OkHttpUtils(detailView.getBaseContext()).post(params, HTConstant.URL_SOCIAL_DELETE, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getIntValue("code");
                switch (code) {
                    case 1:
                        detailView.finish();

                        break;

                    default:
                        Toast.makeText(detailView.getBaseContext(), R.string.delete_dynamic_failed, Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(detailView.getBaseContext(), R.string.delete_dynamic_failed_msg + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void comment(String content) {
        String aid=data.getString("id");
        List<Param> params = new ArrayList<>();
        params.add(new Param("tid", aid));
        params.add(new Param("content", content));
        new OkHttpUtils(detailView.getBaseContext()).post(params, HTConstant.URL_SOCIAL_COMMENT, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getIntValue("code");
                switch (code) {
                    case 1:
                        JSONArray comments = jsonObject.getJSONArray("comments");
                        detailView.updateCommentView( comments);
                        break;
                    case -1:
                        Toast.makeText(detailView.getBaseContext(), R.string.service_not_response, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(detailView.getBaseContext(), R.string.service_not_response, Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(detailView.getBaseContext(), R.string.service_not_response, Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void  getDataFromServer(String mid){
        List<Param> params=new ArrayList<>();
        params.add(new Param("tid",mid));
        new OkHttpUtils(detailView.getBaseContext()).post(params, HTConstant.URL_SOCIAL_GET_DETAIL, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code=jsonObject.getInteger("code");
                if(code==1){
                    JSONObject dataTemp=jsonObject.getJSONObject("data");
                    if(dataTemp!=null){
                        String serverTime=dataTemp.getString("serverTime");
                        data=dataTemp;
                        detailView.initMomentView(data,serverTime);
                    }
                }
            }

            @Override
            public void onFailure(String errorMsg) {

            }
        });

    }
}
