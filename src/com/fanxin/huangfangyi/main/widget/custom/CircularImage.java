package com.fanxin.huangfangyi.main.widget.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

/**
 * @ClassName: CircularImage
 * @Description: 圆形头像工具类
 * @date Sep 21, 2015 11:20:15 AM
 */
public class CircularImage extends MaskedImage {

	public CircularImage(Context context) {
		super(context);
	}

	public CircularImage(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CircularImage(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public Bitmap createMask() {
		int w = getWidth();
		int h = getHeight();
		Bitmap.Config mConfig = Bitmap.Config.ARGB_8888;
		Bitmap mBitmap = Bitmap.createBitmap(w, h, mConfig);
		Canvas mCanvas = new Canvas(mBitmap);
		Paint mPaint = new Paint(1);
		mPaint.setColor(-16777216);
		float f1 = getWidth();
		float f2 = getHeight();
		RectF mRectF = new RectF(0.0F, 0.0F, f1, f2);
		mCanvas.drawOval(mRectF, mPaint);
		return mBitmap;
	}

}
