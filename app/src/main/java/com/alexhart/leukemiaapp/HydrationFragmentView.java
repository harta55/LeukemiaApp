package com.alexhart.leukemiaapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alexhart.leukemiaapp.UserDatabase.WaterDataDBAdapter;
import com.alexhart.leukemiaapp.data.MedContent;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


public class HydrationFragmentView extends Fragment {

    //set up graph view to display data
    TextView mIntakeText, mExcreteText, mDifText;
    GraphView graphView;
    private WaterDataDBAdapter mWaterDataDBAdapter;
    public static final String WATER_UPDATE = "com.alexhart.leukemiaapp.hydration";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_hydration_graph, container, false);
        // Inflate the layout for this fragment

        IntentFilter intentFilter = new IntentFilter(WATER_UPDATE);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver, intentFilter);
        mWaterDataDBAdapter = HydrationHolder.getWaterDatabase();
        graphView = (GraphView) v.findViewById(R.id.graphView);
        mIntakeText = (TextView) v.findViewById(R.id.hydration_intake_text);
        mExcreteText = (TextView) v.findViewById(R.id.hydration_excrete_text);
        mDifText = (TextView) v.findViewById(R.id.hydration_difference_text);

        return v;
    }

    private void createGraphData() {
        graphView.removeAllSeries();
        LineGraphSeries<DataPoint> data = new LineGraphSeries<>(new DataPoint[]{});
        Cursor cursor = mWaterDataDBAdapter.getAllWaterData();
        String month = "";
        // Reset cursor to start, checking to see if there's data:
        if (cursor.moveToFirst()) {
            do {
                long rowID = cursor.getLong(cursor.getColumnIndex(WaterDataDBAdapter.KEY_ROW_ID));
                String date = cursor.getString(cursor.getColumnIndex(WaterDataDBAdapter.KEY_DATE));
                String[] dates = date.split("\\.", -1); //index 1 is day#
                month = dates[0];
                Log.d("Date: ", dates[1]);
                double intake = cursor.getDouble(cursor.getColumnIndex(WaterDataDBAdapter.KEY_IN));
                double excrete = cursor.getDouble(cursor.getColumnIndex(WaterDataDBAdapter.KEY_OUT));
                double dif = cursor.getDouble(cursor.getColumnIndex(WaterDataDBAdapter.KEY_DIF));
                data.appendData(new DataPoint(Integer.parseInt(dates[1]),dif), true, 100);

            } while(cursor.moveToNext());
        }

        graphView.setTitle("Fluid Intake/Excretion for " + month);
        graphView.getViewport().setScalable(true);
        graphView.addSeries(data);

        // Close the cursor to avoid a resource leak.
//        cursor.close();
    }
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Water", "Broadcast received");
            createGraphData();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);

    }
}

