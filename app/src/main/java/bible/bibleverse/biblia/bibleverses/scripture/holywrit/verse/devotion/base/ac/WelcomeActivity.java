package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.SelfAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.DevotionBean;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PrayerBean;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.App;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventClickDailyNt;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.VerseItem;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.notification.NotificationBase;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import de.greenrobot.event.EventBus;
import yuku.afw.storage.Preferences;


public class WelcomeActivity extends BaseActivity {
    private Handler mHandler;
    private int mNotifyId;
    private static final int MIN_STAY_TIME = 1000;
    private static final int MAX_STAY_TIME = 3000;
    InterstitialAd mInterstitialAd;
    private boolean mInterstitialAdShowing = false;
    Runnable mJumpRunnable = new Runnable() {
        @Override
        public void run() {
            goJumpPage();
        }
    };

    Runnable mJumpRunnableTimeOut = new Runnable() {
        @Override
        public void run() {
            goJumpIntent();

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ImageView bg = (ImageView) findViewById(R.id.bg);
        Picasso.with(this).load(R.drawable.ic_welcome_bg).fit().centerCrop().into(bg);
        mHandler = new Handler();
        setHistoryData();
        AnalyticsHelper.getInstance(this).init(this);

        handleIntent();
    }

    private void setHistoryData() {
        TextView devotionNumTv = (TextView) findViewById(R.id.devotion_num);
        TextView prayNumTv = (TextView) findViewById(R.id.pray_num);
        TextView readTimeTv = (TextView) findViewById(R.id.read_time);
        View devotionCon = findViewById(R.id.devotion_container);
        View prayCon = findViewById(R.id.pray_container);
        View readTimeCon = findViewById(R.id.read_time_container);
        long devotionNum = Preferences.getLong(Constants.KEY_WELCOME_DEVOTION_COUNT, 0);
        long prayNum = Preferences.getLong(Constants.KEY_WELCOME_PRAY_COUNT, 0);
        long readTime = Preferences.getLong(Constants.KEY_WELCOME_READ_TIME, 0);
        if (devotionNum == 0) {
            devotionCon.setVisibility(View.GONE);
        } else {
            devotionCon.setVisibility(View.VISIBLE);
            devotionNumTv.setText(String.valueOf(devotionNum));
        }
        if (prayNum == 0) {
            prayCon.setVisibility(View.GONE);
        } else {
            prayCon.setVisibility(View.VISIBLE);
            prayNumTv.setText(String.valueOf(prayNum));
        }
        if (readTime < 1000 * 60) {
            readTimeCon.setVisibility(View.GONE);
        } else {
            readTimeCon.setVisibility(View.VISIBLE);
            readTimeTv.setText(Utility.formatDuring(readTime));
        }
    }

    private void goJumpIntent() {
        mHandler.postDelayed(mJumpRunnable, MIN_STAY_TIME);
    }

    private void goToHome() {
        Intent intent = new Intent(WelcomeActivity.this, NewMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.KEY_SOURCE_TYPE, Constants.KEY_TYPE_FROM_WELCOME);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
        finish();
    }

    private void goJumpPage() {
        if (mInterstitialAdShowing) {
            return;
        }
        startActivity(mJumpIntent);
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
        finish();
    }


    private Intent mJumpIntent;

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.getIntExtra(Constants.KEY_NOTIFICATION_ID, 0) != 0) {
            mNotifyId = intent.getIntExtra(Constants.KEY_NOTIFICATION_ID, 0);
            switch (mNotifyId) {
                case Constants.NOTIFY_DAILY_DEVOTION_ID:
                    DevotionBean listsBean = intent.getParcelableExtra(Constants.DEVOTION_BEAN);
                    mJumpIntent = DevotionDetailWebActivity.createIntent(this, listsBean);
                    break;
                case Constants.NOTIFY_DAILY_VERSE_ID:
                    String quote = intent.getStringExtra(Constants.DEVOTION_QUOTE);
                    String quoteRefer = intent.getStringExtra(Constants.DEVOTION_QUOTE_REFER);
                    int devotionId = intent.getIntExtra(Constants.DEVOTION_ID, 0);
                    String imageUrl = intent.getStringExtra(Constants.DEVOTION_IMAGE_URL);
                    mJumpIntent = NewDailyVerseDetailActivity.createIntent(this, devotionId, quote, quoteRefer, imageUrl);

                    break;
                case Constants.NOTIFY_DAILY_PRAYER_ID:
                    ArrayList<PrayerBean> prays = intent.getParcelableArrayListExtra(Constants.PRAYER_LIST);
                    String categoryName = intent.getStringExtra(Constants.PRAYER_CATEGORY);
                    int index = intent.getIntExtra(Constants.PRAYER_INDEX, 0);
                    if (prays == null) {
                        mJumpIntent = PrayerCategoryGridActivity.createIntent(this, Constants.NOTIFY_DAILY_PRAYER_ID, Constants.TYPE_SYSTEM_NOTIFICATION);
                    } else {
                        mJumpIntent = PrayerDetailActivity.createIntent(this, prays, index, categoryName, Constants.NOTIFY_DAILY_PRAYER_ID, Constants.TYPE_SYSTEM_NOTIFICATION);
                    }
                    //notify type will override AddIntentExtra
                    break;
                case Constants.NOTIFY_BIBLE_PLAN_ID:
                    mJumpIntent = new Intent(this, PlanDayDetailActivity.class);
                    mJumpIntent.putExtra(PlanDayDetailActivity.PLAN_ID, intent.getLongExtra(PlanDayDetailActivity.PLAN_ID, 0));
                    break;
            }
            NotificationBase.setShowingNotificationStatus(this, mNotifyId, false);
            AddIntentExtra(intent);
            EventBus.getDefault().post(new EventClickDailyNt());
            if (mNotifyId == Constants.NOTIFY_DAILY_PRAYER_ID) {
                int layoutType = 0;
                if (layoutType == 1002) {
                    onlyGoJumpIntent();
                } else {
                    judgeIfLoadAd();
                }
            } else {
                judgeIfLoadAd();
            }
        } else {
            mJumpIntent = new Intent(WelcomeActivity.this, NewMainActivity.class);
            mJumpIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            mJumpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mJumpIntent.putExtra(Constants.KEY_SOURCE_TYPE, Constants.KEY_TYPE_FROM_WELCOME);

            mHandler.postDelayed(mJumpRunnableTimeOut, MAX_STAY_TIME - MIN_STAY_TIME);
            SelfAnalyticsHelper.sendLoginSourceAnalytics(this, AnalyticsConstants.A_LOGIN_SOURCE_LAUNCH, null);
        }
    }

    private void judgeIfLoadAd() {
        if (App.sessionDepth > 0) {
            onlyGoJumpIntent();
        } else {
            mJumpIntent.putExtra(Constants.KEY_SOURCE_TYPE, Constants.KEY_TYPE_FROM_WELCOME);
            mHandler.postDelayed(mJumpRunnableTimeOut, MAX_STAY_TIME - MIN_STAY_TIME);
            SelfAnalyticsHelper.sendLoginSourceAnalytics(this, AnalyticsConstants.A_LOGIN_SOURCE_NOTIFY, String.valueOf(mNotifyId));
        }
    }

    private void onlyGoJumpIntent() {
        mJumpIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        mJumpIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mJumpIntent);
        finish();
    }

    private void AddIntentExtra(Intent intent) {
        mJumpIntent.putExtra(Constants.KEY_NOTIFICATION_ID, intent.getIntExtra(Constants.KEY_NOTIFICATION_ID, 0));
        mJumpIntent.putExtra(Constants.KEY_NOTIFICATION_TYPE, intent.getIntExtra(Constants.KEY_NOTIFICATION_TYPE, Constants.TYPE_FLOATING_NOTIFICATION));
        mJumpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static Intent createIntent(Context context, int notifyId, int notifyType, ArrayList<VerseItem> verseItems, long planId, DevotionBean listsBean) {
        Intent resultIntent = new Intent(context, WelcomeActivity.class);
        resultIntent.putExtra(Constants.KEY_NOTIFICATION_ID, notifyId);
        resultIntent.putExtra(Constants.KEY_NOTIFICATION_TYPE, notifyType);

        resultIntent.putExtra(Constants.KEY_VERSE_LIST, verseItems);

        resultIntent.putExtra(PlanDayDetailActivity.PLAN_ID, planId);

        resultIntent.putExtra(Constants.DEVOTION_BEAN, listsBean);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return resultIntent;
    }

    public static Intent createIntent(Context context, int notifyId, int notifyType, String date, String quote, String quoteRefer, int devotionId, String imageUrl) {
        Intent resultIntent = new Intent(context, WelcomeActivity.class);
        resultIntent.putExtra(Constants.KEY_NOTIFICATION_ID, notifyId);
        resultIntent.putExtra(Constants.KEY_NOTIFICATION_TYPE, notifyType);

        resultIntent.putExtra(Constants.DEVOTION_QUOTE, quote);
        resultIntent.putExtra(Constants.DEVOTION_QUOTE_REFER, quoteRefer);

        resultIntent.putExtra(Constants.DEVOTION_ID, devotionId);
        resultIntent.putExtra(Constants.DEVOTION_IMAGE_URL, imageUrl);
        resultIntent.putExtra(Constants.DEVOTION_DATE, date);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return resultIntent;
    }

    public static Intent createPrayerIntent(Context context, int notifyId, int notifyType, ArrayList<PrayerBean> prays, int index, String category) {
        return new Intent(context, WelcomeActivity.class)
                .putParcelableArrayListExtra(Constants.PRAYER_LIST, prays)
                .putExtra(Constants.PRAYER_INDEX, index)
                .putExtra(Constants.PRAYER_CATEGORY, category)
                .putExtra(Constants.KEY_NOTIFICATION_ID, notifyId)
                .putExtra(Constants.KEY_NOTIFICATION_TYPE, notifyType)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacks(mJumpRunnableTimeOut);
            mHandler.removeCallbacks(mJumpRunnable);
        }
        if (mInterstitialAd != null) {
            mInterstitialAd.setAdListener(null);
            mInterstitialAd.destroy();
        }
    }


}
