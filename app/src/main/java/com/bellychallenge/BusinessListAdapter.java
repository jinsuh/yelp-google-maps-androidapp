package com.bellychallenge;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
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
        public TextView closed;
        public ImageView image;

        private String url;
        private final Context context;

        public ViewHolder(View v) {
            super(v);
            context = v.getContext();
            title = (TextView) v.findViewById(R.id.name);
            distance = (TextView) v.findViewById(R.id.distance);
            type = (TextView) v.findViewById(R.id.type);
            closed = (TextView) v.findViewById(R.id.closed);
            image = (ImageView) v.findViewById(R.id.image);
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

        public void setImage(byte[] imageArr) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageArr, 0, imageArr.length);
            image.setImageBitmap(bitmap);
        }
    }

    public BusinessListAdapter(ArrayList<Parcelable> bParcelables) {
        this.dataset = bParcelables;
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
        holder.setImage(bp.getImageArr());
        setOpenClosed(holder.closed, bp.getClosed());
    }

    private void setOpenClosed(TextView closed, boolean isClosed) {
        if (isClosed) {
            closed.setText(closed.getContext().getString(R.string.closed_sign));
            closed.setTextColor(ContextCompat.getColor(closed.getContext(), R.color.closed_gray));
        }
        else {
            closed.setText(closed.getContext().getString(R.string.open_sign));
            closed.setTextColor(ContextCompat.getColor(closed.getContext(), R.color.green));
        }
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
