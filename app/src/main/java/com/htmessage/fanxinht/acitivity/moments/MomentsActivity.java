package com.htmessage.fanxinht.acitivity.moments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.htmessage.fanxinht.R;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;

public class MomentsActivity extends BaseActivity {
	private MomentsPresenter momentsPresenter;
	private MomentsFragment momentsFragment;



	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_base);
		setTitle(R.string.moments);
		showRightView(R.drawable.icon_camera_moments, new OnClickListener() {
			@Override
			public void onClick(View v) {
				momentsPresenter.onBarRightViewClicked();
			}
		});
		momentsFragment= (MomentsFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if(momentsFragment==null){
			momentsFragment=new MomentsFragment();
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.add(R.id.contentFrame, momentsFragment);
			transaction.commit();
		}
		momentsPresenter=new MomentsPresenter(momentsFragment);
	}


 	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		momentsPresenter.onResult(requestCode,resultCode,data);
		super.onActivityResult(requestCode, resultCode, data);
	}



}
