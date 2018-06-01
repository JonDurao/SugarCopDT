package com.dtapps.sugarcop;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.dtapps.sugarcop.common.CommonData;
import com.dtapps.sugarcop.common.DayAxisValueFormatter;
import com.dtapps.sugarcop.common.MyAxisValueFormatter;
import com.dtapps.sugarcop.common.WeekAxisValueFormatter;
import com.dtapps.sugarcop.common.XYMarkerView;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.dtapps.sugarcop.SugarHistoryContract.SugarHistoryEntry.Columns.*;
import static com.dtapps.sugarcop.common.Common.*;

public class HistoryActivity extends BaseActivity {
    private Spinner spinner;
    private boolean firstTime = true;
    private Calendar mCalendar = Calendar.getInstance();
    private ContentResolver mResolver;
    private Float mMaxSugar = 0.0f, mSugarPerDay = 0.0f, mSugarAcum = 0.0f;
    private List<String> dates = new ArrayList<>();
    private SharedPreferences mSharedPreferences;
    private DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    private Activity mActivityHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivityHelper = this;

        commonInitActivity(this, R.id.adView_history, CommonData.getHistoryColor());

        mSharedPreferences = getSharedPreferences(getResources().getString(R.string.string_shared_preferences_name), 0);

        try {
            mSugarPerDay = Float.valueOf(mSharedPreferences.getString(getString(R.string.string_shared_preferences_user_sugar_per_day),
                    null));
        } catch (Exception e){
            Toast.makeText(this, "¡Error History!", Toast.LENGTH_LONG).show();
        }
        mMaxSugar = mSugarPerDay;
        mResolver = getContentResolver();

        spinner = findViewById(R.id.history_spinner);
        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        run7DaysChart(getString(R.string.string_period_7_days));
    }

    private void runYearlyChart(String type) {
        Cursor cursor = mResolver.query(SugarHistoryContract.CONTENT_URI, null, null,
                null, SugarHistoryContract.SugarHistoryEntry.Columns.SUGAR_ID + " ASC LIMIT 2555");

        historyChartConfig(historyData(cursor, type), type);
    }

    private void runMonthlyChart(String type) {
        Cursor cursor = mResolver.query(SugarHistoryContract.CONTENT_URI, null, null,
                null, SugarHistoryContract.SugarHistoryEntry.Columns.SUGAR_ID + " ASC LIMIT 360");

        historyChartConfig(historyData(cursor, type), type);
    }

    private void runWeeklyChart(String type) {
        Cursor cursor = mResolver.query(SugarHistoryContract.CONTENT_URI, null, null,
                null, SugarHistoryContract.SugarHistoryEntry.Columns.SUGAR_ID + " ASC LIMIT 50");

        historyChartConfig(historyData(cursor, type), type);
    }

    private void run7DaysChart(String type) {
        Cursor cursor = mResolver.query(SugarHistoryContract.CONTENT_URI, null, null,
                null, SugarHistoryContract.SugarHistoryEntry.Columns.SUGAR_ID + " DESC LIMIT 6");

        historyChartConfig(historyData(cursor, type), type);
    }

    private List<SugarHistory> historyData(Cursor cursor, String type) {
        dates = new ArrayList<>();
        List<SugarHistory> lista = new ArrayList<>();

        if (type.equalsIgnoreCase(getString(R.string.string_period_7_days))) {
            try{
                mMaxSugar = Float.valueOf(mSharedPreferences.getString(this.getString(R.string.string_shared_preferences_user_sugar), null));

                if (cursor.moveToLast()) {
                    do {
                        dates.add(cursor.getString(cursor.getColumnIndex(SUGAR_DATE)));

                        lista.add(new SugarHistory(
                                cursor.getString(cursor.getColumnIndex(SUGAR_SUGAR)),
                                cursor.getString(cursor.getColumnIndex(SUGAR_DATE))));

                        if (Float.valueOf(cursor.getString(cursor.getColumnIndex(SUGAR_SUGAR))) > mMaxSugar) {
                            mMaxSugar = Float.valueOf(cursor.getString(cursor.getColumnIndex(SUGAR_SUGAR)));
                        }
                    } while (cursor.moveToPrevious());
                }

                dates.add(mSharedPreferences.getString(this.getString(R.string.string_shared_preferences_user_date), null));
                lista.add(new SugarHistory(
                        mSharedPreferences.getString(this.getString(R.string.string_shared_preferences_user_sugar), null),
                        mSharedPreferences.getString(this.getString(R.string.string_shared_preferences_user_date), null)
                ));
            } catch (Exception e){
                Toast.makeText(mActivityHelper, "¡Error historyData!", Toast.LENGTH_LONG).show();
            }
        } else if (type.equalsIgnoreCase(getString(R.string.string_period_week))) {
            lista = historyCommonAdding(cursor, type);
        } else if (type.equalsIgnoreCase(getString(R.string.string_period_month))) {
            lista = historyCommonAdding(cursor, type);
        } else if (type.equalsIgnoreCase(getString(R.string.string_period_year))) {
            lista = historyCommonAdding(cursor, type);
        }

        try {
            mCalendar.setTime(df.parse(mSharedPreferences.getString(this.getString(R.string.string_shared_preferences_user_date), null)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (type.equalsIgnoreCase(getString(R.string.string_period_week))) {
            lista.addAll(historyCommonAddFinal(type));
        } else if (type.equalsIgnoreCase(getString(R.string.string_period_month))) {
            lista.addAll(historyCommonAddFinal(type));
        } else if (type.equalsIgnoreCase(getString(R.string.string_period_year))) {
            lista.addAll(historyCommonAddFinal(type));
        }

        cursor.close();

        return lista;
    }

    protected void historyChartConfig(List<SugarHistory> list, String type) {
        BarChart chart = new BarChart(this);

        BarChart.LayoutParams lp = new BarChart.LayoutParams(BarChart.LayoutParams.MATCH_PARENT,
                BarChart.LayoutParams.MATCH_PARENT);
        chart.setLayoutParams(lp);

        LinearLayout ll = findViewById(R.id.history_ll);
        ll.removeAllViews();
        ll.addView(chart);

        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.getDescription().setEnabled(false);
        chart.setMaxVisibleValueCount(100);
        chart.setPinchZoom(true);
        chart.setDrawGridBackground(false);
        chart.animateY(1500);

        IAxisValueFormatter xAxisFormatter = null;

        if (type.equalsIgnoreCase(getString(R.string.string_period_7_days))) {
            xAxisFormatter = new DayAxisValueFormatter(dates);
        } else if (type.equalsIgnoreCase(getString(R.string.string_period_week))) {
            xAxisFormatter = new WeekAxisValueFormatter(dates);
        } else if (type.equalsIgnoreCase(getString(R.string.string_period_month))) {
            xAxisFormatter = new WeekAxisValueFormatter(dates);
        } else if (type.equalsIgnoreCase(getString(R.string.string_period_year))) {
            xAxisFormatter = new WeekAxisValueFormatter(dates);
        }

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(8);
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setLabelRotationAngle(60);

        IAxisValueFormatter custom = new MyAxisValueFormatter();

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f);

        Legend l = chart.getLegend();
        l.setEnabled(false);

        XYMarkerView mv = new XYMarkerView(this, xAxisFormatter);
        mv.setChartView(chart);
        chart.setMarker(mv);

        historySetData(list, chart);
    }

    protected void historySetData(List<SugarHistory> list, BarChart chart) {
        if (list.size() > 0) {
            float start = 1f;
            ArrayList<BarEntry> yVals1 = new ArrayList<>();

            for (int i = (int) start; i <= list.size(); i++) {
                try{
                    yVals1.add(new BarEntry(i, Float.valueOf(list.get(i - 1).getmSugar().toString().replace(",","."))));
                } catch (Exception e){
                    Toast.makeText(mActivityHelper, "¡Error historySetData!", Toast.LENGTH_LONG).show();
                }
            }

            BarDataSet set1;

            if (chart.getData() != null &&
                    chart.getData().getDataSetCount() > 0) {
                set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
                set1.setValues(yVals1);
                chart.getData().notifyDataChanged();
                chart.notifyDataSetChanged();
            } else {
                set1 = new BarDataSet(yVals1, getString(R.string.string_history_label_x));

                set1.setDrawIcons(false);
                set1.setColor(commonGetColor(this, R.color.colorAccent));

                ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                dataSets.add(set1);

                BarData data = new BarData(dataSets);
                data.setValueTextSize(10f);
                data.setBarWidth(0.9f);

                chart.setData(data);
            }
        }
    }


    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (firstTime) {
                firstTime = false;
            } else {
                switch ((int) spinner.getSelectedItemId()) {
                    case 0:
                        dates.clear();
                        run7DaysChart(getString(R.string.string_period_7_days));
                        break;
                    case 1:
                        mSugarAcum = 0.0f;
                        dates.clear();
                        runWeeklyChart(getString(R.string.string_period_week));
                        break;
                    case 2:
                        mSugarAcum = 0.0f;
                        dates.clear();
                        runMonthlyChart(getString(R.string.string_period_month));
                        break;
                    case 3:
                        mSugarAcum = 0.0f;
                        dates.clear();
                        runYearlyChart(getString(R.string.string_period_year));
                        break;
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg) {
        }
    }

    protected List<SugarHistory> historyCommonAdding(Cursor cursor, String type) {
        int auxDate = 0;
        List<SugarHistory> lista = new ArrayList<>();
        int calendarAux = 0;

        try {
            if (type.equalsIgnoreCase(getString(R.string.string_period_week))) {
                mMaxSugar = Float.valueOf(
                        mSharedPreferences.getString(this.getString(R.string.string_shared_preferences_user_sugar), null)
                ) * 7;
                calendarAux = Calendar.WEEK_OF_YEAR;
            } else if (type.equalsIgnoreCase(getString(R.string.string_period_month))) {
                mMaxSugar = Float.valueOf(
                        mSharedPreferences.getString(this.getString(R.string.string_shared_preferences_user_sugar), null)
                ) * 30;
                calendarAux = Calendar.MONTH;
            } else if (type.equalsIgnoreCase(getString(R.string.string_period_year))) {
                mMaxSugar = Float.valueOf(
                        mSharedPreferences.getString(this.getString(R.string.string_shared_preferences_user_sugar), null)
                ) * 365;
                calendarAux = Calendar.YEAR;
            }


            if (cursor.moveToFirst()) {
                do {
                    try {
                        mCalendar.setTime(df.parse(cursor.getString(cursor.getColumnIndex(SUGAR_DATE))));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (auxDate == 0) {
                        auxDate = mCalendar.get(calendarAux);
                        mSugarAcum = mSugarAcum + Float.valueOf(cursor.getString(cursor.getColumnIndex(SUGAR_SUGAR)));
                    } else {
                        if (auxDate != mCalendar.get(calendarAux)) {
                            String aux = "";
                            if (type.equalsIgnoreCase(getString(R.string.string_period_week))) {
                                aux = getString(R.string.string_history_week) + auxDate;
                            } else if (type.equalsIgnoreCase(getString(R.string.string_period_month))) {
                                aux = commonRetrieveMonthName(this, auxDate);
                            } else if (type.equalsIgnoreCase(getString(R.string.string_period_year))) {
                                aux = String.valueOf(auxDate);
                            }

                            dates.add(aux);

                            lista.add(new SugarHistory(
                                    String.valueOf(mSugarAcum),
                                    aux));

                            auxDate = mCalendar.get(calendarAux);

                            mSugarAcum = 0.0f;
                            mSugarAcum = mSugarAcum + Float.valueOf(cursor.getString(cursor.getColumnIndex(SUGAR_SUGAR)));
                        } else {
                            mSugarAcum = mSugarAcum + Float.valueOf(cursor.getString(cursor.getColumnIndex(SUGAR_SUGAR)));
                        }
                    }
                } while (cursor.moveToNext());
            }

            return lista;
        } catch (Exception e){
            Toast.makeText(mActivityHelper, "¡Error historyCommonAdding!", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    protected List<SugarHistory> historyCommonAddFinal(String type) {
        int auxDate = 0;
        List<SugarHistory> lista = new ArrayList<>();
        NumberFormat formatter = new DecimalFormat("#0.00");
        int calendarAux = 0;

        try {
            if (type.equalsIgnoreCase(getString(R.string.string_period_week))) {
                calendarAux = Calendar.WEEK_OF_YEAR;
            } else if (type.equalsIgnoreCase(getString(R.string.string_period_month))) {
                calendarAux = Calendar.MONTH;
            } else if (type.equalsIgnoreCase(getString(R.string.string_period_year))) {
                calendarAux = Calendar.YEAR;
            }

            if (auxDate != mCalendar.get(calendarAux)) {
                if (auxDate == 0) {
                    mSugarAcum = mSugarAcum + Float.valueOf(mSharedPreferences.getString(this.getString(R.string.string_shared_preferences_user_sugar), null));
                } else {
                    String aux = "";
                    if (type.equalsIgnoreCase(getString(R.string.string_period_week))) {
                        aux = getString(R.string.string_history_week) + auxDate;
                    } else if (type.equalsIgnoreCase(getString(R.string.string_period_month))) {
                        aux = commonRetrieveMonthName(this, auxDate);
                    } else if (type.equalsIgnoreCase(getString(R.string.string_period_year))) {
                        aux = String.valueOf(auxDate);
                    }

                    dates.add(aux);

                    lista.add(new SugarHistory(
                            String.valueOf(formatter.format(mSugarAcum)),
                            aux));

                    mSugarAcum = 0.0f;
                    mSugarAcum = mSugarAcum + Float.valueOf(mSharedPreferences.getString(this.getString(R.string.string_shared_preferences_user_sugar), null));
                }

                auxDate = mCalendar.get(calendarAux);
            } else {
                mSugarAcum = mSugarAcum + Float.valueOf(mSharedPreferences.getString(this.getString(R.string.string_shared_preferences_user_sugar), null));
            }

            String aux = "";
            if (type.equalsIgnoreCase(getString(R.string.string_period_week))) {
                aux = getString(R.string.string_history_week) + auxDate;
            } else if (type.equalsIgnoreCase(getString(R.string.string_period_month))) {
                aux = commonRetrieveMonthName(this, auxDate);
            } else if (type.equalsIgnoreCase(getString(R.string.string_period_year))) {
                aux = String.valueOf(auxDate);
            }

            dates.add(aux);
            lista.add(new SugarHistory(
                    String.valueOf(formatter.format(mSugarAcum)),
                    aux
            ));

            return lista;
        } catch (Exception e){
            Toast.makeText(mActivityHelper, "¡Error historyCommonAddFinal!", Toast.LENGTH_LONG).show();
            return null;
        }
    }
}
