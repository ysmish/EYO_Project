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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LabelChipsContainer extends ViewGroup {

    private List<Label> labels = new ArrayList<>();
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

    public void setLabels(List<Label> labels) {
        this.labels = labels != null ? labels : new ArrayList<>();
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
        
        if (appliedLabelIds.isEmpty()) {
            return;
        }

        android.util.Log.d("LabelChipsContainer", "refreshChips - applied labels: " + appliedLabelIds);
        android.util.Log.d("LabelChipsContainer", "refreshChips - available labels count: " + labels.size());

        LayoutInflater inflater = LayoutInflater.from(getContext());
        Set<String> addedLabels = new HashSet<>(); // Track added labels to avoid duplicates
        
        // If we have available labels with colors, use proper chip display
        if (!labels.isEmpty()) {
            android.util.Log.d("LabelChipsContainer", "Using proper chip display with colors");
            
            // Debug: Log all available labels
            for (Label label : labels) {
                android.util.Log.d("LabelChipsContainer", "Available label - ID: " + label.getId() + ", Name: " + label.getName() + ", Color: " + label.getColor());
            }
            
            // First, handle system labels (1-5) to ensure they take priority
            for (Label label : labels) {
                String labelId = String.valueOf(label.getId());
                String labelName = label.getName();
                
                // Only process system labels (1-5) first
                if (labelId.equals("1") || labelId.equals("2") || labelId.equals("3") || 
                    labelId.equals("4") || labelId.equals("5")) {
                    
                    // Check if this system label is applied (by ID or by name, case-insensitive)
                    boolean isApplied = appliedLabelIds.contains(labelId) || 
                                     appliedLabelIds.contains(labelName) ||
                                     appliedLabelIds.contains(labelName.toLowerCase()) ||
                                     appliedLabelIds.contains(labelName.toUpperCase());
                    
                    android.util.Log.d("LabelChipsContainer", "System label check - ID: " + labelId + ", Name: " + labelName + ", isApplied: " + isApplied);
                    
                    if (isApplied) {
                        String displayName = getDisplayNameForLabel(labelId);
                        if (addedLabels.contains(displayName)) {
                            continue;
                        }
                        
                        android.util.Log.d("LabelChipsContainer", "Adding system label chip: " + label.getName());
                        
                        View chipView = inflater.inflate(R.layout.chip_label, this, false);
                        TextView labelNameText = chipView.findViewById(R.id.tv_label_name);
                        
                        // Set label name
                        labelNameText.setText(label.getName());
                        
                        // Set label color as background
                        try {
                            int color = Color.parseColor(label.getColor());
                            chipView.setBackground(createColoredBackground(color));
                        } catch (IllegalArgumentException | NullPointerException e) {
                            // Use default color if parsing fails
                            chipView.setBackground(createColoredBackground(Color.parseColor("#4F46E5")));
                        }
                        
                        // Set click listener if provided
                        if (onChipClickListener != null) {
                            chipView.setOnClickListener(v -> onChipClickListener.onChipClicked(label));
                            chipView.setClickable(true);
                            chipView.setFocusable(true);
                        }
                        
                        addView(chipView);
                        addedLabels.add(displayName);
                    }
                }
            }
            
            // Then handle custom labels (non-system labels)
            for (Label label : labels) {
                String labelId = String.valueOf(label.getId());
                String labelName = label.getName();
                
                // Skip system labels as they were already handled
                if (labelId.equals("1") || labelId.equals("2") || labelId.equals("3") || 
                    labelId.equals("4") || labelId.equals("5")) {
                    continue;
                }
                
                // Check if this custom label is applied to the mail (by ID or by name, case-insensitive)
                boolean isApplied = appliedLabelIds.contains(labelId) || 
                                 appliedLabelIds.contains(labelName) ||
                                 appliedLabelIds.contains(labelName.toLowerCase()) ||
                                 appliedLabelIds.contains(labelName.toUpperCase());
                
                android.util.Log.d("LabelChipsContainer", "Custom label check - ID: " + labelId + ", Name: " + labelName + ", isApplied: " + isApplied);
                
                if (isApplied) {
                    // Skip if we already added a label with the same display name
                    if (addedLabels.contains(labelName)) {
                        continue;
                    }
                    
                    android.util.Log.d("LabelChipsContainer", "Adding custom label chip: " + label.getName());
                    
                    View chipView = inflater.inflate(R.layout.chip_label, this, false);
                    TextView labelNameText = chipView.findViewById(R.id.tv_label_name);
                    
                    // Set label name
                    labelNameText.setText(label.getName());
                    
                    // Set label color as background
                    try {
                        int color = Color.parseColor(label.getColor());
                        chipView.setBackground(createColoredBackground(color));
                    } catch (IllegalArgumentException | NullPointerException e) {
                        // Use default color if parsing fails
                        chipView.setBackground(createColoredBackground(Color.parseColor("#4F46E5")));
                    }
                    
                    // Set click listener if provided
                    if (onChipClickListener != null) {
                        chipView.setOnClickListener(v -> onChipClickListener.onChipClicked(label));
                        chipView.setClickable(true);
                        chipView.setFocusable(true);
                    }
                    
                    addView(chipView);
                    addedLabels.add(labelName);
                }
            }
        }
        
        // If no chips were added but we have applied labels, show simple chips
        if (getChildCount() == 0 && !appliedLabelIds.isEmpty()) {
            android.util.Log.d("LabelChipsContainer", "No chips added, falling back to simple chips");
            showSimpleChips(appliedLabelIds);
        } else if (getChildCount() > 0) {
            android.util.Log.d("LabelChipsContainer", "Chips were added successfully: " + getChildCount() + " chips");
        }
        
        android.util.Log.d("LabelChipsContainer", "refreshChips completed - total chips: " + getChildCount());
    }

    /**
     * Convenience method to show chips for applied labels without needing full Label objects
     * Shows both system labels and custom labels
     */
    public void showSimpleChips(List<String> labelNames) {
        removeAllViews();
        
        if (labelNames == null || labelNames.isEmpty()) {
            return;
        }

        android.util.Log.d("LabelChipsContainer", "showSimpleChips - processing labels: " + labelNames);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        Set<String> addedLabels = new HashSet<>(); // Track added labels to avoid duplicates
        
        for (String labelName : labelNames) {
            // Handle system labels by ID (1-5)
            if (labelName.equals("1") || labelName.equals("2") || labelName.equals("3") || 
                labelName.equals("4") || labelName.equals("5")) {
                
                String displayName = getDisplayNameForLabel(labelName);
                
                // Skip if we already added a label with the same display name
                if (addedLabels.contains(displayName)) {
                    continue;
                }
                
                android.util.Log.d("LabelChipsContainer", "Adding simple system label chip by ID: " + displayName);
                
                View chipView = inflater.inflate(R.layout.chip_label, this, false);
                TextView labelNameText = chipView.findViewById(R.id.tv_label_name);
                
                // Set display name for system labels
                labelNameText.setText(displayName);
                
                // Use system label color from server if available, otherwise fallback
                int color = getSystemLabelColor(labelName);
                chipView.setBackground(createColoredBackground(color));
                
                addView(chipView);
                addedLabels.add(displayName);
                continue;
            }
            
            // Handle system labels by name (Inbox, Sent, Starred, Drafts, Spam)
            if (labelName.equalsIgnoreCase("Inbox") || labelName.equalsIgnoreCase("Sent") || 
                labelName.equalsIgnoreCase("Starred") || labelName.equalsIgnoreCase("Drafts") || 
                labelName.equalsIgnoreCase("Spam")) {
                
                // Get proper display name (with correct case)
                String displayName = getSystemLabelDisplayName(labelName);
                
                // Skip if we already added a label with the same display name
                if (addedLabels.contains(displayName)) {
                    continue;
                }
                
                android.util.Log.d("LabelChipsContainer", "Adding simple system label chip by name: " + displayName);
                
                View chipView = inflater.inflate(R.layout.chip_label, this, false);
                TextView labelNameText = chipView.findViewById(R.id.tv_label_name);
                
                // Set display name for system labels
                labelNameText.setText(displayName);
                
                // Use system label color based on name
                int color = getSystemLabelColorByName(displayName);
                chipView.setBackground(createColoredBackground(color));
                
                addView(chipView);
                addedLabels.add(displayName);
                continue;
            }
            
            // Handle custom labels (numeric IDs or custom names)
            // Skip if we already added a label with the same display name
            if (addedLabels.contains(labelName)) {
                continue;
            }
            
            android.util.Log.d("LabelChipsContainer", "Adding simple custom label chip: " + labelName);
            
            View chipView = inflater.inflate(R.layout.chip_label, this, false);
            TextView labelNameText = chipView.findViewById(R.id.tv_label_name);
            
            // Set label name (for custom labels, use the name as-is)
            labelNameText.setText(labelName);
            
            // Use theme-aware color for custom labels
            Context context = getContext();
            int color;
            if (context != null) {
                color = context.getResources().getColor(R.color.chip_background_color, context.getTheme());
            } else {
                color = Color.parseColor("#4F46E5"); // Default purple fallback
            }
            chipView.setBackground(createColoredBackground(color));
            
            addView(chipView);
            addedLabels.add(labelName);
        }
        
        android.util.Log.d("LabelChipsContainer", "showSimpleChips completed - total chips: " + getChildCount());
    }
    
    /**
     * Convert system label IDs to readable display names
     * This is now only used for fallback when labels are not loaded
     */
    private String getDisplayNameForLabel(String labelId) {
        switch (labelId) {
            case "1":
                return "Inbox";
            case "2":
                return "Sent";
            case "3":
                return "Starred";
            case "4":
                return "Drafts";
            case "5":
                return "Spam";
            default:
                return labelId; // Return as-is for custom labels
        }
    }

    /**
     * Get system label color based on label ID
     * Uses theme-aware colors that work in both light and dark modes
     */
    private int getSystemLabelColor(String labelId) {
        Context context = getContext();
        if (context == null) {
            return Color.parseColor("#4F46E5"); // Default purple fallback
        }
        
        // Use theme-aware chip colors similar to search wrapper
        return context.getResources().getColor(R.color.chip_background_color, context.getTheme());
    }

    /**
     * Get system label color based on label name
     * Uses theme-aware colors that work in both light and dark modes
     */
    private int getSystemLabelColorByName(String labelName) {
        Context context = getContext();
        if (context == null) {
            return Color.parseColor("#4F46E5"); // Default purple fallback
        }
        
        // Use theme-aware chip colors similar to search wrapper
        return context.getResources().getColor(R.color.chip_background_color, context.getTheme());
    }

    /**
     * Converts a system label name (e.g., "Inbox", "Sent") to its display name (e.g., "Inbox", "Sent")
     * This is useful for simple chips where the name might be in a different case.
     */
    private String getSystemLabelDisplayName(String labelName) {
        if (labelName.equalsIgnoreCase("Inbox")) {
            return "Inbox";
        } else if (labelName.equalsIgnoreCase("Sent")) {
            return "Sent";
        } else if (labelName.equalsIgnoreCase("Starred")) {
            return "Starred";
        } else if (labelName.equalsIgnoreCase("Drafts")) {
            return "Drafts";
        } else if (labelName.equalsIgnoreCase("Spam")) {
            return "Spam";
        } else {
            return labelName; // Return as-is for custom labels
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = 0;
        int lineWidth = 0;
        int lineHeight = 0;
        int spacing = 16; // Fixed spacing between chips

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                
                if (lineWidth + childWidth + spacing > width) {
                    // Move to next line
                    height += lineHeight;
                    lineWidth = childWidth;
                    lineHeight = childHeight;
                } else {
                    // Add to current line
                    lineWidth += childWidth + spacing;
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
        int spacing = 16; // Fixed spacing between chips

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                
                // Check if this child would exceed the width
                if (currentX + childWidth > width) {
                    // Move to next line
                    currentX = 0;
                    currentY += lineHeight;
                    lineHeight = 0;
                }
                
                child.layout(currentX, currentY, currentX + childWidth, currentY + childHeight);
                
                // Add the child width plus spacing to currentX
                currentX += childWidth + spacing;
                
                lineHeight = Math.max(lineHeight, childHeight);
            }
        }
    }
    
    /**
     * Creates a colored background drawable with rounded corners
     */
    private android.graphics.drawable.GradientDrawable createColoredBackground(int color) {
        android.graphics.drawable.GradientDrawable drawable = new android.graphics.drawable.GradientDrawable();
        drawable.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        drawable.setColor(color);
        drawable.setCornerRadius(20 * getResources().getDisplayMetrics().density); // 20dp in pixels
        return drawable;
    }
}
