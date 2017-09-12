package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
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
import java.util.List;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.BaseRetrofit;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.DevotionService;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.DevotionBean;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.DevotionListResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventSiteSubscribeChange;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.DateTimeUtil;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.view.LoadingView;
import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Response;
import yuku.afw.V;


/**
 * Created by Mr_ZY on 2017/3/8.
 */

public class DevotionAllListActivity extends BaseActivity {

    public static final String LOAD_MORE = "LOADMORE";
    public static final String NONE = "NONE";

    private RecyclerView mRecycleView;
    private DevotionAdapter mDevotionAdapter;
    private TwinklingRefreshLayout mRefreshLayout;

    private ProgressBar mProgressBar;
    private TextView mProgressText;
    private LoadingView mLoadingView;

    private LinearLayout mEmptyLayout;
    private TextView mNoItemTitle, mNoItemButton, mNoItemMsg;
    private ImageView mNoItemImage;


    private List<DevotionBean> mDevotions;
    private LoadDevotionTask mLoadDataTask;
    private String mNextPageStart;

    private LayoutInflater mInflater;

    private boolean mIsLoadingData = false;

    private String mLoadDataType;
    private List<Integer> mReadDevotions = new ArrayList<>();
    private String mCurrentDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devotion_all_list);
        handleIntent();
        initVar();
        initToolbar();
        initView();

        mLoadDataTask = new LoadDevotionTask();
        mLoadDataTask.execute(NONE);
        EventBus.getDefault().register(this);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
    }

    private void initVar() {
        mInflater = getLayoutInflater();
        mDevotions = new ArrayList<>();
        mCurrentDate = DateTimeUtil.getDateStr4ApiRequest(System.currentTimeMillis());
    }


    private void initToolbar() {
        final Toolbar toolbar = V.get(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_back_black);
        setTitle(R.string.devotion);
    }

    private void initView() {

        mRecycleView = V.get(this, R.id.list_view);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mDevotionAdapter = new DevotionAdapter();
        mRecycleView.setAdapter(mDevotionAdapter);

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

        mNoItemButton.setVisibility(View.GONE);
        mEmptyLayout.setBackgroundColor(getResources().getColor(R.color.white));
        mNoItemTitle.setTextColor(getResources().getColor(R.color.black));
    }


    private RefreshListenerAdapter mRefreshListener = new RefreshListenerAdapter() {
        @Override
        public void onRefresh(TwinklingRefreshLayout refreshLayout) {
        }

        @Override
        public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
            if (!mIsLoadingData) {
                mLoadDataTask = new LoadDevotionTask();
                mLoadDataTask.execute(LOAD_MORE);
            }

        }

        @Override
        public void onRefreshCanceled() {
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
        EventBus.getDefault().unregister(this);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image, newFlag;
        TextView title, summary, readNum, source;
        View rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            image = V.get(itemView, R.id.image);
            title = V.get(itemView, R.id.title);
            summary = V.get(itemView, R.id.content);
            readNum = V.get(itemView, R.id.read_num);
            source = V.get(itemView, R.id.source);
            newFlag = V.get(itemView, R.id.new_tag_icon);
        }
    }

    private class DevotionAdapter extends RecyclerView.Adapter<ViewHolder> {


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder holder;
            View view = mInflater.inflate(R.layout.item_devotion, parent, false);
            holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holderDevotion, int position) {
            final DevotionBean devotion = mDevotions.get(position);
            if (devotion.getImageUrl() == null || TextUtils.isEmpty(devotion.getImageUrl())) {
                holderDevotion.image.setVisibility(View.GONE);
            } else {
                holderDevotion.image.setVisibility(View.VISIBLE);
                Picasso.with(DevotionAllListActivity.this).load(devotion.getImageUrl()).into(holderDevotion.image);
            }
            holderDevotion.title.setText(devotion.getTitle());
            holderDevotion.summary.setText(Html.fromHtml(devotion.getContent()));
            holderDevotion.source.setText(devotion.getSource());
            holderDevotion.readNum.setText(String.valueOf(devotion.getView()));
            if (devotion.getIsHot() == 1) {
                holderDevotion.readNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_hot, 0, 0, 0);
            } else {
                holderDevotion.readNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_view, 0, 0, 0);
            }
            if (!devotion.getDate().equalsIgnoreCase(mCurrentDate) || mReadDevotions.contains(devotion.getId())) {
                holderDevotion.newFlag.setVisibility(View.INVISIBLE);
            } else {
                holderDevotion.newFlag.setVisibility(View.VISIBLE);
            }

            if (mReadDevotions.contains(devotion.getId())) {
                holderDevotion.title.setTextColor(getResources().getColor(R.color.black_999));
                holderDevotion.summary.setTextColor(getResources().getColor(R.color.black_999));
            } else {
                holderDevotion.title.setTextColor(getResources().getColor(R.color.black));
                holderDevotion.summary.setTextColor(getResources().getColor(R.color.black));
            }
            holderDevotion.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(DevotionDetailWebActivity.createIntent(DevotionAllListActivity.this, devotion));
                    addReadDevotionId(devotion.getId());
                }
            });
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return mDevotions.size();
        }
    }

    private void addReadDevotionId(int id) {
        mReadDevotions.add(id);
        if (mDevotionAdapter != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDevotionAdapter.notifyDataSetChanged();
                }
            }, 500);
        }
    }

    private class LoadDevotionTask extends OmAsyncTask<String, Void, List<DevotionBean>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mIsLoadingData = true;
        }

        @Override
        protected List<DevotionBean> doInBackground(String... voids) {
            mLoadDataType = voids[0];
            List<Integer> site_ids = S.getDb().listAllSubscribeDevotion();
            String subSites = "";
            if (site_ids != null && site_ids.size() > 0) {
                subSites += String.valueOf(site_ids.get(0));
                for (int i = 1; i < site_ids.size(); ++i) {
                    subSites += ("," + site_ids.get(i));
                }
            }
            try {
                DevotionService devotionService = BaseRetrofit.getDevotionService();
                Call<DevotionListResponse> call = null;
                call = devotionService.getDevotionAllList(subSites.isEmpty() ? null : subSites, mNextPageStart);
                Response<DevotionListResponse> response = call.execute();
                DevotionListResponse DevotionListResponse = response != null ? response.body() : null;
                if (DevotionListResponse != null && DevotionListResponse.getData() != null && DevotionListResponse.getData().getLists() != null) {
                    mReadDevotions = S.getDb().listAllDevotionHistory();
                    if (mLoadDataType == NONE) {
                        saveSiteLastDevotionId(DevotionListResponse.getData().getLists());
                    }
                    return DevotionListResponse.getData().getLists();
                }
            } catch (IllegalStateException | IOException | com.google.gson.JsonSyntaxException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute
                (List<DevotionBean> devotionBeans) {
            int size = devotionBeans != null ? devotionBeans.size() : 0;
            if (size > 0) {
                DevotionBean last = devotionBeans.get(size - 1);
                mDevotions.addAll(devotionBeans);
                mNextPageStart = String.valueOf(last.getId());
                mDevotionAdapter.notifyDataSetChanged();
                mLoadingView.setVisibility(View.GONE);
                mEmptyLayout.setVisibility(View.GONE);
            } else if (mDevotions.size() == 0) {
                mLoadingView.setVisibility(View.GONE);
                mEmptyLayout.setVisibility(View.VISIBLE);
            }

            mRefreshLayout.finishLoadmore();
            mIsLoadingData = false;
        }

    }

    private void saveSiteLastDevotionId(List<DevotionBean> listsBeen) {
        if (listsBeen.size() <= 0) {
            return;
        }
        int newLastId = listsBeen.get(0).getId();
        if (newLastId < 0) {
            return;
        }
        Utility.setAllLastedDevotionIds(this, newLastId);
    }

    public void onEventMainThread(EventSiteSubscribeChange event) {
        if (event != null) {
            mDevotions.clear();
            mNextPageStart = null;
            mLoadDataTask = new LoadDevotionTask();
            mLoadDataTask.execute(NONE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_devotion_all_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.devotion_site_sub) {
            startActivity(new Intent(this, DevotionSitesActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
