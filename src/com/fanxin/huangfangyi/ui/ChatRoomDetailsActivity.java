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
package com.fanxin.huangfangyi.ui;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fanxin.huangfangyi.main.activity.ChatActivity;
import com.fanxin.easeui.utils.EaseUserUtils;
import com.fanxin.easeui.widget.EaseAlertDialog;
import com.fanxin.easeui.widget.EaseExpandGridView;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.fanxin.huangfangyi.R;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.NetUtils;

public class ChatRoomDetailsActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "ChatRoomDetailsActivity";
	private static final int REQUEST_CODE_EXIT = 1;
	private static final int REQUEST_CODE_EXIT_DELETE = 2;
	private static final int REQUEST_CODE_CLEAR_ALL_HISTORY = 3;

	String longClickUsername = null;

	private EaseExpandGridView userGridview;
	private String roomId;
	private ProgressBar loadingPB;
	private Button exitBtn;
	private Button deleteBtn;
	private EMChatRoom room;
	private GridAdapter adapter;
	private int referenceWidth;
	private int referenceHeight;
	private ProgressDialog progressDialog;

	public static ChatRoomDetailsActivity instance;
	
	String st = "";
	// clear all history
	private RelativeLayout clearAllHistory;
	private RelativeLayout blacklistLayout;
	private RelativeLayout changeGroupNameLayout;
	
	private RelativeLayout blockGroupMsgLayout;
	private RelativeLayout showChatRoomIdLayout;
	private TextView chatRoomIdTextView;
	private TextView chatRoomNickTextView;
	private TextView chatRoomOwnerTextView;
	private RelativeLayout showChatRoomNickLayout;
	private RelativeLayout showChatRoomOwnerLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.em_activity_group_details);
		instance = this;
		st = getResources().getString(R.string.people);
		clearAllHistory = (RelativeLayout) findViewById(R.id.clear_all_history);
		clearAllHistory.setVisibility(View.GONE);
		userGridview = (EaseExpandGridView) findViewById(R.id.gridview);
		userGridview.setVisibility(View.GONE);
		loadingPB = (ProgressBar) findViewById(R.id.progressBar);
		exitBtn = (Button) findViewById(R.id.btn_exit_grp);
		deleteBtn = (Button) findViewById(R.id.btn_exitdel_grp);
		blacklistLayout = (RelativeLayout) findViewById(R.id.rl_blacklist);
		changeGroupNameLayout = (RelativeLayout) findViewById(R.id.rl_change_group_name);

		blockGroupMsgLayout = (RelativeLayout)findViewById(R.id.rl_switch_block_groupmsg);
		showChatRoomIdLayout = (RelativeLayout)findViewById(R.id.rl_group_id);
		showChatRoomNickLayout = (RelativeLayout)findViewById(R.id.rl_group_nick);
		showChatRoomOwnerLayout = (RelativeLayout)findViewById(R.id.rl_group_owner);
		chatRoomIdTextView = (TextView)findViewById(R.id.tv_group_id);
		chatRoomNickTextView = (TextView)findViewById(R.id.tv_group_nick_value);
		chatRoomOwnerTextView = (TextView)findViewById(R.id.tv_group_owner_value);
		

		Drawable referenceDrawable = getResources().getDrawable(R.drawable.em_smiley_add_btn);
		referenceWidth = referenceDrawable.getIntrinsicWidth();
		referenceHeight = referenceDrawable.getIntrinsicHeight();

		 // get room id
		 roomId = getIntent().getStringExtra("roomId");
		 
		 showChatRoomIdLayout.setVisibility(View.VISIBLE);
		 chatRoomIdTextView.setText(getResources().getString(R.string.chat_room) + " ID："+roomId);
		 showChatRoomNickLayout.setVisibility(View.VISIBLE);
		 showChatRoomOwnerLayout.setVisibility(View.VISIBLE);
		 
		 room = EMClient.getInstance().chatroomManager().getChatRoom(roomId);
		 chatRoomNickTextView.setText(room.getName());
		 chatRoomOwnerTextView.setText(room.getOwner());

		exitBtn.setVisibility(View.GONE);
		deleteBtn.setVisibility(View.GONE);
		blacklistLayout.setVisibility(View.GONE);
		changeGroupNameLayout.setVisibility(View.GONE);
		blockGroupMsgLayout.setVisibility(View.GONE);
		
		// show dismiss button if you are owner
		if (EMClient.getInstance().getCurrentUser().equals(room.getOwner())) {
			exitBtn.setVisibility(View.GONE);
			deleteBtn.setVisibility(View.GONE);
		}
		
		((TextView) findViewById(R.id.group_name)).setText(room.getName());
		List<String> owner = new java.util.ArrayList<String>();
		owner.add(room.getOwner());
		adapter = new GridAdapter(this, R.layout.em_grid, owner);
		userGridview.setAdapter(adapter);
		
		updateRoom();


		// set OnTouchListener
		userGridview.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (adapter.isInDeleteMode) {
						adapter.isInDeleteMode = false;
						adapter.notifyDataSetChanged();
						return true;
					}
					break;
				default:
					break;
				}
				return false;
			}
		});

		clearAllHistory.setOnClickListener(this);
		blacklistLayout.setOnClickListener(this);
		changeGroupNameLayout.setOnClickListener(this);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String st1 = getResources().getString(R.string.being_added);
		String st2 = getResources().getString(R.string.is_quit_the_group_chat);
		String st3 = getResources().getString(R.string.chatting_is_dissolution);
		String st4 = getResources().getString(R.string.are_empty_group_of_news);
		String st5 = getResources().getString(R.string.is_modify_the_group_name);
		final String st6 = getResources().getString(R.string.Modify_the_group_name_successful);
		final String st7 = getResources().getString(R.string.change_the_group_name_failed_please);
		String st8 = getResources().getString(R.string.Are_moving_to_blacklist);
		final String st9 = getResources().getString(R.string.failed_to_move_into);
		
		final String stsuccess = getResources().getString(R.string.Move_into_blacklist_success);
		if (resultCode == Activity.RESULT_OK) {
			if (progressDialog == null) {
				progressDialog = new ProgressDialog(ChatRoomDetailsActivity.this);
				progressDialog.setMessage(st1);
				progressDialog.setCanceledOnTouchOutside(false);
			}
			switch (requestCode) {
			case REQUEST_CODE_EXIT: // quit the group
				progressDialog.setMessage(st2);
				progressDialog.show();
				exitGroup();
				break;

			default:
				break;
			}
		}
	}


	public void exitGroup(View view) {
		startActivityForResult(new Intent(this, ExitGroupDialog.class), REQUEST_CODE_EXIT);

	}


	public void exitDeleteGroup(View view) {
		startActivityForResult(new Intent(this, ExitGroupDialog.class).putExtra("deleteToast", getString(R.string.dissolution_group_hint)),
				REQUEST_CODE_EXIT_DELETE);

	}

	/**
	 * clear conversation history in group
	 */
	public void clearGroupHistory() {
		EMConversation conversation = EMClient.getInstance().chatManager().getConversation(room.getId(), EMConversationType.ChatRoom);
		if (conversation != null) {
			conversation.clearAllMessages();
		}
		Toast.makeText(this, R.string.messages_are_empty, 0).show();
	}

	/**
	 * exit group
	 * 
	 * @param groupId
	 */
	private void exitGroup() {
		new Thread(new Runnable() {
			public void run() {
				try {
					EMClient.getInstance().chatroomManager().leaveChatRoom(roomId);
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							setResult(Activity.RESULT_OK);
							finish();
							if(ChatActivity.activityInstance != null)
							    ChatActivity.activityInstance.finish();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(), "Failed to quit group: " + e.getMessage(), 1).show();
						}
					});
				}
			}
		}).start();
	}
	
	protected void updateRoom() {
		new Thread(new Runnable() {
			public void run() {
				try {
					final EMChatRoom returnRoom = EMClient.getInstance().chatroomManager().fetchChatRoomFromServer(roomId);

					runOnUiThread(new Runnable() {
						public void run() {
							((TextView) findViewById(R.id.group_name)).setText(returnRoom.getName());
							loadingPB.setVisibility(View.INVISIBLE);
							adapter.notifyDataSetChanged();
							if (EMClient.getInstance().getCurrentUser().equals(returnRoom.getOwner())) {
								// show dismiss button
								exitBtn.setVisibility(View.GONE);
								deleteBtn.setVisibility(View.GONE);
							} else {
								// show exit button
								exitBtn.setVisibility(View.GONE);
								deleteBtn.setVisibility(View.GONE);

							}
						}
					});

				} catch (Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							loadingPB.setVisibility(View.INVISIBLE);
						}
					});
				}
			}
		}).start();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.clear_all_history: // clear conversation history
			String st9 = getResources().getString(R.string.sure_to_empty_this);
			new EaseAlertDialog(ChatRoomDetailsActivity.this, null, st9, null, new EaseAlertDialog.AlertDialogUser() {
                
                @Override
                public void onResult(boolean confirmed, Bundle bundle) {
                    if(confirmed){
                        clearGroupHistory();
                    }
                }
            }, true).show();
			break;

		default:
			break;
		}

	}

	/**
	 * group member gridadapter
	 * 
	 * @author admin_new
	 * 
	 */
	private class GridAdapter extends ArrayAdapter<String> {

		private int res;
		public boolean isInDeleteMode;
		private List<String> objects;

		public GridAdapter(Context context, int textViewResourceId, List<String> objects) {
			super(context, textViewResourceId, objects);
			this.objects = objects;
			res = textViewResourceId;
			isInDeleteMode = false;
		}

		@Override
		public View getView(final int position, View convertView, final ViewGroup parent) {
		    ViewHolder holder = null;
			if (convertView == null) {
			    holder = new ViewHolder();
				convertView = LayoutInflater.from(getContext()).inflate(res, null);
				holder.imageView = (ImageView) convertView.findViewById(R.id.iv_avatar);
				holder.textView = (TextView) convertView.findViewById(R.id.tv_name);
				holder.badgeDeleteView = (ImageView) convertView.findViewById(R.id.badge_delete);
				convertView.setTag(holder);
			}else{
			    holder = (ViewHolder) convertView.getTag();
			}
			final LinearLayout button = (LinearLayout) convertView.findViewById(R.id.button_avatar);
			// last item is "remove" button
			if (position == getCount() - 1) {
			    holder.textView.setText("");
				// set "remove" button
			    holder.imageView.setImageResource(R.drawable.em_smiley_minus_btn);

				// no "remove" button if you are not owner of the group
				if (!room.getOwner().equals(EMClient.getInstance().getCurrentUser())) {
					// if current user is not the owner, hide add/remove btn
					convertView.setVisibility(View.INVISIBLE);
				} else { // show delete icon
					if (isInDeleteMode) {
						// already delete mode, hide "remove" button
						convertView.setVisibility(View.INVISIBLE);
					} else {
						// normal mode
						convertView.setVisibility(View.VISIBLE);
						convertView.findViewById(R.id.badge_delete).setVisibility(View.INVISIBLE);
					}
					final String st10 = getResources().getString(R.string.The_delete_button_is_clicked);
					button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							EMLog.d(TAG, st10);
							isInDeleteMode = true;
							notifyDataSetChanged();
						}
					});
				}
			} else if (position == getCount() - 2) { // "add" button
			    holder.textView.setText("");
			    holder.imageView.setImageResource(R.drawable.em_smiley_add_btn);

				// only owner of room has permission to add/remove member
				if (!room.getOwner().equals(EMClient.getInstance().getCurrentUser())) {
					// if current user is not room owner, hide add/remove btn
					convertView.setVisibility(View.INVISIBLE);
				} else {
					// already delete mode, hide "remove" button
					if (isInDeleteMode) {
						convertView.setVisibility(View.INVISIBLE);
					} else {
						convertView.setVisibility(View.VISIBLE);
						convertView.findViewById(R.id.badge_delete).setVisibility(View.INVISIBLE);
					}
				}
			} else { // group member item
				final String username = getItem(position);
				convertView.setVisibility(View.VISIBLE);
				button.setVisibility(View.VISIBLE);
				holder.textView.setText(username);
				EaseUserUtils.setUserAvatar(getContext(), username, holder.imageView);
				// here we just use default avatar, you need handle it if want to show other avatar
				if (isInDeleteMode) {
					// show remove icon if under delete mode
					convertView.findViewById(R.id.badge_delete).setVisibility(View.VISIBLE);
				} else {
					convertView.findViewById(R.id.badge_delete).setVisibility(View.INVISIBLE);
				}
				button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (isInDeleteMode) {
							// just return if user want remove himself
							if (EMClient.getInstance().getCurrentUser().equals(username)) {
							    new EaseAlertDialog(ChatRoomDetailsActivity.this, R.string.not_delete_myself).show();
								return;
							}
							if (!NetUtils.hasNetwork(getApplicationContext())) {
								Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable), 0).show();
								return;
							}
							EMLog.d("room", "remove user from room:" + username);
						} else {
                            // do nothing here, you can show group member's profile here
						}
					}
				});

			}
			return convertView;
		}

		@Override
		public int getCount() {
			return super.getCount() + 2;
		}
	}


	public void back(View view) {
		setResult(Activity.RESULT_OK);
		finish();
	}

	@Override
	public void onBackPressed() {
		setResult(Activity.RESULT_OK);
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		instance = null;
	}
	
	private static class ViewHolder{
	    ImageView imageView;
	    TextView textView;
	    ImageView badgeDeleteView;
	}

}
