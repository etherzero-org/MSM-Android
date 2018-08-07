package com.etzwallet.presenter.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.etzwallet.BreadApp;
import com.etzwallet.R;
import com.etzwallet.presenter.activities.settings.WebViewActivity;
import com.etzwallet.presenter.activities.util.BRActivity;
import com.etzwallet.presenter.customviews.BRButton;
import com.etzwallet.presenter.customviews.BRSearchBar;
import com.etzwallet.presenter.customviews.BRText;
import com.etzwallet.tools.animation.BRAnimator;
import com.etzwallet.tools.animation.BRDialog;
import com.etzwallet.tools.manager.BRSharedPrefs;
import com.etzwallet.tools.manager.FontManager;
import com.etzwallet.tools.manager.InternetManager;
import com.etzwallet.tools.manager.TxManager;
import com.etzwallet.tools.services.SyncService;
import com.etzwallet.tools.sqlite.RatesDataSource;
import com.etzwallet.tools.threads.executor.BRExecutor;
import com.etzwallet.tools.util.CurrencyUtils;
import com.etzwallet.tools.util.SyncTestLogger;
import com.etzwallet.tools.util.Utils;
import com.etzwallet.wallet.WalletsMaster;
import com.etzwallet.wallet.abstracts.BaseWalletManager;
import com.etzwallet.wallet.abstracts.OnBalanceChangedListener;
import com.etzwallet.wallet.abstracts.OnTxListModified;
import com.etzwallet.wallet.abstracts.SyncListener;
import com.etzwallet.wallet.util.CryptoUriParser;
import com.etzwallet.wallet.util.HttpUtils;
import com.etzwallet.wallet.util.JsonRpcHelper;
import com.etzwallet.wallet.util.PowerResponse;
import com.etzwallet.wallet.wallets.CryptoAddress;
import com.etzwallet.wallet.wallets.bitcoin.BaseBitcoinWalletManager;
import com.etzwallet.wallet.wallets.ethereum.WalletEthManager;


import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import com.google.gson.Gson;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static java.lang.Math.*;

/**
 * Created by byfieldj on 1/16/18.
 * <p>
 * <p>
 * This activity will display pricing and transaction information for any currency the user has access to
 * (BTC, BCH, ETH)
 */

public class WalletActivity extends BRActivity implements InternetManager.ConnectionReceiverListener,
        OnTxListModified, RatesDataSource.OnDataChanged, SyncListener, OnBalanceChangedListener {
    private static final String TAG = WalletActivity.class.getName();

    private static final String SYNCED_THROUGH_DATE_FORMAT = "MM/dd/yy HH:mm";
    private static final float SYNC_PROGRESS_LAYOUT_ANIMATION_ALPHA = 0.0f;

    private BRText mCurrencyTitle;
    private BRText mCurrencyPriceUsd;
    private BRText mBalancePrimary;
    private BRText mBalanceSecondary;
    private Toolbar mToolbar;
    private ImageButton mBackButton;

    private BRButton mSendButton;
    private BRButton mReceiveButton;

    private BRText maxPowerValue;
    private BRText availablePowerValue;
    private LinearLayout powerContainer;
    private Toolbar breadBar;


    private LinearLayout mProgressLayout;
    private BRText mSyncStatusLabel;
    private BRText mProgressLabel;
    public ViewFlipper mBarFlipper;
    private BRSearchBar mSearchBar;
    private ImageButton mSearchIcon;
    private ConstraintLayout mToolBarConstraintLayout;

    public static final String EXTRA_URL = "com.etzwallet.EXTRA_URL";

    private static final float PRIMARY_TEXT_SIZE = 30;
    private static final float SECONDARY_TEXT_SIZE = 16;

    private static final boolean RUN_LOGGER = false;

    private SyncTestLogger mTestLogger;

    private SyncNotificationBroadcastReceiver mSyncNotificationBroadcastReceiver;
    private String mCurrentWalletIso;

    private BaseWalletManager mWallet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wallet);

        BRSharedPrefs.putIsNewWallet(this, false);

        mCurrencyTitle = findViewById(R.id.currency_label);
        mCurrencyPriceUsd = findViewById(R.id.currency_usd_price);
        mBalancePrimary = findViewById(R.id.balance_primary);

        //power
        maxPowerValue = findViewById(R.id.max_power_value);
        powerContainer = findViewById(R.id.power_container);
        breadBar = findViewById(R.id.bread_bar);
        availablePowerValue = findViewById(R.id.available_power_value);


        mBalanceSecondary = findViewById(R.id.balance_secondary);
        mToolbar = findViewById(R.id.bread_bar);



        mBackButton = findViewById(R.id.back_icon);
        mSendButton = findViewById(R.id.send_button);
        mReceiveButton = findViewById(R.id.receive_button);
//        mBuyButton = findViewById(R.id.buy_button);
//        mSellButton = findViewById(R.id.sell_button);
        mBarFlipper = findViewById(R.id.tool_bar_flipper);
        mSearchBar = findViewById(R.id.search_bar);
        mSearchIcon = findViewById(R.id.search_icon);
        mToolBarConstraintLayout = findViewById(R.id.bread_toolbar);
        mProgressLayout = findViewById(R.id.progress_layout);
        mSyncStatusLabel = findViewById(R.id.sync_status_label);
        mProgressLabel = findViewById(R.id.syncing_label);

        startSyncLoggerIfNeeded();

        setUpBarFlipper();

//        setPowerMax();

        mBalancePrimary.setTextSize(TypedValue.COMPLEX_UNIT_SP, PRIMARY_TEXT_SIZE);
        mBalanceSecondary.setTextSize(TypedValue.COMPLEX_UNIT_SP, SECONDARY_TEXT_SIZE);

        mSendButton.setHasShadow(false);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BRAnimator.showSendFragment(WalletActivity.this, null);

            }
        });

        mSendButton.setHasShadow(false);
        mReceiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BRAnimator.showReceiveFragment(WalletActivity.this, true);

            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        mSearchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!BRAnimator.isClickAllowed()) {
                    return;
                }
                mBarFlipper.setDisplayedChild(1); //search bar
                mSearchBar.onShow(true);
            }
        });


//        mBuyButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startWebActivity(HTTPServer.URL_BUY);
//            }
//        });
//
//        mSellButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startWebActivity(HTTPServer.URL_SELL);
//            }
//        });


        mBalancePrimary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swap();
            }
        });
        mBalanceSecondary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swap();
            }
        });

        TxManager.getInstance().init(this);

        onConnectionChanged(InternetManager.getInstance().isConnected(this));

        updateUi();




        BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("BG:" + TAG + ":refreshBalances and address");
                Activity app = WalletActivity.this;
                WalletsMaster.getInstance(app).refreshBalances(app);
                WalletsMaster.getInstance(app).getCurrentWallet(app).refreshAddress(app);
            }
        });

        // Check if the "Twilight" screen altering app is currently running
        if (Utils.checkIfScreenAlteringAppIsRunning(this, "com.urbandroid.lux")) {
            BRDialog.showSimpleDialog(this, "Screen Altering App Detected", getString(R.string.Android_screenAlteringMessage));
        }

        mWallet = WalletsMaster.getInstance(this).getCurrentWallet(this);

        boolean cryptoPreferred = BRSharedPrefs.isCryptoPreferred(this);

        setPriceTags(cryptoPreferred, false);

    }

//    private void setPowerMax(){
//        //设置power max value
//        final int balance = 1;
//        String powerMax = String.valueOf(exp(-1/(balance*50)*10000) * 10000000 + 200000);
//        powerTextValue.setText(powerMax);
//    }

    private void startWebActivity(String action) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra(EXTRA_URL, action);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_bottom, R.anim.fade_down);
    }

    private void startSyncLoggerIfNeeded() {
        if (Utils.isEmulatorOrDebug(this) && RUN_LOGGER) {
            if (mTestLogger != null) {
                mTestLogger.interrupt();
            }
            mTestLogger = new SyncTestLogger(this); //Sync logger
            mTestLogger.start();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //since we have one instance of activity at all times, this is needed to know when a new intent called upon this activity
        handleUrlClickIfNeeded(intent);
    }

    private void handleUrlClickIfNeeded(Intent intent) {
        Uri data = intent.getData();
        if (data != null && !data.toString().isEmpty()) {
            //handle external click with crypto scheme
            CryptoUriParser.processRequest(this, data.toString(), WalletsMaster.getInstance(this).getCurrentWallet(this));
        }
    }

    private void getPower(CryptoAddress receiveAddr){
        String powerUrl = "https://easyetz.io/etzq/api/v1/getPower?address="+receiveAddr;

        HttpUtils.sendOkHttpRequest(powerUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                try{
                    JSONObject resObject = new JSONObject(responseText);

                    final String PowerValue = resObject.getString("result");
                    runOnUiThread(new Runnable() {
                    @Override
                        public void run() {

                            BigDecimal bg = new BigDecimal(PowerValue);
                            double e = bg.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();


                            availablePowerValue.setText(Double.toString(e));
                        }
                    });

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(WalletActivity.this, "getPower失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    //把String转化为double
    public static double convertToDouble(String number, double defaultValue) {
        if (TextUtils.isEmpty(number)) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(number);
        } catch (Exception e) {
            return defaultValue;
        }

    }


    private void updateUi() {
        final BaseWalletManager wm = WalletsMaster.getInstance(this).getCurrentWallet(this);
        if (wm == null) {
            Log.e(TAG, "updateUi: wallet is null");
            return;
        }

        CryptoAddress addr = wm.getReceiveAddress(this);

        String fiatExchangeRate = CurrencyUtils.getFormattedAmount(this, BRSharedPrefs.getPreferredFiatIso(this), wm.getFiatExchangeRate(this));
        String fiatBalance = CurrencyUtils.getFormattedAmount(this, BRSharedPrefs.getPreferredFiatIso(this), wm.getFiatBalance(this));
        String cryptoBalance = CurrencyUtils.getFormattedAmount(this, wm.getIso(), wm.getCachedBalance(this), wm.getUiConfiguration().getMaxDecimalPlacesForUi());

        mCurrencyTitle.setText(wm.getName());
        mCurrencyPriceUsd.setText(String.format("%s per %s", fiatExchangeRate, wm.getIso()));

        //只在etz显示power 值
        if(!wm.getIso().equalsIgnoreCase("ETZ")){
            powerContainer.setVisibility(View.GONE);
//            breadBar 设置高度 160dp  etz 200dp

        }else{
            getPower(addr);
            powerContainer.setVisibility(View.VISIBLE);

            int b = cryptoBalance.length();

            String c = cryptoBalance.substring(0,b-3).trim();

            DecimalFormat df = new DecimalFormat("#.00");//保留小数点后2位

            double d = convertToDouble(c,0);

            String d1 = df.format(d);

            float d2 = Double.valueOf(d1).floatValue();

            double e1  = (double)-1/(d2*50);
            double e2 = e1*10000;
            double e3 = ( Math.exp(e2) *10000000 + 200000)*18*Math.pow(10,9)/Math.pow(10,18);

            BigDecimal bg = new BigDecimal(e3);
            double e4 = bg.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();

            maxPowerValue.setText(Double.toString(e4));

        }


        mBalancePrimary.setText(fiatBalance);
        mBalanceSecondary.setText(cryptoBalance);

        String startColor = wm.getUiConfiguration().getStartColor();
        String endColor = wm.getUiConfiguration().getEndColor();

        if (endColor != null) {
            //it's a gradient
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT,
                    new int[]{Color.parseColor(startColor), Color.parseColor(endColor)});
            gd.setCornerRadius(0f);
            mToolbar.setBackground(gd);
            mToolbar.setMinimumHeight(260);
            mSendButton.setColor(Color.parseColor(endColor)); //end color if gradient
            mReceiveButton.setColor(Color.parseColor(endColor));
//            mBuyButton.setColor(Color.parseColor(endColor));
//            mSellButton.setColor(Color.parseColor(endColor));
        } else {
            //it's a solid color
            mToolbar.setBackgroundColor(Color.parseColor(startColor));
            mSendButton.setColor(Color.parseColor(startColor));
            mReceiveButton.setColor(Color.parseColor(startColor));
//            mBuyButton.setColor(Color.parseColor(startColor));
//            mSellButton.setColor(Color.parseColor(startColor));
        }

        BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("BG:" + TAG + ":updateTxList");
                TxManager.getInstance().updateTxList(WalletActivity.this);
            }
        });

    }

    private void swap() {
        if (!BRAnimator.isClickAllowed()) {
            return;
        }
        BRSharedPrefs.setIsCryptoPreferred(WalletActivity.this, !BRSharedPrefs.isCryptoPreferred(WalletActivity.this));
        setPriceTags(BRSharedPrefs.isCryptoPreferred(WalletActivity.this), true);
    }


    private void setPriceTags(final boolean cryptoPreferred, boolean animate) {
        ConstraintSet set = new ConstraintSet();
        set.clone(mToolBarConstraintLayout);
        if (animate) {
            TransitionManager.beginDelayedTransition(mToolBarConstraintLayout);
        }
        int px8 = Utils.getPixelsFromDps(this, 8);

        // CRYPTO on RIGHT
        if (cryptoPreferred) {
            // Align crypto balance to the right parent
            set.connect(R.id.balance_secondary, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, px8);
            mBalancePrimary.setPadding(0, Utils.getPixelsFromDps(WalletActivity.this, 22), 0, 0);
            mBalanceSecondary.setPadding(0, Utils.getPixelsFromDps(WalletActivity.this, 12), 0, 0);

            // Align swap icon to left of crypto balance
            set.connect(R.id.swap, ConstraintSet.END, R.id.balance_secondary, ConstraintSet.START, px8);

            // Align usd balance to left of swap icon
            set.connect(R.id.balance_primary, ConstraintSet.END, R.id.swap, ConstraintSet.START, px8);

            mBalanceSecondary.setTextSize(PRIMARY_TEXT_SIZE);
            mBalancePrimary.setTextSize(SECONDARY_TEXT_SIZE);

            set.applyTo(mToolBarConstraintLayout);

        } else {
            // CRYPTO on LEFT
            // Align primary to right of parent
            set.connect(R.id.balance_primary, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, px8);

            // Align swap icon to left of usd balance
            set.connect(R.id.swap, ConstraintSet.END, R.id.balance_primary, ConstraintSet.START, px8);


            // Align secondary currency to the left of swap icon
            set.connect(R.id.balance_secondary, ConstraintSet.END, R.id.swap, ConstraintSet.START, px8);
            mBalancePrimary.setPadding(0, Utils.getPixelsFromDps(WalletActivity.this, 12), 0, 0);
            mBalanceSecondary.setPadding(0, Utils.getPixelsFromDps(WalletActivity.this, 22), 0, 0);


            mBalanceSecondary.setTextSize(SECONDARY_TEXT_SIZE);
            mBalancePrimary.setTextSize(PRIMARY_TEXT_SIZE);

            set.applyTo(mToolBarConstraintLayout);
        }
        mBalanceSecondary.setTextColor(getResources().getColor(cryptoPreferred ? R.color.white : R.color.currency_subheading_color, null));
        mBalancePrimary.setTextColor(getResources().getColor(cryptoPreferred ? R.color.currency_subheading_color : R.color.white, null));
        mBalanceSecondary.setTypeface(FontManager.get(this, cryptoPreferred ? "CircularPro-Bold.otf" : "CircularPro-Book.otf"));
        mBalancePrimary.setTypeface(FontManager.get(this, !cryptoPreferred ? "CircularPro-Bold.otf" : "CircularPro-Book.otf"));

        updateUi();

    }

    @Override
    protected void onResume() {
        super.onResume();

        InternetManager.registerConnectionReceiver(this, this);

        TxManager.getInstance().onResume(this);

        RatesDataSource.getInstance(this).addOnDataChangedListener(this);
        final BaseWalletManager wallet = WalletsMaster.getInstance(this).getCurrentWallet(this);
        wallet.addTxListModifiedListener(this);
        BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {
                WalletEthManager.getInstance(WalletActivity.this).estimateGasPrice();
                wallet.refreshCachedBalance(WalletActivity.this);
                BRExecutor.getInstance().forMainThreadTasks().execute(new Runnable() {
                    @Override
                    public void run() {
                        updateUi();
                    }
                });
                if (wallet.getConnectStatus() != 2) {
                    wallet.connect(WalletActivity.this);
                }

            }
        });
        wallet.addBalanceChangedListener(this);

        mCurrentWalletIso = wallet.getIso();

        wallet.addSyncListener(this);

        mSyncNotificationBroadcastReceiver = new SyncNotificationBroadcastReceiver();
        SyncService.registerSyncNotificationBroadcastReceiver(WalletActivity.this.getApplicationContext(), mSyncNotificationBroadcastReceiver);
        SyncService.startService(this.getApplicationContext(), SyncService.ACTION_START_SYNC_PROGRESS_POLLING, mCurrentWalletIso);

        handleUrlClickIfNeeded(getIntent());
    }

    @Override
    protected void onPause() {
        super.onPause();
        InternetManager.unregisterConnectionReceiver(this, this);
        mWallet.removeSyncListener(this);
        SyncService.unregisterSyncNotificationBroadcastReceiver(WalletActivity.this.getApplicationContext(), mSyncNotificationBroadcastReceiver);
    }

    /* SyncListener methods */
    @Override
    public void syncStopped(String error) {

    }

    @Override
    public void syncStarted() {
        BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {
                SyncService.startService(WalletActivity.this.getApplicationContext(), SyncService.ACTION_START_SYNC_PROGRESS_POLLING, mCurrentWalletIso);
            }
        });
    }
    /* SyncListener methods End*/

    private void setUpBarFlipper() {
        mBarFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.flipper_enter));
        mBarFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.flipper_exit));
    }

    public void resetFlipper() {
        mBarFlipper.setDisplayedChild(0);
    }


    @Override
    public void onConnectionChanged(boolean isConnected) {
        Log.d(TAG, "onConnectionChanged: isConnected: " + isConnected);
        if (isConnected) {
            if (mBarFlipper != null && mBarFlipper.getDisplayedChild() == 2) {
                mBarFlipper.setDisplayedChild(0);
            }
            SyncService.startService(this.getApplicationContext(), SyncService.ACTION_START_SYNC_PROGRESS_POLLING, mCurrentWalletIso);

        } else {
            if (mBarFlipper != null) {
                mBarFlipper.setDisplayedChild(2);
            }

        }
    }

    @Override
    public void onBackPressed() {
        int c = getFragmentManager().getBackStackEntryCount();
        if (c > 0) {
            super.onBackPressed();
            return;
        }
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        if (!isDestroyed()) {
            finish();
        }
    }

    @Override
    public void txListModified(String hash) {
        BRExecutor.getInstance().forMainThreadTasks().execute(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("UI:" + TAG + ":updateUi");
                updateUi();
            }
        });
    }

    public void updateSyncProgress(double progress) {
        if (progress != SyncService.PROGRESS_FINISH) {
            StringBuffer labelText = new StringBuffer(getString(R.string.SyncingView_syncing));
            labelText.append(' ')
                     .append(NumberFormat.getPercentInstance().format(progress));
            mProgressLabel.setText(labelText);
            mProgressLayout.setVisibility(View.VISIBLE);

            if (mWallet instanceof BaseBitcoinWalletManager) {
                BaseBitcoinWalletManager baseBitcoinWalletManager = (BaseBitcoinWalletManager) mWallet;
                long syncThroughDateInMillis = baseBitcoinWalletManager.getPeerManager().getLastBlockTimestamp() * DateUtils.SECOND_IN_MILLIS;
                String syncedThroughDate = new SimpleDateFormat(SYNCED_THROUGH_DATE_FORMAT).format(syncThroughDateInMillis);
                mSyncStatusLabel.setText(String.format(getString(R.string.SyncingView_syncedThrough), syncedThroughDate));
            }
        } else {
            mProgressLayout.animate()
                    .translationY(-mProgressLayout.getHeight())
                    .alpha(SYNC_PROGRESS_LAYOUT_ANIMATION_ALPHA)
                    .setDuration(DateUtils.SECOND_IN_MILLIS)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mProgressLayout.setVisibility(View.GONE);
                        }
                    });
        }
    }

    @Override
    public void onChanged() {
        BRExecutor.getInstance().forMainThreadTasks().execute(new Runnable() {
            @Override
            public void run() {
                updateUi();
            }
        });
    }

    @Override
    public void onBalanceChanged(BigDecimal newBalance) {
        BRExecutor.getInstance().forMainThreadTasks().execute(new Runnable() {
            @Override
            public void run() {
                updateUi();
            }
        });
    }

    /**
     * The {@link SyncNotificationBroadcastReceiver} is responsible for receiving updates from the
     * {@link SyncService} and updating the UI accordingly.
     */
    private class SyncNotificationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SyncService.ACTION_SYNC_PROGRESS_UPDATE.equals(intent.getAction())) {
                String intentWalletIso = intent.getStringExtra(SyncService.EXTRA_WALLET_ISO);
                double progress = intent.getDoubleExtra(SyncService.EXTRA_PROGRESS, SyncService.PROGRESS_NOT_DEFINED);
                if (mCurrentWalletIso.equals(intentWalletIso)) {
                    if (progress >= SyncService.PROGRESS_START) {
                        WalletActivity.this.updateSyncProgress(progress);
                    } else {
                        Log.e(TAG, "SyncNotificationBroadcastReceiver.onReceive: Progress not set:" + progress);
                    }
                } else {
                    Log.e(TAG, "SyncNotificationBroadcastReceiver.onReceive: Wrong wallet. Expected:"
                            + mCurrentWalletIso + " Actual:" + intentWalletIso + " Progress:" + progress);
                }
            }
        }
    }

}
