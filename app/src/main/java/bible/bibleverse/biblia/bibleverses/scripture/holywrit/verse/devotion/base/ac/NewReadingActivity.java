package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.wang.newversion.NAVLoadingIndicatorView;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.NewAnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.NewAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.SelfAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.UserBehaviorAnalytics;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.App;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.U;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.helper.FullyLinearLayoutManager;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter.PopularListAdapter;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventActiveVersionChanged;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventNewVersionLoaded;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventReadPositionJumped;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.MVersion;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.MVersionDb;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.MVersionInternal;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.storage.Prefkey;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.LidToAri;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.widget.BibleChapterView;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.widget.BottomViewDisplayControl;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.widget.BottomViewVersesOperation;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.widget.GotoButton;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.widget.ScrollbarSetter;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.widget.TwoFingerLinearLayout;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import de.greenrobot.event.EventBus;
import yuku.afw.V;
import yuku.afw.storage.Preferences;
import yuku.alkitab.model.Book;
import yuku.alkitab.model.PericopeBlock;
import yuku.alkitab.model.SingleChapterVerses;
import yuku.alkitab.model.Version;
import yuku.alkitab.util.Ari;

/**
 * Created by yzq on 2017/2/20.
 */

public class NewReadingActivity extends BaseActivity {
    private static final String TAG = "bible_reading";

    // view element
    private LinearLayout mToolbarLayout;
    private View mBackBtn;
    private GotoButton mGotoBtn;
    private TextView mVersionTv;
    private View mSearchBtn, mTextAppearanceBtn;

    private LinearLayout mIndicatorLayout;
    private TextView mIndicatorChapter, mIndicatorVersion;

    private TwoFingerLinearLayout mTwoFingerLayout;
    private BibleChapterView mChapterView;

    private FloatingActionButton mLeftBtn;
    private FloatingActionButton mRightBtn;

    private TextView mCompleteBtn;
    private TextView mRelatedBtn;
    private RecyclerView mPopularList;
    private PopularListAdapter mPopularAdapter;

    protected BottomSheetLayout mOverlayContainer;

    private BottomViewDisplayControl mBottomViewDisplayControl;
    private BottomViewVersesOperation mBottomViewVersesOperation;

    // data filed
    private Book mActiveBook;
    private int mCurrentChapter = 0;
    private boolean mHasReachBottom = false;
    private boolean mHasPopularData = false;

    // ad
    private LinearLayout mBottomBannerAdLayout;
    AdView mBottomBannerAdView;
    private NAVLoadingIndicatorView mLoadingIndicator;
    private View mBottomView;

    private BibleChapterView.OnBottomReachedListener mChapterViewReachedBottomListener = new BibleChapterView.OnBottomReachedListener() {
        @Override
        public void onBottomReached() {
            mHasReachBottom = true;
            mRightBtn.setVisibility(View.GONE);
            mLeftBtn.setVisibility(View.GONE);
            mRelatedBtn.setVisibility(View.GONE);
        }

        @Override
        public void onLeftBottom() {
            mHasReachBottom = false;
            mRightBtn.setVisibility(View.VISIBLE);
            mLeftBtn.setVisibility(View.VISIBLE);
            mRelatedBtn.setVisibility(mHasPopularData ? View.VISIBLE : View.GONE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        setContentView(R.layout.activity_new_reading);

        initView();
        initListener();

        mBottomView.setVisibility(View.GONE);

        handleIntent(getIntent(), "onCreate");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doScroll();
            }
        }, 200);
        getWindow().getDecorView().setKeepScreenOn(Preferences.getBoolean(getString(R.string.pref_keepScreenOn_key),
                getResources().getBoolean(R.bool.pref_keepScreenOn_default)));
    }

    private void initView() {
        mOverlayContainer = V.get(this, R.id.overlayContainer);

        mToolbarLayout = V.get(this, R.id.toolbar_layout);
        mBackBtn = V.get(this, R.id.back_btn);
        mGotoBtn = V.get(this, R.id.bGoto);
        mVersionTv = V.get(this, R.id.bVersion);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTextAppearanceBtn = V.get(this, R.id.menuTextSize);
        mSearchBtn = V.get(this, R.id.menuSearch);

        mIndicatorLayout = V.get(this, R.id.chapter_indicator);
        mIndicatorChapter = V.get(this, R.id.chapter_indicator_chapter);
        mIndicatorVersion = V.get(this, R.id.chapter_indicator_version);

        mTwoFingerLayout = V.get(this, R.id.two_finger_layout);
        mChapterView = V.get(this, R.id.chapter_view);

        mLeftBtn = V.get(this, R.id.bLeft);
        mRightBtn = V.get(this, R.id.bRight);

        mCompleteBtn = V.get(this, R.id.complete_btn);
        mRelatedBtn = V.get(this, R.id.bRelated);
        mPopularList = V.get(this, R.id.popular_list);
        mPopularList.setLayoutManager(new FullyLinearLayoutManager(this));

        mLoadingIndicator = V.get(this, R.id.loading_indicator);
        mLoadingIndicator.setIndicatorColor(getResources().getColor(R.color.theme_color_accent));
        mBottomView = V.get(this, R.id.bottom_loading);
    }

    private void initListener() {
        mChapterView.setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
            @Override
            public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
            }

            @Override
            public void onDownMotionEvent() {
            }

            @Override
            public void onUpOrCancelMotionEvent(ScrollState scrollState) {
                if (scrollState == ScrollState.UP) {
                    if (!mIsFullScreenMode) {
                        hideBigToolbar();
                    }
                } else if (scrollState == ScrollState.DOWN) {
                    if (mIsFullScreenMode) {
                        showBigToolbar();
                    }
                }
            }
        });
        mGotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoBtnClick();
            }
        });
        mVersionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                versionTvClick();
            }
        });
        mTextAppearanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textAppearanceBtnClick();
            }
        });
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBtnClick();
            }
        });

        mTwoFingerLayout.setListener(mTwoFingerOperationListener);

        mChapterView.setSelectVerseListener(mSelectedVersesListener);
        mChapterView.setOnBottomReachedListener(mChapterViewReachedBottomListener);

        mLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelfAnalyticsHelper.sendReadVerseAnalytics(NewReadingActivity.this, "click", "previous");
                leftBtnClick();
            }
        });
        mRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelfAnalyticsHelper.sendReadVerseAnalytics(NewReadingActivity.this, "click", "next");
                rightBtnClick();
            }
        });

        mCompleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelfAnalyticsHelper.sendReadVerseAnalytics(NewReadingActivity.this, "click", "complete_chapter");
                completeBtnClick();
            }
        });
        mRelatedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChapterView != null && mPopularList != null) {
                    mChapterView.smoothScrollTo(0, mPopularList.getTop());
                }
            }
        });

        mPopularAdapter = new PopularListAdapter(this);
        mPopularAdapter.setLoadPopularDataCallback(new PopularListAdapter.LoadPopularDataCallback() {
            @Override
            public void onLoadTaskStart() {
                mHasPopularData = false;
                mRelatedBtn.setVisibility(View.GONE);
            }

            @Override
            public void onLoadTaskFinish(int popularDataCount) {
                if (popularDataCount > 0) {
                    mHasPopularData = true;
                    mRelatedBtn.setVisibility(mHasReachBottom ? View.GONE : View.VISIBLE);
                    mRelatedBtn.setText(getString(R.string.related_tip, String.valueOf(popularDataCount)));
                }
            }
        });
        mPopularList.setAdapter(mPopularAdapter);
        mPopularList.setNestedScrollingEnabled(false);

        App.getLbm().registerReceiver(mReloadAttributeMapReceiver, new IntentFilter(Constants.ACTION_ATTRIBUTE_MAP_CHANGED));
    }

    static class IntentResult {
        public int ari;
        public boolean selectVerse;
        public int selectVerseCount;

        public IntentResult(final int ari) {
            this.ari = ari;
        }
    }

    private void handleIntent(Intent intent, String via) {
        final IntentResult intentResult = processIntent(intent, via);
        final int openingAri;
        final boolean selectVerse;
        final int selectVerseCount;

        if (intentResult == null) {
            // restore the last (version; book; chapter and verse).
            final int lastBookId = Preferences.getInt(Prefkey.lastBookId, 0);
            final int lastChapter = Preferences.getInt(Prefkey.lastChapter, 0);
            final int lastVerse = Preferences.getInt(Prefkey.lastVerse, 0);
            openingAri = Ari.encode(lastBookId, lastChapter, lastVerse);
            selectVerse = false;
            selectVerseCount = 1;
            Log.d(TAG, "Going to the last: bookId=" + lastBookId + " chapter=" + lastChapter + " verse=" + lastVerse);
        } else {
            openingAri = intentResult.ari;
            selectVerse = intentResult.selectVerse;
            selectVerseCount = intentResult.selectVerseCount;
        }

        final String lastVersionId = Preferences.getString(Prefkey.lastVersionId);
        final MVersion mv = getVersionFromVersionId(lastVersionId);

        if (mv != null) {
            loadVersion(mv, false);
        }
        if (mv == null || S.activeVersion == null) {
            loadVersion(getVersionFromVersionId("preset/en-kjv"), false);
        }

        if (via.equals("newIntent")) {
            saveHistoryPosition();
            EventBus.getDefault().post(new EventReadPositionJumped());
        }

        if (S.activeVersion != null) {
            // load book
            final Book book = S.activeVersion.getBook(Ari.toBook(openingAri));
            if (book != null) {
                this.mActiveBook = book;
            } else { // can't load last book or bookId 0
                this.mActiveBook = S.activeVersion.getFirstBook();
            }

            // have not created view yet
            if (mLeftBtn == null) {
                return;
            }

            // load chapter and verse 加载  chapter verse
            display(Ari.toChapter(openingAri), Ari.toVerse(openingAri));

            if (selectVerse) {
                for (int i = 0; i < selectVerseCount; i++) {
                    final int verse_1 = Ari.toVerse(openingAri) + i;
                    callAttentionForVerseToBothSplits(verse_1);
                }
            }
        }
    }

    /**
     * @return non-null if the intent is handled by any of the intent handler (e.g. VIEW)
     */
    private IntentResult processIntent(Intent intent, String via) {
        final IntentResult viewResult = tryGetIntentResultFromView(intent);
        if (viewResult != null) return viewResult;

        return null;
    }

    /**
     * did we get here from VIEW intent?
     */
    private IntentResult tryGetIntentResultFromView(Intent intent) {
        if (!U.equals(intent.getAction(), "yuku.alkitab.action.VIEW")) return null;

        final boolean selectVerse = intent.getBooleanExtra("selectVerse", false);
        final int selectVerseCount = intent.getIntExtra("selectVerseCount", 1);

        if (intent.hasExtra("ari")) {
            int ari = intent.getIntExtra("ari", 0);
            if (ari != 0) {
                final IntentResult res = new IntentResult(ari);
                res.selectVerse = selectVerse;
                res.selectVerseCount = selectVerseCount;
                return res;
            } else {
//                new MaterialDialog.Builder(NewReadingActivity.this)
//                        .content("Invalid ari: " + ari)
//                        .positiveText(R.string.ok)
//                        .show();
                return null;
            }
        } else if (intent.hasExtra("lid")) {
            int lid = intent.getIntExtra("lid", 0);
            int ari = LidToAri.lidToAri(lid);
            if (ari != 0) {
                jumpToAri(ari);
                final IntentResult res = new IntentResult(ari);
                res.selectVerse = selectVerse;
                res.selectVerseCount = selectVerseCount;
                return res;
            } else {
//                new MaterialDialog.Builder(NewReadingActivity.this)
//                        .content("Invalid lid: " + lid)
//                        .positiveText(R.string.ok)
//                        .show();
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        if (U.equals(intent.getAction(), "yuku.alkitab.action.VIEW")) {
            handleIntent(intent, "newIntent");
            doScroll();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doScroll();
            }
        }, 100);
    }

    MVersion getVersionFromVersionId(String versionId) {
        if (versionId == null || MVersionInternal.getVersionInternalId().equals(versionId)) {
            return null; // internal is made the same as null
        }

        // let's look at yes versions
        for (MVersionDb mvDb : S.getDb().listAllVersions()) {
            if (mvDb.getVersionId().equals(versionId)) {
                if (mvDb.hasDataFile()) {
                    return mvDb;
                } else {
                    return null; // this is the one that should have been chosen, but the data file is not available, so let's fallback.
                }
            }
        }

        return null; // not known
    }

    boolean loadVersion(final MVersion mv, boolean display) {
        if (mv == null) {
            return false;
        }
        try {
            final Version version = mv.getVersion();

            if (version == null) {
                throw new RuntimeException(); // caught below
            }

            if (this.mActiveBook != null) { // we already have some other version loaded, so make the new version open the same book
                int bookId = this.mActiveBook.bookId;
                Book book = version.getBook(bookId);
                if (book != null) { // we load the new book succesfully
                    this.mActiveBook = book;
                } else { // too bad, this book was not found, get any book
                    this.mActiveBook = version.getFirstBook();
                }
            }

            S.activeVersion = version;
            S.activeVersionId = mv.getVersionId();

            mVersionTv.setText(S.getVersionInitials(version));
            mIndicatorVersion.setText(S.getVersionInitials(version));

            if (display) {
                display(mCurrentChapter, mChapterView.getVerseBasedOnScroll(), false);
            }

            EventBus.getDefault().post(new EventNewVersionLoaded(mv));

            return true;
        } catch (Throwable e) { // so we don't crash on the beginning of the app
            Log.e(TAG, "Error opening main version", e);

            new MaterialDialog.Builder(NewReadingActivity.this)
                    .content(getString(R.string.version_error_opening, mv != null ? mv.longName : "file"))
                    .positiveText(R.string.ok)
                    .show();

            return false;
        }
    }

    /**
     * Display specified chapter and verse of the active book. By default all checked verses will be unchecked.
     *
     * @return Ari that contains only chapter and verse. Book always set to 0.
     */
    int display(int chapter_1, int verse_1) {
        if (mPopularAdapter != null) {
            mPopularAdapter.loadData();
        }
        return display(chapter_1, verse_1, true);
    }

    /**
     * Display specified chapter and verse of the active book.
     *
     * @param uncheckAllVerses whether we want to always make all verses unchecked after this operation.
     * @return Ari that contains only chapter and verse. Book always set to 0.
     */
    int display(int chapter_1, int verse_1, boolean uncheckAllVerses) {
        int current_chapter_1 = this.mCurrentChapter;

        if (chapter_1 < 1) chapter_1 = 1;
        if (chapter_1 > this.mActiveBook.chapter_count) chapter_1 = this.mActiveBook.chapter_count;

        if (mActiveBook.bookId == 0 && chapter_1 == 1) {
            mLeftBtn.setVisibility(View.GONE);
        } else {
            mLeftBtn.setVisibility(View.VISIBLE);
        }
        if ((mActiveBook.bookId == S.activeVersion.getMaxBookIdPlusOne() - 1) && chapter_1 == this.mActiveBook.chapter_count) {
            mRightBtn.setVisibility(View.GONE);
        } else {
            mRightBtn.setVisibility(View.VISIBLE);
        }

        if (verse_1 < 1) verse_1 = 1;
        if (verse_1 > this.mActiveBook.verse_counts[chapter_1 - 1]) {
            verse_1 = this.mActiveBook.verse_counts[chapter_1 - 1];
        }

        { // main
            try {
                boolean ok = loadChapterToVersesView(mChapterView, S.activeVersion, S.activeVersionId, this.mActiveBook, chapter_1, current_chapter_1, uncheckAllVerses);
                if (!ok) return 0;
            } catch (Exception e) {
            }

            // tell activity
            this.mCurrentChapter = chapter_1;

            mChapterView.scrollToVerse(verse_1);
        }

        // set goto button text
        final String reference = this.mActiveBook.reference(chapter_1);
        mGotoBtn.setText(reference.replace(' ', '\u00a0'));
        mIndicatorChapter.setText(reference.replace(' ', '\u00a0'));

        return Ari.encode(0, chapter_1, verse_1);
    }

    static boolean loadChapterToVersesView(BibleChapterView chapterView, Version version, String versionId, Book book, int chapter_1, int current_chapter_1, boolean uncheckAllVerses) {
        final SingleChapterVerses verses = version.loadChapterText(book, chapter_1);
        if (verses == null) {
            return false;
        }

        //# max is set to 30 (one chapter has max of 30 blocks. Already almost impossible)
        int max = 30;
        int[] pericope_aris = new int[max];
        PericopeBlock[] pericope_blocks = new PericopeBlock[max];
        int nblock = version.loadPericope(book.bookId, chapter_1, pericope_aris, pericope_blocks, max);

        boolean retainSelectedVerses = (!uncheckAllVerses && chapter_1 == current_chapter_1);
        chapterView.setDataWithRetainSelectedVerses(retainSelectedVerses, Ari.encode(book.bookId, chapter_1, 0), pericope_aris, pericope_blocks, nblock, verses, version, versionId);

        return true;
    }

    /**
     * Jump to a given ari
     */
    void jumpToAri(final int ari) {
        if (ari == 0) return;

        final int bookId = Ari.toBook(ari);
        final Book book = S.activeVersion.getBook(bookId);

        if (book == null) {
            Log.w(TAG, "bookId=" + bookId + " not found for ari=" + ari);
            return;
        }

        this.mActiveBook = book;
        final int ari_cv = display(Ari.toChapter(ari), Ari.toVerse(ari));

        // call attention to the verse only if the displayed verse is equal to the requested verse
        if (ari == Ari.encode(this.mActiveBook.bookId, ari_cv)) {
            callAttentionForVerseToBothSplits(Ari.toVerse(ari));
        }
    }

    void callAttentionForVerseToBothSplits(final int verse_1) {
        mChapterView.callAttentionForVerse(verse_1);
    }

    private void applyPreferences() {
        // make sure S applied variables are set first
        S.calculateAppliedValuesBasedOnPreferences();

        { // apply background color, and clear window background to prevent overdraw
            // Bug and damn code: getActivity().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            final int backgroundColor = S.applied.backgroundColor;
            if (mOverlayContainer != null) {
                mOverlayContainer.setBackgroundColor(backgroundColor);
            }

            // ensure scrollbar is visible on Material devices
            if (Build.VERSION.SDK_INT >= 21) {
                final Drawable thumb;
                if (ColorUtils.calculateLuminance(backgroundColor) > 0.5) {
                    thumb = getResources().getDrawable(R.drawable.scrollbar_handle_material_for_light, null);
                } else {
                    thumb = getResources().getDrawable(R.drawable.scrollbar_handle_material_for_dark, null);
                }
                ScrollbarSetter.setVerticalThumb(mChapterView, thumb);
            }
        }

        // necessary    update更新数据
        mChapterView.updateChapterView();

        SettingsActivity.setPaddingBasedOnPreferences(mChapterView);
    }

    void reloadBothAttributeMaps() {
        if (mChapterView != null) {
            mChapterView.reloadAttributeMap();
        }
    }

    private void saveHistoryPosition() {
        if (mActiveBook == null || mChapterView == null || TextUtils.isEmpty(S.activeVersionId)) {
            return;
        }
        Preferences.hold();
        try {
            Preferences.setInt(Prefkey.lastBookId, this.mActiveBook.bookId);
            Preferences.setInt(Prefkey.lastChapter, mCurrentChapter);
            Preferences.setInt(Prefkey.lastVerse, mChapterView.getVerseBasedOnScroll());
            Preferences.setString(Prefkey.lastVersionId, S.activeVersionId);
        } finally {
            Preferences.unhold();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        saveHistoryPosition();
    }

    public void doScroll() {
        if (mChapterView != null) {
            mChapterView.doScroll();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        App.getLbm().unregisterReceiver(mReloadAttributeMapReceiver);

        if (mBottomBannerAdView != null) {
            mBottomBannerAdView.destroy();
        }
//        if (mChapterView != null) {
//            mChapterView.removeAdListener();
//        }
    }

    public boolean handleBackPressed() {
        if (mBottomViewDisplayControl != null && mBottomViewDisplayControl.isShowing()) {
            mBottomViewDisplayControl.hide();
            return true;
        }

        if (mBottomViewVersesOperation != null && mBottomViewVersesOperation.isShowing()) {
            mBottomViewVersesOperation.hide();
            return true;
        }

        if (mIsFullScreenMode) {
            showBigToolbar();
            return true;
        }

        return false;
    }

    public void onEventMainThread(EventActiveVersionChanged event) {
        loadVersion(event.mv, true);
    }

    boolean press(int keyCode) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            leftBtnClick();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            rightBtnClick();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            return handleBackPressed();
        }

        return false;
    }

    private void gotoBtnClick() {
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                startActivity(GotoActivity.createIntent(mActiveBook.bookId, mCurrentChapter, mChapterView.getVerseBasedOnScroll()));
            }
        };
        r.run();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (press(keyCode)) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        if (press(keyCode)) {
            return true;
        }
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }

    private void versionTvClick() {
        NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_SWITCH_VERSION_CLICK, "click");
        startActivity(VersionsActivity.createIntent());
    }

    private void searchBtnClick() {
        startActivity(SearchActivity.createIntent(this.mActiveBook.bookId));
    }

    private void textAppearanceBtnClick() {
        if (mBottomViewDisplayControl == null) { // not showing yet
            mBottomViewDisplayControl = new BottomViewDisplayControl(NewReadingActivity.this, mOverlayContainer, new BottomViewDisplayControl.Listener() {
                @Override
                public void onValueChanged() {
                    int i = mChapterView.getVerseBasedOnScroll();

                    applyPreferences();

                    mChapterView.scrollToVerse(i);
                    mChapterView.post(new Runnable() {
                        @Override
                        public void run() {
                            mChapterView.doScroll();
                        }
                    });
                }

                @Override
                public void onBottomViewDismiss() {
                    mBottomViewDisplayControl.hide();
//                        mBottomViewDisplayControl = null;
                }
            });
            mBottomViewDisplayControl.show();
        } else {
            mBottomViewDisplayControl.show();
        }
        UserBehaviorAnalytics.trackUserBehavior(this, AnalyticsConstants.P_BIBLEPAGE, AnalyticsConstants.B_FONTS);
    }

    private void leftBtnClick() {
        final Book currentBook = this.mActiveBook;
        if (currentBook == null) {
            return;
        }
        if (mCurrentChapter == 1) {
            // we are in the beginning of the book, so go to prev book
            int tryBookId = currentBook.bookId - 1;
            while (tryBookId >= 0) {
                Book newBook = S.activeVersion.getBook(tryBookId);
                if (newBook != null) {
                    this.mActiveBook = newBook;
                    int newChapter_1 = newBook.chapter_count; // to the last chapter
                    display(newChapter_1, 1);
                    break;
                }
                tryBookId--;
            }
            // whileelse: now is already Genesis 1. No need to do anything
        } else {
            int newChapter = mCurrentChapter - 1;
            display(newChapter, 1);
        }
        showBigToolbar();
    }

    private void rightBtnClick() {
        final Book currentBook = this.mActiveBook;
        if (currentBook == null) {
            return;
        }

        if (mCurrentChapter >= currentBook.chapter_count) {
            final int maxBookId = S.activeVersion.getMaxBookIdPlusOne();
            int tryBookId = currentBook.bookId + 1;
            while (tryBookId < maxBookId) {
                final Book newBook = S.activeVersion.getBook(tryBookId);
                if (newBook != null) {
                    this.mActiveBook = newBook;
                    display(1, 1);
                    break;
                }
                tryBookId++;
            }
            // whileelse: now is already Revelation (or the last book) at the last chapter. No need to do anything
        } else {
            int newChapter = mCurrentChapter + 1;
            display(newChapter, 1);
        }
        showBigToolbar();
    }

    private void completeBtnClick() {
        final Book currentBook = this.mActiveBook;
        if (currentBook == null) {
            return;
        }

        S.getDb().insertCompletedChapterProgress(S.activeVersionId, currentBook.bookId, mCurrentChapter);
        final String reference = currentBook.reference(mCurrentChapter);
        int completedCount = S.getDb().getBooksCompletedChapterCount(S.activeVersionId, currentBook.bookId);
        int progress = 0;
        if (currentBook.chapter_count > 0) {
            progress = completedCount * 100 / currentBook.chapter_count;
        }
        startActivity(ExitAppActivity.createIntent(NewReadingActivity.this, ExitAppActivity.TYPE_READ_CHAPTER_DONE, progress, reference));

        rightBtnClick(); // go to next chapter
    }

    final TwoFingerLinearLayout.Listener mTwoFingerOperationListener = new TwoFingerLinearLayout.Listener() {

        @Override
        public void onOneFingerLeft() {
            if (mBottomViewVersesOperation != null && mBottomViewVersesOperation.isShowing()) {
                mBottomViewVersesOperation.hide();
            }
            rightBtnClick();
        }

        @Override
        public void onOneFingerRight() {
            if (mBottomViewVersesOperation != null && mBottomViewVersesOperation.isShowing()) {
                mBottomViewVersesOperation.hide();
            }
            leftBtnClick();
        }

        @Override
        public void onTwoFingerStart() {
        }

        @Override
        public void onTwoFingerScale(final float scale) {
        }

        @Override
        public void onTwoFingerDragX(final float dx) {
        }

        @Override
        public void onTwoFingerDragY(final float dy) {
        }

        @Override
        public void onTwoFingerEnd(final TwoFingerLinearLayout.Mode mode) {
        }
    };

    final BroadcastReceiver mReloadAttributeMapReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            reloadBothAttributeMaps();
        }
    };

    BibleChapterView.SelectedVersesListener mSelectedVersesListener = new BibleChapterView.SelectedVersesListener() {
        @Override
        public void onSomeVersesSelected(BibleChapterView v) {
            mLeftBtn.setVisibility(View.GONE);
            mRightBtn.setVisibility(View.GONE);
            mRelatedBtn.setVisibility(View.GONE);
            if (mBottomViewVersesOperation == null) { // not create yet
                FrameLayout contentRoot = V.get(mOverlayContainer, R.id.content_root);
                mBottomViewVersesOperation = new BottomViewVersesOperation(NewReadingActivity.this, contentRoot, mActiveBook, mCurrentChapter, new BottomViewVersesOperation.Listener() {
                    @Override
                    public void onValueChanged() {
                        mChapterView.uncheckAllVerses(true);
                        reloadBothAttributeMaps();
                    }

                    @Override
                    public void onBottomViewDismiss() {
                        mChapterView.uncheckAllVerses(true);
                        reloadBothAttributeMaps();
                        if (mHasReachBottom == false) {
                            if (mActiveBook.bookId == 0 && mCurrentChapter == 1) {
                                mLeftBtn.setVisibility(View.GONE);
                            } else {
                                mLeftBtn.setVisibility(View.VISIBLE);
                            }
                            mRightBtn.setVisibility(View.VISIBLE);
                            mRelatedBtn.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }

            mBottomViewVersesOperation.updateBookAndChapter(mActiveBook, mCurrentChapter);
            if (!mBottomViewVersesOperation.isShowing()) {
                mBottomViewVersesOperation.show();
            }
            mBottomViewVersesOperation.updateView(mChapterView);
        }

        @Override
        public void onNoVersesSelected(BibleChapterView v) {
            if (mBottomViewVersesOperation != null && mBottomViewVersesOperation.isShowing()) {
                mBottomViewVersesOperation.hide();
            }
        }

        @Override
        public void onVerseSingleClick(BibleChapterView v, int verse_1) {
        }
    };

    private boolean mIsFullScreenMode = false;

    private void hideBigToolbar() {
        ValueAnimator animator = ValueAnimator.ofFloat(ViewHelper.getTranslationY(mToolbarLayout), -mToolbarLayout.getHeight()).setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translationY = (float) animation.getAnimatedValue();
                ViewHelper.setTranslationY(mToolbarLayout, translationY);
            }
        });
        animator.start();

        ValueAnimator animator2 = ValueAnimator.ofFloat(ViewHelper.getTranslationY(mIndicatorLayout), 0).setDuration(150);
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translationY = (float) animation.getAnimatedValue();
                ViewHelper.setTranslationY(mIndicatorLayout, translationY);
            }
        });
        animator2.setStartDelay(200);
        animator2.start();

        mIsFullScreenMode = true;
    }

    private void showBigToolbar() {
        ValueAnimator animator2 = ValueAnimator.ofFloat(ViewHelper.getTranslationY(mIndicatorLayout), -mIndicatorLayout.getHeight()).setDuration(150);
        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translationY = (float) animation.getAnimatedValue();
                ViewHelper.setTranslationY(mIndicatorLayout, translationY);
            }
        });
        animator2.start();

        ValueAnimator animator = ValueAnimator.ofFloat(ViewHelper.getTranslationY(mToolbarLayout), 0).setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translationY = (float) animation.getAnimatedValue();
                ViewHelper.setTranslationY(mToolbarLayout, translationY);
            }
        });
        animator.setStartDelay(150);
        animator.start();

        mIsFullScreenMode = false;
    }


}
