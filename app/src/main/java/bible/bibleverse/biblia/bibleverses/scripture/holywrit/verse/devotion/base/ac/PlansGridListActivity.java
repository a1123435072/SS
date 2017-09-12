package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.ReadingPlan;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.protobuf.BPCDProto;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.protobuf.BPProto;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.service.BPCDService;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.PlanUtil;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.view.LoadingView;
import yuku.afw.V;

/**
 * Created by Yzq on 28/10/16.
 */
public class PlansGridListActivity extends BaseActivity {
    private RecyclerView mGridView;
    private LoadingView mLoadingView;
    private ProgressBar mProgressBar;
    private TextView mProgressText;

    private LinearLayout mEmptyLayout;
    private TextView mNoContentTitle, mRetryButton;
    private ImageView mNoContentImage;

    private int mType = 0;
    private long mCategoryId = 0;
    private String mCategoryName;

    private final int IMG_WIDTH = 156;
    private final int IMG_HEIGHT = 88;
    private int mIconWidth = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans_grid_list);
        initView();
        handleIntent();
        initToolbar();
    }

    private void initView() {
        V.get(this, R.id.loading_layout).setVisibility(View.GONE);
        mProgressBar = V.get(this, R.id.pg);
        mProgressText = V.get(this, R.id.pg_txt);
        mLoadingView = V.get(this, R.id.loading_view);
        mLoadingView.setVisibility(View.VISIBLE);

        mEmptyLayout = (LinearLayout) findViewById(R.id.empty_layout);
        mEmptyLayout.setVisibility(View.GONE);
        mNoContentImage = V.get(this, R.id.empty_icon);
        mNoContentImage.setImageResource(R.drawable.icon_no_connection);
        mNoContentTitle = V.get(this, R.id.tEmpty);
        mNoContentTitle.setText(getString(R.string.no_connection));
        mRetryButton = V.get(this, R.id.bRetry);
        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mType == Constants.TYPE_CATEGORY_PLANS) {
                    new LoadPlansTask().execute();
                }
            }
        });
        mEmptyLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        mNoContentTitle.setTextColor(ContextCompat.getColor(this, R.color.black));
        mGridView = V.get(this, R.id.gridView);
        mGridView.setVisibility(View.GONE);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mGridView.setLayoutManager(gridLayoutManager);

        mIconWidth = (getResources().getDisplayMetrics().widthPixels - 3 * getResources().getDimensionPixelSize(R.dimen.margin_16)) / 2;
    }

    private void initToolbar() {
        Toolbar toolbar = V.get(this, R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (mType == Constants.TYPE_COMPLETE_PLANS) {
                actionBar.setTitle(R.string.completed_plans);
            } else if (mType == Constants.TYPE_CATEGORY_PLANS) {
                actionBar.setTitle(mCategoryName);
            }
            toolbar.setNavigationIcon(R.drawable.ic_back_black);
        }
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            mType = intent.getIntExtra(Constants.KEY_PLAN_GRID_LIST_TYPE, 0);
            mCategoryId = intent.getLongExtra(Constants.KEY_PLAN_CATEGORY_ID, 0);
            mCategoryName = intent.getStringExtra(Constants.KEY_PLAN_CATEGORY_NAME);
            new LoadPlansTask().execute();
        }
    }

    private class LoadPlansTask extends OmAsyncTask<Void, Void, List<ReadingPlan.ReadingPlanInfo>> {

        @Override
        protected void onPreExecute() {
            mLoadingView.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<ReadingPlan.ReadingPlanInfo> doInBackground(Void... voids) {
            return getPlans();
        }

        @Override
        protected void onPostExecute(List<ReadingPlan.ReadingPlanInfo> planList) {
            mLoadingView.setVisibility(View.GONE);
            if (planList != null && !planList.isEmpty()) {
                PlansGridAdapter plansGridAdapter = new PlansGridAdapter(planList);
                mGridView.setAdapter(plansGridAdapter);
                plansGridAdapter.notifyDataSetChanged();
                mEmptyLayout.setVisibility(View.GONE);
                mGridView.setVisibility(View.VISIBLE);
            } else {
                mEmptyLayout.setVisibility(View.VISIBLE);
                mGridView.setVisibility(View.GONE);
            }
        }

        private List<ReadingPlan.ReadingPlanInfo> getPlans() {
            List<ReadingPlan.ReadingPlanInfo> planList = new ArrayList<>();
            if (mType == Constants.TYPE_CATEGORY_PLANS) {
                BPCDProto.BPCD bpcd = new BPCDService(PlansGridListActivity.this).getData(String.valueOf(mCategoryId));
                if (bpcd != null) {
                    List<BPProto.BP> list = bpcd.getListList();
                    if (list != null && !list.isEmpty()) {
                        for (int i = 0; i < list.size(); i++) {
                            planList.add(ReadingPlan.createCategoryPlanItemFromBP(list.get(i)));
                        }
                        return planList;
                    }
                }
            } else if (mType == Constants.TYPE_COMPLETE_PLANS) {
                return S.getDb().listAllCompletedPlan();
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

    private class PlansGridAdapter extends RecyclerView.Adapter<ViewHolderPlan> {
        List<ReadingPlan.ReadingPlanInfo> mPlans;
        LayoutInflater mInflater;

        public PlansGridAdapter(List<ReadingPlan.ReadingPlanInfo> plans) {
            this.mPlans = plans;
            mInflater = LayoutInflater.from(PlansGridListActivity.this);
        }

        @Override
        public int getItemCount() {
            return this.mPlans.size();
        }

        public ReadingPlan.ReadingPlanInfo getItem(int position) {
            return this.mPlans.get(position);
        }

        @Override
        public ViewHolderPlan onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.item_plan_layout, parent, false);
            ViewHolderPlan holder = new ViewHolderPlan(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolderPlan holder, int position) {
            if (holder == null) {
                return;
            }

            final ReadingPlan.ReadingPlanInfo planInfo = getItem(position);
            if (planInfo != null) {
                holder.name.setText(planInfo.mTitle);
                holder.daysTv.setText(getString(R.string.days, String.valueOf(planInfo.mPlannedDayCount)));
                Picasso.with(PlansGridListActivity.this).load(planInfo.mSmallIconUrl).into(holder.icon);
                holder.planItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mType == Constants.TYPE_CATEGORY_PLANS) {
                            PlanUtil.gotoPlanDetailPage(PlansGridListActivity.this, planInfo.mServerQueryId, planInfo.mPlannedDayCount, planInfo.mTitle, planInfo.mBigImageUrl, planInfo.mSmallIconUrl);
                        } else {
                            PlanUtil.gotoPlanProgressDetailPage(PlansGridListActivity.this, planInfo.mDbPlanId);
                        }
                    }
                });
                setImageParams(holder.icon);
            }
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
    protected void onDestroy() {
        super.onDestroy();
        if (mLoadingView != null) {
            mLoadingView.stopLoading();
        }
    }
}
