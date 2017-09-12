package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util;

/**
 * Created by zhangfei on 10/25/16.
 */
public class Constants {

    //base
    public static final String API_BUSINESS_URL = "http://api.freereadbible.com";
    public static final String API_PRAYER_DOWNLOAD_URL = "http://files.freereadbible.com/music/bg/Abide_With_Me.m4a";

    public static final String TERMS_OF_USER_URL = "http://www.freereadbible.com/privacy.html";
    public static final String FEEDBACK_EMAIL = "holybiblebooks.fb@gmail.com";
    public static final String GOOGLE_PLAY_URL = "https://play.google.com/store/apps/details?id=bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion";
    public static final String GOOGLE_SHORT_URL = "https://goo.gl/W0yWQN";

    public static final String IS_SHOW_DOWNLOAD_NOTICE = "is_show_download_notice";
    public static final String LAST_SHOW_DAILY_VERSE_TIME = "last_show_daily_verse_time";


    //notify
    public static final int NOTIFY_DAILY_NOTIFY_ID = 111;//111,113,114 merge as 111

    public static final int NOTIFY_DAILY_VERSE_ID = 111;
    public static final int NOTIFY_BIBLE_PLAN_ID = 112;
    public static final int NOTIFY_DAILY_PRAYER_ID = 113;
    public static final int NOTIFY_DAILY_SIGN_IN_ID = 114;
    public static final int NOTIFY_DAILY_DEVOTION_ID = 115;

    //notify alarm
    public static final String DAILY_VERSE_NOTIFICATION_HOUR = "2000";
    public static final String DAILY_NOTIFICATION_OFF = "-1";
    public static final String DAILY_PRAYER_ALARM_BASIC_REQUEST_CODE = "200";
    public static final String DAILY_SIGNIN_ALARM_BASIC_REQUEST_CODE = "300";
    public static final String DAILY_DEVOTION_ALARM_BASIC_REQUEST_CODE = "400";

    public static final String DAILY_MORNING_PRAYER_NOTIFICATION_CONFIG = "0800";
    public static final String DAILY_EVENING_PRAYER_NOTIFICATION_CONFIG = "2200";
    public static final String DAILY_SIGNIN_NOTIFICATION_CONFIG = "1300";
    public static final String DAILY_DEVOTION_NOTIFICATION_CONFIG = "1800";


    public static final String IMAGE_SAVE_PATH = "image";

    //plan
    public static final String KEY_SERVER_QUERY_PLAN_ID = "server_query_plan_id"; // id in server
    public static final String KEY_DB_PLAN_ID = "db_plan_id"; // id in db
    public static final String KEY_PLAN_GRID_LIST_TYPE = "plan_grid_type";
    public static final String KEY_PLAN_CATEGORY_ID = "plan_cat_id";
    public static final String KEY_PLAN_CATEGORY_NAME = "plan_cat_name";
    public static final String KEY_PLAN_DAY = "plan_day";
    public static final String KEY_PLAN_NAME = "plan_name";
    public static final String KEY_PLAN_BIG_IMAGE = "plan_big_image";
    public static final String KEY_PLAN_ICON = "plan_icon";
    public static final int TYPE_COMPLETE_PLANS = 0;
    public static final int TYPE_CATEGORY_PLANS = 1;

    public static final String KEY_VERSE_LIST = "verse_list";
    public static final String KEY_VERSE_INDEX_IN_LIST = "verse_index";
    public static final String KEY_CURRENT_DAY = "key_current_day";
    public static final String KEY_PLANNED_DAY_COUNT = "key_planned_day_count";
    public static final String KEY_ALREADY_FINISH_CUR_DAY = "KEY_ALREADY_FINISH_CUR_DAY";
    public static final String KEY_CURRENT_COMPLETED_DAY_COUNT = "key_current_completed_day_count";
    public static final String KEY_COMPLETED_TOTAL_PLAN = "key_completed_total_plan";
    public static final String KEY_READ_FINISH_PLAN_ID = "key_read_finish_plan_id";
    public static final String KEY_LAST_DAILY_NOTIFY_CONFIG_REQUEST_TIME = "key_last_notify_config_time";
    public static final String KEY_WELCOME_DEVOTION_COUNT = "key_welcome_devotion_count";
    public static final String KEY_WELCOME_PRAY_COUNT = "key_welcome_pray_count";
    public static final String KEY_WELCOME_READ_TIME = "key_welcome_read_time";

    public final static long MILlI_SECONDS_OF_A_DAY = 86400000;

    // notification
    public static final String KEY_NOTIFICATION_TYPE = "notification_type";
    public static final String KEY_NOTIFICATION_ID = "notification_id";
    public static final int TYPE_SYSTEM_NOTIFICATION = 1;
    public static final int TYPE_FLOATING_NOTIFICATION = 2;


    // broadcast
    public static final String ACTION_ATTRIBUTE_MAP_CHANGED = "com.wm.freebible.action.ATTRIBUTE_MAP_CHANGED";
    public static final String ACTION_ACTIVE_VERSION_CHANGED = "com.wm.freebible.action.ACTIVE_VERSION_CHANGED";
    public static final String ACTION_NIGHT_MODE_CHANGED = "com.wm.freebible.action.NIGHT_MODE_CHANGED";
    public static final String ACTION_NEEDS_RESTART = "com.wm.freebible.action.NEEDS_RESTART";
    public static final String ACTION_VERSION_LIST_RELOAD = "com.wm.freebible.action.reload";
    public static final String ACTION_VERSION_DOWNLOAD_FINISH = "com.wm.freebible.action.download.Finish";

    //finish page
    public static final String KEY_READING_FINISH_PAGE_TYPE = "finish_page_type";
    public static final String KEY_READING_PROGRESS = "reading_progress";
    public static final String KEY_BOOK_NAME = "book_name";
    public static final String KEY_BOOK_REFERENCE = "book_reference";

    //ad
    public static final int PLAN_DISCOVER_CATE_ID = 202;

    public static final String KEY_SOURCE_TYPE = "sourceType";
    public static final int KEY_TYPE_FROM_WELCOME = 1;

    public static final String KEY_RATING_GUIDE = "RATING_GUIDE";


    // paid analysis
    public static final String PAGE_FROM = "from";
    public static final String FROM_TOP_MENU = "top_menu";
    public static final String FROM_LEFT_DRAWER = "left_drawer";
    public static final String FROM_REMOVE_AD = "remove_ad";
    public static final String FROM_WELCOME_FS = "welcome_fs";
    public static final String FROM_FINISH_FS = "finish_fs";
    public static final String FROM_PRAYER_FINISH = "prayer_finish";

    public static final String PRAYER_AUDIO_FILE_NAME = "Abide_With_Me.m4a";
    public static final String KEY_PRAYER_AUDIO_ON = "prayer_audio_on";
    public static final String KEY_LAST_DONWLOAD_PRAYER_AUDIO_TIME = "last_donwload_prayer_audio_time";
    public static final String KEY_IS_FIRST_PRAY = "is_first_pray";

    public static final String KEY_IS_HIDE_FUNCTION = "is_hide_func";


    // devotion
    public static final String DEVOTION_BEAN = "devotion_bean";
    public static final String DEVOTION_ID = "devotion_id";
    public static final String DEVOTION_TITLE = "devotion_title";
    public static final String DEVOTION_IMAGE_URL = "devotion_image_url";
    public static final String DEVOTION_URL = "devotion_url";
    public static final String DEVOTION_DATE = "devotion_date";
    public static final String DEVOTION_SITE = "devotion_site";
    public static final String DEVOTION_QUOTE = "devotion_quote";
    public static final String DEVOTION_QUOTE_REFER = "devotion_quote_refer";

    // devotion site
    public static final String KEY_IS_RECOMMEND_DEVOTION_SITE = "is_recommend_devotion_site";
    public static final String KEY_SITE_LASTED_DEVOTION_ID = "sites_lasted_devotion_id";
    public static final String KEY_All_LASTED_DEVOTION_ID = "all_lasted_devotion_id";
    public static final String SITE_ID = "site_id";
    public static final String SITE_NAME = "site_name";
    public static final String SITE_IMAGE = "site_image_url";
    public static final String SITE_SUBSCRIBE = "site_subscribe";

    public static final String SITEID = "siteId";
    public static final String SITE_LASTED_ID = "lastId";


    //prayer
    public static final String PRAYER_LIST = "prayer_list";
    public static final String PRAYER_INDEX = "prayer_index";
    public static final String PRAYER_CATEGORY = "prayer_category";
    public static final String PRAYER_CATEGORY_ID = "prayer_category_id";
    public static final String PRAYER_CATEGORY_NUM = "prayer_category_people_count";

    //prayer notification
    public static final int DP_TYPE_MORNING = 1;
    public static final int DP_TYPE_EVENING = 2;
    public static final int DP_TYPE_CUSTOM = 3;
    public static final String DP_KEY_TYPE = "type";
    public static final String DP_KEY_CATEGORY_TYPE = "catId";
    public static final String DP_KEY_CATEGORY_NAME = "catName";
    public static final String DP_KEY_TITLE = "title";
    public static final String DP_KEY_CONTENT = "content";
    public static final String DP_KEY_TIME = "time";

    public static final int TYPE_VERSE_ACTION_SHARE = 2;
    public static final int TYPE_VERSE_ACTION_LIKE = 3;

}
