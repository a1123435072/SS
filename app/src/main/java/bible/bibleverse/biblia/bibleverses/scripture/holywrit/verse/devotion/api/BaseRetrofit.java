package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api;


import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.App;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by yzq on 17/1/19.
 */

public class BaseRetrofit {

    private static Retrofit retrofit;

    public static DevotionService getDevotionService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.API_BUSINESS_URL) // base api url
                    .addConverterFactory(GsonConverterFactory.create()) //default converter：Gson
                    .client(App.defaultOkHttpClient())
                    .build();
        }
        return retrofit.create(DevotionService.class);
    }

    public static PrayerService getPrayerService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.API_BUSINESS_URL) // base api url
                    .addConverterFactory(GsonConverterFactory.create()) //default converter：Gson
                    .client(App.defaultOkHttpClient())
                    .build();
        }
        return retrofit.create(PrayerService.class);
    }

    public static VerseService getVerseService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.API_BUSINESS_URL) // base api url
                    .addConverterFactory(GsonConverterFactory.create()) //default converter：Gson
                    .client(App.defaultOkHttpClient())
                    .build();
        }
        return retrofit.create(VerseService.class);
    }


}
