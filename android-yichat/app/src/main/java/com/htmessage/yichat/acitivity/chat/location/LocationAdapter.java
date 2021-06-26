package com.htmessage.yichat.acitivity.chat.location;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import com.htmessage.yichat.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by huangfangyi on 2017/12/10.
 * qq 84543217
 */

public class LocationAdapter extends BaseAdapter {
    private List<LocationBean> locationBeanList = new ArrayList<>();
    private HashMap<String,Boolean> states=new HashMap<String,Boolean>();//记录所有radiobutton被点击的状态
    private Context context;
    public LocationAdapter(List<LocationBean> locationBeanList,Context context) {
        this.context =context;
        this.locationBeanList = locationBeanList;
    }

    @Override
    public int getCount() {
        return locationBeanList.size();
    }

    @Override
    public LocationBean getItem(int position) {
        return locationBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHodler holder;
        if (convertView == null){
            convertView =View.inflate(context,R.layout.item_location_layout,null);
            holder = new ViewHodler(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHodler) convertView.getTag();
        }
        holder.bindHolder(locationBeanList.get(position),position);
        return convertView;
    }

    private class ViewHodler {
        private TextView tv_address_title,tv_address;
        private RadioButton rb_check;
        private LinearLayout ll_location_item;
        public ViewHodler(View view) {
            tv_address_title = (TextView) view.findViewById(R.id.tv_address_title);
            tv_address = (TextView) view.findViewById(R.id.tv_address);
            rb_check = (RadioButton) view.findViewById(R.id.rb_check);
            ll_location_item = (LinearLayout) view.findViewById(R.id.ll_location_item);
        }
        public void bindHolder(LocationBean bean,final int position){
            tv_address_title.setText(bean.getTitle());
            tv_address.setText(bean.getAddress());
            boolean res=false;
            if(getStates(position)==null|| getStates(position)==false){//判断当前位置的radiobutton点击状态
                res=false;
                setStates(position);
            }else{
                res=true;
            }
            rb_check.setChecked(res);
        }
    }
    //用于在activity中重置所有的radiobutton的状态
    public void clearStates(int position){
        // 重置，确保最多只有一项被选中
        for(String key:states.keySet()){
            states.put(key,false);
        }
        states.put(String.valueOf(position), true);
    }
    //用于获取状态值
    public Boolean getStates(int position){
        return states.get(String.valueOf(position));
    }
    //设置状态值
    public void setStates(int position){
        states.put(String.valueOf(position),false);
    }
}
