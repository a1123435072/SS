package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.Date;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import yuku.afw.V;

import yuku.alkitab.model.Marker;

public class TypeBookmarkDialog {
    public interface Listener {
        /**
         * Called when this dialog is closed with the bookmark modified or deleted
         */
        void onModifiedOrDeleted();
    }

    final Context context;
    final Dialog dialog;
    EditText tCaption;

    Marker marker;
    int ariForNewBookmark;
    int verseCountForNewBookmark;
    String defaultCaption;

    // optional
    Listener listener;

    /**
     * Open the bookmark edit dialog, editing existing bookmark.
     *
     * @param context Activity context to create dialogs
     */
    public static TypeBookmarkDialog EditExisting(Context context, long _id) {
        return new TypeBookmarkDialog(context, S.getDb().getMarkerById(_id), null);
    }

    /**
     * Open the bookmark edit dialog for a new bookmark by ari.
     */
    public static TypeBookmarkDialog NewBookmark(Context context, int ari, final int verseCount) {
        final TypeBookmarkDialog res = new TypeBookmarkDialog(context, null, S.getVerseReference(ari, verseCount));
        res.ariForNewBookmark = ari;
        res.verseCountForNewBookmark = verseCount;
        return res;
    }

    private TypeBookmarkDialog(final Context context, final Marker marker, String reference) {
        this.context = context;
        this.marker = marker;

        if (reference == null) {
            reference = S.getVerseReference(marker.ari, marker.verseCount);
        }
        defaultCaption = reference;

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_bookmark, null);

        tCaption = V.get(dialogView, R.id.tCaption);

        tCaption.setText(marker != null ? marker.caption : reference);

        this.dialog = new MaterialDialog.Builder(context)
                .customView(dialogView, false)
                .backgroundColor(Color.WHITE)
                .title(reference)
                .titleColor(Color.BLACK)
//                .iconRes(R.drawable.ic_attr_bookmark)
                .buttonsGravity(GravityEnum.CENTER)
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        bOk_click();
                    }
                })
                .neutralText(marker != null ? R.string.delete : R.string.cancel)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        bDelete_click(marker);
                    }
                })
                .show();
    }

    void bOk_click() {
        String caption = tCaption.getText().toString();

        // If there is no caption, show reference
        if (caption.length() == 0 || caption.trim().length() == 0) {
            caption = defaultCaption;
        }

        final Date now = new Date();
        if (marker != null) { // update existing
            marker.caption = caption;
            marker.modifyTime = now;
            S.getDb().insertOrUpdateMarker(marker);
        } else { // add new
            marker = S.getDb().insertMarker(ariForNewBookmark, Marker.Kind.bookmark, caption, verseCountForNewBookmark, now, now);
        }

        if (listener != null) listener.onModifiedOrDeleted();
    }

    public void show() {
        dialog.show();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    protected void bDelete_click(final Marker marker) {
        if (marker == null) {
            return; // bookmark not saved, so no need to confirm
        }

        new MaterialDialog.Builder(context)
                .backgroundColor(Color.WHITE)
                .content(R.string.bookmark_delete_confirmation)
                .contentColor(Color.BLACK)
                .positiveText(R.string.delete)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    S.getDb().deleteMarkerById(marker._id);

                                    if (listener != null) listener.onModifiedOrDeleted();
                                }
                            }
                )
                .negativeText(R.string.cancel)
                .show();
    }
}
