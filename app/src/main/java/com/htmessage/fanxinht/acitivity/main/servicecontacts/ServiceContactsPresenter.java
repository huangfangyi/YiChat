package com.htmessage.fanxinht.acitivity.main.servicecontacts;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.fanxinht.HTApp;
import com.htmessage.fanxinht.HTConstant;
import com.htmessage.fanxinht.utils.ACache;
import com.htmessage.fanxinht.utils.OkHttpUtils;
import com.htmessage.fanxinht.utils.Param;
import com.htmessage.sdk.model.HTMessage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 项目名称：PersonalTailor
 * 类描述：ServiceContactsPresenter 描述:
 * 创建人：songlijie
 * 创建时间：2017/8/2 13:03
 * 邮箱:814326663@qq.com
 */
public class ServiceContactsPresenter implements ServiceContactsBasePresenter {
    private ServiceContactsNewView serviceContactsNewView;

    private List<ServiceUser> contacts = new ArrayList<>();
    private String cacheKey = HTApp.getInstance().getUsername() + "service";
    private ACache aCache;

    public ServiceContactsPresenter(ServiceContactsNewView serviceContactsNewView) {
        this.serviceContactsNewView = serviceContactsNewView;
        this.serviceContactsNewView.setPresenter(this);
        aCache = ACache.get(serviceContactsNewView.getBaseActivity());
        getData();
    }

    @Override
    public void onDestory() {
        serviceContactsNewView = null;
    }

    @Override
    public List<ServiceUser> getUserListFormCache() {

        contacts.clear();
        contacts.addAll(sortList(getCacheData()));
        return contacts;
    }

    @Override
    public void start() {

    }


    @Override
    public List<ServiceUser> sortList(List<ServiceUser> users) {
        PinyinComparator comparator = new PinyinComparator();
        Collections.sort(users, comparator);
        return users;
    }

    public class PinyinComparator implements Comparator<ServiceUser> {

        @Override
        public int compare(ServiceUser o1, ServiceUser o2) {
            String py1 = o1.getInitialLetter();
            String py2 = o2.getInitialLetter();

            if (py1.equals(py2)) {
                return o1.getNick().compareTo(o2.getNick());
            } else {
                if ("#".equals(py1)) {
                    return 1;
                } else if ("#".equals(py2)) {
                    return -1;
                }
                return py1.compareTo(py2);
            }

        }
    }

    public void getData() {
        List<Param> params = new ArrayList<>();
        new OkHttpUtils(serviceContactsNewView.getBaseActivity()).post(params, HTConstant.URL_GET_SERVICER_LIST, new OkHttpUtils.HttpCallBack() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                int code = jsonObject.getIntValue("code");
                switch (code) {
                    case 1:
                        JSONArray data = jsonObject.getJSONArray("data");
                        if (data != null && data.size() != 0) {
                            contacts.clear();
                            List<ServiceUser> objList = new ArrayList<>();
                            for (int i = 0; i < data.size(); i++) {
                                JSONObject object = data.getJSONObject(i);
                                ServiceUser user = new ServiceUser();
                                user.setNick(object.getString("usernick"));
                                user.setUsername(object.getString("userId"));
                                user.setAvatar(object.getString("avatar"));
                                objList.add(user);
                            }
                            contacts.addAll(sortList(objList));
                        }
                        serviceContactsNewView.refresh();
                        aCache.put(cacheKey, data);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(String errorMsg) {
                serviceContactsNewView.showToast(errorMsg);
            }
        });
    }

    private List<ServiceUser> getCacheData() {
        List<ServiceUser> groups = new ArrayList<>();
        JSONArray data = aCache.getAsJSONArray(cacheKey);
        if (data != null && data.size() != 0) {
            for (int i = 0; i < data.size(); i++) {
                JSONObject object = data.getJSONObject(i);
                ServiceUser user = new ServiceUser();
                user.setNick(object.getString("usernick"));
                user.setUsername(object.getString("userId"));
                user.setAvatar(object.getString("avatar"));
                groups.add(user);
            }
        }
        return groups;
    }

    public void onNewMessage(HTMessage htMessage) {
        //   收到新消息,收到撤回消息,收到群相关消息-被提出群聊
        List<ServiceUser> serviceUsers = new ArrayList<>();
        for (ServiceUser serviceUser : contacts) {
            serviceUsers.add(serviceUser);
            if (serviceUser.getUsername().equals(htMessage.getUsername())) {
                serviceUser.setShow(true);
                serviceContactsNewView.refresh();
                break;
            }
        }
    }
}
