package com.skytel.sdm.ui.skydealer;

import android.content.Context;
import android.util.AttributeSet;

import com.skytel.sdm.R;
import com.skytel.sdm.entities.SalesReport;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.SortStateViewProviders;
import de.codecrafters.tableview.toolkit.TableDataRowColorizers;

public class SortableSalesReportPostPaidPaymentTableView extends SortableTableView<SalesReport> {

    public SortableSalesReportPostPaidPaymentTableView(Context context) {
        this(context, null);
    }

    public SortableSalesReportPostPaidPaymentTableView(Context context, AttributeSet attributes) {
        this(context, attributes, android.R.attr.listViewStyle);
    }

    public SortableSalesReportPostPaidPaymentTableView(Context context, AttributeSet attributes, int styleAttributes) {
        super(context, attributes, styleAttributes);
        SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(context, getResources().getStringArray(R.array.post_paid_payment_report_column_names));
        simpleTableHeaderAdapter.setTextColor(context.getResources().getColor(R.color.colorSkytelYellow));
        setHeaderAdapter(simpleTableHeaderAdapter);

        int rowColorEven = context.getResources().getColor(R.color.colorWhite);
        int rowColorOdd = context.getResources().getColor(R.color.colorTabBackground);
        setDataRowColorizer(TableDataRowColorizers.alternatingRows(rowColorEven, rowColorOdd));
        setHeaderSortStateViewProvider(SortStateViewProviders.darkArrows());

        setColumnWeight(0, 3);
        setColumnWeight(1, 2);
        setColumnWeight(2, 2);
        setColumnWeight(3, 3);

        setColumnComparator(3, SalesReportComparator.getSalesReportDateComparator());
        setColumnComparator(0, SalesReportComparator.getSalesReportPhoneComparator());

    }

}
