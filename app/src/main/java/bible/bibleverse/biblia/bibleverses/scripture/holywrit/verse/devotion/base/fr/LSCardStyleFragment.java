package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;

import java.util.Random;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter.ViewPagerCardAdapter;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.view.CustomViewPager;

/**
 * Created by zhangfei on 3/3/17.
 */

public class LSCardStyleFragment extends Fragment {
    private CustomViewPager mRecyclerView;
    private ViewPagerCardAdapter mViewPagerCardAdapter;
    private float mScaleRadio;
    private boolean mIsHideDevotionCard = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ls_bible_card, container, false);
        mRecyclerView = (CustomViewPager) view.findViewById(R.id.list);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initAdapter();
    }

    private void initAdapter() {
        mScaleRadio = 0.90f;
        Random random = new Random();
        int index = random.nextInt(10);
        LinearLayoutManager layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layout);
        mViewPagerCardAdapter = new ViewPagerCardAdapter(getActivity(), mRecyclerView);
        mRecyclerView.setAdapter(mViewPagerCardAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLongClickable(true);
        mRecyclerView.setSinglePageFling(true);
        initScrollEvent();
        mViewPagerCardAdapter.loadDevotionData(index < 3 ? true : false);
    }

    private void initScrollEvent() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                try {
                    int childCount = mRecyclerView.getChildCount();
                    int width = mRecyclerView.getChildAt(0).getWidth();
                    int padding = (mRecyclerView.getWidth() - width) / 2;

                    for (int j = 0; j < childCount; j++) {
                        View v = recyclerView.getChildAt(j);
                        float rate = 0;
                        if (v == null) {
                            continue;
                        }
                        if (v.getLeft() <= padding) {
                            if (v.getLeft() >= padding - v.getWidth()) {
                                rate = (padding - v.getLeft()) * 1f / v.getWidth();
                            } else {
                                rate = 1;
                            }
                            v.setScaleY(1 - rate * (1 - mScaleRadio));
                            v.setScaleX(1 - rate * (1 - mScaleRadio));

                        } else {
                            if (v.getLeft() <= recyclerView.getWidth() - padding) {
                                rate = (recyclerView.getWidth() - padding - v.getLeft()) * 1f / v.getWidth();
                            }
                            v.setScaleY(mScaleRadio + rate * (1 - mScaleRadio));
                            v.setScaleX(mScaleRadio + rate * (1 - mScaleRadio));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mRecyclerView.addOnPageChangedListener(new RecyclerViewPager.OnPageChangedListener() {
            @Override
            public void OnPageChanged(int oldPosition, int newPosition) {
            }
        });

        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                try {
                    if (mRecyclerView.getChildCount() < 3) {
                        if (mRecyclerView.getChildAt(1) != null) {
                            if (mRecyclerView.getCurrentPosition() == 0) {
                                View v1 = mRecyclerView.getChildAt(1);
                                v1.setScaleY(mScaleRadio);
                                v1.setScaleX(mScaleRadio);
                            } else {
                                View v1 = mRecyclerView.getChildAt(0);
                                v1.setScaleY(mScaleRadio);
                                v1.setScaleX(mScaleRadio);
                            }
                        }
                    } else {
                        if (mRecyclerView.getChildAt(0) != null) {
                            View v0 = mRecyclerView.getChildAt(0);
                            v0.setScaleY(mScaleRadio);
                            v0.setScaleX(mScaleRadio);
                        }
                        if (mRecyclerView.getChildAt(2) != null) {
                            View v2 = mRecyclerView.getChildAt(2);
                            v2.setScaleY(mScaleRadio);
                            v2.setScaleX(mScaleRadio);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mViewPagerCardAdapter != null) {
            mViewPagerCardAdapter.cancleTask();
        }
    }
}
