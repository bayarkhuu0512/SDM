package com.skytel.sdm.ui.registration;


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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.skytel.sdm.LoginActivity;
import com.skytel.sdm.MainActivity;
import com.skytel.sdm.R;
import com.skytel.sdm.adapter.NothingSelectedSpinnerAdapter;
import com.skytel.sdm.adapter.RegReportDealerAdapter;
import com.skytel.sdm.adapter.RegReportSkymediaAdapter;
import com.skytel.sdm.adapter.SalesReportChargeCardAdapter;
import com.skytel.sdm.adapter.SalesReportPostPaidPaymentAdapter;
import com.skytel.sdm.database.DataManager;
import com.skytel.sdm.entities.RegistrationReport;
import com.skytel.sdm.entities.SalesReport;
import com.skytel.sdm.network.HttpClient;
import com.skytel.sdm.ui.skydealer.SortableSalesReportChargeCardTableView;
import com.skytel.sdm.ui.skydealer.SortableSalesReportPostPaidPaymentTableView;
import com.skytel.sdm.utils.Constants;
import com.skytel.sdm.utils.CustomProgressDialog;
import com.skytel.sdm.utils.PrefManager;
import com.skytel.sdm.utils.ValidationChecker;

import org.joda.time.DateTime;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class RegistrationReportFragment extends Fragment implements Constants {

    String TAG = RegistrationReportFragment.class.getName();

    private OkHttpClient mClient;
    private Context mContext;
    private DataManager mDataManager;
    private PrefManager mPrefManager;
    private List<RegistrationReport> mRegistrationReportArrayList;
    private CustomProgressDialog mProgressDialog;

    private RelativeLayout mReportTableViewContainer;

    private Button mSearch;
    private  Button mRefresh;
    private Button mFilterByAll;
    private Button mFilterBySuccess;
    private Button mFilterByFailed;
    private Button mFilterByWaiting;
    private EditText mFilterByPhoneNumber;
    private EditText mFilterByEndDate;
    private EditText mFilterByStartDate;

    private Button mFilterButtonByEndDate;
    private Button mFilterButtonByStartDate;

    private Spinner mReportTypeSpinner;

    private int mSelectedFilterButton = FILTER_ALL;
    private int mSelectedItemId = -1;
    private String[] mReportTypeValue = null;

    private int fromPage = 0;
    private int numRow = 100;

    private int mYear;
    private int mMonth;
    private int mDay;

    private final Calendar mCalendar = Calendar.getInstance();


    public RegistrationReportFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.registration_report, container, false);

        mContext = getActivity();
        mDataManager = new DataManager(mContext);
        mClient = HttpClient.getInstance();
        mPrefManager = new PrefManager(mContext);
        mRegistrationReportArrayList = new ArrayList<>();
        mProgressDialog = new CustomProgressDialog(getActivity());

        mReportTableViewContainer = (RelativeLayout) rootView.findViewById(R.id.reportTableViewContainer);

        mSearch = (Button) rootView.findViewById(R.id.search);
        mSearch.setOnClickListener(searchOnClick);

        mRefresh = (Button) rootView.findViewById(R.id.refresh);
        mRefresh.setOnClickListener(searchOnClick);

        mFilterByAll = (Button) rootView.findViewById(R.id.filterByAll);
        mFilterByAll.setOnClickListener(filterByAllOnClick);

        mFilterByWaiting = (Button) rootView.findViewById(R.id.filterByWaiting);
        mFilterByWaiting.setOnClickListener(filterByWaitingOnClick);

        mFilterBySuccess = (Button) rootView.findViewById(R.id.filterBySuccess);
        mFilterBySuccess.setOnClickListener(filterBySuccessOnClick);

        mFilterByFailed = (Button) rootView.findViewById(R.id.filterByFailed);
        mFilterByFailed.setOnClickListener(filterByFailedOnClick);

        mFilterByPhoneNumber = (EditText) rootView.findViewById(R.id.filterByOrderedNumber);
        mFilterByEndDate = (EditText) rootView.findViewById(R.id.filterByEndDate);
        mFilterByStartDate = (EditText) rootView.findViewById(R.id.filterByStartDate);

        mFilterButtonByStartDate = (Button) rootView.findViewById(R.id.btn_start_date);
        mFilterButtonByStartDate.setOnClickListener(filterByStartDateOnClick);
        mFilterButtonByEndDate = (Button) rootView.findViewById(R.id.btn_end_date);
        mFilterButtonByEndDate.setOnClickListener(filterByEndDateOnClick);

        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);

        // TODO: Nothing selected state and then choose none abter selected
        mReportTypeSpinner = (Spinner) rootView.findViewById(R.id.choose_registration_report_type);
        ArrayAdapter<CharSequence> adapterReport = ArrayAdapter.createFromResource(getActivity(), R.array.registration_report_type, android.R.layout.simple_spinner_item);
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

                    if (ValidationChecker.isSelected(mSelectedItemId)) {
                        mProgressDialog.show();

                        DateTime currentDateJoda = DateTime.parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                        String startDate = new SimpleDateFormat("yyyy-MM-dd").format(currentDateJoda.minusMonths(3).toDate());
                        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd").format(currentDateJoda.toDate());

                        runRegistrationReportFunction(mSelectedItemId, mSelectedFilterButton, "", startDate, currentDateTime);
                        mFilterByStartDate.setText(startDate);
                        mFilterByEndDate.setText(currentDateTime);
                        Log.d(TAG, "Report Type: " + mSelectedItemId);
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

        mReportTypeValue = getResources().getStringArray(R.array.registration_report_type);

        return rootView;
    }

    View.OnClickListener searchOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                mSelectedItemId = (int) mReportTypeSpinner.getSelectedItemId();
                if (ValidationChecker.isSelected(mSelectedItemId)) {
                    mProgressDialog.show();
                    mSelectedItemId = (int) mReportTypeSpinner.getSelectedItemId();
                    String phone_number = mFilterByPhoneNumber.getText().toString();
                    int order_status = mSelectedFilterButton;
                    String start_date = mFilterByStartDate.getText().toString();
                    String end_date = mFilterByEndDate.getText().toString();

                    runRegistrationReportFunction(mSelectedItemId, order_status, phone_number, start_date, end_date);

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
    View.OnClickListener filterByWaitingOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mSelectedFilterButton = FILTER_WAITING;
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
            case FILTER_ALL:
                mFilterByAll.setBackground(getResources().getDrawable(R.drawable.btn_yellow_selected));
                mFilterByAll.setTextColor(Color.WHITE);
                mFilterBySuccess.setBackground(getResources().getDrawable(R.drawable.btn_yellow));
                mFilterBySuccess.setTextColor(getResources().getColor(R.color.colorSkytelYellow));
                mFilterByFailed.setBackground(getResources().getDrawable(R.drawable.btn_yellow));
                mFilterByFailed.setTextColor(getResources().getColor(R.color.colorSkytelYellow));
                mFilterByWaiting.setBackground(getResources().getDrawable(R.drawable.btn_yellow));
                mFilterByWaiting.setTextColor(getResources().getColor(R.color.colorSkytelYellow));
                break;
            case FILTER_SUCCESS:
                mFilterByAll.setBackground(getResources().getDrawable(R.drawable.btn_yellow));
                mFilterByAll.setTextColor(getResources().getColor(R.color.colorSkytelYellow));

                mFilterBySuccess.setBackground(getResources().getDrawable(R.drawable.btn_yellow_selected));
                mFilterBySuccess.setTextColor(Color.WHITE);

                mFilterByFailed.setBackground(getResources().getDrawable(R.drawable.btn_yellow));
                mFilterByFailed.setTextColor(getResources().getColor(R.color.colorSkytelYellow));

                mFilterByWaiting.setBackground(getResources().getDrawable(R.drawable.btn_yellow));
                mFilterByWaiting.setTextColor(getResources().getColor(R.color.colorSkytelYellow));
                break;
            case FILTER_FAILED:
                mFilterByAll.setBackground(getResources().getDrawable(R.drawable.btn_yellow));
                mFilterByAll.setTextColor(getResources().getColor(R.color.colorSkytelYellow));

                mFilterBySuccess.setBackground(getResources().getDrawable(R.drawable.btn_yellow));
                mFilterBySuccess.setTextColor(getResources().getColor(R.color.colorSkytelYellow));

                mFilterByFailed.setBackground(getResources().getDrawable(R.drawable.btn_yellow_selected));
                mFilterByFailed.setTextColor(Color.WHITE);

                mFilterByWaiting.setBackground(getResources().getDrawable(R.drawable.btn_yellow));
                mFilterByWaiting.setTextColor(getResources().getColor(R.color.colorSkytelYellow));
                break;

            case FILTER_WAITING:
                mFilterByAll.setBackground(getResources().getDrawable(R.drawable.btn_yellow));
                mFilterByAll.setTextColor(getResources().getColor(R.color.colorSkytelYellow));

                mFilterBySuccess.setBackground(getResources().getDrawable(R.drawable.btn_yellow));
                mFilterBySuccess.setTextColor(getResources().getColor(R.color.colorSkytelYellow));

                mFilterByFailed.setBackground(getResources().getDrawable(R.drawable.btn_yellow));
                mFilterByFailed.setTextColor(getResources().getColor(R.color.colorSkytelYellow));

                mFilterByWaiting.setBackground(getResources().getDrawable(R.drawable.btn_yellow_selected));
                mFilterByWaiting.setTextColor(Color.WHITE);
                break;
        }
    }
    public void runRegistrationReportFunction(int report_type, int order_status , String phone, String start_date, String end_date) throws Exception {
        mProgressDialog.show();
        final StringBuilder url = new StringBuilder();
        url.append(Constants.SERVER_URL);
        url.append(Constants.FUNCTION_REGISTER_REPORT);
        url.append("?order_status=" + order_status);
        url.append("&len=" + numRow);
        url.append("&from=" + fromPage);
        url.append("&service_type=" + report_type);
        url.append("&phone=" + phone);
        url.append("&start_date=" + start_date);
        url.append("&end_date=" + end_date);


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "send URL: "+url.toString());
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

                    JSONArray jArray = jsonObj.getJSONArray("user_registrations");

                    Log.d(TAG, "*****JARRAY*****" + jArray.length());
                    mRegistrationReportArrayList.clear();
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jsonData = jArray.getJSONObject(i);


                        String date = jsonData.getString("date");
                        String order_status = jsonData.getString("order_status");
                        int service_type = jsonData.getInt("service_type");
                        String description;
                        RegistrationReport registrationReport = new RegistrationReport();
                        if(jsonData.has("description")){
                            description = jsonData.getString("description");
                            registrationReport.setDescription(description);
                            Log.d(TAG, "description: " + description);
                        }
                        String phone = jsonData.getString("phone");
                        String comment = "";
                        if(!jsonData.isNull("operator_comment")){
                            comment = jsonData.getString("operator_comment");
                        }

                        registrationReport.setId(i);
                        registrationReport.setPhone(phone);
                        registrationReport.setOrderStatus(order_status);
                        registrationReport.setServiceType(service_type);
                        registrationReport.setComment(comment);
                        registrationReport.setDate(date);

                        Log.d(TAG, "INDEX:       " + i);

                        Log.d(TAG, "phone: " + phone);
                        Log.d(TAG, "order_status: " + order_status);
                        Log.d(TAG, "service_type: " + service_type);
                        Log.d(TAG, "comment: " + comment);
                        Log.d(TAG, "date: " + date);

                        // if it is dealer get discpunt and dealer type
                         if(service_type == 1){
                             int discount = jsonData.getInt("discount");
                             String dealer_type = jsonData.getString("dealer_type");

                             registrationReport.setDealerType(dealer_type);
                             registrationReport.setDiscount(discount);

                             Log.d(TAG, "dealer_type: " + dealer_type);
                             Log.d(TAG, "discount: " + discount);
                         }

                        mRegistrationReportArrayList.add(i, registrationReport);
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mSelectedItemId == 0) {
                                SortableRegReportSkymediaTableView sortableRegReportSkymediaTableView = new SortableRegReportSkymediaTableView(getActivity());
                                mReportTableViewContainer.removeAllViews();
                                mReportTableViewContainer.addView(sortableRegReportSkymediaTableView);
                                sortableRegReportSkymediaTableView.setColumnCount(getResources().getInteger(R.integer.skymedia_report_column));
                                sortableRegReportSkymediaTableView.setHeaderBackgroundColor(Color.TRANSPARENT);
                                sortableRegReportSkymediaTableView.setDataAdapter(new RegReportSkymediaAdapter(getActivity(), mRegistrationReportArrayList));
                            } else {
                                SortableRegReportDealerTableView sortableRegReportDealerTableView = new SortableRegReportDealerTableView(getActivity());
                                mReportTableViewContainer.removeAllViews();
                                mReportTableViewContainer.addView(sortableRegReportDealerTableView);
                                sortableRegReportDealerTableView.setColumnCount(getResources().getInteger(R.integer.dealer_report_column));
                                sortableRegReportDealerTableView.setHeaderBackgroundColor(Color.TRANSPARENT);
                                sortableRegReportDealerTableView.setDataAdapter(new RegReportDealerAdapter(getActivity(), mRegistrationReportArrayList));
                            }
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
