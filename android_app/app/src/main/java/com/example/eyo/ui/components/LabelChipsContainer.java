package com.example.eyo.ui.components;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.eyo.R;
import com.example.eyo.data.Label;

import java.util.ArrayList;
import java.util.List;

public class LabelChipsContainer extends ViewGroup {

    private List<Label> availableLabels = new ArrayList<>();
    private List<String> appliedLabelIds = new ArrayList<>();
    private OnChipClickListener onChipClickListener;

    public interface OnChipClickListener {
        void onChipClicked(Label label);
    }

    public LabelChipsContainer(Context context) {
        super(context);
    }

    public LabelChipsContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LabelChipsContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAvailableLabels(List<Label> labels) {
        this.availableLabels = labels != null ? labels : new ArrayList<>();
        refreshChips();
    }

    public void setAppliedLabels(List<String> appliedLabels) {
        this.appliedLabelIds = appliedLabels != null ? appliedLabels : new ArrayList<>();
        refreshChips();
    }

    public void setOnChipClickListener(OnChipClickListener listener) {
        this.onChipClickListener = listener;
    }

    private void refreshChips() {
        removeAllViews();
        
        if (availableLabels.isEmpty() || appliedLabelIds.isEmpty()) {
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());
        
        for (Label label : availableLabels) {
            String labelId = String.valueOf(label.getId());
            String labelName = label.getName();
            
            // Check if this label is applied to the mail
            if (appliedLabelIds.contains(labelId) || appliedLabelIds.contains(labelName)) {
                View chipView = inflater.inflate(R.layout.chip_label, this, false);
                
                View colorIndicator = chipView.findViewById(R.id.v_label_color);
                TextView labelNameText = chipView.findViewById(R.id.tv_label_name);
                
                // Set label name
                labelNameText.setText(label.getName());
                
                // Set label color
                try {
                    int color = Color.parseColor(label.getColor());
                    colorIndicator.setBackgroundColor(color);
                } catch (IllegalArgumentException | NullPointerException e) {
                    // Use default color if parsing fails
                    colorIndicator.setBackgroundColor(Color.parseColor("#4F46E5"));
                }
                
                // Set click listener if provided
                if (onChipClickListener != null) {
                    chipView.setOnClickListener(v -> onChipClickListener.onChipClicked(label));
                    chipView.setClickable(true);
                    chipView.setFocusable(true);
                }
                
                addView(chipView);
            }
        }
    }

    /**
     * Convenience method to show chips for applied labels without needing full Label objects
     */
    public void showSimpleChips(List<String> labelNames) {
        removeAllViews();
        
        if (labelNames == null || labelNames.isEmpty()) {
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());
        
        for (String labelName : labelNames) {
            // Skip numeric system labels
            if (labelName.matches("\\d+")) {
                continue;
            }
            
            View chipView = inflater.inflate(R.layout.chip_label, this, false);
            
            View colorIndicator = chipView.findViewById(R.id.v_label_color);
            TextView labelNameText = chipView.findViewById(R.id.tv_label_name);
            
            // Set label name
            labelNameText.setText(labelName);
            
            // Use default color for simple chips
            colorIndicator.setBackgroundColor(Color.parseColor("#4F46E5"));
            
            addView(chipView);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = 0;
        int lineWidth = 0;
        int lineHeight = 0;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                
                if (lineWidth + childWidth > width) {
                    // Move to next line
                    height += lineHeight;
                    lineWidth = childWidth;
                    lineHeight = childHeight;
                } else {
                    // Add to current line
                    lineWidth += childWidth;
                    lineHeight = Math.max(lineHeight, childHeight);
                }
            }
        }
        height += lineHeight;
        
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int currentX = 0;
        int currentY = 0;
        int lineHeight = 0;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                
                if (currentX + childWidth > width) {
                    // Move to next line
                    currentX = 0;
                    currentY += lineHeight;
                    lineHeight = 0;
                }
                
                child.layout(currentX, currentY, currentX + childWidth, currentY + childHeight);
                currentX += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }
        }
    }
}
