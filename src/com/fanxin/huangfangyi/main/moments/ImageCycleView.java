package com.fanxin.huangfangyi.main.moments;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.FXConstant;

/**
 * 广告图片自动轮播控件</br>
 * 
 */
public class ImageCycleView extends LinearLayout {

    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 图片轮播视图
     */
    private ViewPager mAdvPager = null;
    /**
     * 滚动图片视图适配
     */
    private ImageCycleAdapter mAdvAdapter;
    /**
     * 图片轮播指示器控件
     */
    private ViewGroup mGroup;

    /**
     * 图片轮播指示个图
     */
    private ImageView mImageView = null;

    /**
     * 滚动图片指示视图列表
     */
    private ImageView[] mImageViews = null;

    /**
     * 手机密度
     */
    private float mScale;
  //  private boolean isStop;
    private List<String> rewardList = new ArrayList<String>();

  //  private TextView imageName;

    /**
     * @param context
     */
    public ImageCycleView(Context context) {
        super(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public ImageCycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mScale = context.getResources().getDisplayMetrics().density;
        LayoutInflater.from(context).inflate(R.layout.fx_layout_imagecycle,
                this);
        mAdvPager = (ViewPager) findViewById(R.id.adv_pager);
        mAdvPager.setOnPageChangeListener(new GuidePageChangeListener());
        mAdvPager.setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    // 开始图片滚动
                    // / startImageTimerTask();
                    break;
                default:
                    // 停止图片滚动
                    // / stopImageTimerTask();
                    break;
                }
                return false;
            }
        });
        // 滚动图片右下指示器视
        mGroup = (ViewGroup) findViewById(R.id.viewGroup);
      //  imageName = (TextView) findViewById(R.id.viewGroup2);
    }

    /**
     * 装填图片数据
     * 
     * @param
     * @param imageCycleViewListener
     */
    public void setImageResources(String[] images, int page,
            ImageCycleViewListener imageCycleViewListener) {
//        for (int i = 0; i < images.length; i++) {
//            String imageName = String.valueOf(jsonArr.getJSONObject(i)
//                    .getJSONArray("photo_reward").size());
//            rewardList.add(num);
       // }

        // 清除
        mGroup.removeAllViews();
        // 图片广告数量
        final int imageCount = images.length;
        mImageViews = new ImageView[imageCount];
        for (int i = 0; i < imageCount; i++) {
            mImageView = new ImageView(mContext);
            int imageParams = (int) (mScale * 10 + 0.5f);// XP与DP转换，适应应不同分辨率
            int imagePadding = (int) (mScale * 5 + 0.5f);
            LayoutParams params = new LayoutParams(imageParams, imageParams);
            params.leftMargin = 30;
            mImageView.setScaleType(ScaleType.CENTER_CROP);
            mImageView.setLayoutParams(params);
            mImageView.setPadding(imagePadding, imagePadding, imagePadding,
                    imagePadding);

            mImageViews[i] = mImageView;
            if (i == 0) {
                mImageViews[i]
                        .setBackgroundResource(R.drawable.fx_banner_dian_focus);
            } else {
                mImageViews[i]
                        .setBackgroundResource(R.drawable.fx_banner_dian_blur);
            }
            mGroup.addView(mImageViews[i]);
        }

      //  imageName.setText("打赏(" + rewardList.get(page) + ")");
        mAdvAdapter = new ImageCycleAdapter(mContext, images, page,
                imageCycleViewListener);
        mAdvPager.setAdapter(mAdvAdapter);
        mAdvPager.setCurrentItem(page);
        // startImageTimerTask();
    }

    /**
     * 图片轮播(手动控制自动轮播与否，便于资源控件）
     */
    public void startImageCycle() {
        // / startImageTimerTask();
    }

    /**
     * 暂停轮播—用于节省资源
     */
    public void pushImageCycle() {
        // / stopImageTimerTask();
    }

    // /**
    // * 图片滚动任务
    // */
    // private void startImageTimerTask() {
    // stopImageTimerTask();
    // // 图片滚动
    // mHandler.postDelayed(mImageTimerTask, 3000);
    // }
    //
    // /**
    // * 停止图片滚动任务
    // */
    // private void stopImageTimerTask() {
    // isStop=true;
    // mHandler.removeCallbacks(mImageTimerTask);
    // }

    // private Handler mHandler = new Handler();

    // /**
    // * 图片自动轮播Task
    // */
    // private Runnable mImageTimerTask = new Runnable() {
    // @Override
    // public void run() {
    // if (mImageViews != null) {
    // mAdvPager.setCurrentItem(mAdvPager.getCurrentItem()+1);
    // if(!isStop){ //if isStop=true //当你退出后 要把这个给停下来 不然 这个一直存在 就一直在后台循环
    // mHandler.postDelayed(mImageTimerTask, 3000);
    // }
    //
    // }
    // }
    // };

    /**
     * 轮播图片监听
     * 
     * 
     */
    private final class GuidePageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int state) {
            // // if (state == ViewPager.SCROLL_STATE_IDLE)
            // // startImageTimerTask();
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int index) {
            index = index % mImageViews.length;
            // 设置图片滚动指示器背
            mImageViews[index]
                    .setBackgroundResource(R.drawable.fx_banner_dian_focus);
            // imageNameList.get(index)

        //    imageName.setText("打赏(" + rewardList.get(index) + ")");
            for (int i = 0; i < mImageViews.length; i++) {
                if (index != i) {
                    mImageViews[i]
                            .setBackgroundResource(R.drawable.fx_banner_dian_blur);
                }
            }
        }
    }

    private class ImageCycleAdapter extends PagerAdapter {

        /**
         * 图片视图缓存列表
         */
        private ArrayList<ImageView> mImageViewCacheList;

        /**
         * 图片资源列表
         */
        String[] imageArray;
     //   private JSONArray jsonArray = new JSONArray();

        // private ArrayList<String> mAdList = new ArrayList<String>();
        // private ArrayList<String> nameList = new ArrayList<String>();

        /**
         * 广告图片点击监听
         */
        private ImageCycleViewListener mImageCycleViewListener;

        private Context mContext;

        public ImageCycleAdapter(Context context, String[] imageArray,
                int page, ImageCycleViewListener imageCycleViewListener) {
            this.mContext = context;
            this.imageArray = imageArray;
            mImageCycleViewListener = imageCycleViewListener;
            mImageViewCacheList = new ArrayList<ImageView>();
        }

        @Override
        public int getCount() {
              return imageArray.length;
         //   return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            String imageUrl = FXConstant.URL_SOCIAL_PHOTO
                    + "big_"
                    + imageArray[position % imageArray.length];
                             
            ImageView imageView = null;
            if (mImageViewCacheList.isEmpty()) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                imageView.setScaleType(ScaleType.CENTER_CROP);
                // 设置图片点击监听
                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mImageCycleViewListener.onImageClick(position % imageArray.length, v);
                    }
                });
            } else {
                imageView = mImageViewCacheList.remove(0);
            }
        //    imageView.setTag(imageUrl);
            container.addView(imageView);
            mImageCycleViewListener.displayImage(imageUrl, imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ImageView view = (ImageView) object;
            mAdvPager.removeView(view);
            mImageViewCacheList.add(view);

        }

    }

    /**
     * 轮播控件的监听事件
     * 
     * @author minking
     */
    public static interface ImageCycleViewListener {
        /**
         * 加载图片资源
         * 
         * @param imageURL
         * @param imageView
         */
        public void displayImage(String imageURL, ImageView imageView);

        /**
         * 单击图片事件
         * 
         * @param position
         * @param imageView
         */
        public void onImageClick(int position, View imageView);
    }

}
