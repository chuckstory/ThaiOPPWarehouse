package com.thaiopp.thaioppwarehouse;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.thaiopp.utils.BadgeView;
import com.thaiopp.utils.DataUtil;
import com.thaiopp.utils.GlobalVar;


public class MainActivity extends AppCompatActivity {

    // UI Var
    private View btnIssue;
    private View btnIssueSale;
    private View btnPick;
    private View btnProdRcv;
    private View btnTrans;
    private View btnCheck;
    private View btnSearch;


    private BadgeView bdgIssue;
    private BadgeView bdgIssueSale;
    private BadgeView bdgPick;
    private BadgeView bdgProdRcv;

    private ProgressBar prgBar;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        bindWidgets();
        setEvents();
        setBadge();

    }

    private void setBadge() {
        DataUtil data = new DataUtil(this);

        bdgIssue.setText(String.valueOf(data.countTransaction("ISS")));
        bdgIssue.show();
        bdgIssueSale.setText(String.valueOf(data.countTransaction("SO")));
        bdgIssueSale.show();
        bdgPick.setText(String.valueOf(data.countTransaction("RCV")));
        bdgPick.show();
        bdgProdRcv.setText(String.valueOf(data.countTransaction("PRD")));
        bdgProdRcv.show();
    }

    private void setEvents() {

        btnIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prgBar.setVisibility(View.VISIBLE);
                startActivity(new Intent(getApplicationContext(), IssueMainActivity.class));
            }
        });

        btnIssueSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prgBar.setVisibility(View.VISIBLE);
                startActivity(new Intent(getApplicationContext(), IssueSaleMainActivity.class));
            }
        });

        btnPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prgBar.setVisibility(View.VISIBLE);
                startActivity(new Intent(getApplicationContext(), PickMainActivity.class));
            }
        });

        btnProdRcv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prgBar.setVisibility(View.VISIBLE);
                startActivity(new Intent(getApplicationContext(), ProdRcvMainActivity.class));
            }
        });

        btnTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prgBar.setVisibility(View.VISIBLE);
                startActivity(new Intent(getApplicationContext(), TransferMainActivity.class));
            }
        });


        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prgBar.setVisibility(View.VISIBLE);
                startActivity(new Intent(getApplicationContext(), CheckMainActivity.class));
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prgBar.setVisibility(View.VISIBLE);
                startActivity(new Intent(getApplicationContext(), SearchMainActivity.class));
            }
        });
    }

    private void bindWidgets() {
        int badgeBGColor;
        badgeBGColor = getResources().getColor(R.color.color_badge_red);
        btnIssue = findViewById(R.id.issueMenuImg);
        btnIssueSale = findViewById(R.id.issueSaleMenuImg);
        btnPick = findViewById(R.id.pickMenuImg);
        btnProdRcv = findViewById(R.id.prodPickMenuImg);
        btnTrans = findViewById(R.id.transMenuImg);
        btnCheck = findViewById(R.id.checkMenuImg);
        btnSearch = findViewById(R.id.searchMenuImg);

        bdgIssue = new BadgeView(this, btnIssue);
        bdgIssue.setBadgeBackgroundColor(badgeBGColor);
        bdgIssue.setTextColor(Color.WHITE);
        bdgIssue.setTextSize(14);
        bdgIssue.setBadgeMargin(2, 5);

        bdgIssueSale = new BadgeView(this, btnIssueSale);
        bdgIssueSale.setBadgeBackgroundColor(badgeBGColor);
        bdgIssueSale.setTextColor(Color.WHITE);
        bdgIssueSale.setTextSize(14);
        bdgIssueSale.setBadgeMargin(2, 5);

        bdgPick = new BadgeView(this, btnPick);
        bdgPick.setBadgeBackgroundColor(badgeBGColor);
        bdgPick.setTextColor(Color.WHITE);
        bdgPick.setTextSize(14);
        bdgPick.setBadgeMargin(2, 5);

        bdgProdRcv = new BadgeView(this, btnProdRcv);
        bdgProdRcv.setBadgeBackgroundColor(badgeBGColor);
        bdgProdRcv.setTextColor(Color.WHITE);
        bdgProdRcv.setTextSize(14);
        bdgProdRcv.setBadgeMargin(2, 5);

        prgBar = (ProgressBar) findViewById(R.id.progressBar);
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

    private void showPinDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.pin_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.pin_input);

        dialogBuilder.setTitle("Setting");
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

    @Override
    public void onResume() {
        super.onResume();
        setBadge();
        prgBar.setVisibility(View.GONE);
    }
}
