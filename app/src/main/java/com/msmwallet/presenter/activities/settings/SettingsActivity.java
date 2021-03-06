package com.msmwallet.presenter.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;

import com.msmwallet.R;
import com.msmwallet.presenter.activities.CurrencySettingsActivity;
import com.msmwallet.presenter.activities.ManageWalletsActivity;
import com.msmwallet.presenter.activities.UpdatePinActivity;
import com.msmwallet.presenter.entities.BRSettingsItem;
import com.msmwallet.tools.adapter.SettingsAdapter;
import com.msmwallet.tools.manager.BRSharedPrefs;
import com.msmwallet.wallet.WalletsMaster;
import com.msmwallet.wallet.wallets.bitcoin.WalletBitcoinManager;
import com.msmwallet.wallet.wallets.ethereum.WalletEthManager;
import com.platform.entities.TokenListMetaData;
import com.platform.tools.KVStoreManager;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends BaseSettingsActivity {
    private static final String TAG = SettingsActivity.class.getName();
    private ListView listView;
    public List<BRSettingsItem> items;
    public static boolean appVisible = false;
    private static SettingsActivity app;

    public static SettingsActivity getApp() {
        return app;
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listView = findViewById(R.id.settings_list);


    }


    @Override
    protected void onResume() {
        super.onResume();
        appVisible = true;
        app = this;
        if (items == null)
            items = new ArrayList<>();
        items.clear();

        populateItems();
        listView.addFooterView(new View(this), null, true);
        listView.setAdapter(new SettingsAdapter(this, R.layout.settings_list_item, items));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    private void populateItems() {

        items.add(new BRSettingsItem(getString(R.string.Settings_wallet), "", null, true, 0));
        //钱包管理
        items.add(new BRSettingsItem(getString(R.string.TokenList_manageTitle), "", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ManageWalletsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        }, false, R.drawable.chevron_right_light));


        items.add(new BRSettingsItem(getString(R.string.Settings_wipe), "", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, UnlinkActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        }, false, R.drawable.chevron_right_light));


        items.add(new BRSettingsItem(getString(R.string.Settings_preferences), "", null, true, 0));

        items.add(new BRSettingsItem(getString(R.string.UpdatePin_updateTitle), "", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, UpdatePinActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        }, false, R.drawable.chevron_right_light));

        items.add(new BRSettingsItem(getString(R.string.Settings_currency), BRSharedPrefs.getPreferredFiatIso(this), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, DisplayCurrencyActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        }, false, R.drawable.chevron_right_light));


        items.add(new BRSettingsItem(getString(R.string.Settings_currencySettings), "", null, true, 0));

        final WalletBitcoinManager btcWallet = WalletBitcoinManager.getInstance(app);
        if (btcWallet.getSettingsConfiguration().mSettingList.size() > 0)
            items.add(new BRSettingsItem(btcWallet.getName(), "", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SettingsActivity.this, CurrencySettingsActivity.class);
                    BRSharedPrefs.putCurrentWalletIso(app, btcWallet.getIso()); //change the current wallet to the one they enter settings to
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                }
            }, false, R.drawable.chevron_right_light));
//        final WalletBchManager bchWallet = WalletBchManager.getInstance(app);
//        if (bchWallet.getSettingsConfiguration().mSettingList.size() > 0)
//            items.add(new BRSettingsItem(WalletBchManager.getInstance(app).getName(), "", new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(SettingsActivity.this, CurrencySettingsActivity.class);
//                    BRSharedPrefs.putCurrentWalletIso(app, bchWallet.getIso());//change the current wallet to the one they enter settings to
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
//                }
//            }, false, R.drawable.chevron_right_light));
        final WalletEthManager ethWallet = WalletEthManager.getInstance(app);
        if (ethWallet.getSettingsConfiguration().mSettingList.size() > 0)
            items.add(new BRSettingsItem(WalletEthManager.getInstance(app).getName(), "", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SettingsActivity.this, CurrencySettingsActivity.class);
                    BRSharedPrefs.putCurrentWalletIso(app, ethWallet.getIso());//change the current wallet to the one they enter settings to
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                }
            }, false, R.drawable.chevron_right_light));

        // 设置中  重置已添加的钱包
//        items.add(new BRSettingsItem(getString(R.string.Tokens_Reset), "", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                resetToDefaultCurrencies();
//            }
//        }, false, 0));

        items.add(new BRSettingsItem(getString(R.string.Settings_other), "", null, true, 0));

        String shareAddOn = BRSharedPrefs.getShareData(SettingsActivity.this) ? getString(R.string.PushNotifications_on) : getString(R.string.PushNotifications_off);

        items.add(new BRSettingsItem(getString(R.string.Settings_shareData), shareAddOn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ShareDataActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        }, false, R.drawable.chevron_right_light));

//        items.add(new BRSettingsItem(getString(R.string.Settings_review), "", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    Intent appStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.breadwallet"));
//                    appStoreIntent.setPackage("com.android.vending");
//
//                    startActivity(appStoreIntent);
//                } catch (android.content.ActivityNotFoundException exception) {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.breadwallet")));
//                }
//                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
//            }
//        }, false, R.drawable.arrow_leave));

//        items.add(new BRSettingsItem(getString(R.string.About_title), "", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(SettingsActivity.this, AboutActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
//            }
//        }, false, R.drawable.chevron_right_light));

        items.add(new BRSettingsItem(getString(R.string.Settings_advancedTitle), "", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, AdvancedActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
            }
        }, false, R.drawable.chevron_right_light));


    }

    private void resetToDefaultCurrencies() {

        TokenListMetaData tokenMeta = KVStoreManager.getInstance().getTokenListMetaData(this);

        tokenMeta.enabledCurrencies = new ArrayList<>();

        TokenListMetaData.TokenInfo btc = new TokenListMetaData.TokenInfo("BTC", false, null);
        TokenListMetaData.TokenInfo bch = new TokenListMetaData.TokenInfo("BCH", false, null);
        TokenListMetaData.TokenInfo eth = new TokenListMetaData.TokenInfo("ETH", false, null);
        TokenListMetaData.TokenInfo brd = new TokenListMetaData.TokenInfo("BRD", true, null);

        tokenMeta.enabledCurrencies.add(btc);
        tokenMeta.enabledCurrencies.add(bch);
        tokenMeta.enabledCurrencies.add(eth);
        tokenMeta.enabledCurrencies.add(brd);


        // Publish the changes back to the KVStore
        KVStoreManager.getInstance().putTokenListMetaData(this, tokenMeta);

        // Notify WalletsMaster so the reset will be reflected on the Home Screen
        WalletsMaster.getInstance(this).updateWallets(this);

        // Go back to Home Screen
        onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        appVisible = false;
    }
}
