package com.skytel.sdp.ui.skydealer;

import com.skytel.sdp.entities.SalesReport;

import java.util.Comparator;

public class SalesReportComparator {

    public static Comparator<SalesReport> getSalesReportDateComparator() {
        return new SalesReportDateComparator();
    }

    private static class SalesReportDateComparator implements Comparator<SalesReport> {

        @Override
        public int compare(SalesReport salesReport1, SalesReport salesReport2) {
            return salesReport1.getDate().compareTo(salesReport2.getDate());
        }
    }

    public static Comparator<SalesReport> getSalesReportPhoneComparator() {
        return new SalesReportPhoneComparator();
    }

    private static class SalesReportPhoneComparator implements Comparator<SalesReport> {

        @Override
        public int compare(SalesReport salesReport1, SalesReport salesReport2) {
            return salesReport1.getPhone().compareTo(salesReport2.getPhone());
        }
    }
}
