package edu.cnm.deepdive.agriculture.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import edu.cnm.deepdive.agriculture.R;
import edu.cnm.deepdive.agriculture.ui.crop.CropActivity;
import edu.cnm.deepdive.agriculture.ui.water.WaterActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialCardView cardSoilLabs = findViewById(R.id.cardSoilLabs);
        cardSoilLabs.setOnClickListener(v ->
                startActivity(new Intent(this, LabListActivity.class)));

        MaterialCardView cardWater = findViewById(R.id.cardWater);
        cardWater.setOnClickListener(v ->
                startActivity(new Intent(this, WaterActivity.class)));

        MaterialCardView cardCrops = findViewById(R.id.cardCrops);
        cardCrops.setOnClickListener(v ->
                startActivity(new Intent(this, CropActivity.class)));

        MaterialCardView cardRanchFinder = findViewById(R.id.cardRanchFinder);
        cardRanchFinder.setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://replit.com/@tmhollins/Ranch-Finder"))));
    }
}
