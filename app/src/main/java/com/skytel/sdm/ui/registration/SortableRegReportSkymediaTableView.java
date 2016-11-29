package com.skytel.sdp.ui.registration;

import android.content.Context;
import android.util.AttributeSet;

import com.skytel.sdp.R;
import com.skytel.sdp.entities.RegistrationReport;
import com.skytel.sdp.ui.skydealer.SalesReportComparator;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.SortStateViewProviders;
import de.codecrafters.tableview.toolkit.TableDataRowColorizers;

/**
 * Created by Altanchimeg on 7/6/2016.
 */

public class SortableRegReportSkymediaTableView extends SortableTableView<RegistrationReport> {
    public SortableRegReportSkymediaTableView(Context context) {
        this(context, null);
    }

    public SortableRegReportSkymediaTableView(Context context, AttributeSet attributes) {
        this(context, attributes, android.R.attr.listViewStyle);
    }

    public SortableRegReportSkymediaTableView(Context context, AttributeSet attributes, int styleAttributes) {
        super(context, attributes, styleAttributes);
        SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(context, getResources().getStringArray(R.array.skymedia_report_column_names));
        simpleTableHeaderAdapter.setTextColor(context.getResources().getColor(R.color.colorSkytelYellow));
        setHeaderAdapter(simpleTableHeaderAdapter);

        int rowColorEven = context.getResources().getColor(R.color.colorWhite);
        int rowColorOdd = context.getResources().getColor(R.color.colorTabBackground);
        setDataRowColorizer(TableDataRowColorizers.alternatingRows(rowColorEven, rowColorOdd));
        setHeaderSortStateViewProvider(SortStateViewProviders.darkArrows());

        setColumnWeight(0, 3);
        setColumnWeight(1, 3);
        setColumnWeight(2, 2);
        setColumnWeight(3, 3);
        setColumnWeight(4, 2);

        setColumnComparator(3, RegistrationReportComparator.getRegistrationReportDateComparator());
        setColumnComparator(0, RegistrationReportComparator.getRegistrationReportPhoneComparator());

    }
}
