package com.thaiopp.vars;

/**
 * Created by sutt on 2/12/2016.
 */

public class StoreItem {
    public String itemId;
    public String tranId;
    public String itemType;
    public String itemDetail;
    public String itemLocation;
    public String itemPallet;
    public String itemBarcode;
    public String stockSeq;
    public String stockNo;
    public String matCode;
    public String grade;
    public String lotId;
    public int itemQty;

    public StoreItem(String iId,String iTran, String iType, String iDetail,  String iLocation, String iPallet, int iQty,  String iBarcode, String iStockSeq, String iStockNo, String iMatCode, String iGrade, String iLotId) {
        itemId = iId;
        tranId = iTran;
        itemType = iType;
        itemDetail = iDetail;
        itemLocation = iLocation;
        itemPallet = iPallet;
        itemQty = iQty;
        itemBarcode = iBarcode;
        stockSeq = iStockSeq;
        stockNo = iStockNo;
        matCode = iMatCode;
        grade = iGrade;
        lotId = iLotId;
    }

}
