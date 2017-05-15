package com.skytel.sdm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.MaskFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.skytel.sdm.adapter.NothingSelectedSpinnerAdapter;
import com.skytel.sdm.database.DataManager;
import com.skytel.sdm.entities.Phonenumber;
import com.skytel.sdm.network.HttpClient;
import com.skytel.sdm.utils.BitmapSaver;
import com.skytel.sdm.utils.ConfirmDialog;
import com.skytel.sdm.utils.Constants;
import com.skytel.sdm.utils.CustomProgressDialog;
import com.skytel.sdm.utils.PrefManager;
import com.skytel.sdm.utils.Utility;
import com.skytel.sdm.utils.ValidationChecker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class NumberUserInfoActivity extends AppCompatActivity implements Constants {
    String TAG = NumberUserInfoActivity.class.getName();

    private String mReservationId;
    private String mRegisterNumber;
    private String mPhoneNumber;

    private EditText mLastName;
    private EditText mFirstName;
    private EditText mRegistrationNumberEt;
    private EditText mSimCardSerial;
    private Spinner mHobby;
    private Spinner mJob;
    private EditText mContactNumber;
    private EditText mDescription;
    private EditText mChosenNumber;

    private Button mOrderUserInfo;

    private ImageView mFrontImage;
    private ImageView mBackImage;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChosenTask;
    private boolean isFirst = true;

    private String imageFront = "image_front.png";
    private String imageBack = "image_back.png";

    private Bitmap bm = null;

    private PrefManager mPrefManager;
    private OkHttpClient mClient;
    private ConfirmDialog mConfirmDialog;
    private CustomProgressDialog mProgressDialog;
    private Context mContext;
    private DataManager mDataManager;

    private int mSelectedHobbyId = -1;
    private int mSelectedJobId = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newuserinfo);

        mPrefManager = new PrefManager(this);
        mClient = HttpClient.getInstance();
        mContext = this;
        mDataManager = new DataManager(mContext);

        mConfirmDialog = new ConfirmDialog();
        Bundle args = new Bundle();
        args.putInt("message", R.string.confirm);
        args.putInt("title", R.string.confirm);

        mConfirmDialog.setArguments(args);
        mConfirmDialog.registerCallback(dialogConfirmListener);
        mProgressDialog = new CustomProgressDialog(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mReservationId = extras.getString("reservation_id");
            mRegisterNumber = extras.getString("register_number");
            mPhoneNumber = extras.getString("phone_number");

            Log.d(TAG, "Reservation ID: " + mReservationId);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mLastName = (EditText) findViewById(R.id.last_name);
        mFirstName = (EditText) findViewById(R.id.first_name);
        mRegistrationNumberEt = (EditText) findViewById(R.id.reg_number);
        mSimCardSerial = (EditText) findViewById(R.id.sim_card_serial);
        mHobby = (Spinner) findViewById(R.id.hobby);
        mJob = (Spinner) findViewById(R.id.job);
        mContactNumber = (EditText) findViewById(R.id.contact_number);
        mDescription = (EditText) findViewById(R.id.description_order);
        mChosenNumber = (EditText) findViewById(R.id.chosen_number);

        mFrontImage = (ImageView) findViewById(R.id.img_front);
        mBackImage = (ImageView) findViewById(R.id.img_back);

        mOrderUserInfo = (Button) findViewById(R.id.send_order);

        ArrayAdapter<CharSequence> adapterHobbyReport = ArrayAdapter.createFromResource(this, R.array.hobby_list, android.R.layout.simple_spinner_item);
        adapterHobbyReport.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mHobby.setAdapter(new NothingSelectedSpinnerAdapter(adapterHobbyReport,
                R.layout.spinner_row_nothing_selected,
                // R.layout.contact_spinner_nothing_selected_dropdown, // Option
                this));
        mHobby.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    mSelectedHobbyId = (int) mHobby.getSelectedItemId();
                    Log.d(TAG, "selected item id: "+mSelectedHobbyId);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> adapterJobReport = ArrayAdapter.createFromResource(this, R.array.job_list, android.R.layout.simple_spinner_item);
        adapterJobReport.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mJob.setAdapter(new NothingSelectedSpinnerAdapter(adapterJobReport,
                R.layout.spinner_row_nothing_selected,
                // R.layout.contact_spinner_nothing_selected_dropdown, // Option
                this));
        mJob.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    mSelectedJobId = (int) mJob.getSelectedItemId();
                    Log.d(TAG, "selected item id: "+mSelectedJobId);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mOrderUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ValidationChecker.isValidationPassed(mLastName) && ValidationChecker.isValidationPassed(mFirstName) && ValidationChecker.isValidationPassed(mRegistrationNumberEt) &&
                        ValidationChecker.isValidationPassed(mSimCardSerial) &&  ValidationChecker.isSelected((int )mHobby.getSelectedItemId()) &&  ValidationChecker.isSelected((int )mJob.getSelectedItemId()) &&
                        ValidationChecker.isValidationPassed(mContactNumber) && ValidationChecker.isValidationPassed(mChosenNumber) && ValidationChecker.hasBitmapValue(bm)) {
                    if(ValidationChecker.isSimcardSerial(mSimCardSerial.length())) {
                        mConfirmDialog.show(getFragmentManager(), "dialog");
                    }else{
                        Toast.makeText(mContext, getString(R.string.check_sim_serial), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(mContext, getString(R.string.please_fill_the_field), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mRegistrationNumberEt.setText(mRegisterNumber);
        mChosenNumber.setText(mPhoneNumber);

        mFrontImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFirst = true;
                selectImage();
            }
        });
        mBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFirst = false;
                selectImage();
            }
        });

    }

    final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    private void runSendUserInfoDetailInfo() throws Exception {
        final StringBuilder url = new StringBuilder();
        url.append(Constants.SERVER_URL);
        url.append(Constants.FUNCTION_NEW_NUMBER_DETAIL);
        url.append("?reservation_id=" + mReservationId);
        url.append("&last_name=" + mLastName.getText().toString());
        url.append("&first_name=" + mFirstName.getText().toString());
        url.append("&sim_serial=" + mSimCardSerial.getText().toString());
        url.append("&hobby=" + String.valueOf(mSelectedHobbyId));
        url.append("&work=" + String.valueOf(mSelectedJobId));
        url.append("&contact=" + mContactNumber.getText().toString());
        url.append("&description=" + mDescription.getText().toString());

        Log.d(TAG, "sedn URL: " + url.toString());

        System.out.print(url + "\n");
        System.out.println(mPrefManager.getAuthToken());

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("photo1_path", imageFront,
                        RequestBody.create(MEDIA_TYPE_PNG, BitmapSaver.imageFile(mContext, imageFront)))
                .addFormDataPart("photo2_path", imageBack,
                        RequestBody.create(MEDIA_TYPE_PNG, BitmapSaver.imageFile(mContext, imageBack))
                ).build();
        Request request = new Request.Builder()
                .url(url.toString())
                .addHeader(Constants.PREF_AUTH_TOKEN, mPrefManager.getAuthToken())
                .post(requestBody)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mProgressDialog.dismiss();
                System.out.println("onFailure");
                e.printStackTrace();

                Toast.makeText(mContext, getResources().getString(R.string.check_internet_connection), Toast.LENGTH_LONG).show();
                // Used for debug

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mProgressDialog.dismiss();

                System.out.println("onResponse");

                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                String resp = response.body().string();
                System.out.println("resp " + resp);

                try {
                    JSONObject jsonObj = new JSONObject(resp);
                    int result_code = jsonObj.getInt("result_code");
                    final String result_msg = jsonObj.getString("result_msg");

                    Log.d(TAG, "result_code " + result_code);
                    Log.d(TAG, "result_msg " + result_msg);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "" + result_msg, Toast.LENGTH_SHORT).show();
                        }
                    });

                    if (result_code == RESULT_CODE_SUCCESS) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new CountDownTimer(2000, 1000) {

                                    public void onTick(long millisUntilFinished) {
                                    }

                                    public void onFinish() {
                                        finish();
                                    }
                                }.start();
                            }
                        });
                    }

                    else if (result_code == Constants.RESULT_CODE_UNREGISTERED_TOKEN) {

                                MainActivity.sCurrentScreen = Constants.MENU_NEWNUMBER;
                                mPrefManager.setIsLoggedIn(false);
                                mDataManager.resetCardTypes();

                                finish();
                                Intent intent = new Intent(mContext, LoginActivity.class);
                                startActivity(intent);

                    }


                } catch (JSONException e) {
                    Toast.makeText(mContext, getResources().getString(R.string.error_result), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChosenTask.equals(getString(R.string.take_photo)))
                        cameraIntent();
                    else if (userChosenTask.equals(getString(R.string.choose_from_library)))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {getString(R.string.take_photo), getString(R.string.choose_from_library),
                getString(R.string.cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(getString(R.string.add_photo));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(mContext);

                if (items[item].equals(getString(R.string.take_photo))) {
                    userChosenTask = getString(R.string.take_photo);
                    if (result) {
                        mProgressDialog.show();
                        cameraIntent();
                    }

                } else if (items[item].equals(getString(R.string.choose_from_library))) {
                    userChosenTask = getString(R.string.choose_from_library);
                    if (result) {
                        mProgressDialog.show();
                        galleryIntent();
                    }

                } else if (items[item].equals(getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_file)), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        } else {
            mProgressDialog.dismiss();
        }
    }

    private void onCaptureImageResult(Intent data) {
        bm = (Bitmap) data.getExtras().get("data");
        if (isFirst) {
            mFrontImage.setImageBitmap(bm);
            BitmapSaver.saveBitmapToFile(mContext,bm, imageFront);
        } else {
            mBackImage.setImageBitmap(bm);
            BitmapSaver.saveBitmapToFile(mContext,bm, imageBack);
        }
        mProgressDialog.dismiss();
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (isFirst) {
            mFrontImage.setImageBitmap(bm);
            BitmapSaver.saveBitmapToFile(mContext,bm, imageFront);

        } else {
            mBackImage.setImageBitmap(bm);
            BitmapSaver.saveBitmapToFile(mContext,bm, imageBack);

        }
        mProgressDialog.dismiss();

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private ConfirmDialog.OnDialogConfirmListener dialogConfirmListener = new ConfirmDialog.OnDialogConfirmListener() {

        @Override
        public void onPositiveButton() {
            try {
                mProgressDialog.show();
                runSendUserInfoDetailInfo();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                mProgressDialog.dismiss();
                Toast.makeText(mContext, "Error on Failure!", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onNegativeButton() {

        }
    };

}
