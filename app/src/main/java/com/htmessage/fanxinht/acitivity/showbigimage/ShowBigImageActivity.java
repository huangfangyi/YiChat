/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.htmessage.fanxinht.acitivity.showbigimage;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import com.htmessage.fanxinht.acitivity.login.BaseActivity;
import com.htmessage.fanxinht.R;

/**
 * download and show original image
 * 
 */
public class ShowBigImageActivity extends BaseActivity {

//	private PhotoView image;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base);
		findViewById(R.id.title).setVisibility(View.GONE);
		ShowBigImageFragment fragment = (ShowBigImageFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
		if (fragment == null){
			fragment = new ShowBigImageFragment();
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.add(R.id.contentFrame,fragment);
			transaction.commit();
		}
		Bundle bundle = new Bundle();
		bundle.putString("localPath",getIntent().getStringExtra("localPath"));
		fragment.setArguments(bundle);
	}


	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		finish();
	}
}
