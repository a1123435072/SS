package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Date;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.SelfAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.App;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.U;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventEditNoteDone;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventVerseOperate;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.widget.VerseItemView;
import de.greenrobot.event.EventBus;
import yuku.afw.V;

import yuku.alkitab.model.Marker;

public class NoteActivity extends BaseActivity {
    //region static constructors
    private static final String EXTRA_marker_id = "marker_id";
    private static final String EXTRA_reference = "reference";
    private static final String EXTRA_ariForNewNote = "ariForNote";
    private static final String EXTRA_verseCountForNewNote = "verseCountForNote";
    private static final String EXTRA_NOTE_EDIT_TYPE = "note_edit_type";
    public static final int TYPE_NOTE_EDIT = 0;
    public static final int TYPE_NOTE_CREATE = 1;

    private static Intent createIntent(final long marker_id, final String reference, final int ariForNewNote, final int verseCountForNewNote, int editType) {
        final Intent res = new Intent(App.context, NoteActivity.class);
        res.putExtra(EXTRA_marker_id, marker_id);
        res.putExtra(EXTRA_reference, reference);
        res.putExtra(EXTRA_ariForNewNote, ariForNewNote);
        res.putExtra(EXTRA_verseCountForNewNote, verseCountForNewNote);
        res.putExtra(EXTRA_NOTE_EDIT_TYPE, editType);
        return res;
    }

    /**
     * Open the note edit dialog, editing existing note.
     */
    public static Intent createEditExistingIntent(long _id, int editType) {
        return createIntent(_id, null, 0, 0, editType);
    }

    /**
     * Open the note edit dialog for a new note by ari.
     */
    public static Intent createNewNoteIntent(String reference, int ari, int verseCount, int editType) {
        return createIntent(0, reference, ari, verseCount, editType);
    }

    Marker marker;
    int ariForNote;
    int verseCountForNote, mNoteEditType;
    String reference;

    private TextView tvReference;
    private EditText tCaption;
    private VerseItemView mVerseItemView;
    private TextView btnOk, btnCancelOrDelete;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        initToolbar();
        handleIntent();
        initView();
    }

    private void initToolbar() {
        final Toolbar toolbar = V.get(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.tulis_catatan);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            ariForNote = intent.getIntExtra(EXTRA_ariForNewNote, 0);
            ariForNote = intent.getIntExtra(EXTRA_ariForNewNote, 0);
            verseCountForNote = intent.getIntExtra(EXTRA_verseCountForNewNote, 0);
            mNoteEditType = intent.getIntExtra(EXTRA_NOTE_EDIT_TYPE, 0);
            final long _id = intent.getLongExtra(EXTRA_marker_id, 0L);
            if (_id != 0L) {
                marker = S.getDb().getMarkerById(_id);
                if (marker != null) {
                    ariForNote = marker.ari;
                    verseCountForNote = marker.verseCount;
                }
            }
            reference = intent.getStringExtra(EXTRA_reference);
            if (reference == null) {
                reference = S.getVerseReference(marker.ari, marker.verseCount);
            }
        }

    }

    private void initView() {
        tvReference = V.get(this, R.id.verses_reference_text);
        tvReference.setText(reference);

        tCaption = V.get(this, R.id.tCaption);
        if (marker != null) {
            tCaption.setText(marker.caption);
        }

        mVerseItemView = V.get(this, R.id.verse_item_view);
        mVerseItemView.loadVerse(ariForNote, verseCountForNote);

        btnCancelOrDelete = V.get(this, R.id.cancle_btn);
        if (marker != null) {
            btnCancelOrDelete.setText(R.string.delete);
            btnCancelOrDelete.setOnClickListener(deleteClickListener);
        } else {
            btnCancelOrDelete.setText(R.string.cancel);
            btnCancelOrDelete.setOnClickListener(cancelClickListener);
        }

        btnOk = V.get(this, R.id.ok_btn);
        btnOk.setOnClickListener(okClickListener);
    }


    @Override
    protected void onStart() {
        super.onStart();

        { // apply background color, by overriding window background
            getWindow().setBackgroundDrawable(new ColorDrawable(S.applied.backgroundColor));
        }

//        // text formats
//        for (final TextView tv : new TextView[]{tCaption}) {
//            tv.setTextColor(S.applied.fontColor);
//            tv.setTypeface(S.applied.fontFace, S.applied.fontBold);
//            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, S.applied.fontSize2dp);
//            tv.setLineSpacing(0, S.applied.lineSpacingMult);
//
//            SettingsActivity.setPaddingBasedOnPreferences(tv);
//        }
    }

    View.OnClickListener deleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new MaterialDialog.Builder(NoteActivity.this)
                    .content(R.string.anda_yakin_mau_menghapus_catatan_ini)
                    .positiveText(R.string.delete)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            if (marker != null) {
                                // really delete from db
                                S.getDb().deleteNonBookmarkMarkerById(marker._id);
                                EventBus.getDefault().post(new EventVerseOperate(EventVerseOperate.NOTE));
                            } else {
                                // do nothing, because it's indeed not in the db, only in editor buffer
                            }

                            postEditNoteEvent(false);
                            realFinish();
                        }
                    })
                    .negativeText(R.string.cancel)
                    .show();
        }
    };

    View.OnClickListener cancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setResult(RESULT_CANCELED);
            postEditNoteEvent(true);
            realFinish();
        }
    };

    View.OnClickListener okClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final String caption = tCaption.getText().toString();
            final Date now = new Date();

            if (marker != null) { // update existing marker
                if (U.equals(marker.caption, caption)) {
                    // when there is no change, do nothing
                } else {
                    if (caption.length() == 0) { // delete instead of update
                        S.getDb().deleteNonBookmarkMarkerById(marker._id);
                    } else {
                        marker.caption = caption;
                        marker.modifyTime = now;
                        S.getDb().insertOrUpdateMarker(marker);
                    }
                }
            } else { // marker == null; not existing, so only insert when there is some text
                if (caption.length() > 0) {
                    marker = S.getDb().insertMarker(ariForNote, Marker.Kind.note, caption, verseCountForNote, now, now);
                    EventBus.getDefault().post(new EventVerseOperate(EventVerseOperate.NOTE));
                } else {
                    Snackbar.make(NoteActivity.this.findViewById(android.R.id.content), getString(R.string.note_content_is_required), Snackbar.LENGTH_SHORT).show();
                    return;
                }
            }

            postEditNoteEvent(false);
            realFinish();
            SelfAnalyticsHelper.sendReadVerseAnalytics(NoteActivity.this, AnalyticsConstants.A_SAVE, AnalyticsConstants.L_NOTE_SAVE);
        }
    };

    private void postEditNoteEvent(boolean isCancel) {
        EventBus.getDefault().post(new EventEditNoteDone(mNoteEditType, isCancel));
    }

    @Override
    public void finish() {
        realFinish();
    }

    void realFinish() {
        super.finish();
    }
}
