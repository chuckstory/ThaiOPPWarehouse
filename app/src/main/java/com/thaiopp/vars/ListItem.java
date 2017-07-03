package com.thaiopp.vars;

/**
 * Created by sutt on 2/12/2016.
 */

public class ListItem {
    public String itemId;
    public String itemDate;
    public String itemStatus;
    public String itemGroup;
    public int allJob;
    public int finJob;

    public ListItem(String iId, String iDate, String iStatus, String iGroup, int iAllJob, int iFinJob) {
        itemId = iId;
        itemDate = iDate;
        itemStatus = iStatus;
        itemGroup = iGroup;
        allJob = iAllJob;
        finJob = iFinJob;
    }

}
