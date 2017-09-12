package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by Mr_ZY on 16/9/5.
 */
public class UserBehaviorAnalytics {
    private final static String SP_TRACK_USER_BEHAVIOR = "track_user_behavior";
    private final static String SP_KEY_TRACK_USER_BEHAVIOR = "track_user_behavior_sequence";
    private final static String SP_KEY_USER_BEHAVIOR_START_TIME = "user_behavior_start_time";

    public static void trackUserBehavior(Context context, String viewName, String event) {
    }

    public static void sendUserBehavior(Context context) {
    }

    private static void clearUserBehaviorSequence(SharedPreferences sp) {
    }

}
