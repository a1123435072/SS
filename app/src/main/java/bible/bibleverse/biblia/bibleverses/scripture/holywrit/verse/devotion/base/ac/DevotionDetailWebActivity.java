package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.BaseRetrofit;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.DevotionService;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.DevotionBean;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.DevotionListResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.EmptyResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PopularResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventRefreshFavoriteList;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.storage.Db;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yuku.afw.V;

/**
 * Created by yzq on 2017/3/3.
 */

public class DevotionDetailWebActivity extends BaseActivity {


    RelativeLayout mRootLayout;
    LinearLayout mMainLayout;
    private TextView mTitle;
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private LinearLayout mEmptyLayout;

    private LinearLayout mBottomBannerAdLayout;
    AdView mBottomBannerAdView;

    private DevotionBean mDevotionBean;
    private int mDevotionId;
    private String mDevotionTitle;
    private String mDevotionUrl, mDevotionShareUrl;
    private String mDevotionDate;
    private String mDevotionSite;
    private int mNotificationType;

    boolean mIsInFavorite = false;
    private MenuItem mFavBtn;

    public static Intent createIntent(Context context, DevotionBean listsBean) {
        Intent intent = new Intent(context, DevotionDetailWebActivity.class);
        intent.putExtra(Constants.DEVOTION_BEAN, listsBean);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Let's display the progress in the activity title bar, like the
        // browser app does.
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        setContentView(R.layout.activity_devotion_detail_web);

        handleIntent(getIntent());

        initView();

        initToolbar();

        findViewById(R.id.ad_banner_layout).setVisibility(View.GONE);

        mWebView.loadUrl(mDevotionUrl);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);

        mTitle.setText(mDevotionTitle);
        mTitle.setSelected(true);
        mWebView.loadUrl(mDevotionUrl);
    }

    private void handleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        mDevotionBean = intent.getParcelableExtra(Constants.DEVOTION_BEAN);
        if (mDevotionBean != null) {
            mDevotionId = mDevotionBean.getId();
            mDevotionTitle = mDevotionBean.getTitle();
            mDevotionUrl = mDevotionBean.getLinkUrl();
            mDevotionDate = mDevotionBean.getDate();
            mDevotionSite = mDevotionBean.getSource();
            mDevotionShareUrl = mDevotionBean.getShareLink();
        }
        setDevotionViewed(mDevotionId);
        mNotificationType = intent.getIntExtra(Constants.KEY_NOTIFICATION_TYPE, 0);
    }

    private void initToolbar() {
        final Toolbar toolbar = V.get(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_back_black);
        ab.setTitle(null);
    }

    private void initView() {

        mRootLayout = V.get(this, R.id.root_layout);
        mMainLayout = V.get(this, R.id.main_layout);
        mTitle = V.get(this, R.id.my_title);
        mTitle.setText(mDevotionTitle);
        mTitle.setSelected(true);
        V.get(this, R.id.loading_layout).setVisibility(View.GONE);
        mProgressBar = V.get(this, R.id.loading_progress);


        mEmptyLayout = V.get(this, R.id.empty_layout);
        mEmptyLayout.setVisibility(View.GONE);
        ((ImageView) V.get(this, R.id.empty_icon)).setImageResource(R.drawable.icon_no_connection);
        ((TextView) V.get(this, R.id.tEmpty)).setText(getString(R.string.no_connection));
        ((TextView) V.get(this, R.id.msgEmpty)).setText(getString(R.string.empty_verse_of_the_day));
        V.get(this, R.id.bRetry).setVisibility(View.GONE);
        mEmptyLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        ((TextView) V.get(this, R.id.tEmpty)).setTextColor(ContextCompat.getColor(this, R.color.black));

        initWebView();
    }

    private void initWebView() {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWebView = new WebView(getApplicationContext());
        mWebView.setLayoutParams(params);
        mMainLayout.addView(mWebView);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                mEmptyLayout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                mEmptyLayout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
//                mEmptyLayout.setVisibility(View.VISIBLE);
//                mProgressBar.setVisibility(View.GONE);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    mProgressBar.setProgress(newProgress);
                } else {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    private void setDevotionViewed(int id) {
        DevotionService service = BaseRetrofit.getDevotionService();
        service.setDevotionViewed(String.valueOf(id)).enqueue(new Callback<EmptyResponse>() {
            @Override
            public void onResponse(Call<EmptyResponse> call, Response<EmptyResponse> response) {
            }

            @Override
            public void onFailure(Call<EmptyResponse> call, Throwable t) {

            }
        });
        long idInDb = S.getDb().addNewDevotionReadHistory(id, mDevotionDate, mDevotionSite);
        if (idInDb != 0) {
            S.getDb().addTodayReadDevotionCount(1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);

            mWebView.clearHistory();
            mMainLayout.removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }

        if (mBottomBannerAdView != null) {
            mBottomBannerAdView.destroy();
        }
        EventBus.getDefault().post(new EventRefreshFavoriteList(Db.Favorite.TYPE_DEVOTION));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_devotion_detail, menu);
        mFavBtn = menu.findItem(R.id.fav_button);
        mIsInFavorite = S.getDb().isInFavorite(Db.Favorite.TYPE_DEVOTION, mDevotionId);
        mFavBtn.setIcon(mIsInFavorite ? R.drawable.ic_fav_select : R.drawable.ic_fav_unselect);
        mFavBtn.setTitle(mIsInFavorite ? R.string.remove_favorites : R.string.add_to_favorites);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fav_button:
                doFavAction();
                break;
            case R.id.share_button:
                doShareAction();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doFavAction() {
        if (mDevotionBean == null) {
            return;
        }
        if (mIsInFavorite) {
            long id = S.getDb().removeFavoriteData(Db.Favorite.TYPE_DEVOTION, mDevotionId);
            if (id > 0) {
                mFavBtn.setIcon(R.drawable.ic_fav_unselect);
                mFavBtn.setTitle(R.string.add_to_favorites);
                Toast.makeText(this, R.string.removed_favorites, Toast.LENGTH_SHORT).show();
                mIsInFavorite = false;
            }
        } else {
            long id = S.getDb().addFavoriteDevotion(mDevotionBean);
            if (id > 0) {
                mFavBtn.setIcon(R.drawable.ic_fav_select);
                mFavBtn.setTitle(R.string.remove_favorites);
                Toast.makeText(this, R.string.added_to_favorites, Toast.LENGTH_SHORT).show();
                mIsInFavorite = true;
            }
        }
    }

    private void doShareAction() {
        Utility.shareTextBySystem(this, getShareText());
    }

    private String getShareText() {
        String str = "";
        if (!TextUtils.isEmpty(mDevotionShareUrl)) {
            str = getString(R.string.devotion_share_text, mDevotionTitle, mDevotionShareUrl, Constants.GOOGLE_SHORT_URL);
        } else if (!TextUtils.isEmpty(mDevotionUrl)) {
            str = Html.fromHtml(mDevotionUrl) + "\n\n" + getString(R.string.share_msg) + Constants.GOOGLE_SHORT_URL;
        } else {
            str = getString(R.string.share_msg) + Constants.GOOGLE_SHORT_URL;
        }
        return str;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }


}
