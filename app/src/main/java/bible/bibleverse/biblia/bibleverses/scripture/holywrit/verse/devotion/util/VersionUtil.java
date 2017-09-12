package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.App;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.U;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.config.VersionConfig;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.MVersion;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.MVersionDb;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.MVersionInternal;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.MVersionPreset;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.DownloadMapper;
import yuku.afw.storage.Preferences;

import static bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S.getDb;
import static yuku.afw.storage.Preferences.getString;

/**
 * Created by Mr_ZY on 16/11/9.
 */

public class VersionUtil {
    public static List<MVersion> getAllVersionsByLanguage(Locale locale) {
        List<MVersion> versionList = new ArrayList<>();
        final Set<String> presetNamesInDb = new HashSet<>();

        for (MVersionDb mv : getDb().listAllVersionsByLanguage(locale.getLanguage())) {
            versionList.add(mv);
            if (mv.preset_name != null) {
                presetNamesInDb.add(mv.preset_name);
            }
        }

        final boolean showHidden = Preferences.getBoolean(getString(R.string.pref_showHiddenVersion_key), false);
        for (MVersionPreset preset : VersionConfig.get().presets) {
            if (!showHidden && preset.hidden) continue;
            if (!preset.locale.equals(locale.getLanguage())) continue;
            if (presetNamesInDb.contains(preset.preset_name)) continue;

            versionList.add(preset);
        }

        return versionList;
    }

    public static void sortItemList(List<MVersion> itemList) {
        Collections.sort(itemList, new Comparator<MVersion>() {
                    @Override
                    public int compare(MVersion a, MVersion b) {
                        final String locale_a = a.locale;
                        final String locale_b = b.locale;
                        if (U.equals(locale_a, locale_b)) {
                            return a.longName.compareToIgnoreCase(b.longName);
                        }
                        if (locale_a == null) {
                            return +1;
                        } else if (locale_b == null) {
                            return -1;
                        }

                        return a.longName.compareToIgnoreCase(b.longName);
                    }
                }
        );
    }

    public static boolean isDownloading(MVersion mv) {
        boolean downloading = false;
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
        return downloading;
    }

    public static void startDownload(Context context, MVersion mv) {
        if (!mv.getActive() && mv instanceof MVersionPreset) {
            startDownload(context, (MVersionPreset) mv);
            App.getLbm().sendBroadcast(new Intent(Constants.ACTION_VERSION_LIST_RELOAD));

        }
    }


    public static void startDownload(final Context context, final MVersionPreset mv) {
        if (mv.getActive()) {
            throw new RuntimeException("THIS SHOULD NOT HAPPEN: preset may not have the active checkbox checked.");
        }

        {
            int enabled = -1;
            try {
                enabled = App.context.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
            } catch (Exception e) {
            }

            if (enabled == -1
                    || enabled == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    || enabled == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER) {
                new MaterialDialog.Builder(context)
                        .content(R.string.ed_download_manager_not_enabled_prompt)
                        .positiveText(R.string.ok)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            try {
                                                context.startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.android.providers.downloads")));
                                            } catch (ActivityNotFoundException e) {
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
        App.getLbm().sendBroadcast(new Intent(Constants.ACTION_VERSION_LIST_RELOAD));
    }

    public static MVersionDb getMVersionDb(MVersion m) {
        return S.getDb().getMVersionDbByName(m.longName);
    }
}
