package com.skytel.sdm.ui.service;


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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.skytel.sdm.LoginActivity;
import com.skytel.sdm.MainActivity;
import com.skytel.sdm.R;
import com.skytel.sdm.adapter.HandsetChangeTypeAdapter;
import com.skytel.sdm.adapter.NothingSelectedSpinnerAdapter;
import com.skytel.sdm.adapter.VasTypeAdapter;
import com.skytel.sdm.database.DataManager;
import com.skytel.sdm.entities.HandsetChangeType;
import com.skytel.sdm.entities.VasType;
import com.skytel.sdm.network.HttpClient;
import com.skytel.sdm.utils.ConfirmDialog;
import com.skytel.sdm.utils.Constants;
import com.skytel.sdm.utils.CustomProgressDialog;
import com.skytel.sdm.utils.PrefManager;
import com.skytel.sdm.utils.ValidationChecker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ValueAddedServiceFragment extends Fragment implements Constants {

    String TAG = ValueAddedServiceFragment.class.getName();
    private CustomProgressDialog mProgressDialog;
    private Context mContext;
    private PrefManager mPrefManager;
    private OkHttpClient mClient;
    private List<VasType> mVastypeChange;
    private ConfirmDialog mConfirmDialog;
    private DataManager mDataManager;

    private Spinner mVasTypeSpinner;
    private EditText mPhonenumber;
    private Button mSendOrder;
    private TextView mPriceMongoldoo;
    private TextView mPricePost;
    private TextView mPricePre;

    private int mChosenVasTypeId = 0;
    private boolean mIsActivate = true;

    private Button mActive;
    private Button mDeactive;

    public ValueAddedServiceFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.value_added_service, container, false);

        mContext = getActivity();
        mProgressDialog = new CustomProgressDialog(getActivity());
        mPrefManager = new PrefManager(mContext);
        mClient = HttpClient.getInstance();
        mVastypeChange = new ArrayList<>();
        mDataManager = new DataManager(mContext);

        mConfirmDialog = new ConfirmDialog();
        Bundle args = new Bundle();
        args.putInt("message", R.string.confirm);
        args.putInt("title", R.string.confirm);
        mConfirmDialog.setArguments(args);
        mConfirmDialog.registerCallback(dialogConfirmListener);

        mVasTypeSpinner = (Spinner) rootView.findViewById(R.id.choose_vas_type);
        mPhonenumber = (EditText) rootView.findViewById(R.id.phone_number);
        mSendOrder = (Button) rootView.findViewById(R.id.send_order);
        mPriceMongoldoo = (TextView) rootView.findViewById(R.id.price_mongoldoo);
        mPricePost = (TextView) rootView.findViewById(R.id.price_post);
        mPricePre = (TextView) rootView.findViewById(R.id.price_pre);

        mActive = (Button) rootView.findViewById(R.id.btn_active);
        mActive.setOnClickListener(activeOnClick);

        mDeactive = (Button) rootView.findViewById(R.id.btn_deactive);
        mDeactive.setOnClickListener(deactiveOnClick);

        mSendOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ValidationChecker.isValidationPassed(mPhonenumber) && ValidationChecker.isSelected((int) mVasTypeSpinner.getSelectedItemId())){
                    mConfirmDialog.show(getFragmentManager(), "dialog");
                }
                else{
                    Toast.makeText(mContext, getResources().getString(R.string.please_fill_the_field), Toast.LENGTH_SHORT).show();
                }
            }
        });


        try {
            mProgressDialog.show();
            runGetVasTypeInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }
    View.OnClickListener activeOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mIsActivate = true;
            invalidateFilterButtons();
        }
    };

    View.OnClickListener deactiveOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mIsActivate = false;
            invalidateFilterButtons();
        }
    };
    private void invalidateFilterButtons() {
       if(mIsActivate) {
           mActive.setBackground(getResources().getDrawable(R.drawable.btn_yellow_selected));
           mActive.setTextColor(Color.WHITE);

           mDeactive.setBackground(getResources().getDrawable(R.drawable.btn_yellow));
           mDeactive.setTextColor(getResources().getColor(R.color.colorSkytelYellow));
       }else{
                mActive.setBackground(getResources().getDrawable(R.drawable.btn_yellow));
                mActive.setTextColor(getResources().getColor(R.color.colorSkytelYellow));

                mDeactive.setBackground(getResources().getDrawable(R.drawable.btn_yellow_selected));
                mDeactive.setTextColor(Color.WHITE);
        }
    }

    public void runGetVasTypeInfo() throws Exception {

        final StringBuilder url = new StringBuilder();
        url.append(Constants.SERVER_URL);
        url.append(Constants.FUNCTION_VAS_TYPE);


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

                            Toast.makeText(mContext, "" + result_msg, Toast.LENGTH_LONG).show();

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

                    JSONArray jArray = jsonObj.getJSONArray("vas_types");

                    Log.d(TAG, "*****JARRAY*****" + jArray.length());
                    mVastypeChange.clear();
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jsonData = jArray.getJSONObject(i);

                        String type_name = jsonData.getString("type_name");
                        String price_post_alt = jsonData.getString("price_post_alt");
                        String price_post = jsonData.getString("price_post");
                        String price_pre = jsonData.getString("price_pre");
                        int vas_type_id = jsonData.getInt("id");

                        Log.d(TAG, "INDEX:       " + i);

                        Log.d(TAG, "type_name: " + type_name);
                        Log.d(TAG, "price_post_alt: " + price_post_alt);
                        Log.d(TAG, "price_post: " + price_post);
                        Log.d(TAG, "price_pre: " + price_pre);
                        Log.d(TAG, "vas_type_id: " + vas_type_id);


                        VasType vasType = new VasType();
                        vasType.setId(i);
                        vasType.setPricePost(price_post);
                        vasType.setTypeName(type_name);
                        vasType.setPricePostAlt(price_post_alt);
                        vasType.setPricePre(price_pre);
                        vasType.setVasTypeId(vas_type_id);


                        mVastypeChange.add(i, vasType);
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final List<VasType> vasTypes = mVastypeChange;
                            VasTypeAdapter adapterFilter = new VasTypeAdapter(getActivity(), R.layout.vas_type_item, mVastypeChange);
                            mVasTypeSpinner.setAdapter(new NothingSelectedSpinnerAdapter(adapterFilter,
                                    R.layout.spinner_row_nothing_selected,
                                    getActivity()));
                            mVasTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                    try {
                                        mChosenVasTypeId = vasTypes.get(position-1).getVasTypeId();
                                        mPriceMongoldoo.setText(vasTypes.get(position-1).getPricePostAlt());
                                        mPricePre.setText(vasTypes.get(position-1).getPricePre());
                                        mPricePost.setText(vasTypes.get(position-1).getPricePost());

                                    } catch (ArrayIndexOutOfBoundsException e){

                                    }
                                }
                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                    Toast.makeText(getActivity(),"Nothing Selected",Toast.LENGTH_SHORT).show();
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
    public void runSendOrder() throws Exception {

        final StringBuilder url = new StringBuilder();
        url.append(Constants.SERVER_URL);
        url.append(Constants.FUNCTION_VAS_CHANGE_STATE);


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "send URL: "+url.toString());
            }
        });

        System.out.print(url + "\n");
        System.out.println(mPrefManager.getAuthToken());

        RequestBody formBody = new FormBody.Builder()
                .add("phone",mPhonenumber.getText().toString())
                .add("is_activate", String.valueOf(mIsActivate))
                .add("vas_type", String.valueOf(mChosenVasTypeId))
                .build();
        Request request = new Request.Builder()
                .url(url.toString())
                .addHeader(Constants.PREF_AUTH_TOKEN, mPrefManager.getAuthToken())
                .post(formBody)
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPhonenumber.setText("");
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
