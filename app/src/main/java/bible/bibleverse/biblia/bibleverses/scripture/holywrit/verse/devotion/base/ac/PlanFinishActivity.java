package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.fw.basemodules.utils.OmAsyncTask;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
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
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.view.DrawHookView;
import yuku.afw.V;


/**
 * Created by Mr_ZY on 16/11/7.
 */

public class PlanFinishActivity extends BaseActivity {
    private TextView mFinishGood, mReadDayStatus, mFinishTitle, mOkBtn;
    private DrawHookView mDrawHookView;
    private LinearLayout mTitleLayout, mRelatedLayout;
    private View mResultLayout;
    private Toolbar mToolbar;
    private GridView mGridView;

    private String mCurrentDay, mAllDayCount, mTitle;
    private long mPlanId;
    private int mCateId = Constants.PLAN_DISCOVER_CATE_ID;
    private boolean mFinishTotalPlan;

    private final int IMG_WIDTH = 156;
    private final int IMG_HEIGHT = 88;
    private int mIconWidth;

    List<ReadingPlan.ReadingPlanInfo> mPlanList = new ArrayList<>();

    private int mMoveDisatnce;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_finish);

        handleIntent();
        initView();
        initToolbar();
        startDrawHookAnimation();
    }

    public static Intent createIntent(Context context, int finishDay, int allDay, boolean finishTotalPlan, long planId) {
        Intent intent = new Intent();
        intent.setClass(context, PlanFinishActivity.class);
        intent.putExtra(Constants.KEY_CURRENT_DAY, finishDay);
        intent.putExtra(Constants.KEY_PLANNED_DAY_COUNT, allDay);
        intent.putExtra(Constants.KEY_COMPLETED_TOTAL_PLAN, finishTotalPlan);
        intent.putExtra(Constants.KEY_READ_FINISH_PLAN_ID, planId);
        return intent;
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            mCurrentDay = String.valueOf(intent.getIntExtra(Constants.KEY_CURRENT_DAY, 0));
            mAllDayCount = String.valueOf(intent.getIntExtra(Constants.KEY_PLANNED_DAY_COUNT, 0));
            mFinishTotalPlan = intent.getBooleanExtra(Constants.KEY_COMPLETED_TOTAL_PLAN, false);
            mPlanId = intent.getLongExtra(Constants.KEY_READ_FINISH_PLAN_ID, 0);
            ArrayList<String> list = S.getDb().getCateIdByPlanId(mPlanId);
            if (list != null && list.size() >= 2) {
                mTitle = list.get(1);
            }
        } else {
            finish();
        }
    }

    private void initView() {
        int screenheight = getResources().getDisplayMetrics().heightPixels;
        int contentHeight = getResources().getDimensionPixelSize(R.dimen.plan_finish_content_height);
        mMoveDisatnce = (screenheight - Utility.getStatusBarHeight(this) - contentHeight) / 2;
        mDrawHookView = V.get(this, R.id.finish_icon);
        mResultLayout = V.get(this, R.id.result_layout);
        mTitleLayout = V.get(this, R.id.txt_layout);
        mFinishGood = V.get(this, R.id.finish_good);
        mFinishTitle = V.get(this, R.id.finish_title);
        mReadDayStatus = V.get(this, R.id.plan_read_day_status);
        mRelatedLayout = V.get(this, R.id.related_layout);
        mGridView = V.get(this, R.id.gridView);

        mOkBtn = V.get(this, R.id.ok_btn);
        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (mFinishTotalPlan) {
            mFinishTitle.setText(getString(R.string.plan_finish_all_day_title));
            mFinishGood.setText(getString(R.string.very_good));
            new LoadRelatedPlansTask().execute();
        } else {
            mFinishTitle.setText(getString(R.string.plan_finish_one_day_title, mCurrentDay));
            mFinishGood.setText(getString(R.string.good));
        }
        mReadDayStatus.setText(getString(R.string.plan_finish_have_read_days, mCurrentDay, mAllDayCount));
        mIconWidth = (getResources().getDisplayMetrics().widthPixels - 3 * getResources().getDimensionPixelSize(R.dimen.margin_16)) / 2;
    }

    private void initToolbar() {
        mToolbar = V.get(this, R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
            mToolbar.setNavigationIcon(R.drawable.ic_back_white);
            mToolbar.setBackgroundColor(getResources().getColor(R.color.read_finish_page_bg_color));
        }
    }

    private void startDrawHookAnimation() {
        mDrawHookView.setVisibility(View.VISIBLE);
        mDrawHookView.setDrawHookListener(new DrawHookView.DrawHookListener() {
            @Override
            public void onDrawEnd() {
                if (mTitleLayout.getVisibility() == View.VISIBLE) return;
                YoYo.with(Techniques.BounceInUp).duration(1000).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mTitleLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        showReadDays();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).playOn(mTitleLayout);
            }
        });
        mDrawHookView.reDraw();
    }

    private void showReadDays() {
        YoYo.with(Techniques.FadeIn).duration(500).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mReadDayStatus.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(mReadDayStatus);
    }

    private void showBottomLayout(final View view) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.SlideInUp).duration(500).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        view.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).playOn(view);
            }
        }, 200);

    }

    private class LoadRelatedPlansTask extends OmAsyncTask<Void, Void, List<ReadingPlan.ReadingPlanInfo>> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected List<ReadingPlan.ReadingPlanInfo> doInBackground(Void... voids) {
            return getPlans();
        }

        @Override
        protected void onPostExecute(List<ReadingPlan.ReadingPlanInfo> planList) {
            if (planList != null && !planList.isEmpty()) {
                PlansGridAdapter plansGridAdapter = new PlansGridAdapter(planList);
                mGridView.setAdapter(plansGridAdapter);
                plansGridAdapter.notifyDataSetChanged();
            }
        }

        private List<ReadingPlan.ReadingPlanInfo> getPlans() {
            List<ReadingPlan.ReadingPlanInfo> planList = new ArrayList<>();
            BPCDProto.BPCD bpcd = new BPCDService(PlanFinishActivity.this).getData(String.valueOf(mCateId));
            if (bpcd != null) {
                List<BPProto.BP> list = bpcd.getListList();
                if (list != null && !list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        if (planList.size() >= 4) {
                            break;
                        }
                        if (list.get(i).getTitle().equals(mTitle)) {
                            continue;
                        }
                        planList.add(ReadingPlan.createCategoryPlanItemFromBP(list.get(i)));
                    }
                    mPlanList = planList;
                    return planList;
                }
            }
            return planList;
        }
    }

    static class ViewHolder {
        ImageView icon;
        TextView name;
        TextView daysTv;
    }

    private class PlansGridAdapter extends BaseAdapter {
        List<ReadingPlan.ReadingPlanInfo> mPlans;
        LayoutInflater mInflater;

        public PlansGridAdapter(List<ReadingPlan.ReadingPlanInfo> plans) {
            this.mPlans = plans;
            mInflater = LayoutInflater.from(PlanFinishActivity.this);
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_plan_layout, parent, false);
                viewHolder.name = (TextView) convertView.findViewById(R.id.plan_title);
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.plan_icon);
                viewHolder.daysTv = (TextView) convertView.findViewById(R.id.plan_days);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final ReadingPlan.ReadingPlanInfo readingPlan = getItem(position);
            if (readingPlan != null) {
                viewHolder.name.setText(readingPlan.mTitle);
                viewHolder.daysTv.setText(getString(R.string.days, String.valueOf(readingPlan.mPlannedDayCount)));
                Picasso.with(PlanFinishActivity.this).load(readingPlan.mSmallIconUrl).into(viewHolder.icon);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PlanUtil.gotoPlanDetailPage(PlanFinishActivity.this, readingPlan.mServerQueryId, readingPlan.mPlannedDayCount, readingPlan.mTitle, readingPlan.mBigImageUrl, readingPlan.mSmallIconUrl);
                    }
                });
            }
            setImageParams(viewHolder.icon);
            return convertView;
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

    private void showViewUpAnim(View view) {
        ObjectAnimator animUp = ObjectAnimator.ofFloat(view, "translationY", 0, -mMoveDisatnce);
        animUp.setDuration(500);
        animUp.setInterpolator(new AccelerateDecelerateInterpolator());
        animUp.start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mToolbar.setBackgroundColor(getResources().getColor(R.color.read_finish_page_bg_color));
    }

}
