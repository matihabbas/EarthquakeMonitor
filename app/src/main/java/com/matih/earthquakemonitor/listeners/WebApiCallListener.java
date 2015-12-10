package com.matih.earthquakemonitor.listeners;

import android.content.Context;

public interface WebApiCallListener {

    void onLoadingStarted(Context context);
    void onLoadingFailed(Context context, String failedReason);
    void onLoadingComplete(Context context, String resultJson);
    void onLoadingCancelled(Context context);
}