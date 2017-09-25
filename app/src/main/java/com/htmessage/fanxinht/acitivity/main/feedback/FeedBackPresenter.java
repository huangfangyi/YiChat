package com.htmessage.fanxinht.acitivity.main.feedback;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.utils.CommonUtils;
import com.htmessage.fanxinht.utils.OkHttpUtils;
import com.htmessage.fanxinht.utils.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：PersonalTailor
 * 类描述：FeedBackPresenter 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/31 16:25
 * 邮箱:814326663@qq.com
 */
public class FeedBackPresenter implements FeedBackBasePresenter {

    private FeedBackView feedBackView;

    public FeedBackPresenter(FeedBackView feedBackView) {
        this.feedBackView = feedBackView;
        this.feedBackView.setPresenter(this);
    }

    @Override
    public void onDestory() {
        feedBackView = null;
    }

    @Override
    public void sendFeedBack(String content) {
        CommonUtils.showDialogNumal(feedBackView.getBaseActivity(), feedBackView.getBaseActivity().getString(R.string.feedbacking));
        List<Param> params = new ArrayList<>();
        params.add(new Param("content", content));
        new OkHttpUtils(feedBackView.getBaseActivity()).post(params, HTConstant.URL_FEEDBACK, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getIntValue("code");
                CommonUtils.cencelDialog();
                switch (code) {
                    case 1:
                        feedBackView.showToast(R.string.fb_content_success);
                        feedBackView.getBaseActivity().finish();
                        break;
                    default:
                        feedBackView.showToast(R.string.fb_content_failed);
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                CommonUtils.cencelDialog();
                feedBackView.showToast(R.string.fb_content_failed);
            }
        });
    }

    @Override
    public void start() {

    }
}
