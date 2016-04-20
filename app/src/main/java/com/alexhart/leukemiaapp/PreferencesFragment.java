package com.alexhart.leukemiaapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Size;
import android.widget.Toast;

import com.alexhart.leukemiaapp.UserDatabase.MedicationDBAdapter;
import com.alexhart.leukemiaapp.UserDatabase.WaterDataDBAdapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PreferencesFragment extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFrag()).commit();
    }

    public static class PrefsFrag extends PreferenceFragment implements Preference.OnPreferenceChangeListener{

        private static final String TAG = "PrefFrag";
        private PreferenceScreen mMedClear, mWaterClear;
        private EditTextPreference mMedUpdate, mWaterUpdate;
        private MedicationDBAdapter mMedicationDBAdapter;
        private WaterDataDBAdapter mWaterDataDBAdapter;

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

            return true;
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
