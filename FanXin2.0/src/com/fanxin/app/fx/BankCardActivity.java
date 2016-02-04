package com.fanxin.app.fx;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fanxin.app.Constant;
import com.fanxin.app.MYApplication;
import com.fanxin.app.R;
import com.fanxin.app.activity.BaseActivity;
import com.fanxin.app.db.ACache;
import com.fanxin.app.fx.others.LoadDataFromServer;
import com.fanxin.app.fx.others.LoadDataFromServer.DataCallBack;

public class BankCardActivity extends BaseActivity {
    private MyAdapter adapter;
    private JSONArray datas;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card);
        initView();

    }

    @SuppressLint("InflateParams")
    private void initView() {
        listView = (ListView) this.findViewById(R.id.listView);
        View footView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.item_bank_card_footer, null);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                if (position == datas.size()) {
                   startActivity(new Intent(BankCardActivity.this,AddCardActivity.class));
                } else {

                }
            }

        });
        listView.addFooterView(footView);

        datas = ACache.get(getApplicationContext()).getAsJSONArray(
                Constant.CARDLIST);
        if (datas == null) {
            Log.e("111111", "111111");
            getData(true);
        } else {
            Log.e("22222",datas.toJSONString());
            adapter = new MyAdapter(BankCardActivity.this, datas);
            listView.setAdapter(adapter);
            getData(false);
        }

    }

    private void getData(final boolean needDialog) {
        final ProgressDialog dialog = new ProgressDialog(BankCardActivity.this);
        if (needDialog) {

            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("正在加载...");
            dialog.show();
        }
        
        Map<String, String> map = new HashMap<String, String>();

        map.put("hxid", MYApplication.getInstance().getUserName());

        LoadDataFromServer task = new LoadDataFromServer(BankCardActivity.this,
                Constant.URL_CARDS, map);

        task.getData(new DataCallBack() {

            @Override
            public void onDataCallBack(JSONObject data) {
                if (needDialog && dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (data == null) {

                    Toast.makeText(getApplicationContext(), "访问服务器错误,更新失敗...",
                            Toast.LENGTH_SHORT).show();
                    return;

                }
                int code = data.getInteger("code");
                if (code == 1) {
                    datas = data.getJSONArray("data");
                    adapter = new MyAdapter(BankCardActivity.this, datas);
                    listView.setAdapter(adapter);
                    ACache.get(getApplicationContext()).put(Constant.CARDLIST,
                            datas);
                } else {

                    Toast.makeText(getApplicationContext(), "更新失敗...",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class MyAdapter extends BaseAdapter {
        private Context context;
        private JSONArray jsons;
        private LayoutInflater inflater;

        public MyAdapter(Context context_, JSONArray jsons) {

            this.context = context_;
            this.jsons = jsons;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return jsons.size();
        }

        @Override
        public JSONObject getItem(int position) {
            return jsons.getJSONObject(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (position != jsons.size()) {
                convertView = inflater.inflate(R.layout.item_bank_card, parent,
                false);
                TextView tv_card_num=(TextView) convertView.findViewById(R.id.tv_card_num);
                
               JSONObject json=getItem(position);
               String cardID=json.getString("cardID");
               tv_card_num.setText(cardID);
            } else {

                convertView = inflater.inflate(R.layout.item_bank_card_footer,
                        parent, false);
            }

            return convertView;
        }

    }

    @Override
    protected void onResume() {
         super.onResume();
         datas = ACache.get(getApplicationContext()).getAsJSONArray(
                 Constant.CARDLIST);
         if(datas!=null){
             adapter = new MyAdapter(BankCardActivity.this, datas);
             listView.setAdapter(adapter);
             
         }
         
         
         
    }
    
}
