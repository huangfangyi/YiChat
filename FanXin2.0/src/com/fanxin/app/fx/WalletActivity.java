package com.fanxin.app.fx;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fanxin.app.R;
import com.fanxin.app.activity.BaseActivity;
import com.fanxin.app.fx.others.LocalUserInfo;

public class WalletActivity extends BaseActivity implements OnClickListener {
    private ImageView iv_more;
    private MyGridView gridView;
    private final static String[] names = new String[] { "刷卡", "转账", "手机充值",
            "理财通", "滴滴出行", "生活缴费", "电影票", "美丽说", "京东精选", "信用卡还款", "微信红包",
            "火车票机票", "吃喝玩乐", "腾讯公益", "AA收款" };
    private final static int[] images = new int[] { R.drawable.w1,
            R.drawable.w2, R.drawable.w3, R.drawable.w4, R.drawable.w5,
            R.drawable.w6, R.drawable.w7, R.drawable.w8, R.drawable.w9,
            R.drawable.w10, R.drawable.w11, R.drawable.w12, R.drawable.w13,
            R.drawable.w14, R.drawable.w15 };
    private Myadapter adapter;
    private ScrollView srollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        initView();

    }

    private void initView() {
        iv_more = (ImageView) this.findViewById(R.id.iv_more);
        iv_more.setOnClickListener(this);
      
        
        gridView = (MyGridView) findViewById(R.id.gridView);
        adapter = new Myadapter(WalletActivity.this, images, names);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                if (position == 1) {
                    startActivity(new Intent(WalletActivity.this,
                            PayActivity.class));
                }
            }

        });

        srollView = (ScrollView) findViewById(R.id.srollView);

        this.findViewById(R.id.re_money).setOnClickListener(this);
        RelativeLayout re_card = (RelativeLayout) this.findViewById(R.id.re_card);
        re_card.setOnClickListener(this);
        TextView tv_money = (TextView) this.findViewById(R.id.tv_money);

        tv_money.setText("￥"+LocalUserInfo.getInstance(getApplicationContext())
                .getUserInfo("money"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.iv_more:
            showPopView();

            break;

        case R.id.re_money:
            startActivity(new Intent(WalletActivity.this, MoneyActivity.class));

            break;
            
        case R.id.re_card:
            startActivity(new Intent(WalletActivity.this, BankCardActivity.class));

            break;
            
        default:
            break;

        }
    }

    private void showPopView() {

        MorePopWindow addPopWindow = new MorePopWindow(WalletActivity.this);
        addPopWindow.showPopupWindow(iv_more);

    }

    class Myadapter extends BaseAdapter {

        private LayoutInflater inflater;
        private int[] imageDatas;
        private String[] nameDatas;

        public Myadapter(Context context, int[] imageDatas, String[] nameDatas) {
            inflater = LayoutInflater.from(context);

            this.imageDatas = imageDatas;
            this.nameDatas = nameDatas;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return imageDatas.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return imageDatas[position];
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.itme_wallet, null, false);
            ImageView iv_image = (ImageView) convertView
                    .findViewById(R.id.iv_image);
            TextView tv_name = (TextView) convertView
                    .findViewById(R.id.tv_name);
            String name = nameDatas[position];
            int imageRes = imageDatas[position];
            iv_image.setImageResource(imageRes);
            tv_name.setText(name);
            return convertView;
        }

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        srollView.smoothScrollTo(0, 0);
        super.onResume();
        TextView tv_money = (TextView) this.findViewById(R.id.tv_money);

        tv_money.setText("￥"+LocalUserInfo.getInstance(getApplicationContext())
                .getUserInfo("money"));
    }
}
