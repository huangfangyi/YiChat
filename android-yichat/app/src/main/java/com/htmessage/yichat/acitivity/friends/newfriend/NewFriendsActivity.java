package com.htmessage.yichat.acitivity.friends.newfriend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.htmessage.yichat.R;
import com.htmessage.yichat.acitivity.BaseActivity;
import com.htmessage.yichat.acitivity.friends.addfriend.AddFriendsNextActivity;

/**
 * 申请与通知
 */
public class NewFriendsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setTitle(R.string.new_friend);
        NewFriendFragment newFriendFragment = new NewFriendFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.contentFrame, newFriendFragment);
        transaction.commit();

        NewFriendPresenter presenter = new NewFriendPresenter(newFriendFragment);
//        showRightView(R.drawable.add_icon, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(NewFriendsActivity.this,
//                        AddFriendsNextActivity.class));
//            }
//        });
//

      showRightTextView("清空", new View.OnClickListener() {
          @Override
          public void onClick(View v) {
               presenter.deleteAll();

          }
      });
    }


}
