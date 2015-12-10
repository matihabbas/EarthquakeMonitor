package com.matih.earthquakemonitor.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.matih.earthquakemonitor.R;
import com.matih.earthquakemonitor.activities.DetailActivity;
import com.matih.earthquakemonitor.activities.SummaryMapActivity;
import com.matih.earthquakemonitor.adapters.SummaryRecyclerViewAdapter;
import com.matih.earthquakemonitor.dataclasses.EarthquakeElement;
import com.matih.earthquakemonitor.listeners.RecyclerViewItemClickListener;
import com.matih.earthquakemonitor.listeners.SimpleWebApiCallListener;
import com.matih.earthquakemonitor.managers.WebManager;
import com.matih.earthquakemonitor.utils.EarthquakeColorUtil;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class SummaryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, RecyclerViewItemClickListener {

    private static final String DEBUG_TAG = SummaryFragment.class.getSimpleName();

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private SummaryRecyclerViewAdapter mAdapter;

    public SummaryFragment() {

    }

    public static SummaryFragment newInstance() {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout_content_summary);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_content_summary);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mAdapter = new SummaryRecyclerViewAdapter(new ArrayList<EarthquakeElement>(), this);
        mRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setColorSchemeColors(Color.parseColor(EarthquakeColorUtil.getQuakeColor(0.1)),
                Color.parseColor(EarthquakeColorUtil.getQuakeColor(9.1)));
        loadFeed();
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        loadFeed();
    }

    public void onMapPressed(){
        Intent summaryMapIntent = new Intent(getActivity(), SummaryMapActivity.class);
        summaryMapIntent.putParcelableArrayListExtra("EarthquakeElements", mAdapter.getmArrayList());
        startActivity(summaryMapIntent);
    }

    private void loadFeed(){
        WebManager.getInstance().GetSummary(getActivity(), new SimpleWebApiCallListener(){
            @Override
            public void onLoadingComplete(Context context, String resultJson) {
                ArrayList<EarthquakeElement> earthquakeElements;
                try{
                    earthquakeElements = EarthquakeElement.getEarthquakeElementsFromJson(resultJson);
                }
                catch (JSONException je){
                    Log.e(DEBUG_TAG, je.getMessage());
                    return;
                }
                mAdapter.setmArrayList(earthquakeElements);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onLoadingFailed(Context context, String failedReason) {
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onLoadingCancelled(Context context) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        EarthquakeElement selectedElement = mAdapter.getItemAtPosition(position);

        Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
        detailIntent.putExtra("DetailElement", selectedElement);
        startActivity(detailIntent);
    }
}
