package com.thaiopp.thaioppwarehouse;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.thaiopp.utils.DataUtil;
import com.thaiopp.vars.ItemInfo;
import com.thaiopp.vars.ListItemDetail;
import com.thaiopp.vars.ListLocation;
import com.thaiopp.vars.ListSaleOrder;
import com.thaiopp.vars.StoreItem;

import java.util.ArrayList;
import java.util.List;

public class IssueSaleDetailActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context c;
    private View dialogView;
    private TextView txtSoId;
    private TextView txtSoDetail;
    private boolean haveUpdate;
    private EditText edtBarcode;
    private EditText edtQty;
    private CheckBox cbCheck;
    private ProgressBar prgBar;

    private ListView listView;

    private List<ListItemDetail> lsItemDetail;
    private List<String> saveBarcode;
    private List<String> saveBarcodeType;
    private ListLocation locInfo = new ListLocation("", "", 0);
    private DataUtil data;
    private ListSaleOrder soData;

    private IssueSaleItemDetailAdapter cAdapter;

    private List<StoreItem> lsRoll = new ArrayList<>();
    private List<StoreItem> lsPallet = new ArrayList<>();

    private final static int SHOW_PROGRESSBAR = 1;
    private final static int HIDE_PROGRESSBAR = 0;

    private final Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case SHOW_PROGRESSBAR:
                    showProgressBar();
                    break;
                case HIDE_PROGRESSBAR:
                    hideProgressBar();
                    break;
            }
        }


    };

    private void hideProgressBar() {
        prgBar.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        prgBar.setVisibility(View.VISIBLE);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.issuesale_detaillist_layout);
        c = getApplicationContext();
        data = new DataUtil(c);
        Intent intent = getIntent();
        soData = new ListSaleOrder("", "", "", "", "");
        soData.soId = intent.getStringExtra("soId");
        soData.soSeq = intent.getStringExtra("soSeq");
        soData.seqDeli = intent.getStringExtra("seqDeli");
        soData.tfLotDocId = intent.getStringExtra("tfLot");
        soData.deliDate = intent.getStringExtra("deliDate");
        haveUpdate = false;


        saveBarcode = new ArrayList<String>();
        saveBarcodeType = new ArrayList<String>();

        bindWidgets();


        setEvents();

        txtSoId.setText(soData.soId);
        txtSoDetail.setText("TF DOC : " + soData.tfLotDocId + "    DATE : " + soData.deliDate);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        new datagenAsynTask().execute();
    }

    private void dataGen(String id) {
        myHandler.sendEmptyMessage(SHOW_PROGRESSBAR);

        lsItemDetail = data.getSoDetailList(soData);
        listView = (ListView) findViewById(R.id.lvItemDetail);
        listView.setAdapter(new IssueSaleItemDetailAdapter(this, lsItemDetail));
        myHandler.sendEmptyMessage(HIDE_PROGRESSBAR);
    }

    private int itemSearch(String searchId) {
        for (int i = 0; i < lsItemDetail.size(); i++) {
            if (lsItemDetail.get(i).itemBarcode.equals(searchId)) {
                return i;
            }
        }
        return -1;
    }

    private void setEvents() {

        edtBarcode.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    myHandler.sendEmptyMessage(SHOW_PROGRESSBAR);
                    String id = edtBarcode.getText().toString().toUpperCase().trim();
                    processBarcode(id);
                    return true;
                }
                return false;
            }
        });
        edtBarcode.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String tmp = s.toString();
                if ((tmp.length() > 0) && (tmp.indexOf("\n") > 0)) {
                    String id = edtBarcode.getText().toString().toUpperCase().trim();
                    processBarcode(id);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        edtQty.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    edtQty.setText("");
                    edtQty.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(edtQty, InputMethodManager.SHOW_IMPLICIT);
                }
                return true; // return is important...
            }
        });

        edtQty.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (edtQty.getText().toString().equals("")) {
                        edtQty.setText("1");
                    }
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtQty.getWindowToken(), 0);
                    edtBarcode.requestFocus();
                    return true;
                }
                return false;
            }
        });

        edtQty.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (edtQty.getText().toString().equals("")) {
                        edtQty.setText("1");
                    }
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtQty.getWindowToken(), 0);
                    edtBarcode.requestFocus();
                    return true;
                }
                return false;
            }
        });


    }

    private boolean checkLocationRule(String itemLocation) {
        if (itemLocation.equals("null")) {
            alertMessage(getString(R.string.msg_location_null));
            return false;
        } else if (itemLocation.equals("KPPSND")) {
            alertMessage(getString(R.string.msg_kpp_notallow));
            return false;
        } else {
            return true;
        }
    }


    private void bindWidgets() {
        txtSoId = (TextView) findViewById(R.id.textSoId);
        txtSoDetail = (TextView) findViewById(R.id.textSoDetail);

        edtBarcode = (EditText) findViewById(R.id.editBarcode);
        edtQty = (EditText) findViewById(R.id.editQty);
        prgBar = (ProgressBar) findViewById(R.id.progressBar);
        cbCheck = (CheckBox) findViewById(R.id.itemCheckBox);
    }

    private void processBarcode(String id) {
        myHandler.sendEmptyMessage(SHOW_PROGRESSBAR);
        int searchResult = itemSearch(id);
        ItemInfo iInfo = data.getItemInfo(id);
        if (searchResult >= 0) {
            if (!(lsItemDetail.get(searchResult).itemAlert == 1)) {
                if (checkLocationRule(iInfo.itemLocation)) {
                    StoreItem itm = new StoreItem("", "", "", "", "", "", 0, "", "", "", "", "", "");
                    itm.itemId = lsItemDetail.get(searchResult).itemId;
                    itm.itemBarcode = id;
                    itm.tranId = soData.soId;
                    itm.itemType = iInfo.itemType;
                    itm.itemLocation = iInfo.itemLocation;
                    if (!iInfo.itemId.equals("")) {
                        if (itm.itemType.equals("ม้วน")) {
                            itm.stockNo = lsItemDetail.get(searchResult).stockNo;
                            itm.stockSeq = lsItemDetail.get(searchResult).stockSeq;
                            itm.matCode = lsItemDetail.get(searchResult).matCode;
                            itm.grade = lsItemDetail.get(searchResult).grade;
                            itm.lotId = lsItemDetail.get(searchResult).lotId;
                            itm.itemQty = Integer.parseInt(edtQty.getText().toString());
                        } else if (itm.itemType.equals("PALLET")) {
                            itm.stockNo = lsItemDetail.get(searchResult).stockNo;
                            itm.stockSeq = lsItemDetail.get(searchResult).stockSeq;
                            itm.itemQty = 1;
                            iInfo.itemQty = 1;
                        }
                        if (itm.itemLocation.equals("MANY")) {
                            //-- Identify Location --
                            selectLocationDialog(itm, searchResult);

                        } else {
                            if (itm.itemQty <= iInfo.itemQty) {
                                processIssue(itm, searchResult);
                            } else {
                                alertMessage(getString(R.string.msg_exceed_locqty));
                            }
                        }

                        haveUpdate = true;


                    } else {
                        alertMessage(getString(R.string.msg_notfound));
                    }
                }
            } else {
                alertMessage(getString(R.string.msg_reserve));
            }
        } else {
            alertInfo(getString(R.string.msg_notfound_iss), iInfo);
        }
        myHandler.sendEmptyMessage(HIDE_PROGRESSBAR);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        this.runOnUiThread(updList);
        edtBarcode.setText("");
        edtBarcode.requestFocus();

    }

    private Runnable updList = new Runnable() {
        public void run() {
            updateListView();

        }
    };


    private void alertMessage(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setIcon(R.drawable.ic_warning);

        alertDialogBuilder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void alertInfo(String header, ItemInfo itm) {

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .customView(R.layout.info_dialog, true)
                .positiveText(R.string.txt_close)
                .build();

        TextView tvHeader = (TextView) dialog.getCustomView().findViewById(R.id.txtHeader);
        TextView tvInfo1 = (TextView) dialog.getCustomView().findViewById(R.id.txtInfo1);
        TextView tvInfo2 = (TextView) dialog.getCustomView().findViewById(R.id.txtInfo2);
        TextView tvInfo3 = (TextView) dialog.getCustomView().findViewById(R.id.txtInfo3);

        tvHeader.setText(header);
        tvInfo1.setText(itm.itemBarcode + " [" + itm.itemLocation + "]");
        tvInfo2.setText(itm.itemDetail);
        tvInfo3.setText(itm.itemDetail2);
        dialog.show();

    }

    private void processIssue(StoreItem iInfo, int idx) {
        int pos;
        int lmtQty = 0;
        if (iInfo.itemType.equals("ม้วน")) {
            pos = searchInRollList(iInfo.itemBarcode, iInfo.itemLocation, iInfo.itemPallet);
            if (pos > -1) {
                lmtQty = lsItemDetail.get(idx).itemAll - (lsItemDetail.get(idx).itemFin - lsRoll.get(pos).itemQty);
                if (lmtQty >= iInfo.itemQty) {
                    lsItemDetail.get(idx).itemFin = (lsItemDetail.get(idx).itemAll - lmtQty) + iInfo.itemQty;
                    lsRoll.set(pos, iInfo);
                    swapToTop(idx);
                    updateListView();
                } else {
                    alertMessage(getString(R.string.msg_exceed_qty));
                }
            } else {
                lmtQty = lsItemDetail.get(idx).itemAll - lsItemDetail.get(idx).itemFin;
                if (lmtQty >= iInfo.itemQty) {
                    lsItemDetail.get(idx).itemFin = lsItemDetail.get(idx).itemFin + iInfo.itemQty;
                    lsRoll.add(iInfo);
                    swapToTop(idx);
                } else {
                    alertMessage(getString(R.string.msg_exceed_qty));
                }
            }

        } else if (iInfo.itemType.equals("PALLET")) {
            pos = searchInPalletList(iInfo.itemBarcode, iInfo.itemLocation);
            if (pos > -1) {
                alertMessage(getString(R.string.msg_already_check));
            } else {
                lsItemDetail.get(idx).itemFin = 1;
                lsPallet.add(iInfo);
                swapToTop(idx);
            }
        }

        View edtView = getCurrentFocus();
        if (edtView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtView.getWindowToken(), 0);
        }
        updateListView();
    }

    private void swapToTop(int idx) {
        ListItemDetail tmp;
        tmp = lsItemDetail.get(idx);
        lsItemDetail.remove(idx);
        lsItemDetail.add(0, tmp);
    }

    private int searchInRollList(String itemBarcode, String itemLocation, String itemPallet) {
        for (int i = 0; i < lsRoll.size(); i++) {
            if ((lsRoll.get(i).itemBarcode.equals(itemBarcode)) && (lsRoll.get(i).itemLocation).equals(itemLocation) && (lsRoll.get(i).itemPallet).equals(itemPallet)) {
                return i;
            }
        }
        return -1;
    }

    private int searchInPalletList(String itemBarcode, String itemLocation) {
        for (int i = 0; i < lsPallet.size(); i++) {
            if ((lsPallet.get(i).itemBarcode.equals(itemBarcode)) && (lsPallet.get(i).itemLocation).equals(itemLocation)) {
                return i;
            }
        }
        return -1;
    }

    private void selectLocationDialog(final StoreItem iInfo, final int idx) {
        final String bc = iInfo.itemBarcode;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.destination_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edtLoc = (EditText) dialogView.findViewById(R.id.dest_input);
        edtLoc.setText("");
        dialogBuilder.setTitle(getString(R.string.msg_req_destlocation));
        dialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ListLocation loc = checkLocation(bc, edtLoc.getText().toString().toUpperCase().trim());
                if (!loc.locationId.equals("")) {
                    if (loc.locationQty >= iInfo.itemQty) {
                        locInfo = loc;
                        iInfo.itemLocation = loc.locationId;
                        iInfo.itemPallet = loc.palletId;
                        processIssue(iInfo, idx);

                    } else {
                        alertMessage(getString(R.string.msg_exceed_locqty));
                    }
                } else {
                    alertMessage(getString(R.string.msg_location_notfound));
                }

            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });

        final AlertDialog b = dialogBuilder.create();


        b.show();
        edtLoc.requestFocus();

        edtQty.setText("1");
        edtBarcode.setText("");
        edtBarcode.requestFocus();

    }

    private ListLocation checkLocation(String barcode, String location) {
        List<ListLocation> lsLoc = data.getLocationList(barcode);
        ListLocation tmpLoc = new ListLocation("", "", 0);
        for (int i = 0; i < lsLoc.size(); i++) {
            if ((lsLoc.get(i).locationId.equals(location)) || (lsLoc.get(i).palletId.equals(location))) {
                tmpLoc.locationId = lsLoc.get(i).locationId;
                tmpLoc.locationQty = lsLoc.get(i).locationQty;
                tmpLoc.palletId = lsLoc.get(i).palletId;
                return tmpLoc;
            }
        }
        return tmpLoc;
    }

    public void updateListView() {

        cAdapter = new IssueSaleItemDetailAdapter(this, lsItemDetail);
        listView.setAdapter(cAdapter);
        cAdapter.notifyDataSetChanged();
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_action_save, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save:
                updateData();
                return true;

            case R.id.action_cancel:
                closeActivity();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void updateData() {
        DataUtil data = new DataUtil(c);
        if (data.saveIssueSale(soData, lsRoll, lsPallet)) {
            Toast.makeText(IssueSaleDetailActivity.this, getString(R.string.msg_save_success), Toast.LENGTH_LONG).show();
            lsRoll = new ArrayList<>();
            lsPallet = new ArrayList<>();
            haveUpdate = false;
            this.finish();
        } else {
            alertMessage(getString(R.string.msg_save_error));
        }
    }

    private void closeActivity() {
        if (haveUpdate) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(R.string.msg_haveupdate);

            alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    finish();
                }
            });

            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            this.finish();
        }
    }


    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            finish();

        } else if (id == R.id.nav_issue) {
            startActivity(new Intent(getApplicationContext(), IssueMainActivity.class));
            finish();

        } else if (id == R.id.nav_issuesale) {
            prgBar.setVisibility(View.VISIBLE);
            startActivity(new Intent(getApplicationContext(), IssueSaleMainActivity.class));
            finish();

        } else if (id == R.id.nav_pick) {
            startActivity(new Intent(getApplicationContext(), PickMainActivity.class));
            finish();

        } else if (id == R.id.nav_prodpick) {
            startActivity(new Intent(getApplicationContext(), ProdRcvMainActivity.class));
            finish();

        } else if (id == R.id.nav_transfer) {
            startActivity(new Intent(getApplicationContext(), TransferMainActivity.class));
            finish();

        } else if (id == R.id.nav_search) {
            startActivity(new Intent(getApplicationContext(), SearchMainActivity.class));
            finish();

        } else if (id == R.id.nav_check) {
            startActivity(new Intent(getApplicationContext(), CheckMainActivity.class));
            finish();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

//************** Webservice AsyncTask ********************


//begin ---------- datagenAsynTask ------------------

    public class datagenAsynTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {

            return null;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            prgBar.setVisibility(View.VISIBLE);
        }


        protected void onProgressUpdate() {
            super.onProgressUpdate();
        }

        protected void onPostExecute(Void param) {
            dataGen(soData.soId);
            prgBar.setVisibility(View.GONE);
        }
    }
//end ---------- datagenAsynTask ------------------


//********************************************************
}
