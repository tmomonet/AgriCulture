package edu.cnm.deepdive.agriculture.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.cnm.deepdive.agriculture.R;
import edu.cnm.deepdive.agriculture.data.LabRepository;
import edu.cnm.deepdive.agriculture.model.SoilLab;

public class LabListActivity extends AppCompatActivity implements LabAdapter.OnLabClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        LabAdapter adapter = new LabAdapter(this);
        RecyclerView recyclerViewLabs = findViewById(R.id.recyclerViewLabs);
        recyclerViewLabs.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewLabs.setAdapter(adapter);
        adapter.submitList(LabRepository.getAll());
    }

    @Override
    public void onLabClick(SoilLab lab) {
        Intent intent = new Intent(this, LabDetailActivity.class);
        intent.putExtra(LabDetailActivity.EXTRA_LAB_ID, lab.getId());
        startActivity(intent);
    }
}
