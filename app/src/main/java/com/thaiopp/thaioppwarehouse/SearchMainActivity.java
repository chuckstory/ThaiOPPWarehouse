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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thaiopp.utils.DataUtil;
import com.thaiopp.vars.ItemInfo;
import com.thaiopp.vars.ItemStockInfo;
import com.thaiopp.vars.ListLocation;

import java.util.ArrayList;
import java.util.List;


public class SearchMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private View dialogView;
    private TextView txtId;
    private TextView txtInfo;
    private boolean haveUpdate;
    private DataUtil data;

    private SearchListAdapter cAdapter;
    private ListView listView;


    private EditText edtBarcode;
    private EditText edtQty;

    private ProgressBar prgBar;

    private String locationId = "";

    private String bid;
    private ItemInfo bInfo;

    private List<ItemInfo> lsItemDetail = new ArrayList<ItemInfo>();
    private int[] inStock;

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
        setContentView(R.layout.search_layout);

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


        listView = (ListView) findViewById(R.id.lvSearchItem);
        cAdapter = new SearchListAdapter(this, lsItemDetail);
        listView.setAdapter(cAdapter);
        edtBarcode.requestFocus();
        View edtView = this.getCurrentFocus();
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

    }

    private void processSearch(String id, ItemInfo itm) {
        txtId.setText(id + " (" + itm.itemType + ")");

        if (itm.itemType.equals("LOCATION")) {
            txtInfo.setText(itm.itemDetail);
            List<ItemStockInfo> lsData = data.getDetailCheckList(id);

            SearchLocationListAdapter sAdapter = new SearchLocationListAdapter(this, lsData);
            listView.setAdapter(sAdapter);

        } else if (itm.itemType.equals("PALLET")) {
            txtInfo.setText("LOCATION : " + itm.itemLocation);
            List<ItemInfo> lsData = data.getPalletDetail(id);

            SearchPalletListAdapter sAdapter = new SearchPalletListAdapter(this, lsData);
            listView.setAdapter(sAdapter);

        } else if (itm.itemType.equals("ม้วน")) {
            txtInfo.setText(itm.itemDetail + "\nID : " + itm.itemDetail2 + "\nRoll No : " + itm.rollNo + "\nLOT ID : " + itm.lotId);
            List<ListLocation> lsLoc = data.getLocationList(id);

            SearchRollListAdapter sAdapter = new SearchRollListAdapter(this, lsLoc);
            listView.setAdapter(sAdapter);
            sAdapter.notifyDataSetChanged();

        }
    }


    private void bindWidgets() {
        data = new DataUtil(getApplicationContext());

        txtId = (TextView) findViewById(R.id.textId);
        txtInfo = (TextView) findViewById(R.id.textInfo);

        edtBarcode = (EditText) findViewById(R.id.editBarcodeCheck);
        edtQty = (EditText) findViewById(R.id.editQty);

        listView = (ListView) findViewById(R.id.lvCheckItem);

        prgBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void processBarcode(String id) {
        myHandler.sendEmptyMessage(SHOW_PROGRESSBAR);
        bid = id;
        ItemInfo iInfo = data.getItemInfo(id);
        bInfo = iInfo;
        if (!iInfo.itemId.equals("")) {
            processSearch(id, iInfo);

        } else {
            alertMessage(getString(R.string.msg_notfound));
        }

        myHandler.sendEmptyMessage(HIDE_PROGRESSBAR);
        edtBarcode.setText("");
        edtBarcode.requestFocus();

    }


    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_action_close, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save:
                return true;

            case R.id.action_cancel:
                closeActivity();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


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
            startActivity(new Intent(getApplicationContext(), SearchMainActivity.class));
            finish();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
