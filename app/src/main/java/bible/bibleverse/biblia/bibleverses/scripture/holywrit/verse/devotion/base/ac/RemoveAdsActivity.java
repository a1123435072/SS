package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.SelfAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.devote.IabHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.devote.IabResult;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.devote.Inventory;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.devote.Purchase;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.devote.SkuDetails;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.devote.SkuItemHelp;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import yuku.afw.V;
import yuku.afw.storage.Preferences;

public class RemoveAdsActivity extends BaseActivity {
    IabHelper mHelper;

    private List<String> moreSubsSkus;

    private TextView mMonthlyPrice, mHalfYearPrice, mYearlyPrice, mDonateBtn, mSubscribeSuccessMsg, mTestPrice;
    private View mNoConnectionReminder, mPayLayout, mContentLayout, mSubscribeSuccessLayout, mMonthlyBtn, mHalfYearBtn, mYearlyBtn, mTestBtn;
    private ProgressBar mProgressBar;

    private String mPageFrom;

    private final static int RC_REQUEST = 10001;

    public static Intent createIntent(Context context, String pageFrom) {
        Intent intent = new Intent(context, RemoveAdsActivity.class);
        intent.putExtra(Constants.PAGE_FROM, pageFrom);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_remove);

        mHelper = new IabHelper(this, SkuItemHelp.getPublicKey());

        initView();

        initToolBar();

        mPageFrom = getIntent().getStringExtra(Constants.PAGE_FROM);

        initSKUList();

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh no, there was a problem.
//                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
                    showNoConnectionReminder();
                    return;
                }
                if (mHelper == null) {
                    showNoConnectionReminder();
                    return;
                }


                // Hooray, IAB is fully set up!
                try {
                    mHelper.queryInventoryAsync(true, moreSubsSkus, moreSubsSkus, mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
//                    Log.d(TAG, "Error querying inventory. Another async operation in progress.");
                } catch (NullPointerException e) {
                }

            }
        });

        mHelper.enableDebugLogging(false);
        SelfAnalyticsHelper.sendPaidAnalytics(this, AnalyticsConstants.C_REMOVE_ADS, AnalyticsConstants.A_ENTER, mPageFrom);
    }

    private void initView() {
        mNoConnectionReminder = V.get(this, R.id.no_connection_reminder);
        mProgressBar = V.get(this, R.id.pg);
        mContentLayout = V.get(this, R.id.content_layout);

        mPayLayout = V.get(this, R.id.pay_layout);
//        //test pay layout
//        mTestBtn = V.get(this, R.id.test_unit);
//        ((TextView) V.get(mTestBtn, R.id.subscibtion_time)).setText(R.string.one_month_short_name);
//        mTestPrice = V.get(mTestBtn, R.id.subscibtion_price);
//        V.get(mTestBtn, R.id.subscibtion_preoffer_price).setVisibility(View.GONE);

        //monthly pay layout
        mMonthlyBtn = V.get(this, R.id.one_month_unit);
        ((TextView) V.get(mMonthlyBtn, R.id.subscibtion_time)).setText(R.string.one_month);
        mMonthlyPrice = V.get(mMonthlyBtn, R.id.subscibtion_price);
        V.get(mMonthlyBtn, R.id.subscibtion_preoffer_price).setVisibility(View.GONE);

        //six month layout
        mHalfYearBtn = V.get(this, R.id.six_month_unit);
        ((TextView) V.get(mHalfYearBtn, R.id.subscibtion_time)).setText(R.string.six_months);
        mHalfYearPrice = V.get(mHalfYearBtn, R.id.subscibtion_price);
        TextView preofferPriceTv = V.get(mHalfYearBtn, R.id.subscibtion_preoffer_price);
        preofferPriceTv.setText(R.string.save_30);
        preofferPriceTv.setVisibility(View.VISIBLE);

        //yearly layout
        mYearlyBtn = V.get(this, R.id.one_year_unit);
        ((TextView) V.get(mYearlyBtn, R.id.subscibtion_time)).setText(R.string.one_year);
        mYearlyPrice = V.get(mYearlyBtn, R.id.subscibtion_price);
        TextView preofferYearlyPriceTv = V.get(mYearlyBtn, R.id.subscibtion_preoffer_price);
        preofferYearlyPriceTv.setText(R.string.save_50);
        preofferYearlyPriceTv.setVisibility(View.VISIBLE);

        //subscribe success layout
        mSubscribeSuccessLayout = V.get(this, R.id.id_remove_ads_success_layout);
        mSubscribeSuccessMsg = V.get(mSubscribeSuccessLayout, R.id.subscibtion_success_msg);
        mDonateBtn = V.get(this, R.id.donate_btn);
        mDonateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(DonateActivity.createIntent(RemoveAdsActivity.this, Constants.FROM_REMOVE_AD));
            }
        });
    }

    public void initToolBar() {
        Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        topToolBar.setNavigationIcon(R.drawable.ic_back_black);
        setTitle(getString(R.string.remove_ads));
    }

    private void initSKUList() {
        moreSubsSkus = SkuItemHelp.getSubsSkuList();
    }


    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
//            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) {
                showNoConnectionReminder();
                return;
            }

            // Is it a failure?
            if (result.isFailure()) {
//                Log.d(TAG, "Failed to query inventory: " + result);
                showNoConnectionReminder();
                return;
            }

            boolean isSubcirbionPurchased = false;
            String subcribeType = "";
//            Purchase skuTestMonthly = inventory.getPurchase(SkuItemHelp.SKU_99_MONTHLY);
            Purchase skuMonthly = inventory.getPurchase(SkuItemHelp.SKU_199_MONTHLY);
            Purchase skuHalfYear = inventory.getPurchase(SkuItemHelp.SKU_799_SIX_MONTHLY);
            Purchase skuYearly = inventory.getPurchase(SkuItemHelp.SKU_1199_YEARLY);
            if (skuMonthly != null /*&& skuMonthly.isAutoRenewing()*/) {
                isSubcirbionPurchased = true;
                subcribeType = getString(R.string.one_month_short_name);
            } else if (skuHalfYear != null /*&& skuHalfYear.isAutoRenewing()*/) {
                isSubcirbionPurchased = true;
                subcribeType = getString(R.string.six_months_short_name);
            } else if (skuYearly != null /*&& skuYearly.isAutoRenewing()*/) {
                isSubcirbionPurchased = true;
                subcribeType = getString(R.string.one_year_short_name);
            } /*else if (skuTestMonthly != null && skuTestMonthly.isAutoRenewing()) {
                isSubcirbionPurchased = true;
                subcribeType = "TEST";
            }*/
            if (isSubcirbionPurchased) {
                Preferences.setBoolean(getString(R.string.pref_ad_show), false);
                showSubscribeSuccessLayout(subcribeType);
                return;
            }

            handleSKU(inventory);

//            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    private void showSubscribeSuccessLayout(String type) {
        mContentLayout.setVisibility(View.VISIBLE);
        mPayLayout.setVisibility(View.GONE);
        mSubscribeSuccessLayout.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mNoConnectionReminder.setVisibility(View.GONE);
        mSubscribeSuccessMsg.setText(getString(R.string.ad_remove_success_msg, type));
    }


    private void handleSKU(Inventory inventory) {
        boolean isShowPayLayout = false;
        SkuDetails sku199Details = inventory.getSkuDetails(SkuItemHelp.SKU_199_MONTHLY);
        if (sku199Details != null) {
            showSubscribeInfo(mMonthlyPrice, mMonthlyBtn, sku199Details);
            isShowPayLayout = true;
        }
        SkuDetails sku799Details = inventory.getSkuDetails(SkuItemHelp.SKU_799_SIX_MONTHLY);
        if (sku799Details != null) {
            showSubscribeInfo(mHalfYearPrice, mHalfYearBtn, sku799Details);
            isShowPayLayout = true;
        }

        SkuDetails sku1199Details = inventory.getSkuDetails(SkuItemHelp.SKU_1199_YEARLY);
        if (sku1199Details != null) {
            showSubscribeInfo(mYearlyPrice, mYearlyBtn, sku1199Details);
            isShowPayLayout = true;
        }

//        SkuDetails skuTestDetails = inventory.getSkuDetails(SkuItemHelp.SKU_99_MONTHLY);
//        if (skuTestDetails != null) {
//            showSubscribeInfo(mTestPrice, mTestBtn, skuTestDetails);
//            isShowPayLayout = true;
//        }

        if (isShowPayLayout) {
            mContentLayout.setVisibility(View.VISIBLE);
            mPayLayout.setVisibility(View.VISIBLE);
            mSubscribeSuccessLayout.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            mNoConnectionReminder.setVisibility(View.GONE);
        } else {
            showNoConnectionReminder();
        }
    }

    private void showSubscribeInfo(TextView priceTv, View btn, final SkuDetails skuDetails) {
        priceTv.setText(skuDetails.getPrice());
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SelfAnalyticsHelper.sendPaidAnalytics(RemoveAdsActivity.this, AnalyticsConstants.C_REMOVE_ADS, AnalyticsConstants.A_CLICK_PAY, mPageFrom);
                    mHelper.launchPurchaseFlow(RemoveAdsActivity.this, skuDetails.getSku(), RC_REQUEST,
                            mPurchaseFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showNoConnectionReminder() {
        mContentLayout.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mNoConnectionReminder.setVisibility(View.VISIBLE);
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
//            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
//                Log.d(TAG, "Purchase failure result isFailure");
                return;
            }

            if (purchase == null) {
//                Log.d(TAG, "Purchase failure result isSuccess purchase == null");
                return;
            }
//            Log.d(TAG, "Purchase successful.");
            SelfAnalyticsHelper.sendPaidAnalytics(RemoveAdsActivity.this, AnalyticsConstants.C_REMOVE_ADS, AnalyticsConstants.A_PAID_SUCCESS, mPageFrom);

            String subcribeType = "";
            if (purchase.getSku().equalsIgnoreCase(SkuItemHelp.SKU_199_MONTHLY)) {
                subcribeType = getString(R.string.one_month_short_name);
            } else if (purchase.getSku().equalsIgnoreCase(SkuItemHelp.SKU_799_SIX_MONTHLY)) {
                subcribeType = getString(R.string.six_months_short_name);
            } else if (purchase.getSku().equalsIgnoreCase(SkuItemHelp.SKU_1199_YEARLY)) {
                subcribeType = getString(R.string.one_year_short_name);
            }/*else if (purchase.getSku().equals(SkuItemHelp.SKU_99_MONTHLY)) {
                subcribeType = "TEST";
            }*/

            if (!TextUtils.isEmpty(subcribeType)) {
                Preferences.setBoolean(getString(R.string.pref_ad_show), false);
                showSubscribeSuccessLayout(subcribeType);
            }

        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
//            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
//                Log.d(TAG, "Consumption successful. Provisioning. purchase:" + ((purchase == null) ? "null" : purchase.toString()));
                if (purchase != null) {
                }
            } else {
//                Log.d(TAG, "Consumption failure. Provisioning.");
            }
//            Log.d(TAG, "End consumption flow.");
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        } else {
//            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) {
            try {
                mHelper.dispose();
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            } finally {
                mHelper = null;
            }
        }
    }

}
