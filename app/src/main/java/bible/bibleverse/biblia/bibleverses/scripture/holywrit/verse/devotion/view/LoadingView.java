package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import yuku.afw.V;

/**
 * Created by xiao on 2016/11/18.
 */

public class LoadingView extends FrameLayout {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private View mLoadingCircleView;
    private Animation mRotateAnim;


    public LoadingView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (changedView == this && mLoadingCircleView != null) {
            if (visibility == VISIBLE) {
                if (mLoadingCircleView.getAnimation() == null) {
                    mLoadingCircleView.startAnimation(mRotateAnim);
                }
            } else {
                mLoadingCircleView.clearAnimation();
                mRotateAnim.reset();
            }
        }
    }

    private void init() {
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewRoot = mLayoutInflater.inflate(R.layout.loading_layout, null);
        addView(viewRoot);
        mLoadingCircleView = V.get(viewRoot, R.id.loading_circle);
        mRotateAnim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnim.setRepeatCount(Animation.INFINITE);
        mRotateAnim.setDuration(1000);
        mRotateAnim.setInterpolator(new LinearInterpolator());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void stopLoading() {
        if (mLoadingCircleView != null && mLoadingCircleView.getAnimation() != null) {
            mLoadingCircleView.clearAnimation();
        }
    }
}
