package com.htmessage.yichat.acitivity.red;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.update.data.UserManager;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.red.history.RpHistoryActivity;
import com.htmessage.yichat.utils.Validator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 项目名称：Treasure
 * 类描述：RedDetailFragment 描述:
 * 创建人：songlijie
 * 创建时间：2017/9/27 16:42
 * 邮箱:814326663@qq.com
 */
public class RedDetailFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private TextView tv_nick, tv_rp_msg, tv_rp_money, tv_watch_history,tv_tip;
    private ListView lv_rp_people;
    private ImageView iv_avatar;
    private RpDetailsAdapter adapter;
    private RelativeLayout rl_detail_list, rl_money;
    private TextView tv_notice, tv_bottom_notice;
    private int chatType = 1;
    private JSONObject redData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_red_content, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();
        initView();
        setListener();
    }


//
//    //提示已过期或者已领取完、
//    private void showTipForOver(String status, int num, int hasGot, String getMoney) {
//        if (!status.equals("1")) {
//            getView().findViewById(R.id.tv_temp_money).setVisibility(View.GONE);
//            if (status.equals("2")) {
//                tv_rp_money.setText("红包已过期");
//            }
//            if (!status.equals("2") && num == hasGot) {
//                tv_rp_money.setText("手速慢了，红包已经被抢完了");
//            }
//        }
//
//        if (getMoney.equals("0.00") || getMoney.equals("0") && num == hasGot) {
//            getView().findViewById(R.id.tv_temp_money).setVisibility(View.GONE);
//            tv_rp_money.setText("手速慢了，红包已经被抢完了");
//            tv_rp_money.setTextSize(16);
//        }
//
//    }


    private void reFreshList(List<JSONObject> jsonObjectList) {
        Collections.sort(jsonObjectList, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                long time1 = o1.getLong("getTime");
                long time2 = o2.getLong("getTime");
                if (time1 > time2) {
                    return 1;
                } else if ((time1 < time2)) {
                    return -1;
                }


                return 0;
            }
        });


    }

//    private void showUi(String money, String receiveStatus, String getMoney, String msg, int rednum, List<JSONObject> jsonObjectList) {
//         tv_nick.setText(String.format(getString(R.string.who_rp), nick));
//        UserManager.get().loadUserAvatar(getActivity(), avatar, iv_avatar);
//        if (chatType == MessageUtils.CHAT_GROUP) {
//            tv_notice.setText(String.format(getString(R.string.single_rp_done_has_left), rednum, Validator.formatMoney(money), jsonObjectList.size(), rednum));
//            tv_rp_money.setText(Validator.formatMoney(getMoney));
//            showTipForOver(receiveStatus, rednum, jsonObjectList.size(), getMoney);
//            if ("0".equals(receiveStatus)) {
//                if (message.getDirect() == HTMessage.Direct.SEND) {
//                    tv_bottom_notice.setVisibility(View.VISIBLE);
//                    tv_watch_history.setVisibility(View.GONE);
//                } else {
//                    tv_bottom_notice.setVisibility(View.GONE);
//                    tv_watch_history.setVisibility(View.VISIBLE);
//                }
//            } else {
//
//                tv_notice.setText(String.format(getString(R.string.single_rp_done_has_left), rednum, Validator.formatMoney(money), jsonObjectList.size(), rednum));
//                if (message.getDirect() == HTMessage.Direct.SEND) {
//                    tv_bottom_notice.setVisibility(View.GONE);
//                    tv_watch_history.setVisibility(View.VISIBLE);
//                    lv_rp_people.setVisibility(View.VISIBLE);
//                } else {
//                    tv_bottom_notice.setVisibility(View.GONE);
//                    tv_watch_history.setVisibility(View.VISIBLE);
//                    lv_rp_people.setVisibility(View.VISIBLE);
//                }
//
//                // reFreshList(jsonObjectList);
//            }
//        } else {
//            tv_rp_money.setText(Validator.formatMoney(money));
//            if ("0".equals(receiveStatus)) {
//                if (message.getDirect() == HTMessage.Direct.SEND) {
//                    tv_notice.setText(String.format(getString(R.string.single_rp), Validator.formatMoney(money)));
//                    tv_bottom_notice.setVisibility(View.VISIBLE);
//                    tv_watch_history.setVisibility(View.GONE);
//                } else {
//                    tv_notice.setVisibility(View.GONE);
//                    tv_bottom_notice.setVisibility(View.GONE);
//                    tv_watch_history.setVisibility(View.VISIBLE);
//                }
//            } else {
//                if (message.getDirect() == HTMessage.Direct.SEND) {
//                    tv_notice.setText(String.format(getString(R.string.single_rp_done), rednum, Validator.formatMoney(money)));
//                    tv_bottom_notice.setVisibility(View.GONE);
//                    tv_watch_history.setVisibility(View.VISIBLE);
//                    lv_rp_people.setVisibility(View.VISIBLE);
////                    reFreshList();
//                } else {
//                    tv_notice.setVisibility(View.GONE);
//                    tv_bottom_notice.setVisibility(View.GONE);
//                    tv_watch_history.setVisibility(View.VISIBLE);
//                    lv_rp_people.setVisibility(View.INVISIBLE);
//                }
//            }
//        }
//        tv_rp_msg.setText(msg);
//        adapter = new RpDetailsAdapter(jsonObjectList, getActivity());
//        lv_rp_people.setAdapter(adapter);
//    }

    private void setListener() {
        lv_rp_people.setOnItemClickListener(this);
        tv_watch_history.setOnClickListener(this);
    }

    private void initView() {
        iv_avatar = (ImageView) getView().findViewById(R.id.iv_avatar);
        tv_nick = (TextView) getView().findViewById(R.id.tv_nick);
        tv_tip=(TextView) getView().findViewById(R.id.tv_tip);
        tv_rp_msg = (TextView) getView().findViewById(R.id.tv_rp_msg);
        tv_rp_money = (TextView) getView().findViewById(R.id.tv_rp_money);
        tv_watch_history = (TextView) getView().findViewById(R.id.tv_watch_history);
        lv_rp_people = (ListView) getView().findViewById(R.id.lv_rp_people);
        rl_detail_list = (RelativeLayout) getView().findViewById(R.id.rl_detail_list);
        tv_notice = (TextView) getView().findViewById(R.id.tv_notice);
        tv_bottom_notice = (TextView) getView().findViewById(R.id.tv_bottom_notice);
        rl_money = getView().findViewById(R.id.rl_money);
        UserManager.get().loadUserAvatar(getActivity(), redData.getString("avatar"), iv_avatar);
        tv_nick.setText(redData.getString("nick"));
        tv_rp_msg.setText(redData.getString("content"));

        if (chatType == 1) {
            //单聊情况
            //我是发送方
            if (UserManager.get().getMyUserId().equals(redData.getString("userId"))) {

                if (redData.getInteger("status") == 0) {
                    // 对方未领取
                    rl_money.setVisibility(View.GONE);
                    tv_notice.setText(String.format(getString(R.string.single_rp), Validator.formatMoney(redData.getString("money"))));
                    return;
                }
                if (redData.getInteger("status") == 1||redData.getInteger("status") == 2) {
                    //对方已领取
                    rl_money.setVisibility(View.GONE);
                    tv_notice.setText(String.format(getString(R.string.single_rp_done), redData.getInteger("num"), Validator.formatMoney(redData.getString("money"))));
                    adapter = new RpDetailsAdapter(redData.getJSONArray("list"), getActivity());
                    lv_rp_people.setAdapter(adapter);
                    return;
                }
                if (redData.getInteger("status") == 3) {
                    // 对方未领取，并且红包过期
                    rl_money.setVisibility(View.GONE);
                    //todo  红包过期场景
                    tv_tip.setVisibility(View.VISIBLE);
                    tv_tip.setText(R.string.no_rp_timeover);
                    tv_notice.setVisibility(View.GONE);
                    //    tv_notice.setText(String.format(getString(R.string.single_rp), Validator.formatMoney(redData.getString("money"))));
                    return;
                }


            } else {

                //我是接收方---只会在领取之后显示，未领取严禁进入此页
                if (redData.getInteger("status") == 1||redData.getInteger("status") == 2) {

                    rl_money.setVisibility(View.VISIBLE);
                    tv_rp_money.setText(Validator.formatMoney(redData.getString("receiveMoney")));
                    tv_notice.setVisibility(View.GONE);
                    return;
                }

                if(redData.getInteger("status") == 3){
                    // 对方未领取，并且红包过期
                    rl_money.setVisibility(View.GONE);
                    //todo  红包过期场景
                    tv_tip.setVisibility(View.VISIBLE);
                    tv_tip.setText(R.string.no_rp_timeover);
                    tv_notice.setVisibility(View.GONE);
                }
            }


        } else {
            //群红包
            if (redData.getInteger("status") == 1 || redData.getInteger("status") == 2) {
                //我已领取或者已经别人领完了。
                String receiveMoney = redData.getString("receiveMoney");
                if (!TextUtils.isEmpty(receiveMoney) && !Double.valueOf(receiveMoney).equals(0)) {
                    //我领到了金额
                    rl_money.setVisibility(View.VISIBLE);
                    tv_rp_money.setText(Validator.formatMoney(redData.getString("receiveMoney")));
                } else {

                    rl_money.setVisibility(View.GONE);
                    if(redData.getInteger("status") == 2){
                      tv_tip.setVisibility(View.VISIBLE);
                      tv_tip.setText(R.string.no_rp);
                    }
                }



            }else if(redData.getInteger("status") == 3){
                rl_money.setVisibility(View.GONE);
                tv_tip.setVisibility(View.VISIBLE);
                tv_tip.setText(R.string.no_rp_timeover);
                tv_notice.setVisibility(View.GONE);
            }

            tv_notice.setText(String.format(getString(R.string.single_rp_done_has_left), redData.getInteger("num"), Validator.formatMoney(redData.getString("money")), redData.getInteger("receiveNum"), redData.getInteger("num")));

            adapter = new RpDetailsAdapter(redData.getJSONArray("list"), getActivity());
            lv_rp_people.setAdapter(adapter);
        }


    }

    private void getData() {
        chatType = getArguments().getInt("chatType");
        String jsonString = getArguments().getString("data");
        redData = JSONObject.parseObject(jsonString);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_watch_history:
                startActivity(new Intent(getActivity(), RpHistoryActivity.class).putExtra("chatType", chatType));
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

}
