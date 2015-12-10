package com.matih.earthquakemonitor.adapters;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.matih.earthquakemonitor.R;
import com.matih.earthquakemonitor.dataclasses.EarthquakeElement;
import com.matih.earthquakemonitor.listeners.RecyclerViewItemClickListener;
import com.matih.earthquakemonitor.utils.EarthquakeColorUtil;

import java.util.ArrayList;

public class SummaryRecyclerViewAdapter extends RecyclerView.Adapter<SummaryRecyclerViewAdapter.ViewHolder> {

    private ArrayList<EarthquakeElement> mArrayList;
    private RecyclerViewItemClickListener mItemClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextViewPlaceName;
        public TextView mTextViewMagnitude;
        public LinearLayout mLinearLayoutBg;

        public ViewHolder(View itemView) {
            super(itemView);

            mTextViewPlaceName = (TextView) itemView.findViewById(R.id.textView_row_summary_place);
            mTextViewMagnitude = (TextView) itemView.findViewById(R.id.textView_row_summary_magnitude);
            mLinearLayoutBg = (LinearLayout) itemView.findViewById(R.id.linearLayout_row_summary_bg);
        }
    }

    public SummaryRecyclerViewAdapter(ArrayList<EarthquakeElement> mArrayList, RecyclerViewItemClickListener mItemClickListener) {
        this.mArrayList = mArrayList;
        this.mItemClickListener = mItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_summary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.setIsRecyclable(true);

        EarthquakeElement earthquakeElement = mArrayList.get(position);

        holder.mTextViewPlaceName.setText(earthquakeElement.getPlaceName());
        holder.mTextViewMagnitude.setText(String.valueOf(earthquakeElement.getMagnitude()));

        float[] hsl = new float[3];
        ColorUtils.colorToHSL(Color.parseColor(EarthquakeColorUtil.getQuakeColor(earthquakeElement.getMagnitude())), hsl);
        int color = ColorUtils.HSLToColor(new float[]{ hsl[0], 0.5f, 0.5f});
        EarthquakeColorUtil.makeRoundCorner(holder.mLinearLayoutBg, color, 8);

        final int itemPosition = position;

        holder.mLinearLayoutBg.setOnClickListener(null);
        holder.mLinearLayoutBg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        LinearLayout view = (LinearLayout) v;
                        view.getBackground().setColorFilter(Color.parseColor("#999999"), PorterDuff.Mode.MULTIPLY);
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        LinearLayout view = (LinearLayout) v;
                        view.getBackground().clearColorFilter();
                        view.invalidate();
                        mItemClickListener.onItemClick(itemPosition);
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        LinearLayout view = (LinearLayout) v;
                        view.getBackground().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public void setmArrayList(ArrayList<EarthquakeElement> mArrayList) {
        this.mArrayList = mArrayList;
        notifyDataSetChanged();
    }

    public ArrayList<EarthquakeElement> getmArrayList() {
        return mArrayList;
    }

    public EarthquakeElement getItemAtPosition(int position){
        return mArrayList.get(position);
    }
}
