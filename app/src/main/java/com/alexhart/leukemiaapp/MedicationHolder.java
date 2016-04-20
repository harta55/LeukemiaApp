package com.alexhart.leukemiaapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.alexhart.leukemiaapp.UserDatabase.MedicationDBAdapter;

import java.sql.SQLException;

public class MedicationHolder extends AppCompatActivity {
    ViewPager mViewPager;
    private static MedicationDBAdapter mMedicationDBAdapter;
    private final String TAG = "medication holder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_holder);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new pagerAdapter(fm));
        openDB();

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    View view= getCurrentFocus();
                    if (view!= null) {
                        InputMethodManager imm= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
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
            mMedicationDBAdapter = new MedicationDBAdapter(getApplicationContext()).open();
        }catch (SQLException e){
            Toast.makeText(getApplicationContext(), "Data not created!", Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "Table created!");
    }

    private void closeDB() {
        mMedicationDBAdapter.close();
    }

    public static MedicationDBAdapter getMedicationDBAdapter() {
        return mMedicationDBAdapter;
    }

    public static void setMedicationDBAdapter(MedicationDBAdapter medicationDBAdapter) {
        mMedicationDBAdapter = medicationDBAdapter;
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
        }

        return true;
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
                fragment = new MedicationsFragAdd();
                break;
            case 1:
                fragment = new MedicationsFragView();
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
                return "Medication Add";
            case 1:
                return "Medication View";
        }
        return null;
    }
}
