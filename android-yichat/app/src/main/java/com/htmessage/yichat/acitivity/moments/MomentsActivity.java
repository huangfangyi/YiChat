package com.htmessage.yichat.acitivity.moments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;

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
		}, new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {

				momentsPresenter.onBarRightViewLongClicked();

				return true;
			}
		});

			momentsFragment=new MomentsFragment();
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.add(R.id.contentFrame, momentsFragment);
			transaction.commit();

		momentsPresenter=new MomentsPresenter(momentsFragment);
	}


 	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		momentsPresenter.onResult(requestCode,resultCode,data);
		super.onActivityResult(requestCode, resultCode, data);
	}



}
