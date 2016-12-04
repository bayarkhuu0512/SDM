package com.skytel.sdm;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.skytel.sdm.database.DataManager;
import com.skytel.sdm.ui.TabInformationFragment;
import com.skytel.sdm.ui.TabNewNumberFragment;
import com.skytel.sdm.ui.TabRegistrationFragment;
import com.skytel.sdm.ui.TabServiceFragment;
import com.skytel.sdm.ui.TabSettingsFragment;
import com.skytel.sdm.ui.TabSkyDealerFragment;
import com.skytel.sdm.ui.feedback.FeedbackFragment;
import com.skytel.sdm.ui.plan.PlanFragment;
import com.skytel.sdm.ui.skydealer.ChargeCardFragment;
import com.skytel.sdm.ui.skydealer.PostPaidPaymentFragment;
import com.skytel.sdm.utils.BalanceUpdateListener;
import com.skytel.sdm.utils.ConfirmDialog;
import com.skytel.sdm.utils.Constants;
import com.skytel.sdm.utils.PrefManager;

public class MainActivity extends AppCompatActivity implements BalanceUpdateListener, NavigationView.OnNavigationItemSelectedListener, Constants {
    String TAG = MainActivity.class.getName();

    private Context mContext;
    private DataManager mDataManager;
    private PrefManager mPrefManager;
    private TextView mDealerName;
    private TextView mDealerBalance;
    private TextView mDealerZone;

    private String[] mTitles;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private int currentScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mTitles = getResources().getStringArray(R.array.leftmenu_array);

        mDataManager = new DataManager(this);
        mPrefManager = new PrefManager(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        android.support.v7.app.ActionBarDrawerToggle toggle = new android.support.v7.app.ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        View headerView = navigationView.getHeaderView(0);

        if (savedInstanceState == null) {
            selectItem(MENU_NEWNUMBER);
        }


/*
        mDealerName = (TextView) findViewById(R.id.dealer_name);
        mDealerName.setText(mPrefManager.getDealerName());
        mDealerBalance = (TextView) findViewById(R.id.dealer_balance);
        mDealerBalance.setText(mPrefManager.getDealerBalance());
        mDealerZone = (TextView) findViewById(R.id.dealer_zone);
        mDealerZone.setText(mPrefManager.getDealerZone());
*/
        new LongOperation().execute();

        ChargeCardFragment.sBalanceUpdateListener = this;
        PostPaidPaymentFragment.sBalanceUpdateListener = this;


    }

    // Login
    public void loginView(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.menu_newnumber) {
            selectItem(MENU_NEWNUMBER);
        } else if (id == R.id.menu_skydealer) {
            selectItem(MENU_SKYDEALER);
        } else if (id == R.id.menu_registration) {
            selectItem(MENU_SERVICE);
        } else if (id == R.id.menu_service) {
            selectItem(MENU_REGISTRATION);
        } else if (id == R.id.menu_information) {
            selectItem(MENU_INFORMATION);
        } else if (id == R.id.menu_plan) {
            selectItem(MENU_PLAN);
        } else if (id == R.id.menu_feedback) {
            selectItem(MENU_FEEDBACK);
        } else if (id == R.id.menu_settings) {
            selectItem(MENU_SETTINGS);
        } else if (id == R.id.menu_exit) {
            selectItem(MENU_LOGOUT);
        }

        setTitle(mTitles[currentScreen]);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void selectItem(int groupPos) {
        currentScreen = groupPos;
        Fragment fragment = null;
        switch (groupPos) {
            case MENU_NEWNUMBER:
                fragment = new TabNewNumberFragment();
                break;
            case MENU_SKYDEALER:
                fragment = new TabSkyDealerFragment();
                break;
            case MENU_SERVICE:
                fragment = new TabServiceFragment();
                break;
            case MENU_REGISTRATION:
                fragment = new TabRegistrationFragment();
                break;
            case MENU_INFORMATION:
                fragment = new TabInformationFragment();
                break;
            case MENU_PLAN:
                fragment = new PlanFragment();
                break;
            case MENU_FEEDBACK:
                fragment = new FeedbackFragment();
                break;
            case MENU_SETTINGS:
                fragment = new TabSettingsFragment();
                break;
            case MENU_LOGOUT:
                fragment = new TabNewNumberFragment();
/*
                ConfirmDialog confirmDialog = new ConfirmDialog();
                Bundle args = new Bundle();
                args.putInt("message", R.string.dialog_logout_confirm);
                args.putInt("title", R.string.confirm);

                confirmDialog.setArguments(args);
                confirmDialog.registerCallback(dialogConfirmListener);
                confirmDialog.show(getFragmentManager(), "dialog");
*/
                break;
            default:
                fragment = new TabNewNumberFragment();
                break;
        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_fragment, fragment)
                .commit();
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
