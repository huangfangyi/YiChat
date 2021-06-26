package com.htmessage.yichat.acitivity.moments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.htmessage.yichat.HTApp;
import com.htmessage.yichat.HTConstant;
import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.utils.CommonUtils;
import com.htmessage.yichat.utils.DateUtils;
import com.htmessage.yichat.utils.OkHttpUtils;
import com.htmessage.yichat.widget.HTAlertDialog;
import com.htmessage.yichat.widget.PhotoViewPager;

import java.io.File;

import uk.co.senab.photoview.PhotoView;


public class BigImageActivity extends BaseActivity {
    private boolean isNetUrl = true;
    private PhotoViewPager photoViewPager;
    private String[] images;
    private int page = 0;
    private String filePath = null;
    private TextView tv_show_items;

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
        photoViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                page = position;
                filePath = images[position];
                showTips(page);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initData() {
        MyAdapter adapter = new MyAdapter(images);
        photoViewPager.setAdapter(adapter);
        photoViewPager.setCurrentItem(page);
        showTips(page);
    }

    private void initView() {
        photoViewPager = (PhotoViewPager) this.findViewById(R.id.viewpager_image);
        tv_show_items = (TextView) this.findViewById(R.id.tv_show_items);
    }

    private void getData() {
        //图片路径
        images = getIntent().getStringArrayExtra("images");
        //当前索引
        page = getIntent().getIntExtra("page", 0);
        //是否是网络图片
        isNetUrl = getIntent().getBooleanExtra("isNetUrl", true);
        filePath = images[page];
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
            return view == (PhotoView) object;
        }

        @Override
        public PhotoView instantiateItem(ViewGroup container, int position) {
            String image = images[position];
            final PhotoView photoView = new PhotoView(BigImageActivity.this);
            if (!isNetUrl) {
                CommonUtils.loadImage(BigImageActivity.this, image, photoView);
            }
            if (isNetUrl && !image.contains("http")) {
                image = HTConstant.baseImgUrl + image;
            }
            CommonUtils.loadImage(BigImageActivity.this, image, photoView);
            photoView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showDialog(photoView);
                    return true;
                }
            });
            container.addView(photoView);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((PhotoView) object);
        }
    }

    private void showDialog(final View view) {
        HTAlertDialog dialog = new HTAlertDialog(BigImageActivity.this, null, new String[]{getString(R.string.save)});//, getString(R.string.scan_qrcode_image)
        dialog.init(new HTAlertDialog.OnItemClickListner() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        saveImage(filePath);
                        break;
                }
            }
        });
    }

    /**
     * 保存图片
     *
     * @param filePath
     */
    private void saveImage(String filePath) {
        if (ActivityCompat.checkSelfPermission(BigImageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||ActivityCompat.checkSelfPermission(BigImageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            showToast(R.string.need_sdcard_permission);
            return;
        }
        CommonUtils.showDialogNumal(BigImageActivity.this, getString(R.string.saving));
        if (TextUtils.isEmpty(filePath)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showToast(R.string.saving_failed);
                }
            }, 500);
            return;
        }
        if (isNetUrl && !filePath.contains("http")) {
            filePath = HTConstant.baseImgUrl + filePath;
        }
        String savePath = getSavePath();
        if (!isNetUrl) {
            saveLocalImage(filePath, page, 0);
        } else if (isNetUrl && filePath.contains("http")) {
            saveNetImage(filePath, savePath, page);
        }
    }

    @NonNull
    private String getSavePath() {
        String dirFilePath = HTApp.getInstance().getImageDirFilePath();
        String fileName = DateUtils.getyyMMddTime(System.currentTimeMillis()) + ".png";
        return dirFilePath + "/" + fileName;
    }

    /**
     * 保存图片
     *
     * @param filePath 网络地址
     * @param savePath 保存的地址
     */
    private void saveNetImage(String filePath, final String savePath, final int page) {
        if (TextUtils.isEmpty(savePath)) {
            showToast(R.string.saving_failed);
            return;
        }
        new OkHttpUtils(getBaseContext()).loadFile(filePath, savePath, new OkHttpUtils.DownloadCallBack() {
            @Override
            public void onSuccess() {
                saveLocalImage(savePath, page, 1);
            }

            @Override
            public void onFailure(String message) {
                showToast(R.string.saving_failed);
            }
        });
    }

    /**
     * 保存图片
     *
     * @param filePath
     */
    private void saveLocalImage(final String filePath, int page, final int type) {
        new Thread() {
            @Override
            public void run() {
                boolean success = CommonUtils.saveImageToAlubm(getBaseContext(), filePath);
                if (success) {
                    if (type == 1) {
                        File file = new File(filePath);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    showToast(R.string.saving_successful);
                } else {
                    showToast(R.string.saving_failed);
                }
            }
        }.start();
    }

    /**
     * 吐司
     *
     * @param resId
     */
    private void showToast(final int resId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CommonUtils.cencelDialog();
                CommonUtils.showToastShort(getBaseContext(), resId);
            }
        });
    }

    /**
     * 显示顶部的button
     * @param page
     */
    private void showTips(int page){
        if (tv_show_items!=null){
            tv_show_items.setText((page+1)+"/"+images.length);
        }
    }
}
