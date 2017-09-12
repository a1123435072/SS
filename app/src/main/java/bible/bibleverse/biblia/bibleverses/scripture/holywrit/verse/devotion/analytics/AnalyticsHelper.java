package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics;

import android.content.Context;
import android.os.Bundle;


import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Calendar;
import java.util.Date;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;

public class AnalyticsHelper {


    private static AnalyticsHelper sInstance;
    private Context mContext;
    private FirebaseAnalytics mAnalytics;
    public static AnalyticsHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AnalyticsHelper(context);
            sInstance.mContext = context.getApplicationContext();
        }
        return sInstance;
    }

    private AnalyticsHelper(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }
    }

    public void sendEvent(String viewID, String viewItem, String eventID, String action, String extension) {

        /*Bundle bundle = new Bundle();
        bundle.putString("view_id", viewID);
        bundle.putString("view_item", viewItem);
        bundle.putString("event_id", eventID);
        bundle.putString("action", action);
        bundle.putString("extension", extension);
        mAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);*/

    }

    public void sendView(String viewName) {
        /*Bundle bundle = new Bundle();
        bundle.putString("pageview", viewName);

        mAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);*/
    }
    public void sendEvent(String category, String action, String label) {
//
//        Bundle bundle = new Bundle();
//        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, category);
//        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, label);
//        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, action);
//        mAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
//
    }
//
//    public void sendView(String viewName) {
//        //mAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
//    }

    public void sendBasicAnalytics(String type) {
    }

    public void init(Context ctx) {
        mAnalytics = FirebaseAnalytics.getInstance(ctx);
    }


    public static long send_reach_analytics_lasttime = 0;

    public void sendReachAnalytics() {
        sendBasicAnalytics("reach");
        send_reach_analytics_lasttime = System.currentTimeMillis();
    }

    public void sendLoginAnalytics() {
        if (mustSendActiveAnalytic()) {
            sendReachAnalytics();
        }

        sendBasicAnalytics("login");
    }

    private static boolean mustSendActiveAnalytic() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - send_reach_analytics_lasttime > 10 * 60 * 1000 || !isSameDay(currentTime, send_reach_analytics_lasttime)) {
            return true;
        }
        return false;
    }

    public static boolean isSameDay(long time1, long time2) {
        try {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(new Date(time1));
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(new Date(time2));
            return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                    calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR);
        } catch (Exception e) {

        }
        return false;
    }

}
