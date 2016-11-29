package com.skytel.sdm.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skytel.sdm.R;
import com.skytel.sdm.entities.NewNumberReport;
import com.skytel.sdm.entities.SalesReport;

import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;

/**
 * Created by Altanchimeg on 6/30/2016.
 */

public class NewNumberReportAdapter extends TableDataAdapter<NewNumberReport> {
    private Context mContext;

//    TODO uuniig shuud resource -s shiiddeg bolgoh /Zolbayar
    private int textSize = 14;

    public NewNumberReportAdapter(Context context, List<NewNumberReport> data){
        super(context, data);
        mContext = context;

        if ((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            textSize = 18;

        }
    }
    // put columns to charge card table
    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        NewNumberReport newNumberReport = getRowData(rowIndex);
        View renderedView = null;

        switch (columnIndex) {
            case 0:
                renderedView = renderServiceType(newNumberReport);
                break;
            case 1:
                renderedView = renderNumberType(newNumberReport);
                break;
            case 2:
                renderedView = renderNumber(newNumberReport);
                break;
            case 3:
                renderedView = renderUnitAndDay(newNumberReport);
                break;
            case 4:
                renderedView = renderPrice(newNumberReport);
                break;
            case 5:
                renderedView = renderOrderState(newNumberReport);
                break;
            case 6:
                renderedView = renderDate(newNumberReport);
                break;
            case 7:
                renderedView = renderComment(newNumberReport);
                break;
        }

        return renderedView;
    }

    //Set columns layout

    private View renderServiceType(final NewNumberReport newNumberReport) {
        final TextView textView = new TextView(getContext());
        textView.setText(newNumberReport.getServiceType() + "");
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(textSize);
        return textView;
    }
    private View renderNumberType(final NewNumberReport newNumberReport) {
        final TextView textView = new TextView(getContext());
        textView.setText(newNumberReport.getNumberType() + "");
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(textSize);
        return textView;
    }
    private View renderNumber(final NewNumberReport newNumberReport) {
        final TextView textView = new TextView(getContext());
        textView.setText(newNumberReport.getNumber() + "");
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(textSize);
        return textView;
    }
    private View renderUnitAndDay(final NewNumberReport newNumberReport) {
        final TextView textView = new TextView(getContext());
        textView.setText(newNumberReport.getUnitAndDay() + "");
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(textSize);
        return textView;
    }
    private View renderPrice(final NewNumberReport newNumberReport) {
        final TextView textView = new TextView(getContext());
        textView.setText(newNumberReport.getPrice() + "");
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(textSize);
        return textView;
    }
    private View renderOrderState(final NewNumberReport newNumberReport) {
        final TextView textView = new TextView(getContext());
        textView.setText(newNumberReport.getOrderState() + "");
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(textSize);
        return textView;
    }

    private View renderDate(final NewNumberReport newNumberReport) {
        final TextView textView = new TextView(getContext());
        textView.setText(newNumberReport.getDate().toString() + "");
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(textSize);
        return textView;
    }
    private View renderComment(final NewNumberReport newNumberReport) {
        final TextView textView = new TextView(getContext());
        textView.setText(newNumberReport.getComment().toString() + "");
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(textSize);
        return textView;
    }

}
