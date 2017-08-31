package com.htmessage.fanxinht.acitivity.login.country;

import android.content.Context;

import com.htmessage.fanxinht.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 项目名称：htmessage_sdk
 * 类描述：CountryCodeUtil 描述:
 * 创建人：songlijie
 * 创建时间：2017/3/3 19:25
 * 邮箱:814326663@qq.com
 */
public class CountryCodeUtil {
    /**
     * 获取国家列表
     */
    public static List<CountrySortModel> getCountryList(Context context, boolean isCn) {

        List<CountrySortModel> mAllCountryList = new ArrayList<>();
        String[] countryList = context.getResources().getStringArray(R.array.country_code_list_ch);
        if (!isCn){
            countryList = context.getResources().getStringArray(R.array.country_code_list_en);
        }

        for (int i = 0, length = countryList.length; i < length; i++) {
            String[] country = countryList[i].split("\\*");
            String countryName = country[0];
            String countryNumber = country[1];
            String countrySortKey = new CharacterParserUtil().getSelling(countryName);
            CountrySortModel countrySortModel = new CountrySortModel(countryName, countryNumber,
                    countrySortKey);
            String sortLetter = new GetCountryNameSort().getSortLetterBySortKey(countrySortKey);
            if (sortLetter == null) {
                sortLetter = new GetCountryNameSort().getSortLetterBySortKey(countryName);
            }
            countrySortModel.sortLetters = sortLetter;
            mAllCountryList.add(countrySortModel);
        }
        Collections.sort(mAllCountryList, new CountryComparator());
        return mAllCountryList;
    }
}
