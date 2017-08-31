package com.htmessage.fanxinht.acitivity.moments.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by huangfangyi on 2017/7/11.
 * qq 84543217
 */

public class MomentsItemView extends LinearLayout {
    private ImageView ivAvatar;
    private TextView tvNick;
    private TextView tvContent;
    private LinearLayout linearLayout;
    private TextView tvLocation;
    private TextView tvDelete;
    private ImageView ivPop;
    private TextView tvGood;
    private TextView tvComment;
    private TextView tvTime;
    private PopupWindow mMorePopupWindow;
    private int mShowMorePopupWindowWidth;
    private int mShowMorePopupWindowHeight;

    public MomentsItemView(Context context) {
        super(context);
        init(context);
    }

    public MomentsItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MomentsItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {

        LayoutInflater.from(context).inflate(R.layout.layout_moments_item, this);
        ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
        tvNick = (TextView) findViewById(R.id.tv_nick);
        tvContent = (TextView) findViewById(R.id.tv_content);
        linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        tvLocation = (TextView) findViewById(R.id.tv_location);
        tvDelete = (TextView) findViewById(R.id.tv_delete);
        ivPop = (ImageView) findViewById(R.id.iv_pop);
        tvGood = (TextView) findViewById(R.id.tv_good);
        tvComment = (TextView) findViewById(R.id.tv_comment);
        tvTime = (TextView) findViewById(R.id.tv_time);

    }

    private JSONArray goodArray;
    private JSONArray commentArray;

    public void initView(JSONObject json, String serverTime) {
        final String userId = json.getString("userId");
        String nickName = json.getString("usernick");
        String avatar = json.getString("avatar");
        // 点赞评论的数据
        goodArray = json.getJSONArray("praises");
        commentArray = json.getJSONArray("comment");
        final String aId = json.getString("id");
        String publishTime = json.getString("time");
        String content = json.getString("content");
        String location = json.getString("location");
        String imageStr = json.getString("imagestr");

        tvNick.setText(nickName);
        tvContent.setText(content);
        Glide.with(getContext()).load(avatar).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_avatar).into(ivAvatar);
        tvNick.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMenuClickListener != null) {
                    onMenuClickListener.onUserClicked(userId);
                }
            }
        });
        ivAvatar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMenuClickListener != null) {
                    onMenuClickListener.onUserClicked(userId);
                }
            }
        });

        if (!TextUtils.isEmpty(imageStr)) {
            linearLayout.setVisibility(VISIBLE);
            String[] images = imageStr.split(",");
            linearLayout.removeAllViews();
            initImgaesView(new ArrayList<String>(Arrays.asList(images)), linearLayout);

        } else {
            linearLayout.setVisibility(GONE);
        }
        // 设置删除键
        if (HTApp.getInstance().getUsername().equals(userId)) {
            tvDelete.setVisibility(View.VISIBLE);
            tvDelete.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (onMenuClickListener != null) {
                        onMenuClickListener.onDeleted(aId);
                    }
                    //  showPhotoDialog(position - 1, sID);
                }

            });
        } else {
            tvDelete.setVisibility(View.GONE);
        }

        // 显示位置
        if (location != null && !location.equals("0")) {
            tvLocation.setVisibility(View.VISIBLE);
            tvLocation.setText(location);
        } else {
            tvLocation.setVisibility(GONE);
        }
        // 显示时间
        tvTime.setText(CommonUtils.getDuration(getContext(), publishTime, serverTime));
        //显示点赞
        initGoodView(tvGood, goodArray);
        //设置评论
        initCommentView(tvComment, commentArray);
        //设置右下角弹出按钮
        ininPop(ivPop, aId);
    }

    private OnMenuClickListener onMenuClickListener;

    public void setOnMenuClickListener(OnMenuClickListener onMenuClickListener) {
        this.onMenuClickListener = onMenuClickListener;
    }


    public interface OnMenuClickListener {
        void onUserClicked(String userId);

        void onGoodIconClicked(String aid);

        void onCommentIconClicked(String aid);

        void onCancelGoodClicked(String gid);

        void onCommentDeleteCilcked(String cid);

        void onDeleted(String aid);

        void onImageListClicked(int index, ArrayList<String> images);


    }

    public void updateGoodView(JSONArray data) {
        goodArray.clear();
        goodArray.addAll(data);
        initGoodView(tvGood, goodArray);
    }

    public void updateCommentView(JSONArray data) {
        commentArray.clear();
        commentArray.addAll(data);
        initCommentView(tvComment, data);
    }

    public void initImgaesView(ArrayList<String> images, LinearLayout linearLayout) {
        switch (images.size()) {
            case 1:
                initSingle(images.get(0), linearLayout);
                break;
            default:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    initFour(images, linearLayout);
                }
                break;
        }

    }

    private void initSingle(final String url, LinearLayout linearLayout) {
        View view =LayoutInflater.from(getContext()).inflate(R.layout.item_moments_sigle_image, null);
        final ImageView ivSingle= (ImageView) view.findViewById(R.id.iv_single);
       // ImageView imageView = new ImageView(getContext());
    //    imageView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ivSingle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMenuClickListener != null) {
                    List<String> images = new ArrayList<String>();
                    images.add(url);
                    onMenuClickListener.onImageListClicked(0, (ArrayList<String>) images);
                }
            }
        });

         Glide.with(getContext()).load(HTConstant.baseImgUrl + url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_image2).centerCrop()
                 .into(ivSingle);
        linearLayout.addView(view);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initFour(final ArrayList<String> images, LinearLayout mainLinearLayout) {
        int numColumns = 3;
        if (images.size() == 4) {
            numColumns = 2;
        }
        int lines = images.size() % numColumns == 0 ? images.size() / numColumns : (images.size() / numColumns) + 1;
        for (int i = 0; i < lines; i++) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setWeightSum(3);
            for (int n = i * numColumns; n < (numColumns * (i + 1)); n++) {
                if (n >= images.size()) {
                    break;
                }
                SquareImageView imageView = new SquareImageView(getContext());
                imageView.setPadding(0, 20, 20, 0);
                String url = images.get(n);
                linearLayout.addView(imageView, new LayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, (float) 1)));
                Glide.with(getContext()).load(HTConstant.baseImgUrl + url).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_image2).centerCrop().into(imageView);
                final int finalN = n;
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onMenuClickListener != null) {
                            onMenuClickListener.onImageListClicked(finalN, images);
                        }
                    }
                });
            }
            mainLinearLayout.addView(linearLayout);
        }
    }

    private void initGoodView(TextView textView, JSONArray goodData) {
        if (goodData.size() == 0) {
            textView.setVisibility(GONE);
            return;
        } else {
            textView.setVisibility(VISIBLE);
        }
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        int start = 0;
        for (int i = 0; i < goodData.size(); i++) {
            JSONObject goodJson = goodData.getJSONObject(i);
            String userId = goodJson.getString("userId");
            String nick = goodJson.getString("nickname");
            if (i != (goodData.size() - 1) && goodData.size() > 1) {
                ssb.append(nick + ",");
            } else {
                ssb.append(nick);
            }
            ssb.setSpan(new TextClickableSpan(userId, 0), start,
                    start + nick.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            start = ssb.length();
        }
        textView.setText(ssb);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private class TextClickableSpan extends ClickableSpan {
        private String userId;
        private String cid;
        private int type;//0--名字,1--整行评论

        public TextClickableSpan(String id, int type) {
            this.type = type;
            switch (type) {
                case 0:
                    userId = id;
                    break;
                case 1:
                    cid = id;
                    break;
            }

        }

        @Override
        public void updateDrawState(TextPaint ds) {
            if (type == 0) {
                ds.setColor(getResources().getColor(R.color.text_color));
                ds.setUnderlineText(false); // 去掉下划线
            }

        }

        @Override
        public void onClick(final View widget) {
            if (widget instanceof TextView) {
                ((TextView) widget).setHighlightColor(getResources().getColor(android.R.color.darker_gray));
                new Handler().postDelayed(new Runnable() {

                    public void run() {
                        ((TextView) widget).setHighlightColor(getResources().getColor(android.R.color.transparent));
                    }
                }, 500);
            }

            if (type == 0) {
                if (onMenuClickListener != null) {
                    onMenuClickListener.onUserClicked(userId);
                }

            } else if (type == 1) {
                if (onMenuClickListener != null) {
                    onMenuClickListener.onCommentDeleteCilcked(cid);
                }
            }


        }
    }

    private void initCommentView(TextView textView, JSONArray comments) {

        if (comments.size() == 0) {
            textView.setVisibility(View.GONE);
            return;
        } else {
            textView.setVisibility(View.VISIBLE);
        }
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        int start = 0;

        for (int i = 0; i < comments.size(); i++) {

            JSONObject json = comments.getJSONObject(i);
            String userId = json.getString("userId");
            String nick = json.getString("nickname");
            String content = json.getString("content");
            String cid = json.getString("cid");
            String content_0 = "";
            String content_1 = ": " + content;
            String content_2 = ": " + content + "\n";
            if (i == (comments.size() - 1) || (comments.size() == 1 && i == 0)) {
                ssb.append(nick + content_1);
                content_0 = content_1;
            } else {
                ssb.append(nick + content_2);
                content_0 = content_2;
            }

            ssb.setSpan(new TextClickableSpan(userId, 0), start,
                    start + nick.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (userId.equals(HTApp.getInstance().getUsername())) {

                ssb.setSpan(
                        new TextClickableSpan(cid, 1),
                        start, start + nick.length() + content_0.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            start = ssb.length();

        }

        textView.setText(ssb);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }


    /**
     * 弹出点赞和评论框
     */
    private void ininPop(final ImageView imageView, final String aid) {

        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showMorePop(imageView, aid);
            }
        });


    }

    private TextView tvPopGood;
    private View conentView;

    private void showMorePop(ImageView imageView, final String aid) {

//判断是显示取消还是取消点赞
        boolean isCancel = false;
        String pid = null;

        imageView.setTag(getContext().getString(R.string.good));
        for (int i = 0; i < goodArray.size(); i++) {
            JSONObject jsonObject = goodArray.getJSONObject(i);
            String userId = jsonObject.getString("userId");

            if (HTApp.getInstance().getUsername().equals(userId)) {
                isCancel = true;
                pid = jsonObject.getString("pid");
                break;
            }
        }
        final String finalPid = pid;
        final boolean finalIsCancel = isCancel;
        if (mMorePopupWindow == null) {

            mMorePopupWindow = new PopupWindow(getContext());

            conentView = LayoutInflater.from(getContext()).inflate(R.layout.popwindow_moments, null);

            // 设置SelectPicPopupWindow的View
            mMorePopupWindow.setContentView(conentView);
            // 设置SelectPicPopupWindow弹出窗体的宽
            mMorePopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            // 设置SelectPicPopupWindow弹出窗体的高
            mMorePopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            // 设置SelectPicPopupWindow弹出窗体可点击
            mMorePopupWindow.setFocusable(true);
            mMorePopupWindow.setOutsideTouchable(true);
            // 刷新状态
            mMorePopupWindow.update();
            // 实例化一个ColorDrawable颜色为半透明
            ColorDrawable dw = new ColorDrawable(0000000000);
            // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
            mMorePopupWindow.setBackgroundDrawable(dw);

            // 设置SelectPicPopupWindow弹出窗体动画效果
            mMorePopupWindow.setAnimationStyle(R.style.AnimationPreview);

            conentView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            mShowMorePopupWindowWidth = conentView.getMeasuredWidth();
            mShowMorePopupWindowHeight = conentView.getMeasuredHeight();
            tvPopGood = (TextView) conentView.findViewById(R.id.tv_good);


            conentView.findViewById(R.id.ll_pl).setOnClickListener(new OnClickListener() {
                // 扫一扫 ，调出扫二维码 gongfan
                @Override
                public void onClick(View v) {
                    if (onMenuClickListener != null) {
                        onMenuClickListener.onCommentIconClicked(aid);
                    }
                    mMorePopupWindow.dismiss();
                }

            });
        }
        final boolean finalIsCancel1 = isCancel;
        final String finalPid1 = pid;
        conentView.findViewById(R.id.ll_zan).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                mMorePopupWindow.dismiss();
                if (finalIsCancel1 && finalPid1 != null) {
                    if (onMenuClickListener != null) {
                        onMenuClickListener.onCancelGoodClicked(finalPid1);
                    }
                } else {
                    if (onMenuClickListener != null) {
                        onMenuClickListener.onGoodIconClicked(aid);
                    }
                }

            }

        });


        if (mMorePopupWindow.isShowing()) {
            mMorePopupWindow.dismiss();
        } else {
            if (isCancel) {
                tvPopGood.setText(getContext().getString(R.string.cancel));
            } else {
                tvPopGood.setText(getContext().getString(R.string.good));
            }
            int heightMoreBtnView = imageView.getHeight();
            mMorePopupWindow.showAsDropDown(imageView, -mShowMorePopupWindowWidth,
                    -(mShowMorePopupWindowHeight + heightMoreBtnView) / 2);
        }
    }


}
