package com.alexhart.leukemiaapp;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.alexhart.leukemiaapp.UserDatabase.MedicationDBAdapter;

public class MedicationsFragAdd extends Fragment {

    private MedicationDBAdapter mMedicationDBAdapter;
    private String TAG = "MedAdd";
    private EditText mNameEdit, mDoseEdit, mFreqEdit;
    public static final String MED_UPDATE = "com.alexhart.leukemiaapp";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_medications_add, container, false);
        mMedicationDBAdapter = MedicationHolder.getMedicationDBAdapter();
        initUI(v);

        return v;
    }

    private void initUI(View view) {
        mNameEdit = (EditText)view.findViewById(R.id.text_medication_name);
        mDoseEdit = (EditText)view.findViewById(R.id.text_medication_dose);
        mFreqEdit = (EditText)view.findViewById(R.id.text_medication_freq);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!inputCheck()) {
                    makeToast("Check inputs!");
                    return;
                }

                String name = mNameEdit.getText().toString();
                double dose = Double.parseDouble(mDoseEdit.getText().toString());
                double freq = Double.parseDouble(mFreqEdit.getText().toString());

                mMedicationDBAdapter.createMedicationData(name, dose, freq);

                Snackbar.make(view, "Data added", Snackbar.LENGTH_SHORT).show();
                resetText();
                sendBroadcast(MED_UPDATE);
            }
        });

        FloatingActionButton fab2 = (FloatingActionButton) view.findViewById(R.id.fab_delete);
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

    private boolean inputCheck() {
        return !mNameEdit.getText().toString().equals("") &&
                !mDoseEdit.getText().toString().equals("") &&
                !mFreqEdit.getText().toString().equals("");
    }

    private void resetText() {
        mNameEdit.setText("");
        mDoseEdit.setText("");
        mFreqEdit.setText("");
    }

    private void makeToast(String msg) {
        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
    }

    private void sendBroadcast(String action) {
        Log.d(TAG, "Broadcast sent");
        Intent i = new Intent(action);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(i);
    }
}
