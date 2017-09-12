package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr.MyFavoriteDevotionsFragment;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr.MyFavoritePraysFragment;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import yuku.afw.V;


public class FavoriteActivity extends BaseActivity {
    public static final String TAG = FavoriteActivity.class.getSimpleName();

    private static final String INSTANCE_STATE_tab = "tab";

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TabLayout mTablayout;
    private View mFragmentLayout, mViewPagerLayout, mShadowLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        initToolbar();

        initView();
    }

    private void initView() {
        mViewPagerLayout = V.get(this, R.id.viewPager_layout);
        mShadowLayout = V.get(this, R.id.shadow_layout);
        mFragmentLayout = V.get(this, R.id.fragment_frame);
        mViewPager = V.get(this, R.id.viewPager);
        mTablayout = V.get(this, R.id.tablayout);
        mViewPagerLayout.setVisibility(View.VISIBLE);
        mFragmentLayout.setVisibility(View.GONE);
        mShadowLayout.setVisibility(View.GONE);
        mViewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        mTablayout.setTabMode(TabLayout.MODE_FIXED);
        mTablayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(0);
    }

    private void initToolbar() {
        mToolbar = V.get(this, R.id.toolbar);
        setSupportActionBar(mToolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_black);
            mToolbar.setTitle(R.string.favorites);
            mToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black));
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateUp();
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(INSTANCE_STATE_tab, mViewPager.getCurrentItem());
    }

    public class MyAdapter extends FragmentPagerAdapter {
        final int[] pageTitleResIds = {R.string.devotion, R.string.prayer};

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(final int position) {
            final Fragment res;
            if (position == 0) {
                res = new MyFavoriteDevotionsFragment();
            } else {
                res = new MyFavoritePraysFragment();
            }
            return res;
        }

        @Override
        public int getCount() {
            return pageTitleResIds.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(pageTitleResIds[position]);
        }

        public Fragment getFragment(int position) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            return fragmentManager.findFragmentByTag(getFragmentTag(position));
        }

        private String getFragmentTag(int position) {
            return "android:switcher:" + mViewPager.getId() + ":" + getItemId(position);
        }
    }

}
