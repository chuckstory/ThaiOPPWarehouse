package com.thaiopp.vars;

public class ListSaleOrder {
    public String soId;
    public String soSeq;
    public String seqDeli;
    public String tfLotDocId;
    public String deliDate;

    public ListSaleOrder(String iSoId, String iSoSeq, String iSeqDeli, String iTfLotDecId, String iDeliDate) {
        soId = iSoId;
        soSeq = iSoSeq;
        seqDeli = iSeqDeli;
        tfLotDocId = iTfLotDecId;
        deliDate = iDeliDate;
    }

}

