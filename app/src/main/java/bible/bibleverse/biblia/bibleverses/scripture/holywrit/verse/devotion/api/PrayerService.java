package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.EmptyResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PrayerCategoryResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PrayerPeopleResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PrayerResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by yzq on 2017/2/24.
 */

public interface PrayerService {


    // https://api.freereadbible.com/prayer/category/lists
    @Headers("Cache-Control: public, max-age=604800")
    @GET("prayer/category/lists")
    Call<PrayerCategoryResponse> getPrayerCategory();

    // https://api.freereadbible.com/prayer/category
    @Headers("Cache-Control: public, max-age=1604800")
    @GET("prayer/category")
    Call<PrayerResponse> getPrayer4Category(@Query("id") String categoryId, @Query("beginId") String beginId, @Query("endId") String endId);

    // https://api.freereadbible.com/prayer/view
    @Headers("Cache-Control: public, max-age=1800")
    @GET("prayer/view")
    Call<EmptyResponse> setPrayerViewed(@Query("id") String prayerId);

    // http://files.freereadbible.com/music/bg/Abide_With_Me.m4a
    @GET
    Call<ResponseBody> downloadPrayerAudioFile(@Url String url);

    // https://api.freereadbible.com/prayer/pick
    @Headers("Cache-Control: public, max-age=1800")
    @GET("prayer/pick")
    Call<PrayerResponse> getPrayer4Notification(@Query("id") String categoryId);

    // https://api.freereadbible.com/prayer/foobar
    @Headers("Cache-Control: public, max-age=604800")
    @GET("prayer/foobar")
    Call<PrayerPeopleResponse> getPrayerPeopleNum();
}
