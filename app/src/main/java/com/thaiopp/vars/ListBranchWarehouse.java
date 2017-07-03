package com.thaiopp.vars;

import java.util.List;

public class ListBranchWarehouse {
    public List<ListBranch> lsBbranch;
    public List<ListWarehouse> lsWarehouse;

    public ListBranchWarehouse(List<ListBranch> listBranches, List<ListWarehouse> listWarehouses) {
        lsBbranch = listBranches;
        lsWarehouse = listWarehouses;
    }
}
