package com.bellychallenge;

import android.os.Parcel;
import android.os.Parcelable;

import com.yelp.clientlib.entities.Business;

/**
 * Created by Suhong on 7/16/2016.
 */
public class BusinessParcelable implements Parcelable {

    private String name;
    private double distance;
    private String type;
    private String url;

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
    }

    private BusinessParcelable(Parcel in) {
        name = in.readString();
        distance = in.readDouble();
        type = in.readString();
        url = in.readString();
    }

    public BusinessParcelable(Business business) {
        name = business.name();
        distance = business.distance();
        type = business.categories().get(0).name();
        url = business.url();
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
}
