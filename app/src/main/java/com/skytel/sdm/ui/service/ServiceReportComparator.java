package com.skytel.sdm.ui.service;

import com.skytel.sdm.entities.RegistrationReport;
import com.skytel.sdm.entities.ServiceReport;

import java.util.Comparator;

/**
 * Created by Altanchimeg on 7/8/2016.
 */

public class ServiceReportComparator{
    public static Comparator<ServiceReport> getServiceReportDateComparator() {
        return new ServiceReportDateComparator();
    }

    private static class ServiceReportDateComparator implements Comparator<ServiceReport> {

        @Override
        public int compare(ServiceReport serviceReport1, ServiceReport serviceReport2) {
            return serviceReport1.getDate().compareTo(serviceReport2.getDate());
        }
    }

    public static Comparator<ServiceReport> getServiceReportPhoneComparator() {
        return new ServiceReportPhoneComparator();
    }
    private static class ServiceReportPhoneComparator implements Comparator<ServiceReport> {

        @Override
        public int compare(ServiceReport serviceReport1, ServiceReport serviceReport2) {
            return serviceReport1.getPhone().compareTo(serviceReport2.getPhone());
        }
    }

}
