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
import com.thaiopp.vars.ProdRcv;

import java.util.ArrayList;
import java.util.List;


public class ProdRcvMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private View dialogView;
    private boolean haveUpdate;
    private DataUtil data;

    private ListView listView;
    private ProdRcvListAdapter cAdapter;

    private EditText edtBarcode;
    private ProgressBar prgBar;

    private List<ProdRcv> lsProdRcv = new ArrayList<>();
    private List<ProdRcv> lsPallet = new ArrayList<>();
    private List<ProdRcv> lsRoll = new ArrayList<>();

    private String destLocation;
    private String lastType = "";

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
        setContentView(R.layout.prodrcv_layout);

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

        lsProdRcv = data.getProdRcvList();
        cAdapter = new ProdRcvListAdapter(this, lsProdRcv);
        listView.setAdapter(cAdapter);
    }


    private void setEvents() {
        edtBarcode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    edtBarcode.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(edtBarcode, InputMethodManager.SHOW_IMPLICIT);
                }
                return true; // return is important...
            }
        });

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

    /**
     * Hides the soft keyboard
     */
    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Shows the soft keyboard
     */
    public void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    private void swapToTop(int res) {
        ProdRcv tmp;
        tmp = lsProdRcv.get(res);
        lsProdRcv.remove(res);
        lsProdRcv.add(0, tmp);
    }

    private int checkAllRollinPallet(String palletId) {
        int size = lsProdRcv.size();
        int cnt = 0;
        for (int i = 0; i < size; i++) {
            if (lsProdRcv.get(i).palletId.equals(palletId)) {
                lsProdRcv.get(i).check = true;
                swapToTop(i);
                cnt++;
            }
        }
        return cnt;
    }

    private void uncheckAll() {
        int size = lsProdRcv.size();
        int cnt = 0;
        for (int i = 0; i < size; i++) {
            lsProdRcv.get(i).check = false;
        }
    }


    private void bindWidgets() {
        data = new DataUtil(getApplicationContext());

        edtBarcode = (EditText) findViewById(R.id.editBarcodeCheck);

        listView = (ListView) findViewById(R.id.lvProdRcvItem);
        prgBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void processBarcode(String id) {
        myHandler.sendEmptyMessage(SHOW_PROGRESSBAR);
        int res = searchListFromBarcode(id);
        if (res > -1) {
            if (!lsProdRcv.get(res).check) {
                if ((lsProdRcv.get(res).inputType.equals(lastType)) || (lastType.equals(""))) {
                    lastType = lsProdRcv.get(res).inputType;
                    if ((lsProdRcv.get(res).inputType.equals("ROLL")) && (lsProdRcv.get(res).barcode.equals(id))) {
                        lsProdRcv.get(res).check = true;
                        lsRoll.add(lsProdRcv.get(res));
                        swapToTop(res);
                        updateListView(lsProdRcv);

                    } else if (lsProdRcv.get(res).inputType.equals("PALLET")) {
                        if (lsProdRcv.get(res).palletId.equals(id)) {
                            if (lastType.equals("PALLET")) {
                                uncheckAll();
                            }
                            lsPallet = new ArrayList<ProdRcv>();
                            lsPallet.add(lsProdRcv.get(res));
                            int upd = checkAllRollinPallet(lsProdRcv.get(res).palletId);
                            Toast.makeText(ProdRcvMainActivity.this, "ปรับปรุง " + String.valueOf(upd) + " รายการ", Toast.LENGTH_LONG).show();
                            updateListView(lsProdRcv);

                        } else if (lsProdRcv.get(res).barcode.equals(id)) {
                            alertMessage(getString(R.string.msg_pallet_only));
                        }

                    }
                } else {
                    alertMessage(getString(R.string.msg_wrong_type));
                }
            } else {
                Toast.makeText(ProdRcvMainActivity.this, getString(R.string.msg_already_check), Toast.LENGTH_LONG).show();
            }
        } else {
            // alert not found
            ItemInfo iInfo = data.getItemInfo(id);
            alertInfo(getString(R.string.msg_notfound_rcv), iInfo);
        }
        myHandler.sendEmptyMessage(HIDE_PROGRESSBAR);
        edtBarcode.setText("");
        edtBarcode.requestFocus();

    }

    public void updateItemListView(int idx) {
        View v = listView.getChildAt(idx);
        if (v == null)
            return;

        CheckBox checkBox = (CheckBox) v.findViewById(R.id.check);
        checkBox.setChecked(lsProdRcv.get(idx).check);

    }


    public void updateListView(List<ProdRcv> item) {
        prgBar.setVisibility(View.VISIBLE);
        cAdapter = new ProdRcvListAdapter(this, item);
        listView.setAdapter(cAdapter);
        cAdapter.notifyDataSetChanged();
        prgBar.setVisibility(View.GONE);
    }


    public int searchListFromBarcode(String id) {
        int size = lsProdRcv.size();
        boolean found = false;
        for (int i = 0; i < size; i++) {

            if ((lsProdRcv.get(i).barcode.equals(id)) || (lsProdRcv.get(i).palletId.equals(id))) {
                return i;
            }
        }
        return -1;
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

                if (itm.itemType.equals("LOCATION")) {
                    updateData();
                } else {
                    alertMessage(getString(R.string.msg_req_destlocation));

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

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_action_save, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save:
                //updateData();
                if ((lsPallet.size() > 0) || (lsRoll.size() > 0)) {
                    showBarcodeDialog();
                } else {
                    alertMessage(getString(R.string.msg_no_update));
                }
                return true;

            case R.id.action_cancel:
                closeActivity();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void updateData() {

        if (!(destLocation.equals(""))) {
            prgBar.setVisibility(View.VISIBLE);
            if (data.sendProdRcv(destLocation, lsRoll, lsPallet)) {
                Toast.makeText(ProdRcvMainActivity.this, R.string.msg_save_success, Toast.LENGTH_LONG).show();
                lsProdRcv = new ArrayList<>();
                lsRoll = new ArrayList<>();
                lsPallet = new ArrayList<>();
                lsProdRcv = data.getProdRcvList();
                cAdapter = new ProdRcvListAdapter(this, lsProdRcv);
                listView.setAdapter(cAdapter);
                cAdapter.notifyDataSetChanged();
                listView.setAdapter(cAdapter);
                haveUpdate = false;
                lastType = "";

            } else {
                alertMessage(getString(R.string.msg_save_error));
            }
            prgBar.setVisibility(View.GONE);
        } else {
            Toast.makeText(ProdRcvMainActivity.this, R.string.msg_nolocation, Toast.LENGTH_LONG).show();
        }

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
                    //Toast.makeText(CheckMainActivity.this, "You clicked yes button", Toast.LENGTH_LONG).show();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            prgBar.setVisibility(View.VISIBLE);
            this.finish();
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
            startActivity(new Intent(getApplicationContext(), ProdRcvMainActivity.class));
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
