package com.skytel.sdp.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skytel.sdp.R;
import com.skytel.sdp.entities.SalesReport;

import java.util.List;

import de.codecrafters.tableview.TableDataAdapter;

public class SalesReportPostPaidPaymentAdapter extends TableDataAdapter<SalesReport> {
    private Context mContext;
    //    TODO uuniig shuud resource -s shiiddeg bolgoh /Zolbayar
    private int textSize = 14;

    public SalesReportPostPaidPaymentAdapter(Context context, List<SalesReport> data) {
        super(context, data);
        mContext = context;

        if ((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            textSize = 18;

        }
    }
    // put columns to postpaid payment table
    @Override
    public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        SalesReport salesReport = getRowData(rowIndex);
        View renderedView = null;

        switch (columnIndex) {
            case 0:
                renderedView = renderPhone(salesReport);
                break;
            case 1:
                renderedView = renderValue(salesReport);
                break;
            case 2:
                renderedView = renderSuccess(salesReport);
                break;
            case 3:
                renderedView = renderDate(salesReport);
                break;
            case 4:
                renderedView = renderComment(salesReport);
                break;
        }

        return renderedView;
    }
    //Set columns layout
    private View renderPhone(final SalesReport salesReport) {
        final TextView textView = new TextView(getContext());
        textView.setText(salesReport.getPhone() + "");
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(textSize);
        return textView;
    }
    private View renderValue(final SalesReport salesReport) {
        final TextView textView = new TextView(getContext());
        textView.setText(salesReport.getValue() + "");
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(textSize);
        return textView;
    }
    private View renderSuccess(final SalesReport salesReport) {
        final TextView textView = new TextView(getContext());
//        textView.setText(salesReport.getDate().toString() + "");
        if (salesReport.isSuccess()) {
            textView.setText(mContext.getResources().getString(R.string.successful));
        } else {
            textView.setText(mContext.getResources().getString(R.string.unsuccessful));
        }
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(textSize);
        return textView;
    }

    private View renderDate(final SalesReport salesReport) {
        final TextView textView = new TextView(getContext());
        textView.setText(salesReport.getDate().toString() + "");
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(textSize);
        return textView;
    }
    private View renderComment(final SalesReport salesReport) {
        final TextView textView = new TextView(getContext());
        textView.setText(salesReport.getComment().toString() + "");
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(textSize);
        return textView;
    }


}