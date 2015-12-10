package com.matih.earthquakemonitor.dataclasses;

import android.os.Parcel;
import android.os.Parcelable;

public class QuakeLocationElement implements Parcelable {

    private double lng;
    private double lat;
    private double depth;

    public QuakeLocationElement() {
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    protected QuakeLocationElement(Parcel in) {
        lng = in.readDouble();
        lat = in.readDouble();
        depth = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(lng);
        dest.writeDouble(lat);
        dest.writeDouble(depth);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<QuakeLocationElement> CREATOR = new Parcelable.Creator<QuakeLocationElement>() {
        @Override
        public QuakeLocationElement createFromParcel(Parcel in) {
            return new QuakeLocationElement(in);
        }

        @Override
        public QuakeLocationElement[] newArray(int size) {
            return new QuakeLocationElement[size];
        }
    };
}
