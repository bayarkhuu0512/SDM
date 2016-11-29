package com.skytel.sdp.ui.newnumber;

import com.skytel.sdp.entities.NewNumberReport;

import java.util.Comparator;

/**
 * Created by Altanchimeg on 6/30/2016.
 */

public class NewNumberReportComparator {
    public static Comparator<NewNumberReport> getNewNumberReportDateComparator() {
        return new NewNumberReportDateComparator();
    }

    private static class NewNumberReportDateComparator implements Comparator<NewNumberReport> {

        @Override
        public int compare(NewNumberReport newNumberReport1, NewNumberReport newNumberReport2) {
            return newNumberReport1.getDate().compareTo(newNumberReport2.getDate());
        }
    }

    public static Comparator<NewNumberReport> getNewNumberReportPhoneComparator() {
        return new NewNumberReportPhoneComparator();
    }
    private static class NewNumberReportPhoneComparator implements Comparator<NewNumberReport> {

        @Override
        public int compare(NewNumberReport newNumberReport1, NewNumberReport newNumberReport2) {
            return newNumberReport1.getNumber().compareTo(newNumberReport2.getNumber());
        }
    }
}
