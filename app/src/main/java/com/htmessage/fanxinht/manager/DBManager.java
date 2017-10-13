package com.htmessage.fanxinht.manager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.domain.InviteMessage;
import com.htmessage.fanxinht.domain.InviteMessage.Status;
import com.htmessage.fanxinht.domain.InviteMessgeDao;
import com.htmessage.fanxinht.domain.MomentsMessage;
import com.htmessage.fanxinht.domain.MomentsMessageDao;
import com.htmessage.fanxinht.domain.User;
import com.htmessage.fanxinht.domain.UserDao;
import com.htmessage.fanxinht.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class DBManager {
    static private DBManager dbMgr = new DBManager();
    private DbOpenHelper dbHelper;

    private DBManager() {
        dbHelper = DbOpenHelper.getInstance(HTApp.getInstance().getApplicationContext());
    }

    public static synchronized DBManager getInstance() {
        if (dbMgr == null) {
            dbMgr = new DBManager();
        }
        return dbMgr;
    }


    /**
     * save contact list
     *
     * @param contactList
     */
    synchronized public void saveContactList(List<User> contactList) {
        Log.d("saveContactList3----->", contactList.size() + "");
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(UserDao.TABLE_NAME, null, null);
            for (User user : contactList) {
                ContentValues values = new ContentValues();
                values.put(UserDao.COLUMN_NAME_ID, user.getUsername());
                if (user.getNick() != null)
                    values.put(UserDao.COLUMN_NAME_NICK, user.getNick());
                if (user.getAvatar() != null)
                    values.put(UserDao.COLUMN_NAME_AVATAR, user.getAvatar());
                if (user.getUserInfo() != null)
                    values.put(UserDao.COLUMN_NAME_INFO, user.getUserInfo());
                db.replace(UserDao.TABLE_NAME, null, values);
            }
        }
    }


    /**
     * get contact list
     *
     * @return
     */
    synchronized public Map<String, User> getContactList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<String, User> users = new Hashtable<String, User>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + UserDao.TABLE_NAME /* + " desc" */, null);
            while (cursor.moveToNext()) {
                String username = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_ID));
                String nick = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_NICK));
                String avatar = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_AVATAR));
                String userInfo = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_INFO));
                User user = new User(username);
                user.setNick(nick);
                user.setAvatar(avatar);
                user.setUserInfo(userInfo);
                CommonUtils.setUserInitialLetter(user);
                if ("123456789".contains(user.getInitialLetter())) {
                    user.setInitialLetter("#");
                }
                users.put(username, user);
            }
            cursor.close();
        }
        return users;
    }

    /**
     * delete a contact
     *
     * @param username
     */
    synchronized public void deleteContact(String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(UserDao.TABLE_NAME, UserDao.COLUMN_NAME_ID + " = ?", new String[]{username});
        }
    }

    /**
     * save a contact
     *
     * @param user
     */
    synchronized public void saveContact(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDao.COLUMN_NAME_ID, user.getUsername());
        if (user.getNick() != null)
            values.put(UserDao.COLUMN_NAME_NICK, user.getNick());
        if (user.getAvatar() != null)
            values.put(UserDao.COLUMN_NAME_AVATAR, user.getAvatar());
        if (user.getUserInfo() != null)
            values.put(UserDao.COLUMN_NAME_INFO, user.getUserInfo());
        if (db.isOpen()) {
            db.replace(UserDao.TABLE_NAME, null, values);
        }
    }

    public void setDisabledGroups(List<String> groups) {
        setList(UserDao.COLUMN_NAME_DISABLED_GROUPS, groups);
    }

    public List<String> getDisabledGroups() {
        return getList(UserDao.COLUMN_NAME_DISABLED_GROUPS);
    }

    public void setDisabledIds(List<String> ids) {
        setList(UserDao.COLUMN_NAME_DISABLED_IDS, ids);
    }

    public List<String> getDisabledIds() {
        return getList(UserDao.COLUMN_NAME_DISABLED_IDS);
    }

    synchronized private void setList(String column, List<String> strList) {
        StringBuilder strBuilder = new StringBuilder();

        for (String hxid : strList) {
            strBuilder.append(hxid).append("$");
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(column, strBuilder.toString());

            db.update(UserDao.PREF_TABLE_NAME, values, null, null);
        }
    }

    synchronized private List<String> getList(String column) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + column + " from " + UserDao.PREF_TABLE_NAME, null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        String strVal = cursor.getString(0);
        if (strVal == null || strVal.equals("")) {
            return null;
        }

        cursor.close();

        String[] array = strVal.split("$");

        if (array != null && array.length > 0) {
            List<String> list = new ArrayList<String>();
            for (String str : array) {
                list.add(str);
            }

            return list;
        }

        return null;
    }

    /**
     * save a message
     *
     * @param message
     * @return return cursor of the message
     */
    public synchronized Integer saveMessage(InviteMessage message) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int id = -1;
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(InviteMessgeDao.COLUMN_NAME_FROM, message.getFrom());

            values.put(InviteMessgeDao.COLUMN_NAME_REASON, message.getReason());
            values.put(InviteMessgeDao.COLUMN_NAME_TIME, message.getTime());
            values.put(InviteMessgeDao.COLUMN_NAME_STATUS, message.getStatus().ordinal());
             db.insert(InviteMessgeDao.TABLE_NAME, null, values);

            Cursor cursor = db.rawQuery("select last_insert_rowid() from " + InviteMessgeDao.TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                id = cursor.getInt(0);
            }

            cursor.close();
        }
        return id;
    }


    /**
     * update message
     *
     * @param msgId
     * @param values
     */
    synchronized public void updateMessage(int msgId, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.update(InviteMessgeDao.TABLE_NAME, values, InviteMessgeDao.COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(msgId)});
        }
    }

    /**
     * get messges
     *
     * @return
     */
    synchronized public List<InviteMessage> getMessagesList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<InviteMessage> msgs = new ArrayList<InviteMessage>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + InviteMessgeDao.TABLE_NAME + " desc", null);
            while (cursor.moveToNext()) {
                InviteMessage msg = new InviteMessage();
                int id = cursor.getInt(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_ID));
                String from = cursor.getString(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_FROM));
                String groupid = cursor.getString(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_GROUP_ID));
                String groupname = cursor.getString(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_GROUP_Name));
                String reason = cursor.getString(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_REASON));
                long time = cursor.getLong(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_TIME));
                int status = cursor.getInt(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_STATUS));
                String groupInviter = cursor.getString(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_GROUPINVITER));

                msg.setId(id);
                msg.setFrom(from);
                msg.setReason(reason);
                msg.setTime(time);


                if (status == Status.BEINVITEED.ordinal())
                    msg.setStatus(Status.BEINVITEED);
                else if (status == Status.BEAGREED.ordinal())
                    msg.setStatus(Status.BEAGREED);
                else if (status == Status.BEREFUSED.ordinal())
                    msg.setStatus(Status.BEREFUSED);
                else if (status == Status.AGREED.ordinal())
                    msg.setStatus(Status.AGREED);
                else if (status == Status.REFUSED.ordinal())
                    msg.setStatus(Status.REFUSED);

                msgs.add(msg);
            }
            cursor.close();
        }
        return msgs;
    }

    /**
     * delete invitation message
     *
     * @param from
     */
    synchronized public void deleteMessage(String from) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(InviteMessgeDao.TABLE_NAME, InviteMessgeDao.COLUMN_NAME_FROM + " = ?", new String[]{from});
        }
    }

    public synchronized int getUnreadNotifyCount() {
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select " + InviteMessgeDao.COLUMN_NAME_UNREAD_MSG_COUNT + " from " + InviteMessgeDao.TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    public synchronized void setUnreadNotifyCount(int count) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(InviteMessgeDao.COLUMN_NAME_UNREAD_MSG_COUNT, count);

            db.update(InviteMessgeDao.TABLE_NAME, values, null, null);

        }
    }


    synchronized public void closeDB() {
        if (dbHelper != null) {
            dbHelper.closeDB();
        }
        dbMgr = null;
    }



    public synchronized void saveMomentsNotice(MomentsMessage momentsMessage) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            boolean notice = checkMomentsNotice(db,momentsMessage);
            values.put(MomentsMessageDao.COLUMN_NAME_USERID, momentsMessage.getUserId());
            values.put(MomentsMessageDao.COLUMN_NAME_USERNICK, momentsMessage.getUserNick());
            values.put(MomentsMessageDao.COLUMN_NAME_AVATAR, momentsMessage.getUserAvatar());
            values.put(MomentsMessageDao.COLUMN_NAME_CONTENT, momentsMessage.getContent());
            values.put(MomentsMessageDao.COLUMN_NAME_IMAGEURL, momentsMessage.getImageUrl());
            values.put(MomentsMessageDao.COLUMN_NAME_TIME, momentsMessage.getTime());
            values.put(MomentsMessageDao.COLUMN_NAME_TYPE, momentsMessage.getType().ordinal());
            values.put(MomentsMessageDao.COLUMN_NAME_STATUS, momentsMessage.getStatus().ordinal());
            values.put(MomentsMessageDao.COLUMN_NAME_MOMENTS_ID, momentsMessage.getMid());
            if (notice){
                db.update(MomentsMessageDao.TABLE_NAME,values,MomentsMessageDao.COLUMN_NAME_TIME +" = ?",new String[]{momentsMessage.getTime()+""});
            }else{
                db.insert(MomentsMessageDao.TABLE_NAME, null, values);
            }
        }
    }


    public synchronized List<MomentsMessage> getMomentsMessageList() {
        List<MomentsMessage> momentsMessages = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + MomentsMessageDao.TABLE_NAME + " order by "+MomentsMessageDao.COLUMN_NAME_TIME+" desc", null);
            while (cursor.moveToNext()) {
                MomentsMessage momentsMessage = new MomentsMessage();
                int id = cursor.getInt(cursor.getColumnIndex(MomentsMessageDao.COLUMN_NAME_ID));
                String userId = cursor.getString(cursor.getColumnIndex(MomentsMessageDao.COLUMN_NAME_USERID));
                String userNick = cursor.getString(cursor.getColumnIndex(MomentsMessageDao.COLUMN_NAME_USERNICK));
                String userAvatar = cursor.getString(cursor.getColumnIndex(MomentsMessageDao.COLUMN_NAME_AVATAR));
                String momentsId = cursor.getString(cursor.getColumnIndex(MomentsMessageDao.COLUMN_NAME_MOMENTS_ID));
                long time = cursor.getLong(cursor.getColumnIndex(MomentsMessageDao.COLUMN_NAME_TIME));
                int status = cursor.getInt(cursor.getColumnIndex(MomentsMessageDao.COLUMN_NAME_STATUS));
                int type = cursor.getInt(cursor.getColumnIndex(MomentsMessageDao.COLUMN_NAME_TYPE));
                String content = cursor.getString(cursor.getColumnIndex(MomentsMessageDao.COLUMN_NAME_CONTENT));
                String imageUrl = cursor.getString(cursor.getColumnIndex(MomentsMessageDao.COLUMN_NAME_IMAGEURL));
                momentsMessage.setContent(content);
                momentsMessage.setId(id);
                momentsMessage.setImageUrl(imageUrl);
                momentsMessage.setMid(momentsId);
                momentsMessage.setUserAvatar(userAvatar);
                momentsMessage.setUserId(userId);
                momentsMessage.setUserNick(userNick);
                momentsMessage.setTime(time);

                if (status == MomentsMessage.Status.READ.ordinal())
                    momentsMessage.setStatus(MomentsMessage.Status.READ);
                else if (status == MomentsMessage.Status.UNREAD.ordinal())
                    momentsMessage.setStatus(MomentsMessage.Status.UNREAD);

                if (type == MomentsMessage.Type.GOOD.ordinal())
                    momentsMessage.setType(MomentsMessage.Type.GOOD);
                else if (type == MomentsMessage.Type.COMMENT.ordinal())
                    momentsMessage.setType(MomentsMessage.Type.COMMENT);
                else if (type == MomentsMessage.Type.REPLY_COMMENT.ordinal())
                    momentsMessage.setType(MomentsMessage.Type.REPLY_COMMENT);

                momentsMessages.add(momentsMessage);

            }
            cursor.close();
        }


        return momentsMessages;

    }


    public synchronized void clearMomentsUnReadCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(MomentsMessageDao.COLUMN_NAME_STATUS, MomentsMessage.Status.READ.ordinal());
            db.update(MomentsMessageDao.TABLE_NAME, values, MomentsMessageDao.COLUMN_NAME_STATUS + " = ?", new String[]{MomentsMessage.Status.UNREAD.ordinal()+""});
        }
    }

    public synchronized MomentsMessage getLastMomentsMessage() {
        MomentsMessage momentsMessage = null;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + MomentsMessageDao.TABLE_NAME + " order by "+MomentsMessageDao.COLUMN_NAME_ID +" desc limit 1", null);
            while (cursor.moveToNext()) {
                momentsMessage = new MomentsMessage();
                int id = cursor.getInt(cursor.getColumnIndex(MomentsMessageDao.COLUMN_NAME_ID));
                String userId = cursor.getString(cursor.getColumnIndex(MomentsMessageDao.COLUMN_NAME_USERID));
                String userNick = cursor.getString(cursor.getColumnIndex(MomentsMessageDao.COLUMN_NAME_USERNICK));
                String userAvatar = cursor.getString(cursor.getColumnIndex(MomentsMessageDao.COLUMN_NAME_AVATAR));
                String momentsId = cursor.getString(cursor.getColumnIndex(MomentsMessageDao.COLUMN_NAME_MOMENTS_ID));
                long time = cursor.getLong(cursor.getColumnIndex(MomentsMessageDao.COLUMN_NAME_TIME));
                int status = cursor.getInt(cursor.getColumnIndex(MomentsMessageDao.COLUMN_NAME_STATUS));
                int type = cursor.getInt(cursor.getColumnIndex(MomentsMessageDao.COLUMN_NAME_TYPE));
                String content = cursor.getString(cursor.getColumnIndex(MomentsMessageDao.COLUMN_NAME_CONTENT));
                String imageUrl = cursor.getString(cursor.getColumnIndex(MomentsMessageDao.COLUMN_NAME_IMAGEURL));
                momentsMessage.setContent(content);
                momentsMessage.setId(id);
                momentsMessage.setImageUrl(imageUrl);
                momentsMessage.setMid(momentsId);
                momentsMessage.setUserAvatar(userAvatar);
                momentsMessage.setUserId(userId);
                momentsMessage.setUserNick(userNick);
                momentsMessage.setTime(time);

                if (status == MomentsMessage.Status.READ.ordinal())
                    momentsMessage.setStatus(MomentsMessage.Status.READ);
                else if (status == MomentsMessage.Status.UNREAD.ordinal())
                    momentsMessage.setStatus(MomentsMessage.Status.UNREAD);

                if (type == MomentsMessage.Type.GOOD.ordinal())
                    momentsMessage.setType(MomentsMessage.Type.GOOD);
                else if (type == MomentsMessage.Type.COMMENT.ordinal())
                    momentsMessage.setType(MomentsMessage.Type.COMMENT);
                else if (type == MomentsMessage.Type.REPLY_COMMENT.ordinal())
                    momentsMessage.setType(MomentsMessage.Type.REPLY_COMMENT);
                cursor.close();
                return momentsMessage;


            }
            cursor.close();
        }
        return momentsMessage;
    }

    public synchronized void  deleteAllMomentsMessage() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        if (db.isOpen()) {

            db.delete(MomentsMessageDao.TABLE_NAME,null,null);

        }

    }

    public synchronized int getMomentsUnReadCount() {
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + MomentsMessageDao.TABLE_NAME + " desc where "+MomentsMessageDao.COLUMN_NAME_STATUS+" = ?", new String[]{MomentsMessage.Status.UNREAD.ordinal()+""});
            while (cursor.moveToNext()) {
                count++;
            }
            cursor.close();
        }
        return count;
    }


    public synchronized boolean checkMomentsNotice(SQLiteDatabase db,MomentsMessage momentsMessage) {
        String id = momentsMessage.getUserId();
        String mid = momentsMessage.getMid();
        String sql = "select * from "+MomentsMessageDao.TABLE_NAME+" where "+MomentsMessageDao.COLUMN_NAME_USERID+" = "+id
                + " and "+MomentsMessageDao.COLUMN_NAME_TYPE+" = "+ MomentsMessage.Type.GOOD.ordinal()+" and "+MomentsMessageDao.COLUMN_NAME_MOMENTS_ID+" = " +mid;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()){
            String userId = cursor.getString(cursor.getColumnIndex(MomentsMessageDao.COLUMN_NAME_USERID));
            String momentsId = cursor.getString(cursor.getColumnIndex(MomentsMessageDao.COLUMN_NAME_MOMENTS_ID));
            if (id.equals(userId) && mid.equals(momentsId) && momentsMessage.getType().ordinal() ==MomentsMessage.Type.GOOD.ordinal()){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }



}
