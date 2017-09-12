package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fw.basemodules.utils.OmAsyncTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventUpdateMyPlanLists;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr.base.BaseGotoFragment;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.ReadingPlan;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.PlanUtil;
import de.greenrobot.event.EventBus;
import yuku.afw.V;

public class MyPlansFragment extends BaseGotoFragment {
    public static final String TAG = MyPlansFragment.class.getSimpleName();

    private ListView mMyPlansLv;
    private ProgressBar mProgressBar;
    private View mLoadingLayout, mEmptyLayout;
    private TextView mProgressText, mNoContentTitle, mRetryButton;
    private ImageView mNoContentImage;

    private LoadMyPlansTask mLoadMyPlansTask;
    private PlansAdapter mPlansAdapter;

    private View mHeaderView, mAdListTopDivider;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View res = inflater.inflate(R.layout.fragment_my_plans, container, false);
        mMyPlansLv = V.get(res, R.id.my_plans_lv);
        mLoadingLayout = V.get(res, R.id.loading_layout);
        mProgressBar = V.get(res, R.id.pg);
        mProgressText = V.get(res, R.id.pg_txt);
        mLoadingLayout.setVisibility(View.VISIBLE);

        mEmptyLayout = V.get(res, android.R.id.empty);
        mEmptyLayout.setVisibility(View.GONE);
        mNoContentImage = V.get(res, R.id.empty_icon);
        mNoContentImage.setImageResource(R.drawable.ic_no_plan_reminder);
        mNoContentTitle = V.get(res, R.id.tEmpty);
        mNoContentTitle.setText(getString(R.string.my_plans_no_content_reminder));
        V.get(res, R.id.bClearFilter).setVisibility(View.GONE);
        mEmptyLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        mNoContentTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));

        return res;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLoadMyPlansTask = new LoadMyPlansTask();
        mLoadMyPlansTask.execute();
    }

    private class LoadMyPlansTask extends OmAsyncTask<Void, Void, List<ReadingPlan.ReadingPlanInfo>> {

        @Override
        protected void onPreExecute() {
            mLoadingLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<ReadingPlan.ReadingPlanInfo> doInBackground(Void... voids) {
            return getReadingPlans();
        }

        @Override
        protected void onPostExecute(List<ReadingPlan.ReadingPlanInfo> readingPlans) {
            mLoadingLayout.setVisibility(View.GONE);
            if (readingPlans != null && !readingPlans.isEmpty()) {
                showPlansListLayout(readingPlans);
            } else {
                showNoPlansLayout();
            }
        }

        private List<ReadingPlan.ReadingPlanInfo> getReadingPlans() {
            List<ReadingPlan.ReadingPlanInfo> readingPlans = new ArrayList<>();
            List<ReadingPlan.ReadingPlanInfo> readingPlanInfos = S.getDb().listAllReadingPlanInfo();
            if (readingPlanInfos != null && !readingPlanInfos.isEmpty()) {
                boolean isExistCompletePlans = false;
                for (ReadingPlan.ReadingPlanInfo readingPlanInfo : readingPlanInfos) {
                    if (readingPlanInfo.mCompletedDayCount < readingPlanInfo.mPlannedDayCount) {
                        readingPlans.add(readingPlanInfo);
                    } else {
                        isExistCompletePlans = true;
                    }
                }
                if (isExistCompletePlans) {
                    readingPlans.add(ReadingPlan.createCompletedIndicatorItem());
                }
            }
            return readingPlans;
        }

        private void showPlansListLayout(List<ReadingPlan.ReadingPlanInfo> readingPlans) {
            mMyPlansLv.setVisibility(View.VISIBLE);
            mEmptyLayout.setVisibility(View.GONE);
            if (mPlansAdapter == null) {
                mPlansAdapter = new PlansAdapter(readingPlans);
                mMyPlansLv.addHeaderView(getHeaderView());
                mMyPlansLv.setAdapter(mPlansAdapter);
            } else {
                if (mMyPlansLv.getHeaderViewsCount() == 0) {
                    mMyPlansLv.addHeaderView(getHeaderView());
                }
                mMyPlansLv.setAdapter(mPlansAdapter);
                mPlansAdapter.setData(readingPlans);
                mPlansAdapter.notifyDataSetChanged();
            }
        }

        private void showNoPlansLayout() {
            mMyPlansLv.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
        }
    }

    private View getHeaderView() {
        if (mHeaderView == null) {
            mHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.my_plan_lv_headerview_layout, null, false);
        }
        mAdListTopDivider = mHeaderView.findViewById(R.id.divider);
        mAdListTopDivider.setVisibility(View.GONE);
        return mHeaderView;
    }

    static class ViewHolder {
        ImageView icon;
        TextView name;
        ProgressBar pg;
    }

    private class PlansAdapter extends BaseAdapter {
        List<ReadingPlan.ReadingPlanInfo> mPlans;
        LayoutInflater mInflater;
        final int TYPE_COMPLETED_PLAN = 0;
        final int TYPE_NORMAL_PLAN = 1;

        public PlansAdapter(List<ReadingPlan.ReadingPlanInfo> plans) {
            this.mPlans = plans;
            mInflater = LayoutInflater.from(getActivity());
        }

        public void setData(List<ReadingPlan.ReadingPlanInfo> plans) {
            if (this.mPlans == null) {
                this.mPlans = new ArrayList<>();
            }
            if (!this.mPlans.isEmpty()) {
                this.mPlans.clear();
            }
            this.mPlans.addAll(plans);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return this.mPlans.size();
        }

        @Override
        public ReadingPlan.ReadingPlanInfo getItem(int position) {
            return this.mPlans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            ReadingPlan.ReadingPlanInfo readingPlan = getItem(position);
            if (readingPlan != null && readingPlan.mCompletedDayCount < readingPlan.mPlannedDayCount
                    && readingPlan.mPlannedDayCount != 0) {
                return TYPE_NORMAL_PLAN;
            }
            return TYPE_COMPLETED_PLAN;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        /**
         * Exception View.
         *
         * @param convertView
         * @return
         */
        private View getExceptionView(View convertView) {
            return new View(getContext());
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (!isAdded()) {
                return getExceptionView(convertView);
            }
            int type = getItemViewType(position);
            if (type == TYPE_NORMAL_PLAN) {
                convertView = getNormalPlanView(position, convertView, parent);
            } else if (type == TYPE_COMPLETED_PLAN) {
                convertView = getCompletedPlanView(position, convertView, parent);
            }
            if (convertView == null) {
                return getExceptionView(convertView);
            }
            return convertView;
        }

        private View getCompletedPlanView(final int position, View convertView, ViewGroup parent) {
            convertView = mInflater.inflate(R.layout.item_completed_plan, parent, false);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlanUtil.gotoPlanCompletedListPage(getActivity());
                }
            });
            return convertView;
        }

        private View getNormalPlanView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_my_plan, parent, false);
                viewHolder.name = (TextView) convertView.findViewById(R.id.plan_name);
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.plan_icon);
                viewHolder.pg = (ProgressBar) convertView.findViewById(R.id.plan_progressbar);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final ReadingPlan.ReadingPlanInfo readingPlan = getItem(position);
            if (readingPlan != null) {
                viewHolder.name.setText(readingPlan.mTitle);
                Picasso.with(getContext()).load(readingPlan.mSmallIconUrl).into(viewHolder.icon);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PlanUtil.gotoPlanProgressDetailPage(getActivity(), readingPlan.mDbPlanId);
                    }
                });
                int progress = 0;
                if (readingPlan.mPlannedDayCount > 0) {
                    progress = readingPlan.mCompletedDayCount * 100 / readingPlan.mPlannedDayCount;
                }
                viewHolder.pg.setProgress(progress);
            }
            return convertView;
        }

    }

    public void onEventMainThread(EventUpdateMyPlanLists event) {
        if (event == null) {
            return;
        }
        new LoadMyPlansTask().execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoadMyPlansTask != null) {
            mLoadMyPlansTask.cancel(true);
        }
        EventBus.getDefault().unregister(this);
    }

}
