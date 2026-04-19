package edu.cnm.deepdive.agriculture.ui.water;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import edu.cnm.deepdive.agriculture.R;

public class WaterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Water Quality");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
