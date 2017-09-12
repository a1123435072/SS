package com.fw.basemodules.rest;

import org.apache.http.HttpEntity;

import java.util.Set;

import me.onemobile.rest.client.MultivaluedMap;
import me.onemobile.rest.client.RequestEntity;
import me.onemobile.rest.client.UriBuilder;

public class RequestEntityImplParams extends RequestEntity {

    public RequestEntityImplParams() {
    }

    public HttpEntity get(UriBuilder uriBuilder, MultivaluedMap<String, String> headers) {
        Set<String> keys = uriBuilder.getEntity().keySet();
        UriBuilder uriBuilder2 = new UriBuilder(null);
        for (String key : keys) {
            uriBuilder2.addQuery(key, uriBuilder.getEntity().get(key));
        }
        return this.getStringEntity(uriBuilder2.assambleParams());
    }
}