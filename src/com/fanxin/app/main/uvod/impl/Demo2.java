package com.fanxin.app.main.uvod.impl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.fanxin.app.R;
import com.fanxin.app.main.uvod.preference.Settings;
import com.ucloud.player.widget.v2.UVideoView;


import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class Demo2 extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, UVideoView.Callback {
	public static final String TAG = Demo2.class.getSimpleName();

	@Bind(R.id.videoview)
	UVideoView mVideoView;

	@Bind(R.id.definition_radio_group)
	RadioGroup mDefinitionRadioGroup;

	@Bind(R.id.decoder_radio_group)
	RadioGroup mDecoderRadioGroup;

	private String mUri;
	List<UVideoView.DefinitionType> definitions;
	UVideoView.DefinitionType defaultDefinition;

	Settings mSettings;
	private int ratioIndex = 0;
	private boolean isInitDefaultSelectDefinition = false;

	@Override
	protected void onCreate(Bundle bundles) {
		super.onCreate(bundles);
		setContentView(R.layout.activity_video_demo2);

		ButterKnife.bind(this);

		mSettings = new Settings(this);

		mUri = getIntent().getStringExtra("videoPath");

		mVideoView.setPlayType(UVideoView.PlayType.NORMAL);  //UVideoView.PlayType.NORMAL 点播  UVideoView.PlayType.LIVE 直播

		mVideoView.setDecoder(UVideoView.DECODER_VOD_SW);
		mVideoView.setRatio(UVideoView.VIDEO_RATIO_FIT_PARENT);
		mVideoView.setPlayMode(UVideoView.PlayMode.REPEAT);
		mVideoView.setVideoPath(mUri);
		mVideoView.registerCallback(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mVideoView != null) {
			int currentPosition = mVideoView.getCurrentPosition();
			Log.d(TAG, "save currentPosition:" + currentPosition);
		}
	}

	public void toggleRatio(View view) {
		mVideoView.setRatio(ratioIndex);
		switch (ratioIndex) {
			case UVideoView.VIDEO_RATIO_FIT_PARENT:
				Toast.makeText(getApplicationContext(), "VIDEO_RATIO_FIT_PARENT", Toast.LENGTH_SHORT).show();
				break;
			case UVideoView.VIDEO_RATIO_WRAP_CONTENT:
				Toast.makeText(getApplicationContext(), "VIDEO_RATIO_WRAP_CONTENT", Toast.LENGTH_SHORT).show();
				break;
			case UVideoView.VIDEO_RATIO_FILL_PARENT:
				Toast.makeText(getApplicationContext(), "VIDEO_RATIO_FILL_PARENT", Toast.LENGTH_SHORT).show();
				break;
			case UVideoView.VIDEO_RATIO_MATCH_PARENT:
				Toast.makeText(getApplicationContext(), "VIDEO_RATIO_MATCH_PARENT", Toast.LENGTH_SHORT).show();
				break;
			case UVideoView.VIDEO_RATIO_16_9_FIT_PARENT:
				Toast.makeText(getApplicationContext(), "VIDEO_RATIO_16_9_FIT_PARENT", Toast.LENGTH_SHORT).show();
				break;
			case UVideoView.VIDEO_RATIO_4_3_FIT_PARENT:
				Toast.makeText(getApplicationContext(), "VIDEO_RATIO_4_3_FIT_PARENT", Toast.LENGTH_SHORT).show();
				break;
		}
		ratioIndex = (++ratioIndex) % 6;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mVideoView != null) {
			if ( mVideoView.isInPlaybackState()) {
				mVideoView.stopPlayback();
			}
			mVideoView.release(true);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		int radioButtonId = group.getCheckedRadioButtonId();
		RadioButton radioButton = (RadioButton) findViewById(radioButtonId);
		try {
			switch (checkedId) {
				case R.id.fhd_radio_button:
					mVideoView.toggleDefinition(UVideoView.DefinitionType.FHD);
					break;
				case R.id.shd_radio_button:
					mVideoView.toggleDefinition(UVideoView.DefinitionType.SHD);
					break;
				case R.id.hd_radio_button:
					mVideoView.toggleDefinition(UVideoView.DefinitionType.HD);
					break;
				case R.id.sd_radio_button:
					mVideoView.toggleDefinition(UVideoView.DefinitionType.SD);
					break;
				case R.id.hw_radio_button:
					mVideoView.toggleDecoder(UVideoView.DECODER_VOD_HW);
					break;
				case R.id.sw_radio_button:
					mVideoView.toggleDecoder(UVideoView.DECODER_VOD_SW);
					break;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void onEvent(int what, String message) {
		switch (what) {
			case UVideoView.Callback.EVENT_PLAY_START:    //prepared
				definitions = mVideoView.getDefinitions();
				for(UVideoView.DefinitionType definition: definitions) {
					Log.d(TAG, "support definition types:" + definition.name());
				}
				defaultDefinition = mVideoView.getDefaultDefinition(); //
				if (!isInitDefaultSelectDefinition) {
					isInitDefaultSelectDefinition = true;
					switch (defaultDefinition) {
						case FHD:
							mDefinitionRadioGroup.check(R.id.fhd_radio_button);
							break;
						case SHD:
							mDefinitionRadioGroup.check(R.id.shd_radio_button);
							break;
						case HD:
							mDefinitionRadioGroup.check(R.id.hd_radio_button);
							break;
						case SD:
							mDefinitionRadioGroup.check(R.id.sd_radio_button);
							break;
					}
					int decoder = mVideoView.getDecoder();
					if (decoder ==  UVideoView.DECODER_VOD_HW) {
						mDecoderRadioGroup.check(R.id.hw_radio_button);
					} else if (decoder == UVideoView.DECODER_VOD_SW) {
						mDecoderRadioGroup.check(R.id.sw_radio_button);
					}
					mDecoderRadioGroup.setOnCheckedChangeListener(Demo2.this);
					mDefinitionRadioGroup.setOnCheckedChangeListener(Demo2.this);
				}
//						mVideoView.setVolume(0,0);
				break;
			case UVideoView.Callback.EVENT_PLAY_PAUSE:
				break;
			case UVideoView.Callback.EVENT_PLAY_STOP:
				break;
			case UVideoView.Callback.EVENT_PLAY_COMPLETION:
				break;
			case UVideoView.Callback.EVENT_PLAY_DESTORY:
				break;
			case UVideoView.Callback.EVENT_PLAY_ERROR:
				break;
		}
	}
}
