package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fw.basemodules.utils.OmAsyncTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.SelfAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.BaseRetrofit;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.DevotionService;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.LockScreenResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PrayerBean;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PrayerPeopleResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.DevotionDetailWebActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.NewDailyVerseDetailActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.PrayerDetailActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.CardBean;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.DateTimeUtil;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.protobuf.VODLProto;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.view.CustomViewPager;
import retrofit2.Call;
import retrofit2.Response;
import yuku.afw.V;

/**
 * Created by zhangfei on 3/3/17.
 */

public class ViewPagerCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<LockScreenResponse.DataBean.ListsBean> mDevotions = new ArrayList<>();
    public final int VIEW_TYPE_LOADING = 0;
    public final int VIEW_TYPE_AD_PRAY = 3;
    public final int VIEW_TYPE_AD_VERSE_NEW = 4;
    public final int VIEW_TYPE_DEVOTION = 5;
    public final int VIEW_TYPE_PRAY = 6;

    public final int TYPE_DEVOTION = 1;
    public final int TYPE_PRAY = 2;
    public final int TYPE_VERSE_NEW = 3;


    private List<CardBean> mItems;
    private LayoutInflater mInflater;

    private VODLProto.VODL.VOD mVod;
    private int mAri, mVerseCount;
    private String mVerseText, mReference;
    private LoadDevotionData mLoadDevotionData;
    private Context mContext;

    private boolean mIsLoadingDataFailure = false;
    private CustomViewPager mRecyclerView;
    private boolean mIsScrollToFirstCard;
    private int mPaddingLeft, mPaddingRight;
    private int mPrayTotalNum, mPrayCatNum;

    public ViewPagerCardAdapter(Context context, CustomViewPager recyclerView) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mItems = new ArrayList<>();
//        mItems.add(new CardBean(VIEW_TYPE_AD_VERSE, null));
//        mItems.add(new CardBean(VIEW_TYPE_LOADING, null));

        //ad tag
        int backgroundImageColor = -1;//Use default color
        mRecyclerView = recyclerView;
        mPaddingLeft = context.getResources().getDimensionPixelSize(R.dimen.lock_screen_vp_padding_left);
        mPaddingRight = context.getResources().getDimensionPixelSize(R.dimen.lock_screen_vp_padding_right);
    }

    public void loadDevotionData(boolean isScrollToFirstCard) {
        mIsScrollToFirstCard = isScrollToFirstCard;
        mLoadDevotionData = new LoadDevotionData();
        mLoadDevotionData.execute();
    }

    public void addLocalPage() {
        mItems.add(new CardBean(VIEW_TYPE_AD_VERSE_NEW, null));
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        if (position >= 0 && position < mItems.size()) {
            return mItems.get(position).mType;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case VIEW_TYPE_LOADING:
                view = mInflater.inflate(R.layout.ls_loading_item_view, parent, false);
                holder = new LoadingHolder(view);
                break;
            case VIEW_TYPE_DEVOTION:
                view = mInflater.inflate(R.layout.ls_card_item_devotion, parent, false);
                holder = new DevotionItemHolder(view);
                break;
            case VIEW_TYPE_PRAY:
                view = mInflater.inflate(R.layout.ls_card_item_pray, parent, false);
                holder = new PrayItemHolder(view);
                break;
            case VIEW_TYPE_AD_VERSE_NEW:
                view = mInflater.inflate(R.layout.ls_home_verse_item_view, parent, false);
                holder = new VerseItemHolder(view);
                break;
            case VIEW_TYPE_AD_PRAY:
                view = mInflater.inflate(R.layout.ls_home_prayer_view, parent, false);
                holder = new PrayAdItemHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder == null) {
            return;
        }
        int type = getItemViewType(position);
        CardBean cardBean = mItems.get(position);
        if (cardBean != null) {
            switch (type) {
                case VIEW_TYPE_LOADING:
                    final LoadingHolder loadingHolder = (LoadingHolder) holder;
                    bindLoadingView(loadingHolder);
                    break;
                case VIEW_TYPE_DEVOTION:
                    final DevotionItemHolder devotionItemHolder = (DevotionItemHolder) holder;
                    devotionItemHolder.bindDevotion(position, cardBean);
                    break;
                case VIEW_TYPE_AD_VERSE_NEW:
                    final VerseItemHolder verseItemHolderNew = (VerseItemHolder) holder;
                    bindVerseViewNew(position, verseItemHolderNew, cardBean, !mIsLoadingDataFailure ? true : false);
                    break;
                case VIEW_TYPE_PRAY:
                    final PrayItemHolder prayItemHolder = (PrayItemHolder) holder;
                    prayItemHolder.bindPrayView(position, cardBean);
                    break;
                case VIEW_TYPE_AD_PRAY:
                    final PrayAdItemHolder prayAdItemHolder = (PrayAdItemHolder) holder;
                    prayAdItemHolder.bindPrayAdView(position, cardBean);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class DevotionItemHolder extends RecyclerView.ViewHolder {
        public View mRootView;
        public ImageView mImage, mNewTagIcon, mCloseBtn;
        public TextView mTvTitle, mTvReadNum, mTvSource, mTvSnippet, mReadBtn, mTvPageNum;

        public DevotionItemHolder(final View itemView) {
            super(itemView);
            mRootView = V.get(itemView, R.id.root_item);
            mNewTagIcon = V.get(itemView, R.id.new_tag_icon);
            mReadBtn = V.get(itemView, R.id.read_btn);
            mImage = V.get(itemView, R.id.image);
            mTvTitle = V.get(itemView, R.id.title);
            mTvReadNum = V.get(itemView, R.id.read_num);
            mTvSource = V.get(itemView, R.id.source);
            mTvSnippet = V.get(itemView, R.id.snippet);
            mCloseBtn = V.get(itemView, R.id.close_btn);
            mTvPageNum = V.get(itemView, R.id.page_num);
        }

        public void bindDevotion(final int pos, final CardBean cardBean) {
            if (cardBean == null) {
                return;
            }

            final LockScreenResponse.DataBean.ListsBean devotion = cardBean.mDevotionBean;
            if (devotion == null) {
                return;
            }

            mNewTagIcon.setVisibility(cardBean.mIsNew ? View.VISIBLE : View.GONE);
            String imageUrl = devotion.getImageUrl();
            if (!TextUtils.isEmpty(imageUrl)) {
                Picasso.with(mRootView.getContext()).load(imageUrl).fit().into(mImage);
            }
            mTvTitle.setText(Html.fromHtml(devotion.getTitle()));
            mTvSource.setText(devotion.getSource());
            mTvSnippet.setText(Html.fromHtml(devotion.getContent()));
            mTvReadNum.setText(String.valueOf(devotion.getView()));
            if (devotion.getIsHot() == 1) {
                mTvReadNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_hot, 0, 0, 0);
            } else {
                mTvReadNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_view, 0, 0, 0);
            }
            mTvPageNum.setText(getPageNumText(pos));

            mReadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goAppInside(devotion, pos);
                }
            });

            mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goAppInside(devotion, pos);
                }
            });

            mCloseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItems.remove(cardBean);
                    notifyDataSetChanged();
                    S.getDb().addDevotionLikeHistory(cardBean.mDevotionBean, false);
                    SelfAnalyticsHelper.sendLockerDevotionDeleteAnalytics(mContext, cardBean.mDevotionBean.getId());
                }
            });

        }

        private void goAppInside(LockScreenResponse.DataBean.ListsBean devotion, int pos) {
            Context context = mReadBtn.getContext();
            Intent intent = DevotionDetailWebActivity.createIntent(mContext, devotion.convertToDevotionListBean());
            mContext.startActivity(intent);

            SelfAnalyticsHelper.sendLoginSourceAnalytics(mContext, AnalyticsConstants.A_LOGIN_SOURCE_LOCKSCREEN, AnalyticsConstants.L_LOCKSCREEN_DEVOTION);
            if (context instanceof Activity) {
                ((Activity) context).finish();
                SelfAnalyticsHelper.sendLockerDevotionClickAnalytics(mContext, String.valueOf(devotion.getSiteId()), String.valueOf(devotion.getId()));
                SelfAnalyticsHelper.sendLockAnalytics(context, AnalyticsConstants.A_DEVOTION_LOCK, devotion.getId() + "_" + pos);
            }
        }

    }

    public class LoadingHolder extends RecyclerView.ViewHolder {
        public ProgressBar mProgressBar;
        public View mNoconnectionLayout;
        public TextView mRetryBtn;

        public LoadingHolder(final View itemView) {
            super(itemView);
            mProgressBar = V.get(itemView, R.id.progressBar);
            mNoconnectionLayout = V.get(itemView, R.id.no_connetion_layout);
            mRetryBtn = V.get(itemView, R.id.retry_btn);
        }

    }


    public class BaseAdItemHolder extends RecyclerView.ViewHolder {

        public BaseAdItemHolder(final View itemView) {
            super(itemView);
        }

    }

    public class VerseItemHolder extends BaseAdItemHolder {
        public TextView mBibleDetail, mBibleChapter, mReadBtn, mPageNum;
        public View mVerseView;

        public VerseItemHolder(final View itemView) {
            super(itemView);
            mVerseView = V.get(itemView, R.id.ls_verse_item);
            mBibleDetail = V.get(itemView, R.id.txt_bible_detail);
            mBibleChapter = V.get(itemView, R.id.txt_bible_Chapter);
            mReadBtn = V.get(itemView, R.id.read_btn);
            mPageNum = V.get(itemView, R.id.page_num);
        }
    }

    public class PrayItemHolder extends RecyclerView.ViewHolder {

        public View mRootView;
        public ImageView mImage, mNewTagIcon, mCloseBtn;

        public TextView mTvTitle, mTvSnippet, mReadBtn, mPrayerTotal, mTvReadNum, mPrayerCate, mPageNum;

        public PrayItemHolder(final View itemView) {
            super(itemView);
            mRootView = V.get(itemView, R.id.root_item);
            mPrayerTotal = V.get(itemView, R.id.prayer_people_total);
            mTvTitle = V.get(itemView, R.id.title);
            mTvSnippet = V.get(itemView, R.id.snippet);
            mTvReadNum = V.get(itemView, R.id.read_num);
            mPrayerCate = V.get(itemView, R.id.source);
            mNewTagIcon = V.get(itemView, R.id.new_tag_icon);
            mReadBtn = V.get(itemView, R.id.read_btn);
            mCloseBtn = V.get(itemView, R.id.close_btn);
            mPageNum = V.get(itemView, R.id.page_num);
        }

        public void bindPrayView(int pos, final CardBean cardBean) {
            if (cardBean == null) {
                return;
            }

            final LockScreenResponse.DataBean.ListsBean prayBean = cardBean.mDevotionBean;
            if (prayBean == null) {
                return;
            }

            mPageNum.setText(getPageNumText(pos));
            mNewTagIcon.setVisibility(cardBean.mIsNew ? View.VISIBLE : View.GONE);
            mTvTitle.setText(Html.fromHtml(prayBean.getTitle()));
            mTvSnippet.setText(Html.fromHtml(prayBean.getContent()));
            mTvReadNum.setText(String.valueOf(prayBean.getView()));
            mPrayerCate.setText(String.valueOf(prayBean.getSource()));
            mPrayerTotal.setText(String.valueOf(mPrayTotalNum));

            mReadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToPrayDetailPage(cardBean, "");
                }
            });

            mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToPrayDetailPage(cardBean, "");
                }
            });

            mCloseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItems.remove(cardBean);
                    notifyDataSetChanged();
                }
            });
        }

        private void goToPrayDetailPage(CardBean cardBean, String catName) {
            LockScreenResponse.DataBean.ListsBean listsBean = cardBean.mDevotionBean;
            ArrayList<PrayerBean> prays = new ArrayList<>();
            prays.add(new PrayerBean(listsBean.getId(), listsBean.getTitle(),
                    listsBean.getContent(), listsBean.getSource(), listsBean.getView()));
            mContext.startActivity(PrayerDetailActivity.createIntentFromList(mContext, prays, 0, catName, mPrayCatNum, 0));
//            SelfAnalyticsHelper.sendPrayerAnalytics(mContext, AnalyticsConstants.A_CLICK_PRAYER, catName);
        }
    }

    public class PrayAdItemHolder extends BaseAdItemHolder {

        public View mRootView;
        public TextView mTvTitle, mTvPrayerCatTitle, mTvPrayContent, mTvPrayNum, mStartPrayBtn, mPageNum;

        public PrayAdItemHolder(final View itemView) {
            super(itemView);
            mRootView = V.get(itemView, R.id.root_item);
            mTvPrayerCatTitle = V.get(itemView, R.id.prayer_category_title);
            mTvTitle = V.get(itemView, R.id.prayer_title);
            mTvPrayContent = V.get(itemView, R.id.prayer_content);
            mTvPrayNum = V.get(itemView, R.id.praying_num_txt);
            mStartPrayBtn = V.get(itemView, R.id.start_pray_btn);
            mPageNum = V.get(itemView, R.id.page_num);
        }

        private void bindPrayAdView(int pos, final CardBean cardBean) {
            if (cardBean == null || cardBean.mDevotionBean == null) {
                return;
            }
            final String catName = getPrayCatName(cardBean.mDevotionBean.getCateId());
            mTvPrayerCatTitle.setText(catName);
            mPageNum.setText(getPageNumText(pos));
            mTvTitle.setText(Html.fromHtml(cardBean.mDevotionBean.getTitle()));
            mTvPrayContent.setText(Html.fromHtml(cardBean.mDevotionBean.getContent()));
            mTvPrayNum.setText(mContext.getString(R.string.is_praying, String.valueOf(mPrayCatNum)));
            mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LockScreenResponse.DataBean.ListsBean listsBean = cardBean.mDevotionBean;
                    ArrayList<PrayerBean> prays = new ArrayList<>();
                    prays.add(new PrayerBean(listsBean.getId(), listsBean.getTitle(),
                            listsBean.getContent(), listsBean.getSource(), listsBean.getView()));
                    mContext.startActivity(PrayerDetailActivity.createIntentFromList(mContext, prays, 0, catName, mPrayCatNum, 0));
                    SelfAnalyticsHelper.sendPrayerAnalytics(mContext, AnalyticsConstants.A_CLICK_PRAYER, catName);
                }
            });
        }

        private String getPrayCatName(int catId) {
            if (catId == 9) {
                return mContext.getString(R.string.prayer_morning_notification_title);
            } else if (catId == 3) {
                return mContext.getString(R.string.prayer_evening_notification_title);
            }
            return mContext.getString(R.string.prayer_morning_notification_title);
        }

    }


    private void bindVerseViewNew(int pos, final VerseItemHolder verseNewItemHolder, final CardBean cardbean, boolean isDataFromWeb) {
        if (cardbean != null && cardbean.mDevotionBean != null) {
            verseNewItemHolder.mVerseView.setVisibility(View.VISIBLE);
            verseNewItemHolder.mReadBtn.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            verseNewItemHolder.mBibleDetail.setText(cardbean.mDevotionBean.getQuote());
            verseNewItemHolder.mBibleChapter.setText(cardbean.mDevotionBean.getQuoteRefer());
            verseNewItemHolder.mPageNum.setText(getPageNumText(pos));
            verseNewItemHolder.mVerseView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = verseNewItemHolder.mVerseView.getContext();
                    Intent intent = NewDailyVerseDetailActivity.createIntent(mContext, cardbean.mDevotionBean.getId(), cardbean.mDevotionBean.getQuote(), cardbean.mDevotionBean.getQuoteRefer(), cardbean.mDevotionBean.getImageUrl());
                    mContext.startActivity(intent);
                    SelfAnalyticsHelper.sendLoginSourceAnalytics(context, AnalyticsConstants.A_LOGIN_SOURCE_LOCKSCREEN, AnalyticsConstants.L_LOCKSCREEN_VERSE);
                    SelfAnalyticsHelper.sendLockAnalytics(context, AnalyticsConstants.A_VERSE_LOCK, null);
                    SelfAnalyticsHelper.sendVerseReadAnalytics(context, AnalyticsConstants.A_VERSE_CLICK_LOCKSCREEN);
                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                    }
                }
            });
        } else {
            verseNewItemHolder.mPageNum.setText(getPageNumText(pos));
            verseNewItemHolder.mVerseView.setVisibility(View.GONE);
        }

    }

    private String getPageNumText(int position) {
        return mContext.getString(R.string.page_num, String.valueOf((position + 1)), String.valueOf(mItems.size()));
    }


    private void bindLoadingView(final LoadingHolder loadingHolder) {
//        loadingHolder.mProgressBar.setVisibility(!mIsLoadingDataFailure ? View.VISIBLE : View.GONE);
//        loadingHolder.mNoconnectionLayout.setVisibility(mIsLoadingDataFailure && mDevotions.isEmpty() ? View.VISIBLE : View.GONE);
//        loadingHolder.mRetryBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                loadingHolder.mProgressBar.setVisibility(View.VISIBLE);
//                loadingHolder.mNoconnectionLayout.setVisibility(View.GONE);
//                loadDevotionData();
//            }
//        });
    }


    private class LoadDevotionData extends OmAsyncTask<Object, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mIsLoadingDataFailure = false;
        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            try {
                if (mDevotions != null && !mDevotions.isEmpty()) {
                    mDevotions.clear();
                }
                DevotionService lockScreenService = BaseRetrofit.getDevotionService();
                Call<LockScreenResponse> call = lockScreenService.getLockScreenMixList(DateTimeUtil.getHour(System.currentTimeMillis()), "2");
                Response<LockScreenResponse> response = call.execute();
                if (response != null) {
                    LockScreenResponse homeResponse = response.body();
                    if (homeResponse != null && homeResponse.getData() != null && homeResponse.getData().getLists() != null) {
                        List<LockScreenResponse.DataBean.ListsBean> list = homeResponse.getData().getLists();
                        if (list != null && list.size() > 0) {
                            //get pray total num & category num
                            calculatePrayNum(list);

                            //filter dislike devotion
                            filterDislikeDevotion(list);
                        }

                        if (mDevotions != null && mDevotions.size() > 0) {
                            return true;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        private void filterDislikeDevotion(List<LockScreenResponse.DataBean.ListsBean> list) {
            List<Integer> dislikeIds = S.getDb().listAllDisLikeDevotionHistory();
            for (LockScreenResponse.DataBean.ListsBean listsBean : list) {
                if (dislikeIds.contains(listsBean.getId())) {
                    continue;
                }
                mDevotions.add(listsBean);
            }
        }

        private void calculatePrayNum(List<LockScreenResponse.DataBean.ListsBean> list) {
            if (list.get(0).getType() != 2) {

            }
            int catId = list.get(0).getCateId();
            try {
                Call<PrayerPeopleResponse> peopleNumCall = BaseRetrofit.getPrayerService().getPrayerPeopleNum();
                Response<PrayerPeopleResponse> peopleNumResponse = peopleNumCall.execute();
                if (peopleNumResponse != null) {
                    PrayerPeopleResponse prayerPeopleResponse = peopleNumResponse.body();
                    if (prayerPeopleResponse != null) {
                        PrayerPeopleResponse.DataBean dataBean = prayerPeopleResponse.getData();
                        if (dataBean != null) {
                            mPrayTotalNum = Utility.getCurrentTimePrayerPeople(dataBean.getTotal());
                            loadPrayerCategoryNum(dataBean.getCategory(), mPrayTotalNum, catId);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void loadPrayerCategoryNum(List<PrayerPeopleResponse.DataBean.CategoryRadioBean> categoryRadioBeanList, int totalNum, int catId) {
            if (categoryRadioBeanList == null || categoryRadioBeanList.isEmpty() || totalNum == 0) {
                return;
            }
            for (PrayerPeopleResponse.DataBean.CategoryRadioBean categoryRadioBean : categoryRadioBeanList) {
                if (categoryRadioBean.getId() == catId) {
                    mPrayCatNum = (int) (categoryRadioBean.getRatio() * totalNum);
                    break;
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            try {
                if (result) {
                    boolean isShowNew = false;
                    for (int i = 0; i < mDevotions.size(); i++) {
                        LockScreenResponse.DataBean.ListsBean devotionBean = mDevotions.get(i);
                        if (devotionBean == null) {
                            continue;
                        }
                        if (i == 0) {
                            addHomeItem(devotionBean);
                        } else {
                            if (!isShowNew) {
                                mItems.add(new CardBean(devotionBean.getContentType() == TYPE_PRAY ? VIEW_TYPE_PRAY : VIEW_TYPE_DEVOTION, devotionBean, true));
                                isShowNew = true;
                            } else {
                                mItems.add(new CardBean(devotionBean.getContentType() == TYPE_PRAY ? VIEW_TYPE_PRAY : VIEW_TYPE_DEVOTION, devotionBean));
                            }
                        }

                    }
                    notifyDataSetChanged();
                    mRecyclerView.setPadding(mPaddingLeft, 0, mPaddingRight, 0);
                    if (mIsScrollToFirstCard) {
                        mRecyclerView.scrollToPosition(1);
                    }
                } else {
                    addLocalPage();
                }
            } catch (Exception e) {
                addLocalPage();
            }
            mIsLoadingDataFailure = !result;
        }

        private void addHomeItem(LockScreenResponse.DataBean.ListsBean devotionBean) {
            int type = devotionBean.getContentType();
            switch (type) {
                case TYPE_PRAY:
                    mItems.add(new CardBean(VIEW_TYPE_AD_PRAY, devotionBean, false));
                    break;
                case TYPE_VERSE_NEW:
                    mItems.add(new CardBean(VIEW_TYPE_AD_VERSE_NEW, devotionBean, false));
                    break;
            }
        }
    }


    public void cancleTask() {
        if (mLoadDevotionData != null) {
            mLoadDevotionData.cancel(true);
        }
    }

}
