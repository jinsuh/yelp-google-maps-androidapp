package com.bellychallenge;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.yelp.clientlib.entities.Business;

import java.io.ByteArrayOutputStream;

/**
 * Created by Suhong on 7/16/2016.
 */
public class BusinessParcelable implements Parcelable {

    private String name;
    private double distance;
    private String type;
    private String url;
    private byte[] imageArr;
    private boolean isClosed;

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<BusinessParcelable> CREATOR
            = new Parcelable.Creator<BusinessParcelable>() {
        public BusinessParcelable createFromParcel(Parcel in) {
            return new BusinessParcelable(in);
        }

        public BusinessParcelable[] newArray(int size) {
            return new BusinessParcelable[size];
        }
    };

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(name);
        out.writeDouble(distance);
        out.writeString(type);
        out.writeString(url);
        out.writeByte((byte) (isClosed ? 1 : 0));
        out.writeInt(imageArr.length);
        out.writeByteArray(imageArr);
    }

    private BusinessParcelable(Parcel in) {
        name = in.readString();
        distance = in.readDouble();
        type = in.readString();
        url = in.readString();
        isClosed = in.readByte() != 0;
        int length = in.readInt();
        imageArr = new byte[length];
        in.readByteArray(imageArr);
    }

    public BusinessParcelable(Business business, Bitmap bm) {
        name = business.name();
        distance = business.distance();
        type = business.categories().get(0).name();
        url = business.url();
        isClosed = business.isClosed();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        imageArr = stream.toByteArray();
    }

    public BusinessParcelable(String name, double distance, String type, String url, byte[] imageArr, boolean isClosed) {
        this.name = name;
        this.distance = distance;
        this.type = type;
        this.url = url;
        this.imageArr = imageArr;
        this.isClosed = isClosed;
    }

    public String getName() {
        return name;
    }

    public Double getDistance() {
        return distance;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public boolean getClosed() {
        return isClosed;
    }

    public byte[] getImageArr() {
        return imageArr;
    }
}
