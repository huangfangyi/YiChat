package com.htmessage.fanxinht.acitivity.main.region;

import com.htmessage.fanxinht.acitivity.BaseView;

import java.util.List;

/**
 * 项目名称：yichat0504
 * 类描述：RegionView 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/10 14:05
 * 邮箱:814326663@qq.com
 */
public interface RegionView extends BaseView<RegionPresenter>{
    void showCityList(List<String> cityList);
    void showProvince(String province);
    void showCity(String city);
}
