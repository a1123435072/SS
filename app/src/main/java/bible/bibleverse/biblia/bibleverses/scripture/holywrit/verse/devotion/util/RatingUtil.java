package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fw.basemodules.utils.AndroidUtils;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.view.Dialog;

/**
 * Created by yzq on 16/11/18.
 */

public class RatingUtil {

    public final static String PREFS_NAME = "rate";
    private final static String KEY_TRIGGER_EVENT_TIMES = "trigger_event_times";
    public final static String KEY_SHOW_RATING_ON_TRIGGER = "show_rating_on_trigger";

    static Dialog rateDialog;

    public static boolean showRatingDialog(final Context context, boolean cancelable) {
        if (context == null) {
            return false;
        }
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.dialog_rating, null);
        final TextView titleTv = (TextView) layout.findViewById(R.id.title);
        final TextView level = (TextView) layout.findViewById(R.id.level);
        final TextView msg = (TextView) layout.findViewById(R.id.rateus_msg);
        final RatingBar ratingBar = (RatingBar) layout.findViewById(R.id.raingbar);
        TextView cancel = (TextView) layout.findViewById(R.id.dialog_cancel);
        final TextView submit = (TextView) layout.findViewById(R.id.dialog_ok);

        final RelativeLayout ratingGuideLayout = (RelativeLayout) layout.findViewById(R.id.rating_guide_layout);
        layout.post(new Runnable() {
            @Override
            public void run() {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ratingGuideLayout.getLayoutParams();
                layoutParams.height = layout.getHeight();
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                ratingGuideLayout.setLayoutParams(layoutParams);
            }
        });

        String appName = context.getString(R.string.app_name);
        msg.setText(context.getString(R.string.rateus_dialog_msg, appName));
        submit.setEnabled(false);
        cancel.setTextColor(context.getResources().getColor(R.color.black_999999));
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                level.setVisibility(View.VISIBLE);
                if (rating == 0) {
                    submit.setTextColor(context.getResources().getColor(R.color.white));
                    submit.setEnabled(false);
                    level.setTextColor(context.getResources().getColor(R.color.white));
                    level.setText(R.string.rateus_level_poor);
                } else if (rating == 1) {
                    submit.setTextColor(context.getResources().getColor(R.color.white));
                    submit.setEnabled(true);
                    level.setTextColor(context.getResources().getColor(R.color.black_333333));
                    level.setText(R.string.rateus_level_poor);
                } else if (rating == 2) {
                    submit.setTextColor(context.getResources().getColor(R.color.white));
                    submit.setEnabled(true);
                    level.setTextColor(context.getResources().getColor(R.color.black_333333));
                    level.setText(R.string.rateus_level_fair);
                } else if (rating == 3) {
                    submit.setTextColor(context.getResources().getColor(R.color.white));
                    submit.setEnabled(true);
                    level.setTextColor(context.getResources().getColor(R.color.black_333333));
                    level.setText(R.string.rateus_level_good);
                } else if (rating == 4) {
                    submit.setTextColor(context.getResources().getColor(R.color.white));
                    submit.setEnabled(true);
                    level.setTextColor(context.getResources().getColor(R.color.black_333333));
                    level.setText(R.string.rateus_level_verygood);
                } else if (rating == 5) {
                    submit.setTextColor(context.getResources().getColor(R.color.white));
                    submit.setEnabled(true);
                    level.setTextColor(context.getResources().getColor(R.color.black_333333));
                    level.setText(R.string.rateus_level_excellent);
                    showRateSubmitDialog(context);
                    rateDialog.dismiss();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateDialog.dismiss();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateDialog.dismiss();
                if (ratingBar.getRating() < 5) {
                    Utility.sendEmailToOffical(context,
                            context.getString(R.string.feedback_email_subject, AndroidUtils.getAppName(context)) + "(" + Build.BRAND + " " + Build.MODEL + "_"
                                    + Build.VERSION.RELEASE + "_" + Utility.getAppVersionName(context)
                                    + ")");
                } else {
                    showRateSubmitDialog(context);
                }
            }
        });
        rateDialog = DialogUtils.showCustomDialog(context, layout, -1, null, -1, null);
        rateDialog.setCancelable(cancelable);

        // Rating touch gesture guide
        startRatingGuideAnim(layout, ratingBar);

        return true;
    }

    private static void startRatingGuideAnim(final View view, final RatingBar ratingBar) {
        if (Utility.isRatingGuideShown(view.getContext())) {
            return;
        } else {
            Utility.setRatingGuideShown(view.getContext(), true);
        }

        final View mRatingGuideLayout = view.findViewById(R.id.rating_guide_layout);
        final ImageView mImgGestureTrackingView = (ImageView) view.findViewById(R.id.gesture_tracking_view);
        final ImageView mImgFinger = (ImageView) view.findViewById(R.id.finger);
        mRatingGuideLayout.setVisibility(View.VISIBLE);
        mImgGestureTrackingView.setVisibility(View.INVISIBLE);
        mImgGestureTrackingView.setScaleX(0);
        mImgGestureTrackingView.setTranslationX(0);

        // Mark the guide view is touched.
        mRatingGuideLayout.setTag(false);
        mRatingGuideLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mRatingGuideLayout.setTag(true);
                return true;
            }
        });

        // Run animation
        mImgFinger.postDelayed(new Runnable() {
            int mShowCount = 0;
            Runnable mRunnable = this;

            @Override
            public void run() {
                if (mShowCount == 2 || ((boolean) mRatingGuideLayout.getTag())) {
                    mRatingGuideLayout.setVisibility(View.GONE);
                    ratingBar.setProgress(0);
                    return;
                }
                mShowCount++;
                final Resources resources = view.getContext().getResources();
                final float maxScale = 2f;
                final float gestureTrackingViewWidth = mImgGestureTrackingView.getWidth();
                final float swipeDistance = resources.getDimensionPixelSize(R.dimen.rating_guide_swipe_distance);
                final float gestureMargin = resources.getDimensionPixelSize(R.dimen.rating_guide_gesture_margin);

                ObjectAnimator animFinger = ObjectAnimator.ofFloat(mImgFinger, "translationX", 0, swipeDistance);
                animFinger.setDuration(1000);
                animFinger.setRepeatMode(ObjectAnimator.RESTART);
                animFinger.setRepeatCount(0);
                animFinger.setInterpolator(new AccelerateInterpolator());
                animFinger.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mImgGestureTrackingView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mImgFinger.postDelayed(mRunnable, 800);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                animFinger.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (float) animation.getAnimatedValue();
                        float critical = swipeDistance * 2 / 3;
                        float scale;
                        float targetX;
                        if (value <= critical) {
                            scale = value / critical * maxScale;
                        } else {
                            scale = (value - critical) / (swipeDistance - critical) * maxScale;
                            scale = maxScale - scale;
                        }
                        targetX = value - (scale * gestureTrackingViewWidth / 2) + (scale * gestureMargin / 2.5f);
                        mImgGestureTrackingView.setScaleX(scale);
                        mImgGestureTrackingView.setTranslationX(targetX);

                        // Set rating bar
                        ratingBar.setProgress(Float.valueOf(value / swipeDistance * 5).intValue());
                    }
                });
                animFinger.start();
            }
        }, 200);
    }

    static Dialog rateSubmitDialog;

    private static void showRateSubmitDialog(final Context context) {
        if (context == null) {
            return;
        }
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_rating_submit, null);
        TextView cancel = (TextView) layout.findViewById(R.id.dialog_cancel);
        TextView submit = (TextView) layout.findViewById(R.id.dialog_ok);
        cancel.setText(R.string.rateus_dialog_cancle);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rateSubmitDialog.dismiss();
            }
        });
        submit.setText(R.string.rateus_dialog_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rateInGooglePlay(context);
                rateSubmitDialog.dismiss();
            }
        });
        rateSubmitDialog = DialogUtils.showCustomDialog(context, layout, -1, null, -1, null);
    }

    private static void rateInGooglePlay(Context context) {
        if (context == null) {
            return;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.android.vending", "com.android.vending.AssetBrowserActivity");
            String uri = "market://details?id=" + context.getPackageName();
            intent.setData(Uri.parse(uri));
            context.startActivity(intent);
        } catch (Exception e) {
        }
    }

    public static void recordTriggerEventTimes(Context ctx) {
        if (ctx == null) {
            return;
        }
        SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
        int usedTimes = settings.getInt(KEY_TRIGGER_EVENT_TIMES, 0);
        if (usedTimes < 13) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(KEY_TRIGGER_EVENT_TIMES, ++usedTimes);
            if (usedTimes == 6 || usedTimes == 12) {
                editor.putBoolean(KEY_SHOW_RATING_ON_TRIGGER, true);
            }
            editor.apply();
        }
    }

    public static boolean ratingUsOnTrigger(Context ctx) {
        if (ctx == null) {
            return false;
        }
        SharedPreferences settings = ctx.getSharedPreferences(PREFS_NAME, 0);
        boolean showRating = settings.getBoolean(KEY_SHOW_RATING_ON_TRIGGER, false);
        if (showRating) {
            try {
                boolean shown = showRatingDialog(ctx, false);

                settings.edit()
                        .putBoolean(KEY_SHOW_RATING_ON_TRIGGER, false)
                        .apply();

                return shown;
            } catch (Exception e) {
            }
        }
        return false;
    }
}
