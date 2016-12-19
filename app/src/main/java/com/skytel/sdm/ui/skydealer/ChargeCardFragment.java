package com.skytel.sdm.ui.skydealer;


import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.skytel.sdm.R;
import com.skytel.sdm.adapter.ChargeCardPackageTypeAdapter;
import com.skytel.sdm.adapter.ChargeCardTypeAdapter;
import com.skytel.sdm.database.DataManager;
import com.skytel.sdm.entities.CardType;
import com.skytel.sdm.enums.PackageTypeEnum;
import com.skytel.sdm.network.HttpClient;
import com.skytel.sdm.utils.BalanceUpdateListener;
import com.skytel.sdm.utils.ConfirmDialog;
import com.skytel.sdm.utils.Constants;
import com.skytel.sdm.utils.CustomProgressDialog;
import com.skytel.sdm.utils.PrefManager;
import com.skytel.sdm.utils.ValidationChecker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Altanchimeg
 */
public class ChargeCardFragment extends Fragment {
    String TAG = ChargeCardFragment.class.getName();

    private OkHttpClient mClient;
    private Context mContext;
    private DataManager mDataManager;
    private PackageTypeEnum mPackageTypeEnum = null;
    private CardType mCardType;
    // UI Widgets

    private Button mChargeCardOrderBtn;
    private EditText mChargeCardPhoneNumber;
    private EditText mChargeCardPinCode;
    private TextView mPackageTypeName;
    private TextView mCardTypeName;

    private ListView mPackageTypeListView;
    private ListView mCardTypeListView;

    private PrefManager mPrefManager;

    private ConfirmDialog mConfirmDialog;

    private CustomProgressDialog mProgressDialog;

    public static BalanceUpdateListener sBalanceUpdateListener;
    private String[] mPackageTypes;
    private List<CardType> mCardList;

    //    IP76 carduud deer tsenegleh dugaariig bish ooriin dugaariig oruulah uchir - Zolbayar
    private TextView numberToSendLabel;

    public ChargeCardFragment() {
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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.charge_card, container, false);
        mContext = getActivity();
        mDataManager = new DataManager(mContext);
        mClient = HttpClient.getInstance();
        mPrefManager = new PrefManager(mContext);
        mPackageTypes = mContext.getResources().getStringArray(R.array.skydealer_charge_card_types);

        numberToSendLabel = (TextView) rootView.findViewById(R.id.number_to_send_label);

        // Set Package Type List
        mPackageTypeListView = (ListView) rootView.findViewById(R.id.package_type_list_view);
        mPackageTypeListView.setAdapter(new ChargeCardPackageTypeAdapter(getActivity()));
        mPackageTypeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case Constants.CONST_COLOR_DATA_PACKAGE:
                        mPackageTypeEnum = PackageTypeEnum.COLOR_DATA_PACKAGE;
                        break;
                    case Constants.CONST_COLOR_CALL_PACKAGE:
                        mPackageTypeEnum = PackageTypeEnum.COLOR_CALL_PACKAGE;
                        break;
                    case Constants.CONST_SKYTEL_NODAY_PACKAGE:
                        mPackageTypeEnum = PackageTypeEnum.SKYTEL_NODAY_PACKAGE;
                        break;
                    case Constants.CONST_SKYTEL_DAY_PACKAGE:
                        mPackageTypeEnum = PackageTypeEnum.SKYTEL_DAY_PACKAGE;
                        break;
                    case Constants.CONST_SKYTEL_DATA_PACKAGE:
                        mPackageTypeEnum = PackageTypeEnum.SKYTEL_DATA_PACKAGE;
                        break;
                    case Constants.CONST_SKYMEDIA_IP76_PACKAGE:
                        mPackageTypeEnum = PackageTypeEnum.SKYMEDIA_IP76_PACKAGE;
                        numberToSendLabel.setText(getResources().getString(R.string.insert_charge_number_for_ip));
                        break;
                    case Constants.CONST_SMART_PACKAGE:
                        mPackageTypeEnum = PackageTypeEnum.SMART_PACKAGE;
                        break;
                }
                mCardTypeListView.setAdapter(new ChargeCardTypeAdapter(getActivity(), mPackageTypeEnum));
                mCardList = mDataManager.getCardTypeByPackageType(mPackageTypeEnum);

            }
        });

        // Set Card Type List
        mCardTypeListView = (ListView) rootView.findViewById(R.id.card_type_list_view);
        mCardTypeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCardType = mDataManager.getCardType(view.getId());
                Log.d(TAG, "PackageType: " + mPackageTypeEnum);
                Log.d(TAG, "CardType: " + mCardType.getName());

                Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_charge_card);
                dialog.setTitle(getString(R.string.tab_skydealer_chargecard));
                mChargeCardPhoneNumber = (EditText) dialog.findViewById(R.id.charge_card_phone_number);
                mChargeCardPinCode = (EditText) dialog.findViewById(R.id.charge_card_pin_code);
                mPackageTypeName = (TextView) dialog.findViewById(R.id.package_type_name);
                mCardTypeName = (TextView) dialog.findViewById(R.id.card_type_name);
                mChargeCardOrderBtn = (Button) dialog.findViewById(R.id.charge_card_order_btn);
                mPackageTypeName.setText(mPackageTypes[position]);
                mCardTypeName.setText(mCardList.get(position).getDesciption());
                mChargeCardOrderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ValidationChecker.isValidationPassed(mChargeCardPhoneNumber) && ValidationChecker.isValidationPassed(mChargeCardPinCode)) {
                            mConfirmDialog.show(getFragmentManager(), "dialog");

                        } else {
                            Toast.makeText(mContext, getResources().getString(R.string.please_fill_the_field), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.show();
            }
        });

        for (CardType cardType : mDataManager.getCardTypeByPackageType(PackageTypeEnum.COLOR_DATA_PACKAGE)) {
            Log.d(TAG, "cardTypeName " + cardType.getName());
        }

        return rootView;
    }

    public void runChargeFunction() throws Exception {
        final StringBuilder url = new StringBuilder();
        url.append(Constants.SERVER_URL);
        url.append(Constants.FUNCTION_CHARGE);
        url.append("?card_type=" + mCardType.getName());
        url.append("&phone=" + mChargeCardPhoneNumber.getText().toString());
        url.append("&pin=" + mChargeCardPinCode.getText().toString());

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, url.toString());
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
                                                         // Used for debug
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
                                                     Log.d(TAG, "result_msg " + result_msg);
                                                     Log.d(TAG, "result_code " + result_code);

                                                     getActivity().runOnUiThread(new Runnable() {
                                                         @Override
                                                         public void run() {
                                                             Toast.makeText(mContext, "" + result_msg, Toast.LENGTH_LONG).show();
                                                         }
                                                     });

                                                     if (result_code == Constants.RESULT_CODE_SUCCESS) {

                                                         String dealer_id = jsonObj.getString("dealer_id");
                                                         final String balance = jsonObj.getString("balance");

                                                         Log.d(TAG, "dealer_id " + dealer_id);
                                                         Log.d(TAG, "balance " + balance);


                                                         try {
                                                             getActivity().runOnUiThread(new Runnable() {
                                                                 @Override
                                                                 public void run() {
                                                                     mPrefManager.saveDealerBalance(balance);
                                                                     if (sBalanceUpdateListener != null) {
                                                                         sBalanceUpdateListener.onBalanceUpdate();
                                                                     }
                                                                     Log.d(TAG, "Show the success message to user");

/*
                                                                     mChargeCardPhoneNumber.setText("");
                                                                     mChargeCardPinCode.setText("");
                                                                     mCardTypeName.setText("");
                                                                     mPackageTypeName.setText("");
*/

                                                                 }
                                                             });
                                                         } catch (Exception ex) {
                                                             ex.printStackTrace();
                                                         }

                                                     }
/*
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
*/
                                                     else {

                                                         getActivity().runOnUiThread(new Runnable() {
                                                             @Override
                                                             public void run() {

/*
                                                                 mChargeCardPhoneNumber.setText("");
                                                                 mChargeCardPinCode.setText("");
*/

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
                                         }

        );
    }


    private ConfirmDialog.OnDialogConfirmListener dialogConfirmListener = new ConfirmDialog.OnDialogConfirmListener() {

        @Override
        public void onPositiveButton() {
            try {
                mProgressDialog.show();
                runChargeFunction();
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


    @Override
    public void onPause() {
        super.onPause();
/*
        mChargeCardPhoneNumber.setText("");
        mChargeCardPinCode.setText("");
*/
    }
}
