package com.htmessage.sdk.model;

/**
 * Created by huangfangyi on 2016/12/14.
 * qq 84543217
 */

public class CurrentUser{
    private String password;
    private String username;

    public void setUsername(String username){
        this.username=username;

    }

    public void setPassword(String password){
        this.password=password;
    }

    public String getPassword(){
        return password;
    }
    public String getUsername(){

        return  username;
    }
}