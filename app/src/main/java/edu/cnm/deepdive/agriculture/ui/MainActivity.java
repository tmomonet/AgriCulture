package edu.cnm.deepdive.agriculture.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import edu.cnm.deepdive.agriculture.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialCardView cardSoilLabs = findViewById(R.id.cardSoilLabs);
        cardSoilLabs.setOnClickListener(v ->
                startActivity(new Intent(this, LabListActivity.class)));
    }
}
