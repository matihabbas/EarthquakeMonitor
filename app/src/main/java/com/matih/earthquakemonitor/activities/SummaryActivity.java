package com.matih.earthquakemonitor.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.matih.earthquakemonitor.R;
import com.matih.earthquakemonitor.fragments.SummaryMapFragment;
import com.matih.earthquakemonitor.fragments.SummaryFragment;

public class SummaryActivity extends AppCompatActivity {

    private SummaryMapFragment summaryMapFragment;
    private SummaryFragment summaryFragment;
    private Toolbar toolbar;
    private boolean onMap = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(savedInstanceState != null){
            onMap = savedInstanceState.getBoolean("onMap", false);
            if(onMap) {
                MenuItem menuItem = toolbar.getMenu().getItem(0);
                menuItem.setIcon(R.drawable.summary_list);
                menuItem.setTitle(R.string.action_summary);
            }
            summaryMapFragment = (SummaryMapFragment)getSupportFragmentManager().findFragmentByTag(
                    SummaryMapFragment.class.getSimpleName());
            summaryFragment = (SummaryFragment)getSupportFragmentManager().findFragmentByTag(
                    SummaryFragment.class.getSimpleName());
        }
        else{
            summaryFragment = SummaryFragment.newInstance();

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout_activity_main_fragment, summaryFragment,
                    SummaryFragment.class.getSimpleName());
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("onMap", onMap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_summary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.action_refresh) {
            if(summaryFragment != null){
                summaryFragment.onRefresh();
            }
            if(summaryMapFragment != null){
                summaryMapFragment.loadDataFromWeb();
            }
            return true;
        }
        else if(id == R.id.action_map_switch){
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            onMap = !onMap;

            if(onMap){
                item.setIcon(R.drawable.summary_list);
                item.setTitle(R.string.action_summary);
                if(summaryMapFragment == null) summaryMapFragment = SummaryMapFragment.newInstance();
                fragmentTransaction.replace(R.id.frameLayout_activity_main_fragment,
                        summaryMapFragment, SummaryMapFragment.class.getSimpleName());
            }
            else{
                item.setIcon(R.drawable.summary_map);
                item.setTitle(R.string.action_map);
                if(summaryFragment == null) summaryFragment = SummaryFragment.newInstance();
                fragmentTransaction.replace(R.id.frameLayout_activity_main_fragment,
                        summaryFragment, SummaryFragment.class.getSimpleName());
            }

            fragmentTransaction.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
