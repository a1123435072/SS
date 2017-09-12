package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.ReadingPlan;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.storage.InternalDb;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.DateTimeUtil;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.receiver.DailyNotificationReceiver;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.PrayerNotificationUtil;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import yuku.afw.storage.Preferences;


/**
 * Created by Mr_ZY on 16/11/3.
 */

public class DailyNotifyAlarm {
    public static void updateDailyNotifyAlarm(Context context) {
        checkDailyNotifyConfig(context);
        scheduleAlarm(context);
    }

    public static void checkDailyNotifyConfig(Context context) {
        Preferences.setString(context.getString(R.string.pref_verse_of_day_reminder), Constants.DAILY_VERSE_NOTIFICATION_HOUR);
        Preferences.setString(context.getString(R.string.pref_verse_of_day_reminder_sever), Constants.DAILY_VERSE_NOTIFICATION_HOUR);
    }

    public static void scheduleAlarm(Context context) {
        //daily verse alarm
        String reminder = Preferences.getString(context.getString(R.string.pref_verse_of_day_reminder));
        if (reminder == null) {//new user
            Preferences.setString(context.getString(R.string.pref_verse_of_day_reminder), Constants.DAILY_VERSE_NOTIFICATION_HOUR);
            reminder = Constants.DAILY_VERSE_NOTIFICATION_HOUR;
        }
        if (!reminder.equals(Constants.DAILY_NOTIFICATION_OFF)) {//off notification
            setDailyVerseNotifyAlarm(context, reminder);
        }

        //plan alarm
        setAllPlanNotifyAlarm(context);

        //prayer alarm
        setPrayerNotifyAlarm(context);

        //sign in
        setSignInNotifyAlarm(context);
    }

    public static void setDailyVerseNotifyAlarm(final Context context, final String reminderTime) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = DailyNotificationReceiver.createIntent(context, Constants.NOTIFY_DAILY_VERSE_ID);

        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // always cancel current (if any)
        am.cancel(pi);
        if (reminderTime == null || reminderTime.equals(Constants.DAILY_NOTIFICATION_OFF)) {
            return;
        }
        setAlarm(context, reminderTime, pi);
    }

    public static void setAllPlanNotifyAlarm(final Context context) {
        InternalDb db = S.getDb();
        List<ReadingPlan.ReadingPlanInfo> list = db.listAllReadingPlanInfo();
        for (int i = 0; i < list.size(); i++) {
            ReadingPlan.ReadingPlanInfo info = list.get(i);
            if (info != null) {
                setSinglePlanNotifyAlarm(context, info.mReminderTime, info.mDbPlanId, info.mTitle, info.mBigImageUrl, info.mStartTime);
            }
        }
    }

    public static void setSinglePlanNotifyAlarm(final Context context, final int reminderTime, long planId, String planTitle, String bigImg, long planStartTime) {
        if (planTitle == null || planTitle.isEmpty() || planId <= 0) {
            return;
        }
        Intent intent = DailyNotificationReceiver.createIntent(context, planId, planTitle, bigImg, planStartTime);

        PendingIntent pi = PendingIntent.getBroadcast(context, (int) planId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        int[] reminderTimes = DateTimeUtil.parseReminderTime(reminderTime);
        String reminderTimeStr = String.valueOf(reminderTimes[0]) + String.valueOf(reminderTimes[1]);

        setAlarm(context, reminderTimeStr, pi);
    }

    public static void cancelSinglePlanNotifyAlarm(final Context context, long planId, String planTitle, String bigImg, long planStartTime) {
        if (planTitle == null || planTitle.isEmpty() || planId == 0) {
            return;
        }
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = DailyNotificationReceiver.createIntent(context, planId, bigImg, planTitle, planStartTime);

        PendingIntent pi = PendingIntent.getBroadcast(context, (int) planId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // always cancel current (if any)
        am.cancel(pi);
    }


    public static void setPrayerNotifyAlarm(Context context) {
        String reminderTimes = Preferences.getString(context.getString(R.string.pref_prayer_of_day_reminder), PrayerNotificationUtil.getPrayerDefaultReminder(context));
        if (TextUtils.isEmpty(reminderTimes)) {
            return;
        }
        try {
            JSONArray jsonArray = new JSONArray(reminderTimes);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = (JSONObject) jsonArray.opt(i);
                if (item != null) {
                    String time = item.optString(Constants.DP_KEY_TIME);
                    if (!TextUtils.isEmpty(time) && !TextUtils.equals(time, Constants.DAILY_NOTIFICATION_OFF)) {
                        Intent intent = DailyNotificationReceiver.createIntent(context, Constants.NOTIFY_DAILY_PRAYER_ID,
                                item.optInt(Constants.DP_KEY_TYPE), item.optString(Constants.DP_KEY_CATEGORY_TYPE),
                                item.optString(Constants.DP_KEY_CATEGORY_NAME));
                        PendingIntent pi = PendingIntent.getBroadcast(context, Integer.parseInt(Constants.DAILY_PRAYER_ALARM_BASIC_REQUEST_CODE) +
                                getCategoryType(item.optString(Constants.DP_KEY_CATEGORY_TYPE)), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        setAlarm(context, time, pi);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cancelPrayerNotifyAlarm(final Context context, int prayerType, String catId, String catName) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = DailyNotificationReceiver.createIntent(context, Constants.NOTIFY_DAILY_PRAYER_ID,
                prayerType, catId, catName);
        PendingIntent pi = PendingIntent.getBroadcast(context, Integer.parseInt(Constants.DAILY_PRAYER_ALARM_BASIC_REQUEST_CODE) +
                getCategoryType(catId), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(pi);
    }

    private static int getCategoryType(String categroyType) {
        try {
            return Integer.parseInt(categroyType);
        } catch (Exception e) {
        }
        return 0;
    }

    public static void setSignInNotifyAlarm(Context context) {
        String reminderTime = Preferences.getString(context.getString(R.string.pref_sign_in_reminder));
        if (reminderTime == null) {
            return;
        }

        Intent intent = DailyNotificationReceiver.createIntent(context, Constants.NOTIFY_DAILY_SIGN_IN_ID);
        PendingIntent pi = PendingIntent.getBroadcast(context, Integer.parseInt(Constants.DAILY_SIGNIN_ALARM_BASIC_REQUEST_CODE), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        setAlarm(context, reminderTime, pi);
    }

    public static void setAlarm(Context context, String reminderTime, PendingIntent pi) {
        if (TextUtils.isEmpty(reminderTime)) {
            return;
        }
        try {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            // always cancel current (if any)
            am.cancel(pi);
            Calendar c = GregorianCalendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(reminderTime.substring(0, 2)));
            c.set(Calendar.MINUTE, Integer.parseInt(reminderTime.substring(2, 4)));
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);

            if (c.getTimeInMillis() < System.currentTimeMillis()) {
                c.add(Calendar.DAY_OF_YEAR, 1);
            }
            am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
