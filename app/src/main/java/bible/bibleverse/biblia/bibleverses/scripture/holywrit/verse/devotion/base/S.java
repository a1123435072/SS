package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.VersionsActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.config.AppConfig;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.MVersion;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.MVersionDb;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.MVersionInternal;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.storage.InternalDb;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.storage.InternalDbHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.storage.Prefkey;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.FontManager;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import yuku.afw.storage.Preferences;
import yuku.alkitab.model.Version;
import yuku.alkitabintegration.display.Launcher;

public class S {
    static final String TAG = S.class.getSimpleName();

    /**
     * values applied from settings
     */
    public static class applied {
        /**
         * in dp
         */
        public static float fontSize2dp;

        public static Typeface fontFace;
        public static float lineSpacingMult;
        public static int fontBold;

        public static int fontColor;
        public static int fontRedColor;
        public static int backgroundColor;
        public static int verseNumberColor;

        /**
         * 0.f to 1.f
         */
        public static float backgroundBrightness;

        // semua di bawah dalam px
        public static int indentParagraphFirst;
        public static int indentParagraphRest;
        public static int indentSpacing1;
        public static int indentSpacing2;
        public static int indentSpacing3;
        public static int indentSpacing4;
        public static int indentSpacingExtra;
        public static int paragraphSpacingBefore;
        public static int pericopeSpacingTop;
        public static int pericopeSpacingBottom;
    }

    // The process-global active Bible version. Both of these must always be set at the same time.
    public static Version activeVersion;
    public static String activeVersionId;

    public static void checkActiveVersion() {
        if (activeVersion == null) {
            Utility.initDeafaultVersion();
        }
        if (activeVersion == null) {
            List<MVersion> versions = getAvailableVersions();
            if (versions.size() > 0 && versions.get(0) != null) {
                activeVersion = versions.get(0).getVersion();
                activeVersionId = versions.get(0).getVersionId();
            }
        }
    }

    public static boolean shouldRemoveSpecialCodes4ActiveVersion() {
        checkActiveVersion();
        if (activeVersion == null) {
            return false;
        }

        return activeVersionId.equals("preset/en-kjv")
                || activeVersionId.equals("preset/en-net")
                || activeVersionId.equals("preset/en-esv");
    }

    public static String getVerseByAri(int ari) {
        checkActiveVersion();
        if (activeVersion == null) {
            return "";
        }
        String result = activeVersion.loadVerseText(ari);
        return U.removeSpecialCodes(result);
    }

    public static String getVerseReference(int ari, int verseCount) {
        checkActiveVersion();
        if (activeVersion == null) {
            return "";
        }
        return activeVersion.referenceWithVerseCount(ari, verseCount);
    }

    public static String getVerseByAriAndCount(int ari, int verseCount) {
        String result = "";
        for (int i = 0; i < verseCount; i++) {
            result += getVerseByAri(ari + i);
        }
        return result;
    }

    public static void gotoVerse(Context context, int ari, int verseCount) {
        Intent intent = Launcher.openAppAtBibleLocationWithVerseSelected(ari, verseCount);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void copyOrShareVerse(Activity context, int ari, int verseCount, boolean is4Share) {
        String verseText = getVerseByAriAndCount(ari, verseCount);
        String reference = getVerseReference(ari, verseCount);
        verseText += "\n";
        verseText += reference;

        if (is4Share) {
            Utility.shareTextBySystem(context, verseText);
        } else {
            U.copyToClipboard(verseText);
            Snackbar.make(context.findViewById(android.R.id.content), context.getString(R.string.alamat_sudah_disalin, reference), Snackbar.LENGTH_SHORT).show();
        }
    }

    public static void calculateAppliedValuesBasedOnPreferences() {
        //# configure font size
        {
            applied.fontSize2dp = Preferences.getFloat(Prefkey.ukuranHuruf2, (float) App.context.getResources().getInteger(R.integer.pref_ukuranHuruf2_default));
        }

        //# configure fonts
        {
            applied.fontFace = FontManager.typeface(Preferences.getString(Prefkey.jenisHuruf, null));
            applied.lineSpacingMult = Preferences.getFloat(Prefkey.lineSpacingMult, 1.5f);
            applied.fontBold = Preferences.getBoolean(Prefkey.boldHuruf, false) ? Typeface.BOLD : Typeface.NORMAL;
        }

        //# configure text color, red text color, bg color, and verse color
        {
            if (Preferences.getBoolean(Prefkey.is_night_mode, false)) {
                applied.fontColor = Preferences.getInt(R.string.pref_textColor_night_key, R.integer.pref_textColor_night_default);
                applied.backgroundColor = Preferences.getInt(R.string.pref_backgroundColor_night_key, R.integer.pref_backgroundColor_night_default);
                applied.verseNumberColor = Preferences.getInt(R.string.pref_verseNumberColor_night_key, R.integer.pref_verseNumberColor_night_default);
                applied.fontRedColor = Preferences.getInt(R.string.pref_redTextColor_night_key, R.integer.pref_redTextColor_night_default);
            } else {
                applied.fontColor = Preferences.getInt(R.string.pref_textColor_key, R.integer.pref_textColor_default);
                applied.backgroundColor = Preferences.getInt(R.string.pref_backgroundColor_key, R.integer.pref_backgroundColor_default);
                applied.verseNumberColor = Preferences.getInt(R.string.pref_verseNumberColor_key, R.integer.pref_verseNumberColor_default);
                applied.fontRedColor = Preferences.getInt(R.string.pref_redTextColor_key, R.integer.pref_redTextColor_default);
            }

            // calculation of backgroundColor brightness. Used somewhere else.
            {
                int c = applied.backgroundColor;
                applied.backgroundBrightness = (0.30f * Color.red(c) + 0.59f * Color.green(c) + 0.11f * Color.blue(c)) * 0.003921568627f;
            }
        }

        Resources res = App.context.getResources();

        float scaleBasedOnFontSize = applied.fontSize2dp / 17.f;
        applied.indentParagraphFirst = (int) (scaleBasedOnFontSize * res.getDimensionPixelOffset(R.dimen.indentParagraphFirst) + 0.5f);
        applied.indentParagraphRest = (int) (scaleBasedOnFontSize * res.getDimensionPixelOffset(R.dimen.indentParagraphRest) + 0.5f);
        applied.indentSpacing1 = (int) (scaleBasedOnFontSize * res.getDimensionPixelOffset(R.dimen.indent_1) + 0.5f);
        applied.indentSpacing2 = (int) (scaleBasedOnFontSize * res.getDimensionPixelOffset(R.dimen.indent_2) + 0.5f);
        applied.indentSpacing3 = (int) (scaleBasedOnFontSize * res.getDimensionPixelOffset(R.dimen.indent_3) + 0.5f);
        applied.indentSpacing4 = (int) (scaleBasedOnFontSize * res.getDimensionPixelOffset(R.dimen.indent_4) + 0.5f);
        applied.indentSpacingExtra = (int) (scaleBasedOnFontSize * res.getDimensionPixelOffset(R.dimen.indentExtra) + 0.5f);
        applied.paragraphSpacingBefore = (int) (scaleBasedOnFontSize * res.getDimensionPixelOffset(R.dimen.paragraphSpacingBefore) + 0.5f);
        applied.pericopeSpacingTop = (int) (scaleBasedOnFontSize * res.getDimensionPixelOffset(R.dimen.pericopeSpacingTop) + 0.5f);
        applied.pericopeSpacingBottom = (int) (scaleBasedOnFontSize * res.getDimensionPixelOffset(R.dimen.pericopeSpacingBottom) + 0.5f);
    }

    private static InternalDb db;

    public static synchronized InternalDb getDb() {
        if (db == null) {
            db = new InternalDb(new InternalDbHelper(App.context));
        }

        return db;
    }

    /**
     * Returns the list of versions that are:
     * 1. internal, or
     * 2. database versions that have the data file and active
     **/
    public static List<MVersion> getAvailableVersions() {
        final List<MVersion> res = new ArrayList<>();

        // 1. Internal version
//        res.add(S.getMVersionInternal());

        // 2. Database versions
        for (MVersionDb mvDb : S.getDb().listAllVersions()) {
            if (mvDb.hasDataFile() /*&& mvDb.getActive()*/) {
                res.add(mvDb);
            }
        }

        // sort based on ordering
        Collections.sort(res, new Comparator<MVersion>() {
            @Override
            public int compare(MVersion lhs, MVersion rhs) {
                return lhs.ordering - rhs.ordering;
            }
        });

        return res;
    }

    /**
     * Get the internal version model. This does not return a singleton. The ordering is the latest taken from preferences.
     */
    public static MVersionInternal getMVersionInternal() {
        final AppConfig ac = AppConfig.get();
        final MVersionInternal res = new MVersionInternal();
        res.locale = ac.internalLocale;
        res.shortName = ac.internalShortName;
        res.longName = ac.internalLongName;
        res.description = null;
        res.ordering = Preferences.getInt(Prefkey.internal_version_ordering, MVersionInternal.DEFAULT_ORDERING);
        return res;
    }

    public interface VersionDialogListener {
        void onVersionSelected(MVersion mv);
    }

    public static void openVersionsDialog(final Activity activity, final boolean withNone, final String selectedVersionId, final VersionDialogListener listener) {
        final List<MVersion> versions = getAvailableVersions();

        if (withNone) {
            versions.add(0, null);
        }

        // determine the currently selected one
        int selected = -1;
        if (withNone && selectedVersionId == null) {
            selected = 0; // "none"
        } else {
            for (int i = (withNone ? 1 : 0) /* because 0 is None */; i < versions.size(); i++) {
                final MVersion mv = versions.get(i);
                if (mv.getVersionId().equals(selectedVersionId)) {
                    selected = i;
                    break;
                }
            }
        }

        final String[] options = new String[versions.size()];
        for (int i = 0; i < versions.size(); i++) {
            final MVersion version = versions.get(i);
            options[i] = version == null ? activity.getString(R.string.split_version_none) : version.longName;
        }
        int blackColor = ContextCompat.getColor(activity, R.color.black_999);
        int whiteColor = ContextCompat.getColor(activity, R.color.white);
        new MaterialDialog.Builder(activity)
                .items(options)
                .titleColor(ContextCompat.getColor(activity, R.color.black))
                .contentColor(blackColor)
                .title(R.string.recently_used)
                .itemsCallbackSingleChoice(selected, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        if (which == -1) {
                            // it is possible that 'which' is -1 in the case that
                            // a version is already deleted, but the current displayed version is that version
                            // (hence the initial selected item position is -1) and then the user
                            // presses the "other version" button. This callback will still be triggered
                            // before the positive button callback.
                        } else {
                            final MVersion mv = versions.get(which);
                            listener.onVersionSelected(mv);
                            dialog.dismiss();
                        }
                        return true;
                    }
                })
                .alwaysCallSingleChoiceCallback()
                .positiveText(R.string.download_other_version)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(final MaterialDialog dialog) {
                        activity.startActivity(VersionsActivity.createIntent());
                    }
                })
                .show();
    }

    public static String getVersionInitials(final Version version) {
        final String shortName = version.getShortName();
        if (shortName != null) {
            return shortName;
        } else {
            final String longName = version.getLongName();
            if (longName.length() <= 6) {
                return longName.toUpperCase();
            }

            // try to get the first letter of each word
            final String[] words = longName.split("[^A-Za-z0-9]+");
            final char[] chars = new char[words.length];
            int cnt = 0;
            for (int i = 0; i < chars.length; i++) {
                if (words[i].length() > 0) {
                    chars[cnt++] = Character.toUpperCase(words[i].charAt(0));
                }
            }
            return new String(chars, 0, cnt);
        }
    }
}
