package com.htmessage.yichat.acitivity.red.send;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.red.pay.RpPayActivity;
import com.htmessage.sdk.manager.MmvkManger;
import com.htmessage.yichat.utils.OkHttpUtils;
import com.htmessage.yichat.utils.Param;
import com.htmessage.yichat.utils.Validator;
import com.htmessage.sdk.utils.MessageUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：CityBz
 * 类描述：RedGroupSendFragment 描述:
 * 创建人：songlijie
 * 创建时间：2017/11/1 13:05
 * 邮箱:814326663@qq.com
 */
public class RedGroupSendFragment extends Fragment implements View.OnClickListener {
    private EditText et_peak_num, et_peak_amount, et_peak_message;
    private TextView tv_amount_for_show, pop_message, tv_group_member_num;
    private Button btn_putin;
    private String msg, groupId = "";
    private List<JSONObject> membersJSONArray = new ArrayList<>();
    private int maxRed = 100;
    private int maxMoney = 200;
    private int NUMBER = -1;
    private float AMOUNTMONEY = -1f;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_group_send_red, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();
        initView();
        initData();
        setListenter();
    }

    private void getData() {
        msg = getString(R.string.words_numal);
        groupId = getActivity().getIntent().getStringExtra(HTConstant.JSON_KEY_USERID);
        if (TextUtils.isEmpty(groupId)) {
            getActivity().finish();
            return;
        }
        JSONArray jsonArrayCache =   MmvkManger.getIntance().getJSONArray(HTApp.getInstance().getUsername() + groupId);
        arrayToList(jsonArrayCache, membersJSONArray);
    }

    private void setListenter() {
        btn_putin.setOnClickListener(this);
        et_peak_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                NUMBER = checkRedNum();
                if (NUMBER != -1) {
                    AMOUNTMONEY = checkAmount();
                }
                checkForAllAmount();
                setButtonEnable();
            }
        });
        et_peak_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                NUMBER = checkRedNum();
                if (NUMBER != -1) {
                    AMOUNTMONEY = checkAmount();
                }
                checkForAllAmount();
                setButtonEnable();
            }
        });
    }

    private void initData() {
        et_peak_num.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        et_peak_num.setFocusable(true);
        et_peak_num.setFocusableInTouchMode(true);
        et_peak_num.requestFocus();
        et_peak_num.setSelection(et_peak_num.getText().length());
        et_peak_amount.setFilters(new InputFilter[]{new InputMoney()});
        btn_putin.setEnabled(false);
        tv_group_member_num.setText(String.format(getString(R.string.group_total_people),String.valueOf(membersJSONArray.size())));
        refreshGroupMembersInserver();
    }

    private void initView() {
        et_peak_num = (EditText) getView().findViewById(R.id.et_peak_num);
        et_peak_amount = (EditText) getView().findViewById(R.id.et_peak_amount);
        et_peak_message = (EditText) getView().findViewById(R.id.et_peak_message);
        tv_amount_for_show = (TextView) getView().findViewById(R.id.tv_amount_for_show);
        tv_group_member_num = (TextView) getView().findViewById(R.id.tv_group_member_num);
        pop_message = (TextView) getView().findViewById(R.id.pop_message);
        btn_putin = (Button) getView().findViewById(R.id.btn_putin);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_putin:
                String num = et_peak_num.getText().toString().trim();
                String money = et_peak_amount.getText().toString().trim();
                String rp_msg = et_peak_message.getText().toString().trim();
                if (TextUtils.isEmpty(rp_msg)) {
                    rp_msg = msg;
                }
//                try{
//                    double moneyDoube = Double.parseDouble(money);
//                    double balance= MmvkManger.getIntance().getDouble(HTApp.get().getUserId()+"balance");
//                    if(moneyDoube>balance){
//                        showNotice("余额不足，请降低金额发送");
//                        return;
//                    }
//
//                }catch (NumberFormatException e){
//
//                }
                 Intent intent = new Intent(getActivity(), RpPayActivity.class);
                intent.putExtra("money", money);
                intent.putExtra("content", rp_msg);
                intent.putExtra("rundom", num);
                intent.putExtra("chatType", MessageUtils.CHAT_GROUP);
                intent.putExtra(HTConstant.JSON_KEY_USERID, groupId);
                getActivity().startActivityForResult(intent, 1000);
                break;
        }
    }

    private class InputMoney implements InputFilter {
        private InputMoney() {
        }

        public CharSequence filter(CharSequence var1, int var2, int var3, Spanned var4, int var5, int var6) {
            if (var1.toString().equals(".") && var5 == 0 && var6 == 0) {
                et_peak_amount.setText("0" + var1 + var4);
                et_peak_amount.setSelection(2);
            }

            return var5 >= 8 ? "" : (var4.toString().indexOf(".") != -1 && var4.length() - var4.toString().indexOf(".") > 2 && var4.length() - var5 < 3 ? "" : null);
        }
    }

    private void setButtonEnable() {
        btn_putin.setEnabled(false);
        if (NUMBER > 0 && AMOUNTMONEY > 0.0F) {
            btn_putin.setEnabled(true);
        }
    }
    private int curretRdNum=0;

    private int checkRedNum() {
        String str = et_peak_num.getText().toString();
        if (!Validator.isEmpty(str)) {
            if (Validator.isNumber(str)) {
                BigDecimal bd = new BigDecimal(str);
                int num = bd.intValue();
                curretRdNum=num;
                if (num == 0) {
                    showNotice(getString(R.string.rp_just_one));
                    return -1;
                } else if (num > maxRed) {
                    showNotice(String.format(getString(R.string.rp_max_one), maxRed + ""));
                    return -1;
                } else {
                    hideNotice();
                    return num;
                }
            } else {
                showNotice(getString(R.string.rp_input_right_cumt));
                return -1;
            }
        } else {
            hideNotice();
            return -1;
        }
    }

    //金额验证
    private float checkAmount() {
        String str = et_peak_amount.getText().toString();
        if (!Validator.isEmpty(str)) {
            if (!str.startsWith(".")) {
                BigDecimal bd = new BigDecimal(str);
                float amount = bd.floatValue();
                if (amount == 0) {
                    hideNotice();
                    return -1;
                } else {
                    if (amount < 0.01f) {
                        showNotice(getString(R.string.one_rp_not_1));
                        return -1;
                    } else {
//                        String numStr = et_peak_num.getText().toString();
//                        if (numStr != null && !numStr.isEmpty()) {
//                            float num = 0;
//                            num = Validator.formatMoneyFloat(numStr);
//                            if (num != 0 && num * amount > maxMoney) {
//                                showNotice(String.format(getString(R.string.total_rp_max), Validator.formatMoney(maxMoney)));
//                                return -1;
//                            } else {
//                                hideNotice();
//                                return amount;
//                            }
//                        } else {
                        if (amount > maxMoney*curretRdNum) {
                            showNotice(String.format(getString(R.string.total_rp_max), Validator.formatMoney(maxMoney*curretRdNum)));
                            return -1;
                        } else {
                            hideNotice();
                            return amount;
                        }
//                        }
                    }
                }
            } else {
                showNotice(getString(R.string.one_rp_right));
                return -1;
            }
        } else {
            hideNotice();
            return -1;
        }
    }

    private void checkForAllAmount() {
        int NUM = -1;
        String str1 = et_peak_num.getText().toString();
        if (!Validator.isEmpty(str1)) {
            if (Validator.isNumber(str1)) {
                BigDecimal bd = new BigDecimal(str1);
                NUM = bd.intValue();
            }
        }
        float AMOUNT = 0f;
        String str2 = et_peak_amount.getText().toString();
        if (!Validator.isEmpty(str2)) {
            if (!str2.startsWith(".")) {
                BigDecimal bd = new BigDecimal(str2);
                AMOUNT = bd.floatValue();
            }
        }
        if (NUM > 0 && AMOUNT > 0) {
            BigDecimal zonggong = new BigDecimal(str2);
            zonggong = zonggong.setScale(2, BigDecimal.ROUND_HALF_UP);
            tv_amount_for_show.setText(zonggong.toString());
        } else {
            tv_amount_for_show.setText(R.string.money_numal);
        }
    }

    private void showNotice(String var1) {
        pop_message.setText(var1);
        pop_message.setVisibility(View.VISIBLE);
    }

    private void hideNotice() {
        pop_message.setText("");
        pop_message.setVisibility(View.INVISIBLE);
    }

    private void arrayToList(JSONArray jsonArray, List<JSONObject> jsonObjects) {
        jsonObjects.clear();
        if (jsonArray == null) {
            return;
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObjectTemp = jsonArray.getJSONObject(i);
            if (!jsonObjects.contains(jsonObjectTemp)) {
                jsonObjects.add(jsonObjectTemp);
            }
        }
    }

    public void refreshGroupMembersInserver() {
        List<Param> params = new ArrayList<>();
        params.add(new Param("gid", groupId));
        params.add(new Param("uid", HTApp.getInstance().getUsername()));
        new OkHttpUtils(getActivity()).post(params, HTConstant.URL_GROUP_MEMBERS, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject.containsKey("code")) {
                    int code = Integer.parseInt(jsonObject.getString("code"));
                    if (code == 1000) {
                        if (jsonObject.containsKey("data") && jsonObject.get("data") instanceof JSONArray) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            if (jsonArray != null && jsonArray.size() != 0) {
                              MmvkManger.getIntance().putJSONArray(HTApp.getInstance().getUsername() + groupId, jsonArray);
                                arrayToList(jsonArray, membersJSONArray);
                                tv_group_member_num.setText(String.format(getString(R.string.group_total_people),String.valueOf(membersJSONArray.size())));
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                tv_group_member_num.setText(String.format(getString(R.string.group_total_people),String.valueOf(membersJSONArray.size())));
            }
        });
    }
}
