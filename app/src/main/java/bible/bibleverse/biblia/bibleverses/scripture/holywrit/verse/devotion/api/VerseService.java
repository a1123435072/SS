package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.DailyVerseDetailResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.EmptyResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.VerseListResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.DailyVerseListActivity;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by yzq on 2017/2/24.
 */

public interface VerseService {


    // https://api.freereadbible.com/book/recommend?to_day=%1$s&from_day=%2$s
    @Headers("Cache-Control: public, max-age=1800")
    @GET("book/recommend")
    Call<VerseListResponse> getVerseList(@Query("from_day") String from, @Query("to_day") String to);

    // https://api.freereadbible.com/book/verse?to_day=%1$s&from_day=%2$s
    @Headers("Cache-Control: public, max-age=1800")
    @GET("book/verse")
    Call<VerseListResponse> getVerseListNew(@Query("from_day") String from, @Query("to_day") String to);

    // https://api.freereadbible.com/counter/upload
    @Headers("Cache-Control: public, max-age=1800")
    @GET("counter/upload")
    Call<EmptyResponse> postVerseAction(@Query("type") int type, @Query("id") String id);


    // http://api.freereadbible.com/devotion/related?id=%s
    @Headers("Cache-Control: public, max-age=1800")
    @GET("devotion/related")
    Call<VerseListResponse> getRelatedDailyVerse(@Query("id") String id);

    // http://api.freereadbible.com/devotion/details?id=%s
    @Headers("Cache-Control: public, max-age=1800")
    @GET("devotion/details")
    Call<DailyVerseDetailResponse> getDailyVerseDetails(@Query("id") String id);

}
