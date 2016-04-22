package com.alexhart.leukemiaapp;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.alexhart.leukemiaapp.UserDatabase.WaterDataDBAdapter;

import java.util.Calendar;


public class HydrationFragmentAdd extends Fragment {
    private EditText mInake, mExcrete;
    private WaterDataDBAdapter mWaterDataDBAdapter;

    public static final String[] months = {"January", "February", "March", "April",
        "May", "June", "July", "August", "September", "October", "November", "December"};



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hydration_add, container, false);
        mWaterDataDBAdapter = HydrationHolder.getWaterDatabase();
        initUI(v);
        return v;
    }

    private void initUI(View view) {
        mInake = (EditText)view.findViewById(R.id.hydration_intake_edit);
        mExcrete = (EditText)view.findViewById(R.id.hydration_excrete_edit);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.hydration_fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!inputCheck()) {
                    makeToast("Check inputs!");
                    return;
                }
                String date = getDate();

                double intake = Double.parseDouble(mInake.getText().toString());
                double excrete = Double.parseDouble(mExcrete.getText().toString());
                double dif = intake - excrete;

                Log.d("Intake: ", ""+intake);
                Log.d("Excrete: ", ""+excrete);
                Log.d("Date: ", date);

                long l = mWaterDataDBAdapter.createWaterData("date", 1.0, 1.0,1.0);
                if (l == -1) {
                    Snackbar.make(view, "Error!", Snackbar.LENGTH_SHORT).show();
                }else {
                    Snackbar.make(view, "Data added", Snackbar.LENGTH_SHORT).show();
                }
                resetText();
            }
        });

        FloatingActionButton fab2 = (FloatingActionButton) view.findViewById(R.id.hydration_fab_delete);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetText();
                View v = getActivity().getCurrentFocus();
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });
    }

    private String getDate() {
        Calendar c= Calendar.getInstance();
        int day=c.get(Calendar.DAY_OF_MONTH);
        int year=c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        return months[month] + "." + day + "." + year;
//
    }

    private boolean inputCheck() {
        return !mExcrete.getText().toString().equals("") &&
                !mInake.getText().toString().equals("");
    }

    private void resetText() {
        mInake.setText("");
        mExcrete.setText("");
    }

    private void makeToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }
}


