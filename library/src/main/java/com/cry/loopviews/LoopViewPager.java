package com.cry.loopviews;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cry.loopviews.listener.OnItemSelectedListener;
import com.library.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by zxj on 15/6/6.
 */
public class LoopViewPager extends FrameLayout {
    int pagerWidth = 0;
    int pagerHeight = 0;
    boolean touching = false;//is touching
    int last_touch_point = 0;
    int position = 0;//now view x position
    private List<View> viewList = new ArrayList<>();
    private String[] userNames;
    private String[] payments;
    private GestureDetector mGestureDetector;
    private int item = 0;
    private int distence = 0;
    private ValueAnimator valueAnimator = null;
    private boolean autoChange = false;
    private long autoChangeTime = 4000;
    private ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
    private OnItemSelectedListener onItemSelectedListener = null;
    private boolean horizontal = true;//
    private TextView textView0, textView1;
    LoopHandler loopHandler = new LoopHandler(4000) {
        @Override
        public void du() {
            try {
                if (horizontal) {
                    AnimationTo(getScrollX(), (getScrollX() / pagerWidth + 1) * pagerWidth);
                } else {
                    AnimationTo(getScrollY(), (getScrollY() / pagerHeight + 1) * pagerHeight);
                }
            } catch (Exception e) {
            }
        }
    };

    public LoopViewPager(Context context) {
        super(context);
        init(context);
    }

    public LoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoopViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        userNameIdx = new Random().nextInt(100);

        mGestureDetector = new GestureDetector(context, getOnGestureListener());
    }

    private GestureDetector.SimpleOnGestureListener getOnGestureListener() {
        return new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (horizontal) {
                    if (pagerWidth != 0) {
                        distence = getScrollX() + (int) distanceX;
                        if (Math.abs(distence / pagerWidth + 1) == viewList.size() + 1) {
                            distence = 0;
                        }
                    }
                } else {
                    if (pagerHeight != 0) {
                        distence = getScrollY() + (int) distanceY;
                        if (Math.abs(distence / pagerHeight + 1) == viewList.size() + 1) {
                            distence = 0;
                        }
                    }
                }

                try {
                    invate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        };
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            pagerWidth = r - l;
            pagerHeight = b - t;
        }
    }


    public void setHorizontal(boolean horizontal) {
        try {
            valueAnimator.end();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.item = 0;
        this.horizontal = horizontal;
        this.requestLayout();
        try {
            distence = 0;
            invate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getUserName() {
        userNameIdx++;
        if (userNameIdx >= userNames.length) {
            userNameIdx = 0;
        }
        return userNames[userNameIdx];
    }

    private String getPayment() {
        int idx = new Random().nextInt(4);
        return payments[idx];
    }

    public void setTextId(@StringRes int d) {
        this.textId = d;
    }


    private int textId = 0;
    private int userNameIdx;

    public void invate() throws Exception {
//        Log.i("ds", "distence:" + distence);
        recycleAndAddView();
        if (horizontal) {
            LoopViewPager.this.scrollTo(distence, 0);
        } else {
            LoopViewPager.this.scrollTo(0, distence);
        }
    }


    private void recycleAndAddView() {
        int next_position = 0;
        int nextItem = 0;//下一个
        int thisItem = 0;

        int pagerLength = 0;
        if (horizontal) {
            pagerLength = pagerWidth;
        } else {
            pagerLength = pagerHeight;
        }
        if (distence > 0) {
            next_position = distence / pagerLength + 1;
            nextItem = Math.abs(next_position % viewList.size());

            thisItem = nextItem - 1;
            if (thisItem < 0) {
                thisItem = viewList.size() - 1;
            }
            if (thisItem > (viewList.size() - 1)) {
                thisItem = 0;
            }
            try {
                this.addView(viewList.get(thisItem), layoutParams);
            } catch (Exception e) {
            }
            viewList.get(thisItem).bringToFront();
            if (horizontal) {
                viewList.get(thisItem).setX((next_position - 1) * pagerLength);
                viewList.get(thisItem).setY(0);
            } else {
                viewList.get(thisItem).setY((next_position - 1) * pagerLength);
                viewList.get(thisItem).setX(0);
            }


            try {
                this.addView(viewList.get(nextItem), layoutParams);
            } catch (Exception e) {
            }
            viewList.get(nextItem).bringToFront();
            if (horizontal) {
                viewList.get(nextItem).setX(next_position * pagerLength);
                viewList.get(nextItem).setY(0);
            } else {
                viewList.get(nextItem).setY(next_position * pagerLength);
                viewList.get(nextItem).setX(0);
            }
        } else if (distence < 0) {
            next_position = distence / pagerLength - 1;
            nextItem = Math.abs(next_position % viewList.size());
            thisItem = nextItem - 1;
            if (thisItem < 0) {
                thisItem = viewList.size() - 1;
            }
            if (thisItem > (viewList.size() - 1)) {
                thisItem = 0;
            }
            try {
                this.addView(viewList.get(thisItem), layoutParams);
            } catch (Exception e) {
            }
            viewList.get(thisItem).bringToFront();
            if (horizontal) {
                viewList.get(thisItem).setX((next_position + 1) * pagerLength);
                viewList.get(thisItem).setY(0);
            } else {
                viewList.get(thisItem).setY((next_position + 1) * pagerLength);
                viewList.get(thisItem).setX(0);
            }

            try {
                this.addView(viewList.get(nextItem), layoutParams);
            } catch (Exception e) {
            }
            viewList.get(nextItem).bringToFront();
            if (horizontal) {
                viewList.get(nextItem).setX(next_position * pagerLength);
                viewList.get(nextItem).setY(0);
            } else {
                viewList.get(nextItem).setY(next_position * pagerLength);
                viewList.get(nextItem).setX(0);
            }
        } else {
            next_position = 0;
            nextItem = 0;
            thisItem = 0;
            try {
                this.addView(viewList.get(0), layoutParams);
            } catch (Exception e) {
            }
            viewList.get(nextItem).bringToFront();
            viewList.get(0).setX(0);
            viewList.get(0).setY(0);
        }

        if (distence >= 0) {
            position = distence / pagerLength;
        } else {
            position = distence / pagerLength - 1;
        }
        item = thisItem;
        /*回收*/
        for (int i = 0; i < viewList.size(); i++) {
            if (i != nextItem && i != thisItem) {
                try {
                    this.removeView(viewList.get(i));
                } catch (Exception e) {
                }
            }
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        try {
//            if (viewList.size() <= 1) {
//                return super.dispatchTouchEvent(ev);
//            }
//        } catch (Exception e) {
//        }
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            touching = true;
//            if (horizontal) {
//                last_touch_point = (int) ev.getX();
//            } else {
//                last_touch_point = (int) ev.getY();
//            }
//
//            try {
//                setAuto(false);
//            } catch (Exception e) {
//            }
//        }
//        mGestureDetector.onTouchEvent(ev);
//        if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
//            touching = false;
//            if (Math.abs(ev.getX() - last_touch_point) > 10) {
//                if (horizontal) {
//                    if (ev.getX() - last_touch_point < 0) {//left
//                        AnimationTo(getScrollX(), (position + 1) * pagerWidth);
//                    } else {//right
//                        AnimationTo(getScrollX(), (position) * pagerWidth);
//                    }
//                } else {
//                    if (ev.getY() - last_touch_point < 0) {//
//                        AnimationTo(getScrollY(), (position + 1) * pagerHeight);
//                    } else {//right
//                        AnimationTo(getScrollY(), (position) * pagerHeight);
//                    }
//                }
//            } else {
//                backToAutoChange();
//            }
//        }
//        if (Math.abs(ev.getX() - last_touch_point) > 20 && horizontal == true) {
//            return true;
//        } else if (Math.abs(ev.getY() - last_touch_point) > 20 && horizontal == false) {
//            return true;
//        } else {
        return super.dispatchTouchEvent(ev);
//        }
    }

    private void backToAutoChange() {
        if (autoChange) {
            try {
                setAuto(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }


    private void AnimationTo(int from, int to) {
        if (from == to) {
            return;
        }
        try {
            valueAnimator = ValueAnimator.ofInt(from, to);
            valueAnimator.setDuration(500);
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    String username = getUserName();
                    String payment = getPayment();
                    String tt = getContext().getString(textId, username, payment);
                    if (item == 0) {
                        formatTextStyleWithSizeAndColor(getContext(), textView1, 1.2f, username, payment, tt, 0);
                    } else {
                        formatTextStyleWithSizeAndColor(getContext(), textView0, 1.2f, username, payment, tt, 0);
                    }
                    backToAutoChange();
                    if (onItemSelectedListener != null) {
                        onItemSelectedListener.selected(item, viewList.get(item));
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        if (touching == false) {
                            distence = (Integer) animation.getAnimatedValue();
                            invate();
                        }
                    } catch (Exception e) {
                    }
                }
            });
            valueAnimator.start();
        } catch (Exception e) {
        }
    }


    public void setCurrentItem(int item) {
        this.item = item;
        try {
            invate();
        } catch (Exception e) {
        }
    }

    public int getItem() {
        return item;
    }

    private void setAuto(boolean auto) throws Exception {
        loopHandler.setLoop(auto);
    }

    private void stopLoop() {
        loopHandler.setLoop(false);
    }

    public long getAutoChangeTime() {
        return autoChangeTime;
    }

    public void setAutoChangeTime(long autoChangeTime) {
        this.autoChangeTime = autoChangeTime;
        loopHandler.setLoopTime(autoChangeTime);
    }

    public boolean isAutoChange() {
        return autoChange;
    }

    public void setAutoChange(boolean change) {
        if (viewList.size() < 1) {
            try {
                setAuto(false);
            } catch (Exception e) {
            }
        } else {
            autoChange = change;
            try {
                setAuto(change);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public List<View> getViewList() {
        return viewList;
    }

    public void setViewList(List<View> viewList, String[] userNames, String[] payments) {
        try {
            setAutoChange(false);
        } catch (Exception e) {
        }
        try {
            removeAllViews();
        } catch (Exception e) {
        }
        this.viewList = viewList;
        this.userNames = userNames;
        this.payments = payments;
        textView0 = (TextView) viewList.get(0).findViewById(R.id.text);
        textView1 = (TextView) viewList.get(1).findViewById(R.id.text);
        String username = getUserName();
        String payment = getPayment();
        String tt = getContext().getString(textId, username, payment);
        String username1 = getUserName();
        String payment1 = getPayment();
        String tt1 = getContext().getString(textId, username1, payment1);
        formatTextStyleWithSizeAndColor(getContext(), textView0, 1.2f, username, payment, tt, 0);
        formatTextStyleWithSizeAndColor(getContext(), textView1, 1.2f, username1, payment1, tt1, 0);
        try {
            invate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (loopHandler != null) {
            loopHandler.setLoop(false);
        }
    }

    public void formatTextStyleWithSizeAndColor(Context context, TextView tv, float size, String text0, String text1, String text, int colorId) {
        if (tv == null || context == null) {
            return;
        }
        if (!TextUtils.isEmpty(text)) {
            SpannableString ss1 = new SpannableString(text);
            int index = text.indexOf(text0);
            int index1 = text.indexOf(text1);
            if (index == -1) {
                return;
            }
            if (!TextUtils.isEmpty(ss1) && size > 0) {
                ss1.setSpan(new RelativeSizeSpan(size), index, index + text0.length(), 0);
                ss1.setSpan(new RelativeSizeSpan(size), index1, index1 + text1.length(), 0);
            }
            SpannableStringBuilder builder = new SpannableStringBuilder(ss1);
            if (colorId != -1) {
//                ForegroundColorSpan color0 = new ForegroundColorSpan(ContextCompat.getColor(context, colorId));
//                ForegroundColorSpan color1 = new ForegroundColorSpan(ContextCompat.getColor(context, colorId));
                builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), index, index + text0.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), index1, index1 + text1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            tv.setText(builder);
        }
    }

}
