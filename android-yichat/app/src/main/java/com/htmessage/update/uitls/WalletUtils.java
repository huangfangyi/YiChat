package com.htmessage.update.uitls;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.htmessage.update.data.UserManager;
import com.htmessage.sdk.manager.MmvkManger;



/**
 * Created by huangfangyi on 2019/8/12.
 * qq 84543217
 */
public class WalletUtils  {

    private static WalletUtils walletUtils;

    public static WalletUtils getInstance() {
        if (walletUtils == null) {
            walletUtils = new WalletUtils();
        }
        return walletUtils;
    }


    public void saveBalance(double balance){
        MmvkManger.getIntance().putDouble(UserManager.get().getMyUserId()+"_balance",balance);
    }

    public double getBalance(){
        return  MmvkManger.getIntance().getDouble(UserManager.get().getMyUserId()+"_balance");
    }


    public void saveBankCardList(JSONArray jsonArray){
        MmvkManger.getIntance().putJSONArray(UserManager.get().getMyUserId()+"_bankCardList",jsonArray);
    }

    public JSONArray getBankCardList(){
        return MmvkManger.getIntance().getJSONArray(UserManager.get().getMyUserId()+"_bankCardList");
    }

    public boolean isSetPayPassword(){
        return MmvkManger.getIntance().getBoolean("payPasswordStatus",false);
    }

    public void setPayPassword(boolean isSet){
        MmvkManger.getIntance().putBoolean("payPasswordStatus",isSet);
    }


    public  void setWithDrawConfig(JSONObject data){
        MmvkManger.getIntance().putJSON("_setWithDrawConfig",data);
    }

    public JSONObject getWithDrawConfig(){
        JSONObject jsonObject=   MmvkManger.getIntance().getJSON("_setWithDrawConfig");
        if(jsonObject==null){
            jsonObject=new JSONObject();
            jsonObject.put("minLimit",0);
            jsonObject.put("rate",0);
            jsonObject.put("text","");
        }
        return jsonObject;

    }

}
