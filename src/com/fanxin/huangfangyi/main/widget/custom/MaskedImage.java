package com.fanxin.huangfangyi.main.widget.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * @ClassName: MaskedImage
 * @Description: 圆形ImageView蒙层
 * @date Sep 21, 2015 11:18:55 AM
 */
public abstract class MaskedImage extends ImageView {
	private static final Xfermode MASK_XFERMODE = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
	private Bitmap mask;
	private Paint paint;

	// static {//另一种给静态常量赋值的方法
	// PorterDuff.Mode localMode = PorterDuff.Mode.DST_IN;//取两层绘制交集。显示下层。
	// MASK_XFERMODE = new PorterDuffXfermode(localMode);
	// }

	public MaskedImage(Context context) {
		super(context);
	}

	public MaskedImage(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MaskedImage(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public abstract Bitmap createMask();

	protected void onDraw(Canvas paramCanvas) {
		Drawable mDrawable = getDrawable();
		if (mDrawable == null) {
			return;
		}
		try {
			if (this.paint == null) {
				Paint mPaint1 = new Paint();
				this.paint = mPaint1;
				this.paint.setFilterBitmap(false);
				Paint mPaint2 = this.paint;
				Xfermode mXfermode1 = MASK_XFERMODE;
				@SuppressWarnings("unused")
				Xfermode mXfermode2 = mPaint2.setXfermode(mXfermode1);
			}
			float width = getWidth();
			float height = getHeight();
			int i = paramCanvas.saveLayer(0.0F, 0.0F, width, height, null, 31);
			int w = getWidth();
			int h = getHeight();
			mDrawable.setBounds(0, 0, w, h);
			mDrawable.draw(paramCanvas);
			if ((this.mask == null) || (this.mask.isRecycled())) {
				Bitmap mBitmap1 = createMask();
				this.mask = mBitmap1;
			}
			Bitmap mBitmap2 = this.mask;
			Paint mpPaint3 = this.paint;
			paramCanvas.drawBitmap(mBitmap2, 0.0F, 0.0F, mpPaint3);
			paramCanvas.restoreToCount(i);
			return;
		} catch (Exception e) {
			StringBuffer strBuff = new StringBuffer().append("Attempting to draw with recycled bitmap. View ID = ");
			e.printStackTrace();
		}
	}

}
