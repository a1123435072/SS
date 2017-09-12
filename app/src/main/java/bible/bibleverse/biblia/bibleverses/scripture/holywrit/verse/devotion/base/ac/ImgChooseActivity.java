package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventExitImageChoosePage;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr.ImgChooseFragment;
import de.greenrobot.event.EventBus;
import me.iwf.photopicker.PhotoPicker;
import yuku.afw.V;

/**
 * Created by Mr_ZY on 16/11/4.
 */

public class ImgChooseActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        initToolbar();
        EventBus.getDefault().register(this);

        openFragment(ImgChooseFragment.class, null, ImgChooseFragment.class.getName(), false, bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity.AnimType.DISABLE);
    }

    private void initToolbar() {
        Toolbar toolbar = V.get(this, R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.images);
            toolbar.setNavigationIcon(R.drawable.ic_back_black);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PhotoPicker.REQUEST_CODE) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_frame);
            fragment.onActivityResult(requestCode, requestCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static Intent createIntent(Context context, int ari, int count) {
        Intent intent = new Intent();
        intent.setClass(context, ImgChooseActivity.class);
        intent.putExtra("ari", ari);
        intent.putExtra("count", count);
        return intent;
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onEventMainThread(EventExitImageChoosePage event) {
        if (event == null) {
            return;
        }
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(ImgChooseFragment.class.getName());
        boolean res = ((ImgChooseFragment) fragment).onKeyPress(keyCode, event);
        if (res) {
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

}
