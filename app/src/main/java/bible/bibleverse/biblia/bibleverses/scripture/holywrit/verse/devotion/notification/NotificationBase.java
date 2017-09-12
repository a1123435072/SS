package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.service.notification.StatusBarNotification;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;


/**
 * Created by yzq on 16/9/12.
 */

public class NotificationBase {

    public final static String LAST_NOTIFICATION_ID = "last_notification_id";
    public final static String IS_SHOWING_NOTIFICATION_PREFIX = "is_showing_notification_";
    public final static String SHOW_NOTIFICATION_ID_TIME = "show_notification_time_";

    public static boolean isShowingNotification(Context context, int notificationId) {
        if (context == null)
            return false;

//        if (Build.VERSION.SDK_INT >= 23) {
//            NotificationManager notifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//            if (notifyMgr == null)
//                return false;
//            try {
//                StatusBarNotification[] notifications = notifyMgr.getActiveNotifications();
//                for (StatusBarNotification notification : notifications) {
//                    if (notification.getId() == notificationId) {
//                        return true;
//                    }
//                }
//                return false;
//            } catch (Exception e) {
//                return false;
//            }
//        } else {
        return checkShowingPreferences(context, notificationId);
//        }
    }

    private static boolean checkShowingPreferences(Context context, int notificationId) {
        if (System.currentTimeMillis() - getShowingNotificationLastTime(context, notificationId) > 24 * 60 * 60 * 1000) {
            return false;
        }
        SharedPreferences sp = context.getSharedPreferences(Utility.APP_NAME, 0);
        boolean showing = sp.getBoolean(IS_SHOWING_NOTIFICATION_PREFIX + notificationId, false);
        return showing;
    }

    public static void setLastTimeShowNotificationID(Context ctx, int notificationId) {
        SharedPreferences sp = ctx.getSharedPreferences(Utility.APP_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(LAST_NOTIFICATION_ID, notificationId);
        editor.commit();
    }

    public static void setShowingNotificationStatus(Context context, int notificationId, boolean status) {
        SharedPreferences sp = context.getSharedPreferences(Utility.APP_NAME, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(IS_SHOWING_NOTIFICATION_PREFIX + notificationId, status);
        if (status == true) {
            editor.putLong(SHOW_NOTIFICATION_ID_TIME + notificationId, System.currentTimeMillis());
        }
        editor.commit();
    }

    public static long getShowingNotificationLastTime(Context context, int notificationId) {
        SharedPreferences sp = context.getSharedPreferences(Utility.APP_NAME, 0);
        long lastTime = sp.getLong(SHOW_NOTIFICATION_ID_TIME + notificationId, 0);
        return lastTime;
    }

    public static PendingIntent getDeleteIntent(Context context, int notificationId) {
        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        intent.setAction("notification_cancelled");
        intent.putExtra("notification_id", notificationId);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}
