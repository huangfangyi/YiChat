package com.htmessage.fanxinht.anyrtc.weight;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.htmessage.fanxinht.anyrtc.Utils.Anims;
import com.htmessage.fanxinht.anyrtc.Utils.ScreenUtils;

/**
 * Created by Skyline on 2016/8/2 0018.
 */

public class BottomControl extends RelativeLayout {

    public static final Interpolator ACCELERATE = new AccelerateInterpolator();
    public static final Interpolator LINEA = new LinearInterpolator();
    public static final Interpolator EASE_IN_EASE_OUT = new AccelerateDecelerateInterpolator();
    public static final Interpolator EASE_OUT = new DecelerateInterpolator();

    public boolean mAvailable;
    Context context;

    public BottomControl(Context context) {
        super(context);
        init(context, null);
    }

    public BottomControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, null);
    }

    public BottomControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, null);
    }


    private void init(Context paramContext, AttributeSet paramAttributeSet) {
        this.context = paramContext;
        this.mAvailable = true;
    }

    public void hide() {
        this.mAvailable = false;
        animateBottomMarginTo(this, ScreenUtils.dip2px(context, -90), 300L, Anims.ACCELERATE);
        makeInvisible();
    }

    public void makeInvisible() {
        fadeOut(this, 300L);
    }

    public void show() {
        this.mAvailable = true;
        animateBottomMarginTo(this, ScreenUtils.dip2px(context, 0), 300L,
                EASE_OUT);
        makeVisible();
    }

    public void makeVisible() {
        fadeIn(this, 300L, 0L);
    }

    public void animateBottomMarginTo(final BottomControl paramView, int paramInt, long paramLong, TimeInterpolator paramTimeInterpolator) {
        final MarginLayoutParams localLayoutParams = (MarginLayoutParams) paramView.getLayoutParams();
        final int[] arrayOfInt = new int[2];
        arrayOfInt[0] = localLayoutParams.bottomMargin;
        arrayOfInt[1] = paramInt;
        ValueAnimator localValueAnimator = ValueAnimator.ofInt(arrayOfInt);
        localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator) {
                localLayoutParams.bottomMargin = ((Integer) paramAnonymousValueAnimator.getAnimatedValue()).intValue();
                paramView.requestLayout();
            }
        });
        localValueAnimator.setInterpolator(paramTimeInterpolator);
        localValueAnimator.setDuration(paramLong);
        localValueAnimator.start();
    }

    public void fadeOut(BottomControl paramView, long paramLong) {
        float[] arrayOfFloat = new float[2];
        arrayOfFloat[0] = paramView.getAlpha();
        arrayOfFloat[1] = 0.0F;
        ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(paramView, "alpha", arrayOfFloat);
        localObjectAnimator.setDuration(paramLong);
        localObjectAnimator.setInterpolator(EASE_OUT);
        localObjectAnimator.start();
    }

    public void fadeIn(View paramView, long paramLong1, long paramLong2) {
        float[] arrayOfFloat = new float[2];
        arrayOfFloat[0] = paramView.getAlpha();
        arrayOfFloat[1] = 1.0F;
        ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(paramView, "alpha", arrayOfFloat);
        localObjectAnimator.setDuration(paramLong1);
        if (paramLong2 > 0L) {
            localObjectAnimator.setStartDelay(paramLong2);
        }
        localObjectAnimator.setInterpolator(EASE_OUT);
        localObjectAnimator.start();
        paramView.setVisibility(View.VISIBLE);
    }

}
