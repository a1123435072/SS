package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics;

import android.content.Context;
import android.os.Bundle;

/**
 * Created by Mr_ZY on 16/9/5.
 */
public class SelfAnalyticsHelper {

    // user behavior
    public static void sendPathAnalytics(Context context, String text) {
        if (context == null) {
            return;
        }
        try {
            //Bundle params = new Bundle();
            //AnalyticsHelper.getInstance(context).sendEvent("path", params);
        } catch (Exception e) {
        }
    }

    // notification
    public static void sendNotificationAnalytics(Context context, String action, String label) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(AnalyticsConstants.C_NOTIFICATION, action, label);
        } catch (Exception e) {
        }
    }

    // read
    public static void sendReadVerseAnalytics(Context context, String action, String label) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(AnalyticsConstants.C_READ, action, label);
        } catch (Exception e) {
        }
    }


    //bible version
    public static void sendBibileVersionsAnalytics(Context context, String action, String label) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(AnalyticsConstants.C_VERSIONS, action, label);
        } catch (Exception e) {
        }
    }

    //plan
    public static void sendPlanAnalytics(Context context, String action, String label) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(AnalyticsConstants.C_PLAN, action, label);
        } catch (Exception e) {
        }
    }

    // search
    public static void sendSearchAnalytics(Context context, String text) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(AnalyticsConstants.C_SEARCH, text, "");
        } catch (Exception e) {
        }
    }

    // paid analytics
    public static void sendPaidAnalytics(Context context, String page, String action, String from) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(page, action, from);
        } catch (Exception e) {
        }
    }

    // paid analytics
    public static void sendHomeActionAnalytics(Context context, String action) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(AnalyticsConstants.C_HOME_ACTION, action, "");
        } catch (Exception e) {
        }
    }

    // prayer analytics
    public static void sendPrayerAnalytics(Context context, String action, String label) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(AnalyticsConstants.C_PRAYER, action, label);
        } catch (Exception e) {
        }
    }

    // login analytics
    public static void sendLoginSourceAnalytics(Context context, String action, String label) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(AnalyticsConstants.C_LOGIN_SOURCE, action, label);
        } catch (Exception e) {
        }
    }

    // web devotion analytics
    public static void sendWebDevotionAnalytics(Context context, String action, String site) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(AnalyticsConstants.C_WEB_DEVOTION, action, site);
        } catch (Exception e) {
        }
    }

    // time analytics
    public static void sendTimeAnalytics(Context context, String page, String milliseconds) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(AnalyticsConstants.C_STAY_TIME, page, milliseconds);
        } catch (Exception e) {
        }
    }

    // ls show analytics
    public static void sendLSGuideShowAnalytics(Context context, String page) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(AnalyticsConstants.C_LS_GUIDE, page, AnalyticsConstants.L_LS_GUIDE_SHOW);
        } catch (Exception e) {
        }
    }

    // ls open from guide analytics
    public static void sendLSGuideOpenAnalytics(Context context, String page) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(AnalyticsConstants.C_LS_GUIDE, page, AnalyticsConstants.L_LS_GUIDE_OPEN);
        } catch (Exception e) {
        }
    }

    //devotion  site analytics
    public static void sendDevotionSiteAnalytics(Context context, String siteName) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(AnalyticsConstants.C_DEVOTION_SITE, AnalyticsConstants.A_SITE_DEVOTION_CLICK, siteName);
        } catch (Exception e) {
        }
    }

    //lockscreen analytics
    public static void sendLockAnalytics(Context context, String action, String label) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(AnalyticsConstants.C_LOCKSCREEN, action, label);
        } catch (Exception e) {
        }
    }

    //home devotion click analytics
    public static void sendHomeDevotionClickAnalytics(Context context, String catId, String id) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(AnalyticsConstants.C_DEVOTION_CLK_HOME, catId, id);
        } catch (Exception e) {
        }
    }

    //locker devotion click analytics
    public static void sendLockerDevotionClickAnalytics(Context context, String catId, String id) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(AnalyticsConstants.C_DEVOTION_CLK_LOCKER, catId, id);
        } catch (Exception e) {
        }
    }

    //category list devotion click analytics
    public static void sendCatListDevotionClickAnalytics(Context context, String catId, String id) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(AnalyticsConstants.C_DEVOTION_CLK_CAT, catId, id);
        } catch (Exception e) {
        }
    }

    // devotion click from notification analytics
    public static void sendDevotionNotifyClickAnalytics(Context context, String catId, String id) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(AnalyticsConstants.C_DEVOTION_CLK_NOTIFY, catId, id);
        } catch (Exception e) {
        }
    }

    // devotion click from notification analytics
    public static void sendDevotionNotifyShowAnalytics(Context context, String catId, String id) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(AnalyticsConstants.C_DEVOTION_NOTIFY_SHOW, catId, id);
        } catch (Exception e) {
        }
    }

    //devotion category entry analytics
    public static void sendDevotionCatEntryClickAnalytics(Context context, String catId) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(AnalyticsConstants.C_DEVOTION_CAT_ENTRY, catId, null);
        } catch (Exception e) {
        }
    }

    public static void sendDevotionSiteSubscribeAnalytics(Context context, String action, String label) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(AnalyticsConstants.C_DEVOTION_SITE_SUBSCRIBE, action, label);
        } catch (Exception e) {
        }
    }

    public static void sendDevotionSiteUnSubscribeAnalytics(Context context, String action, String label) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(AnalyticsConstants.C_DEVOTION_SITE_UNSUBSCRIBE, action, label);
        } catch (Exception e) {
        }
    }

    //locker devotion delete analytics
    public static void sendLockerDevotionDeleteAnalytics(Context context, int id) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(AnalyticsConstants.C_DEVOTION_DELETE, String.valueOf(id), null);
        } catch (Exception e) {
        }
    }

    //locker devotion delete analytics
    public static void sendHideFuncAnalytics(Context context, boolean isHide) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(AnalyticsConstants.C_HIDE_FUNCTION, String.valueOf(isHide), null);
        } catch (Exception e) {
        }
    }

    //daily verse list analytics
    public static void sendDailyVerseAnalytics(Context context, String action) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(AnalyticsConstants.C_DAILY_VERSE, action, null);
        } catch (Exception e) {
        }
    }

    //daily verse click verse analytics
    public static void sendVerseReadAnalytics(Context context, String where) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(AnalyticsConstants.C_DAILY_VERSE, AnalyticsConstants.A_VERSE_CLICK, where);
        } catch (Exception e) {
        }
    }

    public static void sendReadRelatedAnalytics(Context context, String action, int label) {
        if (context == null) {
            return;
        }
        try {
            AnalyticsHelper.getInstance(context).sendEvent(AnalyticsConstants.C_DEVOTION_CLK_RR, action, String.valueOf(label));
        } catch (Exception e) {
        }
    }
}
