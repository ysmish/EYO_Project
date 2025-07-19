package com.example.eyo.ui.components;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eyo.R;
import com.example.eyo.data.Label;

import java.util.ArrayList;
import java.util.List;

public class LabelSelectionAdapter extends RecyclerView.Adapter<LabelSelectionAdapter.LabelViewHolder> {

    private List<Label> labels = new ArrayList<>();
    private List<String> appliedLabels = new ArrayList<>(); // Labels that are currently applied to the mail
    private OnLabelToggleListener onLabelToggleListener;

    public interface OnLabelToggleListener {
        void onLabelToggled(Label label, boolean isSelected);
    }

    public LabelSelectionAdapter(OnLabelToggleListener listener) {
        this.onLabelToggleListener = listener;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels != null ? labels : new ArrayList<>();
        this.labels.removeIf(label -> label.getId() <= 5);
        notifyDataSetChanged();
    }

    public void setAppliedLabels(List<String> appliedLabels) {
        this.appliedLabels = appliedLabels != null ? appliedLabels : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnLabelToggleListener(OnLabelToggleListener listener) {
        this.onLabelToggleListener = listener;
    }

    @NonNull
    @Override
    public LabelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_label_selection, parent, false);
        return new LabelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LabelViewHolder holder, int position) {
        Label label = labels.get(position);
        holder.bind(label);
    }

    @Override
    public int getItemCount() {
        return labels.size();
    }

    class LabelViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
        private View colorIndicator;
        private TextView labelName;

        public LabelViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.cb_label_selected);
            colorIndicator = itemView.findViewById(R.id.v_label_color);
            labelName = itemView.findViewById(R.id.tv_label_name);

            itemView.setOnClickListener(v -> {
                if (onLabelToggleListener != null) {
                    Label label = labels.get(getAdapterPosition());
                    boolean isSelected = !checkBox.isChecked();
                    checkBox.setChecked(isSelected);
                    onLabelToggleListener.onLabelToggled(label, isSelected);
                }
            });

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (onLabelToggleListener != null) {
                    Label label = labels.get(getAdapterPosition());
                    onLabelToggleListener.onLabelToggled(label, isChecked);
                }
            });
        }

        public void bind(Label label) {
            labelName.setText(label.getName());

            // Set color indicator
            try {
                int color = Color.parseColor(label.getColor());
                colorIndicator.setBackgroundColor(color);
            } catch (IllegalArgumentException e) {
                // Default color if parsing fails
                colorIndicator.setBackgroundColor(Color.parseColor("#4F46E5"));
            }

            // Check if this label is currently applied to the mail
            boolean isApplied = appliedLabels.contains(String.valueOf(label.getId())) || 
                               appliedLabels.contains(label.getName());
            
            // Temporarily remove listener to avoid triggering during binding
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(isApplied);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (onLabelToggleListener != null) {
                    onLabelToggleListener.onLabelToggled(label, isChecked);
                }
            });
        }
    }
}
