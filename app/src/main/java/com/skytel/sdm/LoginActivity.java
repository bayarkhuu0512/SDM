package com.skytel.sdp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.skytel.sdp.network.HttpClient;
import com.skytel.sdp.utils.Constants;
import com.skytel.sdp.utils.CustomProgressDialog;
import com.skytel.sdp.utils.PrefManager;
import com.skytel.sdp.utils.ValidationChecker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends Activity implements Constants {
    String TAG = LoginActivity.class.getName();

    private OkHttpClient mClient;
    private Context mContext;
    private Button mBtnLogin;
    private EditText mEtUserName;
    private EditText mEtPassword;
    private PrefManager mPrefManager;

    private CustomProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.mContext = this;
        mClient = HttpClient.getInstance();
        mPrefManager = new PrefManager(this);
        mProgressDialog = new CustomProgressDialog(this);

/**
 * If code is running on Debug
*/
              Intent i = new Intent(LoginActivity.this, MainActivity.class);
  /*      startActivity(i);
        finish();
*/

        if (mPrefManager.getIsLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        mEtUserName = (EditText) findViewById(R.id.et_username);
        mEtPassword = (EditText) findViewById(R.id.et_password);

        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ValidationChecker.isValidationPassed(mEtUserName) && ValidationChecker.isValidationPassed(mEtPassword)) {
//                        Toast.makeText(context, "Please wait", Toast.LENGTH_SHORT).show();
                        mProgressDialog.show();
                        runLoginFunction();

                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.please_fill_the_field), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void runLoginFunction() throws Exception {

        StringBuilder url = new StringBuilder();
        url.append(SERVER_URL);
        url.append(FUNCTION_LOGIN);
        url.append("?login=" + mEtUserName.getText().toString());
        url.append("&pass=" + mEtPassword.getText().toString());

       Log.d(TAG, "send URL: "+url.toString());

        Request request = new Request.Builder()
                .url(url.toString())
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("onFailure");
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                        Toast.makeText(mContext, getResources().getString(R.string.check_internet_connection), Toast.LENGTH_LONG).show();
                        // Used for debug
//                        PrefManager.getSessionInstance().setIsLoggedIn(true);
//                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                        startActivity(intent);
//                        finish();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                    }
                });


                System.out.println("onResponse");

                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                String resp = response.body().string();
                System.out.println("resp " + resp);

                try {
                    JSONObject jsonObj = new JSONObject(resp);
                    int result_code = jsonObj.getInt("result_code");


                    if (result_code == RESULT_CODE_SUCCESS) {
                        String auth_token = jsonObj.getString("auth_token");

                        mPrefManager.saveAuthToken(auth_token);

                        Log.d(TAG, "result_code " + result_code);
                        Log.d(TAG, "auth_token " + auth_token);

                        runProfileInfoFunction();
                        mProgressDialog.show();

                    } else {
                        final String result_msg = jsonObj.getString("result_msg");

                        Log.d(TAG, "result_code " + result_code);
                        Log.d(TAG, "result_msg " + result_msg);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, result_msg + "", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "Алдаатай хариу ирлээ", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void forgetPasswordView(View view) {
        Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
        startActivity(intent);
//        finish();
    }

    public void runProfileInfoFunction() throws Exception {
        StringBuilder url = new StringBuilder();
        url.append(SERVER_URL);
        url.append(FUNCTION_PROFILE_INFO);

        System.out.print(url + "\n");

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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //     progressDialog.dismiss();
                        Toast.makeText(mContext, getResources().getString(R.string.check_internet_connection), Toast.LENGTH_LONG).show();
                        // Used for debug
//                        PrefManager.getSessionInstance().setIsLoggedIn(true);
//                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                        startActivity(intent);
//                        finish();

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
                    String result_msg = jsonObj.getString("result_msg");
                    String balance = jsonObj.getString("balance");
                    String dealer_name = jsonObj.getString("dealer_name");
                    String zone_code = jsonObj.getString("zone_code");
                    String zone_name = jsonObj.getString("zone_name");

                    Log.d(TAG, "result_code " + result_code);
                    Log.d(TAG, "result_msg " + result_msg);
                    Log.d(TAG, "balance " + balance);
                    Log.d(TAG, "dealer_name " + dealer_name);
                    Log.d(TAG, "zone_code " + zone_code);
                    Log.d(TAG, "zone_name " + zone_name);

                    mPrefManager.saveDealerName(dealer_name);
                    mPrefManager.saveDealerBalance(balance);
                    mPrefManager.saveDealerZone(zone_name);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });

                    mPrefManager.setIsLoggedIn(true);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } catch (JSONException e) {
                    runOnUiThread(new Runnable() {
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

}
