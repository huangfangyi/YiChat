package com.htmessage.yichat.acitivity.moments.details;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ScrollView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.chat.video.VideoPlayActivity;
import com.htmessage.yichat.acitivity.details.UserDetailActivity;
import com.htmessage.yichat.acitivity.moments.BigImageActivity;
import com.htmessage.yichat.acitivity.moments.widget.MomentsItemView;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.HTMessageUtils;
import com.htmessage.yichat.widget.HTAlertDialog;

import java.util.ArrayList;

/**
 * Created by huangfangyi on 2017/7/22.
 * qq 84543217
 */

public class MomentsDetailFragment extends Fragment implements MomentsContract.View {

    private MomentsContract.Presenter presenter;
    private MomentsItemView momentsItemView;
    private ScrollView scrollView;

    @Override
    public void setPresenter(MomentsContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Context getBaseContext() {
        return getContext();
    }

    @Override
    public Activity getBaseActivity() {
        return getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_moments_detail, container, false);
        momentsItemView = (MomentsItemView) root.findViewById(R.id.momentsItemView);
        scrollView = (ScrollView) root.findViewById(R.id.scrollView);
        getBaseActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        presenter.initData(getArguments());
    }

    @Override
    public void finish() {
        getActivity().finish();
    }

    @Override
    public void initMomentView(JSONObject jsonObject) {
        momentsItemView.initView(jsonObject);
        momentsItemView.setOnMenuClickListener(new MomentsItemView.OnMenuClickListener() {
            @Override
            public void onUserClicked(String userId) {
                startActivity(new Intent(getContext(), UserDetailActivity.class).putExtra("userId", userId));

            }

            @Override
            public void onGoodIconClicked(String aid) {
                presenter.setGood(aid);

            }

            @Override
            public void onCommentIconClicked(String aid) {
                showBottomInputDialog(aid);
            }

            @Override
            public void onCancelGoodClicked(String gid) {
                presenter.cancelGood(gid);
            }

            @Override
            public void onCommentDeleteCilcked(String cid) {

                showDelCommentDilog(cid);
            }

            @Override
            public void onDeleted(String aid) {
                presenter.delete(aid);
            }

            @Override
            public void onImageListClicked(int index, ArrayList<String> images) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), BigImageActivity.class);
                intent.putExtra("images", images.toArray(new String[images.size()]));
                intent.putExtra("page", index);
                getActivity().startActivity(intent);
            }

            @Override
            public void onVideoClick(final String videoPath) {
                Log.d("videoPath----->",videoPath);
                HTMessageUtils.loadVideoFromService(getActivity(), videoPath, new HTMessageUtils.CallBack() {
                    @Override
                    public void error() {
                        CommonUtils.showToastShort(getBaseContext(), "加载失败");
                    }

                    @Override
                    public void completed(String localPath) {
                        String videoName = videoPath.substring(videoPath.lastIndexOf("/") + 1);
                        Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
                        intent.putExtra(VideoPlayActivity.VIDEO_NAME, videoName);
                        intent.putExtra(VideoPlayActivity.VIDEO_PATH, localPath);
                        Log.d("localPath--->",localPath);
                        getActivity().startActivity(intent);
                    }
                });
            }

            @Override
            public void onSeeClick(String aid, String userIds, String type) {
             }
        });
    }

    @Override
    public void updateGoodView(JSONArray praises) {
        momentsItemView.updateGoodView(praises);

    }

    @Override
    public void updateCommentView(JSONArray comments) {
        momentsItemView.updateCommentView(comments);
    }


    private void showDelCommentDilog(final String cid) {
        HTAlertDialog HTAlertDialog = new HTAlertDialog(getActivity(), null, new String[]{getString(R.string.delete)});
        HTAlertDialog.init(new com.htmessage.yichat.widget.HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int itemIndex) {
                switch (itemIndex) {
                    case 0:
                        presenter.deleteComment(cid);
                        break;
                }
            }
        });
    }

    /**
     * 显示评论的输入框
     *
     * @param aid
     */
    private void showBottomInputDialog(String aid) {
        CommonUtils.showMomentBottomInputEditFragment(getChildFragmentManager(), new CommonUtils.DialogClickListener() {
            @Override
            public void onCancleClock() {
                scrollViewFocusDown();
            }

            @Override
            public void onPriformClock(String comment) {
                if (TextUtils.isEmpty(comment)) {
                    CommonUtils.showToastShort(getContext(), R.string.input_talks);
                    return;
                }
                presenter.comment(comment);
                scrollViewFocusDown();
            }
        });
    }

    private void scrollViewFocusDown() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);

            }
        }, 800);

    }
}
