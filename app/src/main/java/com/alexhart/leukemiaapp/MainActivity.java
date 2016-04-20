package com.alexhart.leukemiaapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alexhart.leukemiaapp.UserDatabase.DBAdapter;

import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {

    private Button mMedication, mHydration;
    private DBAdapter mDatabaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHydration = (Button) findViewById(R.id.Hydration);
        mMedication = (Button) findViewById(R.id.Medication);

        mHydration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // When button is clicked it will go to mHydration Activity
//                Intent hydration = new Intent(getBaseContext(), HydrationActivity.class);
//                startActivity(hydration);

                startActivity(new Intent(getApplicationContext(),DBTest.class));
            }
        });

        mMedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //When button is clicked it will go to mMedication Activity
                Intent medication = new Intent(getBaseContext(), MedicationHolder.class);
                startActivity(medication);
            }
        });

        try {
            mDatabaseAdapter = new DBAdapter(this).open();
        }catch (SQLException e){
            Toast.makeText(this, "database not created!", Toast.LENGTH_SHORT).show();
        }
    }
}
