package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.wang.newversion.NAVLoadingIndicatorView;

import java.util.ArrayList;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.SelfAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.UserBehaviorAnalytics;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventCompletedDayPlan;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventUpdateMyPlanLists;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.VerseItem;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.DateTimeUtil;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.widget.VerseItemView;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.PlanUtil;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import de.greenrobot.event.EventBus;
import yuku.afw.V;
import yuku.alkitabintegration.display.Launcher;


/**
 * Created by Mr_ZY on 16/10/28.
 */

public class PlanVerseDetailActivity extends BaseActivity {
    private Toolbar mToolbar;
    private TextView mTime, mVerseTitle;
    private TextView mReadFull;
    private VerseItemView mVerseItemView;

    // ad
    private LinearLayout mBottomBannerAdLayout;
    AdView mBottomBannerAdView;
    private NAVLoadingIndicatorView mLoadingIndicator;
    private View mBottomView;

    private ArrayList<VerseItem> mVerseItems;
    private int mIndex;
    private FloatingActionButton mPreBtn, mNextBtn;
    private VerseItem mVerseItem;
    int mNotificationType;

    private boolean mIsAlreadyCompletedCurDayPlan;
    private int mCurDayIndex;
    private int mCurCompletedDayCount;
    private int mPlanDayCount;
    private int mPlanId;
    private String mPlanName;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_verse_detail);
        handleIntent();

        initToolBar();

        initView();

        if (mPlanId == -1) {
            mBottomView.setVisibility(View.GONE);
        } else {
            mBottomView.setVisibility(View.GONE);
        }
    }

    public static Intent createIntent(Context context, int ari, int count, String reference, String day) {
        return createIntent(context, ari, count, reference, day, null, false, 0);
    }

    public static Intent createIntent(Context context, int ari, int count, String reference, String day, String planTitle, boolean isAlreadyCompletedCurDay, int fromNotificationType) {
        ArrayList<VerseItem> verseItems = new ArrayList<>();
        verseItems.add(VerseItem.createVerseItem(ari, count, day, reference));
        Intent intent = createIntent(context, verseItems, planTitle, 0, -1, -1, -1, -1, isAlreadyCompletedCurDay);
        intent.putExtra(Constants.KEY_NOTIFICATION_TYPE, fromNotificationType);
        return intent;
    }

    public static Intent createIntent(Context context, ArrayList<VerseItem> verseItems, String plantName, int progressIndex, int planId, int curDayIndex, int plannedDayCount, int curCompletedCount, boolean isAlreadyCompletedCurDay) {
        Intent intent = new Intent(context, PlanVerseDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constants.KEY_VERSE_LIST, verseItems);
        intent.putExtra(Constants.KEY_VERSE_INDEX_IN_LIST, progressIndex);
        intent.putExtra(Constants.KEY_DB_PLAN_ID, planId);
        intent.putExtra(Constants.KEY_CURRENT_DAY, curDayIndex);
        intent.putExtra(Constants.KEY_PLANNED_DAY_COUNT, plannedDayCount);
        intent.putExtra(Constants.KEY_CURRENT_COMPLETED_DAY_COUNT, curCompletedCount);
        intent.putExtra(Constants.KEY_ALREADY_FINISH_CUR_DAY, isAlreadyCompletedCurDay);
        intent.putExtra(Constants.KEY_PLAN_NAME, plantName);
        return intent;
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            mVerseItems = (ArrayList<VerseItem>) intent.getSerializableExtra(Constants.KEY_VERSE_LIST);
            mIndex = intent.getIntExtra(Constants.KEY_VERSE_INDEX_IN_LIST, 0);

            mPlanId = intent.getIntExtra(Constants.KEY_DB_PLAN_ID, -1);
            mCurDayIndex = intent.getIntExtra(Constants.KEY_CURRENT_DAY, -1);
            mPlanDayCount = intent.getIntExtra(Constants.KEY_PLANNED_DAY_COUNT, -1);
            mCurCompletedDayCount = intent.getIntExtra(Constants.KEY_CURRENT_COMPLETED_DAY_COUNT, -1);
            mIsAlreadyCompletedCurDayPlan = intent.getBooleanExtra(Constants.KEY_ALREADY_FINISH_CUR_DAY, false);
            mNotificationType = intent.getIntExtra(Constants.KEY_NOTIFICATION_TYPE, 0);
            if (mNotificationType > 0) {//from daily verse notify
                SelfAnalyticsHelper.sendNotificationAnalytics(this, AnalyticsConstants.A_VERSE_NOTICE, AnalyticsConstants.L_VERSE_CLICK + "_" + mNotificationType);
                Utility.recordReadBibleStartTime(this);
            }
            mPlanName = intent.getStringExtra(Constants.KEY_PLAN_NAME);
        }
    }

    private void initView() {
        mVerseTitle = (TextView) findViewById(R.id.verse_title);
        mVerseItemView = V.get(this, R.id.verse_item_view);
        mTime = (TextView) findViewById(R.id.verse_time);
        mReadFull = (TextView) findViewById(R.id.read_full_chapter_btn);
        mPreBtn = V.get(this, R.id.bLeft);
        mNextBtn = V.get(this, R.id.bRight);

        mReadFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Launcher.openAppAtBibleLocationWithVerseSelected(mVerseItem.mAri, mVerseItem.mVerseCount));
                UserBehaviorAnalytics.trackUserBehavior(PlanVerseDetailActivity.this, mPlanId == -1 ? AnalyticsConstants.P_VERSEPAGE : AnalyticsConstants.P_PLANVERSEPAGE
                        , AnalyticsConstants.B_READFULL);
                finish();
            }
        });

        loadVerse();
        mPreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIndex == 0) {
                    return;
                }
                mIndex--;
                loadVerse();
                UserBehaviorAnalytics.trackUserBehavior(PlanVerseDetailActivity.this, AnalyticsConstants.P_PLANVERSEPAGE, AnalyticsConstants.B_PREVIOUS);

            }
        });
        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIndex == mVerseItems.size() - 1) {
                    if (!mIsAlreadyCompletedCurDayPlan) {
                        ++mCurCompletedDayCount;
                        S.getDb().setDayPlanCompleted(mPlanId, mCurDayIndex, mCurCompletedDayCount, mCurCompletedDayCount == mPlanDayCount);
                        EventBus.getDefault().post(new EventCompletedDayPlan());
                    }
                    if (mCurCompletedDayCount == mPlanDayCount) {
                        SelfAnalyticsHelper.sendPlanAnalytics(PlanVerseDetailActivity.this, AnalyticsConstants.A_PLAN_DONE, mPlanName);
                    }
                    PlanUtil.gotoPlanFinishPage(PlanVerseDetailActivity.this, mCurDayIndex, mPlanDayCount, mCurCompletedDayCount == mPlanDayCount, mPlanId);
                    EventBus.getDefault().post(new EventUpdateMyPlanLists());
                    UserBehaviorAnalytics.trackUserBehavior(PlanVerseDetailActivity.this, AnalyticsConstants.P_PLANVERSEPAGE, AnalyticsConstants.B_DONE);
                    finish();
                } else {
                    mIndex++;
                    loadVerse();
                    UserBehaviorAnalytics.trackUserBehavior(PlanVerseDetailActivity.this, AnalyticsConstants.P_PLANVERSEPAGE, AnalyticsConstants.B_NEXT);
                }
            }
        });
        if (mPlanId == -1) {
            mPreBtn.setVisibility(View.GONE);
            mNextBtn.setVisibility(View.GONE);
        }

        mLoadingIndicator = V.get(this, R.id.loading_indicator);
        mLoadingIndicator.setIndicatorColor(getResources().getColor(R.color.theme_color_accent));
        mBottomView = V.get(this, R.id.bottom_loading);
    }

    private void loadVerse() {
        mVerseItem = getCurrentVerseItem();
        if (mVerseItem == null) {
            return;
        }
        mVerseTitle.setText(mVerseItem.mReference);
        mVerseItemView.loadVerse(mVerseItem.mAri, mVerseItem.mVerseCount);

        String dateStr = mVerseItem.mDate;
        if (!TextUtils.isEmpty(dateStr)) {
            try {
                dateStr = DateTimeUtil.getLocaleDateStr4Display(mVerseItem.mDate);
            } catch (Exception e) {
            }
        }
        mTime.setText(dateStr);
        mPreBtn.setVisibility(View.VISIBLE);
        mNextBtn.setVisibility(View.VISIBLE);
        mPreBtn.setImageResource(R.drawable.ic_left_gray);
        mNextBtn.setImageResource(R.drawable.ic_right_white);
        if (mIndex == mVerseItems.size() - 1) {
            mNextBtn.setImageResource(R.drawable.ic_plan_complete);
        }
        if (mIndex == 0) {
            mPreBtn.setVisibility(View.GONE);
        }
    }

    private VerseItem getCurrentVerseItem() {
        if (mVerseItems == null || mVerseItems.isEmpty()) {
            return null;
        }
        if (mIndex > mVerseItems.size() - 1 || mIndex < 0) {
            mIndex = 0;
        }
        return mVerseItems.get(mIndex);
    }

    private void initToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.verse));
        mToolbar.setNavigationIcon(R.drawable.ic_back_black);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.activity_daily_verse, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.share_btn) {
            if (mVerseItem != null) {
                S.copyOrShareVerse(this, mVerseItem.mAri, mVerseItem.mVerseCount, true);
            }
            UserBehaviorAnalytics.trackUserBehavior(PlanVerseDetailActivity.this, AnalyticsConstants.P_VERSEPAGE, AnalyticsConstants.B_SHARE);
        } else if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBottomBannerAdView != null) {
            mBottomBannerAdView.destroy();
        }
    }

}
