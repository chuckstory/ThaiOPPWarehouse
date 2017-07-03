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
import android.util.Log;
import android.view.KeyEvent;
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
import com.thaiopp.vars.ItemStockInfo;

import java.util.ArrayList;
import java.util.List;


public class CheckMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private View dialogView;
    private TextView txtLocId;
    private TextView txtLocDetail;
    private boolean haveUpdate;
    private DataUtil data;

    private ListView listView;
    private CheckListAdapter cAdapter;

    private EditText edtBarcode;
    private EditText edtQty;

    private ProgressBar prgBar;

    private String locationId = "";
    private List<ItemStockInfo> lsItemDetail = new ArrayList<ItemStockInfo>();
    private int[] inStock;

    private final static int SHOW_PROGRESSBAR = 1;
    private final static int HIDE_PROGRESSBAR = 0;

    private final Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            final int what = msg.what;
            switch(what) {
                case SHOW_PROGRESSBAR: showProgressBar(); break;
                case HIDE_PROGRESSBAR: hideProgressBar(); break;
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
        setContentView(R.layout.check_layout);

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
        cAdapter = new CheckListAdapter(this, lsItemDetail);
        listView.setAdapter(cAdapter);
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
                if(MotionEvent.ACTION_UP == event.getAction()) {
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
                if (actionId == EditorInfo.IME_ACTION_DONE){
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

        prgBar = (ProgressBar) findViewById(R.id.progressBar);

        listView = (ListView) findViewById(R.id.lvCheckItem);
    }

    private void processBarcode(String id) {
        myHandler.sendEmptyMessage(SHOW_PROGRESSBAR);
        int pos = searchListFromBarcode(id);
        if (pos == -1) {

            ItemInfo iInfo = data.getItemInfo(id);
            if (locationId.equals("")) {
                if ((iInfo.itemType.equals(getString(R.string.bc_location_type_en))) || (iInfo.itemType.equals(getString(R.string.bc_location_type_th)))) {
                    locationId = id;
                    updateLocation(iInfo);
                    lsItemDetail = data.getRollCheckList(locationId);
                    updateListView(lsItemDetail);
                    haveUpdate = false;

                } else {
                    Toast.makeText(CheckMainActivity.this, R.string.msg_nolocation, Toast.LENGTH_LONG).show();
                }
            } else if (!(locationId.equals("")) && ((iInfo.itemType.equals(getString(R.string.bc_location_type_en))) || (iInfo.itemType.equals(getString(R.string.bc_location_type_th))))) {
                Toast.makeText(CheckMainActivity.this, R.string.msg_location_exist, Toast.LENGTH_LONG).show();
            } else {
                ItemStockInfo iStockInfo = new ItemStockInfo("", "", "", "", "", "", "", "", "", 0, 0, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
                iStockInfo.itemId = id;
                iStockInfo.itemBarcode = id;
                iStockInfo.itemQty = 0;
                iStockInfo.itemCount = Integer.parseInt(edtQty.getText().toString());
                lsItemDetail.add(iStockInfo);
                haveUpdate = true;
            }

        } else {
            ItemStockInfo updItem = lsItemDetail.get(pos);
            updItem.itemCount = updItem.itemCount + Integer.parseInt(edtQty.getText().toString());
            lsItemDetail.set(pos, updItem);
            haveUpdate = true;
        }

        updateListView(lsItemDetail);
        myHandler.sendEmptyMessage(HIDE_PROGRESSBAR);
        edtQty.setText("1");
        edtBarcode.setText("");
        edtBarcode.requestFocus();
    }



    private void updateLocation(ItemInfo iInfo) {
        txtLocId.setText(iInfo.itemId + " " + iInfo.itemName);
        txtLocDetail.setText(iInfo.itemDetail);
    }

    public void updateListView(List<ItemStockInfo> item) {

        //listView = (ListView) findViewById(R.id.lvCheckItem);
        cAdapter = new CheckListAdapter(this, item);
        listView.setAdapter(cAdapter);
        cAdapter.notifyDataSetChanged();
        Log.i("web view", "check list" + String.valueOf(item.size()));
    }


    public int searchListFromBarcode(String id) {
        int size = lsItemDetail.size();
        Log.i("check", id + " >> count" + String.valueOf(size));
        boolean found = false;
        for (int i = 0; i < size; i++) {
            Log.i("check", "barcode found " + lsItemDetail.get(i).itemBarcode + " : " + lsItemDetail.get(i).itemBarcode + " : " + id);

            if (lsItemDetail.get(i).itemBarcode.equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_action_save, menu);
        //getMenuInflater().inflate(R.menu.menu_action_close, menu);
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
        if (haveUpdate) {
            if (!(locationId.equals(""))) {
                if (data.sendCheckItem(locationId, lsItemDetail)) {
                    Toast.makeText(CheckMainActivity.this, R.string.msg_save_success, Toast.LENGTH_SHORT).show();
                    txtLocId.setText("");
                    txtLocDetail.setText(R.string.msg_start_location);
                    locationId = "";
                    lsItemDetail = new ArrayList<ItemStockInfo>();
                    cAdapter = new CheckListAdapter(this, lsItemDetail);
                    listView.setAdapter(new CheckListAdapter(this, lsItemDetail));
                    cAdapter = new CheckListAdapter(this, lsItemDetail);
                    cAdapter.notifyDataSetChanged();
                    listView.setAdapter(cAdapter);
                    haveUpdate = false;

                } else {
                    Toast.makeText(CheckMainActivity.this, R.string.msg_save_error, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(CheckMainActivity.this, R.string.msg_nolocation, Toast.LENGTH_SHORT).show();
            }
        }

        haveUpdate = false;
    }


    private void closeActivity() {
        Toast.makeText(CheckMainActivity.this, "Close", Toast.LENGTH_SHORT).show();
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

        } else if (id == R.id.nav_check) {
            prgBar.setVisibility(View.VISIBLE);
            startActivity(new Intent(getApplicationContext(), CheckMainActivity.class));
            finish();

        } else if (id == R.id.nav_search) {
            prgBar.setVisibility(View.VISIBLE);
            startActivity(new Intent(getApplicationContext(), SearchMainActivity.class));
            finish();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
