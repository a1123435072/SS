package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fw.basemodules.utils.OmAsyncTask;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.SelfAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.UserBehaviorAnalytics;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventCompletedDayPlan;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventUpdateMyPlanLists;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.ReadingPlan;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.VerseItem;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.storage.Db;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.DateTimeUtil;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.notification.DailyNotifyAlarm;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.protobuf.BPProto;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.service.BPPlanService;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.PlanUtil;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import stikkyheader.core.StikkyHeaderBuilder;
import de.greenrobot.event.EventBus;
import yuku.afw.V;

/**
 * Created by yzq on 16/11/7.
 */

public class PlanDayDetailActivity extends BaseActivity {

    public static final String PLAN_ID = "plan_id";

    private TextView mTitle;
    private ProgressBar mProgressBar;
    private LinearLayout mMainLayout;
    private ImageView mTopImageView;
    private TextView mDayTipTextView;
    private TextView mDateTextView;
    private RecyclerView mPlanListView;
    ListView mProgressListView;
    private TextView mReadBtn;
    private MenuItem menuItemReminder;

    private DayPlanAdapter mDayPlanAdapter;
    private DayProgressAdapter mDayProgressAdapter;

    private ReadingPlan.ReadingPlanInfo mPlanInfo;
    private HashMap<String, ReadingPlan.ReadingDayPlanInfo> mDayPlans;
    private String mSelectedDatePointStr;
    private ReadingPlan.ReadingDayPlanInfo mSelectedDayPlan;
    private Calendar[] mPlanScopeDays;
    private long mPlanId;
    int mNotifyId;
    LayoutInflater mLayoutInflater;


    public static Intent createIntent(Context context, long planId) {
        Intent intent = new Intent(context, PlanDayDetailActivity.class);
        intent.putExtra(PLAN_ID, planId);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_plan_day_detail);

        initToolbar();

        handleIntent();

        initView();

        init();
    }

    private void initToolbar() {
        final Toolbar toolbar = V.get(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_back_black);
        ab.setTitle(null);
    }

    private void init() {
        mLayoutInflater = LayoutInflater.from(this);
        mDayPlans = new HashMap<>();
        mDayPlanAdapter = new DayPlanAdapter();
        mPlanListView.setAdapter(mDayPlanAdapter);
        mDayProgressAdapter = new DayProgressAdapter(PlanDayDetailActivity.this);
        mProgressListView.setAdapter(mDayProgressAdapter);

        new LoadPlanInfoTask().execute((int) mPlanId);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }

        mNotifyId = intent.getIntExtra(Constants.KEY_NOTIFICATION_ID, 0);
        if (mNotifyId > 0) {
            SelfAnalyticsHelper.sendNotificationAnalytics(this, AnalyticsConstants.A_PLAN_NOTICE, AnalyticsConstants.L_PLAN_CLICK);
            Utility.recordReadBibleStartTime(this);
        }
        mPlanId = intent.getLongExtra(PLAN_ID, 0);
    }

    private void initView() {
        mTitle = V.get(this, R.id.my_title);
        mProgressBar = V.get(this, R.id.pg);
        mTopImageView = V.get(this, R.id.topBigImage);
        mDayTipTextView = V.get(this, R.id.dayTip);
        mDateTextView = V.get(this, R.id.dateTv);
        mDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });
        mPlanListView = V.get(this, R.id.dayPlanList);
        mMainLayout = V.get(this, R.id.main_layout);
        mProgressListView = V.get(this, R.id.dayPlanProgressList);

        StikkyHeaderBuilder.stickTo(mProgressListView).setHeader(mMainLayout).minHeightHeader(0).build();

        mReadBtn = V.get(this, R.id.readBtn);
        mReadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedDayPlan != null && mSelectedDayPlan.mDayPlanProgresses != null) {
                    gotoVerseDetailPage(0);
                }
            }
        });
    }

    private void updateSelectedDay() {
        if (mDayPlans.containsKey(mSelectedDatePointStr)) {
            updateView();
        } else {
            new LoadSelectedMonthPlanDayDetailTask().execute(mSelectedDatePointStr);
        }
    }

    private void updateView() {
        mDateTextView.setText(mSelectedDatePointStr);

        mDayPlanAdapter.updateCalendar(mSelectedDatePointStr);
        mDayPlanAdapter.notifyDataSetChanged();

        mSelectedDayPlan = mDayPlans.get(mSelectedDatePointStr);
        if (mSelectedDayPlan != null) {
            mDayTipTextView.setText(getString(R.string.plan_finish_have_read_days, String.valueOf(mSelectedDayPlan.mDayIndex + 1), String.valueOf(mPlanInfo.mPlannedDayCount)));
        }
        mDayProgressAdapter.notifyDataSetChanged();

        int pos = DateTimeUtil.getDayFromPointFormat(mSelectedDatePointStr) - 1;
        mPlanListView.scrollToPosition(pos < 0 ? 0 : pos);
    }

    private void updatePlanProgress() {
        boolean oldStatus = mSelectedDayPlan.mIsCompleted;
        mSelectedDayPlan.updateCompleted();
        if (mSelectedDayPlan.mIsCompleted != oldStatus) {
            mDayPlanAdapter.notifyDataSetChanged();
            if (mSelectedDayPlan.mIsCompleted) {
                mPlanInfo.mCompletedDayCount += 1;
            } else {
                mPlanInfo.mCompletedDayCount -= 1;
            }
            S.getDb().updateReadingPlanCompletedDayCount(mPlanInfo.mDbPlanId, mPlanInfo.mCompletedDayCount, mPlanInfo.mCompletedDayCount == mPlanInfo.mPlannedDayCount);
            EventBus.getDefault().post(new EventUpdateMyPlanLists());
        }
        if (mSelectedDayPlan.mIsCompleted) {
            PlanUtil.gotoPlanFinishPage(PlanDayDetailActivity.this, mSelectedDayPlan.mDayIndex, mPlanInfo.mPlannedDayCount, mPlanInfo.mCompletedDayCount == mPlanInfo.mPlannedDayCount, mPlanInfo.mDbPlanId);
            if (mPlanInfo.mCompletedDayCount == mPlanInfo.mPlannedDayCount) {
                DailyNotifyAlarm.cancelSinglePlanNotifyAlarm(PlanDayDetailActivity.this, mPlanInfo.mDbPlanId, mPlanInfo.mTitle, mPlanInfo.mBigImageUrl, mPlanInfo.mStartTime);
                SelfAnalyticsHelper.sendPlanAnalytics(this, AnalyticsConstants.A_PLAN_DONE, mPlanInfo.mTitle);
            }
        }
    }

    private void gotoVerseDetailPage(int curProgressIndex) {
        ArrayList<VerseItem> verseItems = new ArrayList<>();
        for (int j = 0; j < mSelectedDayPlan.mDayPlanProgresses.size(); ++j) {
            ReadingPlan.ReadingDayPlanProgressInfo progressInfo = mSelectedDayPlan.mDayPlanProgresses.get(j);
            String reference = S.getVerseReference(progressInfo.mAri, progressInfo.mVerseCount);
            VerseItem verseItem = new VerseItem(progressInfo.mAri, progressInfo.mVerseCount, "", reference);
            verseItems.add(verseItem);
        }

        Intent intent = PlanVerseDetailActivity.createIntent(
                PlanDayDetailActivity.this,
                verseItems,
                mPlanInfo.mTitle,
                curProgressIndex,
                (int) mPlanInfo.mDbPlanId,
                mSelectedDayPlan.mDayIndex,
                mPlanInfo.mPlannedDayCount,
                mPlanInfo.mCompletedDayCount,
                mSelectedDayPlan.mIsCompleted);
        startActivity(intent);
    }


    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
            mSelectedDatePointStr = DateTimeUtil.getPointFormatDateStr(year, monthOfYear, dayOfMonth);
            updateSelectedDay();
        }
    };

    private void selectDate() {
        Calendar calendar = DateTimeUtil.getCalendarFromPointFormat(mSelectedDatePointStr);
        if (calendar == null) {
            calendar = Calendar.getInstance();
        }
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setThemeDark(false);
        dpd.vibrate(true);
        dpd.dismissOnPause(true);
        dpd.showYearPickerFirst(false);
        if (mPlanScopeDays != null && mPlanScopeDays.length > 0) {
            dpd.setSelectableDays(mPlanScopeDays);
            dpd.setHighlightedDays(mPlanScopeDays);
        }
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }


    private AlertDialog mDeleteDialog;

    private void stopPlanClick() {
        mDeleteDialog = new AlertDialog.Builder(PlanDayDetailActivity.this)
                .setMessage(getString(R.string.confirm_delete_plan))
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.stop, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        S.getDb().deleteReadingPlan(mPlanInfo.mDbPlanId);
                        EventBus.getDefault().post(new EventUpdateMyPlanLists());
                        DailyNotifyAlarm.cancelSinglePlanNotifyAlarm(PlanDayDetailActivity.this, mPlanInfo.mDbPlanId, mPlanInfo.mTitle, mPlanInfo.mBigImageUrl, mPlanInfo.mStartTime);

                        dialog.dismiss();
                        finish();
                    }
                }).create();
        mDeleteDialog.show();
    }

    private TimePickerDialog.OnTimeSetListener mReminderOnListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
            S.getDb().updateReadingPlanReminderInfo(mPlanInfo.mDbPlanId, true, hourOfDay * 100 + minute);
            mPlanInfo.mIsReminderOpen = true;
            mPlanInfo.mReminderTime = hourOfDay * 100 + minute;
            updateMenuItem();
            DailyNotifyAlarm.setSinglePlanNotifyAlarm(PlanDayDetailActivity.this, mPlanInfo.mReminderTime, mPlanInfo.mDbPlanId, mPlanInfo.mTitle, mPlanInfo.mBigImageUrl, mPlanInfo.mStartTime);
        }
    };

    private DialogInterface.OnCancelListener mReminderOffListener = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            S.getDb().updateReadingPlanReminderInfo(mPlanInfo.mDbPlanId, false, -1);
            mPlanInfo.mIsReminderOpen = false;
            updateMenuItem();
            DailyNotifyAlarm.cancelSinglePlanNotifyAlarm(PlanDayDetailActivity.this, mPlanInfo.mDbPlanId, mPlanInfo.mTitle, mPlanInfo.mBigImageUrl, mPlanInfo.mStartTime);
        }
    };

    private void setReminderTime() {
        int[] reminderTime = DateTimeUtil.parseReminderTime(mPlanInfo.mReminderTime);
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                mReminderOnListener,
                reminderTime[0],
                reminderTime[1],
                true
        );
        tpd.setCancelText(R.string.turn_off);
        tpd.setThemeDark(false);
        tpd.vibrate(true);
        tpd.dismissOnPause(true);
        tpd.setOnCancelListener(mReminderOffListener);
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.activity_plan_day_detail, menu);
        return true;
    }

    private void updateMenuItem() {
        if (menuItemReminder != null) {
            String showString = getString(R.string.dr_reminder);
            if (mPlanInfo != null && mPlanInfo.mIsReminderOpen) {
                showString += " (" + DateTimeUtil.reminderTimeToDisplay(mPlanInfo.mReminderTime) + ")";
            } else {
                showString += " (" + getString(R.string.dr_off) + ")";
            }
            menuItemReminder.setTitle(showString);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menuItemReminder = menu.findItem(R.id.menuReminder);
        updateMenuItem();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.stop_plan:
                stopPlanClick();
                UserBehaviorAnalytics.trackUserBehavior(this, AnalyticsConstants.P_PLANPROGRAMPAGE, AnalyticsConstants.B_STOPPLAN);
                return true;
            case R.id.menuReminder:
                setReminderTime();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onEventMainThread(EventCompletedDayPlan event) {
        if (event == null) {
            return;
        }
        new LoadSelectedMonthPlanDayDetailTask().execute(mSelectedDatePointStr);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    class LoadPlanInfoTask extends OmAsyncTask<Integer, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            mReadBtn.setVisibility(View.GONE);
        }

        @Override
        protected Boolean doInBackground(Integer... integers) {
            if (integers.length > 0) {
                // load plan info from db
                mPlanInfo = S.getDb().getReadingPlanInfo(integers[0]);
                if (mPlanInfo != null) {
                    // init plan scope
                    mPlanScopeDays = new Calendar[mPlanInfo.mPlannedDayCount];
                    for (int i = 0; i < mPlanInfo.mPlannedDayCount; i++) {
                        Calendar date = Calendar.getInstance();
                        date.setTimeInMillis(mPlanInfo.mStartTime);
                        date.add(Calendar.DAY_OF_MONTH, i);
                        mPlanScopeDays[i] = date;
                    }

                    // init selectDay
                    long curTimeMillis = System.currentTimeMillis();
                    if (curTimeMillis < mPlanScopeDays[0].getTimeInMillis()) {
                        mSelectedDatePointStr = DateTimeUtil.getPointFormatDateStr(mPlanScopeDays[0].getTimeInMillis());
                    } else if (curTimeMillis > mPlanScopeDays[mPlanInfo.mPlannedDayCount - 1].getTimeInMillis()) {
                        mSelectedDatePointStr = DateTimeUtil.getPointFormatDateStr(mPlanScopeDays[0].getTimeInMillis());
                    } else {
                        mSelectedDatePointStr = DateTimeUtil.getPointFormatDateStr(curTimeMillis);
                    }

                    return Boolean.TRUE;
                }
            }

            return Boolean.FALSE;
        }

        @Override
        protected void onPostExecute(Boolean res) {
            if (res) {
                mTitle.setText(mPlanInfo.mTitle);
                if (mPlanInfo.mBigImageUrl != null && !TextUtils.isEmpty(mPlanInfo.mBigImageUrl)) {
                    Picasso.with(PlanDayDetailActivity.this).load(mPlanInfo.mBigImageUrl).into(mTopImageView);
                } else {
                    Picasso.with(PlanDayDetailActivity.this).load(mPlanInfo.mSmallIconUrl).into(mTopImageView);
                }
                updateSelectedDay();
            } else {
                // handle error
            }
        }
    }

    class LoadSelectedMonthPlanDayDetailTask extends OmAsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            if (mDayPlans == null) {
                mDayPlans = new HashMap<>();
            }
            mDayPlans.clear();
            mProgressBar.setVisibility(View.VISIBLE);
            mReadBtn.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(String... voids) {
            int dayIndexStart = 0;
            int dayIndexEnd = 0;

            mSelectedDatePointStr = voids[0];
            Calendar calendar = DateTimeUtil.getCalendarFromPointFormat(mSelectedDatePointStr);
            calendar.set(Calendar.DATE, 1);
            dayIndexStart = (int) ((calendar.getTimeInMillis() - mPlanInfo.mStartTime) / Constants.MILlI_SECONDS_OF_A_DAY);

            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
            dayIndexEnd = (int) ((calendar.getTimeInMillis() - mPlanInfo.mStartTime) / Constants.MILlI_SECONDS_OF_A_DAY);

            dayIndexStart = dayIndexStart < 0 ? 0 : dayIndexStart;
            dayIndexEnd = dayIndexEnd >= mPlanInfo.mPlannedDayCount ? mPlanInfo.mPlannedDayCount - 1 : dayIndexEnd;
            Cursor c = S.getDb().getDayPlan((int) mPlanInfo.mDbPlanId, dayIndexStart, dayIndexEnd);
            if (c != null && (c.getCount() >= (dayIndexEnd - dayIndexStart + 1))) {
                try {
                    while (c.moveToNext()) {
                        int dayIndex = c.getInt(c.getColumnIndexOrThrow(Db.ReadingDayPlan.day_index));
                        String dateStr = DateTimeUtil.getPointFormatDateStr(mPlanInfo.mStartTime + dayIndex * Constants.MILlI_SECONDS_OF_A_DAY);
                        if (!mDayPlans.containsKey(dateStr)) {
                            ReadingPlan.ReadingDayPlanInfo dayPlanInfo = new ReadingPlan.ReadingDayPlanInfo();
                            dayPlanInfo.mId = c.getLong(c.getColumnIndexOrThrow("_id"));
                            dayPlanInfo.mPlanId = (int) mPlanInfo.mDbPlanId;
                            dayPlanInfo.mDayIndex = c.getInt(c.getColumnIndexOrThrow(Db.ReadingDayPlan.day_index));
                            dayPlanInfo.mDayPlanProgresses = new ArrayList<>();
                            mDayPlans.put(dateStr, dayPlanInfo);
                        }

                        ReadingPlan.ReadingDayPlanInfo dayPlanInfo = mDayPlans.get(dateStr);
                        int progressId = c.getInt(c.getColumnIndexOrThrow(Db.ReadingDayPlan.progress_id));
                        ReadingPlan.ReadingDayPlanProgressInfo progressInfo = getPlanProgress(progressId);
                        if (progressInfo != null) {
                            dayPlanInfo.mDayPlanProgresses.add(progressInfo);
                            dayPlanInfo.updateCompleted();
                        }
                    }
                } catch (Exception e) {
                } finally {
                    c.close();
                }
            } else {
                BPProto.BP bp = new BPPlanService(PlanDayDetailActivity.this)
                        .getData(String.valueOf(mPlanInfo.mServerQueryId),
                                String.valueOf(dayIndexStart),
                                String.valueOf(dayIndexEnd));
                if (bp != null) {
                    List<ReadingPlan.ReadingDayPlanInfo> dayPlans = ReadingPlan.getDayPlanListFromBP(bp, dayIndexStart);
                    if (dayPlans.size() == (dayIndexEnd - dayIndexStart + 1)) {
                        dayPlans = S.getDb().insertReadingDayPlan(mPlanInfo.mDbPlanId, dayPlans);

                        for (int i = 0; i < dayPlans.size(); ++i) {
                            ReadingPlan.ReadingDayPlanInfo dayPlanInfo = dayPlans.get(i);
                            int dayIndex = dayPlanInfo.mDayIndex;
                            String dateStr = DateTimeUtil.getPointFormatDateStr(mPlanInfo.mStartTime + dayIndex * Constants.MILlI_SECONDS_OF_A_DAY);
                            if (!mDayPlans.containsKey(dateStr)) {
                                dayPlanInfo.mPlanId = (int) mPlanInfo.mDbPlanId;
                                mDayPlans.put(dateStr, dayPlanInfo);
                            }
                        }
                    }
                }
            }

            return null;
        }

        private ReadingPlan.ReadingDayPlanProgressInfo getPlanProgress(final int progressId) {
            Cursor c = S.getDb().getDayProgress(progressId);
            if (c != null) {
                ReadingPlan.ReadingDayPlanProgressInfo planProgressInfo = new ReadingPlan.ReadingDayPlanProgressInfo();
                try {
                    if (c.moveToNext()) {
                        planProgressInfo.mId = c.getLong(c.getColumnIndexOrThrow("_id"));
                        planProgressInfo.mAri = c.getInt(c.getColumnIndexOrThrow(Db.ReadingDayPlanProgress.ari));
                        planProgressInfo.mVerseCount = c.getInt(c.getColumnIndexOrThrow(Db.ReadingDayPlanProgress.verse_count));
                        planProgressInfo.mIsCompleted = c.getInt(c.getColumnIndexOrThrow(Db.ReadingDayPlanProgress.is_completed));
                        planProgressInfo.mExtensionData = c.getString(c.getColumnIndexOrThrow(Db.ReadingDayPlanProgress.extension_data));
                    }
                } catch (Exception e) {
                    return null;
                } finally {
                    c.close();
                }
                return planProgressInfo;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void res) {
            mProgressBar.setVisibility(View.GONE);
            mReadBtn.setVisibility(View.VISIBLE);
            if (mDayPlans.size() > 0) {
                updateView();
            }
        }
    }

    public class DayPlanItemHolder extends RecyclerView.ViewHolder {
        protected View mRootView;
        protected View mSelectedBolder;
        protected TextView mDayIndexTv;
        protected ImageView mCheckedIv;

        public DayPlanItemHolder(View view) {
            super(view);
            this.mRootView = view;
            this.mSelectedBolder = V.get(view, R.id.selected_bolder);
            this.mDayIndexTv = V.get(view, R.id.dayIndexTv);
            this.mCheckedIv = V.get(view, R.id.checkedIv);
        }
    }

    class DayPlanAdapter extends RecyclerView.Adapter<DayPlanItemHolder> {

        private Calendar mCal;

        public DayPlanAdapter() {
            mCal = Calendar.getInstance();
        }

        public void updateCalendar(String pointFormatDate) {
            Calendar calendar = DateTimeUtil.getCalendarFromPointFormat(pointFormatDate);
            if (calendar != null) {
                mCal = calendar;
            }
        }

        @Override
        public DayPlanItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = mLayoutInflater.inflate(R.layout.item_day_plan_layout, null);
            DayPlanItemHolder itemHolder = new DayPlanItemHolder(v);
            return itemHolder;
        }

        @Override
        public void onBindViewHolder(DayPlanItemHolder holder, final int position) {
            holder.mDayIndexTv.setText(String.valueOf(position + 1));

            mCal.set(Calendar.DAY_OF_MONTH, position + 1);
            final String curDateStr = DateTimeUtil.getPointFormatDateStr(mCal.getTimeInMillis());
            ReadingPlan.ReadingDayPlanInfo dayPlanInfo = mDayPlans.get(curDateStr);
            if (dayPlanInfo == null) {
                holder.mDayIndexTv.setTextColor(getResources().getColor(R.color.mdtp_date_picker_text_disabled));
                holder.mSelectedBolder.setBackgroundResource(R.drawable.day_plan_bolder_normal);
                holder.mCheckedIv.setVisibility(View.GONE);
                holder.mRootView.setOnClickListener(null);
            } else {
                holder.mDayIndexTv.setTextColor(getResources().getColor(R.color.black));
                holder.mSelectedBolder.setBackgroundResource(
                        curDateStr.equals(mSelectedDatePointStr)
                                ? R.drawable.day_plan_bolder_selected
                                : R.drawable.day_plan_bolder_normal
                );

                holder.mCheckedIv.setVisibility(View.VISIBLE);
                holder.mCheckedIv.setImageResource(
                        dayPlanInfo.mIsCompleted
                                ? R.drawable.ic_progress_finished
                                : R.drawable.ic_progress_unfinished
                );

                holder.mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSelectedDatePointStr = curDateStr;
                        updateSelectedDay();
                        notifyDataSetChanged();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mCal.getActualMaximum(Calendar.DATE);
        }
    }

    public class DayProgressItemHolder {
        public TextView mVerseReferenceTv;
        public ImageButton mProgressCheckBox;
    }

    class DayProgressAdapter extends ArrayAdapter<ReadingPlan.ReadingDayPlanProgressInfo> {

        public DayProgressAdapter(Context context) {
            super(context, 0);
            if (S.activeVersion == null) {
                S.checkActiveVersion();
            }
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ReadingPlan.ReadingDayPlanProgressInfo progressInfo = getItem(position);
            DayProgressItemHolder holder = null;
            if (convertView == null) {
                holder = new DayProgressItemHolder();
                View view = mLayoutInflater.inflate(R.layout.item_day_progress_layout, parent, false);
                convertView = view;
                holder.mVerseReferenceTv = V.get(view, R.id.verseReferenceTv);
                holder.mProgressCheckBox = V.get(view, R.id.progressCheckBox);
                view.setTag(holder);
            } else {
                holder = (DayProgressItemHolder) convertView.getTag();
            }

            if (S.activeVersion == null) {
                S.checkActiveVersion();
            }
            final String reference = S.getVerseReference(progressInfo.mAri, progressInfo.mVerseCount);
            holder.mVerseReferenceTv.setText(reference);
            holder.mProgressCheckBox.setSelected(progressInfo.mIsCompleted == 1);

            holder.mVerseReferenceTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoVerseDetailPage(position);
                }
            });
            holder.mProgressCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (progressInfo.mIsCompleted == 1) {
                        progressInfo.mIsCompleted = 0;
                    } else {
                        progressInfo.mIsCompleted = 1;
                    }
                    S.getDb().updateReadingDayPlanProgress(progressInfo.mId, progressInfo.mIsCompleted == 1);
                    notifyDataSetChanged();
                    updatePlanProgress();
                }
            });
            return convertView;
        }

        @Override
        public ReadingPlan.ReadingDayPlanProgressInfo getItem(int position) {
            final ReadingPlan.ReadingDayPlanProgressInfo progressInfo = mSelectedDayPlan.mDayPlanProgresses.get(position);
            return progressInfo;
        }

        public int getCount() {
            return (mSelectedDayPlan == null || mSelectedDayPlan.mDayPlanProgresses == null) ? 0 : mSelectedDayPlan.mDayPlanProgresses.size();
        }
    }
}
