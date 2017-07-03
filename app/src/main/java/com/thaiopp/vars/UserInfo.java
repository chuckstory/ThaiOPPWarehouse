package com.thaiopp.vars;

public class UserInfo {
    public String userLogin;
    public String userName;
    public String dbsId;
    public String orgId;
    public String branchId;
    public String warehouseId;
    public String status;
    public String loginStat;
    public String imei;

    public UserInfo(String uLogin, String uName, String uDbsId, String uOrgId, String uBranchId, String uWarehouseId, String uStatus, String uLoginStat, String uImei) {
        userLogin = uLogin;
        userName = uName;
        dbsId = uDbsId;
        orgId = uOrgId;
        branchId = uBranchId;
        warehouseId = uWarehouseId;
        status = uStatus;
        loginStat = uLoginStat;
        imei = uImei;
    }

}
