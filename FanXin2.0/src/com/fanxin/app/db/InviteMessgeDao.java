package com.fanxin.app.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fanxin.app.domain.InviteMessage;
import com.fanxin.app.domain.InviteMessage.InviteMesageStatus;

public class InviteMessgeDao {
	public static final String TABLE_NAME = "new_friends_msgs";
	public static final String COLUMN_NAME_ID = "id";
	public static final String COLUMN_NAME_FROM = "username";
	public static final String COLUMN_NAME_GROUP_ID = "groupid";
	public static final String COLUMN_NAME_GROUP_Name = "groupname";
	
	public static final String COLUMN_NAME_TIME = "time";
	public static final String COLUMN_NAME_REASON = "reason";
	public static final String COLUMN_NAME_STATUS = "status";
	public static final String COLUMN_NAME_ISINVITEFROMME = "isInviteFromMe";
	
	private DbOpenHelper dbHelper;
	
	public InviteMessgeDao(Context context){
		dbHelper = DbOpenHelper.getInstance(context);
	}
	
	/**
	 * 保存message
	 * @param message
	 * @return  返回这条messaged在db中的id
	 */
	public synchronized Integer saveMessage(InviteMessage message){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int id = -1;
		if(db.isOpen()){
			ContentValues values = new ContentValues();
			values.put(COLUMN_NAME_FROM, message.getFrom());
			values.put(COLUMN_NAME_GROUP_ID, message.getGroupId());
			values.put(COLUMN_NAME_GROUP_Name, message.getGroupName());
			values.put(COLUMN_NAME_REASON, message.getReason());
			values.put(COLUMN_NAME_TIME, message.getTime());
			values.put(COLUMN_NAME_STATUS, message.getStatus().ordinal());
			db.insert(TABLE_NAME, null, values);
			
			Cursor cursor = db.rawQuery("select last_insert_rowid() from " + TABLE_NAME,null); 
            if(cursor.moveToFirst()){
                id = cursor.getInt(0);
            }
            
            cursor.close();
		}
		return id;
	}
	
	/**
	 * 更新message
	 * @param msgId
	 * @param values
	 */
	public void updateMessage(int msgId,ContentValues values){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.update(TABLE_NAME, values, COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(msgId)});
		}
	}
	
	/**
	 * 获取messges
	 * @return
	 */
	public List<InviteMessage> getMessagesList(){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<InviteMessage> msgs = new ArrayList<InviteMessage>();
		if(db.isOpen()){
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " desc",null);
			while(cursor.moveToNext()){
				InviteMessage msg = new InviteMessage();
				int id = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_ID));
				String from = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_FROM));
				String groupid = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_GROUP_ID));
				String groupname = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_GROUP_Name));
				String reason = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_REASON));
				long time = cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_TIME));
				int status = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_STATUS));
				
				msg.setId(id);
				msg.setFrom(from);
				msg.setGroupId(groupid);
				msg.setGroupName(groupname);
				msg.setReason(reason);
				msg.setTime(time);
				if(status == InviteMesageStatus.BEINVITEED.ordinal())
					msg.setStatus(InviteMesageStatus.BEINVITEED);
				else if(status == InviteMesageStatus.BEAGREED.ordinal())
					msg.setStatus(InviteMesageStatus.BEAGREED);
				else if(status == InviteMesageStatus.BEREFUSED.ordinal())
					msg.setStatus(InviteMesageStatus.BEREFUSED);
				else if(status == InviteMesageStatus.AGREED.ordinal())
					msg.setStatus(InviteMesageStatus.AGREED);
				else if(status == InviteMesageStatus.REFUSED.ordinal())
					msg.setStatus(InviteMesageStatus.REFUSED);
				else if(status == InviteMesageStatus.BEAPPLYED.ordinal()){
					msg.setStatus(InviteMesageStatus.BEAPPLYED);
				}
				msgs.add(msg);
			}
			cursor.close();
		}
		return msgs;
	}
	
	public void deleteMessage(String from){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			db.delete(TABLE_NAME, COLUMN_NAME_FROM + " = ?", new String[]{from});
		}
	}
	public void deleteALLMessage(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db.isOpen()){
        db.execSQL("DELETE FROM "+TABLE_NAME);
        }
        if(db!=null){
            db.close();
        }
    }
}
