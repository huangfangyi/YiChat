package com.htmessage.yichat.acitivity.chat.card;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.update.data.UserManager;
import com.htmessage.yichat.domain.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 项目名称：YiChatDemoTwo
 * 类描述：CheckCardPresenter 描述:
 * 创建人：songlijie
 * 创建时间：2017/11/17 11:58
 * 邮箱:814326663@qq.com
 */
public class CheckCardPresenter implements CheckCardBasePresenter {
    private CheckCardView checkCardView;
    private List<User> userList=new ArrayList<>();


    public CheckCardPresenter(CheckCardView checkCardView) {
        this.checkCardView = checkCardView;
        this.checkCardView.setPresenter(this);
     }

    @Override
    public List<User> getContents() {
        JSONArray jsonArray = UserManager.get().getMyFrindsJsonArray();
        if (jsonArray != null) {
            List<User> users = new ArrayList<User>();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject friend = jsonArray.getJSONObject(i);
                 users.add(new User(friend));
            }
            userList.clear();
            userList.addAll(users);
        }

        Collections.sort(userList, new PinyinComparator() {});
        return userList;
    }

    @Override
    public void destory() {
        checkCardView = null;
    }

    @Override
    public void start() {

    }

    private class PinyinComparator implements Comparator<User> {
        @Override
        public int compare(User o1, User o2) {
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
}
