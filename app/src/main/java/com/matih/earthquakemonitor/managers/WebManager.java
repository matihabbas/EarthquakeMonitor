package com.matih.earthquakemonitor.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.matih.earthquakemonitor.R;
import com.matih.earthquakemonitor.listeners.WebApiCallListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebManager {

    private static final String DEBUG_TAG = WebManager.class.getSimpleName();

    private static WebManager sharedInstance;

    public static WebManager getInstance(){
        if(sharedInstance == null){
            sharedInstance = new WebManager();
        }
        return sharedInstance;
    }

    private String httpGet(Context context, String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setAllowUserInteraction(false);
        conn.setConnectTimeout(context.getResources().getInteger(R.integer.conn_timeout_ms));
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        if (conn.getResponseCode() != 200) {
            throw new IOException(conn.getResponseMessage());
        }

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();

        conn.disconnect();
        return sb.toString();
    }

    private class HttpPostTask extends AsyncTask<String, Void, String> {
        private Context context;
        private WebApiCallListener mListener;

        public HttpPostTask(Context context, WebApiCallListener mListener) {
            this.context = context;
            this.mListener = mListener;
        }

        @Override
        protected void onPreExecute() {
            mListener.onLoadingStarted(context);
            if(!isConnected(context)) {
                cancel(true);
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String responseJson = null;
            if(isCancelled()) return null;
            try{
                responseJson = httpGet(context, params[0]);
            }
            catch (Exception e){
                Log.e(DEBUG_TAG, e.getMessage());
            }
            return responseJson;
        }

        @Override
        protected void onPostExecute(String jsonResponse) {
            if(jsonResponse != null) mListener.onLoadingComplete(context, jsonResponse);
            else mListener.onLoadingFailed(context, "POST failed");
        }

        @Override
        protected void onCancelled() {
            if(context != null) mListener.onLoadingFailed(context, "Not connected");
        }
    }

    private boolean isConnected(Context context){
        if(context == null) return false;
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public void GetSummary(Context context, final WebApiCallListener listener){
        String downloadUrl = context.getResources().getString(R.string.usgs_hour_url);
        final String cacheKeyName = "cache_all_hour";

        new HttpPostTask(context, new WebApiCallListener() {
            @Override
            public void onLoadingStarted(Context context) {
                listener.onLoadingStarted(context);
            }

            @Override
            public void onLoadingFailed(Context context, String failedReason) {
                listener.onLoadingFailed(context, failedReason);
                String cachedResponse = getJsonResponseFromCache(context, cacheKeyName);
                if(!cachedResponse.isEmpty()) {
                    listener.onLoadingComplete(context, cachedResponse);
                }
            }

            @Override
            public void onLoadingComplete(Context context, String resultJson) {
                listener.onLoadingComplete(context, resultJson);
                saveJsonResponseToCache(context, cacheKeyName, resultJson);
            }

            @Override
            public void onLoadingCancelled(Context context) {
                listener.onLoadingCancelled(context);
                String cachedResponse = getJsonResponseFromCache(context, cacheKeyName);
                if(!cachedResponse.isEmpty()) {
                    listener.onLoadingComplete(context, cachedResponse);
                }
            }
        }).execute(downloadUrl);
    }

    private void saveJsonResponseToCache(Context context, String keyName, String jsonString){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getResources().getString(R.string.cache_filename), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(keyName, jsonString);
        editor.apply();
    }

    private String getJsonResponseFromCache(Context context, String keyName){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getResources().getString(R.string.cache_filename), Context.MODE_PRIVATE);
        return sharedPreferences.getString(keyName, "");
    }
}