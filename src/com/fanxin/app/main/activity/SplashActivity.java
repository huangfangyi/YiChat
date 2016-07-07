package com.fanxin.app.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.redpacketsdk.RPCallback;
import com.easemob.redpacketsdk.RedPacket;
import com.fanxin.app.DemoHelper;
import com.fanxin.app.main.service.ContactsService;
import com.fanxin.app.ui.*;
import com.hyphenate.chat.EMClient;
import com.fanxin.app.R;

/**
 * 开屏页
 *
 */
public class SplashActivity extends   BaseActivity {
	private RelativeLayout rootLayout;
	private TextView versionText;
	
	private static final int sleepTime = 2000;

	@Override
	protected void onCreate(Bundle arg0) {
		setContentView(R.layout.fx_activity_splash);
		super.onCreate(arg0);

		rootLayout = (RelativeLayout) findViewById(R.id.splash_root);
		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(1500);
		rootLayout.startAnimation(animation);
	}

	@Override
	protected void onStart() {
		super.onStart();

		new Thread(new Runnable() {
			public void run() {
				if (DemoHelper.getInstance().isLoggedIn()) {
					// auto login mode, make sure all group and conversation is loaed before enter the main screen
					long start = System.currentTimeMillis();
					EMClient.getInstance().groupManager().loadAllGroups();
					EMClient.getInstance().chatManager().loadAllConversations();

					RedPacket.getInstance().initRPToken(DemoHelper.getInstance().getCurrentUsernName(), DemoHelper.getInstance().getCurrentUsernName(), EMClient.getInstance().getChatConfig().getAccessToken(), new RPCallback() {
						@Override
						public void onSuccess() {

						}

						@Override
						public void onError(String s, String s1) {

						}
					});
					long costTime = System.currentTimeMillis() - start;
					//wait
					if (sleepTime - costTime > 0) {
						try {
							Thread.sleep(sleepTime - costTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					startService(new Intent(SplashActivity.this, ContactsService.class));
					//enter main screen
					startActivity(new Intent(SplashActivity.this, MainActivity.class));
					finish();
				}else {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
					startActivity(new Intent(SplashActivity.this, LoginActivity.class));
					finish();
				}
			}
		}).start();

	}

}
