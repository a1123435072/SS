package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr.base;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fw.basemodules.view.RobotoTextView;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.view.GridViewWithHeaderAndFooter;


public abstract class BaseGridWithHeaderAndFooterFragment extends BaseFragment {

    // Loading
    protected LinearLayout loadingLayout;
    protected LinearLayout reloadLayout;
    protected TextView warningText;
    protected TextView btn_reload;

    // Footer
    public View mListFooterView = null;
    //    public LinearLayout mListFooterLayout;
    public TextView mListFooterText;
    public ProgressBar mListFooterProgress;
    public RobotoTextView mListFooterReloadBtn;
    protected View mListFooterReloadGroup;

    // Footer for bottom margin
    protected View mListFooterViewMarginBottom;


    protected abstract GridViewWithHeaderAndFooter getGridView();

    /**
     * Callback when list data is reset and request the new one.
     */
    protected void onListDataRefresh(boolean isSuccess) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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
//        getGridView().addFooterView(mListFooterView);
    }

    protected boolean initLoadingView(View root) {
        if (root == null) {
            return false;
        }

        // Loading
        loadingLayout = (LinearLayout) root.findViewById(R.id.loading_layout);

        // Reload
        reloadLayout = (LinearLayout) root.findViewById(R.id.reload_layout);
        if (reloadLayout != null) {
            warningText = (TextView) reloadLayout.findViewById(R.id.warning_text);
            warningText.setText(reloadLayout.getResources().getString(R.string.loading));
            btn_reload = (TextView) reloadLayout.findViewById(R.id.btn_reload);
            btn_reload.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            btn_reload.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    if (loadingLayout != null) {
                        loadingLayout.setVisibility(View.VISIBLE);
                    }
                    if (reloadLayout != null) {
                        reloadLayout.setVisibility(View.GONE);
                    }
                    onRetry();
                }
            });
        }

        return true;
    }

    protected void initFooterView() {
        mListFooterView = getLayoutInflater(null).inflate(R.layout.image_choose_list_footer, null);
        mListFooterProgress = (ProgressBar) mListFooterView.findViewById(R.id.footer_progress);
        mListFooterText = (TextView) mListFooterView.findViewById(R.id.footer_main_text);
        mListFooterText.setText(getString(R.string.no_connection) + ".");
        mListFooterProgress.setVisibility(View.VISIBLE);
        mListFooterReloadBtn = (RobotoTextView) mListFooterView.findViewById(R.id.btn_reload);
        mListFooterReloadGroup = mListFooterView.findViewById(R.id.reload_group);
    }

    protected void processNextPageError() {
        if (mListFooterView == null) {
            return;
        }

        if (mListFooterReloadGroup != null && mListFooterReloadGroup.getVisibility() != View.VISIBLE) {
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

    protected void processEmptyList() {
        getGridView().setVisibility(View.VISIBLE);
        if (loadingLayout != null) {
            loadingLayout.setVisibility(View.GONE);
        }

        if (reloadLayout != null) {
            reloadLayout.setVisibility(View.VISIBLE);
        }

        if (warningText != null) {
            warningText.setText(getErrorHint());
        }
        onProcessError(0);
    }

    public String getErrorHint() {
        return getString(R.string.no_connection) + ".";
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

    public class LoadResultListenerImpl implements LoadResultListener {
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
            GridViewWithHeaderAndFooter gridView = getGridView();
            gridView.setVisibility(View.VISIBLE);
            if (loadingLayout.getVisibility() == View.VISIBLE) {
                loadingLayout.setVisibility(View.GONE);
            }
            if (reloadLayout.getVisibility() == View.VISIBLE) {
                reloadLayout.setVisibility(View.GONE);
            }
            if ((gridView != null) && mListFooterView != null && gridView.getFooterViewCount() > 0) {
                mListFooterView.setVisibility(View.GONE);
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
            processEmptyList();
        }

        @Override
        public void continueLoad(int code) {
            if (getView() == null) {
                return;
            }
            getGridView().setVisibility(View.VISIBLE);
            if (mListFooterView != null) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
