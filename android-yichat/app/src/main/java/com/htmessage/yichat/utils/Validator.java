package com.htmessage.yichat.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * 校验器：利用正则表达式校验邮箱、手机号等
 *
 * @author liujiduo
 */
public class Validator {
    /**
     * 正则表达式：验证用户名
     */
    public static final String REGEX_USERNAME = "^[a-zA-Z]\\w{5,17}$";

    /**
     * 正则表达式：验证密码
     */
    public static final String REGEX_PASSWORD = "^[a-zA-Z0-9]{6,20}$";

    /**
     * 正则表达式：验证手机号  .matches("^[1][3578]\\d{9}")
     */
//	public static final String REGEX_MOBILE = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";//
    public static final String REGEX_MOBILE = "^(1[0-9][0-9])\\d{8}$";
    /**
     * 正则表达式：验证邮箱
     */
    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    /**
     * 正则表达式：验证汉字
     */
    public static final String REGEX_CHINESE = "^[\u4e00-\u9fa5],{0,}$";

    /**
     * 正则表达式：验证身份证
     */
    public static final String REGEX_ID_CARD = "(^\\d{18}$)|(^\\d{15}$)";

    /**
     * 正则表达式：验证URL
     */
    public static final String REGEX_URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";

    /**
     * 正则表达式：验证IP地址
     */
    public static final String REGEX_IP_ADDR = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";
    public static final String REGEX_ID = "^[a-zA-Z0-9],{0,}$";

    /**
     * 校验用户名
     *
     * @param username
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUsername(String username) {
        return Pattern.matches(REGEX_USERNAME, username);
    }

    /**
     * 校验密码
     *
     * @param password
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isPassword(String password) {
        return Pattern.matches(REGEX_PASSWORD, password);
    }

    /**
     * 校验手机号
     *
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMobile(String mobile) {
        return Pattern.matches(REGEX_MOBILE, mobile);
    }

    //判断浮点数（double和float）
    public static boolean isDouble(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 校验邮箱
     *
     * @param email
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isEmail(String email) {
        return Pattern.matches(REGEX_EMAIL, email);
    }

    /**
     * 校验汉字
     *
     * @param chinese
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isChinese(String chinese) {
        return Pattern.matches(REGEX_CHINESE, chinese);
    }

    /**
     * 校验用户输入的ID
     *
     * @param chinese
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUserId(String chinese) {
        return Pattern.matches(REGEX_ID, chinese);
    }

    /**
     * 校验身份证
     *
     * @param idCard
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isIDCard(String idCard) {
        return Pattern.matches(REGEX_ID_CARD, idCard);
    }

    /**
     * 校验URL
     *
     * @param url
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUrl(String url) {
        return Pattern.matches(REGEX_URL, url);
    }

    /**
     * 校验IP地址
     *
     * @param ipAddr
     * @return
     */
    public static boolean isIPAddr(String ipAddr) {
        return Pattern.matches(REGEX_IP_ADDR, ipAddr);
    }

    // 判断一个字符是否是中文
    public static boolean isChinese(char c) {
        return c >= 0x4E00 && c <= 0x9FA5;// 根据字节码判断
    }

    // 判断一个字符串是否含有中文
    public static boolean isChineseStr(String str) {
        if (str == null)
            return false;
        for (char c : str.toCharArray()) {
            if (isChinese(c))
                return true;// 有一个中文字符就返回
        }
        return false;
    }



    public static String formatMoney(double money) {
        String strMoney = String.valueOf(money);
        BigDecimal zonggong = new BigDecimal(strMoney);
        return zonggong.setScale(2, 4).toPlainString();
    }

    public static String formatMoney(float money) {
        String strMoney = String.valueOf(money);
        BigDecimal zonggong = new BigDecimal(strMoney);
        return zonggong.setScale(2, 4).toPlainString();
    }


    public static boolean isNotEmptyAndNull(String str) {
        return str != null && str.trim().length() > 0 && !"null".equals(str);
    }

    public static float formatMoneyFloat(float money) {
        BigDecimal zonggong = new BigDecimal((double) money);
        return zonggong.setScale(2, 4).floatValue();
    }


    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() <= 0;
    }

    public static boolean isNotEmpty(String str) {
        return str != null && str.trim().length() > 0;
    }


    public static boolean checkBankCard(String cardId) {
        if (TextUtils.isEmpty(cardId)) {
            return false;
        } else {
            char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
            return bit == 78 ? false : cardId.charAt(cardId.length() - 1) == bit;
        }
    }

    private static char getBankCardCheckCode(String nonCheckCodeCardId) {
        if (nonCheckCodeCardId != null && nonCheckCodeCardId.trim().length() != 0 && nonCheckCodeCardId.matches("\\d+")) {
            char[] chs = nonCheckCodeCardId.trim().toCharArray();
            int luhmSum = 0;
            int i = chs.length - 1;

            for (int j = 0; i >= 0; ++j) {
                int k = chs[i] - 48;
                if (j % 2 == 0) {
                    k *= 2;
                    k = k / 10 + k % 10;
                }

                luhmSum += k;
                --i;
            }

            return luhmSum % 10 == 0 ? '0' : (char) (10 - luhmSum % 10 + 48);
        } else {
            return 'N';
        }
    }


    public static boolean isNumber(String str) {
        return !isEmpty(str) ? str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$") : false;
    }


    public static String formatMoney(String money) {
        if (!isNotEmptyAndNull(money)) {
            return "";
        } else {
            BigDecimal amount = new BigDecimal(money.trim());
            return amount.setScale(2, 4).toPlainString();
        }
    }

    public static float formatMoneyFloat(String money) {
        if (!isNotEmptyAndNull(money)) {
            return 0.0F;
        } else {
            BigDecimal zonggong = new BigDecimal(money);
            return zonggong.setScale(2, 4).floatValue();
        }
    }

    public static double formatMoneyDouble(String money) {
        if (!isNotEmptyAndNull(money)) {
            return 0.0D;
        } else {
            BigDecimal zonggong = new BigDecimal(money);
            return zonggong.setScale(2, 4).doubleValue();
        }
    }


    public static double formatMoneyDouble(double money) {
        BigDecimal zonggong = new BigDecimal(money);
        return zonggong.setScale(2, 4).doubleValue();
    }

    public static Double string2double(String num) {
        Double d = null;
        if (isEmpty(num)) {
            return Double.valueOf(0.0D);
        } else {
            try {
                d = Double.valueOf(num);
            } catch (NumberFormatException var3) {
                var3.printStackTrace();
            }

            return d;
        }
    }

    public static String replace(String str) {
        byte start;
        int end;
        StringBuilder builder;
        int self;
        StringBuilder var5;
        if (str.length() >= 8) {
            start = 4;
            end = str.length() - 4;
            builder = new StringBuilder(end - start);

            for (self = 0; self < end - start; ++self) {
                builder = builder.append("*");
            }

            var5 = new StringBuilder(str);
            return var5.replace(start, end, builder.toString()).toString();
        } else if (str.length() == 0) {
            return str;
        } else {
            start = 2;
            end = str.length() - 2;
            builder = new StringBuilder(end - start);

            for (self = 0; self < end - start; ++self) {
                builder = builder.append("*");
            }

            var5 = new StringBuilder(str);
            return var5.replace(start, end, builder.toString()).toString();
        }
    }

    public static String nameReplace(String userName) {
        if (isEmpty(userName)) {
            userName = "";
        }

        int nameLength = userName.length();
        StringBuilder sb = new StringBuilder();
        if (nameLength <= 1) {
            sb.append("*");
        } else if (nameLength == 2) {
            sb.append("*").append(userName.charAt(nameLength - 1));
        } else if (nameLength == 3) {
            sb.append("**").append(userName.charAt(nameLength - 1));
        } else {
            sb.append("***").append(userName.charAt(nameLength - 1));
        }

        return sb.toString();
    }

    public static String idCardReplace(String cardNum) {
        if (isEmpty(cardNum)) {
            return "";
        } else {
            StringBuffer sb = new StringBuffer();
            if (cardNum.length() <= 15) {
                sb.append(cardNum.charAt(0)).append("*************").append(cardNum.charAt(cardNum.length() - 1));
            } else {
                sb.append(cardNum.charAt(0)).append("****************").append(cardNum.charAt(cardNum.length() - 1));
            }

            return sb.toString();
        }
    }

    public static String getFixUserName(String str) {
        return !isEmpty(str) && str.length() > 10 ? str.substring(0, 10) + "..." : str;
    }

    public static String phoneNumReplace(String phoneNum) {
        if (isEmpty(phoneNum)) {
            return "";
        } else if (!isMobile(phoneNum)) {
            return phoneNum;
        } else {
            StringBuffer sb = new StringBuffer(phoneNum);
            sb.replace(3, 7, "****");
            return sb.toString();
        }
    }

    public static boolean isSameStr(String one, String two) {
        return isNotEmpty(one) && isNotEmpty(two) && one.equals(two);
    }

    public static String getAfterFour(String bankCard) {
        return bankCard.length() > 4 ? bankCard.substring(bankCard.length() - 4, bankCard.length()) : bankCard;
    }

}
