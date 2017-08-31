package com.htmessage.fanxinht.acitivity.main.region;

import com.htmessage.fanxinht.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 项目名称：yichat0504
 * 类描述：RegionPresenter 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/10 14:04
 * 邮箱:814326663@qq.com
 */
public class RegionPresenter implements RegionBasePrestener {
    // 一个省份的数据列表
    private List<String> provinces = new ArrayList<String>();
    // 一个城市的数据列表
    private List<String> citys = new ArrayList<String>();
    // 所有省份下的城市列表
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

    private RegionView regionView;

    public RegionPresenter(RegionView regionView) {
        this.regionView = regionView;
        this.regionView.setPresenter(this);
    }

    @Override
    public List<String> getProvinceList() {
        provinces = Arrays.asList(regionView.getBaseActivity().getResources().getStringArray(R.array.province_item));
        return provinces;
    }

    @Override
    public List<String> getCityList(int position) {
       citys =  Arrays.asList(regionView.getBaseActivity().getResources().getStringArray(ARRAY_CITY[position]));
        return citys;
    }

    @Override
    public void onDestory() {
        regionView = null;
    }

    @Override
    public void start() {

    }
   public void onItemClickListener(int position,int type){
       if (type ==1){
            String province = provinces.get(position);
            regionView.showProvince(province);
            regionView.showCityList(getCityList(position));
       }else{
           String city = citys.get(position);
           regionView.showCity(city);
       }
   }
}
