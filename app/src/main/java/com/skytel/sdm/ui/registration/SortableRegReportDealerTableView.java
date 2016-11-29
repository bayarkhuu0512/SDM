package com.skytel.sdm.ui.registration;

import android.content.Context;
import android.util.AttributeSet;

import com.skytel.sdm.R;
import com.skytel.sdm.entities.RegistrationReport;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.SortStateViewProviders;
import de.codecrafters.tableview.toolkit.TableDataRowColorizers;

/**
 * Created by Altanchimeg on 7/6/2016.
 */

public class SortableRegReportDealerTableView extends SortableTableView<RegistrationReport> {
    public SortableRegReportDealerTableView(Context context) {
        this(context, null);
    }

    public SortableRegReportDealerTableView(Context context, AttributeSet attributes) {
        this(context, attributes, android.R.attr.listViewStyle);
    }

    public SortableRegReportDealerTableView(Context context, AttributeSet attributes, int styleAttributes) {
        super(context, attributes, styleAttributes);
        SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(context, getResources().getStringArray(R.array.dealer_report_column_names));
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


        setColumnComparator(4, RegistrationReportComparator.getRegistrationReportDateComparator());
        setColumnComparator(0, RegistrationReportComparator.getRegistrationReportPhoneComparator());

    }
}
