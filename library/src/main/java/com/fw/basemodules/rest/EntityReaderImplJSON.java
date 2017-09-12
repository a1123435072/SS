package com.fw.basemodules.rest;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import org.apache.http.ParseException;
import me.onemobile.json.JSONException;
import me.onemobile.json.JSONObject;
import me.onemobile.rest.client.EntityReader;
import me.onemobile.rest.client.RESTClient;

public class EntityReaderImplJSON extends EntityReader<JSONObject> {

	@Override
	public JSONObject read(HttpEntity entity) {
		Header encodingHead = entity.getContentEncoding();
		if (encodingHead != null && encodingHead.getValue() != null && encodingHead.getValue().contains(ENCODING_GZIP)) {
			entity = new GzipDecompressingEntity(entity);
		}
		try {
			String result = EntityUtils.toString(entity, HTTP.UTF_8);
			if (RESTClient.DEBUG) {
				this.debug = result;
			}
			return new JSONObject(result);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) { //Not so good
			e.printStackTrace();
			System.runFinalization();
			System.exit(0);
			return null;
		}

		return null;
	}
}
