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
package com.htmessage.sdk.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.htmessage.sdk.data.dao.GroupDao;
import com.htmessage.sdk.data.dao.ConversationDao;

import com.htmessage.sdk.data.dao.MessageDao;
import com.htmessage.sdk.manager.HTPreferenceManager;


public class SDKDbOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static SDKDbOpenHelper instance;
    private static final String CONVERSATION_TABLE_CREATE = "CREATE TABLE "
            + ConversationDao.TABLE_NAME + " ("
            + ConversationDao.COLUMN_NAME_MSG_ID + " TEXT, "
            +ConversationDao.COLUMN_NAME_TIME_TOP+" INTEGER default 0 ,"
            + ConversationDao.COLUMN_NAME_TIME + " TEXT, "
            + ConversationDao.COLUMN_NAME_UNREADCOUNT + " INTEGER, "
            + ConversationDao.COLUMN_NAME_CHAT_TYPE + " INTEGER, "
            + ConversationDao.COLUMN_NAME_USER_ID + " TEXT PRIMARY KEY);";

    private static final String GROUP_TABLE_CREATE = "CREATE TABLE "
            + GroupDao.TABLE_NAME + " ("
            + GroupDao.COLUMN_NAME_NAME + " TEXT, "
            + GroupDao.COLUMN_NAME_DESC + " TEXT, "
            + GroupDao.COLUMN_NAME_OWNER + " TEXT, "
            + GroupDao.COLUMN_NAME_TIME + " TEXT, "
            + GroupDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";

    private static final String MESSAGE_TABLE_CREATE = "CREATE TABLE "
            + MessageDao.TABLE_NAME + " ("

            + MessageDao.COLUMN_NAME_CHAT_TYPE + " INTEGER, "
            + MessageDao.COLUMN_NAME_BODY + " TEXT, "
            + MessageDao.COLUMN_NAME_DIRECT + " INTEGER, "
            + MessageDao.COLUMN_NAME_MSG_TIME + " TEXT, "
            + MessageDao.COLUMN_NAME_FROM + " TEXT, "
            + MessageDao.COLUMN_NAME_TO + " TEXT, "
            + MessageDao.COLUMN_NAME_lOCAL_TIME + " TEXT, "
            + MessageDao.COLUMN_NAME_STATUS + " INTEGER, "
            + MessageDao.COLUMN_NAME_TYPE + " INTEGER, "
            + MessageDao.COLUMN_NAME_ATTRIBUTE + " TEXT, "
            + MessageDao.COLUMN_NAME_MSG_ID + " TEXT PRIMARY KEY);";



    private SDKDbOpenHelper(Context context) {

        super(context, getUserDatabaseName(), null, DATABASE_VERSION);
         Log.d("DBManager6----->",getUserDatabaseName());
    }

    public static SDKDbOpenHelper getInstance(Context context) {
      //   if (instance == null) {
        instance = new SDKDbOpenHelper(context);
       // }
        return instance;
    }

    private static String getUserDatabaseName() {


        return HTPreferenceManager.getInstance().getUser().getUsername()+ "_sdk.db";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

         db.execSQL(MESSAGE_TABLE_CREATE);
        db.execSQL(CONVERSATION_TABLE_CREATE);
        db.execSQL(GROUP_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //给CONVERSATION_TABLE 增加一个topTimestamp;
               if(newVersion>2){
                   db.execSQL("alter table "+ConversationDao.TABLE_NAME+" add "+ConversationDao.COLUMN_NAME_TIME_TOP+" INTEGER default 0 ");

               }
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
