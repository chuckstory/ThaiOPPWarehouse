package com.thaiopp.thaioppwarehouse;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;

import com.thaiopp.utils.DataUtil;
import com.thaiopp.vars.ItemInfo;

import java.util.ArrayList;
import java.util.List;


public class PalletMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private View dialogView;
    private TextView txtLocId;
    private TextView txtLocDetail;
    private boolean haveUpdate;
    private DataUtil data;

    private ListView listView;
    private PalletListAdapter cAdapter;

    private EditText edtBarcode;
    private EditText edtQty;

    private String palletId = "";
    private String destPallet = "";
    private String transType = "";

    private List<ItemInfo> lsItemTrans = new ArrayList<ItemInfo>();
    private List<ItemInfo> lsItemCheck = new ArrayList<ItemInfo>();

    private int[] inStock;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pallet_layout);

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
        cAdapter = new PalletListAdapter(this, lsItemTrans);
        listView.setAdapter(cAdapter);
    }


    private void setEvents() {
        edtBarcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String id = edtBarcode.getText().toString();
                    int pos = searchListFromBarcode(id);
                    int cnt = 0;
                    if (pos == -1) {
                        ItemInfo iInfo = data.getItemInfo(id);
                        if (palletId.equals("")) {
                            if ((iInfo.itemType.contains(getString(R.string.bc_pallet_type_en))) || (iInfo.itemType.contains(getString(R.string.bc_pallet_type_th)))) {
                                palletId = id;
                                updatePallet(iInfo);
                                lsItemCheck = data.getCheckList(palletId);
                                haveUpdate = false;

                            } else {
                                Toast.makeText(PalletMainActivity.this, R.string.msg_nolocation, Toast.LENGTH_LONG).show();
                            }
                        } else if (!(palletId.equals("")) && ((iInfo.itemType.contains(getString(R.string.bc_pallet_type_en))) || (iInfo.itemType.contains(getString(R.string.bc_pallet_type_th))))) {
                            Toast.makeText(PalletMainActivity.this, R.string.msg_pallet_exist, Toast.LENGTH_LONG).show();
                        } else if ((iInfo.itemType.equals(getString(R.string.bc_location_type_en))) || (iInfo.itemType.equals(getString(R.string.bc_location_type_th)))) {
                            Toast.makeText(PalletMainActivity.this, R.string.msg_location_notallow, Toast.LENGTH_LONG).show();
                        } else {
                            int idx = checkAvailable(id);
                            if (idx > -1) {
                                iInfo = lsItemCheck.get(idx);
                                cnt = Integer.parseInt(edtQty.getText().toString());
                                if (cnt <= iInfo.itemQty) {
                                    if (iInfo.itemType.equals("ม้วน")) {
                                        iInfo = data.getBarcodeDetail(iInfo);
                                    }
                                    iInfo.itemCount = cnt;
                                    lsItemTrans.add(iInfo);
                                    haveUpdate = true;
                                }

                            } else {
                                Toast.makeText(PalletMainActivity.this, R.string.msg_not_inpallet, Toast.LENGTH_LONG).show();
                            }
                        }

                    } else {
                        cnt = Integer.parseInt(edtQty.getText().toString());
                        ItemInfo updItem = lsItemTrans.get(pos);
                        if (updItem.itemCount + cnt <= updItem.itemQty) {
                            updItem.itemCount = updItem.itemCount + cnt;
                            lsItemTrans.set(pos, updItem);
                            haveUpdate = true;
                        }
                    }

                    updateListView(lsItemTrans);
                    edtQty.setText("1");
                    edtBarcode.setText("");
                    edtBarcode.requestFocus();

                    return true;

                }
                return false;
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

        txtLocId = (TextView) findViewById(R.id.textPalletId);
        txtLocDetail = (TextView) findViewById(R.id.textLocationInfo);

        edtBarcode = (EditText) findViewById(R.id.editBarcodeCheck);
        edtQty = (EditText) findViewById(R.id.editQty);

        listView = (ListView) findViewById(R.id.lvCheckItem);
    }


    private void updatePallet(ItemInfo iInfo) {
        txtLocId.setText(iInfo.itemId);
        txtLocDetail.setText(iInfo.itemName);
    }

    public void updateListView(List<ItemInfo> item) {

        cAdapter = new PalletListAdapter(this, item);
        listView.setAdapter(cAdapter);
        cAdapter.notifyDataSetChanged();
        Log.i("web view", "Pallet list" + String.valueOf(item.size()));
    }


    public int searchListFromBarcode(String id) {
        int size = lsItemTrans.size();
        Log.i("Pallet", id + " >> count" + String.valueOf(size));
        boolean found = false;
        for (int i = 0; i < size; i++) {

            if (lsItemTrans.get(i).itemBarcode.equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public int checkAvailable(String id) {
        int size = lsItemCheck.size();
        Log.i("Pallet", id + " >> count" + String.valueOf(size));
        boolean found = false;
        for (int i = 0; i < size; i++) {

            if (lsItemCheck.get(i).itemBarcode.equals(id)) {
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


    private void updateData() {
        if (haveUpdate) {
            if ((!(palletId.equals(""))) && (!(destPallet.equals("")))) {
                if (data.sendPalletItem(palletId, destPallet, lsItemTrans)) {
                    Toast.makeText(PalletMainActivity.this, R.string.msg_save_success, Toast.LENGTH_SHORT).show();
                    txtLocId.setText("");
                    txtLocDetail.setText(R.string.msg_start_location);
                    palletId = "";
                    lsItemTrans = new ArrayList<ItemInfo>();
                    lsItemCheck = new ArrayList<ItemInfo>();
                    cAdapter = new PalletListAdapter(this, lsItemTrans);
                    listView.setAdapter(new PalletListAdapter(this, lsItemTrans));
                    cAdapter = new PalletListAdapter(this, lsItemTrans);
                    cAdapter.notifyDataSetChanged();
                    listView.setAdapter(cAdapter);
                    haveUpdate = false;

                } else {
                    Toast.makeText(PalletMainActivity.this, R.string.msg_save_error, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(PalletMainActivity.this, R.string.msg_nopallet, Toast.LENGTH_SHORT).show();
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

        dialogBuilder.setTitle(getString(R.string.msg_req_destpallet));
        dialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                ItemInfo itm = data.getItemInfo(edt.getText().toString());
                if ((itm.itemType.contains(getString(R.string.bc_pallet_type_en))) || (itm.itemType.contains(getString(R.string.bc_location_type_th)))) {
                    destPallet = edt.getText().toString();
                    if (!palletId.equals(destPallet)) {
                        updateData();
                    } else {
                        Toast.makeText(getBaseContext(), getString(R.string.msg_same_destination), Toast.LENGTH_LONG).show();
                    }
                    edt.setText("");
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.msg_req_destpallet), Toast.LENGTH_LONG).show();
                    edt.setText("");
                    edt.requestFocus();
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void closeActivity() {
        Toast.makeText(PalletMainActivity.this, "Close", Toast.LENGTH_SHORT).show();
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
}
