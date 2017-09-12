package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter.ViewPagerCardAdapter;

/**
 * Created by zhangfei on 11/12/16.
 */
public class CustomViewPager extends RecyclerViewPager {
    private boolean isPagingEnabled = true;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        handleInterceptTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        handleInterceptTouchEvent(event);
        return super.onInterceptTouchEvent(event);
    }

    private void handleInterceptTouchEvent(MotionEvent event) {
        if (getCurrentPosition() == 0) {
            RecyclerView.ViewHolder viewHolder = findViewHolderForLayoutPosition(0);
            if (viewHolder instanceof ViewPagerCardAdapter.BaseAdItemHolder) {
                ViewPagerCardAdapter.BaseAdItemHolder baseAdItemHolder = (ViewPagerCardAdapter.BaseAdItemHolder) viewHolder;
            }
        } else {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

}
