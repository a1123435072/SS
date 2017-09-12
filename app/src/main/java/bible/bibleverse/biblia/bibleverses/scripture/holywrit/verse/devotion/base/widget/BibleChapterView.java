package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.widget;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fw.basemodules.utils.OmAsyncTask;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.App;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.U;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.VersionsActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.Appearances;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.Highlights;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.VersionUtil;
import yuku.afw.V;
import yuku.alkitab.model.PericopeBlock;
import yuku.alkitab.model.SingleChapterVerses;
import yuku.alkitab.model.Version;
import yuku.alkitab.util.IntArrayList;

import static yuku.afw.App.context;

/**
 * Created by yzq on 16/12/2.
 */

public class BibleChapterView extends ObservableScrollView {

    private static final int MAX_VERSE_COUNT = 80;
    private static final String PARAGRAPH_START = "    ";
    private static final String PARAGRAPH_END = "\n";

    private int mBottomViewHeight;
    // view element
    private TextView mVerseTv;

    // # field setData
    private int mAriBC;
    private SingleChapterVerses mSingleChapterVerses;
    private Version mVersion;
    private String mVersionId;

    private int mVerseCount;
    private ArrayList<String> mVerseStrList;
    private IntArrayList mVerseStartPos;    // verse start word position  in the whole chapter string
    private IntArrayList mVerseEndPos;  // verse end word position  in the whole chapter string

    private HashSet<Integer> mParagraphStartVerseIdx;
    private HashSet<Integer> mParagraphEndVerseIdx;

    private Highlights.Info[] mHighlightInfos;
    private IntArrayList mVerse2LineNum; // verse idx => start line num, used for jump to verse
    private HashSet<Integer> mSelectedVerseIdx;

    private CommonSpanBuilder mSpanBuilder;

    // event listener
    private SelectedVersesListener mSelectVerseListener;
    private boolean mReachBottom = false;
    private OnBottomReachedListener mReachedBottomListener;

    // guide container
    LinearLayout mGuideContainer;
    TextView mVersionGuideTitle;

    // which verse is scroll to
    private int mScrollVerseNum = -1;

    public BibleChapterView(Context context) {
        super(context);
        init();
    }

    public BibleChapterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mBottomViewHeight = 2 * context.getResources().getDimensionPixelSize(R.dimen.margin_82);

        mVerseStrList = new ArrayList<>(MAX_VERSE_COUNT);
        mVerseStartPos = new IntArrayList(MAX_VERSE_COUNT);
        mVerseEndPos = new IntArrayList(MAX_VERSE_COUNT);

        mParagraphStartVerseIdx = new HashSet<>();
        mParagraphEndVerseIdx = new HashSet<>();

        mVerse2LineNum = new IntArrayList(MAX_VERSE_COUNT);
        mSelectedVerseIdx = new HashSet<>();

        mSpanBuilder = new CommonSpanBuilder();
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mVerseTv = V.get(this, R.id.verse_tv);

        // guide container
        mGuideContainer = V.get(this, R.id.version_guide);
        mVersionGuideTitle = V.get(this, R.id.title);

        initViewListener();
    }

    private void initViewListener() {
        mVerseTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final int y = getScrollY();
                performSelectVerse();
                updateChapterView();

                post(new Runnable() {
                    @Override
                    public void run() {
                        smoothScrollTo(0, y);
                    }
                });
            }
        });

        mVerseTv.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });


        TextView okBtn = V.get(this, R.id.ok_btn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = VersionsActivity.createIntent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                App.context.startActivity(intent);
                hideLanguageGuide();
//                loadAndFillPageTopAd();
            }
        });

        View closeBtn = V.get(this, R.id.close_btn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideLanguageGuide();
//                loadNextPageTopAd();
            }
        });

        View rootView = V.get(this, R.id.root_view);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = VersionsActivity.createIntent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                App.context.startActivity(intent);
                hideLanguageGuide();
//                loadAndFillPageTopAd();
            }
        });
    }

    private void performSelectVerse() {
        int start = mVerseTv.getSelectionStart();
        int end = mVerseTv.getSelectionEnd();

        int verseIdx = getVerseIdxByStrPos(start, end);
        if (verseIdx == -1) {
            return;
        }

        if (mSelectedVerseIdx.contains(verseIdx)) {
            mSelectedVerseIdx.remove(verseIdx);
        } else {
            mSelectedVerseIdx.add(verseIdx);
        }

        if (mSelectVerseListener != null) {
            if (mSelectedVerseIdx.size() > 0) {
                mSelectVerseListener.onSomeVersesSelected(BibleChapterView.this);
            } else {
                mSelectVerseListener.onNoVersesSelected(BibleChapterView.this);
            }
        }
    }

    private int getVerseIdxByStrPos(int start, int end) {
        int low = 0, high = mVerseCount - 1;
        int idx = -1;
        while (low <= high) {
            idx = (low + high) / 2;

            if (start > mVerseStartPos.get(idx) && end < mVerseEndPos.get(idx)) {
                return idx;
            } else if (start > mVerseEndPos.get(idx)) {
                low = idx + 1;
            } else if (end < mVerseStartPos.get(idx)) {
                high = idx - 1;
            } else {
                break;
            }
        }

        return -1;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        int diff = (mVerseTv.getBottom() - (getHeight() + getScrollY()));

        if (diff < -mBottomViewHeight) {
            if (!mReachBottom) {
                if (mReachedBottomListener != null) {
                    mReachedBottomListener.onBottomReached();
                }
                mReachBottom = true;
            }
        } else {
            if (mReachBottom) {
                if (mReachedBottomListener != null) {
                    mReachedBottomListener.onLeftBottom();
                }
                mReachBottom = false;
            }
        }

        super.onScrollChanged(l, t, oldl, oldt);
    }

    /**
     * @return 1-based verse
     */
    public int getVerseBasedOnScroll() {
        int y = getScrollY();
        int topLineVerseIdx = 1;

        Layout layout = mVerseTv.getLayout();
        if (layout == null) {
            return topLineVerseIdx;
        }
        for (int i = mVerseCount - 1; i >= 0; --i) {
            if (i < mVerse2LineNum.size()) {
                int lineNum = mVerse2LineNum.get(i);
                if (lineNum >= layout.getLineCount()) {
                    continue;
                }

                try {
                    if (y >= layout.getLineTop(lineNum)) {
                        topLineVerseIdx = i + 1;
                        return topLineVerseIdx;
                    }
                } catch (IndexOutOfBoundsException e) {
                    continue;
                }
            }
        }

        return topLineVerseIdx;
    }

    /**
     * This is different from {@lidnk #scrollToVerse(int, float)} in that if the requested
     * verse has a pericope header, this will scroll to the top of the pericope header,
     * not to the top of the verse.
     */
    public void scrollToVerse(final int verse_1) {
        // verse_1 : 1-based verse
        mScrollVerseNum = verse_1 > 0 ? verse_1 - 1 : 0;
    }

    public void doScroll() {
        post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mScrollVerseNum == 0) {
                        smoothScrollTo(0, 0);
                    }

                    if (mVerse2LineNum.size() != mVerseCount) {
                        synchronized (mVerse2LineNum) {
                            recordLineNum();
                        }
                    }

                    Layout layout = mVerseTv.getLayout();
                    int lineNum = mVerse2LineNum.get(mScrollVerseNum);
                    if (layout == null || lineNum >= layout.getLineCount()) {
                        return;
                    }

                    int y = layout.getLineTop(lineNum);
                    smoothScrollTo(0, y);
                } catch (IndexOutOfBoundsException e) {
                }
            }
        });
    }

    //设置数据 和 保存选择的状态
    public void setDataWithRetainSelectedVerses(boolean retainSelectedVerses, int ariBc, int[] pericope_aris, PericopeBlock[] pericope_blocks, int nblock, SingleChapterVerses verses, @NonNull Version version, @NonNull String versionId) {
        HashSet<Integer> oldSelected = null;
        if (retainSelectedVerses) {
            oldSelected = (HashSet<Integer>) mSelectedVerseIdx.clone();
        }

//        // fill ad, every time to change chapter
//        {
//            fillAdAndLoadNext();
//        }

        //# fill adapter with new data. make sure all checked states are reset
        setData(ariBc, verses, pericope_aris, pericope_blocks, nblock, version, versionId);
        if (retainSelectedVerses && oldSelected.size() > 0) {
            mSelectedVerseIdx.addAll(oldSelected);
        }
        reloadAttributeMap();  // this will update view

        post(new Runnable() {
            @Override
            public void run() {
                mVerseTv.requestFocus();
                smoothScrollTo(0, 0);
            }
        });

        if (mSelectedVerseIdx.size() > 0) {
            if (mSelectVerseListener != null) {
                mSelectVerseListener.onSomeVersesSelected(this);
            }
        }
    }

    /**
     * @param version   can be null if no text size multiplier is to be used
     * @param versionId can be null if no text size multiplier is to be used
     */
    public void setData(int ariBc, SingleChapterVerses verses, int[] pericopeAris, PericopeBlock[] pericopeBlocks, int nblock, @Nullable Version version, @Nullable String versionId) {
        mAriBC = ariBc;
        mSingleChapterVerses = verses;
        mVersion = version;
        mVersionId = versionId;

        mVerseCount = mSingleChapterVerses.getVerseCount();

        mVerseStrList.clear();
        mVerseStartPos.clear();
        mVerseEndPos.clear();

        mParagraphStartVerseIdx.clear();
        mParagraphEndVerseIdx.clear();

        mSelectedVerseIdx.clear();

        convertStringFormat();

        if (Utility.getIsShowDownloadNotice(App.context)) {
            new LoadVersionGuideView().execute();
        }

//        if (mAdInnerBannerStyle != null) {
//            mAdInnerBannerStyle.setVisibility(View.GONE);
//        }
    }

    public synchronized void reloadAttributeMap() {
        // book_ can be empty when the selected (book, chapter) is not available in this version
        if (mAriBC == 0) return;

        final int[] bookmarkCountMap;
        final int[] noteCountMap;
        final Highlights.Info[] highlightColorMap;

        final int ariBc = mAriBC;
        if (S.getDb().countMarkersForBookChapter(ariBc) > 0) {
            bookmarkCountMap = new int[mVerseCount];
            noteCountMap = new int[mVerseCount];
            highlightColorMap = new Highlights.Info[mVerseCount];

            S.getDb().putAttributes(ariBc, bookmarkCountMap, noteCountMap, highlightColorMap);
        } else {
            bookmarkCountMap = null;
            noteCountMap = null;
            highlightColorMap = null;
        }

        final int y = getScrollY();
        mHighlightInfos = highlightColorMap;
        updateChapterView();

        post(new Runnable() {
            @Override
            public void run() {
                smoothScrollTo(0, y);
            }
        });
    }

    /**
     * 转换文字格式
     */
    private void convertStringFormat() {
        // @@ = start a verse containing paragraphs or formatting 开始一个包含段落或格式的段落
        // @0 = start with indent 0 [paragraph]                     从缩进0段开始
        // @1 = start with indent 1 [paragraph]
        // @2 = start with indent 2 [paragraph]
        // @3 = start with indent 3 [paragraph]
        // @4 = start with indent 4 [paragraph]
        // @6 = start of red text [formatting]
        // @5 = end of red text   [formatting]
        // @9 = start of italic [formatting]
        // @7 = end of italic   [formatting]
        // @8 = put a blank line to the next verse [formatting]
        // @^ = start-of-paragraph marker
        // @< to @> = special tags (not visible for unsupported tags) [can be considered formatting]
        // @/ = end of special tags (closing tag) (As of 2013-10-04, all special tags must be closed) [can be considered formatting]

        for (int i = 0; i < mVerseCount; ++i) {
            String verse_1 = mSingleChapterVerses.getVerse(i);

            boolean isPStart = (i == 0); //是否是第一条数据
            if (!verse_1.startsWith("@@") || verse_1.startsWith("@@@^")) {
                isPStart = true;
            }
//            else {
//                verse_1 = verse_1.replace("@@", "");
//
//                if (verse_1.startsWith("@^")) {
//                    isPStart = true;
//                    verse_1 = verse_1.replace("@^", "");
//                }
//            }

            if (isPStart) {
                mParagraphStartVerseIdx.add(i); //i  ：mVerseCount索引
                if (i > 0) {
                    mParagraphEndVerseIdx.add(i - 1);
                }
            }
// ignore 1-9
//            verse_1 = verse_1.replace("@0", "");
//            verse_1 = verse_1.replace("@1", " ");
//            verse_1 = verse_1.replace("@2", "  ");
//            verse_1 = verse_1.replace("@3", "   ");
//            verse_1 = verse_1.replace("@4", "    ");
//            verse_1 = verse_1.replace("@5", "<font color=\"#B71C1C\">");
//            verse_1 = verse_1.replace("@6", "</font>");
//            verse_1 = verse_1.replace("@9", "<i>");
//            verse_1 = verse_1.replace("@7", "</i>");
//            verse_1 = verse_1.replace("@8", "\n\n");

            verse_1 = U.removeSpecialCodes(verse_1);//移除掉特殊的符号

            mVerseStrList.add(verse_1.trim());
        }

        mParagraphEndVerseIdx.add(mVerseCount - 1);
    }


    private void appendFormattedVerse(int i) {
        final Highlights.Info highlightInfo = mHighlightInfos == null ? null : mHighlightInfos[i];
        if (mParagraphStartVerseIdx.contains(i)) {
            mSpanBuilder.append(PARAGRAPH_START);
        }

        mVerseStartPos.add(mSpanBuilder.toString().length());

        if (highlightInfo != null) {
            mSpanBuilder.append(" " + String.valueOf(i + 1) + " ", new RelativeSizeSpan(0.6f), new ForegroundColorSpan(VerseItemView.INDEX_COLOR), new BackgroundColorSpan(Highlights.alphaMix(highlightInfo.colorRgb)));
        } else {
            mSpanBuilder.append(" " + String.valueOf(i + 1) + " ", new RelativeSizeSpan(0.6f), new ForegroundColorSpan(VerseItemView.INDEX_COLOR));
        }

        if (mSelectedVerseIdx.contains(i)) {
            mSpanBuilder.append(mVerseStrList.get(i), new UnderlineSpan());
        } else if (mSelectedVerseIdx.size() == 0 && highlightInfo != null) {
            mSpanBuilder.append(mVerseStrList.get(i), new BackgroundColorSpan(Highlights.alphaMix(highlightInfo.colorRgb)));
        } else {
            mSpanBuilder.append(mVerseStrList.get(i));
        }

        mVerseEndPos.add(mSpanBuilder.toString().length());

        if (mParagraphEndVerseIdx.contains(i)) {
            mSpanBuilder.append(PARAGRAPH_END);
        }
    }

    public void updateChapterView() {
        mSpanBuilder.clear();
        mVerseStartPos.clear();
        mVerseEndPos.clear();
        for (int i = 0; i < mVerseCount; ++i) {
            appendFormattedVerse(i);
        }

        Appearances.applyTextAppearance(mVerseTv, 1.0f);
        mVerseTv.setText(mSpanBuilder);

        synchronized (mVerse2LineNum) {
            recordLineNum();
        }
    }

    private void recordLineNum() {
        post(new Runnable() {
            @Override
            public void run() {
                Layout layout = mVerseTv.getLayout();

                if (layout != null) {
                    mVerse2LineNum.clear();
                    int lineNum = 0;
                    // consider: 1 line has 2 verses starts
                    for (int verseIdx = 0; verseIdx < mVerseCount; ++verseIdx) {
                        if (mVerseStartPos.size() <= verseIdx) {
                            break;
                        }

                        if (lineNum < layout.getLineCount()) {
                            int lineEndPos = layout.getLineEnd(lineNum);
                            while (lineEndPos < mVerseStartPos.get(verseIdx)) {
                                lineNum++;
                                if (lineNum < layout.getLineCount()) {
                                    lineEndPos = layout.getLineEnd(lineNum);
                                } else {
                                    break;
                                }
                            }

                            if (lineEndPos >= mVerseStartPos.get(verseIdx)) {
                                mVerse2LineNum.add(lineNum);
                            } else {
                                mVerse2LineNum.add(-1);
                            }
                        } else {
                            mVerse2LineNum.add(-1);
                        }
                    }
                }
            }
        });
    }

    public IntArrayList getSelectedVerses_1() {
        IntArrayList selected = new IntArrayList();
        for (int i : mSelectedVerseIdx) {
            selected.add(i + 1); // verse idx is from 1, only in this view, it is start from 0 as in array
        }

        selected.sort();
        return selected;
    }

    public String getVerseText(int verseIdx) {
        if (mVerseStrList != null && verseIdx <= mVerseStrList.size()) {
            return mVerseStrList.get(verseIdx - 1);
        }
        if (verseIdx <= mSingleChapterVerses.getVerseCount()) {
            return U.removeSpecialCodes(mSingleChapterVerses.getVerse(verseIdx - 1));
        }
        return "";
    }

    public void uncheckAllVerses(boolean callListener) {
        mSelectedVerseIdx.clear();
        updateChapterView();

        if (callListener) {
            if (mSelectVerseListener != null) {
                mSelectVerseListener.onNoVersesSelected(this);
            }
        }
    }

    public void callAttentionForVerse(final int verse_1) {
        // do nothing for now
    }

    public void setSelectVerseListener(SelectedVersesListener selectVerseListener) {
        mSelectVerseListener = selectVerseListener;
    }

    public interface SelectedVersesListener {
        void onSomeVersesSelected(BibleChapterView v);

        void onNoVersesSelected(BibleChapterView v);

        void onVerseSingleClick(BibleChapterView v, int verse_1);
    }

    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {
        mReachedBottomListener = onBottomReachedListener;
    }

    public interface OnBottomReachedListener {
        void onBottomReached();

        void onLeftBottom();
    }

    protected Locale mLocale;
    protected int mLocaleVersionCount;

    private boolean mIsShowDownloadGuide = false;

    private class LoadVersionGuideView extends OmAsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mLocaleVersionCount < 1) {
                mLocale = Locale.getDefault();
                mLocaleVersionCount = VersionUtil.getAllVersionsByLanguage(mLocale).size();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (Utility.getIsShowDownloadNotice(App.context)) {
                mGuideContainer.setVisibility(VISIBLE);
                mVersionGuideTitle.setText(App.context.getString(R.string.download_version_language, mLocale.getLanguage().equals("en") ? "English" : mLocale.getDisplayLanguage(), String.valueOf(mLocaleVersionCount)));
                mIsShowDownloadGuide = true;
//                if (mAdInnerBannerStyle != null) {
//                    mAdInnerBannerStyle.setVisibility(GONE);
//                }
            } else {
                hideLanguageGuide();
            }
        }
    }

    protected void hideLanguageGuide() {
        if (mGuideContainer != null && mGuideContainer.getVisibility() == View.VISIBLE) {
            mGuideContainer.setVisibility(View.GONE);
            Utility.setIsShowDownloadNotice(App.context, false);
        }
        mIsShowDownloadGuide = false;
    }

}
