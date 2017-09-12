package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.NewAnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.NewAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.UserBehaviorAnalytics;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.App;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.U;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventEditNoteDone;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventVerseOperate;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.storage.Db;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.DateTimeUtil;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.Debouncer;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.Highlights;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.QueryTokenizer;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.SearchEngine;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import de.greenrobot.event.EventBus;
import yuku.afw.V;
import yuku.afw.widget.EasyAdapter;
import yuku.alkitab.model.Marker;
import yuku.alkitab.model.Version;
import yuku.alkitabintegration.display.Launcher;


public class MarkerListActivity extends BaseActivity {
    public static final String TAG = MarkerListActivity.class.getSimpleName();

    private static final int REQCODE_edit_note = 1;

    // in
    private static final String EXTRA_filter_kind = "filter_kind";
    private static final String EXTRA_filter_labelId = "filter_labelId";

    /**
     * Action to broadcast when marker list needs to be reloaded due to some background changes
     */
    public static final String ACTION_RELOAD = MarkerListActivity.class.getName() + ".action.RELOAD";

    View root;
    View empty;
    View bClearFilter;
    View mProgressLayout;
    //    SearchView searchView;
    SwipeRefreshLayout refreshLayout;
    ListView lv;
    View emptyView;
    TextView tEmpty;
    TextView msgEmpty;
    ImageView icEmpty;
    private PopupWindow mPopupWindow;
    MarkerListAdapter adapter;

    String sort_column;
    boolean sort_ascending;
    @IdRes
    int sort_columnId;
    String currentlyUsedFilter;

    List<Marker> allMarkers;
    Marker.Kind filter_kind;
    long filter_labelId;

    int hiliteColor;
    Version version = S.activeVersion;
    String versionId = S.activeVersionId;
    float textSizeMult = S.getDb().getPerVersionSettings(versionId).fontSizeMultiplier;

    private int noteTextPaddingLeft, noteTextPaddingTop;

    public static Intent createIntent(Context context, Marker.Kind filter_kind, long filter_labelId) {
        Intent res = new Intent(context, MarkerListActivity.class);
        res.putExtra(EXTRA_filter_kind, filter_kind.code);
        res.putExtra(EXTRA_filter_labelId, filter_labelId);
        return res;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_list);

        final Toolbar toolbar = V.get(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_back_black);


        root = V.get(this, R.id.root);
        empty = V.get(this, android.R.id.empty);
        tEmpty = V.get(this, R.id.tEmpty);
        msgEmpty = V.get(this, R.id.msgEmpty);
        icEmpty = V.get(this, R.id.empty_icon);
        bClearFilter = V.get(this, R.id.bClearFilter);
        mProgressLayout = V.get(this, R.id.loading_layout);
        refreshLayout = V.get(this, R.id.refreshLayout);
        lv = V.get(this, android.R.id.list);
        emptyView = V.get(this, android.R.id.empty);

        tEmpty.setVisibility(View.GONE);
        msgEmpty.setVisibility(View.GONE);
        icEmpty.setVisibility(View.GONE);
        filter_kind = Marker.Kind.fromCode(getIntent().getIntExtra(EXTRA_filter_kind, 0));
        filter_labelId = getIntent().getLongExtra(EXTRA_filter_labelId, 0);

//        bClearFilter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                searchView.setQuery("", true);
//            }
//        });

        setTitleAndNothingText();

        // default sort ...
        sort_column = Db.Marker.createTime;
        sort_ascending = false;

        initVar();

        adapter = new MarkerListAdapter();
        lv.setAdapter(adapter);
        lv.setCacheColorHint(S.applied.backgroundColor);
        lv.setOnItemClickListener(lv_itemClick);
        lv.setEmptyView(emptyView);
        refreshLayout.setColorSchemeResources(R.color.theme_color_accent);
        refreshLayout.setOnRefreshListener(refreshListener);
        EventBus.getDefault().register(this);

        App.getLbm().registerReceiver(br, new IntentFilter(ACTION_RELOAD));
    }

    SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refreshLayout.setRefreshing(false);
        }
    };

    private void initVar() {
        noteTextPaddingLeft = getResources().getDimensionPixelSize(R.dimen.margin_10);
        noteTextPaddingTop = getResources().getDimensionPixelSize(R.dimen.margin_12);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        App.getLbm().unregisterReceiver(br);
    }

    BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (ACTION_RELOAD.equals(intent.getAction())) {
                loadAndFilter();
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

//        { // apply background color, and clear window background to prevent overdraw
//            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//            root.setBackgroundColor(S.applied.backgroundColor);
//        }

//        tEmpty.setTextColor(S.applied.fontColor);

        hiliteColor = U.getSearchKeywordTextColorByBrightness(S.applied.backgroundBrightness);

        loadAndFilter();
    }

    void loadAndFilter() {
        loadAndFilter(false);
    }

    void loadAndFilter(final boolean immediate) {
        allMarkers = S.getDb().listMarkers(filter_kind, filter_labelId, sort_column, sort_ascending);
        if (immediate) {
            filter.submit(currentlyUsedFilter, 0);
        } else {
            filter.submit(currentlyUsedFilter);
        }
    }

    void setTitleAndNothingText() {
        String title = null;
        String nothingText = null;
        String nothingTitle = null;
        int notingIcon = 0;

        // set title based on filter
        if (filter_kind == Marker.Kind.note) {
            title = getString(R.string.bmcat_notes);
            nothingTitle = getString(R.string.empty_notes_title);
            nothingText = getString(R.string.empty_notes_msg);
            notingIcon = R.drawable.ic_empty_notes;
//            nothingText = getString(R.string.bl_no_notes_written_yet);
        } else if (filter_kind == Marker.Kind.highlight) {
            title = getString(R.string.bmcat_highlights);
            nothingTitle = getString(R.string.empty_highlights_title);
            nothingText = getString(R.string.empty_highlights_msg);
            notingIcon = R.drawable.ic_empty_highlights;
//            nothingText = getString(R.string.bl_no_highlighted_verses);
        } else if (filter_kind == Marker.Kind.bookmark) {
            title = getString(R.string.bmcat_all_bookmarks);
            nothingTitle = getString(R.string.empty_bookmarks_title);
            nothingText = getString(R.string.empty_bookmarks_msg);
            notingIcon = R.drawable.ic_empty_bookmark;
//            if (filter_labelId == 0) {
//                title = getString(R.string.bmcat_all_bookmarks);
//                nothingText = getString(R.string.belum_ada_pembatas_buku);
//            } else if (filter_labelId == LABELID_noLabel) {
//                title = getString(R.string.bmcat_unlabeled_bookmarks);
//                nothingText = getString(R.string.bl_there_are_no_bookmarks_without_any_labels);
//            } else {
//                Label label = S.getDb().getLabelById(filter_labelId);
//                if (label != null) {
//                    title = label.title;
//                    nothingText = getString(R.string.bl_there_are_no_bookmarks_with_the_label_label, label.title);
//                }
//            }
        }

        // if we're using text filter (as opposed to kind filter), we use a different nothingText
//        search filter
//        if (currentlyUsedFilter != null) {
//            nothingText = getString(R.string.bl_no_items_match_the_filter_above);
//            bClearFilter.setVisibility(View.VISIBLE);
//        } else {
//            bClearFilter.setVisibility(View.GONE);
//        }

        if (title != null) {
            setTitle(title);
            tEmpty.setText(nothingTitle);
            msgEmpty.setText(nothingText);
            icEmpty.setImageResource(notingIcon);
        } else {
            finish(); // shouldn't happen
        }
    }

    static class FilterResult {
        String query;
        boolean needFilter;
        List<Marker> filteredMarkers;
        SearchEngine.ReadyTokens rt;
    }

    final Debouncer<String, FilterResult> filter = new Debouncer<String, FilterResult>(200) {
        @Override
        public FilterResult process(@Nullable final String payload) {
            final boolean needFilter;

            final String query = payload == null ? "" : payload.trim();
            if (query.length() == 0) {
                needFilter = false;
            } else {
                final String[] tokens = QueryTokenizer.tokenize(query);
                if (tokens.length == 0) {
                    needFilter = false;
                } else {
                    needFilter = true;
                }
            }

            final String[] tokens;
            if (query.length() == 0) {
                tokens = null;
            } else {
                tokens = QueryTokenizer.tokenize(query);
            }

            final SearchEngine.ReadyTokens rt = tokens == null || tokens.length == 0 ? null : new SearchEngine.ReadyTokens(tokens);

            final List<Marker> filteredMarkers = filterEngine(version, allMarkers, filter_kind, rt);

            final FilterResult res = new FilterResult();
            res.query = query;
            res.needFilter = needFilter;
            res.filteredMarkers = filteredMarkers;
            res.rt = rt;
            return res;
        }

        @Override
        public void onResult(final FilterResult result) {
            if (result.needFilter) {
                currentlyUsedFilter = result.query;
            } else {
                currentlyUsedFilter = null;
            }

            setTitleAndNothingText();
            adapter.setData(result.filteredMarkers, result.rt);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        return true;
    }

    public void onEventMainThread(EventEditNoteDone event) {
        if (event == null) {
            return;
        }
        loadAndFilter();
        App.getLbm().sendBroadcast(new Intent(Constants.ACTION_ATTRIBUTE_MAP_CHANGED));
    }

    final AdapterView.OnItemClickListener lv_itemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Marker marker = adapter.getItem(position);
            if (filter_kind == Marker.Kind.note) {
                startActivity(NoteActivity.createEditExistingIntent(marker._id, NoteActivity.TYPE_NOTE_EDIT));
            } else {
                startActivity(Launcher.openAppAtBibleLocationWithVerseSelected(marker.ari, marker.verseCount));
            }
        }
    };

    /**
     * The real work of filtering happens here.
     *
     * @param rt Tokens have to be already lowercased.
     */
    public static List<Marker> filterEngine(Version version, List<Marker> allMarkers, Marker.Kind filter_kind, @Nullable SearchEngine.ReadyTokens rt) {
        final List<Marker> res = new ArrayList<>();

        if (rt == null) {
            res.addAll(allMarkers);
            return res;
        }

        for (final Marker marker : allMarkers) {
            if (filter_kind != Marker.Kind.highlight) { // "caption" in highlights only stores color information, so it's useless to check
                String caption_lc = marker.caption.toLowerCase(Locale.getDefault());
                if (SearchEngine.satisfiesTokens(caption_lc, rt)) {
                    res.add(marker);
                    continue;
                }
            }

            // try the verse text!
            String verseText = U.removeSpecialCodes(version.loadVerseText(marker.ari));
            if (verseText != null) { // this can be null! so beware.
                String verseText_lc = verseText.toLowerCase(Locale.getDefault());
                if (SearchEngine.satisfiesTokens(verseText_lc, rt)) {
                    res.add(marker);
                }
            }
        }

        return res;
    }

    class MarkerListAdapter extends EasyAdapter {
        List<Marker> filteredMarkers = new ArrayList<>();
        SearchEngine.ReadyTokens rt;

        @Override
        public Marker getItem(final int position) {
            return filteredMarkers.get(position);
        }

        @Override
        public View newView(final int position, final ViewGroup parent) {
            return getLayoutInflater().inflate(R.layout.item_marker, parent, false);
        }

        @Override
        public void bindView(final View view, final int position, final ViewGroup parent) {
            final TextView lDate = V.get(view, R.id.lDate);
            final TextView lCaption = V.get(view, R.id.lCaption);
            final TextView lSnippet = V.get(view, R.id.lSnippet);
            final ImageView icon = V.get(view, R.id.icon);
            final View iconHighLight = V.get(view, R.id.highlight_color);
            final LinearLayout moreAction = V.get(view, R.id.action_more);

            moreAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopUpWindow(position, V.get(v, R.id.action_more));
                }
            });
            final Marker marker = getItem(position);
            final Date createTime = marker.createTime;
            final Date modifyTime = marker.modifyTime;
            final String createTimeDisplay = DateTimeUtil.getLocaleDateStr4Display(createTime);

            if (createTime.equals(modifyTime)) {
                lDate.setText(createTimeDisplay);
            } else {
                final String modifyTimeDisplay = DateTimeUtil.getLocaleDateStr4Display(modifyTime);
                if (U.equals(createTimeDisplay, modifyTimeDisplay)) {
                    // show time for modifyTime when createTime and modifyTime is on the same day
                    lDate.setText(getString(R.string.create_edited_modified_time, createTimeDisplay, DateTimeUtil.getLocaleTime4Display(modifyTime)));
                } else {
                    lDate.setText(getString(R.string.create_edited_modified_time, createTimeDisplay, modifyTimeDisplay));
                }
            }
//            Appearances.applyMarkerDateTextAppearance(lDate, textSizeMult);


            final int ari = marker.ari;
            final String reference = version.referenceWithVerseCount(ari, marker.verseCount);
            final String caption = marker.caption;

            final String rawVerseText = U.removeSpecialCodes(version.loadVerseText(ari));
            final CharSequence verseText;
            if (rawVerseText == null) {
                verseText = getString(R.string.generic_verse_not_available_in_this_version);
            } else {
                verseText = rawVerseText;
            }

            if (filter_kind == Marker.Kind.bookmark) {
                iconHighLight.setVisibility(View.GONE);
                icon.setVisibility(View.VISIBLE);
                lSnippet.setBackgroundColor(0);
                icon.setImageResource(R.drawable.ic_left_drawer_bookmarks);
                lCaption.setText(currentlyUsedFilter != null ? SearchEngine.hilite(caption, rt, hiliteColor) : caption);
//                Appearances.applyMarkerTitleTextAppearance(lCaption, textSizeMult);
                CharSequence snippet = currentlyUsedFilter != null ? SearchEngine.hilite(verseText, rt, hiliteColor) : verseText;

//                Appearances.applyMarkerSnippetContentAndAppearance(lSnippet, reference, snippet, textSizeMult);
                lSnippet.setText(snippet);
                lSnippet.setPadding(0, 0, 0, 0);

            } else if (filter_kind == Marker.Kind.note) {
                iconHighLight.setVisibility(View.GONE);
                icon.setVisibility(View.VISIBLE);
                lSnippet.setBackgroundColor(getResources().getColor(R.color.notes_item_bg));
                icon.setImageResource(R.drawable.ic_left_drawer_notes);
                lCaption.setText(reference);
//                Appearances.applyMarkerTitleTextAppearance(lCaption, textSizeMult);
                lSnippet.setText(currentlyUsedFilter != null ? SearchEngine.hilite(caption, rt, hiliteColor) : caption);
                lSnippet.setPadding(noteTextPaddingLeft, noteTextPaddingTop, noteTextPaddingLeft, noteTextPaddingTop);
//                Appearances.applyTextAppearance(lSnippet, textSizeMult);

            } else if (filter_kind == Marker.Kind.highlight) {
                iconHighLight.setVisibility(View.VISIBLE);
                icon.setVisibility(View.GONE);

                final Highlights.Info info = Highlights.decode(caption);

                ShapeDrawable shapeDrawable = new ShapeDrawable();
                shapeDrawable.setShape(new OvalShape());
                shapeDrawable.getPaint().setColor(info.colorRgb);
                if (Build.VERSION.SDK_INT > 15) {
                    iconHighLight.setBackground(shapeDrawable);
                } else {
                    iconHighLight.setBackgroundDrawable(shapeDrawable);
                }
//                icon.setImageResource(0);
                lSnippet.setBackgroundColor(0);
                lSnippet.setPadding(0, 0, 0, 0);

                lCaption.setText(reference);
//                Appearances.applyMarkerTitleTextAppearance(lCaption, textSizeMult);

                //verse highlight
//                final SpannableStringBuilder snippet = currentlyUsedFilter != null ? SearchEngine.hilite(verseText, rt, hiliteColor) : new SpannableStringBuilder(verseText);
//                final Highlights.Info info = Highlights.decode(caption);
//                if (info != null) {
//                    final BackgroundColorSpan span = new BackgroundColorSpan(Highlights.alphaMix(info.colorRgb));
//                    if (info.shouldRenderAsPartialForVerseText(verseText)) {
//                        snippet.setSpan(span, info.partial.startOffset, info.partial.endOffset, 0);
//                    } else {
//                        snippet.setSpan(span, 0, snippet.length(), 0);
//                    }
//                }
//                lSnippet.setText(snippet);
                lSnippet.setText(verseText);

//                Appearances.applyTextAppearance(lSnippet, textSizeMult);
            }
        }

        @Override
        public int getCount() {
            return filteredMarkers.size();
        }

        public void setData(List<Marker> filteredMarkers, SearchEngine.ReadyTokens rt) {
            this.filteredMarkers = filteredMarkers;
            this.rt = rt;

            // set up empty view to make sure it does not show loading progress again
            tEmpty.setVisibility(View.VISIBLE);
            msgEmpty.setVisibility(View.VISIBLE);
            icEmpty.setVisibility(View.VISIBLE);

            mProgressLayout.setVisibility(View.GONE);

            notifyDataSetChanged();
        }
    }

    private AlertDialog deleteDialog;

    private void showPopUpWindow(int position, View v) {
        final Marker marker = adapter.getItem(position);
        int deleteString = 0;
        switch (filter_kind) {
            case bookmark:
                deleteString = R.string.confirm_delete_bookmark;
                break;
            case note:
                deleteString = R.string.confirm_delete_note;
                break;
            case highlight:
                deleteString = R.string.confirm_delete_highlight;
                break;
            default:
                throw new RuntimeException("Unknown kind: " + filter_kind);
        }
        mPopupWindow = new PopupWindow(this);

        ArrayList<String> sortList = new ArrayList<String>();
        sortList.add(getString(R.string.read));
        sortList.add(getString(R.string.menuShare));
        sortList.add(getString(R.string.copy));
        sortList.add(getString(R.string.delete));

        ListView listViewSort = Utility.getPopupWindowList(this, sortList);
        // set on item selected
        final int finalDeleteString = deleteString;
        listViewSort.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //read
                        startActivity(Launcher.openAppAtBibleLocationWithVerseSelected(marker.ari, marker.verseCount));
                        break;
                    case 1:
                        NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_HIGHLIGHTS_SHARE, "click");
                        S.copyOrShareVerse(MarkerListActivity.this, marker.ari, marker.verseCount, true);
                        break;
                    case 2:
                        S.copyOrShareVerse(MarkerListActivity.this, marker.ari, marker.verseCount, false);
                        break;
                    case 3:
                        //delete
                        deleteDialog = new AlertDialog.Builder(MarkerListActivity.this)
                                .setMessage(getString(finalDeleteString))
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        S.getDb().deleteMarkerById(marker._id);
                                        loadAndFilter();
                                        App.getLbm().sendBroadcast(new Intent(Constants.ACTION_ATTRIBUTE_MAP_CHANGED));
                                        String eventKind = "";
                                        if (marker.kind.equals(Marker.Kind.bookmark)) {
                                            eventKind = EventVerseOperate.BOOKMARK;
                                        } else if (marker.kind.equals(Marker.Kind.highlight)) {
                                            eventKind = EventVerseOperate.HIGHLIGHTS;
                                        } else if (marker.kind.equals(Marker.Kind.note)) {
                                            eventKind = EventVerseOperate.NOTE;
                                        }
                                        EventBus.getDefault().post(new EventVerseOperate(eventKind));
                                        dialog.dismiss();
                                    }
                                }).create();
                        deleteDialog.show();

                        break;
                }
                recordActionEvent(position);
                if (mPopupWindow != null)
                    mPopupWindow.dismiss();

            }
        });

        // some other visual settings for popup window
        mPopupWindow.setFocusable(true);
        mPopupWindow.setWidth(getResources().getDimensionPixelSize(R.dimen.mark_list_popup_window_width));
        mPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setContentView(listViewSort);

        int windowPos[] = Utility.getPopWindowPosInList(this, v, root);
        int xOff = 10;
        windowPos[0] += xOff;
        mPopupWindow.showAtLocation(v, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
    }

    private void recordActionEvent(int position) {
        String viewPage = "";
        String event = "";
        switch (filter_kind) {
            case bookmark:
                viewPage = AnalyticsConstants.P_BOOKMARKLIST;
                break;
            case note:
                viewPage = AnalyticsConstants.P_NOTELIST;
                break;
            case highlight:
                viewPage = AnalyticsConstants.P_HIGHLIGHTLIST;
                break;
        }

        switch (position) {
            case 0:
                event = AnalyticsConstants.B_READ;
                break;
            case 1:
                event = AnalyticsConstants.B_SHARE;
                break;
            case 2:
                event = AnalyticsConstants.B_COPY;
                break;
            case 3:
                event = AnalyticsConstants.B_DELETE;
                break;
        }
        UserBehaviorAnalytics.trackUserBehavior(MarkerListActivity.this, viewPage, event);
    }
}
