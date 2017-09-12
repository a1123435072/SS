package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fw.basemodules.utils.OmAsyncTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.DevotionBean;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.DevotionListResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.DevotionDetailWebActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventRefreshFavoriteList;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr.base.BaseFragment;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.storage.Db;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.DateTimeUtil;
import de.greenrobot.event.EventBus;
import yuku.afw.V;

public class MyFavoriteDevotionsFragment extends BaseFragment {
    public static final String TAG = MyFavoriteDevotionsFragment.class.getSimpleName();

    private LayoutInflater mInflater;
    private List<DevotionBean> mDevotions;
    private LoadDevotionTask mLoadDataTask;
    private String mCurrentDate;

    private RecyclerView mRecycleView;
    private DevotionAdapter mDevotionAdapter;

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
        mDevotions = new ArrayList<>();
        mCurrentDate = DateTimeUtil.getDateStr4ApiRequest(System.currentTimeMillis());
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

        mEmptyLayout.setVisibility(View.GONE);
        mNoItemButton.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.VISIBLE);

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
        mDevotionAdapter = new DevotionAdapter();
        mRecycleView.setAdapter(mDevotionAdapter);
        mLoadDataTask = new LoadDevotionTask();
        mLoadDataTask.execute();
    }

    private class LoadDevotionTask extends OmAsyncTask<String, Void, List<DevotionBean>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<DevotionBean> doInBackground(String... voids) {
            return S.getDb().getDevotionFavoriteList();
        }

        @Override
        protected void onPostExecute(List<DevotionBean> devotionBeans) {
            int size = devotionBeans != null ? devotionBeans.size() : 0;
            if (size > 0) {
                mDevotions.addAll(devotionBeans);
                mDevotionAdapter.notifyDataSetChanged();
                mLoadingLayout.setVisibility(View.GONE);
                mEmptyLayout.setVisibility(View.GONE);
            } else if (mDevotions.size() == 0) {
                mLoadingLayout.setVisibility(View.GONE);
                mEmptyLayout.setVisibility(View.VISIBLE);
            }
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
                Picasso.with(getActivity()).load(devotion.getImageUrl()).into(holderDevotion.image);
            }
            holderDevotion.title.setText(devotion.getTitle());
            holderDevotion.summary.setText(Html.fromHtml(devotion.getContent()));
            holderDevotion.source.setText(devotion.getSource());
            holderDevotion.readNum.setText(String.valueOf(devotion.getView()));
            holderDevotion.newFlag.setVisibility(View.GONE);
            holderDevotion.title.setTextColor(getResources().getColor(R.color.black));
            holderDevotion.summary.setTextColor(getResources().getColor(R.color.black));
            holderDevotion.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(DevotionDetailWebActivity.createIntent(getActivity(), devotion));
//                    SelfAnalyticsHelper.sendCatListDevotionClickAnalytics(getActivity(), String.valueOf(devotion.getSource()), String.valueOf(devotion.getId()));
//                    SelfAnalyticsHelper.sendDevotionSiteAnalytics(getActivity(), devotion.getSource());
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

    public void onEventMainThread(EventRefreshFavoriteList event) {
        if (event != null && event.type == Db.Favorite.TYPE_DEVOTION) {
            mDevotions.clear();
            mLoadDataTask = new LoadDevotionTask();
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
