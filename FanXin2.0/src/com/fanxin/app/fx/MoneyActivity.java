package com.fanxin.app.fx;

import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fanxin.app.Constant;
import com.fanxin.app.R;
import com.fanxin.app.activity.BaseActivity;
import com.fanxin.app.db.ACache;
import com.fanxin.app.fx.others.LocalUserInfo;

public class MoneyActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money);
        initView();

    }

    private void initView() {

        TextView tv_money = (TextView) this.findViewById(R.id.tv_money);

        tv_money.setText("￥"
                + LocalUserInfo.getInstance(getApplicationContext())
                        .getUserInfo("money"));
        // JSONArray
        // jsons=ACache.get(getApplicationContext()).getAsJSONArray(Constant.CARDLIST);
        // String firstCard=null;
        // if(jsons!=null&&jsons.size()!=0){
        // for(int i=0;i<jsons.size();i++){
        // JSONObject json=jsons.getJSONObject(i);
        // String number=json.getString("cardID");
        // if(i==0){
        // firstCard= number;
        //
        // }
        //
        // }
        //
        // }
        // chooseCard()

        Button btn_chongzhi = (Button) this.findViewById(R.id.btn_chongzhi);
        Button btn_tixian = (Button) this.findViewById(R.id.btn_tixian);
        btn_chongzhi.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(MoneyActivity.this,
                        MoneyTradeActivity.class).putExtra("isWithdraw", false));
            }

        });
        btn_tixian.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(MoneyActivity.this,
                        MoneyTradeActivity.class).putExtra("isWithdraw", true));
            }

        });
         
    }

    private void chooseCard(List<Map<String, String>> nameList) {

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

        SimpleAdapter adapter = new SimpleAdapter(MoneyActivity.this, nameList,
                R.layout.item_uploadphoto_select_name, new String[] { "name" },
                new int[] { R.id.tv_uploadphoto_select_name_item });
        listView.setAdapter(adapter);

        linearLayoutMain.addView(listView);// 往这个布局中加入listview

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("选择患者姓名").setView(linearLayoutMain)// 在这里把写好的这个listview的布局加载dialog中
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

            }
        });

    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
       // srollView.smoothScrollTo(0, 0);
        super.onResume();
        TextView tv_money = (TextView) this.findViewById(R.id.tv_money);

        tv_money.setText("￥"
                + LocalUserInfo.getInstance(getApplicationContext())
                        .getUserInfo("money"));
    }
}
