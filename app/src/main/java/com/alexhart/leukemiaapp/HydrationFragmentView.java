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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alexhart.leukemiaapp.UserDatabase.WaterDataDBAdapter;
import com.alexhart.leukemiaapp.data.WaterContent;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;


public class HydrationFragmentView extends Fragment {

    TextView mIntakeText, mExcreteText, mDifText;
    private Spinner mSpinner;
    private ArrayList<WaterContent.WaterItem> mWaterContents = new ArrayList<>();
    private SpinnerAdapter mStringArrayAdapter;
    private ArrayList<String> mDates = new ArrayList<>();
    GraphView graphView;
    private WaterDataDBAdapter mWaterDataDBAdapter;
    public static final String WATER_UPDATE = "com.alexhart.leukemiaapp.hydration";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_hydration_graph, container, false);
        IntentFilter intentFilter = new IntentFilter(WATER_UPDATE);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver, intentFilter);
        mWaterDataDBAdapter = HydrationHolder.getWaterDatabase();

        graphView = (GraphView) v.findViewById(R.id.graphView);
        mSpinner = (Spinner) v.findViewById(R.id.spinner);
        mIntakeText = (TextView) v.findViewById(R.id.hydration_intake_show);
        mExcreteText = (TextView) v.findViewById(R.id.hydration_excrete_show);
        mDifText = (TextView) v.findViewById(R.id.hydration_dif_show);

        createGraphData();
        return v;
    }

    private void createGraphData() {
        graphView.removeAllSeries();
        mWaterContents.clear();
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
                mWaterContents.add(new WaterContent.WaterItem(date,intake,excrete,dif));

            } while(cursor.moveToNext());
        }

        graphView.setTitle("Fluid Intake/Excretion for " + month);
        graphView.getViewport().setScalable(true);
        graphView.addSeries(data);

        for (int i = 0; i<mWaterContents.size(); i++) {
            String date = mWaterContents.get(i).date;
            if (!mDates.contains(date)){
                mDates.add(date);
            }
        }
        // Close the cursor to avoid a resource leak.
        cursor.close();

        mStringArrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, mDates);
        mSpinner.setAdapter(mStringArrayAdapter);
        mSpinner.setOnItemSelectedListener(mSpinnerListener);
    }

    private Spinner.OnItemSelectedListener mSpinnerListener = new Spinner.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String date = mDates.get(i);
            double intake=0,excrete=0,dif=0;
            for (WaterContent.WaterItem item: mWaterContents) {
                if (item.date.equals(date)) {
                    intake += item.intake;
                    excrete += item.outtake;
                    dif += item.difference;
                }
            }
            mIntakeText.setText(String.valueOf(intake));
            mExcreteText.setText(String.valueOf(excrete));
            mDifText.setText(String.valueOf(dif));
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Water", "Broadcast received");
            createGraphData();
        }
    };

    private void makeToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);

    }
}

