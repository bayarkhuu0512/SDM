package com.skytel.sdm.ui.newnumber;


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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.skytel.sdm.LoginActivity;
import com.skytel.sdm.MainActivity;
import com.skytel.sdm.R;
import com.skytel.sdm.adapter.NewNumberReportAdapter;
import com.skytel.sdm.database.DataManager;
import com.skytel.sdm.entities.NewNumberReport;
import com.skytel.sdm.network.HttpClient;
import com.skytel.sdm.utils.Constants;
import com.skytel.sdm.utils.CustomProgressDialog;
import com.skytel.sdm.utils.PrefManager;

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

/**
 * Created by Altanchimeg on 4/18/2016.
 */
public class NumberOrderReportFragment extends Fragment implements Constants {
    String TAG = NumberOrderReportFragment.class.getName();

    private OkHttpClient mClient;
    private Context mContext;
    private DataManager mDataManager;
    private PrefManager mPrefManager;

    private List<NewNumberReport> mNewNumberReportArrayList;
    private CustomProgressDialog mProgressDialog;

    private Button mButtonFilter;

    private RelativeLayout mReportTableViewContainer;

    private int mSelectedFilterButton = FILTER_ALL;

    private int mSelectedItemId = -1;
    private String[] mReportTypeValue = null;

    private int fromPage = 0;
    private int numRow = 100;

    private int mYear;
    private int mMonth;
    private int mDay;
    String startDate;
    String currentDateTime;

    private final Calendar mCalendar = Calendar.getInstance();

    public NumberOrderReportFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.number_order_report, container, false);

        mContext = getActivity();
        mDataManager = new DataManager(mContext);
        mClient = HttpClient.getInstance();

        mPrefManager = new PrefManager(mContext);
        mNewNumberReportArrayList = new ArrayList<>();
        mProgressDialog = new CustomProgressDialog(getActivity());

        mReportTableViewContainer = (RelativeLayout) rootView.findViewById(R.id.reportTableViewContainer);
        mButtonFilter = (Button) rootView.findViewById(R.id.btn_filter);

        mButtonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), NumberOrderReportFilterActivity.class);
                startActivityForResult(myIntent, 1);
            }
        });

        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);

        try {
            mProgressDialog.show();

            DateTime currentDateJoda = DateTime.parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            startDate = new SimpleDateFormat("yyyy-MM-dd").format(currentDateJoda.minusMonths(3).toDate());
            currentDateTime = new SimpleDateFormat("yyyy-MM-dd").format(currentDateJoda.toDate());

            runNewNumberReportFunction(fromPage, mSelectedFilterButton, "", startDate, currentDateTime);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }



    /**
     * @param from
     * @param order_status
     * @param phone
     * @param start_date
     * @param end_date
     * @throws Exception
     */
    public void runNewNumberReportFunction(int from, int order_status, String phone, String start_date, String end_date) throws Exception {
        mProgressDialog.show();
        final StringBuilder url = new StringBuilder();
        url.append(Constants.SERVER_URL);
        url.append(Constants.FUNCTION_NEW_NUMBER_REPORT);
        url.append("?len=" + numRow); //100,
        url.append("&from=" + from);
        url.append("&order_status=" + order_status);
        url.append("&phone=" + phone);
        url.append("&start_date=" + start_date);
        url.append("&end_date=" + end_date);


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "send URL: " + url.toString());
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

                            Toast.makeText(mContext, "" + result_msg, Toast.LENGTH_LONG).show();
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



//                  Udaan hugatsaanii daraa app -iig neeh uyed end exception shidej bsan tul.. - Zolbayar
                    if (!jsonObj.isNull("reservations")) {

                        JSONArray jArray = jsonObj.getJSONArray("reservations");

                        Log.d(TAG, "*****JARRAY*****" + jArray.length());
                        mNewNumberReportArrayList.clear();
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jsonData = jArray.getJSONObject(i);

                            String date = jsonData.getString("date");
                            String order_status = jsonData.getString("order_status");
                            String number_type = jsonData.getString("number_type");
                            String unit_and_day = jsonData.getString("unit_and_day");
                            String price = jsonData.getString("price");
                            String number = jsonData.getString("number");
                            String comment = "";
                            if (!jsonData.isNull("operator_comment")) {
                                comment = jsonData.getString("operator_comment");
                            }

                            Log.d(TAG, "INDEX:       " + i);

                            Log.d(TAG, "date: " + date);
                            Log.d(TAG, "order_status: " + order_status);
                            Log.d(TAG, "number_type: " + number_type);
                            Log.d(TAG, "unit_and_day: " + unit_and_day);
                            Log.d(TAG, "price: " + price);
                            Log.d(TAG, "number: " + number);
                            Log.d(TAG, "comment: " + comment);

                            NewNumberReport newNumberReport = new NewNumberReport();
                            newNumberReport.setId(i);
                            newNumberReport.setOrderState(order_status);
                            newNumberReport.setNumberType(number_type);
                            newNumberReport.setUnitAndDay(unit_and_day);
                            newNumberReport.setPrice(price);
                            newNumberReport.setNumber(number);
                            newNumberReport.setDate(date);
                            newNumberReport.setComment(comment);

                            mNewNumberReportArrayList.add(i, newNumberReport);
                        }
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            SortableNewNumberReportTableView sortableNewNumberReportTableView = new SortableNewNumberReportTableView(getActivity());
                            mReportTableViewContainer.removeAllViews();
                            mReportTableViewContainer.addView(sortableNewNumberReportTableView);
                            sortableNewNumberReportTableView.setColumnCount(getResources().getInteger(R.integer.new_number_report_column));
                            sortableNewNumberReportTableView.setHeaderBackgroundColor(Color.TRANSPARENT);
                            sortableNewNumberReportTableView.setDataAdapter(new NewNumberReportAdapter(getActivity(), mNewNumberReportArrayList));

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

  /*  @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filter_action,menu);


        for (int j = 0; j < menu.size(); j++) {
            MenuItem item = menu.getItem(j);
            Log.d(TAG, "set flag for " + item.getTitle());
            //  item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        super.onCreateOptionsMenu(menu, inflater);


    }*/


  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_menu:
                Intent myIntent = new Intent(getActivity(), NumberOrderReportFilterActivity.class);
                startActivityForResult(myIntent, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {

                int order_status = data.getIntExtra("order_status",0);
                String phone_number = data.getStringExtra("phone_number");
                String start_date = data.getStringExtra("start_date");
                String end_date = data.getStringExtra("end_date");

                try {
                    mProgressDialog.show();
                    runNewNumberReportFunction(fromPage, order_status, phone_number, start_date, end_date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
