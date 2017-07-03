package com.thaiopp.thaioppwarehouse;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ProgressBar;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.thaiopp.utils.DataUtil;
import com.thaiopp.vars.ListItem;
import com.thaiopp.vars.ListWorkOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class PickMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private View dialogView;
    private DataUtil data;

    private List<ListWorkOrder> arrDoc;
    private ProgressBar prgBar;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<ListWorkOrder>> listDataChild;

    private GoogleApiClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_layout);
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

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);


        new datagenAsynTask().execute();


        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub

                prgBar.setVisibility(View.VISIBLE);

                Intent i = new Intent(getApplicationContext(), PickDetailActivity.class);
                i.putExtra("transId", listDataChild.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition).transId);
                i.putExtra("poId", listDataChild.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition).poId);
                i.putExtra("transDate", listDataChild.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition).transDate);
                i.putExtra("transType", listDataChild.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition).transType);
                i.putExtra("stockSeq", listDataChild.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition).stockSeq);
                startActivity(i);
                return false;
            }
        });


        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void bindWidgets() {
        data = new DataUtil(getApplicationContext());
        prgBar = (ProgressBar) findViewById(R.id.progressBar);
    }


    private void setEvents() {
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

    /*
     * Preparing the list data
     */
    private void dataGen() {

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        ListItem addItem;

        arrDoc = new ArrayList<>();
        arrDoc = data.getWorkOrder("RCV");
        if (arrDoc.size() > 0) {
            String grp = "";
            int iGrp = -1;
            List<ListWorkOrder> grpItem = new ArrayList<>();
            for (int i = 0; i < arrDoc.size(); i++) {
                if (!grp.equals(arrDoc.get(i).transTypeId)) {
                    listDataHeader.add(arrDoc.get(i).transType);

                    grp = arrDoc.get(i).transTypeId;
                    if (i > 0) {
                        listDataChild.put(listDataHeader.get(iGrp), grpItem);
                        grpItem = new ArrayList<ListWorkOrder>();
                    }
                    iGrp = iGrp + 1;
                }

                grpItem.add(arrDoc.get(i));
            }
            listDataChild.put(listDataHeader.get(iGrp), grpItem);
        }

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return false;
            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_action_close, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_cancel:
                prgBar.setVisibility(View.VISIBLE);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onResume() {
        super.onResume();
        new datagenAsynTask().execute();
        prgBar.setVisibility(View.GONE);
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
            dataGen();
            prgBar.setVisibility(View.GONE);
        }
    }
    //end ---------- datagenAsynTask ------------------


}
