package com.thaiopp.thaioppwarehouse;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.thaiopp.utils.DataUtil;
import com.thaiopp.utils.GlobalVar;
import com.thaiopp.vars.ListBranch;
import com.thaiopp.vars.ListBranchWarehouse;
import com.thaiopp.vars.ListWarehouse;
import com.thaiopp.vars.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    //adding this comment for test commiting to github
    // all user interface
    private EditText edtUserName;
    private EditText edtPassword;
    private View btnConfirm;
    private Spinner spinBranch;
    private Spinner spinWarehouse;
    private ProgressBar prgBar;
    private DataUtil data;

    private boolean userAuthen;

    private List<ListBranch> lsBranch;
    private List<ListWarehouse> lsWarehouse;
    private List<ListWarehouse> lsWarehouseInBranch;
    private List<String> lsBranchID;

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
    //Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        UserInfo u = new UserInfo("", "", "", "", "", "", "", "", "");
        GlobalVar.getInstance().setUserInfo(u);
        bindWidgets();
        setEvents();

        new datagenAsynTask().execute();

    }

    @Override
    public void onStart() {
        super.onStart();

    }


    private void setEvents() {
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lsBranch.size() > 0) {
                /* Check User Login Process */
                    String branchId = lsBranch.get(spinBranch.getSelectedItemPosition()).branchId;
                    String warehouseID = lsWarehouseInBranch.get(spinWarehouse.getSelectedItemPosition()).warehouseId;


                /* Go to Main Page */

                    if (data.loginAuthen(edtUserName.getText().toString(), edtPassword.getText().toString(), getString(R.string.val_org), branchId, warehouseID)) {

                        if (GlobalVar.getInstance().getUserInfo().loginStat.equals("S")) {

                            prgBar.setVisibility(View.VISIBLE);

                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else if (GlobalVar.getInstance().getUserInfo().loginStat.equals("E")) {
                            Toast.makeText(LoginActivity.this, R.string.msg_wrong_password, Toast.LENGTH_LONG).show();
                            edtPassword.setText("");
                            edtPassword.requestFocus();
                        } else if (GlobalVar.getInstance().getUserInfo().loginStat.equals("F")) {
                            Toast.makeText(LoginActivity.this, R.string.msg_connection_fail, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this, R.string.msg_user_notfound, Toast.LENGTH_LONG).show();
                            edtUserName.setText("");
                            edtPassword.setText("");
                            edtUserName.requestFocus();
                        }

                    }
                } else {
                    Toast.makeText(LoginActivity.this, R.string.msg_check_config, Toast.LENGTH_LONG).show();
                }
            }
        });

        spinBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setWarehouseFromBranch(lsBranch.get(position).branchId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void bindWidgets() {

        data = new DataUtil(this);
        edtUserName = (EditText) findViewById(R.id.usernameEditText);
        edtUserName.setText("");
        edtPassword = (EditText) findViewById(R.id.passwordEditText);
        edtPassword.setText("");
        spinBranch = (Spinner) findViewById(R.id.spinBranch);
        spinWarehouse = (Spinner) findViewById(R.id.spinWarehouse);

        btnConfirm = findViewById(R.id.confirmButton);
        prgBar = (ProgressBar) this.findViewById(R.id.progressBar);

    }

    private void setWarehouseFromBranch(String bId) {
        lsWarehouseInBranch = new ArrayList<>();
        for (int i = 0; i < lsWarehouse.size(); i++) {
            if (lsWarehouse.get(i).warehouseBranch.equals(bId)) {
                lsWarehouseInBranch.add(lsWarehouse.get(i));
            }
        }
        List<String> wh = new ArrayList<>();
        for (int i = 0; i < lsWarehouseInBranch.size(); i++) {
            wh.add(lsWarehouseInBranch.get(i).warehouseDesc);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, wh);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinWarehouse.setAdapter(dataAdapter);
    }

    private void dataGen() {
        myHandler.sendEmptyMessage(SHOW_PROGRESSBAR);


        ListBranchWarehouse lsBW = data.getBranchWarehouseList();
            if (lsBW != null) {
            lsBranch = lsBW.lsBbranch;
            lsWarehouse = lsBW.lsWarehouse;
            if (lsBranch.size() > 0) {
                List<String> branch = new ArrayList<>();
                for (int i = 0; i < lsBranch.size(); i++) {
                    branch.add(lsBranch.get(i).branchDetail);
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item, branch);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinBranch.setAdapter(dataAdapter);
                setWarehouseFromBranch(lsBranch.get(spinBranch.getSelectedItemPosition()).branchId);
            }}
        myHandler.sendEmptyMessage(HIDE_PROGRESSBAR);
    }


    private void showPinDialog() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.pin_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.pin_input);

        dialogBuilder.setTitle("Setting");
        //dialogBuilder.setMessage("Enter PIN CODE");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (edt.getText().toString().equals(GlobalVar.getInstance().getConfigSetting().pin)) {
                    startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                } else {
                    Toast.makeText(getBaseContext(), "WRONG PIN CODE", Toast.LENGTH_LONG).show();
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

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_action_setting, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_setting:
                showPinDialog();
                return true;

            case R.id.action_close:
                finish();
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




    //begin ---------- datagenAsynTask ------------------

    public class datagenAsynTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params)  {

            return null;
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }


        protected void onProgressUpdate() {
            super.onProgressUpdate();
        }

        protected void onPostExecute(Void param) {
            data.loadConfigData();
            dataGen();

            if (lsBranch.size() <= 0) {
                alertMessage(getString(R.string.msg_check_config));
            }
        }
    }
    //end ---------- datagenAsynTask ------------------


    @Override
    public void onPause() {
        super.onResume();
        prgBar.setVisibility(View.GONE);
        finish();
    }
}
