package com.skytel.sdp.ui.registration;

import com.skytel.sdp.entities.NewNumberReport;
import com.skytel.sdp.entities.RegistrationReport;
import com.skytel.sdp.entities.SalesReport;

import java.util.Comparator;

/**
 * Created by Altanchimeg on 7/6/2016.
 */

public class RegistrationReportComparator {
    public static Comparator<RegistrationReport> getRegistrationReportDateComparator() {
        return new RegistrationReportDateComparator();
    }

    private static class RegistrationReportDateComparator implements Comparator<RegistrationReport> {

        @Override
        public int compare(RegistrationReport registrationReport1, RegistrationReport registrationReport2) {
            return registrationReport1.getDate().compareTo(registrationReport2.getDate());
        }
    }

    public static Comparator<RegistrationReport> getRegistrationReportPhoneComparator() {
        return new RegistrationReportPhoneComparator();
    }
    private static class RegistrationReportPhoneComparator implements Comparator<RegistrationReport> {

        @Override
        public int compare(RegistrationReport registrationReport1, RegistrationReport registrationReport2) {
            return registrationReport1.getPhone().compareTo(registrationReport2.getPhone());
        }
    }

}
