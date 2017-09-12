package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.receiver.ScreenOnReceiver;

/**
 * Created by KevinZhong on 16/9/1.
 */
public class SyncService extends IntentService {

    public SyncService() {
        super("SyncService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // Active log
        AnalyticsHelper.getInstance(this.getApplicationContext()).sendReachAnalytics();
        // register screen receiver
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        getApplicationContext().registerReceiver(new ScreenOnReceiver(), intentFilter);
    }

}
