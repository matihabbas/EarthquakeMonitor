package com.matih.earthquakemonitor.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.matih.earthquakemonitor.R;
import com.matih.earthquakemonitor.dataclasses.EarthquakeElement;
import com.matih.earthquakemonitor.listeners.SimpleWebApiCallListener;
import com.matih.earthquakemonitor.managers.WebManager;
import com.matih.earthquakemonitor.utils.EarthquakeColorUtil;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class SummaryMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String DEBUG_TAG = SummaryMapActivity.class.getSimpleName();

    private ArrayList<EarthquakeElement> earthquakeElements;
    private GoogleMap googleMap;
    private HashMap<Marker, EarthquakeElement> earthquakeMarkerMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        earthquakeMarkerMap = new HashMap<>();
        earthquakeElements = getIntent().getParcelableArrayListExtra("EarthquakeElements");

        FragmentManager fragmentManager = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager
                .findFragmentById(R.id.map_activity_summary_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        this.googleMap.setBuildingsEnabled(false);
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        this.googleMap.getUiSettings().setAllGesturesEnabled(true);
        final Context context = this;
        this.googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                EarthquakeElement earthquakeElement = earthquakeMarkerMap.get(marker);
                Intent detailIntent = new Intent(context, DetailActivity.class);
                detailIntent.putExtra("DetailElement", earthquakeElement);
                startActivity(detailIntent);
            }
        });

        loadData();
    }

    private void loadData(){
        this.googleMap.clear();
        this.earthquakeMarkerMap.clear();
        if(earthquakeElements.size() > 0){
            for(EarthquakeElement earthquakeElement:earthquakeElements){
                LatLng location = new LatLng(earthquakeElement.getQuakeLocation().getLat(),
                        earthquakeElement.getQuakeLocation().getLng());

                float[] hsl = new float[3];
                int color = Color.parseColor(EarthquakeColorUtil.getQuakeColor(earthquakeElement.getMagnitude()));
                ColorUtils.colorToHSL(color, hsl);
                MarkerOptions markerOpts = new MarkerOptions()
                        .position(location)
                        .icon(BitmapDescriptorFactory.defaultMarker(hsl[0]))
                        .title(earthquakeElement.getPlaceName())
                        .snippet(getString(R.string.detail_magnitude) + String.valueOf(earthquakeElement.getMagnitude())
                                + " " + getString(R.string.detail_depth) + earthquakeElement.getQuakeLocation().getDepth());
                earthquakeMarkerMap.put(this.googleMap.addMarker(markerOpts), earthquakeElement);
            }

            this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(
                    earthquakeElements.get(0).getQuakeLocation().getLat(),
                    earthquakeElements.get(0).getQuakeLocation().getLng())));
        }
    }

    private void loadDataFromWeb(){
        WebManager.getInstance().GetSummary(this, new SimpleWebApiCallListener(){
            @Override
            public void onLoadingComplete(Context context, String resultJson) {
                try{
                    earthquakeElements = EarthquakeElement.getEarthquakeElementsFromJson(resultJson);
                    loadData();
                }
                catch (JSONException je){
                    Log.e(DEBUG_TAG, je.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_summary_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            loadDataFromWeb();
            return true;
        }
        else if(id == R.id.action_summary){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
