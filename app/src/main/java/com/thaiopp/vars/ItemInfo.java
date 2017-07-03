package com.thaiopp.vars;

public class ItemInfo {
    public String itemId;
    public String itemBarcode;
    public String itemName;
    public String itemType;
    public String itemDetail;
    public String itemDetail2;
    public String itemLocation;
    public String itemPallet;
    public int itemQty;
    public int itemCount;
    public String itemWeight;
    public String matCode;
    public String grade;
    public String lotId;
    public String rollNo;

    public ItemInfo(String iId, String iBarcode, String iName, String iType, String iDetail,String iDetail2, String iLocation, String iPallet, int iQty, int iCount, String iWeight, String iMatCode, String iGrade, String iLotId, String iRollNo) {
        itemId = iId;
        itemBarcode = iBarcode;
        itemName = iName;
        itemType = iType;
        itemDetail = iDetail;
        itemDetail2 = iDetail2;
        itemLocation = iLocation;
        itemPallet = iPallet;
        itemQty = iQty;
        itemCount = iCount;
        itemWeight = iWeight;
        matCode = iMatCode;
        grade = iGrade;
        lotId = iLotId;
        rollNo = iRollNo;
    }

}
