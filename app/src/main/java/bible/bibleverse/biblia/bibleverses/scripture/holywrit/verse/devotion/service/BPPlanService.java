package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.service;

import android.content.Context;

import com.fw.basemodules.rest.AbstractService;

import org.json.JSONArray;
import org.json.JSONObject;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.protobuf.BPProto;
import me.onemobile.cache.CacheFile;
import me.onemobile.cache.DataCacheUtil;
import me.onemobile.rest.client.RESTClient;
import me.onemobile.rest.client.Response;
import yuku.alkitab.model.Book;

public class BPPlanService extends AbstractService<BPProto.BP> {
    private final static int EXPIRE = 24 * 60 * 60 * 1000;
    private final static String URL = "http://api.freereadbible.com/plan/planContent?id=%1$s&start=%2$s&end=%3$s";

    public BPPlanService(Context ctx) {
        super(ctx, "BP/PBP");
    }

    @Override
    protected BPProto.BP getFromCache(CacheFile cacheFile, String s, String... strings) {
        return cacheFile.read(BPProto.BP.class);
    }

    @Override
    protected Response request(String etag, String uriPath, String... params) {
        String url = String.format(URL, params[0], params[1], params[2]);
        RESTClient client = RESTClient.create(url);

        Response response = client.get();
        return response;
    }

    @Override
    protected BPProto.BP parseResponse(Response response, String uriPath, String... params) {
        BPProto.BP bp = new BPProto.BP();
        JSONObject data = (JSONObject) response.getEntity();
        if (data == null)
            return null;
        try {
            JSONArray daysList = data.optJSONArray("planDaysList");
            for (int i = 0; i < daysList.length(); i++) {
                BPProto.BP.SingleDayPlan singleDayPlan = new BPProto.BP.SingleDayPlan();
                JSONObject ob = daysList.getJSONObject(i);
                JSONArray dayVerseList = ob.getJSONArray("dayVerseList");
                for (int j = 0; j < dayVerseList.length(); j++) {
                    BPProto.BP.SingleDayPlan.Verse verse = new BPProto.BP.SingleDayPlan.Verse();
                    JSONObject verseObj = dayVerseList.getJSONObject(j);
                    int bookId = verseObj.optInt("bookId");
                    int chapterId = verseObj.optInt("chapterId");
                    String verseId = verseObj.optString("verseId");
                    //if 0 set the whole of chater
                    if (verseId.equals("0")) {
                        Book book = S.activeVersion.getBook(bookId);
                        verseId = "1-" + (book.verse_counts[chapterId - 1]);
                    }
                    verse.setBookId(bookId);
                    verse.setChapterId(chapterId);
                    verse.setVerseId(verseId);
                    singleDayPlan.addVerse(verse);
                }
                bp.addPlanDaysList(singleDayPlan);
            }

        } catch (Exception e) {

        }
        if (bp.getPlanDaysListCount() > 0) {
            DataCacheUtil.backup("temp", EXPIRE, bp, uriPath, params);

        }
        return bp;
    }

}
