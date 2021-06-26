package com.htmessage.yichat.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.promeg.pinyinhelper.Pinyin;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.update.Constant;
import com.htmessage.update.data.GroupInfoManager;
import com.htmessage.update.uitls.AESUtils;
import com.htmessage.update.uitls.ApiUtis;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.IMAction;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.details.UserDetailActivity;
import com.htmessage.yichat.acitivity.friends.invitefriend.ContactInfo;
import com.htmessage.yichat.acitivity.main.WebViewActivity;
import com.htmessage.yichat.acitivity.moments.MomentInputFragment;
import com.htmessage.yichat.widget.TipsAlertDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;


public class CommonUtils {
    private static final String TAG = "CommonUtils";
    private static Toast toast;
    private static Dialog progressDialog;
    private static TipsAlertDialog tipsDialog;//提示框

    /**
     * 打开网址
     *
     * @param context
     * @param url
     */
    public static void openUrl(Context context, String url, String title) {
        if (context == null || TextUtils.isEmpty(url)) {
            return;
        }
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("title", TextUtils.isEmpty(title) ? "网页" : title);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }
    /**
     * 复制文字
     *
     * @param context
     * @param content
     */
    public static void copyText(Context context, String content) {
        ClipboardManager copy = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//        //创建ClipData对象
//        ClipData clipData = ClipData.newPlainText(context.getString(R.string.app_name), content);
//        //添加ClipData对象到剪切板中
//        copy.setPrimaryClip(clipData);
        copy.setText(content);
    }


    /**
     * 加载默认URL图片
     *
     * @param context
     * @param object
     * @param imageView
     */
    public static void loadNumalUrlIcon(Context context, Object object, ImageView imageView) {
        if (context == null) {
            return;
        }
        Glide.with(context).load(object).placeholder(R.drawable.default_image).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);

    }


    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
            }
        }

        return false;
    }


    /**
     * 显示添加留言的弹窗
     *
     * @param
     * @param
     */
    public static void showPayMentMessageInputDialog(final Context context, String message, final OnReChargeDialogClickListener listener) {
        if (context == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = View.inflate(context, R.layout.dialog_pay_ment_money_input, null);
        final EditText edt_recharge = (EditText) dialogView.findViewById(R.id.et_remarks);
        TextView tv_cancle = (TextView) dialogView.findViewById(R.id.tv_cancel);
        TextView tv_ok = (TextView) dialogView.findViewById(R.id.tv_ok);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.show();
        edt_recharge.setText(message);
        edt_recharge.setSelection(edt_recharge.getText().length());
        dialog.setCanceledOnTouchOutside(false);
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_recharge.getText().clear();
                dialog.dismiss();
                listener.onCancleClock();
            }
        });
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rechargeMoney = edt_recharge.getText().toString().trim();
                if (TextUtils.isEmpty(rechargeMoney)) {
                    CommonUtils.showToastShort(context, context.getString(R.string.pay_ment_message_hint));
                    return;
                }
                dialog.dismiss();
                listener.onPriformClock(rechargeMoney);
            }
        });
    }


    public interface OnReChargeDialogClickListener {
        void onPriformClock(String inputMsg);

        void onCancleClock();
    }


    /**
     * 删除一个字符
     *
     * @param editText
     */
    public static void deleteChar(EditText editText) {
        int index = editText.getSelectionStart();
        Editable editable = editText.getText();
        editable.delete(index - 1, index);
    }


    /**
     * check if sdcard exist
     *
     * @return
     */
    public static boolean isSdcardExist() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }


    /**
     * @param bMute 值为true时为关闭背景音乐。
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    public static boolean muteAudioFocus(Context context, boolean bMute) {
        if (context == null) {
            Log.d(TAG, "-----context is null");
            return false;
        }
        boolean bool = false;
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {
            if (bMute) {
                int result = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
            } else {
                int result = am.abandonAudioFocus(null);
                bool = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
            }
        }
        return bool;
    }


    public static boolean check(String fstrData) {
        char c = fstrData.charAt(0);
        if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 短吐司
     *
     * @param context
     * @param msg
     */
    public static void showToastShort(Context context, String msg) {
        if (context == null) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    /**
     * 短吐司
     *
     * @param context
     * @param msg
     */
    public static void showToastShort(Context context, Object msg) {
        if (context == null) {
            return;
        }
        if (msg instanceof String) {
            showToastShort(context, (String) msg);
        } else if (msg instanceof Integer) {
            showToastShort(context, (int) msg);
        }
    }

    /**
     * 短吐司
     *
     * @param context
     * @param msg
     */
    public static void showToastShort(Context context, int msg) {
        if (context == null) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }


    /**
     * 长吐司
     *
     * @param context
     * @param msg
     */
    public static void showToastLong(Context context, int msg) {
        if (context == null) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    /**
     * 弹窗
     *
     * @param context
     * @param title
     * @param content
     * @param listener
     */
    public static void showAlertDialog(Activity context, String title, String content, final OnDialogClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = View.inflate(context, R.layout.layout_alert_dialog_delete, null);
        TextView tv_delete_people = (TextView) dialogView.findViewById(R.id.tv_delete_people);
        View view_line_dialog = dialogView.findViewById(R.id.view_line_dialog);
        TextView tv_delete_title = (TextView) dialogView.findViewById(R.id.tv_delete_title);
        TextView tv_cancle = (TextView) dialogView.findViewById(R.id.tv_cancle);
        TextView tv_ok = (TextView) dialogView.findViewById(R.id.tv_ok);
        tv_delete_title.setText(TextUtils.isEmpty(title) ? context.getString(R.string.prompt) : title);
        tv_delete_people.setText(content);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.show();
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listener.onPriformClock();
            }
        });
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listener.onCancleClock();
            }
        });
    }

    public interface OnDialogClickListener {
        void onPriformClock();

        void onCancleClock();
    }

    /**
     * 弹窗没有取消按钮
     *
     * @param context
     * @param title
     * @param content
     */
    public static void showAlertDialogNoCancle(Context context, String title, String content) {
        if (context == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = View.inflate(context, R.layout.layout_alert_dialog_delete, null);
        TextView tv_delete_people = (TextView) dialogView.findViewById(R.id.tv_delete_people);
        View view_line_dialog = dialogView.findViewById(R.id.view_line_dialog);
        TextView tv_delete_title = (TextView) dialogView.findViewById(R.id.tv_delete_title);
        TextView tv_cancle = (TextView) dialogView.findViewById(R.id.tv_cancle);
        TextView tv_ok = (TextView) dialogView.findViewById(R.id.tv_ok);
        view_line_dialog.setVisibility(View.GONE);
        tv_cancle.setVisibility(View.GONE);
        tv_delete_title.setText(title);
        tv_delete_people.setText(content);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.show();
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 弹窗没有取消按钮
     *
     * @param context
     * @param title
     * @param content
     */
    public static void showAlertDialogNoCancle(Context context, String title, String content, final OnDialogClickListener listener) {
        if (context == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = View.inflate(context, R.layout.layout_alert_dialog_delete, null);
        TextView tv_delete_people = (TextView) dialogView.findViewById(R.id.tv_delete_people);
        View view_line_dialog = dialogView.findViewById(R.id.view_line_dialog);
        TextView tv_delete_title = (TextView) dialogView.findViewById(R.id.tv_delete_title);
        TextView tv_cancle = (TextView) dialogView.findViewById(R.id.tv_cancle);
        TextView tv_ok = (TextView) dialogView.findViewById(R.id.tv_ok);
        view_line_dialog.setVisibility(View.GONE);
        tv_cancle.setVisibility(View.GONE);
        tv_delete_title.setText(title);
        tv_delete_people.setText(content);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.show();
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listener.onPriformClock();
            }
        });
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                listener.onCancleClock();
            }
        });
    }


    public static  void cancelDialogOnDestroy(){
        if(progressDialog!=null&&progressDialog.isShowing()){
            progressDialog.dismiss();
        }


    }

    /**
     * 发起弹窗
     *
     * @param context
     * @param loadText
     */
    public static void showDialogNumal(Context context, Object loadText) {
        if (context == null || loadText == null) {
            return;
        }
        progressDialog = new Dialog(context, R.style.progress_dialog);
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ProgressBar progressBarWaiting = (ProgressBar) progressDialog.findViewById(R.id.iv_loading);
        TextView tv_loading_text = (TextView) progressDialog.findViewById(R.id.tv_loading_text);
        if (loadText instanceof Integer) {
            tv_loading_text.setText(context.getString(((int) loadText)));
        } else if (loadText instanceof String) {
            tv_loading_text.setText((String) loadText);
        }
        if (android.os.Build.VERSION.SDK_INT > 22) {//android 6.0替换clip的加载动画
            final Drawable drawable = context.getDrawable(R.drawable.progress_drawable_white_v23);
            progressBarWaiting.setIndeterminateDrawable(drawable);
        }
        progressDialog.show();
    }

    /**
     * 取消弹窗
     */
    public static void cencelDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    /**
     * 上传聊天记录
     *
     * @param
     * @param htMessage
     */
    public static void upLoadMessage(final HTMessage htMessage) {
        JSONObject data = new JSONObject();
        data.put("referId", htMessage.getUsername());
        data.put("referType", htMessage.getChatType().ordinal() + 1);
        try {
            data.put("content", URLEncoder.encode(htMessage.toXmppMessageBody(), "utf-8"));
        } catch (UnsupportedEncodingException e) {

            data.put("content", URLEncoder.encode(htMessage.toXmppMessageBody()));
            LoggerUtils.d("-----编码错误:" + htMessage.toXmppMessageBody());

        }
        data.put("time", htMessage.getTime());
        data.put("messageId", htMessage.getMsgId());
        ApiUtis.getInstance().postJSON(data, Constant.URL_UPLOAD_MESSAGE, new ApiUtis.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                LoggerUtils.d("-----上传聊天记录jsonObject:" + jsonObject.toJSONString());
            }

            @Override
            public void onFailure(int errorCode) {

            }
        });


    }



    /**
     * 发送广播提示管理员解除全员禁言
     *
     * @param context
     * @param groupId
     * @param object
     */
    public static void sendCancleNoTalkBrocast(Context context, String groupId, JSONObject object) {
        String adminId = object.getString("adminId");
        String adminNick = object.getString("adminNick");
        String groupName = object.getString("groupName");
        if (TextUtils.isEmpty(adminNick)) {
            adminNick = adminId;
        }
        if (TextUtils.isEmpty(groupName)) {
            groupName = groupId;
        }
 //        String content = String.format(context.getString(R.string.manager_is_cancle_no_talk), adminNick, groupName);
        String content = context.getString(R.string.has_cancle_no_talk);
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(IMAction.ACTION_HAS_CANCLED_NO_TALK).putExtra("userId", groupId).putExtra("content", content));
    }




    /**
     * 加载群组默认头像
     *
     * @param context
     * @param object
     * @param imageView
     */
    public static void loadGroupAvatar(Context context, Object object, ImageView imageView) {
        if (context == null||object==null) {
            imageView.setImageResource(R.drawable.default_group);
            return;
        }
        Glide.with(context).load(object).placeholder(R.drawable.default_group).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);

    }


    /**
     * 显示输入的弹窗
     *
     * @param context
     * @param listener
     */
    public static void showInputDialog(final Context context, String title, String hint,String msg, final DialogClickListener listener) {
        if (context == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = View.inflate(context, R.layout.dialog_layout_input_edt, null);
        final TextView tv_title = (TextView) dialogView.findViewById(R.id.tv_title);
        final EditText edt_recharge = (EditText) dialogView.findViewById(R.id.et_remarks);
        TextView tv_cancle = (TextView) dialogView.findViewById(R.id.tv_cancel);
        TextView tv_ok = (TextView) dialogView.findViewById(R.id.tv_ok);
        tv_title.setText(TextUtils.isEmpty(title) ? context.getString(R.string.remark_input) : title);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.show();
        if(hint!=null){
            edt_recharge.setHint(hint);
        }
        if(msg!=null){
            edt_recharge.setText(msg);
        }
        edt_recharge.setSelection(edt_recharge.getText().length());
        dialog.setCanceledOnTouchOutside(false);
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_recharge.getText().clear();
                dialog.dismiss();
                listener.onCancleClock();
            }
        });
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rechargeMoney = edt_recharge.getText().toString().trim();
                if (TextUtils.isEmpty(rechargeMoney)) {
//                    CommonUtils.showToastShort(context, context.getString(R.string.remark_input));
//                    return;
                    rechargeMoney = "";
                }
                dialog.dismiss();
                listener.onPriformClock(rechargeMoney);
            }
        });
    }

    public interface DialogClickListener {
        void onCancleClock();

        void onPriformClock(String msg);
    }



    /**
     * 保存图片到相册
     *
     * @param context
     * @param bmp
     * @return
     */
    public static boolean saveImageToGallery(Context context, Bitmap bmp) {
        if (context == null) {
            return false;
        }
        if (bmp == null) {
            return false;
        }
        String appDir = HTApp.getInstance().getImageDirFilePath();
        String fileName = DateUtils.getyyMMddTime(System.currentTimeMillis()) + ".png";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            return false;
        }
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
        } catch (Exception e) {
            return false;
        }

        //通知图库更新
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//如果是4.4及以上版本
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        } else {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
//        // 最后通知图库更新
//        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
        return true;
    }

    /**
     * 保存图片到相册
     *
     * @param context
     * @param filePath
     * @return
     */
    public static boolean saveImageToAlubm(Context context, String filePath) {
        if (context == null) {
            return false;
        }
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        // 首先保存图片
        String appDir = HTApp.getInstance().getImageDirFilePath();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        String fileName = DateUtils.getyyMMddTime(System.currentTimeMillis()) + ".png";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            return false;
        }
//        // 其次把文件插入到系统图库
//        try {
//            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
//        } catch (Exception e) {
//            return false;
//        }

        //通知图库更新
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//如果是4.4及以上版本
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        } else {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
//        // 最后通知图库更新
//        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
        return true;
    }

    /**
     * 加载图片
     *
     * @param context
     * @param object
     * @param imageView
     */
    public static void loadImage(Context context, Object object, ImageView imageView) {
        if (context == null) {
            return;
        }
        Glide.with(context).load(object).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_image).error(R.drawable.default_image).into(imageView);
    }

    /**
     * 加载图片
     *
     * @param context
     * @param object
     * @param imageView
     */
    public static void loadImageCenterCrop(Context context, Object object, ImageView imageView) {
        if (context == null) {
            return;
        }
        Glide.with(context).load(object).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().placeholder(R.drawable.default_image).error(R.drawable.default_image).into(imageView);
    }






    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static boolean copyFile(Context context, String oldPath, String newPath) {
        if (context == null || TextUtils.isEmpty(newPath) || TextUtils.isEmpty(oldPath)) {
            return false;
        }
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[2048];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        //通知图库更新
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//如果是4.4及以上版本
            Uri uri = Uri.fromFile(new File(newPath));
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
        } else {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
        return true;
    }

    /**
     * 显示默认的tipsAlert
     *
     * @param context
     * @param title
     * @param content
     * @param listener
     */
    public static void showTipsAlert(Context context, String title, String content, final OnDialogClickListener listener) {
        if (context == null) {
            return;
        }
        tipsDialog = new TipsAlertDialog(context);
        tipsDialog.setTipsTitle(title);
        tipsDialog.setTipsContent(content);
        tipsDialog.setOnTipsAlertClickListener(new TipsAlertDialog.OnTipsAlertClickListener() {
            @Override
            public void onTipsPromitClick() {
                listener.onPriformClock();
            }

            @Override
            public void onTipsCancleClick() {
                listener.onCancleClock();
            }
        });
        tipsDialog.showTips();
    }

    /**
     * 显示没有取消按钮的tipsAlert
     *
     * @param context
     * @param title
     * @param content
     * @param listener
     */
    public static void showTipsAlertNoCancleButton(Context context, String title, String content, final OnDialogClickListener listener) {
        if (context == null) {
            return;
        }
        tipsDialog = new TipsAlertDialog(context);
        tipsDialog.setTipsTitle(title);
        tipsDialog.setTipsContent(content);
        tipsDialog.setNoCancleButton(View.GONE);
        tipsDialog.setOnTipsAlertClickListener(new TipsAlertDialog.OnTipsAlertClickListener() {
            @Override
            public void onTipsPromitClick() {
                listener.onPriformClock();
            }

            @Override
            public void onTipsCancleClick() {
                listener.onCancleClock();
            }
        });
        tipsDialog.showTips();
    }

    /**
     * 显示没有确定按钮的tipsAlert
     *
     * @param context
     * @param title
     * @param content
     * @param listener
     */
    public static void showTipsAlertNoOkButton(Context context, String title, String content, final OnDialogClickListener listener) {
        if (context == null) {
            return;
        }
        tipsDialog = new TipsAlertDialog(context);
        tipsDialog.setTipsTitle(title);
        tipsDialog.setTipsContent(content);
        tipsDialog.setNoOkButton(View.GONE);
        tipsDialog.setOnTipsAlertClickListener(new TipsAlertDialog.OnTipsAlertClickListener() {
            @Override
            public void onTipsPromitClick() {
                listener.onPriformClock();
            }

            @Override
            public void onTipsCancleClick() {
                listener.onCancleClock();
            }
        });
        tipsDialog.showTips();
    }

    /**
     * 显示包含有图片及内容,确定及取消按钮的tipsAlert
     *
     * @param context
     * @param title
     * @param content
     * @param listener
     */
    public static void showTipsAlertHasContentImageView(Context context, String title, String content, Object imagePath, final OnDialogClickListener listener) {
        if (context == null) {
            return;
        }
        tipsDialog = new TipsAlertDialog(context);
        tipsDialog.setTipsTitle(title);
        tipsDialog.setTipsContent(content);
        if (imagePath != null) {
            tipsDialog.setImageViewVisiblity(View.VISIBLE);
            tipsDialog.setImagePath(imagePath);
        } else {
            tipsDialog.setImageViewVisiblity(View.GONE);
        }
        tipsDialog.setOnTipsAlertClickListener(new TipsAlertDialog.OnTipsAlertClickListener() {
            @Override
            public void onTipsPromitClick() {
                listener.onPriformClock();
            }

            @Override
            public void onTipsCancleClick() {
                listener.onCancleClock();
            }
        });
        tipsDialog.showTips();
    }

    /**
     * 显示消息转发或者复制的tipsAlert
     *
     * @param context
     * @param title
     * @param content
     * @param listener
     */
    public static void showMessageCopyForwordTipsAlert(Context context, Object title, Object content, final OnDialogClickListener listener) {
        if (context == null) {
            return;
        }
        tipsDialog = new TipsAlertDialog(context);
        tipsDialog.setTipsTitle(title);
        tipsDialog.setTipsContent(content);
        tipsDialog.setOnTipsAlertClickListener(new TipsAlertDialog.OnTipsAlertClickListener() {
            @Override
            public void onTipsPromitClick() {
                listener.onPriformClock();
            }

            @Override
            public void onTipsCancleClick() {
                listener.onCancleClock();
            }
        });
        tipsDialog.showTips();
    }





    @TargetApi(Build.VERSION_CODES.M)
    public static boolean checkPermission(Activity context, String permissionName) {
        PackageManager pm = context.getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission(permissionName, context.getPackageName()));
        if (permission) {
            return true;
        } else {
            return false;
        }
    }








    /**
     * array2jsonList
     *
     * @param jsonArray
     * @param objectList
     * @return
     */
    public static void arrayToJsonList(JSONArray jsonArray, List<JSONObject> objectList) {
        if (jsonArray == null && jsonArray.size() == 0) {
            return;
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            if (!objectList.contains(object)) {
                objectList.add(object);
            }
        }

        Collections.sort(objectList, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                long time1 = o1.getLong("getTime");
                long time2 = o2.getLong("getTime");
                if (time1 > time2) {
                    return -1;
                } else if ((time1 < time2)) {
                    return 1;
                }


                return 0;
            }
        });

    }






    public static void handleQRcodeResult(final Activity activity, String result) {

        try {
            String resString = new AESUtils("A286D372M63HFUQW").decryptData(result);
            JSONObject jsonObject = JSONObject.parseObject(resString);
            int type = jsonObject.getInteger("type");
            if (type == 2) {
                String userId = jsonObject.getJSONObject("data").getString("userId");
                JSONObject data = new JSONObject();
                data.put("userId", userId);
                ApiUtis.getInstance().postJSON(data, Constant.URL_USER_INFO, new ApiUtis.HttpCallBack() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        String code = jsonObject.getString("code");
                        if ("0".equals(code)) {
                            JSONObject data = jsonObject.getJSONObject("data");
                            if (activity != null && !activity.isDestroyed()) {
                                activity.startActivity(new Intent(activity, UserDetailActivity.class).putExtra("data", data.toJSONString()));
                            }
                        } else if ("116".equals(code)) {
                            if (activity != null && !activity.isDestroyed()) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activity, R.string.user_not_exit, Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        } else {
                            if (activity != null && !activity.isDestroyed()) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(activity, R.string.api_error_5, Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailure(final int errorCode) {
                        if (activity != null && !activity.isDestroyed()) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, errorCode, Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                });

            }

        } catch (Exception e) {
            if (result.startsWith("http:")) {
                activity.startActivity(new Intent(activity, WebViewActivity.class).putExtra("url", result));
            } else {
                Toast.makeText(activity, R.string.not_support_qr, Toast.LENGTH_SHORT).show();
            }
            e.printStackTrace();
        }

    }

    /**
     * 显示输入的弹窗
     *
     * @param fragmentManager
     * @param listener
     */
    public static void showMomentBottomInputEditFragment(FragmentManager fragmentManager, final DialogClickListener listener) {
        if (fragmentManager == null) {
            return;
        }

        MomentInputFragment keyboardDialogFragment = new MomentInputFragment();
        keyboardDialogFragment.show(fragmentManager, "MomentInputFragment");
        keyboardDialogFragment.setEdittextListener(new MomentInputFragment.EdittextListener() {
            @Override
            public void setTextStr(String text) {
                listener.onPriformClock(text);
            }

            @Override
            public void dismiss(DialogFragment dialogFragment) {
                listener.onCancleClock();
            }
        });
    }


    /**
     * set initial letter of according user's nickname( username if no nickname)
     *
     * @param
     * @param user
     */
    public static void setContactsInfoInitialLetter(ContactInfo user) {
        final String DefaultLetter = "#";
        String letter = DefaultLetter;
        if (!TextUtils.isEmpty(user.getName())) {
            letter = Pinyin.toPinyin(user.getName().toCharArray()[0]);
            user.setLetter(letter.toUpperCase().substring(0, 1));
            if (isNumeric(user.getLetter()) || !check(user.getLetter())) {
                user.setLetter("#");
            }
            return;
        }
        if (letter == DefaultLetter && !TextUtils.isEmpty(user.getName())) {
            letter = Pinyin.toPinyin(user.getName().toCharArray()[0]);
        }
        user.setLetter(letter.substring(0, 1));
        if (isNumeric(user.getLetter()) || !check(user.getLetter())) {
            user.setLetter("#");
        }
    }


}
