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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.alexhart.leukemiaapp.UserDatabase.MedicationDBAdapter;
import com.alexhart.leukemiaapp.medication.MedContent;

/**
 * A fragment representing a list of Items.
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 **/
public class MedicationsFragView extends Fragment implements AbsListView.OnItemClickListener {

    private AbsListView mListView;
    private ListAdapter mAdapter;
    private MedicationDBAdapter mMedicationDBAdapter;
    private String[] mMedNames;
    private final String TAG = "MedFragView";
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);
        Log.d(TAG, "onCreateView");
        mMedicationDBAdapter = MedicationHolder.getMedicationDBAdapter();
        mListView = (AbsListView) view.findViewById(android.R.id.list);

        IntentFilter intentFilter = new IntentFilter(MedicationsFragAdd.MED_UPDATE);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver, intentFilter);

        createMedList();
        displayMedList();

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(),"pos: " + position, Toast.LENGTH_SHORT).show();

    }

    private void displayMedList() {
        mMedNames = new String[MedContent.ITEMS.size()];

        if (mMedNames.length == 0) {
            makeToast("No data!");
            return;
        }

        int n = 0;
        for (MedContent.MedItem medItem:MedContent.ITEMS) {
            mMedNames[n] = medItem.name;
            n++;
        }

        mAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, mMedNames);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    // Display an entire recordset to the screen.
    private void createMedList() {
        MedContent.ITEMS.clear();
        Cursor cursor = mMedicationDBAdapter.getAllUserInfo();
        // Reset cursor to start, checking to see if there's data:
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                double dose = cursor.getInt(cursor.getColumnIndex("dose"));
                double freq = cursor.getInt(cursor.getColumnIndex("freq"));

                Log.d("View","Name: "+name);

                MedContent.ITEMS.add(new MedContent.MedItem(name, dose, freq));

            } while(cursor.moveToNext());
        }

        // Close the cursor to avoid a resource leak.
        cursor.close();
    }

    private void makeToast(String msg) {
        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MedView","Broadcast received");
            createMedList();
            displayMedList();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
    }

}
