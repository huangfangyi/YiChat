package com.fanxin.app.fx;

import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

public class RecordsActivity extends BaseActivity {
    private JSONArray datas;
    private MyAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_records);
        initView();

    }

    private void initView() {
        listView = (ListView) this.findViewById(R.id.listView);

        datas = ACache.get(getApplicationContext()).getAsJSONArray(
                Constant.C_RECORDS);
        if (datas == null) {
            getData(true);
        } else {
            adapter = new MyAdapter(RecordsActivity.this, datas);
            listView.setAdapter(adapter);
            getData(false);
        }
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                JSONObject json = adapter.getItem(position);
                Intent intent = new Intent();
                intent.putExtra("json", json.toJSONString());
                intent.setClass(RecordsActivity.this,
                        RecordsDetailActivity.class);
                startActivity(intent);

            }

        });
        // iv_more.setOnClickListener(this);

    }

    private void getData(final boolean needDialog) {
        final ProgressDialog dialog = new ProgressDialog(RecordsActivity.this);
        if (needDialog) {

            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("正在加载...");

        }

        Map<String, String> map = new HashMap<String, String>();

        map.put("hxid", MYApplication.getInstance().getUserName());

        LoadDataFromServer task = new LoadDataFromServer(RecordsActivity.this,
                Constant.URL_RECORDS, map);

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
                    adapter = new MyAdapter(RecordsActivity.this, datas);
                    listView.setAdapter(adapter);
                    ACache.get(getApplicationContext()).put(Constant.C_RECORDS,
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
        private JSONArray jsonArray;
        private LayoutInflater inflater;

        public MyAdapter(Context context_, JSONArray jsonArray) {
            this.context = context_;
            this.jsonArray = jsonArray;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return jsonArray.size();
        }

        @Override
        public JSONObject getItem(int position) {
            // TODO Auto-generated method stub
            return jsonArray.getJSONObject(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_records, parent,
                        false);

            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder == null) {

                holder = new ViewHolder();
                holder.tv_content = (TextView) convertView
                        .findViewById(R.id.tv_content);
                holder.tv_type = (TextView) convertView
                        .findViewById(R.id.tv_type);

                holder.tv_money = (TextView) convertView
                        .findViewById(R.id.tv_money);
                holder.tv_time = (TextView) convertView
                        .findViewById(R.id.tv_time);
                convertView.setTag(holder);
            }
            JSONObject json = getItem(position);

            // String fHxid = json.getString("fHxid");
            String tHxid = json.getString("tHxid");
            String money = json.getString("money");

            String time = json.getString("time");
            String type = json.getString("type");
            String state = json.getString("state");
            if (type.equals("1")) {
                holder.tv_type.setText("提现");
                if (state.equals("1")) {
                    holder.tv_content.setText("提现正在处理");

                } else {

                    holder.tv_content.setText("提现成功");
                }

            } else if (type.equals("2")) {
                holder.tv_type.setText("充值");
                holder.tv_content.setText("充值成功");
            } else {
                holder.tv_type.setText("微信转账");
                if (tHxid.equals(MYApplication.getInstance().getUserName())) {

                    holder.tv_content.setText("已存入零钱");
                } else {

                    holder.tv_content.setText("朋友已收钱");
                }
            }

            holder.tv_time.setText(time.substring(2));
            holder.tv_money.setText("￥" + money);
            return convertView;
        }

    }

    static class ViewHolder {
        TextView tv_type;
        TextView tv_money;
        TextView tv_content;
        TextView tv_time;

    }
}
