package com.alexhart.leukemiaapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MedicationHolder extends AppCompatActivity {
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_holder);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new pagerAdapter(fm));
//        mViewPager.setOnPageChangeListener(mOnPageChangeListener);
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
