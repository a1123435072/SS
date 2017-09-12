package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.service;

import android.content.Context;

import com.fw.basemodules.rest.AbstractService;

import org.json.JSONArray;
import org.json.JSONObject;


import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.protobuf.VODLProto;
import me.onemobile.cache.CacheFile;
import me.onemobile.cache.DataCacheUtil;
import me.onemobile.rest.client.RESTClient;
import me.onemobile.rest.client.Response;

/**
 * Created by Mr_ZY on 16/10/28.
 */

public class DailyVerseService extends AbstractService<VODLProto.VODL> {
    private final static int EXPIRE = 24 * 60 * 60 * 1000;
    private final static String URL_1 = "http://api.freereadbible.com/book/recommend?to_day=%1$s";
    private final static String URL_2 = "http://api.freereadbible.com/book/recommend?to_day=%1$s&from_day=%2$s";

    public DailyVerseService(Context ctx) {

        super(ctx, "VOD/VODL");
    }

    @Override
    protected VODLProto.VODL getFromCache(CacheFile cacheFile, String s, String... strings) {
        return cacheFile.read(VODLProto.VODL.class);
    }

    @Override
    protected Response request(String etag, String uriPath, String... params) {
        String url = "";
        if (params.length > 1) {
            url = String.format(URL_2, params[0], params[1]);
        } else if (params.length == 1) {
            url = String.format(URL_1, params[0]);
        } else {
            return null;
        }
        RESTClient client = RESTClient.create(url);
        Response response = client.get();
        return response;
    }

    @Override
    protected VODLProto.VODL parseResponse(Response response, String uriPath, String... params) {
        VODLProto.VODL vodl = new VODLProto.VODL();
        JSONObject root = (JSONObject) response.getEntity();
        if (root == null)
            return null;
        try {
            JSONObject data = root.optJSONObject("data");
            int count = data.optInt("count");
            vodl.setCount(count);
            JSONArray lists = data.optJSONArray("lists");
            for (int i = 0; i < lists.length(); i++) {
                VODLProto.VODL.VOD vod = new VODLProto.VODL.VOD();
                JSONObject ob = (JSONObject) lists.get(i);
                String day = ob.optString("day");
                int bookid = ob.optInt("bookid");
                int chapterid = ob.optInt("chapterid");
                String verseid = ob.optString("verseid");
                vod.setDay(day);
                vod.setBookid(bookid);
                vod.setChapterid(chapterid);
                vod.setVerseid(verseid);
                vodl.addLists(vod);
            }

        } catch (Exception e) {

        }
        if (vodl.getListsCount() > 0) {
            DataCacheUtil.backup("temp", EXPIRE, vodl, uriPath, params);

        }
        return vodl;


    }

}
