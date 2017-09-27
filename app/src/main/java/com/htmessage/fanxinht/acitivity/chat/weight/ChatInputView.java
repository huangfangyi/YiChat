package com.htmessage.fanxinht.acitivity.chat.weight;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.chat.weight.emojicon.EmojiFragment;
import com.htmessage.fanxinht.acitivity.chat.weight.emojicon.Emojicon;
import com.htmessage.fanxinht.acitivity.chat.weight.emojicon.EmojiconDatas;
import com.htmessage.fanxinht.acitivity.chat.weight.emojicon.SmileUtils;
import com.htmessage.fanxinht.manager.PreferenceManager;
import com.htmessage.fanxinht.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by huangfangyi on 2017/7/4.
 * qq 84543217
 */

public class ChatInputView extends LinearLayout implements View.OnClickListener {
    private static final String SHARE_PREFERENCE_NAME = "com.yichatsystem.app";
    private static final String SHARE_PREFERENCE_TAG = "soft_input_height";
    //切换到语音输入按钮
    private Button btn_set_mode_voice;
    //文本输入框
    private EditText et_sendmessage;
    //文本输入框的linearlayou
    private LinearLayout ll_press_to_input;
    //语音输入的linearlayout
    private LinearLayout ll_press_to_speak;
    //切换到文字输入的按钮
    private Button btn_set_mode_keyboard;
    //按住录音的按钮
    private Button tv_recording;
    //表情按钮正常状态
    private Button btn_emoticons_normal;
    //右边切换到输入状态的按钮
    private Button btn_emoticons_checked;
    //选择更多的消息类型
    private Button btn_more;
    //消息发送和按钮
    private Button btn_send;
    //表情及更多消息类型的父View
    private LinearLayout ll_more;
    //表情区域
    private TabLayout tl_face_container;
    private ViewPager emoji_viewpager;
    //更多消息类型
    private TabLayout tablelayout_extend;
    private ViewPager extend_viewpager;
    //   private EmotionInputDetector emotionInputDetector;
    //输入框上面的view
    private View mContentView;
    private InputMethodManager mInputManager;
    private LinearLayout ll_emoji;
    private LinearLayout ll_extend;
    private InputViewLisenter inputViewLisenter;

    public ChatInputView(Context context) {
        super(context);
        init(context);
    }

    public ChatInputView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChatInputView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void showEmotionLayout() {
        int softInputHeight = getSupportSoftInputHeight();
        if (softInputHeight == 0) {
            softInputHeight = PreferenceManager.getInstance().getSoftKeybardHeight();
        }
        hideSoftInput();
        ll_more.getLayoutParams().height = softInputHeight;
        ll_more.setVisibility(View.VISIBLE);
        //  inputViewLisenter.onEditTextUp();
    }

    public void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.widget_input_view, this);

    }

    int[] itemNames ;
    int[] itemIcons ;
    public void initView(Activity activity, View aboveView,int[] itemNames,int[] itemIcons) {
        this.itemNames=itemNames;
        this.itemIcons=itemIcons;


        mContentView = aboveView;
        //切换到语音输入按钮
        btn_set_mode_voice = (Button) this.findViewById(R.id.btn_set_mode_voice);

        //文本输入框
        et_sendmessage = (EditText) this.findViewById(R.id.et_sendmessage);
        //文本输入框的linearlayou

        ll_press_to_input = (LinearLayout) this.findViewById(R.id.ll_press_to_input);

        //语音输入的linearlayout
        ll_press_to_speak = (LinearLayout) this.findViewById(R.id.ll_press_to_speak);

        //切换到文字输入的按钮(左边)
        btn_set_mode_keyboard = (Button) this.findViewById(R.id.btn_set_mode_keyboard);

        //按住录音的按钮
        tv_recording = (Button) this.findViewById(R.id.tv_recording);

        //表情按钮正常状态
        btn_emoticons_normal = (Button) this.findViewById(R.id.btn_emoticons_normal);

        //右边切换到输入状态的按钮

        btn_emoticons_checked = (Button) this.findViewById(R.id.btn_emoticons_checked);

        //选择更多的消息类型

        btn_more = (Button) this.findViewById(R.id.btn_more);

        //消息发送和按钮

        btn_send = (Button) this.findViewById(R.id.btn_send);

        //表情及更多消息类型的父View
        ll_more = (LinearLayout) this.findViewById(R.id.ll_more);

        //表情区域
        ll_emoji = (LinearLayout) this.findViewById(R.id.ll_emoji);
        tl_face_container = (TabLayout) this.findViewById(R.id.tl_face_container);

        emoji_viewpager = (ViewPager) this.findViewById(R.id.emoji_viewpager);

        //更多消息类型
        ll_extend = (LinearLayout) this.findViewById(R.id.ll_extend);
        tablelayout_extend = (TabLayout) this.findViewById(R.id.tablelayout_extend);

        extend_viewpager = (ViewPager) this.findViewById(R.id.extend_viewpager);

        //初始化表情区域
        initEmojiView(activity);
        //初始化更多消息
        ininExtendView();
        //设置按钮监听
        setListener();
        mInputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        hideSoftInput();
    }

    public void setInputViewLisenter(InputViewLisenter inputViewLisenter) {
        this.inputViewLisenter = inputViewLisenter;
    }
//    public void setAboveView(Activity activity,View upView){
//        emotionInputDetector=  EmotionInputDetector.with((AppCompatActivity)activity)
//                .setEmotionView(ll_more)
//                .bindToContent(upView)
//                .bindToEditText(et_sendmessage)
//                .bindToEmotionButton(btn_emoticons_normal)
//                .build();
//    }


    public boolean interceptBackPress() {
        // TODO: 15/11/2 change this method's name
        if (ll_more.isShown()) {
            hideEmotionLayout(false);
            if (!btn_emoticons_normal.isShown()) {
                btn_emoticons_checked.setVisibility(GONE);
                btn_emoticons_normal.setVisibility(VISIBLE);
            }
            return true;
        }
        return false;
    }


    private void setListener() {
        btn_set_mode_voice.setOnClickListener(this);
        et_sendmessage.setOnClickListener(this);
        btn_set_mode_keyboard.setOnClickListener(this);
        btn_emoticons_normal.setOnClickListener(this);
        btn_emoticons_checked.setOnClickListener(this);
        btn_more.setOnClickListener(this);
        btn_send.setOnClickListener(this);

        et_sendmessage.requestFocus();
        et_sendmessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (!btn_emoticons_normal.isShown()) {
                        btn_emoticons_checked.setVisibility(GONE);
                        btn_emoticons_normal.setVisibility(VISIBLE);
                    }
                    if (ll_more.isShown()) {

                        lockContentHeight();
                        hideEmotionLayout(true);
                        et_sendmessage.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                unlockContentHeightDelayed();
                            }
                        }, 200L);
                        inputViewLisenter.onEditTextUp();
                        return false;
                    }

                    if (!isSoftInputShown()) {
                        inputViewLisenter.onEditTextUp();
                    }


                }
                return false;
            }
        });
//        et_sendmessage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//               // et_sendmessage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//               float imagePositionX  = et_sendmessage.getX();
//                float imagePositionY  = et_sendmessage.getY();
//                float imageWidth      = et_sendmessage.getWidth();
//                float imageHeight     = et_sendmessage.getHeight();
//                //设置文本大小
//               // tvInImage.setMaxWidth((int) imageWidth);
//                Log.d("et_sendmessage--->",imagePositionX+"-"+imagePositionY+"-"+imageWidth+"-"+imageHeight);
//            }
//        });
//
//


        et_sendmessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!TextUtils.isEmpty(s.toString())) {
                    btn_more.setVisibility(GONE);
                    btn_send.setVisibility(VISIBLE);

                } else {
                    btn_send.setVisibility(GONE);
                    btn_more.setVisibility(VISIBLE);
                }
                Log.d("onEditTextClicked1", s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    btn_more.setVisibility(GONE);
                    btn_send.setVisibility(VISIBLE);

                } else {
                    btn_send.setVisibility(GONE);
                    btn_more.setVisibility(VISIBLE);
                }
                Log.d("onEditTextClicked", s.toString());
            }
        });
//     //   tv_recording.setClickable(true);
//        View ll_te=this.findViewById(R.id.ll_speak);
//        ll_te.setClickable(true);
//        ll_te.setLongClickable(true);
        tv_recording.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("voiceFilePath--->", "onTouch");
                if (inputViewLisenter != null) {
                    Log.d("voiceFilePath--->", "inputViewLisenter");
                    inputViewLisenter.onPressToSpeakBtnTouch(v, event);
                }

                return false;
            }
        });
        et_sendmessage.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (inputViewLisenter != null) {
                    return inputViewLisenter.onEditTextLongClick();
                }
                return false;
            }
        });

    }

    public void hideSoftInput() {
        mInputManager.hideSoftInputFromWindow(et_sendmessage.getWindowToken(), 0);
    }


    private void hideEmotionLayout(boolean showSoftInput) {
        if (ll_more.isShown()) {
            ll_more.setVisibility(View.GONE);
            if (showSoftInput) {
                showSoftInput();
            }
        }
    }

    private void unlockContentHeightDelayed() {
        et_sendmessage.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout.LayoutParams) mContentView.getLayoutParams()).weight = 1.0F;
            }
        }, 200L);
    }

    private void showSoftInput() {
        et_sendmessage.requestFocus();
        et_sendmessage.post(new Runnable() {
            @Override
            public void run() {
                mInputManager.showSoftInput(et_sendmessage, 0);
                //  inputViewLisenter.onEditTextUp();
            }
        });
    }

    private void lockContentHeight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        params.height = mContentView.getHeight();
        params.weight = 0.0F;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_set_mode_voice:
                showVoiceRecordingView();
                inputViewLisenter.onEditTextUp();
                btn_emoticons_checked.setVisibility(GONE);
                btn_send.setVisibility(GONE);
                btn_more.setVisibility(VISIBLE);
                btn_emoticons_normal.setVisibility(VISIBLE);
                break;
//            case R.id.et_sendmessage:
//
//                break;
            case R.id.btn_set_mode_keyboard:
                showInputView();
                inputViewLisenter.onEditTextUp();
                if (!TextUtils.isEmpty(et_sendmessage.getText().toString())) {
                    btn_more.setVisibility(GONE);
                    btn_send.setVisibility(VISIBLE);
                }
                break;
            case R.id.btn_emoticons_normal:
                showInputView();
                inputViewLisenter.onEditTextUp();
                btn_emoticons_normal.setVisibility(GONE);
                btn_emoticons_checked.setVisibility(VISIBLE);
                if (isSoftInputShown()) {
                    lockContentHeight();
                    showEmotionLayout();

                    unlockContentHeightDelayed();

                } else {
                    setEmojiMode();
                    showEmotionLayout();
                }

                break;
            case R.id.btn_emoticons_checked:
                showInputView();
                inputViewLisenter.onEditTextUp();
                et_sendmessage.requestFocus();
                btn_emoticons_checked.setVisibility(GONE);
                btn_emoticons_normal.setVisibility(VISIBLE);

                if (ll_extend.isShown()) {

                    setEmojiMode();

                } else {
                    lockContentHeight();
                    hideEmotionLayout(true);
                    unlockContentHeightDelayed();
                }


                break;
            case R.id.btn_more:
                btn_emoticons_normal.setVisibility(VISIBLE);
                btn_emoticons_checked.setVisibility(GONE);
                showInputView();
                inputViewLisenter.onEditTextUp();
                if (ll_more.isShown()) {
                    if (ll_emoji.isShown()) {
                        setExtendMode();

                    } else {
                        lockContentHeight();
                        hideEmotionLayout(true);
                        unlockContentHeightDelayed();
                    }

                } else {

                    if (isSoftInputShown()) {
                        lockContentHeight();
                        showEmotionLayout();
                        unlockContentHeightDelayed();
                    } else {
                        setExtendMode();
                        showEmotionLayout();
                    }
                }
                break;
            case R.id.btn_send:
                String content = et_sendmessage.getText().toString();
                et_sendmessage.setText("");
                inputViewLisenter.onSendButtonClicked(content);
                break;

        }

    }


    private boolean isSoftInputShown() {
        return getSupportSoftInputHeight() != 0;
    }

    private void showVoiceRecordingView() {
        ll_press_to_input.setVisibility(GONE);
        ll_press_to_speak.setVisibility(VISIBLE);
        interceptBackPress();
    }

    private void setEmojiMode() {
        ll_extend.setVisibility(GONE);
        ll_emoji.setVisibility(VISIBLE);
    }

    private void setExtendMode() {

        ll_emoji.setVisibility(GONE);
        ll_extend.setVisibility(VISIBLE);


    }


    private void showInputView() {
        ll_press_to_speak.setVisibility(GONE);
        ll_press_to_input.setVisibility(VISIBLE);


    }

    private void initEmojiView(Context context) {
        ll_emoji.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE | LinearLayout.SHOW_DIVIDER_BEGINNING);
        ll_emoji.setDividerDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider_horizontal));
        final List<Fragment> fragmentList = new ArrayList<>();

        fragmentList.add(new EmojiFragment());
        emoji_viewpager.setAdapter(new FragmentPagerAdapter(((AppCompatActivity) context).getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Override
            public Fragment getItem(int position) {
                EmojiFragment emojiFragment = (EmojiFragment) fragmentList.get(position);
                emojiFragment.init(Arrays.asList(EmojiconDatas.getData()), 7, 3, new EmojiFragment.OnEmojiListener() {
                    @Override
                    public void onDeleteImageClicked() {
                        editTextDelete(et_sendmessage);
                    }

                    @Override
                    public void onExpressionClicked(Emojicon emojicon) {
                        editTextAddEmoji(emojicon);
                    }
                });
                return emojiFragment;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {

            }
        });
        tl_face_container.setupWithViewPager(emoji_viewpager);
        TabLayout.Tab[] tabs = new TabLayout.Tab[fragmentList.size()];
        for (int i = 0; i < fragmentList.size(); i++) {
            tabs[i] = tl_face_container.getTabAt(i);
            //   View view=View.inflate(getContext(),R.layout.wigth_emoji_bottom_item,null);
            //(ImageView) view.findViewById(R.id.iv_emoji);
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(R.drawable.ee_0);
            tabs[i].setCustomView(imageView);

        }
        LinearLayout mLinearLayout = (LinearLayout) tl_face_container.getChildAt(0);
        // 在所有子控件的中间显示分割线（还可能只显示顶部、尾部和不显示分割线）
        mLinearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE | LinearLayout.SHOW_DIVIDER_END);
        //   mLinearLayout.setShowDividers();
        // 设置分割线的距离本身（LinearLayout）的内间距
        //   mLinearLayout.setDividerPadding(20);
        // 设置分割线的样式

        mLinearLayout.setDividerDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider_vertical));


    }

    private void editTextDelete(EditText editText) {
        if (!TextUtils.isEmpty(editText.getText())) {
            KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
            editText.dispatchKeyEvent(event);
        }

    }

    private void editTextAddEmoji(Emojicon emojicon) {
        if (emojicon.getType() != Emojicon.Type.BIG_EXPRESSION) {
            if (emojicon.getEmojiText() != null) {
                et_sendmessage.append(SmileUtils.getSmiledText(getContext(), emojicon.getEmojiText()));
            }
        } else {
            if (inputViewLisenter != null) {
                inputViewLisenter.onBigExpressionClicked(emojicon);
            }
        }
    }

    private int getSupportSoftInputHeight() {
        int hight = CommonUtils.getSupportSoftInputHeight((Activity) getContext());
        return hight;
    }


    public interface InputViewLisenter {

        boolean onPressToSpeakBtnTouch(View v, MotionEvent event);

        void onBigExpressionClicked(Emojicon emojicon);

        void onSendButtonClicked(String content);

        boolean onEditTextLongClick();

        void onEditTextUp();

        void onAlbumItemClicked();

        void onPhotoItemClicked();

        void onLocationItemClicked();

        void onVideoItemClicked();

        void onCallItemClicked();

        void onFileItemClicked();

        void onRedPackageItemClicked();

        void onTransferItemClicked();
    }


    /**
     * handle the click event for extend menu
     */
    class MyItemClickListener implements ChatExtendMenu.ChatExtendMenuItemClickListener {

        @Override
        public void onClick(int itemId, View view) {

            switch (itemId) {
                case 0:
                    if (inputViewLisenter != null) {
                        inputViewLisenter.onPhotoItemClicked();
                    }
                    break;
                case 1:
                    if (inputViewLisenter != null) {
                        inputViewLisenter.onAlbumItemClicked();
                    }
                    break;
                case 2:
                    if (inputViewLisenter != null) {
                        inputViewLisenter.onLocationItemClicked();
                    }
                    break;
                case 3:
                    if (inputViewLisenter != null) {
                        inputViewLisenter.onVideoItemClicked();
                    }
                    break;
                case 4:
                    if (inputViewLisenter != null) {
                        inputViewLisenter.onCallItemClicked();
                    }
                    break;
                case 5:
                    if (inputViewLisenter != null) {
                        inputViewLisenter.onFileItemClicked();
                    }

                    break;
                case 6:
                    if (inputViewLisenter != null) {
                        inputViewLisenter.onRedPackageItemClicked();
                    }
                    break;
                case 7:
                    if (inputViewLisenter != null) {
                        inputViewLisenter.onTransferItemClicked();
                    }
                    break;
                default:
                    break;
            }
        }

    }


    private void ininExtendView() {

        MyItemClickListener chatExtendMenuItemClickListener = new MyItemClickListener();

        ChatExtendMenu chatExtendMenu = new ChatExtendMenu(getContext());
        for (int i = 0; i < itemNames.length; i++) {
            chatExtendMenu.registerMenuItem(itemNames[i], itemIcons[i], i, chatExtendMenuItemClickListener);
        }
        chatExtendMenu.init();
        final List<View> views = new ArrayList<>();
        views.add(chatExtendMenu);

        extend_viewpager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public Object instantiateItem(ViewGroup arg0, int arg1) {
                ((ViewPager) arg0).addView(views.get(arg1));
                return views.get(arg1);
            }


            @Override
            public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
                ((ViewPager) arg0).removeView(views.get(arg1));

            }

        });


    }
}
