package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.view.GridViewWithHeaderAndFooter;


public abstract class BaseGridAdapter<T> extends ArrayAdapter<T> implements OnScrollListener, LoaderCallbacks<List<T>> {

    public static final String EXTRA_IS_REFRESH = "is_refresh";

    protected Activity context;

    private BaseGridWithHeaderAndFooterFragment.LoadResultListenerImpl loadResultListener;

    protected int currentPage = 1;
    protected int lastCurrentPage = 1;
    protected boolean currentPageLoaded = false;

    /**
     * Get list pages count.
     *
     * @return Int
     */
    public abstract int getListPagesCount();

    /**
     * Initial loader for page to request data.
     *
     * @param page page number
     */
    public abstract void initLoad(int page);

    /**
     * Restart loader.
     *
     * @param page page number
     * @param arg  arguments
     */
    public abstract void restartLoad(int page, Bundle arg);

    public abstract void hideFooterView();

    public BaseGridAdapter(Activity activity, BaseGridWithHeaderAndFooterFragment.LoadResultListenerImpl listener) {
        super(activity, 0);
        this.loadResultListener = listener;
        this.context = activity;
    }

    public ScrollUpAndDownListener scrollUpAndDownListener;

    public void setScrollUpAndDownListener(ScrollUpAndDownListener scrollUpAndDownListener) {
        this.scrollUpAndDownListener = scrollUpAndDownListener;
    }

    public interface ScrollUpAndDownListener {
        void onScrollUpAndDown(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        GridViewWithHeaderAndFooter gridViewWithHeaderAndFooter = (GridViewWithHeaderAndFooter) view;
        int headersCount = gridViewWithHeaderAndFooter.getHeaderViewCount();
        int footersCount = gridViewWithHeaderAndFooter.getFooterViewCount();
        if (currentPage > lastCurrentPage) {
            currentPageLoaded = false;
        }
        if ((getListPagesCount() == 0 || currentPage <= getListPagesCount()) &&
                (((firstVisibleItem + visibleItemCount) == totalItemCount - headersCount - footersCount)
                        || (firstVisibleItem + visibleItemCount) == totalItemCount) && !currentPageLoaded) {
            initLoad(currentPage);
            lastCurrentPage = currentPage;
            currentPageLoaded = true;
        }
        if (currentPage > getListPagesCount()) {
            hideFooterView();
        }
        if (this.scrollUpAndDownListener != null) {
            this.scrollUpAndDownListener.onScrollUpAndDown(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    private void updatePageAndUI(List<T> latn) {
        loadResultListener.onUpdateUI(0);
        if (latn != null && latn.size() > 0 && getListPagesCount() > 0) {
            if (currentPage < getListPagesCount()) {
                // continue
                loadResultListener.continueLoad(0);
                currentPage++;
            } else if (currentPage >= getListPagesCount()) {
                // over
                loadResultListener.finishLoad(0);
                currentPage++;
            }
        } else {
            if (currentPage == 1) {
                // empty
                loadResultListener.failedLoadAtStart(0);
            } else {
                // next page error
                loadResultListener.failedLoadNextPage(0);
            }
        }
    }

    public void reload() {
        restartLoad(currentPage, null);
    }

    public void clearLoadTasks() {
    }

    Set<Integer> set = new HashSet<Integer>();

    @Override
    public void onLoadFinished(Loader<List<T>> loader, List<T> data) {
        CommonListLoader commonListLoader = (CommonListLoader) loader;
        Bundle args = commonListLoader.getArgs();
        boolean isRefresh = false;
        if (args != null) {
            isRefresh = args.getBoolean(EXTRA_IS_REFRESH);
        }

        int currentResultHashCode = (data == null || data.size() == 0) ? -1 : data.hashCode();
        if (!isRefresh && currentPage > getListPagesCount()) {
            updatePageAndUI(data);
        } else if (!set.contains(currentResultHashCode)) {
            if (data != null && data.size() > 0) {
                if (isRefresh) {
                    onRefreshList(true);
                }
                for (T lb : data) {
                    this.add(lb);
                }
                set.add(currentResultHashCode);
            } else if (isRefresh) {
                onRefreshList(false);
            }
            updatePageAndUI(data);
        } else if (set.contains(currentResultHashCode)) {
            updatePageAndUI(data);
        }

        notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<T>> arg0) {

    }

    public void setLoadResultListener(BaseGridWithHeaderAndFooterFragment.LoadResultListenerImpl listener) {
        this.loadResultListener = listener;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    @Override
    public void clear() {
        super.clear();
        this.currentPage = 1;
        this.set.clear();
    }

    public void reset() {
        clear();
        reload();
    }

    public void refresh(List<T> list) {
        this.currentPage = 2;
        set.add(list.hashCode());
        addAll(list);
    }

    /**
     * Invoked when loader is finished that the list is needed to be refreshed.
     */
    protected void onRefreshList(boolean isSuccess) {
        if (loadResultListener != null) {
//            loadResultListener.onRefresh(isSuccess);
        }
        if (isSuccess) {
            // Clear the list.
            clear();
        }
    }

}
