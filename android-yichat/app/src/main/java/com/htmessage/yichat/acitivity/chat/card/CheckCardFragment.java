package com.htmessage.yichat.acitivity.chat.card;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.R;
import com.htmessage.yichat.domain.User;
import com.htmessage.yichat.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：YiChatDemoTwo
 * 类描述：CheckCardFragment 描述:
 * 创建人：songlijie
 * 创建时间：2017/11/17 11:55
 * 邮箱:814326663@qq.com
 */
public class CheckCardFragment extends Fragment implements AdapterView.OnItemClickListener, CheckCardView {
    private EditText edt_search;
    private ListView list;
    private CheckCardAdapter adapter;
    private CheckCardPresenter presenter;
    private List<User> contents;
    private ImageView iv_clear;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_check_card, container, false);
    }

    @Override
    public void onAttach(Context context) {
        presenter = new CheckCardPresenter(this);
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        list.setOnItemClickListener(this);
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    iv_clear.setVisibility(View.VISIBLE);
                    String str_s = edt_search.getText().toString().trim();
                    List<User> usersTemp = new ArrayList<User>();
                    for (User user : contents) {
                        if (user.getNick().contains(str_s)) {
                            usersTemp.add(user);
                        }
                    }
                    refreshList(usersTemp);
                } else {
                    iv_clear.setVisibility(View.VISIBLE);
                    refreshList(contents);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_search.getText().clear();
                iv_clear.setVisibility(View.GONE);
            }
        });
    }

    private void initData() {
        refreshList(contents);
    }

    private void initView() {
        list = (ListView) getView().findViewById(R.id.list);
        edt_search = (EditText) getView().findViewById(R.id.edt_search);
        iv_clear = (ImageView) getView().findViewById(R.id.iv_clear);
    }

    private void getData() {
        contents = presenter.getContents();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final User user = (User) list.getItemAtPosition(position);
        CommonUtils.showAlertDialog(getActivity(), getString(R.string.prompt), getString(R.string.send_card_or_no), new CommonUtils.OnDialogClickListener() {
            @Override
            public void onPriformClock() {
                Intent intent = new Intent();
                JSONObject object = new JSONObject();
                object.put(HTConstant.JSON_KEY_USERID,user.getUserId());
                object.put(HTConstant.JSON_KEY_NICK,user.getNick());
                object.put(HTConstant.JSON_KEY_AVATAR,user.getAvatar());
                 intent.putExtra("user", object.toJSONString());
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }

            @Override
            public void onCancleClock() {

            }
        });
    }

    @Override
    public void showToast(Object object) {
        CommonUtils.showToastShort(getActivity(), object);
    }

    @Override
    public void refreshList(List<User> users) {
        adapter = new CheckCardAdapter(getActivity(), users);
        list.setAdapter(adapter);
    }

    @Override
    public void setPresenter(CheckCardPresenter presenter) {

    }

    @Override
    public Context getBaseContext() {
        return getContext();
    }

    @Override
    public Activity getBaseActivity() {
        return getActivity();
    }
}
