package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventPlanTabSelected;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr.base.BaseFragment;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.ReadingPlan;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.protobuf.BPCDProto;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.protobuf.BPProto;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.service.BPCDService;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.PlanUtil;
import de.greenrobot.event.EventBus;
import yuku.afw.V;

/**
 * Created by Mr_ZY on 16/11/7.
 */

public class PlanDiscoverFragment extends BaseFragment {
    private static final String AD_CATEGORY = "ad";
    private RecyclerView mGridView;
    private View mLoadingLayout;
    private ProgressBar mProgressBar;
    private TextView mProgressText;
    private LinearLayout mEmptyLayout;
    private TextView mNoContentTitle, mRetryButton;
    private ImageView mNoContentImage;
    PlansGridAdapter mPlansGridAdapter;


    private long mCategoryId = Constants.PLAN_DISCOVER_CATE_ID;
    private final int IMG_WIDTH = 156;
    private final int IMG_HEIGHT = 88;
    private int mIconWidth = 0;

    List<ReadingPlan.ReadingPlanInfo> mFinalPlans;
    LayoutInflater mInflater;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = LayoutInflater.from(getActivity());

        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_plans_grid_list, container, false);

        initView(view);
        return view;
    }

    private void initView(View view) {
        mLoadingLayout = V.get(view, R.id.loading_layout);
        mProgressBar = V.get(view, R.id.pg);
        mProgressText = V.get(view, R.id.pg_txt);
        mLoadingLayout.setVisibility(View.VISIBLE);

        mGridView = V.get(view, R.id.gridView);
        mEmptyLayout = V.get(view, R.id.empty_layout);
        mNoContentImage = V.get(view, R.id.empty_icon);
        mNoContentTitle = V.get(view, R.id.tEmpty);
        V.get(view, R.id.toolbar_layout).setVisibility(View.GONE);
        V.get(view, R.id.toolbar_shadow).setVisibility(View.GONE);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        mGridView.setLayoutManager(gridLayoutManager);

        mNoContentImage.setImageResource(R.drawable.icon_no_connection);
        mNoContentTitle.setText(getString(R.string.no_connection));
        mEmptyLayout.setVisibility(View.GONE);
        mRetryButton = V.get(view, R.id.bRetry);
        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoadPlansTask().execute();
            }
        });
        mEmptyLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        mNoContentTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
        mGridView.setVisibility(View.GONE);
        mIconWidth = (getResources().getDisplayMetrics().widthPixels - 3 * getResources().getDimensionPixelSize(R.dimen.margin_16)) / 2;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mFinalPlans != null && !mFinalPlans.isEmpty()) {
            // only keep ad Item
            ReadingPlan.ReadingPlanInfo firstItem = mFinalPlans.get(0);
            mFinalPlans.clear();
            if (firstItem != null && firstItem.mCategoryId == AD_CATEGORY) {
                mFinalPlans.add(0, firstItem);
            }
        }
        mEmptyLayout.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.VISIBLE);
        new LoadPlansTask().execute();
    }

    private class LoadPlansTask extends OmAsyncTask<Void, Void, List<ReadingPlan.ReadingPlanInfo>> {

        @Override
        protected void onPreExecute() {
            mLoadingLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<ReadingPlan.ReadingPlanInfo> doInBackground(Void... voids) {
            return getPlans();
        }

        @Override
        protected void onPostExecute(List<ReadingPlan.ReadingPlanInfo> planList) {
            mLoadingLayout.setVisibility(View.GONE);
            if (planList != null && !planList.isEmpty()) {
                if (mFinalPlans == null) {// already has ad item
                    mFinalPlans = planList;
                } else {
                    mFinalPlans.addAll(planList);
                }
                mPlansGridAdapter = new PlansGridAdapter();
                mGridView.setAdapter(mPlansGridAdapter);
                mPlansGridAdapter.notifyDataSetChanged();
                mEmptyLayout.setVisibility(View.GONE);
                mGridView.setVisibility(View.VISIBLE);
            } else {
                mEmptyLayout.setVisibility(View.VISIBLE);
                mGridView.setVisibility(View.GONE);
            }
        }

        private List<ReadingPlan.ReadingPlanInfo> getPlans() {
            List<ReadingPlan.ReadingPlanInfo> planList = new ArrayList<>();
            BPCDProto.BPCD bpcd = new BPCDService(getActivity()).getData(String.valueOf(mCategoryId));
            if (bpcd != null) {
                List<BPProto.BP> list = bpcd.getListList();
                if (list != null && !list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        planList.add(ReadingPlan.createCategoryPlanItemFromBP(list.get(i)));
                    }
                    return planList;
                }
            }
            return planList;
        }
    }

    class ViewHolderPlan extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;
        TextView daysTv;
        LinearLayout planItem;

        public ViewHolderPlan(View itemView) {
            super(itemView);

            planItem = V.get(itemView, R.id.item_plan);
            name = V.get(itemView, R.id.plan_title);
            icon = V.get(itemView, R.id.plan_icon);
            daysTv = V.get(itemView, R.id.plan_days);
        }
    }

    private class PlansGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public PlansGridAdapter() {
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            RecyclerView.ViewHolder holder = null;

            switch (viewType) {
                case 1:
                    view = mInflater.inflate(R.layout.item_plan_layout, parent, false);
                    holder = new ViewHolderPlan(view);
                    break;
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder == null) {
                return;
            }

            int type = getItemViewType(position);
            switch (type) {
                case 1:
                    final ViewHolderPlan holderPlan = (ViewHolderPlan) holder;
                    ReadingPlan.ReadingPlanInfo planInfo = getItem(position);
                    bindPlanView(holderPlan, planInfo);
                    break;
            }
        }


        private void bindPlanView(ViewHolderPlan holderPlan, final ReadingPlan.ReadingPlanInfo planInfo) {
            holderPlan.name.setText(planInfo.mTitle);
            holderPlan.daysTv.setText(getString(R.string.days, String.valueOf(planInfo.mPlannedDayCount)));
            Picasso.with(getContext()).load(planInfo.mSmallIconUrl).into(holderPlan.icon);
            holderPlan.planItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlanUtil.gotoPlanDetailPage(getActivity(), planInfo.mServerQueryId, planInfo.mPlannedDayCount, planInfo.mTitle, planInfo.mBigImageUrl, planInfo.mSmallIconUrl);
                }
            });
            setImageParams(holderPlan.icon);
        }

        public ReadingPlan.ReadingPlanInfo getItem(int position) {
            return mFinalPlans == null ? null : mFinalPlans.get(position);
        }

        @Override
        public int getItemViewType(int position) {
            ReadingPlan.ReadingPlanInfo item = getItem(position);
            if (item != null && item.mCategoryId.equals(AD_CATEGORY)) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getItemCount() {
            return mFinalPlans == null ? 0 : mFinalPlans.size();
        }
    }

    private void setImageParams(ImageView iv) {
        ViewGroup.LayoutParams params = iv.getLayoutParams();
        if (params != null) {
            params.width = mIconWidth;
            params.height = mIconWidth * IMG_HEIGHT / IMG_WIDTH;
            iv.setLayoutParams(params);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onEventMainThread(EventPlanTabSelected event) {
        if (event != null && event.mPosition == 1) {
            if (mPlansGridAdapter != null) {
                mPlansGridAdapter.notifyDataSetChanged();
            }
        }
    }
}
