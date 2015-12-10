package com.matih.earthquakemonitor.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.ColorUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.matih.earthquakemonitor.R;
import com.matih.earthquakemonitor.dataclasses.EarthquakeElement;
import com.matih.earthquakemonitor.utils.EarthquakeColorUtil;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetailFragment extends Fragment {

    private static final String ARG_EARTHQUAKE_ELEMENT = "earthquakeElement";

    private EarthquakeElement earthquakeElement;
    private OnFragmentInteractionListener mListener;
    private TextView textViewMagnitude;
    private TextView textViewDate;
    private TextView textViewTime;
    private TextView textViewLocation;
    private TextView textViewDepth;

    public DetailFragment() {

    }

    public static DetailFragment newInstance(EarthquakeElement earthquakeElement) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_EARTHQUAKE_ELEMENT, earthquakeElement);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            earthquakeElement = getArguments().getParcelable(ARG_EARTHQUAKE_ELEMENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail, container, false);
        textViewMagnitude = (TextView)v.findViewById(R.id.textView_fragment_detail_magnitude);
        textViewDate = (TextView)v.findViewById(R.id.textView_fragment_detail_date);
        textViewTime = (TextView)v.findViewById(R.id.textView_fragment_detail_time);
        textViewLocation = (TextView)v.findViewById(R.id.textView_fragment_detail_location_value);
        textViewDepth = (TextView)v.findViewById(R.id.textView_fragment_detail_depth_value);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        float[] hsl = new float[3];
        ColorUtils.colorToHSL(Color.parseColor(EarthquakeColorUtil.getQuakeColor(earthquakeElement.getMagnitude())), hsl);
        int color = ColorUtils.HSLToColor(new float[]{ hsl[0], 0.5f, 0.5f});
        EarthquakeColorUtil.makeRoundCorner(textViewMagnitude, color, 9);

        setDetailData();
    }

    private void setDetailData(){
        textViewMagnitude.setText(String.valueOf(earthquakeElement.getMagnitude()));
        textViewDate.setText(new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(earthquakeElement.getTime()));
        textViewTime.setText(new SimpleDateFormat("H:mm", Locale.US).format(earthquakeElement.getTime()));
        textViewLocation.setText(earthquakeElement.getPlaceName()
                + "\nlng: " + earthquakeElement.getQuakeLocation().getLng()
                + "\nlat: " + earthquakeElement.getQuakeLocation().getLat());
        textViewDepth.setText(earthquakeElement.getQuakeLocation().getDepth() + " km");
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
