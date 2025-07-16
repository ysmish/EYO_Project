package com.example.eyo.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import java.util.Calendar;

public class DatePickerHelper {
    
    public interface DatePickerCallback {
        void onDateSelected(String formattedDate);
    }
    
    private Context context;
    private DatePickerCallback callback;
    private long maxDate;
    private long minDate;
    private boolean hasMaxDate = false;
    private boolean hasMinDate = false;
    
    public DatePickerHelper(Context context, DatePickerCallback callback) {
        this.context = context;
        this.callback = callback;
    }
    
    public DatePickerHelper setMaxDate(long maxDate) {
        this.maxDate = maxDate;
        this.hasMaxDate = true;
        return this;
    }
    
    public DatePickerHelper setMinDate(long minDate) {
        this.minDate = minDate;
        this.hasMinDate = true;
        return this;
    }
    
    public DatePickerHelper setMaxDateToToday() {
        this.maxDate = System.currentTimeMillis();
        this.hasMaxDate = true;
        return this;
    }
    
    public void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            context,
            (view, selectedYear, selectedMonth, selectedDay) -> {
                // Format the date as YYYY-MM-DD
                String formattedDate = String.format("%04d-%02d-%02d", 
                    selectedYear, selectedMonth + 1, selectedDay);
                if (callback != null) {
                    callback.onDateSelected(formattedDate);
                }
            },
            year, month, day
        );
        
        // Set constraints if specified
        if (hasMaxDate) {
            datePickerDialog.getDatePicker().setMaxDate(maxDate);
        }
        
        if (hasMinDate) {
            datePickerDialog.getDatePicker().setMinDate(minDate);
        }
        
        datePickerDialog.show();
    }
} 