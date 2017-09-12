package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.fw.basemodules.utils.OmAsyncTask;
import com.github.florent37.expectanim.ExpectAnim;
import com.nineoldandroids.animation.Animator;
import com.wx.goodview.GoodView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.SelfAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.UserBehaviorAnalytics;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.BaseRetrofit;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.PrayerService;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.EmptyResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PrayerBean;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PrayerPeopleResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventClickDailyNt;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventRefreshFavoriteList;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventUserOperationChanged;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.storage.Db;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.view.CenterAlignImageSpan;
import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yuku.afw.V;

import static bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants.PRAYER_CATEGORY_ID;
import static bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants.PRAYER_CATEGORY_NUM;
import static com.github.florent37.expectanim.core.Expectations.alphaValue;
import static com.github.florent37.expectanim.core.Expectations.leftOfParent;
import static com.github.florent37.expectanim.core.Expectations.scale;
import static com.github.florent37.expectanim.core.Expectations.toRightOf;
import static com.github.florent37.expectanim.core.Expectations.topOfParent;


public class PrayerDetailActivity extends BaseActivity implements View.OnClickListener {
    private View mPrayContentLayout, mPrayCompleteLayout, mTxtLayout, mShiningIv, mCrossIcon, mBtnGroup,
            mAdHeaderLayout, mAdFlagLayout, mAdCloseBtn;
    private TextView mPrayContentTv, mPrayTitleTv, mBackBtn, mNextPrayerBtn, mPrayNumTv;

    private ScrollView mScrollView;
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;

    private FrameLayout mAdLayoutContainer;

    private ExpectAnim mExpectAnim;

    private boolean isAnimationEnd = false;

    public static final int SLIDE_UP_DURATION = 1200;


    private ArrayList<PrayerBean> mPrayers;
    private int mPrayerIndex;
    private PrayerBean mPray;
    private String mCategoryName, amen;
    private int mPrayerNum, mCategoryId;
    private LoadPrayerPeopleNumTask mLoadPrayerPeopleNumTask;
    private GoodView mGoodView;
    private Handler mHandler;
    private Random mRandom;
    private boolean mIsStopPrayerNumAnim = false;

    private MenuItem mVolumeItem, mFavoriteItem, mShareItem;
    private boolean mIsInFavorite = false;

    public static Intent createIntentFromList(Context context, ArrayList<PrayerBean> prays,
                                              int index, String category, int prayerCount, int catId) {
        return new Intent(context, PrayerDetailActivity.class)
                .putParcelableArrayListExtra(Constants.PRAYER_LIST, prays)
                .putExtra(Constants.PRAYER_INDEX, index)
                .putExtra(Constants.PRAYER_CATEGORY, category)
                .putExtra(PRAYER_CATEGORY_ID, catId)
                .putExtra(PRAYER_CATEGORY_NUM, prayerCount);
    }

    public static Intent createIntent(Context context, ArrayList<PrayerBean> prays,
                                      int index, String category, int notifyId, int notifyType) {
        return new Intent(context, PrayerDetailActivity.class)
                .putParcelableArrayListExtra(Constants.PRAYER_LIST, prays)
                .putExtra(Constants.PRAYER_INDEX, index)
                .putExtra(Constants.PRAYER_CATEGORY, category)
                .putExtra(Constants.KEY_NOTIFICATION_ID, notifyId)
                .putExtra(Constants.KEY_NOTIFICATION_TYPE, notifyType)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer_detail);

        handleIntent();
        if (mPray == null) {
            return;
        }

        initToolBar();

        initView();

        setupExpectAnim();

        getWindow().getDecorView().setKeepScreenOn(true);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            mCategoryName = intent.getStringExtra(Constants.PRAYER_CATEGORY);
            mPrayers = intent.getParcelableArrayListExtra(Constants.PRAYER_LIST);
            mPrayerIndex = intent.getIntExtra(Constants.PRAYER_INDEX, 0);
            mPrayerNum = intent.getIntExtra(Constants.PRAYER_CATEGORY_NUM, 0);
            mCategoryId = intent.getIntExtra(Constants.PRAYER_CATEGORY_ID, 0);
            if (mPrayers == null || mPrayers.size() == 0) {
                finish();
                return;
            }
            if (mPrayerIndex > mPrayers.size() - 1 || mPrayerIndex < 0) {
                mPrayerIndex = 0;
            }
            mPray = mPrayers.get(mPrayerIndex);
            if (mPrayerNum == 0) {
                getPrayerPeopleNum();
            }
            int type = intent.getIntExtra(Constants.KEY_NOTIFICATION_TYPE, 0);
            if (type > 0) {
                SelfAnalyticsHelper.sendNotificationAnalytics(this, AnalyticsConstants.A_PRAYER_NOTICE, AnalyticsConstants.L_PRAYER_CLICK + "_" + type);
                EventBus.getDefault().post(new EventClickDailyNt());
            }
        }
    }

    public void initToolBar() {
        Toolbar topToolBar = V.get(this, R.id.toolbar);
        topToolBar.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
        setSupportActionBar(topToolBar);
        topToolBar.setNavigationIcon(R.drawable.ic_back_white);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_prayer_content_toolbar, null, false);
        mPrayNumTv = V.get(view, R.id.prayer_num);
        topToolBar.addView(view);
        setTitle("");
    }

    private void getPrayerPeopleNum() {
        mLoadPrayerPeopleNumTask = new LoadPrayerPeopleNumTask();
        mLoadPrayerPeopleNumTask.execute();
    }

    private class LoadPrayerPeopleNumTask extends OmAsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            if (mCategoryId > 0) {
                loadPrayerPeopleData();
            } else {
                mPrayerNum = getRandomPrayerNum();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            startPrayerNumAnim();
        }
    }

    private int getRandomPrayerNum() {
        Random random = new Random();
        return random.nextInt(200) + 100;
    }

    private void loadPrayerPeopleData() {
        try {
            PrayerService prayerService = BaseRetrofit.getPrayerService();
            Call<PrayerPeopleResponse> peopleNumCall = prayerService.getPrayerPeopleNum();
            Response<PrayerPeopleResponse> peopleNumResponse = peopleNumCall.execute();
            if (peopleNumResponse != null) {
                PrayerPeopleResponse prayerPeopleResponse = peopleNumResponse.body();
                if (prayerPeopleResponse != null) {
                    PrayerPeopleResponse.DataBean dataBean = prayerPeopleResponse.getData();
                    if (dataBean != null) {
                        int totalNum = getCurrentTimePrayerPeople(dataBean.getTotal());
                        loadPrayerCategoryNum(dataBean.getCategory(), totalNum);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getCurrentTimePrayerPeople(List<PrayerPeopleResponse.DataBean.TotalBean> totalBeanList) {
        if (totalBeanList == null || totalBeanList.isEmpty()) {
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Random random = new Random();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        for (PrayerPeopleResponse.DataBean.TotalBean totalBean : totalBeanList) {
            if (totalBean != null && totalBean.getHour() == hour) {
                return random.nextInt(totalBean.getRangeMax() - totalBean.getRangeMin() + 1) + totalBean.getRangeMin();
            }
        }
        return 0;
    }


    private void loadPrayerCategoryNum(List<PrayerPeopleResponse.DataBean.CategoryRadioBean> categoryRadioBeanList, int totalNum) {
        if (categoryRadioBeanList == null || categoryRadioBeanList.isEmpty() || totalNum == 0) {
            return;
        }
        for (PrayerPeopleResponse.DataBean.CategoryRadioBean categoryRadioBean : categoryRadioBeanList) {
            if (categoryRadioBean.getId() == mCategoryId) {
                mPrayerNum = (int) (categoryRadioBean.getRatio() * totalNum);
                break;
            }
        }
    }

    private void initView() {
        amen = getString(R.string.amen);
        mGoodView = new GoodView(this);
        mMediaPlayer = new MediaPlayer();
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mHandler = new Handler();
        mRandom = new Random();

        //pray content layout
        mPrayContentLayout = V.get(this, R.id.prayer_content_layout);
        mScrollView = V.get(this, R.id.scrollview);
        mPrayTitleTv = V.get(this, R.id.prayer_title);
        mPrayContentTv = V.get(this, R.id.prayer_detail);

        //pray complete layout
        mPrayCompleteLayout = V.get(this, R.id.prayer_complete_layout);
        mShiningIv = V.get(this, R.id.shining_light_icon);
        mCrossIcon = V.get(this, R.id.icon);
        mTxtLayout = V.get(this, R.id.txt_layout);
        mBtnGroup = V.get(this, R.id.btn_group);
        mBackBtn = V.get(this, R.id.back_btn);
        mNextPrayerBtn = V.get(this, R.id.next_btn);

        //AD layout
        mAdHeaderLayout = V.get(this, R.id.ad_header_layout);
        mAdFlagLayout = V.get(this, R.id.ad_flag);
        mAdCloseBtn = V.get(this, R.id.ad_close);
        mAdLayoutContainer = V.get(this, R.id.ad_container);


        mAdCloseBtn.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
        mNextPrayerBtn.setOnClickListener(this);

        mPrayTitleTv.setText(mPray.getTitle());
        mPrayTitleTv.setSelected(true);
        setContentTv();

        startPrayerNumAnim();
    }

    private void startPrayerNumAnim() {
        if (mPrayerNum == 0) {
            return;
        }
        mIsStopPrayerNumAnim = false;
        mPrayNumTv.setText(String.valueOf(mPrayerNum));
        //start add num anim
        mHandler.postDelayed(mAddNumRunnable, 2000);
    }

    private void stopPrayerNumAnim() {
        mIsStopPrayerNumAnim = true;
        mHandler.removeCallbacks(mAddNumRunnable);
        mHandler.removeCallbacksAndMessages(null);
    }

    private Runnable mAddNumRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsStopPrayerNumAnim) {
                return;
            }
            int count = mRandom.nextInt(8) - 2;
            if (count == 0) {
                count = 1;
            }
            String addNum = count > 0 ? "+" + count : "-" + count;
            if (mPrayerNum + count <= 0) {
                mPrayerNum += -count;
                addNum = "+" + (-count);
            } else {
                mPrayerNum += count;
            }
            if (mPrayerNum <= 0) {
                mPrayerNum = 1;
            }
            mGoodView.setTextInfo(addNum, ContextCompat.getColor(PrayerDetailActivity.this, R.color.new_flag_color), 16);
            mPrayNumTv.setText(String.valueOf(mPrayerNum));
            mGoodView.show(mPrayNumTv);
            mGoodView.setDuration(1200);
            mHandler.postDelayed(mAddNumRunnable, 3000);
        }
    };

    private void initPrayerAudio() {
        if (Utility.isPrayerAudioFileExist(this)) {
            mVolumeItem.setEnabled(true);
            mVolumeItem.setVisible(true);
        } else {
            mVolumeItem.setEnabled(false);
            mVolumeItem.setVisible(false);
            return;
        }
        if (Utility.isPrayerAudioOn(this)) {
            playOrStopAudio();
            mVolumeItem.setIcon(R.drawable.ic_prayer_volume_on);
        } else {
            mVolumeItem.setIcon(R.drawable.ic_prayer_volume_off);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                if (!onBack()) {
                    finish();
                }
                break;
            case R.id.prayer_complete_btn:
                turnOffAudio();
                stopPrayerNumAnim();
                startAnim();
                setPrayerViewed();
                SelfAnalyticsHelper.sendPrayerAnalytics(this, AnalyticsConstants.A_COMPLETE_PRAYER, mCategoryName);
                UserBehaviorAnalytics.trackUserBehavior(this, AnalyticsConstants.P_PRAYER_READ, AnalyticsConstants.B_COMPLETE_PRAYER);
                S.getDb().addTodayPrayerCount(1);
                EventBus.getDefault().post(new EventUserOperationChanged(EventUserOperationChanged.TYPE_PRAY));
                break;
            case R.id.next_btn:
                mPrayerIndex++;
                if (mPrayerIndex > mPrayers.size() - 1) {
                    return;
                }
                endShiningAnim();
                initPrayerAudio();
                updatePrayerContent();
                startPrayerNumAnim();
                break;
            case R.id.ad_close:
                startActivity(RemoveAdsActivity.createIntent(PrayerDetailActivity.this, Constants.FROM_PRAYER_FINISH));
                finish();
                break;
        }
    }

    private void updatePrayerContent() {
        mPray = mPrayers.get(mPrayerIndex);
        mScrollView.fullScroll(ScrollView.FOCUS_UP);
        mPrayContentLayout.setVisibility(View.VISIBLE);
        mPrayCompleteLayout.setVisibility(View.GONE);
        mPrayTitleTv.setText(mPray.getTitle());
        setContentTv();
    }

    private void setContentTv() {
        String content = mPray.getContent();
        CharSequence newContent = "";
        if (content.toLowerCase().endsWith(amen.toLowerCase())) {
            newContent = content.subSequence(0, content.length() - amen.length());
        } else if (content.toLowerCase().endsWith(amen.toLowerCase() + ".")) {
            newContent = content.subSequence(0, content.length() - amen.length() - 1);
        } else {
            newContent = content;
        }
        mPrayContentTv.setText(Html.fromHtml(newContent.toString()));
    }

    private void setPrayerViewed() {
        PrayerService prayerService = BaseRetrofit.getPrayerService();
        prayerService.setPrayerViewed(String.valueOf(mPray.getId())).enqueue(new Callback<EmptyResponse>() {
            @Override
            public void onResponse(Call<EmptyResponse> call, Response<EmptyResponse> response) {
            }

            @Override
            public void onFailure(Call<EmptyResponse> call, Throwable t) {

            }
        });
    }

    private void playOrStopAudio() {
        if (mMediaPlayer == null) {
            return;
        }
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mVolumeItem.setIcon(R.drawable.ic_prayer_volume_off);
            Utility.setPrayerAudioOn(this, false);
            UserBehaviorAnalytics.trackUserBehavior(this, AnalyticsConstants.P_PRAYER_READ, AnalyticsConstants.B_MUTE);
        } else {
            try {
                playLocalMp3(Utility.getPrayerAudioFileName(this));
                mVolumeItem.setIcon(R.drawable.ic_prayer_volume_on);
                Utility.setPrayerAudioOn(this, true);
            } catch (IOException | IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    private void playLocalMp3(String path) throws IOException, IllegalStateException {
        mMediaPlayer.reset();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setDataSource(path);
        mMediaPlayer.prepareAsync();
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mMediaPlayer.start();
            }
        });
        int origionalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int volume = origionalVolume;
        if (Utility.isFirstPray(this)) {
            int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int leastVolume = maxVolume / 2;
            volume = origionalVolume >= leastVolume ? origionalVolume : leastVolume;
            Utility.setIsFirstPray(this, false);
        }
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        mMediaPlayer.setLooping(true);
    }

    private void turnOffAudio() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pray_detail, menu);
        mVolumeItem = menu.findItem(R.id.menuVolume);
        mFavoriteItem = menu.findItem(R.id.menuFavorite);
        mShareItem = menu.findItem(R.id.menuShare);
        initPrayerAudio();
        initFavoriteItem();
        setIcons();
        return true;
    }

    private void setIcons() {
        setMenuSpnannableTitle(mShareItem, getString(R.string.share), R.drawable.ic_share);
        setMenuSpnannableTitle(mFavoriteItem, getString(getFavoriteItemTextId()), getFavoriteItemIconRes());
    }

    private void setMenuSpnannableTitle(MenuItem menuItem, String text, int resId) {
        SpannableString spannableString = new SpannableString("   " + text);
        CenterAlignImageSpan donateImageSpan = new CenterAlignImageSpan(this, resId);
        spannableString.setSpan(donateImageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        menuItem.setTitle(spannableString);
    }

    private void initFavoriteItem() {
        mIsInFavorite = S.getDb().isInFavorite(Db.Favorite.TYPE_PRAYER, mPray.getId());
        mFavoriteItem.setIcon(getFavoriteItemIconRes());
        mFavoriteItem.setTitle(getFavoriteItemTextId());
    }

    private int getFavoriteItemTextId() {
        return mIsInFavorite ? R.string.remove_favorites : R.string.add_to_favorites;
    }

    private int getFavoriteItemIconRes() {
        return mIsInFavorite ? R.drawable.ic_fav_select : R.drawable.ic_fav_unselect;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuVolume:
                if (!Utility.isPrayerAudioFileExist(this)) {
                    break;
                }
                playOrStopAudio();
                break;
            case R.id.menuFavorite:
                doFavAction();
                break;
            case R.id.menuShare:
                Utility.shareTextBySystem(this, Html.fromHtml(mPray.getContent()) + "\n\n" + getString(R.string.share_msg) + "\n" + Constants.GOOGLE_SHORT_URL);
                SelfAnalyticsHelper.sendPrayerAnalytics(this, AnalyticsConstants.A_SHARE_PRAYER, mCategoryName);
                UserBehaviorAnalytics.trackUserBehavior(this, AnalyticsConstants.P_PRAYER_READ, AnalyticsConstants.B_SHARE_PRAYER);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void doFavAction() {
        if (mPray == null) {
            return;
        }
        if (mIsInFavorite) {
            long id = S.getDb().removeFavoriteData(Db.Favorite.TYPE_PRAYER, mPray.getId());
            if (id > 0) {
                mIsInFavorite = false;
                setMenuSpnannableTitle(mFavoriteItem, getString(getFavoriteItemTextId()), getFavoriteItemIconRes());
                Toast.makeText(this, R.string.removed_favorites, Toast.LENGTH_SHORT).show();
            }
            SelfAnalyticsHelper.sendPrayerAnalytics(this, AnalyticsConstants.A_UNFAVORITE_PRAYER, String.valueOf(mPray.getId()));
        } else {
            long id = S.getDb().addFavoritePrayer(mPray);
            if (id > 0) {
                mIsInFavorite = true;
                setMenuSpnannableTitle(mFavoriteItem, getString(getFavoriteItemTextId()), getFavoriteItemIconRes());
                Toast.makeText(this, R.string.added_to_favorites, Toast.LENGTH_SHORT).show();
            }
            SelfAnalyticsHelper.sendPrayerAnalytics(this, AnalyticsConstants.A_FAVORITE_PRAYER, String.valueOf(mPray.getId()));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        turnOffAudio();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mShiningIv != null) {
            mShiningIv.clearAnimation();
        }
        if (mLoadPrayerPeopleNumTask != null) {
            mLoadPrayerPeopleNumTask.cancel(true);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.removeCallbacks(mAddNumRunnable);
        }
        EventBus.getDefault().post(new EventRefreshFavoriteList(Db.Favorite.TYPE_PRAYER));
    }


    private void startAnim() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.BounceIn).duration(1000).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mTxtLayout.setVisibility(View.GONE);
                        mBtnGroup.setVisibility(View.GONE);
                        mShiningIv.setVisibility(View.GONE);
                        mCrossIcon.setVisibility(View.VISIBLE);
                        mPrayContentLayout.setVisibility(View.GONE);
                        mPrayCompleteLayout.setVisibility(View.VISIBLE);
                        if (mPrayerIndex >= mPrayers.size() - 1) {
                            mNextPrayerBtn.setVisibility(View.GONE);
                        } else {
                            mNextPrayerBtn.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fadeInTxt();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).playOn(mCrossIcon);
            }
        }, 200);
    }

    private void fadeInTxt() {
        YoYo.with(Techniques.FadeIn).duration(100).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mTxtLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                startShiningAim(mShiningIv);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(mTxtLayout);

    }

    private void startShiningAim(View view) {
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.shining_icon_rotate_anim);
        rotation.setRepeatCount(Animation.INFINITE);
        view.startAnimation(rotation);
    }

    private void endShiningAnim() {
        if (mShiningIv != null) {
            mShiningIv.clearAnimation();
        }
    }


    private void showBottomLayout(final View view) {
        showBottomLayout(view, 500);
    }

    private void showBottomLayout(final View view, final int duration) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.SlideInUp).duration(duration).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        view.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isAnimationEnd = true;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).playOn(view);
            }
        }, 200);

    }

    private void setupExpectAnim() {
        mExpectAnim = new ExpectAnim()
                .expect(mShiningIv)
                .toBe(
                        alphaValue(0.0f)
                )

                .expect(mTxtLayout)
                .toBe(
                        topOfParent().withMarginDp(28),
                        toRightOf(mCrossIcon),
                        scale(0.875f, 0.875f)
                )

                .expect(mCrossIcon)
                .toBe(
                        topOfParent().withMarginDp(28),
                        leftOfParent().withMarginDp(42),
                        scale(0.3333333f, 0.3333333f)
                )

                .toAnimation()
                .setDuration(SLIDE_UP_DURATION);
    }

}
