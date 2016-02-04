package com.fanxin.app.fx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fanxin.app.Constant;
import com.fanxin.app.MYApplication;
import com.fanxin.app.R;
import com.fanxin.app.activity.BaseActivity;
import com.fanxin.app.db.ACache;
import com.fanxin.app.fx.others.LoadDataFromServer;
import com.fanxin.app.fx.others.LocalUserInfo;
import com.fanxin.app.fx.others.LoadDataFromServer.DataCallBack;

public class MoneyTradeActivity extends BaseActivity {

    private String firstCard;
    private TextView tv_cardID;
    private boolean isWithdraw = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_trade);
        initView();

    }

    private void initView() {

        isWithdraw = this.getIntent().getBooleanExtra("isWithdraw", false);
        TextView tv_title = (TextView) this.findViewById(R.id.tv_title);
        if (isWithdraw) {
            tv_title.setText("零钱提现");
        } else {
            tv_title.setText("零钱充值");

        }
        JSONArray jsons = ACache.get(getApplicationContext()).getAsJSONArray(
                Constant.CARDLIST);

        final List<Map<String, String>> nameList = new ArrayList<Map<String, String>>();// 建立一个数组存储listview上显示的数据
        // for (int m = 0; m < initData.size(); m++) {//initData为一个list类型的数据源
        //
        // }

        if (jsons != null && jsons.size() != 0) {
            for (int i = 0; i < jsons.size(); i++) {
                JSONObject json = jsons.getJSONObject(i);
                String number = json.getString("cardID");
                if (i == 0) {
                    firstCard = number;

                }

                Map<String, String> nameMap = new HashMap<String, String>();
                nameMap.put("name", number);
                nameList.add(nameMap);

            }

        }
        tv_cardID = (TextView) this.findViewById(R.id.tv_cardID);
        tv_cardID.setText(firstCard);
        final EditText et_money = (EditText) this.findViewById(R.id.et_money);

        Button btn_ok = (Button) this.findViewById(R.id.btn_ok);

        btn_ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String money = et_money.getText().toString().trim();
                if (money == null) {

                    Toast.makeText(getApplicationContext(), "请输入金额",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isFloathString(money) && !isNumberString(money)) {

                    Toast.makeText(getApplicationContext(), "请输入正确的金额",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isWithdraw) {

                    recharge(firstCard, money);
                } else {

                    withdraws(firstCard, money);
                }

            }

        });

        RelativeLayout re_click = (RelativeLayout) this
                .findViewById(R.id.re_click);
        re_click.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                chooseCard(nameList);
            }

        });

    }

    public boolean isFloathString(String testString) {
        if (!testString.contains(".")) {
            return isNumberString(testString);
        } else {
            String[] floatStringPartArray = testString.split("\\.");
            if (floatStringPartArray.length == 2) {
                if (true == isNumberString(floatStringPartArray[0])
                        && true == isNumberString(floatStringPartArray[1]))
                    return true;
                else
                    return false;
            } else
                return false;

        }

    }

    private boolean isNumberString(String testString) {
        String numAllString = "0123456789";
        if (testString.length() <= 0)
            return false;
        for (int i = 0; i < testString.length(); i++) {
            String charInString = testString.substring(i, i + 1);
            if (!numAllString.contains(charInString))
                return false;
        }
        return true;
    }

    private void chooseCard(final List<Map<String, String>> nameList) {

        LinearLayout linearLayoutMain = new LinearLayout(this);// 自定义一个布局文件
        linearLayoutMain.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        ListView listView = new ListView(this);// this为获取当前的上下文
        listView.setFadingEdgeLength(0);

        // = new ArrayList<Map<String, String>>();//建立一个数组存储listview上显示的数据
        // for (int m = 0; m < initData.size(); m++) {//initData为一个list类型的数据源
        // Map<String, String> nameMap = new HashMap<String, String>();
        // nameMap.put("name", initData.get(m).get(TagItem.NAME).toString());
        // nameList.add(nameMap);
        // }

        SimpleAdapter adapter = new SimpleAdapter(MoneyTradeActivity.this,
                nameList, R.layout.item_uploadphoto_select_name,
                new String[] { "name" },
                new int[] { R.id.tv_uploadphoto_select_name_item });
        listView.setAdapter(adapter);

        linearLayoutMain.addView(listView);// 往这个布局中加入listview

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("选择银行卡").setView(linearLayoutMain)// 在这里把写好的这个listview的布局加载dialog中
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                    }
                }).create();
        dialog.setCanceledOnTouchOutside(false);// 使除了dialog以外的地方不能被点击
        dialog.show();
        listView.setOnItemClickListener(new OnItemClickListener() {// 响应listview中的item的点击事件

            // @Override
            // public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
            // long arg3) {
            // // TODO Auto-generated method stub
            // TextView tv = (TextView) arg1
            // .findViewById(R.id.tv_uploadphoto_select_name_item);//取得每条item中的textview控件
            // et_name.setText(tv.getText().toString());
            // dialog.cancel();
            // }

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                firstCard = nameList.get(position).get("name");
                tv_cardID.setText(firstCard);
                dialog.cancel();
            }
        });

    }

    // 充值
    private void recharge(String cardID, final String money) {
        final ProgressDialog dialog = new ProgressDialog(
                MoneyTradeActivity.this);

        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("正在处理...");
        dialog.show();

        Map<String, String> map = new HashMap<String, String>();

        map.put("hxid", MYApplication.getInstance().getUserName());
        map.put("cardID", cardID);
        map.put("money", money);

        LoadDataFromServer task = new LoadDataFromServer(
                MoneyTradeActivity.this, Constant.URL_RECHARGE, map);

        task.getData(new DataCallBack() {

            @Override
            public void onDataCallBack(JSONObject data) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (data == null) {

                    Toast.makeText(getApplicationContext(), "访问服务器错误,操作失敗...",
                            Toast.LENGTH_SHORT).show();
                    return;

                }
                int code = data.getInteger("code");
                if (code == 1) {

                    String moneyNow = LocalUserInfo.getInstance(
                            getApplicationContext()).getUserInfo("money");
                    String moneyAfter = String.valueOf(Float
                            .parseFloat(moneyNow) + Float.parseFloat(money));
                    LocalUserInfo.getInstance(getApplicationContext())
                            .setUserInfo("money", moneyAfter);
                    Toast.makeText(getApplicationContext(), "充值成功!",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    // datas = data.getJSONArray("data");
                    // adapter = new MyAdapter(MoneyTradeActivity.this, datas);
                    // listView.setAdapter(adapter);
                    // ACache.get(getApplicationContext()).put(Constant.CARDLIST,
                    // datas);
                } else if (code == 4) {

                    Toast.makeText(getApplicationContext(), "充值卡余额不足!",
                            Toast.LENGTH_LONG).show();

                } else if (code == 3) {

                    Toast.makeText(getApplicationContext(), "卡转出出错!",
                            Toast.LENGTH_LONG).show();

                } else if (code == 2) {

                    Toast.makeText(getApplicationContext(), "零钱存入出错!",
                            Toast.LENGTH_LONG).show();

                }

                else {

                    Toast.makeText(getApplicationContext(), "充值错误",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // 提现
    private void withdraws(String cardID, final String money) {
        final ProgressDialog dialog = new ProgressDialog(
                MoneyTradeActivity.this);

        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("正在处理...");
        dialog.show();
        Map<String, String> map = new HashMap<String, String>();

        map.put("hxid", MYApplication.getInstance().getUserName());
        map.put("cardID", cardID);
        map.put("money", money);
        LoadDataFromServer task = new LoadDataFromServer(
                MoneyTradeActivity.this, Constant.URL_WIRHDROW, map);

        task.getData(new DataCallBack() {

            @Override
            public void onDataCallBack(JSONObject data) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (data == null) {

                    Toast.makeText(getApplicationContext(), "访问服务器错误,操作失敗...",
                            Toast.LENGTH_SHORT).show();
                    return;

                }
                int code = data.getInteger("code");
                if (code == 1) {

                    String moneyNow = LocalUserInfo.getInstance(
                            getApplicationContext()).getUserInfo("money");
                    String moneyAfter = String.valueOf(Float
                            .parseFloat(moneyNow) - Float.parseFloat(money));
                    LocalUserInfo.getInstance(getApplicationContext())
                            .setUserInfo("money", moneyAfter);
                    Toast.makeText(getApplicationContext(), "提现成功!",
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else if (code == 4) {

                    Toast.makeText(getApplicationContext(), "零钱余额不足!",
                            Toast.LENGTH_LONG).show();

                } else if (code == 3) {

                    Toast.makeText(getApplicationContext(), "零钱提出出错!",
                            Toast.LENGTH_LONG).show();

                } else if (code == 2) {

                    Toast.makeText(getApplicationContext(), "转到银行卡出错!",
                            Toast.LENGTH_LONG).show();

                }

                else {

                    Toast.makeText(getApplicationContext(), "提现失敗...",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
