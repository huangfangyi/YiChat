package com.htmessage.yichat.acitivity.red.send;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.red.pay.RpPayActivity;
import com.htmessage.yichat.utils.Validator;
import com.htmessage.sdk.utils.MessageUtils;

import java.math.BigDecimal;


/**
 * 项目名称：Treasure
 * 类描述：RedSendFragment 描述:
 * 创建人：songlijie
 * 创建时间：2017/9/28 13:52
 * 邮箱:814326663@qq.com
 */
public class RedSendFragment extends Fragment implements View.OnClickListener {
    private TextView tv_show_money, tv_money, tv_money_size, tv_notice, tv_input_money, tv_show_input_msg;
    private EditText edt_input_message, edt_input_money;
    private Button btn_inset_money;
    private String userId;
    private String msg = null;
    boolean isTransfer = false;//红包false 转账true

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_red_send, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isTransfer = getArguments().getBoolean("isTransfer");
        getData();
        initView();
        initData();
        setListener();

        if (isTransfer) {
            tv_show_input_msg.setHint("");
            btn_inset_money.setText("转账");
        }


    }

    private void setListener() {
        btn_inset_money.setOnClickListener(this);
        edt_input_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    tv_input_money.setVisibility(View.VISIBLE);
                } else {
                    tv_input_money.setVisibility(View.GONE);
                }
                int length = s.toString().length();
                boolean sign1 = length > 0;
                if (sign1) {
                    btn_inset_money.setEnabled(true);
                } else {
                    btn_inset_money.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                showNotice();
            }
        });
        edt_input_money.setFilters(new InputFilter[]{new InputMoney()});
        edt_input_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    tv_show_input_msg.setVisibility(View.VISIBLE);
                } else {
                    tv_show_input_msg.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initData() {
        btn_inset_money.setEnabled(false);
        edt_input_money.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    private void initView() {
        tv_show_money = (TextView) getView().findViewById(R.id.tv_show_money);
        tv_money = (TextView) getView().findViewById(R.id.tv_money);
        tv_money_size = (TextView) getView().findViewById(R.id.tv_money_size);
        tv_notice = (TextView) getView().findViewById(R.id.tv_notice);
        tv_input_money = (TextView) getView().findViewById(R.id.tv_input_money);
        tv_show_input_msg = (TextView) getView().findViewById(R.id.tv_show_input_msg);
        edt_input_message = (EditText) getView().findViewById(R.id.edt_input_message);
        edt_input_money = (EditText) getView().findViewById(R.id.edt_input_money);
        btn_inset_money = (Button) getView().findViewById(R.id.btn_inset_money);
    }

    private void getData() {
        if (!isTransfer) {
            msg = getString(R.string.words_numal);
        } else {
            msg = "";
        }

        userId = getActivity().getIntent().getStringExtra(HTConstant.JSON_KEY_USERID);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_inset_money:
                String money = edt_input_money.getText().toString().trim();
                String input = edt_input_message.getText().toString().trim();
                if (TextUtils.isEmpty(money) || getString(R.string.money_numal).equals(Validator.formatMoney(money))) {
                    showNotice(getString(R.string.money_is_no_0));
                    return;
                }
                if (TextUtils.isEmpty(input)) {
                    input = msg;
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
                intent.putExtra("content", input);
                intent.putExtra("rundom", "1");
                intent.putExtra("chatType", MessageUtils.CHAT_SINGLE);
                intent.putExtra("isTransfer", isTransfer);
                intent.putExtra(HTConstant.JSON_KEY_USERID, userId);
                getActivity().startActivityForResult(intent, 1000);
                break;
        }
    }

    private class InputMoney implements InputFilter {
        private InputMoney() {
        }

        public CharSequence filter(CharSequence var1, int var2, int var3, Spanned var4, int var5, int var6) {
            if (var1.toString().equals(".") && var5 == 0 && var6 == 0) {
                edt_input_money.setText("0" + var1 + var4);
                edt_input_money.setSelection(2);
            }
            return var5 >= 8 ? "" : (var4.toString().indexOf(".") != -1 && var4.length() - var4.toString().indexOf(".") > 2 && var4.length() - var5 < 3 ? "" : null);
        }
    }

    private void showNotice() {
        String var1 = edt_input_money.getText().toString();
        if (!Validator.isEmpty(var1)) {
            if (!var1.startsWith(".")) {
                BigDecimal var2 = new BigDecimal(var1);
                float var3 = var2.floatValue();
                if (var3 == 0.0F) {
                    hideNotice();
                } else if (var3 < 0.01F) {
                    showNotice(getString(R.string.one_rp_not_1));
                    btn_inset_money.setEnabled(false);
                } else if ((double) var3 > 200 && !isTransfer) {

                    showNotice(String.format(getString(R.string.one_rp_max), Validator.formatMoney(200)));
                    btn_inset_money.setEnabled(false);
                }


                //本项目取消限制当个红包200元
//                else if ((double) var3 > 200) {
//                    showNotice(String.format(getString(R.string.one_rp_max), Validator.formatMoney(200)));
//                    btn_inset_money.setEnabled(false);
//                }
                else {
                    hideNotice();
                    btn_inset_money.setEnabled(true);
                }

                if (var3 > 0.0F) {
                    var2 = var2.setScale(2, 4);
                    tv_show_money.setText(var2.toString());
                } else {
                    tv_show_money.setText(R.string.money_numal);
                }
            } else {
                showNotice(getString(R.string.one_rp_right));
                btn_inset_money.setEnabled(false);
            }
        } else {
            hideNotice();
            tv_show_money.setText(R.string.money_numal);
        }
    }

    private void showNotice(String var1) {
        tv_notice.setText(var1);
        tv_notice.setVisibility(View.VISIBLE);
    }

    private void hideNotice() {
        tv_notice.setText("");
        tv_notice.setVisibility(View.INVISIBLE);
    }
}
