package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.notification.floating;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.SelfAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.DevotionBean;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PrayerBean;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.VerseListResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.ExitAppActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.NewMainActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.WelcomeActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventClickDailyNt;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventScreenOn;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.notification.NotificationBase;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.PrayerNotificationUtil;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import de.greenrobot.event.EventBus;

import static yuku.afw.App.context;

/**
 * Created by zhangfei on 9/26/16.
 */
public class DailyNotifyFloatView {
    public final static int TYPE_DAILY_VERSE = 0;
    public final static int TYPE_DAILY_PRAYER = 1;
    public final static int TYPE_DAILY_SIGN_IN = 2;
//    public final static int TYPE_DAILY_DEVOTION = 3;

    private Context mContext;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private boolean isViewAdded;
    private View mContentView;
    private static final int DISMISS_INTERVAL = 10000;

    private View mRootView, close;
    private TextView mTitle, mVerseTxt;
    private ImageView mBg, mIcon;

    ValueAnimator mBgAnim;

    private VerseListResponse.DataBean.VerseBean mVerseItem;
    private DevotionBean mDevotionItem;
    private int mType, mPrayerType;
    private PrayerBean mPray;
    private String mPrayerCatName;

    public DailyNotifyFloatView(Context context) {
        this.mContext = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        EventBus.getDefault().register(this);
        init();
    }

    public void setNotifyType(VerseListResponse.DataBean.VerseBean verseItem, int type) {
        this.mVerseItem = verseItem;
        this.mType = type;
    }

    public void setNotifyType(DevotionBean devotionItem, int type) {
        this.mDevotionItem = devotionItem;
        this.mType = type;
    }

    public void setNotifyType(int type) {
        this.mType = type;
    }

    public void setPrayerType(int prayerType) {
        this.mPrayerType = prayerType;
    }

    public void setPrayer(PrayerBean pray, String catName) {
        this.mPray = pray;
        this.mPrayerCatName = catName;
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);

        mLayoutParams = new WindowManager.LayoutParams();

        if (Build.VERSION.SDK_INT >= 19) {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }

        mLayoutParams.format = PixelFormat.RGBA_8888;

        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mLayoutParams.windowAnimations = R.style.NotificationAnim;
        int padding = (int) pxFromDp(10.0f);
        int height = (int) pxFromDp(80.0f);
        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        mLayoutParams.width = metrics.widthPixels - 2 * padding;
        mLayoutParams.height = height;
        mLayoutParams.x = padding;
        mLayoutParams.y = padding;


        mContentView = inflater.inflate(R.layout.notify_float_daily_verse_layout, null, false);
        mRootView = mContentView.findViewById(R.id.root_view);
        mIcon = (ImageView) mContentView.findViewById(R.id.icon);
        close = mContentView.findViewById(R.id.close_btn);
        mTitle = (TextView) mContentView.findViewById(R.id.title);
        mVerseTxt = (TextView) mContentView.findViewById(R.id.verse_text);
        mBg = (ImageView) mContentView.findViewById(R.id.background);
    }

    private void onClickNotification() {
        Utility.deleteNotification(mContext, Constants.NOTIFY_DAILY_NOTIFY_ID);

        switch (mType) {
            case TYPE_DAILY_VERSE:
                Intent resultIntent = WelcomeActivity.createIntent(context, Constants.NOTIFY_DAILY_VERSE_ID, Constants.TYPE_FLOATING_NOTIFICATION, mVerseItem.getDate(), mVerseItem.getQuote(), mVerseItem.getQuoteRefer(), mVerseItem.getId(), mVerseItem.getImageUrl());
                mContext.startActivity(resultIntent);
                break;
            case TYPE_DAILY_PRAYER:
                if (mPray != null) {
                    ArrayList<PrayerBean> list = new ArrayList<>();
                    list.add(mPray);
                    mContext.startActivity(WelcomeActivity.createPrayerIntent(context, Constants.NOTIFY_DAILY_PRAYER_ID, Constants.TYPE_FLOATING_NOTIFICATION, list, 0, mPrayerCatName));
                } else {
                    mContext.startActivity(WelcomeActivity.createPrayerIntent(context, Constants.NOTIFY_DAILY_PRAYER_ID, Constants.TYPE_FLOATING_NOTIFICATION, null, 0, ""));
                }
                break;
            case TYPE_DAILY_SIGN_IN:
                Intent signIntent = ExitAppActivity.createIntent(mContext, ExitAppActivity.TYPE_SIGN_IN_NOTIFICATION);
                signIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                signIntent.putExtra(Constants.KEY_NOTIFICATION_TYPE, Constants.TYPE_FLOATING_NOTIFICATION);
                mContext.startActivity(signIntent);
                break;
            default:
                mContext.startActivity(new Intent(mContext, NewMainActivity.class));
                break;
        }
        hide();
    }

    public void show() {
        try {
            if (mContentView.isShown()) {
                setContent();
            } else {
                setContent();
                mWindowManager.addView(mContentView, mLayoutParams);
                isViewAdded = true;
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startAnim();
                }
            }, 200);
        } catch (Exception e) {
        }
    }

    public void setContent() {
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                String action = "";
                switch (mType) {
                    case TYPE_DAILY_VERSE:
                        action = AnalyticsConstants.A_VERSE_NOTICE;
                        break;
                    case TYPE_DAILY_PRAYER:
                        action = AnalyticsConstants.A_PRAYER_NOTICE;
                        break;
                    case TYPE_DAILY_SIGN_IN:
                        action = AnalyticsConstants.A_SIGNIN_NOTICE;
                        break;
                }
                SelfAnalyticsHelper.sendNotificationAnalytics(mContext, action, AnalyticsConstants.L_FLOAT_NT_CLOSE);
            }
        });
        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickNotification();
            }
        });
        switch (mType) {
            case TYPE_DAILY_VERSE:
                mIcon.setImageResource(R.mipmap.ic_launcher);
                if (mVerseItem != null) {
                    mTitle.setText(mVerseItem.getQuoteRefer());
                    mVerseTxt.setText(mVerseItem.getQuote());
                }
                break;
            case TYPE_DAILY_PRAYER:
                mIcon.setImageResource(R.drawable.ic_notify_prayer);
                if (mPray != null) {
                    mTitle.setText(Html.fromHtml(mPray.getTitle()));
                    mVerseTxt.setText(Html.fromHtml(mPray.getContent()));
                } else {
                    mTitle.setText(PrayerNotificationUtil.getReminderTitle(mContext, mPrayerType));
                    mVerseTxt.setText(PrayerNotificationUtil.getReminderContent(mContext, mPrayerType));
                }
                break;
            case TYPE_DAILY_SIGN_IN:
                mIcon.setImageResource(R.drawable.ic_notify_sgin_in);
                mTitle.setText(mContext.getString(R.string.notify_daily_sign_in_title));
                mVerseTxt.setText(mContext.getString(R.string.notify_daily_sign_in_msg));
                break;
        }
    }

    public void onEventMainThread(EventClickDailyNt event) {
        if (event != null) {
            hide();
        }
    }

    public void onEventMainThread(EventScreenOn event) {
        if (event != null) {
            if (isViewAdded && mBgAnim != null && !mBgAnim.isRunning()) {
                startAnim();
            }
        }
    }


    public void hide() {
        if (isViewAdded) {
            if (mBgAnim != null) {
                mBgAnim.cancel();
            }
            mHandler.removeMessages(HIDE_WINDOW);
            mWindowManager.removeView(mContentView);
            isViewAdded = false;
            EventBus.getDefault().unregister(mContext);
            switch (mType) {
                case TYPE_DAILY_VERSE:
                    NotificationBase.setShowingNotificationStatus(mContext, Constants.NOTIFY_DAILY_VERSE_ID, false);
                    break;
                case TYPE_DAILY_PRAYER:
                    NotificationBase.setShowingNotificationStatus(mContext, Constants.NOTIFY_DAILY_PRAYER_ID, false);
                    break;
                case TYPE_DAILY_SIGN_IN:
                    NotificationBase.setShowingNotificationStatus(mContext, Constants.NOTIFY_DAILY_SIGN_IN_ID, false);
                    break;
            }
        }
    }

    private void startAnim() {
        if (mBgAnim != null && mBgAnim.isRunning()) {
            mBgAnim.cancel();
        }
        mBgAnim = ValueAnimator.ofFloat(0f, 360f);
        mBgAnim.setDuration(8000);
        final Bitmap bitmapOrg = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.notify_float_daily_verse_bg);
        final Matrix matrix = new Matrix();
        mBgAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                matrix.postRotate(v);
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, bitmapOrg.getWidth(), bitmapOrg.getHeight(), matrix, true);
                mBg.setImageBitmap(rotatedBitmap);
                matrix.reset();
            }
        });
        mBgAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mBg.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        mBgAnim.setInterpolator(new LinearInterpolator());
        mBgAnim.start();
    }


    private void autoDismiss() {
        mHandler.removeMessages(HIDE_WINDOW);
        mHandler.sendEmptyMessageDelayed(HIDE_WINDOW, DISMISS_INTERVAL);
    }

    public static float pxFromDp(final float dp) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public int getStatusBarHeight() {
        int height = 0;
        int resId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            height = mContext.getResources().getDimensionPixelSize(resId);
        }
        return height;
    }

    private static final int HIDE_WINDOW = 0;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HIDE_WINDOW:
                    hide();
                    break;
            }
            return false;
        }
    });
}
