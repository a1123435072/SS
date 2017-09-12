package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import java.util.List;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;


public class ContainerActivity extends BaseActivity {

    public final static String CLASS = "class";
    public final static String BUNDLE = "bundle";
    public final static String TITLE = "title";

    private Toolbar topToolBar;

    private boolean isNewIntent = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        handleIntent(getIntent(), savedInstanceState);

        Intent intent = getIntent();
        String fragmentClazz = intent.getStringExtra(CLASS);
        if (!TextUtils.isEmpty(fragmentClazz)) {
            try {
                openFragment((Class<? extends Fragment>) Class.forName(fragmentClazz), null, fragmentClazz, false, AnimType.DISABLE);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        String title = intent.getStringExtra(TITLE);
        initToolBar(title);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onNavigateUp() {
        this.finish();
        return super.onNavigateUp();
    }

    public void initToolBar(String title) {
        topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        topToolBar.setNavigationIcon(R.drawable.ic_back_black);
        setTitle(title);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent, null);
    }

    /**
     * Handle intent input.
     *
     * @param intent
     * @param savedInstanceState
     */
    void handleIntent(Intent intent, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            return;
        }
        if (intent != null && (intent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK) == Intent.FLAG_ACTIVITY_NEW_TASK) {
            isNewIntent = true;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        boolean isEmpty = (fragments == null || fragments.size() == 0);
        if (intent != null && intent.getStringExtra(CLASS) != null) {
            String className = intent.getStringExtra(CLASS);
            Bundle args = intent.getBundleExtra(BUNDLE);
            Uri data = intent.getData();
            try {
                Class<? extends Fragment> clazz = (Class<? extends Fragment>) Class.forName(className);
                openFragment(clazz, args, clazz.getName(), false, AnimType.DISABLE);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void open(Context ctx, Class<? extends Fragment> fragment, String title) {
        Intent intent = new Intent(ctx, ContainerActivity.class);
        intent.putExtra(CLASS, fragment.getName());
        intent.putExtra(TITLE, title);
        ctx.startActivity(intent);
    }

}
