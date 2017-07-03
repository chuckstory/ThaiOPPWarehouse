package com.thaiopp.utils;


import android.app.Application;

import com.thaiopp.vars.ConfigSetting;
import com.thaiopp.vars.Transaction;
import com.thaiopp.vars.UserInfo;

import java.util.List;

public class GlobalVar extends Application {

    private static GlobalVar instance;

    private List<String> listBranch;
    private List<Transaction> listIssueTrans;
    private List<Transaction> listPickTrans;
    private UserInfo userInfo;
    private ConfigSetting configSetting;

    // *** set globalvar instance

    static {
        instance = new GlobalVar();
    }

    private GlobalVar() {

    }

    public static GlobalVar getInstance() {
        return GlobalVar.instance;
    }

    //***************************

    public void setListBranch(List<String> ls) {
        listBranch = ls;
    }

    public List<String> getListBranch() {
        return listBranch;
    }

    public void setListIssueTrans(List<Transaction> ls) {
        listIssueTrans = ls;
    }

    public List<Transaction> getListIssueTrans() {
        return listIssueTrans;
    }

    public void setListPickTrans(List<Transaction> ls) {
        listPickTrans = ls;
    }

    public List<Transaction> getListPickTrans() {
        return listPickTrans;
    }

    public void setUserInfo(UserInfo uInfo) {
        userInfo = uInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setConfigSetting(ConfigSetting sVal) {
        configSetting = sVal;
    }

    public ConfigSetting getConfigSetting() {
        return configSetting;
    }
}
