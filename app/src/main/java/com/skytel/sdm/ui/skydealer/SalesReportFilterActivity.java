package com.skytel.sdm.ui.skydealer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.skytel.sdm.R;
import com.skytel.sdm.adapter.NothingSelectedSpinnerAdapter;
import com.skytel.sdm.adapter.SalesReportCardTypeAdapter;
import com.skytel.sdm.database.DataManager;
import com.skytel.sdm.entities.CardType;
import com.skytel.sdm.utils.Constants;
import com.skytel.sdm.utils.CustomProgressDialog;
import com.skytel.sdm.utils.ValidationChecker;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SalesReportFilterActivity extends Activity implements Constants {

    String TAG = SalesReportFilterActivity.class.getName();
    private CustomProgressDialog mProgressDialog;
    private DataManager mDataManager;
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
    private int mSelectedFilterButton = FILTER_SUCCESS;
    private boolean isSuccessFilter = true;

    private int mSelectedItemId = -1;


    private int mYear;
    private int mMonth;
    private int mDay;
    private final Calendar mCalendar = Calendar.getInstance();

    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_sales_report);

        context = this;
        mProgressDialog = new CustomProgressDialog(context);
        mDataManager = new DataManager(context);

        mSearch = (Button) findViewById(R.id.search);
        mSearch.setOnClickListener(searchOnClick);

        mRefresh = (Button) findViewById(R.id.refresh);
        mRefresh.setOnClickListener(searchOnClick);

        /*mFilterByAll = (Button) rootView.findViewById(R.id.filterByAll);
        mFilterByAll.setOnClickListener(filterByAllOnClick);*/

        mFilterBySuccess = (Button) findViewById(R.id.filterBySuccess);
        mFilterBySuccess.setOnClickListener(filterBySuccessOnClick);

        mFilterByFailed = (Button) findViewById(R.id.filterByFailed);
        mFilterByFailed.setOnClickListener(filterByFailedOnClick);

        mFilterByPhoneNumber = (EditText) findViewById(R.id.filterByOrderedNumber);
        mFilterByEndDate = (EditText) findViewById(R.id.filterByEndDate);
        mFilterByStartDate = (EditText) findViewById(R.id.filterByStartDate);
        mFilterByUnitPackage = (Spinner) findViewById(R.id.filterByUnitPackage);
        mTextUnitPackage = (TextView) findViewById(R.id.txt_unit_type);


        final List<CardType> cardTypes = mDataManager.getCardTypes();
        SalesReportCardTypeAdapter adapterFilter = new SalesReportCardTypeAdapter(context, R.layout.sales_report_card_types_item, cardTypes);
        mFilterByUnitPackage.setAdapter(new NothingSelectedSpinnerAdapter(adapterFilter,
                R.layout.spinner_row_nothing_selected,
                context));
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
                Toast.makeText(context,"Nothing Selected",Toast.LENGTH_SHORT).show();
            }
        });

        DateTime currentDateJoda = DateTime.parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        String startDate = new SimpleDateFormat("yyyy-MM-dd").format(currentDateJoda.minusMonths(3).toDate());
        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd").format(currentDateJoda.toDate());

        mFilterByStartDate.setText(startDate);
        mFilterByEndDate.setText(currentDateTime);

        mFilterButtonByStartDate = (Button)findViewById(R.id.btn_start_date);
        mFilterButtonByStartDate.setOnClickListener(filterByStartDateOnClick);
        mFilterButtonByEndDate = (Button) findViewById(R.id.btn_end_date);
        mFilterButtonByEndDate.setOnClickListener(filterByEndDateOnClick);

        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);


    }


    View.OnClickListener searchOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                String phone_number = mFilterByPhoneNumber.getText().toString();
                Boolean isSuccess = isSuccessFilter;

                String start_date = mFilterByStartDate.getText().toString();
                String end_date = mFilterByEndDate.getText().toString();
                if (ValidationChecker.isSelected(mSelectedItemId)) {
                    if(ValidationChecker.isOnInterval(Days.daysBetween(DateTime.parse(start_date), DateTime.parse(end_date)).getDays())){
                        mProgressDialog.show();

                        Intent intent = new Intent();
                        intent.putExtra("phone_number", phone_number);
                        intent.putExtra("is_success", isSuccess);
                        intent.putExtra("start_date", start_date);
                        intent.putExtra("end_date", end_date);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                    else{
                        Toast.makeText(context, getText(R.string.interval_error), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(context, getText(R.string.choose_report_type), Toast.LENGTH_SHORT).show();
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

    @Override
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
    }

}
