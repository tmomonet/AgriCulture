package edu.cnm.deepdive.agriculture.ui.crop;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import edu.cnm.deepdive.agriculture.R;
import edu.cnm.deepdive.agriculture.model.CropResult;

public class CropResultAdapter extends ListAdapter<CropResult, CropResultAdapter.ViewHolder> {

    private static final String COLOR_SUITABLE         = "#2E7D32";
    private static final String COLOR_CAUTION          = "#F57F17";
    private static final String COLOR_NOT_RECOMMENDED  = "#C62828";

    public CropResultAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crop_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final View     statusStrip;
        private final TextView cropName;
        private final TextView statusLabel;
        private final TextView headlineText;
        private final TextView detailText;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            statusStrip  = itemView.findViewById(R.id.status_strip);
            cropName     = itemView.findViewById(R.id.crop_name);
            statusLabel  = itemView.findViewById(R.id.status_label);
            headlineText = itemView.findViewById(R.id.headline_text);
            detailText   = itemView.findViewById(R.id.detail_text);
        }

        void bind(CropResult result) {
            String color = colorFor(result.getStatus());
            statusStrip.setBackgroundColor(Color.parseColor(color));
            statusLabel.setTextColor(Color.parseColor(color));

            cropName.setText(result.getCropType().displayName);
            statusLabel.setText(labelFor(result.getStatus()));
            headlineText.setText(result.getHeadline());

            String bullets = "\u2022 " + String.join("\n\u2022 ", result.getDetails());
            detailText.setText(bullets);
        }

        private String colorFor(CropResult.Status status) {
            switch (status) {
                case SUITABLE:        return COLOR_SUITABLE;
                case CAUTION:         return COLOR_CAUTION;
                case NOT_RECOMMENDED: return COLOR_NOT_RECOMMENDED;
                default:              return COLOR_SUITABLE;
            }
        }

        private String labelFor(CropResult.Status status) {
            switch (status) {
                case SUITABLE:        return "SUITABLE";
                case CAUTION:         return "CAUTION";
                case NOT_RECOMMENDED: return "NOT RECOMMENDED";
                default:              return "";
            }
        }
    }

    private static final DiffUtil.ItemCallback<CropResult> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<CropResult>() {
                @Override
                public boolean areItemsTheSame(@NonNull CropResult a, @NonNull CropResult b) {
                    return a.getCropType() == b.getCropType();
                }

                @Override
                public boolean areContentsTheSame(@NonNull CropResult a, @NonNull CropResult b) {
                    return a.getStatus() == b.getStatus()
                            && a.getHeadline().equals(b.getHeadline());
                }
            };
}
