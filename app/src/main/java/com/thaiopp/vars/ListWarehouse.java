package com.thaiopp.vars;

public class ListWarehouse {
    public String warehouseId;
    public String warehouseDesc;
    public String warehouseShortName;
    public String warehouseType;
    public String warehouseBranch;

    public ListWarehouse(String wId, String wDesc, String wShortName, String wType, String wBranch) {
        warehouseId = wId;
        warehouseDesc = wDesc;
        warehouseShortName = wShortName;
        warehouseType = wType;
        warehouseBranch = wBranch;
    }

}
