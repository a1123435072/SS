package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.ReminderBean;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.notification.DailyNotifyAlarm;
import yuku.afw.storage.Preferences;

/**
 * Created by zhangfei on 3/7/17.
 */

public class PrayerNotificationUtil {

    public static String getPrayerDefaultReminder(Context context) {
        JSONArray root = new JSONArray();
        //default morning pray
        root.put(getPrayerReminderJsonItem(Constants.DP_TYPE_MORNING,
                "9",
                "Morning",
                Constants.DAILY_MORNING_PRAYER_NOTIFICATION_CONFIG));

        //default evening pray
        root.put(getPrayerReminderJsonItem(Constants.DP_TYPE_EVENING,
                "3",
                "Evening",
                Constants.DAILY_EVENING_PRAYER_NOTIFICATION_CONFIG));

        return root.toString();
    }

    private static JSONObject getPrayerReminderJsonItem(int type, String catType, String catName, String time) {
        JSONObject item = new JSONObject();
        try {
            item.put(Constants.DP_KEY_TYPE, type);
            item.put(Constants.DP_KEY_TIME, time);
            item.put(Constants.DP_KEY_CATEGORY_TYPE, catType);
            item.put(Constants.DP_KEY_CATEGORY_NAME, catName);
            return item;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<ReminderBean> getPrayerReminderBeanList(Context context, String result) {
        try {
            JSONArray jsonArray = new JSONArray(result);
            List<ReminderBean> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = (JSONObject) jsonArray.opt(i);
                if (item != null) {
                    ReminderBean reminderBean = new ReminderBean();
                    reminderBean.type = item.optInt(Constants.DP_KEY_TYPE);
                    reminderBean.time = getFormatTime(context, item.optString(Constants.DP_KEY_TIME));
                    reminderBean.timeValue = item.optString(Constants.DP_KEY_TIME);
                    reminderBean.catName = item.optString(Constants.DP_KEY_CATEGORY_NAME);
                    list.add(reminderBean);
                }
            }
            return list;
        } catch (Exception e) {
        }
        return null;
    }

    private static String getFormatTime(Context context, String timeValue) {
        if (context == null || TextUtils.isEmpty(timeValue)) {
            return null;
        }
        try {
            Calendar time = GregorianCalendar.getInstance();
            time.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeValue.substring(0, 2)));
            time.set(Calendar.MINUTE, Integer.parseInt(timeValue.substring(2, 4)));
            return DateFormat.getTimeFormat(context).format(time.getTime());
        } catch (Exception e) {
        }
        return context.getString(R.string.dr_off);
    }

    public static String getFormatTime(Context context, int hourOfDay, int minute) {
        if (context == null) {
            return null;
        }
        try {
            Calendar time = GregorianCalendar.getInstance();
            time.set(Calendar.HOUR_OF_DAY, hourOfDay);
            time.set(Calendar.MINUTE, minute);
            return DateFormat.getTimeFormat(context).format(time.getTime());
        } catch (Exception e) {
        }
        return context.getString(R.string.dr_off);
    }

    public static void updatePrayerReminderTime(Context context, int index, int hourOfDay, int minute) {
        if (context == null) {
            return;
        }
        String value = Preferences.getString(context.getString(R.string.pref_prayer_of_day_reminder));
        if (!TextUtils.isEmpty(value)) {
            try {
                JSONArray jsonArray = new JSONArray(value);
                JSONObject item = (JSONObject) jsonArray.get(index);
                item.put(Constants.DP_KEY_TIME, String.format(Locale.US, "%02d%02d", hourOfDay, minute));
                Preferences.setString(context.getString(R.string.pref_prayer_of_day_reminder), jsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void turnOffPrayerReminderTime(Context context, int index) {
        if (context == null) {
            return;
        }
        String value = Preferences.getString(context.getString(R.string.pref_prayer_of_day_reminder));
        if (!TextUtils.isEmpty(value)) {
            try {
                JSONArray jsonArray = new JSONArray(value);
                JSONObject item = (JSONObject) jsonArray.get(index);
                item.put(Constants.DP_KEY_TIME, Constants.DAILY_NOTIFICATION_OFF);
                DailyNotifyAlarm.cancelPrayerNotifyAlarm(context,
                        item.optInt(Constants.DP_KEY_TYPE),
                        item.optString(Constants.DP_KEY_CATEGORY_TYPE),
                        item.optString(Constants.DP_KEY_CATEGORY_NAME));
                Preferences.setString(context.getString(R.string.pref_prayer_of_day_reminder), jsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }
        }
    }

    public static void turnOffPrayerReminderTime(Context context, String catId) {
        if (context == null) {
            return;
        }
        String value = Preferences.getString(context.getString(R.string.pref_prayer_of_day_reminder));
        if (!TextUtils.isEmpty(value)) {
            try {
                JSONArray jsonArray = new JSONArray(value);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = (JSONObject) jsonArray.get(i);
                    if (TextUtils.equals(catId, item.optString(Constants.DP_KEY_CATEGORY_TYPE))) {
                        item.put(Constants.DP_KEY_TIME, Constants.DAILY_NOTIFICATION_OFF);
                        DailyNotifyAlarm.cancelPrayerNotifyAlarm(context,
                                item.optInt(Constants.DP_KEY_TYPE),
                                item.optString(Constants.DP_KEY_CATEGORY_TYPE),
                                item.optString(Constants.DP_KEY_CATEGORY_NAME));
                        break;
                    }
                }
                Preferences.setString(context.getString(R.string.pref_prayer_of_day_reminder), jsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }
        }
    }

    public static void deletePrayerReminderTime(Context context, int index) {
        if (context == null) {
            return;
        }
        String value = Preferences.getString(context.getString(R.string.pref_prayer_of_day_reminder));
        if (!TextUtils.isEmpty(value)) {
            try {
                JSONArray oldData = new JSONArray(value);
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < oldData.length(); i++) {
                    if (i == index) {
                        JSONObject item = (JSONObject) jsonArray.opt(i);
                        DailyNotifyAlarm.cancelPrayerNotifyAlarm(context,
                                item.optInt(Constants.DP_KEY_TYPE),
                                item.optString(Constants.DP_KEY_CATEGORY_TYPE),
                                item.optString(Constants.DP_KEY_CATEGORY_NAME));
                        continue;
                    }
                    jsonArray.put(oldData.opt(i));
                }
                Preferences.setString(context.getString(R.string.pref_prayer_of_day_reminder), jsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }
        }
    }

    public static void deletePrayerReminderTime(Context context, String catId) {
        if (context == null) {
            return;
        }
        String value = Preferences.getString(context.getString(R.string.pref_prayer_of_day_reminder));
        if (!TextUtils.isEmpty(value)) {
            try {
                JSONArray oldData = new JSONArray(value);
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < oldData.length(); i++) {
                    JSONObject item = (JSONObject) oldData.get(i);
                    if (TextUtils.equals(catId, item.optString(Constants.DP_KEY_CATEGORY_TYPE))) {
                        DailyNotifyAlarm.cancelPrayerNotifyAlarm(context,
                                item.optInt(Constants.DP_KEY_TYPE),
                                item.optString(Constants.DP_KEY_CATEGORY_TYPE),
                                item.optString(Constants.DP_KEY_CATEGORY_NAME));
                        continue;
                    }
                    jsonArray.put(oldData.opt(i));
                }
                Preferences.setString(context.getString(R.string.pref_prayer_of_day_reminder), jsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public static void addPrayerReminderTime(Context context, int hourOfDay, int minute, String catId, String catName) {
        if (context == null) {
            return;
        }
        String value = Preferences.getString(context.getString(R.string.pref_prayer_of_day_reminder));
        if (!TextUtils.isEmpty(value)) {
            try {
                JSONArray jsonArray = new JSONArray(value);
                JSONObject item = new JSONObject();
                item.put(Constants.DP_KEY_TYPE, Constants.DP_TYPE_CUSTOM);
                item.put(Constants.DP_KEY_CATEGORY_TYPE, catId);
                item.put(Constants.DP_KEY_CATEGORY_NAME, catName);
                item.put(Constants.DP_KEY_TIME, String.format(Locale.US, "%02d%02d", hourOfDay, minute));
                jsonArray.put(item);
                Preferences.setString(context.getString(R.string.pref_prayer_of_day_reminder), jsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getReminderTitle(Context context, int type) {
        String title = null;
        switch (type) {
            case Constants.DP_TYPE_MORNING:
                title = context.getString(R.string.prayer_morning_notification_title);
                break;
            case Constants.DP_TYPE_EVENING:
                title = context.getString(R.string.prayer_evening_notification_title);
                break;
            case Constants.DP_TYPE_CUSTOM:
                title = context.getString(R.string.prayer_custom_notification_title);
                break;
        }
        return title;
    }

    public static String getReminderContent(Context context, int type) {
        String content = null;
        switch (type) {
            case Constants.DP_TYPE_MORNING:
                content = context.getString(R.string.prayer_morning_notification_content);
                break;
            case Constants.DP_TYPE_EVENING:
                content = context.getString(R.string.prayer_evening_notification_content);
                break;
            case Constants.DP_TYPE_CUSTOM:
                content = context.getString(R.string.prayer_custom_notification_content);
                break;
        }
        return content;
    }

    public static ReminderBean getReminderBean(Context context, String catId) {
        String value = Preferences.getString(context.getString(R.string.pref_prayer_of_day_reminder), getPrayerDefaultReminder(context));
        if (!TextUtils.isEmpty(value)) {
            try {
                JSONArray jsonArray = new JSONArray(value);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = (JSONObject) jsonArray.get(i);
                    if (TextUtils.equals(catId, item.optString(Constants.DP_KEY_CATEGORY_TYPE))) {
                        ReminderBean reminderBean = new ReminderBean();
                        reminderBean.type = item.optInt(Constants.DP_KEY_TYPE);
                        reminderBean.time = getFormatTime(context, item.optString(Constants.DP_KEY_TIME));
                        reminderBean.timeValue = item.optString(Constants.DP_KEY_TIME);
                        reminderBean.catName = item.optString(Constants.DP_KEY_CATEGORY_NAME);
                        return reminderBean;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
