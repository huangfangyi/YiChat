package com.fanxin.huangfangyi.main.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.fanxin.huangfangyi.R;
import com.fanxin.huangfangyi.main.FXConstant;
import com.fanxin.huangfangyi.main.uvod.preference.SettingsActivity;
import com.ucloud.common.util.SystemUtil;



public class LivesActivity extends AppCompatActivity implements OnItemClickListener{
	String[] demoDirects;
	String[] demoNames;
	public static final String TAG = "LivesActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ListView mListView = (ListView) findViewById(R.id.listview);
		demoNames = getResources().getStringArray(R.array.demoNames);
		mListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, demoNames));
		mListView.setOnItemClickListener(this);
		demoDirects = getResources().getStringArray(R.array.demoDirects);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(!SystemUtil.isNetworkConnected(this)) {
            Toast.makeText(this, "当前网络不可用.", Toast.LENGTH_SHORT).show();
			return;
		}
		switch (SystemUtil.getConnectedType(this)) {
			case ConnectivityManager.TYPE_MOBILE:
				Toast.makeText(this, "当前网络: mobile", Toast.LENGTH_SHORT).show();
				break;
			case ConnectivityManager.TYPE_ETHERNET:
				Toast.makeText(this, "当前网络: ehternet", Toast.LENGTH_SHORT).show();
				break;
			case ConnectivityManager.TYPE_WIFI:
				Toast.makeText(this, "当前网络: wifi", Toast.LENGTH_SHORT).show();
				break;
		}

		if (demoDirects != null && demoDirects.length > position && !TextUtils.isEmpty(demoDirects[position].trim())) {


			if(position==0){
				final EditText inputServer = new EditText(this);
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("输入直播间ID(如:12345)").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
						.setNegativeButton("取消", null);
				builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						setLiveId(inputServer.getText().toString());

					}
				});
				builder.show();

			}else{
				Intent intent = new Intent();
				intent.setAction(demoDirects[position]);
				intent.putExtra("title", demoNames[position]);
				intent.putExtra("videoPath", FXConstant.RTEM_URL);
				startActivity(intent);
			}

		}
	}

	private void setLiveId(String liveId){
		if(TextUtils.isEmpty(liveId)){
			return;
		}
		Intent intent = new Intent();
		intent.setAction(demoDirects[0]);
		intent.putExtra("title", demoNames[0]);
		intent.putExtra("videoPath", FXConstant.RTEM_URL_LIVE+liveId);
		startActivity(intent);
	}




	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_app, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				SettingsActivity.intentTo(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}
}
