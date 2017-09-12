package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.NotificationCompat;
import android.text.Html;
import android.widget.RemoteViews;

import java.util.ArrayList;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.SelfAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PrayerBean;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.VerseListResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.ExitAppActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.WelcomeActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.notification.floating.DailyNotifyFloatView;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.PrayerNotificationUtil;


/**
 * Created by Mr_ZY on 16/10/28.
 */

public class NotificationUtils {
    static volatile DailyNotifyFloatView floatNotification;


    public static DailyNotifyFloatView getFloat(Context context) {
        if (floatNotification == null) {
            synchronized (DailyNotifyFloatView.class) {
                if (floatNotification == null) {
                    floatNotification = new DailyNotifyFloatView(context);
                }
            }
        }
        return floatNotification;
    }

    public static void showDailyVerseFloatNt(Context context, VerseListResponse.DataBean.VerseBean verseItem) {
        if (context == null || verseItem == null)
            return;

        DailyNotifyFloatView dailyNotifyFloatView = getFloat(context);
        dailyNotifyFloatView.setNotifyType(verseItem, DailyNotifyFloatView.TYPE_DAILY_VERSE);
        dailyNotifyFloatView.show();
        SelfAnalyticsHelper.sendNotificationAnalytics(context, AnalyticsConstants.A_VERSE_NOTICE, AnalyticsConstants.L_VERSE_DISPLAY + "_2");
    }

    public static void showDailySignInFloatNt(Context context) {
        if (context == null)
            return;
        DailyNotifyFloatView dailyNotifyFloatView = getFloat(context);
        dailyNotifyFloatView.setNotifyType(DailyNotifyFloatView.TYPE_DAILY_SIGN_IN);
        dailyNotifyFloatView.show();
        SelfAnalyticsHelper.sendNotificationAnalytics(context, AnalyticsConstants.A_SIGNIN_NOTICE, AnalyticsConstants.L_SIGNIN_DISPLAY + "_2");
    }

    public static void showDailyPrayerFloatNt(Context context, int prayerType) {
        if (context == null)
            return;
        DailyNotifyFloatView dailyNotifyFloatView = getFloat(context);
        dailyNotifyFloatView.setNotifyType(DailyNotifyFloatView.TYPE_DAILY_PRAYER);
        dailyNotifyFloatView.setPrayerType(prayerType);
        dailyNotifyFloatView.show();
        SelfAnalyticsHelper.sendNotificationAnalytics(context, AnalyticsConstants.A_PRAYER_NOTICE, AnalyticsConstants.L_PRAYER_DISPLAY + "_2");
    }

    public static void showDailyPrayerFloatNt(Context context, PrayerBean pray, String catName) {
        if (context == null)
            return;
        DailyNotifyFloatView dailyNotifyFloatView = getFloat(context);
        dailyNotifyFloatView.setNotifyType(DailyNotifyFloatView.TYPE_DAILY_PRAYER);
        dailyNotifyFloatView.setPrayer(pray, catName);
        dailyNotifyFloatView.show();
        SelfAnalyticsHelper.sendNotificationAnalytics(context, AnalyticsConstants.A_PRAYER_NOTICE, AnalyticsConstants.L_PRAYER_DISPLAY + "_2");
    }

    public static Boolean showDailyVerseNotification(Context ctx, VerseListResponse.DataBean.VerseBean verseItem) {
        if (verseItem == null) return false;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);
        builder.setSmallIcon(R.drawable.ic_notification_small);
        builder.setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.mipmap.ic_launcher));
        builder.setAutoCancel(true);
        builder.setContentTitle(verseItem.getQuoteRefer());
        builder.setContentText(verseItem.getQuote());
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(verseItem.getQuote()));

        Intent resultIntent = WelcomeActivity.createIntent(ctx, Constants.NOTIFY_DAILY_VERSE_ID, Constants.TYPE_SYSTEM_NOTIFICATION, verseItem.getDate(), verseItem.getQuote(), verseItem.getQuoteRefer(), verseItem.getId(), verseItem.getImageUrl());

        PendingIntent resultPendingIntent = PendingIntent.getActivity(ctx, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        builder.setDeleteIntent(NotificationBase.getDeleteIntent(ctx, Constants.NOTIFY_DAILY_NOTIFY_ID));

        Notification n = builder.build();
        n.flags |= Notification.FLAG_SHOW_LIGHTS;
        if (Build.VERSION.SDK_INT >= 16) {
            n.priority = Notification.PRIORITY_MAX;
        }

        NotificationManager notifyMgr = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        notifyMgr.notify(Constants.NOTIFY_DAILY_NOTIFY_ID, n);
        SelfAnalyticsHelper.sendNotificationAnalytics(ctx, AnalyticsConstants.A_VERSE_NOTICE, AnalyticsConstants.L_VERSE_DISPLAY + "_1");
        return true;
    }

    public static Boolean showBiblePlanNotification(Context context, Long planId, String title, String bigImg, long planStartTime) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.bible_plan_notify_layout);
        remoteViews.setTextViewText(R.id.plan_txt, Html.fromHtml(context.getString(R.string.plan_notify_txt, title)));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_notification_small);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));

        Intent resultIntent = WelcomeActivity.createIntent(context, Constants.NOTIFY_BIBLE_PLAN_ID, Constants.TYPE_SYSTEM_NOTIFICATION, null, planId, null);

        PendingIntent pendingMainIntent = PendingIntent.getActivity(context, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingMainIntent);
        builder.setAutoCancel(true);
        builder.setDeleteIntent(NotificationBase.getDeleteIntent(context, Constants.NOTIFY_BIBLE_PLAN_ID));

        Notification notify = builder.build();
        notify.contentView = remoteViews;
        notify.flags |= Notification.FLAG_SHOW_LIGHTS;
        if (Build.VERSION.SDK_INT >= 16) {
            notify.priority = Notification.PRIORITY_MAX;
        }
        NotificationManager notifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifyMgr.notify(Constants.NOTIFY_BIBLE_PLAN_ID, notify);
        SelfAnalyticsHelper.sendNotificationAnalytics(context, AnalyticsConstants.A_PLAN_NOTICE, AnalyticsConstants.L_PLAN_DISPLAY);
        return true;
    }


    public static Boolean showDailyPrayerNt(Context ctx, int type) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);
        builder.setSmallIcon(R.drawable.ic_notification_small);
        builder.setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_notify_prayer));

        builder.setAutoCancel(true);
        builder.setContentTitle(PrayerNotificationUtil.getReminderTitle(ctx, type));
        String content = PrayerNotificationUtil.getReminderContent(ctx, type);
        builder.setContentText(content);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(content));

        Intent resultIntent = WelcomeActivity.createPrayerIntent(ctx, Constants.NOTIFY_DAILY_PRAYER_ID, Constants.TYPE_SYSTEM_NOTIFICATION, null, 0, "");

        PendingIntent resultPendingIntent = PendingIntent.getActivity(ctx, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        builder.setDeleteIntent(NotificationBase.getDeleteIntent(ctx, Constants.NOTIFY_DAILY_NOTIFY_ID));

        Notification n = builder.build();
        n.flags |= Notification.FLAG_SHOW_LIGHTS;
        if (Build.VERSION.SDK_INT >= 16) {
            n.priority = Notification.PRIORITY_MAX;
        }

        NotificationManager notifyMgr = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notifyMgr.notify(Constants.NOTIFY_DAILY_NOTIFY_ID, n);
        SelfAnalyticsHelper.sendNotificationAnalytics(ctx, AnalyticsConstants.A_PRAYER_NOTICE, AnalyticsConstants.L_PRAYER_DISPLAY + "_1");
        return true;
    }

    public static Boolean showDailyPrayerNt(Context ctx, PrayerBean pray, String catName) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);
        builder.setSmallIcon(R.drawable.ic_notification_small);
        builder.setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_notify_prayer));

        builder.setAutoCancel(true);
        builder.setContentTitle(Html.fromHtml(pray.getTitle()));
        String content = pray.getContent();
        builder.setContentText(Html.fromHtml(content));
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(Html.fromHtml(content)));

        ArrayList<PrayerBean> prays = new ArrayList<>();
        prays.add(pray);
        Intent resultIntent = WelcomeActivity.createPrayerIntent(ctx, Constants.NOTIFY_DAILY_PRAYER_ID, Constants.TYPE_SYSTEM_NOTIFICATION, prays, 0, catName);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(ctx, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        builder.setDeleteIntent(NotificationBase.getDeleteIntent(ctx, Constants.NOTIFY_DAILY_NOTIFY_ID));

        Notification n = builder.build();
        n.flags |= Notification.FLAG_SHOW_LIGHTS;
        if (Build.VERSION.SDK_INT >= 16) {
            n.priority = Notification.PRIORITY_MAX;
        }

        NotificationManager notifyMgr = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notifyMgr.notify(Constants.NOTIFY_DAILY_NOTIFY_ID, n);
        SelfAnalyticsHelper.sendNotificationAnalytics(ctx, AnalyticsConstants.A_PRAYER_NOTICE, AnalyticsConstants.L_PRAYER_DISPLAY + "_1");
        return true;
    }


    public static Boolean showDailySignInNt(Context ctx) {
        if (ctx == null) return false;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);
        builder.setSmallIcon(R.drawable.ic_notification_small);
        builder.setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_notify_sgin_in));
        builder.setAutoCancel(true);
        builder.setContentTitle(ctx.getString(R.string.notify_daily_sign_in_title));
        builder.setContentText(ctx.getString(R.string.notify_daily_sign_in_msg));
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(ctx.getString(R.string.notify_daily_sign_in_msg)));

        Intent resultIntent = ExitAppActivity.createIntent(ctx, ExitAppActivity.TYPE_SIGN_IN_NOTIFICATION);
        resultIntent.putExtra(Constants.KEY_NOTIFICATION_TYPE, Constants.TYPE_SYSTEM_NOTIFICATION);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(ctx, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        builder.setDeleteIntent(NotificationBase.getDeleteIntent(ctx, Constants.NOTIFY_DAILY_NOTIFY_ID));

        Notification notify = builder.build();
        notify.flags |= Notification.FLAG_SHOW_LIGHTS;
        if (Build.VERSION.SDK_INT >= 16) {
            notify.priority = Notification.PRIORITY_MAX;
        }

        NotificationManager notifyMgr = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        notifyMgr.notify(Constants.NOTIFY_DAILY_NOTIFY_ID, notify);

        SelfAnalyticsHelper.sendNotificationAnalytics(ctx, AnalyticsConstants.A_SIGNIN_NOTICE, AnalyticsConstants.L_SIGNIN_DISPLAY + "_1");
        return true;
    }
}
