package com.thaiopp.vars;

public class ItemStockInfo {
    public String itemId;
    public String itemBarcode;
    public String itemName;
    public String itemType;
    public String itemDetail;
    public String rollInpt;
    public String rollNo;
    public String itemLocation;
    public String itemPallet;
    public int itemQty;
    public int itemCount;
    public String itemWeight;
    public String matCode;
    public String grade;
    public String lotId;
    public String itemStatus;
    public String matCat;
    public String matType;
    public String matFml;
    public String itemThick;
    public String itemWidth;
    public String unitWidth;
    public String itemLength;
    public String unitLength;
    public String core;
    public String joint;
    public String matSpec1;
    public String matSpec2;

    public ItemStockInfo(String iId, String iBarcode, String iName, String iType, String iDetail,String rInpt, String rNo, String iLocation, String iPallet, int iQty, int iCount, String iWeight, String iMatCode, String iGrade, String iLotId,
                         String iStatus, String mCat, String mType, String mFml, String iThick, String iWidth, String uWidth, String iLength, String uLength, String iCore, String iJoint, String mSpec1, String mSpec2) {
        itemId = iId;
        itemBarcode = iBarcode;
        itemName = iName;
        itemType = iType;
        itemDetail = iDetail;
        rollInpt = rInpt;
        rollNo = rNo;
        itemLocation = iLocation;
        itemPallet = iPallet;
        itemQty = iQty;
        itemCount = iCount;
        itemWeight = iWeight;
        matCode = iMatCode;
        grade = iGrade;
        lotId = iLotId;
        itemStatus = iStatus;
        matCat = mCat;
        matType = mType;
        matFml = mFml;
        itemThick = iThick;
        itemWidth = iWidth;
        unitWidth = uWidth;
        itemLength = iLength;
        unitLength = uLength;
        core = iCore;
        joint = iJoint;
        matSpec1 = mSpec1;
        matSpec2 = mSpec2;


    }
}

