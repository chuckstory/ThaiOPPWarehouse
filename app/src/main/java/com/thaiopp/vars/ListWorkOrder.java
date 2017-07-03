package com.thaiopp.vars;

/**
 * Created by sutt on 2/12/2016.
 */

public class ListWorkOrder {
    public String stockSeq;
    public String transId;
    public String transTypeId;
    public String transType;
    public String transDate;
    public String poId;
    public int allJob;
    public int finJob;

    public ListWorkOrder(String iStockSeq, String iTransId, String iTransTypeId, String iTransType, String iTransDate,String iPoId, int iAllJob, int iFinJob) {
        stockSeq = iStockSeq;
        transId = iTransId;
        transTypeId = iTransTypeId;
        transType = iTransType;
        transDate = iTransDate;
        poId = iPoId;
        allJob = iAllJob;
        finJob = iFinJob;
    }

}
