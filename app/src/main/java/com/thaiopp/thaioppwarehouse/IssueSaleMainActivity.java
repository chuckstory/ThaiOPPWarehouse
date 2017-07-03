package com.thaiopp.thaioppwarehouse;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.thaiopp.utils.DataUtil;
import com.thaiopp.vars.ListSaleOrder;

import java.util.List;


public class IssueSaleMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private View dialogView;
    private DataUtil data;

    private List<ListSaleOrder> lsSo;
    private ProgressBar prgBar;

    private IssueSaleListAdapter listAdapter;
    private ListView listView;

    private GoogleApiClient client;

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

    private GoogleApiClient client2;

    private void hideProgressBar() {
        prgBar.setVisibility(View.GONE);
    }

    private void showProgressBar() {
        prgBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.issuesale_layout);
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


        new datagenAsynTask().execute();

        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void bindWidgets() {
        listView = (ListView) findViewById(R.id.lvSaleOrder);
        data = new DataUtil(getApplicationContext());
        prgBar = (ProgressBar) findViewById(R.id.progressBar);
    }


    private void setEvents() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                prgBar.setVisibility(View.VISIBLE);

                Intent i = new Intent(getApplicationContext(), IssueSaleDetailActivity.class);
                i.putExtra("soId", lsSo.get(position).soId);
                i.putExtra("tfLot", lsSo.get(position).tfLotDocId);
                i.putExtra("deliDate", lsSo.get(position).deliDate);
                i.putExtra("soSeq", lsSo.get(position).soSeq);
                i.putExtra("seqDeli", lsSo.get(position).seqDeli);
                startActivity(i);

            }
        });
    }


    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            prgBar.setVisibility(View.VISIBLE);
            finish();

        } else if (id == R.id.nav_issue) {
            prgBar.setVisibility(View.VISIBLE);
            startActivity(new Intent(getApplicationContext(), IssueSaleMainActivity.class));
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

        lsSo = data.getSoList();
        listAdapter = new IssueSaleListAdapter(this, lsSo);

        // setting list adapter


        listView.setAdapter(listAdapter);
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
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2.connect();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "IssueSaleMain Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.thaiopp.thaioppwarehouse/http/host/path")
        );
        AppIndex.AppIndexApi.start(client2, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "IssueSaleMain Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.thaiopp.thaioppwarehouse/http/host/path")
        );
        AppIndex.AppIndexApi.end(client2, viewAction);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2.disconnect();
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
