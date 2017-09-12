package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.AtomicFile;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.BaseRetrofit;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.PrayerService;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PrayerPeopleResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.NewMainActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.MVersionDb;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.storage.YesReaderFactory;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.AddonManager;
import okhttp3.ResponseBody;
import retrofit2.Callback;
import yuku.afw.App;
import yuku.afw.storage.Preferences;
import yuku.alkitab.io.BibleReader;

import static android.content.Context.POWER_SERVICE;
import static yuku.afw.App.context;

/**
 * Created by zhangfei on 8/30/16.
 */
public class Utility {
    public static final String APP_NAME = "bibble";

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static String getAppVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String formatSize(long paramLong) {
        StringBuffer str1 = new StringBuffer();
        if (paramLong >= 1073741824L) {
            double d1 = paramLong;
            double d2 = 1073741824L;
            String str2 = String.valueOf(formatDecimal(d1 / d2));
            return str1.append(str2).append("GB").toString();
        }
        if (paramLong >= 1048576L) {
            double d3 = paramLong;
            double d4 = 1048576L;
            String str3 = String.valueOf(formatDecimal(d3 / d4));
            return str1.append(str3).append("MB").toString();
        }
        if (paramLong >= 1048L) {
            double d5 = paramLong;
            double d6 = 1024L;
            String str4 = String.valueOf(formatDecimal(d5 / d6));
            return str1.append(str4).append("KB").toString();
        }
        return str1.append(String.valueOf(paramLong)).append("B").toString();
    }

    public static String[] formatSizeToArray(long paramLong) {
        String[] res = new String[2];
        if (paramLong >= 1073741824L) {
            double d1 = paramLong;
            double d2 = 1073741824L;
            String str2 = String.valueOf(formatDecimal(d1 / d2));
            res[0] = str2;
            res[1] = "GB";
        } else if (paramLong >= 1048576L) {
            double d3 = paramLong;
            double d4 = 1048576L;
            String str3 = String.valueOf(formatDecimal(d3 / d4));
            res[0] = str3;
            res[1] = "MB";
        } else if (paramLong >= 1048L) {
            double d5 = paramLong;
            double d6 = 1024L;
            String str4 = String.valueOf(formatDecimal(d5 / d6));
            res[0] = str4;
            res[1] = "KB";
        } else {
            res[0] = String.valueOf(paramLong);
            res[1] = "B";
        }
        return res;
    }

    public static String formatDecimal(double paramDouble) {
        BigDecimal bd = new BigDecimal(paramDouble).setScale(1, 5);
        if (bd == null) {
            return "unknown";
        }
        return bd.toString();
    }

    public static void formatTextStyleWithSizeAndColor(Context context, TextView tv, float size, String text1, String text, int colorId) {
        if (tv == null || context == null) {
            return;
        }
        if (!TextUtils.isEmpty(text)) {
            SpannableString ss1 = new SpannableString(text);
            int index = text.indexOf(text1);
            if (index == -1) {
                return;
            }
            if (!TextUtils.isEmpty(ss1) && size > 0) {
                ss1.setSpan(new RelativeSizeSpan(size), index, index + text1.length(), 0);
            }
            SpannableStringBuilder builder = new SpannableStringBuilder(ss1);
            if (colorId != -1) {
                ForegroundColorSpan color1 = new ForegroundColorSpan(ContextCompat.getColor(context, colorId));
                builder.setSpan(color1, index, index + text1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            tv.setText(builder);
        }
    }

    public static void formatTextStyleWithSizeColorAndStyle(Context context, TextView tv, float size, String text1, String text, int colorId, int style) {
        if (tv == null || context == null) {
            return;
        }
        if (!TextUtils.isEmpty(text)) {
            SpannableString ss1 = new SpannableString(text);
            int index = text.indexOf(text1);
            if (index == -1) {
                return;
            }
            if (!TextUtils.isEmpty(ss1) && size > 0) {
                ss1.setSpan(new RelativeSizeSpan(size), index, index + text1.length(), 0);
                ss1.setSpan(new StyleSpan(style), index, index + text1.length(), 0);

            }
            SpannableStringBuilder builder = new SpannableStringBuilder(ss1);
            ForegroundColorSpan color1 = new ForegroundColorSpan(context.getResources().getColor(colorId));
            builder.setSpan(color1, index, index + text1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv.setText(builder);
        }
    }


    public static Bitmap drawable2Bitmap(Drawable drawable) {
        try {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            Bitmap bitmap = bd.getBitmap();
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isGooglePlayExist(Context context) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo("com.android.vending", 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    public static boolean isFacebookExist(Context context) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    public static void sendEmailToOffical(Context context, String subject) {
        String[] receiver = new String[]{Constants.FEEDBACK_EMAIL};
        sendEmail(context, receiver, subject);
    }

    public static void sendEmail(Context context, String[] receiver, String subject) {
        try {
            Intent send = new Intent(Intent.ACTION_SENDTO);
            String uriText = "mailto:" + Uri.encode(receiver[0]);
            Uri uri = Uri.parse(uriText);
            send.setData(uri);
            send.putExtra(Intent.EXTRA_SUBJECT, subject);
            send.putExtra(Intent.EXTRA_TEXT, "\n\n\n--------------------------\n" +
                    "C:" + ReleaseConfig.getChannel(context) + "\n" +
                    "--------------------------");
            context.startActivity(Intent.createChooser(send, context.getString(R.string.about_contact_us_text)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void goHome(Activity mActivity) {
        Intent it = NewMainActivity.createHomeIntent(context);
        mActivity.startActivity(it);
        if (Build.VERSION.SDK_INT < 11) { // Before OS v3.0 3.0系统以前
            if (mActivity != null && !(mActivity instanceof NewMainActivity)) {
                mActivity.finish();
            }
        }
    }

    public static void hideSoftKey(Activity activity) {
        if (activity == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(activity.getWindow().getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e) {
        }
    }

    public static void scanFile(String path, Context c) {
        try {
            if (Build.VERSION.SDK_INT >= 19) {
                MediaScannerConnection.scanFile(c, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
            } else {
                Uri contentUri = Uri.fromFile(new File(path));
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED, contentUri);
                c.sendBroadcast(mediaScanIntent);
            }
        } catch (Exception e) {
        }
    }

    public static int getActionbarHeight(Context context) {
        if (context == null)
            return 0;
        int height = 0;
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            height = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
        return height;
    }

    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public static boolean getIsShowDownloadNotice(Context context) {
        if (context == null)
            return false;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(Constants.IS_SHOW_DOWNLOAD_NOTICE, true);
    }

    public static void setIsShowDownloadNotice(Context context, boolean ishow) {
        if (context == null)
            return;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(Constants.IS_SHOW_DOWNLOAD_NOTICE, ishow).commit();
    }


    public static void shareAppBySystem(Context context) {
        try {
            File apkFile = null;
            ApplicationInfo applicationInfo = null;
            try {
                applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (applicationInfo != null && applicationInfo.sourceDir != null) {
                apkFile = new File(applicationInfo.sourceDir);
            }
            Intent it = new Intent(Intent.ACTION_SEND);
            it.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.version_menu_share));
            it.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.version_menu_share));
            if (apkFile != null) {
                it.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(apkFile));
            }
            it.setType("text/plain");
            Intent chooser = Intent.createChooser(it, context.getResources().getString(R.string.version_menu_share));
            if (chooser == null) {
                return;
            }
            context.startActivity(chooser);
        } catch (android.content.ActivityNotFoundException ex) {
        }
    }

    public static void shareTextBySystem(Context context, String sharedContent) {
        try {
            Intent it = new Intent(Intent.ACTION_SEND);
            it.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.version_menu_share));
            it.putExtra(Intent.EXTRA_TEXT, sharedContent);
            it.setType("text/plain");
            Intent chooser = Intent.createChooser(it, context.getResources().getString(R.string.version_menu_share));
            if (chooser == null) {
                return;
            }
            context.startActivity(chooser);
        } catch (android.content.ActivityNotFoundException ex) {
        }
    }

    public static void initDeafaultVersion() {
        File destFile = AddonManager.getWritableVersionFile("en-kjv" + ".yes");
        if (destFile == null || !destFile.exists()) {
            copyVersionFileFromAsset(destFile);
            insertVersionData(destFile, "en-kjv");
        }
    }

    public static void copyVersionFileFromAsset(File destFile) {
        InputStream in = null;
        FileOutputStream fos = null;
        try {
            in = context.getAssets().open("en-kjv--1.yes");
            final AtomicFile af = new AtomicFile(destFile);
            fos = af.startWrite();

            final byte[] buf = new byte[4096];
            while (true) {
                final int read = in.read(buf);
                if (read < 0) break;
                fos.write(buf, 0, read);
            }
            af.finishWrite(fos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static void insertVersionData(File destFile, String presetName) {
        final BibleReader reader = YesReaderFactory.createYesReader(destFile.getAbsolutePath());
        if (reader != null) {
            int maxOrdering = S.getDb().getVersionMaxOrdering();
            if (maxOrdering == 0) maxOrdering = MVersionDb.DEFAULT_ORDERING_START;

            final MVersionDb mvDb = new MVersionDb();
            mvDb.locale = reader.getLocale();
            mvDb.shortName = reader.getShortName();
            mvDb.longName = reader.getLongName();
            mvDb.description = reader.getDescription();
            mvDb.filename = destFile.getAbsolutePath();
            mvDb.preset_name = presetName;
            mvDb.modifyTime = (int) System.currentTimeMillis();//FIXME
            mvDb.ordering = maxOrdering + 1;

            S.getDb().insertOrUpdateVersionWithActive(mvDb, true);
            MVersionDb.clearVersionImplCache();
            S.activeVersion = mvDb.getVersion();
            S.activeVersionId = mvDb.getVersionId();

        }
    }

    public static boolean isSameDay(long timeStamp1, long timeStamp2) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
            return fmt.format(timeStamp1).equals(fmt.format(timeStamp2));
        } catch (Exception e) {
            return false;
        }
    }

    public static String getFormatTime(String time) {
        Date date = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            date = formatter.parse(time);
            return new SimpleDateFormat("yyyy-MM-dd").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long getLastShowDailyVerseTime(Context context) {
        if (context == null)
            return 0;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(Constants.LAST_SHOW_DAILY_VERSE_TIME, 0);
    }

    public static void setLastShowDailyVerseTime(Context context, long time) {
        if (context == null)
            return;

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(Constants.LAST_SHOW_DAILY_VERSE_TIME, time).commit();
    }

    public static int getFitTextsize(Context context, String text, int maxWidth, int maxHeight) {
        int size = 27;
        while (getHeightOfMultiLineText(context, text, size, maxWidth) > maxHeight)
            size--;
        return size;
    }

    private static int getHeightOfMultiLineText(Context context, String text, int textSize, int maxWidth) {
        TextPaint paint = new TextPaint();
        paint.setTextSize(getSizeSpValue(context, textSize));
        int index = 0;
        int lineCount = 0;
        while (index < text.length()) {
            index += paint.breakText(text, index, text.length(), true, maxWidth, null);
            lineCount++;
        }

        Rect bounds = new Rect();
        paint.getTextBounds("Yy", 0, 2, bounds);
        // obtain space between lines
        double lineSpacing = Math.max(0, ((lineCount - 1) * bounds.height() * 0.6));

        return (int) Math.floor(lineSpacing + lineCount * bounds.height());
    }

    private static float getSizeSpValue(Context context, float sp) {
        if (context == null) {
            return 0;
        }
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static int parseVerseId(String verseIdStr, boolean getEndId) {
        String[] arr = verseIdStr.split("-|,");
        String str = verseIdStr;
        if (arr.length > 1) {
            str = getEndId ? arr[arr.length - 1] : arr[0];
        }

        while (str.length() > 0 && !str.matches("^[0-9]+")) {
            str = str.substring(0, str.length() - 1);
        }

        int verseId = -1;
        try {
            verseId = Integer.parseInt(str);
        } catch (Exception e) {
            verseId = -1;
        }
        return verseId;
    }

    public static String sqliteEscape(String keyWord) {
        keyWord = keyWord.replace("/", "//");
        keyWord = keyWord.replace("'", "''");
        keyWord = keyWord.replace("[", "/[");
        keyWord = keyWord.replace("]", "/]");
        keyWord = keyWord.replace("%", "/%");
        keyWord = keyWord.replace("&", "/&");
        keyWord = keyWord.replace("_", "/_");
        keyWord = keyWord.replace("(", "/(");
        keyWord = keyWord.replace(")", "/)");
        return keyWord;
    }

    public static void deleteNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

    public static int getTabHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    public static String getTimeFormatStyleText(Context context, long time) {
        if (context == null || time == 0) {
            return "";
        }
        long minutes = 0;
        long hour = time / (1000 * 60 * 60);
        int size1 = context.getResources().getDimensionPixelSize(R.dimen.tv_size_20);
        int size2 = context.getResources().getDimensionPixelSize(R.dimen.tv_size_16);
        if (hour == 0) {
            minutes = time / (1000 * 60);
            SpannableString span1 = new SpannableString(String.valueOf(minutes));
            span1.setSpan(new AbsoluteSizeSpan(size1), 0, String.valueOf(minutes).length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            SpannableString span2 = new SpannableString("mins");
            span2.setSpan(new AbsoluteSizeSpan(size2), 0, "mins".length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            return TextUtils.concat(span1, " ", span2).toString();
        } else {
            minutes = (time - (1000 * 60 * 60) * hour) / (1000 * 60);

            SpannableString span1 = new SpannableString(String.valueOf(hour));
            span1.setSpan(new AbsoluteSizeSpan(size1), 0, String.valueOf(hour).length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            SpannableString span2 = new SpannableString("h");
            span2.setSpan(new AbsoluteSizeSpan(size2), 0, "h".length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            CharSequence result1 = TextUtils.concat(span1, "", span2);

            SpannableString span3 = new SpannableString(String.valueOf(minutes));
            span3.setSpan(new AbsoluteSizeSpan(size1), 0, String.valueOf(minutes).length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            SpannableString span4 = new SpannableString("mins");
            span4.setSpan(new AbsoluteSizeSpan(size2), 0, "mins".length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            CharSequence result2 = TextUtils.concat(span3, "", span4);

            return TextUtils.concat(result1, " ", result2).toString();
        }
    }

    public static void recordSigninDays(Context context) {
        if (context == null) {
            return;
        }
        int days = Preferences.getInt(context.getString(R.string.pref_sign_in_days), 0);
        Preferences.setInt(context.getString(R.string.pref_sign_in_days), ++days);
    }

    public static ListView getPopupWindowList(Context context, ArrayList<String> data) {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, data);
        // the drop down list is a list view
        ListView listViewSort = new ListView(context);
        listViewSort.setBackgroundColor(context.getResources().getColor(R.color.white));
        listViewSort.setDivider(null);
        // set our adapter and pass our pop up window contents
        listViewSort.setAdapter(adapter);
        return listViewSort;
    }

    public static int[] getPopWindowPosInList(Context context, final View anchorView, final View contentView) {
        final int windowPos[] = new int[2];
        final int anchorLoc[] = new int[2];
        anchorView.getLocationOnScreen(anchorLoc);
        final int anchorHeight = anchorView.getHeight();

        final int screenHeight = App.context.getResources().getDisplayMetrics().heightPixels;
        final int screenWidth = App.context.getResources().getDisplayMetrics().widthPixels;
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        final int popupWindowHeight = contentView.getMeasuredHeight();
        final int popupWindowWidth = context.getResources().getDimensionPixelSize(R.dimen.mark_list_popup_window_width);

        final boolean isNeedShowUp = (screenHeight - anchorLoc[1] - anchorHeight < popupWindowHeight);
        if (isNeedShowUp) {
            windowPos[0] = screenWidth - popupWindowWidth - context.getResources().getDimensionPixelSize(R.dimen.margin_6) * 2;
            windowPos[1] = anchorLoc[1] - popupWindowHeight;
        } else {
            windowPos[0] = screenWidth - popupWindowWidth - context.getResources().getDimensionPixelSize(R.dimen.margin_6) * 2;
            windowPos[1] = anchorLoc[1] + anchorHeight;
        }
        return windowPos;
    }

    public static void recordReadBibleStartTime(Context context) {
        if (context == null) {
            return;
        }
        long startTime = Preferences.getLong(context.getString(R.string.pref_reading_start_time), 0);
        if (startTime == 0) {
            Preferences.setLong(context.getString(R.string.pref_reading_start_time), System.currentTimeMillis());
        }
    }

    public static void removeViewGlobalLayoutListener(View view, ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener) {
        if (view != null) {
            if (Build.VERSION.SDK_INT < 16) {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(onGlobalLayoutListener);
            } else {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
            }
        }
    }

    public static boolean isShowAd(Context context) {
        if (context == null) {
            return true;
        }
        return Preferences.getBoolean(context.getString(R.string.pref_ad_show), true);
    }

    public static String getFormatDailyNotifyTime(int t) {
        String time = "";
        if (t >= 10 && t <= 24) {
            time = t + "00";
        } else {
            time = "0" + t + "00";
        }
        return time;
    }

    public static void setRatingGuideShown(Context context, boolean isShown) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean(Constants.KEY_RATING_GUIDE, isShown).apply();
    }

    public static boolean isRatingGuideShown(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(Constants.KEY_RATING_GUIDE, false);
    }

    public static boolean isPrayerAudioFileExist(Context context) {
        File file = new File(getPrayerAudioFileName(context));
        return file.exists();
    }

    public static boolean isNeedDownloadPrayerAudioFile(Context context) {
        if (!isPrayerAudioFileExist(context)) {
            return true;
        }
        SharedPreferences sp = context.getSharedPreferences(Utility.APP_NAME, 0);
        long lastDownloadTime = sp.getLong(Constants.KEY_LAST_DONWLOAD_PRAYER_AUDIO_TIME, 0);
        if (System.currentTimeMillis() - lastDownloadTime < 7 * 24 * 3600 * 1000l) {
            return false;
        }
        return true;
    }

    public static String getPrayerAudioFileName(Context context) {
        return Environment.getExternalStorageDirectory() + File.separator + context.getString(R.string.app_name)
                + File.separator + Constants.PRAYER_AUDIO_FILE_NAME;
    }

    public static void downloadPrayerBgAudioFile(Context context) {
        if (!isNeedDownloadPrayerAudioFile(context)) {
            return;
        }
        final String appName = context.getString(R.string.app_name);
        final SharedPreferences sp = context.getSharedPreferences(Utility.APP_NAME, 0);
        PrayerService prayerService = BaseRetrofit.getPrayerService();
        prayerService.downloadPrayerAudioFile(Constants.API_PRAYER_DOWNLOAD_URL).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + appName);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    File audioFile = new File(dir, Constants.PRAYER_AUDIO_FILE_NAME);
                    InputStream inputStream = null;
                    OutputStream outputStream = null;
                    ResponseBody body = response.body();
                    if (body == null) {
                        return;
                    }
                    try {
                        byte[] fileReader = new byte[4096];
                        long fileSize = body.contentLength();
                        long fileSizeDownloaded = 0;
                        inputStream = body.byteStream();
                        outputStream = new FileOutputStream(audioFile);
                        while (true) {
                            int read = inputStream.read(fileReader);
                            if (read == -1) {
                                break;
                            }
                            outputStream.write(fileReader, 0, read);
                            fileSizeDownloaded += read;
                        }

                        outputStream.flush();
                        sp.edit().putLong(Constants.KEY_LAST_DONWLOAD_PRAYER_AUDIO_TIME, System.currentTimeMillis()).apply();
                    } catch (IOException e) {
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    }
                } catch (IOException e) {
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    public static void setPrayerAudioOn(Context context, boolean on) {
        SharedPreferences sp = context.getSharedPreferences(Utility.APP_NAME, 0);
        sp.edit().putBoolean(Constants.KEY_PRAYER_AUDIO_ON, on).apply();
    }

    public static boolean isPrayerAudioOn(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Utility.APP_NAME, 0);
        return sp.getBoolean(Constants.KEY_PRAYER_AUDIO_ON, true);
    }

    public static boolean isRecommendDevotionSite(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Utility.APP_NAME, 0);
        return sp.getBoolean(Constants.KEY_IS_RECOMMEND_DEVOTION_SITE, true);
    }

    public static void setIsRecommendDevotionSite(Context context, boolean is) {
        SharedPreferences sp = context.getSharedPreferences(Utility.APP_NAME, 0);
        sp.edit().putBoolean(Constants.KEY_IS_RECOMMEND_DEVOTION_SITE, is).apply();
    }

    public static String getSiteLastedDevotionIds(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Utility.APP_NAME, 0);
        return sp.getString(Constants.KEY_SITE_LASTED_DEVOTION_ID, "");
    }

    public static void setSiteLastedDevotionIds(Context context, String json) {
        SharedPreferences sp = context.getSharedPreferences(Utility.APP_NAME, 0);
        sp.edit().putString(Constants.KEY_SITE_LASTED_DEVOTION_ID, json).apply();
    }

    public static int getAllLastedDevotionIds(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Utility.APP_NAME, 0);
        return sp.getInt(Constants.KEY_All_LASTED_DEVOTION_ID, 0);
    }

    public static void setAllLastedDevotionIds(Context context, int id) {
        SharedPreferences sp = context.getSharedPreferences(Utility.APP_NAME, 0);
        sp.edit().putInt(Constants.KEY_All_LASTED_DEVOTION_ID, id).apply();
    }

    public static boolean isScreenOn(Context context) {
        if (Build.VERSION.SDK_INT >= 20) {
            // If you use API20 or more:
            DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            for (Display display : dm.getDisplays()) {
                if (display.getState() != Display.STATE_OFF) {
                    return true;
                }
            }
            return false;
        } else {
            // If you use less than API20:
            PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
            if (powerManager.isScreenOn()) {
                return true;
            }
            return false;
        }
    }

    public static void setIsFirstPray(Context context, boolean isFirstPray) {
        SharedPreferences sp = context.getSharedPreferences(Utility.APP_NAME, 0);
        sp.edit().putBoolean(Constants.KEY_IS_FIRST_PRAY, isFirstPray).apply();
    }

    public static boolean isFirstPray(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Utility.APP_NAME, 0);
        return sp.getBoolean(Constants.KEY_IS_FIRST_PRAY, true);
    }

    public static String formatDuring(long mss) {
        String str = "";
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        if (days > 0) {
            str = days + " d ";
        }
        if (hours > 0) {
            str = str + hours + " h ";
        }
        if (minutes > 0) {
            str = str + minutes + " m ";
        }
        return str;
    }

    public static boolean isPkgExists(Context context, String pkg) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkg, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    public static int getCurrentTimePrayerPeople(List<PrayerPeopleResponse.DataBean.TotalBean> totalBeanList) {
        if (totalBeanList == null || totalBeanList.isEmpty()) {
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Random random = new Random();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        for (PrayerPeopleResponse.DataBean.TotalBean totalBean : totalBeanList) {
            if (totalBean != null && totalBean.getHour() == hour) {
                return random.nextInt(totalBean.getRangeMax() - totalBean.getRangeMin() + 1) + totalBean.getRangeMin();
            }
        }
        return 0;
    }
}