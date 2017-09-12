package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.ads.MediaView;
import com.fw.basemodules.utils.OmAsyncTask;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.NewAnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.NewAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.BaseRetrofit;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.DevotionService;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.VerseService;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.DevotionBean;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.DevotionListResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.VerseListResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.DailyVerseListActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.DevotionAllListActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.DevotionDetailWebActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.DevotionSitesDetailActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.DevotionSitesGuideActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.NewDailyVerseDetailActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.NewMainActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.PrayerCategoryGridActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter.viewholder.ViewHolderDailyVerse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter.viewholder.ViewHolderDevotionBigImage;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter.viewholder.ViewHolderDevotionNoImage;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter.viewholder.ViewHolderDevotionSmallImage;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter.viewholder.ViewHolderLockScreenGuide;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter.viewholder.ViewHolderSeparator;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.storage.Db;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.storage.Prefkey;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.DateTimeUtil;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import jp.wasabeef.blurry.Blurry;
import me.itangqi.waveloadingview.WaveLoadingView;
import retrofit2.Call;
import retrofit2.Response;
import yuku.afw.V;
import yuku.afw.storage.Preferences;
import yuku.alkitab.util.Ari;
import yuku.alkitabintegration.display.Launcher;


/**
 * Created by yzq on 2017/2/21.
 */

public class HomeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int ITEM_FUNCTION_ENTER = 100;
    //    public static final int ITEM_DOWNLOAD_NOTICE = 102;
    public static final int ITEM_DAILY_VERSE_HEADER = 200;
    public static final int ITEM_DAILY_VERSE = 201;


    public static final int ITEM_DAILY_DEVOTION_HEADER = 300;
    public static final int ITEM_DAILY_DEVOTION_START = 301;
    public static final int ITEM_DAILY_DEVOTION_END = 399;
    public static final int ITEM_END = 9999;
    public static final int ITEM_AD_POSITION_IN_DEVOTIONS = 1;

    public static final int TYPE_SMALL_IMAGE = 1301;
    public static final int TYPE_NO_IMAGE = 1302;
    public static final int TYPE_BIG_IMAGE = 1303;
    public static final int TYPE_LOCK_SCREEN = 1304;

    private static final int TARGET_PRAY_COUNT = 2;
    private static final long TARGET_READ_TIME = 20 * 60 * 1000;

    private LayoutInflater mInflater;
    private ArrayList<Integer> mItems;
    private Context mContext;

    private int mLastedAri, mLastBookId, mLastChapter, mLastVerse;
    private List<DevotionBean> mDevotions;
    private VerseListResponse.DataBean.VerseBean mDailyVerse;
    private HashSet<Integer> mReadDevotionIds;
    private OmAsyncTask mDevotionTask, mUserTask, mVerseTask;

    private String mCurrentDate;

    private int mTopAdImgWidth, mImgWidth;

    private String mAdKey;
    private View mAdRoot;
    private boolean mAdIsCanRegister = false;


    private boolean isShowLockReminderInDevotion = true;

    // function entry view and anim
    private RotateAnimation mAnim4NewDevotion;
    private long mTodayReadVerseCount, mTodayReadMilliseconds, mTodayPrayCount, mTodayUpdateDevotionCount;
    private int[] mTodayYMD;
    private boolean mDoneShine4Pray, mDoingShine4Pray, mDoneShine4Read, mDoingShine4Read;


    public HomeListAdapter(Context context, LayoutInflater inflater) {
        super();
        mContext = context;
        mInflater = inflater;
        mItems = new ArrayList<>();
        mItems.add(ITEM_FUNCTION_ENTER);
        mItems.add(ITEM_END);

        mDevotions = new ArrayList<>();
        mReadDevotionIds = new HashSet<>();
        mCurrentDate = DateTimeUtil.getDateStr4ApiRequest(System.currentTimeMillis());
        mTodayYMD = DateTimeUtil.getTodayYMD();
        mDoneShine4Pray = false;
        mDoneShine4Read = false;
        mDoingShine4Pray = false;
        mDoingShine4Read = false;

        mTopAdImgWidth = context.getResources().getDisplayMetrics().widthPixels;
        mImgWidth = mTopAdImgWidth - context.getResources().getDimensionPixelSize(R.dimen.margin_36);
    }



    public void loadItemData() {
        //verse
        mVerseTask = new LoadDailyVerseData();
        mVerseTask.execute();

        //devotion
        mDevotionTask = new LoadDevotionData();
        mDevotionTask.execute();

        mTodayUpdateDevotionCount = 0;
        mTodayReadMilliseconds = 0;
        mTodayPrayCount = 0;
        mTodayReadVerseCount = 0;
        mUserTask = new LoadUserOperationData();
        mUserTask.execute();
    }

    public void addItem(int item, boolean sort) {
        if (mItems == null) {
            mItems = new ArrayList<>();
        }
        if (!mItems.contains(item)) {
            mItems.add(item);
        }
        if (sort) {
            sortListItems();
        }
        notifyDataSetChanged();
    }

    public void removeItem(int item) {
        if (mItems == null) {
            mItems = new ArrayList<>();
        }
        mItems.remove(new Integer(item));
        notifyDataSetChanged();
    }

    public void clearItems() {
        mItems.clear();
    }

    private void sortListItems() {
        Collections.sort(mItems, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return lhs.compareTo(rhs);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems == null || position >= mItems.size()) {
            return -1;
        }
        int item = mItems.get(position);
        if (item >= ITEM_DAILY_DEVOTION_START && item <= ITEM_DAILY_DEVOTION_END) {
            int idx = item - ITEM_DAILY_DEVOTION_START;
            final DevotionBean devotion = (idx < mDevotions.size()) ? mDevotions.get(idx) : null;
            if (devotion != null) {
                if (devotion.getId() == DevotionBean.ITEM_LOCK_REMINDER_ID) {
                    return TYPE_LOCK_SCREEN;
                } else {
                    if (devotion.getType() == 1) {
                        return TYPE_SMALL_IMAGE;
                    } else if (devotion.getType() == 2) {
                        return TYPE_NO_IMAGE;
                    } else if (devotion.getType() == 3) {
                        return TYPE_BIG_IMAGE;
                    } else {
                        return TYPE_SMALL_IMAGE;
                    }
                }
            }
        }

        return item;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder holder = null;

        switch (viewType) {
            case ITEM_FUNCTION_ENTER:
                view = mInflater.inflate(R.layout.home_item_functon_entry, parent, false);
                holder = new ViewHolderFunctionEntry(view);
                break;
            case ITEM_DAILY_DEVOTION_HEADER:
            case ITEM_DAILY_VERSE_HEADER:
                holder = ViewHolderSeparator.createViewHolder(mInflater, parent);
                break;
            case ITEM_DAILY_VERSE:
                holder = ViewHolderDailyVerse.createViewHolder(mInflater, parent);
                break;
            case TYPE_NO_IMAGE:
                holder = ViewHolderDevotionNoImage.createViewHolder(mInflater, parent);
                break;
            case TYPE_SMALL_IMAGE:
                holder = ViewHolderDevotionSmallImage.createViewHolder(mInflater, parent);
                break;
            case TYPE_BIG_IMAGE:
                holder = ViewHolderDevotionBigImage.createViewHolder(mInflater, parent);
                break;
            case TYPE_LOCK_SCREEN:
                view = mInflater.inflate(R.layout.home_item_lockscreen_guide, parent, false);
                holder = new ViewHolderLockScreenGuide(view);
                break;
            case ITEM_END:
                view = mInflater.inflate(R.layout.home_item_end, parent, false);
                holder = new ViewHolderEnd(view);
                break;
            default:
                view = mInflater.inflate(R.layout.home_item_devotion_no_image, parent, false);
                holder = new ViewHolderDevotionNoImage(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder == null) {
            return;
        }
        int type = getItemViewType(position);
        switch (type) {
            case ITEM_FUNCTION_ENTER:
                final ViewHolderFunctionEntry holderFunctionEntry = (ViewHolderFunctionEntry) holder;
                bindFunctionEntryView(holderFunctionEntry);
                break;
            case ITEM_DAILY_DEVOTION_HEADER:
            case ITEM_DAILY_VERSE_HEADER:
                final ViewHolderSeparator holderSeparator = (ViewHolderSeparator) holder;
                bindSeparatorView(holderSeparator, type);
                break;
            case ITEM_DAILY_VERSE:
                final ViewHolderDailyVerse viewHolderDailyVerse = (ViewHolderDailyVerse) holder;
                bindDailyVerse(viewHolderDailyVerse);
                break;
            case TYPE_NO_IMAGE:
                final ViewHolderDevotionNoImage holderDevotionNoImage = (ViewHolderDevotionNoImage) holder;
                bindDevotionNoImageView(holderDevotionNoImage, position);
                break;
            case TYPE_SMALL_IMAGE:
                final ViewHolderDevotionSmallImage holderDevotionSmallImage = (ViewHolderDevotionSmallImage) holder;
                bindDevotionSmallImageView(holderDevotionSmallImage, position);
                break;
            case TYPE_BIG_IMAGE:
                final ViewHolderDevotionBigImage holderDevotionBigImage = (ViewHolderDevotionBigImage) holder;
                bindDevotionBigImageView(holderDevotionBigImage, position);
                break;
            case TYPE_LOCK_SCREEN:
                final ViewHolderLockScreenGuide holderLockScreenGuide = (ViewHolderLockScreenGuide) holder;
                bindLockScreenGuideView(holderLockScreenGuide);
                break;
            case ITEM_END:
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    private void fadeOutView(final View view, final boolean is4Pray) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(1000);
        fadeOut.setDuration(1000);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
                if (is4Pray) {
                    mDoneShine4Pray = true;
                    mDoingShine4Pray = false;
                } else {
                    mDoneShine4Read = true;
                    mDoingShine4Read = false;
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(fadeOut);
    }

    private void bindFunctionEntryView(final ViewHolderFunctionEntry holderFunctionEntry) {
        // devotion
        if (mTodayUpdateDevotionCount > 0) {
            holderFunctionEntry.devotionUpdateCounter.setVisibility(View.VISIBLE);
            holderFunctionEntry.devotionInfoLayout.setVisibility(View.VISIBLE);
            holderFunctionEntry.devotionTvCenter.setText(String.valueOf(mTodayUpdateDevotionCount));

            mAnim4NewDevotion = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            mAnim4NewDevotion.setDuration(15000);
            mAnim4NewDevotion.setRepeatCount(Animation.INFINITE);
            mAnim4NewDevotion.setInterpolator(new LinearInterpolator());
            holderFunctionEntry.devotionUpdateCounter.startAnimation(mAnim4NewDevotion);

        } else {
            if (mAnim4NewDevotion != null && !mAnim4NewDevotion.hasEnded()) {
                mAnim4NewDevotion.cancel();
            }
            holderFunctionEntry.devotionUpdateCounter.clearAnimation();
            holderFunctionEntry.devotionUpdateCounter.setVisibility(View.GONE);
            holderFunctionEntry.devotionInfoLayout.setVisibility(View.GONE);
        }
        holderFunctionEntry.devotionItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_HOME_NAV_DEVOTION, "click");
                mContext.startActivity(new Intent(mContext, Utility.isRecommendDevotionSite(mContext) ? DevotionSitesGuideActivity.class : DevotionAllListActivity.class));
                mTodayUpdateDevotionCount = 0;
                notifyDataSetChanged();
            }
        });

        // pray
        holderFunctionEntry.prayerItem.setVisibility(View.VISIBLE);
        if ((mTodayPrayCount >= TARGET_PRAY_COUNT)) {
            holderFunctionEntry.prayWaveView.setVisibility(View.GONE);
            holderFunctionEntry.prayTvBottom.setVisibility(View.GONE);
            holderFunctionEntry.prayTvCenter.setVisibility(View.GONE);
            if (!mDoneShine4Pray) {
                if (!mDoingShine4Pray) {
                    mDoingShine4Pray = true;
                    holderFunctionEntry.prayerInfoLayout.setVisibility(View.VISIBLE);
                    holderFunctionEntry.prayShineButton.setVisibility(View.VISIBLE);
                    holderFunctionEntry.prayShineButton.setAnimListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            fadeOutView(holderFunctionEntry.prayerInfoLayout, true);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holderFunctionEntry.prayShineButton.startAnim();
                        }
                    }, 1500);

                }

            } else {
                holderFunctionEntry.prayerInfoLayout.setVisibility(View.GONE);
            }
        } else {
            holderFunctionEntry.prayerInfoLayout.setVisibility(View.VISIBLE);
            holderFunctionEntry.prayWaveView.setVisibility(View.VISIBLE);
            holderFunctionEntry.prayTvBottom.setVisibility(View.VISIBLE);
            holderFunctionEntry.prayTvCenter.setVisibility(View.VISIBLE);
            holderFunctionEntry.prayTvCenter.setText(String.valueOf(mTodayPrayCount));
            if (mTodayPrayCount < 2) {
                holderFunctionEntry.prayTvBottom.setText(R.string.time);
            } else {
                holderFunctionEntry.prayTvBottom.setText(R.string.times);
            }
            int progress = (int) (mTodayPrayCount * 100 / TARGET_PRAY_COUNT);
            progress = (progress < 20) ? 20 : progress;
            holderFunctionEntry.prayWaveView.setProgressValue(progress);
        }
        holderFunctionEntry.prayerItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_HOME_NAV_PRAYER, "click");
                mContext.startActivity(new Intent(mContext, PrayerCategoryGridActivity.class));
            }
        });

        // verse
        if (mTodayReadVerseCount > 0) {
            holderFunctionEntry.verseInfoLayout.setVisibility(View.GONE);
        } else {
            holderFunctionEntry.verseInfoLayout.setVisibility(View.VISIBLE);
            holderFunctionEntry.verseTvCenter.setText(String.valueOf(mTodayYMD[2]));
            holderFunctionEntry.verseTvBottom.setText(DateTimeUtil.getShortMonthName(mTodayYMD[1]));
        }
        holderFunctionEntry.verseItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_HOME_NAV_VERSE, "click");
                mContext.startActivity(new Intent(mContext, DailyVerseListActivity.class));
            }
        });

        // read
        if (mTodayReadMilliseconds >= TARGET_READ_TIME) {
            holderFunctionEntry.readWaveView.setVisibility(View.GONE);
            holderFunctionEntry.readTvBottom.setVisibility(View.GONE);
            holderFunctionEntry.readTvCenter.setVisibility(View.GONE);
            if (!mDoneShine4Read) {
                if (!mDoingShine4Read) {
                    mDoingShine4Read = true;
                    holderFunctionEntry.readInfoLayout.setVisibility(View.VISIBLE);
                    holderFunctionEntry.readShineButton.setVisibility(View.VISIBLE);
                    holderFunctionEntry.readShineButton.setAnimListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            fadeOutView(holderFunctionEntry.readInfoLayout, false);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holderFunctionEntry.readShineButton.startAnim();
                        }
                    }, 1500);
                }
            } else {
                holderFunctionEntry.prayerInfoLayout.setVisibility(View.GONE);
            }
        } else {
            holderFunctionEntry.readInfoLayout.setVisibility(View.VISIBLE);
            holderFunctionEntry.readWaveView.setVisibility(View.VISIBLE);
            holderFunctionEntry.readTvBottom.setVisibility(View.VISIBLE);
            holderFunctionEntry.readTvCenter.setVisibility(View.VISIBLE);
            String[] timeAndUnit = DateTimeUtil.getTimeAndUnitFromMillisecond(mTodayReadMilliseconds);
            holderFunctionEntry.readTvCenter.setText(timeAndUnit[0]);
            holderFunctionEntry.readTvBottom.setText(timeAndUnit[1]);
            int progress = (int) (mTodayReadMilliseconds * 100 / TARGET_READ_TIME);
            progress = (progress < 20) ? 20 : progress;
            holderFunctionEntry.readWaveView.setProgressValue(progress);
        }
        holderFunctionEntry.readItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_HOME_NAV_READ, "click");
                mContext.startActivity(Launcher.openAppAtBibleLocationNonVerseSelected(mLastedAri));
            }
        });
    }


    private void bindSeparatorView(ViewHolderSeparator holderSeparator, int type) {
        if (type == ITEM_DAILY_DEVOTION_HEADER) {
            holderSeparator.tvTitle.setText(R.string.dr_notification_title);
        } else if (type == ITEM_DAILY_VERSE_HEADER) {
            holderSeparator.tvTitle.setText(R.string.verse_of_day);
        }
    }

    private void addReadDevotionId(int id) {
        mReadDevotionIds.add(id);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        }, 500);
    }

    private void bindDevotionNoImageView(final ViewHolderDevotionNoImage holderDevotionNoImage, int position) {
        int idx = mItems.get(position) - ITEM_DAILY_DEVOTION_START;
        final DevotionBean devotion = (idx < mDevotions.size()) ? mDevotions.get(idx) : null;
        if (devotion != null) {
            holderDevotionNoImage.bindHomeDevotion(
                    mContext,
                    devotion,
                    mReadDevotionIds.contains(devotion.getId()),
                    mCurrentDate,
                    (idx + 1) == mDevotions.size()
            );

            holderDevotionNoImage.contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = DevotionDetailWebActivity.createIntent(mContext, devotion);
                    mContext.startActivity(intent);

                    addReadDevotionId(devotion.getId());
                }
            });
        }
    }

    private void bindDevotionSmallImageView(final ViewHolderDevotionSmallImage holderDevotionSmallImage, int position) {
        int idx = mItems.get(position) - ITEM_DAILY_DEVOTION_START;
        final DevotionBean devotion = (idx < mDevotions.size()) ? mDevotions.get(idx) : null;
        if (devotion != null) {
            holderDevotionSmallImage.bindHomeDevotion(
                    mContext,
                    devotion,
                    mReadDevotionIds.contains(devotion.getId()),
                    mCurrentDate,
                    (idx + 1) == mDevotions.size()
            );

            holderDevotionSmallImage.contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = DevotionDetailWebActivity.createIntent(mContext, devotion);
                    mContext.startActivity(intent);

                    addReadDevotionId(devotion.getId());
                }
            });
        }
    }

    private void bindDevotionBigImageView(final ViewHolderDevotionBigImage holderDevotionBigImage, int position) {
        int idx = mItems.get(position) - ITEM_DAILY_DEVOTION_START;
        final DevotionBean devotion = (idx < mDevotions.size()) ? mDevotions.get(idx) : null;
        if (devotion != null) {
            holderDevotionBigImage.bindHomeDevotion(
                    mContext,
                    devotion,
                    mReadDevotionIds.contains(devotion.getId()),
                    mCurrentDate,
                    (idx + 1) == mDevotions.size()
            );

            holderDevotionBigImage.contentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = DevotionDetailWebActivity.createIntent(mContext, devotion);
                    mContext.startActivity(intent);

                    addReadDevotionId(devotion.getId());
                }
            });
        }
    }

    private void bindLockScreenGuideView(final ViewHolderLockScreenGuide holderLockScreenGuide) {
        holderLockScreenGuide.bindLockScreenView(mContext, mImgWidth);
        holderLockScreenGuide.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holderLockScreenGuide.tvOpen.setText(R.string.enabled_successfully);
                removeLockScreenViewAnimation(holderLockScreenGuide.rootView);
            }
        });
    }

    private void removeLockScreenViewAnimation(View view) {
        if (view != null) {
            final Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_out_left);
            animation.setStartOffset(200);
            animation.setStartOffset(1000);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mDevotions.remove(ITEM_AD_POSITION_IN_DEVOTIONS);
                    mItems.remove(mItems.size() - 1);
                    notifyDataSetChanged();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            view.startAnimation(animation);
        } else {
            mDevotions.remove(ITEM_AD_POSITION_IN_DEVOTIONS);
            mItems.remove(mItems.size() - 1);
            notifyDataSetChanged();
        }
    }


    private void bindDailyVerse(ViewHolderDailyVerse viewHolderDailyVerse) {
        if (mDailyVerse == null) {
            viewHolderDailyVerse.rootView.setVisibility(View.GONE);
            return;
        }

        Picasso.with(mContext).load(mDailyVerse.getImageUrl()).into(viewHolderDailyVerse.image);
        viewHolderDailyVerse.tvQuote.setText(mDailyVerse.getQuote());
        viewHolderDailyVerse.tvQuoteRefer.setText(mDailyVerse.getQuoteRefer());
        viewHolderDailyVerse.tvLikeNum.setText(String.valueOf(mDailyVerse.getLike()));
        viewHolderDailyVerse.tvShareNum.setText(String.valueOf(mDailyVerse.getShare()));
        viewHolderDailyVerse.tvReadAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(NewDailyVerseDetailActivity.createIntent(mContext, mDailyVerse.getId(), mDailyVerse.getQuote(), mDailyVerse.getQuoteRefer(), mDailyVerse.getImageUrl()));
            }
        });
        viewHolderDailyVerse.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(NewDailyVerseDetailActivity.createIntent(mContext, mDailyVerse.getId(), mDailyVerse.getQuote(), mDailyVerse.getQuoteRefer(), mDailyVerse.getImageUrl()));
            }
        });
        if (mTodayReadVerseCount > 0) {
            viewHolderDailyVerse.guideArrow.setVisibility(View.GONE);
            if (arrowHandler != null && arrowRunnable != null) {
                arrowHandler.removeCallbacks(arrowRunnable);
            }
        } else {
            viewHolderDailyVerse.guideArrow.setVisibility(View.VISIBLE);
            arrowHandler = new Handler();
            arrow(viewHolderDailyVerse);
        }
    }

    int start = 3;
    Handler arrowHandler;
    private Runnable arrowRunnable;

    private void arrow(final ViewHolderDailyVerse viewHolderDailyVerse) {
        arrowRunnable = new Runnable() {
            @Override
            public void run() {
                int sub = start % 3;
                if (sub == 0) {
                    viewHolderDailyVerse.arrow1.setAlpha(0.3f);
                    viewHolderDailyVerse.arrow2.setAlpha(0.6f);
                    viewHolderDailyVerse.arrow3.setAlpha(1f);
                } else if (sub == 1) {
                    viewHolderDailyVerse.arrow1.setAlpha(1f);
                    viewHolderDailyVerse.arrow2.setAlpha(0.3f);
                    viewHolderDailyVerse.arrow3.setAlpha(0.3f);
                } else if (sub == 2) {
                    viewHolderDailyVerse.arrow1.setAlpha(0.3f);
                    viewHolderDailyVerse.arrow2.setAlpha(1f);
                    viewHolderDailyVerse.arrow3.setAlpha(0.6f);
                }

                start++;
                if (start >= 9) {
                    start = 3;
                }
                arrowHandler.removeCallbacks(this);
                arrowHandler.postDelayed(arrowRunnable, 300);
            }
        };
        arrowHandler.post(arrowRunnable);
    }


    private class ViewHolderFunctionEntry extends RecyclerView.ViewHolder {
        // devotion
        View devotionItem, devotionInfoLayout;
        TextView devotionTvCenter;
        View devotionUpdateCounter;
        // pray
        View prayerItem, prayerInfoLayout;
        ShineButton prayShineButton;
        WaveLoadingView prayWaveView;
        TextView prayTvCenter, prayTvBottom;
        // verse
        View verseItem, verseInfoLayout;
        TextView verseTvCenter, verseTvBottom;
        // read
        View readItem, readInfoLayout;
        ShineButton readShineButton;
        WaveLoadingView readWaveView;
        TextView readTvCenter, readTvBottom;


        public ViewHolderFunctionEntry(View itemView) {
            super(itemView);
            devotionItem = V.get(itemView, R.id.devotion_item);
            devotionInfoLayout = V.get(itemView, R.id.devotion_info_layout);
            devotionTvCenter = V.get(itemView, R.id.devotion_text_center);
            devotionUpdateCounter = V.get(itemView, R.id.devotion_updated_contour);

            prayerItem = V.get(itemView, R.id.prayer_item);
            prayerInfoLayout = V.get(itemView, R.id.pray_info_layout);
            prayShineButton = V.get(itemView, R.id.prayer_shine_button);
            prayWaveView = V.get(itemView, R.id.pray_wave_view);
            prayTvCenter = V.get(itemView, R.id.pray_text_center);
            prayTvBottom = V.get(itemView, R.id.pray_text_bottom);

            verseItem = V.get(itemView, R.id.verse_item);
            verseInfoLayout = V.get(itemView, R.id.verse_info_layout);
            verseTvCenter = V.get(itemView, R.id.verse_text_center);
            verseTvBottom = V.get(itemView, R.id.verse_text_bottom);

            readItem = V.get(itemView, R.id.read_item);
            readInfoLayout = V.get(itemView, R.id.read_info_layout);
            readWaveView = V.get(itemView, R.id.read_wave_view);
            readShineButton = V.get(itemView, R.id.read_shine_button);
            readTvCenter = V.get(itemView, R.id.read_text_center);
            readTvBottom = V.get(itemView, R.id.read_text_bottom);
        }
    }

    private class ViewHolderEnd extends RecyclerView.ViewHolder {

        public ViewHolderEnd(View itemView) {
            super(itemView);
        }
    }


    public void destroy() {
        if (mDevotionTask != null) {
            mDevotionTask.cancel(true);
        }
        if (mVerseTask != null) {
            mVerseTask.cancel(true);
        }
        if (mUserTask != null) {
            mUserTask.cancel(true);
        }

        if (mAnim4NewDevotion != null) {
            mAnim4NewDevotion.cancel();
            mAnim4NewDevotion = null;
        }
        if (arrowHandler != null && arrowRunnable != null) {
            arrowHandler.removeCallbacks(arrowRunnable);
        }
    }

    public void refreshReadHistory() {
        //last read
        mLastBookId = Preferences.getInt(Prefkey.lastBookId, 0);
        mLastChapter = Preferences.getInt(Prefkey.lastChapter, 0);
        mLastVerse = Preferences.getInt(Prefkey.lastVerse, 0);
        mLastedAri = Ari.encode(mLastBookId, mLastChapter, mLastVerse);
    }

    private class LoadUserOperationData extends OmAsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... objects) {
            updateReadBibleTime();
            updateReadVerseCount();
            updatePrayCount();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            notifyDataSetChanged();
        }
    }

    public void updatePrayCount() {
        mTodayPrayCount = S.getDb().getTodayUserOperationDataByType(Db.UserDayOperations.type_pray_count);
        mTodayPrayCount = (mTodayPrayCount < 0) ? 0 : mTodayPrayCount;
    }

    public void updateReadBibleTime() {
        mTodayReadMilliseconds = S.getDb().getTodayUserOperationDataByType(Db.UserDayOperations.type_read_bible_milliseconds);
        mTodayReadMilliseconds = (mTodayReadMilliseconds < 0) ? 0 : mTodayReadMilliseconds;
    }

    public void updateReadVerseCount() {
        mTodayReadVerseCount = S.getDb().getTodayUserOperationDataByType(Db.UserDayOperations.type_daily_verse_read_count);
        mTodayReadVerseCount = (mTodayReadVerseCount < 0) ? 0 : mTodayReadVerseCount;
    }

    private class LoadDailyVerseData extends OmAsyncTask<Object, Void, Boolean> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Object... objects) {

            try {
                VerseService verseService = BaseRetrofit.getVerseService();
                String today = DateTimeUtil.getDateStr4ApiRequest(System.currentTimeMillis());
                Call<VerseListResponse> call = verseService.getVerseListNew(today, today);
                Response<VerseListResponse> response = null;
                response = call.execute();
                VerseListResponse verseListResponse = response.body();
                if (verseListResponse != null && verseListResponse.getData() != null && verseListResponse.getData().getLists() != null && verseListResponse.getData().getLists().size() > 0) {
                    mDailyVerse = verseListResponse.getData().getLists().get(0);
                    return true;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                addItem(ITEM_DAILY_VERSE_HEADER, false);
                addItem(ITEM_DAILY_VERSE, true);
            }
        }
    }

    private class LoadDevotionData extends OmAsyncTask<Object, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            mReadDevotionIds.clear();
            mDevotions.clear();
        }

        @Override
        protected Boolean doInBackground(Object... objects) {

            try {
                List<Integer> ids = S.getDb().listAllDevotionHistoryByDevotionDate(mCurrentDate);
                if (ids != null && ids.size() > 0) {
                    mReadDevotionIds.addAll(ids);
                }

//                List<Integer> site_ids = S.getDb().listAllSubscribeDevotion();
//                String subSites = "";
//                if (site_ids != null && site_ids.size() > 0) {
//                    subSites += String.valueOf(site_ids.get(0));
//                    for (int i = 1; i < site_ids.size(); ++i) {
//                        subSites += ("," + site_ids.get(i));
//                    }
//                }

                DevotionService devotionService = BaseRetrofit.getDevotionService();
                Call<DevotionListResponse> call = devotionService.getHomeDevotionList(null);
                Response<DevotionListResponse> response = call.execute();
                DevotionListResponse homeResponse = response != null ? response.body() : null;
                if (homeResponse != null && homeResponse.getData() != null && homeResponse.getData().getLists() != null) {
                    mDevotions.addAll(homeResponse.getData().getLists());
                    int lastId = Utility.getAllLastedDevotionIds(mContext);
                    for (int i = 0; i < homeResponse.getData().getLists().size(); ++i) {
                        DevotionBean bean = homeResponse.getData().getLists().get(i);
                        if (bean.getId() != lastId) {
                            mTodayUpdateDevotionCount++;
                        } else {
                            break;
                        }
                    }
                }

                if (mDevotions.size() > 2) {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                addItem(ITEM_DAILY_DEVOTION_HEADER, false);
                int size = mDevotions.size();
                for (int i = 0; i < size; ++i) {
                    addItem(ITEM_DAILY_DEVOTION_START + i, (i == size - 1));
                }
                if (isShowLockReminderInDevotion) {
                    addLockScreenGuideInDevotionList();
                }
            }
        }

    }


    private void addLockScreenGuideInDevotionList() {
        int size = mDevotions.size();
        addItem(ITEM_DAILY_DEVOTION_START + size, true);
        mDevotions.add(ITEM_AD_POSITION_IN_DEVOTIONS, DevotionBean.createLockScreenGuideItem());
        notifyDataSetChanged();
    }


    private void loadBackground(final ImageView background, final Bitmap bitmap) {
        try {
            Blurry.with(mContext)
                    .radius(18)
                    .sampling(8)
                    .from(bitmap)
                    .into(background);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

