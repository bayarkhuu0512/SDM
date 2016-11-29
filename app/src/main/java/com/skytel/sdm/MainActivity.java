package com.skytel.sdp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.skytel.sdp.adapter.LeftMenuListAdapter;
import com.skytel.sdp.database.DataManager;
import com.skytel.sdp.ui.TabInformationFragment;
import com.skytel.sdp.ui.TabNewNumberFragment;
import com.skytel.sdp.ui.TabRegistrationFragment;
import com.skytel.sdp.ui.TabServiceFragment;
import com.skytel.sdp.ui.TabSettingsFragment;
import com.skytel.sdp.ui.TabSkyDealerFragment;
import com.skytel.sdp.ui.feedback.FeedbackFragment;
import com.skytel.sdp.ui.plan.PlanFragment;
import com.skytel.sdp.ui.skydealer.ChargeCardFragment;
import com.skytel.sdp.ui.skydealer.PostPaidPaymentFragment;
import com.skytel.sdp.utils.BalanceUpdateListener;
import com.skytel.sdp.utils.ConfirmDialog;
import com.skytel.sdp.utils.Constants;
import com.skytel.sdp.utils.PrefManager;

public class MainActivity extends AppCompatActivity implements BalanceUpdateListener {
    String TAG = MainActivity.class.getName();

    public static int sCurrentMenu = Constants.MENU_NEWNUMBER;
    private ListView mLeftMenuListView;
    private LeftMenuListAdapter mLeftMenuListAdapter;
    private FragmentTransaction mTransaction;
    private Context mContext;
    private DataManager mDataManager;
    private PrefManager mPrefManager;
    private TextView mDealerName;
    private TextView mDealerBalance;
    private TextView mDealerZone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        mDataManager = new DataManager(this);
        mPrefManager = new PrefManager(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (savedInstanceState == null) {
            changeMenu(new TabNewNumberFragment());
        }

        mDealerName = (TextView) findViewById(R.id.dealer_name);
        mDealerName.setText(mPrefManager.getDealerName());
        mDealerBalance = (TextView) findViewById(R.id.dealer_balance);
        mDealerBalance.setText(mPrefManager.getDealerBalance());
        mDealerZone = (TextView) findViewById(R.id.dealer_zone);
        mDealerZone.setText(mPrefManager.getDealerZone());


        mLeftMenuListView = (ListView) findViewById(R.id.leftMenuListView);
        mLeftMenuListAdapter = new LeftMenuListAdapter(this);
        mLeftMenuListView.setAdapter(mLeftMenuListAdapter);
        mLeftMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sCurrentMenu = position;

                switch (position) {
                    case Constants.MENU_NEWNUMBER:
                        changeMenu(new TabNewNumberFragment());
                        break;
                    case Constants.MENU_SKYDEALER:
                        changeMenu(new TabSkyDealerFragment());
                        break;
                    case Constants.MENU_SERVICE:
                        changeMenu(new TabServiceFragment());
                        break;
                    case Constants.MENU_REGISTRATION:
                        changeMenu(new TabRegistrationFragment());
                        break;
                    case Constants.MENU_INFORMATION:
                        changeMenu(new TabInformationFragment());
                        break;
                    case Constants.MENU_PLAN:
                        changeMenu(new PlanFragment());
                        break;
                    case Constants.MENU_FEEDBACK:
                        changeMenu(new FeedbackFragment());
                        break;
                    case Constants.MENU_SETTINGS:
                        changeMenu(new TabSettingsFragment());

                        break;
                    case Constants.MENU_LOGOUT:
                        //logoutDialog();
                        ConfirmDialog confirmDialog = new ConfirmDialog();
                        Bundle args = new Bundle();
                        args.putInt("message", R.string.dialog_logout_confirm);
                        args.putInt("title", R.string.confirm);

                        confirmDialog.setArguments(args);
                        confirmDialog.registerCallback(dialogConfirmListener);
                        confirmDialog.show(getFragmentManager(), "dialog");

                        sCurrentMenu = Constants.MENU_NEWNUMBER;

                        break;
                }
                mLeftMenuListAdapter.notifyDataSetChanged();
            }
        });

        new LongOperation().execute();

        ChargeCardFragment.sBalanceUpdateListener = this;
        PostPaidPaymentFragment.sBalanceUpdateListener = this;

    }

    // Login
    public void loginView(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void changeMenu(Fragment fragment) {
        mTransaction = getFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        mTransaction
                .replace(R.id.main_detail_container, fragment)
                .commit();
    }


    private ConfirmDialog.OnDialogConfirmListener dialogConfirmListener = new ConfirmDialog.OnDialogConfirmListener() {

        @Override
        public void onPositiveButton() {
            //  Toast.makeText(this, "Confirmed", Toast.LENGTH_LONG).show();

            mPrefManager.setIsLoggedIn(false);
            mDataManager.resetCardTypes();

            finish();
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }

        @Override
        public void onNegativeButton() {

        }
    };

    @Override
    public void onBalanceUpdate() {
        Log.d(TAG, "BALANCE: " + mPrefManager.getDealerBalance());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDealerBalance.setText(mPrefManager.getDealerBalance());
            }
        });

    }

    private class LongOperation extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                if (mDataManager.resetCardTypes()) {
                    mDataManager.setCardTypes();
                }

                return true;
            } catch (
                    Exception e
                    )

            {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            //  progressDialog.dismiss();
            if (result) {
                Log.d(TAG, "AsyncTask Finished");

            } else {
                Toast.makeText(mContext, "Error!", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "Load Asyns started");
            //   progressDialog = new ProgressDialog(context);
            //   progressDialog.setCancelable(true);
            //  progressDialog.show(context, getResources().getString(R.string.please_wait), getResources().getString(R.string.checking));
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
