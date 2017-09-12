package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fw.basemodules.utils.OmAsyncTask;
import com.lcodecore.tkrefreshlayout.Footer.BottomProgressView;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.squareup.picasso.Picasso;
import com.wx.goodview.GoodView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.BaseRetrofit;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.VerseService;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.VerseListResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.DateTimeUtil;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.view.LoadingView;
import retrofit2.Call;
import retrofit2.Response;
import yuku.afw.V;


/**
 * Created by yzq on 2017/2/27.
 */

public class DailyVerseListActivity extends BaseActivity {


    public static final String REFRESH = "REFRES";
    public static final String LOAD_MORE = "LOADMORE";
    public static final String NONE = "NONE";

    private ListView mListView;
    private GoodView mGoodView;
    private DailyVerseAdapter mListAdapter;
    private TwinklingRefreshLayout mRefreshLayout;

    private LoadingView mLoadingView;

    private LinearLayout mEmptyLayout;
    private TextView mNoItemTitle, mNoItemButton, mNoItemMsg;
    private ImageView mNoItemImage;
    private Toolbar mToolBar;


    private List<VerseListResponse.DataBean.VerseBean> mVerseItems;
    private List<String> mVerseLikeDates;
    private LoadVerseDataTask mLoadDataTask;
    private LoadVerseLikeHistoryTask mLoadVerseLikeHistoryTask;
    private String mNextPageStart;

    private LayoutInflater mInflater;
    private boolean mIsLoadingData = false;
    private String mLoadDataType;
    private String mCurrentDate;
    private Handler mHanlder;


    public static Intent createIntent(Context context, int id, String name, String imageUrl, boolean subscribe) {
        Intent intent = new Intent(context, DailyVerseListActivity.class);
        intent.putExtra(Constants.SITE_ID, id);
        intent.putExtra(Constants.SITE_NAME, name);
        intent.putExtra(Constants.SITE_IMAGE, imageUrl);
        intent.putExtra(Constants.SITE_SUBSCRIBE, subscribe);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verse_list);

        handleIntent();
        initVar();

        initToolbar();
        initView();

        mLoadDataTask = new LoadVerseDataTask();
        mLoadDataTask.execute(NONE);


        mLoadVerseLikeHistoryTask = new LoadVerseLikeHistoryTask();
        mLoadVerseLikeHistoryTask.execute();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
    }

    private void initVar() {
        mHanlder = new Handler();
        mInflater = getLayoutInflater();
        mNextPageStart = DateTimeUtil.getDateStr4ApiRequest(System.currentTimeMillis());
        mVerseItems = new ArrayList<>();
        mVerseLikeDates = new ArrayList<>();
        mCurrentDate = DateTimeUtil.getDateStr4ApiRequest(System.currentTimeMillis());
    }

    private void initToolbar() {
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.ic_back_black);
        setTitle(getString(R.string.verse_of_day));
    }

    private void initView() {
        mGoodView = new GoodView(this);
        mListView = V.get(this, R.id.list_view);
        mListAdapter = new DailyVerseAdapter();
        mListView.setAdapter(mListAdapter);
        mRefreshLayout = V.get(this, R.id.refresh_layout);
        BottomProgressView bottomView = new BottomProgressView(this);
        bottomView.setNormalColor(getResources().getColor(R.color.theme_color_accent));
        bottomView.setAnimatingColor(getResources().getColor(R.color.theme_color_accent));
        mRefreshLayout.setBottomView(bottomView);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
        mRefreshLayout.setEnableRefresh(false);

        V.get(this, R.id.loading_layout).setVisibility(View.GONE);

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
        mNoItemButton.setVisibility(View.GONE);
        mEmptyLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        mNoItemTitle.setTextColor(ContextCompat.getColor(this, R.color.black));
    }

    private RefreshListenerAdapter mRefreshListener = new RefreshListenerAdapter() {
        @Override
        public void onRefresh(TwinklingRefreshLayout refreshLayout) {
//            if (!mIsLoadingData) {
//                mLoadDataTask = new LoadVerseDataTask();
//                mLoadDataTask.execute(REFRESH);
//            }
        }

        @Override
        public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
            if (!mIsLoadingData) {
                mLoadDataTask = new LoadVerseDataTask();
                mLoadDataTask.execute(LOAD_MORE);
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

    class ViewHolder {
        View contentLayout, separatorLayout, divider;
        TextView tvQuote;
        TextView tvQuoteRefer;
        TextView tvDate;
        ImageView iconLike, iconShare;
        TextView tvShareNum, tvLikeNum, separatorTitle;
        ImageView image;
        LinearLayout actionShareLayout, actionLikeLayout;

        public ViewHolder(View itemView) {
            separatorLayout = V.get(itemView, R.id.item_separator_layout);
            separatorTitle = V.get(itemView, R.id.separator_title);
            divider = V.get(itemView, R.id.divider);
            contentLayout = V.get(itemView, R.id.content_layout);
            tvQuote = V.get(itemView, R.id.quote);
            tvQuoteRefer = V.get(itemView, R.id.quote_refer);
            image = V.get(itemView, R.id.image);
            tvDate = V.get(itemView, R.id.date);
            actionShareLayout = V.get(itemView, R.id.action_share);
            actionLikeLayout = V.get(itemView, R.id.action_like);
            tvShareNum = V.get(itemView, R.id.share_num);
            tvLikeNum = V.get(itemView, R.id.like_num);
            iconLike = V.get(itemView, R.id.like_icon);
            iconShare = V.get(itemView, R.id.share_icon);
        }
    }

    private class DailyVerseAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mVerseItems.size();
        }

        @Override
        public Object getItem(int position) {
            if (position < mVerseItems.size()) {
                return mVerseItems.get(position);
            }
            return 0;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_daily_verse, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final VerseListResponse.DataBean.VerseBean verseBean = mVerseItems.get(position);
            if (verseBean != null) {
                holder.tvQuote.setText(verseBean.getQuote());
                holder.tvQuoteRefer.setText(verseBean.getQuoteRefer());
                String day = DateTimeUtil.getLocaleDateStr4Display(verseBean.getDate());
                holder.tvDate.setText(day);
                holder.tvShareNum.setText(String.valueOf(verseBean.getShare()));
                holder.tvLikeNum.setText(String.valueOf(verseBean.getLike()));
                holder.iconLike.setImageResource(mVerseLikeDates.contains(verseBean.getDate()) ? R.drawable.ic_verse_like : R.drawable.ic_verse_unlike);
                if (!TextUtils.isEmpty(verseBean.getImageUrl())) {
                    Picasso.with(DailyVerseListActivity.this).load(verseBean.getImageUrl()).into(holder.image);
                }
                holder.separatorLayout.setVisibility(position == 0 || position == 1 ? View.VISIBLE : View.GONE);
                holder.divider.setVisibility(position != 0 ? View.VISIBLE : View.GONE);
                holder.separatorTitle.setText(getString(position == 0 ? R.string.today_verse : R.string.history_verse));
                holder.actionShareLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utility.shareTextBySystem(DailyVerseListActivity.this, verseBean.getQuote() + "\n" + verseBean.getQuoteRefer());
                        mHanlder.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                holder.tvShareNum.setText(String.valueOf(verseBean.getShare() + 1));
                            }
                        }, 1000);
                        postActionAnalytic(verseBean.getDate(), Constants.TYPE_VERSE_ACTION_SHARE);
                    }
                });

                holder.actionLikeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mVerseLikeDates.contains(verseBean.getDate())) {
                            return;
                        }
                        holder.iconLike.setImageResource(R.drawable.ic_verse_like);
                        mGoodView.setText("+1");
                        mGoodView.show(holder.iconLike);
                        holder.tvLikeNum.setText(String.valueOf(verseBean.getLike() + 1));
                        mVerseLikeDates.add(verseBean.getDate());
                        addLikeHistory(verseBean.getDate());
                        postActionAnalytic(verseBean.getDate(), Constants.TYPE_VERSE_ACTION_LIKE);
                    }
                });

                holder.contentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(NewDailyVerseDetailActivity.createIntent(DailyVerseListActivity.this, verseBean.getId(), verseBean.getQuote(), verseBean.getQuoteRefer(), verseBean.getImageUrl()));
                    }
                });
            }

            return convertView;
        }
    }

    private class LoadVerseLikeHistoryTask extends OmAsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            List<String> historys = S.getDb().listAllVerseLikeHistory();
            if (historys != null && !historys.isEmpty()) {
                mVerseLikeDates.addAll(historys);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private class LoadVerseDataTask extends OmAsyncTask<String, Void, List<VerseListResponse.DataBean.VerseBean>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mIsLoadingData = true;
        }

        @Override
        protected List<VerseListResponse.DataBean.VerseBean> doInBackground(String... voids) {
            String type = voids[0];
            mLoadDataType = type;
            try {
                VerseService verseService = BaseRetrofit.getVerseService();
                Call<VerseListResponse> call = null;
                switch (type) {
                    case LOAD_MORE:
                        call = verseService.getVerseListNew("", String.valueOf(mNextPageStart));
                        break;
                    case NONE:
                        call = verseService.getVerseListNew("", String.valueOf(mNextPageStart));
                        break;
                    default:
                        call = verseService.getVerseListNew("", String.valueOf(mNextPageStart));
                        break;
                }
                Response<VerseListResponse> response = call.execute();
                VerseListResponse verseListResponse = response != null ? response.body() : null;
                if (verseListResponse != null && verseListResponse.getData() != null && verseListResponse.getData().getLists() != null) {
                    return verseListResponse.getData().getLists();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<VerseListResponse.DataBean.VerseBean> verseBeens) {
            int size = verseBeens != null ? verseBeens.size() : 0;
            if (size > 0) {
                VerseListResponse.DataBean.VerseBean last = verseBeens.get(size - 1);
                switch (mLoadDataType) {
                    case LOAD_MORE:
                        mVerseItems.addAll(verseBeens);
                        mNextPageStart = DateTimeUtil.getOffsetDateString(last.getDate(), -1);
                        break;
                    case NONE:
                        mVerseItems.addAll(verseBeens);
                        mNextPageStart = DateTimeUtil.getOffsetDateString(last.getDate(), -1);
                        break;
                    default:
                        break;

                }
                mListAdapter.notifyDataSetChanged();
                mLoadingView.setVisibility(View.GONE);
                mEmptyLayout.setVisibility(View.GONE);
            } else if (mVerseItems.size() == 0) {
                mLoadingView.setVisibility(View.GONE);
                mEmptyLayout.setVisibility(View.VISIBLE);
            }

            mRefreshLayout.finishRefreshing();
            mRefreshLayout.finishLoadmore();
            mIsLoadingData = false;
        }
    }

    private void postActionAnalytic(final String id, final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                VerseService verseService = BaseRetrofit.getVerseService();
                try {
                    verseService.postVerseAction(type, id).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void addLikeHistory(final String day) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                S.getDb().addVerseLikeHistory(day, true);
            }
        }).start();
    }
}
