package com.htmessage.yichat.acitivity.chat.weight.header;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.htmessage.yichat.utils.DensityUtil;


/**
 * Created by caizhiming on 2016/2/4.
 * 下拉加载更多控件（仿QQ和微信的对话聊天记录页面）
 */
public class PullToLoadMoreListView extends FrameLayout {
    public static final String TAG = PullToLoadMoreListView.class.getSimpleName();

    private RefreshHeader mRefreshHeader;
    private ListView mListView;
    private int mHeight;

    private DecelerateInterpolator mInterpolator = new DecelerateInterpolator(5);


    private int mHeaderHeight;
    private float mStartY;
    private float mCurY;
    private boolean mIsRefreshing;

    private int mLastCount = 0;
    private float mLastTranslationY = 0f;


    public static final int REFRESH_STATUS_PULL_REFRESH = 0;//查看更早记录...;
    public static final int REFRESH_STATUS_RELEASE_REFRESH = 1;//松开开始加载...
    public static final int REFRESH_STATUS_REFRESHING = 2;// "正在加载...";
    public static final int REFRESH_STATUS_REFRESH_FINISH = 3;//"加载完成";
    private int mRefreshStatus = REFRESH_STATUS_PULL_REFRESH;


    public PullToLoadMoreListView(Context context) {
        this(context, null, 0);
    }

    public PullToLoadMoreListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToLoadMoreListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }
    public ListView getListView(){
        return  mListView;
    }
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mHeaderHeight =  DensityUtil.dip2px(getContext(), 50);

        addHeaderView(context);
        addListView(context, attrs);
    }

    private void addHeaderView(Context context){
        mRefreshHeader = new RefreshHeader(context);
        mHeaderHeight = (int) mRefreshHeader.getHeaderHeight();
        addView(mRefreshHeader, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mHeaderHeight));
    }
    private void addListView(Context context, AttributeSet attrs){
        mListView = new ListView(context,attrs);
        addView(mListView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mHeight <= 0){
            mHeight = getMeasuredHeight();
            Log.d("czm", "Height =" + mHeight);
        }
    }

    private boolean canChildScrollUp() {
        if (mListView == null) {
            return false;
        }
        return ViewCompat.canScrollVertically(mListView, -1);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mIsRefreshing) {
            return true;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartY = ev.getY();
                mCurY = mStartY;
                break;
            case MotionEvent.ACTION_MOVE:
                float curY = ev.getY();
                float dy = curY - mStartY;
                Log.d("czm", "dy=" + dy);
                if (dy > 0 && !canChildScrollUp()) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsRefreshing) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mCurY = event.getY();
                float dy = mCurY - mStartY;
                Log.v("czm","dy====="+dy);
                if(dy <0)
                    return true;
                dy = Math.max(0, dy);
                if (mListView != null) {
                    Log.d("czm", "dy/mHeight=" + (dy / mHeight));
                    float offsetY = mInterpolator.getInterpolation(dy / mHeight) * dy/3;
                    mListView.setTranslationY(offsetY);
                    mRefreshHeader.getLayoutParams().height = (int) (offsetY+0.5f);
                    mRefreshHeader.requestLayout();
                    if(mListView.getTranslationY() >= mHeaderHeight){
                        mRefreshStatus = REFRESH_STATUS_RELEASE_REFRESH;
                        mRefreshHeader.updateRefreshStatus(mRefreshStatus);
                    }else{
                        mRefreshStatus = REFRESH_STATUS_PULL_REFRESH;
                        mRefreshHeader.updateRefreshStatus(mRefreshStatus);
                    }
                }
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(mListView != null){
                    if(mListView.getTranslationY() >= mHeaderHeight){
                        mRefreshStatus = REFRESH_STATUS_REFRESHING;
                        mRefreshHeader.updateRefreshStatus(mRefreshStatus);
                        mIsRefreshing = true;
                        upToMiddleAnim();
                    }else{
                        upToTopAnim();
                    }
                }
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }
    private void upToMiddleAnim(){
        float offsetY = mListView.getTranslationY();
        final ValueAnimator backToMiddleAnim = ValueAnimator.ofFloat(offsetY, 0);
        final float pullHeight = offsetY;
        backToMiddleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (float) animation.getAnimatedValue();
                val = mInterpolator.getInterpolation(val / pullHeight) * val;
                if (mListView != null) {
                    mListView.setTranslationY(val);
                }
                mRefreshHeader.getLayoutParams().height = (int) (val + 0.5f);
                mRefreshHeader.requestLayout();
                Log.d("czm", "offsetY=" + mListView.getTranslationY());
                if (mListView.getTranslationY() <= mHeaderHeight) {
                    backToMiddleAnim.cancel();
                }
            }
        });
        backToMiddleAnim.setDuration((long) (offsetY * 600 / pullHeight));
        backToMiddleAnim.start();
        backToMiddleAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mOnRefreshListener != null) {
                    mLastCount = mListView.getAdapter().getCount();
                    mOnRefreshListener.onPullDownLoadMore();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
    private void upToTopAnim(){
        float offsetY = mListView.getTranslationY();
        final ValueAnimator backToTopAnim = ValueAnimator.ofFloat(offsetY, 0);
        final float pullHeight = offsetY;
        backToTopAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (float) animation.getAnimatedValue();
                val = mInterpolator.getInterpolation(val / pullHeight) * val;
                if (mListView != null) {
                    mListView.setTranslationY(val);
                }
                mRefreshHeader.getLayoutParams().height = (int) (val+0.5f);
                mRefreshHeader.requestLayout();
                Log.d("czm", "offsetY=" + mListView.getTranslationY());
            }
        });
        backToTopAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mRefreshStatus = REFRESH_STATUS_PULL_REFRESH;
                mRefreshHeader.updateRefreshStatus(mRefreshStatus);

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        backToTopAnim.setDuration((long) (offsetY * 600 / pullHeight));
        backToTopAnim.start();
    }


    private void completeBackToTop(){
        mListView.setTranslationY(0);
        int num = mListView.getAdapter().getCount() - mLastCount;
        Log.v("czm","num="+num);
        if(num > 0){
            mListView.setSelectionFromTop(num, (int) mLastTranslationY);
        }
    }
    public void onRefreshComplete(){
        mLastTranslationY = mListView.getTranslationY();
        mIsRefreshing = false;
        mRefreshStatus = REFRESH_STATUS_REFRESH_FINISH;
        mRefreshHeader.updateRefreshStatus(mRefreshStatus);
        completeBackToTop();
    }
    private OnRefreshListener mOnRefreshListener;
    public void setOnRefreshListener(OnRefreshListener listener){
        mOnRefreshListener = listener;
    }
    public interface OnRefreshListener{
        void onPullDownLoadMore();
    }
}
