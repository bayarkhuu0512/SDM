package com.skytel.sdm.ui.service;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.skytel.sdm.adapter.ServiceReportHandsetChangeAdapter;
import com.skytel.sdm.adapter.ServiceReportVasAdapter;
import com.skytel.sdm.database.DataManager;
import com.skytel.sdm.entities.RegistrationReport;
import com.skytel.sdm.entities.ServiceReport;
import com.skytel.sdm.network.HttpClient;
import com.skytel.sdm.ui.registration.RegistrationReportFilterActivity;
import com.skytel.sdm.ui.registration.SortableRegReportDealerTableView;
import com.skytel.sdm.ui.registration.SortableRegReportSkymediaTableView;
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

public class ServiceReportFragment extends Fragment implements Constants{
    String TAG = ServiceReportFragment.class.getName();

    private OkHttpClient mClient;
    private Context mContext;
    private DataManager mDataManager;
    private PrefManager mPrefManager;
    private List<ServiceReport> mServiceReportArrayList;
    private CustomProgressDialog mProgressDialog;

    private RelativeLayout mReportTableViewContainer;

    private Button mButtonFilter;

    private Spinner mReportTypeSpinner;

    private int mSelectedFilterButton = FILTER_ALL;
    private int mSelectedItemId = -1;
    private String[] mReportTypeValue = null;

    private int mYear;
    private int mMonth;
    private int mDay;

    private int fromPage = 0;
    private int numRow = 100;

    private final Calendar mCalendar = Calendar.getInstance();

    public ServiceReportFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.service_report, container, false);
        mContext = getActivity();
        mDataManager = new DataManager(mContext);
        mClient = HttpClient.getInstance();
        mPrefManager = new PrefManager(mContext);
        mServiceReportArrayList = new ArrayList<>();
        mProgressDialog = new CustomProgressDialog(getActivity());

        mReportTableViewContainer = (RelativeLayout) rootView.findViewById(R.id.reportTableViewContainer);
        mButtonFilter = (Button) rootView.findViewById(R.id.btn_filter);
        mButtonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), ServiceReportFilterActivity.class);
                startActivityForResult(myIntent, 1);
            }
        });

        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);

        mReportTypeSpinner = (Spinner) rootView.findViewById(R.id.choose_service_report_type);
        ArrayAdapter<CharSequence> adapterReport = ArrayAdapter.createFromResource(getActivity(), R.array.service_report_type, android.R.layout.simple_spinner_item);
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
                     //   setHasOptionsMenu(true);
                        mButtonFilter.setVisibility(View.VISIBLE);
                        DateTime currentDateJoda = DateTime.parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                        String startDate = new SimpleDateFormat("yyyy-MM-dd").format(currentDateJoda.minusMonths(3).toDate());
                        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd").format(currentDateJoda.toDate());

                        runServiceReportFunction(mSelectedItemId,mSelectedFilterButton, startDate, currentDateTime,"");
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

    public void runServiceReportFunction(final int report_type, int order_status, String start_date, String end_date, String phone) throws Exception {
        mProgressDialog.show();
        final StringBuilder url = new StringBuilder();
        url.append(Constants.SERVER_URL);
        url.append(Constants.FUNCTION_SERVICE_REPORT);
        url.append("?service_status=" + order_status);
        url.append("&len=" + numRow);
        url.append("&from=" + fromPage);
        url.append("&service_type=" + report_type);
        url.append("&start_date=" + start_date);
        url.append("&end_date=" + end_date);
        url.append("&phone=" + phone);


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
                                MainActivity.sCurrentScreen = Constants.MENU_NEWNUMBER;
                                mPrefManager.setIsLoggedIn(false);
                                mDataManager.resetCardTypes();

                                getActivity().finish();
                                Intent intent = new Intent(mContext, LoginActivity.class);
                                startActivity(intent);


                            }
                        });
                    }


                    JSONArray jArray = jsonObj.getJSONArray("vas_transactions");

                    Log.d(TAG, "*****JARRAY*****" + jArray.length());
                    mServiceReportArrayList.clear();
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jsonData = jArray.getJSONObject(i);


                        String date = jsonData.getString("date");
                        String order_status = jsonData.getString("order_status");
                        String service_type = jsonData.getString("service_type");
                        String phone = jsonData.getString("phone");

                        String comment = "";
                        if(!jsonData.isNull("operator_comment")){
                            comment = jsonData.getString("operator_comment");
                        }

                        ServiceReport serviceReport = new ServiceReport();
                        serviceReport.setId(i);
                        serviceReport.setPhone(phone);
                        serviceReport.setOrderStatus(order_status);
                        serviceReport.setServiceType(service_type);
                        serviceReport.setDate(date);
                        serviceReport.setComment(comment);


                        Log.d(TAG, "INDEX:       " + i);

                        Log.d(TAG, "phone: " + phone);
                        Log.d(TAG, "order_status: " + order_status);
                        Log.d(TAG, "service_type: " + service_type);
                        Log.d(TAG, "date: " + date);
                        Log.d(TAG, "comment: " + comment);
                        //Handset change
                        if(report_type == 0){
                            String sim_serial = jsonData.getString("sim_serial");
                            serviceReport.setSimcardSerial(sim_serial);
                            Log.d(TAG, "sim_serial: " + sim_serial);

                        }
                        //VAS
                        else{
                            String is_activation  = jsonData.getString("is_activation");
                            is_activation = (is_activation == "true") ? mContext.getString(R.string.active) : mContext.getString(R.string.decline);
                            serviceReport.setIsActivation(is_activation);
                            Log.d(TAG, "is_activation: " + is_activation);
                        }


                        mServiceReportArrayList.add(i, serviceReport);
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mSelectedItemId == 0) {
                                SortableServiceReportHandsetChangeTableView sortableServiceReportHandsetChangeTableView = new SortableServiceReportHandsetChangeTableView(getActivity());
                                mReportTableViewContainer.removeAllViews();
                                mReportTableViewContainer.addView(sortableServiceReportHandsetChangeTableView);
                                sortableServiceReportHandsetChangeTableView.setColumnCount(getResources().getInteger(R.integer.handset_change_report_column));
                                sortableServiceReportHandsetChangeTableView.setHeaderBackgroundColor(Color.TRANSPARENT);
                                sortableServiceReportHandsetChangeTableView.setDataAdapter(new ServiceReportHandsetChangeAdapter(getActivity(), mServiceReportArrayList));
                            } else {
                                SortableServiceReportVasTableView sortableServiceReportVasTableView = new SortableServiceReportVasTableView(getActivity());
                                mReportTableViewContainer.removeAllViews();
                                mReportTableViewContainer.addView(sortableServiceReportVasTableView);
                                sortableServiceReportVasTableView.setColumnCount(getResources().getInteger(R.integer.vas_report_column));
                                sortableServiceReportVasTableView.setHeaderBackgroundColor(Color.TRANSPARENT);
                                sortableServiceReportVasTableView.setDataAdapter(new ServiceReportVasAdapter(getActivity(), mServiceReportArrayList));
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

/*    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.filter_action,menu);
        for (int j = 0; j < menu.size(); j++) {
            MenuItem item = menu.getItem(j);
            Log.d(TAG, "set flag for " + item.getTitle());
            //  item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_menu:
                Intent myIntent = new Intent(getActivity(), ServiceReportFilterActivity.class);
                startActivityForResult(myIntent, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {

                int order_status = data.getIntExtra("order_status",0);
                String phone_number = data.getStringExtra("phone_number");
                String start_date = data.getStringExtra("start_date");
                String end_date = data.getStringExtra("end_date");
                mSelectedItemId = (int) mReportTypeSpinner.getSelectedItemId();

                try {
                    mProgressDialog.show();
                    runServiceReportFunction(mSelectedItemId, order_status, start_date, end_date,phone_number);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
