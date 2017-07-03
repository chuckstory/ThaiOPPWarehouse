package com.thaiopp.vars;

import java.text.DecimalFormat;

public class ListItemDetail {

    public String itemId;
    public String itemType;
    public String itemDetail;
    public String itemLocation;
    public String itemQty;
    public String itemWeight;
    public int itemAlert;
    public int itemAll;
    public int itemFin;
    public String itemBarcode;
    public String stockSeq;
    public String stockNo;
    public String matCode;
    public String matName;
    public String grade;
    public String lotId;



    public ListItemDetail(String iId,String iType, String iDetail,  String iLocation, int iQty, String uQty,  double iWgt, String uWgt,
                          int iAlert, int iFin, String iBarcode, String iStockSeq, String iStockNo, String iMatCode, String iMatName, String iGrade, String iLotId) {
        itemId = iId;
        itemType = iType;
        itemDetail = iDetail;
        itemLocation = iLocation;
        //itemWeight = new DecimalFormat("#.##").format(iWgt) + " " + uWgt;
        //itemQty = String.valueOf(iQty) + " " + uQty;
        itemWeight = new DecimalFormat("#.##").format(iWgt);
        itemQty = String.valueOf(iQty);
        itemAll = iQty;
        itemFin = iFin;
        itemAlert = iAlert;
        itemBarcode = iBarcode;
        stockSeq = iStockSeq;
        stockNo = iStockNo;
        matCode = iMatCode;
        matName = iMatName;
        grade = iGrade;
        lotId = iLotId;

    }

    public String GetItemStatus() {
        return String.valueOf(itemFin) + "/" + String.valueOf(itemAll);
    }

    public Boolean Finished() {
        boolean finished;

        if (itemFin < itemAll) {
            finished = false;
        } else {
            finished = true;
        }
        return finished;
    }

}
