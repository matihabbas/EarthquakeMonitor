package com.matih.earthquakemonitor.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.matih.earthquakemonitor.R;
import com.matih.earthquakemonitor.activities.DetailActivity;
import com.matih.earthquakemonitor.dataclasses.EarthquakeElement;
import com.matih.earthquakemonitor.listeners.SimpleWebApiCallListener;
import com.matih.earthquakemonitor.managers.WebManager;
import com.matih.earthquakemonitor.utils.EarthquakeColorUtil;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class SummaryMapFragment extends Fragment implements OnMapReadyCallback {

    private static final String DEBUG_TAG = SummaryMapFragment.class.getSimpleName();

    private GoogleMap googleMap;
    private HashMap<Marker, EarthquakeElement> earthquakeMarkerMap;

    public SummaryMapFragment() {

    }

    public static SummaryMapFragment newInstance() {
        SummaryMapFragment fragment = new SummaryMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_summary_map, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        earthquakeMarkerMap = new HashMap<>();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        SupportMapFragment mapFragment = new SupportMapFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout_summary_map_fragment, mapFragment,
                SupportMapFragment.class.getSimpleName());
        fragmentTransaction.commit();

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
        this.googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                EarthquakeElement earthquakeElement = earthquakeMarkerMap.get(marker);
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                detailIntent.putExtra("DetailElement", earthquakeElement);
                startActivity(detailIntent);
            }
        });

        loadDataFromWeb();
    }

    public void loadDataFromWeb(){
        WebManager.getInstance().GetSummary(getActivity(), new SimpleWebApiCallListener() {
            @Override
            public void onLoadingComplete(Context context, String resultJson) {
                try {
                    ArrayList<EarthquakeElement> earthquakeElements = EarthquakeElement.getEarthquakeElementsFromJson(resultJson);
                    loadData(earthquakeElements);
                } catch (JSONException je) {
                    Log.e(DEBUG_TAG, je.getMessage());
                }
            }
        });
    }

    private void loadData(ArrayList<EarthquakeElement> earthquakeElements){
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
}
