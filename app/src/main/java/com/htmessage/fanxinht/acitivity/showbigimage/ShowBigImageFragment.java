package com.htmessage.fanxinht.acitivity.showbigimage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.htmessage.fanxinht.R;

import uk.co.senab.photoview.PhotoView;

/**
 * 项目名称：yichat0718
 * 类描述：ShowBigImageFragment 描述:
 * 创建人：songlijie
 * 创建时间：2017/8/24 13:53
 * 邮箱:814326663@qq.com
 */
public class ShowBigImageFragment extends Fragment {
    private RelativeLayout title;
    private PhotoView image;
    private String localPath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_show_big_image, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();
        initView();
        initData();
    }

    private void initData() {
//        title.setVisibility(View.GONE);
        if (TextUtils.isEmpty(localPath)){
            getActivity().finish();
            return;
        }
        Glide.with(getContext()).load(localPath).diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.default_image).into(image);
    }

    private void initView() {
        image = (PhotoView) getView().findViewById(R.id.image);
        title = (RelativeLayout) getView().findViewById(R.id.title);
    }

    private void getData() {
        localPath = getArguments().getString("localPath");
    }
}
