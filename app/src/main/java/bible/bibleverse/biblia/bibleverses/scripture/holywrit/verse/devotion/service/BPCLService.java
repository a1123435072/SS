package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.service;

import android.content.Context;

import com.fw.basemodules.rest.AbstractService;

import org.json.JSONArray;
import org.json.JSONObject;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.protobuf.BPCLProto;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.protobuf.BPProto;
import me.onemobile.cache.CacheFile;
import me.onemobile.cache.DataCacheUtil;
import me.onemobile.rest.client.RESTClient;
import me.onemobile.rest.client.Response;

/**
 * Created by Mr_ZY on 16/10/28.
 */

public class BPCLService extends AbstractService<BPCLProto.BPCL> {
    private final static int EXPIRE = 24 * 60 * 60 * 1000;
    private final static String URL_1 = "http://api.freereadbible.com/plan/categoryList";

    public BPCLService(Context ctx) {
        super(ctx, "BP/BPCL");
    }

    @Override
    protected BPCLProto.BPCL getFromCache(CacheFile cacheFile, String s, String... strings) {
        return cacheFile.read(BPCLProto.BPCL.class);
    }

    @Override
    protected Response request(String etag, String uriPath, String... params) {
        RESTClient client = RESTClient.create(URL_1);

        Response response = client.get();
        return response;
    }

    @Override
    protected BPCLProto.BPCL parseResponse(Response response, String uriPath, String... params) {
        BPCLProto.BPCL bpcl = new BPCLProto.BPCL();
        JSONObject data = (JSONObject) response.getEntity();
        if (data == null)
            return null;
        try {
            int catesCount = data.optInt("catesCount");
            bpcl.setCatesCount(catesCount);

            JSONArray cateList = data.optJSONArray("cateList");
            for (int i = 0; i < cateList.length(); i++) {
                BPCLProto.BPCL.BPC bpc = new BPCLProto.BPCL.BPC();
                JSONObject ob = (JSONObject) cateList.get(i);
                int cateId = ob.optInt("categoryId");
                String cateName = ob.optString("categoryName");
                bpc.setCategoryId(cateId);
                bpc.setCategoryName(cateName);

                JSONArray PreviewPlanList = ob.optJSONArray("list");
                for (int j = 0; j < PreviewPlanList.length(); j++) {
                    BPProto.BP bp = new BPProto.BP();
                    JSONObject bpObj = (JSONObject) PreviewPlanList.get(j);
                    int planId = bpObj.optInt("planId");
                    String iconUrl = bpObj.optString("iconUrl");
                    String title = bpObj.optString("title");
                    int daysCount = bpObj.optInt("daysCount");
                    bp.setPlanId(planId);
                    bp.setIconUrl(iconUrl);
                    bp.setTitle(title);
                    bp.setDaysCount(daysCount);
                    bpc.addList(bp);
                }
                bpcl.addCateList(bpc);
            }

        } catch (Exception e) {

        }
        if (bpcl.getCatesCount() > 0) {
            DataCacheUtil.backup("temp", EXPIRE, bpcl, uriPath, params);

        }
        return bpcl;
    }

}
