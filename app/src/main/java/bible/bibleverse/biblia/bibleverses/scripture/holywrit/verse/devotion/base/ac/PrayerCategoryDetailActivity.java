package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fw.basemodules.utils.OmAsyncTask;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.lcodecore.tkrefreshlayout.Footer.BottomProgressView;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.IOException;
import java.util.ArrayList;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.NewAnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.NewAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.SelfAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.UserBehaviorAnalytics;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.BaseRetrofit;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.PrayerService;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PrayerBean;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PrayerResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventClickDailyNt;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.ReminderBean;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.notification.DailyNotifyAlarm;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.PrayerNotificationUtil;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.view.LoadingView;
import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Response;
import yuku.afw.V;

/**
 * Created by yzq on 2017/3/1.
 */

public class PrayerCategoryDetailActivity extends BaseActivity {

    private static final String CATEGORY_ID = "category_id";
    private static final String CATEGORY_NAME = "category_name";
    private static final String CATEGORY_PRAYER_NUM = "category_prayer_num";

    private String mCategoryId;
    private String mCategoryName;
    private int mPrayerCount;

    private View mBackBtn;
    private ImageView mTopImage;
    private ImageView mBackImage, mAddReminderBtn;
    private TextView mToolbarTitle;
    private View mToolbarView, mToolbarShadow;
    private View mListBackgroundView;
    private ObservableListView mListView;
    private int mParallaxImageHeight;

    private PrayerAdapter mPrayerAdapter;

    private ProgressBar mProgressBar;
    private TextView mProgressText;
    private LoadingView mLoadingView;

    private LinearLayout mEmptyLayout;
    private TextView mNoItemTitle, mNoItemButton, mNoItemMsg;
    private ImageView mNoItemImage;

    private ArrayList<PrayerBean> mPrayers;
    private LoadDataTask mLoadDataTask;
    private String mNextPageStart;

    private LayoutInflater mInflater;

    private boolean mIsLoadingData = false;
    private ReminderBean mReminderBean;

    private int mImgWidth, mImgHeight;
    private int mActionbarHeight;

    private TwinklingRefreshLayout mRefreshLayout;


    public static Intent createIntent(Context context, String categoryId, String categoryName, int count) {
        Intent intent = new Intent(context, PrayerCategoryDetailActivity.class);
        intent.putExtra(CATEGORY_ID, categoryId);
        intent.putExtra(CATEGORY_NAME, categoryName);
        intent.putExtra(CATEGORY_PRAYER_NUM, count);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer_category_detail);

        handleIntent(getIntent());

        initVar();
        initToolbar();
        initView();

        mLoadDataTask = new LoadDataTask();
        mLoadDataTask.execute();
    }

    private void initVar() {
        mInflater = getLayoutInflater();

        mImgWidth = getResources().getDisplayMetrics().widthPixels;
        mImgHeight = mImgWidth * 204 / 360;

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            mActionbarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        } else {
            mActionbarHeight = 0;
        }

        mNextPageStart = "";
        mPrayers = new ArrayList<>();
    }

    private void initToolbar() {
        mToolbarView = findViewById(R.id.toolbar_layout);
        mToolbarShadow = findViewById(R.id.toolbar_shadow);
        mBackBtn = findViewById(R.id.back_btn);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mBackImage = V.get(this, R.id.back_image);
        mToolbarTitle = V.get(this, R.id.title);
        mToolbarTitle.setText(mCategoryName);
        mAddReminderBtn = V.get(this, R.id.add_reminder_setting);
        mAddReminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });
    }

    private void initView() {
        mTopImage = V.get(this, R.id.image);
        mListView = V.get(this, R.id.list_view);
        View paddingView = new View(this);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                mParallaxImageHeight);
        paddingView.setLayoutParams(lp);
        paddingView.setClickable(true);

        mListView.addHeaderView(paddingView);
        mPrayerAdapter = new PrayerAdapter();
        mListView.setAdapter(mPrayerAdapter);
        mListView.setScrollViewCallbacks(mScrollCallbacks);
        mListBackgroundView = findViewById(R.id.list_background);

        mRefreshLayout = V.get(this, R.id.refresh_layout);
        ProgressLayout headerView = new ProgressLayout(this);
        headerView.setColorSchemeResources(R.color.theme_color_accent);
        mRefreshLayout.setHeaderView(headerView);
        BottomProgressView bottomView = new BottomProgressView(this);
        bottomView.setNormalColor(getResources().getColor(R.color.theme_color_accent));
        bottomView.setAnimatingColor(getResources().getColor(R.color.theme_color_accent));
        mRefreshLayout.setBottomView(bottomView);
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);

        V.get(this, R.id.loading_layout).setVisibility(View.GONE);
        mProgressBar = V.get(this, R.id.pg);
        mProgressText = V.get(this, R.id.pg_txt);
        mLoadingView = V.get(this, R.id.loading_view);
        mLoadingView.setVisibility(View.VISIBLE);

        mEmptyLayout = V.get(this, R.id.empty_layout);
        mEmptyLayout.setVisibility(View.GONE);
        mNoItemImage = V.get(this, R.id.empty_icon);
        mNoItemImage.setImageResource(R.drawable.icon_no_connection);
        mNoItemTitle = V.get(this, R.id.tEmpty);
        mNoItemTitle.setText(getString(R.string.no_connection));
        mNoItemMsg = V.get(this, R.id.msgEmpty);
        mNoItemMsg.setText(getString(R.string.empty_verse_of_the_day));
        mNoItemButton = V.get(this, R.id.bRetry);
        mNoItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        mNoItemButton.setVisibility(View.GONE);
        mEmptyLayout.setBackgroundColor(getResources().getColor(R.color.white));
        mNoItemTitle.setTextColor(getResources().getColor(R.color.black));
    }

    private ObservableScrollViewCallbacks mScrollCallbacks = new ObservableScrollViewCallbacks() {
        @Override
        public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
            int baseColor = getResources().getColor(R.color.white);
            float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
            mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
            ViewHelper.setTranslationY(mTopImage, -scrollY / 2);

            // Translate list background
            ViewHelper.setTranslationY(mListBackgroundView, Math.max(0, -scrollY + mParallaxImageHeight));
            if ((-scrollY + mParallaxImageHeight) < mActionbarHeight) {
                mBackImage.setImageResource(R.drawable.ic_back_black);
                mToolbarShadow.setVisibility(View.VISIBLE);
                mToolbarTitle.setTextColor(Color.BLACK);
            } else {
                mBackImage.setImageResource(R.drawable.ic_back_white);
                mToolbarShadow.setVisibility(View.GONE);
                mToolbarTitle.setTextColor(Color.WHITE);
            }
        }

        @Override
        public void onDownMotionEvent() {

        }

        @Override
        public void onUpOrCancelMotionEvent(ScrollState scrollState) {

        }
    };
    private RefreshListenerAdapter mRefreshListener = new RefreshListenerAdapter() {
        @Override
        public void onRefresh(TwinklingRefreshLayout refreshLayout) {
        }

        @Override
        public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
            if (mIsLoadingData == false) {
                mLoadDataTask = new LoadDataTask();
                mLoadDataTask.execute();
            }
        }

        @Override
        public void onRefreshCanceled() {
            if (mLoadDataTask != null) {
                mLoadDataTask.cancel(true);
                mLoadDataTask = null;
            }
        }
    };

//    private AbsListView.OnScrollListener mListScrollListener = new AbsListView.OnScrollListener() {
//        @Override
//        public void onScrollStateChanged(AbsListView view, int scrollState) {
//        }
//
//        @Override
//        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//            if (firstVisibleItem + visibleItemCount == (totalItemCount - 1) && totalItemCount != 0) {
//                if (mIsLoadingData == false) {
//                    mLoadDataTask = new LoadDataTask();
//                    mLoadDataTask.execute();
//                }
//            }
//        }
//    };

    private void handleIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        mCategoryId = intent.getStringExtra(CATEGORY_ID);
        mCategoryName = intent.getStringExtra(CATEGORY_NAME);
        int id = intent.getIntExtra(Constants.KEY_NOTIFICATION_ID, 0);
        int type = intent.getIntExtra(Constants.KEY_NOTIFICATION_TYPE, 0);
        mPrayerCount = intent.getIntExtra(CATEGORY_PRAYER_NUM, 0);
        if (id != 0) {
            SelfAnalyticsHelper.sendNotificationAnalytics(this, AnalyticsConstants.A_PRAYER_NOTICE, AnalyticsConstants.L_PRAYER_CLICK + "_" + type);
            EventBus.getDefault().post(new EventClickDailyNt());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadDataTask != null) {
            mLoadDataTask.cancel(true);
            mLoadDataTask = null;
        }
        if (mLoadingView != null) {
            mLoadingView.stopLoading();
        }
    }

    class ViewHolderPrayer {

        TextView title, readNum, source, snippet;
        View startView, contentView;

        public ViewHolderPrayer(View itemView) {
            title = V.get(itemView, R.id.title);
            readNum = V.get(itemView, R.id.read_num);
            source = V.get(itemView, R.id.source);
            snippet = V.get(itemView, R.id.snippet);
            startView = V.get(itemView, R.id.more_layout);
            contentView = V.get(itemView, R.id.content_layout);
        }
    }

    private class PrayerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mPrayers.size();
        }

        @Override
        public Object getItem(int position) {
            return mPrayers != null ? mPrayers.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolderPrayer holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_pray, parent, false);
                holder = new ViewHolderPrayer(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolderPrayer) convertView.getTag();
            }

            final PrayerBean pray = mPrayers.get(position);
            holder.title.setText(pray.getTitle());
            holder.readNum.setText(String.valueOf(pray.getView()));
            holder.source.setText(getString(R.string.pray_source_format, pray.getSource()));
            holder.snippet.setText(Html.fromHtml(pray.getContent()));

            holder.contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(PrayerDetailActivity.createIntentFromList(PrayerCategoryDetailActivity.this, mPrayers, position, mCategoryName, mPrayerCount, getCatId()));
                    SelfAnalyticsHelper.sendPrayerAnalytics(PrayerCategoryDetailActivity.this, AnalyticsConstants.A_CLICK_PRAYER, mCategoryName);
                    UserBehaviorAnalytics.trackUserBehavior(PrayerCategoryDetailActivity.this, AnalyticsConstants.P_PRAYER_TOPIC, AnalyticsConstants.B_CLICK_PRAYER);
                }
            });

            holder.startView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(PrayerDetailActivity.createIntentFromList(PrayerCategoryDetailActivity.this, mPrayers, position, mCategoryName, mPrayerCount, getCatId()));
                    Bundle params = new Bundle();
                    params.putString("prayer_category", mCategoryName);
                    NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_PRAYES_LIST_START, params);
                    SelfAnalyticsHelper.sendPrayerAnalytics(PrayerCategoryDetailActivity.this, AnalyticsConstants.A_CLICK_PRAYER, mCategoryName);
                    UserBehaviorAnalytics.trackUserBehavior(PrayerCategoryDetailActivity.this, AnalyticsConstants.P_PRAYER_TOPIC, AnalyticsConstants.B_CLICK_PRAYER);
                }
            });
            return convertView;
        }
    }

    private int getCatId() {
        try {
            return Integer.valueOf(mCategoryId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private class LoadDataTask extends OmAsyncTask<Void, Void, PrayerResponse.Data> {
        @Override
        protected void onPreExecute() {
            mIsLoadingData = true;
        }

        @Override
        protected PrayerResponse.Data doInBackground(Void... voids) {
            try {
                PrayerService prayerService = BaseRetrofit.getPrayerService();
                Call<PrayerResponse> call = prayerService.getPrayer4Category(mCategoryId, "", mNextPageStart);
                Response<PrayerResponse> response = call.execute();
                PrayerResponse prayerResponse = response != null ? response.body() : null;
                if (prayerResponse != null && prayerResponse.getData() != null) {
                    return prayerResponse.getData();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(PrayerResponse.Data data) {
            if (data != null && data.getInfo() != null) {
                Picasso.with(PrayerCategoryDetailActivity.this).load(data.getInfo().getImageUrl()).fit().into(mTopImage);
            }

            int size = data != null ? data.getPrays().size() : 0;
            if (size > 0) {
                PrayerBean last = data.getPrays().get(size - 1);
                mNextPageStart = String.valueOf(last.getId());
                mPrayers.addAll(data.getPrays());
                mPrayerAdapter.notifyDataSetChanged();

                mLoadingView.setVisibility(View.GONE);
                mEmptyLayout.setVisibility(View.GONE);

            } else if (mPrayers.size() == 0) {
                mLoadingView.setVisibility(View.GONE);
                mEmptyLayout.setVisibility(View.VISIBLE);
            }
            mRefreshLayout.finishLoadmore();
            mIsLoadingData = false;
        }
    }

    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
            PrayerNotificationUtil.addPrayerReminderTime(PrayerCategoryDetailActivity.this, hourOfDay, minute, mCategoryId, mCategoryName);
            new SetAlarm().execute();

        }
    };

    private DialogInterface.OnCancelListener mOffListener = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            if (mReminderBean != null) {
                if (mReminderBean.type == Constants.DP_TYPE_CUSTOM) {
                    PrayerNotificationUtil.deletePrayerReminderTime(PrayerCategoryDetailActivity.this, mCategoryId);
                } else {
                    PrayerNotificationUtil.turnOffPrayerReminderTime(PrayerCategoryDetailActivity.this, mCategoryId);
                }
            }
        }
    };

    private void showTimePicker() {
        try {
            mReminderBean = PrayerNotificationUtil.getReminderBean(this, mCategoryId);
            int hour = getReminderHourOfDay(mReminderBean);
            int minute = getReminderMinute(mReminderBean);
            TimePickerDialog dailyVersePicker = TimePickerDialog.newInstance(
                    mTimeSetListener,
                    hour,
                    minute,
                    DateFormat.is24HourFormat(this)//mode24Hours
            );
            dailyVersePicker.setCancelText(getCancleText());
            dailyVersePicker.setThemeDark(false);
            dailyVersePicker.vibrate(false);
            dailyVersePicker.setOnCancelListener(mOffListener);
            dailyVersePicker.show(getFragmentManager(), "DailyPrayerTimePickerDialog");
        } catch (Exception e) {
        }
    }

    private int getReminderHourOfDay(ReminderBean reminderBean) {
        if (reminderBean == null) {
            return 8;
        }
        if (!TextUtils.isEmpty(reminderBean.timeValue) && !TextUtils.equals(reminderBean.timeValue, Constants.DAILY_NOTIFICATION_OFF)) {
            return Integer.parseInt(reminderBean.timeValue.substring(0, 2));
        } else if (reminderBean.type > 0) {
            return reminderBean.type == Constants.DP_TYPE_MORNING ? 8 : 22;
        } else {
            return 8;
        }
    }

    private int getReminderMinute(ReminderBean reminderBean) {
        if (reminderBean == null) {
            return 0;
        }
        return TextUtils.isEmpty(reminderBean.timeValue) || reminderBean.timeValue.equals(Constants.DAILY_NOTIFICATION_OFF) ?
                0 : Integer.parseInt(reminderBean.timeValue.substring(2, 4));
    }

    private String getCancleText() {
        if (mReminderBean != null && mReminderBean.type != Constants.DP_TYPE_CUSTOM) {
            return getString(R.string.turn_off);
        } else if (mReminderBean != null && mReminderBean.type == Constants.DP_TYPE_CUSTOM) {
            return getString(R.string.delete);
        }
        return getString(R.string.cancel);
    }

    private class SetAlarm extends OmAsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            DailyNotifyAlarm.setPrayerNotifyAlarm(PrayerCategoryDetailActivity.this);
            return null;
        }
    }

}


