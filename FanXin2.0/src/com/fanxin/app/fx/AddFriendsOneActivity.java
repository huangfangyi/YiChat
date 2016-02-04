package com.fanxin.app.fx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.fanxin.app.R;

public class AddFriendsOneActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfriends);
        TextView tv_search=(TextView) this.findViewById(R.id.tv_search);
        tv_search.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
             startActivity(new Intent(AddFriendsOneActivity.this,AddFriendsTwoActivity.class));                
            }
            
        });
    }
    
    public void back(View view){
        finish();
    }
}
