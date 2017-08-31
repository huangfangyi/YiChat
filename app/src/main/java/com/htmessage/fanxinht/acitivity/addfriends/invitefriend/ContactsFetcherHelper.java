package com.htmessage.fanxinht.acitivity.addfriends.invitefriend;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import com.alibaba.fastjson.JSONArray;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.utils.CommonUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactsFetcherHelper implements Runnable {
    private static final String TAG = ContactsFetcherHelper.class.getSimpleName();

    public interface OnFetchContactsListener {
        void onFetcherContactsComplete(List<ContactInfo> list);
    }

    public interface OnContactsHasListener {
        void onContactsComplete(List<ContactInfo> list, JSONArray moblies, String mobileString);
    }

    /**
     * 查询联系人信息
     *
     * @param context
     */
    public static void queryContactInfo(final Context context, final OnContactsHasListener listener) {
        CommonUtils.showDialogNumal(context, context.getString(R.string.loading));
        ContactsFetcherHelper mContactsFetcherHelper = new ContactsFetcherHelper();
        mContactsFetcherHelper.start(context, new ContactsFetcherHelper.OnFetchContactsListener() {

            @Override
            public void onFetcherContactsComplete(final List<ContactInfo> list) {
                String listToString = listToString(list);
                JSONArray mobiles = listToArray(list);
                CommonUtils.cencelDialog();
                listener.onContactsComplete(list, mobiles,listToString);
            }
        });
    }

    public static String listToString(List<ContactInfo> stringList) {
        if (stringList == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (ContactInfo string : stringList) {
            CommonUtils.setContactsInfoInitialLetter(string);
            if (flag) {
                result.append(",");
            } else {
                flag = true;
            }
            result.append(string.getPhoneNumber());
        }
        return result.toString();
    }

    public static JSONArray listToArray(List<ContactInfo> stringList) {

        if (stringList == null) {
            return null;
        }
        JSONArray mobiles = new JSONArray();
        for (int i = 0; i < stringList.size(); i++) {
            mobiles.add(stringList.get(i).getPhoneNumber());
        }
        return mobiles;
    }


    private OnFetchContactsListener mListener;
    private boolean mCancel = false;
    private Context mContext;
    private boolean mIsFetching = false;
    private Thread mFetchThread;

    public void start(Context context, OnFetchContactsListener l) {
        if (mIsFetching) {
            return;
        }
        mContext = context;
        mCancel = false;
        mIsFetching = true;
        mListener = l;
        mFetchThread = new Thread(this);
        mFetchThread.start();
    }

    public void cancel() {
        mCancel = true;
    }

    @Override
    public void run() {
        List<ContactInfo> list = new ArrayList<ContactInfo>();
        Set<String> set = new HashSet<String>();
        if (!mCancel) {
            //读取手机里的手机联系人
            getPhoneContactHighVersion(list, set);
        }
        if (!mCancel) {
            //读取Sim卡中的手机联系人
            getSimContact("content://icc/adn", list, set);
        }

        if (!mCancel) {
            getSimContact("content://sim/adn", list, set);
        }
        if (!mCancel && null != mListener) {
            mIsFetching = false;
            mListener.onFetcherContactsComplete(list);
        }
    }

    // 从本机中取号
    private void getPhoneContactHighVersion(List<ContactInfo> list,
                                            Set<String> set) {
        // 得到ContentResolver对象
        try {
            if (null == mContext) {
                return;
            }
            ContentResolver cr = mContext.getContentResolver();
            if (null == cr) {
                return;
            }
            // 取得电话本中开始一项的光标
            Uri uri = ContactsContract.Contacts.CONTENT_URI;
            Cursor cursor = null;
            try {
                String[] projection = {ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME, "sort_key"};
                cursor = cr.query(uri, projection, null, null,
                        null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            while (!mCancel && null != cursor && cursor.moveToNext()) {
                int nameFieldColumnIndex = cursor
                        .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                int idCol = cursor.getInt(cursor
                        .getColumnIndex(ContactsContract.Contacts._ID));
                int sort_key_index = cursor.getColumnIndex("sort_key");
                // 取得联系人名字
                // 取得联系人ID
                Cursor phone = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                        new String[]{Integer.toString(idCol)}, null);//
                // 再类ContactsContract.CommonDataKinds.Phone中根据查询相应id联系人的所有电话；

                // 取得电话号码(可能存在多个号码)
                while (!mCancel && phone.moveToNext()) {
                    String strPhoneNumber = formatMobileNumber(phone
                            .getString(phone
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    boolean b = isUserNumber(strPhoneNumber);
                    if (b) {
                        ContactInfo cci = new ContactInfo();
                        cci.setName(cursor.getString(nameFieldColumnIndex));
//                        cci.setLetter(cursor.getString(sort_key_index));
                        cci.setPhoneNumber(strPhoneNumber);
                        cci.setId(String.valueOf(idCol));
                        cci.setType(1);
                        cci.setUserId("0");
                        CommonUtils.setContactsInfoInitialLetter(cci);
                        list.add(cci);
                        set.add(cci.getPhoneNumber());
                    }
                }
                phone.close();
            }
            if (null != cursor) {
                cursor.close();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void getSimContact(String adn, List<ContactInfo> list,
                               Set<String> set) {
        // 读取SIM卡手机号,有两种可能:content://icc/adn与content://sim/adn
        Cursor cursor = null;
        try {

            Intent intent = new Intent();
            intent.setData(Uri.parse(adn));
            Uri uri = intent.getData();
            cursor = mContext.getContentResolver().query(uri, null,
                    null, null, null);
            if (cursor != null) {
                while (!mCancel && cursor.moveToNext()) {
                    // 取得联系人名字
                    int nameIndex = cursor.getColumnIndex("name");
                    // 取得电话号码
                    int numberIndex = cursor.getColumnIndex("number");
                    String number = cursor.getString(numberIndex);
                    if (isUserNumber(number)) {// 是否是手机号码
                        ContactInfo sci = new ContactInfo();
                        sci.setPhoneNumber(formatMobileNumber(number));
                        sci.setName(cursor.getString(nameIndex));
                        if (!isContain(set, sci.getPhoneNumber())) {// //是否在LIST内存在
                            list.add(sci);
                            set.add(sci.getPhoneNumber());
                        }
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            if (cursor != null)
                cursor.close();
        }
    }

    private String formatMobileNumber(String num2) {
        String num;
        if (num2 != null) {
            // 有的通讯录格式为135-1568-1234
            num = num2.replaceAll("-", "");
            // 有的通讯录格式中- 变成了空格
            num = num.replaceAll(" ", "");
            if (num.startsWith("+86")) {
                num = num.substring(3);
            } else if (num.startsWith("86")) {
                num = num.substring(2);
            } else if (num.startsWith("86")) {
                num = num.substring(2);
            }
        } else {
            num = "";
        }
        // 有些手机号码格式 86后有空格
        return num.trim();
    }

    private boolean isUserNumber(String num) {
        if (null == num || "".equalsIgnoreCase(num)) {
            return false;
        }
        boolean re = false;
        if (num.length() == 11) {
            if (num.startsWith("1")) {
                re = true;
            }
        }
        return re;
    }

    private boolean isContain(Set<String> set, String un) {
        return set.contains(un);
    }
}