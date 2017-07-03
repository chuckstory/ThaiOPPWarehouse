package com.thaiopp.vars;

public class ListLocation {
    public String locationId;
    public String palletId;
    public int locationQty;

    public ListLocation(String lId, String pId, int lQty) {
        locationId = lId;
        palletId = pId;
        locationQty = lQty;
    }
}
