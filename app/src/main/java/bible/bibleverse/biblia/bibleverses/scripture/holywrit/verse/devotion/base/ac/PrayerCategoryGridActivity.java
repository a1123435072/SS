package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fw.basemodules.utils.OmAsyncTask;
import com.lcodecore.tkrefreshlayout.Footer.BottomProgressView;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.NewAnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.NewAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.SelfAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.UserBehaviorAnalytics;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.BaseRetrofit;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.PrayerService;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PrayerCategoryResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PrayerPeopleResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.view.LoadingView;
import retrofit2.Call;
import retrofit2.Response;
import yuku.afw.V;

/**
 * Created by yzq on 2017/3/1.
 */

public class PrayerCategoryGridActivity extends BaseActivity {

    private RecyclerView mRecyclerView;

    private CategoryAdapter mCategoryAdapter;
    private TwinklingRefreshLayout mRefreshLayout;

    private ProgressBar mProgressBar;
    private TextView mProgressText;
    private LoadingView mLoadingView;

    private LinearLayout mEmptyLayout;
    private TextView mNoItemTitle, mNoItemButton, mNoItemMsg;
    private ImageView mNoItemImage;


    private List<PrayerCategoryResponse.CategoryBean> mCategories;
    private LoadCategoryTask mLoadDataTask;

    private LayoutInflater mInflater;

    private int mImgWidth, mImgHeight;

    private int mCurrentPrayPeopleNum;
    private Map<Integer, Integer> mCategoryPeopleNumMap;

    public static Intent createIntent(Context context, int notifyId, int notifyType) {
        Intent resultIntent = new Intent(context, PrayerCategoryGridActivity.class);
        resultIntent.putExtra(Constants.KEY_NOTIFICATION_ID, notifyId);
        resultIntent.putExtra(Constants.KEY_NOTIFICATION_TYPE, notifyType);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        return resultIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer_category_grid);

        initVar();

        initToolbar();
        initView();

        mLoadDataTask = new LoadCategoryTask();
        mLoadDataTask.execute();
    }

    private void initVar() {
        mInflater = getLayoutInflater();

        int space = getResources().getDimensionPixelSize(R.dimen.margin_48);
        mImgWidth = (getResources().getDisplayMetrics().widthPixels - space) / 3;
        mImgHeight = mImgWidth;

        mCategories = new ArrayList<>();
        mCategoryPeopleNumMap = new HashMap<>();
    }

    private void initToolbar() {
        final Toolbar toolbar = V.get(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_back_black);
        setTitle(R.string.prayers);
    }

    private void initView() {
        mRefreshLayout = V.get(this, R.id.refresh_layout);
        ProgressLayout headerView = new ProgressLayout(this);
        headerView.setColorSchemeResources(R.color.theme_color_accent);
        mRefreshLayout.setHeaderView(headerView);
        BottomProgressView bottomView = new BottomProgressView(this);
        bottomView.setNormalColor(getResources().getColor(R.color.theme_color_accent));
        bottomView.setAnimatingColor(getResources().getColor(R.color.theme_color_accent));
        mRefreshLayout.setBottomView(bottomView);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
        mRefreshLayout.setEnableLoadmore(false);

        mRecyclerView = V.get(this, R.id.list_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mCategoryAdapter = new CategoryAdapter();
        mRecyclerView.setAdapter(mCategoryAdapter);


        V.get(this, R.id.loading_layout).setVisibility(View.GONE);
        mLoadingView = V.get(this, R.id.loading_view);
        mProgressBar = V.get(this, R.id.pg);
        mProgressText = V.get(this, R.id.pg_txt);
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
        mEmptyLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        mNoItemTitle.setTextColor(ContextCompat.getColor(this, R.color.black));
    }

    private RefreshListenerAdapter mRefreshListener = new RefreshListenerAdapter() {
        @Override
        public void onRefresh(TwinklingRefreshLayout refreshLayout) {
            mLoadDataTask = new LoadCategoryTask();
            mLoadDataTask.execute();
        }

        @Override
        public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
            mRefreshLayout.finishLoadmore();
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

    class ViewHolderCategory extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title, prayerNumTv;
        View rootView;

        public ViewHolderCategory(View itemView) {
            super(itemView);
            rootView = itemView;
            image = V.get(itemView, R.id.image);
            title = V.get(itemView, R.id.title);
            prayerNumTv = V.get(itemView, R.id.cat_prayer_num);
            ViewGroup.LayoutParams layoutParams = image.getLayoutParams();
            layoutParams.width = mImgWidth;
            layoutParams.height = mImgHeight;
            image.setLayoutParams(layoutParams);
        }
    }

    class ViewHolderCategoryHeader extends RecyclerView.ViewHolder {

        TextView numTv;
        View root;

        public ViewHolderCategoryHeader(View itemView) {
            super(itemView);
            numTv = V.get(itemView, R.id.prayer_people_num);
            root = V.get(itemView, R.id.header);
        }
    }

    private class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 1) {
                View view = mInflater.inflate(R.layout.layout_prayer_category_grid_header, parent, false);
                return new ViewHolderCategoryHeader(view);
            }
            ViewHolderCategory holder;
            View view = mInflater.inflate(R.layout.item_pray_category, parent, false);
            holder = new ViewHolderCategory(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            if (position == 0) {
                ViewHolderCategoryHeader holderCategoryHeader = (ViewHolderCategoryHeader) viewHolder;
                if (mCurrentPrayPeopleNum > 0) {
                    holderCategoryHeader.root.setVisibility(View.VISIBLE);
                    holderCategoryHeader.numTv.setText(String.valueOf(mCurrentPrayPeopleNum));
                } else {
                    holderCategoryHeader.root.setVisibility(View.GONE);
                }
                return;
            }

            ViewHolderCategory holder = (ViewHolderCategory) viewHolder;
            final PrayerCategoryResponse.CategoryBean category = mCategories.get(position - 1);
            Picasso.with(PrayerCategoryGridActivity.this).load(category.getImageUrl()).into(holder.image);
            holder.title.setText(category.getName());
            int count = 0;
            if (mCategoryPeopleNumMap.containsKey(category.getId())) {
                count = mCategoryPeopleNumMap.get(category.getId());
            }
            holder.prayerNumTv.setText(String.valueOf(category.getView()));
            final int finalCount = count;
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = PrayerCategoryDetailActivity.createIntent(PrayerCategoryGridActivity.this, String.valueOf(category.getId()), category.getName(), finalCount);
                    startActivity(intent);
                    Bundle params = new Bundle();
                    params.putString("prayer_topic", category.getName());
                    NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_PRAYES_CATEGROY_CLICK, params);
                    SelfAnalyticsHelper.sendPrayerAnalytics(PrayerCategoryGridActivity.this, AnalyticsConstants.A_CLICK_PRAYER_TOPIC, category.getName());
                    UserBehaviorAnalytics.trackUserBehavior(PrayerCategoryGridActivity.this, AnalyticsConstants.P_PRAYER, category.getName());
                }
            });
        }

        @Override
        public int getItemCount() {
            return mCategories.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return 1;
            } else {
                return 2;
            }
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return getItemViewType(position) == 1
                                ? gridManager.getSpanCount() : 1;
                    }
                });
            }
        }
    }

    private class LoadCategoryTask extends OmAsyncTask<Void, Void, List<PrayerCategoryResponse.CategoryBean>> {
        @Override
        protected List<PrayerCategoryResponse.CategoryBean> doInBackground(Void... voids) {
            try {
                PrayerService prayerService = BaseRetrofit.getPrayerService();
                Call<PrayerCategoryResponse> call = prayerService.getPrayerCategory();
                Response<PrayerCategoryResponse> response = call.execute();
                if (response != null) {
                    PrayerCategoryResponse prayerCategoryResponse = response.body();
                    if (prayerCategoryResponse != null && prayerCategoryResponse.getData() != null) {
                        //load prayer people data
                        loadPrayerPeopleData(prayerService);
                        return prayerCategoryResponse.getData();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        private void loadPrayerPeopleData(PrayerService prayerService) {
            try {
                Call<PrayerPeopleResponse> peopleNumCall = prayerService.getPrayerPeopleNum();
                Response<PrayerPeopleResponse> peopleNumResponse = peopleNumCall.execute();
                if (peopleNumResponse != null) {
                    PrayerPeopleResponse prayerPeopleResponse = peopleNumResponse.body();
                    if (prayerPeopleResponse != null) {
                        PrayerPeopleResponse.DataBean dataBean = prayerPeopleResponse.getData();
                        if (dataBean != null) {
                            mCurrentPrayPeopleNum = Utility.getCurrentTimePrayerPeople(dataBean.getTotal());
                            loadPrayerCategoryPeople(dataBean.getCategory());
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void loadPrayerCategoryPeople(List<PrayerPeopleResponse.DataBean.CategoryRadioBean> categoryRadioBeanList) {
            if (categoryRadioBeanList == null || categoryRadioBeanList.isEmpty() || mCurrentPrayPeopleNum == 0) {
                return;
            }
            if (!mCategoryPeopleNumMap.isEmpty()) {
                mCategoryPeopleNumMap.clear();
            }
            for (PrayerPeopleResponse.DataBean.CategoryRadioBean categoryRadioBean : categoryRadioBeanList) {
                mCategoryPeopleNumMap.put(categoryRadioBean.getId(), (int) (categoryRadioBean.getRatio() * mCurrentPrayPeopleNum));
            }
        }

        @Override
        protected void onPostExecute(List<PrayerCategoryResponse.CategoryBean> devotionBeans) {
            int size = devotionBeans != null ? devotionBeans.size() : 0;
            if (size > 0) {
                if (mCategories != null) {
                    mCategories.clear();
                }
                mCategories.addAll(devotionBeans);
                mCategoryAdapter.notifyDataSetChanged();

                mEmptyLayout.setVisibility(View.GONE);
            } else if (mCategories.size() == 0) {
                mEmptyLayout.setVisibility(View.VISIBLE);
            }
            mLoadingView.setVisibility(View.GONE);

            mRefreshLayout.finishRefreshing();
            mRefreshLayout.finishLoadmore();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_prayer_cat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.prayer_notification_setting) {
            startActivity(new Intent(this, DailyPrayerReminderSettingActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}



