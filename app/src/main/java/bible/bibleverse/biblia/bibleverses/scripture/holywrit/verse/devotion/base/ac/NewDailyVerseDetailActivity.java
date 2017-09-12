package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.SelfAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.BaseRetrofit;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.DevotionService;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.VerseService;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.DailyVerseDetailResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.EmptyResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.VerseListResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.helper.WrapContentLinearLayoutManager;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter.viewholder.ViewHolderRelatedDailyVerse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventUserOperationChanged;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yuku.afw.V;

import static bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R.id.innerTitle;

/**
 * Created by yzq on 16/11/7.
 */

public class NewDailyVerseDetailActivity extends BaseActivity {

    private ScrollView mScrollview;
    private TextView mTitle;
    private ProgressBar mProgressBar;
    private LinearLayout mEmptyLayout;
    private LinearLayout mMainLayout;
    private ImageView mTopImageView;
    private TextView mInnerTitle;
    private TextView mSourceInfo;
    private View mQuoteLayout;
    private TextView mQuote;
    private TextView mQuoteRefer;
    private TextView mContent;
    private View mPrayerLayout;
    private TextView mPrayerContent;
    private View mRelatedSeparator;
    private RecyclerView mRelatedLv;

    private int mVerseId;
    private String mVerseTitle, mQuoteTxt, mQuoteReferTxt;
    private String mVerseImageUrl;
    private int mNotificationType, mNotificationId;

    private RelatedAdapter mRelatedAdapter;
    private List<VerseListResponse.DataBean.VerseBean> mRelatedList;

    private LayoutInflater mInflater;
    private int mScreenWidth;

    public static Intent createIntent(Context context, int devotionId, String devotionTitle, String devotionImageUrl) {
        Intent intent = new Intent(context, NewDailyVerseDetailActivity.class);
        intent.putExtra(Constants.DEVOTION_ID, devotionId);
        intent.putExtra(Constants.DEVOTION_TITLE, devotionTitle);
        intent.putExtra(Constants.DEVOTION_IMAGE_URL, devotionImageUrl);
        return intent;
    }

    public static Intent createIntent(Context context, int devotionId, String quote, String quoteRefer, String devotionImageUrl) {
        Intent intent = new Intent(context, NewDailyVerseDetailActivity.class);
        intent.putExtra(Constants.DEVOTION_ID, devotionId);
        intent.putExtra(Constants.DEVOTION_QUOTE, quote);
        intent.putExtra(Constants.DEVOTION_QUOTE_REFER, quoteRefer);
        intent.putExtra(Constants.DEVOTION_IMAGE_URL, devotionImageUrl);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_daily_verse_detail);
        mInflater = getLayoutInflater();
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mRelatedList = new ArrayList<>();

        findViewById(R.id.ad_banner_layout).setVisibility(View.GONE);

        initToolbar();


        handleIntent(getIntent());

        initView();

        loadDevotionDetail();
        loadRelated();


        S.getDb().addTodayDailyVerseReadCount(1);
        EventBus.getDefault().post(new EventUserOperationChanged(EventUserOperationChanged.TYPE_DAILY_VERSE));
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

    private void initView() {
        mTitle = V.get(this, R.id.my_title);
        V.get(this, R.id.loading_layout).setVisibility(View.GONE);
        mScrollview = V.get(this, R.id.scrollview);
        mMainLayout = V.get(this, R.id.main_layout);
        mTopImageView = V.get(this, R.id.topBigImage);
        mInnerTitle = V.get(this, innerTitle);

        mSourceInfo = V.get(this, R.id.sourceInfo);
        mQuoteLayout = V.get(this, R.id.quote_layout);
        mQuote = V.get(this, R.id.quote);
        mQuoteRefer = V.get(this, R.id.quoteRefer);
        mContent = V.get(this, R.id.content);
        mPrayerLayout = V.get(this, R.id.prayer_layout);
        mPrayerContent = V.get(this, R.id.prayer_content);
        mProgressBar = V.get(this, R.id.loading_progress);
        //related
        mRelatedSeparator = V.get(this, R.id.related_separator);
        mRelatedLv = V.get(this, R.id.related_list);
        mRelatedLv.setLayoutManager(new WrapContentLinearLayoutManager(NewDailyVerseDetailActivity.this));
        mRelatedLv.setNestedScrollingEnabled(false);

        mEmptyLayout = V.get(this, R.id.empty_layout);
        mEmptyLayout.setVisibility(View.GONE);
        ((ImageView) V.get(this, R.id.empty_icon)).setImageResource(R.drawable.icon_no_connection);
        ((TextView) V.get(this, R.id.tEmpty)).setText(getString(R.string.no_connection));
        ((TextView) V.get(this, R.id.msgEmpty)).setText(getString(R.string.empty_verse_of_the_day));
        V.get(this, R.id.bRetry).setVisibility(View.GONE);
        mEmptyLayout.setBackgroundColor(getResources().getColor(R.color.white));
        ((TextView) V.get(this, R.id.tEmpty)).setTextColor(getResources().getColor(R.color.black));

//        ((TextView) V.get(this, R.id.separator_title)).setText(R.string.related_devotion);

        if (!TextUtils.isEmpty(mVerseTitle)) {
            mTitle.setText(mVerseTitle);
            mInnerTitle.setText(mVerseTitle);
        }
        if (!TextUtils.isEmpty(mQuoteTxt)) {
            mQuote.setText(mQuoteTxt);
        }
        if (!TextUtils.isEmpty(mQuoteReferTxt)) {
            mQuoteRefer.setText(mQuoteReferTxt);
        }

        if (!TextUtils.isEmpty(mVerseImageUrl)) {
            Picasso.with(NewDailyVerseDetailActivity.this).load(mVerseImageUrl).fit().into(mTopImageView);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        mRelatedList.clear();
        handleIntent(intent);

        mTitle.setText(mVerseTitle);
        mInnerTitle.setText(mVerseTitle);

        loadDevotionDetail();
        loadRelated();

    }

    private void handleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        mVerseId = intent.getIntExtra(Constants.DEVOTION_ID, 0);
        mVerseTitle = intent.getStringExtra(Constants.DEVOTION_TITLE);
        mQuoteTxt = intent.getStringExtra(Constants.DEVOTION_QUOTE);
        mQuoteReferTxt = intent.getStringExtra(Constants.DEVOTION_QUOTE_REFER);
        mNotificationType = intent.getIntExtra(Constants.KEY_NOTIFICATION_TYPE, 0);
        mNotificationId = intent.getIntExtra(Constants.KEY_NOTIFICATION_ID, 0);
        mVerseImageUrl = intent.getStringExtra(Constants.DEVOTION_IMAGE_URL);
        //from notify
        if (mNotificationType > 0) {
            SelfAnalyticsHelper.sendNotificationAnalytics(this, AnalyticsConstants.A_VERSE_NOTICE, AnalyticsConstants.L_VERSE_CLICK + "_" + mNotificationType);
            SelfAnalyticsHelper.sendVerseReadAnalytics(this, AnalyticsConstants.A_VERSE_CLICK_NOTICE);
        }
    }


    private void loadDevotionDetail() {
        mProgressBar.setVisibility(View.VISIBLE);
        VerseService verseService = BaseRetrofit.getVerseService();
        Call<DailyVerseDetailResponse> call = verseService.getDailyVerseDetails(String.valueOf(mVerseId));
        call.enqueue(new Callback<DailyVerseDetailResponse>() {

            @Override
            public void onResponse(Call<DailyVerseDetailResponse> call, Response<DailyVerseDetailResponse> response) {
                if (response != null && response.isSuccessful()) {
                    DailyVerseDetailResponse dailyVerseDetailResponse = response.body();
                    if (dailyVerseDetailResponse != null && dailyVerseDetailResponse.getDetail() != null) {
                        DailyVerseDetailResponse.Detail devotion = dailyVerseDetailResponse.getDetail();
                        if (devotion != null) {
                            mMainLayout.setVisibility(View.VISIBLE);
                            String sourceInfoText = getString(R.string.devotion_source_format, devotion.getAuthor(), devotion.getSourceSite());
                            mSourceInfo.setText(sourceInfoText);

                            if (!TextUtils.isEmpty(devotion.getQuote())) {
                                mQuoteLayout.setVisibility(View.VISIBLE);
                                mQuote.setText(devotion.getQuote());
                                mQuoteRefer.setText("-" + devotion.getQuoteRefer());
                            } else {
                                mQuoteLayout.setVisibility(View.GONE);
                            }
                            if (!TextUtils.isEmpty(devotion.getTitle())) {
                                mTitle.setText(devotion.getTitle());
                                mInnerTitle.setText(devotion.getTitle());
                            }

                            mContent.setText(Html.fromHtml(devotion.getContent()));
                            if (!TextUtils.isEmpty(devotion.getPrayer())) {
                                mPrayerLayout.setVisibility(View.VISIBLE);
                                mPrayerContent.setText(devotion.getPrayer());
                            } else {
                                mPrayerLayout.setVisibility(View.GONE);
                            }
                            setDevotionViewed(devotion.getId(), devotion.getDate(), devotion.getSourceSite());
                        } else {
                            mMainLayout.setVisibility(View.GONE);
                            mEmptyLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<DailyVerseDetailResponse> call, Throwable t) {
                mMainLayout.setVisibility(View.GONE);
                mEmptyLayout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setDevotionViewed(int id, String devotionDate, String devotionSite) {
        DevotionService service = BaseRetrofit.getDevotionService();
        service.setDevotionViewed(String.valueOf(id)).enqueue(new Callback<EmptyResponse>() {
            @Override
            public void onResponse(Call<EmptyResponse> call, Response<EmptyResponse> response) {
            }

            @Override
            public void onFailure(Call<EmptyResponse> call, Throwable t) {

            }
        });
        long idInDb = S.getDb().addNewDevotionReadHistory(id, devotionDate, devotionSite);
        if (idInDb != 0) {
            S.getDb().addTodayReadDevotionCount(1);
        }
    }

    private void loadRelated() {
        VerseService verseService = BaseRetrofit.getVerseService();
        Call<VerseListResponse> call = verseService.getRelatedDailyVerse(String.valueOf(mVerseId));
        call.enqueue(new Callback<VerseListResponse>() {
            @Override
            public void onResponse(Call<VerseListResponse> call, Response<VerseListResponse> response) {
                if (response != null && response.isSuccessful()) {
                    VerseListResponse body = response.body();
                    mRelatedList.addAll(body.getData().getLists());
                    if (mRelatedAdapter == null) {
                        mRelatedAdapter = new RelatedAdapter();
                        mRelatedLv.setAdapter(mRelatedAdapter);
                    } else {
                        mRelatedAdapter.notifyDataSetChanged();
                    }
                    mRelatedSeparator.setVisibility(View.VISIBLE);
                    mRelatedLv.setVisibility(View.VISIBLE);
                } else {
                    mRelatedSeparator.setVisibility(View.GONE);
                    mRelatedLv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<VerseListResponse> call, Throwable t) {
                mRelatedSeparator.setVisibility(View.GONE);
                mRelatedLv.setVisibility(View.GONE);
            }
        });
    }

    private class RelatedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder holder;
            View view = mInflater.inflate(R.layout.item_devotion_related, parent, false);
            holder = new ViewHolderRelatedDailyVerse(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final ViewHolderRelatedDailyVerse holderRelatedDailyVerse = (ViewHolderRelatedDailyVerse) holder;
            final VerseListResponse.DataBean.VerseBean verseBean = mRelatedList.get(position);
            holderRelatedDailyVerse.bindDailyVerse(NewDailyVerseDetailActivity.this,
                    verseBean,
                    false);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return mRelatedList.size();
        }
    }

}
