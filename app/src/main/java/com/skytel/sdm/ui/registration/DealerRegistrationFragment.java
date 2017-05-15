package com.skytel.sdm.ui.registration;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.skytel.sdm.LoginActivity;
import com.skytel.sdm.MainActivity;
import com.skytel.sdm.R;
import com.skytel.sdm.adapter.DealerChannelTypeAdapter;
import com.skytel.sdm.adapter.NothingSelectedSpinnerAdapter;
import com.skytel.sdm.adapter.SalesReportCardTypeAdapter;
import com.skytel.sdm.adapter.SalesReportChargeCardAdapter;
import com.skytel.sdm.adapter.SalesReportPostPaidPaymentAdapter;
import com.skytel.sdm.database.DataManager;
import com.skytel.sdm.entities.CardType;
import com.skytel.sdm.entities.DealerChannelType;
import com.skytel.sdm.entities.SalesReport;
import com.skytel.sdm.network.HttpClient;
import com.skytel.sdm.ui.skydealer.SortableSalesReportChargeCardTableView;
import com.skytel.sdm.ui.skydealer.SortableSalesReportPostPaidPaymentTableView;
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
import java.util.ArrayList;
import java.util.List;

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

public class DealerRegistrationFragment extends Fragment implements Constants {


    String TAG = DealerRegistrationFragment.class.getName();
    private CustomProgressDialog mProgressDialog;
    private Context mContext;
    private PrefManager mPrefManager;
    private OkHttpClient mClient;
    private List<DealerChannelType> mDealerChannelType;
    private ConfirmDialog mConfirmDialog;
    private DataManager mDataManager;
    private Spinner mChannelSalesType;

    private EditText mDiscountPercent;
    private EditText mLastName;
    private EditText mFirstName;
    private EditText mRegNumber;
    private EditText mCardSellAddress;
    private EditText mSkydealerNumber;
    private EditText mContactNumber;
    private EditText mOrderDesc;

    private Button mSendOrder;

    private ImageView mFrontImage;
    private ImageView mBackImage;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChosenTask;
    private boolean isFirst = true;

    private String imageFront = "image_front.png";
    private String imageBack = "image_back.png";

    private String mChosenDealerTypeCode = null;

    private Bitmap bm = null;
    private static String skydealerNumber = "";

    public DealerRegistrationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dealer_registration, container, false);

        mContext = getActivity();
        mDataManager = new DataManager(mContext);
        mProgressDialog = new CustomProgressDialog(getActivity());
        mPrefManager = new PrefManager(mContext);
        mClient = HttpClient.getInstance();
        mDealerChannelType = new ArrayList<>();

        mConfirmDialog = new ConfirmDialog();
        Bundle args = new Bundle();
        args.putInt("message", R.string.confirm);
        args.putInt("title", R.string.confirm);
        mConfirmDialog.setArguments(args);
        mConfirmDialog.registerCallback(dialogConfirmListener);

        mChannelSalesType = (Spinner) rootView.findViewById(R.id.choose_chanel_sales_type);
        mDiscountPercent = (EditText) rootView.findViewById(R.id.discount_precent);
        mLastName = (EditText) rootView.findViewById(R.id.last_name);
        mFirstName = (EditText) rootView.findViewById(R.id.first_name);
        mContactNumber = (EditText) rootView.findViewById(R.id.contact_number);
        mCardSellAddress = (EditText) rootView.findViewById(R.id.address_sell_card);
        mSkydealerNumber = (EditText) rootView.findViewById(R.id.skydealer_number);
        mSendOrder = (Button) rootView.findViewById(R.id.send_order);
        mRegNumber = (EditText) rootView.findViewById(R.id.reg_number);
        mOrderDesc = (EditText) rootView.findViewById(R.id.order_desc);


        mFrontImage = (ImageView) rootView.findViewById(R.id.img_front);
        mBackImage = (ImageView) rootView.findViewById(R.id.img_back);

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

        mSendOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ValidationChecker.isValidationPassed(mLastName) && ValidationChecker.isValidationPassed(mFirstName) && ValidationChecker.isValidationPassed(mRegNumber) &&
                        ValidationChecker.isValidationPassed(mCardSellAddress) && ValidationChecker.isSelected(mChannelSalesType)  &&
                        ValidationChecker.isValidationPassed(mContactNumber)) {
                    if(ValidationChecker.hasBitmapValue(bm)){
                        mConfirmDialog.show(getFragmentManager(), "dialog");
                    }else{
                        Toast.makeText(mContext, getResources().getString(R.string.please_insert_pic), Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(mContext, getResources().getString(R.string.please_fill_the_field), Toast.LENGTH_SHORT).show();
                }
            }
        });


        try {
            mProgressDialog.show();
            runGetDealerChannelInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return rootView;
    }

    public void runGetDealerChannelInfo() throws Exception {

        final StringBuilder url = new StringBuilder();
        url.append(Constants.SERVER_URL);
        url.append(Constants.FUNCTION_DEALER_CHANNEL_INFO);


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
                    final String  result_msg = jsonObj.getString("result_msg");

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


                    JSONArray jArray = jsonObj.getJSONArray("dealer_channel_types");

                    Log.d(TAG, "*****JARRAY*****" + jArray.length());
                    mDealerChannelType.clear();
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jsonData = jArray.getJSONObject(i);

                        String type_name = jsonData.getString("type_name");
                        String type_code = jsonData.getString("type_code");
                        String discount = jsonData.getString("discount");

                        Log.d(TAG, "INDEX:       " + i);

                        Log.d(TAG, "type_name: " + type_name);
                        Log.d(TAG, "type_code: " + type_code);
                        Log.d(TAG, "discount: " + discount);


                        DealerChannelType dealerChannelType = new DealerChannelType();
                        // DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                        dealerChannelType.setId(i);
                        dealerChannelType.setTypeCode(type_code);
                        dealerChannelType.setTypeName(type_name);
                        dealerChannelType.setDiscount(discount);


                        mDealerChannelType.add(i, dealerChannelType);
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final List<DealerChannelType> dealerChannelType = mDealerChannelType;
                            DealerChannelTypeAdapter adapterFilter = new DealerChannelTypeAdapter(getActivity(), R.layout.dealer_channel_type_item, mDealerChannelType);
                            mChannelSalesType.setAdapter(new NothingSelectedSpinnerAdapter(adapterFilter,
                                    R.layout.spinner_row_nothing_selected,
                                    getActivity()));
                            mChannelSalesType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                    try {
                                        mChosenDealerTypeCode = dealerChannelType.get(position - 1).getTypeCode().toString();
                                        mDiscountPercent.setText(dealerChannelType.get(position - 1).getDiscount().toString());
                                        Log.d(TAG, "Dealer channel type discount percent: " + dealerChannelType.get(position - 1).getDiscount()+"%");
                                        Log.d(TAG, "Dealer channel type code : " + dealerChannelType.get(position - 1).getTypeCode().toString());
                                       if( dealerChannelType.get(position - 1).getId() == 0){
                                           mSkydealerNumber.setVisibility(View.GONE);
                                           mSkydealerNumber.setText("0");
                                       }
                                        else{
                                           mSkydealerNumber.setVisibility(View.VISIBLE);

                                       }





                                    } catch (ArrayIndexOutOfBoundsException e) {

                                    }

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                    Toast.makeText(getActivity(), "Nothing Selected", Toast.LENGTH_SHORT).show();
                                }
                            });
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

    final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    public void runSendOrder() throws Exception {

        final StringBuilder url = new StringBuilder();
        url.append(Constants.SERVER_URL);
        url.append(Constants.FUNCTION_NEW_DEALER);
/*
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "send URL: "+url.toString());
                if(mChosenDealerTypeCode.toString() == "b"){

                    skydealerNumber = "0";
                }
                else{
                    skydealerNumber = mSkydealerNumber.getText().toString();
                }
                Log.d(TAG, "Skydealer number: "+skydealerNumber);
            }
        });
*/
        System.out.print(url + "\n");
        System.out.println(mPrefManager.getAuthToken());

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)

                .addFormDataPart("phone",mSkydealerNumber.getText().toString())
                .addFormDataPart("last_name", mLastName.getText().toString())
                .addFormDataPart("first_name", mFirstName.getText().toString())
                .addFormDataPart("register", mRegNumber.getText().toString())
                .addFormDataPart("address", mCardSellAddress.getText().toString())
                .addFormDataPart("sales_type", mChosenDealerTypeCode)
                .addFormDataPart("contact", mContactNumber.getText().toString())
                .addFormDataPart("description", mOrderDesc.getText().toString())
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
                            mSkydealerNumber.setVisibility(View.VISIBLE);
                            // Used for debug
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        mCardSellAddress.setText("");
                        mChannelSalesType.setSelection(-1);
                        mLastName.setText("");
                        mFirstName.setText("");
                        mRegNumber.setText("");
                        mDiscountPercent.setText("");
                        mSkydealerNumber.setText("");
                        mOrderDesc.setText("");

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

                } else if (items[item].equals( getString(R.string.choose_from_library))) {
                    userChosenTask =  getString(R.string.choose_from_library);
                    if (result) {
                        mProgressDialog.show();
                        galleryIntent();
                    }

                } else if (items[item].equals( getString(R.string.cancel))) {
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
        startActivityForResult(Intent.createChooser(intent,  getString(R.string.select_file)), SELECT_FILE);
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
    private ConfirmDialog.OnDialogConfirmListener dialogConfirmListener = new ConfirmDialog.OnDialogConfirmListener() {

        @Override
        public void onPositiveButton() {
            try {
                mProgressDialog.show();

                runSendOrder();
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
