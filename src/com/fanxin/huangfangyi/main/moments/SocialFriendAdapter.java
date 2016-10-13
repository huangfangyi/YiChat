package com.fanxin.huangfangyi.main.moments;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;
import com.fanxin.huangfangyi.DemoApplication;
import com.fanxin.huangfangyi.DemoHelper;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.huangfangyi.main.utils.OkHttpManager;
import com.fanxin.huangfangyi.main.utils.Param;
import com.fanxin.easeui.domain.EaseUser;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SocialFriendAdapter extends BaseAdapter {

    private SocialFriendActivity context;
    private List<JSONObject> users;
    private LayoutInflater inflater;
    public RelativeLayout re_edittext;

    private String recordDate = "";
    private String myuserID;
    private String myAvatar;
    private String myNick;

    public SocialFriendAdapter(SocialFriendActivity context1,
                               List<JSONObject> jsonArray) {
        this.context = context1;

        this.users = jsonArray;
        inflater = LayoutInflater.from(context);
        myuserID = DemoHelper.getInstance().getCurrentUsernName();
        myNick=DemoApplication.getInstance().getUserJson().getString(FXConstant.JSON_KEY_NICK);
        myAvatar =DemoApplication.getInstance().getUserJson().getString(FXConstant.JSON_KEY_AVATAR);
        // 底部评论输入框
        re_edittext = (RelativeLayout) context.findViewById(R.id.re_edittext);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public JSONObject getItem(int position) {

        return users.get(position);

    }

    public List<JSONObject> getJSONs() {

        return users;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fx_item_moments_me, parent,
                    false);

        }


        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();

            holder.image_1 = (SimpleDraweeView) convertView
                    .findViewById(R.id.image_1);

            holder.tv_num = (TextView) convertView
                    .findViewById(R.id.tv_num);
            holder.tv_day = (TextView) convertView
                    .findViewById(R.id.tv_day);
            holder.tv_month = (TextView) convertView
                    .findViewById(R.id.tv_month);
            holder.tv_content = (TextView) convertView
                    .findViewById(R.id.tv_content);
            holder.tv_location = (TextView) convertView
                    .findViewById(R.id.tv_location);
            holder.view_header = (View) convertView
                    .findViewById(R.id.view_header);
            convertView.setTag(holder);
        }

        JSONObject json = users.get(position);
        // 如果数据出错....

        if (json == null || json.size() == 0) {
            users.remove(position);
            this.notifyDataSetChanged();
        }
        final String userID = json.getString("userID");
        String content = json.getString("content");
        String imageStr = json.getString("imageStr");
        String location = json.getString("location");
        final String sID = json.getString("sID");
        // String token = json.getString("token");
        String rel_time = json.getString("time");
        // 设置文章中的图片
        Log.e("imageStr--->>", imageStr);
        if (!imageStr.equals("0")) {
            String[] images = imageStr.split("split");
            int imNumb = images.length;
            holder.image_1.setVisibility(View.VISIBLE);
            holder.image_1.setImageURI(Uri.parse(FXConstant.URL_SOCIAL_PHOTO
                    + images[0]));
            holder.tv_num.setVisibility(View.VISIBLE);
            holder.tv_num.setText("共" + String.valueOf(imNumb) + "张");
        } else {

            holder.image_1.setVisibility(View.GONE);
            holder.tv_num.setVisibility(View.GONE);
        }
        // 显示位置
        if (location != null && !location.equals("0")) {
            holder.tv_location.setVisibility(View.VISIBLE);
            holder.tv_location.setText(location);
        }
        // 显示文章内容
        holder.tv_content.setText(content);

        // 显示时间

        setDateText(rel_time, DemoApplication.getInstance().getTime(),
                holder.tv_day, holder.tv_month, holder.view_header);

        return convertView;


    }

    public static class ViewHolder {

        // 时间
        TextView tv_num;

        SimpleDraweeView image_1;

        // 动态内容
        TextView tv_content;

        // 位置
        TextView tv_location;
        TextView tv_month;
        TextView tv_day;
        // 顶部空格
        View view_header;

    }

    private void setDateText(String rel_time, String nowTime, TextView tv_day,
                             TextView tv_month, View view_header) {
        String date = rel_time.substring(0, 10);
        String moth = rel_time.substring(5, 7);
        String day = rel_time.substring(8, 10);
        if (moth.startsWith("0")) {
            moth = moth.substring(1);
        }
        if (!date.equals(recordDate)) {
            view_header.setVisibility(View.VISIBLE);
            tv_day.setVisibility(View.VISIBLE);
            tv_month.setVisibility(View.VISIBLE);
            tv_day.setText(day);
            tv_month.setText(moth + "月");
        } else {
            view_header.setVisibility(View.GONE);
            tv_day.setVisibility(View.GONE);
            tv_month.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String getTime(String rel_time, String now_time) {
        String backStr = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(rel_time);
            d2 = format.parse(now_time);

            // 毫秒ms
            long diff = d2.getTime() - d1.getTime();

            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffDays != 0) {
                if (diffDays < 30) {
                    if (1 < diffDays && diffDays < 2) {
                        backStr = "昨天";
                    } else if (1 < diffDays && diffDays < 2) {
                        backStr = "前天";

                    } else {

                        backStr = String.valueOf(diffDays) + "天前";
                    }
                } else {
                    backStr = "很久以前";
                }

            } else if (diffHours != 0) {
                backStr = String.valueOf(diffHours) + "小时前";

            } else if (diffMinutes != 0) {
                backStr = String.valueOf(diffMinutes) + "分钟前";

            } else {

                backStr = "刚刚";

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return backStr;

    }

    class ImageListener implements OnClickListener {
        String[] images;
        int page;

        public ImageListener(String[] images, int page) {

            this.images = images;
            this.page = page;
        }

        @Override
        public void onClick(View v) {

            Intent intent = new Intent();
            intent.setClass(context, BigImageActivity.class);
            intent.putExtra("images", images);
            intent.putExtra("page", page);
            context.startActivity(intent);

        }

    }

    // 设置点赞的
    private void setGoodTextClick(TextView mTextView2, JSONArray data,
                                  LinearLayout ll_goodmembers, View view, int cSize) {
        if (data == null || data.size() == 0) {
            ll_goodmembers.setVisibility(View.GONE);
        } else {

            ll_goodmembers.setVisibility(View.VISIBLE);
        }
        if (cSize > 0 && data.size() > 0) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);

        }
        SpannableStringBuilder ssb = new SpannableStringBuilder();

        int start = 0;
        for (int i = 0; i < data.size(); i++) {

            JSONObject json_good = data.getJSONObject(i);
            // String userID = json_good.getString("userID");

            String userID_temp = json_good.getString("userID");
            String nick = userID_temp;

            if (userID_temp.equals(myuserID)) {
                nick = myNick;

            } else {

                EaseUser user = DemoHelper.getInstance().getContactList()
                        .get(userID_temp);
                if (user != null) {

                    nick = user.getNick();

                }

            }
            if (i != (data.size() - 1) && data.size() > 1) {
                ssb.append(nick + ",");
            } else {
                ssb.append(nick);

            }

            ssb.setSpan(new TextViewURLSpan(nick, userID_temp, 0), start, start
                    + nick.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            start = ssb.length();

        }

        mTextView2.setText(ssb);
        mTextView2.setMovementMethod(LinkMovementMethod.getInstance());

        // SpannableStringBuilder newString = new SpannableStringBuilder();
        // SpannableString temp = (SpannableString) mTextView2.getText();
        // newString.append("000000");
        // newString.append(temp);
        // mTextView2.setText(newString);
    }

    // 设置点赞的
    private void setCommentTextClick(TextView mTextView2, JSONArray data,
                                     View view, int goodSize) {
        if (goodSize > 0 && data.size() > 0) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
        if (data.size() == 0) {
            mTextView2.setVisibility(View.GONE);
        } else {
            mTextView2.setVisibility(View.VISIBLE);

        }
        SpannableStringBuilder ssb = new SpannableStringBuilder();

        int start = 0;

        for (int i = 0; i < data.size(); i++) {

            JSONObject json = data.getJSONObject(i);
            String userID_temp = json.getString("userID");
            String content = json.getString("content");
            String scID = json.getString("scID");
            String nick = userID_temp;

            if (userID_temp.equals(myuserID)) {
                nick = myNick;

            } else {

                EaseUser user = DemoHelper.getInstance().getContactList()
                        .get(userID_temp);
                if (user != null) {

                    nick = user.getNick();

                }

            }
            String content_0 = "";
            String content_1 = ": " + content;
            String content_2 = ": " + content + "\n";
            if (i == (data.size() - 1) || (data.size() == 1 && i == 0)) {
                ssb.append(nick + content_1);
                content_0 = content_1;
            } else {

                ssb.append(nick + content_2);
                content_0 = content_2;
            }

            ssb.setSpan(new TextViewURLSpan(nick, userID_temp, 1), start, start
                    + nick.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (userID_temp.equals(myuserID)) {

                ssb.setSpan(new TextViewURLSpan(nick, userID_temp, i, scID, 2,
                                mTextView2, data, view, goodSize), start,
                        start + nick.length() + content_0.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            start = ssb.length();

        }

        mTextView2.setText(ssb);
        mTextView2.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private class TextViewURLSpan extends ClickableSpan {
        private String userID;
        // 0是点赞里面的名字。1是评论里面的名字；2是评论中的删除
        private int type = 0;
        private TextView ctextView;
        private JSONArray cjsons;
        private View view;
        private int goodSize;
        private String scID;
        private int postion;

        public TextViewURLSpan(String nick, String userID, int postion,
                               String scID, int type, TextView ctextView, JSONArray cjsons,
                               View view, int goodSize) {
            this.userID = userID;
            this.type = type;
            this.ctextView = ctextView;
            this.cjsons = cjsons;
            this.view = view;
            this.goodSize = goodSize;
            this.scID = scID;
            this.postion = postion;
        }

        public TextViewURLSpan(String nick, String userID, int type) {
            this.userID = userID;
            this.type = type;

        }

        @Override
        public void updateDrawState(TextPaint ds) {
            if (type != 2) {
                ds.setColor(context.getResources().getColor(R.color.text_color));

            }
            ds.setUnderlineText(false); // 去掉下划线
        }

        @Override
        public void onClick(final View widget) {

            if (widget instanceof TextView) {
                ((TextView) widget).setHighlightColor(context.getResources()
                        .getColor(android.R.color.darker_gray));
                new Handler().postDelayed(new Runnable() {

                    public void run() {

                        ((TextView) widget).setHighlightColor(context
                                .getResources().getColor(
                                        android.R.color.transparent));

                    }

                }, 1000);

            }

            if (type == 2) {
                showDeleteDialog(userID, postion, scID, type, ctextView,
                        cjsons, view, goodSize);

            } else {

                Toast.makeText(context, userID, Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     * 显示发表评论的输入框
     */

    public void showCommentEditText(final String sID,
                                    final TextView tv_comment, final JSONArray jsons, final View view,
                                    final int goodSize) {
        if (re_edittext == null || re_edittext.getVisibility() != View.VISIBLE) {
            re_edittext = (RelativeLayout) context
                    .findViewById(R.id.re_edittext);
            re_edittext.setVisibility(View.VISIBLE);
            final EditText et_comment = (EditText) re_edittext
                    .findViewById(R.id.et_comment);
            Button btn_send = (Button) re_edittext.findViewById(R.id.btn_send);
            btn_send.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    String comment = et_comment.getText().toString().trim();
                    if (TextUtils.isEmpty(comment)) {
                        Toast.makeText(context, "请输入评论", Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }

                    submitComment(sID, comment, tv_comment, jsons, view,
                            goodSize);
                    et_comment.setText("");
                    hideCommentEditText();
                }

            });
        }

    }

    /**
     * 隐藏发表评论的输入框
     */
    public void hideCommentEditText() {
        if (re_edittext != null && re_edittext.getVisibility() == View.VISIBLE)
            re_edittext.setVisibility(View.GONE);
    }

    /**
     * 提交评论
     */
    private void submitComment(String sID, String comment, TextView tv_comment,
                               JSONArray jsons, View view, int goodSize) {
        String tag = String.valueOf(System.currentTimeMillis());

        // 即时改变当前UI
        JSONObject json = new JSONObject();
        json.put("userID", myuserID);
        json.put("content", comment);
        // 本地标记，方便本地定位删除，服务器端用不到这个字段
        json.put("tag", tag);
        jsons.add(json);
        setCommentTextClick(tv_comment, jsons, view, goodSize);
        //
        // 更新后台

        List<Param> params = new ArrayList<Param>();
        params.add(new Param("sID", sID));
        params.add(new Param("content", comment));
        params.add(new Param("userID", myuserID));
        params.add(new Param("tag", tag));
        OkHttpManager.getInstance().post(params, FXConstant.URL_SOCIAL_COMMENT, new OkHttpManager.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {

            }

            @Override
            public void onFailure(String errorMsg) {

            }
        });


    }

    /**
     * 点赞
     */
    public void setGood(String sID, TextView tv_good, JSONArray jsons,
                        LinearLayout ll_goodmembers_temp, View view, int cSize) {
        // 即时改变当前UI
        JSONObject json = new JSONObject();
        json.put("userID", myuserID);
        jsons.add(json);
        setGoodTextClick(tv_good, jsons, ll_goodmembers_temp, view, cSize);
        // 更新后台

        List<Param> params = new ArrayList<Param>();
        params.add(new Param("sID", sID));
        params.add(new Param("userID", myuserID));
        OkHttpManager.getInstance().post(params, FXConstant.URL_SOCIAL_GOOD, new OkHttpManager.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {

            }

            @Override
            public void onFailure(String errorMsg) {

            }
        });


    }

    /**
     * 取消点赞
     */
    public void cancelGood(String sID, TextView tv_good, JSONArray jsons,
                           LinearLayout ll_goodmembers_temp, View view, int cSize) {

        // 即时改变当前UI
        for (int i = 0; i < jsons.size(); i++) {
            JSONObject json = jsons.getJSONObject(i);
            if (json.getString("userID").equals(myuserID)) {
                jsons.remove(i);
            }
        }
        setGoodTextClick(tv_good, jsons, ll_goodmembers_temp, view, cSize);

        List<Param> params = new ArrayList<Param>();
        params.add(new Param("sID", sID));
        params.add(new Param("userID", myuserID));
         OkHttpManager.getInstance().post(params, FXConstant.URL_SOCIAL_GOOD_CANCEL, new OkHttpManager.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {

            }

            @Override
            public void onFailure(String errorMsg) {

            }
        });




    }

    private void showDeleteDialog(final String userID, final int postion,
                                  final String scID, final int type, final TextView ctextView,
                                  final JSONArray cjsons, final View view, final int goodSize) {
        final AlertDialog dlg = new AlertDialog.Builder(context).create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.fx_dialog_social_main);
        TextView tv_paizhao = (TextView) window.findViewById(R.id.tv_content1);
        tv_paizhao.setText("复制");
        tv_paizhao.setOnClickListener(new OnClickListener() {
            @SuppressWarnings("deprecation")
            @SuppressLint("SdCardPath")
            public void onClick(View v) {
                ClipboardManager cmb = (ClipboardManager) context
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(cjsons.getJSONObject(postion).getString("content")
                        .trim());

                // cmb.setPrimaryClip(ClipData clip)

                dlg.cancel();
            }
        });
        TextView tv_xiangce = (TextView) window.findViewById(R.id.tv_content2);
        tv_xiangce.setText("删除");
        tv_xiangce.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                deleteComment(userID, postion, scID, type, ctextView, cjsons,
                        view, goodSize);

                dlg.cancel();
            }
        });

    }

    // 删除评论
    private void deleteComment(String userID, final int postion, String scID,
                               int type, TextView ctextView, final JSONArray cjsons, View view,
                               int goodSize) {

        if (scID == null) {
            scID = "LOCAL";
        }
        ;
        String tag = cjsons.getJSONObject(postion).getString("tag");
        if (tag == null) {
            tag = String.valueOf(System.currentTimeMillis());
        }
        // 更新UI
        cjsons.remove(postion);
        setCommentTextClick(ctextView, cjsons, view, goodSize);

        List<Param> params = new ArrayList<Param>();
        params.add(new Param("scID", scID));
        params.add(new Param("userID", myuserID));
        params.add(new Param("tag", tag));
        OkHttpManager.getInstance().post(params, FXConstant.URL_SOCIAL_DELETE_COMMENT, new OkHttpManager.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {

            }

            @Override
            public void onFailure(String errorMsg) {

            }
        });

    }

    private void setUrlTextView(String test_temp, TextView tv_content) {

        String test = test_temp;

        if ((test_temp != null)
                && (test_temp.contains("http://")
                || test_temp.contains("https://") || test_temp
                .contains("www."))) {
            int start = 0;
            while (test != null
                    && !(test.startsWith("http://")
                    || test.startsWith("https://") || test
                    .startsWith("www."))) {

                test = test.substring(1);
                start++;

            }
            int end = 0;

            for (int i = 0; i < test.length(); i++) {
                char item = test.charAt(i);
                if (isChinese(item) || item == ' ') {

                    break;
                }
                end = i;

            }

            String result = (String) test_temp
                    .substring(start, start + end + 1);
            // 可以检验是否有效连接，但是影响效率
            // if(result!=nullcheckURL(result)){
            //
            // }
            if (result != null) {

                SpannableStringBuilder ssb = new SpannableStringBuilder();
                ssb.append(test_temp);

                ssb.setSpan(new ContentURLSpan(result), start, start + end + 1,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                tv_content.setText(ssb);
                tv_content.setMovementMethod(LinkMovementMethod.getInstance());
            }

        } else {
            tv_content.setText(test_temp);
        }

    }

    // 根据Unicode编码完美的判断中文汉字和符号
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    private class ContentURLSpan extends ClickableSpan {
        private String url;

        public ContentURLSpan(String url) {
            this.url = url;

        }

        @Override
        public void updateDrawState(TextPaint ds) {

            ds.setUnderlineText(false); // 去掉下划线
        }

        @Override
        public void onClick(final View widget) {

            if (widget instanceof TextView) {
                ((TextView) widget).setHighlightColor(context.getResources()
                        .getColor(android.R.color.darker_gray));
                new Handler().postDelayed(new Runnable() {

                    public void run() {

                        ((TextView) widget).setHighlightColor(context
                                .getResources().getColor(
                                        android.R.color.transparent));

                    }

                }, 1000);

            }
            context.startActivity(new Intent(context, MyWebViewActivity.class)
                    .putExtra("url", url));

        }

    }

    public static boolean checkURL(String url) {
        boolean value = false;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            int code = conn.getResponseCode();
            System.out.println(">>>>>>>>>>>>>>>> " + code
                    + " <<<<<<<<<<<<<<<<<<");
            if (code != 200) {
                value = false;
            } else {
                value = true;
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return value;
    }

    private void showPhotoDialog(final int index, final String sID) {
        final AlertDialog dlg = new AlertDialog.Builder(context).create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.fx_dialog_social_delete);
        TextView tv_cancel = (TextView) window.findViewById(R.id.tv_cancel);

        tv_cancel.setOnClickListener(new OnClickListener() {
            @SuppressLint("SdCardPath")
            public void onClick(View v) {
                dlg.cancel();
            }
        });
        TextView tv_ok = (TextView) window.findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                users.remove(index);
                notifyDataSetChanged();
                // 更新服务器
                List<Param> params = new ArrayList<Param>();
                params.add(new Param("sID", sID));
                OkHttpManager.getInstance().post(params, FXConstant.URL_SOCIAL_DELETE, new OkHttpManager.HttpCallBack() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                    }

                    @Override
                    public void onFailure(String errorMsg) {

                    }
                });


                dlg.cancel();
            }
        });

    }
}
