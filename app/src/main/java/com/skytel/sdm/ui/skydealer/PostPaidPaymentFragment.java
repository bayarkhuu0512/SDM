package com.skytel.sdp.ui.skydealer;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.skytel.sdp.LoginActivity;
import com.skytel.sdp.MainActivity;
import com.skytel.sdp.R;
import com.skytel.sdp.database.DataManager;
import com.skytel.sdp.network.HttpClient;
import com.skytel.sdp.utils.BalanceUpdateListener;
import com.skytel.sdp.utils.ConfirmDialog;
import com.skytel.sdp.utils.Constants;
import com.skytel.sdp.utils.CustomProgressDialog;
import com.skytel.sdp.utils.PrefManager;
import com.skytel.sdp.utils.ValidationChecker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class PostPaidPaymentFragment extends Fragment {

    String TAG = PostPaidPaymentFragment.class.getName();

    private OkHttpClient mClient;
    private Context mContext;
    private DataManager mDataManager;
    private PrefManager mPrefManager;

    // UI Widgets
    private Button mDoPaymentBtn;
    private Button mGetInvoiceBtn;
    private EditText mInvoicePhoneNumber;
    private EditText mPincode;
    private EditText mConfirmCode;

    private LinearLayout mInvoiceLayout;
    private LinearLayout mPaymentLayout;

    private String mBalance = "";

    private ConfirmDialog mConfirmDialog;

    private CustomProgressDialog mProgressDialog;

    public static BalanceUpdateListener sBalanceUpdateListener;

    public PostPaidPaymentFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mConfirmDialog = new ConfirmDialog();
        Bundle args = new Bundle();
        args.putInt("message", R.string.confirm);
        args.putInt("title", R.string.confirm);

        mConfirmDialog.setArguments(args);
        mConfirmDialog.registerCallback(dialogConfirmListener);
        mProgressDialog = new CustomProgressDialog(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.postpaid_payment, container, false);

        mContext = getActivity();
        mDataManager = new DataManager(mContext);
        mClient = HttpClient.getInstance();
        mPrefManager = new PrefManager(mContext);

        mInvoicePhoneNumber = (EditText) rootView.findViewById(R.id.invoice_phone_number);
        mPincode = (EditText) rootView.findViewById(R.id.pin_code);
        mConfirmCode = (EditText) rootView.findViewById(R.id.confirm_code);

        mGetInvoiceBtn = (Button) rootView.findViewById(R.id.get_invoice_btn);
        mDoPaymentBtn = (Button) rootView.findViewById(R.id.do_payment_btn);

        mInvoiceLayout = (LinearLayout) rootView.findViewById(R.id.invoice_layout);
        mPaymentLayout = (LinearLayout) rootView.findViewById(R.id.payment_layout);

        mGetInvoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (ValidationChecker.isValidationPassed(mInvoicePhoneNumber) && ValidationChecker.isValidationPassed(mPincode)) {
                        mProgressDialog.show();
                        runInvoiceFunction();
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.please_fill_the_field), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mDoPaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (ValidationChecker.isValidationPassed(mConfirmCode)) {
                        mConfirmDialog.show(getFragmentManager(), "dialog");
                    } else {
                        Toast.makeText(getActivity(), "Please fill the field!", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

    public void runInvoiceFunction() throws Exception {
        final StringBuilder url = new StringBuilder();
        url.append(Constants.SERVER_URL);
        url.append(Constants.FUNCTION_GET_INVOICE);
        url.append("?phone=" + mInvoicePhoneNumber.getText().toString());
        url.append("&pin=" + mPincode.getText().toString());

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
                .addHeader(Constants.PREF_AUTH_TOKEN, mPrefManager.getAuthToken())
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mProgressDialog.dismiss();
                System.out.println("onFailure");
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, getResources().getString(R.string.check_internet_connection), Toast.LENGTH_LONG).show();
                    }
                });
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

                    // Log.d(TAG, "result_code " + result_code);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(mContext, "" + result_msg, Toast.LENGTH_LONG).show();
                            // Used for debug
                        }
                    });

                    if (result_code == Constants.RESULT_CODE_SUCCESS) {
                        mBalance = jsonObj.getString("balance");
                        Log.d(TAG, "balance " + mBalance);
                        Log.d(TAG, "Show the success message to user");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
//                                    Toast.makeText(mContext, "SUCCESSFUL!", Toast.LENGTH_LONG).show();
                                    mInvoiceLayout.setVisibility(View.GONE);
                                    mPaymentLayout.setVisibility(View.VISIBLE);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }else if (result_code == Constants.RESULT_CODE_INVOICE_ALREADY_CREATED) {
//                        mBalance = jsonObj.getString("balance");
//                        Log.d(TAG, "balance " + mBalance);
                        Log.d(TAG, "RESULT_CODE_INVOICE_ALREADY_CREATED");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
//                                    Toast.makeText(mContext, "SUCCESSFUL!", Toast.LENGTH_LONG).show();
                                    Toast.makeText(mContext, "Код хэрэглэгч рүү аль хэдийн илгээгдсэн байна!", Toast.LENGTH_LONG).show();
                                    mInvoiceLayout.setVisibility(View.GONE);
                                    mPaymentLayout.setVisibility(View.VISIBLE);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }else if (result_code == Constants.RESULT_CODE_UNREGISTERED_TOKEN) {
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

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    // Amountiig yos todii yavuulj baigaa - Backend deer invoice_num -oor ni avchihaj bgaa yum bilee - Zolbayar
    public void runPaymentFunction() throws Exception {
        final StringBuilder url = new StringBuilder();
        url.append(Constants.SERVER_URL);
        url.append(Constants.FUNCTION_DO_PAYMENT);
        url.append("?amount=" + "-1");
        url.append("&phone=" + mInvoicePhoneNumber.getText().toString());
        url.append("&pin=" + mPincode.getText().toString());
        url.append("&invoice_num=" + mConfirmCode.getText().toString());

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
                .addHeader(Constants.PREF_AUTH_TOKEN, mPrefManager.getAuthToken())
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mProgressDialog.dismiss();
                System.out.println("onFailure");
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, getResources().getString(R.string.check_internet_connection), Toast.LENGTH_LONG).show();
                    }
                });
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

                    Log.d(TAG, "result_code " + result_code);
                    Log.d(TAG, "result_mesage  " + result_msg);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(mContext, ""+ result_msg, Toast.LENGTH_LONG).show();

                        }
                    });

                    if (result_code == Constants.RESULT_CODE_SUCCESS) {

                        String dealer_id = jsonObj.getString("dealer_id");
                        String balance = jsonObj.getString("balance");

                        Log.d(TAG, "dealer_id " + dealer_id);
                        Log.d(TAG, "balance " + balance);

                        mPrefManager.saveDealerBalance(balance);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (sBalanceUpdateListener != null) {
                                    sBalanceUpdateListener.onBalanceUpdate();
                                }
                            }
                        });

                        Log.d(TAG, "Show the success message to user");

                    }
                    else if (result_code == Constants.RESULT_CODE_UNREGISTERED_TOKEN) {
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

//                  Tuhain dugaar buhii hereglegchid ali hediin tan code ochchihson gsn ug
                    else if (result_code == Constants.RESULT_CODE_INVOICE_ALREADY_CREATED) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "Код хэрэглэгч рүү аль хэдийн илгээгдсэн байна!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

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
                runPaymentFunction();


            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
                mProgressDialog.dismiss();
                Toast.makeText(mContext, "Error on Failure!", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onNegativeButton() {

        }
    };

}
