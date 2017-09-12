package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fw.basemodules.utils.OmAsyncTask;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.BaseRetrofit;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.DevotionService;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.DevotionSitesResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.view.LoadingView;
import de.greenrobot.event.EventBus;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import yuku.afw.V;

/**
 * Created by Mr_ZY on 2017/3/8.
 */

public class DevotionSitesGuideActivity extends BaseActivity {

    private View mGridRoot, mGridTop, mGridToolbarLayout, mGridToolbarShadow, mGridEmptyList, mSubscribeLayout;
    private ObservableRecyclerView mRecyclerViewGrid;
    private SiteGridAdapter mGridAdapter;
    private TextView mSubscribe, mGridToolbarTitle;

    private LoadingView mLoadingView;

    private View mLoadRootLayout, mEmptyLayout;
    private TextView mNoItemTitle, mNoItemButton, mNoItemMsg;
    private ImageView mNoItemImage;

    private List<SiteBeanCustom> mSites;
    private int mSelectedSize = 0;
    private LoadDevotionSitesTask mLoadDataTask;
    private LayoutInflater mInflater;

    private int mImgWidth, mImgHeight, mGridHeaderHeight, mActionbarHeight;

    public class SiteBeanCustom {
        DevotionSitesResponse.DataBean siteBean;
        boolean isSubscribe;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devotion_site_guide);

        initVar();

        initView();

        mLoadDataTask = new LoadDevotionSitesTask();
        mLoadDataTask.execute();
    }

    private void initVar() {
        mInflater = getLayoutInflater();
        int space = getResources().getDimensionPixelSize(R.dimen.margin_48);
        mImgWidth = (getResources().getDisplayMetrics().widthPixels - space) / 3;
        mImgHeight = mImgWidth;
        mGridHeaderHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);
        mActionbarHeight = getResources().getDimensionPixelSize(R.dimen.actionbar_height);
        mSites = new ArrayList<>();
    }


    private void initView() {
        mGridToolbarLayout = V.get(this, R.id.toolbar_layout);
        mGridToolbarShadow = V.get(this, R.id.toolbar_shadow);
        mGridEmptyList = V.get(this, R.id.sites_empty_grid_list);
        mGridRoot = V.get(this, R.id.grid_root);
        mGridTop = V.get(this, R.id.devotion_site_grid_top);
        mGridToolbarTitle = V.get(this, R.id.grid_toolbar_title);
        mSubscribeLayout = V.get(this, R.id.subscribe_all_layout);
        mSubscribe = V.get(this, R.id.subscribe_all);
        mSubscribe.setClickable(false);
        mRecyclerViewGrid = V.get(this, R.id.grid_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerViewGrid.setLayoutManager(gridLayoutManager);
        mRecyclerViewGrid.setScrollViewCallbacks(mScrollCallbacks);

        mSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSubscribe();
                startActivity(new Intent(DevotionSitesGuideActivity.this, DevotionAllListActivity.class));
                finish();
                Utility.setIsRecommendDevotionSite(DevotionSitesGuideActivity.this, false);
            }
        });

        initRefreshAndLoadingViews();
    }

    private void initRefreshAndLoadingViews() {
        ProgressLayout headerView = new ProgressLayout(this);
        headerView.setColorSchemeResources(R.color.theme_color_accent);
        mLoadRootLayout = V.get(this, R.id.no_connection_layout);
        mLoadingView = V.get(this, R.id.loading_view);
        V.get(this, R.id.loading_layout).setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);

        mEmptyLayout = V.get(this, R.id.empty_layout);
        mEmptyLayout.setVisibility(View.GONE);
        mNoItemImage = V.get(this, R.id.empty_icon);
        mNoItemImage.setImageResource(R.drawable.icon_no_connection);
        mNoItemTitle = V.get(this, R.id.tEmpty);
        mNoItemTitle.setText(getString(R.string.no_connection));
        mNoItemMsg = V.get(this, R.id.msgEmpty);
        mNoItemMsg.setText(getString(R.string.empty_verse_of_the_day));
        mEmptyLayout.setBackgroundColor(getResources().getColor(R.color.white));
        mNoItemTitle.setTextColor(getResources().getColor(R.color.black));
        mNoItemButton = V.get(this, R.id.bRetry);
        mNoItemButton.setVisibility(View.GONE);
        initIsRecommend();
    }

    private void initIsRecommend() {
        mGridRoot.setVisibility(View.VISIBLE);
    }

    private void saveSubscribe() {
        int subscribedCount = 0;
        for (int i = 0; i < mSites.size(); i++) {
            SiteBeanCustom siteBeanCustom = mSites.get(i);
            if (siteBeanCustom.isSubscribe) {
                S.getDb().addDevotionSiteSubscribe(siteBeanCustom.siteBean.getId());
                subscribedCount++;
            }
        }
    }


    private ObservableScrollViewCallbacks mScrollCallbacks = new ObservableScrollViewCallbacks() {
        @Override
        public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
            ViewHelper.setTranslationY(mGridTop, -scrollY / 2);

            int baseColor = getResources().getColor(R.color.white);
            float alpha = Math.min(1, (float) scrollY / (mGridHeaderHeight - mActionbarHeight));
            mGridToolbarLayout.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
            if ((-scrollY + mGridHeaderHeight) < mActionbarHeight) {
                mGridToolbarTitle.setVisibility(View.VISIBLE);
                mGridToolbarShadow.setVisibility(View.VISIBLE);
            } else {
                mGridToolbarTitle.setVisibility(View.GONE);
                mGridToolbarShadow.setVisibility(View.GONE);
            }
        }

        @Override
        public void onDownMotionEvent() {
        }

        @Override
        public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        }
    };


    class ViewHolderGridHeader extends RecyclerView.ViewHolder {


        public ViewHolderGridHeader(View itemView) {
            super(itemView);
        }
    }

    class ViewHolderGrid extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title;
        View rootView;
        ImageButton checkbox;

        public ViewHolderGrid(View itemView) {
            super(itemView);
            rootView = itemView;
            image = V.get(itemView, R.id.image);
            title = V.get(itemView, R.id.title);
            checkbox = V.get(itemView, R.id.checkbox);
        }
    }


    private class SiteGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 1) {
                View paddingView = new View(DevotionSitesGuideActivity.this);
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, mGridHeaderHeight);
                paddingView.setLayoutParams(lp);
                return new ViewHolderGridHeader(paddingView);
            }
            ViewHolderGrid holder;
            View view = mInflater.inflate(R.layout.item_devotion_site_guide, parent, false);
            holder = new ViewHolderGrid(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            if (position == 0 || position > mSites.size()) {
                return;
            }
            final ViewHolderGrid holder2 = (ViewHolderGrid) holder;
            final SiteBeanCustom siteBeanCustom = mSites.get(position - 1);
            if (siteBeanCustom == null || siteBeanCustom.siteBean == null) {
                return;
            }
            final DevotionSitesResponse.DataBean site = siteBeanCustom.siteBean;

            Picasso.with(DevotionSitesGuideActivity.this).load(site.getImageUrl()).resize(mImgWidth, mImgHeight).into(holder2.image);
            holder2.title.setText(site.getName());
            holder2.checkbox.setSelected(siteBeanCustom.isSubscribe);
            holder2.checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBoxClick(holder2.checkbox, position - 1);
                }
            });
            holder2.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBoxClick(holder2.checkbox, position - 1);
                }
            });

        }

        @Override
        public int getItemCount() {
            return mSites.size() + 1;
        }

        private void checkBoxClick(ImageButton checkbox, int position) {
            checkbox.setSelected(!checkbox.isSelected());
            mSites.get(position).isSubscribe = checkbox.isSelected();
            if (checkbox.isSelected()) {
                mSelectedSize++;
            } else {
                mSelectedSize--;
            }
            mSubscribe.setEnabled(mSelectedSize <= 0 ? false : true);
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


    private class LoadDevotionSitesTask extends OmAsyncTask<Void, Void, List<SiteBeanCustom>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mSites == null || mSites.size() <= 0) {
                mSubscribeLayout.setVisibility(View.GONE);
            }
            mGridEmptyList.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<SiteBeanCustom> doInBackground(Void... voids) {
            try {
                DevotionService service = BaseRetrofit.getDevotionService();
                RequestBody body = RequestBody.create(MediaType.parse("application/json"), Utility.getSiteLastedDevotionIds(DevotionSitesGuideActivity.this));
                Call<DevotionSitesResponse> call = service.getDevotionSites(body);
                Response<DevotionSitesResponse> response = call.execute();
                DevotionSitesResponse devotionSitesResponse = response != null ? response.body() : null;
                if (devotionSitesResponse != null && devotionSitesResponse.getData() != null) {
                    List<DevotionSitesResponse.DataBean> siteBeans = devotionSitesResponse.getData();
                    for (int i = 0; i < siteBeans.size(); i++) {
                        SiteBeanCustom siteBeanCustom = new SiteBeanCustom();
                        DevotionSitesResponse.DataBean siteBean = siteBeans.get(i);
                        siteBeanCustom.siteBean = siteBean;
                        siteBeanCustom.isSubscribe = true;
                        mSites.add(siteBeanCustom);
                    }
                    mSelectedSize = mSites.size();
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
                mGridAdapter = new SiteGridAdapter();
                mRecyclerViewGrid.setAdapter(mGridAdapter);
                mGridAdapter.notifyDataSetChanged();
                mGridRoot.setVisibility(View.VISIBLE);
                mSubscribeLayout.setVisibility(View.VISIBLE);
                mLoadRootLayout.setVisibility(View.GONE);

            } else if (size == 0) {
                mLoadingView.setVisibility(View.GONE);
                mEmptyLayout.setVisibility(View.VISIBLE);
            }
            if (mGridEmptyList.getVisibility() == View.VISIBLE) {
                mGridEmptyList.setVisibility(View.GONE);
            }
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



