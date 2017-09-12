package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.fw.basemodules.utils.OmAsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.BaseRetrofit;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.DevotionService;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.DevotionBean;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PopularResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PrayerBean;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter.viewholder.ViewHolderDevotionBigImage;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter.viewholder.ViewHolderDevotionNoImage;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter.viewholder.ViewHolderDevotionSmallImage;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter.viewholder.ViewHolderPopularHeader;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter.viewholder.ViewHolderSeparator;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by yzq on 2017/3/17.
 */

public class PopularListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_SEPARATOR = 0;
    public static final int ITEM_DEVOTION_HEADER = 100;
    private static final int ITEM_DEVOTION_START = 101;
    private static final int ITEM_DEVOTION_END = 199;
    private static final int ITEM_PRAYER_HEADER = 200;
    private static final int ITEM_PRAYER_START = 201;
    private static final int ITEM_PRAYER_END = 299;

    private static final int ITEM_TYPE_SEPARATOR = 0;
    private static final int ITEM_TYPE_DEVOTION_NO = 1;
    private static final int ITEM_TYPE_DEVOTION_SMALL = 2;
    private static final int ITEM_TYPE_DEVOTION_BIG = 3;
    private static final int ITEM_TYPE_PRAYER = 4;
    private static final int ITEM_TYPE_HEADER = 5;

    private BaseActivity mContext;
    private LayoutInflater mInflater;
    private OmAsyncTask mLoadDataTask;
    private List<Integer> mItems;

    private List<DevotionBean> mDevotions;
    private List<PrayerBean> mPrayers;
    private LoadPopularDataCallback mLoadPopularDataCallback;


    public PopularListAdapter(BaseActivity activity) {
        this.mContext = activity;
        mInflater = mContext.getLayoutInflater();
        mItems = new ArrayList<>();
    }

    public void setLoadPopularDataCallback(LoadPopularDataCallback loadPopularDataCallback) {
        this.mLoadPopularDataCallback = loadPopularDataCallback;
    }

    public void loadData() {
        mItems.clear();
        if (mLoadDataTask != null) {
            mLoadDataTask.cancel(true);
            mLoadDataTask = null;
        }
        mLoadDataTask = new LoadDataTask();
        mLoadDataTask.execute();
    }

    public void destroy() {
        if (mLoadDataTask != null) {
            mLoadDataTask.cancel(true);
            mLoadDataTask = null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int item = mItems.get(position);
        if (item >= ITEM_DEVOTION_START && item <= ITEM_DEVOTION_END) {
            int idx = item - ITEM_DEVOTION_START;
            final DevotionBean devotion = (idx < mDevotions.size()) ? mDevotions.get(idx) : null;
            if (devotion != null) {
                if (devotion.getType() == 1) {
                    return ITEM_TYPE_DEVOTION_SMALL;
                } else if (devotion.getType() == 2) {
                    return ITEM_TYPE_DEVOTION_NO;
                } else if (devotion.getType() == 3) {
                    return ITEM_TYPE_DEVOTION_BIG;
                } else {
                    return ITEM_TYPE_DEVOTION_NO;
                }
            }
        } else if (item >= ITEM_PRAYER_START && item <= ITEM_PRAYER_END) {
            return ITEM_TYPE_PRAYER;
        } else if (item == ITEM_DEVOTION_HEADER || item == ITEM_PRAYER_HEADER) {
            return ITEM_TYPE_HEADER;
        }
        return ITEM_TYPE_SEPARATOR;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        switch (viewType) {
            case ITEM_TYPE_DEVOTION_NO:
                holder = ViewHolderDevotionNoImage.createViewHolder(mInflater, parent);
                break;
            case ITEM_TYPE_DEVOTION_SMALL:
                holder = ViewHolderDevotionSmallImage.createViewHolder(mInflater, parent);
                break;
            case ITEM_TYPE_DEVOTION_BIG:
                holder = ViewHolderDevotionBigImage.createViewHolder(mInflater, parent);
                break;
            case ITEM_TYPE_PRAYER:
                holder = ViewHolderDevotionNoImage.createViewHolder(mInflater, parent);
                break;
            case ITEM_TYPE_HEADER:
                holder = ViewHolderPopularHeader.createViewHolder(mInflater, parent);
                break;
            case ITEM_SEPARATOR:
            default:
                holder = ViewHolderSeparator.createViewHolder(mInflater, parent);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int item = mItems.get(position);
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ITEM_TYPE_DEVOTION_NO: {
                int idx = mItems.get(position) - ITEM_DEVOTION_START;
                final DevotionBean devotion = (idx < mDevotions.size()) ? mDevotions.get(idx) : null;
                if (devotion != null) {
                    ((ViewHolderDevotionNoImage) holder).bindPopularDevotion(mContext, devotion, ((idx + 1) == mDevotions.size()));
                }
                break;
            }
            case ITEM_TYPE_DEVOTION_SMALL: {
                int idx = mItems.get(position) - ITEM_DEVOTION_START;
                final DevotionBean devotion = (idx < mDevotions.size()) ? mDevotions.get(idx) : null;
                if (devotion != null) {
                    ((ViewHolderDevotionSmallImage) holder).bindPopularDevotion(mContext, devotion, ((idx + 1) == mDevotions.size()));
                }
                break;
            }
            case ITEM_TYPE_DEVOTION_BIG: {
                int idx = mItems.get(position) - ITEM_DEVOTION_START;
                final DevotionBean devotion = (idx < mDevotions.size()) ? mDevotions.get(idx) : null;
                if (devotion != null) {
                    ((ViewHolderDevotionBigImage) holder).bindPopularDevotion(mContext, devotion, ((idx + 1) == mDevotions.size()));
                }
                break;
            }
            case ITEM_TYPE_PRAYER: {
                int idx = mItems.get(position) - ITEM_PRAYER_START;
                final PrayerBean prayer = (idx < mPrayers.size()) ? mPrayers.get(idx) : null;
                if (prayer != null) {
                    ((ViewHolderDevotionNoImage) holder).bindPopularPrayer(mContext, prayer, ((idx + 1) == mPrayers.size()));
                }
                break;
            }
            case ITEM_TYPE_HEADER:
                ((ViewHolderPopularHeader) holder).bindView(item);
                break;
            case ITEM_SEPARATOR:
            default:
                ((ViewHolderSeparator) holder).tvTitle.setAllCaps(false);
                ((ViewHolderSeparator) holder).tvTitle.setText(R.string.might_also_like);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    public interface LoadPopularDataCallback {
        public void onLoadTaskStart();

        public void onLoadTaskFinish(int popularDataCount);
    }

    private class LoadDataTask extends OmAsyncTask<Object, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mLoadPopularDataCallback != null) {
                mLoadPopularDataCallback.onLoadTaskStart();
            }
        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            try {
                DevotionService devotionService = BaseRetrofit.getDevotionService();
                Call<PopularResponse> call = devotionService.getPopularContent();
                Response<PopularResponse> response = call.execute();
                PopularResponse popularResponse = response != null ? response.body() : null;
                if (popularResponse != null && popularResponse.getData() != null) {
                    mDevotions = popularResponse.getData().getDevotion();
                    mPrayers = popularResponse.getData().getPrayer();
                }

                if (mDevotions != null && mDevotions.size() > 0) {
                    return Boolean.TRUE;
                }

            } catch (IOException e) {
            }

            return Boolean.FALSE;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            int count = 0;
            if (aBoolean == Boolean.TRUE) {
                mItems.add(ITEM_SEPARATOR);
                if (mDevotions != null && mDevotions.size() > 0) {
                    mItems.add(ITEM_DEVOTION_HEADER);
                    for (int i = 0; i < mDevotions.size(); ++i) {
                        mItems.add(ITEM_DEVOTION_START + i);
                        count++;
                    }
                }
                if (mPrayers != null) {
                    mPrayers.clear();
                }
            }
            notifyDataSetChanged();
            if (mLoadPopularDataCallback != null) {
                mLoadPopularDataCallback.onLoadTaskFinish(count);
            }
        }
    }
}
