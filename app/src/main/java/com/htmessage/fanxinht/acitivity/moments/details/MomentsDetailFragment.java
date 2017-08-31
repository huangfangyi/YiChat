package com.htmessage.fanxinht.acitivity.moments.details;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.main.details.UserDetailsActivity;
import com.htmessage.fanxinht.acitivity.moments.BigImageActivity;
import com.htmessage.fanxinht.acitivity.moments.widget.MomentsItemView;
import com.htmessage.fanxinht.widget.HTAlertDialog;

import java.util.ArrayList;

/**
 * Created by huangfangyi on 2017/7/22.
 * qq 84543217
 */

public class MomentsDetailFragment extends Fragment implements MomentsContract.View {

    private MomentsContract.Presenter presenter;
    private MomentsItemView momentsItemView;
    private EditText etComment;
    private Button buttonSend;
    private InputMethodManager inputMethodManager;
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
        etComment = (EditText) root.findViewById(R.id.et_comment);
        buttonSend = (Button) root.findViewById(R.id.btn_send);
        scrollView = (ScrollView) root.findViewById(R.id.scrollView);
        inputMethodManager = (InputMethodManager) getBaseActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        getBaseActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        presenter.initData(getArguments());
        buttonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String comment = etComment.getText().toString();
                if (TextUtils.isEmpty(comment)) {
                    Toast.makeText(getContext(), R.string.input_talks, Toast.LENGTH_SHORT).show();
                    return;
                }
                presenter.comment(comment);
                etComment.setText("");
                hideInputMenu();
               // scrollViewFocusDown();

            }

        });

        etComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 scrollViewFocusDown();
            }
        });

    }

    @Override
    public void finish() {
        getActivity().finish();
    }

    @Override
    public void initMomentView(JSONObject jsonObject, String tiemStamp) {
        momentsItemView.initView(jsonObject, tiemStamp);
        momentsItemView.setOnMenuClickListener(new MomentsItemView.OnMenuClickListener() {
            @Override
            public void onUserClicked(String userId) {
                startActivity(new Intent(getContext(), UserDetailsActivity.class).putExtra("userId", userId));

            }

            @Override
            public void onGoodIconClicked(String aid) {
                presenter.setGood(aid);

            }

            @Override
            public void onCommentIconClicked(String aid) {
                Log.d("cid---->", aid);

                showInputMenu();
            }

            @Override
            public void onCancelGoodClicked(String gid) {
                presenter.cancelGood(gid);
            }

            @Override
            public void onCommentDeleteCilcked(String cid) {


                showDelCommentDilog(cid);
                // presenter.deleteComment(cid);
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
        HTAlertDialog HTAlertDialog = new HTAlertDialog(getActivity(), null, new String[]{"删除"});
        HTAlertDialog.init(new HTAlertDialog.OnItemClickListner() {
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

     public void hideInputMenu() {
        inputMethodManager.hideSoftInputFromWindow(getBaseActivity().getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


    }

    private void showInputMenu() {

        etComment.requestFocus();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                inputMethodManager.showSoftInput(etComment, 0);

            }
        },300);

        scrollViewFocusDown();
    }

    private void scrollViewFocusDown(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);

            }
        },800);

    }

}
