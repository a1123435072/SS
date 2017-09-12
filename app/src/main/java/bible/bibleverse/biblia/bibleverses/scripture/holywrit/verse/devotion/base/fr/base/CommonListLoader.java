package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

public abstract class CommonListLoader<T> extends AsyncTaskLoader<List<T>> {

    protected List<T> mData;

    protected Bundle mArgs;

    public abstract List<T> getListData();

    public CommonListLoader(Context context, Bundle args) {
        super(context);
        this.mArgs = args;
    }

    @Override
    public List<T> loadInBackground() {
        return getListData();
    }

    @Override
    public void deliverResult(List<T> apps) {
        if (isReset()) {
            if (apps != null) {
                onReleaseResources(apps);
            }
        }
        List<T> oldApps = apps;
        mData = apps;

        if (isStarted()) {
            super.deliverResult(apps);
        }

        if (oldApps != null) {
            onReleaseResources(oldApps);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        if (mData != null) {
            deliverResult(mData);
        }

        if (takeContentChanged() || mData == null) {
            forceLoad();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mData != null) {
            onReleaseResources(mData);
            mData = null;
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(List<T> apps) {
        super.onCanceled(apps);
        onReleaseResources(apps);
    }

    protected void onReleaseResources(List<T> apps) {
        // For a simple List<> there is nothing to do. For something
        // like a Cursor, we would close it here.
    }

    public int remove(T obj) {
        if (mData != null) {
            mData.remove(obj);
        }
        return mData.hashCode();
    }

    /**
     * Get loader's arguments.
     *
     * @return Bundle
     */
    public Bundle getArgs() {
        return this.mArgs;
    }

}
