package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fw.basemodules.utils.OmAsyncTask;
import com.squareup.picasso.Picasso;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.SelfAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.UserBehaviorAnalytics;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventUpdateMyPlanLists;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventVerseOperate;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.ReadingPlan;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.DateTimeUtil;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.notification.DailyNotifyAlarm;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.protobuf.BPProto;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.service.BPService;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.PlanUtil;
import de.greenrobot.event.EventBus;
import yuku.afw.V;

public class PlansDetailActivity extends BaseActivity {
    public static final String TAG = PlansDetailActivity.class.getSimpleName();

    private TextView mNoConnectionText, mPlanTitleTv, mPlanDesTv, mPlanDayTv, mStartPlan;
    private ImageView mPlanImage;
    private ProgressBar mProgressbar;
    private Toolbar mToolbar;

    private final int IMG_WIDTH = 360;
    private final int IMG_HEIGHT = 203;

    private long mServerQueryPlanId;
    private String mPlanTitle;
    private int mPlanDay;
    private long mDbPlanId;
    private long mPlanStartTime;
    private RecordReadingPlanInfoTask mRecordReadingPlanInfoTask;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_detail);
        initToolbar();
        intView();
        handleIntent();
    }

    private void initToolbar() {
        mToolbar = V.get(this, R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle("");
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setBackgroundDrawable(null);
        ab.setHomeAsUpIndicator(R.drawable.ic_back_white);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
    }

    private void intView() {
        mNoConnectionText = V.get(this, R.id.no_connection_reminder);
        mPlanImage = V.get(this, R.id.plan_image);
        mPlanTitleTv = V.get(this, R.id.id_plan_title);
        mPlanDayTv = V.get(this, R.id.id_plan_day);
        mPlanDesTv = V.get(this, R.id.id_plan_description);
        mStartPlan = V.get(this, R.id.start_plan_btn);
        mProgressbar = V.get(this, R.id.pg);
        mStartPlan.setEnabled(false);
        setImageHeight();
    }

    private void setImageHeight() {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        ViewGroup.LayoutParams params = mPlanImage.getLayoutParams();
        if (params != null) {
            params.height = screenWidth * IMG_HEIGHT / IMG_WIDTH;
            mPlanImage.setLayoutParams(params);
        }
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            mServerQueryPlanId = intent.getLongExtra(Constants.KEY_SERVER_QUERY_PLAN_ID, 0);
            mPlanTitle = intent.getStringExtra(Constants.KEY_PLAN_NAME);
            mPlanDay = intent.getIntExtra(Constants.KEY_PLAN_DAY, 0);
            mPlanTitleTv.setText(mPlanTitle);
            mPlanDayTv.setText(getString(R.string.days, String.valueOf(mPlanDay)));
            if (mServerQueryPlanId > 0) {
                new LoadPlanDetail().execute();
            } else {
                showNoContentLayout();
            }

        }
    }

    private class LoadPlanDetail extends OmAsyncTask<Void, Void, BPProto.BP> {
        @Override
        protected void onPreExecute() {
            mProgressbar.setVisibility(View.VISIBLE);
            mNoConnectionText.setVisibility(View.GONE);
            mDbPlanId = -1;
            mPlanStartTime = 0;
            mStartPlan.setClickable(false);
        }

        @Override
        protected BPProto.BP doInBackground(Void... voids) {
            BPProto.BP bp = new BPService(PlansDetailActivity.this).getData(String.valueOf(mServerQueryPlanId));
            if (bp != null) {
                long[] res = S.getDb().getPlanIdAndStartTimeByPlanTitle(bp.getTitle());
                mDbPlanId = res[0];
                mPlanStartTime = res[1];
            }
            return bp;
        }

        @Override
        protected void onPostExecute(BPProto.BP bp) {
            mProgressbar.setVisibility(View.GONE);
            mStartPlan.setClickable(true);
            if (bp != null) {
                showDetail(bp);
            } else {
                showNoContentLayout();
            }
        }
    }

    private void goToPlanProgressPage(BPProto.BP bp) {
        PlanUtil.gotoPlanProgressDetailPage(this, mDbPlanId);
        finish();
    }

    private class RecordReadingPlanInfoTask extends OmAsyncTask<Void, Void, Void> {
        BPProto.BP mBp;

        public RecordReadingPlanInfoTask(BPProto.BP bp) {
            this.mBp = bp;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(PlansDetailActivity.this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ReadingPlan.ReadingPlanInfo planInfo = ReadingPlan.createPlanItemFromBP(this.mBp);
            planInfo.mStartTime = System.currentTimeMillis();
            planInfo.mIsReminderOpen = true;
            planInfo.mReminderTime = DateTimeUtil.covertToReminderTime(planInfo.mStartTime);
            mPlanStartTime = planInfo.mStartTime;
            mDbPlanId = S.getDb().insertReadingPlan(planInfo);
            DailyNotifyAlarm.setSinglePlanNotifyAlarm(PlansDetailActivity.this, DateTimeUtil.covertToReminderTime(mPlanStartTime), mDbPlanId, mPlanTitle, mBp.getImageUrl(), mPlanStartTime);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mProgressDialog != null && !isFinishing()) {
                mProgressDialog.dismiss();
            }
            EventBus.getDefault().post(new EventUpdateMyPlanLists());
            EventBus.getDefault().post(new EventVerseOperate(EventVerseOperate.PLANS));
            SelfAnalyticsHelper.sendPlanAnalytics(PlansDetailActivity.this, AnalyticsConstants.A_PLAN_SUBSCRIBE, mPlanTitle);
            UserBehaviorAnalytics.trackUserBehavior(PlansDetailActivity.this, AnalyticsConstants.P_PLANDETAILSPAGE, AnalyticsConstants.A_PLAN_SUBSCRIBE);
            goToPlanProgressPage(mBp);
        }
    }

    private void showDetail(final BPProto.BP bp) {
        mStartPlan.setEnabled(true);
        if (mDbPlanId != -1) {
            mStartPlan.setText(R.string.view_plan);
        } else {
            mStartPlan.setText(R.string.start_plan);
        }
        mStartPlan.setVisibility(View.VISIBLE);
        mNoConnectionText.setVisibility(View.GONE);
        mPlanTitle = bp.getTitle();
        mPlanTitleTv.setText(mPlanTitle);
        mPlanDayTv.setText(getString(R.string.days, String.valueOf(bp.getDaysCount())));
        mPlanDesTv.setText(bp.getPlanDes());
        Picasso.with(PlansDetailActivity.this).load(bp.getImageUrl()).into(mPlanImage);
        mStartPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDbPlanId == -1) {
                    mRecordReadingPlanInfoTask = new RecordReadingPlanInfoTask(bp);
                    mRecordReadingPlanInfoTask.execute();
                } else {
                    goToPlanProgressPage(bp);
                }
            }
        });
    }

    private void showNoContentLayout() {
        mProgressbar.setVisibility(View.GONE);
//        mNoConnectionText.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null && !isFinishing()) {
            mProgressDialog.dismiss();
        }
        if (mRecordReadingPlanInfoTask != null) {
            mRecordReadingPlanInfoTask.cancel(true);
        }
    }
}

