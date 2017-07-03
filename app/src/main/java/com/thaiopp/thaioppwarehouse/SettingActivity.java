package com.thaiopp.thaioppwarehouse;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.thaiopp.utils.GlobalVar;
import com.thaiopp.vars.ConfigSetting;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class SettingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // UI Var
    private TextView edtUrl;
    private TextView edtSoapAction;
    private TextView edtOperationName;
    private TextView edtNamespace;
    private TextView edtTimeout;
    private TextView edtPin;
    private Switch swtRecLog;

    private ConfigSetting val;
    private boolean pinPass = false;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        bindWidgets();
        setEvents();
        loadConfigData();

    }

    private void setEvents() {

    }

    private void bindWidgets() {
        edtUrl = (TextView) findViewById(R.id.editTextUrl);
        edtSoapAction = (TextView) findViewById(R.id.editTextSoapAction);
        edtOperationName = (TextView) findViewById(R.id.editTextOperationName);
        edtNamespace = (TextView) findViewById(R.id.editTextNamespace);
        edtTimeout = (TextView) findViewById(R.id.editTextTimeout);
        edtPin = (TextView) findViewById(R.id.editTextPin);
        swtRecLog = (Switch) findViewById(R.id.switchRecLog);
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_action_save, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save:
                writeConfigData();
                return true;

            case R.id.action_cancel:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void loadConfigData() {
        String filename = getString(R.string.config_filename);
        try {
            File cfgFolder = new File(Environment.getExternalStorageDirectory(), "ThaiOPP");
            File cfgFile = new File(cfgFolder, filename);

            // Open the file that is the first
            // command line parameter
            FileInputStream fstream = new FileInputStream(cfgFile);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            val = new ConfigSetting("", "", "", "", 0, "", "");
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

            edtUrl.setText(val.url);
            edtSoapAction.setText(val.soapAction);
            edtOperationName.setText(val.operationName);
            edtNamespace.setText(val.nameSpace);
            edtTimeout.setText(String.valueOf(val.timeout));
            edtPin.setText(val.pin);
            if (val.recLog.equals("Y")) {
                swtRecLog.setChecked(true);
            } else {
                swtRecLog.setChecked(false);
            }

        } catch (Exception e) {//Catch exception if any
            Toast.makeText(getBaseContext(), "Exception :" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void writeConfigData() {
        String filename = getString(R.string.config_filename);
        String recLog;
        String cfg = "";
        if (swtRecLog.isChecked()) {
            recLog = "Y";
        } else {
            recLog = "N";
        }
        cfg = "[URL]" + edtUrl.getText() + "\n"
                + "[SOAP_ACTION]" + edtSoapAction.getText() + "\n"
                + "[OPERATION_NAME]" + edtOperationName.getText() + "\n"
                + "[NAMESPACE]" + edtNamespace.getText() + "\n"
                + "[TIMEOUT]" + edtTimeout.getText() + "\n"
                + "[PIN]" + edtPin.getText() + "\n"
                + "[REC_LOG]" + recLog + "\n";
        boolean isSuccess = false;
        try {
            File cfgFolder = new File(Environment.getExternalStorageDirectory(), "ThaiOPP");
            if (!cfgFolder.exists()) {
                cfgFolder.mkdir();
            }
            try {
                File cfgFile = new File(cfgFolder, filename);
                cfgFile.createNewFile();

                FileOutputStream fos;
                byte[] data = cfg.getBytes();
                try {
                    fos = new FileOutputStream(cfgFile);
                    fos.write(data);
                    fos.flush();
                    fos.close();
                    isSuccess = true;
                    Toast.makeText(SettingActivity.this, R.string.msg_save_success, Toast.LENGTH_LONG).show();
                    val.url = edtUrl.getText().toString();
                    val.soapAction = edtSoapAction.getText().toString();
                    val.operationName = edtOperationName.getText().toString();
                    val.nameSpace = edtNamespace.getText().toString();
                    val.timeout = Integer.parseInt(edtTimeout.getText().toString());
                    val.pin = edtPin.getText().toString();
                    if (swtRecLog.isChecked()) {
                        val.recLog = "Y";
                    } else {
                        val.recLog = "N";
                    }
                    GlobalVar.getInstance().setConfigSetting(val);
                    finish();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } catch (Exception ex) {
                System.out.println("ex: " + ex);
            }
        } catch (Exception e) {
            System.out.println("e: " + e);
        }

        if (!isSuccess) {
            Toast.makeText(SettingActivity.this, R.string.msg_save_error, Toast.LENGTH_LONG).show();
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
