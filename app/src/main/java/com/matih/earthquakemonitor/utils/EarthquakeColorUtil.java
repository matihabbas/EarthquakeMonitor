package com.matih.earthquakemonitor.utils;

import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.view.View;

public class EarthquakeColorUtil {

    public static String getQuakeColor(double magnitude){
        if(magnitude > 0.0){
            if(magnitude < 1.0) return "#00FF00";
            else if(magnitude < 2.0) return "#1CE300";
            else if(magnitude < 3.0) return "#39C600";
            else if(magnitude < 4.0) return "#55AA00";
            else if(magnitude < 5.0) return "#718E00";
            else if(magnitude < 6.0) return "#8E7100";
            else if(magnitude < 7.0) return "#AA5500";
            else if(magnitude < 8.0) return "#C63900";
            else if(magnitude < 9.0) return "#E31C00";
            else if(magnitude < 10.0) return "#FF0000";
            else return "#BDBDBD";
        }
        else return "#BDBDBD";
    }

    public static void makeRoundCorner(View v, int color, int radius){
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(color);
        gradientDrawable.setCornerRadius(radius);
        if(Build.VERSION.SDK_INT < 16) v.setBackgroundDrawable(gradientDrawable);
        else v.setBackground(gradientDrawable);
    }
}
