package com.bellychallenge;

import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.yelp.clientlib.entities.SearchResponse;

import java.util.ArrayList;

/**
 * Created by Suhong on 7/16/2016.
 */
public class DisplayList extends AppCompatActivity{

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displaylist);
        Intent intent = getIntent();
        boolean needToStore = intent.getBooleanExtra(MainActivity.EXTRA_STORE, false);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(this));

        ArrayList<Parcelable> bps = intent.getParcelableArrayListExtra(MainActivity.EXTRA_LIST);
        adapter = new BusinessListAdapter(bps);
        recyclerView.setAdapter(adapter);
    }

}
