package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.storage;

public class Db {
    public static final String TABLE_Marker = "Marker";

    public static final class Marker {
        public static final String gid = "gid";
        public static final String ari = "ari";
        public static final String kind = "kind";
        public static final String caption = "caption";
        public static final String verseCount = "verseCount";
        public static final String createTime = "createTime";
        public static final String modifyTime = "modifyTime";
    }

    public static final String TABLE_Version = "Version";

    public static final class Version {
        public static final String locale = "locale";
        public static final String shortName = "shortName";
        public static final String longName = "longName";
        public static final String description = "description";
        public static final String filename = "filename";
        public static final String preset_name = "preset_name";
        public static final String modifyTime = "modifyTime";
        public static final String active = "active";
        public static final String ordering = "ordering";
    }

    public static final String TABLE_ReadingPlan = "ReadingPlan";

    public static final class ReadingPlan {
        public static final String category_id = "category_id"; // category info
        public static final String title = "title";
        public static final String description = "description";
        public static final String small_icon_url = "small_icon_url";
        public static final String big_image_url = "big_image_url";
        public static final String planned_day_count = "planned_day_count";
        public static final String completed_day_count = "completed_day_count";
        public static final String start_time = "start_time";
        public static final String completed_time = "completed_time";
        public static final String extension_data = "extension_data";
        public static final String is_reminder_open = "is_reminder_open";
        public static final String reminder_time = "reminder_time";
        public static final String server_query_plan_id = "server_query_plan_id";
    }

    public static final String TABLE_ReadingDayPlan = "ReadingDayPlan";

    /**
     * Unique in (plan_id, day_index, progress_id)
     */
    public static final class ReadingDayPlan {
        public static final String plan_id = "plan_id";
        public static final String day_index = "day_index";
        public static final String progress_id = "progress_id";
    }


    public static final String TABLE_ReadingDayPlanProgress = "ReadingDayPlanProgress";

    public static final class ReadingDayPlanProgress {
        public static final String ari = "ari";
        public static final String verse_count = "verse_count";
        public static final String is_completed = "is_completed"; // 0: not , 1: yes
        public static final String extension_data = "extension_data";
    }


    public static final String TABLE_ChapterReadingProgress = "ChapterReadingProgress";

    public static final class ChapterReadingProgress {
        public static final String version_id = "version_id";
        public static final String book_id = "book_id";
        public static final String chapter_id = "chapter_id";
        public static final String is_completed = "is_completed";// 0: not , 1: yes
    }

    public static final String TABLE_Favorite = "favorite";

    public static final class Favorite {
        public static final int TYPE_DEVOTION = 1;
        public static final int TYPE_PRAYER = 2;

        public static final String type = "fav_type";  // 1: devotion , 2: prayer
        public static final String inner_id = "inner_id";
        public static final String json_data = "json_data";  // json data
        public static final String add_time = "add_time";
        public static final String extension_data = "extension_data";
    }

    public static final String TABLE_DevotionSubscribe = "DevotionSubscribe";

    public static final class DevotionSubscribe {
        public static final String subscribe_site_id = "site_id";
        public static final String subscribe_time = "subscribe_time";
        public static final String extension_data = "extension_data";
    }

    public static final String TABLE_DevotionReadHistory = "DevotionReadHistory";

    public static final class DevotionReadHistory {
        public static final String devotion_id = "devotion_id";
        public static final String devotion_date = "devotion_date";
        public static final String devotion_source_site = "devotion_source_site";
        public static final String read_time = "read_time";
        public static final String extension_data = "extension_data";
        public static final String is_read = "is_read";//unread 0   read 1
        public static final String is_like = "is_like";//like 0   dislike 1
    }

    public static final String TABLE_VerseLikeHistory = "VerseLikeHistory";

    public static final class VerseLikeHistory {
        public static final String verse_day = "verse_day";
        public static final String is_like = "is_like";
    }

    public static final String TABLE_UserDayOperations = "UserDayOperations";

    public static final class UserDayOperations {
        public static final String date = "date";
        public static final String operation_type = "operation_type";
        public static final String operation_count = "operation_count";
        public static final String extension_data = "extension_data";

        public static final int type_pray_count = 1;
        public static final int type_devotion_read_count = 2;
        public static final int type_read_bible_milliseconds = 3;
        public static final int type_daily_verse_read_count = 4;
        public static final int type_highlight_count = 5;
        public static final int type_bookmark_count = 6;
        public static final int type_note_count = 7;
        public static final int type_image_count = 8;
        public static final int type_plan_count = 9;
    }
}
