package com.htmessage.fanxinht.acitivity.main.region;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.htmessage.fanxinht.R;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：yichat0504
 * 类描述：RegionFragment 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/10 14:21
 * 邮箱:814326663@qq.com
 */
public class RegionFragment extends Fragment implements RegionView{
    private ListView list_province;
    private ListView list_city;
    // 一个城市的数据列表
    private List<String> citys = new ArrayList<String>();
    private RegionAdapter cAdapter;
    private RegionAdapter pAdapter;
    private String province;
    private RegionPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View regionView = inflater.inflate(R.layout.activity_region, container, false);
        initView(regionView);
        initData();
        setListener();
        return regionView;
    }

    private void setListener() {
        list_province.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.onItemClickListener(position,1);
            }
        });
        list_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.onItemClickListener(position,2);
            }
        });
    }

    private void initData() {
        pAdapter = new RegionAdapter(getBaseContext(), presenter.getProvinceList());
        cAdapter = new RegionAdapter(getBaseContext(), citys);
        list_province.setAdapter(pAdapter);
        list_city.setAdapter(cAdapter);
    }

    private void initView(View regionView) {
        list_province = (ListView) regionView.findViewById(R.id.list_province);
        list_city = (ListView) regionView.findViewById(R.id.list_city);
    }

    @Override
    public void showCityList(List<String> cityList) {
        cAdapter.setData(cityList);
        cAdapter.notifyDataSetChanged();
    }

    @Override
    public void showProvince(String province) {
        this.province = province;
    }

    @Override
    public void showCity(String city) {
        Intent intent=new Intent();
        intent.putExtra("city", city);
        intent.putExtra("province", province);
        getBaseActivity().setResult(Activity.RESULT_OK,intent);
        getBaseActivity().finish();
    }

    @Override
    public void setPresenter(RegionPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Context getBaseContext() {
        return getContext();
    }

    @Override
    public Activity getBaseActivity() {
        return getActivity();
    }


    private class RegionAdapter extends BaseAdapter {
        private Context context;
        private List<String> data;
        private LayoutInflater inflater;
        public RegionAdapter(Context _context, List<String> data) {
            this.context = _context;
            this.data = data;
            inflater = LayoutInflater.from(context);
        }

        public void setData(List<String> _data) {
            data = _data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public String getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_region, parent, false);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new ViewHolder();
                holder.tv_name = (TextView) convertView
                        .findViewById(R.id.tv_name);
                convertView.setTag(holder);
            }
            String regionName = getItem(position);
            holder.tv_name.setText(regionName);
            return convertView;
        }
    }

    static class ViewHolder {
        TextView tv_name;
    }

    @Override
    public void onDestroy() {
        presenter.onDestory();
        super.onDestroy();
    }
}
