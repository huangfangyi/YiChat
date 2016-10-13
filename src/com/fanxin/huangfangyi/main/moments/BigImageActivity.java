package com.fanxin.huangfangyi.main.moments;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.ui.BaseActivity;


public class BigImageActivity extends BaseActivity {
  //  private JSONArray json = null;

    private ImageCycleView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fx_activity_bigimage);
      //  String jsonStr = getIntent().getStringExtra("jsonStr");
        
        String[] images=getIntent().getStringArrayExtra("images");
     //   json = JSONArray.parseArray(jsonStr);
//        if (json == null) {
//            finish();
//            return;
//        }

        int page = getIntent().getIntExtra("page", 0);

        mAdView = (ImageCycleView) this.findViewById(R.id.ad_view);
        mAdView.setImageResources(images, page, mAdCycleViewListener);

    }

     
       
     

    private ImageCycleView.ImageCycleViewListener mAdCycleViewListener = new ImageCycleView.ImageCycleViewListener() {
        @Override
        public void onImageClick(int position, View imageView) {

           // finish();
        }

        @Override
        public void displayImage(String imageURL, ImageView imageView) {
            Glide.with(BigImageActivity.this).load(imageURL).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
        //    ImageLoader.getInstance().displayImage(imageURL, imageView);
        }
    };

}
