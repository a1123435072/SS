package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.DailyVerseDetailResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.DevotionListResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.DevotionSitesResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.EmptyResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.LockScreenResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PopularResponse;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by yzq on 2017/2/24.
 */

public interface DevotionService {

    //https://api.freereadbible.com/devotion/site/lists
    @Headers({"Cache-Control: public, max-age=604800", "Content-Type: application/json"})
    @POST("devotion/site/lists")
    Call<DevotionSitesResponse> getDevotionSites(@Body RequestBody body);

    //https://api.freereadbible.com/devotion/site/content
    @Headers("Cache-Control: public, max-age=1800")
    @GET("devotion/site/content")
    Call<DevotionListResponse> getSiteDevotionList(@Query("id") String id, @Query("beginId") String beginId, @Query("endId") String endId);

    // https://api.freereadbible.com/devotion/view
    @Headers("Cache-Control: public, max-age=1800")
    @GET("devotion/view")
    Call<EmptyResponse> setDevotionViewed(@Query("id") String devotionId);

    //https://api.freereadbible.com/devotion/home/feed
    @Headers("Cache-Control: public, max-age=1800")
    @GET("/devotion/home/feed")
    Call<DevotionListResponse> getHomeDevotionList(@Query("id") String ids);

    //https://api.freereadbible.com/devotion/home/feed
    @Headers("Cache-Control: public, max-age=1800")
    @GET("/devotion/home/feed")
    Call<DevotionListResponse> getDevotionAllList(@Query("id") String ids, @Query("endId") String endId);

    // https://api.freereadbible.com/lockscreen/content
    @Headers("Cache-Control: public, max-age=3600")
    @GET("lockscreen/content")
    Call<LockScreenResponse> getLockScreenMixList(@Query("hour") String hour, @Query("verse") String verse);

    // http://api.freereadbible.com/hots/top
    @Headers("Cache-Control: public, max-age=30")
    @GET("hots/top")
    Call<PopularResponse> getPopularContent();
}
