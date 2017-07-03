package com.thaiopp.vars;

/**
 * Created by sutt on 2/28/2016.
 */
public class DocInfo {
    public String docId;
    public String docDate;
    public String docStatus;
    public String docGroup;
    public int allItem;
    public int finItem;

    public DocInfo(String dId, String dDate, String dStatus, String dGroup, int dAllItem, int dFinItem) {
        docId = dId;
        docDate = dDate;
        docStatus = dStatus;
        docGroup = dGroup;
        allItem = dAllItem;
        finItem = dFinItem;
    }

}

