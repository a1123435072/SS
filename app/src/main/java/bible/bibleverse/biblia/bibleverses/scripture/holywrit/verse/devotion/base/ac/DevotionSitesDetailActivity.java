package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.view.LoadingView;
import de.greenrobot.event.EventBus;
import jp.wasabeef.blurry.Blurry;
import retrofit2.Call;
import retrofit2.Response;
import yuku.afw.V;


/**
 * Created by yzq on 2017/2/27.
 */

public class DevotionSitesDetailActivity extends BaseActivity {


    public static final String REFRESH = "REFRESH";
    public static final String LOAD_MORE = "LOAD_MORE";
    public static final String NONE = "NONE";

    private View mBackBtn;
    private ImageView mBackImage, mTopBg, mTopIcon;

    private TextView mToolbarTitle, mTopTitle, mSubscribeButton;
    private View mToolbarView, mToolbarShadow, mTopLayout, mListBackgroundView;
    private int mParallaxImageHeight;


    private ObservableListView mListView;
    private DevotionAdapter mDevotionAdapter;
    private TwinklingRefreshLayout mRefreshLayout;

    private LoadingView mLoadingView;

    private LinearLayout mEmptyLayout;
    private TextView mNoItemTitle, mNoItemButton, mNoItemMsg;
    private ImageView mNoItemImage;


    private List<DevotionBean> mDevotions;
    private LoadDevotionTask mLoadDataTask;
    private String mNextPageStart, mPreviousEndId;

    private LayoutInflater mInflater;

    private int mActionbarHeight;
    private int mSiteId;
    private String mSiteName, mSiteImg;
    private boolean mIsSubscribe;
    private boolean mIsLoadingData = false;

    private String mLoadDataType;
    private List<Integer> mReadDevotions = new ArrayList<>();
    private String mCurrentDate;


    public static Intent createIntent(Context context, int id, String name, String imageUrl, boolean subscribe) {
        Intent intent = new Intent(context, DevotionSitesDetailActivity.class);
        intent.putExtra(Constants.SITE_ID, id);
        intent.putExtra(Constants.SITE_NAME, name);
        intent.putExtra(Constants.SITE_IMAGE, imageUrl);
        intent.putExtra(Constants.SITE_SUBSCRIBE, subscribe);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_detail);

        handleIntent();
        initVar();

        initToolbar();
        initView();

        mLoadDataTask = new LoadDevotionTask();
        mLoadDataTask.execute(NONE);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mSiteId = intent.getIntExtra(Constants.SITE_ID, 0);
        mSiteName = intent.getStringExtra(Constants.SITE_NAME);
        mSiteImg = intent.getStringExtra(Constants.SITE_IMAGE);
        mIsSubscribe = intent.getBooleanExtra(Constants.SITE_SUBSCRIBE, false);
    }

    private void initVar() {
        mInflater = getLayoutInflater();

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);
        mActionbarHeight = getResources().getDimensionPixelSize(R.dimen.actionbar_height);
        mNextPageStart = DateTimeUtil.getDateStr4ApiRequest(System.currentTimeMillis());
        mDevotions = new ArrayList<>();
        mCurrentDate = DateTimeUtil.getDateStr4ApiRequest(System.currentTimeMillis());
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
        mToolbarTitle.setText(mSiteName);
    }

    private void initView() {
        mTopLayout = V.get(this, R.id.header_layout);
        mTopBg = V.get(this, R.id.header_bg);
        mTopIcon = V.get(this, R.id.site_icon);
        mTopTitle = V.get(this, R.id.site_title);
        mSubscribeButton = V.get(this, R.id.subscribe_button);

        View paddingView = new View(this);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, mParallaxImageHeight);
        paddingView.setLayoutParams(lp);
        paddingView.setClickable(true);
        mListBackgroundView = findViewById(R.id.list_background);

        mListView = V.get(this, R.id.list_view);
        mListView.addHeaderView(paddingView);
        mDevotionAdapter = new DevotionAdapter();
        mListView.setAdapter(mDevotionAdapter);
        mListView.setScrollViewCallbacks(mScrollCallbacks);

        mRefreshLayout = V.get(this, R.id.refresh_layout);
        ProgressLayout headerView = new ProgressLayout(this);
        headerView.setColorSchemeResources(R.color.theme_color_accent);
        mRefreshLayout.setHeaderView(headerView);
        BottomProgressView bottomView = new BottomProgressView(this);
        bottomView.setNormalColor(ContextCompat.getColor(this, R.color.theme_color_accent));
        bottomView.setAnimatingColor(ContextCompat.getColor(this, R.color.theme_color_accent));
        mRefreshLayout.setBottomView(bottomView);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);

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
        if (mIsSubscribe) {
            subscribe();
        } else {
            unSubscribe();
        }
        mNoItemButton.setVisibility(View.GONE);
        mEmptyLayout.setBackgroundColor(getResources().getColor(R.color.white));
        mNoItemTitle.setTextColor(getResources().getColor(R.color.black));
        mTopTitle.setText(mSiteName);
        Picasso.with(this).load(mSiteImg).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
                if (mTopBg != null) {
                    mTopIcon.setImageBitmap(bitmap);
                    try {
                        Blurry.with(DevotionSitesDetailActivity.this)
                                .radius(18)
                                .sampling(8)
                                .from(bitmap)
                                .into(mTopBg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onBitmapFailed(Drawable drawable) {

            }

            @Override
            public void onPrepareLoad(Drawable drawable) {

            }
        });
    }

    private void subscribe() {
        mSubscribeButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.unsubscrbe_button_bg));
        mSubscribeButton.setTextColor(getResources().getColor(R.color.black_999999));
        mSubscribeButton.setText(getResources().getString(R.string.un_subscribe));
        mSubscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unSubscribe();
                S.getDb().deleteDevotionSiteSubscribeById(mSiteId);
                EventBus.getDefault().post(new EventSiteSubscribeChange());
            }
        });
    }

    private void unSubscribe() {
        mSubscribeButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.feedback_btn_bg));
        mSubscribeButton.setTextColor(getResources().getColor(R.color.white));
        mSubscribeButton.setText(getResources().getString(R.string.subscribe));
        mSubscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscribe();
                S.getDb().addDevotionSiteSubscribe(mSiteId);
                EventBus.getDefault().post(new EventSiteSubscribeChange());
            }
        });
    }

    private ObservableScrollViewCallbacks mScrollCallbacks = new ObservableScrollViewCallbacks() {
        @Override
        public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
            int baseColor = getResources().getColor(R.color.white);
            float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
            mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
            ViewHelper.setTranslationY(mTopLayout, -scrollY / 2);
            ViewHelper.setTranslationY(mSubscribeButton, -scrollY / 2);

            // Translate list background
            ViewHelper.setTranslationY(mListBackgroundView, Math.max(0, -scrollY + mParallaxImageHeight));
            if ((-scrollY + mParallaxImageHeight) < mActionbarHeight) {
                mBackImage.setImageResource(R.drawable.ic_back_black);
                mToolbarShadow.setVisibility(View.VISIBLE);
                mToolbarTitle.setVisibility(View.VISIBLE);
                mToolbarTitle.setTextColor(Color.BLACK);
            } else {
                mBackImage.setImageResource(R.drawable.ic_back_white);
                mToolbarShadow.setVisibility(View.GONE);
                mToolbarTitle.setVisibility(View.GONE);
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
            if (!mIsLoadingData) {
                mLoadDataTask = new LoadDevotionTask();
                mLoadDataTask.execute(REFRESH);
            }

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

        ImageView image, newFlag;
        TextView title, summary, readNum, source;
        View rootView;

        public ViewHolder(View itemView) {
            rootView = itemView;
            image = V.get(itemView, R.id.image);
            title = V.get(itemView, R.id.title);
            summary = V.get(itemView, R.id.content);
            readNum = V.get(itemView, R.id.read_num);
            source = V.get(itemView, R.id.source);
            newFlag = V.get(itemView, R.id.new_tag_icon);
        }
    }

    private class DevotionAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return mDevotions.size();
        }

        @Override
        public Object getItem(int position) {
            return mDevotions.get(position);

        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holderDevotion;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_devotion, parent, false);
                holderDevotion = new ViewHolder(convertView);
                convertView.setTag(holderDevotion);
            } else {
                holderDevotion = (ViewHolder) convertView.getTag();
            }
            final DevotionBean devotion = mDevotions.get(position);
            if (devotion.getImageUrl() == null || TextUtils.isEmpty(devotion.getImageUrl())) {
                holderDevotion.image.setVisibility(View.INVISIBLE);
            } else {
                holderDevotion.image.setVisibility(View.VISIBLE);
                Picasso.with(DevotionSitesDetailActivity.this).load(devotion.getImageUrl()).into(holderDevotion.image);
            }
            holderDevotion.title.setText(devotion.getTitle());
            holderDevotion.summary.setText(devotion.getContent());
            holderDevotion.source.setText(DateTimeUtil.getLocaleDateStr4Display(devotion.getDate()));
            holderDevotion.readNum.setText(String.valueOf(devotion.getView()));
            if (devotion.getIsHot() == 1) {
                holderDevotion.readNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_hot, 0, 0, 0);
            } else {
                holderDevotion.readNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_view, 0, 0, 0);
            }
            if (!devotion.getDate().equalsIgnoreCase(mCurrentDate) || mReadDevotions.contains(devotion.getId())) {
                holderDevotion.newFlag.setVisibility(View.GONE);
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
                    startActivity(DevotionDetailWebActivity.createIntent(DevotionSitesDetailActivity.this, devotion));
                    addReadDevotionId(devotion.getId());
                }
            });
            return convertView;
        }
    }

    private void addReadDevotionId(int id) {
        mReadDevotions.add(id);
        if (mDevotionAdapter != null) {
            mDevotionAdapter.notifyDataSetChanged();
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
            String type = voids[0];
            mLoadDataType = type;
            try {
                DevotionService devotionService = BaseRetrofit.getDevotionService();
                Call<DevotionListResponse> call = null;
                switch (type) {
                    case REFRESH:
                        call = devotionService.getSiteDevotionList(String.valueOf(mSiteId), String.valueOf(mPreviousEndId), "");
                        break;
                    case LOAD_MORE:
                        call = devotionService.getSiteDevotionList(String.valueOf(mSiteId), "", String.valueOf(mNextPageStart));
                        break;
                    case NONE:
                        call = devotionService.getSiteDevotionList(String.valueOf(mSiteId), "", "");
                        break;
                    default:
                        call = devotionService.getSiteDevotionList(String.valueOf(mSiteId), "", "");
                        break;
                }
                Response<DevotionListResponse> response = call.execute();
                DevotionListResponse DevotionListResponse = response != null ? response.body() : null;
                if (DevotionListResponse != null && DevotionListResponse.getData() != null && DevotionListResponse.getData().getLists() != null) {
                    if (type == REFRESH || type == NONE) {
                        saveSiteLastDevotionId(DevotionListResponse.getData().getLists());
                    }

                    mReadDevotions = S.getDb().listAllDevotionHistoryByDevotionSource(mSiteName);
                    return DevotionListResponse.getData().getLists();
                }
            } catch (IOException e) {
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
                switch (mLoadDataType) {
                    case REFRESH:
                        mPreviousEndId = String.valueOf(devotionBeans.get(0).getId() + 1);
                        mDevotions.addAll(devotionBeans);
                        break;
                    case LOAD_MORE:
                        mDevotions.addAll(devotionBeans);
                        mNextPageStart = String.valueOf(last.getId());
                        break;
                    case NONE:
                        mPreviousEndId = String.valueOf(devotionBeans.get(0).getId() + 1);
                        mDevotions.addAll(devotionBeans);
                        mNextPageStart = String.valueOf(last.getId());
                        break;
                    default:
                        break;

                }
                mDevotionAdapter.notifyDataSetChanged();
                mLoadingView.setVisibility(View.GONE);
                mEmptyLayout.setVisibility(View.GONE);
            } else if (mDevotions.size() == 0) {
                mLoadingView.setVisibility(View.GONE);
                mEmptyLayout.setVisibility(View.VISIBLE);
            }

            mRefreshLayout.finishRefreshing();
            mRefreshLayout.finishLoadmore();
            mIsLoadingData = false;
        }
    }

    private void saveSiteLastDevotionId(List<DevotionBean> listsBeen) {
        if (listsBeen.size() <= 0) {
            return;
        }
        boolean isUpdated = false;
        int newLastId = listsBeen.get(0).getId();
        String json = Utility.getSiteLastedDevotionIds(this);
        try {
            if (!json.isEmpty()) {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject js = jsonArray.getJSONObject(i);
                    if (js.optInt(Constants.SITEID) == mSiteId) {
                        js.put(Constants.SITE_LASTED_ID, newLastId);
                        jsonArray.put(i, js);
                        isUpdated = true;
                        break;
                    }
                }
                if (!isUpdated) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(Constants.SITEID, mSiteId);
                    jsonObject.put(Constants.SITE_LASTED_ID, newLastId);
                    jsonArray.put(jsonObject);
                    Utility.setSiteLastedDevotionIds(this, jsonArray.toString());
                }
                Utility.setSiteLastedDevotionIds(this, jsonArray.toString());
            } else {
                JSONArray newJsonArr = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Constants.SITEID, mSiteId);
                jsonObject.put(Constants.SITE_LASTED_ID, newLastId);
                newJsonArr.put(jsonObject);
                Utility.setSiteLastedDevotionIds(this, newJsonArr.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
