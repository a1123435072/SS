//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.fw.basemodules.rest;

import org.apache.http.HttpEntity;

import me.onemobile.json.JSONObject;
import me.onemobile.rest.client.MultivaluedMap;
import me.onemobile.rest.client.RequestEntity;
import me.onemobile.rest.client.UriBuilder;

public class RequestEntityImplJSON extends RequestEntity {
    public RequestEntityImplJSON() {
    }

    public HttpEntity get(UriBuilder uriBuilder, MultivaluedMap<String, String> headers) {
        String entityStr = (new JSONObject(uriBuilder.getEntity())).toString();
        return this.getStringEntity(entityStr);
    }
}
