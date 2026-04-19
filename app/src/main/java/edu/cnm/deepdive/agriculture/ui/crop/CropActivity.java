package edu.cnm.deepdive.agriculture.ui.crop;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import edu.cnm.deepdive.agriculture.R;

public class CropActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Crop Recommendations");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
