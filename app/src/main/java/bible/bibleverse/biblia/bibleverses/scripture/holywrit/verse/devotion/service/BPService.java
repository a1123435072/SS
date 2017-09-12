package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.service;

import android.content.Context;

import com.fw.basemodules.rest.AbstractService;

import org.json.JSONObject;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.protobuf.BPProto;
import me.onemobile.cache.CacheFile;
import me.onemobile.cache.DataCacheUtil;
import me.onemobile.rest.client.RESTClient;
import me.onemobile.rest.client.Response;

/**
 * Created by Mr_ZY on 16/10/28.
 */

public class BPService extends AbstractService<BPProto.BP> {
    private final static int EXPIRE = 24 * 60 * 60 * 1000;
    private final static String URL = "http://api.freereadbible.com/plan/planDesc?id=%s";

    public BPService(Context ctx) {
        super(ctx, "BP/PB");
    }

    @Override
    protected BPProto.BP getFromCache(CacheFile cacheFile, String s, String... strings) {
        return cacheFile.read(BPProto.BP.class);
    }

    @Override
    protected Response request(String etag, String uriPath, String... params) {
        String url = String.format(URL, params[0]);
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
            int planId_ = data.optInt("planId");
            int daysCount = data.optInt("daysCount");
            int categoryId = data.optInt("categoryId");
            String title = data.optString("title");
            String iconUrl = data.optString("iconUrl");
            String imageUrl = data.optString("imageUrl");
            String planDes = data.optString("planDes");
            bp.setPlanId(planId_);
            bp.setDaysCount(daysCount);
            bp.setCategoryId(categoryId);
            bp.setTitle(title);
            bp.setIconUrl(iconUrl);
            bp.setImageUrl(imageUrl);
            bp.setPlanDes(planDes);
        } catch (Exception e) {

        }
        if (bp.getPlanDaysListCount() > 0) {
            DataCacheUtil.backup("temp", EXPIRE, bp, uriPath, params);

        }
        return bp;
    }

}
