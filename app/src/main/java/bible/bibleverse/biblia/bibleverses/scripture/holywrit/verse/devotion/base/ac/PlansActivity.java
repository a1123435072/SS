package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.UserBehaviorAnalytics;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventPlanTabSelected;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr.MyPlansFragment;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr.PlanDiscoverFragment;
import de.greenrobot.event.EventBus;
import yuku.afw.V;

/**
 * Created by yzq on 2017/2/20.
 */

public class PlansActivity extends BaseActivity {

    private static final String TO_MY_PLAN = "to_my_plan";

    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;
    TabLayout mTabLayout;

    public static Intent createIntent(Context context, boolean toMyPlan) {
        Intent intent = new Intent(context, PlansActivity.class);
        intent.putExtra(TO_MY_PLAN, toMyPlan);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);

        initView();
        initToolbar();

        boolean toMyPlan = getIntent() != null ? getIntent().getBooleanExtra(TO_MY_PLAN, false) : false;
        if (!toMyPlan) {
            locatePlanDiscoverPage();
        }
    }

    private void initView() {
        mViewPager = V.get(this, R.id.viewPager);
        mTabLayout = V.get(this, R.id.tablayout);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    UserBehaviorAnalytics.trackUserBehavior(PlansActivity.this, AnalyticsConstants.P_MYPLANLIST, null);
                } else if (position == 1) {
                    UserBehaviorAnalytics.trackUserBehavior(PlansActivity.this, AnalyticsConstants.P_DISCOVERPAGE, null);
                }
                EventBus.getDefault().post(new EventPlanTabSelected(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initToolbar() {
        final Toolbar toolbar = V.get(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_back_black);
        setTitle(R.string.plans);
    }

    public void locatePlanDiscoverPage() {
        if (mViewPager != null) {
            mViewPager.setCurrentItem(1);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private PlanDiscoverFragment discoverFragment;
        private MyPlansFragment myPlansFragment;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 1) {
                if (discoverFragment == null) {
                    discoverFragment = new PlanDiscoverFragment();
                }
                return discoverFragment;
            } else {
                if (myPlansFragment == null) {
                    myPlansFragment = new MyPlansFragment();
                }
                return myPlansFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.my_plans);
                case 1:
                    return getString(R.string.discover);
            }
            return null;
        }
    }

}
