package com.fanxin.huangfangyi.main.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fanxin.huangfangyi.db.DbOpenHelper;

public class TopUserDao {
    public static final String TABLE_NAME = "top_user";
    public static final String COLUMN_NAME_ID = "username";
    public static final String COLUMN_NAME_TIME = "time";
    public static final String COLUMN_NAME_IS_GOUP = "is_group";
    private DbOpenHelper dbHelper;
    public TopUserDao(Context context) {
        dbHelper = DbOpenHelper.getInstance(context);
    }
    /**
     * 保存置顶联系人
     * 
     * @param contactList
     */
    public void saveTopUserList(List<TopUser> contactList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(TABLE_NAME, null, null);
            for (TopUser user : contactList) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_NAME_ID, user.getUserName());

                values.put(COLUMN_NAME_TIME, user.getTime());

                values.put(COLUMN_NAME_IS_GOUP, user.getType());
                db.replace(TABLE_NAME, null, values);
            }
        }
        db.close();
    }

    /**
     * 获取置顶联系人列表
     * 
     * @return
     */
    @SuppressLint("DefaultLocale")
    public Map<String, TopUser> getTopUserList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<String, TopUser> users = new HashMap<String, TopUser>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + TABLE_NAME
                    + " order by time asc ", null);
            while (cursor.moveToNext()) {
                String username = cursor.getString(cursor
                        .getColumnIndex(COLUMN_NAME_ID));
                long time = cursor.getLong(cursor
                        .getColumnIndex(COLUMN_NAME_TIME));
                int is_group = cursor.getInt(cursor
                        .getColumnIndex(COLUMN_NAME_IS_GOUP));
                TopUser user = new TopUser();
                user.setTime(time);
                user.setType(is_group);
                user.setUserName(username);
                users.put(username, user);
            }
            cursor.close();
        }
        db.close();
        return users;
    }

    /**
     * 删除一个置顶
     * 
     * @param username
     */
    public void deleteTopUser(String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(TABLE_NAME, COLUMN_NAME_ID + " = ?",
                    new String[] { username });
        }
        db.close();
    }

    /**
     * 增加一个置顶
     * 
     * @param user
     */
    public void saveTopUser(TopUser user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_ID, user.getUserName());
        values.put(COLUMN_NAME_TIME, user.getTime());
        values.put(COLUMN_NAME_IS_GOUP, user.getType());
        if (db.isOpen()) {
            db.replace(TABLE_NAME, null, values);
        }
        db.close();
    }
}
