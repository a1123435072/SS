package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fw.basemodules.utils.OmAsyncTask;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.helper.WrapContentLinearLayoutManager;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventRefreshPrayerReminder;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.ReminderBean;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.notification.DailyNotifyAlarm;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.PrayerNotificationUtil;
import de.greenrobot.event.EventBus;
import yuku.afw.V;
import yuku.afw.storage.Preferences;


public class DailyPrayerReminderSettingActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private LayoutInflater mInflater;
    private MyAdapter mAdapter;
    private LoadReminderTask mLoadReminderTask;
    private boolean mIsPrayerReminderChange = false;

    public static Intent createIntent(Context context) {
        return new Intent(context, DailyPrayerReminderSettingActivity.class);
    }

    private class TimeSetImp implements TimePickerDialog.OnTimeSetListener {
        private ReminderBean mReminderBean;
        private int mPos;

        public TimeSetImp(ReminderBean reminderBean, int pos) {
            mReminderBean = reminderBean;
            mPos = pos;
        }

        @Override
        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
            String formatTime = PrayerNotificationUtil.getFormatTime(DailyPrayerReminderSettingActivity.this, hourOfDay, minute);
            if (mReminderBean != null && mPos >= 0) {
                mReminderBean.time = formatTime;
                mAdapter.notifyDataSetChanged();
                PrayerNotificationUtil.updatePrayerReminderTime(DailyPrayerReminderSettingActivity.this, mPos, hourOfDay, minute);
            }
            mIsPrayerReminderChange = true;
        }
    }

    private class TimeTurnOffImp implements DialogInterface.OnCancelListener {
        private ReminderBean mReminderBean;
        private int mPos;

        public TimeTurnOffImp(ReminderBean reminderBean, int pos) {
            mReminderBean = reminderBean;
            mPos = pos;
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            if (mReminderBean != null && mPos >= 0) {
                if (mReminderBean.type == Constants.DP_TYPE_CUSTOM) {
                    mAdapter.removeItem(mReminderBean);
                    mAdapter.notifyDataSetChanged();
                    PrayerNotificationUtil.deletePrayerReminderTime(DailyPrayerReminderSettingActivity.this, mPos);
                } else {
                    mReminderBean.time = getString(R.string.dr_off);
                    mAdapter.notifyDataSetChanged();
                    PrayerNotificationUtil.turnOffPrayerReminderTime(DailyPrayerReminderSettingActivity.this, mPos);
                }
                mIsPrayerReminderChange = true;
            }
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer_reminder_setting);

        initToolbar();

        initView();
    }

    private void initToolbar() {
        final Toolbar toolbar = V.get(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        setTitle(getString(R.string.daily_prayer));
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_back_black);
    }

    private void initView() {
        mInflater = LayoutInflater.from(this);
        mRecyclerView = V.get(this, R.id.recyclerview);
        WrapContentLinearLayoutManager linearLayoutManager = new WrapContentLinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        loadRemiderData();
    }

    class ViewHolderReminder extends RecyclerView.ViewHolder {

        TextView titleTv, timeTv;
        View rootView;

        public ViewHolderReminder(View itemView) {
            super(itemView);
            rootView = V.get(itemView, R.id.prayer_alarm_item);
            titleTv = V.get(itemView, R.id.prayer_reminder_title);
            timeTv = V.get(itemView, R.id.prayer_val);
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<ViewHolderReminder> {

        private List<ReminderBean> mItems;

        public MyAdapter(List<ReminderBean> items) {
            mItems = new ArrayList<>();
            mItems.addAll(items);
        }

        public void addItem(ReminderBean reminderBean) {
            mItems.add(reminderBean);
            notifyDataSetChanged();
        }

        public void removeItem(ReminderBean reminderBean) {
            mItems.remove(reminderBean);
            notifyDataSetChanged();
        }

        @Override
        public ViewHolderReminder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.layout_prayer_reminder_item, parent, false);
            return new ViewHolderReminder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolderReminder holder, final int position) {
            final ReminderBean reminderBean = getItem(position);
            if (reminderBean != null) {
                if (reminderBean.type != Constants.DP_TYPE_CUSTOM) {
                    holder.titleTv.setText(PrayerNotificationUtil.getReminderTitle(DailyPrayerReminderSettingActivity.this, reminderBean.type));
                } else if (!TextUtils.isEmpty(reminderBean.catName)) {
                    holder.titleTv.setText(reminderBean.catName);
                } else {
                    holder.titleTv.setText(getString(R.string.prayer_custom_notification_title));
                }
                holder.timeTv.setText(reminderBean.time);
                holder.rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showTimePicker(reminderBean, position);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public ReminderBean getItem(int pos) {
            if (pos < mItems.size()) {
                return mItems.get(pos);
            }
            return null;
        }
    }

    public void loadRemiderData() {
        mLoadReminderTask = new LoadReminderTask();
        mLoadReminderTask.execute();
    }

    private class LoadReminderTask extends OmAsyncTask<Void, Void, List<ReminderBean>> {
        @Override
        protected List<ReminderBean> doInBackground(Void... voids) {
            String currentValue = Preferences.getString(getString(R.string.pref_prayer_of_day_reminder));
            if (TextUtils.isEmpty(currentValue)) {
                currentValue = PrayerNotificationUtil.getPrayerDefaultReminder(DailyPrayerReminderSettingActivity.this);
                Preferences.setString(getString(R.string.pref_prayer_of_day_reminder), currentValue);
            }
            return PrayerNotificationUtil.getPrayerReminderBeanList(DailyPrayerReminderSettingActivity.this, currentValue);
        }

        @Override
        protected void onPostExecute(List<ReminderBean> reminderBeens) {
            if (reminderBeens != null && !reminderBeens.isEmpty()) {
                mAdapter = new MyAdapter(reminderBeens);
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }

    private void showTimePicker(ReminderBean reminderBean, int pos) {
        try {
            String timeValue = null;
            int type = 0;
            if (reminderBean != null && pos >= 0) {
                timeValue = reminderBean.timeValue;
                type = reminderBean.type;
            } else {
                timeValue = Constants.DAILY_NOTIFICATION_OFF;
            }
            final int hour = getReminderHourOfDay(timeValue, type);
            final int minute = TextUtils.isEmpty(timeValue) || timeValue.equals(Constants.DAILY_NOTIFICATION_OFF) ? 0 : Integer.parseInt(timeValue.substring(2, 4));
            TimePickerDialog dailyVersePicker = TimePickerDialog.newInstance(
                    new TimeSetImp(reminderBean, pos),
                    hour,
                    minute,
                    DateFormat.is24HourFormat(this)//mode24Hours
            );
            dailyVersePicker.setCancelText(getTimePickerCancleText(reminderBean));
            dailyVersePicker.setThemeDark(false);
            dailyVersePicker.vibrate(false);
            dailyVersePicker.setOnCancelListener(new TimeTurnOffImp(reminderBean, pos));
            dailyVersePicker.show(getFragmentManager(), "DailyPrayerTimePickerDialog");
        } catch (Exception e) {
        }
    }

    private int getReminderHourOfDay(String timeValue, int type) {
        if (!TextUtils.isEmpty(timeValue) && !TextUtils.equals(timeValue, Constants.DAILY_NOTIFICATION_OFF)) {
            return Integer.parseInt(timeValue.substring(0, 2));
        } else if (type > 0) {
            return type == Constants.DP_TYPE_MORNING ? 8 : 22;
        } else {
            return 8;
        }
    }

    private String getTimePickerCancleText(ReminderBean reminderBean) {
        int resId = R.string.cancel;
        if (reminderBean != null) {
            resId = reminderBean.type == Constants.DP_TYPE_CUSTOM ? R.string.delete : R.string.turn_off;
        }
        return getString(resId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //set alarm
        if (mIsPrayerReminderChange) {
            EventBus.getDefault().post(new EventRefreshPrayerReminder());
            new SetAlarm().execute();
        }
    }

    private class SetAlarm extends OmAsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            DailyNotifyAlarm.setPrayerNotifyAlarm(DailyPrayerReminderSettingActivity.this);
            return null;
        }
    }
}
