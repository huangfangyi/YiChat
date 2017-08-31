package com.htmessage.fanxinht.acitivity.moments;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;
import com.htmessage.fanxinht.widget.PhotoViewPager;

import uk.co.senab.photoview.PhotoView;


public class BigImageActivity extends BaseActivity {
    private boolean isNetUrl=true;
    private PhotoViewPager photoViewPager;
    private TabLayout tabLayout;
    private TabLayout.Tab[] tabs;
    private String[] images;
    private int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bigimage);
        getData();
        initView();
        initData();
        setListener();
    }

    private void setListener() {

    }

    private void initData() {
        MyAdapter adapter = new MyAdapter(images);
        photoViewPager.setAdapter(adapter);
        photoViewPager.setCurrentItem(page);
        tabLayout.setupWithViewPager(photoViewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabs = new TabLayout.Tab[images.length];
        for (int i = 0; i < images.length; i++) {
            tabs[i] = tabLayout.getTabAt(i);
            tabs[i].setCustomView(getBottomView(i,page, R.drawable.pointer_selector));
        }
    }

    private void initView() {
        photoViewPager= (PhotoViewPager) this.findViewById(R.id.viewpager_image);
        tabLayout= (TabLayout) this.findViewById(R.id.tabLayout);
    }

    private void getData() {
        //图片路径
        images=getIntent().getStringArrayExtra("images");
        //当前索引
        page = getIntent().getIntExtra("page", 0);
        //是否是网络图片
        isNetUrl=getIntent().getBooleanExtra("isNetUrl",true);
    }


    private View getBottomView(final int index,final int page,int drawableRes) {
        View view = getLayoutInflater().inflate(R.layout.layout_big_photo_point, null);
        ImageView button = (ImageView) view.findViewById(R.id.iv_foot_point);
        if (index == page){
            tabs[index].select();
        }
        button.setBackground(getDrawable(drawableRes));
        if (isCompatible(Build.VERSION_CODES.LOLLIPOP)) {
            button.setStateListAnimator(null);
        }
        return view;
    }




    private class MyAdapter extends PagerAdapter {
        private String[] images;

        public MyAdapter(String[] images) {
            this.images = images;
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == (PhotoView)object;
        }

        @Override
        public PhotoView instantiateItem(ViewGroup container, int position) {
            String image = images[position];
            PhotoView photoView = new PhotoView(BigImageActivity.this);
            if(!isNetUrl){
                Glide.with(BigImageActivity.this).load(image).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_image).error(R.drawable.default_image).into(photoView);
            }
            if(isNetUrl&&!image.contains("http")){
                image=HTConstant.baseImgUrl+image;
            }
            Glide.with(BigImageActivity.this).load(image).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.default_image).error(R.drawable.default_image).into(photoView);
            container.addView(photoView);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((PhotoView) object);
        }
    }
}
