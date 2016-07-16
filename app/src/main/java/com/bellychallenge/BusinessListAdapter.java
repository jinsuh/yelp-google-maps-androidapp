package com.bellychallenge;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Suhong on 7/16/2016.
 */
public class BusinessListAdapter extends RecyclerView.Adapter<BusinessListAdapter.ViewHolder> {

    private ArrayList<Parcelable> dataset;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView title;
        public TextView distance;
        public TextView type;

        private String url;
        private final Context context;

        public ViewHolder(View v) {
            super(v);
            context = v.getContext();
            title = (TextView) v.findViewById(R.id.name);
            distance = (TextView) v.findViewById(R.id.distance);
            type = (TextView) v.findViewById(R.id.type);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public BusinessListAdapter(ArrayList<Parcelable> bParcelables) {
        dataset = bParcelables;
    }

    @Override
    public BusinessListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BusinessParcelable bp = (BusinessParcelable) dataset.get(position);
        holder.title.setText(bp.getName());
        String distanceText = formatDistance(bp.getDistance());
        holder.distance.setText(distanceText);
        holder.type.setText(bp.getType());
        holder.setUrl(bp.getUrl());
    }

    private String formatDistance(double dist) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(dist);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }


}
