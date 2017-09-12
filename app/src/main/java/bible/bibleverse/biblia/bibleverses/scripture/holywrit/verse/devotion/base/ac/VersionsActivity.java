package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.NewAnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.NewAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.SelfAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.App;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.U;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.config.VersionConfig;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventActiveVersionChanged;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.MVersion;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.MVersionDb;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.MVersionInternal;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.MVersionPreset;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.sv.VersionConfigUpdaterService;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.DownloadMapper;
import de.greenrobot.event.EventBus;
import yuku.afw.V;
import yuku.afw.storage.Preferences;
import yuku.afw.widget.EasyAdapter;

public class VersionsActivity extends BaseActivity {
    public static final String TAG = VersionsActivity.class.getSimpleName();

    private ListView mVersionsLv;
    private SwipeRefreshLayout mSwiper;
    private VersionAdapter mAdapter;
    private final int TYPE_NORMAL_ITEM = 0;
    private final int TYPE_DOWNLOADED_ITEM = 1;
    private final int TYPE_TITLE_ITEM = 2;
    private final int TYPE_LANGUAGE_SELECT_ITEM = 3;
    private final int TYPE_COUNT = 4;

    public static final String ACTION_RELOAD = TAG + ".verse.action.RELOAD";
    public static final String ACTION_UPDATE_REFRESHING_STATUS = TAG + ".verse.action.UPDATE_REFRESHING_STATUS";
    public static final String EXTRA_refreshing = "refreshing";

    private final String[] recommendVersions = new String[]{"New English Translation", "American Standard",
            "Bible in Basic English", "English Standard Version", "New King James Version", "American King James Version"};//only for en

    private Map<String, String> cache_displayLanguage = new HashMap<>();

    public static Intent createIntent() {
        return new Intent(App.context, VersionsActivity.class);
    }

    final BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (ACTION_RELOAD.equals(action)) {
                if (mAdapter != null) {
                    mAdapter.reload();
                }
            } else if (ACTION_UPDATE_REFRESHING_STATUS.equals(action)) {
                final boolean refreshing = intent.getBooleanExtra(EXTRA_refreshing, false);
                if (mSwiper != null) {
                    mSwiper.setRefreshing(refreshing);
                }
            }
        }
    };

    final SwipeRefreshLayout.OnRefreshListener swiper_refresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            VersionConfigUpdaterService.checkUpdate(false);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.willNeedStoragePermission();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_versions);

        initToolbar();

        initView();

        App.getLbm().registerReceiver(br, new IntentFilter(ACTION_RELOAD));
        App.getLbm().registerReceiver(br, new IntentFilter(ACTION_UPDATE_REFRESHING_STATUS));
        // try to auto-update version list
        VersionConfigUpdaterService.checkUpdate(true);
    }

    private void initToolbar() {
        setTitle(R.string.kelola_versi);

        final Toolbar toolbar = V.get(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black));
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_back_black);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.clear();
        getMenuInflater().inflate(R.menu.activity_versions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.versions_delete) {
            startActivity(new Intent(this, VersionsDownloadedActivity.class));
        }
        return false;
    }

    private void initView() {
        mAdapter = new VersionAdapter(this);

        mVersionsLv = V.get(this, R.id.lsVersions);
        mVersionsLv.setAdapter(mAdapter);

        mSwiper = V.get(this, R.id.swiper);
        if (mSwiper != null) { // Can be null, if the layout used is fragment_versions_downloaded.
            final int accentColor = ResourcesCompat.getColor(getResources(), R.color.accent, null);
            mSwiper.setColorSchemeColors(accentColor, 0xffcbcbcb);
            mSwiper.setOnRefreshListener(swiper_refresh);
            mSwiper.setEnabled(false);
            mSwiper.setRefreshing(false);
        }

    }

    public class VersionAdapter extends EasyAdapter {
        final List<Item> items = new ArrayList<>();
        Set<String> presetNamesInDb = new HashSet<>();
        List<Item> downloadedItems = new ArrayList<>();
        List<Item> downloadItems = new ArrayList<>();
        List<String> localeList = new ArrayList<>();
        List<String> recommendList = new ArrayList<>();
        Map<String, Item> recommendMap = new HashMap<>();
        private LayoutInflater mInflater;
        private Context mContext;
        private String mSelectedLanguage;

        public VersionAdapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(VersionsActivity.this);
            recommendList = Arrays.asList(recommendVersions);
            reload();
        }

        public void setSelectedLanguage(String locale) {
            mSelectedLanguage = locale;
            Preferences.setString(getString(R.string.pref_bible_language_key), locale);
            reload();
        }

        private void sortItemList(List<Item> itemList, final boolean isDownloaded) {
            Collections.sort(itemList, new Comparator<Item>() {
                        @Override
                        public int compare(Item a, Item b) {
                            if (isDownloaded) {
                                if (a != null && a.mv != null && a.mv.getVersionId().equalsIgnoreCase(S.activeVersionId)) {
                                    return -1;
                                }
                                if (b != null && b.mv != null && b.mv.getVersionId().equalsIgnoreCase(S.activeVersionId)) {
                                    return 1;
                                }
                            }
                            final String locale_a = a.mv.locale;
                            final String locale_b = b.mv.locale;
                            if (U.equals(locale_a, locale_b)) {
                                return a.mv.longName.compareToIgnoreCase(b.mv.longName);
                            }
                            if (locale_a == null) {
                                return +1;
                            } else if (locale_b == null) {
                                return -1;
                            }

                            return getDisplayLanguage(locale_a).compareToIgnoreCase(getDisplayLanguage(locale_b));
                        }
                    }
            );
        }

        private void reload() {
            items.clear();
            downloadedItems.clear();
            downloadItems.clear();
            localeList.clear();
            presetNamesInDb.clear();
            if (TextUtils.isEmpty(mSelectedLanguage)) {
                mSelectedLanguage = Preferences.getString(getString(R.string.pref_bible_language_key), "");
            }

            if (TextUtils.isEmpty(mSelectedLanguage)) {
                mSelectedLanguage = Locale.getDefault().getLanguage();
            }

            //add recent used item title
            items.add(new Item(getString(R.string.recently_used), TYPE_TITLE_ITEM, false));

            // add downloaded item
            for (MVersionDb mv : S.getDb().listAllVersions()) {
                downloadedItems.add(new Item(mv, TYPE_DOWNLOADED_ITEM, true));
                if (mv.preset_name != null) {
                    presetNamesInDb.add(mv.preset_name);
                    if (!localeList.contains(mv.locale)) {
                        localeList.add(mv.locale);
                    }
                }
            }
            //sort the downloaded item list
            sortItemList(downloadedItems, true);
            items.addAll(downloadedItems);

            //add language select item title
            items.add(new Item(getString(R.string.bible_language), TYPE_TITLE_ITEM, true));

            //add language select item
            items.add(new Item(getDisplayLanguage(mSelectedLanguage), TYPE_LANGUAGE_SELECT_ITEM, false));

            // add download item list
            final boolean showHidden = Preferences.getBoolean(getString(R.string.pref_showHiddenVersion_key), false);
            for (MVersionPreset preset : VersionConfig.get().presets) {
                Log.e("preset", preset.shortName);
                if (!showHidden && preset.hidden) {
                    continue;
                }
                if (!localeList.contains(preset.locale)) {
                    localeList.add(preset.locale);
                }
                if (preset.locale != null && !preset.locale.equalsIgnoreCase(mSelectedLanguage)) {
                    continue;
                }
                if (!TextUtils.isEmpty(mSelectedLanguage) && mSelectedLanguage.equalsIgnoreCase("en") && recommendList.contains(preset.longName)) {
                    recommendMap.put(preset.longName, new Item(preset, TYPE_NORMAL_ITEM, presetNamesInDb.contains(preset.preset_name) ? true : false));
                    continue;
                }
                downloadItems.add(new Item(preset, TYPE_NORMAL_ITEM, presetNamesInDb.contains(preset.preset_name) ? true : false));
            }
            //add download item title
            String title = getString(R.string.download_version_language, getDisplayLanguage(mSelectedLanguage), String.valueOf(downloadItems.size()));
            items.add(new Item(title, TYPE_TITLE_ITEM, true));

            //sort download item list
            sortItemList(downloadItems, false);
            //add recommend list
            if (recommendMap.size() > 0 && !TextUtils.isEmpty("en") && mSelectedLanguage.equalsIgnoreCase("en")) {
                /*for (String name : recommendList) {
                    items.add(recommendMap.get(name));
                }*/
                Iterator iter = recommendMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    items.add((Item)entry.getValue());
                }
            }

            //add download list
            items.addAll(downloadItems);

            //sort language List
            sortLanguageList(localeList);

            if (!localeList.contains(mSelectedLanguage)) {
                mSelectedLanguage = "en";
                reload();
            } else {
                notifyDataSetChanged();
            }
        }

        private void sortLanguageList(List<String> localeList) {
            if (localeList == null) {
                return;
            }
            Collections.sort(localeList, new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    if (!TextUtils.isEmpty(lhs) && lhs.equalsIgnoreCase(mSelectedLanguage)) {
                        return -1;
                    }
                    if (!TextUtils.isEmpty(rhs) && rhs.equalsIgnoreCase(mSelectedLanguage)) {
                        return 1;
                    }

                    if (TextUtils.isEmpty(lhs)) {
                        return 1;
                    } else if (TextUtils.isEmpty(rhs)) {
                        return -1;
                    }

                    return getDisplayLanguage(lhs).compareToIgnoreCase(getDisplayLanguage(rhs));
                }
            });
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Item getItem(int position) {
            return items.get(position);
        }

        @Override
        public int getItemViewType(int position) {
            Item item = getItem(position);
            if (item != null) {
                return item.mType;
            }
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_COUNT;
        }

        @Override
        public View newView(final int position, final ViewGroup parent) {
            View view = null;
            int type = getItemViewType(position);
            switch (type) {
                case TYPE_TITLE_ITEM:
                    view = mInflater.inflate(R.layout.item_version_title, parent, false);
                    break;
                case TYPE_NORMAL_ITEM:
                    view = mInflater.inflate(R.layout.item_version_download_item, parent, false);
                    break;
                case TYPE_DOWNLOADED_ITEM:
                    view = mInflater.inflate(R.layout.item_version_downloaded_item, parent, false);
                    break;
                case TYPE_LANGUAGE_SELECT_ITEM:
                    view = mInflater.inflate(R.layout.item_version_language_select_item, parent, false);
                    break;
                default:
                    view = new View(mContext);
                    break;
            }
            return view;
        }

        private void bindDownloadItemView(final View view, final Item item) {
            final View panelRight = V.get(view, R.id.panelRight);
            final ImageView downloadIv = V.get(view, R.id.download_btn);
            final ProgressBar progress = V.get(view, R.id.progress);
            final TextView bLongName = V.get(view, R.id.bLongName);
            final TextView bShortName = V.get(view, R.id.bShortName);
            panelRight.setVisibility(View.VISIBLE);
            bLongName.setText(item.mv.longName);
            bShortName.setText(item.mv.shortName);
            final MVersion mv = item.mv;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.mIsDownloaded) {
                        return;
                    }
                    if (!mv.getActive() && mv instanceof MVersionPreset) {
                        clickOnPresetVersion((MVersionPreset) mv);
                        App.getLbm().sendBroadcast(new Intent(ACTION_RELOAD));
                    }
                    //SelfAnalyticsHelper.sendBibileVersionsAnalytics(mContext, AnalyticsConstants.A_DOWNLOAD, mv != null ? mv.longName : "");
                    Bundle params = new Bundle();
                    params.putString("download_version", mv!= null ? mv.longName : "");
                    NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_VERSION_DOWNLOAD, params);
                }
            });
            downloadIv.setImageResource(item.mIsDownloaded ? R.drawable.ic_download_done : R.drawable.ic_download_btn);
            // downloading or not?
            final boolean downloading;
            if (mv instanceof MVersionInternal) {
                downloading = false;
            } else if (mv instanceof MVersionPreset) {
                final String downloadKey = "version:preset_name:" + ((MVersionPreset) mv).preset_name;
                final int status = DownloadMapper.instance.getStatus(downloadKey);
                downloading = (status == DownloadManager.STATUS_PENDING || status == DownloadManager.STATUS_RUNNING);
            } else if (mv instanceof MVersionDb && ((MVersionDb) mv).preset_name != null) { // probably downloading, in case of updating
                final String downloadKey = "version:preset_name:" + ((MVersionDb) mv).preset_name;
                final int status = DownloadMapper.instance.getStatus(downloadKey);
                downloading = (status == DownloadManager.STATUS_PENDING || status == DownloadManager.STATUS_RUNNING);
            } else {
                downloading = false;
            }
            if (downloading) {
                downloadIv.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
            } else {
                downloadIv.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }
            if (mv.getVersionId().equalsIgnoreCase(S.activeVersionId)) {
                bShortName.setTextColor(ContextCompat.getColor(mContext, R.color.accent));
            } else {
                bShortName.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            }
        }

        private void bindTitleView(View view, int position, Item item) {
            TextView titleTv = V.get(view, R.id.title);
            V.get(view, R.id.divider).setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            titleTv.setText(item.mTitle);
            view.setOnClickListener(null);
        }

        private boolean isActiveVersion(Item item) {
            if (item != null && item.mv != null && !TextUtils.isEmpty(item.mv.getVersionId())) {
                return item.mv.getVersionId().equalsIgnoreCase(S.activeVersionId);
            }
            return false;
        }

        private void bindDownloadedItemView(final View view, final int position, final Item item) {
            final CheckBox radioBtn = V.get(view, R.id.downloaded_radio_btn);
            final TextView bLongName = V.get(view, R.id.bLongName);
            final TextView bShortName = V.get(view, R.id.bShortName);
            radioBtn.setVisibility(View.VISIBLE);
            boolean isActive = isActiveVersion(item);
            radioBtn.setChecked(isActive);
            bLongName.setText(item.mv.longName);
            bShortName.setText(item.mv.shortName);
            if (isActive) {
                bShortName.setTextColor(ContextCompat.getColor(mContext, R.color.accent));
            } else {
                bShortName.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            }
            final MVersion mv = item.mv;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickOnDbVersion((MVersionDb) mv);
                    notifyDataSetChanged();
                    //AnalyticsHelper.getInstance(VersionsActivity.this).sendEvent(AnalyticsConstants.C_VERSIONS, action, label);
//                    SelfAnalyticsHelper.sendBibileVersionsAnalytics(VersionsActivity.this, AnalyticsConstants.A_USED, mv != null ? mv.longName : "");
                    finish();
                }
            });
            radioBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickOnDbVersion((MVersionDb) mv);
                    notifyDataSetChanged();
                    SelfAnalyticsHelper.sendBibileVersionsAnalytics(VersionsActivity.this, AnalyticsConstants.A_USED, mv != null ? mv.longName : "");
                    finish();
                }
            });
        }

        private void bindLanguageSelectView(View view, Item item) {
            final TextView bLongName = V.get(view, R.id.bLongName);
            final TextView changeLanguageBtn = V.get(view, R.id.change_language_btn);
            changeLanguageBtn.setVisibility(View.VISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLanguageSelectionDialog(localeList, mSelectedLanguage);
                }
            });
            bLongName.setText(item.mTitle);
        }

        @Override
        public void bindView(final View view, final int position, final ViewGroup parent) {
            final Item item = getItem(position);
            if(item == null){
                return;
            }
            if (item.mType == TYPE_TITLE_ITEM) {
                bindTitleView(view, position, item);
            } else if (item.mType == TYPE_DOWNLOADED_ITEM) {
                bindDownloadedItemView(view, position, item);
            } else if (item.mType == TYPE_LANGUAGE_SELECT_ITEM) {
                bindLanguageSelectView(view, item);
            } else if (item.mType == TYPE_NORMAL_ITEM) {
                bindDownloadItemView(view, item);
            }
        }

    }

    private List getLanguageList(List<String> localeList) {
        List<String> languageList = new ArrayList<>();
        for (String locale : localeList) {
            languageList.add(getDisplayLanguage(locale));
        }
        return languageList;
    }

    private int getSelectedLanguageIndex(List<String> localeList, String selectedLanguage) {
        if (localeList != null && !TextUtils.isEmpty(selectedLanguage)) {
            int index = localeList.indexOf(selectedLanguage);
            if (index < 0) {
                return 0;
            }
            return index;
        }
        return 0;
    }

    private void showLanguageSelectionDialog(final List<String> localeList, String selectedLanguage) {
        int selectedIndex = getSelectedLanguageIndex(localeList, selectedLanguage);
        int blackColor = ContextCompat.getColor(this, R.color.black_999);
        int whiteColor = ContextCompat.getColor(this, R.color.white);
        new MaterialDialog.Builder(this)
                .items(getLanguageList(localeList))
                .titleColor(ContextCompat.getColor(VersionsActivity.this, R.color.black))
                .contentColor(blackColor)
                .title(R.string.pref_bahasa_language)
                .itemsCallbackSingleChoice(selectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        if (which == -1) {
                            // it is possible that 'which' is -1 in the case that
                            // a version is already deleted, but the current displayed version is that version
                            // (hence the initial selected item position is -1) and then the user
                            // presses the "other version" button. This callback will still be triggered
                            // before the positive button callback.
                        } else {
                            mAdapter.setSelectedLanguage(localeList.get(which));
                            dialog.dismiss();
                        }
                        return true;
                    }
                })
                .alwaysCallSingleChoiceCallback()
                .show();
    }

    private void clickOnPresetVersion(final MVersionPreset mv) {
        if (mv.getActive()) {
            throw new RuntimeException("THIS SHOULD NOT HAPPEN: preset may not have the active checkbox checked.");
        }

        startDownload(mv);
    }

    private void startDownload(final MVersionPreset mv) {
        {
            int enabled = -1;
            try {
                enabled = App.context.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
            } catch (Exception e) {
                Log.d(TAG, "getting app enabled setting", e);
            }

            if (enabled == -1
                    || enabled == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    || enabled == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER) {
                new MaterialDialog.Builder(this)
                        .content(R.string.ed_download_manager_not_enabled_prompt)
                        .positiveText(R.string.ok)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            try {
                                                startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.android.providers.downloads")));
                                            } catch (ActivityNotFoundException e) {
                                                Log.e(TAG, "opening apps setting", e);
                                            }
                                        }
                                    }
                        ).negativeText(R.string.cancel)
                        .show();

                return;
            }
        }

        final String downloadKey = "version:preset_name:" + mv.preset_name;

        final int status = DownloadMapper.instance.getStatus(downloadKey);
        if (status == DownloadManager.STATUS_PENDING || status == DownloadManager.STATUS_RUNNING) {
            // it's downloading!
            return;
        }

        final DownloadManager.Request req = new DownloadManager.Request(Uri.parse(mv.download_url))
                .setTitle(mv.longName)
                .setVisibleInDownloadsUi(false)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

        final Map<String, String> attrs = new LinkedHashMap<>();
        attrs.put("download_type", "preset");
        attrs.put("preset_name", mv.preset_name);
        attrs.put("modifyTime", "" + mv.modifyTime);

        DownloadMapper.instance.enqueue(downloadKey, req, attrs);

        App.getLbm().sendBroadcast(new Intent(ACTION_RELOAD));
    }

    public String getDisplayLanguage(String locale) {
        if (TextUtils.isEmpty(locale)) {
            return "not specified";
        }

        String display = cache_displayLanguage.get(locale);
        if (display != null) {
            return display;
        }

        display = new Locale(locale).getDisplayLanguage();
        if (display == null || U.equals(display, locale)) {

            // try asking version config locale display
            display = VersionConfig.get().locale_display.get(locale);

            if (display == null) {
                display = locale; // can't be null now
            }
        }
        cache_displayLanguage.put(locale, display);

        return display;
    }

    static class Item {
        MVersion mv;
        boolean mIsHasDivider;
        boolean mIsDownloaded;
        int mType;//0:normal item 1: downloaded item  2.title item 3.language select item
        String mTitle;//for title item

        public Item(final MVersion mv, int type, boolean isDownloaded) {
            this.mv = mv;
            this.mType = type;
            this.mIsDownloaded = isDownloaded;
        }

        public Item(String title, int type, boolean isHasDivider) {
            this.mTitle = title;
            this.mType = type;
            this.mIsHasDivider = isHasDivider;
        }

    }

    private void clickOnDbVersion(final MVersionDb mv) {
        Bundle params = new Bundle();
        params.putString("selected_version", mv != null ? mv.longName : "");
        NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_VERSION_SELECTED, params);
        if (mv.hasDataFile()) {
            EventBus.getDefault().post(new EventActiveVersionChanged(mv));
        } else {
            new MaterialDialog.Builder(this)
                    .content(getString(R.string.the_file_for_this_version_is_no_longer_available_file, mv.filename))
                    .positiveText(R.string.delete)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            S.getDb().deleteVersion(mv);
                            App.getLbm().sendBroadcast(new Intent(ACTION_RELOAD));
                        }
                    })
                    .negativeText(R.string.no)
                    .show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getLbm().unregisterReceiver(br);
    }

}
