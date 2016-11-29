package com.skytel.sdp.ui.plan;


import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.DefaultValueFormatter;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.skytel.sdp.R;
import com.skytel.sdp.utils.Constants;

import java.util.ArrayList;

public class PlanFragment extends Fragment {

    private PieChart pieChart;
    private BarChart barChart;

    private Context mContext;

    public PlanFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.plan, container, false);

        pieChart = (PieChart) rootView.findViewById(R.id.pieChart);
        barChart = (BarChart) rootView.findViewById(R.id.barChart);

        setPieData(4, 100);
        pieChart.animateY(3000);
        setBarData();
        barChart.animateY(3000);
        barChart.setPinchZoom(false);
        barChart.setDrawBarShadow(false);

        return rootView;
    }

    private void setPieData(int count, float range) {

        ArrayList<Entry> values = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < count; i++) {
            values.add(new Entry((float) ((Math.random() * range) + range / 5), 10));
            xVals.add(i + "");
        }

        PieDataSet dataSet = new PieDataSet(values, "Election Results");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        pieChart.setData(data);
        pieChart.invalidate();
    }

    private void setBarData() {
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < 10; i++) {
            int mult = 10;
            int val1 = (int) (Math.random() * mult) + mult;
            int val2 = (int) (Math.random() * mult) + mult;

            yVals1.add(new BarEntry(new float[]{val1, val2}, i));
        }
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 1; i <= 10; i++) {
            xVals.add(i+"сар");
        }


        BarDataSet set1;

            set1 = new BarDataSet(yVals1, "");
            set1.setColors(getColors());
            set1.setStackLabels(new String[]{getString(R.string.money_plan), getString(R.string.performance)});

            ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(xVals,dataSets);
            data.setValueFormatter(new DefaultValueFormatter(10));
            data.setValueTextColor(getResources().getColor(R.color.colorBackground));

            barChart.setData(data);

        barChart.setFitsSystemWindows(true);
        barChart.invalidate();
    }

    private int[] getColors() {

        int stacksize = 2;

        // have as many colors as stack-values per entry
        int[] colors = new int[stacksize];

        for (int i = 0; i < colors.length; i++) {
            colors[i] = Constants.MATERIAL_COLORS[i];
        }

        return colors;
    }

}
