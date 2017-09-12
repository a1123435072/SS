package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fw.basemodules.utils.OmAsyncTask;

import java.util.ArrayList;
import java.util.List;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PrayerBean;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PrayerResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.PrayerDetailActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventRefreshFavoriteList;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr.base.BaseFragment;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.storage.Db;
import de.greenrobot.event.EventBus;
import yuku.afw.V;

public class MyFavoritePraysFragment extends BaseFragment {
    public static final String TAG = MyFavoritePraysFragment.class.getSimpleName();

    private LayoutInflater mInflater;
    private ArrayList<PrayerBean> mPrayers;
    private LoadDataTask mLoadDataTask;

    private RecyclerView mRecycleView;
    private PrayerAdapter mPrayerAdapter;

    private ProgressBar mProgressBar;
    private TextView mProgressText;
    private View mLoadingLayout;

    private LinearLayout mEmptyLayout;
    private TextView mNoItemTitle, mNoItemButton, mNoItemMsg;
    private ImageView mNoItemImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVar();
        EventBus.getDefault().register(this);
    }

    private void initVar() {
        mInflater = getActivity().getLayoutInflater();
        mPrayers = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_devotion, container, false);

        mRecycleView = V.get(view, R.id.list_view);
        mLoadingLayout = V.get(view, R.id.loading_layout);
        mProgressBar = V.get(view, R.id.pg);
        mProgressText = V.get(view, R.id.pg_txt);

        mEmptyLayout = V.get(view, android.R.id.empty);
        mNoItemImage = V.get(view, R.id.empty_icon);
        mNoItemTitle = V.get(view, R.id.tEmpty);
        mNoItemMsg = V.get(view, R.id.msgEmpty);
        mNoItemButton = V.get(view, R.id.bClearFilter);

        mNoItemButton.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.VISIBLE);
        mEmptyLayout.setVisibility(View.GONE);

        mNoItemTitle.setText(getString(R.string.no_favorites));
        mNoItemMsg.setText(R.string.no_favorites_summary);
        mEmptyLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.transparent));
        mNoItemImage.setImageResource(R.drawable.ic_no_favorites);
        mNoItemTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPrayerAdapter = new PrayerAdapter();
        mRecycleView.setAdapter(mPrayerAdapter);
        mLoadDataTask = new LoadDataTask();
        mLoadDataTask.execute();
    }

    private class LoadDataTask extends OmAsyncTask<String, Void, List<PrayerBean>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<PrayerBean> doInBackground(String... voids) {
            return S.getDb().getPrayFavoriteList();
        }

        @Override
        protected void onPostExecute(List<PrayerBean> prayerBeans) {
            int size = prayerBeans != null ? prayerBeans.size() : 0;
            if (size > 0) {
                mPrayers.addAll(prayerBeans);
                mPrayerAdapter.notifyDataSetChanged();
                mLoadingLayout.setVisibility(View.GONE);
                mEmptyLayout.setVisibility(View.GONE);
            } else if (mPrayers.size() == 0) {
                mLoadingLayout.setVisibility(View.GONE);
                mEmptyLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private class PrayerAdapter extends RecyclerView.Adapter<ViewHolderPrayer> {

        @Override
        public ViewHolderPrayer onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.item_pray, parent, false);
            ViewHolderPrayer holder = new ViewHolderPrayer(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolderPrayer holder, final int position) {
            final PrayerBean pray = mPrayers.get(position);
            holder.title.setText(pray.getTitle());
            holder.readNum.setText(String.valueOf(pray.getView()));
            holder.source.setText(getString(R.string.pray_source_format, pray.getSource()));
            holder.snippet.setText(Html.fromHtml(pray.getContent()));

            holder.contentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(PrayerDetailActivity.createIntentFromList(getActivity(), mPrayers, position, pray.getSource(), 0, 0));
//                    SelfAnalyticsHelper.sendPrayerAnalytics(getActivity(), AnalyticsConstants.A_CLICK_PRAYER, pray.getSource());
//                    UserBehaviorAnalytics.trackUserBehavior(getActivity(), AnalyticsConstants.P_PRAYER_TOPIC, AnalyticsConstants.B_CLICK_PRAYER);
                }
            });

            holder.startView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(PrayerDetailActivity.createIntentFromList(getActivity(), mPrayers, position, pray.getSource(), 0, 0));
//                    SelfAnalyticsHelper.sendPrayerAnalytics(getActivity(), AnalyticsConstants.A_CLICK_PRAYER, pray.getSource());
//                    UserBehaviorAnalytics.trackUserBehavior(getActivity(), AnalyticsConstants.P_PRAYER_TOPIC, AnalyticsConstants.B_CLICK_PRAYER);
                }
            });
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return mPrayers.size();
        }

    }

    class ViewHolderPrayer extends RecyclerView.ViewHolder {

        public TextView title, readNum, source, snippet;
        public View startView, contentView;

        public ViewHolderPrayer(View itemView) {
            super(itemView);
            title = V.get(itemView, R.id.title);
            readNum = V.get(itemView, R.id.read_num);
            source = V.get(itemView, R.id.source);
            snippet = V.get(itemView, R.id.snippet);
            startView = V.get(itemView, R.id.more_layout);
            contentView = V.get(itemView, R.id.content_layout);
        }
    }

    public void onEventMainThread(EventRefreshFavoriteList event) {
        if (event != null && event.type == Db.Favorite.TYPE_PRAYER) {
            mPrayers.clear();
            mLoadDataTask = new LoadDataTask();
            mLoadDataTask.execute();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoadDataTask != null) {
            mLoadDataTask.cancel(true);
            mLoadDataTask = null;
        }
        EventBus.getDefault().unregister(this);
    }

}
