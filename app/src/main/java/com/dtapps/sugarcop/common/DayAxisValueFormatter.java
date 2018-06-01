package com.dtapps.sugarcop.common;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.List;

/**
 * Created by jon.durao on 1/9/2018.
 */

public class DayAxisValueFormatter implements IAxisValueFormatter {
    private List<String> dates;

    public DayAxisValueFormatter(List<String> dates) {
        this.dates = dates;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return dates.get((int) value - 1);
    }
}