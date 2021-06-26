package com.htmessage.yichat.acitivity.main.wallet;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.update.Constant;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.yichat.R;

/**
 * Created by huangfangyi on 2019/5/27.
 * qq 84543217
 */
public class RecordsFragment extends Fragment {

    private RecyclerView recyclerView;
    private VpSwipeRefreshLayout swipyRefreshLayout;
    private MyAdapter adapter;
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
                    Toast.makeText(getActivity(),resId,Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_records, container, false);

        return view;
    }

    private JSONArray jsonArray=new JSONArray();

//    public  void setData(JSONArray records){
//         jsonArray.clear();
//         jsonArray.addAll(records);
//         adapter.notifyDataSetChanged();
//    }
//    public void addData(JSONArray records){
//        jsonArray.addAll(records);
//        adapter.notifyDataSetChanged();
//    }

    private int type;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        type = getArguments().getInt("type");
        recyclerView = getView().findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext() );
//设置布局管理器

//设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper. VERTICAL);
 //设置增加或删除条目的动画
        recyclerView.setItemAnimator( new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dec = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        dec.setDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.divider_recy));
        dec.setOrientation(DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dec);

        swipyRefreshLayout = getView().findViewById(R.id.swipyrefresh);
        //  swipyRefreshLayout.setFirstIndex(0);
        adapter = new MyAdapter(jsonArray, getActivity());
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

    private int currentIndex = 0;

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

            View view = View.inflate(context, R.layout.item_trade_details, null);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            JSONObject jsonObject = data.getJSONObject(position);
            String memo=jsonObject.getString("memo");
            String moneyDesc=jsonObject.getString("moneyDesc");
            String dateDesc=jsonObject.getString("dateDesc");
//            String userId=jsonObject.getString("userId");
//            int type=jsonObject.getInteger("type");
            MyViewHolder myViewHolder= (MyViewHolder) holder;
            myViewHolder.tvDetail.setText("交易成功");

            myViewHolder.tvMomey.setText(moneyDesc);
            myViewHolder.tvTime.setText(dateDesc);
            myViewHolder.tvType.setText(memo);


        }

        @Override
        public int getItemCount() {
            return data.size();
        }


    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTime;

        public TextView tvMomey;
        public TextView tvType;
        public TextView tvDetail;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.tvTime = itemView.findViewById(R.id.tv_time);
            this.tvMomey = itemView.findViewById(R.id.tv_money);
            this.tvType = itemView.findViewById(R.id.tv_type);
            this.tvDetail = itemView.findViewById(R.id.tv_detail);
        }


    }


    private void getData() {
        JSONObject body=new JSONObject();


        if(type!=0){
          body.put("type",type-1+"");
        }
        body.put("pageNo",currentIndex+1);
        body.put("pageSize",20);
        ApiUtis.getInstance().postJSON(body, Constant.URL_balance_list, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if(handler==null){
                    return;
                }
                String code=jsonObject.getString("code");

                if("0".equals(code)){
                    JSONArray data=jsonObject.getJSONArray("data");
                    Message message=handler.obtainMessage();
                    message.obj=data;
                    message.what=1000;
                    message.sendToTarget();
                }else {
                    Message message=handler.obtainMessage();
                    message.arg1=R.string.api_error_5;
                    message.what=1001;
                    message.sendToTarget();

                }
            }

            @Override
            public void onFailure(int errorCode) {
                if(handler==null){
                    return;
                }
                Message message=handler.obtainMessage();
                message.arg1=errorCode;
                message.what=1001;
                message.sendToTarget();
            }
        });

//        List<Param> paramList = new ArrayList<>();
//        paramList.add(new Param("page", String.valueOf(currentIndex + 1)));
//        if (type != 0) {
//            paramList.add(new Param("type", type + ""));
//        }
//
//        paramList.add(new Param("userId", HTApp.getInstance().getUsername()));
//        new OkHttpUtils(getContext()).post(paramList, HTConstant.URL_TRADE_RECORDS, new OkHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                int code = jsonObject.getInteger("code");
//
//                if (code != 1) {
//                    Toast.makeText(getContext(), "无更多数据", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                JSONArray data = jsonObject.getJSONArray("data");
//                if (currentIndex == 0) {
//                    jsonArray.clear();
//
//                 }
//                jsonArray.addAll(data);
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                Toast.makeText(getContext(), "接口请求错误，请重试", Toast.LENGTH_SHORT).show();
//
//            }
//        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler=null;
    }
}
