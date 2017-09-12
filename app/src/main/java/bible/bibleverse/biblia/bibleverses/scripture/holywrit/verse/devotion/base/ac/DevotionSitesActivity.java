package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fw.basemodules.utils.OmAsyncTask;
import com.lcodecore.tkrefreshlayout.Footer.BottomProgressView;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.BaseRetrofit;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.DevotionService;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.DevotionSitesResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventSiteSubscribeChange;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.view.LoadingView;
import de.greenrobot.event.EventBus;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import yuku.afw.V;

/**
 * Created by yzq on 2017/3/1.
 */

public class DevotionSitesActivity extends BaseActivity {

    private RecyclerView mRecyclerViewList;
    private SiteListAdapter mListAdapter;
    private TwinklingRefreshLayout mRefreshLayout;

    private LoadingView mLoadingView;

    private View mLoadRootLayout, mEmptyLayout;
    private TextView mNoItemTitle, mNoItemButton, mNoItemMsg;
    private ImageView mNoItemImage;

    private List<SiteBeanCustom> mSites;
    private LoadDevotionSitesTask mLoadDataTask;
    private LayoutInflater mInflater;

    private int mImgWidth, mImgHeight;

    public class SiteBeanCustom {
        DevotionSitesResponse.DataBean siteBean;
        boolean isSubscribe;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devotion_site);

        initVar();

        initToolbar();
        initView();

        mLoadDataTask = new LoadDevotionSitesTask();
        mLoadDataTask.execute();
        EventBus.getDefault().register(this);
    }

    private void initVar() {
        mInflater = getLayoutInflater();
        int space = getResources().getDimensionPixelSize(R.dimen.margin_48);
        mImgWidth = (getResources().getDisplayMetrics().widthPixels - space) / 3;
        mImgHeight = mImgWidth;
        mSites = new ArrayList<>();
    }

    private void initToolbar() {
        final Toolbar toolbar = V.get(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_back_black);
        setTitle(getString(R.string.subscribe) + " " + getString(R.string.devotion));
    }

    private void initView() {
        mRecyclerViewList = V.get(this, R.id.list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerViewList.setLayoutManager(linearLayoutManager);
        initRefreshAndLoadingViews();
    }

    private void initRefreshAndLoadingViews() {
        mRefreshLayout = V.get(this, R.id.refresh_layout);
        ProgressLayout headerView = new ProgressLayout(this);
        headerView.setColorSchemeResources(R.color.theme_color_accent);
        mRefreshLayout.setHeaderView(headerView);
        BottomProgressView bottomView = new BottomProgressView(this);
        bottomView.setNormalColor(ContextCompat.getColor(this, R.color.theme_color_accent));
        bottomView.setAnimatingColor(ContextCompat.getColor(this, R.color.theme_color_accent));
        mRefreshLayout.setBottomView(bottomView);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
        mRefreshLayout.setEnableLoadmore(false);

        mLoadRootLayout = V.get(this, R.id.no_connection_layout);
        V.get(this, R.id.loading_layout).setVisibility(View.GONE);
        mLoadingView = V.get(this, R.id.loading_view);

        mEmptyLayout = V.get(this, R.id.empty_layout);
        mEmptyLayout.setVisibility(View.GONE);
        mNoItemImage = V.get(this, R.id.empty_icon);
        mNoItemImage.setImageResource(R.drawable.icon_no_connection);
        mNoItemTitle = V.get(this, R.id.tEmpty);
        mNoItemTitle.setText(getString(R.string.no_connection));
        mNoItemMsg = V.get(this, R.id.msgEmpty);
        mNoItemMsg.setText(getString(R.string.empty_verse_of_the_day));
        mEmptyLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        mNoItemTitle.setTextColor(ContextCompat.getColor(this, R.color.black));
        mNoItemButton = V.get(this, R.id.bRetry);
        mNoItemButton.setVisibility(View.GONE);
        initIsRecommend();
    }

    private void initIsRecommend() {

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mLoadRootLayout.getLayoutParams();
        layoutParams.setMargins(0, getResources().getDimensionPixelSize(R.dimen.actionbar_height_include_shadow), 0, 0);
        mLoadRootLayout.setLayoutParams(layoutParams);
    }

    private RefreshListenerAdapter mRefreshListener = new RefreshListenerAdapter() {
        @Override
        public void onRefresh(TwinklingRefreshLayout refreshLayout) {
            if (mSites != null) {
                mSites.clear();
            }
            mLoadDataTask = new LoadDevotionSitesTask();
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

    class ViewHolderList extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title, subscribeNum;
        View rootView, subscribeLayout, newContentFlag;
        ImageView subscribe;

        public ViewHolderList(View itemView) {
            super(itemView);
            rootView = V.get(itemView, R.id.root_layout);
            image = V.get(itemView, R.id.image);
            title = V.get(itemView, R.id.title);
            subscribeNum = V.get(itemView, R.id.subscribe_num);
            subscribe = V.get(itemView, R.id.subscribe);
            subscribeLayout = V.get(itemView, R.id.subscribe_layout);
            newContentFlag = V.get(itemView, R.id.new_content_flag);
        }
    }

    private class SiteListAdapter extends RecyclerView.Adapter<ViewHolderList> {

        @Override
        public ViewHolderList onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolderList holder;
            View view = mInflater.inflate(R.layout.item_devotion_site, parent, false);
            holder = new ViewHolderList(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolderList holder, final int position) {
            if (mSites.size() <= position) {
                return;
            }
            final SiteBeanCustom siteBeanCustom = mSites.get(position);
            final DevotionSitesResponse.DataBean site = siteBeanCustom.siteBean;
            Picasso.with(DevotionSitesActivity.this).load(site.getImageUrl()).resize(mImgWidth, mImgHeight).into(holder.image);
            holder.title.setText(site.getName());
            if (site.getSubscribe() == 0) {
                holder.subscribeNum.setVisibility(View.GONE);
            } else {
                holder.subscribeNum.setVisibility(View.VISIBLE);
                holder.subscribeNum.setText(getString(R.string.subscribe_num, site.getSubscribe()));
            }
            holder.subscribeLayout.setVisibility(siteBeanCustom.isSubscribe ? View.INVISIBLE : View.VISIBLE);
            holder.subscribeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!siteBeanCustom.isSubscribe) {
                        siteBeanCustom.isSubscribe = true;
                        mSites.set(position, siteBeanCustom);
                        mListAdapter.notifyDataSetChanged();
                        S.getDb().addDevotionSiteSubscribe(site.getId());
                        EventBus.getDefault().post(new EventSiteSubscribeChange());
                        Toast.makeText(DevotionSitesActivity.this, getString(R.string.subscribe_success), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = DevotionSitesDetailActivity.createIntent(DevotionSitesActivity.this, site.getId(), site.getName(), site.getImageUrl(), siteBeanCustom.isSubscribe);
                    startActivity(intent);
                }
            });

            holder.newContentFlag.setVisibility(site.getIsUpdated() ? View.VISIBLE : View.GONE);

        }

        @Override
        public int getItemCount() {
            return mSites.size();
        }

    }

    private void sortDevotionSites() {
        Collections.sort(mSites, new Comparator<SiteBeanCustom>() {
            @Override
            public int compare(SiteBeanCustom lhs, SiteBeanCustom rhs) {
                return new Boolean(rhs.isSubscribe).compareTo(new Boolean(lhs.isSubscribe));
            }
        });
    }

    private void refreshSubscribeSites() {
        if (mSites == null || mSites.size() <= 0) {
            return;
        }
        for (int i = 0; i < mSites.size(); i++) {
            SiteBeanCustom siteBeanCustom = mSites.get(i);
            DevotionSitesResponse.DataBean bean = siteBeanCustom.siteBean;
            siteBeanCustom.isSubscribe = S.getDb().isInDevotionSubscribe(bean.getId());
            mSites.set(i, siteBeanCustom);
        }
        sortDevotionSites();
        if (mListAdapter != null) {
            mListAdapter.notifyDataSetChanged();
        }
    }

    private class LoadDevotionSitesTask extends OmAsyncTask<Void, Void, List<SiteBeanCustom>> {
        @Override
        protected List<SiteBeanCustom> doInBackground(Void... voids) {
            try {
                DevotionService service = BaseRetrofit.getDevotionService();
                RequestBody body = RequestBody.create(MediaType.parse("application/json"), Utility.getSiteLastedDevotionIds(DevotionSitesActivity.this));
                Call<DevotionSitesResponse> call = service.getDevotionSites(body);
                Response<DevotionSitesResponse> response = call.execute();

                DevotionSitesResponse devotionSitesResponse = response != null ? response.body() : null;
                if (devotionSitesResponse != null && devotionSitesResponse.getData() != null) {
                    List<DevotionSitesResponse.DataBean> siteBeans = devotionSitesResponse.getData();
                    for (int i = 0; i < siteBeans.size(); i++) {
                        SiteBeanCustom siteBeanCustom = new SiteBeanCustom();
                        DevotionSitesResponse.DataBean siteBean = siteBeans.get(i);
                        siteBeanCustom.siteBean = siteBean;
                        siteBeanCustom.isSubscribe = S.getDb().isInDevotionSubscribe(siteBean.getId());
                        mSites.add(siteBeanCustom);
                    }
                    sortDevotionSites();
                    return mSites;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<SiteBeanCustom> siteBeans) {


            int size = siteBeans != null ? siteBeans.size() : 0;
            if (size > 0) {
                mListAdapter = new SiteListAdapter();
                mRecyclerViewList.setAdapter(mListAdapter);
                mListAdapter.notifyDataSetChanged();
                mLoadRootLayout.setVisibility(View.GONE);

            } else if (size == 0) {
                mLoadingView.setVisibility(View.GONE);
                mEmptyLayout.setVisibility(View.VISIBLE);
            }
            mRefreshLayout.finishRefreshing();
            mRefreshLayout.finishLoadmore();
        }
    }


    public void onEventMainThread(EventSiteSubscribeChange event) {
        if (event != null) {
            refreshSubscribeSites();
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
        EventBus.getDefault().unregister(this);
    }
}



