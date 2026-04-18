package edu.cnm.deepdive.agriculture.ui;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import edu.cnm.deepdive.agriculture.R;
import edu.cnm.deepdive.agriculture.model.SoilLab;

public class LabAdapter extends ListAdapter<SoilLab, LabAdapter.LabViewHolder> {

    private final OnLabClickListener listener;

    public LabAdapter(@NonNull OnLabClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public LabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lab_card, parent, false);
        return new LabViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LabViewHolder holder, int position) {
        SoilLab lab = getItem(position);

        holder.tvLabName.setText(lab.getName());
        holder.tvCityState.setText(
                holder.itemView.getContext().getString(R.string.format_city_state,
                        lab.getCity(), lab.getState()));

        holder.chipPap.setVisibility(lab.isPap() ? View.VISIBLE : View.GONE);
        holder.chipNapt.setVisibility(lab.isNapt() ? View.VISIBLE : View.GONE);

        // Both tags visible for all current labs; GONE guards are defensive for future data.
        holder.tvHomeownerTag.setVisibility(View.VISIBLE);
        holder.tvFarmerTag.setVisibility(View.VISIBLE);

        holder.itemView.setOnClickListener(v -> listener.onLabClick(lab));

        holder.btnWebsite.setOnClickListener(v ->
                v.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(lab.getWebsiteUrl()))));
    }

    public interface OnLabClickListener {
        void onLabClick(SoilLab lab);
    }

    static class LabViewHolder extends RecyclerView.ViewHolder {

        final TextView tvLabName;
        final TextView tvCityState;
        final Chip chipPap;
        final Chip chipNapt;
        final TextView tvHomeownerTag;
        final TextView tvFarmerTag;
        final Button btnWebsite;

        LabViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLabName = itemView.findViewById(R.id.tvLabName);
            tvCityState = itemView.findViewById(R.id.tvCityState);
            chipPap = itemView.findViewById(R.id.chipPap);
            chipNapt = itemView.findViewById(R.id.chipNapt);
            tvHomeownerTag = itemView.findViewById(R.id.tvHomeownerTag);
            tvFarmerTag = itemView.findViewById(R.id.tvFarmerTag);
            btnWebsite = itemView.findViewById(R.id.btnWebsite);
        }
    }

    private static final DiffUtil.ItemCallback<SoilLab> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<SoilLab>() {
                @Override
                public boolean areItemsTheSame(@NonNull SoilLab o, @NonNull SoilLab n) {
                    return o.getId() == n.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull SoilLab o, @NonNull SoilLab n) {
                    return o.getId() == n.getId()
                            && o.getName().equals(n.getName())
                            && o.getCity().equals(n.getCity())
                            && o.getState().equals(n.getState());
                }
            };
}
