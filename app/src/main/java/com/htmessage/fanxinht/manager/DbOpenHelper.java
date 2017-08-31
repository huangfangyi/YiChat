/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.htmessage.fanxinht.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.domain.InviteMessgeDao;
import com.htmessage.fanxinht.domain.MomentsMessageDao;
import com.htmessage.fanxinht.domain.UserDao;

public class DbOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static DbOpenHelper instance;


    private static final String USERNAME_TABLE_CREATE = "CREATE TABLE "
            + UserDao.TABLE_NAME + " ("
            + UserDao.COLUMN_NAME_NICK + " TEXT, "
            + UserDao.COLUMN_NAME_AVATAR + " TEXT, "
            + UserDao.COLUMN_NAME_INFO+ " TEXT, "
            + UserDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";

    private static final String INIVTE_MESSAGE_TABLE_CREATE = "CREATE TABLE "
            + InviteMessgeDao.TABLE_NAME + " ("
            + InviteMessgeDao.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + InviteMessgeDao.COLUMN_NAME_FROM + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_GROUP_ID + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_GROUP_Name + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_REASON + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_STATUS + " INTEGER, "
            + InviteMessgeDao.COLUMN_NAME_ISINVITEFROMME + " INTEGER, "
            + InviteMessgeDao.COLUMN_NAME_UNREAD_MSG_COUNT + " INTEGER, "
            + InviteMessgeDao.COLUMN_NAME_TIME + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_GROUPINVITER + " TEXT); ";
    private static final String MOMENTS_TABLE_CREATE = "CREATE TABLE "
            + MomentsMessageDao.TABLE_NAME + " ("
            + MomentsMessageDao.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,  "
            + MomentsMessageDao.COLUMN_NAME_AVATAR + " TEXT, "
            + MomentsMessageDao.COLUMN_NAME_USERID+ " TEXT, "
            + MomentsMessageDao.COLUMN_NAME_USERNICK+ " TEXT, "
            + MomentsMessageDao.COLUMN_NAME_TIME+ " TEXT, "
            + MomentsMessageDao.COLUMN_NAME_CONTENT+ " TEXT, "
            + MomentsMessageDao.COLUMN_NAME_TYPE+ " INTEGER, "
            + MomentsMessageDao.COLUMN_NAME_STATUS+ " INTEGER, "
            + MomentsMessageDao.COLUMN_NAME_IMAGEURL+ " TEXT, "
            + MomentsMessageDao.COLUMN_NAME_MOMENTS_ID + " TEXT); ";

    private DbOpenHelper(Context context) {

        super(context, getUserDatabaseName(), null, DATABASE_VERSION);
         Log.d("SDKDbOpenHelper----->",getUserDatabaseName());
    }

    public static DbOpenHelper getInstance(Context context) {
         if (instance == null) {
             instance = new DbOpenHelper(context.getApplicationContext());
        }
        return instance;
    }

    private static String getUserDatabaseName() {
        return HTApp.getInstance().getUsername()+ "_app.db";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USERNAME_TABLE_CREATE);
        db.execSQL(INIVTE_MESSAGE_TABLE_CREATE);
        db.execSQL(MOMENTS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void closeDB() {
        if (instance != null) {
            try {
                SQLiteDatabase db = instance.getWritableDatabase();
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            instance = null;
        }
    }

}
