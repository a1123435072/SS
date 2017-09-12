package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fw.basemodules.utils.OmAsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.SelfAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.UserBehaviorAnalytics;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.App;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.U;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.MVersionInternal;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.storage.Prefkey;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.Appearances;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.Jumper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.QueryTokenizer;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.SearchEngine;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import yuku.afw.V;
import yuku.afw.storage.Preferences;
import yuku.afw.widget.EasyAdapter;

import yuku.alkitab.model.Book;
import yuku.alkitab.model.Version;
import yuku.alkitab.util.Ari;
import yuku.alkitab.util.IntArrayList;
import yuku.alkitabintegration.display.Launcher;

import static bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.Literals.Array;

public class SearchActivity extends BaseActivity {
    public static final String TAG = SearchActivity.class.getSimpleName();

    private static final String EXTRA_openedBookId = "openedBookId";

    private static final long ID_CLEAR_HISTORY = -1L;
    private static final int COL_INDEX_ID = 0;
    private static final int COL_INDEX_QUERY_STRING = 1;

    LinearLayout mSearchResultsLayout;
    LinearLayout mSearchCandidateLayout;
    ListView mSearchResultsLv;
    ListView mSearchCandidatesLv;
    LinearLayout mSearchLoadingLayout;
    LinearLayout mEmptyResultLayout;
    TextView mEmptyTipTv;

    //custom search
    private EditText mEditText;
    private ImageView mClearKeywordBtn;
    private ImageView mSearchBtn;

    int mHighLightColor;
    float mTextSizeMult;

    SparseBooleanArray selectedBookIds = new SparseBooleanArray();
    SearchResultsAdapter mSearchResultAdapter;
    SearchCandidateAdapter mSearchCandidateAdapter;

    Version searchInVersion;
    String searchInVersionId;

    public static Intent createIntent(int openedBookId) {
        Intent res = new Intent(App.context, SearchActivity.class);
        res.putExtra(EXTRA_openedBookId, openedBookId);
        return res;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mSearchResultsLv = V.get(this, R.id.search_result_list);
        mSearchResultsLayout = V.get(this, R.id.search_results_layout);
        mSearchCandidateLayout = V.get(this, R.id.search_candidate_layout);
        mSearchCandidatesLv = V.get(this, R.id.search_candidate_list);
        mSearchLoadingLayout = V.get(this, R.id.search_loading);
        mEmptyTipTv = V.get(this, R.id.empty_tip);
        mEmptyResultLayout = V.get(this, R.id.empty_layout);

        final Toolbar toolbar = V.get(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);
        ab.setHomeAsUpIndicator(R.drawable.ic_back_black);

        mEditText = V.get(this, R.id.app_search_edit);
        mClearKeywordBtn = V.get(this, R.id.app_search_delete);
        mSearchBtn = V.get(this, R.id.app_search_submit);

        initSearchInputView();
        initSearchCandidateView();
        initSearchResultView();

        searchInVersion = S.activeVersion;
        searchInVersionId = S.activeVersionId;
        for (final Book book : searchInVersion.getConsecutiveBooks()) {
            selectedBookIds.put(book.bookId, true);
        }
        mTextSizeMult = S.getDb().getPerVersionSettings(searchInVersionId).fontSizeMultiplier;
        mHighLightColor = U.getSearchKeywordTextColorByBrightness(S.applied.backgroundBrightness);

        if (usingRevIndex()) {
            SearchEngine.preloadRevIndex();
        }
        new LoadBooksNameAsCandidate().execute();
    }

    private void initSearchInputView() {
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mSearchCandidateLayout.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(s)) {
                    mClearKeywordBtn.setVisibility(View.VISIBLE);
                    //filterCandidates history
                    if (mSearchCandidateAdapter != null) {
                        mSearchCandidateAdapter.setQuery(s.toString());
                        mSearchCandidateAdapter.notifyDataSetChanged();
                    }
                } else {
                    mClearKeywordBtn.setVisibility(View.INVISIBLE);
                }
            }
        });

        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mSearchCandidateLayout.setVisibility(View.VISIBLE);
                    if (mSearchCandidateAdapter == null) {
                        mSearchCandidateAdapter = new SearchCandidateAdapter();
                        mSearchCandidateAdapter.setData(loadSearchCandidates());
                        mSearchCandidatesLv.setAdapter(mSearchCandidateAdapter);
                    } else {
                        mSearchCandidateAdapter.setData(loadSearchCandidates());
                        mSearchCandidateAdapter.notifyDataSetChanged();
                    }
                } else {
                    mSearchCandidateLayout.setVisibility(View.GONE);
                }
            }
        });

        mEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (mEditText != null) {
                        String keyword = mEditText.getText().toString();
                        search(keyword);
                        Utility.hideSoftKey(SearchActivity.this);
                    }
                    return true;
                }
                return false;
            }
        });

        mClearKeywordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText.setText("");
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(mEditText.getText().toString());
            }
        });
    }

    private void initSearchCandidateView() {
        mSearchCandidateLayout.setVisibility(View.VISIBLE);
        mSearchCandidatesLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Cursor c = mSearchCandidateAdapter.getCursor();
                if (c == null) return;

                final boolean ok = c.moveToPosition(position);
                if (!ok) return;


                final long _id = c.getLong(COL_INDEX_ID);
                if (_id == ID_CLEAR_HISTORY) {
                    saveSearchHistory(null);
                    mSearchCandidateAdapter.setData(loadSearchCandidates());
                } else {
                    String searchHistory = c.getString(COL_INDEX_QUERY_STRING);
                    mEditText.setText(searchHistory);
                    search(searchHistory);
                }
            }
        });
    }

    private void initSearchResultView() {
        SpannableStringBuilder sb = new SpannableStringBuilder(mEmptyTipTv.getText());
        while (true) {
            final int pos = TextUtils.indexOf(sb, "[q]");
            if (pos < 0) break;
            sb.replace(pos, pos + 3, "\"");
        }
        mEmptyTipTv.setText(sb);

        mSearchResultsLv.setBackgroundColor(S.applied.backgroundColor);
        mSearchResultsLv.setCacheColorHint(S.applied.backgroundColor);
        mSearchResultsLv.setEmptyView(mEmptyResultLayout);

        mSearchResultsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int ari = mSearchResultAdapter.getSearchResults().get(position);
                startActivity(Launcher.openAppAtBibleLocationWithVerseSelected(ari));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mSearchCandidateAdapter != null) {
            mSearchCandidateAdapter.setData(loadSearchCandidates());
        }
    }

    protected SearchEngine.Query getQuery() {
        SearchEngine.Query res = new SearchEngine.Query();
        res.query_string = mEditText.getText().toString();
        res.bookIds = selectedBookIds;
        return res;
    }

    protected void search(final String query_string) {
        mSearchResultsLayout.setVisibility(View.VISIBLE);
        mSearchCandidateLayout.setVisibility(View.GONE);
        if (query_string.trim().length() == 0) {
            return;
        }

        final String[] tokens = QueryTokenizer.tokenize(query_string);

        mSearchLoadingLayout.setVisibility(View.VISIBLE);

        new SearchTask(query_string, tokens).execute();

        SelfAnalyticsHelper.sendSearchAnalytics(SearchActivity.this, query_string);
        UserBehaviorAnalytics.trackUserBehavior(this, AnalyticsConstants.P_SEARCHPAGE, AnalyticsConstants.B_SEARCH);
    }

    @NonNull
    Set<String> loadSearchCandidates() {
        Set<String> candidates = new HashSet<>();
        try {
            final Set<String> history = Preferences.getStringSet(Prefkey.searchHistory);
            if (history != null && history.size() > 0) {
                candidates.addAll(history);
            }
        } catch (ClassCastException e) {
            saveSearchHistory(null);
        }

        if (mBooksNameList != null && mBooksNameList.size() > 0) {
            candidates.addAll(mBooksNameList);
        }
        return candidates;
    }

    void saveSearchHistory(@Nullable Set<String> sh) {
        if (sh == null) {
            Preferences.remove(Prefkey.searchHistory);
        } else {
            Preferences.setStringSet(Prefkey.searchHistory, sh);
        }
    }

    // returns the modified SearchHistory
    Set<String> addSearchHistoryEntry(final String query_string) {
        final Set<String> sh = loadSearchCandidates();
        sh.add(query_string);

        // if more than MAX, remove last
        while (sh.size() > 120) {
            sh.remove(mBooksNameList);
        }
        saveSearchHistory(sh);
        return sh;
    }

    boolean usingRevIndex() {
        return searchInVersionId == null || searchInVersionId.equals(MVersionInternal.getVersionInternalId());
    }

    private List<String> mBooksNameList = null;
    private List<Book> mActiveVersionBooks;

    private class LoadBooksNameAsCandidate extends OmAsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            List<String> list = new ArrayList<>();
            Book[] booksc = S.activeVersion.getConsecutiveBooks();
            mActiveVersionBooks = Arrays.asList(booksc.clone());
            for (int i = 0; i < booksc.length; ++i) {
                list.add(booksc[i].shortName);
            }

            return list;
        }

        @Override
        protected void onPostExecute(List<String> entries) {
            mBooksNameList = entries;
            if (mSearchCandidateAdapter != null) {
                mSearchCandidateAdapter.entries.addAll(mBooksNameList);
                mSearchCandidateAdapter.filterCandidates();
                mSearchCandidatesLv.setVisibility(View.VISIBLE);
            }
        }
    }

    private class SearchCandidateAdapter extends CursorAdapter {
        List<String> entries = new ArrayList<>();
        String query_string;

        public SearchCandidateAdapter() {
            super(App.context, null, 0);
        }

        @Override
        public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
            return getLayoutInflater().inflate(R.layout.search_history_item_layout, parent, false);
        }

        @Override
        public void bindView(final View view, final Context context, final Cursor cursor) {
            final TextView historyTxt = V.get(view, R.id.history_txt);
            final ImageView icon = V.get(view, R.id.icon);
            final long _id = cursor.getLong(COL_INDEX_ID);

            final CharSequence text;
            if (_id == -1) {
                final SpannableStringBuilder sb = new SpannableStringBuilder(getString(R.string.search_clear_history));
                sb.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.theme_color_accent)), 0, sb.length(), 0);
                text = sb;
                icon.setVisibility(View.INVISIBLE);
            } else {
                text = cursor.getString(COL_INDEX_QUERY_STRING);
                icon.setVisibility(View.VISIBLE);
            }

            historyTxt.setText(text);
        }

        @Override
        public CharSequence convertToString(final Cursor cursor) {
            return cursor.getString(COL_INDEX_QUERY_STRING);
        }

        public void setData(@NonNull final Set<String> searchHistory) {
            entries.clear();
            entries.addAll(searchHistory);
            filterCandidates();
        }

        public void setQuery(final String query_string) {
            this.query_string = query_string;
            filterCandidates();
        }

        private void filterCandidates() {
            final MatrixCursor mc = new MatrixCursor(Array("_id", "query_string") /* Can be any string, but this must correspond to COL_INDEX_ID and COL_INDEX_QUERY_STRING */);
            for (int i = 0; i < entries.size(); i++) {
                final String entry = entries.get(i);
                if (TextUtils.isEmpty(entry)) {
                    continue;
                }
                if (TextUtils.isEmpty(query_string) || entry.toLowerCase().startsWith(query_string.toLowerCase())) {
                    mc.addRow(Array((long) i, entry));
                }
            }

            // add last item to clear search history only if there is something else
            if (mc.getCount() > 0) {
                mc.addRow(Array(ID_CLEAR_HISTORY, ""));
            }

            // sometimes this is called from bg. So we need to make sure this is run on UI thread.
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    swapCursor(mc);
                }
            });
        }
    }

    class SearchTask extends OmAsyncTask<Void, Void, IntArrayList> {
        String query_string;
        String[] tokens;

        boolean debugstats_revIndexUsed;
        long debugstats_totalTimeMs;
        long debugstats_cpuTimeMs;

        int bookResultCount;

        public SearchTask(String query_string, String[] tokens) {
            this.query_string = query_string;
            this.bookResultCount = 0;
            this.tokens = tokens;
        }

        @Override
        protected IntArrayList doInBackground(Void... params) {
            mSearchCandidateAdapter.setData(addSearchHistoryEntry(query_string));

            final long totalMs = System.currentTimeMillis();
            final long cpuMs = SystemClock.currentThreadTimeMillis();
            final IntArrayList verseResult;

            synchronized (SearchActivity.this) {
                if (usingRevIndex()) {
                    debugstats_revIndexUsed = true;
                    verseResult = SearchEngine.searchByRevIndex(searchInVersion, getQuery());
                } else {
                    debugstats_revIndexUsed = false;
                    verseResult = SearchEngine.searchByGrep(searchInVersion, getQuery());
                }
            }

            List<Book> bookResult = new ArrayList<>();
            if (mActiveVersionBooks != null && mActiveVersionBooks.size() > 0) {
                for (Book book : mActiveVersionBooks) {
                    if (book.shortName.contains(query_string) || book.shortName.toLowerCase().contains(query_string)) {
                        bookResult.add(book);
                    }
                }
            }
            bookResultCount = bookResult.size();

            final IntArrayList res = new IntArrayList(bookResultCount + verseResult.size());
            for (Book book : bookResult) {
                res.add(Ari.encode(book.bookId, 1, 1));
            }
            for (int i = 0; i < verseResult.size(); ++i) {
                res.add(verseResult.get(i));
            }

            debugstats_totalTimeMs = System.currentTimeMillis() - totalMs;
            debugstats_cpuTimeMs = SystemClock.currentThreadTimeMillis() - cpuMs;

            return res;
        }

        @Override
        protected void onPostExecute(IntArrayList result) {
            if (result == null) {
                result = new IntArrayList(); // empty result
            }

            mSearchResultsLv.setAdapter(mSearchResultAdapter = new SearchResultsAdapter(result, bookResultCount, tokens));

            if (result.size() > 0) {
//                    Snackbar.make(mSearchResultsLv, getString(R.string.size_hasil, result.size()), Snackbar.LENGTH_LONG).show();

                //# close soft keyboard
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(mEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                mEditText.clearFocus();
                mSearchResultsLv.requestFocus();
                mEmptyResultLayout.setVisibility(View.GONE);
                mSearchResultsLv.setVisibility(View.VISIBLE);
            } else {
                final Jumper jumper = new Jumper(query_string);
                CharSequence noresult = getText(R.string.search_no_result);
                noresult = TextUtils.expandTemplate(noresult, query_string);
                final int fallbackAri = shouldShowFallback(jumper);

                if (fallbackAri != 0) {
                    final SpannableStringBuilder sb = new SpannableStringBuilder();
                    sb.append(noresult);
                    sb.append("\n\n");

                    CharSequence fallback = getText(R.string.search_no_result_fallback);
                    fallback = TextUtils.expandTemplate(fallback, searchInVersion.reference(fallbackAri));
                    sb.append(fallback);

                    mEmptyTipTv.setText(sb);
                    mEmptyTipTv.setOnClickListener(new View.OnClickListener() {
                                                       @Override
                                                       public void onClick(View v) {
                                                           if (Ari.toVerse(fallbackAri) == 0) {
                                                               startActivity(Launcher.openAppAtBibleLocation(fallbackAri));
                                                           } else {
                                                               startActivity(Launcher.openAppAtBibleLocationWithVerseSelected(fallbackAri));
                                                           }
                                                       }
                                                   }
                    );
                } else {
                    mEmptyTipTv.setText(noresult);
                    mEmptyTipTv.setClickable(false);
                    mEmptyTipTv.setOnClickListener(null);
                }

                mSearchResultsLv.setVisibility(View.GONE);
                mEmptyResultLayout.setVisibility(View.VISIBLE);
            }

            mSearchLoadingLayout.setVisibility(View.GONE);
        }

        /**
         * @return ari not 0 if fallback is to be shown
         */

        int shouldShowFallback(final Jumper jumper) {
            if (!jumper.getParseSucceeded()) {
                return 0;
            }

            final int chapter_1 = jumper.getChapter();
            if (chapter_1 == 0) return 0;

            final Version version = searchInVersion;

            final int bookId = jumper.getBookId(version.getConsecutiveBooks());
            if (bookId == -1) return 0;

            final Book book = version.getBook(bookId);
            if (book == null) return 0;

            if (chapter_1 > book.chapter_count) return 0;

            final int verse_1 = jumper.getVerse();
            if (verse_1 != 0 && verse_1 > book.verse_counts[chapter_1 - 1]) return 0;

            return Ari.encode(bookId, chapter_1, verse_1);
        }
    }

    class SearchResultsAdapter extends EasyAdapter {
        final int bookResultsCount;
        final IntArrayList searchResults;
        final SearchEngine.ReadyTokens rt;

        public SearchResultsAdapter(IntArrayList searchResults, int bookResultsCount, String[] tokens) {
            this.searchResults = searchResults;
            this.bookResultsCount = bookResultsCount;
            this.rt = tokens == null ? null : new SearchEngine.ReadyTokens(tokens);
        }

        @Override
        public int getCount() {
            return searchResults.size();
        }

        @Override
        public View newView(int position, ViewGroup parent) {
            return getLayoutInflater().inflate(R.layout.item_search_result, parent, false);
        }

        @Override
        public void bindView(View view, int position, ViewGroup parent) {

            final TextView lReference = V.get(view, R.id.lReference);
            final TextView lSnippet = V.get(view, R.id.lSnippet);

            final int ari = searchResults.get(position);

            if (position < bookResultsCount) {
                final SpannableStringBuilder sb = new SpannableStringBuilder(searchInVersion.reference(ari));
                Appearances.applySearchResultReferenceAppearance(lReference,
                        SearchEngine.hilite(sb, rt, /*checked ? checkedTextColor :*/ mHighLightColor),
                        mTextSizeMult);

                Appearances.applyTextAppearance(lSnippet, mTextSizeMult);
                final String verseText = U.removeSpecialCodes(searchInVersion.loadVerseText(ari));
                if (verseText != null) {
                    lSnippet.setText(verseText);
                } else {
                    lSnippet.setText(R.string.generic_verse_not_available_in_this_version);
                }
            } else {
                final SpannableStringBuilder sb = new SpannableStringBuilder(searchInVersion.reference(ari));
                Appearances.applySearchResultReferenceAppearance(lReference, sb, mTextSizeMult);

                Appearances.applyTextAppearance(lSnippet, mTextSizeMult);

                final String verseText = U.removeSpecialCodes(searchInVersion.loadVerseText(ari));
                if (verseText != null) {
                    lSnippet.setText(SearchEngine.hilite(verseText, rt, /*checked ? checkedTextColor :*/ mHighLightColor));
                } else {
                    lSnippet.setText(R.string.generic_verse_not_available_in_this_version);
                }
            }
        }

        IntArrayList getSearchResults() {
            return searchResults;
        }
    }
}
