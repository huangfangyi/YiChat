package com.htmessage.fanxinht.acitivity.main.region;

import com.htmessage.fanxinht.acitivity.BasePresenter;

import java.util.List;

/**
 * 项目名称：yichat0504
 * 类描述：RegionBasePrestener 描述:
 * 创建人：songlijie
 * 创建时间：2017/7/10 13:53
 * 邮箱:814326663@qq.com
 */
public interface RegionBasePrestener extends BasePresenter {
    List<String> getProvinceList();
    List<String> getCityList(int position);
    void onDestory();
}
