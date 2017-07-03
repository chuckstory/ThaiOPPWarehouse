package com.thaiopp.thaioppwarehouse;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.thaiopp.utils.DataUtil;
import com.thaiopp.vars.ItemInfo;
import com.thaiopp.vars.ListLocation;

import java.util.ArrayList;
import java.util.List;


public class TransferMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private View dialogView;
    private TextView txtLocId;
    private TextView txtLocDetail;
    private boolean haveUpdate;
    private DataUtil data;

    private ListView listView;
    private TransferListAdapter cAdapter;

    private EditText edtBarcode;
    private EditText edtQty;
    private ProgressBar prgBar;

    private String locationId = "";
    private String destLocation = "";
    private String transType = "";
    private boolean havePallet = false; // flag to check if there is any pallet in list : cannot set destination to pallet


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

    private List<ItemInfo> lsItemTrans = new ArrayList<ItemInfo>();
    private ListLocation locInfo = new ListLocation("", "", 0);


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transfer_layout);

        Intent intent = getIntent();

        haveUpdate = false;

        bindWidgets();
        setEvents();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        listView = (ListView) findViewById(R.id.lvCheckItem);
        cAdapter = new TransferListAdapter(this, lsItemTrans);
        listView.setAdapter(cAdapter);
        edtBarcode.requestFocus();
        View edtView = this.getCurrentFocus();
        if (edtView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtView.getWindowToken(), 0);
        }
    }

    private void processTransfer(ItemInfo iInfo, int cnt) {
        int pos = searchListFromBarcode(iInfo);
        if (pos == -1) { // not in List : add new
            if (iInfo.itemQty >= cnt) { // not exceed limit qty  : add new
                iInfo.itemCount = cnt;
                lsItemTrans.add(0, iInfo);
                haveUpdate = true;
                if (iInfo.itemType.equals("PALLET"))
                    havePallet = true;
                updateListView(lsItemTrans);
            } else { // exceed limit qty
                alertMessage(getString(R.string.msg_exceed_locqty));
            }
        } else { // found in list : update count
            ItemInfo updItem = lsItemTrans.get(pos);

            int total = updItem.itemCount + cnt;

            if (updItem.itemQty >= total) {
                updItem.itemCount = total;
                lsItemTrans.set(pos, updItem);
                haveUpdate = true;
                updateListView(lsItemTrans);
            } else { // exceed limit qty
                alertMessage(getString(R.string.msg_exceed_locqty));
            }
        }
        edtQty.setText("1");
        edtBarcode.setText("");
        edtBarcode.requestFocus();
        View edtView = getCurrentFocus();
        if (edtView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtView.getWindowToken(), 0);
        }

    }

    private void setEvents() {

        edtBarcode.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {

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


    private void bindWidgets() {
        data = new DataUtil(getApplicationContext());

        txtLocId = (TextView) findViewById(R.id.textLocationId);
        txtLocDetail = (TextView) findViewById(R.id.textLocationInfo);

        edtBarcode = (EditText) findViewById(R.id.editBarcodeCheck);
        edtQty = (EditText) findViewById(R.id.editQty);

        listView = (ListView) findViewById(R.id.lvCheckItem);
        prgBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void processBarcode(String id) {
        myHandler.sendEmptyMessage(SHOW_PROGRESSBAR);
        ItemInfo iInfo = data.getItemInfo(id);
        int qty = Integer.parseInt(edtQty.getText().toString());

        if (!iInfo.itemId.equals("")) {
            if (checkLocationRule(iInfo.itemLocation)) {
                if (iInfo.itemType.equals("ม้วน")) {
                    iInfo = data.getBarcodeDetail(iInfo);
                    if (iInfo.itemLocation.equals("MANY")) {
                        //-- Identify Location --
                        selectLocationDialog(iInfo, qty);
                    } else {
                        ListLocation loc = checkLocation(id, iInfo.itemLocation);
                        if (loc.locationQty > 0) {
                            locInfo = loc;
                            iInfo.itemLocation = loc.locationId;
                            iInfo.itemQty = loc.locationQty;
                            iInfo.itemPallet = loc.palletId;

                            processTransfer(iInfo, qty);
                        }
                    }
                } else if (iInfo.itemType.equals("PALLET")) {
                    iInfo.itemQty = 1;
                    processTransfer(iInfo, qty);
                }
            }
        } else {
            alertMessage(getString(R.string.msg_notfound));
        }
        myHandler.sendEmptyMessage(HIDE_PROGRESSBAR);
    }

    private boolean checkLocationRule(String itemLocation) {
        if (itemLocation.equals("null")) {
            alertMessage(getString(R.string.msg_location_null));
            return false;
        } else {
            return true;
        }
    }

    private void updateLocation(ItemInfo iInfo) {
        txtLocId.setText(iInfo.itemId + " " + iInfo.itemName);
        txtLocDetail.setText(iInfo.itemDetail);
    }

    public void updateListView(List<ItemInfo> item) {

        cAdapter = new TransferListAdapter(this, item);
        listView.setAdapter(cAdapter);
        cAdapter.notifyDataSetChanged();
    }


    public int searchListFromBarcode(ItemInfo info) {
        int size = lsItemTrans.size();
        for (int i = 0; i < size; i++) {

            if ((lsItemTrans.get(i).itemBarcode.equals(info.itemBarcode)) && lsItemTrans.get(i).itemLocation.equals(info.itemLocation)) {
                return i;
            }
        }
        return -1;
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_action_save, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save:
                showBarcodeDialog();
                return true;

            case R.id.action_cancel:
                closeActivity();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void updateData(String destType) {
        if (haveUpdate) {
            if (!destLocation.equals("")) {
                if (data.sendTransferItem(destType, destLocation, transType, lsItemTrans)) {
                    Toast.makeText(TransferMainActivity.this, R.string.msg_save_success, Toast.LENGTH_SHORT).show();
                    locationId = "";
                    lsItemTrans = new ArrayList<ItemInfo>();
                    //lsItemCheck = new ArrayList<ItemInfo>();
                    cAdapter = new TransferListAdapter(this, lsItemTrans);
                    listView.setAdapter(cAdapter);
                    cAdapter.notifyDataSetChanged();
                    haveUpdate = false;

                } else {
                    alertMessage(getString(R.string.msg_save_error));
                }
            } else {
                alertMessage(getString(R.string.msg_nolocation));
            }
        }

        haveUpdate = false;
    }

    private void showBarcodeDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.destination_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.dest_input);

        dialogBuilder.setTitle(getString(R.string.msg_req_destlocation));
        dialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                destLocation = edt.getText().toString().toUpperCase().trim();
                ItemInfo itm = data.getItemInfo(destLocation);
                if (destLocation.equals("KPPSND")) {
                    updateData("KPPSND");
                } else {
                    if (itm.itemType.equals("LOCATION")) {
                        updateData("LOCATION");
                    } else if (itm.itemType.equals("PALLET")) {
                        if (havePallet) {
                            alertMessage(getString(R.string.msg_pallet_notallow));
                        } else {
                            updateData("PALLET");
                        }
                    } else {
                        alertMessage(getString(R.string.msg_req_destlocation));

                    }
                }
                edt.setText("");
                edt.requestFocus();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
        edt.setText("");
        edt.requestFocus();
    }


    private void selectLocationDialog(final ItemInfo iInfo, final int qty) {
        final String bc = iInfo.itemBarcode;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.destination_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edtLoc = (EditText) dialogView.findViewById(R.id.dest_input);

        dialogBuilder.setTitle(getString(R.string.msg_req_orglocation));
        dialogBuilder.setPositiveButton(getString(R.string.txt_confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                ListLocation loc = checkLocation(bc, edtLoc.getText().toString().toUpperCase().trim());
                if (loc.locationQty > 0) {
                    locInfo = loc;
                    iInfo.itemLocation = loc.locationId;
                    iInfo.itemQty = loc.locationQty;
                    iInfo.itemPallet = loc.palletId;

                    processTransfer(iInfo, qty);
                } else {
                    alertMessage(getString(R.string.msg_not_inlocation));
                }

            }
        });
        dialogBuilder.setNegativeButton(getString(R.string.txt_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        edtLoc.setText("");
        edtLoc.requestFocus();
        final AlertDialog b = dialogBuilder.create();
        b.show();


        edtQty.setText("1");
        edtBarcode.setText("");
        edtBarcode.requestFocus();

    }

    private ListLocation checkLocation(String barcode, String location) {
        List<ListLocation> lsLoc = data.getLocationList(barcode);
        ListLocation tmpLoc = new ListLocation("", "", 0);
        for (int i = 0; i < lsLoc.size(); i++) {
            if (lsLoc.get(i).locationId.equals(location)) {
                tmpLoc.locationId = lsLoc.get(i).locationId;
                tmpLoc.locationQty = lsLoc.get(i).locationQty;
                tmpLoc.palletId = lsLoc.get(i).palletId;
                return tmpLoc;
            }
        }
        return tmpLoc;
    }

    private void alertMessage(String msg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setIcon(R.drawable.ic_warning);

        alertDialogBuilder.setPositiveButton(getString(R.string.txt_close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void closeActivity() {
        if (haveUpdate) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(R.string.msg_haveupdate);

            alertDialogBuilder.setPositiveButton(getString(R.string.txt_confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    prgBar.setVisibility(View.VISIBLE);
                    finish();
                }
            });

            alertDialogBuilder.setNegativeButton(getString(R.string.txt_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            prgBar.setVisibility(View.VISIBLE);
            this.finish();
        }
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            prgBar.setVisibility(View.VISIBLE);
            finish();

        } else if (id == R.id.nav_issue) {
            prgBar.setVisibility(View.VISIBLE);
            startActivity(new Intent(getApplicationContext(), IssueMainActivity.class));
            finish();

        } else if (id == R.id.nav_issuesale) {
            prgBar.setVisibility(View.VISIBLE);
            startActivity(new Intent(getApplicationContext(), IssueSaleMainActivity.class));
            finish();

        } else if (id == R.id.nav_pick) {
            prgBar.setVisibility(View.VISIBLE);
            startActivity(new Intent(getApplicationContext(), PickMainActivity.class));
            finish();

        } else if (id == R.id.nav_prodpick) {
            prgBar.setVisibility(View.VISIBLE);
            startActivity(new Intent(getApplicationContext(), ProdRcvMainActivity.class));
            finish();

        } else if (id == R.id.nav_transfer) {
            prgBar.setVisibility(View.VISIBLE);
            startActivity(new Intent(getApplicationContext(), TransferMainActivity.class));
            finish();

        } else if (id == R.id.nav_search) {
            prgBar.setVisibility(View.VISIBLE);
            startActivity(new Intent(getApplicationContext(), SearchMainActivity.class));
            finish();

        } else if (id == R.id.nav_check) {
            prgBar.setVisibility(View.VISIBLE);
            startActivity(new Intent(getApplicationContext(), CheckMainActivity.class));
            finish();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
