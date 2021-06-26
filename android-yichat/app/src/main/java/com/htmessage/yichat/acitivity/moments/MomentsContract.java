package com.htmessage.yichat.acitivity.moments;

import android.content.Intent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.acitivity.BasePresenter;
import com.htmessage.yichat.acitivity.BaseView;

import java.util.List;

/**
 * Created by huangfangyi on 2017/7/11.
 * qq 84543217
 */

public interface MomentsContract {

    public interface View extends BaseView<Presenter> {
        void showInputMenu(int position, String aid);
        void showPicDialog(int type, String string);
        void showBackGroundPicDialog(int type, String string);
        void onRefreshComplete();
        void refreshListView(String time);
        void updateCommentView(int position, JSONArray jsonArray);
        void updateGoodView(int position, JSONArray jsonArray);
        void showBackground(String url);
    }

    public interface Presenter extends BasePresenter {
        //获取数据
            List<JSONObject> getData();
            String getBackgroudMoment();
             void loadeData(int pageIndex);
            void setGood(int position, String aid);
            void comment(int position, String aid, String content);
            void cancelGood(int position, String gid);
            void deleteComment(int position, String cid);
            void onBarRightViewClicked();
            void deleteItem(int position, String aid);
            void startToPhoto(int type);
            void startToAlbum(int type);
            void startToVideo(int type);
            void onResult(int requestCode, int resultCode, Intent data);
            void onBarRightViewLongClicked();
     }

}
