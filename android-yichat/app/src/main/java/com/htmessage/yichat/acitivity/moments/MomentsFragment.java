package com.htmessage.yichat.acitivity.moments;

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
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.chat.video.VideoPlayActivity;
import com.htmessage.yichat.acitivity.details.UserDetailActivity;
import com.htmessage.yichat.acitivity.moments.widget.MomentsItemView;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.HTMessageUtils;
import com.htmessage.yichat.widget.HTAlertDialog;
import com.htmessage.yichat.widget.swipyrefresh.SwipyRefreshLayout;

import java.util.ArrayList;

/**
 * Created by huangfangyi on 2017/7/10.
 * qq 84543217
 */

public class  MomentsFragment extends Fragment implements MomentsContract.View, SwipyRefreshLayout.OnRefreshListener {
    private SwipyRefreshLayout pullToRefreshListView;
    private ListView actualListView;
    private MomentsAdapter adapter;
    private MomentsContract.Presenter presenter;
    private AdapterListener adapterListener;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getBaseActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        adapter = new MomentsAdapter(getActivity(), presenter.getData(), presenter.getBackgroudMoment());

        adapterListener = new AdapterListener() {
            @Override
            public void onUserClicked(int position, String userId) {
                startActivity(new Intent(getContext(), UserDetailActivity.class).putExtra("userId", userId));
            }

            @Override
            public void onPraised(int position, String aid) {
                presenter.setGood(position, aid);
            }

            @Override
            public void onCommented(int position, String aid) {
                showInputMenu(position, aid);
            }

            @Override
            public void onCancelPraised(int position, String gid) {
                presenter.cancelGood(position, gid);

            }

            @Override
            public void onCommentDelete(int position, String cid) {
                showDelCommentDilog(position, cid);
            }

            @Override
            public void onDeleted(int position, String aid) {
                presenter.deleteItem(position, aid);
            }
            @Override
            public void onSeeClick(int position, String aid,String userIds,String type) {
             }

            @Override
            public void onVideoViewCLick(int position, final String videoPath) {
                Log.d("videoPath----->",videoPath);
                HTMessageUtils.loadVideoFromService(getActivity(), videoPath, new HTMessageUtils.CallBack() {
                    @Override
                    public void error() {
                        CommonUtils.showToastShort(getBaseContext(), R.string.set_failed);
                    }

                    @Override
                    public void completed(String localPath) {
                        String videoName = videoPath.substring(videoPath.lastIndexOf("/") + 1);
                        Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
                        intent.putExtra(VideoPlayActivity.VIDEO_NAME, videoName);
                        intent.putExtra(VideoPlayActivity.VIDEO_PATH, localPath);
                        startActivity(intent);
                    }
                });
            }
            @Override
            public void onImageClicked(int position, int index, ArrayList<String> images) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), BigImageActivity.class);
                intent.putExtra("images", images.toArray(new String[images.size()]));
                intent.putExtra("page", index);
                getActivity().startActivity(intent);
            }

            @Override
            public void onMomentTopBackGroundClock() {
                showBackGroundPicDialog(2, getString(R.string.change_moment_bg));
            }
        };
        adapter.setListener(adapterListener);

        actualListView.setAdapter(adapter);
        presenter.loadeData(1);

    }

    @Override
    public void onRefresh(int index) {
        index = 1;
        presenter.loadeData(index);
    }

    @Override
    public void onLoad(int index) {
        index++;
        presenter.loadeData(index);
    }




    private void showDelCommentDilog(final int position, final String cid) {
        HTAlertDialog HTAlertDialog = new HTAlertDialog(getActivity(), null, new String[]{getString(R.string.delete)});
        HTAlertDialog.init(new com.htmessage.yichat.widget.HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int itemIndex) {
                switch (itemIndex) {
                    case 0:
                        presenter.deleteComment(position, cid);
                        break;
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_moments, container, false);
        pullToRefreshListView = (SwipyRefreshLayout) root.findViewById(R.id.pull_refresh_list);
        actualListView = (ListView) root.findViewById(R.id.refresh_list);
        pullToRefreshListView.setOnRefreshListener(this);
        return root;
    }

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


    @Override
    public void showInputMenu(final int position, final String aid) {
        CommonUtils.showMomentBottomInputEditFragment(getChildFragmentManager(), new CommonUtils.DialogClickListener() {
            @Override
            public void onCancleClock() {

            }

            @Override
            public void onPriformClock(String msg) {
                if (TextUtils.isEmpty(msg)) {
                    CommonUtils.showToastShort(getContext(), R.string.input_talks);
                    return;
                }
                presenter.comment(position, aid, msg);
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                int listviewHeight = actualListView.getMeasuredHeight();
                MomentsItemView momentsItemView = adapter.getItemView(position);
                int itemHeight = momentsItemView.getMeasuredHeight();
//                actualListView.setSelectionFromTop(position + 2, listviewHeight - itemHeight);
                actualListView.setSelectionFromTop(position, -(itemHeight + 10));

            }
        }, 1000);


    }

    public void updateCommentView(int position, JSONArray jsonArray) {
        MomentsItemView momentsItemView = adapter.getItemView(position);
        momentsItemView.updateCommentView(jsonArray);

    }

    @Override
    public void updateGoodView(int position, JSONArray jsonArray) {
        MomentsItemView momentsItemView = adapter.getItemView(position);
        momentsItemView.updateGoodView(jsonArray);
    }

    @Override
    public void showBackground(String url) {
        adapter.setBackground(url);
    }

    @Override
    public void showPicDialog(final int type, String title) {
        HTAlertDialog HTAlertDialog = new HTAlertDialog(getContext(), title, new String[]{getString(R.string.small_video), getString(R.string.image_manager)});
        HTAlertDialog.init(new com.htmessage.yichat.widget.HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        presenter.startToVideo(type);
                        break;
                    case 1:
                        presenter.startToAlbum(type);
                        break;
                }
            }
        });
    }


    @Override
    public void showBackGroundPicDialog(final int type, String title) {
        HTAlertDialog HTAlertDialog = new HTAlertDialog(getContext(), title, new String[]{getString(R.string.attach_take_pic), getString(R.string.image_manager)});
        HTAlertDialog.init(new com.htmessage.yichat.widget.HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        presenter.startToPhoto(type);
                        break;
                    case 1:
                        presenter.startToAlbum(type);
                        break;
                }
            }
        });
    }



    @Override
    public void onRefreshComplete() {
        pullToRefreshListView.setRefreshing(false);
    }

    @Override
    public void refreshListView(String time) {

        adapter.notifyDataSetChanged();

    }


}
