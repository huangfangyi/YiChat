package com.htmessage.yichat.acitivity.moments.widget;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.htmessage.update.data.UserManager;
import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.R;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.widget.HTAlertDialog;
import com.htmessage.yichat.widget.StretchTextView;

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
    private StretchTextView tvContent;
    private LinearLayout linearLayout;
    private TextView tvLocation;
    private TextView tvDelete;
    private ImageView ivPop, iv_see;
    private RelativeLayout rl_video_show;
    private SimpleDraweeView sdv_video;
    private TextView tvGood;
    private TextView tvComment;
    private TextView tvTime;
    private PopupWindow mMorePopupWindow;
    private int mShowMorePopupWindowWidth;
    private int mShowMorePopupWindowHeight;
    private TextView tv_open;

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
        tvContent = (StretchTextView) findViewById(R.id.tv_content);
        linearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        tvLocation = (TextView) findViewById(R.id.tv_location);
        tvDelete = (TextView) findViewById(R.id.tv_delete);
        ivPop = (ImageView) findViewById(R.id.iv_pop);
        tvGood = (TextView) findViewById(R.id.tv_good);
        tvComment = (TextView) findViewById(R.id.tv_comment);
        tvTime = (TextView) findViewById(R.id.tv_time);
        rl_video_show = (RelativeLayout) findViewById(R.id.rl_video_show);
        sdv_video = (SimpleDraweeView) findViewById(R.id.sdv_video);
        iv_see = (ImageView) findViewById(R.id.iv_see);
        tv_open = (TextView) findViewById(R.id.tv_open);
    }

    private JSONArray goodArray;
    private JSONArray commentArray;

    public void initView(JSONObject json) {
        final String userId = json.getString("userId");
        String nickName = json.getString("nick");
        String avatar = json.getString("avatar");
        // 点赞评论的数据
        goodArray = json.getJSONArray("praiseList");
        commentArray = json.getJSONArray("commentList");
        final String aId = json.getString("trendId");
        String content = json.getString("content");
        String location = json.getString("location");
        String imageStr = json.getString("imgs");
        String videopath = json.getString("videos");//TODO 暂时屏蔽小视频与权限判断
//        String coverImage = json.getString("coverImage");
//        final String restrict = json.getString("restrict");
//        String userIds = "";
//        if ("2".equals(restrict)){
//            userIds = json.getString("visible");
//        }else if ("3".equals(restrict)){
//            userIds = json.getString("invisible");
//        }
         tvNick.setText(nickName);
        tvContent.setText(content);
//        avatar = checkIsFriendAvatar(userId,avatar);
        UserManager.get().loadUserAvatar(getContext(), avatar, ivAvatar);
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
        tvContent.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showCopyAlert(tvContent.getText().toString().trim());
                return true;
            }
        });
        tvContent.setoPenCLoseListener(new StretchTextView.OnOPenCLoseListener() {
            @Override
            public void Open() {
                tv_open.setText(R.string.Open);
            }

            @Override
            public void close() {
                tv_open.setText(R.string.Close);
            }

            @Override
            public void show() {
                tv_open.setVisibility(VISIBLE);
            }

            @Override
            public void hind() {
                tv_open.setVisibility(GONE);
            }
        });
        tv_open.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tvContent.onClick(tvContent);
            }
        });

        if (!TextUtils.isEmpty(videopath)) {

            linearLayout.setVisibility(GONE);
            if (!TextUtils.isEmpty(videopath)) { //TODO 暂时屏蔽小视频显示
                rl_video_show.setVisibility(VISIBLE);
                initVideoView(videopath);
            }

        } else if (!TextUtils.isEmpty(imageStr)) {
            linearLayout.setVisibility(VISIBLE);
            rl_video_show.setVisibility(GONE);
            Log.d("imageStr---->",imageStr);
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
                }

            });
//            if ("2".equals(restrict) || "3".equals(restrict)){ //TODO 暂时屏蔽权限显示
//                iv_see.setVisibility(View.VISIBLE);
//            }else{
//                iv_see.setVisibility(View.GONE);
//            }
        } else {
            iv_see.setVisibility(View.GONE);
            tvDelete.setVisibility(View.GONE);
        }
//        final String finalUserIds = userIds; //TODO 暂时屏蔽权限显示
//        iv_see.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (onMenuClickListener != null) {
//                    onMenuClickListener.onSeeClick(aId, finalUserIds,restrict);
//                }
//            }
//        });

        // 显示位置
        if (location != null && !location.equals("0")) {
            tvLocation.setVisibility(View.VISIBLE);
            tvLocation.setText(location);
        } else {
            tvLocation.setVisibility(GONE);
        }
        // 显示时间
        tvTime.setText(json.getString("timeDesc"));
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

    private void showCopyAlert(final String content) {
        HTAlertDialog dialog = new HTAlertDialog(getContext(), null, new String[]{getContext().getString(R.string.copy)});
        dialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        CommonUtils.copyText(getContext(), content);
                        CommonUtils.showToastShort(getContext(), getContext().getString(R.string.copy_success));
                        break;
                }
            }
        });
    }

    private void initVideoView(final String videoUrl) {
        Log.d("videoUrl----", videoUrl + HTConstant.baseVideoUrl_set);

        Glide.with(getContext()).load(videoUrl + HTConstant.baseVideoUrl_set).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_image).error(R.drawable.default_image).into(sdv_video);
        rl_video_show.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onMenuClickListener != null) {
                    onMenuClickListener.onVideoClick(videoUrl);
                }
            }
        });
    }

    public interface OnMenuClickListener {
        void onUserClicked(String userId);

        void onGoodIconClicked(String aid);

        void onCommentIconClicked(String aid);

        void onCancelGoodClicked(String gid);

        void onCommentDeleteCilcked(String cid);

        void onDeleted(String aid);

        void onImageListClicked(int index, ArrayList<String> images);

        void onVideoClick(String object);

        void onSeeClick(String aid, String userIds, String type);
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
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_moments_sigle_image, null);
        final ImageView ivSingle = (ImageView) view.findViewById(R.id.iv_single);
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

        Glide.with(getContext()).load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_chat_image).centerCrop()
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
                Glide.with(getContext()).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_chat_image).centerCrop().into(imageView);
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
        if(goodData==null){
            goodData=new JSONArray();
        }
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
            String nick = goodJson.getString("nick");
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

        if (comments==null||comments.size() == 0) {
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
            String nick = json.getString("nick");
            String content = json.getString("content");
            String cid = json.getString("commentId");
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

        imageView.setTag(getContext().getString(R.string.good));
        for (int i = 0; i < goodArray.size(); i++) {
            JSONObject jsonObject = goodArray.getJSONObject(i);
            String userId = jsonObject.getString("userId");

            if (HTApp.getInstance().getUsername().equals(userId)) {
                isCancel = true;
                 break;
            }
        }
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
         conentView.findViewById(R.id.ll_zan).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                mMorePopupWindow.dismiss();
                if (finalIsCancel1 ) {
                    if (onMenuClickListener != null) {
                        onMenuClickListener.onCancelGoodClicked(aid);
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
