package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by miaohongwei on 2017/8/22.
 */

public class NewAnalyticsHelper {

    private static NewAnalyticsHelper sInstance;
    private FirebaseAnalytics mAnalytics;

    public static NewAnalyticsHelper init(Context context){
        if(sInstance == null){
            sInstance = new NewAnalyticsHelper(context);
        }

        return sInstance;
    }

    public static NewAnalyticsHelper getInstance(){
        return sInstance;
    }

    private NewAnalyticsHelper(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context cannot be null");
        }

        mAnalytics = FirebaseAnalytics.getInstance(context);
        mAnalytics.setUserProperty("UUID", AnalyticsUtils.getUUID(context));
    }

    public void sendEvent(String event, String action){
        Bundle params = new Bundle();
        params.putString("action", action);

        mAnalytics.logEvent(event, params);
    }

    public void sendEvent(String event, Bundle params){
        mAnalytics.logEvent(event, params);
    }


}
