package com.fanxin.app.fx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.fanxin.app.Constant;
import com.fanxin.app.MYApplication;
import com.fanxin.app.R;
import com.fanxin.app.activity.BaseActivity;
import com.fanxin.app.domain.User;
import com.fanxin.app.fx.others.LoadUserAvatar;
import com.fanxin.app.fx.others.LoadUserAvatar.ImageDownloadedCallBack;

public class PayActivity extends BaseActivity {
    private ListView listView;
   private PickContactAdapter  contactAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        initView();

    }

    @SuppressLint("InflateParams")
	private void initView() {
        // 获取好友列表
        final List<User> alluserList = new ArrayList<User>();
        for (User user : MYApplication.getInstance().getContactList()
                .values()) {
            if (!user.getUsername().equals(Constant.NEW_FRIENDS_USERNAME)
                    & !user.getUsername().equals(Constant.GROUP_USERNAME))
                alluserList.add(user);
        }
        // 对list进行排序
        Collections.sort(alluserList, new PinyinComparator() {
        });

        listView = (ListView) findViewById(R.id.list);
//        LayoutInflater layoutInflater = LayoutInflater.from(this);
//        View headerView = layoutInflater.inflate(R.layout.item_chatroom_header,
//                null);
//        TextView tv_header = (TextView) headerView.findViewById(R.id.tv_header);
//        tv_header.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // startActivity(new Intent(PayActivity.this,
//                // ChatRoomActivity.class));
//                // finish();
//            }
//
//        });

        final EditText et_search = (EditText) this.findViewById(R.id.et_search);

        et_search.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {
                if (s.length() > 0) {
                    String str_s = et_search.getText().toString().trim();
                    List<User> users_temp = new ArrayList<User>();
                    for (User user : alluserList) {
                        String usernick = user.getNick();
                        Log.e("usernick--->>>", usernick);
                        Log.e("str_s--->>>", str_s);

                        if (usernick.contains(str_s)) {

                            users_temp.add(user);
                        }
                        contactAdapter = new PickContactAdapter(
                                PayActivity.this,
                                R.layout.item_contact_list,
                                users_temp);
                        listView.setAdapter(contactAdapter);

                    }

                } else {
                    contactAdapter = new PickContactAdapter(
                            PayActivity.this,
                            R.layout.item_contact_list,
                            alluserList);
                    listView.setAdapter(contactAdapter);
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            public void afterTextChanged(Editable s) {

            }
        });
    //    listView.addHeaderView(headerView);

        contactAdapter = new PickContactAdapter(this,
                R.layout.item_contact_list, alluserList);
        listView.setAdapter(contactAdapter);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                    
                
              //  Bitmap bitmap=contactAdapter.getBitmap(position);
              User user=alluserList.get(position);
              Intent intent=new Intent(PayActivity.this,PayDetailActivity.class);
              intent.putExtra("hxid", user.getUsername());
              intent.putExtra("avatar", user.getAvatar());
              intent.putExtra("nick", user.getNick());
              startActivity(intent);
              finish();
                
//                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
//                checkBox.toggle();

            }
        });
        

      
   
    }

    /**
     * adapter
     */
    private class PickContactAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
       // private boolean[] isCheckedArray;
       
        private LoadUserAvatar avatarLoader;
        private List<User> list = new ArrayList<User>();
        private int res;

        @SuppressLint("SdCardPath")
        public PickContactAdapter(Context context, int resource,
                List<User> users) {

            layoutInflater = LayoutInflater.from(context);
            avatarLoader = new LoadUserAvatar(context, "/sdcard/fanxin/");
           
            this.res = resource;
            this.list = users;

        }

       
        @SuppressLint("ViewHolder")
        @Override
        public View getView(final int position, View convertView,
                ViewGroup parent) {

            convertView = layoutInflater.inflate(res, null);

            ImageView iv_avatar = (ImageView) convertView
                    .findViewById(R.id.iv_avatar);
            TextView tv_name = (TextView) convertView
                    .findViewById(R.id.tv_name);
            TextView tvHeader = (TextView) convertView
                    .findViewById(R.id.header);
            final User user = list.get(position);

            final String avater = user.getAvatar();
            String name = user.getNick();
            String header = user.getHeader();
            final String username = user.getUsername();
            tv_name.setText(name);
            iv_avatar.setImageResource(R.drawable.default_useravatar);
            iv_avatar.setTag(avater);
            Bitmap bitmap = null;
            if (avater != null && !avater.equals("")) {
                bitmap = avatarLoader.loadImage(iv_avatar, avater,
                        new ImageDownloadedCallBack() {

                            @Override
                            public void onImageDownloaded(ImageView imageView,
                                    Bitmap bitmap) {
                                if (imageView.getTag() == avater) {
                                    imageView.setImageBitmap(bitmap);

                                }
                            }

                        });

                if (bitmap != null) {

                    iv_avatar.setImageBitmap(bitmap);

                }
 
            }
            if (position == 0 || header != null
                    && !header.equals(getItem(position - 1))) {
                if ("".equals(header)) {
                    tvHeader.setVisibility(View.GONE);
                } else {
                    tvHeader.setVisibility(View.VISIBLE);
                    tvHeader.setText(header);
                }
            } else {
                tvHeader.setVisibility(View.GONE);
            }

            return convertView;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public String getItem(int position) {
            if (position < 0) {
                return "";
            }

            String header = list.get(position).getHeader();

            return header;

        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }
    }

    @SuppressLint("DefaultLocale")
    public class PinyinComparator implements Comparator<User> {

        @SuppressLint("DefaultLocale")
        @Override
        public int compare(User o1, User o2) {
            // TODO Auto-generated method stub
            String py1 = o1.getHeader();
            String py2 = o2.getHeader();
            // 判断是否为空""
            if (isEmpty(py1) && isEmpty(py2))
                return 0;
            if (isEmpty(py1))
                return -1;
            if (isEmpty(py2))
                return 1;
            String str1 = "";
            String str2 = "";
            try {
                str1 = ((o1.getHeader()).toUpperCase()).substring(0, 1);
                str2 = ((o2.getHeader()).toUpperCase()).substring(0, 1);
            } catch (Exception e) {
                System.out.println("某个str为\" \" 空");
            }
            return str1.compareTo(str2);
        }

        private boolean isEmpty(String str) {
            return "".equals(str.trim());
        }
    }

}
