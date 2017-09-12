package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;

import com.fw.basemodules.utils.OmAsyncTask;

import java.io.IOException;
import java.util.List;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.BaseRetrofit;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.PrayerService;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.VerseService;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PrayerBean;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PrayerResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.VerseListResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.DateTimeUtil;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.notification.NotificationBase;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.notification.NotificationUtils;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.protobuf.VODLProto;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import retrofit2.Call;
import retrofit2.Response;

import static bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.DateTimeUtil.getDateStr4ApiRequest;
import static yuku.afw.App.context;

/**
 * Created by Mr_ZY on 16/11/3.
 */

public class DailyNotificationReceiver extends BroadcastReceiver {
    Context mContext;

    int mNotifyId;
    String mPrayCatName, mPrayCatId;
    int mPrayerType;
    Intent mIntent;
    private int mAdViewId;
    Handler mHandler;
    Runnable showNotifyRunnable = new Runnable() {
        @Override
        public void run() {
            showNotifyById();
        }
    };

    public static Intent createIntent(Context context, long planDbId, String planTitle, String bigImg, long planStartTime) {
        Intent intent = new Intent(context, DailyNotificationReceiver.class);
        intent.putExtra("notify_id", Constants.NOTIFY_BIBLE_PLAN_ID);
        intent.putExtra("plan_id", planDbId);
        intent.putExtra("plan_title", planTitle);
        intent.putExtra("big_img", bigImg);
        intent.putExtra("start_time", planStartTime);
        return intent;
    }

    public static Intent createIntent(Context context, int id) {
        Intent intent = new Intent(context, DailyNotificationReceiver.class);
        intent.putExtra("notify_id", id);
        return intent;
    }

    public static Intent createIntent(Context context, int id, int prayerType, String catId, String catName) {
        Intent intent = new Intent(context, DailyNotificationReceiver.class);
        intent.putExtra("notify_id", id);
        intent.putExtra(Constants.DP_KEY_TYPE, prayerType);
        intent.putExtra(Constants.DP_KEY_CATEGORY_TYPE, catId);
        intent.putExtra(Constants.DP_KEY_CATEGORY_NAME, catName);
        return intent;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        mIntent = intent;
        mContext = context;
        mPrayCatId = null;
        if (intent != null) {
            mNotifyId = intent.getIntExtra("notify_id", 0);
            mPrayerType = intent.getIntExtra(Constants.DP_KEY_TYPE, 0);
            mPrayCatId = intent.getStringExtra(Constants.DP_KEY_CATEGORY_TYPE);
            mPrayCatName = intent.getStringExtra(Constants.DP_KEY_CATEGORY_NAME);
        }
        // sign in not use launcher ad
        showNotifyById();
    }

    private void showNotifyById() {
        if (mHandler != null) {
            mHandler.removeCallbacks(showNotifyRunnable);
        }
        switch (mNotifyId) {
            case Constants.NOTIFY_DAILY_VERSE_ID:
                new getDailyVerseData().execute();
                break;
            case Constants.NOTIFY_BIBLE_PLAN_ID:
                NotificationUtils.showBiblePlanNotification(context,
                        mIntent.getLongExtra("plan_id", 0),
                        mIntent.getStringExtra("plan_title"),
                        mIntent.getStringExtra("big_img"),
                        mIntent.getLongExtra("start_time", Long.valueOf(0)));
                NotificationBase.setShowingNotificationStatus(context, Constants.NOTIFY_BIBLE_PLAN_ID, true);
                break;
            case Constants.NOTIFY_DAILY_PRAYER_ID:
                if (!TextUtils.isEmpty(mPrayCatId)) {
                    new GetDailyPrayerData().execute();
                } else {
                    NotificationUtils.showDailyPrayerNt(context, mPrayerType);
                    NotificationUtils.showDailyPrayerFloatNt(context, mPrayerType);
                    NotificationBase.setShowingNotificationStatus(context, Constants.NOTIFY_DAILY_PRAYER_ID, true);
                }
                break;
            case Constants.NOTIFY_DAILY_SIGN_IN_ID:
                NotificationUtils.showDailySignInNt(context);
                NotificationUtils.showDailySignInFloatNt(context);
                NotificationBase.setShowingNotificationStatus(context, Constants.NOTIFY_DAILY_SIGN_IN_ID, true);
                break;
        }
    }


    private class getDailyVerseData extends OmAsyncTask<Void, Void, VerseListResponse.DataBean.VerseBean> {
        @Override
        protected VerseListResponse.DataBean.VerseBean doInBackground(Void... longs) {
            VerseService verseService = BaseRetrofit.getVerseService();
            String today = DateTimeUtil.getDateStr4ApiRequest(System.currentTimeMillis());
            Call<VerseListResponse> call = verseService.getVerseListNew(today, today);
            try {
                Response<VerseListResponse> response = call.execute();
                VerseListResponse verseListResponse = response != null ? response.body() : null;
                if (verseListResponse != null && verseListResponse.getData() != null && verseListResponse.getData().getLists() != null && verseListResponse.getData().getLists().size() > 0) {
                    return verseListResponse.getData().getLists().get(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(VerseListResponse.DataBean.VerseBean vod) {
            if (vod == null) return;
            NotificationUtils.showDailyVerseNotification(context, vod);
            NotificationUtils.showDailyVerseFloatNt(context, vod);
            NotificationBase.setShowingNotificationStatus(context, Constants.NOTIFY_DAILY_VERSE_ID, true);
        }
    }

    private class GetDailyPrayerData extends OmAsyncTask<Void, Void, List<PrayerBean>> {
        @Override
        protected List<PrayerBean> doInBackground(Void... longs) {
            try {
                PrayerService prayerService = BaseRetrofit.getPrayerService();
                Call<PrayerResponse> call = prayerService.getPrayer4Notification(mPrayCatId);
                Response<PrayerResponse> response = call.execute();
                PrayerResponse prayerResponse = response != null ? response.body() : null;
                if (prayerResponse != null) {
                    PrayerResponse.Data data = prayerResponse.getData();
                    if (data != null) {
                        List<PrayerBean> prayList = data.getPrays();
                        if (prayList != null && !prayList.isEmpty()) {
                            return prayList;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<PrayerBean> list) {
            if (list != null && list.size() > 0) {
                PrayerBean pray = list.get(0);
                if (pray != null) {
                    NotificationUtils.showDailyPrayerNt(context, pray, mPrayCatName);
                    NotificationUtils.showDailyPrayerFloatNt(context, pray, mPrayCatName);
                    NotificationBase.setShowingNotificationStatus(context, Constants.NOTIFY_DAILY_PRAYER_ID, true);
                }
            } else {
                NotificationUtils.showDailyPrayerNt(context, mPrayerType);
                NotificationUtils.showDailyPrayerFloatNt(context, mPrayerType);
                NotificationBase.setShowingNotificationStatus(context, Constants.NOTIFY_DAILY_PRAYER_ID, true);
            }
        }
    }
}