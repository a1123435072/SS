package com.fw.basemodules.rest;

import android.content.Context;

import com.fw.basemodules.BaseConfig;

import java.util.concurrent.ConcurrentHashMap;

import me.onemobile.rest.client.EntityReader;
import me.onemobile.rest.client.RESTClient;

public class CustomRESTClient extends RESTClient {

    @Override
    public String toString() {
        return super.toString();
    }

    public static CustomRESTClient create(Context ctx) {
        String baseUrl = BaseConfig.API_SDA_URL;
        return create(ctx, baseUrl);
    }

    public static CustomRESTClient create(Context ctx, String baseUri) {
        CustomRESTClient client = new CustomRESTClient(baseUri);
        CustomRESTClient.DEBUG = false;
        client.setRequestEntity(new RequestEntityImplJSON());
        client.setEntityReader(new EntityReaderImplJSON());
        client.type("application/json");
        client.accept("application/json");
        client.acceptEncoding(EntityReader.ENCODING_GZIP);

        return client;
    }

    protected CustomRESTClient(String baseUri) {
        super(baseUri);
    }
}
