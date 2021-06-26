package com.htmessage.yichat.acitivity.main.wallet;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.update.Constant;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;

/**
 * Created by huangfangyi on 2019/6/18.
 * qq 84543217
 */
public class WithDrawListActivity extends BaseActivity {
    private JSONArray jsonArray=new JSONArray();
    private int currentIndex = 0;
    private RecyclerView recyclerView;
    private VpSwipeRefreshLayout swipyRefreshLayout;
    private  MyAdapter adapter;
    private Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1000:
                    JSONArray data = (JSONArray) msg.obj;
                    if (currentIndex == 0) {
                        jsonArray.clear();

                    }
                    jsonArray.addAll(data);
                    adapter.notifyDataSetChanged();
                    break;
                case 1001:
                    int resId=msg.arg1;
                    Toast.makeText(WithDrawListActivity.this,resId,Toast.LENGTH_SHORT).show();
                    break;
                case 1002:
                    break;


            }
        }
    };
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_withdrawlist);
        setTitle("提现明细");
        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(WithDrawListActivity.this );
//设置布局管理器

//设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper. VERTICAL);
        //设置增加或删除条目的动画
        recyclerView.setItemAnimator( new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dec = new DividerItemDecoration(WithDrawListActivity.this,DividerItemDecoration.VERTICAL);
        dec.setDrawable(ContextCompat.getDrawable(WithDrawListActivity.this,R.drawable.divider_recy));
        dec.setOrientation(DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dec);

        swipyRefreshLayout = findViewById(R.id.swipyrefresh);
        //  swipyRefreshLayout.setFirstIndex(0);
        adapter = new   MyAdapter(jsonArray, WithDrawListActivity.this);
        recyclerView.setAdapter(adapter);
        swipyRefreshLayout.setOnRefreshListener(new VpSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentIndex = 0;
                getData();

                swipyRefreshLayout.setRefreshing(false);

            }
//
//            @Override
//            public void onRefresh(int index) {
//                Log.d("index----",index+"");
//                getData(index,type);
//                swipyRefreshLayout.setRefreshing(false);
//            }
//
//            @Override
//            public void onLoad(int index) {
//                getData(index,type);
//                Log.d("index----",index+"");
//                swipyRefreshLayout.setRefreshing(false);
//
//            }
        });
        initLoadMoreListener();
        getData();
    }

    private void initLoadMoreListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //判断RecyclerView的状态 是空闲时，同时，是最后一个可见的ITEM时才加载
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == adapter.getItemCount()) {
                    currentIndex++;
                    getData();

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //最后一个可见的ITEM
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });


    }


    class MyAdapter extends RecyclerView.Adapter {

        private JSONArray data;
        private Context context;

        public MyAdapter(JSONArray jsonArray, Context context) {

            data = jsonArray;
            this.context = context;

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = View.inflate(context, R.layout.item_withdraw, null);
             MyViewHolder holder = new  MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            JSONObject jsonObject = data.getJSONObject(position);
             String money=jsonObject.getString("money");
            String time=jsonObject.getString("time");
            int status=jsonObject.getInteger("status");
            String bankcard=jsonObject.getString("bankNumber");
            String refuseReason=jsonObject.getString("refuseReason");
             MyViewHolder myViewHolder= ( MyViewHolder) holder;



            myViewHolder.tvTime.setText(time);
            if(!TextUtils.isEmpty(bankcard)){

                myViewHolder.tvTitle.setText("零钱提现-到银行卡("+bankcard.substring(bankcard.length()-4,bankcard.length())+")");

            }else {
                myViewHolder.tvTitle.setText("零钱提现");
            }

            if(status==2){
                myViewHolder.tvStatus.setText("处理失败");
            }else if(status==1){
                myViewHolder.tvStatus.setText("处理成功");

            }else if(status==0){
                myViewHolder.tvStatus.setText("待处理");

            }

            if(!TextUtils.isEmpty(refuseReason)){
                myViewHolder.tvReason.setText(refuseReason);
            }else {
                myViewHolder.tvReason.setText("");
            }
            myViewHolder.tvMomey.setText(money);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }


    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTime;

        public TextView tvMomey;
        public TextView tvStatus;
        public TextView tvReason;
        public TextView tvTitle;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.tvTime = itemView.findViewById(R.id.tv_time);
            this.tvMomey = itemView.findViewById(R.id.tv_money);
            this.tvTitle = itemView.findViewById(R.id.tv_title);
            this.tvStatus = itemView.findViewById(R.id.tv_status);
            this.tvReason = itemView.findViewById(R.id.tv_reason);
        }


    }


    private void getData() {
        JSONObject body=new JSONObject();
        body.put("pageNo",currentIndex + 1);
        body.put("pageSize",20);
        ApiUtis.getInstance().postJSON(body, Constant.URL_withdraw_list, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                 if(handler==null){
                     return;
                 }

                 String code=jsonObject.getString("code");
                 if("0".equals(code)){
                     JSONArray data = jsonObject.getJSONArray("data");
                     Message message=handler.obtainMessage();
                     message.what=1000;
                     message.obj=data;
                     message.sendToTarget();
                 }else {
                     Message message=handler.obtainMessage();
                     message.what=1001;
                     message.arg1=R.string.api_error_5;
                     message.sendToTarget();
                 }

            }

            @Override
            public void onFailure(int errorCode) {
                if(handler==null){
                    return;
                }
                Message message=handler.obtainMessage();
                message.what=1001;
                message.arg1=errorCode;
                message.sendToTarget();
            }
        });

//        List<Param> paramList = new ArrayList<>();
//        paramList.add(new Param("page", String.valueOf(currentIndex + 1)));
//
//
//        paramList.add(new Param("userId", HTApp.getInstance().getUsername()));
//        new OkHttpUtils(WithDrawListActivity.this).post(paramList, HTConstant.WITH_DRAW_LIST, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                int code = jsonObject.getInteger("code");
//
//                if (code != 1) {
//                    Toast.makeText(WithDrawListActivity.this, "无更多数据", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                JSONArray data = jsonObject.getJSONArray("data");
//                if (currentIndex == 0) {
//                    jsonArray.clear();
//
//                }
//                jsonArray.addAll(data);
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                Toast.makeText(WithDrawListActivity.this, "接口请求错误，请重试", Toast.LENGTH_SHORT).show();
//
//            }
//        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler=null;

    }
}
