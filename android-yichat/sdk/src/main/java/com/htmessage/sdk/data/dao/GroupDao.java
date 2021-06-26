package com.htmessage.sdk.data.dao;

import android.content.Context;

import com.htmessage.sdk.manager.HTDatabaseManager;
import com.htmessage.sdk.model.HTGroup;

import java.util.List;
import java.util.Map;

/**
 * Created by huangfangyi on 2016/12/22.
 * qq 84543217
 */

public class GroupDao {

    public  static final String TABLE_NAME = "ht_group";
    public  static final String COLUMN_NAME_ID = "groupId";
    public static final String COLUMN_NAME_NAME= "groupName";
    public static final String COLUMN_NAME_DESC = "desc";
    public static final String COLUMN_NAME_OWNER = "owner";
    public static final String COLUMN_NAME_TIME = "time";
    public GroupDao(Context context) {

    }

    /**
     * save message
     *
     * @param
     * @return return cursor of the message
     */
    public void saveGroupList(List<HTGroup > htGroupList) {
        HTDatabaseManager.getInstance().saveGroupList(htGroupList);
    }



    /**
     * save message
     *
     * @param
     * @return return cursor of the message
     */
    public void saveGroup(HTGroup htGroup) {
        HTDatabaseManager.getInstance().saveGroup(htGroup);
    }



    public Map<String, HTGroup> getAllGroups() {
        return HTDatabaseManager.getInstance().getAllGroups();
    }

    public void deleteGroup(String groupId){
        HTDatabaseManager.getInstance().deleteGroup(groupId);

    }


}
