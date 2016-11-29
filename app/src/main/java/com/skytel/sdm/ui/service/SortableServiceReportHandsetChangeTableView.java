package com.skytel.sdp.ui.service;

import android.content.Context;
import android.util.AttributeSet;

import com.skytel.sdp.R;
import com.skytel.sdp.entities.ServiceReport;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.SortStateViewProviders;
import de.codecrafters.tableview.toolkit.TableDataRowColorizers;

/**
 * Created by Altanchimeg on 7/8/2016.
 */

public class SortableServiceReportHandsetChangeTableView  extends SortableTableView<ServiceReport> {
    public SortableServiceReportHandsetChangeTableView(Context context) {
        this(context, null);
    }

    public SortableServiceReportHandsetChangeTableView(Context context, AttributeSet attributes) {
        this(context, attributes, android.R.attr.listViewStyle);
    }

    public SortableServiceReportHandsetChangeTableView(Context context, AttributeSet attributes, int styleAttributes) {
        super(context, attributes, styleAttributes);
        SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(context, getResources().getStringArray(R.array.handset_change_report_column_names));
        simpleTableHeaderAdapter.setTextColor(context.getResources().getColor(R.color.colorSkytelYellow));
        setHeaderAdapter(simpleTableHeaderAdapter);

        int rowColorEven = context.getResources().getColor(R.color.colorWhite);
        int rowColorOdd = context.getResources().getColor(R.color.colorTabBackground);
        setDataRowColorizer(TableDataRowColorizers.alternatingRows(rowColorEven, rowColorOdd));
        setHeaderSortStateViewProvider(SortStateViewProviders.darkArrows());

        setColumnWeight(0, 2);
        setColumnWeight(1, 2);
        setColumnWeight(2, 2);
        setColumnWeight(3, 2);
        setColumnWeight(4, 2);
        setColumnWeight(5, 2);


        setColumnComparator(4, ServiceReportComparator.getServiceReportDateComparator());
        setColumnComparator(1, ServiceReportComparator.getServiceReportPhoneComparator());

    }
}
