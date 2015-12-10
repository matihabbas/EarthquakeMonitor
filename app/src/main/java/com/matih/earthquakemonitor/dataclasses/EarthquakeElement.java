package com.matih.earthquakemonitor.dataclasses;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EarthquakeElement implements Parcelable{

    private String id;
    private String placeName;
    private double magnitude;
    private Date time;
    private QuakeLocationElement quakeLocation;

    public EarthquakeElement() {
    }

    public static ArrayList<EarthquakeElement> getEarthquakeElementsFromJson(String jsonString)
            throws JSONException{
        ArrayList<EarthquakeElement> earthquakeElementArrayList = new ArrayList<>();

        JSONArray featuresArray = new JSONObject(jsonString).getJSONArray("features");
        for(int i = 0; i < featuresArray.length(); i++){
            JSONObject featuresObject = featuresArray.getJSONObject(i);

            EarthquakeElement earthquakeElement = new EarthquakeElement();
            earthquakeElement.setId(featuresObject.getString("id"));
            earthquakeElement.setMagnitude(featuresObject.getJSONObject("properties").getDouble("mag"));
            earthquakeElement.setPlaceName(featuresObject.getJSONObject("properties").getString("place"));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(featuresObject.getJSONObject("properties").getLong("time"));
            earthquakeElement.setTime(calendar.getTime());

            QuakeLocationElement quakeLocation = new QuakeLocationElement();
            quakeLocation.setLng(featuresObject.getJSONObject("geometry")
                    .getJSONArray("coordinates").getDouble(0));
            quakeLocation.setLat(featuresObject.getJSONObject("geometry")
                    .getJSONArray("coordinates").getDouble(1));
            quakeLocation.setDepth(featuresObject.getJSONObject("geometry")
                    .getJSONArray("coordinates").getDouble(2));
            earthquakeElement.setQuakeLocation(quakeLocation);

            earthquakeElementArrayList.add(earthquakeElement);
        }

        return earthquakeElementArrayList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public QuakeLocationElement getQuakeLocation() {
        return quakeLocation;
    }

    public void setQuakeLocation(QuakeLocationElement quakeLocation) {
        this.quakeLocation = quakeLocation;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    protected EarthquakeElement(Parcel in) {
        id = in.readString();
        placeName = in.readString();
        magnitude = in.readDouble();
        long tmpTime = in.readLong();
        time = tmpTime != -1 ? new Date(tmpTime) : null;
        quakeLocation = (QuakeLocationElement) in.readValue(QuakeLocationElement.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(placeName);
        dest.writeDouble(magnitude);
        dest.writeLong(time != null ? time.getTime() : -1L);
        dest.writeValue(quakeLocation);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<EarthquakeElement> CREATOR = new Parcelable.Creator<EarthquakeElement>() {
        @Override
        public EarthquakeElement createFromParcel(Parcel in) {
            return new EarthquakeElement(in);
        }

        @Override
        public EarthquakeElement[] newArray(int size) {
            return new EarthquakeElement[size];
        }
    };
}
