package com.thaiopp.vars;

public class ProdRcv {
    public String jobId;
    public String inputType;
    public String barcode;
    public String palletId;
    public String matCode;
    public String grade;
    public String lotId;
    public String rollId;
    public String cpSpec;
    public String crDate;
    public String weight;
    public String gWeight;
    public int qty;
    public boolean check;
    public String locationId;
    public String itemLocation;

    public ProdRcv(String iJobId, String iInputType, String iBarcode, String iPalletId, String iMatCode, String iGrade, String iLotId, String iRollId, String iCpSpec,String iCrDate, String iWeight, String iGWeight, int iQty, boolean iCheck, String iLocationId, String iItemLocation) {
        jobId = iJobId;
        inputType = iBarcode;
        barcode = iInputType;
        palletId = iPalletId;
        matCode = iMatCode;
        grade = iGrade;
        lotId = iLotId;
        rollId = iRollId;
        cpSpec = iCpSpec;
        crDate = iCrDate;
        weight = iWeight;
        matCode = iMatCode;
        gWeight = iGWeight;
        qty = iQty;
        check = iCheck;
        locationId = iLocationId;
        itemLocation = iItemLocation;
    }

}
