package com.thaiopp.vars;

public class Transaction {
    public String transId;
    public String transDate;
    public String transType;
    public String matCode;
    public String matName;
    public String matGrade;
    public String matQty;
    public String matWeight;
    public String lotId;
    public String locId;
    public String barcode;
    public String stockSeq;
    public String stockNo;
    public String chkReserve;


    public Transaction(String tTransId, String tTransDate, String tTransType, String tMatCode, String tMatName,
                       String tMatGrade, String tMatQty, String tMatWeight, String tLotId, String tLocId, String tBarcode, String tStockSeq, String tStockNo, String tChkReserve) {
        transId = tTransId;
        transDate = tTransDate;
        transType = tTransType;
        matCode = tMatCode;
        matName = tMatName;
        matGrade = tMatGrade;
        matQty = tMatQty;
        matWeight = tMatWeight;
        lotId = tLotId;
        locId = tLocId;
        barcode = tBarcode;
        stockSeq = tStockSeq;
        stockNo = tStockNo;
        chkReserve = tChkReserve;
    }
}