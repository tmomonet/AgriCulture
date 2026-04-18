package edu.cnm.deepdive.agriculture.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import edu.cnm.deepdive.agriculture.R;
import edu.cnm.deepdive.agriculture.data.LabRepository;
import edu.cnm.deepdive.agriculture.model.SoilLab;

public class LabDetailActivity extends AppCompatActivity {

    public static final String EXTRA_LAB_ID = "edu.cnm.deepdive.agriculture.LAB_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_detail);

        int labId = getIntent().getIntExtra(EXTRA_LAB_ID, -1);
        SoilLab lab = LabRepository.getById(labId);
        if (lab == null) {
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(lab.getName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextView tvDetailAddress = findViewById(R.id.tvDetailAddress);
        TextView tvDetailPhone = findViewById(R.id.tvDetailPhone);
        TextView tvDetailAccred = findViewById(R.id.tvDetailAccred);
        TextView tvHomeownerTests = findViewById(R.id.tvHomeownerTests);
        TextView tvFarmerTests = findViewById(R.id.tvFarmerTests);
        Button btnVisitWebsite = findViewById(R.id.btnVisitWebsite);

        tvDetailAddress.setText(lab.getFormattedAddress());
        tvDetailAddress.setOnClickListener(v -> openMaps(lab.getFormattedAddress()));

        tvDetailPhone.setText(lab.getPhone());
        tvDetailPhone.setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + lab.getPhone()))));

        tvDetailAccred.setText(getString(R.string.label_accreditation, lab.getAccreditation()));

        tvHomeownerTests.setText(lab.getHomeownerTests());

        if (lab.isFarmerSameAsHomeowner()) {
            tvFarmerTests.setText(R.string.farmer_tests_same_as_homeowner);
            tvFarmerTests.setAlpha(0.6f);
        } else {
            tvFarmerTests.setText(lab.getFarmerTests());
            tvFarmerTests.setAlpha(1.0f);
        }

        btnVisitWebsite.setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(lab.getWebsiteUrl()))));
    }

    private void openMaps(String address) {
        Uri geoUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://maps.google.com/?q=" + Uri.encode(address))));
        }
    }
}
