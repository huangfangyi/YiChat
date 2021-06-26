package com.htmessage.yichat.acitivity.moments.details;

import android.os.Bundle;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.acitivity.BasePresenter;
import com.htmessage.yichat.acitivity.BaseView;

/**
 * Created by huangfangyi on 2017/7/22.
 * qq 84543217
 */

public interface MomentsContract {

    public interface View extends BaseView<Presenter> {


        void finish();

        void initMomentView(JSONObject jsonObject);

        void updateGoodView(JSONArray praises);

        void updateCommentView(JSONArray comments);

    }


    public interface Presenter extends BasePresenter {

        void initData(Bundle bundle);

        void setGood(String aid);


        void cancelGood(String aid);

        void deleteComment(String cid);

        void delete(String aid);
        void comment(String content);


    }


}
