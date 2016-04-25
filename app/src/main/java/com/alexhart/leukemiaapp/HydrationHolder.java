package com.alexhart.leukemiaapp;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.alexhart.leukemiaapp.UserDatabase.WaterDataDBAdapter;

import java.sql.SQLException;
import java.util.Random;

public class HydrationHolder extends AppCompatActivity {
    private ViewPager hViewPager;
    private static WaterDataDBAdapter mWaterDataDBAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_holder);
        ActionBar actionBar = getActionBar();
        if (actionBar!=null) {
            actionBar.setHomeButtonEnabled(true);
        }

        hViewPager = (ViewPager) findViewById(R.id.pager);

        openDB();
        FragmentManager fm = getSupportFragmentManager();
        hViewPager.setAdapter(new pagerAdapter(fm));


        hViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void openDB() {
        try {
            mWaterDataDBAdapter = new WaterDataDBAdapter(getApplicationContext()).open();
        }catch (SQLException e){
            Toast.makeText(getApplicationContext(), "Data not created!", Toast.LENGTH_SHORT).show();
        }
    }

    private void closeDB() {
        mWaterDataDBAdapter.close();
    }

    public static WaterDataDBAdapter getWaterDatabase() {
        return mWaterDataDBAdapter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        closeDB();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main, menu);


        invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();

        switch(id){
            case R.id.menu_database:
                startActivity(new Intent(getApplicationContext(),PreferencesFragment.class));
                break;
            case R.id.menu_gen_water_data:
                Random rand = new Random();
                mWaterDataDBAdapter.deleteAll();
                for (int i = 0; i<11;i++) {
                    String date = "April." + i + "." + 2016;
                    double intake = rand.nextInt(50);
                    double excrete = rand.nextInt(50);
                    double dif = intake - excrete;

                    Log.d("Date: ",date);

                    long temp = mWaterDataDBAdapter.createWaterData(date, intake, excrete, dif);
                    if (temp == -1) {
                        Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Data Created", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.menu_refresh:
                sendBroadcast(HydrationFragmentView.WATER_UPDATE);
                break;
            case android.R.id.home:
                this.finish();
                break;

        }
        return true;
    }

    private void sendBroadcast(String action) {
        Log.d("Hydration", "Broadcast sent");
        Intent i = new Intent(action);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
    }
}

    class pagerAdapter extends FragmentPagerAdapter {

        public pagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //return fragment at given position
        @Override
        public Fragment getItem(int position) {
            Log.d("Frag", "Position: " + position);
            Fragment fragment = null;

            switch (position) {
                case 0:
                    fragment = new HydrationFragmentAdd();
                    break;
                case 1:
                    fragment = new HydrationFragmentView();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            //# pages
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Log.d("MainActivity", "GetPageTitle");
            switch (position) {
                case 0:
                    return "Hydration Add";
                case 1:
                    return "Hydration View";
            }
            return null;
        }
    }

