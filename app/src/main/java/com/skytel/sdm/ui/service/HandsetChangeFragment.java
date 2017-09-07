package com.skytel.sdm.ui.service;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.skytel.sdm.LoginActivity;
import com.skytel.sdm.MainActivity;
import com.skytel.sdm.R;
import com.skytel.sdm.adapter.HandsetChangeTypeAdapter;
import com.skytel.sdm.adapter.NothingSelectedSpinnerAdapter;
import com.skytel.sdm.database.DataManager;
import com.skytel.sdm.entities.HandsetChangeType;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.mindorks.paracamera.Camera.REQUEST_TAKE_PHOTO;



public class HandsetChangeFragment extends Fragment implements Constants {

    String TAG = HandsetChangeFragment.class.getName();
    private CustomProgressDialog mProgressDialog;
    private Context mContext;
    private PrefManager mPrefManager;
    private OkHttpClient mClient;
    private List<HandsetChangeType> mHandsetChangeType;
    private ConfirmDialog mConfirmDialog;
    private DataManager mDataManager;

    private Spinner mHandsetChangeTypeSpinner;
    private EditText mPhonenumber;
    private EditText mSimcardSerial;
    private TextView mPrice;

    private Button mSendOrder;

    private ImageView mFrontImage;
    private ImageView mBackImage;

    // private Camera camera;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChosenTask;
    private boolean isFirst = true;

    private String imageFront = "image_front.png";
    private String imageBack = "image_back.png";

    private Bitmap bm = null;

    private int mChosenHandsetChangeTypeId = -1;

    public HandsetChangeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.handset_change, container, false);
// Instantiate the camera
        mContext = getActivity();
        mProgressDialog = new CustomProgressDialog(getActivity());
        mPrefManager = new PrefManager(mContext);
        mClient = HttpClient.getInstance();
        mHandsetChangeType = new ArrayList<>();
        mDataManager = new DataManager(mContext);

        mConfirmDialog = new ConfirmDialog();
        Bundle args = new Bundle();
        args.putInt("message", R.string.confirm);
        args.putInt("title", R.string.confirm);
        mConfirmDialog.setArguments(args);
        mConfirmDialog.registerCallback(dialogConfirmListener);

        mHandsetChangeTypeSpinner = (Spinner) rootView.findViewById(R.id.choose_handset_change);
        mPhonenumber = (EditText) rootView.findViewById(R.id.phone_number);
        mSimcardSerial = (EditText) rootView.findViewById(R.id.sim_card_serial);
        mPrice = (TextView) rootView.findViewById(R.id.price);
        mSendOrder = (Button) rootView.findViewById(R.id.send_order);

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
            public void onClick(View v) {
                isFirst = false;
                selectImage();
            }
        });

        mSendOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ValidationChecker.isValidationPassed(mPhonenumber) && ValidationChecker.isValidationPassed(mSimcardSerial)
                        && ValidationChecker.hasBitmapValue(bm) && ValidationChecker.isSelected((int) mHandsetChangeTypeSpinner.getSelectedItemId())) {
                    if (ValidationChecker.isSimcardSerial(mSimcardSerial.length())) {
                        mConfirmDialog.show(getFragmentManager(), "dialog");
                    } else {
                        Toast.makeText(mContext, getString(R.string.check_sim_serial), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.please_fill_the_field), Toast.LENGTH_SHORT).show();
                }
            }
        });


        try {
            mProgressDialog.show();
            runGetHandsetChangeInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return rootView;
    }

    public void runGetHandsetChangeInfo() throws Exception {

        final StringBuilder url = new StringBuilder();
        url.append(Constants.SERVER_URL);
        url.append(Constants.FUNCTION_HANDSET_CHANGE_INFO);


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
            public void onFailure(Call call, final
            IOException e) {
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


                    JSONArray jArray = jsonObj.getJSONArray("types");

                    Log.d(TAG, "*****JARRAY*****" + jArray.length());
                    mHandsetChangeType.clear();
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jsonData = jArray.getJSONObject(i);

                        String type_name = jsonData.getString("type_name");
                        int price = jsonData.getInt("price");
                        int simchange_type_id = jsonData.getInt("id");

                        Log.d(TAG, "INDEX:       " + i);

                        Log.d(TAG, "type_name: " + type_name);
                        Log.d(TAG, "price: " + price);
                        Log.d(TAG, "sim change type id: " + simchange_type_id);


                        HandsetChangeType handsetChangeType = new HandsetChangeType();
                        // DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                        handsetChangeType.setId(i);
                        handsetChangeType.setPrice(price);
                        handsetChangeType.setTypeName(type_name);
                        handsetChangeType.setSimChangeTypeId(simchange_type_id);


                        mHandsetChangeType.add(i, handsetChangeType);
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final List<HandsetChangeType> handsetChangeTypes = mHandsetChangeType;
                            HandsetChangeTypeAdapter adapterFilter = new HandsetChangeTypeAdapter(getActivity(), R.layout.handset_change_type_item, mHandsetChangeType);
                            mHandsetChangeTypeSpinner.setAdapter(new NothingSelectedSpinnerAdapter(adapterFilter,
                                    R.layout.spinner_row_nothing_selected,
                                    getActivity()));
                            mHandsetChangeTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                    try {
                                        mChosenHandsetChangeTypeId = handsetChangeTypes.get(position - 1).getSimChangeTypeId();
                                        mPrice.setText(handsetChangeTypes.get(position - 1).getPrice() + "₮");
                                        Log.d(TAG, "Handset change price: " + mPrice.getText().toString());
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


                } catch (final JSONException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "Алдаатай хариу ирлээ " + e.toString(), Toast.LENGTH_LONG).show();
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
        url.append(Constants.FUNCTION_HANDSET_CHANGE);


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "send URL: " + url.toString());
            }
        });

        System.out.print(url + "\n");
        System.out.println(mPrefManager.getAuthToken());

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("phone", mPhonenumber.getText().toString())
                .addFormDataPart("sim_serial", mSimcardSerial.getText().toString())
                .addFormDataPart("simchange_type", String.valueOf(mChosenHandsetChangeTypeId))
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            mHandsetChangeTypeSpinner.setSelection(-1);
                            mPhonenumber.setText("");
                            mSimcardSerial.setText("");
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

    File photoFile = null;
    private void cameraIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            try {
                String imgName = "";
                if (isFirst) {
                    imgName = imageFront;
                } else {
                    imgName = imageBack;
                }
                photoFile = BitmapSaver.imageFile(mContext, imgName);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(mContext,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult();
            }
        } else {
            mProgressDialog.dismiss();
        }

    }

    private void onCaptureImageResult() {

        Log.d(TAG, "mCurrentPhotoPath " + photoFile.getAbsolutePath());

        Bitmap d = new BitmapDrawable(mContext.getResources(), photoFile.getAbsolutePath()).getBitmap();
        int nh = (int) (d.getHeight() * (512.0 / d.getWidth()));
        bm = Bitmap.createScaledBitmap(d, 512, nh, true);
        if (isFirst) {
            mFrontImage.setImageBitmap(bm);
        } else {
            mBackImage.setImageBitmap(bm);
        }

        mProgressDialog.dismiss();

    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        if (data != null) {
            Log.d(TAG, "Image Loaded");
            try {
//                bm = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), data.getData());
                bm = BitmapSaver.getThumbnail(mContext, data.getData());
//                BitmapSaver.imageFile(mContext, "image_fron_gal");

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (isFirst) {
            mFrontImage.setImageBitmap(bm);
            BitmapSaver.saveBitmapToFile(mContext, bm, imageFront);

        } else

        {
            mBackImage.setImageBitmap(bm);
            BitmapSaver.saveBitmapToFile(mContext, bm, imageBack);

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
