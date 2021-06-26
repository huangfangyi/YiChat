package com.htmessage.yichat.acitivity.moments.details;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;

/**
 * Created by huangfangyi on 2017/7/22.
 * qq 84543217
 */

public class MomentsDetailActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_base);
        setTitle(R.string.moments);


        MomentsDetailFragment momentsDetailFragment = (MomentsDetailFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (momentsDetailFragment == null) {

            momentsDetailFragment=new MomentsDetailFragment();
            FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.contentFrame,momentsDetailFragment);
            fragmentTransaction.commit();
        }

        momentsDetailFragment.setArguments(getIntent().getExtras());

        MomentsPresenter momentsPresenter=new MomentsPresenter(momentsDetailFragment);
    }


}
