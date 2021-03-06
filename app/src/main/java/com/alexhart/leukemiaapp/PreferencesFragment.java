package com.alexhart.leukemiaapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alexhart.leukemiaapp.UserDatabase.MedicationDBAdapter;
import com.alexhart.leukemiaapp.UserDatabase.WaterDataDBAdapter;

import java.sql.SQLException;

public class PreferencesFragment extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFrag()).commit();


    }

    public void onMedUpdateName(View view) {

//        Toast.makeText(getApplicationContext(), "Clicked!", Toast.LENGTH_SHORT).show();

    }


    public static class PrefsFrag extends PreferenceFragment implements Preference.OnPreferenceChangeListener,
            View.OnClickListener
    {

        private static final String TAG = "PrefFrag";
        private PreferenceScreen mMedClear, mWaterClear;
        private EditTextPreference mMedUpdate, mWaterUpdate;
        private MedicationDBAdapter mMedicationDBAdapter;
        private WaterDataDBAdapter mWaterDataDBAdapter;
        private String[] mMedUpdateStrings = {MedicationDBAdapter.KEY_NAME,
                MedicationDBAdapter.KEY_DOSE, MedicationDBAdapter.KEY_FREQUENCY};
        private String[] mWaterUpdateStrings = {WaterDataDBAdapter.KEY_DATE, WaterDataDBAdapter.KEY_IN,
                WaterDataDBAdapter.KEY_OUT};

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_frag);
            openDB();
            initUI();
        }

        private void initUI() {
            mMedClear = (PreferenceScreen)findPreference(getString(R.string.pref_key_med_delete));
            mWaterClear = (PreferenceScreen)findPreference(getString(R.string.pref_key_water_delete));
            mMedUpdate = (EditTextPreference)findPreference(getString(R.string.pref_key_med_update));
            mWaterUpdate = (EditTextPreference)findPreference(getString(R.string.pref_key_water_update));

            mMedClear.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    onDeleteUserData("medication");
                    return true;
                }
            });
            mWaterClear.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    onDeleteUserData("water");
                    return true;
                }
            });

            mWaterUpdate.setOnPreferenceChangeListener(this);
            mMedUpdate.setOnPreferenceChangeListener(this);
        }


        public void onDeleteUserData(String type) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle("Delete all collected " + type + " data?");

            if (type.equals("medication")) {
                alert.setMessage("This includes: name,dose,frequency");
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mMedicationDBAdapter.deleteAll();
                        Toast.makeText(getActivity(), "Data Cleared", Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                alert.setMessage("This includes: date,in,out,difference");
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mWaterDataDBAdapter.deleteAll();
                        Toast.makeText(getActivity(), "Data Cleared", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //Handle click of the 'no' button
                }
            });
            alert.show();
        }

        private void openDB() {
            try {
                mMedicationDBAdapter = new MedicationDBAdapter(getActivity()).open();
                mWaterDataDBAdapter = new WaterDataDBAdapter(getActivity()).open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void closeDB() {
            mMedicationDBAdapter.close();
            mWaterDataDBAdapter.close();
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object val) {

            //can't do switch
            if (preference.getKey().equals(getString(R.string.pref_key_med_update))) {
                final String name = val.toString();
                if (mMedicationDBAdapter.contains(val.toString(),MedicationDBAdapter.KEY_NAME)){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle("Edit which value?");
                    alertDialog.setItems(mMedUpdateStrings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //0 - name
                            //1 - dose
                            //2 - frequency
                            createMedUpdateDialog(mMedUpdateStrings[i], name);
                        }
                    });
                    alertDialog.show();
                }else {
                    makeToast("Not found");
                }
            }else if (preference.getKey().equals(getString(R.string.pref_key_water_update))) {
                final String date = val.toString();
                final Long id = mWaterDataDBAdapter.contains(date,WaterDataDBAdapter.KEY_DATE);
                if (id != null) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle("Edit which value?");
                    alertDialog.setItems(mWaterUpdateStrings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //0 - Date
                            //1 - Intake
                            //2 - Excrete
                            //3 - Difference
                            createWaterUpdateDialog(mWaterUpdateStrings[i], date,id);
                        }
                    });
                    alertDialog.show();
                }else {
                    makeToast("Not found");
                }

            }
            return true;
        }

        private void createMedUpdateDialog(final String type, final String name) {
            final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
            final LayoutInflater inflater = getActivity().getLayoutInflater();
            final View v = inflater.inflate(R.layout.med_alert_dialog_text,null);

            final EditText updateText = (EditText)v.findViewById(R.id.pref_med_update_edit);
            alertBuilder.setView(v)
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            String update = updateText.getText().toString();
                            if (!update.equals("")) {
                                String sql = "UPDATE " + MedicationDBAdapter.DATABASE_TABLE +
                                        " SET " + type + " = " + update +
                                        " WHERE " + MedicationDBAdapter.KEY_NAME +
                                        " = " + "'" + name + "'";
                                mMedicationDBAdapter.execSQL(sql);
                                makeToast("Updated");
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
//                            long l = mMedicationDBAdapter.getRowID(name);
//                            makeToast("Long: " + l);
//                            dialog.cancel();
                        }
                    });
            alertBuilder.setTitle(type);
            alertBuilder.show();
        }

        private void createWaterUpdateDialog(final String type, final String date, final long idd) {
            final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
            final LayoutInflater inflater = getActivity().getLayoutInflater();
            final View v = inflater.inflate(R.layout.med_alert_dialog_text,null);

            final EditText updateText = (EditText)v.findViewById(R.id.pref_med_update_edit);
            alertBuilder.setView(v)
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            String update = updateText.getText().toString();
                            if (!update.equals("")) {
                                double intake = 0, excrete = 0;
                                try {
                                    Cursor c = mWaterDataDBAdapter.getWaterDataRow(idd);
                                    if (c.moveToFirst()) {
                                        do {
                                            intake = c.getDouble(c.getColumnIndex(WaterDataDBAdapter.KEY_IN));
                                            excrete = c.getDouble(c.getColumnIndex(WaterDataDBAdapter.KEY_OUT));
                                        }
                                        while (c.moveToNext());
                                    }
                                    c.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                    return;
                                }

                                switch (type) {
                                    case WaterDataDBAdapter.KEY_IN:
                                        mWaterDataDBAdapter.updateWaterData(idd, date, Double.parseDouble(update), excrete);
                                        break;
                                    case WaterDataDBAdapter.KEY_OUT:
                                        mWaterDataDBAdapter.updateWaterData(idd, date, intake, Double.parseDouble(update));
                                        break;
                                    default:
                                        //date
                                        mWaterDataDBAdapter.updateWaterData(idd, update, intake, excrete);
                                        break;
                                }
                                makeToast("Updated");
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //cancel
                        }
                    });
            alertBuilder.setTitle(type);
            alertBuilder.show();
        }

        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.updateName_button:
                    makeToast("Name!");
                    break;
                case R.id.updateDose_button:
                    makeToast("Dose!");
                    break;
                case R.id.updateFreq_button:
                    makeToast("Freq!");
                    break;
            }
        }

        private void makeToast(String msg) {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStop() {
            super.onStop();
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            closeDB();
        }


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }
}
