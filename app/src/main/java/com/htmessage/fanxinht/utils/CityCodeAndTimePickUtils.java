package com.htmessage.fanxinht.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.country.CountryCodeUtil;
import com.htmessage.fanxinht.acitivity.login.country.CountryComparator;
import com.htmessage.fanxinht.acitivity.login.country.CountrySortAdapter;
import com.htmessage.fanxinht.acitivity.login.country.CountrySortModel;
import com.htmessage.fanxinht.acitivity.login.country.GetCountryNameSort;
import com.htmessage.fanxinht.acitivity.login.country.SideBar;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * 类描述：CityCodeAndTimePickUtils 描述: 国家代码与时间选择器工具类
 * 创建人：songlijie
 * 创建时间：2017/5/13 9:57
 * 邮箱:814326663@qq.com
 */
public class CityCodeAndTimePickUtils {
    private static String TAG = CityCodeAndTimePickUtils.class.getSimpleName();

    /**
     * 获取国家代码
     *
     * @param context     上下文对象
     * @param country     显示国家的textview
     * @param countryCode 显示国家代码的textview
     */
    public static void showPup(final Context context, final TextView country, final TextView countryCode) {
        final boolean cn = context.getResources().getConfiguration().locale.getCountry().equals("CN");
        final List<CountrySortModel> countryList = CountryCodeUtil.getCountryList(context, cn);
        //获得pup的view
        View view = LayoutInflater.from(context).inflate(R.layout.layout_pup, null, false);
        final ImageView iv_back = (ImageView) view.findViewById(R.id.iv_back);
        final TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText(R.string.country);
        final EditText country_et_search = (EditText) view.findViewById(R.id.country_et_search);
        final ImageView country_iv_cleartext = (ImageView) view.findViewById(R.id.country_iv_cleartext);
        final ListView ll_country = (ListView) view.findViewById(R.id.country_lv_list);
        final TextView country_dialog = (TextView) view.findViewById(R.id.country_dialog);
        final SideBar country_sidebar = (SideBar) view.findViewById(R.id.country_sidebar);
        country_sidebar.setTextView(country_dialog);
        final CountrySortAdapter adapter = new CountrySortAdapter(context, countryList);
        ll_country.setAdapter(adapter);
        DisplayMetrics dm = new DisplayMetrics();
        //取得窗口属性
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        //窗口的宽度
        int screenWidth = dm.widthPixels;
        //窗口高度
        int screenHeight = dm.heightPixels / 2;
        //设置window的宽高   1 window的布局 2、window的宽  3、window的高  4、window是否获取焦点
//        final PopupWindow window = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, screenHeight, true);
        final PopupWindow window = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);
        //设置window背景色
        window.setBackgroundDrawable(new ColorDrawable(0x00000000));
        //设置可以获取焦点，否则弹出菜单中的EditText是无法获取输入的
        window.setFocusable(true);
        //设置键盘不遮盖
        window.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //设置window动画
//        window.setAnimationStyle(R.style.custom_pup_style);
        //设置window在底部显示
        window.showAtLocation(view, Gravity.BOTTOM, 0, 0);

        country_iv_cleartext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                country_et_search.setText("");
                Collections.sort(countryList, new CountryComparator());
                adapter.updateListView(countryList);
            }
        });

        country_et_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchContent = country_et_search.getText().toString();
                if (searchContent.equals("")) {
                    country_iv_cleartext.setVisibility(View.INVISIBLE);
                } else {
                    country_iv_cleartext.setVisibility(View.VISIBLE);
                }

                if (searchContent.length() > 0) {
                    // 按照输入内容进行匹配
                    ArrayList<CountrySortModel> fileterList = (ArrayList<CountrySortModel>) new GetCountryNameSort()
                            .search(searchContent, countryList);

                    adapter.updateListView(fileterList);
                } else {
                    adapter.updateListView(countryList);
                }
                ll_country.setSelection(0);
            }
        });

        // 右侧sideBar监听
        country_sidebar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    ll_country.setSelection(position);
                }
            }
        });

        ll_country.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                window.dismiss();
                String countryName = null;
                String countryNumber = null;
                String searchContent = country_et_search.getText().toString();
                if (searchContent.length() > 0) {
                    // 按照输入内容进行匹配
                    ArrayList<CountrySortModel> fileterList = (ArrayList<CountrySortModel>) new GetCountryNameSort()
                            .search(searchContent, countryList);
                    //获取国家名字及代码
                    countryName = fileterList.get(i).countryName;
                    countryNumber = fileterList.get(i).countryNumber;
                } else {
                    //获取国家名字及代码
                    countryName = countryList.get(i).countryName;
                    countryNumber = countryList.get(i).countryNumber;
                }
                country.setText(countryName);
                countryCode.setText(countryNumber);
                Log.e(TAG, "countryName: + " + countryName + "countryNumber: " + countryNumber);
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                window.dismiss();
            }
        });
    }


}
