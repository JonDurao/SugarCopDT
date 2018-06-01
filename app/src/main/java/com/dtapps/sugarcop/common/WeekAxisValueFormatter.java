package com.dtapps.sugarcop.common;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.List;

/**
 * Created by jon.durao on 1/9/2018.
 */

public class WeekAxisValueFormatter implements IAxisValueFormatter {

    private List<String> weeks;

    public WeekAxisValueFormatter(List<String> weeks) {
        this.weeks = weeks;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return weeks.get((int) value - 1);
    }
}
