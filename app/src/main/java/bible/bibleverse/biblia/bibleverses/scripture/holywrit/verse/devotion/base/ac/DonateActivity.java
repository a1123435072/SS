package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cry.loopviews.LoopViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.devote.IabHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.devote.IabResult;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.devote.Inventory;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.devote.Purchase;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.devote.SkuDetails;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.devote.SkuItemHelp;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;

public class DonateActivity extends BaseActivity {

    IabHelper mHelper;

    private List<String> moreItemSkus;
    private List<String> moreSubsSkus;

    private TextView donateBtn1, donateBtn2, donateBtn3, donateBtn4;
    private View noConnectionReminder, donateLayout;
    private ProgressBar pg;
    private LoopViewPager mLoop;

    private static String[] payments = {
            "$1.99",
            "$9.99",
            "$19.99",
            "$29.99"
    };
    private static String[] userNames = {
            "Darren Wilson",
            "Abel Apple",
            "Laura Jones",
            "Stacey Castle",
            "Lynda Downs",
            "Helin Wilson",
            "Kimberly Thompson",
            "Grady Dennis",
            "Milne Bull",
            "Sherri Dickinson",
            "Bridget Walker",
            "Blume Rose",
            "Victor Nixon",
            "Veronica Apple",
            "Kingsley Grove",
            "Chapman Goodal",
            "Janis Moore",
            "Bertie Green",
            "Allan Brown",
            "Dixie Brown",
            "Harriet Evans",
            "Geordie Bell",
            "Marcus Jones",
            "Sharp Taylor",
            "Hazlitt Taylor",
            "Wilfred Lewis",
            "Ruth Goodal",
            "Fast Hill",
            "Bacon Lewis",
            "Kennedy Davies",
            "Harper Moore",
            "Juliana Thompson",
            "John Ford",
            "Estelle Rose",
            "Kitty Bridges",
            "Antonio Patel",
            "Crystal Moore",
            "Ernest Fleming",
            "Joe Jones",
            "Shaun Smith",
            "Ford French",
            "Rayleign Apple",
            "Manuel Street",
            "Rosie Wilson",
            "Morse French",
            "Norris Street",
            "Longman Dickinson",
            "Amelia Dennis",
            "Pater Simpson",
            "Browne Moore",
            "Patsy Vine",
            "Nicol Nixon",
            "Priscilla Green",
            "Nehemiah Hughes",
            "Gibson French",
            "Cody White",
            "Virginia Taylor",
            "Ann Simpson",
            "Jackie Pardie",
            "Richards Voyager",
            "Shawna Simpson",
            "Emmy Davies",
            "Judith Dennis",
            "Alerander Roberts",
            "Denis Roberts",
            "Kent Thompson",
            "Joseph Banks",
            "Chris Castle",
            "Abe White",
            "Alton Towers",
            "Jessica Dickinson",
            "Rosa Dickinson",
            "Bennie Hughes",
            "Emanuel Goodal",
            "Simon Voyager",
            "Clemens Brown",
            "Joann Norman",
            "Terry White",
            "Veronica Lewis",
            "Keith Hughes",
            "Nancy Brown",
            "Faraday Conquest",
            "Pepys Drum",
            "Kristie Nixon",
            "Aledk Dennis",
            "Timmy Moore",
            "Toby Jackson",
            "Annie Voyager",
            "Mercedes Hill",
            "Arnold Wright",
            "Sheri Voyager",
            "Ruth Thompson",
            "Cotton Hughes",
            "Bacon Smith",
            "Ervin Castle",
            "Vincent Jackson",
            "Megan Rivers",
            "Pablo Fleming",
            "Birrell Evans",
            "Randal Conquest"
    };


    private int userNameIdx = 0;

    private String getUserName() {
        userNameIdx++;
        if (userNameIdx >= userNames.length) {
            userNameIdx = 0;
        }
        return userNames[userNameIdx];
    }

    private String getPayment() {
        int idx = new Random().nextInt(4);
        return payments[idx];
    }

    private static final int MSG_INIT_USER = 0;
    private static final int MSG_CHANGE_USER = 1;

    private String mPageFrom;

    private final static int RC_REQUEST = 10001;

    public static Intent createIntent(Context context, String pageFrom) {
        Intent intent = new Intent(context, DonateActivity.class);
        intent.putExtra(Constants.PAGE_FROM, pageFrom);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

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
                    mHelper.queryInventoryAsync(true, moreItemSkus, moreSubsSkus, mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
//                    Log.d(TAG, "Error querying inventory. Another async operation in progress.");
                } catch (NullPointerException e) {
                }

            }
        });
        mHelper.enableDebugLogging(false);
    }

    private void initView() {
        noConnectionReminder = findViewById(R.id.no_connection_reminder);
        pg = (ProgressBar) findViewById(R.id.pg);
        donateLayout = findViewById(R.id.donate_layout);
        donateBtn1 = (TextView) findViewById(R.id.pay_btn1);
        donateBtn2 = (TextView) findViewById(R.id.pay_btn2);
        donateBtn3 = (TextView) findViewById(R.id.pay_btn3);
        donateBtn4 = (TextView) findViewById(R.id.pay_btn4);

        mLoop = (LoopViewPager) findViewById(R.id.loop_pager);
        mLoop.setTextId(R.string.donate_tip_1);
        List<View> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            View v = LayoutInflater.from(this).inflate(R.layout.donate_textview, null);
            list.add(v);
        }
        mLoop.setViewList(list, userNames, payments);
        mLoop.setAutoChangeTime(3000);
        mLoop.setHorizontal(false);
        mLoop.setAutoChange(true);
    }

    public void initToolBar() {
        Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        topToolBar.setNavigationIcon(R.drawable.ic_back_black);
        setTitle(getString(R.string.donate));
    }

    private void initSKUList() {
        moreItemSkus = SkuItemHelp.getSkuItemList();
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

            Purchase sku199 = inventory.getPurchase(SkuItemHelp.SKU_199);
            Purchase sku999 = inventory.getPurchase(SkuItemHelp.SKU_999);
            Purchase sku1999 = inventory.getPurchase(SkuItemHelp.SKU_1999);
            Purchase sku2999 = inventory.getPurchase(SkuItemHelp.SKU_2999);
            if (sku199 != null) {
                try {
                    mHelper.consumeAsync(sku199, mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                }
            } else if (sku999 != null) {
                try {
                    mHelper.consumeAsync(sku999, mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                }
            } else if (sku1999 != null) {
                try {
                    mHelper.consumeAsync(sku1999, mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                }
            } else if (sku2999 != null) {
                try {
                    mHelper.consumeAsync(sku2999, mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                }
            }

            for (String sku : moreItemSkus) {
                handleSKU(inventory, sku);
            }

//            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    private void handleSKU(Inventory inventory, String sku) {
        SkuDetails skuDetails = inventory.getSkuDetails(sku);
        if (skuDetails != null) {
//            Log.d(TAG, "sku:" + sku + " " + skuDetails.toString());
            TextView tv = null;
            if (sku.equalsIgnoreCase(SkuItemHelp.SKU_199)) {
                tv = donateBtn1;
            } else if (sku.equalsIgnoreCase(SkuItemHelp.SKU_999)) {
                tv = donateBtn2;
            } else if (sku.equalsIgnoreCase(SkuItemHelp.SKU_1999)) {
                tv = donateBtn3;
            } else if (sku.equalsIgnoreCase(SkuItemHelp.SKU_2999)) {
                tv = donateBtn4;
            }
            if (tv != null) {
                donateLayout.setVisibility(View.VISIBLE);
                pg.setVisibility(View.GONE);
                noConnectionReminder.setVisibility(View.GONE);
                showSKUUI(skuDetails, sku, tv);
            } else {
                showNoConnectionReminder();
            }
        }
    }

    private void showNoConnectionReminder() {
        donateLayout.setVisibility(View.GONE);
        pg.setVisibility(View.GONE);
        noConnectionReminder.setVisibility(View.VISIBLE);
    }

    private void showSKUUI(SkuDetails skuDetails, final String skuId, TextView tv) {
        tv.setText(skuDetails.getPrice());
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mHelper.launchPurchaseFlow(DonateActivity.this, skuId, RC_REQUEST,
                            mPurchaseFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
            }
        });
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
//            if (!verifyDeveloperPayload(purchase)) {
//                complain("Error purchasing. Authenticity verification failed.");
//                setWaitScreen(false);
//                return;
//            }

            if (purchase == null) {
//                Log.d(TAG, "Purchase failure result isSuccess purchase == null");
                return;
            }
//            Log.d(TAG, "Purchase successful.");

            if (purchase.getSku().equals(SkuItemHelp.SKU_199)
                    || purchase.getSku().equalsIgnoreCase(SkuItemHelp.SKU_999)
                    || purchase.getSku().equalsIgnoreCase(SkuItemHelp.SKU_1999)
                    || purchase.getSku().equalsIgnoreCase(SkuItemHelp.SKU_2999)) {
                try {
                    mHelper.consumeAsync(purchase, mConsumeFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    return;
                }
            }/* else if (purchase.getSku().equals(SKU_TEST2)) {
                // bought the premium upgrade!
                Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
            }*/
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
        if (mLoop != null) {
            mLoop.destroy();
        }
    }
}
