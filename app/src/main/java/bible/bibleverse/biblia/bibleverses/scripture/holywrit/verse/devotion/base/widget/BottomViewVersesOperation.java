package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.widget;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.NewAnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.NewAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.SelfAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.UserBehaviorAnalytics;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.U;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.ImgChooseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.NoteActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.dialog.TypeBookmarkDialog;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventVerseOperate;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import de.greenrobot.event.EventBus;
import yuku.afw.V;
import yuku.afw.storage.Preferences;
import yuku.alkitab.model.Book;
import yuku.alkitab.model.Version;
import yuku.alkitab.util.Ari;
import yuku.alkitab.util.IntArrayList;

import static bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.Literals.Array;


public class BottomViewVersesOperation {
    public static final String TAG = BottomViewVersesOperation.class.getSimpleName();

    public interface Listener {
        void onValueChanged();

        void onBottomViewDismiss();
    }

    final Activity activity;
    final FrameLayout parent;
    BibleChapterView chapterView;
    Book activeBook;
    int chapter;
    final Listener listener;
    final View content;

    RecyclerView highlightColorList;
    LinearLayout btnShare, btnImage, btnBookmark, btnNote, btnCopy;

    HighlightTypesAdapter highlightTypesAdapter;
    boolean shown = false;
    private IntArrayList selectedVerses;
    private int selectedVersesHighlightIndex;

    public BottomViewVersesOperation(Activity activity, FrameLayout parent, Book activeBook, int chapter, Listener listener) {
        this.activity = activity;
        this.parent = parent;
        this.activeBook = activeBook;
        this.chapter = chapter;
        this.listener = listener;
        this.content = activity.getLayoutInflater().inflate(R.layout.bottom_sheet_verses_operation, parent, false);

        this.content.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        }); // prevent click-through

        this.shown = false;
        this.selectedVersesHighlightIndex = -1;
        this.selectedVerses = null;

        highlightColorList = V.get(content, R.id.highlight_types_list);
        highlightTypesAdapter = new HighlightTypesAdapter();
        highlightColorList.setAdapter(highlightTypesAdapter);

        btnShare = V.get(content, R.id.share_btn);
        btnShare.setOnClickListener(new ShareOrCopyListener(true));

        btnImage = V.get(content, R.id.image_btn);
        btnImage.setOnClickListener(btnImageOnClickListener);

        btnBookmark = V.get(content, R.id.bookmark_btn);
        btnBookmark.setOnClickListener(btnBookmarkOnClickListener);

        btnNote = V.get(content, R.id.note_btn);
        btnNote.setOnClickListener(btnNoteOnClickListener);

        btnCopy = V.get(content, R.id.copy_btn);
        btnCopy.setOnClickListener(new ShareOrCopyListener(false));
    }

    public void updateBookAndChapter(Book activeBook, int chapter) {
        this.activeBook = activeBook;
        this.chapter = chapter;
    }

    public boolean isShowing() {
        return shown;
    }

    public void updateView(BibleChapterView chapterView) {
        this.chapterView = chapterView;
        selectedVersesHighlightIndex = -1;
        selectedVerses = chapterView.getSelectedVerses_1();

        boolean isSelectedNonContiguousVerse = false;
        if (selectedVerses.size() > 0) {
            isSelectedNonContiguousVerse = (selectedVerses.get(selectedVerses.size() - 1) - selectedVerses.get(0) != selectedVerses.size() - 1);
        }
        if (isSelectedNonContiguousVerse) {
            btnImage.setVisibility(View.GONE);
            btnBookmark.setVisibility(View.GONE);
            btnNote.setVisibility(View.GONE);
        } else {
            btnImage.setVisibility(View.VISIBLE);
            btnBookmark.setVisibility(View.VISIBLE);
            btnNote.setVisibility(View.VISIBLE);
        }

        if (selectedVerses.size() > 0) {
            final int ariBc = Ari.encode(activeBook.bookId, chapter, 0);
            int colorRgb = S.getDb().getHighlightColorRgb(ariBc, selectedVerses);
            selectedVersesHighlightIndex = highlightTypesAdapter.getPositionByColor(colorRgb);
            if (selectedVersesHighlightIndex != -1) {
                highlightColorList.scrollToPosition(selectedVersesHighlightIndex);
            }
            highlightTypesAdapter.notifyDataSetChanged();
        } else {
            hide();
        }
    }

    public void show() {
        if (shown) return;

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        parent.addView(content, params);

        shown = true;
    }

    public void hide() {
        if (!shown) return;

        shown = false;
        parent.removeView(content);
        if (listener != null) {
            listener.onBottomViewDismiss();
        }
    }

    private CharSequence referenceFromSelectedVerses(IntArrayList selectedVerses, Book book) {
        if (selectedVerses.size() == 0) {
            // should not be possible. So we don't do anything.
            return book.reference(this.chapter);
        } else if (selectedVerses.size() == 1) {
            return book.reference(this.chapter, selectedVerses.get(0));
        } else {
            return book.reference(this.chapter, selectedVerses);
        }
    }

    /**
     * Construct text for copying or sharing (in plain text).
     *
     * @param isSplitVersion whether take the verse text from the main or from the split version.
     * @return [0] text for copy/share, [1] text to be submitted to the share url service
     */
    String[] prepareTextForCopyShare(IntArrayList selectedVerses_1, CharSequence reference, boolean isSplitVersion) {
        final StringBuilder res0 = new StringBuilder();
        final StringBuilder res1 = new StringBuilder();

        res0.append(reference);

        if (Preferences.getBoolean(activity.getString(R.string.pref_copyWithVersionName_key), activity.getResources().getBoolean(R.bool.pref_copyWithVersionName_default))) {
            final Version version = /*isSplitVersion ? activeSplitVersion :*/ S.activeVersion;
            final String versionShortName = version.getShortName();
            if (versionShortName != null) {
                res0.append(" (").append(versionShortName).append(")");
            }
        }

        if (Preferences.getBoolean(activity.getString(R.string.pref_copyWithVerseNumbers_key), false) && selectedVerses_1.size() > 1) {
            res0.append('\n');

            // append each selected verse with verse number prepended
            for (int i = 0, len = selectedVerses_1.size(); i < len; i++) {
                final int verse_1 = selectedVerses_1.get(i);
                final String verseText = /*isSplitVersion ? lsSplit1.getVerseText(verse_1) :*/ chapterView.getVerseText(verse_1);

                if (verseText != null) {
                    final String verseTextPlain = U.removeSpecialCodes(verseText);

                    res0.append(verse_1);
                    res1.append(verse_1);
                    res0.append(' ');
                    res1.append(' ');

                    res0.append(verseTextPlain);
                    res1.append(verseText);

                    if (i != len - 1) {
                        res0.append('\n');
                        res1.append('\n');
                    }
                }
            }
        } else {
            res0.append("  ");

            // append each selected verse without verse number prepended
            for (int i = 0; i < selectedVerses_1.size(); i++) {
                final int verse_1 = selectedVerses_1.get(i);
                final String verseText = /*isSplitVersion ? lsSplit1.getVerseText(verse_1) : */chapterView.getVerseText(verse_1);

                if (verseText != null) {
                    final String verseTextPlain = U.removeSpecialCodes(verseText);

                    if (i != 0) {
                        res0.append('\n');
                        res1.append('\n');
                    }
                    res0.append(verseTextPlain);
                    res1.append(verseText);
                }
            }
        }

        return Array(res0.toString(), res1.toString());
    }

    class ShareOrCopyListener implements View.OnClickListener {

        private boolean is4Share;

        public ShareOrCopyListener(boolean is4Share) {
            this.is4Share = is4Share;
        }

        @Override
        public void onClick(View view) {
            if (selectedVerses == null || selectedVerses.size() == 0) return;
            final CharSequence reference = referenceFromSelectedVerses(selectedVerses, activeBook);
            final String[] t;
            t = prepareTextForCopyShare(selectedVerses, reference, false);
            final String textToShare = t[0];
            final String textToCopy = t[0];
            final String textToSubmit = t[1];

            if (is4Share) {
                Utility.shareTextBySystem(activity, textToShare);
                SelfAnalyticsHelper.sendReadVerseAnalytics(activity, AnalyticsConstants.A_CLICK, AnalyticsConstants.L_SHARE);
                UserBehaviorAnalytics.trackUserBehavior(activity, AnalyticsConstants.P_BIBLEPAGE, AnalyticsConstants.B_SHARE);
                NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_READ_BOTTOM_SHARE, "click");
//                final Intent intent = ShareCompat.IntentBuilder.from(activity)
//                        .setType("text/plain")
//                        .setSubject(reference.toString())
//                        .getIntent();
//
//                intent.putExtra(Intent.EXTRA_TEXT, textToShare);
//                activity.startActivityForResult(ShareActivity.createIntent(intent, activity.getString(R.string.bagikan_alamat, reference)), IsiActivity.REQCODE_share);
            } else {
                U.copyToClipboard(textToCopy);
                Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.alamat_sudah_disalin, reference), Snackbar.LENGTH_SHORT).show();
                SelfAnalyticsHelper.sendReadVerseAnalytics(activity, AnalyticsConstants.A_CLICK, AnalyticsConstants.L_COPY);
                UserBehaviorAnalytics.trackUserBehavior(activity, AnalyticsConstants.P_BIBLEPAGE, AnalyticsConstants.B_COPY);
                NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_READ_BOTTOM_COPY , "click");
            }
            hide();

//            ShareUrl.make(activity, !Preferences.getBoolean(activity.getString(R.string.pref_copyWithShareUrl_key), activity.getResources().getBoolean(R.bool.pref_copyWithShareUrl_default)), textToSubmit, Ari.encode(activeBook.bookId, chapter, 0), selectedVerses, reference.toString(), S.activeVersion, MVersionDb.presetNameFromVersionId(S.activeVersionId), new ShareUrl.Callback() {
//                @Override
//                public void onSuccess(final String shareUrl) {
//                    if (is4Share) {
//                        intent.putExtra(Intent.EXTRA_TEXT, textToShare + "\n\n" + shareUrl);
//                        intent.putExtra(IsiActivity.EXTRA_verseUrl, shareUrl);
//                    } else {
//                        U.copyToClipboard(textToCopy + "\n\n" + shareUrl);
//                    }
//                }
//
//                @Override
//                public void onUserCancel() {
//                    if (is4Share) {
//                        intent.putExtra(Intent.EXTRA_TEXT, textToShare);
//                    } else {
//                        U.copyToClipboard(textToCopy);
//                    }
//                }
//
//                @Override
//                public void onError(final Exception e) {
//                    if (is4Share) {
//                        intent.putExtra(Intent.EXTRA_TEXT, textToShare);
//                    } else {
//                        U.copyToClipboard(textToCopy);
//                    }
//                }
//
//                @Override
//                public void onFinally() {
//                    hide();
//                    if (is4Share) {
//                        activity.startActivityForResult(ShareActivity.createIntent(intent, activity.getString(R.string.bagikan_alamat, reference)), IsiActivity.REQCODE_share);
//                    } else {
//                        Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.alamat_sudah_disalin, reference), Snackbar.LENGTH_SHORT).show();
//                    }
//                }
//            });
        }
    }

    View.OnClickListener btnBookmarkOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (selectedVerses != null && selectedVerses.size() > 0) {
                // contract: this menu only appears when contiguous verses are selected
                if (selectedVerses.get(selectedVerses.size() - 1) - selectedVerses.get(0) != selectedVerses.size() - 1) {
//                    throw new RuntimeException("Non contiguous verses when adding bookmark: " + selectedVerses);
                    Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.verse_operation_error), Snackbar.LENGTH_LONG).show();
                } else {
                    final int ari = Ari.encode(activeBook.bookId, chapter, selectedVerses.get(0));
                    final int verseCount = selectedVerses.size();

                    // always create a new bookmark
                    TypeBookmarkDialog dialog = TypeBookmarkDialog.NewBookmark(activity, ari, verseCount);
                    dialog.setListener(new TypeBookmarkDialog.Listener() {
                        @Override
                        public void onModifiedOrDeleted() {
                            listener.onValueChanged();
                            EventBus.getDefault().post(new EventVerseOperate(EventVerseOperate.BOOKMARK));
                            SelfAnalyticsHelper.sendReadVerseAnalytics(activity, AnalyticsConstants.A_SAVE, AnalyticsConstants.L_BOOKMARK_SAVE);
                            NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_READ_BOTTOM_BOOKMARK_SAVE, "click");
                        }
                    });
                    dialog.show();
                }
            }
            hide();
            SelfAnalyticsHelper.sendReadVerseAnalytics(activity, AnalyticsConstants.A_CLICK, AnalyticsConstants.L_BOOKMARK);
            UserBehaviorAnalytics.trackUserBehavior(activity, AnalyticsConstants.P_BIBLEPAGE, AnalyticsConstants.B_BOOKMARK);
            NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_READ_BOTTOM_BOOKMARK, "click");
        }
    };

    View.OnClickListener btnNoteOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (selectedVerses != null && selectedVerses.size() > 0) {
                // contract: this menu only appears when contiguous verses are selected
                if (selectedVerses.get(selectedVerses.size() - 1) - selectedVerses.get(0) != selectedVerses.size() - 1) {
//                    throw new RuntimeException("Non contiguous verses when adding note: " + selectedVerses);
                    Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.verse_operation_error), Snackbar.LENGTH_LONG).show();
                } else {
                    final int ari = Ari.encode(activeBook.bookId, chapter, selectedVerses.get(0));
                    final int verseCount = selectedVerses.size();

                    // always create a new note
                    activity.startActivity(NoteActivity.createNewNoteIntent(S.getVerseReference(ari, verseCount), ari, verseCount, NoteActivity.TYPE_NOTE_CREATE));
                }
            }
            hide();
            SelfAnalyticsHelper.sendReadVerseAnalytics(activity, AnalyticsConstants.A_CLICK, AnalyticsConstants.L_NOTE);
            UserBehaviorAnalytics.trackUserBehavior(activity, AnalyticsConstants.P_BIBLEPAGE, AnalyticsConstants.B_NOTE);
            NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_READ_BOTTOM_NOTE, "click");
        }
    };

    View.OnClickListener btnImageOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (selectedVerses != null && selectedVerses.size() > 0) {
                // contract: this menu only appears when contiguous verses are selected
                if (selectedVerses.get(selectedVerses.size() - 1) - selectedVerses.get(0) != selectedVerses.size() - 1) {
                    Snackbar.make(activity.findViewById(android.R.id.content), activity.getString(R.string.verse_operation_error), Snackbar.LENGTH_LONG).show();
                } else {
                    final int ari = Ari.encode(activeBook.bookId, chapter, selectedVerses.get(0));
                    final int verseCount = selectedVerses.size();
                    activity.startActivity(ImgChooseActivity.createIntent(activity, ari, verseCount));
                }
            }
            hide();
            SelfAnalyticsHelper.sendReadVerseAnalytics(activity, AnalyticsConstants.A_CLICK, AnalyticsConstants.L_IMAGE);
            UserBehaviorAnalytics.trackUserBehavior(activity, AnalyticsConstants.P_BIBLEPAGE, AnalyticsConstants.B_IMAGE);
            NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_READ_BOTTOM_IMAGE, "click");
        }
    };

    public class HighlightTypesItemHolder extends RecyclerView.ViewHolder {
        protected View btnAdd;
        protected ImageView btnDelete;

        public HighlightTypesItemHolder(View view) {
            super(view);
            this.btnAdd = V.get(view, R.id.add_btn);
            this.btnDelete = V.get(view, R.id.delete_btn);
        }
    }

    class HighlightTypesAdapter extends RecyclerView.Adapter<HighlightTypesItemHolder> {
        final int[] rgbs = {
                Color.parseColor("#fffeca"), Color.parseColor("#fffe93"), Color.parseColor("#fffe00"), Color.parseColor("#beffaa"),
                Color.parseColor("#a3ffa8"), Color.parseColor("#5dff79"), Color.parseColor("#a6f7ff"), Color.parseColor("#56f3ff"),
                Color.parseColor("#00d6ff"), Color.parseColor("#e9e5ff"), Color.parseColor("#ffcaf7"), Color.parseColor("#ff95ef"),
                Color.parseColor("#ffe5d3"), Color.parseColor("#ffc66f")
        };


        public HighlightTypesAdapter() {
        }

        @Override
        public HighlightTypesItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(activity).inflate(R.layout.item_highlight_type_list, null);
            HighlightTypesItemHolder itemHolder = new HighlightTypesItemHolder(v);
            return itemHolder;
        }

        @Override
        public void onBindViewHolder(HighlightTypesItemHolder holder, final int position) {
            ShapeDrawable shapeDrawable = new ShapeDrawable();
            shapeDrawable.setShape(new OvalShape());
            shapeDrawable.getPaint().setColor(rgbs[position]);
            if (Build.VERSION.SDK_INT > 15) {
                holder.btnAdd.setBackground(shapeDrawable);
            } else {
                holder.btnAdd.setBackgroundDrawable(shapeDrawable);
            }

            holder.btnDelete.setVisibility(position != selectedVersesHighlightIndex ? View.GONE : View.VISIBLE);
            holder.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selectedVerses != null && selectedVerses.size() > 0) {
                        final int ariBc = Ari.encode(activeBook.bookId, chapter, 0);
                        int colorRgb = rgbs[position];
                        S.getDb().updateOrInsertHighlights(ariBc, selectedVerses, colorRgb);
                        EventBus.getDefault().post(new EventVerseOperate(EventVerseOperate.HIGHLIGHTS));
                    }
                    hide();
                    SelfAnalyticsHelper.sendReadVerseAnalytics(activity, AnalyticsConstants.A_CLICK, AnalyticsConstants.L_HIGHLIGHT);
                    UserBehaviorAnalytics.trackUserBehavior(activity, AnalyticsConstants.P_BIBLEPAGE, AnalyticsConstants.B_HIGHLIGHT);
                }
            });

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (int i = 0; i < selectedVerses.size(); i++) {
                        int verse = selectedVerses.get(i);
                        int ari = Ari.encode(activeBook.bookId, chapter, verse);
                        S.getDb().deleteHighlight(ari);
                        EventBus.getDefault().post(new EventVerseOperate(EventVerseOperate.HIGHLIGHTS));
                    }
                    hide();
                }
            });
        }

        @Override
        public int getItemCount() {
            return rgbs.length;
        }

        public int getPositionByColor(int color) {
            for (int i = 0; i < rgbs.length; i++) {
                if (color == rgbs[i]) {
                    return i;
                }
            }
            return -1;
        }
    }
}
