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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.thaiopp.utils.DataUtil;
import com.thaiopp.vars.ItemInfo;
import com.thaiopp.vars.ListItemDetail;

import java.util.ArrayList;
import java.util.List;


public class PickDetailActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Context c;
    private View dialogView;
    private TextView txtDocId;
    private TextView txtDocDate;
    private TextView txtDocType;
    private TextView txtPoId;
    private EditText edtBarcode;
    private ProgressBar prgBar;

    private boolean haveUpdate;
    private DataUtil data;
    private String docId;
    private String transStockSeq;
    private String locationId = "";

    private ListView listView;

    private List<ListItemDetail> lsItemDetail;

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
        setContentView(R.layout.pick_detaillist_layout);

        Intent intent = getIntent();

        c = getApplicationContext();
        data = new DataUtil(c);
        transStockSeq = intent.getStringExtra("stockSeq");
        docId = intent.getStringExtra("transId");
        String docDate = intent.getStringExtra("transDate");
        String docType = intent.getStringExtra("transType");
        String poId = intent.getStringExtra("poId");

        haveUpdate = false;

        bindWidgets();
        setEvents();

        txtDocId.setText(docId);
        txtDocDate.setText(docDate);
        txtDocType.setText(docType);
        txtPoId.setText(poId);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        listView = (ListView) findViewById(R.id.lvItemDetail);

        new datagenAsynTask().execute();
    }

    private void dataGen(String v) {
        lsItemDetail = new ArrayList<>();
        lsItemDetail = data.getPickItemDetailList(v);

        listView = (ListView) findViewById(R.id.lvItemDetail);
        listView.setAdapter(new PickItemDetailAdapter(this, lsItemDetail));
    }

    private int itemSearch(String searchId) {
        boolean found = false;
        int idx = 0;
        while ((idx < lsItemDetail.size()) && (!found)) {
            if (lsItemDetail.get(idx).itemBarcode.equals(searchId)) {
                found = true;
            } else {
                idx = idx + 1;
            }
        }
        if (found) {
            return idx;
        } else {
            return -1;
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
    }


    private void bindWidgets() {
        txtDocId = (TextView) findViewById(R.id.textDocId);
        txtDocDate = (TextView) findViewById(R.id.textDocDate);
        txtDocType = (TextView) findViewById(R.id.textDocType);
        txtPoId = (TextView) findViewById(R.id.textPoId);

        edtBarcode = (EditText) findViewById(R.id.editBarcode);
        prgBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void processBarcode(String id) {
        myHandler.sendEmptyMessage(SHOW_PROGRESSBAR);
        int searchResult = itemSearch(id);

        if (searchResult >= 0) {

            if (lsItemDetail.get(searchResult).itemFin == lsItemDetail.get(searchResult).itemAll) {
                alertRemove(searchResult);
            } else {
                lsItemDetail.get(searchResult).itemFin = lsItemDetail.get(searchResult).itemAll;
                swapToTop(searchResult);
                updateListView();
                haveUpdate = true;
            }


        } else {
            DataUtil data = new DataUtil(c);
            ItemInfo iInfo = data.getItemInfo(id);
            if ((iInfo.itemType.equals(getString(R.string.bc_location_type_en))) || (iInfo.itemType.equals(getString(R.string.bc_location_type_th)))) {
                if (haveUpdate) {
                    alertSave(id);
                }
            } else {
                alertInfo(getString(R.string.msg_notfound_rcv), iInfo);

                //alertMessage(getString(R.string.msg_notfound_rcv) + "\n" + id + "\n" + iInfo.itemDetail+ "\n" + iInfo.itemDetail2 + "\nLOCATION : " +iInfo.itemLocation);
            }
        }
        myHandler.sendEmptyMessage(HIDE_PROGRESSBAR);
        edtBarcode.requestFocus();
        edtBarcode.setText("");

    }

    private void swapToTop(int idx) {
        ListItemDetail tmp;
        tmp = lsItemDetail.get(idx);
        lsItemDetail.remove(idx);
        lsItemDetail.add(0, tmp);
    }

    private void swapToBottom(int idx) {
        ListItemDetail tmp;
        tmp = lsItemDetail.get(idx);
        lsItemDetail.remove(idx);
        lsItemDetail.add(tmp);
    }

    public void updateListView() {

        PickItemDetailAdapter cAdapter = new PickItemDetailAdapter(this, lsItemDetail);
        listView.setAdapter(cAdapter);
        cAdapter.notifyDataSetChanged();
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
        edtBarcode.setText("");
        edtBarcode.requestFocus();

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

    private void alertMessageWithInfo(String msg, String info) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(msg);
        alertDialogBuilder.setMessage(info);
        alertDialogBuilder.setIcon(R.drawable.ic_warning);

        alertDialogBuilder.setPositiveButton(getString(R.string.txt_close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void alertRemove(final int pos) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.msg_remove));
        alertDialogBuilder.setIcon(R.drawable.ic_warning);

        alertDialogBuilder.setPositiveButton(getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface arg0, int arg1) {
                lsItemDetail.get(pos).itemFin = 0;
                swapToBottom(pos);
                updateListView();
            }
        });

        alertDialogBuilder.setNegativeButton(getString(R.string.txt_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void alertSave(final String location) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.msg_location_save) + location);
        alertDialogBuilder.setIcon(R.drawable.ic_warning);

        alertDialogBuilder.setPositiveButton(getText(R.string.txt_save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                updateData(location);
            }
        });

        alertDialogBuilder.setNegativeButton(getText(R.string.txt_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private void updateData(String location) {

        DataUtil data = new DataUtil(c);
        if (data.savePick(location, lsItemDetail)) {
            Toast.makeText(PickDetailActivity.this, getString(R.string.msg_save_success), Toast.LENGTH_LONG).show();
            prgBar.setVisibility(View.VISIBLE);
            this.finish();
        } else {
            alertMessage(getString(R.string.msg_save_error));
        }
        haveUpdate = false;

    }

    private void closeActivity() {
        if (haveUpdate) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(R.string.msg_haveupdate);

            alertDialogBuilder.setPositiveButton(getString(R.string.txt_yes), new DialogInterface.OnClickListener() {
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

    //begin ---------- datagenAsynTask ------------------

    public class datagenAsynTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {

            return null;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected void onProgressUpdate() {
            super.onProgressUpdate();
        }

        protected void onPostExecute(Void param) {
            dataGen(transStockSeq);
            prgBar.setVisibility(View.GONE);
        }
    }
    //end ---------- datagenAsynTask ------------------

}
