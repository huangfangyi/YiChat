package com.fanxin.huangfangyi.main.activity;

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

import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.ui.BaseActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegionActivity extends BaseActivity {
	private ListView list_province;
	private ListView list_city;
	// 一个省份的数据列表
	private List<String> provinces = new ArrayList<String>();
	// 一个城市的数据列表
	private List<String> citys = new ArrayList<String>();
	private RegionAdapter cAdapter;
	private RegionAdapter pAdapter;
	private String province;
	// 所有省份下的城市列表
	/**
	 * 省份列表
	 */
	private final int[] ARRAY_CITY = new int[] { R.array.beijin_province_item,
			R.array.heibei_province_item, R.array.shandong_province_item,
			R.array.shanghai_province_item, R.array.guangdong_province_item,
			R.array.anhui_province_item, R.array.fujian_province_item,
			R.array.gansu_province_item, R.array.guangxi_province_item,
			R.array.guizhou_province_item, R.array.hainan_province_item,
			R.array.henan_province_item, R.array.heilongjiang_province_item,
			R.array.hubei_province_item, R.array.hunan_province_item,
			R.array.jilin_province_item, R.array.jiangsu_province_item,
			R.array.jiangxi_province_item, R.array.liaoning_province_item,
			R.array.neimenggu_province_item, R.array.ningxia_province_item,
			R.array.qinghai_province_item, R.array.shanxi1_province_item,
			R.array.shanxi2_province_item, R.array.sichuan_province_item,
			R.array.tianjin_province_item, R.array.xizang_province_item,
			R.array.xinjiang_province_item, R.array.yunnan_province_item,
			R.array.zhejiang_province_item, R.array.chongqing_province_item,
			R.array.taiwan_province_item, R.array.hongkong_province_item,
			R.array.aomen_province_item };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_region);
		initView();
	}

	private void initView() {
		list_province = (ListView) this.findViewById(R.id.list_province);
		list_city = (ListView) this.findViewById(R.id.list_city);
		// 获取省份列表数据
		provinces = Arrays.asList(this.getResources().getStringArray(
				R.array.province_item));
		// 初始化的时候选择一个省的所有城市预先显示--也可以开始空白，点击省份后再显示城市
		// citys=Arrays.asList(this.getResources().getStringArray(ARRAY_CITY[0]));
		pAdapter = new RegionAdapter(RegionActivity.this, provinces);
		cAdapter = new RegionAdapter(RegionActivity.this, citys);
		list_province.setAdapter(pAdapter);
		list_city.setAdapter(cAdapter);

		list_province.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 点击省份两个动作---显示城市和保存当前选择的省份值
				citys = Arrays.asList(getResources().getStringArray(
						ARRAY_CITY[position]));
				// 更新cAdapter里面的数据
                cAdapter.setData(citys);
                //刷新列表
                cAdapter.notifyDataSetChanged();
                 //保存当前选定省份值
                province=pAdapter.getItem(position);
			}

		});
		
		list_city.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
                 //城市列表点击后，即是选定了城市，返回数据即可
				String city=cAdapter.getItem(position);
				Intent intent=new Intent();
				intent.putExtra("city", city);
				intent.putExtra("province", province);
				setResult(RESULT_OK,intent);
				finish();
			}
			
			
		});
	}

	class RegionAdapter extends BaseAdapter {
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
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public String getItem(int position) {
			// TODO Auto-generated method stub
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.fy_item_region, parent,
						false);

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

}
