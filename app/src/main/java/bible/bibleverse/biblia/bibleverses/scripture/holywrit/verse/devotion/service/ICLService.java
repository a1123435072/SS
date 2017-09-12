package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.service;

import android.content.Context;

import com.fw.basemodules.rest.AbstractService;

import org.json.JSONArray;
import org.json.JSONObject;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.protobuf.ICLProto;
import me.onemobile.cache.CacheFile;
import me.onemobile.cache.DataCacheUtil;
import me.onemobile.rest.client.RESTClient;
import me.onemobile.rest.client.Response;

/**
 * Created by Mr_ZY on 16/10/28.
 */

public class ICLService extends AbstractService<ICLProto.ICL> {
    private final static int EXPIRE = 24 * 60 * 60 * 1000;
    private final static String URL_1 = "http://api.freereadbible.com/book/images?page=%s";

    public ICLService(Context ctx) {
        super(ctx, "IC/ICL");
    }

    @Override
    protected ICLProto.ICL getFromCache(CacheFile cacheFile, String s, String... strings) {
        return cacheFile.read(ICLProto.ICL.class);
    }

    @Override
    protected Response request(String etag, String uriPath, String... params) {
        String url = String.format(URL_1, params[0]);
        RESTClient client = RESTClient.create(url);
        client.setTimeoutConnection(5 * 1000);
        client.setTimeoutSocket(5 * 1000);

        Response response = client.get();
        return response;
    }

    @Override
    protected ICLProto.ICL parseResponse(Response response, String uriPath, String... params) {
        ICLProto.ICL icl = new ICLProto.ICL();
        JSONObject root = (JSONObject) response.getEntity();
        if (root == null)
            return null;
        try {
            JSONObject data = root.optJSONObject("data");
            int totalPage = data.optInt("totalPage");
            int totalCount = data.optInt("totalCount");
            icl.setToalCount(totalCount);
            icl.setToalPage(totalPage);
            JSONArray lists = data.optJSONArray("imageList");
            for (int i = 0; i < lists.length(); i++) {
                ICLProto.ICL.ICI ici = new ICLProto.ICL.ICI();
                JSONObject ob = (JSONObject) lists.get(i);
                String image = ob.optString("image");
                String thumbnail = ob.optString("thumbnail");
                ici.setImage(image);
                ici.setThumbnail(thumbnail);
                icl.addImageList(ici);
            }

        } catch (Exception e) {

        }
        if (icl.getImageListCount() > 0) {
            DataCacheUtil.backup("temp", EXPIRE, icl, uriPath, params);

        }
        return icl;
    }

}
