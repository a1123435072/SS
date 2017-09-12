package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr.base;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;


public abstract class BaseListFragment extends ListFragment {

    // Loading
    protected LinearLayout loadingLayout;
    protected LinearLayout reloadLayout;
    protected LinearLayout emptyLayout;
    protected TextView warningText;
    protected TextView emptyText;
    protected TextView btn_reload;

    // Footer
    public View mListFooterView = null;
    public TextView mListFooterText;
    public ProgressBar mListFooterProgress;
    public TextView mListFooterReloadBtn;
    protected View mListFooterReloadGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        initFooterView();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initLoadingView(getView());
    }

    protected boolean initLoadingView(View root) {
        if (root == null) {
            return false;
        }

//         Loading
        loadingLayout = (LinearLayout) root.findViewById(R.id.loading_layout);
        ProgressBar v = (ProgressBar) root.findViewById(R.id.pg);

        // Reload
//        reloadLayout = (LinearLayout) root.findViewById(R.id.reload_layout);
//        if (reloadLayout != null) {
//            warningText = (TextView) reloadLayout.findViewById(R.id.warning_text);
//            warningText.setText(reloadLayout.getResources().getString(R.string.Loading));
//            btn_reload = (TextView) reloadLayout.findViewById(R.id.btn_reload);
//            btn_reload.setOnClickListener(new OnClickListener() {
//                public void onClick(View arg0) {
//                    if (loadingLayout != null) {
//                        loadingLayout.setVisibility(View.VISIBLE);
//                    }
//                    if (reloadLayout != null) {
//                        reloadLayout.setVisibility(View.GONE);
//                    }
//                    onRetry();
//                }
//            });
//        }

//         Empty
        emptyLayout = (LinearLayout) root.findViewById(android.R.id.empty);
        if (emptyLayout != null) {
            emptyText = (TextView) emptyLayout.findViewById(R.id.tEmpty);
        }

        return true;
    }

    protected void processEmptyList(int code) {
        if (loadingLayout != null) {
            loadingLayout.setVisibility(View.GONE);
        }

        if (code == 308) {
            if (reloadLayout != null) {
                reloadLayout.setVisibility(View.GONE);
            }
            if (emptyLayout != null) {
                emptyLayout.setVisibility(View.VISIBLE);
            }
        } else {
            if (reloadLayout != null) {
                reloadLayout.setVisibility(View.VISIBLE);
            }
            if (emptyLayout != null) {
                emptyLayout.setVisibility(View.GONE);
            }
            if (warningText != null) {
                warningText.setText(getErrorHint());
            }
        }

        onProcessError(0);
    }

    public String getErrorHint() {
//        return getString(R.string.network_conn_error);
        return null;
    }

    public String defaultErrorHint(int statusCode) {
//        if (statusCode < 0) {
//            getString(R.string.network_conn_error);
//        } else if (statusCode > 200) {
//            return getString(R.string.network_response_timeout);
//        }
//        return getString(R.string.network_conn_error);
        return null;
    }

    /**
     * On click reload button.
     */
    protected abstract void onRetry();

    /**
     * On handle error.
     *
     * @param type 0:empty list , 1:request next page error
     */
    protected abstract void onProcessError(int type);

    protected void initFooterView() {
        if (!isAdded()) {
            return;
        }
        mListFooterView = getActivity().getLayoutInflater().inflate(R.layout.list_child_footer, null);
        mListFooterProgress = (ProgressBar) mListFooterView.findViewById(R.id.footer_progress);
//        if (mListFooterProgress != null && mListFooterProgress.getIndeterminateDrawable() != null) {
//            mListFooterProgress.getIndeterminateDrawable().setColorFilter(0xFFaaaaaa, android.graphics.PorterDuff.Mode.SRC_ATOP);
//        }
        mListFooterText = (TextView) mListFooterView.findViewById(R.id.footer_main_text);
        mListFooterProgress.setVisibility(View.VISIBLE);
//        mListFooterView.setId(R.layout.list_child_footer);
        mListFooterReloadBtn = (TextView) mListFooterView.findViewById(R.id.btn_reload);
        mListFooterReloadGroup = mListFooterView.findViewById(R.id.reload_group);
    }

    protected void processNextPageError() {
        if (mListFooterView == null) {
            return;
        }

        if (mListFooterReloadGroup.getVisibility() != View.VISIBLE) {
            mListFooterView.setVisibility(View.VISIBLE);
            mListFooterProgress.setVisibility(View.GONE);
            mListFooterReloadGroup.setVisibility(View.VISIBLE);

            mListFooterReloadBtn.setOnClickListener(new OnClickListener() {
                public void onClick(View paramView) {
                    mListFooterReloadGroup.setVisibility(View.GONE);
                    mListFooterProgress.setVisibility(View.VISIBLE);
                    mListFooterView.setOnClickListener(null);
                    onRetry();
                }
            });
        }

        onProcessError(1);
    }

    public class LoadResultListenerImpl implements LoadResultListener {

        @Override
        public void onUpdateUI(int code) {
            if (getView() == null) {
                return;
            }
        }

        @Override
        public void finishLoad(int code) {
            if (getView() == null) {
                return;
            }
            ListView listView = getListView();
            listView.setVisibility(View.VISIBLE);
            if ((listView != null) && mListFooterView != null && listView.getFooterViewsCount() > 0) {
                listView.removeFooterView(mListFooterView);
            }
        }

        @Override
        public void failedLoadNextPage(int code) {
            if (getView() == null) {
                return;
            }
            processNextPageError();
        }

        @Override
        public void failedLoadAtStart(int code) {
            if (getView() == null) {
                return;
            }
//             getListView().setVisibility(View.GONE);
            processEmptyList(code);
        }

        @Override
        public void continueLoad(int code) {
            if (getView() == null) {
                return;
            }
            getListView().setVisibility(View.VISIBLE);
            if (mListFooterView != null) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    if (getListView().getFooterViewsCount() == 0) {
                        getListView().addFooterView(mListFooterView);
                    }
                }
                mListFooterView.setVisibility(View.VISIBLE);
                mListFooterProgress.setVisibility(View.VISIBLE);
                mListFooterReloadGroup.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setListAdapter(null);

        // Loading
        reloadLayout = null;
        loadingLayout = null;
        warningText = null;
        btn_reload = null;

        // Footer
        mListFooterView = null;
        mListFooterText = null;
        mListFooterProgress = null;
        mListFooterReloadBtn = null;
    }

}
