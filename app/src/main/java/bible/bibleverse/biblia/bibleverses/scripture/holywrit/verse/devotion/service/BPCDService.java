package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.service;

import android.content.Context;

import com.fw.basemodules.rest.AbstractService;

import org.json.JSONArray;
import org.json.JSONObject;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.protobuf.BPCDProto;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.protobuf.BPProto;
import me.onemobile.cache.CacheFile;
import me.onemobile.cache.DataCacheUtil;
import me.onemobile.rest.client.RESTClient;
import me.onemobile.rest.client.Response;


/**
 * Created by Mr_ZY on 16/10/28.
 */

public class BPCDService extends AbstractService<BPCDProto.BPCD> {
    private final static int EXPIRE = 24 * 60 * 60 * 1000;
    private final static String URL_1 = "http://api.freereadbible.com/plan/planLists?id=%s";

    public BPCDService(Context ctx) {
        super(ctx, "BP/BPCD");
    }

    @Override
    protected BPCDProto.BPCD getFromCache(CacheFile cacheFile, String s, String... strings) {
        return cacheFile.read(BPCDProto.BPCD.class);
    }

    @Override
    protected Response request(String etag, String uriPath, String... params) {
        String url = String.format(URL_1, params[0]);
        RESTClient client = RESTClient.create(url);

        Response response = client.get();
        return response;
    }

    @Override
    protected BPCDProto.BPCD parseResponse(Response response, String uriPath, String... params) {
        BPCDProto.BPCD bpcd = new BPCDProto.BPCD();
        JSONObject root = (JSONObject) response.getEntity();
        if (root == null)
            return null;
        try {
            int plansCount = root.optInt("plansCount");
            String cateName = root.optString("cateName");
            bpcd.setPlansCount(plansCount);
            bpcd.setCateName(cateName);

            JSONArray planList = root.optJSONArray("list");
            for (int i = 0; i < planList.length(); i++) {
                BPProto.BP bp = new BPProto.BP();
                JSONObject bpObj = (JSONObject) planList.get(i);
                int planId = bpObj.optInt("planId");
                String iconUrl = bpObj.optString("iconUrl");
                String imageUrl = bpObj.optString("imageUrl");
                String title = bpObj.optString("title");
                int daysCount = bpObj.optInt("daysCount");
                bp.setPlanId(planId);
                bp.setIconUrl(iconUrl);
                bp.setImageUrl(imageUrl);
                bp.setTitle(title);
                bp.setDaysCount(daysCount);
                bpcd.addList(bp);
            }

        } catch (Exception e) {

        }
        if (bpcd.getPlansCount() > 0) {
            DataCacheUtil.backup("temp", EXPIRE, bpcd, uriPath, params);

        }
        return bpcd;
    }

}
