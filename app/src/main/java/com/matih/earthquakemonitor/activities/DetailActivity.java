package com.matih.earthquakemonitor.activities;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.matih.earthquakemonitor.R;
import com.matih.earthquakemonitor.dataclasses.EarthquakeElement;
import com.matih.earthquakemonitor.fragments.DetailFragment;
import com.matih.earthquakemonitor.utils.EarthquakeColorUtil;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback, DetailFragment.OnFragmentInteractionListener {

    private static final String DEBUG_TAG = DetailActivity.class.getSimpleName();

    private EarthquakeElement earthquakeElement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.detail_button_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        earthquakeElement = getIntent().getParcelableExtra("DetailElement");

        DetailFragment detailFragment = DetailFragment.newInstance(earthquakeElement);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.frameLayout_activity_detail_fragment, detailFragment,
                DetailFragment.class.getSimpleName());
        fragmentTransaction.commit();

        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager
                .findFragmentById(R.id.map_activity_detail);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(DEBUG_TAG, "onMapReady");

        googleMap.setMyLocationEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.setBuildingsEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.clear();

        LatLng location = new LatLng(earthquakeElement.getQuakeLocation().getLat(),
                earthquakeElement.getQuakeLocation().getLng());

        float[] hsl = new float[3];
        int color = Color.parseColor(EarthquakeColorUtil.getQuakeColor(earthquakeElement.getMagnitude()));
        ColorUtils.colorToHSL(color, hsl);
        MarkerOptions markerOpts = new MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.defaultMarker(hsl[0]));
        googleMap.addMarker(markerOpts);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10.0f));
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
