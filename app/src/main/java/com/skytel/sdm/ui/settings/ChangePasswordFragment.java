package com.skytel.sdm.ui.settings;


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
import android.widget.Toast;

import com.skytel.sdm.LoginActivity;
import com.skytel.sdm.MainActivity;
import com.skytel.sdm.R;
import com.skytel.sdm.database.DataManager;
import com.skytel.sdm.network.HttpClient;
import com.skytel.sdm.utils.ConfirmDialog;
import com.skytel.sdm.utils.Constants;
import com.skytel.sdm.utils.CustomProgressDialog;
import com.skytel.sdm.utils.PrefManager;
import com.skytel.sdm.utils.ValidationChecker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ChangePasswordFragment extends Fragment implements Constants {

    String TAG = ChangePasswordFragment.class.getName();

    private OkHttpClient mClient;
    private Context mContext;
    private DataManager mDataManager;
    private PrefManager mPrefManager;

    // UI Widgets
    private Button mChangeBtn;
    private EditText mOldPassword;
    private EditText mNewPassword;

    private CustomProgressDialog mProgressDialog;

    public ChangePasswordFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.change_password, container, false);

        mContext = getActivity();
        mDataManager = new DataManager(mContext);
        mClient = HttpClient.getInstance();
        mPrefManager = new PrefManager(mContext);
        mProgressDialog = new CustomProgressDialog(mContext);

        mOldPassword = (EditText) rootView.findViewById(R.id.old_password);
        mNewPassword = (EditText) rootView.findViewById(R.id.new_password);
        mChangeBtn = (Button) rootView.findViewById(R.id.btn_change_password);

        mChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ValidationChecker.isValidationPassed(mOldPassword) && ValidationChecker.isValidationPassed(mNewPassword)) {


                        ConfirmDialog confirmDialog = new ConfirmDialog();
                        Bundle args = new Bundle();
                        args.putInt("message", R.string.confirm);
                        args.putInt("title", R.string.confirm);

                        confirmDialog.setArguments(args);
                        confirmDialog.registerCallback(dialogConfirmListener);
                        confirmDialog.show(getFragmentManager(), "dialog");


                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.please_fill_the_field), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

//    Huuchin nuuts ugiig ilgeedeg bolgoson - Zolbayar
    public void runChangeFunction() throws Exception {
        final StringBuilder url = new StringBuilder();
        url.append(Constants.SERVER_URL);
        url.append(Constants.FUNCTION_CHANGE_PASSWORD);
        url.append("?oldpass=" + mOldPassword.getText().toString());
        url.append("&newpass=" + mNewPassword.getText().toString());


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
                    Log.d(TAG, "result_msg " + result_msg);
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

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mNewPassword.setText("");
                            mOldPassword.setText("");

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
                runChangeFunction();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onNegativeButton() {

        }
    };


}
