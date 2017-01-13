package com.skytel.sdm.ui.service;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.skytel.sdm.R;
import com.skytel.sdm.utils.Constants;
import com.skytel.sdm.utils.CustomProgressDialog;
import com.skytel.sdm.utils.ValidationChecker;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ServiceReportFilterActivity extends Activity implements Constants{
    private CustomProgressDialog mProgressDialog;
    private Context context;

    private Button mSearch;
    private Button mRefresh;
    private Button mFilterByAll;
    private Button mFilterBySuccess;
    private Button mFilterByFailed;
    private Button mFilterByWaiting;
    private EditText mFilterByPhoneNumber;
    private EditText mFilterByEndDate;
    private EditText mFilterByStartDate;

    private Button mFilterButtonByEndDate;
    private Button mFilterButtonByStartDate;

    private int mSelectedFilterButton = FILTER_ALL;
    private int mSelectedItemId = -1;
    private String[] mReportTypeValue = null;

    private int mYear;
    private int mMonth;
    private int mDay;

    private final Calendar mCalendar = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        context = this;
        mProgressDialog = new CustomProgressDialog(context);
        setContentView(R.layout.filter_service_report);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);

        mSearch = (Button) findViewById(R.id.search);
        mSearch.setOnClickListener(searchOnClick);

        mRefresh = (Button) findViewById(R.id.refresh);
        mRefresh.setOnClickListener(searchOnClick);

        mFilterByAll = (Button) findViewById(R.id.filterByAll);
        mFilterByAll.setOnClickListener(filterByAllOnClick);

        mFilterByWaiting = (Button) findViewById(R.id.filterByWaiting);
        mFilterByWaiting.setOnClickListener(filterByWaitingOnClick);

        mFilterBySuccess = (Button) findViewById(R.id.filterBySuccess);
        mFilterBySuccess.setOnClickListener(filterBySuccessOnClick);

        mFilterByFailed = (Button) findViewById(R.id.filterByFailed);
        mFilterByFailed.setOnClickListener(filterByFailedOnClick);

        mFilterByPhoneNumber = (EditText) findViewById(R.id.filterByOrderedNumber);
        mFilterByEndDate = (EditText) findViewById(R.id.filterByEndDate);
        mFilterByStartDate = (EditText) findViewById(R.id.filterByStartDate);

        mFilterButtonByStartDate = (Button) findViewById(R.id.btn_start_date);
        mFilterButtonByStartDate.setOnClickListener(filterByStartDateOnClick);
        mFilterButtonByEndDate = (Button) findViewById(R.id.btn_end_date);
        mFilterButtonByEndDate.setOnClickListener(filterByEndDateOnClick);

        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);

        DateTime currentDateJoda = DateTime.parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        String startDate = new SimpleDateFormat("yyyy-MM-dd").format(currentDateJoda.minusMonths(3).toDate());
        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd").format(currentDateJoda.toDate());

        mFilterByStartDate.setText(startDate);
        mFilterByEndDate.setText(currentDateTime);
    }
    View.OnClickListener searchOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {

                    mProgressDialog.show();
                    String phone_number = mFilterByPhoneNumber.getText().toString();
                    int order_status = mSelectedFilterButton;
                    String start_date = mFilterByStartDate.getText().toString();
                    String end_date = mFilterByEndDate.getText().toString();

                Intent intent = new Intent();
                intent.putExtra("phone_number", phone_number);
                intent.putExtra("order_status", order_status);
                intent.putExtra("start_date", start_date);
                intent.putExtra("end_date", end_date);
                setResult(Activity.RESULT_OK, intent);
                finish();

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
            DatePickerDialog dpd = new DatePickerDialog(context,
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
            DatePickerDialog dpd = new DatePickerDialog(context,
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
  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }*/
  @Override
  public boolean onNavigateUp() {
      finish();
      return true;
  }
}
