package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.storage;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.BuildConfig;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.DevotionBean;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.DevotionListResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.LockScreenResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PrayerBean;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PrayerResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.App;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.MVersion;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.MVersionDb;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.MVersionInternal;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.PerVersionSettings;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.ReadingPlan;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.DateTimeUtil;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.Highlights;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.Literals;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import yuku.afw.storage.Preferences;
import yuku.alkitab.model.Marker;
import yuku.alkitab.util.Ari;
import yuku.alkitab.util.IntArrayList;


@TargetApi(Build.VERSION_CODES.KITKAT)
public class InternalDb {
    public static final String TAG = InternalDb.class.getSimpleName();

    private final InternalDbHelper helper;

    public InternalDb(InternalDbHelper helper) {
        this.helper = helper;
    }

    /**
     * _id is not stored
     */
    private static ContentValues markerToContentValues(final Marker marker) {
        final ContentValues res = new ContentValues();

        res.put(Db.Marker.ari, marker.ari);
        res.put(Db.Marker.gid, marker.gid);
        res.put(Db.Marker.kind, marker.kind.code);
        res.put(Db.Marker.caption, marker.caption);
        res.put(Db.Marker.verseCount, marker.verseCount);
        res.put(Db.Marker.createTime, DateTimeUtil.toInt(marker.createTime));
        res.put(Db.Marker.modifyTime, DateTimeUtil.toInt(marker.modifyTime));

        return res;
    }

    public static Marker markerFromCursor(Cursor cursor) {
        final Marker res = Marker.createEmptyMarker();

        res._id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
        res.gid = cursor.getString(cursor.getColumnIndexOrThrow(Db.Marker.gid));
        res.ari = cursor.getInt(cursor.getColumnIndexOrThrow(Db.Marker.ari));
        res.kind = Marker.Kind.fromCode(cursor.getInt(cursor.getColumnIndexOrThrow(Db.Marker.kind)));
        res.caption = cursor.getString(cursor.getColumnIndexOrThrow(Db.Marker.caption));
        res.verseCount = cursor.getInt(cursor.getColumnIndexOrThrow(Db.Marker.verseCount));
        res.createTime = DateTimeUtil.toDate(cursor.getInt(cursor.getColumnIndexOrThrow(Db.Marker.createTime)));
        res.modifyTime = DateTimeUtil.toDate(cursor.getInt(cursor.getColumnIndexOrThrow(Db.Marker.modifyTime)));

        return res;
    }

    public Marker getMarkerById(long _id) {
        Cursor cursor = helper.getReadableDatabase().query(
                Db.TABLE_Marker,
                null,
                "_id=?",
                new String[]{String.valueOf(_id)},
                null, null, null
        );

        try {
            if (!cursor.moveToNext()) return null;
            return markerFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }

    @Nullable
    public Marker getMarkerByGid(@NonNull final String gid) {
        final Cursor cursor = helper.getReadableDatabase().query(Db.TABLE_Marker, null, Db.Marker.gid + "=?", Literals.Array(gid), null, null, null);

        try {
            if (!cursor.moveToNext()) return null;
            return markerFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }

    /**
     * Ordered by modified time, the newest is first.
     */
    public List<Marker> listMarkersForAriKind(final int ari, final Marker.Kind kind) {
        final SQLiteDatabase db = helper.getReadableDatabase();
        final Cursor c = db.query(Db.TABLE_Marker, null, Db.Marker.ari + "=? and " + Db.Marker.kind + "=?", Literals.ToStringArray(ari, kind.code), null, null, Db.Marker.modifyTime + " desc", null);
        try {
            final List<Marker> res = new ArrayList<>();
            while (c.moveToNext()) {
                res.add(markerFromCursor(c));
            }
            return res;
        } finally {
            c.close();
        }
    }

    /**
     * Insert a new marker or update an existing marker.
     *
     * @param marker if the _id is 0, this marker will be inserted. Otherwise, updated.
     */
    public void insertOrUpdateMarker(@NonNull final Marker marker) {
        final SQLiteDatabase db = helper.getWritableDatabase();
        if (marker._id != 0) {
            db.update(Db.TABLE_Marker, markerToContentValues(marker), "_id=?", Literals.Array(String.valueOf(marker._id)));
        } else {
            marker._id = db.insert(Db.TABLE_Marker, null, markerToContentValues(marker));
        }
    }

    public Marker insertMarker(int ari, Marker.Kind kind, String caption, int verseCount, Date createTime, Date modifyTime) {
        final Marker res = Marker.createNewMarker(ari, kind, caption, verseCount, createTime, modifyTime);
        final SQLiteDatabase db = helper.getWritableDatabase();

        res._id = db.insert(Db.TABLE_Marker, null, markerToContentValues(res));
        return res;
    }

    /**
     * Used in migration from v3
     */
    public static long insertMarker(final SQLiteDatabase db, final Marker marker) {
        marker._id = db.insert(Db.TABLE_Marker, null, markerToContentValues(marker));

        return marker._id;
    }

    public void deleteMarkerById(long _id) {
        final Marker marker = getMarkerById(_id);

        final SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        try {
            db.delete(Db.TABLE_Marker, "_id=?", new String[]{String.valueOf(_id)});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteNonBookmarkMarkerById(long _id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(Db.TABLE_Marker, "_id=?", new String[]{String.valueOf(_id)});
    }

    public List<Marker> listMarkers(Marker.Kind kind, long label_id, String sortColumn, boolean sortAscending) {
        final SQLiteDatabase db = helper.getReadableDatabase();
        final String sortClause = sortColumn + (Db.Marker.caption.equals(sortColumn) ? " collate NOCASE " : "") + (sortAscending ? " asc" : " desc");

        final List<Marker> res = new ArrayList<>();
        final Cursor c;
        if (label_id == 0) { // no restrictions
            c = db.query(Db.TABLE_Marker, null, Db.Marker.kind + "=?", new String[]{String.valueOf(kind.code)}, null, null, sortClause);
        } else {
            c = null;
        }

        try {
            while (c.moveToNext()) {
                res.add(markerFromCursor(c));
            }
        } finally {
            c.close();
        }

        return res;
    }

    public List<Marker> listAllMarkers() {
        final SQLiteDatabase db = helper.getReadableDatabase();
        final Cursor c = db.query(Db.TABLE_Marker, null, null, null, null, null, null);
        final List<Marker> res = new ArrayList<>();

        try {
            while (c.moveToNext()) {
                res.add(markerFromCursor(c));
            }
        } finally {
            c.close();
        }

        return res;
    }

    private SQLiteStatement stmt_countMarkersForBookChapter = null;

    public int countMarkersForBookChapter(int ari_bookchapter) {
        final int ariMin = ari_bookchapter & 0x00ffff00;
        final int ariMax = ari_bookchapter | 0x000000ff;

        if (stmt_countMarkersForBookChapter == null) {
            stmt_countMarkersForBookChapter = helper.getReadableDatabase().compileStatement("select count(*) from " + Db.TABLE_Marker + " where " + Db.Marker.ari + ">=? and " + Db.Marker.ari + "<?");
        }

        stmt_countMarkersForBookChapter.bindLong(1, ariMin);
        stmt_countMarkersForBookChapter.bindLong(2, ariMax);

        return (int) stmt_countMarkersForBookChapter.simpleQueryForLong();
    }


    /**
     * Put attributes (bookmark count, note count, and highlight color) for each verse.
     */
    public void putAttributes(final int ari_bookchapter, final int[] bookmarkCountMap, final int[] noteCountMap, final Highlights.Info[] highlightColorMap) {
        final int ariMin = ari_bookchapter & 0x00ffff00;
        final int ariMax = ari_bookchapter | 0x000000ff;

        final String[] params = {
                String.valueOf(ariMin),
                String.valueOf(ariMax),
        };

        // order by modifyTime, so in case a verse has more than one highlight, the latest one is shown
        final Cursor cursor = helper.getReadableDatabase().rawQuery("select * from " + Db.TABLE_Marker + " where " + Db.Marker.ari + ">=? and " + Db.Marker.ari + "<? order by " + Db.Marker.modifyTime, params);
        try {
            final int col_kind = cursor.getColumnIndexOrThrow(Db.Marker.kind);
            final int col_ari = cursor.getColumnIndexOrThrow(Db.Marker.ari);
            final int col_caption = cursor.getColumnIndexOrThrow(Db.Marker.caption);
            final int col_verseCount = cursor.getColumnIndexOrThrow(Db.Marker.verseCount);

            while (cursor.moveToNext()) {
                final int ari = cursor.getInt(col_ari);
                final int kind = cursor.getInt(col_kind);

                int mapOffset = Ari.toVerse(ari) - 1;
                if (mapOffset >= bookmarkCountMap.length) {
                    Log.e(TAG, "mapOffset too many " + mapOffset + " happens on ari 0x" + Integer.toHexString(ari));
                    continue;
                }

                if (kind == Marker.Kind.bookmark.code) {
                    bookmarkCountMap[mapOffset] += 1;
                } else if (kind == Marker.Kind.note.code) {
                    noteCountMap[mapOffset] += 1;
                } else if (kind == Marker.Kind.highlight.code) {
                    // traverse as far as verseCount
                    final int verseCount = cursor.getInt(col_verseCount);

                    for (int i = 0; i < verseCount; i++) {
                        int mapOffset2 = mapOffset + i;
                        if (mapOffset2 >= highlightColorMap.length)
                            break; // do not go past number of verses in this chapter

                        final String caption = cursor.getString(col_caption);
                        final Highlights.Info info = Highlights.decode(caption);

                        highlightColorMap[mapOffset2] = info;
                    }
                }
            }
        } finally {
            cursor.close();
        }
    }

    /**
     * @param colorRgb may NOT be -1. Use {@link #updateOrInsertHighlights(int, IntArrayList, int)} to delete highlight.
     */
    public void updateOrInsertPartialHighlight(final int ari, final int colorRgb, final CharSequence verseText, final int startOffset, final int endOffset) {
        final SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransactionNonExclusive();
        try {
            // order by modifyTime desc so we modify the latest one and remove earlier ones if they exist.
            final Cursor c = db.query(Db.TABLE_Marker, null, Db.Marker.ari + "=? and " + Db.Marker.kind + "=?", Literals.ToStringArray(ari, Marker.Kind.highlight.code), null, null, Db.Marker.modifyTime + " desc");
            try {
                final int hashCode = Highlights.hashCode(verseText.toString());
                final Date now = new Date();

                if (c.moveToNext()) { // check if marker exists
                    { // modify the latest one
                        final Marker marker = markerFromCursor(c);
                        marker.modifyTime = now;
                        marker.caption = Highlights.encode(colorRgb, hashCode, startOffset, endOffset);
                        db.update(Db.TABLE_Marker, markerToContentValues(marker), "_id=?", Literals.ToStringArray(marker._id));
                    }

                    // remove earlier ones if they exist (caused by sync)
                    while (c.moveToNext()) {
                        final long _id = c.getLong(c.getColumnIndexOrThrow("_id"));
                        db.delete(Db.TABLE_Marker, "_id=?", Literals.ToStringArray(_id));
                    }
                } else { // insert
                    final Marker marker = Marker.createNewMarker(ari, Marker.Kind.highlight, Highlights.encode(colorRgb, hashCode, startOffset, endOffset), 1, now, now);
                    db.insert(Db.TABLE_Marker, null, markerToContentValues(marker));
                }
            } finally {
                c.close();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void updateOrInsertHighlights(int ari_bookchapter, IntArrayList selectedVerses_1, int colorRgb) {
        final SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransactionNonExclusive();
        try {
            final String[] params = Literals.ToStringArray(null /* for the ari */, Marker.Kind.highlight.code);

            // every requested verses
            for (int i = 0; i < selectedVerses_1.size(); i++) {
                final int ari = Ari.encodeWithBc(ari_bookchapter, selectedVerses_1.get(i));
                params[0] = String.valueOf(ari);

                // order by modifyTime desc so we modify the latest one and remove earlier ones if they exist.
                final Cursor c = db.query(Db.TABLE_Marker, null, Db.Marker.ari + "=? and " + Db.Marker.kind + "=?", params, null, null, Db.Marker.modifyTime + " desc");
                try {
                    if (c.moveToNext()) { // check if marker exists
                        { // modify the latest one
                            final Marker marker = markerFromCursor(c);
                            marker.modifyTime = new Date();
                            if (colorRgb != -1) {
                                marker.caption = Highlights.encode(colorRgb);
                                db.update(Db.TABLE_Marker, markerToContentValues(marker), "_id=?", Literals.ToStringArray(marker._id));
                            } else {
                                // delete entry
                                db.delete(Db.TABLE_Marker, "_id=?", Literals.ToStringArray(marker._id));
                            }
                        }

                        // remove earlier ones if they exist (caused by sync)
                        while (c.moveToNext()) {
                            final long _id = c.getLong(c.getColumnIndexOrThrow("_id"));
                            db.delete(Db.TABLE_Marker, "_id=?", Literals.ToStringArray(_id));
                        }
                    } else {
                        if (colorRgb == -1) {
                            // no need to do, from no color to no color
                        } else {
                            final Date now = new Date();
                            final Marker marker = Marker.createNewMarker(ari, Marker.Kind.highlight, Highlights.encode(colorRgb), 1, now, now);
                            db.insert(Db.TABLE_Marker, null, markerToContentValues(marker));
                        }
                    }
                } finally {
                    c.close();
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Get the highlight color rgb of several verses.
     *
     * @return the color rgb or -1 if there are multiple colors.
     */
    public int getHighlightColorRgb(int ari_bookchapter, IntArrayList selectedVerses_1) {
        int ariMin = ari_bookchapter & 0xffffff00;
        int ariMax = ari_bookchapter | 0x000000ff;
        int[] colors = new int[256];
        int res = -2;

        for (int i = 0; i < colors.length; i++) colors[i] = -1;

        // check if exists
        final Cursor c = helper.getReadableDatabase().query(
                Db.TABLE_Marker, null, Db.Marker.ari + ">? and " + Db.Marker.ari + "<=? and " + Db.Marker.kind + "=?",
                new String[]{String.valueOf(ariMin), String.valueOf(ariMax), String.valueOf(Marker.Kind.highlight.code)},
                null, null, null
        );

        try {
            final int col_ari = c.getColumnIndexOrThrow(Db.Marker.ari);
            final int col_caption = c.getColumnIndexOrThrow(Db.Marker.caption);

            // put to array first
            while (c.moveToNext()) {
                int ari = c.getInt(col_ari);
                int index = ari & 0xff;
                final Highlights.Info info = Highlights.decode(c.getString(col_caption));
                colors[index] = info.colorRgb;
            }

            // determine default color. If all has color x, then it's x. If one of them is not x, then it's -1.
            for (int i = 0; i < selectedVerses_1.size(); i++) {
                int verse_1 = selectedVerses_1.get(i);
                int color = colors[verse_1];
                if (res == -2) {
                    res = color;
                } else if (color != res) {
                    return -1;
                }
            }

            if (res == -2) return -1;
            return res;
        } finally {
            c.close();
        }
    }

    /**
     * Get the highlight info for a single verse
     */
    public Highlights.Info getHighlightColorRgb(final int ari) {
        try (Cursor c = helper.getReadableDatabase().query(
                Db.TABLE_Marker, null, Db.Marker.ari + "=? and " + Db.Marker.kind + "=?",
                Literals.ToStringArray(ari, Marker.Kind.highlight.code),
                null,
                null,
                Db.Marker.modifyTime + " desc"
        )) {
            final int col_caption = c.getColumnIndexOrThrow(Db.Marker.caption);

            // put to array first
            if (c.moveToNext()) {
                return Highlights.decode(c.getString(col_caption));
            } else {
                return null;
            }
        }
    }

    public boolean deleteHighlight(final int ari) {
        try {
            int i = helper.getWritableDatabase().delete(
                    Db.TABLE_Marker,
                    Db.Marker.ari + "=? and " + Db.Marker.kind + "=?",
                    Literals.ToStringArray(ari, Marker.Kind.highlight.code)
            );
            return i > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public List<MVersionDb> listAllVersions() {
        Cursor cursor = helper.getReadableDatabase().query(Db.TABLE_Version, null, null, null, null, null, Db.Version.ordering + " asc");
        return getListVersionsByCursor(cursor);
    }

    public List<MVersionDb> listAllVersionsByLanguage(String language) {
        List<MVersionDb> res = new ArrayList<>();
        String fiter = Db.Version.locale + " = '" + language + "'";
        Cursor cursor = helper.getReadableDatabase().query(Db.TABLE_Version, null, fiter, null, null, null, Db.Version.ordering + " asc");
        return getListVersionsByCursor(cursor);
    }

    public List<MVersionDb> getListVersionsByCursor(Cursor cursor) {
        List<MVersionDb> res = new ArrayList<>();
        try {
            int col_locale = cursor.getColumnIndexOrThrow(Db.Version.locale);
            int col_shortName = cursor.getColumnIndexOrThrow(Db.Version.shortName);
            int col_longName = cursor.getColumnIndexOrThrow(Db.Version.longName);
            int col_description = cursor.getColumnIndexOrThrow(Db.Version.description);
            int col_filename = cursor.getColumnIndexOrThrow(Db.Version.filename);
            int col_preset_name = cursor.getColumnIndexOrThrow(Db.Version.preset_name);
            int col_modifyTime = cursor.getColumnIndexOrThrow(Db.Version.modifyTime);
            int col_active = cursor.getColumnIndexOrThrow(Db.Version.active);
            int col_ordering = cursor.getColumnIndexOrThrow(Db.Version.ordering);

            while (cursor.moveToNext()) {
                final MVersionDb mv = new MVersionDb();
                mv.locale = cursor.getString(col_locale);
                mv.shortName = cursor.getString(col_shortName);
                mv.longName = cursor.getString(col_longName);
                mv.description = cursor.getString(col_description);
                mv.filename = cursor.getString(col_filename);
                mv.preset_name = cursor.getString(col_preset_name);
                mv.modifyTime = cursor.getInt(col_modifyTime);
                mv.cache_active = cursor.getInt(col_active) != 0;
                mv.ordering = cursor.getInt(col_ordering);
                res.add(mv);
            }
        } finally {
            cursor.close();
        }
        return res;
    }

    public MVersionDb getMVersionDbByName(String name) {
        String fiter = Db.Version.longName + " = '" + name + "'";
        Cursor cursor = helper.getReadableDatabase().query(Db.TABLE_Version, null, fiter, null, null, null, Db.Version.ordering + " asc");
        try {
            int col_locale = cursor.getColumnIndexOrThrow(Db.Version.locale);
            int col_shortName = cursor.getColumnIndexOrThrow(Db.Version.shortName);
            int col_longName = cursor.getColumnIndexOrThrow(Db.Version.longName);
            int col_description = cursor.getColumnIndexOrThrow(Db.Version.description);
            int col_filename = cursor.getColumnIndexOrThrow(Db.Version.filename);
            int col_preset_name = cursor.getColumnIndexOrThrow(Db.Version.preset_name);
            int col_modifyTime = cursor.getColumnIndexOrThrow(Db.Version.modifyTime);
            int col_active = cursor.getColumnIndexOrThrow(Db.Version.active);
            int col_ordering = cursor.getColumnIndexOrThrow(Db.Version.ordering);

            while (cursor.moveToNext()) {
                final MVersionDb mv = new MVersionDb();
                mv.locale = cursor.getString(col_locale);
                mv.shortName = cursor.getString(col_shortName);
                mv.longName = cursor.getString(col_longName);
                mv.description = cursor.getString(col_description);
                mv.filename = cursor.getString(col_filename);
                mv.preset_name = cursor.getString(col_preset_name);
                mv.modifyTime = cursor.getInt(col_modifyTime);
                mv.cache_active = cursor.getInt(col_active) != 0;
                mv.ordering = cursor.getInt(col_ordering);
                return mv;
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    public void setVersionActive(MVersionDb mv, boolean active) {
        final SQLiteDatabase db = helper.getWritableDatabase();
        final ContentValues cv = new ContentValues();
        cv.put(Db.Version.active, active ? 1 : 0);

        if (mv.preset_name != null) {
            db.update(Db.TABLE_Version, cv, Db.Version.preset_name + "=?", new String[]{mv.preset_name});
        } else {
            db.update(Db.TABLE_Version, cv, Db.Version.filename + "=?", new String[]{mv.filename});
        }
    }

    public int getVersionMaxOrdering() {
        final SQLiteDatabase db = helper.getReadableDatabase();
        return (int) DatabaseUtils.longForQuery(db, "select max(" + Db.Version.ordering + ") from " + Db.TABLE_Version, null);
    }

    /**
     * If the filename of the inserted mv already exists in the table,
     * update is performed instead of an insert.
     * In that case, the mv.ordering will be changed to the one in the table,
     * and the passed-in mv.ordering will not be used.
     */
    public void insertOrUpdateVersionWithActive(MVersionDb mv, boolean active) {
        final SQLiteDatabase db = helper.getWritableDatabase();
        final ContentValues cv = new ContentValues();
        cv.put(Db.Version.locale, mv.locale);
        cv.put(Db.Version.shortName, mv.shortName);
        cv.put(Db.Version.longName, mv.longName);
        cv.put(Db.Version.description, mv.description);
        cv.put(Db.Version.filename, mv.filename);
        cv.put(Db.Version.preset_name, mv.preset_name);
        cv.put(Db.Version.modifyTime, mv.modifyTime);
        cv.put(Db.Version.active, active); // special
        cv.put(Db.Version.ordering, mv.ordering);

        db.beginTransactionNonExclusive();
        try { // prevent insert for the same filename (absolute path), update instead
            try (Cursor c = db.query(Db.TABLE_Version, Literals.Array("_id", Db.Version.ordering), Db.Version.filename + "=?", Literals.Array(mv.filename), null, null, null)) {
                if (c.moveToNext()) {
                    final long _id = c.getLong(0);
                    final int ordering = c.getInt(1);

                    mv.ordering = ordering;
                    cv.put(Db.Version.ordering, ordering);

                    db.update(Db.TABLE_Version, cv, "_id=?", Literals.ToStringArray(_id));
                } else {
                    db.insert(Db.TABLE_Version, null, cv);
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteVersion(MVersionDb mv) {
        final SQLiteDatabase db = helper.getWritableDatabase();

        // delete preset by preset_name
        if (mv.preset_name != null) {
            final int deleted = db.delete(Db.TABLE_Version, Db.Version.preset_name + "=?", new String[]{mv.preset_name});
            if (deleted > 0) {
                return; // finished! if not, we fallback to filename
            }
        }

        db.delete(Db.TABLE_Version, Db.Version.filename + "=?", new String[]{mv.filename});
    }

    public void reorderVersions(MVersion from, MVersion to) {
        // original order: A101 B[102] C103 D[104] E105

        // case: move up from=104 to=102:
        //   increase ordering for (to <= ordering < from)
        //   A101 B[103] C104 D[104] E105
        //   replace ordering of 'from' to 'to'
        //   A101 B[103] C104 D[102] E105

        // case: move down from=102 to=104:
        //   decrease ordering for (from < ordering <= to)
        //   A101 B[102] C102 D[103] E105
        //   replace ordering of 'from' to 'to'
        //   A101 B[104] C102 D[103] E105

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "@@reorderVersions from id=" + from.getVersionId() + " ordering=" + from.ordering + " to id=" + to.getVersionId() + " ordering=" + to.ordering);
        }

        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        try {
            {
                final int internal_ordering = Preferences.getInt(Prefkey.internal_version_ordering, MVersionInternal.DEFAULT_ORDERING);
                if (from.ordering > to.ordering) { // move up
                    db.execSQL("update " + Db.TABLE_Version + " set " + Db.Version.ordering + "=(" + Db.Version.ordering + "+1) where ?<=" + Db.Version.ordering + " and " + Db.Version.ordering + "<?", new Object[]{to.ordering, from.ordering});
                    if (to.ordering <= internal_ordering && internal_ordering < from.ordering) {
                        Preferences.setInt(Prefkey.internal_version_ordering, internal_ordering + 1);
                    }
                } else if (from.ordering < to.ordering) { // move down
                    db.execSQL("update " + Db.TABLE_Version + " set " + Db.Version.ordering + "=(" + Db.Version.ordering + "-1) where ?<" + Db.Version.ordering + " and " + Db.Version.ordering + "<=?", new Object[]{from.ordering, to.ordering});
                    if (from.ordering < internal_ordering && internal_ordering <= to.ordering) {
                        Preferences.setInt(Prefkey.internal_version_ordering, internal_ordering - 1);
                    }
                }
            }

            // both move up and move down arrives at this final step
            if (from instanceof MVersionDb) {
                db.execSQL("update " + Db.TABLE_Version + " set " + Db.Version.ordering + "=? where " + Db.Version.filename + "=?", new Object[]{to.ordering, ((MVersionDb) from).filename});
            } else if (from instanceof MVersionInternal) {
                Preferences.setInt(Prefkey.internal_version_ordering, to.ordering);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public long insertReadingPlan(final ReadingPlan.ReadingPlanInfo info) {
        final ContentValues cv = new ContentValues();
        cv.put(Db.ReadingPlan.category_id, info.mCategoryId);
        cv.put(Db.ReadingPlan.title, info.mTitle);
        cv.put(Db.ReadingPlan.description, info.mDescription);
        cv.put(Db.ReadingPlan.small_icon_url, info.mSmallIconUrl);
        cv.put(Db.ReadingPlan.big_image_url, info.mBigImageUrl);
        cv.put(Db.ReadingPlan.planned_day_count, info.mPlannedDayCount);
        cv.put(Db.ReadingPlan.start_time, info.mStartTime);
        cv.put(Db.ReadingPlan.extension_data, info.mExtensionData);
        cv.put(Db.ReadingPlan.is_reminder_open, info.mIsReminderOpen ? 1 : 0);
        cv.put(Db.ReadingPlan.reminder_time, info.mReminderTime);
        cv.put(Db.ReadingPlan.server_query_plan_id, info.mServerQueryId);
        final long dbPlanId = helper.getWritableDatabase().insert(Db.TABLE_ReadingPlan, null, cv);

        return dbPlanId;
    }

    public List<ReadingPlan.ReadingDayPlanInfo> insertReadingDayPlan(long planId, List<ReadingPlan.ReadingDayPlanInfo> dayList) {
        List<ReadingPlan.ReadingDayPlanInfo> newList = new ArrayList<>();
        for (int i = 0; i < dayList.size(); ++i) {
            ReadingPlan.ReadingDayPlanInfo dayPlanInfo = dayList.get(i);
            dayPlanInfo.mPlanId = (int) planId;
            dayPlanInfo.mId = insertReadingDayPlan(planId, dayPlanInfo);
            newList.add(dayPlanInfo);
        }

        return newList;
    }

    public void updateReadingPlanCompletedDayCount(final long planId, final int completedDayCount, boolean isCompletedTotalPlan) {
        final SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        try {
            String where = "_id=" + planId;
            final ContentValues cv = new ContentValues();
            cv.put(Db.ReadingPlan.completed_day_count, completedDayCount);
            cv.put(Db.ReadingPlan.completed_time, isCompletedTotalPlan ? System.currentTimeMillis() : 0);
            db.update(Db.TABLE_ReadingPlan, cv, where, null);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public long[] getPlanIdAndStartTimeByPlanTitle(final String planTitle) {
        long id = -1;
        long startTime = 0;
        String titleStr = planTitle;
        titleStr = Utility.sqliteEscape(titleStr);
        String where = Db.ReadingPlan.title + "='" + titleStr + "'";

        final Cursor c = helper.getReadableDatabase().query(Db.TABLE_ReadingPlan, new String[]{"_id", Db.ReadingPlan.start_time}, where, null, null, null, null);
        try {
            if (c.moveToNext()) {
                id = c.getLong(0);
                startTime = c.getLong(1);
            }
            return new long[]{id, startTime};
        } catch (Exception e) {
        } finally {
            c.close();
        }
        return new long[]{id, startTime};
    }

    private ReadingPlan.ReadingPlanInfo translateToReadingPlanInfo(Cursor c) {
        ReadingPlan.ReadingPlanInfo info = new ReadingPlan.ReadingPlanInfo();
        info.mDbPlanId = c.getLong(0);
        info.mCategoryId = c.getString(c.getColumnIndexOrThrow(Db.ReadingPlan.category_id));
        info.mTitle = c.getString(c.getColumnIndexOrThrow(Db.ReadingPlan.title));
        info.mDescription = c.getString(c.getColumnIndexOrThrow(Db.ReadingPlan.description));
        info.mSmallIconUrl = c.getString(c.getColumnIndexOrThrow(Db.ReadingPlan.small_icon_url));
        info.mBigImageUrl = c.getString(c.getColumnIndexOrThrow(Db.ReadingPlan.big_image_url));
        info.mPlannedDayCount = c.getInt(c.getColumnIndexOrThrow(Db.ReadingPlan.planned_day_count));
        info.mCompletedDayCount = c.getInt(c.getColumnIndexOrThrow(Db.ReadingPlan.completed_day_count));
        info.mStartTime = c.getLong(c.getColumnIndexOrThrow(Db.ReadingPlan.start_time));
        info.mCompletedTime = c.getLong(c.getColumnIndexOrThrow(Db.ReadingPlan.completed_time));
        info.mExtensionData = c.getString(c.getColumnIndexOrThrow(Db.ReadingPlan.extension_data));
        info.mIsReminderOpen = (c.getInt(c.getColumnIndexOrThrow(Db.ReadingPlan.is_reminder_open)) == 1);
        info.mReminderTime = c.getInt(c.getColumnIndexOrThrow(Db.ReadingPlan.reminder_time));
        info.mServerQueryId = c.getInt(c.getColumnIndexOrThrow(Db.ReadingPlan.server_query_plan_id));
        return info;
    }

    public ReadingPlan.ReadingPlanInfo getReadingPlanInfo(long planId) {
        String where = "_id=" + planId;
        final Cursor c = helper.getReadableDatabase().query(Db.TABLE_ReadingPlan, null, where, null, null, null, null);
        try {
            if (c.moveToNext()) {
                ReadingPlan.ReadingPlanInfo info = translateToReadingPlanInfo(c);
                return info;
            }
        } catch (Exception e) {
        } finally {
            c.close();
        }

        return null;
    }

    public int getAllReadingPlanCount() {
        String selection = Db.ReadingPlan.planned_day_count + " >= " + Db.ReadingPlan.completed_day_count;
        String orderBy = Db.ReadingPlan.reminder_time + " DESC";
        final Cursor c = helper.getReadableDatabase().query(Db.TABLE_ReadingPlan, new String[]{"_id"},
                selection, null, null, null, orderBy);
        int count = 0;
        if (c != null) {
            count = c.getCount();
            c.close();
        }

        return count;
    }

    public List<ReadingPlan.ReadingPlanInfo> listAllReadingPlanInfo() {
        List<ReadingPlan.ReadingPlanInfo> infos = new ArrayList<>();
        String selection = Db.ReadingPlan.planned_day_count + " >= " + Db.ReadingPlan.completed_day_count;
        String orderBy = Db.ReadingPlan.reminder_time + " DESC";
        final Cursor c = helper.getReadableDatabase().query(Db.TABLE_ReadingPlan, null,
                selection, null, null, null, orderBy);
        while (c.moveToNext()) {
            ReadingPlan.ReadingPlanInfo info = translateToReadingPlanInfo(c);
            infos.add(info);
        }
        c.close();
        return infos;
    }

    public List<ReadingPlan.ReadingPlanInfo> listAllCompletedPlan() {
        String selection = Db.ReadingPlan.planned_day_count + " = " + Db.ReadingPlan.completed_day_count;
        String orderBy = Db.ReadingPlan.completed_time + " DESC";
        final Cursor c = helper.getReadableDatabase().query(Db.TABLE_ReadingPlan, null,
                selection, null, null, null, orderBy);
        List<ReadingPlan.ReadingPlanInfo> readingPlanList = new ArrayList<>();
        while (c.moveToNext()) {
            ReadingPlan.ReadingPlanInfo info = translateToReadingPlanInfo(c);
            readingPlanList.add(info);
        }
        Collections.sort(readingPlanList, new Comparator<ReadingPlan.ReadingPlanInfo>() {
            @Override
            public int compare(ReadingPlan.ReadingPlanInfo lhs, ReadingPlan.ReadingPlanInfo rhs) {
                return (int) (rhs.mCompletedTime - lhs.mCompletedTime);
            }
        });
        c.close();
        return readingPlanList;
    }

    public ArrayList<String> getCateIdByPlanId(long planid) {
        ArrayList<String> list = new ArrayList<>();
        String selection = "_id = " + planid;
        final Cursor c = helper.getReadableDatabase().query(Db.TABLE_ReadingPlan, null,
                selection, null, null, null, null);
        while (c.moveToNext()) {
            String cateid = c.getString(1);
            String title = c.getString(2);
            list.add(cateid);
            list.add(title);
            return list;
        }
        c.close();
        return null;
    }

    public void updateReadingPlanReminderInfo(final long planId, boolean isReminderOpen, int reminderTime) {
        final SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        try {
            String where = "_id=" + planId;
            final ContentValues cv = new ContentValues();
            cv.put(Db.ReadingPlan.is_reminder_open, isReminderOpen ? 1 : 0);
            if (reminderTime > -1 && reminderTime < 2400) {
                cv.put(Db.ReadingPlan.reminder_time, reminderTime);
            }
            db.update(Db.TABLE_ReadingPlan, cv, where, null);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteReadingPlan(long planId) {
        final SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        try {
            String where = "_id=" + planId;
            db.delete(Db.TABLE_ReadingPlan, where, null);
            String whereDayPlan = Db.ReadingDayPlan.plan_id + "=" + planId;
            db.delete(Db.TABLE_ReadingDayPlan, whereDayPlan, null);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private long insertReadingDayPlan(final long planId, final ReadingPlan.ReadingDayPlanInfo info) {
        long res = -1;
        for (int i = 0; i < info.mDayPlanProgresses.size(); ++i) {
            long progressId = insertReadingDayPlanProgress(info.mDayPlanProgresses.get(i));
            final ContentValues cv = new ContentValues();
            cv.put(Db.ReadingDayPlan.plan_id, planId);
            cv.put(Db.ReadingDayPlan.progress_id, progressId);
            cv.put(Db.ReadingDayPlan.day_index, info.mDayIndex);
            res = helper.getWritableDatabase().insert(Db.TABLE_ReadingDayPlan, null, cv);
        }

        return res;
    }

    private List<Integer> getDayProgressIds(int planId, int dayIndex) {
        List<Integer> ids = new ArrayList<>();
        String where = "(" + Db.ReadingDayPlan.plan_id + "=" + planId + ") AND ("
                + Db.ReadingDayPlan.day_index + "=" + dayIndex + ")";
        final Cursor c = helper.getReadableDatabase().query(Db.TABLE_ReadingDayPlan, new String[]{Db.ReadingDayPlan.progress_id}, where, null, null, null, null);
        try {
            while (c.moveToNext()) {
                int id = (int) c.getLong(0);
                ids.add(id);
            }
        } catch (Exception e) {
        } finally {
            c.close();
        }
        return ids;
    }

    public void setDayPlanCompleted(int planId, int dayIndex, int completedDayCount, boolean isCompletedTotalPlan) {
        updateReadingPlanCompletedDayCount(planId, completedDayCount, isCompletedTotalPlan);

        List<Integer> allProgressIds = getDayProgressIds(planId, dayIndex);
        for (int i = 0; i < allProgressIds.size(); ++i) {
            updateReadingDayPlanProgress(allProgressIds.get(i), true);
        }
    }

    private long insertReadingDayPlanProgress(final ReadingPlan.ReadingDayPlanProgressInfo info) {
        final ContentValues cv = new ContentValues();
        cv.put(Db.ReadingDayPlanProgress.ari, info.mAri);
        cv.put(Db.ReadingDayPlanProgress.verse_count, info.mVerseCount);
        cv.put(Db.ReadingDayPlanProgress.extension_data, info.mExtensionData);

        final long progressId = helper.getWritableDatabase().insert(Db.TABLE_ReadingDayPlanProgress, null, cv);
        info.mId = progressId;
        return progressId;
    }

    public void updateReadingDayPlanProgress(final long progressId, final boolean isCompleted) {
        final SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        try {
            String where = "_id=" + progressId;
            final ContentValues cv = new ContentValues();
            cv.put(Db.ReadingDayPlanProgress.is_completed, isCompleted ? 1 : 0);
            db.update(Db.TABLE_ReadingDayPlanProgress, cv, where, null);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getDayPlan(final int planId) {
        String sql = "select * from " + Db.TABLE_ReadingDayPlan + " where " + Db.ReadingDayPlan.plan_id + "=" + planId;
        Cursor c = helper.getReadableDatabase().rawQuery(sql, null);
        return c;
    }

    public Cursor getDayPlan(final int planId, int dayIndexStart, int dayIndexEnd) {
        String sql = "select * from " + Db.TABLE_ReadingDayPlan
                + " where (" + Db.ReadingDayPlan.plan_id + "=" + planId
                + ") AND (" + Db.ReadingDayPlan.day_index + ">=" + dayIndexStart
                + ") AND (" + Db.ReadingDayPlan.day_index + "<=" + dayIndexEnd
                + ")";
        Cursor c = helper.getReadableDatabase().rawQuery(sql, null);
        return c;
    }

    public Cursor getDayProgress(final int progressId) {
        String sql = "select * from " + Db.TABLE_ReadingDayPlanProgress + " where _id=" + progressId;
        Cursor c = helper.getReadableDatabase().rawQuery(sql, null);
        return c;
    }

    /**
     * Deletes a marker by gid.
     *
     * @return true when deleted.
     */
    public boolean deleteMarkerByGid(final String gid) {
        final boolean deleted = helper.getWritableDatabase().delete(Db.TABLE_Marker, Db.Marker.gid + "=?", Literals.Array(gid)) > 0;
        if (deleted) {
        }
        return deleted;
    }

    @NonNull
    public PerVersionSettings getPerVersionSettings(@NonNull final String versionId) {
        try (Cursor c = helper.getReadableDatabase().query(Table.PerVersion.tableName(), Literals.ToStringArray(Table.PerVersion.settings), Table.PerVersion.versionId + "=?", Literals.Array(versionId), null, null, null)) {
            if (c.moveToNext()) {
                return App.getDefaultGson().fromJson(c.getString(0), PerVersionSettings.class);
            } else {
                return PerVersionSettings.createDefault();
            }
        }
    }

    public void storePerVersionSettings(@NonNull final String versionId, @NonNull PerVersionSettings settings) {
        final ContentValues cv = new ContentValues();
        cv.put(Table.PerVersion.versionId.name(), versionId);
        cv.put(Table.PerVersion.settings.name(), App.getDefaultGson().toJson(settings));

        helper.getWritableDatabase().replace(Table.PerVersion.tableName(), null, cv);
    }

    public long insertCompletedChapterProgress(String versionId, int bookId, int chapterId) {
        final ContentValues cv = new ContentValues();
        cv.put(Db.ChapterReadingProgress.version_id, versionId);
        cv.put(Db.ChapterReadingProgress.book_id, bookId);
        cv.put(Db.ChapterReadingProgress.chapter_id, chapterId);
        cv.put(Db.ChapterReadingProgress.is_completed, 1);

        final long progressId = helper.getWritableDatabase().insert(Db.TABLE_ChapterReadingProgress, null, cv);
        return progressId;
    }

    public HashMap<Integer, Integer> getBooksProgress(String versionId) {
        HashMap<Integer, Integer> booksProgress = new HashMap<>();
        String[] columns = new String[]{Db.ChapterReadingProgress.book_id, "count(" + Db.ChapterReadingProgress.chapter_id + ")"};
        String selection = "(" + Db.ChapterReadingProgress.version_id + "='" + versionId + "') AND (" + Db.ChapterReadingProgress.is_completed + "=1)";
        String groupBy = Db.ChapterReadingProgress.book_id;
        try (Cursor c = helper.getReadableDatabase().query(Db.TABLE_ChapterReadingProgress, columns, selection, null, groupBy, null, null)) {
            while (c.moveToNext()) {
                int bookId = c.getInt(0);
                int completedChapterCount = c.getInt(1);
                booksProgress.put(bookId, completedChapterCount);
            }
        }
        return booksProgress;
    }

    public int getBooksCompletedChapterCount(String versionId, int bookId) {
        String[] columns = new String[]{Db.ChapterReadingProgress.book_id, "count(" + Db.ChapterReadingProgress.chapter_id + ")"};
        String selection = "(" + Db.ChapterReadingProgress.version_id + "='" + versionId + "') AND (" + Db.ChapterReadingProgress.is_completed + "=1 " + ") AND (" + Db.ChapterReadingProgress.book_id + "= '" + bookId + "')";
        try (Cursor c = helper.getReadableDatabase().query(Db.TABLE_ChapterReadingProgress, columns, selection, null, null, null, null)) {
            while (c.moveToNext()) {
                return c.getInt(1);
            }
        }
        return 0;
    }

    public HashSet<Integer> getCompletedChapterId(String versionId, int bookId) {
        HashSet<Integer> completedChapterIds = new HashSet<>();
        String[] columns = new String[]{Db.ChapterReadingProgress.chapter_id};
        String selection = "(" + Db.ChapterReadingProgress.version_id + "='" + versionId + "') AND ("
                + Db.ChapterReadingProgress.book_id + "=" + bookId + ") AND ("
                + Db.ChapterReadingProgress.is_completed + "=1)";
        try (Cursor c = helper.getReadableDatabase().query(Db.TABLE_ChapterReadingProgress, columns, selection, null, null, null, null)) {
            while (c.moveToNext()) {
                int chapterId = c.getInt(0);
                completedChapterIds.add(chapterId);
            }
        }
        return completedChapterIds;
    }

    public List<DevotionBean> getDevotionFavoriteList() {
        List<DevotionBean> favoriteList = new ArrayList();
        String[] columns = new String[]{Db.Favorite.json_data};
        String orderBy = Db.Favorite.add_time + " DESC";
        String where = Db.Favorite.type + " = " + Db.Favorite.TYPE_DEVOTION;
        try (Cursor c = helper.getReadableDatabase().query(Db.TABLE_Favorite, columns, where, null, null, null, orderBy)) {
            while (c.moveToNext()) {
                String json = c.getString(0);
                if (!TextUtils.isEmpty(json)) {
                    DevotionBean listsBean = App.getDefaultGson().fromJson(json, DevotionBean.class);
                    if (listsBean != null) {
                        favoriteList.add(listsBean);
                    }
                }
            }
        }
        return favoriteList;
    }

    public List<PrayerBean> getPrayFavoriteList() {
        List<PrayerBean> favoriteList = new ArrayList();
        String[] columns = new String[]{Db.Favorite.json_data};
        String orderBy = Db.Favorite.add_time + " DESC" + " ";
        String where = Db.Favorite.type + " = " + Db.Favorite.TYPE_PRAYER;
        try (Cursor c = helper.getReadableDatabase().query(Db.TABLE_Favorite, columns, where, null, null, null, orderBy)) {
            while (c.moveToNext()) {
                String json = c.getString(0);
                if (!TextUtils.isEmpty(json)) {
                    PrayerBean listsBean = App.getDefaultGson().fromJson(json, PrayerBean.class);
                    if (listsBean != null) {
                        favoriteList.add(listsBean);
                    }
                }
            }
        }
        return favoriteList;
    }

    public boolean isInFavorite(int type, int innerId) {
        boolean exist = false;
        String[] columns = new String[]{Db.Favorite.inner_id};
        String selection = "(" + Db.Favorite.type + "=" + type + ") AND ("
                + Db.Favorite.inner_id + "=" + innerId + ")";
        try (Cursor c = helper.getReadableDatabase().query(Db.TABLE_Favorite, columns, selection, null, null, null, null)) {
            if (c.getCount() > 0) {
                exist = true;
            }
        }

        return exist;
    }

    public long addFavoritePrayer(PrayerBean pray) {
        long res = -1;
        final ContentValues cv = new ContentValues();
        cv.put(Db.Favorite.type, Db.Favorite.TYPE_PRAYER);
        cv.put(Db.Favorite.inner_id, pray.getId());
        String jsonString = App.getDefaultGson().toJson(pray);
        cv.put(Db.Favorite.json_data, jsonString);
        cv.put(Db.Favorite.add_time, System.currentTimeMillis());
        res = helper.getWritableDatabase().insert(Db.TABLE_Favorite, null, cv);
        return res;
    }

    public long addFavoriteDevotion(DevotionBean devotionBean) {
        long res = -1;
        final ContentValues cv = new ContentValues();
        cv.put(Db.Favorite.type, Db.Favorite.TYPE_DEVOTION);
        cv.put(Db.Favorite.inner_id, devotionBean.getId());
        String jsonString = App.getDefaultGson().toJson(devotionBean);
        cv.put(Db.Favorite.json_data, jsonString);
        cv.put(Db.Favorite.add_time, System.currentTimeMillis());
        res = helper.getWritableDatabase().insert(Db.TABLE_Favorite, null, cv);

        return res;
    }

    public long removeFavoriteData(int type, int id) {
        long res = -1;
        String where = Db.Favorite.type + " = " + type + " AND " + Db.Favorite.inner_id + " = " + id;
        res = helper.getWritableDatabase().delete(Db.TABLE_Favorite, where, null);
        return res;
    }

    public boolean isInDevotionSubscribe(int site_id) {
        boolean exist = false;
        String[] columns = new String[]{Db.DevotionSubscribe.subscribe_site_id};
        String selection = "(" + Db.DevotionSubscribe.subscribe_site_id + "=" + site_id + ")";
        try (Cursor c = helper.getReadableDatabase().query(Db.TABLE_DevotionSubscribe, columns, selection, null, null, null, null)) {
            if (c.getCount() > 0) {
                exist = true;
            }
        }
        return exist;
    }

    public long addDevotionSiteSubscribe(int site_id) {
        if (isInDevotionSubscribe(site_id)) {
            return 0;
        }
        final ContentValues cv = new ContentValues();
        cv.put(Db.DevotionSubscribe.subscribe_site_id, site_id);
        cv.put(Db.DevotionSubscribe.subscribe_time, System.currentTimeMillis());
        Long res = helper.getWritableDatabase().insert(Db.TABLE_DevotionSubscribe, null, cv);
        return res;
    }

    public void deleteDevotionSiteSubscribeById(long site_id) {

        final SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransactionNonExclusive();
        try {
            db.delete(Db.TABLE_DevotionSubscribe, "site_id=?", new String[]{String.valueOf(site_id)});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public List<Integer> listAllSubscribeDevotion() {
        String orderBy = Db.DevotionSubscribe.subscribe_time + " ASC";
        final Cursor c = helper.getReadableDatabase().query(Db.TABLE_DevotionSubscribe, null,
                null, null, null, null, orderBy);
        List<Integer> list = new ArrayList<>();
        while (c.moveToNext()) {
            int site = c.getInt(c.getColumnIndexOrThrow(Db.DevotionSubscribe.subscribe_site_id));
            list.add(site);
        }
        return list;
    }

    public List<Integer> listAllDevotionHistory() {
        String orderBy = Db.DevotionReadHistory.devotion_id + " ASC";
        String where = Db.DevotionReadHistory.is_read + " = " + 1;
        final Cursor c = helper.getReadableDatabase().query(Db.TABLE_DevotionReadHistory, null,
                where, null, null, null, orderBy);
        List<Integer> list = new ArrayList<>();
        while (c.moveToNext()) {
            int site = c.getInt(c.getColumnIndexOrThrow(Db.DevotionReadHistory.devotion_id));
            list.add(site);
        }
        return list;
    }

    public List<Integer> listAllDevotionHistoryByDevotionSource(String devotion_source_site) {
        String orderBy = Db.DevotionReadHistory.devotion_id + " ASC";
        String where = "(" + Db.DevotionReadHistory.devotion_source_site + "='" + devotion_source_site + "' AND "
                + Db.DevotionReadHistory.is_read + " = " + 1 + ")";
        final Cursor c = helper.getReadableDatabase().query(Db.TABLE_DevotionReadHistory, null,
                where, null, null, null, orderBy);
        List<Integer> list = new ArrayList<>();
        while (c.moveToNext()) {
            int site = c.getInt(c.getColumnIndexOrThrow(Db.DevotionReadHistory.devotion_id));
            list.add(site);
        }
        return list;
    }

    public List<Integer> listAllDevotionHistoryByDevotionDate(String devotion_date) {
        String orderBy = Db.DevotionReadHistory.devotion_id + " ASC";
        String where = "(" + Db.DevotionReadHistory.devotion_date + "='" + devotion_date + "' AND " +
                Db.DevotionReadHistory.is_read + " = " + 1 + ")";
        final Cursor c = helper.getReadableDatabase().query(Db.TABLE_DevotionReadHistory, null,
                where, null, null, null, orderBy);
        List<Integer> list = new ArrayList<>();
        while (c.moveToNext()) {
            int site = c.getInt(c.getColumnIndexOrThrow(Db.DevotionReadHistory.devotion_id));
            list.add(site);
        }
        return list;
    }

    public boolean isInDevotionReadHistory(int devotion_id) {
        boolean exist = false;
        String[] columns = new String[]{Db.DevotionReadHistory.devotion_id};
        String selection = "(" + Db.DevotionReadHistory.devotion_id + "=" + devotion_id + " AND " +
                Db.DevotionReadHistory.is_read + "=" + 1 + ")";
        try (Cursor c = helper.getReadableDatabase().query(Db.TABLE_DevotionReadHistory, columns, selection, null, null, null, null)) {
            if (c.getCount() > 0) {
                exist = true;
            }
        }
        return exist;
    }

    public long addNewDevotionReadHistory(int devotionId, String devotionDate, String devotionSite) {
        if (isInDevotionReadHistory(devotionId)) {
            return 0;
        }
        final ContentValues cv = new ContentValues();
        cv.put(Db.DevotionReadHistory.devotion_id, devotionId);
        cv.put(Db.DevotionReadHistory.devotion_date, devotionDate);
        cv.put(Db.DevotionReadHistory.devotion_source_site, devotionSite);
        cv.put(Db.DevotionReadHistory.read_time, System.currentTimeMillis());
        cv.put(Db.DevotionReadHistory.is_read, 1);
        Long res = helper.getWritableDatabase().insert(Db.TABLE_DevotionReadHistory, null, cv);
        return res;
    }

    public long getAllDevotionReadHistoryCount() {
        int count = 0;
        try {
            String where = Db.DevotionReadHistory.is_read + " = " + 1;
            final Cursor c = helper.getReadableDatabase().query(Db.TABLE_DevotionReadHistory, null, where, null, null, null, null);
            if (c != null) {
                count = c.getCount();
            }
        } catch (Exception e) {
        }
        return count;
    }

    public List<String> listAllVerseLikeHistory() {
        String where = Db.VerseLikeHistory.is_like + " = " + 1;
        final Cursor c = helper.getReadableDatabase().query(Db.TABLE_VerseLikeHistory, null,
                where, null, null, null, null);
        List<String> list = new ArrayList<>();
        while (c.moveToNext()) {
            list.add(c.getString(c.getColumnIndexOrThrow(Db.VerseLikeHistory.verse_day)));
        }
        return list;
    }

    public long addVerseLikeHistory(String day, boolean isLike) {
        if (isInVerseLikeHistory(day)) {
            return 0;
        }
        final ContentValues cv = new ContentValues();
        cv.put(Db.VerseLikeHistory.verse_day, day);
        cv.put(Db.VerseLikeHistory.is_like, isLike ? 1 : 0);
        Long res = helper.getWritableDatabase().insert(Db.TABLE_VerseLikeHistory, null, cv);
        return res;
    }

    public boolean isInVerseLikeHistory(String day) {
        boolean exist = false;
        String[] columns = new String[]{Db.VerseLikeHistory.verse_day};
        String selection = "(" + Db.VerseLikeHistory.verse_day + "='" + day + "')";
        try (Cursor c = helper.getReadableDatabase().query(Db.TABLE_VerseLikeHistory, columns, selection, null, null, null, null)) {
            if (c.getCount() > 0) {
                exist = true;
            }
        }
        return exist;
    }


    public long getTodayUserOperationDataByType(int type) {
        String where = "(" + Db.UserDayOperations.date + "=" + DateTimeUtil.getTodayDate() + " and " + Db.UserDayOperations.operation_type + " = " + type + ")";
        Cursor c = helper.getReadableDatabase().query(Db.TABLE_UserDayOperations, null, where, null, null, null, null);
        if (c != null && c.getCount() == 1) {
            c.moveToNext();
            long curCount = c.getLong(c.getColumnIndex(Db.UserDayOperations.operation_count));
            c.close();
            return curCount;
        }
        return -1;
    }

    public void addTodayBibleReadTime(long milliseconds) {
        addTodayOperationDataByType(milliseconds, Db.UserDayOperations.type_read_bible_milliseconds);
    }

    public void addTodayPrayerCount(int increment) {
        addTodayOperationDataByType(increment, Db.UserDayOperations.type_pray_count);
    }

    public void addTodayReadDevotionCount(int increment) {
        addTodayOperationDataByType(increment, Db.UserDayOperations.type_devotion_read_count);
    }

    public void addTodayDailyVerseReadCount(int increment) {
        addTodayOperationDataByType(increment, Db.UserDayOperations.type_daily_verse_read_count);
    }

    private void addTodayOperationDataByType(long increment, int operationType) {
        long curCount = getTodayUserOperationDataByType(operationType);
        boolean exist = (curCount != -1);
        curCount = exist ? curCount : 0;

        int date = DateTimeUtil.getTodayDate();
        final ContentValues cv = new ContentValues();
        cv.put(Db.UserDayOperations.operation_count, curCount + increment);
        if (exist) {
            String where = "(" + Db.UserDayOperations.date + "=" + date + " and " + Db.UserDayOperations.operation_type + " = " + operationType + ")";
            helper.getWritableDatabase().update(Db.TABLE_UserDayOperations, cv, where, null);
        } else {
            cv.put(Db.UserDayOperations.operation_type, operationType);
            cv.put(Db.UserDayOperations.date, date);
            helper.getWritableDatabase().insert(Db.TABLE_UserDayOperations, null, cv);
        }
    }

    public long getAllPrayerHistoryCount() {
        return getAllOperationHistoryCountByType(Db.UserDayOperations.type_pray_count);
    }

    public long getReadBibleTotalTime() {
        return getAllOperationHistoryCountByType(Db.UserDayOperations.type_read_bible_milliseconds);
    }

    public long getAllOperationHistoryCountByType(int type) {
        int count = 0;
        final Cursor c = helper.getReadableDatabase().rawQuery("SELECT SUM(" + Db.UserDayOperations.operation_count + ") FROM " + Db.TABLE_UserDayOperations + " where " + Db.UserDayOperations.operation_type + " = " + type, null);
        c.moveToFirst();
        count = c.getInt(0);
        return count;
    }


    // Do not use this except in rare circumstances
    public SQLiteDatabase getWritableDatabase() {
        return helper.getWritableDatabase();
    }

    public long addDevotionLikeHistory(LockScreenResponse.DataBean.ListsBean listsBean, boolean isLike) {
        long res = -1;
        final ContentValues cv = new ContentValues();
        cv.put(Db.DevotionReadHistory.is_like, isLike ? 0 : 1);
        if (isInDevotionLikeHistory(listsBean.getId())) {
            String where = Db.DevotionReadHistory.devotion_id + " = " + listsBean.getId();
            res = helper.getWritableDatabase().update(Db.TABLE_DevotionReadHistory, cv, where, null);
        } else {
            cv.put(Db.DevotionReadHistory.devotion_id, listsBean.getId());
            cv.put(Db.DevotionReadHistory.devotion_date, listsBean.getDate());
            cv.put(Db.DevotionReadHistory.devotion_source_site, listsBean.getSource());
            cv.put(Db.DevotionReadHistory.read_time, System.currentTimeMillis());
            cv.put(Db.DevotionReadHistory.is_read, 0);
            res = helper.getWritableDatabase().insert(Db.TABLE_DevotionReadHistory, null, cv);
            Log.e("zf_bug", "insert res:" + res);
        }
        return res;
    }

    public boolean isInDevotionLikeHistory(int devotionId) {
        boolean exist = false;
        String selection = Db.DevotionReadHistory.devotion_id + " = " + devotionId;
        try {
            Cursor c = helper.getReadableDatabase().query(Db.TABLE_DevotionReadHistory, null, selection, null, null, null, null);
            if (c != null && c.getCount() > 0) {
                exist = true;
            }
        } catch (Exception e) {
        }
        return exist;
    }

    public List<Integer> listAllDisLikeDevotionHistory() {
        String orderBy = Db.DevotionReadHistory.devotion_id + " ASC";
        String where = Db.DevotionReadHistory.is_like + " = " + 1;
        final Cursor c = helper.getReadableDatabase().query(Db.TABLE_DevotionReadHistory, null,
                where, null, null, null, orderBy);
        List<Integer> list = new ArrayList<>();
        while (c.moveToNext()) {
            int site = c.getInt(c.getColumnIndexOrThrow(Db.DevotionReadHistory.devotion_id));
            list.add(site);
        }
        return list;
    }

}
