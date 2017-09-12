package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.storage;

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;


public class InternalDbHelper extends SQLiteOpenHelper {
    public static final String TAG = InternalDbHelper.class.getSimpleName();
    private static final String DATA_BASE_NAME = "AlkitabDb";
    private static int DB_VERSION = 8;

    //version 4 to version 5
    private static final String DATABASE_ALTER_4 = "ALTER TABLE "
            + Db.TABLE_ReadingPlan + " ADD COLUMN " + Db.ReadingPlan.server_query_plan_id + " INTEGER DEFAULT 0 ;";

    //version 6 to version 7
    private static final String DATABASE_ALTER_70 = "ALTER TABLE "
            + Db.TABLE_DevotionReadHistory + " ADD COLUMN " + Db.DevotionReadHistory.is_like + " INTEGER DEFAULT 0 ;";

    private static final String DATABASE_ALTER_71 = "ALTER TABLE "
            + Db.TABLE_DevotionReadHistory + " ADD COLUMN " + Db.DevotionReadHistory.is_read + " INTEGER DEFAULT 0 ;";

    public InternalDbHelper(Context context) {
        super(context, DATA_BASE_NAME, null, DB_VERSION);
        if (Build.VERSION.SDK_INT >= 16) {
            setWriteAheadLoggingEnabled(true);
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT < 16) {
            db.enableWriteAheadLogging();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "@@onCreate");

        createTableMarker(db);
        createIndexMarker(db);
        createTableReadingPlan(db);
        createTableReadingDayPlan(db);
        createTableReadingDayPlanProgress(db);
        createTableVersion(db);
        createIndexVersion(db);
        createTablePerVersion(db);
        createIndexPerVersion(db);
        createTableChapterReadingProgress(db);
        createTableFavorite(db);
        createTableDevotionSubscribe(db);
        createTableDevotionReadHistory(db);
        createTableVerseLikeHistory(db);
        createTableUserDayOperation(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "@@onUpgrade oldVersion=" + oldVersion + " newVersion=" + newVersion);

        if (oldVersion < 3) {
            // delete ableReadingPlan  TableReadingPlanProgress IndexReadingPlanProgress
            db.execSQL("DROP TABLE IF EXISTS Devotion");
            db.execSQL("DROP INDEX IF EXISTS index_Devotion_01");
            db.execSQL("DROP INDEX IF EXISTS index_Devotion_02");
            db.execSQL("DROP TABLE IF EXISTS Label");
            db.execSQL("DROP INDEX IF EXISTS index_401");
            db.execSQL("DROP INDEX IF EXISTS index_402");
            db.execSQL("DROP TABLE IF EXISTS Marker_Label");
            db.execSQL("DROP INDEX IF EXISTS index_Marker_Label_01");
            db.execSQL("DROP INDEX IF EXISTS index_Marker_Label_02");
            db.execSQL("DROP INDEX IF EXISTS index_Marker_Label_04");
            db.execSQL("DROP TABLE IF EXISTS ProgressMark");
            db.execSQL("DROP INDEX IF EXISTS index_601");
            db.execSQL("DROP TABLE IF EXISTS ProgressMarkHistory");
            db.execSQL("DROP INDEX IF EXISTS index_701");
            db.execSQL("DROP TABLE IF EXISTS SyncLog");
            db.execSQL("DROP INDEX IF EXISTS index_SyncLog_01");
            db.execSQL("DROP TABLE IF EXISTS SyncShadow");
            db.execSQL("DROP INDEX IF EXISTS index_SyncShadow_01");
            db.execSQL("DROP TABLE IF EXISTS " + Db.TABLE_ReadingPlan);
            db.execSQL("DROP TABLE IF EXISTS " + Db.TABLE_ReadingDayPlan);
            db.execSQL("DROP TABLE IF EXISTS " + Db.TABLE_ReadingDayPlanProgress);
            db.execSQL("DROP TABLE IF EXISTS ReadingPlanProgress"); // old table name
            db.execSQL("DROP INDEX IF EXISTS index_902"); // old index name

            createTableReadingPlan(db);
            createTableReadingDayPlan(db);
            createTableReadingDayPlanProgress(db);
        }

        if (oldVersion < 4) {
            createTableChapterReadingProgress(db);
        }

        if (oldVersion < 5) {
            db.execSQL(DATABASE_ALTER_4);
        }
        if (oldVersion < 6) {
            createTableFavorite(db);
            createTableDevotionSubscribe(db);
            createTableDevotionReadHistory(db);
        }
        if (oldVersion < 7) {
            if (oldVersion == 6) {
                db.execSQL(DATABASE_ALTER_70);
                db.execSQL(DATABASE_ALTER_71);
            }
            createTableVerseLikeHistory(db);
            createTableUserDayOperation(db);
        }
        if (oldVersion < 8) {
            //change table name.
            createTableFavorite(db);
        }
    }

    private void createTableMarker(SQLiteDatabase db) {
        db.execSQL(
                "create table if not exists " + Db.TABLE_Marker + " (" +
                        "_id integer primary key autoincrement, " +
                        Db.Marker.gid + " text," +
                        Db.Marker.ari + " integer, " +
                        Db.Marker.kind + " integer, " +
                        Db.Marker.caption + " text, " +
                        Db.Marker.verseCount + " integer, " +
                        Db.Marker.createTime + " integer, " +
                        Db.Marker.modifyTime + " integer" +
                        ")"
        );
    }

    private void createIndexMarker(SQLiteDatabase db) {
        db.execSQL("create index if not exists index_Marker_01 on " + Db.TABLE_Marker + " (" + Db.Marker.ari + ")");
        db.execSQL("create index if not exists index_Marker_02 on " + Db.TABLE_Marker + " (" + Db.Marker.kind + ", " + Db.Marker.ari + ")");
        db.execSQL("create index if not exists index_Marker_03 on " + Db.TABLE_Marker + " (" + Db.Marker.kind + ", " + Db.Marker.modifyTime + ")");
        db.execSQL("create index if not exists index_Marker_04 on " + Db.TABLE_Marker + " (" + Db.Marker.kind + ", " + Db.Marker.createTime + ")");
        db.execSQL("create index if not exists index_Marker_05 on " + Db.TABLE_Marker + " (" + Db.Marker.kind + ", " + Db.Marker.caption + " collate NOCASE)");
        db.execSQL("create index if not exists index_Marker_06 on " + Db.TABLE_Marker + " (" + Db.Marker.gid + ")");
    }

    private void createTablePerVersion(SQLiteDatabase db) {
        final StringBuilder sb = new StringBuilder("create table if not exists " + Table.PerVersion.tableName() + " ( _id integer primary key ");
        for (Table.PerVersion field : Table.PerVersion.values()) {
            sb.append(',');
            sb.append(field.name());
            sb.append(' ');
            sb.append(field.type.name());
            if (field.suffix != null) {
                sb.append(' ');
                sb.append(field.suffix);
            }
        }
        sb.append(")");
        db.execSQL(sb.toString());
    }

    private void createIndexPerVersion(SQLiteDatabase db) {
        db.execSQL("create unique index if not exists index_PerVersion_01 on " + Table.PerVersion.tableName() + " (" + Table.PerVersion.versionId + ")");
    }

    void createTableVersion(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + Db.TABLE_Version + " (" +
                "_id integer primary key autoincrement, " +
                Db.Version.locale + " text," +
                Db.Version.shortName + " text," +
                Db.Version.longName + " text," +
                Db.Version.description + " text," +
                Db.Version.filename + " text," +
                Db.Version.preset_name + " text," +
                Db.Version.modifyTime + " integer," +
                Db.Version.active + " integer," +
                Db.Version.ordering + " integer)"
        );
    }

    void createIndexVersion(SQLiteDatabase db) {
        db.execSQL("create index if not exists index_Version_01 on " + Db.TABLE_Version + " (" + Db.Version.ordering + ")");
        db.execSQL("create index if not exists index_Version_02 on " + Db.TABLE_Version + " (" + Db.Version.active + "," + Db.Version.longName + ")");
        db.execSQL("create index if not exists index_Version_03 on " + Db.TABLE_Version + " (" + Db.Version.preset_name + ")");
    }

    private void createTableReadingPlan(final SQLiteDatabase db) {
        db.execSQL("create table if not exists " + Db.TABLE_ReadingPlan + " (" +
                "_id integer primary key autoincrement, " +
                Db.ReadingPlan.category_id + " text, " +
                Db.ReadingPlan.title + " text, " +
                Db.ReadingPlan.description + " text, " +
                Db.ReadingPlan.small_icon_url + " text, " +
                Db.ReadingPlan.big_image_url + " text, " +
                Db.ReadingPlan.planned_day_count + " integer, " +
                Db.ReadingPlan.completed_day_count + " integer DEFAULT 0, " +
                Db.ReadingPlan.start_time + " long, " +
                Db.ReadingPlan.completed_time + " long DEFAULT 0, " +
                Db.ReadingPlan.extension_data + " text," +
                Db.ReadingPlan.is_reminder_open + " integer DEFAULT 0, " +
                Db.ReadingPlan.reminder_time + " integer DEFAULT 0, " +
                Db.ReadingPlan.server_query_plan_id + " integer DEFAULT 0 )"
        );
    }

    private void createTableReadingDayPlan(final SQLiteDatabase db) {
        db.execSQL("create table if not exists " + Db.TABLE_ReadingDayPlan + " (" +
                "_id integer primary key autoincrement, " +
                Db.ReadingDayPlan.plan_id + " integer, " +
                Db.ReadingDayPlan.day_index + " integer, " +
                Db.ReadingDayPlan.progress_id + " integer)");
    }

    private void createTableReadingDayPlanProgress(final SQLiteDatabase db) {
        db.execSQL("create table if not exists " + Db.TABLE_ReadingDayPlanProgress + " (" +
                "_id integer primary key autoincrement, " +
                Db.ReadingDayPlanProgress.ari + " integer, " +
                Db.ReadingDayPlanProgress.verse_count + " integer, " +
                Db.ReadingDayPlanProgress.is_completed + " integer DEFAULT 0, " +
                Db.ReadingDayPlanProgress.extension_data + " text)");
    }

    private void createTableChapterReadingProgress(final SQLiteDatabase db) {
        db.execSQL("create table if not exists " + Db.TABLE_ChapterReadingProgress + " (" +
                Db.ChapterReadingProgress.version_id + " text NOT NULL, " +
                Db.ChapterReadingProgress.book_id + " integer NOT NULL, " +
                Db.ChapterReadingProgress.chapter_id + " integer NOT NULL, " +
                Db.ChapterReadingProgress.is_completed + " integer DEFAULT 0," +
                "PRIMARY KEY (" + Db.ChapterReadingProgress.version_id + "," + Db.ChapterReadingProgress.book_id + "," + Db.ChapterReadingProgress.chapter_id + ") )"
        );
    }

    private void createTableFavorite(final SQLiteDatabase db) {
        db.execSQL("create table if not exists " + Db.TABLE_Favorite + " (" +
                "_id integer primary key autoincrement, " +
                Db.Favorite.type + " integer NOT NULL, " +
                Db.Favorite.inner_id + " long DEFAULT 0, " +
                Db.Favorite.json_data + " text NOT NULL, " +
                Db.Favorite.add_time + " long DEFAULT 0, " +
                Db.Favorite.extension_data + " text " +
                ")"
        );
    }

    private void createTableDevotionSubscribe(final SQLiteDatabase db) {
        db.execSQL("create table if not exists " + Db.TABLE_DevotionSubscribe + " (" +
                "_id integer primary key autoincrement, " +
                Db.DevotionSubscribe.subscribe_site_id + "  integer UNIQUE NOT NULL, " +
                Db.DevotionSubscribe.subscribe_time + " long NOT NULL, " +
                Db.DevotionSubscribe.extension_data + " text " +
                ")"
        );
    }

    private void createTableDevotionReadHistory(final SQLiteDatabase db) {
        db.execSQL("create table if not exists " + Db.TABLE_DevotionReadHistory + " (" +
                "_id integer primary key autoincrement, " +
                Db.DevotionReadHistory.devotion_id + "  integer UNIQUE NOT NULL, " +
                Db.DevotionReadHistory.devotion_date + " text NOT NULL," +
                Db.DevotionReadHistory.devotion_source_site + " text NOT NULL," +
                Db.DevotionReadHistory.read_time + " long NOT NULL, " +
                Db.DevotionReadHistory.is_like + " integer DEFAULT 0, " +
                Db.DevotionReadHistory.is_read + " integer DEFAULT 0, " +
                Db.DevotionReadHistory.extension_data + " text " +
                ")"
        );
    }

    private void createTableVerseLikeHistory(final SQLiteDatabase db) {
        db.execSQL("create table if not exists " + Db.TABLE_VerseLikeHistory + " (" +
                "_id integer primary key autoincrement, " +
                Db.VerseLikeHistory.verse_day + " text NOT NULL, " +
                Db.VerseLikeHistory.is_like + " integer DEFAULT 0 " +
                ")"
        );
    }

    private void createTableUserDayOperation(final SQLiteDatabase db) {
        db.execSQL("create table if not exists " + Db.TABLE_UserDayOperations + " (" +
                Db.UserDayOperations.date + " integer not null , " +
                Db.UserDayOperations.operation_type + " integer not null, " +
                Db.UserDayOperations.operation_count + " long DEFAULT 0, " +
                Db.UserDayOperations.extension_data + " text ," +
                "PRIMARY KEY (" + Db.UserDayOperations.date + "," + Db.UserDayOperations.operation_type + ") )"

        );
    }
}
