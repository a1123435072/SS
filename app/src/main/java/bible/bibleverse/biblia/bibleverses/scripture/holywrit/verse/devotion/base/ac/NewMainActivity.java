package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.fw.basemodules.view.RobotoTextView;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.NewAnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.NewAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.App;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.helper.LeftDrawerHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.helper.WrapContentLinearLayoutManager;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter.HomeListAdapter;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventExitLockChargeReminder;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventExitMainPage;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventNewVersionLoaded;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventReadPositionJumped;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventUserOperationChanged;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventVerseOperate;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.devote.IabHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.devote.IabResult;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.devote.Inventory;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.devote.SkuItemHelp;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.RatingUtil;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.view.CenterAlignImageSpan;
import de.greenrobot.event.EventBus;
import yuku.afw.V;
import yuku.afw.storage.Preferences;
import yuku.alkitabintegration.display.Launcher;

import static android.view.View.VISIBLE;


/**
 * Created by yzq on 2017/2/20.
 */

public class NewMainActivity extends BaseActivity {

    IabHelper mHelper;

    private long mExitTime;

    private Toolbar mToolbar;
    private View mActionbarShadow;
    private DrawerLayout mDrawerLayout;
    private boolean mDirection;
    private int mActionBarMenuState;

    private LeftDrawerHelper mLeftDrawer;

    SwipeRefreshLayout mRefreshLayout;
    private ObservableRecyclerView mHomeListView;

    private HomeListAdapter mAdapter;

    private boolean mAdIsCanRegister = false;
    private int mScreenWidth, mScreenHeight, mAdImgWidth, mAdImgHeight, mHomeAdIconSize, mAdImgTransY, mAdIconTransX, mAdIconTransY;
    private float mAdImgScaleX, mAdImgScaleY, mAdIconScale;
    // full screen ad new style
    public ImageView mLcNewBackground, mLcNewImage1, mLcNewadIcon;
    public TextView mLcNewSummaryTv, mLcNewtitleTv;
    public RobotoTextView mLcNewAdOpenTv;

    public View mLcNewRootLayout;
    public View mLcNewAdContainer, mLcNewAdDetailLayout;
    public RelativeLayout mLcNewAdLayout;
    public ViewGroup mLcNewImgLayout;
    public View mLcNewImgBackground, mLcNewAdTag;

    private boolean isFsAdShowing = false;
    private AnimatorSet mFsAdAnimatorSet;

    private int mSourceType;

    private Handler mHandler = new Handler();


    private View mToolbarView;
    private View mToolbarOprsDefault;
    private View mToolbarOprsScrolled;
    private ImageView drawerIndicator, actionViewPrayer, actionViewDevotion, actionViewRead, actionViewVerse;
    private int mActionBarHeight;
    private int mBaseToolBarBgColor;
    private int mParallaxFunctionEntryHeight;
    private int mParallaxNavHeight;
    private int mScrollHeightNag;
    private boolean mInitHeaderAnimation;
    private boolean mIsToolbarScrolledView;



    SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                    mRefreshLayout.setRefreshing(false);
                }
            }, 1000);
        }
    };

    //页面跳转
    public static Intent createHomeIntent(Context context) {
        Intent it = new Intent(context, NewMainActivity.class);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return it;
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        NewAnalyticsHelper.init(this);
        setContentView(R.layout.activity_new_main);
        handleIntent();


        initToolbar();
        initView();

        mLeftDrawer = new LeftDrawerHelper(this, mDrawerLayout);
        mLeftDrawer.initLeftDrawer();

        // load exit app ad after on main activity 50s , as exit ad only display when read time > 1 min
//        mHandler.postDelayed(mLoadAppExitAdRunnable, 50 * 10mLoadAppExitAdRunnable00);

        initGPProductionInfo();
        Preferences.setLong(getString(R.string.pref_reading_start_time), System.currentTimeMillis());

        NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_HOME_OPEN, "finish");

    }

    private void initVar() {
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;
        mHomeAdIconSize = getResources().getDimensionPixelSize(R.dimen.margin_48);
    }

    private void initLcVar() {
        mAdImgWidth = mScreenWidth;
        mAdImgHeight = mAdImgWidth * 500 / 1024;
        int sub = getResources().getDimensionPixelSize(R.dimen.margin_6);
        int homeImgWidth = mScreenWidth;
        int homeImgHeight = homeImgWidth * 500 / 1024;
        int homeIconSize = getResources().getDimensionPixelSize(R.dimen.margin_48);
        int launchIconSize = getResources().getDimensionPixelSize(R.dimen.margin_72);
        int iconSub = (launchIconSize - homeIconSize) / 2;
        int actionBarHeight = getResources().getDimensionPixelSize(R.dimen.actionbar_height) + getResources().getDimensionPixelSize(R.dimen.home_function_entry_card_height);
        //48 = icon width ,24 = padding
        mAdIconTransX = (mScreenWidth - launchIconSize) / 2;//+ iconSub;
        //actionbar +padding+home img height+icon margin
        int homeIconToActionBar = actionBarHeight + homeImgHeight + getResources().getDimensionPixelSize(R.dimen.margin_8);
        int homeImgToBar = actionBarHeight;
        mAdIconTransY = mAdImgHeight - getResources().getDimensionPixelSize(R.dimen.margin_36) - homeIconToActionBar + iconSub - sub;
        // home icon / launch  icon
        mAdIconScale = (float) mHomeAdIconSize / launchIconSize;
        mAdImgScaleX = (float) homeImgWidth / mScreenWidth;
        mAdImgTransY = homeImgToBar;
    }

    private void initLcVarNew2() {
        mAdImgWidth = mScreenWidth;
        mAdImgHeight = mAdImgWidth * 180 / 344;
        int launchIconSize = getResources().getDimensionPixelSize(R.dimen.margin_54);

        final int sub = getResources().getDimensionPixelSize(R.dimen.margin_6);
        int homeImgWidth = mScreenWidth;
        final int homeImgHeight = homeImgWidth * 500 / 1024;
        final int actionBarHeight = getResources().getDimensionPixelSize(R.dimen.actionbar_height) + getResources().getDimensionPixelSize(R.dimen.home_function_entry_card_height);
        final int iconSub = (launchIconSize - mHomeAdIconSize) / 2;

        mAdImgScaleX = (float) homeImgWidth / mAdImgWidth;
        mAdIconTransX = (mScreenWidth - launchIconSize) / 2 - getResources().getDimensionPixelSize(R.dimen.margin_18);
        mAdImgScaleY = mAdImgScaleX;
        mAdIconScale = (float) mHomeAdIconSize / launchIconSize;
        mLcNewAdDetailLayout.post(new Runnable() {
            @Override
            public void run() {
                float y = mLcNewAdDetailLayout.getY();
                int imgY = (int) (y + getResources().getDimensionPixelSize(R.dimen.margin_30));
                mAdImgTransY = -((imgY - (homeImgHeight - mAdImgHeight) / 2) - getResources().getDimensionPixelSize(R.dimen.home_function_entry_card_height));
            }
        });
        mLcNewadIcon.post(new Runnable() {
            @Override
            public void run() {
                float y = mLcNewadIcon.getY();
                mAdIconTransY = (int) (y - getResources().getDimension(R.dimen.margin_8) - homeImgHeight - actionBarHeight + iconSub + sub);
            }
        });
    }

    private void initLcVarNew3() {
        mLcNewAdContainer.setBackgroundColor(getResources().getColor(R.color.gray));
        mAdImgWidth = mScreenWidth - 2 * getResources().getDimensionPixelSize(R.dimen.margin_20);
        mAdImgHeight = mAdImgWidth * 178 / 320;
        int homeImgWidth = mScreenWidth;
        final int homeImgHeight = homeImgWidth * 500 / 1024;

        mAdImgScaleX = (float) homeImgWidth / mAdImgWidth;
        mAdImgScaleY = (float) homeImgHeight / mAdImgHeight;
        final int actionBarHeight = getResources().getDimensionPixelSize(R.dimen.actionbar_height) + getResources().getDimensionPixelSize(R.dimen.home_function_entry_card_height);

        mLcNewAdDetailLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                float y = mLcNewAdDetailLayout.getY();
                mAdImgTransY = -(int) ((y - actionBarHeight) - (homeImgHeight - mAdImgHeight) / 2);
            }
        }, 1000);

    }

    private void initLcVarNew5() {
        mAdImgWidth = mScreenWidth;
        mAdImgHeight = mAdImgWidth * 180 / 344;
        int homeImgWidth = mScreenWidth;
        int homeImgHeight = homeImgWidth * 500 / 1024;

        mAdImgScaleX = 1;
        mAdImgScaleY = (float) homeImgHeight / mAdImgHeight;
        mAdImgTransY = -(getResources().getDimensionPixelSize(R.dimen.margin_18) + (mAdImgHeight - homeImgHeight) / 2) + getResources().getDimensionPixelSize(R.dimen.home_function_entry_card_height);
    }

    private void initToolbar() {
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            mActionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        // Base background color.
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.color.white, typedValue, true);
        mBaseToolBarBgColor = typedValue.data;

        // Setup ToolBar.
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mActionbarShadow = findViewById(R.id.toolbar_shadow);

//        toolbar.hideOverflowMenu();
        this.setSupportActionBar(mToolbar);

        mToolbarView = this.getLayoutInflater().inflate(R.layout.activity_new_main_toolbar, null);
        initToolBar(mToolbarView);
        this.getSupportActionBar().setCustomView(mToolbarView);

        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        this.getSupportActionBar().setDisplayShowCustomEnabled(true);

        // ActionBar alpha
        setToolBarAlpha(1f);

        // Transaction heights.
        calculateObserveHeight();
    }

    private void initToolBar(View toolbarView) {
        mToolbarOprsDefault = toolbarView.findViewById(R.id.group_default);
        mToolbarOprsScrolled = toolbarView.findViewById(R.id.group_scrolled);

        MaterialMenuDrawable materialMenuDrawable = new MaterialMenuDrawable(this, Color.BLACK, MaterialMenuDrawable.Stroke.THIN);
        drawerIndicator = (ImageView) toolbarView.findViewById(R.id.drawer_indicator);
        drawerIndicator.setImageDrawable(materialMenuDrawable);
        drawerIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDirection) {
                    mActionBarMenuState = 1;
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    mActionBarMenuState = 0;
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
                getMaterialMenuDrawable().animateIconState(intToState(mActionBarMenuState));
            }
        });

        actionViewPrayer = (ImageView) toolbarView.findViewById(R.id.prayer);
        actionViewDevotion = (ImageView) toolbarView.findViewById(R.id.devotion);
        actionViewRead = (ImageView) toolbarView.findViewById(R.id.read);
        actionViewVerse = (ImageView) toolbarView.findViewById(R.id.verse);

        actionViewPrayer.setVisibility(VISIBLE);
        actionViewPrayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_HOME_TOP_PRAYER, "click");
                startActivity(new Intent(NewMainActivity.this, PrayerCategoryGridActivity.class));
            }
        });
        actionViewDevotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_HOME_TOP_DEVOTION, "click");
                startActivity(new Intent(NewMainActivity.this, Utility.isRecommendDevotionSite(NewMainActivity.this) ? DevotionSitesGuideActivity.class : DevotionAllListActivity.class));
            }
        });
        actionViewRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_HOME_TOP_READ, "click");
                startActivity(Launcher.getBaseViewIntent());
            }
        });
        actionViewVerse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_HOME_TOP_VERSE, "click");
                startActivity(new Intent(NewMainActivity.this, DailyVerseListActivity.class));
            }
        });
    }

    /**
     * Set ToolBar background color alpha.
     *
     * @param alpha
     */
    public void setToolBarAlpha(float alpha) {
        float alphaFinal = alpha * 0.82f + 0.18f;// 0.18f is default value.
        setBackgroundAlpha(mToolbar, alphaFinal, mBaseToolBarBgColor);
    }

    private void setBackgroundAlpha(View view, float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        view.setBackgroundColor(a + rgb);
    }

    /**
     * NewMainActivity callback to notify scroll state.
     */
    public void calculateObserveHeight() {
        int headerFunctionCardHeight, headerNavHeight = 0;

        headerFunctionCardHeight = getResources().getDimensionPixelSize(R.dimen.home_function_entry_item_height);
        if (headerFunctionCardHeight > 0) {
            mParallaxFunctionEntryHeight = headerFunctionCardHeight;
        }

        headerNavHeight = getResources().getDimensionPixelSize(R.dimen.margin_64);
        if (headerNavHeight > 0) {
            mParallaxNavHeight = headerNavHeight;
        }

        if (mParallaxFunctionEntryHeight > 0 && mParallaxNavHeight > 0) {
            mInitHeaderAnimation = true;
        }
    }

    private ObservableScrollViewCallbacks mScrollViewCallbacks = new ObservableScrollViewCallbacks() {
        @Override
        public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

            int itemPositionOnTransfer = 2;

            int transitionHeight = mParallaxFunctionEntryHeight - mActionBarHeight;
            int headerHeight = transitionHeight + mParallaxNavHeight;

            LinearLayoutManager layoutManager = ((LinearLayoutManager) mHomeListView.getLayoutManager());
            int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
            View firstVisibleChild = mHomeListView.getChildAt(0);
            if (firstVisiblePosition > itemPositionOnTransfer) {
                scrollY = headerHeight - firstVisibleChild.getTop();
            } else {
                if (scrollY < 0) {
                    if (firstVisiblePosition == itemPositionOnTransfer && (firstVisibleChild.getHeight() + firstVisibleChild.getTop() <= mActionBarHeight)) {
                        mScrollHeightNag = headerHeight - scrollY;
                    }
                    scrollY = mScrollHeightNag + scrollY;
                    if (mScrollHeightNag == 0) {
                        scrollY = scrollY + mActionBarHeight;
                    }
                }
            }

//            float alpha = 0;
//            if (transitionHeight > 0) {
//                alpha = 1 - (float) Math.max(0, transitionHeight - scrollY) / transitionHeight;
//            }
//            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                alpha = (float) 1.0;
//            }
//            setToolBarAlpha(alpha);

            if (mInitHeaderAnimation) {
                if (scrollY >= headerHeight) {
                    // Toggle ActionBar revert
                    if (!mIsToolbarScrolledView) {
                        upScrollAnimation();
                        mIsToolbarScrolledView = true;
                    }
                } else {
                    if (mIsToolbarScrolledView) {
                        downScrollAnimation();
                        mIsToolbarScrolledView = false;
                    }
                }
            }
        }

        @Override
        public void onDownMotionEvent() {
        }

        @Override
        public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        }
    };

    private void downScrollAnimation() {
        AnimatorSet set = new AnimatorSet();
        AnimatorSet setOut = new AnimatorSet();
        setOut.playTogether(getZoonOutAnimatorSet(actionViewPrayer),
                getZoonOutAnimatorSet(actionViewDevotion),
                getZoonOutAnimatorSet(actionViewRead),
                getZoonOutAnimatorSet(actionViewVerse));
        set.playSequentially(setOut, getRotateInAnimatorSet(mToolbarOprsDefault));
        set.start();
        set.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator arg0) {
                mToolbarOprsDefault.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator arg0) {
            }

            @Override
            public void onAnimationEnd(Animator arg0) {
                mToolbarOprsDefault.setVisibility(View.VISIBLE);
                mToolbarOprsScrolled.setVisibility(View.GONE);
                mActionbarShadow.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator arg0) {
            }
        });
    }

    private void upScrollAnimation() {
        AnimatorSet set = new AnimatorSet();
        AnimatorSet setIn = new AnimatorSet();
        setIn.playTogether(getZoonInAnimatorSet(actionViewPrayer),
                getZoonInAnimatorSet(actionViewDevotion),
                getZoonInAnimatorSet(actionViewRead),
                getZoonInAnimatorSet(actionViewVerse));
        set.playSequentially(getRotateOutAnimatorSet(mToolbarOprsDefault), setIn);
        set.start();
        set.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator arg0) {
                mToolbarOprsScrolled.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator arg0) {
            }

            @Override
            public void onAnimationEnd(Animator arg0) {
                mToolbarOprsDefault.setVisibility(View.GONE);
                mToolbarOprsScrolled.setVisibility(View.VISIBLE);
                mActionbarShadow.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator arg0) {
            }
        });
    }

    AnimatorSet getZoonInAnimatorSet(View target) {
        AnimatorSet setZoomIn = new AnimatorSet();
        setZoomIn.playTogether(ObjectAnimator.ofFloat(target, "scaleX", 0.0f, 1.0f), ObjectAnimator.ofFloat(target, "scaleY", 0.0f, 1.0f),
                ObjectAnimator.ofFloat(target, "alpha", 0.0f, 1.0f));
        return setZoomIn;
    }

    AnimatorSet getZoonOutAnimatorSet(View target) {
        AnimatorSet setZoomOut = new AnimatorSet();
        setZoomOut.playTogether(ObjectAnimator.ofFloat(target, "alpha", 1.0f, 0.0f), ObjectAnimator.ofFloat(target, "scaleX", 1.0f, 0.0f),
                ObjectAnimator.ofFloat(target, "scaleY", 1.0f, 0.0f));
        return setZoomOut;
    }

    AnimatorSet getRotateOutAnimatorSet(View target) {
        AnimatorSet setRotateOut = new AnimatorSet();
        setRotateOut.playTogether(new Animator[]{ObjectAnimator.ofFloat(target, "rotationX", new float[]{0.0F, 90.0F}),
                ObjectAnimator.ofFloat(target, "alpha", new float[]{1.0F, 0.0F})});
        return setRotateOut;
    }

    AnimatorSet getRotateInAnimatorSet(View target) {
        AnimatorSet setRotateIn = new AnimatorSet();
        setRotateIn.playTogether(ObjectAnimator.ofFloat(target, "rotationX", 90.0F, 0.0F), ObjectAnimator.ofFloat(target, "alpha", 0.0f, 1.0f));
        return setRotateIn;
    }

    private void initView() {
        mDrawerLayout = ((DrawerLayout) findViewById(R.id.drawer_layout));
        mDrawerLayout.setScrimColor(Color.parseColor("#66000000"));
        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                getMaterialMenuDrawable().setTransformationOffset(
                        MaterialMenuDrawable.AnimationState.BURGER_ARROW,
                        mDirection ? 2 - slideOffset : slideOffset
                );
            }

            @Override
            public void onDrawerOpened(android.view.View drawerView) {
                mDirection = true;
            }

            @Override
            public void onDrawerClosed(android.view.View drawerView) {
                mDirection = false;
            }
        });

        mRefreshLayout = V.get(this, R.id.refresh_layout);
        mRefreshLayout.setColorSchemeResources(R.color.theme_color_accent);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
        mHomeListView = V.get(this, R.id.list_view);

        WrapContentLinearLayoutManager linearLayoutManager = new WrapContentLinearLayoutManager(this);
        mHomeListView.setLayoutManager(linearLayoutManager);
        if (mAdapter == null) {
            mAdapter = new HomeListAdapter(this, getLayoutInflater());
        }
        mHomeListView.setAdapter(mAdapter);
        mAdapter.loadItemData();
        mHomeListView.setScrollViewCallbacks(mScrollViewCallbacks);
    }


    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            mSourceType = intent.getIntExtra(Constants.KEY_SOURCE_TYPE, 0);
            int notifyType = intent.getIntExtra(Constants.KEY_NOTIFICATION_TYPE, 0);
            int notifyId = intent.getIntExtra(Constants.KEY_NOTIFICATION_ID, 0);
            //from  notify
        }
    }

    private void initGPProductionInfo() {
        mHelper = new IabHelper(this, SkuItemHelp.getPublicKey());
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess() || mHelper == null) {
//                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
                    return;
                }

                try {
                    mHelper.queryInventoryAsync(true, SkuItemHelp.getSkuItemList(), SkuItemHelp.getSubsSkuList(), mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
//                    Log.d(TAG, "Error querying inventory. Another async operation in progress.");
                } catch (NullPointerException e) {
                }
            }
        });
        mHelper.enableDebugLogging(false);
    }

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null || result.isFailure()) {
                return;
            }

            if (SkuItemHelp.isSubscribeSuccess(NewMainActivity.this, inventory)) {
                Preferences.setBoolean(getString(R.string.pref_ad_show), false);
            } else {
                Preferences.setBoolean(getString(R.string.pref_ad_show), true);
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (isSlidingMenuOpen()) {
            closeMenu();
            return true;
        } else if (exit()) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private boolean exit() {
        if (isShowLockScreenGuideDialog()) {
            //
        } else if ((System.currentTimeMillis() - mExitTime) > 2000) {
            boolean shownRating = RatingUtil.ratingUsOnTrigger(this);
            if (!shownRating) {
                Toast.makeText(this, getString(R.string.exit_hint), Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            }
        } else {
            long readingStartTime = Preferences.getLong(getString(R.string.pref_reading_start_time), 0);
            if (readingStartTime > 0 && System.currentTimeMillis() - readingStartTime > 60 * 1000) {
                startActivity(ExitAppActivity.createIntent(this, ExitAppActivity.TYPE_EXIT_APP));
            } else {
                finish();
            }
            RatingUtil.recordTriggerEventTimes(getApplicationContext());
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.clear();
        getMenuInflater().inflate(R.menu.activity_new_main, menu);

        MenuItem removeAdItem = menu.findItem(R.id.menuRemoveAds);
        SpannableString removeAdString = new SpannableString("*  " + getString(R.string.remove_ads));
        CenterAlignImageSpan removeAdImageSpan = new CenterAlignImageSpan(this, R.drawable.ic_menu_remove_ad);
        removeAdString.setSpan(removeAdImageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        removeAdItem.setTitle(removeAdString);

        MenuItem donateItem = menu.findItem(R.id.menuDonate);
        SpannableString donateString = new SpannableString("*  " + getString(R.string.donate));
        CenterAlignImageSpan donateImageSpan = new CenterAlignImageSpan(this, R.drawable.ic_menu_donate);
        donateString.setSpan(donateImageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        donateItem.setTitle(donateString);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuDonate:
                startActivity(DonateActivity.createIntent(this, Constants.FROM_TOP_MENU));
                return true;
            case R.id.menuRemoveAds:
                NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_HOME_NAV_REMOVEADS, "click");
                startActivity(RemoveAdsActivity.createIntent(this, Constants.FROM_TOP_MENU));
                return true;
        }

        super.onOptionsItemSelected(item);
        return false;
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    /// left drawer
    private MaterialMenuDrawable getMaterialMenuDrawable() {
        return (MaterialMenuDrawable) drawerIndicator.getDrawable();
    }

    private static MaterialMenuDrawable.IconState intToState(int state) {
        switch (state) {
            case 0:
                return MaterialMenuDrawable.IconState.BURGER;
            case 1:
                return MaterialMenuDrawable.IconState.ARROW;
            case 2:
                return MaterialMenuDrawable.IconState.X;
            case 3:
                return MaterialMenuDrawable.IconState.CHECK;
        }
        throw new IllegalArgumentException("Must be a number [0,3)");
    }

    public void onEventMainThread(EventVerseOperate event) {
        if (event != null) {
            switch (event.operateType) {
                case EventVerseOperate.HIGHLIGHTS:
                    mLeftDrawer.setHighlightNum();
                    break;
                case EventVerseOperate.NOTE:
                    mLeftDrawer.setNotesNum();
                    break;
                case EventVerseOperate.BOOKMARK:
                    mLeftDrawer.setBookmarkNum();
                    break;
                case EventVerseOperate.PLANS:
                    mLeftDrawer.setPlansNum();
                    break;
                case EventVerseOperate.IMAGES:
                default:
                    mLeftDrawer.refreshAllNum();
                    break;
            }
        }
    }

    public void onEventMainThread(EventNewVersionLoaded event) {
//        if (mAdapter != null) {
//            mAdapter.setCurrentVersion(event.mv.getVersion());
//            mAdapter.notifyDataSetChanged();
//        }
    }



    public void onEventMainThread(EventExitMainPage event) {
        if (event == null) {
            return;
        }
        finish();
        overridePendingTransition(0, 0);
    }

    public void onEventMainThread(EventReadPositionJumped event) {
        if (mAdapter != null) {
            mAdapter.refreshReadHistory();
            mAdapter.notifyDataSetChanged();
        }
    }

    public void onEventMainThread(EventExitLockChargeReminder eventExitLockChargeReminder) {
        if (eventExitLockChargeReminder == null) {
            return;
        }
        finish();
    }

    public void onEventMainThread(EventUserOperationChanged event) {
        switch (event.mType) {
            case EventUserOperationChanged.TYPE_DAILY_VERSE:
                if (mAdapter != null) {
                    mAdapter.updateReadVerseCount();
                }
                break;
            case EventUserOperationChanged.TYPE_PRAY:
                if (mAdapter != null) {
                    mAdapter.updatePrayCount();
                }
                break;
            case EventUserOperationChanged.TYPE_READ_BIBLE_TIME:
                if (mAdapter != null) {
                    mAdapter.updateReadBibleTime();
                }
                break;
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getInstance().sendTimeAnalysis();
        App.getInstance().saveTodayOperationDataToSP();

        isFsAdShowing = false;

        if (mFsAdAnimatorSet != null && mFsAdAnimatorSet.isRunning()) {
            mFsAdAnimatorSet.cancel();
        }
        if (mHelper != null) {
            try {
                mHelper.dispose();
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            } finally {
                mHelper = null;
            }
        }

        if (mLeftDrawer != null) {
            mLeftDrawer.destroy();
        }

        EventBus.getDefault().unregister(this);
        if (mAdapter != null) {
            mAdapter.clearItems();
            mAdapter.destroy();
        }
    }


    /**
     * @return Whether side menu is open
     */
    public boolean isSlidingMenuOpen() {
        return mDrawerLayout.isDrawerVisible(GravityCompat.START);
    }

    public void closeMenu() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private boolean isShowLockScreenGuideDialog() {
//        boolean isShowDialog = Utility.isShowLockScreenGuideExit(this);
        boolean isShowDialog = true;
//        if (isShowDialog) {
        startActivity(new Intent(this, LockChargeNotifyActivity.class));
//        }
        return isShowDialog;
    }
}
