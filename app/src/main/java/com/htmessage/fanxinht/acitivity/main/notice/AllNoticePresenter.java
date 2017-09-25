package com.htmessage.fanxinht.acitivity.main.notice;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.utils.OkHttpUtils;
import com.htmessage.fanxinht.utils.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：PersonalTailor
 * 类描述：AllNoticePresenter 描述:
 * 创建人：songlijie
 * 创建时间：2017/8/2 16:55
 * 邮箱:814326663@qq.com
 */
public class AllNoticePresenter implements AllNoticeBasePresenter {
    private AllNoticeView allNoticeView;
    private List<JSONObject> objectList = new ArrayList<>();


    public AllNoticePresenter(AllNoticeView allNoticeView) {
        this.allNoticeView = allNoticeView;
        this.allNoticeView.setPresenter(this);
    }

    @Override
    public List<JSONObject> getAllNotice() {
        return objectList;
    }


    @Override
    public void onDestory() {
        allNoticeView = null;
    }

    @Override
    public void onRefresh() {
        objectList.clear();
        getAllNotice(1);
    }

    @Override
    public void onLoadMore(int page) {
        if (objectList.size() < 20) {
            onRefresh();
            return;
        }
        getAllNotice(page);
    }

    @Override
    public void start() {

    }

    private void getAllNotice(int page) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("currentPage", String.valueOf(page)));
        params.add(new Param("pageSize", "20"));
        new OkHttpUtils(allNoticeView.getBaseActivity()).post(params, HTConstant.URL_GET_ALL_NOTICE_LIST, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getIntValue("code");
                switch (code) {
                    case 1:
                        JSONArray data = jsonObject.getJSONArray("data");
                        if (data != null && data.size() != 0) {
                            for (int i = 0; i < data.size(); i++) {
                                JSONObject object = data.getJSONObject(i);
                                if (!objectList.contains(object)) {
                                    objectList.add(object);
                                }
                            }
                        }
                        break;
                    case -1:
                        allNoticeView.showToast(allNoticeView.getBaseActivity().getString(R.string.not_more_msg));
                        break;
                    default:
                        break;
                }
                allNoticeView.RefreshList();
            }

            @Override
            public void onFailure(String errorMsg) {
                allNoticeView.showToast(errorMsg);
                allNoticeView.RefreshList();
            }
        });
        allNoticeView.cancleRefresh();
    }
}
