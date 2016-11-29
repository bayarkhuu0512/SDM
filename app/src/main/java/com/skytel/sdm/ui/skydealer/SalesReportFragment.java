package com.skytel.sdm.ui.skydealer;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.skytel.sdm.LoginActivity;
import com.skytel.sdm.MainActivity;
import com.skytel.sdm.R;
import com.skytel.sdm.adapter.NothingSelectedSpinnerAdapter;
import com.skytel.sdm.adapter.SalesReportCardTypeAdapter;
import com.skytel.sdm.adapter.SalesReportChargeCardAdapter;
import com.skytel.sdm.adapter.SalesReportPostPaidPaymentAdapter;
import com.skytel.sdm.database.DataManager;
import com.skytel.sdm.entities.CardType;
import com.skytel.sdm.entities.SalesReport;
import com.skytel.sdm.enums.PackageTypeEnum;
import com.skytel.sdm.network.HttpClient;
import com.skytel.sdm.utils.Constants;
import com.skytel.sdm.utils.CustomProgressDialog;
import com.skytel.sdm.utils.PrefManager;
import com.skytel.sdm.utils.ValidationChecker;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.codecrafters.tableview.SortableTableView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class SalesReportFragment extends Fragment implements Constants {

    String TAG = SalesReportFragment.class.getName();

    private OkHttpClient mClient;
    private Context mContext;
    private DataManager mDataManager;
    private PrefManager mPrefManager;

    private List<SalesReport> mSalesReportArrayList;
    private CustomProgressDialog mProgressDialog;

    private RelativeLayout mReportTableViewContainer;

    private Button mSearch;
    private Button mRefresh;
    //private Button mFilterByAll;
    private Button mFilterBySuccess;
    private Button mFilterByFailed;
    private EditText mFilterByPhoneNumber;
    private EditText mFilterByEndDate;
    private EditText mFilterByStartDate;
    private Spinner mFilterByUnitPackage;
    private TextView mTextUnitPackage;

    private String mSelectedFilterByUnit = null;

    private Button mFilterButtonByEndDate;
    private Button mFilterButtonByStartDate;

    private Spinner mReportTypeSpinner;

    private int mSelectedFilterButton = FILTER_SUCCESS;
    private boolean isSuccessFilter = true;

    private int mSelectedItemId = -1;
    private String[] mReportTypeValue = null;

    private int mYear;
    private int mMonth;
    private int mDay;

    private int fromPage = 0;
    private int numRow = 100;


    private final Calendar mCalendar = Calendar.getInstance();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.skydealer_report, container, false);

        mContext = getActivity();
        mDataManager = new DataManager(mContext);
        mClient = HttpClient.getInstance();

        mPrefManager = new PrefManager(mContext);
        mSalesReportArrayList = new ArrayList<>();
        mProgressDialog = new CustomProgressDialog(getActivity());


        mReportTableViewContainer = (RelativeLayout) rootView.findViewById(R.id.reportTableViewContainer);

        //      mSalesReportTableView = (SortableTableView) rootView.findViewById(R.id.salesReportTableView);

        mSearch = (Button) rootView.findViewById(R.id.search);
        mSearch.setOnClickListener(searchOnClick);

        mRefresh = (Button) rootView.findViewById(R.id.refresh);
        mRefresh.setOnClickListener(searchOnClick);

        /*mFilterByAll = (Button) rootView.findViewById(R.id.filterByAll);
        mFilterByAll.setOnClickListener(filterByAllOnClick);*/

        mFilterBySuccess = (Button) rootView.findViewById(R.id.filterBySuccess);
        mFilterBySuccess.setOnClickListener(filterBySuccessOnClick);

        mFilterByFailed = (Button) rootView.findViewById(R.id.filterByFailed);
        mFilterByFailed.setOnClickListener(filterByFailedOnClick);

        mFilterByPhoneNumber = (EditText) rootView.findViewById(R.id.filterByOrderedNumber);
        mFilterByEndDate = (EditText) rootView.findViewById(R.id.filterByEndDate);
        mFilterByStartDate = (EditText) rootView.findViewById(R.id.filterByStartDate);
        mFilterByUnitPackage = (Spinner) rootView.findViewById(R.id.filterByUnitPackage);
        mTextUnitPackage = (TextView) rootView.findViewById(R.id.txt_unit_type);

        //    ArrayAdapter<CharSequence> adapterFilter = ArrayAdapter.createFromResource(getActivity(), R.array.skydealer_charge_card_types, android.R.layout.simple_spinner_item);
/*
        mFilterByUnitPackage.setAdapter(new NothingSelectedSpinnerAdapter(adapterFilter,
                R.layout.spinner_row_nothing_selected,
                // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                getActivity()));
*/

        final List<CardType> cardTypes = mDataManager.getCardTypes();
        SalesReportCardTypeAdapter adapterFilter = new SalesReportCardTypeAdapter(getActivity(), R.layout.sales_report_card_types_item, cardTypes);
        mFilterByUnitPackage.setAdapter(new NothingSelectedSpinnerAdapter(adapterFilter,
                R.layout.spinner_row_nothing_selected,
                getActivity()));
        mFilterByUnitPackage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    mSelectedFilterByUnit = cardTypes.get(position-1).getName();
                    Log.d(TAG, "Card type name: "+cardTypes.get(position-1).getName());
                } catch (ArrayIndexOutOfBoundsException e){

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getActivity(),"Nothing Selected",Toast.LENGTH_SHORT).show();
            }
        });

        // TODO: Nothing selected state and then choose none abter selected
        mReportTypeSpinner = (Spinner) rootView.findViewById(R.id.choose_skydealer_report_type);
        ArrayAdapter<CharSequence> adapterReport = ArrayAdapter.createFromResource(getActivity(), R.array.skydealer_report_type, android.R.layout.simple_spinner_item);
        adapterReport.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mReportTypeSpinner.setAdapter(new NothingSelectedSpinnerAdapter(adapterReport,
                R.layout.spinner_row_nothing_selected,
                // R.layout.contact_spinner_nothing_selected_dropdown, // Option
                getActivity()));
        mReportTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {

                    mSelectedItemId = (int) mReportTypeSpinner.getSelectedItemId();
                    switch (mSelectedItemId) {
                        case 0:
                            mFilterByUnitPackage.setVisibility(View.VISIBLE);
                            mTextUnitPackage.setVisibility(View.VISIBLE);
                            break;
                        case 1:
                            mFilterByUnitPackage.setVisibility(View.GONE);
                            mTextUnitPackage.setVisibility(View.GONE);
                            break;
                    }
                    if (ValidationChecker.isSelected(mSelectedItemId)) {
                        mProgressDialog.show();

                        DateTime currentDateJoda = DateTime.parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                        String startDate = new SimpleDateFormat("yyyy-MM-dd").format(currentDateJoda.minusMonths(3).toDate());
                        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd").format(currentDateJoda.toDate());

                       // Log.d(TAG,"Days Between: "+ Days.daysBetween(currentDateJoda.toDateMidnight(), DateTime.parse("2016-06-01")).getDays() + "TEST DATE: "+date);

                        runSalesReportFunction(mReportTypeValue[mSelectedItemId], true, "", startDate, currentDateTime, null);
                        Log.d(TAG, "Report Type: " + mReportTypeValue[mSelectedItemId]);
                        mFilterByStartDate.setText(startDate);
                        mFilterByEndDate.setText(currentDateTime);
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.please_select_the_field), Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mReportTypeValue = getResources().getStringArray(R.array.skydealer_report_type_code);

        mFilterButtonByStartDate = (Button) rootView.findViewById(R.id.btn_start_date);
        mFilterButtonByStartDate.setOnClickListener(filterByStartDateOnClick);
        mFilterButtonByEndDate = (Button) rootView.findViewById(R.id.btn_end_date);
        mFilterButtonByEndDate.setOnClickListener(filterByEndDateOnClick);

        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);

        return rootView;
    }

    View.OnClickListener searchOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                mSelectedItemId = (int) mReportTypeSpinner.getSelectedItemId();
                String phone_number = mFilterByPhoneNumber.getText().toString();
                Boolean isSuccess = isSuccessFilter;

                String start_date = mFilterByStartDate.getText().toString();
                String end_date = mFilterByEndDate.getText().toString();
                if (ValidationChecker.isSelected(mSelectedItemId)) {
                    if(ValidationChecker.isOnInterval(Days.daysBetween(DateTime.parse(start_date), DateTime.parse(end_date)).getDays())){
                        mProgressDialog.show();
                        mSelectedItemId = (int) mReportTypeSpinner.getSelectedItemId();

                        runSalesReportFunction(mReportTypeValue[mSelectedItemId], isSuccess, phone_number, start_date, end_date,mSelectedFilterByUnit);
                    }
                    else{
                        Toast.makeText(getActivity(), getText(R.string.interval_error), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getActivity(), getText(R.string.choose_report_type), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    View.OnClickListener filterByAllOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mSelectedFilterButton = FILTER_ALL;
            invalidateFilterButtons();
        }
    };

    View.OnClickListener filterBySuccessOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mSelectedFilterButton = FILTER_SUCCESS;
            invalidateFilterButtons();
        }
    };

    View.OnClickListener filterByFailedOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mSelectedFilterButton = FILTER_FAILED;
            invalidateFilterButtons();
        }
    };
    View.OnClickListener filterByStartDateOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            mCalendar.set(Calendar.YEAR, year);
                            mCalendar.set(Calendar.MONTH, monthOfYear);
                            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            String myFormat = "yyyy-MM-dd"; // In which you need put here
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

                            mFilterByStartDate.setText(sdf.format(mCalendar.getTime()));

                        }
                    }, mYear, mMonth, mDay);
            dpd.show();
        }
    };
    View.OnClickListener filterByEndDateOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            mCalendar.set(Calendar.YEAR, year);
                            mCalendar.set(Calendar.MONTH, monthOfYear);
                            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            String myFormat = "yyyy-MM-dd"; // In which you need put here
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

                            mFilterByEndDate.setText(sdf.format(mCalendar.getTime()));

                        }
                    }, mYear, mMonth, mDay);
            dpd.show();
        }
    };


    private void invalidateFilterButtons() {
        switch (mSelectedFilterButton) {
           /* case FILTER_ALL:
                mFilterByAll.setBackground(getResources().getDrawable(R.drawable.btn_yellow_selected));
                mFilterByAll.setTextColor(Color.WHITE);
                mFilterBySuccess.setBackground(getResources().getDrawable(R.drawable.btn_yellow));
                mFilterBySuccess.setTextColor(getResources().getColor(R.color.colorSkytelYellow));
                mFilterByFailed.setBackground(getResources().getDrawable(R.drawable.btn_yellow));
                mFilterByFailed.setTextColor(getResources().getColor(R.color.colorSkytelYellow));
                isSuccessFilter = true;
                break;*/
            case FILTER_SUCCESS:
                //mFilterByAll.setBackground(getResources().getDrawable(R.drawable.btn_yellow));
                // mFilterByAll.setTextColor(getResources().getColor(R.color.colorSkytelYellow));

                mFilterBySuccess.setBackground(getResources().getDrawable(R.drawable.btn_yellow_selected));
                mFilterBySuccess.setTextColor(Color.WHITE);

                mFilterByFailed.setBackground(getResources().getDrawable(R.drawable.btn_yellow));
                mFilterByFailed.setTextColor(getResources().getColor(R.color.colorSkytelYellow));
                isSuccessFilter = true;
                break;
            case FILTER_FAILED:
                // mFilterByAll.setBackground(getResources().getDrawable(R.drawable.btn_yellow));
                //mFilterByAll.setTextColor(getResources().getColor(R.color.colorSkytelYellow));

                mFilterBySuccess.setBackground(getResources().getDrawable(R.drawable.btn_yellow));
                mFilterBySuccess.setTextColor(getResources().getColor(R.color.colorSkytelYellow));

                mFilterByFailed.setBackground(getResources().getDrawable(R.drawable.btn_yellow_selected));
                mFilterByFailed.setTextColor(Color.WHITE);
                isSuccessFilter = false;
                break;
        }
    }


    public void runSalesReportFunction(String report_type, boolean isSuccess, String phone, String start_date, String end_date, String card_type) throws Exception {
        mProgressDialog.show();
        final StringBuilder url = new StringBuilder();
        url.append(Constants.SERVER_URL);
        url.append(Constants.FUNCTION_DEALER_REPORT);
        url.append("?trans_type=" + report_type);
        url.append("&len=" + numRow);
        url.append("&from=" + fromPage);
        url.append("&is_success=" + isSuccess);
        url.append("&phone=" + phone);
        url.append("&start_date=" + start_date);
        url.append("&end_date=" + end_date);
        if(card_type != null || report_type == "card"){
            url.append("&card_type=" + card_type);
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "sned URL: "+url.toString());
            }
        });

        System.out.print(url + "\n");
        System.out.println(mPrefManager.getAuthToken());

        Request request = new Request.Builder()
                .url(url.toString())
                .addHeader(PREF_AUTH_TOKEN, mPrefManager.getAuthToken())
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mProgressDialog.dismiss();
                System.out.println("onFailure");
                e.printStackTrace();
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(mContext, getResources().getString(R.string.check_internet_connection), Toast.LENGTH_LONG).show();
                            // Used for debug
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mProgressDialog.dismiss();
                System.out.println("onResponse");

                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                String resp = response.body().string();
                System.out.println("resp " + resp);

                try {
                    JSONObject jsonObj = new JSONObject(resp);
                    int result_code = jsonObj.getInt("result_code");
                    final String result_msg = jsonObj.getString("result_msg");


                    Log.d(TAG, "result_code: " + result_code);
                    Log.d(TAG, "result_msg: " + result_msg);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(mContext, ""+ result_msg, Toast.LENGTH_LONG).show();
                            // Used for debug
                        }
                    });
                    if (result_code == Constants.RESULT_CODE_UNREGISTERED_TOKEN) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.sCurrentMenu = Constants.MENU_NEWNUMBER;
                                mPrefManager.setIsLoggedIn(false);
                                mDataManager.resetCardTypes();

                                getActivity().finish();
                                Intent intent = new Intent(mContext, LoginActivity.class);
                                startActivity(intent);


                            }
                        });
                    }
                    JSONArray jArray = jsonObj.getJSONArray("transactions");

                    Log.d(TAG, "*****JARRAY*****" + jArray.length());
                    mSalesReportArrayList.clear();
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jsonData = jArray.getJSONObject(i);

                        String date = jsonData.getString("date");
                        boolean success = jsonData.getBoolean("success");
                        String card_name = jsonData.getString("card_name");
                        String value = jsonData.getString("value");
                        String phone = jsonData.getString("phone");

                        Log.d(TAG, "INDEX:       " + i);

                        Log.d(TAG, "date: " + date);
                        Log.d(TAG, "success: " + success);
                        Log.d(TAG, "card_name: " + card_name);
                        Log.d(TAG, "value: " + value);
                        Log.d(TAG, "phone: " + phone);

                        SalesReport salesReport = new SalesReport();
                        // DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                        salesReport.setId(i);
                        salesReport.setPhone(phone);
                        salesReport.setValue(value);
                        salesReport.setSuccess(success);
                        salesReport.setCardName(card_name);
                        salesReport.setDate(date);

                        mSalesReportArrayList.add(i, salesReport);
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mSelectedItemId == 0) {
                                SortableSalesReportChargeCardTableView sortableSalesReportChargeCardTableView = new SortableSalesReportChargeCardTableView(getActivity());
                                mReportTableViewContainer.removeAllViews();
                                mReportTableViewContainer.addView(sortableSalesReportChargeCardTableView);
                                sortableSalesReportChargeCardTableView.setColumnCount(getResources().getInteger(R.integer.chargecard_report_column));
                                sortableSalesReportChargeCardTableView.setHeaderBackgroundColor(Color.TRANSPARENT);
                                sortableSalesReportChargeCardTableView.setDataAdapter(new SalesReportChargeCardAdapter(getActivity(), mSalesReportArrayList));
                            } else {
                                SortableSalesReportPostPaidPaymentTableView sortableSalesReportPostPaidPaymentTableView = new SortableSalesReportPostPaidPaymentTableView(getActivity());
                                mReportTableViewContainer.removeAllViews();
                                mReportTableViewContainer.addView(sortableSalesReportPostPaidPaymentTableView);
                                sortableSalesReportPostPaidPaymentTableView.setColumnCount(getResources().getInteger(R.integer.postpaidpayment_report_column));
                                sortableSalesReportPostPaidPaymentTableView.setHeaderBackgroundColor(Color.TRANSPARENT);
                                sortableSalesReportPostPaidPaymentTableView.setDataAdapter(new SalesReportPostPaidPaymentAdapter(getActivity(), mSalesReportArrayList));
                            }

                            mSelectedFilterByUnit = null;
                        }
                    });


                } catch (JSONException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "Алдаатай хариу ирлээ", Toast.LENGTH_LONG).show();
                        }
                    });
                    e.printStackTrace();
                }
            }
        });
    }
}
