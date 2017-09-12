package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.NewMainActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.storage.Prefkey;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.ChangeConfigurationHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import yuku.afw.storage.Preferences;

import static bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.Literals.Array;

public abstract class BaseActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    public static final String TAG = BaseActivity.class.getSimpleName();

    private static final int REQCODE_PERMISSION_storage = 1;
    private static final int REQCODE_permissionSettings = 9970;

    private boolean willNeedStoragePermission;

    private int lastKnownConfigurationSerialNumber;

    @Override
    protected void onStart() {
        super.onStart();

        applyActionBarAndStatusBarColors();

        final int currentConfigurationSerialNumber = ChangeConfigurationHelper.getSerialCounter();
        if (lastKnownConfigurationSerialNumber != currentConfigurationSerialNumber) {
            Log.d(TAG, "Restarting activity " + getClass().getName() + " because of configuration change "
                    + lastKnownConfigurationSerialNumber + " -> " + currentConfigurationSerialNumber);
            lastKnownConfigurationSerialNumber = currentConfigurationSerialNumber;
            recreate();
        }
    }

    protected void applyActionBarAndStatusBarColors() {
        // action bar color and status bar color are set based on night mode
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            if (Preferences.getBoolean(Prefkey.is_night_mode, false)) {
                actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary_night_mode)));

                if (Build.VERSION.SDK_INT >= 21) {
                    getWindow().setStatusBarColor(0xff000000);
                }
            } else {
                final TypedValue tv = new TypedValue();
                getTheme().resolveAttribute(R.attr.colorPrimary, tv, true);
                actionBar.setBackgroundDrawable(new ColorDrawable(tv.data));

                if (Build.VERSION.SDK_INT >= 21) {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.primary_dark));
                }
            }
        }
    }

    /**
     * Call this from subclasses before super.onCreate() to make
     * the activity ask for storage permission and do not proceed
     * if the permission is not granted.
     */
    protected void willNeedStoragePermission() {
        this.willNeedStoragePermission = true;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lastKnownConfigurationSerialNumber = ChangeConfigurationHelper.getSerialCounter();

        if (willNeedStoragePermission) {
            askStoragePermission();
        }

    }

    private void askStoragePermission() {
        if (!(
                Build.VERSION.SDK_INT < 16
                        || (
                        ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                )
        )) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    ) {
                final AtomicBoolean oked = new AtomicBoolean(false);
                new MaterialDialog.Builder(this)
                        .content(R.string.storage_permission_rationale)
                        .positiveText(R.string.ok)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                oked.set(true);
                                ActivityCompat.requestPermissions(BaseActivity.this, Array(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), REQCODE_PERMISSION_storage);
                            }
                        })
                        .dismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {

                                if (!oked.get()) {
                                    finish();
                                }
                            }
                        })
                        .show();
            } else

            {
                ActivityCompat.requestPermissions(this, Array(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), REQCODE_PERMISSION_storage);
            }
        } else {
            onNeededPermissionsGranted(true);
        }
    }

    /**
     * Override this to do something after we confirm that all needed permissions are granted.
     * This is only called if {@link #willNeedStoragePermission()} was called.
     *
     * @param immediatelyGranted whether the permission is granted immediately without leaving the first onCreate().
     *                           Use this to determine whether we need to do initialization (e.g. load dir contents)
     *                           and to determine whether it is safe to init now.
     */
    protected void onNeededPermissionsGranted(boolean immediatelyGranted) {
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions,
                                           final int[] grantResults) {
        if (requestCode == REQCODE_PERMISSION_storage) {
            // all must be granted
            boolean allGranted = true;
            for (final int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                }
            }

            if (allGranted) {
                onNeededPermissionsGranted(false);
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        || !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    // user selects do not ask again
                    final AtomicBoolean oked = new AtomicBoolean(false);
                    new MaterialDialog.Builder(this)
                            .content("You need to have the Storage permission enabled to continue, because we need to store shared media such as Bible versions and fonts.")
                            .positiveText(R.string.ok)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    oked.set(true);
                                    startActivityForResult(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.fromParts("package", getPackageName(), null)), REQCODE_permissionSettings);
                                }
                            })
                            .negativeText(R.string.cancel)
                            .dismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    if (!oked.get()) {
                                        finish();
                                    }
                                }
                            })
                            .show();
                } else {
                    finish();
                }
            }

            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {
        if (requestCode == REQCODE_permissionSettings) {
            askStoragePermission();
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @CallSuper
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            navigateUp();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void navigateUp() {
        final Intent upIntent = NavUtils.getParentActivityIntent(this);
        if (upIntent == null) { // not defined in manifest, let us finish() instead.
            if (Build.VERSION.SDK_INT >= 21) {
                finishAfterTransition();
            } else {
                finish();
            }
            return;
        }

        if (NavUtils.shouldUpRecreateTask(this, upIntent) || isTaskRoot()) {
            TaskStackBuilder.create(this)
                    .addNextIntentWithParentStack(upIntent)
                    .startActivities();
        } else {
            NavUtils.navigateUpTo(this, upIntent);
        }
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        { // reconfigure toolbar height (Need to have a toolbar with id toolbar)
            final View v = findViewById(R.id.toolbar);
            if (v instanceof Toolbar) {
                final Toolbar toolbar = (Toolbar) v;
                final ViewGroup.LayoutParams lp = toolbar.getLayoutParams();
                final TypedValue tv = new TypedValue();
                getTheme().resolveAttribute(R.attr.actionBarSize, tv, true);
                final int h = (int) tv.getDimension(getResources().getDisplayMetrics());
                lp.height = h;
                toolbar.setLayoutParams(lp);
                // Workaround for https://code.google.com/p/android/issues/detail?id=79813
                toolbar.setMinimumHeight(h);
            }
        }
    }

    /**
     * Open a fragment
     *
     * @param clazz          Fragment class
     * @param args           Bundle as the Fragment args
     * @param tag            Tag name for FragmentManager
     * @param addToBackStack Add to back stack or not
     * @param animType       {@link AnimType}
     */
    public void openFragment(Class<? extends Fragment> clazz, Bundle args, String tag, boolean addToBackStack, AnimType animType) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment == null || fragment.isRemoving()) {
            try {
                fragment = Fragment.instantiate(this, clazz.getName(), args);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        openFragment(fragment, tag, addToBackStack, animType);
    }

    /**
     * The animations of replacing fragment.
     */
    public enum AnimType {
        DISABLE, FADE, ZOOM
    }

    /**
     * Open a fragment
     *
     * @param fragment
     * @param tag            Tag name for FragmentManager
     * @param addToBackStack Add to back stack or not
     * @param animType       {@link AnimType}
     */
    public void openFragment(Fragment fragment, String tag, boolean addToBackStack, AnimType animType) {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (addToBackStack) {
                ft.addToBackStack("F#" + System.currentTimeMillis());
            }

            List<Fragment> fragmentList = fragmentManager.getFragments();
            if (fragmentList != null && fragmentList.size() > 0) {
                switch (animType) {
                    case DISABLE:
                        break;
                    case FADE:
                        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    case ZOOM:
//                        ft.setCustomAnimations(R.anim.zoom_open_enter, R.anim.zoom_open_exit, R.anim.zoom_close_enter, R.anim.zoom_close_exit);
                        break;
                }
            }
            ft.replace(R.id.fragment_frame, fragment, tag);
            ft.commit();
        } catch (RuntimeException e) {
            // Easy crash, cause by checkStateLoss
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            boolean isConsumed = onBack();
            if (isConsumed) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onBack() {
        FragmentManager fm = this.getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStackImmediate();
            return true;
        } else {
            if (!(this instanceof NewMainActivity) && this.isTaskRoot()) {
                Utility.goHome(this);
            } else {
                if (Build.VERSION.SDK_INT >= 21) {
                    this.finishAfterTransition();
                } else {
                    this.finish();
                }
            }
        }
        return false;
    }


}
