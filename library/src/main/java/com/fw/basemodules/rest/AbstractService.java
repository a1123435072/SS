package com.fw.basemodules.rest;

import android.content.Context;
import android.util.Log;

import com.fw.basemodules.BaseConfig;
import com.google.protobuf.micro.MessageMicro;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.onemobile.cache.CacheFile;
import me.onemobile.cache.DataCacheManagement;
import me.onemobile.cache.DataCacheUtil;
import me.onemobile.rest.client.Response;

public abstract class AbstractService<D> {

    public static final String LOG_TAG = "AbstractService";

    public static final int ERROR_CLIENT_NETWORK = -100;
    public static final int ERROR_CLIENT_IO = -200;
    public static final int ERROR_CLIENT_UNKNOWN = -300;

    public static final int STATUS_SUCCESS = 200;
    public static final int STATUS_NOT_MODIFIED = 304;
    public static final int STATUS_BAD_REQUEST = 400;
    public static final int STATUS_FORBIDDEN = 403;
    public static final int STATUS_NOT_FOUND = 404;

    protected CustomRESTClient restClient;

    protected String wholePath;

    protected int status;

    protected Context context;

    private ExecutorService executorService;
    private RequestListener requestListener;
    private RequestTask requestTask;

    public AbstractService(Context ctx, String wholePath) {
        this.wholePath = wholePath;
        if (ctx != null) {
            this.context = ctx.getApplicationContext();
            DataCacheManagement.setup(ctx, null, null);
        }
    }

    /**
     * Call it when remote request is needed.
     */
    public CustomRESTClient newRESTClient(String etag) {
        String baseUrl = BaseConfig.API_SDA_URL;
        return newRESTClient(baseUrl, etag);
    }

    /**
     * Call it when remote request is needed.
     */
    public CustomRESTClient newRESTClient(String baseUri, String etag) {
        restClient = CustomRESTClient.create(context, baseUri);
        if (etag != null && etag.length() > 0) {
            restClient.match(etag, true);
        }
        return restClient;
    }

    /**
     * Close the API service & RESTClient.
     */
    public void close() {
        if (restClient != null) {
            restClient.close();
        }

        if (requestTask != null && executorService != null) {
            executorService.shutdown();
        }
    }

    /**
     * Try to restore cache first, if no cache then request.
     *
     * @param params
     */
    public D getData(String... params) {
        D obj = null;
        CacheFile cacheFile = getCacheFile(wholePath, params);
        if (cacheFile != null) {
            if (cacheFile.isTimeout()) {
                Response response = request(cacheFile.getEtag(), wholePath, params);
                if (handleResponse(response)) {
                    obj = parseResponse(response, wholePath, params);
                } else {
                    obj = getFromCache(cacheFile, wholePath, params);
                    if (response.getHttpStatus() == 304) {
                        cacheFile.refresh((MessageMicro) obj, response.getExpiresTime() == 0 ? DataCacheManagement.TIMEOUT : response.getExpiresTime());
                    }
                }
            } else {
                obj = getFromCache(cacheFile, wholePath, params);
                if (obj == null) {// error on fetch the cache file.
                    Response response = request(null, wholePath, params);
                    if (handleResponse(response)) {
                        obj = parseResponse(response, wholePath, params);
                    }
                }
            }
        } else {
            Response response = request(null, wholePath, params);
            if (handleResponse(response)) {
                obj = parseResponse(response, wholePath, params);
            }
        }

        return obj;
    }

    /**
     * Request data from server.
     *
     * @param params
     * @return
     */
    public D requestData(String... params) {
        Response response = request(null, wholePath, params);
        if (handleResponse(response)) {
            return parseResponse(response, wholePath, params);
        }
        return null;
    }

    /**
     * Restore cache first if cache exists, otherwise request. If cache is
     * timeout, a new thread will start to request data, and call back on
     * finish.
     */
    public D fetchData(boolean refreshInBackground, String... params) {
        D obj = null;
        CacheFile cacheFile = getCacheFile(wholePath, params);
        if (cacheFile != null) {
            obj = getFromCache(cacheFile, wholePath, params);

            // error on restore from the cache file.
            if (obj == null) {
                Response response = request(null, wholePath, params);
                if (handleResponse(response)) {
                    obj = parseResponse(response, wholePath, params);
                    return obj;
                }
            }

            if (cacheFile.isTimeout()) {
                // if cache is timeout, run a task to get new data.
                // the task will call back on finish.

                if (refreshInBackground) {
                    Log.i(LOG_TAG, "Refresh in background...");
                    if (executorService == null) {
                        executorService = Executors.newCachedThreadPool();
                    }
                    requestTask = new RequestTask(cacheFile, wholePath, params);
                    executorService.execute(requestTask);

                } else {
                    Response response = request(cacheFile.getEtag(), wholePath, params);
                    if (handleResponse(response)) {
                        obj = parseResponse(response, wholePath, params);
                    } else {
                        if (response.getHttpStatus() == 304) {
                            cacheFile.refresh((MessageMicro) obj, response.getExpiresTime() == 0 ? DataCacheManagement.TIMEOUT : response.getExpiresTime());
                        }
                    }
                }
            }
        } else {
            Response response = request(null, wholePath, params);
            if (handleResponse(response)) {
                obj = parseResponse(response, wholePath, params);
            }
        }

        return obj;
    }

    /**
     * The task to request new data when cache is timeout.
     */
    private class RequestTask implements Runnable {
        private CacheFile cacheFile;
        private String uriPath;
        private String[] params;

        public RequestTask(CacheFile cacheFile, String uriPath, String[] params) {
            this.cacheFile = cacheFile;
            this.uriPath = uriPath;
            this.params = params;
        }

        @Override
        public void run() {
            Response response = request(cacheFile.getEtag(), uriPath, params);
            D obj = null;
            if (handleResponse(response)) {
                obj = parseResponse(response, uriPath, params);
                if (requestListener != null && obj != null) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    requestListener.onFinish(obj);
                    Log.i(LOG_TAG, "Refresh in background - Successfully!");
                }

                if (obj == null) {
                    Log.i(LOG_TAG, "Refresh in background - Error on parse response!");
                }

            } else if (response.getHttpStatus() == 304) {
                Log.i(LOG_TAG, "Refresh in background - No updates!");
                cacheFile.refresh((MessageMicro) obj, response.getExpiresTime() == 0 ? DataCacheManagement.TIMEOUT : response.getExpiresTime());
            }
        }
    }

    /**
     * Listener for the request task call back.
     */
    public interface RequestListener {
        void onFinish(Object obj);
    }

    public void setRequestListener(RequestListener requestListener) {
        this.requestListener = requestListener;
    }

    /**
     * Custom method to return a CacheFile.
     */
    protected CacheFile getCacheFile(String uriPath, String... params) {
        return DataCacheUtil.getCacheFile(wholePath, params);
    }

    /**
     * A method to restore cache if cache exists.
     */
    protected abstract D getFromCache(CacheFile cacheFile, String uriPath, String... params);

    /**
     * If cache is not exist, this method will be invoked to request server.
     */
    protected abstract Response request(String etag, String uriPath, String... params);

    /**
     * Get entity from Response, and parse it to target.
     */
    protected abstract D parseResponse(Response response, String uriPath, String... params);

    /**
     * Handle the response.
     *
     * @param response
     * @return true if success.
     */
    protected boolean handleResponse(Response response) {
        if (response.getErrorCode() < 0 || response.getEntity() == null) {
            status = response.getErrorCode();
            return false;
        }
        status = response.getHttpStatus();
        return status == 200;
    }

    protected CustomRESTClient getRESTClient() {
        return this.restClient;
    }

    public int getStatus() {
        return this.status;
    }

    public static void backupCache(Response response, MessageMicro obj, String type, String... params) {
        if (response != null) {
            DataCacheUtil.backup(response.getETag(), response.getExpiresTime(), obj, type, params);
        }
    }

    /**
     * Parse the cache file to object and return.
     *
     * @param params
     * @return null if cache file do not exist.
     */
    public D getCache(String... params) {
        CacheFile cacheFile = getCacheFile(wholePath, params);
        if (cacheFile != null) {
            return getFromCache(cacheFile, wholePath, params);
        }
        return null;
    }

    public boolean refresh(String... params) {
        Response response = request("empty", wholePath, params);
        if (handleResponse(response)) {
            D obj = parseResponse(response, wholePath, params);
            CacheFile cacheFile = getCacheFile(wholePath, params);
            if (cacheFile != null) {
                cacheFile.refresh((MessageMicro) obj, response.getExpiresTime() == 0 ? DataCacheManagement.TIMEOUT : response.getExpiresTime());
            } else {
                backupCache(response, (MessageMicro) obj, wholePath, params);
            }
            return true;
        }

        return false;
    }

    /*
     * Clear the current cache file.
     *
     * @param pagesCount
     * @param params
     */
    public boolean clearCache(String... params) {
        boolean result = DataCacheUtil.deleteCacheFile(wholePath, params);
        Log.i(LOG_TAG, "delete cache file: " + result);
        return result;
    }
}
