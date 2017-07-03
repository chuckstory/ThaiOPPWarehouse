package com.thaiopp.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.thaiopp.thaioppwarehouse.R;
import com.thaiopp.vars.ConfigSetting;
import com.thaiopp.vars.DocInfo;
import com.thaiopp.vars.ItemInfo;
import com.thaiopp.vars.ItemStockInfo;
import com.thaiopp.vars.ListBranch;
import com.thaiopp.vars.ListBranchWarehouse;
import com.thaiopp.vars.ListItemDetail;
import com.thaiopp.vars.ListLocation;
import com.thaiopp.vars.ListSaleOrder;
import com.thaiopp.vars.ListWarehouse;
import com.thaiopp.vars.ListWorkOrder;
import com.thaiopp.vars.ProdRcv;
import com.thaiopp.vars.RecLocation;
import com.thaiopp.vars.StoreItem;
import com.thaiopp.vars.Transaction;
import com.thaiopp.vars.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DataUtil {
    Context c;
    private String barcode = "";
    private List<RecLocation> recLocation;

    public DataUtil(Context context) {
        this.c = context;
    }



    //begin ---------- clrNull ------------------------
    public String clrNull(String data) {
        if (data.equals("null")) {
            data = "";
        }
        return data;
    }
    //end   ---------- clrNull ------------------------

    //begin ---------- serviceAsynTask ------------------

    public class serviceAsynTask extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... params) {
            String spExec = params[0];
            String spParam = params[1];
            String log;
            String result;
            Log.i("webservice", "Call  : " + spExec);
            Log.i("webservice", "Param : " + spParam);
            result = WebserviceUtil.getWebServiceResult(spExec, spParam);
            //result = getTestResult(spExec);
            Log.i("webservice", "Result: " + result);
            if (GlobalVar.getInstance().getConfigSetting().recLog.equals("Y")) {

                log = "SP Call : " + spExec + "\n" + "Param   : " + spParam + "\n" + "Result  : " + result + "\n---------------------------------";
                recordLog(log);
            }
            return result;
        }

        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
            }
        }
    }

    //end ---------- serviceAsynTask ------------------

    //begin ---------- callAsynTask ------------------

    public String callAsynTask(String sp, String param) {
        String wsData = "";
        serviceAsynTask wsInit = new serviceAsynTask();
        AsyncTask<String, Integer, String> wsResult = wsInit.execute(sp, param);
        try {
            wsData = wsResult.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return wsData;
    }
    //end ---------- callAsynTask ------------------

    //begin ------------------- loginAuthen -------------------------------------

    public boolean loginAuthen(String u, String p, String o, String b, String w) {
        String sp = c.getString(R.string.sp_login);
        String param = u + "," + p + "," + o;
        String wsRes = callAsynTask(sp, param);
        UserInfo userInfo;

        if (!wsRes.equals("")) {
            userInfo = loginParseJSON(wsRes);
            userInfo.userLogin = u;
            userInfo.branchId = b;
            userInfo.warehouseId = w;
            userInfo.imei = getIMEI(c);
            GlobalVar.getInstance().setUserInfo(userInfo);
            return true;
        } else {
            return false;
        }
    }

    public UserInfo loginParseJSON(String result) {
        UserInfo uInfo = new UserInfo("", "", "", "", "", "", "", "", "");
        JSONObject jsonObj;
        JSONArray jsonArr;
        int jsonSize;

        try {
            //** data validation***
            jsonObj = new JSONObject(result);
            jsonArr = jsonObj.getJSONArray("Table");
            if (jsonArr.length() > 0) {
                uInfo.status = jsonArr.getJSONObject(0).getString("DEL_STAT").toString();
                uInfo.userName = jsonArr.getJSONObject(0).getString("USER_NAME").toString();
                uInfo.dbsId = jsonArr.getJSONObject(0).getString("DBSID").toString();
                uInfo.orgId = jsonArr.getJSONObject(0).getString("ORG_ID").toString();
                uInfo.loginStat = jsonArr.getJSONObject(0).getString("STAT_LOGIN").toString();
            } else {
                Log.e("JSON", "Login Fail");
                uInfo.loginStat = "F";

            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", e.getMessage().toString());
            uInfo.loginStat = "F";

        }

        return uInfo;
    }

    //end ------------------- loginAuthen -------------------------------------

    //begin ------------------- getBranchList -------------------------------------
    public List<ListBranch> getBranchList() {
        List<ListBranch> res = new ArrayList<ListBranch>();
        String sp = c.getString(R.string.sp_branch);
        String param = "OPP";
        String wsRes = callAsynTask(sp, param);

        if (!wsRes.equals("")) {
            res = branchParseJSON(wsRes);
        }

        return res;
    }

    public List<ListBranch> branchParseJSON(String result) {
        List<ListBranch> lsData = new ArrayList<ListBranch>();
        ListBranch b;
        JSONObject jsonObj;
        JSONArray jsonArr;
        int jsonSize;

        try {
            //** data validation***
            jsonObj = new JSONObject(result);
            jsonArr = jsonObj.getJSONArray("Table");
            if (jsonArr.getJSONObject(0).getString("STAT").toString().equals("S")) {
                jsonArr = jsonObj.getJSONArray("Table1");
                jsonSize = jsonArr.length();
                for (int i = 0; i < jsonSize; i++) {
                    b = new ListBranch("", "");
                    b.branchId = jsonArr.getJSONObject(i).getString("BCH_ID").toString();
                    b.branchDetail = jsonArr.getJSONObject(i).getString("BCH_DESC").toString();
                    lsData.add(b);
                }
            } else if (jsonArr.getJSONObject(0).getString("STAT").toString().equals("F")) {
                Log.e("WEB SERVICE", "STAT : CONECTION FAIL");
            } else {
                Log.e("JSON", "STAT : FAIL");

            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", e.getMessage().toString());
        }

        return lsData;
    }

    //end ------------------- getBranchList -------------------------------------

    //begin ------------------- getBranchWarehouseList -------------------------------------
    public ListBranchWarehouse getBranchWarehouseList() {
        ListBranchWarehouse res;
        String sp = c.getString(R.string.sp_branch_warehouse);
        String param = "OPP";
        String wsRes = callAsynTask(sp, param);

        if (!wsRes.equals("")) {
            res = branchWarehouseParseJSON(wsRes);
            return res;
        }

        return null;
    }

    public ListBranchWarehouse branchWarehouseParseJSON(String result) {
        ListBranchWarehouse lsData;
        List<ListBranch> lsBranch = new ArrayList<>();
        List<ListWarehouse> lsWarehouse = new ArrayList<>();
        ListBranch b;
        ListWarehouse w;
        JSONObject jsonObj;
        JSONArray jsonArr;
        int jsonSize;
        lsData = new ListBranchWarehouse(lsBranch, lsWarehouse);
        try {
            //** data validation***
            jsonObj = new JSONObject(result);
            jsonArr = jsonObj.getJSONArray("Table");
            if (jsonArr.getJSONObject(0).getString("STAT").toString().equals("S")) {
                // --  get Warehouse list ---
                jsonArr = jsonObj.getJSONArray("Table1");
                jsonSize = jsonArr.length();
                for (int i = 0; i < jsonSize; i++) {
                    w = new ListWarehouse("", "", "", "", "");
                    w.warehouseId = jsonArr.getJSONObject(i).getString("WAREHOUSE_ID").toString();
                    w.warehouseDesc = jsonArr.getJSONObject(i).getString("WAREHOUSE_DESC").toString();
                    w.warehouseShortName = jsonArr.getJSONObject(i).getString("WH_SHORT_NAME").toString();
                    w.warehouseType = jsonArr.getJSONObject(i).getString("WAREHOUSE_TYPE").toString();
                    w.warehouseBranch = jsonArr.getJSONObject(i).getString("BCH_ID").toString();
                    lsWarehouse.add(w);
                }

                // --  get Branch list ---
                jsonArr = jsonObj.getJSONArray("Table2");
                jsonSize = jsonArr.length();
                for (int i = 0; i < jsonSize; i++) {
                    b = new ListBranch("", "");
                    b.branchId = jsonArr.getJSONObject(i).getString("BCH_ID").toString();
                    b.branchDetail = jsonArr.getJSONObject(i).getString("BCH_DESC").toString();
                    lsBranch.add(b);
                }

                lsData.lsBbranch = lsBranch;
                lsData.lsWarehouse = lsWarehouse;
                return lsData;
            } else if (jsonArr.getJSONObject(0).getString("STAT").toString().equals("F")) {
                Log.e("WEB SERVICE", "STAT : CONECTION FAIL");
            } else {
                Log.e("JSON", "STAT : FAIL");

            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", e.getMessage().toString());
        }

        return lsData;
    }

    //end ------------------- getBranchList -------------------------------------

    //begin ------------------- countTransaction -------------------------------------

    public int countTransaction(String transType) {
        int cnt = 0;
        String sp = "";
        String param = "";
        switch (transType) {
            case "ISS":
                sp = c.getString(R.string.sp_count_issue);
                param = GlobalVar.getInstance().getUserInfo().warehouseId + "," + GlobalVar.getInstance().getUserInfo().branchId;
                break;
            case "SO":
                sp = c.getString(R.string.sp_count_so);
                param = GlobalVar.getInstance().getUserInfo().warehouseId + "," + GlobalVar.getInstance().getUserInfo().branchId;
                break;
            case "RCV":
                sp = c.getString(R.string.sp_count_pick);
                param = GlobalVar.getInstance().getUserInfo().warehouseId + "," + GlobalVar.getInstance().getUserInfo().branchId;
                break;
            case "PRD":
                sp = c.getString(R.string.sp_count_prodrcv);
                param = GlobalVar.getInstance().getUserInfo().branchId + "," + GlobalVar.getInstance().getUserInfo().orgId;
                break;
        }

        String wsRes = callAsynTask(sp, param);

        if (!wsRes.equals("")) {
            cnt = countParseJSON(wsRes);
        }
        return cnt;
    }

    public int countParseJSON(String result) {
        JSONObject jsonObj;
        JSONArray jsonArr;
        int jsonSize;
        Double v = 0.0;

        try {
            //** data validation***
            jsonObj = new JSONObject(result);
            jsonArr = jsonObj.getJSONArray("Table");
            if (jsonArr.getJSONObject(0).getString("STAT").toString().equals("S")) {
                jsonArr = jsonObj.getJSONArray("Table1");
                v = Double.parseDouble(jsonArr.getJSONObject(0).getString("CNT_JOB").toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", e.getMessage().toString());

        }

        return v.intValue();
    }

    //end ------------------- countTransaction -------------------------------------

    //begin ------------------- getItemInfo -------------------------------------

    public ItemInfo getItemInfo(String sid) {
        barcode = sid;
        ItemInfo iInfo = new ItemInfo("", "", "", "", "", "", "", "", 0, 0, "", "", "", "", "");
        String param = sid + "," + GlobalVar.getInstance().getUserInfo().warehouseId + "," + GlobalVar.getInstance().getUserInfo().branchId;
        String sp = c.getString(R.string.sp_get_barcode_inv);

        String wsRes = callAsynTask(sp, param);

        if (!wsRes.equals("")) {
            iInfo = infoParseJSON(wsRes, sid);
        }
        return iInfo;
    }

    public ItemInfo infoParseJSON(String result, String barcode) {
        List<String> lsData = new ArrayList<String>();
        JSONObject jsonObj;
        JSONArray jsonArr;
        int jsonSize;
        ItemInfo itemInfo = new ItemInfo("", "", "", "", "", "", "", "", 0, 0, "", "", "", "", "");

        try {
            //** data validation***
            jsonObj = new JSONObject(result);
            jsonArr = jsonObj.getJSONArray("Table");
            if (jsonArr.getJSONObject(0).getString("STAT").toString().equals("S")) {
                jsonArr = jsonObj.getJSONArray("Table1");
                jsonSize = jsonArr.length();
                itemInfo.itemId = barcode;
                itemInfo.itemBarcode = barcode;
                itemInfo.itemType = jsonArr.getJSONObject(0).getString("BARCODE_TYPE").toString();
                itemInfo.itemDetail = jsonArr.getJSONObject(0).getString("DESC_01").toString();
                itemInfo.itemDetail2 = jsonArr.getJSONObject(0).getString("DESC_02").toString();
                itemInfo.itemLocation = clrNull(jsonArr.getJSONObject(0).getString("LOC_ID").toString());
                itemInfo.itemPallet = clrNull(jsonArr.getJSONObject(0).getString("PALLET_ID").toString());
                itemInfo.itemQty = jsonArr.getJSONObject(0).getInt("QTY");
                itemInfo.rollNo = clrNull(jsonArr.getJSONObject(0).getString("ROLL_NO").toString());
                itemInfo.lotId = clrNull(jsonArr.getJSONObject(0).getString("LOT_ID").toString());
            } else {
                Log.e("JSON", "STAT : FAIL");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", e.getMessage().toString());
        }

        return itemInfo;
    }

    //end ------------------- getItemInfo -------------------------------------

    //begin ------------------- getSoList -------------------------------------
    public List<ListSaleOrder> getSoList() {
        List<ListSaleOrder> res;
        String sp = c.getString(R.string.sp_getwo_so);
        String param = GlobalVar.getInstance().getUserInfo().warehouseId + "," + GlobalVar.getInstance().getUserInfo().branchId;
        String wsRes = callAsynTask(sp, param);

        if (!wsRes.equals("")) {
            res = soListParseJSON(wsRes);
            return res;
        }

        return null;
    }

    public List<ListSaleOrder> soListParseJSON(String result) {
        List<ListSaleOrder> lsData = new ArrayList<>();
        ListSaleOrder itm;
        JSONObject jsonObj;
        JSONArray jsonArr;
        int jsonSize;
        try {
            //** data validation***
            jsonObj = new JSONObject(result);
            jsonArr = jsonObj.getJSONArray("Table");
            if (jsonArr.getJSONObject(0).getString("STAT").toString().equals("S")) {
                // --  get Warehouse list ---
                jsonArr = jsonObj.getJSONArray("Table1");
                jsonSize = jsonArr.length();
                for (int i = 0; i < jsonSize; i++) {
                    itm = new ListSaleOrder("", "", "", "", "");
                    itm.soId = jsonArr.getJSONObject(i).getString("SO_ID").toString();
                    itm.soSeq = jsonArr.getJSONObject(i).getString("SO_SEQ").toString();
                    itm.seqDeli = jsonArr.getJSONObject(i).getString("SEQ_DELI").toString();
                    itm.tfLotDocId = jsonArr.getJSONObject(i).getString("TFLOT_DOC_ID").toString();
                    itm.deliDate = jsonArr.getJSONObject(i).getString("DELI_DATE").toString();
                    lsData.add(itm);
                }

                return lsData;
            } else if (jsonArr.getJSONObject(0).getString("STAT").toString().equals("F")) {
                Log.e("WEB SERVICE", "STAT : CONECTION FAIL");
            } else {
                Log.e("JSON", "STAT : FAIL");

            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", e.getMessage().toString());
        }

        return lsData;
    }

    //end ------------------- getSoList -------------------------------------

    //begin ------------------- getSoDetailList -------------------------------------
    public List<ListItemDetail> getSoDetailList(ListSaleOrder soData) {
        List<ListItemDetail> lsItem;
        String sp = c.getString(R.string.sp_getwo_sodetail);
        String param = GlobalVar.getInstance().getUserInfo().warehouseId + "," + GlobalVar.getInstance().getUserInfo().branchId
                + "," + soData.soId + "," + soData.tfLotDocId + "," + soData.soSeq + "," + soData.seqDeli;
        String wsRes = callAsynTask(sp, param);

        if (!wsRes.equals("")) {
            lsItem = soDetailParseJSON(wsRes);

            String loc = "";
            String getLoc = "";
            ListItemDetail tmpItem;
            for (int i = 0; i < lsItem.size(); i++) {
                tmpItem = lsItem.get(i);
                loc = "LOCATION : ";
                getLoc = getItemInfo(lsItem.get(i).itemBarcode).itemLocation;
                if (getLoc.equals("MANY")) {
                    List<ListLocation> lsLoc = getLocationList(lsItem.get(i).itemBarcode);

                    for (int j = 0; j < lsLoc.size(); j++) {
                        if (j > 0) {
                            loc = loc + ",";
                        }
                        loc = loc + lsLoc.get(j).locationId + "(" + lsLoc.get(j).locationQty + ")";
                    }

                } else {
                    if (getLoc.equals("null")) {
                        getLoc = "-";
                    }
                    loc = loc + getLoc;
                }
                tmpItem.itemLocation = loc;

                lsItem.set(i, tmpItem);
            }
            return lsItem;
        }

        return null;
    }

    public List<ListItemDetail> soDetailParseJSON(String result) {
        List<ListItemDetail> lsData = new ArrayList<>();
        ListItemDetail itm;
        JSONObject jsonObj;
        JSONArray jsonArr;
        int jsonSize;
        try {
            //** data validation***
            jsonObj = new JSONObject(result);
            jsonArr = jsonObj.getJSONArray("Table");
            if (jsonArr.getJSONObject(0).getString("STAT").toString().equals("S")) {
                // --  get Warehouse list ---
                jsonArr = jsonObj.getJSONArray("Table1");
                jsonSize = jsonArr.length();
                for (int i = 0; i < jsonSize; i++) {
                    itm = new ListItemDetail("", "", "", "", 0, "", 0, "", 0, 0, "", "", "", "", "", "", "");
                    itm.itemId = jsonArr.getJSONObject(i).getString("SO_ID").toString();
                    itm.stockSeq = jsonArr.getJSONObject(i).getString("SO_SEQ").toString();
                    itm.stockNo = jsonArr.getJSONObject(i).getString("TFLOT_DOC_ID").toString();
                    itm.itemDetail = jsonArr.getJSONObject(i).getString("DELI_DATE").toString().substring(0, 10).toString();
                    itm.itemAll = (int) Double.parseDouble(jsonArr.getJSONObject(i).getString("QTY").toString());
                    itm.itemQty = String.valueOf(itm.itemAll);
                    itm.matCode = clrNull(jsonArr.getJSONObject(i).getString("MAT_CODE").toString());
                    itm.matName = clrNull(jsonArr.getJSONObject(i).getString("MAT_NAME").toString());
                    itm.grade = clrNull(jsonArr.getJSONObject(i).getString("GRADE").toString());
                    itm.lotId = clrNull(jsonArr.getJSONObject(i).getString("LOT_ID").toString());
                    itm.itemBarcode = jsonArr.getJSONObject(i).getString("BARCODE_ID").toString();
                    lsData.add(itm);
                }

                return lsData;
            } else if (jsonArr.getJSONObject(0).getString("STAT").toString().equals("F")) {
                Log.e("WEB SERVICE", "STAT : CONECTION FAIL");
            } else {
                Log.e("JSON", "STAT : FAIL");

            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", e.getMessage().toString());
        }

        return lsData;
    }

    //end ------------------- getSoDetailList -------------------------------------

    //begin ------------------- getPalletDetail -------------------------------------
    public List<ItemInfo> getPalletDetail(String id) {
        List<ItemInfo> lsItem = new ArrayList<>();
        String sp = c.getString(R.string.sp_get_pallet_detail);
        String param = id + "," + GlobalVar.getInstance().getUserInfo().warehouseId + "," + GlobalVar.getInstance().getUserInfo().branchId + "," + GlobalVar.getInstance().getUserInfo().orgId;

        String wsRes = callAsynTask(sp, param);

        if (!wsRes.equals("")) {
            lsItem = palletParseJSON(wsRes);
        }
        return lsItem;
    }

    public List<ItemInfo> palletParseJSON(String result) {
        List<ItemInfo> lsData = new ArrayList<>();
        JSONObject jsonObj;
        JSONArray jsonArr;
        int jsonSize;
        ItemInfo itm;

        try {
            //** data validation***
            String tmpStr = "";
            jsonObj = new JSONObject(result);
            jsonArr = jsonObj.getJSONArray("header");
            if (jsonArr.getJSONObject(0).getString("Status").toString().equals("Success")) {
                jsonArr = jsonObj.getJSONArray("Table");
                jsonSize = jsonArr.length();
                for (int i = 0; i < jsonSize; i++) {
                    itm = new ItemInfo("", "", "", "", "", "", "", "", 0, 0, "", "", "", "", "");
                    itm.itemBarcode = jsonArr.getJSONObject(i).getString("BARCODE_ID").toString();
                    itm.itemName = jsonArr.getJSONObject(i).getString("MAT_NAME").toString();
                    itm.itemType = jsonArr.getJSONObject(i).getString("TYPE").toString();
                    itm.itemDetail = jsonArr.getJSONObject(i).getString("ROLL_INPT".toString());
                    if (itm.itemDetail.equals("null")) {
                        itm.itemDetail = "";
                    }
                    itm.itemWeight = jsonArr.getJSONObject(i).getString("WEIGHT_QTY".toString());
                    Double d = Double.parseDouble(jsonArr.getJSONObject(i).getString("QTY").toString());
                    itm.itemQty = d.intValue();
                    lsData.add(itm);
                }
            } else {
                Log.e("JSON", "STAT : FAIL");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", e.getMessage().toString());
        }

        return lsData;
    }
    //end ------------------- getPalletDetail -------------------------------------

    //begin ------------------- getProdRcvList -------------------------------------
    public List<ProdRcv> getProdRcvList() {
        List<ProdRcv> lsItem = new ArrayList<>();
        String sp = c.getString(R.string.sp_get_workorder_prod);
        String param = GlobalVar.getInstance().getUserInfo().branchId + "," + GlobalVar.getInstance().getUserInfo().orgId;

        String wsRes = callAsynTask(sp, param);

        if (!wsRes.equals("")) {
            lsItem = prodRcvParseJSON(wsRes);
        }
        return lsItem;
    }

    public List<ProdRcv> prodRcvParseJSON(String result) {
        List<ProdRcv> lsData = new ArrayList<>();

        JSONObject jsonObj;
        JSONArray jsonArr;
        int jsonSize;
        ProdRcv itm;

        recLocation = new ArrayList<>();

        try {
            //** data validation***
            String tmpStr = "";
            jsonObj = new JSONObject(result);
            jsonArr = jsonObj.getJSONArray("Table");
            if (jsonArr.getJSONObject(0).getString("STAT").toString().equals("S")) {
                jsonArr = jsonObj.getJSONArray("Table1");
                jsonSize = jsonArr.length();
                for (int i = 0; i < jsonSize; i++) {
                    itm = new ProdRcv("", "", "", "", "", "", "", "", "", "", "", "", 0, false, "", "");
                    itm.jobId = jsonArr.getJSONObject(i).getString("JOB_ID").toString();
                    itm.inputType = jsonArr.getJSONObject(i).getString("INPUT_TYPE").toString();
                    itm.barcode = jsonArr.getJSONObject(i).getString("BARCODE_ID").toString();
                    itm.palletId = clrNull(jsonArr.getJSONObject(i).getString("PALLET_ID").toString());
                    if (itm.palletId.equals("null")) {
                        itm.palletId = "";
                    }
                    itm.locationId = jsonArr.getJSONObject(i).getString("LOC_ID").toString();
                    itm.matCode = jsonArr.getJSONObject(i).getString("MAT_CODE").toString();
                    itm.grade = jsonArr.getJSONObject(i).getString("GRADE").toString();
                    itm.lotId = jsonArr.getJSONObject(i).getString("LOT_ID").toString();
                    itm.rollId = jsonArr.getJSONObject(i).getString("ROLL_ID").toString();
                    itm.itemLocation = "-";//getPickLocationList(itm.matCode, itm.grade);
                    if (itm.rollId.equals("null")) {
                        itm.rollId = "";
                    }
                    itm.cpSpec = jsonArr.getJSONObject(i).getString("CP_SPEC").toString();
                    itm.crDate = jsonArr.getJSONObject(i).getString("CR_DATE").toString();
                    //itm.weight = jsonArr.getJSONObject(i).getString("WEIGHT_QTY").toString();
                    Double w = Double.parseDouble(jsonArr.getJSONObject(i).getString("WEIGHT_QTY").toString());
                    itm.weight = String.format("%.2f", w);
                    itm.gWeight = jsonArr.getJSONObject(i).getString("GWEIGHT_QTY").toString();
                    Double d = Double.parseDouble(jsonArr.getJSONObject(i).getString("QTY").toString());
                    itm.qty = d.intValue();
                    lsData.add(itm);
                }
                Log.i("webservice", "JSON : Pass " + String.valueOf(lsData.size()) + " rec.");
            } else {
                Log.i("webservice", "JSON : Fail");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", e.getMessage().toString());
        }

        return lsData;
    }
    //end ------------------- getProdRcvList -------------------------------------

    //begin ------------------- getLocationList -------------------------------------
    public List<ListLocation> getLocationList(String id) {
        List<ListLocation> locationList = new ArrayList<>();
        String sp = c.getString(R.string.sp_get_locationlist);
        String param = id + "," + GlobalVar.getInstance().getUserInfo().warehouseId + "," + GlobalVar.getInstance().getUserInfo().branchId;

        String wsRes = callAsynTask(sp, param);

        if (!wsRes.equals("")) {
            locationList = locationParseJSON(wsRes);
        }
        return locationList;
    }

    public List<ListLocation> locationParseJSON(String result) {
        List<ListLocation> lsData = new ArrayList<>();
        JSONObject jsonObj;
        JSONArray jsonArr;
        int jsonSize;
        ListLocation loc;

        try {
            //** data validation***
            String tmpStr = "";
            jsonObj = new JSONObject(result);
            jsonArr = jsonObj.getJSONArray("Table");
            if (jsonArr.getJSONObject(0).getString("STAT").toString().equals("S")) {
                jsonArr = jsonObj.getJSONArray("Table1");
                jsonSize = jsonArr.length();
                for (int i = 0; i < jsonSize; i++) {
                    loc = new ListLocation("", "", 0);
                    loc.locationId = clrNull(jsonArr.getJSONObject(i).getString("LOC_ID").toString());

                    loc.palletId = clrNull(jsonArr.getJSONObject(i).getString("PALLET_ID").toString());

                    loc.locationQty = jsonArr.getJSONObject(i).getInt("QTY");
                    lsData.add(loc);
                }
            } else {
                Log.e("JSON", "STAT : FAIL");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", e.getMessage().toString());
        }

        return lsData;
    }
    //end ------------------- getLocationList -------------------------------------


    //begin ------------------- getBarcodeDetail -------------------------------------

    public ItemInfo getBarcodeDetail(ItemInfo itm) {
        ItemInfo iInfo;
        String sp = c.getString(R.string.sp_get_barcodedetail);
        String param = itm.itemBarcode;

        String wsRes = callAsynTask(sp, param);

        if (!wsRes.equals("")) {
            iInfo = barcodeDetailParseJSON(wsRes);
            itm.matCode = iInfo.matCode;
            itm.grade = iInfo.grade;
            itm.lotId = iInfo.lotId;
        }
        return itm;
    }


    public ItemInfo barcodeDetailParseJSON(String result) {
        List<String> lsData = new ArrayList<String>();
        JSONObject jsonObj;
        JSONArray jsonArr;
        int jsonSize;
        ItemInfo itemInfo = new ItemInfo("", "", "", "", "", "", "", "", 0, 0, "", "", "", "", "");

        try {
            //** data validation***
            jsonObj = new JSONObject(result);
            jsonArr = jsonObj.getJSONArray("Table");
            if (jsonArr.getJSONObject(0).getString("STAT").toString().equals("S")) {
                jsonArr = jsonObj.getJSONArray("Table1");
                jsonSize = jsonArr.length();
                itemInfo.matCode = jsonArr.getJSONObject(0).getString("MAT_CODE").toString();
                itemInfo.grade = jsonArr.getJSONObject(0).getString("GRADE").toString();
                itemInfo.lotId = jsonArr.getJSONObject(0).getString("LOT_ID").toString();
            } else {
                Log.e("JSON", "STAT : FAIL");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", e.getMessage().toString());
        }

        return itemInfo;
    }

    //end ------------------- getItemInfo -------------------------------------

    //begin ------------------- getCheckList -------------------------------------
    public List<ItemInfo> getCheckList(String locationId) {
        List<ItemInfo> itemInfo = new ArrayList<>();
        String sp = c.getString(R.string.sp_get_loc_stock);
        String param = locationId + "," + GlobalVar.getInstance().getUserInfo().warehouseId + "," + GlobalVar.getInstance().getUserInfo().branchId;

        String wsRes = callAsynTask(sp, param);

        if (!wsRes.equals("")) {
            itemInfo = itemInfoParseJSON(wsRes);
        }
        return itemInfo;
    }

    public List<ItemInfo> itemInfoParseJSON(String result) {
        List<ItemInfo> lsData = new ArrayList<>();
        JSONObject jsonObj;
        JSONArray jsonArr;
        int jsonSize;
        ItemInfo iInfo;

        try {
            //** data validation***
            String tmpStr = "";
            jsonObj = new JSONObject(result);
            jsonArr = jsonObj.getJSONArray("Table");
            if (jsonArr.getJSONObject(0).getString("STAT").toString().equals("S")) {
                jsonArr = jsonObj.getJSONArray("Table1");
                jsonSize = jsonArr.length();
                for (int i = 0; i < jsonSize; i++) {
                    iInfo = new ItemInfo("", "", "", "", "", "", "", "", 0, 0, "", "", "", "", "");

                    tmpStr = jsonArr.getJSONObject(i).getString("TYPE").toString();
                    if (tmpStr.equals("ม้วน:")) {
                        iInfo.itemType = "ม้วน";
                    } else if (tmpStr.equals("พาเล็ต:")) {
                        iInfo.itemType = "PALLET";
                    } else {
                        iInfo.itemType = jsonArr.getJSONObject(i).getString("TYPE").toString();
                    }
                    iInfo.itemId = jsonArr.getJSONObject(i).getString("BARCODE_ID").toString();
                    iInfo.itemBarcode = jsonArr.getJSONObject(i).getString("BARCODE_ID").toString();
                    iInfo.itemName = clrNull(jsonArr.getJSONObject(i).getString("MAT_NAME").toString());
                    iInfo.itemLocation = clrNull(jsonArr.getJSONObject(i).getString("LOC_ID").toString());
                    iInfo.itemQty = jsonArr.getJSONObject(i).getInt("QTY");
                    iInfo.rollNo =  clrNull(jsonArr.getJSONObject(0).getString("ROLL_NO").toString());
                    lsData.add(iInfo);
                }
            } else {
                Log.e("JSON", "STAT : FAIL");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", e.getMessage().toString());
        }

        return lsData;
    }
    //end ------------------- getCheckList -------------------------------------

    //begin ------------------- getRollCheckList -------------------------------------
    public List<ItemStockInfo> getRollCheckList(String locationId) {
        List<ItemStockInfo> roll = new ArrayList<>();
        List<ItemStockInfo> lsRoll;
        List<ItemStockInfo> lsCheck;
        String chk;

        lsCheck = getDetailCheckList(locationId);
        for (int i = 0; i < lsCheck.size(); i++) {
            if (lsCheck.get(i).itemType.equals("PALLET")) {
                lsRoll = getRollList(lsCheck.get(i).itemId, locationId);
                for (int r = 0; r < lsRoll.size(); r++) {
                    roll.add(lsRoll.get(r));
                }
            } else {
                roll.add(lsCheck.get(i));
            }
        }
        return roll;
    }

    //end ----------------------getRollCheckList--------------------------------------

    //begin ------------------- getDetailCheckList -------------------------------------
    public List<ItemStockInfo> getDetailCheckList(String locationId) {
        List<ItemStockInfo> itemInfo = new ArrayList<>();
        String sp = c.getString(R.string.sp_get_location_stock);
        String param = locationId + "," + GlobalVar.getInstance().getUserInfo().warehouseId + "," + GlobalVar.getInstance().getUserInfo().branchId;

        String wsRes = callAsynTask(sp, param);

        if (!wsRes.equals("")) {
            itemInfo = itemDetailParseJSON(wsRes);
        }
        return itemInfo;
    }

    public List<ItemStockInfo> itemDetailParseJSON(String result) {
        List<ItemStockInfo> lsData = new ArrayList<>();
        JSONObject jsonObj;
        JSONArray jsonArr;
        int jsonSize;
        ItemStockInfo iInfo;

        try {
            //** data validation***
            String tmpStr = "";
            jsonObj = new JSONObject(result);
            jsonArr = jsonObj.getJSONArray("Table");
            if (jsonArr.getJSONObject(0).getString("STAT").toString().equals("S")) {
                jsonArr = jsonObj.getJSONArray("Table1");
                jsonSize = jsonArr.length();
                for (int i = 0; i < jsonSize; i++) {
                    iInfo = new ItemStockInfo("", "", "", "", "", "", "", "", "", 0, 0, "", "","", "", "", "", "", "", "", "", "", "", "", "", "", "", "");

                    tmpStr = jsonArr.getJSONObject(i).getString("TYPE").toString();
                    if (tmpStr.equals("ม้วน:")) {
                        iInfo.itemType = "ม้วน";
                    } else if (tmpStr.equals("พาเล็ต:")) {
                        iInfo.itemType = "PALLET";
                    } else {
                        iInfo.itemType = jsonArr.getJSONObject(i).getString("TYPE").toString();
                    }
                    iInfo.itemId = jsonArr.getJSONObject(i).getString("BARCODE_ID").toString();
                    iInfo.itemBarcode = jsonArr.getJSONObject(i).getString("BARCODE_ID").toString();
                    iInfo.itemName = jsonArr.getJSONObject(i).getString("MAT_NAME").toString();
                    iInfo.itemLocation = jsonArr.getJSONObject(i).getString("LOC_ID").toString();
                    iInfo.rollInpt = jsonArr.getJSONObject(i).getString("ROLL_INPT").toString();
                    iInfo.rollNo = jsonArr.getJSONObject(i).getString("ROLL_NO").toString();
                    iInfo.itemQty = jsonArr.getJSONObject(i).getInt("QTY");
                    iInfo.itemWeight = jsonArr.getJSONObject(i).getString("WEIGHT_QTY");
                    iInfo.matCode = jsonArr.getJSONObject(i).getString("MAT_CODE");
                    iInfo.grade = jsonArr.getJSONObject(i).getString("GRADE");
                    iInfo.lotId = jsonArr.getJSONObject(i).getString("LOT_ID");
                    iInfo.itemStatus = jsonArr.getJSONObject(i).getString("STATUS");
                    iInfo.matCat = jsonArr.getJSONObject(i).getString("MAT_CATEGORY_NAME");
                    iInfo.matType = jsonArr.getJSONObject(i).getString("MAT_TYPE_NAME");
                    iInfo.matFml = jsonArr.getJSONObject(i).getString("MAT_FML_NAME");
                    iInfo.itemThick = jsonArr.getJSONObject(i).getString("THICK");
                    iInfo.itemWidth = jsonArr.getJSONObject(i).getString("WIDTH");
                    iInfo.unitWidth = jsonArr.getJSONObject(i).getString("UNIT_WIDTH");
                    iInfo.itemLength = jsonArr.getJSONObject(i).getString("LENGTH");
                    iInfo.unitLength = jsonArr.getJSONObject(i).getString("UNIT_LENGTH");
                    iInfo.core = jsonArr.getJSONObject(i).getString("CORE");
                    iInfo.joint = jsonArr.getJSONObject(i).getString("JOINT");
                    iInfo.matSpec1 = jsonArr.getJSONObject(i).getString("MAT_SPEC1_NAME");
                    if (iInfo.matSpec1.equals("null")) {
                        iInfo.matSpec1 = "-";
                    }
                    iInfo.matSpec2 = jsonArr.getJSONObject(i).getString("MAT_SPEC2_NAME");
                    if (iInfo.matSpec2.equals("null")) {
                        iInfo.matSpec2 = "-";
                    }
                    lsData.add(iInfo);
                }
            } else {
                Log.e("JSON", "STAT : FAIL");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", e.getMessage().toString());
        }

        return lsData;
    }
    //end ------------------- getDetailCheckList -------------------------------------

    //begin ------------------- getRollList -------------------------------------
    public List<ItemStockInfo> getRollList(String palletId, String locationId) {
        List<ItemStockInfo> rollInfo = new ArrayList<ItemStockInfo>();
        String sp = c.getString(R.string.sp_get_chk_roll);
        String param = palletId + "," + locationId + "," + GlobalVar.getInstance().getUserInfo().warehouseId + "," + GlobalVar.getInstance().getUserInfo().branchId + "," + GlobalVar.getInstance().getUserInfo().orgId;

        String wsRes = callAsynTask(sp, param);

        if (!wsRes.equals("")) {
            rollInfo = rollParseJSON(wsRes, palletId);
        }

        return rollInfo;
    }

    public List<ItemStockInfo> rollParseJSON(String result, String pId) {
        List<ItemStockInfo> lsData = new ArrayList<>();
        JSONObject jsonObj;
        JSONArray jsonArr;
        int jsonSize;
        ItemStockInfo iInfo;

        try {
            //** data validation***
            jsonObj = new JSONObject(result);
            jsonArr = jsonObj.getJSONArray("Table");
            if (jsonArr.getJSONObject(0).getString("STAT").toString().equals("S")) {
                jsonArr = jsonObj.getJSONArray("Table1");
                jsonSize = jsonArr.length();
                for (int i = 0; i < jsonSize; i++) {
                    iInfo = new ItemStockInfo("", "", "", "", "", "", "", "", "", 0, 0, "", "", "", "" , "", "", "", "", "", "", "", "", "", "", "", "", "");


                    iInfo.itemType = jsonArr.getJSONObject(i).getString("TYPE").toString() + "[ PALLET : " + pId + " ]";
                    iInfo.itemId = jsonArr.getJSONObject(i).getString("BARCODE_ID").toString();
                    iInfo.itemBarcode = jsonArr.getJSONObject(i).getString("BARCODE_ID").toString();
                    iInfo.itemName = jsonArr.getJSONObject(i).getString("MAT_NAME").toString();
                    iInfo.itemLocation = jsonArr.getJSONObject(i).getString("LOC_ID").toString();
                    iInfo.rollInpt = jsonArr.getJSONObject(i).getString("ROLL_INPT").toString();
                    iInfo.itemQty = jsonArr.getJSONObject(i).getInt("QTY");
                    iInfo.itemWeight = jsonArr.getJSONObject(i).getString("WEIGHT_QTY");
                    iInfo.matCode = jsonArr.getJSONObject(i).getString("MAT_CODE");
                    iInfo.grade = jsonArr.getJSONObject(i).getString("GRADE");
                    iInfo.lotId = jsonArr.getJSONObject(i).getString("LOT_ID");
                    iInfo.itemStatus = jsonArr.getJSONObject(i).getString("STATUS");
                    iInfo.matCat = jsonArr.getJSONObject(i).getString("MAT_CATEGORY_NAME");
                    iInfo.matType = jsonArr.getJSONObject(i).getString("MAT_TYPE_NAME");
                    iInfo.matFml = jsonArr.getJSONObject(i).getString("MAT_FML_NAME");
                    iInfo.itemThick = jsonArr.getJSONObject(i).getString("THICK");
                    iInfo.itemWidth = jsonArr.getJSONObject(i).getString("WIDTH");
                    iInfo.unitWidth = jsonArr.getJSONObject(i).getString("UNIT_WIDTH");
                    iInfo.itemLength = jsonArr.getJSONObject(i).getString("LENGTH");
                    iInfo.unitLength = jsonArr.getJSONObject(i).getString("UNIT_LENGTH");
                    iInfo.core = jsonArr.getJSONObject(i).getString("CORE");
                    iInfo.joint = jsonArr.getJSONObject(i).getString("JOINT");
                    iInfo.matSpec1 = jsonArr.getJSONObject(i).getString("MAT_SPEC1_NAME");
                    iInfo.matSpec2 = jsonArr.getJSONObject(i).getString("MAT_SPEC2_NAME");
                    lsData.add(iInfo);
                }
            } else {
                Log.e("JSON", "STAT : FAIL");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", e.getMessage().toString());
        }

        return lsData;
    }
    //end ------------------- getRollList -------------------------------------


    public List<ListItemDetail> getIssueList(String stockSeq) {
        List<ListItemDetail> lsItem = new ArrayList<>();

        String sp = c.getString(R.string.sp_getwo_issdetail);
        String param = GlobalVar.getInstance().getUserInfo().warehouseId + ","
                + GlobalVar.getInstance().getUserInfo().branchId + "," + stockSeq;

        String wsRes = callAsynTask(sp, param);

        if (!wsRes.equals("")) {
            lsItem = issueListParseJSON(wsRes);

            Double d;
            String loc = "";
            String getLoc = "";
            ListItemDetail tmpItem;
            for (int i = 0; i < lsItem.size(); i++) {
                tmpItem = lsItem.get(i);
                loc = "LOCATION : ";
                getLoc = getItemInfo(lsItem.get(i).itemBarcode).itemLocation;
                if (getLoc.equals("MANY")) {
                    List<ListLocation> lsLoc = getLocationList(lsItem.get(i).itemBarcode);

                    for (int j = 0; j < lsLoc.size(); j++) {
                        if (j > 0) {
                            loc = loc + ",";
                        }
                        loc = loc + lsLoc.get(j).locationId + "(" + lsLoc.get(j).locationQty + ")";
                    }

                } else {
                    if (getLoc.equals("null")) {
                        getLoc = "-";
                    }
                    loc = loc + getLoc;
                }
                tmpItem.itemLocation = loc;
                tmpItem.itemWeight = lsItem.get(i).itemWeight + " กก.";
                d = Double.parseDouble(lsItem.get(i).itemQty);
                tmpItem.itemQty = String.valueOf(lsItem.get(i).itemAll) + " ม้วน";

                lsItem.set(i, tmpItem);
            }
        }
        return lsItem;
    }

    public List<ListItemDetail> issueListParseJSON(String result) {
        List<ListItemDetail> lsData = new ArrayList<>();
        JSONObject jsonObj;
        JSONArray jsonArr;
        int jsonSize;
        String tmpDate;
        ListItemDetail itm;

        try {
            //** data validation***
            jsonObj = new JSONObject(result);
            jsonArr = jsonObj.getJSONArray("Table");
            if (jsonArr.getJSONObject(0).getString("STAT").toString().equals("S")) {
                jsonArr = jsonObj.getJSONArray("Table1");
                jsonSize = jsonArr.length();
                for (int i = 0; i < jsonSize; i++) {
                    itm = new ListItemDetail("", "", "", "", 0, "", 0, "", 0, 0, "", "", "", "", "", "", "");

                    itm.itemBarcode = jsonArr.getJSONObject(i).getString("BARCODE").toString();
                    itm.matCode = jsonArr.getJSONObject(i).getString("MAT_CODE").toString();
                    itm.matName = jsonArr.getJSONObject(i).getString("MAT_NAME").toString();
                    itm.grade = jsonArr.getJSONObject(i).getString("GRADE").toString();
                    itm.lotId = jsonArr.getJSONObject(i).getString("LOT_ID").toString();
                    itm.stockSeq = jsonArr.getJSONObject(i).getString("STOCK_SEQ").toString();
                    itm.stockNo = jsonArr.getJSONObject(i).getString("STOCK_NO").toString();
                    if (jsonArr.getJSONObject(i).getString("CHK_RESERVE").toString().equals("F")) {
                        itm.itemAlert = 1;
                    } else {
                        itm.itemAlert = 0;
                    }
                    itm.itemDetail = clrNull(jsonArr.getJSONObject(i).getString("ROLL_INPT").toString());

                    itm.itemAll = (int) Double.parseDouble(jsonArr.getJSONObject(i).getString("QTY").toString());


                    lsData.add(itm);
                }
            } else {
                Log.e("JSON", "STAT : FAIL");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", e.getMessage().toString());
        }

        return lsData;
    }

    public List<Transaction> transIssueParseJSON(String result) {
        List<Transaction> lsData = new ArrayList<>();
        JSONObject jsonObj;
        JSONArray jsonArr;
        int jsonSize;
        String tmpDate;
        Transaction iTrans;

        try {
            //** data validation***
            jsonObj = new JSONObject(result);
            jsonArr = jsonObj.getJSONArray("Table");
            if (jsonArr.getJSONObject(0).getString("STAT").toString().equals("S")) {
                jsonArr = jsonObj.getJSONArray("Table1");
                jsonSize = jsonArr.length();
                for (int i = 0; i < jsonSize; i++) {
                    iTrans = new Transaction("", "", "", "", "", "", "", "", "", "", "", "", "", "");

                    iTrans.transId = jsonArr.getJSONObject(i).getString("TRAN_ID").toString();


                    iTrans.transType = jsonArr.getJSONObject(i).getString("TRAN_TYPE").toString();
                    iTrans.matCode = jsonArr.getJSONObject(i).getString("MAT_CODE").toString();
                    iTrans.matName = jsonArr.getJSONObject(i).getString("MAT_NAME").toString();
                    iTrans.matGrade = jsonArr.getJSONObject(i).getString("GRADE").toString();
                    iTrans.matQty = jsonArr.getJSONObject(i).getString("QTY").toString();
                    iTrans.lotId = jsonArr.getJSONObject(i).getString("LOT_ID").toString();
                    iTrans.barcode = jsonArr.getJSONObject(i).getString("BARCODE").toString();
                    iTrans.stockSeq = jsonArr.getJSONObject(i).getString("STOCK_SEQ").toString();
                    iTrans.stockNo = jsonArr.getJSONObject(i).getString("STOCK_NO").toString();
                    iTrans.chkReserve = jsonArr.getJSONObject(i).getString("CHK_RESERVE").toString();

                    lsData.add(iTrans);
                }
            } else {
                Log.e("JSON", "STAT : FAIL");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", e.getMessage().toString());
        }

        return lsData;
    }

    public List<DocInfo> getPickList() {
        List<DocInfo> lsDoc = new ArrayList<>();
        List<Transaction> lsTrans;

        String sp = c.getString(R.string.sp_get_pick);
        String param = GlobalVar.getInstance().getUserInfo().warehouseId + "," + GlobalVar.getInstance().getUserInfo().branchId;


        String wsRes = callAsynTask(sp, param);

        if (!wsRes.equals("")) {
            lsTrans = transPickParseJSON(wsRes);
            GlobalVar.getInstance().setListPickTrans(lsTrans);
            lsDoc = genDocList(lsTrans);
        }
        return lsDoc;
    }

    public List<ListWorkOrder> getWorkOrder(String type) {
        List<ListWorkOrder> lsWo = new ArrayList<>();
        List<Transaction> lsTrans;


        String sp = "";
        if (type.equals("ISS")) {
            sp = c.getString(R.string.sp_getwo_iss);
        } else if (type.equals("SO")) {
            sp = c.getString(R.string.sp_getwo_so);
        } else if (type.equals("RCV")) {
            sp = c.getString(R.string.sp_getwo_rcv);
        } else {
            return lsWo;
        }
        String param = GlobalVar.getInstance().getUserInfo().warehouseId + "," + GlobalVar.getInstance().getUserInfo().branchId;


        String wsRes = callAsynTask(sp, param);

        if (!wsRes.equals("")) {
            lsWo = workOrderParseJSON(wsRes, type);
        }
        return lsWo;
    }


    public List<ListWorkOrder> workOrderParseJSON(String result, String woType) {
        List<ListWorkOrder> lsData = new ArrayList<>();
        JSONObject jsonObj;
        JSONArray jsonArr;
        int jsonSize;
        String tmpDate;
        ListWorkOrder iTrans;

        try {
            //** data validation***
            jsonObj = new JSONObject(result);
            jsonArr = jsonObj.getJSONArray("Table");
            if (jsonArr.getJSONObject(0).getString("STAT").toString().equals("S")) {
                jsonArr = jsonObj.getJSONArray("Table1");
                jsonSize = jsonArr.length();
                for (int i = 0; i < jsonSize; i++) {
                    iTrans = new ListWorkOrder("", "", "", "", "", "", 0, 0);
                    iTrans.transId = jsonArr.getJSONObject(i).getString("TRAN_ID").toString();
                    iTrans.stockSeq = jsonArr.getJSONObject(i).getString("STOCK_SEQ").toString();
                    tmpDate = jsonArr.getJSONObject(i).getString("TRAN_DATE").toString();
                    if (tmpDate.equals("null")) {
                        iTrans.transDate = "";
                    } else {
                        iTrans.transDate = tmpDate.substring(0, 10).toString();
                    }

                    iTrans.transTypeId = jsonArr.getJSONObject(i).getString("TRAN_TYPE_ID").toString();
                    iTrans.transType = jsonArr.getJSONObject(i).getString("TRAN_TYPE").toString();

                    if (woType.equals("RCV")) {
                        iTrans.poId = jsonArr.getJSONObject(i).getString("PO_ID").toString();
                        if (iTrans.poId.equals("null")) {
                            iTrans.poId = "";
                        } else {
                            iTrans.poId = "PO. " + iTrans.poId;
                        }
                    }

                    lsData.add(iTrans);
                }
            } else {
                Log.e("JSON", "STAT : FAIL");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", e.getMessage().toString());
        }

        return lsData;
    }

    public List<Transaction> transPickParseJSON(String result) {
        List<Transaction> lsData = new ArrayList<>();
        JSONObject jsonObj;
        JSONArray jsonArr;
        int jsonSize;
        String tmpDate;
        Transaction iTrans;

        try {
            //** data validation***
            jsonObj = new JSONObject(result);
            jsonArr = jsonObj.getJSONArray("Table");
            if (jsonArr.getJSONObject(0).getString("STAT").toString().equals("S")) {
                jsonArr = jsonObj.getJSONArray("Table1");
                jsonSize = jsonArr.length();
                for (int i = 0; i < jsonSize; i++) {
                    iTrans = new Transaction("", "", "", "", "", "", "", "", "", "", "", "", "", "");

                    iTrans.transId = jsonArr.getJSONObject(i).getString("TRAN_ID").toString();
                    tmpDate = jsonArr.getJSONObject(i).getString("TRAN_DATE").toString();
                    if (tmpDate.equals("null")) {
                        iTrans.transDate = "";
                    } else {
                        iTrans.transDate = tmpDate.substring(0, 10).toString();
                    }

                    iTrans.transType = jsonArr.getJSONObject(i).getString("TRAN_TYPE").toString();
                    iTrans.matCode = jsonArr.getJSONObject(i).getString("MAT_CODE").toString();
                    iTrans.matName = jsonArr.getJSONObject(i).getString("MAT_NAME").toString();
                    iTrans.matGrade = jsonArr.getJSONObject(i).getString("GRADE").toString();
                    iTrans.matQty = jsonArr.getJSONObject(i).getString("QTY").toString();
                    iTrans.matWeight = jsonArr.getJSONObject(i).getString("WEIGHT_QTY").toString();
                    iTrans.lotId = jsonArr.getJSONObject(i).getString("LOT_ID").toString();
                    iTrans.locId = jsonArr.getJSONObject(i).getString("LOC_ID").toString();
                    iTrans.barcode = jsonArr.getJSONObject(i).getString("BARCODE").toString();
                    iTrans.stockSeq = jsonArr.getJSONObject(i).getString("STOCK_SEQ").toString();
                    iTrans.stockNo = jsonArr.getJSONObject(i).getString("STOCK_NO").toString();

                    lsData.add(iTrans);
                }
            } else {
                Log.e("JSON", "STAT : FAIL");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", e.getMessage().toString());
        }

        return lsData;
    }

    public List<DocInfo> genDocList(List<Transaction> lsVal) {

        List<DocInfo> lsDocTrans = new ArrayList<>();
        DocInfo v;
        for (int i = 0; i < lsVal.size(); i++) {
            if (!foundInTransList(lsDocTrans, lsVal.get(i).transId)) {
                v = new DocInfo("", "", "", "", 0, 0);
                v.docId = lsVal.get(i).transId;
                v.docDate = lsVal.get(i).transDate;
                v.docStatus = "";
                v.docGroup = lsVal.get(i).transType;
                v.allItem = countItemInTrans(lsVal, v.docId);
                v.finItem = 0;
                lsDocTrans.add(v);
            }
        }
        return lsDocTrans;
    }

    private int countItemInTrans(List<Transaction> lsVal, String docId) {
        int c = 0;
        for (int i = 0; i < lsVal.size(); i++) {
            if (lsVal.get(i).transId.equals(docId)) {
                c++;
            }
        }
        return c;
    }

    public boolean foundInTransList(List<DocInfo> lsVal, String v) {

        for (int i = 0; i < lsVal.size(); i++) {
            if (lsVal.get(i).docId.equals(v)) {
                return true;
            }
        }
        return false;
    }
    //end ------------------- getIssueList getPickList -------------------------------------

    //begin ------------------- getItemDetailList -------------------------------------
    public List<ListItemDetail> getIssueItemDetailList(String v) {
        List<Transaction> lsData = GlobalVar.getInstance().getListIssueTrans();
        List<ListItemDetail> lsCollect = new ArrayList<ListItemDetail>();
        ListItemDetail tmpItem;
        String getLoc, loc;
        ListLocation tmpLoc;
        Double d;
        for (int i = 0; i < lsData.size(); i++) {
            if (lsData.get(i).transId.equals(v)) {
                tmpItem = new ListItemDetail("", "", "", "", 0, "", 0, "", 0, 0, "", "", "", "", "", "", "");
                tmpItem.itemId = lsData.get(i).barcode;
                tmpItem.itemDetail = lsData.get(i).matName;
                loc = "LOCATION : ";
                getLoc = getItemInfo(lsData.get(i).barcode).itemLocation;
                if (getLoc.equals("MANY")) {
                    List<ListLocation> lsLoc = getLocationList(lsData.get(i).barcode);

                    for (int j = 0; j < lsLoc.size(); j++) {
                        if (j > 0) {
                            loc = loc + ",";
                        }
                        loc = loc + lsLoc.get(j).locationId + "(" + lsLoc.get(j).locationQty + ")";
                    }

                } else {
                    loc = getLoc;
                }
                tmpItem.itemLocation = loc;
                tmpItem.itemWeight = lsData.get(i).matWeight + " กก.";
                d = Double.parseDouble(lsData.get(i).matQty);
                tmpItem.itemQty = String.valueOf(d.intValue()) + " ม้วน";


                tmpItem.itemAll = d.intValue();
                tmpItem.itemBarcode = lsData.get(i).barcode;
                tmpItem.matCode = lsData.get(i).matCode;
                tmpItem.grade = lsData.get(i).matGrade;
                tmpItem.lotId = lsData.get(i).lotId;
                tmpItem.stockSeq = lsData.get(i).stockSeq;
                tmpItem.stockNo = lsData.get(i).stockNo;
                if (lsData.get(i).chkReserve.equals("F")) {
                    tmpItem.itemAlert = 1;
                } else {
                    tmpItem.itemAlert = 0;
                }
                lsCollect.add(tmpItem);
            }
        }
        return lsCollect;
    }
    //end ------------------- getIssueItemDetailList -------------------------------------

    //begin ------------------- searchPickLocationList -------------------------------------

    public String searchPickLocationList(String matCode, String matGrade) {
        String loc = "";

        for (int i = 0; i < recLocation.size(); i++) {
            if ((recLocation.get(i).matCode.equals(matCode)) && (recLocation.get(i).grade.equals(matGrade))) {
                loc = recLocation.get(i).location;
                return loc;
            }
        }
        return loc;
    }
    //end ------------------- searchIssueItemDetailList -------------------------------------

    //begin ------------------- getPickLocationList -------------------------------------

    public String getPickLocationList(String matCode, String matGrade) {

        String locList = searchPickLocationList(matCode, matGrade);


        if (locList.equals("")) {
            RecLocation tmpRecLocation = new RecLocation(matCode, matGrade, "");
            String sp = c.getString(R.string.sp_getaval_location);
            String param = GlobalVar.getInstance().getUserInfo().warehouseId + "," + GlobalVar.getInstance().getUserInfo().branchId + "," + matCode + "," + matGrade;

            String wsRes = callAsynTask(sp, param);

            if (!wsRes.equals("")) {
                locList = pickLocationListParseJSON(wsRes);
            } else {
                locList = "-";
            }
            tmpRecLocation.location = locList;
            recLocation.add(tmpRecLocation);

        }
        return locList;

    }

    private String pickLocationListParseJSON(String result) {
        String loc = "";
        JSONObject jsonObj;
        JSONArray jsonArr;
        int jsonSize;

        try {
            //** data validation***
            jsonObj = new JSONObject(result);
            jsonArr = jsonObj.getJSONArray("Table");
            if (jsonArr.getJSONObject(0).getString("STAT").toString().equals("S")) {
                jsonArr = jsonObj.getJSONArray("Table1");
                jsonSize = jsonArr.length();
                for (int i = 0; i < jsonSize; i++) {

                    loc = jsonArr.getJSONObject(i).getString("LOCA_LIST").toString();
                }
            } else {
                Log.e("JSON", "STAT : FAIL");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", e.getMessage().toString());
        }

        return loc;
    }
    //end ------------------- + -------------------------------------


    //begin ------------------- getPickItemDetailList -------------------------------------

    public List<ListItemDetail> getPickItemDetailList(String stockSeq) {

        List<ListItemDetail> lsItem = new ArrayList<>();

        String sp = c.getString(R.string.sp_getwo_rcvdetail);
        String param = GlobalVar.getInstance().getUserInfo().warehouseId + "," + GlobalVar.getInstance().getUserInfo().branchId + "," + stockSeq;

        String wsRes = callAsynTask(sp, param);

        if (!wsRes.equals("")) {
            lsItem = pickItemDetailParseJSON(wsRes);

        }
        return lsItem;

    }

    private List<ListItemDetail> pickItemDetailParseJSON(String result) {
        List<ListItemDetail> lsData = new ArrayList<>();
        JSONObject jsonObj;
        JSONArray jsonArr;
        int jsonSize;
        String tmpDate;
        ListItemDetail itm;
        int qty;
        recLocation = new ArrayList<>();
        try {
            //** data validation***
            jsonObj = new JSONObject(result);
            jsonArr = jsonObj.getJSONArray("Table");
            if (jsonArr.getJSONObject(0).getString("STAT").toString().equals("S")) {
                jsonArr = jsonObj.getJSONArray("Table1");
                jsonSize = jsonArr.length();
                for (int i = 0; i < jsonSize; i++) {
                    itm = new ListItemDetail("", "", "", "", 0, "", 0, "", 0, 0, "", "", "", "", "", "", "");

                    itm.matCode = jsonArr.getJSONObject(i).getString("MAT_CODE").toString();
                    itm.matName = jsonArr.getJSONObject(i).getString("MAT_NAME").toString();
                    itm.grade = jsonArr.getJSONObject(i).getString("GRADE").toString();
                    qty = (int) Double.parseDouble(jsonArr.getJSONObject(i).getString("QTY").toString());
                    itm.itemQty = String.valueOf(qty) + " ม้วน";
                    itm.itemAll = qty;
                    itm.itemWeight = jsonArr.getJSONObject(i).getString("WEIGHT_QTY").toString() + " กก.";
                    itm.lotId = jsonArr.getJSONObject(i).getString("LOT_ID").toString();
                    itm.itemLocation = getPickLocationList(itm.matCode, itm.grade);

                    itm.itemBarcode = jsonArr.getJSONObject(i).getString("BARCODE").toString();
                    itm.stockSeq = jsonArr.getJSONObject(i).getString("STOCK_SEQ").toString();
                    itm.stockNo = jsonArr.getJSONObject(i).getString("STOCK_NO").toString();
                    itm.itemDetail = jsonArr.getJSONObject(i).getString("ROLL_INPT").toString(); //cast for ROLL_INPT field

                    lsData.add(itm);
                }
            } else {
                Log.e("JSON", "STAT : FAIL");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", e.getMessage().toString());
        }

        return lsData;
    }
    //end ------------------- + -------------------------------------


    //begin ------------------- saveIssue / Pick -------------------------------------

    public boolean saveIssue(List<StoreItem> lsRoll, List<StoreItem> lsPallet) {
        UserInfo uInfo = GlobalVar.getInstance().getUserInfo();

        String sp = c.getString(R.string.sp_uptwo_issue);
        String param = "";
        String stockSeq = "";
        String tranId = "";
        String palletId = "";
        int cnt = 0;
        String stat;
        if ((lsRoll.size() > 0) || (lsPallet.size() > 0)) {
            cnt = 1;
            for (int i = 0; i < lsRoll.size(); i++) {
                if (cnt > 1) {
                    param = param + "^";
                } else {
                    stockSeq = lsRoll.get(i).stockSeq;
                    tranId = lsRoll.get(i).tranId;
                }
                if (lsRoll.get(i).itemPallet.equals("")) {
                    palletId = " ";
                } else {
                    palletId = lsRoll.get(i).itemPallet;
                }

                param = param + lsRoll.get(i).stockNo + "|" + lsRoll.get(i).matCode + "|" + lsRoll.get(i).grade + "|" + lsRoll.get(i).lotId
                        + "|" + palletId + "|" + lsRoll.get(i).itemLocation + "|" + String.valueOf(lsRoll.get(i).itemQty);
                cnt++;
            }

            for (int i = 0; i < lsPallet.size(); i++) {
                if (cnt > 1) {
                    param = param + "^";
                } else {
                    stockSeq = lsPallet.get(i).stockSeq;
                    tranId = lsPallet.get(i).tranId;
                }
                param = param + lsPallet.get(i).stockNo + "|" + lsPallet.get(i).matCode + "|" + lsPallet.get(i).grade + "|" + lsPallet.get(i).lotId
                        + "|" + lsRoll.get(i).itemPallet + "|" + lsPallet.get(i).itemLocation + "|" + String.valueOf(lsPallet.get(i).itemQty);
                cnt++;
            }

            param = param + "," + stockSeq + "," + tranId + "," + uInfo.warehouseId + "," + uInfo.orgId + "," + uInfo.userLogin + ",|,^,1";

            String wsRes = callAsynTask(sp, param);

            if (!wsRes.equals("")) {
                stat = updateParseJSON(wsRes);
                if (stat.equals("S")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean saveIssueSale(ListSaleOrder soData, List<StoreItem> lsRoll, List<StoreItem> lsPallet) {
        UserInfo uInfo = GlobalVar.getInstance().getUserInfo();

        String sp = c.getString(R.string.sp_uptwo_so);
        String param = "";
        String tfLotDocId = "";
        int cnt = 0;
        String stat;
        if ((lsRoll.size() > 0) || (lsPallet.size() > 0)) {
            if (lsRoll.size() > 0) {
                tfLotDocId = lsRoll.get(0).stockNo;
            } else {
                tfLotDocId = lsPallet.get(0).stockNo;
            }
            cnt = 1;
            for (int i = 0; i < lsRoll.size(); i++) {
                if (cnt > 1) {
                    param = param + "^";
                }
                param = param + lsRoll.get(i).matCode + "|" + lsRoll.get(i).grade + "|" + lsRoll.get(i).lotId
                        + "|" + lsRoll.get(i).itemLocation + "|" + String.valueOf(lsRoll.get(i).itemQty);
                cnt++;
            }

            param = param + ",";
            cnt = 1;
            for (int i = 0; i < lsPallet.size(); i++) {
                if (cnt > 1) {
                    param = param + "^";
                }
                param = param + lsPallet.get(i).itemBarcode + "|" + lsPallet.get(i).itemLocation + "|"
                        + c.getString(R.string.sale_location);
                cnt++;
            }

            param = param + "," + tfLotDocId + "," + soData.soId + "," + soData.soSeq + "," + soData.seqDeli + "," + uInfo.warehouseId + "," + uInfo.orgId + "," + uInfo.userLogin + ",|,^,1";

            String wsRes = callAsynTask(sp, param);

            if (!wsRes.equals("")) {
                stat = updateParseJSON(wsRes);
                if (stat.equals("S")) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean savePick(String loc, List<ListItemDetail> iDetail) {
        UserInfo uInfo = GlobalVar.getInstance().getUserInfo();

        String sp = "";
        String param = "";
        int cnt = 1;
        String stat;
        if (iDetail.size() > 0) {

            param = "";
            cnt = 1;
            for (int i = 0; i < iDetail.size(); i++) {
                if (iDetail.get(i).itemFin > 0) {
                    if (cnt > 1) {
                        param = param + "^";
                    }
                    param = param + iDetail.get(i).stockSeq + "|" + iDetail.get(i).stockNo + "|" + loc;
                    cnt++;
                }

            }
            param = param + "," + uInfo.warehouseId + "," + uInfo.orgId + "," + uInfo.userLogin + "," + uInfo.imei + ",|,^,1";
            sp = c.getString(R.string.sp_uptwo_rcv);
            String wsRes = callAsynTask(sp, param);
            if (!wsRes.equals("")) {
                stat = updateParseJSON(wsRes);
                if (stat.equals("S")) {
                    return true;
                }
            }

        }
        return false;
    }

    //end ------------------- saveIssue /Pick -------------------------------------

    //begin ------------------- sendProdRcv -------------------------------------

    public boolean sendProdRcv(String destLocation, List<ProdRcv> lsRoll, List<ProdRcv> lsPallet) {
        UserInfo uInfo = GlobalVar.getInstance().getUserInfo();
        String sp;
        String param;
        String stat;
        String wsRes;
        int cnt;

        if (lsRoll.size() > 0) {
            sp = c.getString(R.string.sp_send_prodrcv_roll);
            param = "";
            cnt = 1;
            for (int i = 0; i < lsRoll.size(); i++) {
                if (cnt > 1) {
                    param = param + "^";
                }
                param = param + lsRoll.get(i).barcode + "|" + destLocation;
                cnt++;
            }
            param = param + "," + uInfo.branchId + "," + uInfo.warehouseId + ",," + uInfo.orgId + "," + uInfo.userLogin + ",|,^,1";
            wsRes = callAsynTask(sp, param);

            if (!wsRes.equals("")) {
                stat = updateParseJSON(wsRes);
                if (!stat.equals("S")) {
                    return false;
                }
            }
        }

        if (lsPallet.size() > 0) {
            sp = c.getString(R.string.sp_send_prodrcv_pallet);
            param = "";
            cnt = 1;
            for (int i = 0; i < lsPallet.size(); i++) {

                if (cnt > 1) {
                    param = param + "^";
                }
                param = param + lsPallet.get(i).palletId + "|" + destLocation;
                cnt++;

            }
            param = param + "," + uInfo.branchId + "," + uInfo.warehouseId + ",," + uInfo.orgId + "," + uInfo.userLogin + ",|,^,1";
            wsRes = callAsynTask(sp, param);

            if (!wsRes.equals("")) {
                stat = updateParseJSON(wsRes);
                if (!stat.equals("S")) {
                    return false;
                }
            }
        }
        return true;
    }

    //end ------------------- sendProdRcv /Pick -------------------------------------

    //begin ------------------- sendCheckItem -------------------------------------
    public Boolean sendCheckItem(String loc, List<ItemStockInfo> iInfo) {

        UserInfo uInfo = GlobalVar.getInstance().getUserInfo();

        int cnt;
        String stat;
        String sp = c.getString(R.string.sp_send_check);
        String param = "";
        String location = "";

        String stockLog = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy HH:mm");
        SimpleDateFormat dateFN = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String dateFileName = dateFN.format(now);
        String dateString = sdf.format(now);

        if (iInfo.size() > 0) {
            param = loc + "," + uInfo.warehouseId + "," + uInfo.branchId + ",";
            location = uInfo.branchId + "^" + uInfo.warehouseId + "^" + loc;
            cnt = 1;
            for (int i = 0; i < iInfo.size(); i++) {

                stockLog = stockLog + dateString + "^" + location + "^" + iInfo.get(i).itemBarcode + "^" + String.valueOf(iInfo.get(i).itemQty) + "^" + String.valueOf(iInfo.get(i).itemCount)
                        + "^" + iInfo.get(i).itemStatus + "^" + iInfo.get(i).itemWeight + "^" + iInfo.get(i).matCode + "^" + iInfo.get(i).grade + "^" + iInfo.get(i).lotId
                        + "^" + iInfo.get(i).matCat + "^" + iInfo.get(i).matType + "^" + iInfo.get(i).matFml + "^" + iInfo.get(i).itemThick + "^" + iInfo.get(i).itemWidth
                        + "^" + iInfo.get(i).unitWidth + "^" + iInfo.get(i).itemLength + "^" + iInfo.get(i).unitLength + "^" + iInfo.get(i).core + "^" + iInfo.get(i).joint
                        + "^" + iInfo.get(i).matSpec1 + "^" + iInfo.get(i).matSpec2 + "^" + iInfo.get(i).itemName + "\n";

                //stockRecLog(location + iInfo.get(i).itemBarcode + "," + String.valueOf(iInfo.get(i).itemQty) + "," + String.valueOf(iInfo.get(i).itemCount));
                /*if (iInfo.get(i).itemQty > 0) {
                    if (cnt > 1) {
                        param = param + "^";
                    }
                    param = param + String.valueOf(cnt) + "|" + iInfo.get(i).itemBarcode + "|" + String.valueOf(iInfo.get(i).itemQty) + "|" + String.valueOf(iInfo.get(i).itemCount);
                    cnt++;
                }*/
            }

            if (stockRecLog(stockLog)) {
                return true;
            } else {
                return false;
            }

            /*cnt = 1;
            param = param + ",";
            for (int i = 0; i < iInfo.size(); i++) {
                if ((iInfo.get(i).itemCount > 0) && (iInfo.get(i).itemQty == 0)) {
                    if (cnt > 1) {
                        param = param + "^";
                    }
                    param = param + String.valueOf(cnt) + "|" + iInfo.get(i).itemBarcode + "|" + String.valueOf(iInfo.get(i).itemQty) + "|" + String.valueOf(iInfo.get(i).itemCount);
                    cnt++;
                }

            }
            param = param + ",|,^," + uInfo.userLogin;

            String wsRes = callAsynTask(sp, param);

            if (!wsRes.equals("")) {
                stat = updateParseJSON(wsRes);
                if (stat.equals("S")) {
                    return true;
                }
            }*/

        }
        return false;
    }

    //end ------------------- sendCheckItem -------------------------------------


    //begin ------------------- sendTransferItem -------------------------------------
    public Boolean sendTransferItem(String destType, String newDest, String tType, List<ItemInfo> iInfo) {

        UserInfo uInfo = GlobalVar.getInstance().getUserInfo();
        List<ItemInfo> lsRoll = new ArrayList<>();
        List<ItemInfo> lsPallet = new ArrayList<>();
        int cnt;
        String newLoc = "";
        String newPallet = "";
        String stat;
        String sp = "";
        String param = "";
        String wsRes;
        String palletId = "";
        boolean updResult = false;

        for (int i = 0; i < iInfo.size(); i++) {
            if (iInfo.get(i).itemType.equals("ม้วน")) {
                lsRoll.add(iInfo.get(i));
            } else if (iInfo.get(i).itemType.equals("PALLET")) {
                lsPallet.add(iInfo.get(i));
            }
        }

        if (destType.equals("KPPSND")) {
            newLoc = newDest;


            if (lsRoll.size() > 0) {
                sp = c.getString(R.string.sp_move_roll_tokpp);
                for (int i = 0; i < lsRoll.size(); i++) {
                    if (i > 0) {
                        param = param + "^";
                    }

                    if (lsRoll.get(i).itemPallet.equals("")) {
                        palletId = " ";
                    } else {
                        palletId = lsRoll.get(i).itemPallet;
                    }

                    param = param + lsRoll.get(i).matCode + "|" + lsRoll.get(i).grade + "|" + lsRoll.get(i).lotId
                            + "|" + lsRoll.get(i).itemLocation + "|" + palletId + "|" + lsRoll.get(i).itemCount;

                }
                param = param + "," + GlobalVar.getInstance().getUserInfo().warehouseId + "," + newLoc + "," + newPallet + ","
                        + GlobalVar.getInstance().getUserInfo().orgId + "," + GlobalVar.getInstance().getUserInfo().userLogin + ",|,^,1";

                wsRes = callAsynTask(sp, param);

                if (!wsRes.equals("")) {
                    stat = updateParseJSON(wsRes);
                    if (stat.equals("S")) {
                        updResult = true;
                    }
                }
            }

            if (lsPallet.size() > 0) {
                param = "";
                sp = c.getString(R.string.sp_move_pallet_tokpp);
                for (int i = 0; i < lsPallet.size(); i++) {
                    if (i > 0) {
                        param = param + "^";
                    }
                    param = param + lsPallet.get(i).itemBarcode + "|" + lsPallet.get(i).itemLocation + "|" + newLoc;

                }
                param = param + "," + GlobalVar.getInstance().getUserInfo().warehouseId + "," + GlobalVar.getInstance().getUserInfo().orgId + "," + GlobalVar.getInstance().getUserInfo().userLogin + ",|,^,1";

                wsRes = callAsynTask(sp, param);

                if (!wsRes.equals("")) {
                    stat = updateParseJSON(wsRes);
                    if (stat.equals("S")) {
                        updResult = true;
                    }
                }
            }
        } else {
            if (destType.equals("LOCATION")) {
                newLoc = newDest;
            } else if (destType.equals("PALLET")) {
                newPallet = newDest;
            }


            if (lsRoll.size() > 0) {
                sp = c.getString(R.string.sp_move_roll_location);
                for (int i = 0; i < lsRoll.size(); i++) {
                    if (i > 0) {
                        param = param + "^";
                    }

                    if (lsRoll.get(i).itemPallet.equals("")) {
                        palletId = " ";
                    } else {
                        palletId = lsRoll.get(i).itemPallet;
                    }
                    param = param + lsRoll.get(i).matCode + "|" + lsRoll.get(i).grade + "|" + lsRoll.get(i).lotId
                            + "|" + lsRoll.get(i).itemLocation + "|" + palletId + "|" + lsRoll.get(i).itemCount;

                }
                param = param + "," + GlobalVar.getInstance().getUserInfo().warehouseId + "," + newLoc + "," + newPallet + ","
                        + GlobalVar.getInstance().getUserInfo().orgId + "," + GlobalVar.getInstance().getUserInfo().userLogin + ",|,^,1";

                wsRes = callAsynTask(sp, param);

                if (!wsRes.equals("")) {
                    stat = updateParseJSON(wsRes);
                    if (stat.equals("S")) {
                        updResult = true;
                    }
                }
            }


            if (lsPallet.size() > 0) {
                param = "";
                sp = c.getString(R.string.sp_move_pallet_location);
                for (int i = 0; i < lsPallet.size(); i++) {
                    if (i > 0) {
                        param = param + "^";
                    }
                    param = param + lsPallet.get(i).itemBarcode + "|" + lsPallet.get(i).itemLocation + "|" + newLoc + "|" + lsPallet.get(i).itemCount;

                }
                param = param + "," + GlobalVar.getInstance().getUserInfo().warehouseId + "," + GlobalVar.getInstance().getUserInfo().orgId + "," + GlobalVar.getInstance().getUserInfo().userLogin + ",|,^,1";

                wsRes = callAsynTask(sp, param);

                if (!wsRes.equals("")) {
                    stat = updateParseJSON(wsRes);
                    if (stat.equals("S")) {
                        updResult = true;
                    }
                }
            }
        }

        if (updResult) {
            return true;
        } else {
            return false;
        }
    }

    //end ------------------- sendTransferItem -------------------------------------


    //begin ------------------- sendPalletItem -------------------------------------
    public Boolean sendPalletItem(String oldPallet, String newPallet, List<ItemInfo> iInfo) {

        UserInfo uInfo = GlobalVar.getInstance().getUserInfo();

        int cnt;
        String stat;
        String sp = c.getString(R.string.sp_move_roll_pallet);
        String param = "";

        if (iInfo.size() > 0) {

            cnt = 1;
            for (int i = 0; i < iInfo.size(); i++) {
                if (cnt > 1) {
                    param = param + "^";
                }
                param = param + String.valueOf(cnt) + "|" + iInfo.get(i).itemBarcode + "|" + iInfo.get(i).matCode + "|" + iInfo.get(i).grade + "|" + iInfo.get(i).lotId + "|" + String.valueOf(iInfo.get(i).itemQty) + "|" + String.valueOf(iInfo.get(i).itemCount);
                cnt++;

            }
            param = param + "," + uInfo.warehouseId + ",," + oldPallet + ",," + newPallet + "," + uInfo.orgId + "," + uInfo.userLogin + ",|,^,1";
        } else {
            return false;
        }

        serviceAsynTask wsInit = new serviceAsynTask();
        AsyncTask<String, Integer, String> wsResult = wsInit.execute();
        try {
            String wsData = wsResult.get();
            Log.i("Webservice", "sendCheck : " + wsData);
            stat = updateParseJSON(wsData);
            if (stat.equals("S")) {
                return true;
            } else {
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        }

    }

    //end ------------------- sendPalletItem -------------------------------------


    public String updateParseJSON(String result) {
        String res, status;
        JSONObject jsonObj;
        JSONArray jsonArr;
        int jsonSize;

        try {
            //** data validation***
            jsonObj = new JSONObject(result);
            jsonArr = jsonObj.getJSONArray("header");
            status = jsonArr.getJSONObject(0).getString("Status").toString();
            if (status.equals("Success")) {
                return "S";
            } else {
                return "E";
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", e.getMessage().toString());
            return "F";
        }
    }

    public String updateTableParseJSON(String result) {
        String res, status;
        JSONObject jsonObj;
        JSONArray jsonArr;
        int jsonSize;

        try {
            //** data validation***
            jsonObj = new JSONObject(result);

            jsonArr = jsonObj.getJSONArray("Table");
            status = jsonArr.getJSONObject(0).getString("STAT").toString();

            if (status.equals("S")) {
                return "S";
            } else {
                return "E";
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON", e.getMessage().toString());
            return "F";
        }
    }

    public boolean loadConfigData() {
        String filename = c.getString(R.string.config_filename);
        try {
            File cfgFolder = new File(Environment.getExternalStorageDirectory(), "ThaiOPP");
            File cfgFile = new File(cfgFolder, filename);
            if (!cfgFolder.exists()) {
                cfgFolder.mkdir();
                Log.i("Config", "STAT : Create directory");
            }
            if (!cfgFile.exists()) {
                cfgFile.createNewFile();
                Log.i("Config", "STAT : Create file");
            }

            // Open the file that is the first
            // command line parameter
            FileInputStream fstream = new FileInputStream(cfgFile);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            ConfigSetting val = new ConfigSetting("", "", "", "", 0, "", "");
            while (true) {
                final String line = br.readLine();
                if (line == null) break;

                if (line.contains("[URL]")) {
                    val.url = line.substring(line.lastIndexOf("]") + 1);
                } else if (line.contains("[SOAP_ACTION]")) {
                    val.soapAction = line.substring(line.lastIndexOf("]") + 1);
                } else if (line.contains("[OPERATION_NAME]")) {
                    val.operationName = line.substring(line.lastIndexOf("]") + 1);
                } else if (line.contains("[NAMESPACE]")) {
                    val.nameSpace = line.substring(line.lastIndexOf("]") + 1);
                } else if (line.contains("[TIMEOUT]")) {
                    val.timeout = Integer.parseInt(line.substring(line.lastIndexOf("]") + 1));
                } else if (line.contains("[PIN]")) {
                    val.pin = line.substring(line.lastIndexOf("]") + 1);
                } else if (line.contains("[REC_LOG]")) {
                    val.recLog = line.substring(line.lastIndexOf("]") + 1);
                }
            }
            in.close();

            GlobalVar.getInstance().setConfigSetting(val);
            Log.i("Config", "STAT : Set config");
            if (!val.soapAction.equals("")) {
                return true;
            }

        } catch (Exception e) {//Catch exception if any
            Toast.makeText(c, "Exception :" + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.i("Config", "STAT : Create config error");
        }
        return false;
    }

    public void recordLog(String log) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy HH:mm");
        SimpleDateFormat dateFN = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String dateFileName = dateFN.format(now);


        String filename = c.getString(R.string.log_filename) + dateFileName + c.getString(R.string.log_extension);

        try {
            File cfgFolder = new File(Environment.getExternalStorageDirectory(), "ThaiOPP");
            if (!cfgFolder.exists()) {
                cfgFolder.mkdir();
            }
            try {
                File cfgFile = new File(cfgFolder, filename);
                if (!cfgFile.exists()) {
                    cfgFile.createNewFile();
                }

                try {
                    FileWriter fw = new FileWriter(cfgFile, true);
                    String dateString = sdf.format(now);
                    fw.write(dateString + "\n" + log + "\n\n");
                    fw.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } catch (Exception ex) {
                System.out.println("ex: " + ex);
            }
        } catch (Exception e) {
            System.out.println("e: " + e);
        }
        MediaScannerConnection.scanFile(this.c, new String[]{Environment.getExternalStorageDirectory().getPath() + "/ThaiOPP"}, null, null);
    }

    public boolean stockRecLog(String log) {

        //SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy HH:mm");
        SimpleDateFormat dateFN = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String dateFileName = dateFN.format(now);
        Boolean newFile = false;


        String filename = c.getString(R.string.stockreclog_filename) + dateFileName + c.getString(R.string.csv_extension);

        try {
            File logFolder = new File(Environment.getExternalStorageDirectory(), "ThaiOPPStockRec");
            if (!logFolder.exists()) {
                logFolder.mkdir();
            }
            try {
                File logFile = new File(logFolder, filename);
                if (!logFile.exists()) {
                    logFile.createNewFile();
                    newFile = true;
                }

                try {
                    FileWriter fw = new FileWriter(logFile, true);
                    //String dateString = sdf.format(now);
                    if (newFile) {
                        fw.write("Date-Time^Branch Id^Warehouse Id^Location^Barcode^itemQty^itemCount^STATUS^Kg.^mat_code^grade^lot_id^mat_category_name^mat_type_name^mat_fml_name^thick^width^unit_width^length^unit_length^core^joint^mat_spec1_name^mat_spec2_name^mat_name\n");
                    }
                    fw.write(log);
                    fw.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return false;
                }
            } catch (Exception ex) {
                System.out.println("ex: " + ex);
                return false;
            }
        } catch (Exception e) {
            System.out.println("e: " + e);
            return false;
        }
        MediaScannerConnection.scanFile(this.c, new String[]{Environment.getExternalStorageDirectory().getPath() + "/ThaiOPPStockRec"}, null, null);
        return true;
    }

    public String getIMEI(Context context) {

        TelephonyManager mngr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String imei = mngr.getDeviceId();
        return imei;

    }
}
