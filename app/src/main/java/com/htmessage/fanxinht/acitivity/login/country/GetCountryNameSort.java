/*      						
 * Copyright 2010 Beijing Xinwei, Inc. All rights reserved.
 * 
 * History:
 * ------------------------------------------------------------------------------
 * Date    	|  Who  		|  What  
 * 2015��3��20��	| duanbokan 	| 	create the file                       
 */

package com.htmessage.fanxinht.acitivity.login.country;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 取姓名首字母及模糊匹配查询
 * <p>
 * <p>
 * 类详细描述
 * </p>
 *
 * @author duanbokan
 */

public class GetCountryNameSort {

    CharacterParserUtil characterParser = CharacterParserUtil.getInstance();

    String chReg = "[\\u4E00-\\u9FA5]+";// 中文字符串匹配

    /***
     * 将名字转化为拼音并获得首字母
     *
     * @param name
     * @return
     */
    public String getSortLetter(String name) {
        String letter = "#";
        if (name == null) {
            return letter;
        }
        // 汉字转换成拼音
        String pinyin = characterParser.getSelling(name);
        String sortString = pinyin.substring(0, 1).toUpperCase(Locale.CHINESE);

        // 正则表达式，判断首字母是否是英文字母
        if (sortString.matches("[A-Z]")) {
            letter = sortString.toUpperCase(Locale.CHINESE);
        }
        return letter;
    }

    /***
     * 取首字母
     *
     * @param sortKey
     * @return
     */
    public String getSortLetterBySortKey(String sortKey) {
        if (sortKey == null || "".equals(sortKey.trim())) {
            return null;
        }
        String letter = "#";
        // 汉字转换成拼音
        String sortString = sortKey.trim().substring(0, 1).toUpperCase(Locale.CHINESE);
        // 正则表达式，判断首字母是否是英文字母
        if (sortString.matches("[A-Z]")) {
            letter = sortString.toUpperCase(Locale.CHINESE);
        }
        return letter;
    }

    /***
     * 根据输入内容进行查询
     *
     * @param str  输入内容
     * @param list 需要查询的List
     * @return 查询结果
     */
    public List<CountrySortModel> search(String str, List<CountrySortModel> list) {
        List<CountrySortModel> filterList = new ArrayList<CountrySortModel>();// 过滤后的list
        // if (str.matches("^([0-9]|[/+])*$")) {// 正则表达式 匹配号码
        if (str.matches("^([0-9]|[/+]).*")) {// 正则表达式 匹配以数字或者加号开头的字符串(包括了带空格及-分割的号码)
            String simpleStr = str.replaceAll("\\-|\\s", "");
            for (CountrySortModel contact : list) {
                if (contact.countryName != null && contact.countryName != null) {
                    if (contact.simpleCountryNumber.contains(simpleStr)
                            || contact.countryName.contains(str)) {
                        if (!filterList.contains(contact)) {
                            filterList.add(contact);
                        }
                    }
                }
            }
        } else {
            for (CountrySortModel contact : list) {
                if (contact.countryNumber != null && contact.countryName != null) {
                    // 姓名全匹配,姓名首字母简拼匹配,姓名全字母匹配
                    if (contact.countryName.toLowerCase(Locale.CHINESE).contains(
                            str.toLowerCase(Locale.CHINESE))
                            || contact.countrySortKey.toLowerCase(Locale.CHINESE).replace(" ", "")
                            .contains(str.toLowerCase(Locale.CHINESE))
                            || contact.sortToken.simpleSpell.toLowerCase(Locale.CHINESE).contains(
                            str.toLowerCase(Locale.CHINESE))
                            || contact.sortToken.wholeSpell.toLowerCase(Locale.CHINESE).contains(
                            str.toLowerCase(Locale.CHINESE))) {
                        if (!filterList.contains(contact)) {
                            filterList.add(contact);
                        }
                    }
                }
            }
        }
        return filterList;
    }
}
