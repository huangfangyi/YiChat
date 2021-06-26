package com.htmessage.yichat.acitivity.friends.invitefriend;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;

import com.alibaba.fastjson.JSONObject;
import com.htmessage.update.data.SettingsManager;
import com.htmessage.yichat.R;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.widget.Sidebar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 项目名称：zhigongxing
 * 类描述：ContactsInviteFragment 描述:
 * 创建人：songlijie
 * 创建时间：2017/8/9 14:13
 * 邮箱:814326663@qq.com
 */
public class ContactsInviteFragment extends Fragment implements ContactsInviteAdapter.OnItemClickListener,Sidebar.OnTouchingLetterChangedListener {
    private RecyclerView recyclerview;
    private ContactsInviteAdapter adapter;
    private Sidebar sidebar;
    private TextView floating_header;
    private LinearLayoutManager layoutManager;
    private List<ContactInfo> lists = new ArrayList<>();
    private List<ContactInfo> topList = new ArrayList<>();
    private List<ContactInfo> lastList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_invite_contacts, container, false);
        return inflate;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        adapter.setListener(this);
        sidebar.setVisibility(View.VISIBLE);
        sidebar.setTextView(floating_header);
        sidebar.setOnTouchingLetterChangedListener(this);
    }

    private void initData() {
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerview.setLayoutManager(layoutManager);
        adapter = new ContactsInviteAdapter(getActivity(), lists);
        recyclerview.setAdapter(adapter);
        ContactsFetcherHelper.queryContactInfo(getActivity(), new ContactsFetcherHelper.OnContactsHasListener() {
            @Override
            public void onContactsComplete(final List<ContactInfo> list, final JSONArray moblies, final String mobileString) {
                lists.clear();
                lists.addAll(list);
                Collections.sort(lists, new PinyinComparator());
                if(getActivity()!=null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }

            }
        });
    }

    private void initView() {
        recyclerview = (RecyclerView) getView().findViewById(R.id.recyclerview);
        sidebar = (Sidebar) getView().findViewById(R.id.sidebar);
        floating_header = (TextView) getView().findViewById(R.id.floating_header);
    }

    @Override
    public void onItemClick(View view, ContactInfo info) {
        JSONObject shareJSON = SettingsManager.getInstance().getShareJSON();
        String inviteMsg="我在这里玩，赶紧一起来吧!";
        String url="";
        if(shareJSON!=null){
            inviteMsg=shareJSON.getString("content");
            url="下载地址是:"+shareJSON.getString("androidLink");
        }
        sendSMS(inviteMsg+" "+url, info.getPhoneNumber());
    }

    @Override
    public void onInviteButtonClick(View view, ContactInfo info) {
        int type = info.getType();
        if (type == 1) {
            JSONObject shareJSON = SettingsManager.getInstance().getShareJSON();
            String inviteMsg="我在这里玩，赶紧一起来吧!";
            String url="";
            if(shareJSON!=null){
                inviteMsg=shareJSON.getString("content");
                url="下载地址是:"+shareJSON.getString("androidLink");
            }
            sendSMS(inviteMsg+" "+url, info.getPhoneNumber());
        }

//        else {
//            String userId = info.getUserId();
//            if (!"0".equals(userId)) {
//                startActivity(new Intent(getActivity(), UserDetailsActivity.class).putExtra(HTConstant.JSON_KEY_USERID, userId));
//            }
//        }
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        int position = adapter.getPositionForSection(s.charAt(0));
        if (position != -1) {
            layoutManager.scrollToPositionWithOffset(position, 0);
        }
    }

    public class PinyinComparator implements Comparator<ContactInfo> {

        @Override
        public int compare(ContactInfo o1, ContactInfo o2) {
            String py1 = o1.getLetter();
            String py2 = o2.getLetter();
            if (py1.equals(py2)) {
                return o1.getName().compareTo(o2.getName());
            } else {
                if ("#".equals(py1)) {
                    return 1;
                } else if ("#".equals(py2)) {
                    return -1;
                }
                return py1.compareTo(py2);
            }

        }
    }

    /**
     * 发送短信
     *
     * @param smsBody
     */

    private void sendSMS(String smsBody, String mobile) {
        Uri smsToUri = Uri.parse("smsto:" + mobile);
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        intent.putExtra("sms_body", smsBody);
        startActivity(intent);
    }
}
