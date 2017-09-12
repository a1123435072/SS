package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr.base;

import android.app.Activity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public abstract class BaseListAdapter<T> extends ArrayAdapter<T> implements OnScrollListener, LoaderCallbacks<List<T>> {

    private BaseListFragment.LoadResultListenerImpl loadResultListener;

    protected int currentPage = 0;

    // return list pages count
    public abstract int getAllListCount();

    public abstract void initLoad(int page);

    public abstract void restartLoad(int page);

    protected Activity context;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        ListView listView = (ListView) view;
        int headersCount = listView.getHeaderViewsCount();
        int footersCount = listView.getFooterViewsCount();
        if ((getAllListCount() == 0 || currentPage <= getAllListCount()) && ((firstVisibleItem + visibleItemCount) == (totalItemCount - headersCount - footersCount))) {
            initLoad(currentPage);
        }
    }

    private boolean isNeedLoad(int firstVisibleItem, int visibleItemCount, int totalItemCount, int headersCount, int footersCount) {
        if ((getAllListCount() == 0 || currentPage < getAllListCount()) && ((firstVisibleItem + visibleItemCount) <= (totalItemCount - headersCount - footersCount))) {
            if (visibleItemCount == 0 && totalItemCount > 0)
                currentPage = 0;
            return true;
        }
        if (currentPage == getAllListCount() && (firstVisibleItem + visibleItemCount) <= totalItemCount) {
            return true;
        }
        return false;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    public BaseListAdapter(Activity activity, BaseListFragment.LoadResultListenerImpl listener) {
        super(activity, 0);
        this.loadResultListener = listener;
        this.context = activity;
    }

    private void updatePageAndUI(List<T> latn) {
        loadResultListener.onUpdateUI(0);
        if (latn != null && latn.size() > 0 && getAllListCount() > 0) {
            if (currentPage < getAllListCount()) {
                // continue
                loadResultListener.continueLoad(0);
                currentPage++;
            } else if (currentPage >= getAllListCount()) {
                // over
                loadResultListener.finishLoad(0);
                currentPage++;
            }
        } else {
            if (currentPage == 1 || currentPage == 0) {
                // empty
                loadResultListener.failedLoadAtStart(0);
            } else {
                // next page error
                loadResultListener.failedLoadNextPage(0);
            }
        }
    }

    public void reload() {
        restartLoad(currentPage);
    }

    public void clearLoadTasks() {
    }

    Set<Integer> set = new HashSet<Integer>();

    @Override
    public void onLoadFinished(Loader<List<T>> arg0, List<T> arg1) {
        int currentResultHashCode = (arg1 == null || arg1.size() == 0) ? -1 : arg1.hashCode();
        if (currentPage >= getAllListCount()) {
            updatePageAndUI(arg1);
        } else if (!set.contains(currentResultHashCode)) {
            if (arg1 != null && arg1.size() > 0) {
                for (T lb : arg1) {
                    this.add(lb);
                }
                set.add(currentResultHashCode);
            }
            if (set.size() > 0) {
                updatePageAndUI(arg1);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<T>> arg0) {

    }

    public void setLoadResultListener(BaseListFragment.LoadResultListenerImpl listener) {
        this.loadResultListener = listener;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void reset() {
        clear();
        reload();
    }

    @Override
    public void clear() {
        super.clear();
        this.currentPage = 1;
        this.set.clear();
    }

    public void refresh(List<T> list) {
        reset();
        this.currentPage = 2;
//		this.objects = list;
        set.add(list.hashCode());
        addAll(list);
//        T t = list.get(0);
//        add(t);
//        remove(t);
    }
//
//	public List<T> getList() {
//		return this.objects;
//	}
}
