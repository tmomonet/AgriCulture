package edu.cnm.deepdive.agriculture.ui.water;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// TODO: implement water module in branch 001-water-tab / merge into this branch when ready
public class WaterFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        TextView placeholder = new TextView(requireContext());
        placeholder.setText("Water Quality — coming soon.");
        placeholder.setPadding(48, 48, 48, 48);
        return placeholder;
    }
}
