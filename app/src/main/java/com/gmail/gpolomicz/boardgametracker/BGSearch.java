package com.gmail.gpolomicz.boardgametracker;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

public class BGSearch extends AppCompatActivity implements DownloadXML.OnDataAvailable {

    private static final String TAG = "GPDEB";

    private ListView bg_search_list;
    ParseString parseString;
    private SearchView mSearchView;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bgsearch);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        bg_search_list = findViewById(R.id.bg_search_list);
        spinner = findViewById(R.id.spinner);
        spinner.setVisibility(View.GONE);

        String queryResult = getIntent().getStringExtra("boardgame");

        if(queryResult != null) {
            if(isOnline()) {
                DownloadXML xml = new DownloadXML(this, "https://www.boardgamegeek.com/xmlapi2/search?type=boardgame&query=");
                xml.execute(queryResult);
                spinner.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        mSearchView.setSearchableInfo(searchableInfo);

        if(getIntent().getStringExtra("boardgame") != null) {
            mSearchView.setIconified(true);
        } else {
            mSearchView.setIconified(false);
        }

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchView.clearFocus();
                Intent intent = new Intent(getApplicationContext(), BGSearch.class);
                intent.putExtra("boardgame", query);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public void onDataAvailable(final List<BGEntry> result) {

        spinner.setVisibility(View.GONE);

        Collections.sort(result);

        BGSearchAdapter adapter = new BGSearchAdapter(BGSearch.this, R.layout.list_record, result);
        bg_search_list.setAdapter(adapter);

        bg_search_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(BGSearch.this, BGAdd.class);
                intent.putExtra("game", result.get(position));
                startActivity(intent);
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
