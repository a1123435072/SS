package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.BuildConfig;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.SelfAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.BaseRetrofit;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.base.OkCacheControl;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PrayerPeopleResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.DevotionDetailWebActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.NewDailyVerseDetailActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.NewMainActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.NewReadingActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.PlanVerseDetailActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.PrayerDetailActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventUserOperationChanged;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.notification.DailyNotifyAlarm;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.notification.NotificationBase;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.service.SyncService;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import de.greenrobot.event.EventBus;
import io.fabric.sdk.android.Fabric;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Version;
import yuku.afw.storage.Preferences;
import yuku.alkitabintegration.display.Launcher;

public class App extends yuku.afw.App implements Application.ActivityLifecycleCallbacks {
    public static final String TAG = App.class.getSimpleName();

    private static boolean initted = false;
    public static int sessionDepth = 0;

    static final Interceptor userAgent = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            final Request originalRequest = chain.request();
            final Request requestWithUserAgent = originalRequest.newBuilder()
                    .removeHeader("User-Agent")
                    .addHeader("User-Agent", Version.userAgent() + " " + App.context.getPackageName() + "/" + App.getVersionName())
                    .build();
            return chain.proceed(requestWithUserAgent);
        }
    };

    public enum OkHttpClientWrapper {
        INSTANCE;

        final OkHttpClient longTimeoutClient;

        {
            final OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.addNetworkInterceptor(userAgent)
                    .connectTimeout(300, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .writeTimeout(600, TimeUnit.SECONDS);
            longTimeoutClient = builder.build();
        }
    }

    public enum GsonWrapper {
        INSTANCE;

        Gson gson = new Gson();
    }

    public static String downloadString(String url) throws IOException {
        return downloadCall(url).execute().body().string();
    }

    public static byte[] downloadBytes(String url) throws IOException {
        return downloadCall(url).execute().body().bytes();
    }

    public static Call downloadCall(String url) {
        return okhttp().newCall(new Request.Builder().url(url).build());
    }

    public static OkHttpClient getLongTimeoutOkHttpClient() {
        return OkHttpClientWrapper.INSTANCE.longTimeoutClient;
    }

    private static App mInstance;

    public static App getInstance() {
        return mInstance;
    }

    private static final String MONITOR = ":monitor";

    @Override
    public void onCreate() {
        super.onCreate();
        if (TextUtils.equals(getProcessName(this), getPackageName() + MONITOR)) {
            return;
        }
        Fabric.with(this, new Crashlytics());

        AnalyticsHelper.getInstance(this).init(this);
        Bundle params = new Bundle();

        mTotalInMain = 0;
        mTotalInDevotion = 0;
        mTotalInRead = 0;
        mTotalInVerse = 0;
        registerActivityLifecycleCallbacks(this);
        Fabric.with(this, new Crashlytics());

        staticInit();
        initNotifyStatus();

        DailyNotifyAlarm.updateDailyNotifyAlarm(context);
        startSyncService();
        Utility.downloadPrayerBgAudioFile(context);
        loadPrayNumConfig();
        mInstance = this;
    }

    public synchronized static void staticInit() {
        if (initted) return;
        initted = true;

        forceUpdateConfiguration();

        // all activities need at least the activeVersion from S, so initialize it here.
        synchronized (S.class) {
            if (S.activeVersion == null) {
                S.checkActiveVersion();
            }
        }

        // also pre-calculate calculated preferences value here
        S.calculateAppliedValuesBasedOnPreferences();

        forceOverflowMenu();

        // make sure launcher do not open other variants of the app
        Launcher.setAppPackageName(context.getPackageName());
    }

    private static void forceOverflowMenu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return; // no need to do anything, it is already forced on KitKat
        }

        final ViewConfiguration config = ViewConfiguration.get(context);
        try {
            final Field sHasPermanentMenuKey = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            sHasPermanentMenuKey.setAccessible(true);
            sHasPermanentMenuKey.setBoolean(config, false);
        } catch (Exception e) {
            Log.w(TAG, "ViewConfiguration has no sHasPermanentMenuKey field", e);
        }

        try {
            final Field sHasPermanentMenuKeySet = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKeySet");
            sHasPermanentMenuKeySet.setAccessible(true);
            sHasPermanentMenuKeySet.setBoolean(config, true);
        } catch (Exception e) {
            Log.w(TAG, "ViewConfiguration has no sHasPermanentMenuKeySet field", e);
        }
    }

    private static Locale getLocaleFromPreferences() {
        final String lang = Preferences.getString(R.string.pref_language_key, R.string.pref_language_default);
        if (lang == null || "DEFAULT".equals(lang)) {
            return Locale.getDefault();
        }

        switch (lang) {
            case "zh-CN":
                return Locale.SIMPLIFIED_CHINESE;
            case "zh-TW":
                return Locale.TRADITIONAL_CHINESE;
            default:
                return new Locale(lang);
        }
    }

    private static float getFontScaleFromPreferences() {
        float res = 0.f;

        final String forceFontScale = Preferences.getString(R.string.pref_forceFontScale_key);
        if (forceFontScale != null && !context.getString(R.string.pref_forceFontScale_default).equals(forceFontScale)) {
            if (context.getString(R.string.pref_forceFontScale_value_x1_5).equals(forceFontScale)) {
                res = 1.5f;
            } else if (context.getString(R.string.pref_forceFontScale_value_x1_7).equals(forceFontScale)) {
                res = 1.7f;
            } else if (context.getString(R.string.pref_forceFontScale_value_x2_0).equals(forceFontScale)) {
                res = 2.0f;
            }
        }

        if (res == 0.f) {
            final float defFontScale = Settings.System.getFloat(context.getContentResolver(), Settings.System.FONT_SCALE, 1.f);
            if (BuildConfig.DEBUG) Log.d(TAG, "defFontScale: " + defFontScale);
            res = defFontScale;
        }

        return res;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        Log.d(TAG, "@@onConfigurationChanged: config changed to: " + newConfig);
        forceUpdateConfiguration();
    }

    public static void forceUpdateConfiguration() {
        final Configuration config = context.getResources().getConfiguration();
        boolean updated = false;

        final Locale locale = getLocaleFromPreferences();
        if (!U.equals(config.locale.getLanguage(), locale.getLanguage()) || !U.equals(config.locale.getCountry(), locale.getCountry())) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "@@forceUpdateConfiguration: locale will be updated to: " + locale);

            config.locale = locale;
            updated = true;
        }

        final float fontScale = getFontScaleFromPreferences();
        if (config.fontScale != fontScale) {
            if (BuildConfig.DEBUG)
                Log.d(TAG, "@@forceUpdateConfiguration: fontScale will be updated to: " + fontScale);

            config.fontScale = fontScale;
            updated = true;
        }

        if (updated) {
            context.getResources().updateConfiguration(config, null);
        }
    }

    public static LocalBroadcastManager getLbm() {
        return LocalBroadcastManager.getInstance(context);
    }

    public static Gson getDefaultGson() {
        return GsonWrapper.INSTANCE.gson;
    }


    private static ExecutorService eventSubmitter = Executors.newSingleThreadExecutor();

    private static OkHttpClient okhttp;

    @NonNull
    public static synchronized OkHttpClient okhttp() {
        OkHttpClient res = okhttp;
        if (res == null) {
            final File cacheDir = new File(context.getCacheDir(), "okhttp-cache");
            if (!cacheDir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                cacheDir.mkdirs();
            }

            final OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .cache(new Cache(cacheDir, 50 * 1024 * 1024))
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addNetworkInterceptor(userAgent);

            if (BuildConfig.DEBUG) {
                builder.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
            }

            okhttp = res = builder.build();
        }
        return res;
    }

    static Picasso picasso;

    @NonNull
    public static synchronized Picasso picasso() {
        Picasso res;
        if (picasso == null) {
            picasso = res = new Picasso.Builder(context)
                    .defaultBitmapConfig(Bitmap.Config.RGB_565)
                    .downloader(new OkHttp3Downloader(okhttp()))
                    .build();
            return res;
        }
        return picasso;
    }

    private void startSyncService() {
        Intent serviceIntent = new Intent(this, SyncService.class);
        startService(serviceIntent);
    }

    public static OkHttpClient defaultOkHttpClient() {
        return defaultOkHttpClient(30);
    }

    public static OkHttpClient defaultOkHttpClient(int minutes) {
        OkCacheControl.NetworkMonitor networkMonitor = new OkCacheControl.NetworkMonitor() {
            @Override
            public boolean isOnline() {
                final ConnectivityManager connectivityManager =
                        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                // network info
                final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return (networkInfo != null && networkInfo.isAvailable());
            }
        };

        OkHttpClient.Builder builder = OkCacheControl.on(new OkHttpClient.Builder())
                .overrideServerCachePolicy(minutes, TimeUnit.MINUTES)
                .forceCacheWhenOffline(networkMonitor)
                .apply(); // return to the OkHttpClient.Builder instance

        // cache dir
        File httpCacheDirectory = new File(context.getCacheDir(), ".cache");
        // cache size 10M
        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);

        builder.cache(cache)
                .readTimeout(30 * 1000, TimeUnit.MILLISECONDS)
                .connectTimeout(30 * 1000, TimeUnit.MILLISECONDS);

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(LoggingInterceptor);
        }

        return builder.build();
    }

    private static final Interceptor LoggingInterceptor = new Interceptor() {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();
            long t1 = System.nanoTime();
            Log.i(TAG, String.format("Sending request %s on %s%n%s", request.url(), chain.connection(), request.headers()));
            Response response = chain.proceed(request);
            long t2 = System.nanoTime();
            Log.i(TAG, String.format("Received response for %s in %.1fms%n%s", response.request().url(), (t2 - t1) / 1e6d, response.headers()));
            return response;
        }
    };

    // page time analysis
    private long mTotalInMain;
    private long mTotalInRead;
    private long mTotalInDevotion;
    private long mTotalInVerse;
    private long mTotalInPrayer;
    private long mTotalInPlanVerse;

    private long mMainPageStart;
    private long mReadPageStart;
    private long mDevotionDetailPageStart;
    private long mDailyVersePageStart;
    private long mPrayerPageStart;
    private long mPlanVerseStart;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        sessionDepth++;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (activity instanceof NewMainActivity) {
            mMainPageStart = System.currentTimeMillis();
        } else if (activity instanceof NewReadingActivity) {
            mReadPageStart = System.currentTimeMillis();
        } else if (activity instanceof DevotionDetailWebActivity) {
            mDevotionDetailPageStart = System.currentTimeMillis();
        } else if (activity instanceof NewDailyVerseDetailActivity) {
            mDailyVersePageStart = System.currentTimeMillis();
        } else if (activity instanceof PrayerDetailActivity) {
            mPrayerPageStart = System.currentTimeMillis();
        } else if (activity instanceof PlanVerseDetailActivity) {
            mPlanVerseStart = System.currentTimeMillis();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activity instanceof NewMainActivity) {
            mTotalInMain += (System.currentTimeMillis() - mMainPageStart);
        } else if (activity instanceof NewReadingActivity) {
            long readTime = (System.currentTimeMillis() - mReadPageStart);
            mTotalInRead += readTime;
            S.getDb().addTodayBibleReadTime(readTime);
            EventBus.getDefault().post(new EventUserOperationChanged(EventUserOperationChanged.TYPE_READ_BIBLE_TIME));
        } else if (activity instanceof DevotionDetailWebActivity) {
            mTotalInDevotion += (System.currentTimeMillis() - mDevotionDetailPageStart);
        } else if (activity instanceof NewDailyVerseDetailActivity) {
            mTotalInVerse += (System.currentTimeMillis() - mDailyVersePageStart);
        } else if (activity instanceof PlanVerseDetailActivity) {
            mTotalInPlanVerse += (System.currentTimeMillis() - mPlanVerseStart);
        } else if (activity instanceof PrayerDetailActivity) {
            mTotalInPrayer += (System.currentTimeMillis() - mPrayerPageStart);
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (sessionDepth > 0) {
            sessionDepth--;
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public void sendTimeAnalysis() {
        if (mTotalInRead > 1000) {
            SelfAnalyticsHelper.sendTimeAnalytics(this, AnalyticsConstants.P_BIBLEPAGE, String.valueOf(mTotalInRead));
        }
        if (mTotalInPrayer > 1000) {
            SelfAnalyticsHelper.sendTimeAnalytics(this, AnalyticsConstants.P_PRAYER, String.valueOf(mTotalInPrayer));
        }
        if (mTotalInVerse > 1000) {
            SelfAnalyticsHelper.sendTimeAnalytics(this, AnalyticsConstants.P_VERSEPAGE, String.valueOf(mTotalInVerse));
        }
        if (mTotalInDevotion > 1000) {
            SelfAnalyticsHelper.sendTimeAnalytics(this, AnalyticsConstants.P_DEVOTION_PAGE, String.valueOf(mTotalInDevotion));
        }
        if (mTotalInMain > 1000) {
            SelfAnalyticsHelper.sendTimeAnalytics(this, AnalyticsConstants.P_HOME, String.valueOf(mTotalInMain));
        }
        if (mTotalInPlanVerse > 1000) {
            SelfAnalyticsHelper.sendTimeAnalytics(this, AnalyticsConstants.P_PLAN_VERSE_PAGE, String.valueOf(mTotalInPlanVerse));
        }
        mTotalInMain = 0;
        mTotalInDevotion = 0;
        mTotalInRead = 0;
        mTotalInVerse = 0;
        mTotalInPrayer = 0;
        mTotalInPlanVerse = 0;
    }

    private void initNotifyStatus() {
        NotificationBase.setShowingNotificationStatus(context, Constants.NOTIFY_DAILY_SIGN_IN_ID, false);
        NotificationBase.setShowingNotificationStatus(context, Constants.NOTIFY_DAILY_VERSE_ID, false);
        NotificationBase.setShowingNotificationStatus(context, Constants.NOTIFY_BIBLE_PLAN_ID, false);
        NotificationBase.setShowingNotificationStatus(context, Constants.NOTIFY_DAILY_DEVOTION_ID, false);
        NotificationBase.setShowingNotificationStatus(context, Constants.NOTIFY_DAILY_PRAYER_ID, false);
    }

    public void saveTodayOperationDataToSP() {
        Preferences.setLong(Constants.KEY_WELCOME_DEVOTION_COUNT, S.getDb().getAllDevotionReadHistoryCount());
        Preferences.setLong(Constants.KEY_WELCOME_PRAY_COUNT, S.getDb().getAllPrayerHistoryCount());
        Preferences.setLong(Constants.KEY_WELCOME_READ_TIME, S.getDb().getReadBibleTotalTime());
    }

    private void loadPrayNumConfig() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    retrofit2.Call<PrayerPeopleResponse> peopleNumCall = BaseRetrofit.getPrayerService().getPrayerPeopleNum();
                    peopleNumCall.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public String getProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        if (infos != null) {
            for (ActivityManager.RunningAppProcessInfo processInfo : infos) {
                if (processInfo.pid == pid) {
                    return processInfo.processName;
                }
            }
        }
        return null;
    }
}
