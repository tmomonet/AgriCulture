package edu.cnm.deepdive.agriculture.ui.crop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;

import edu.cnm.deepdive.agriculture.R;
import edu.cnm.deepdive.agriculture.model.NmRegion;
import edu.cnm.deepdive.agriculture.model.WaterContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CropFragment extends Fragment {

    private CropViewModel     viewModel;
    private CropResultAdapter adapter;

    private Spinner      regionSpinner;
    private Button       checkBtn;
    private ProgressBar  loadingIndicator;
    private TextView     statusMessage;
    private ChipGroup    badgesRow;
    private Chip         chipFlow;
    private Chip         chipGw;
    private Chip         chipClimate;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_crop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        regionSpinner    = view.findViewById(R.id.region_spinner);
        checkBtn         = view.findViewById(R.id.check_conditions_btn);
        loadingIndicator = view.findViewById(R.id.loading_indicator);
        statusMessage    = view.findViewById(R.id.status_message);
        badgesRow        = view.findViewById(R.id.data_badges_row);
        chipFlow         = view.findViewById(R.id.chip_flow);
        chipGw           = view.findViewById(R.id.chip_gw);
        chipClimate      = view.findViewById(R.id.chip_climate);
        recyclerView     = view.findViewById(R.id.crop_results_list);

        // Populate spinner
        List<String> names = Arrays.stream(NmRegion.values())
                .map(r -> r.displayName)
                .collect(Collectors.toList());
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                names);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regionSpinner.setAdapter(spinnerAdapter);

        // RecyclerView
        adapter = new CropResultAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // ViewModel
        viewModel = new ViewModelProvider(this).get(CropViewModel.class);

        checkBtn.setOnClickListener(v -> {
            NmRegion selected = NmRegion.values()[regionSpinner.getSelectedItemPosition()];
            viewModel.loadRecommendations(selected);
        });

        viewModel.isLoading().observe(getViewLifecycleOwner(), this::applyLoadingState);
        viewModel.getStatusMessage().observe(getViewLifecycleOwner(), msg -> statusMessage.setText(msg));
        viewModel.getResults().observe(getViewLifecycleOwner(), results -> {
            adapter.submitList(results);
            if (results != null && !results.isEmpty()) {
                badgesRow.setVisibility(View.VISIBLE);
                chipFlow.setVisibility(View.VISIBLE);
                chipClimate.setVisibility(View.VISIBLE);
            }
        });
        viewModel.getErrors().observe(getViewLifecycleOwner(), errs -> {
            if (errs != null && !errs.isEmpty()) {
                String msg = String.join("\n", errs);
                Snackbar.make(requireView(), msg, Snackbar.LENGTH_LONG).show();
            }
        });
        viewModel.getFlowSource().observe(getViewLifecycleOwner(), src -> {
            if (src == WaterContext.DataSource.USGS_LIVE) {
                chipFlow.setText("Flow: USGS live");
            } else {
                chipFlow.setText("Flow: offline");
            }
        });
        viewModel.isClimateAvailable().observe(getViewLifecycleOwner(), available -> {
            chipClimate.setText(Boolean.TRUE.equals(available) ? "Climate: NOAA" : "Climate: unavailable");
        });
    }

    private void applyLoadingState(boolean loading) {
        regionSpinner.setEnabled(!loading);
        checkBtn.setEnabled(!loading);
        loadingIndicator.setVisibility(loading ? View.VISIBLE : View.GONE);
        statusMessage.setVisibility(loading ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility((!loading && adapter.getItemCount() > 0) ? View.VISIBLE : View.GONE);
    }
}
