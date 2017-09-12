package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fw.basemodules.utils.OmAsyncTask;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.App;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventRefreshPrayerReminder;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.ChangeConfigurationHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.notification.DailyNotifyAlarm;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.PrayerNotificationUtil;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import de.greenrobot.event.EventBus;
import yuku.afw.V;
import yuku.afw.storage.Preferences;


public class SettingsActivity extends BaseActivity {
    private LinearLayout mLanguageItem, mVerseNotificationItem, mDevotionNotificationItem, mPrayNotificationItem, mShareItem, mAboutItem;
    private CheckBox mDevotionRemindCheckbox;
    private TextView mVerseOfDayValue, mPrayValue, mCurrentLangTv;
    private String[] mLocaleArray, mLanguageArray;

    public static Intent createIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    private TimePickerDialog.OnTimeSetListener mDailyVerTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
            Preferences.setString(getString(R.string.pref_verse_of_day_reminder), String.format(Locale.US, "%02d%02d", hourOfDay, minute));
            updateVerseOfDay();
            Preferences.setBoolean(getString(R.string.pref_verse_reminder_change_by_user), true);
        }
    };

    private DialogInterface.OnCancelListener mDailyVerseOffListener = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            Preferences.setString(getString(R.string.pref_verse_of_day_reminder), Constants.DAILY_NOTIFICATION_OFF);
            updateVerseOfDay();
            Preferences.setBoolean(getString(R.string.pref_verse_reminder_change_by_user), false);
        }
    };


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViewById(R.id.container).setBackgroundColor(getResources().getColor(R.color.white));

        initToolbar();

        setLanguageItem();

        setVerseNotificationItem();

        setDevotionNotificationItem();

        setPrayNotificationItem();

        setOther();

        EventBus.getDefault().register(this);
    }

    private void initToolbar() {
        final Toolbar toolbar = V.get(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        setTitle(getString(R.string.menuPengaturan));
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_back_black);
    }

    private void setLanguageItem() {
        mLocaleArray = getResources().getStringArray(R.array.pref_language_values);
        mLanguageArray = getResources().getStringArray(R.array.pref_language_labels);

        mLanguageItem = V.get(this, R.id.language_item);
        mCurrentLangTv = V.get(this, R.id.current_language);
        try {
            String locale = Preferences.getString(getString(R.string.pref_language_key), getString(R.string.pref_language_default));
            int index = Arrays.asList(mLocaleArray).indexOf(locale);
            if (index >= 0) {
                mCurrentLangTv.setText(mLanguageArray[index]);
            }
        } catch (Exception e) {
            mCurrentLangTv.setText(mLanguageArray[0]);
        }
        mLanguageItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguageDialog();
            }
        });
    }

    private void setVerseNotificationItem() {
        mVerseNotificationItem = V.get(this, R.id.verse_alarm_item);
        mVerseOfDayValue = V.get(this, R.id.ver_of_day_val);
        updateVerseOfDay();
        mVerseNotificationItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDailyVerseTimePicker();
            }
        });
    }

    private void setDevotionNotificationItem() {
        mDevotionRemindCheckbox = V.get(this, R.id.devotion_reminder_checkbox);
        mDevotionRemindCheckbox.setChecked(Preferences.getBoolean(getString(R.string.pref_devotion_reminder), true));
        mDevotionRemindCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Preferences.setBoolean(getString(R.string.pref_devotion_reminder), isChecked);
            }
        });
    }

    private void setPrayNotificationItem() {
        mPrayNotificationItem = V.get(this, R.id.prayer_alarm_item);
        mPrayNotificationItem.setVisibility(View.VISIBLE);
        mPrayValue = V.get(this, R.id.prayer_val);
        updatePrayTimeContent();
        mPrayNotificationItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, DailyPrayerReminderSettingActivity.class));
            }
        });
    }

    private void setOther() {
        mShareItem = V.get(this, R.id.share_item);
        mAboutItem = V.get(this, R.id.about_item);
        mShareItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.shareTextBySystem(SettingsActivity.this, getString(R.string.share_msg) + "\n" + Constants.GOOGLE_SHORT_URL);
            }
        });
        mAboutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AboutUsActivity.createIntent());
            }
        });
    }


    private void showDailyVerseTimePicker() {
        String serverValue = Preferences.getString(getString(R.string.pref_verse_of_day_reminder_sever));
        final int hour = serverValue.equals(Constants.DAILY_NOTIFICATION_OFF) ? 8 : Integer.parseInt(serverValue.substring(0, 2));
        final int minute = serverValue.equals(Constants.DAILY_NOTIFICATION_OFF) ? 0 : Integer.parseInt(serverValue.substring(2, 4));
        TimePickerDialog dailyVersePicker = TimePickerDialog.newInstance(
                mDailyVerTimeSetListener,
                hour,
                minute,
                DateFormat.is24HourFormat(this)//mode24Hours
        );
        dailyVersePicker.setCancelText(getString(R.string.turn_off));
        dailyVersePicker.setThemeDark(false);
        dailyVersePicker.vibrate(false);
        dailyVersePicker.setOnCancelListener(mDailyVerseOffListener);
        dailyVersePicker.show(getFragmentManager(), "DailyVerseTimePickerDialog");
    }

    private void updateVerseOfDay() {
        String currentValue = Preferences.getString(getString(R.string.pref_verse_of_day_reminder));
        if (currentValue == null) {//new user
            Preferences.setString(getString(R.string.pref_verse_of_day_reminder), Constants.DAILY_VERSE_NOTIFICATION_HOUR);
            currentValue = Constants.DAILY_VERSE_NOTIFICATION_HOUR;
        }
        if (currentValue.equals(Constants.DAILY_NOTIFICATION_OFF)) {
            mVerseOfDayValue.setText(R.string.dr_off);
        } else {
            Calendar time = GregorianCalendar.getInstance();
            time.set(Calendar.HOUR_OF_DAY, Integer.parseInt(currentValue.substring(0, 2)));
            time.set(Calendar.MINUTE, Integer.parseInt(currentValue.substring(2, 4)));
            mVerseOfDayValue.setText(DateFormat.getTimeFormat(this).format(time.getTime()));
        }
    }

    private void updatePrayTimeContent() {
        String currentValue = Preferences.getString(getString(R.string.pref_prayer_of_day_reminder), PrayerNotificationUtil.getPrayerDefaultReminder(this));
        if (TextUtils.isEmpty(currentValue)) {
            mPrayValue.setText(R.string.dr_off);
        } else {
            try {
                JSONArray jsonArray = new JSONArray(currentValue);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = (JSONObject) jsonArray.opt(i);
                    if (item != null) {
                        String timeValue = item.optString(Constants.DP_KEY_TIME);
                        if (!TextUtils.equals(timeValue, Constants.DAILY_NOTIFICATION_OFF)) {
                            Calendar time = GregorianCalendar.getInstance();
                            time.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeValue.substring(0, 2)));
                            time.set(Calendar.MINUTE, Integer.parseInt(timeValue.substring(2, 4)));
                            sb.append(DateFormat.getTimeFormat(this).format(time.getTime())).append("  ");
                        }
                    }
                }
                if (TextUtils.isEmpty(sb.toString())) {
                    mPrayValue.setText(R.string.dr_off);
                } else {
                    mPrayValue.setText(sb.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
            }
        }
    }


    private void showLanguageDialog() {
        final String nowLang = Preferences.getString(R.string.pref_language_key, R.string.pref_language_default);
        int checked = 0;
        final ArrayList<String> localeItems = new ArrayList<>();
        final ArrayList<String> languageItems = new ArrayList<>();
        for (int i = 0; i < mLocaleArray.length; i++) {
            if (nowLang.equals(mLocaleArray[i])) {
                checked = i;
            }
            localeItems.add(mLocaleArray[i]);
            languageItems.add(mLanguageArray[i]);
        }

        new MaterialDialog.Builder(this)
                .title(getString(R.string.pref_bahasa_language))
                .titleColor(ContextCompat.getColor(this, R.color.black))
                .contentColor(ContextCompat.getColor(this, R.color.black_999))
                .items(languageItems)
                .itemsCallbackSingleChoice(checked, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        if (which == -1) {
                            return false;
                        }
                        Preferences.setString(getString(R.string.pref_language_key), localeItems.get(which));
                        mCurrentLangTv.setText(localeItems.get(which));
                        App.forceUpdateConfiguration();
                        ChangeConfigurationHelper.notifyConfigurationNeedsUpdate();
                        dialog.dismiss();
                        // restart this activity
                        finish();
                        Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        return true;
                    }
                })
                .alwaysCallSingleChoiceCallback()
                .show();
    }

    /**
     * set view padding by preferences
     *
     * @param view
     */
    public static void setPaddingBasedOnPreferences(final View view) {
        final Resources r = App.context.getResources();
        if (Preferences.getBoolean(r.getString(R.string.pref_textPadding_key), r.getBoolean(R.bool.pref_textPadding_default))) {
            final int top = r.getDimensionPixelOffset(R.dimen.text_top_padding);
            final int bottom = r.getDimensionPixelOffset(R.dimen.text_bottom_padding);
            final int side = r.getDimensionPixelOffset(R.dimen.text_side_padding);
            view.setPadding(side, top, side, bottom);
        } else {
            final int no = r.getDimensionPixelOffset(R.dimen.text_nopadding);
            view.setPadding(no, no, no, no);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        //set alarm
        new SetAlarm().execute();
    }

    private class SetAlarm extends OmAsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            DailyNotifyAlarm.setDailyVerseNotifyAlarm(SettingsActivity.this, Preferences.getString(getString(R.string.pref_verse_of_day_reminder)));
            return null;
        }
    }

    public void onEventMainThread(EventRefreshPrayerReminder event) {
        if (event != null) {
            updatePrayTimeContent();
        }
    }
}
