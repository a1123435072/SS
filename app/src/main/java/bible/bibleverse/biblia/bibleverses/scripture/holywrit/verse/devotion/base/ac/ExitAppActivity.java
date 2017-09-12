package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.florent37.expectanim.ExpectAnim;
import com.nineoldandroids.animation.Animator;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventClickDailyNt;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventExitMainPage;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.MVersion;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.MVersionDb;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.MVersionInternal;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.storage.Prefkey;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.notification.NotificationBase;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import de.greenrobot.event.EventBus;
import yuku.afw.V;
import yuku.afw.storage.Preferences;
import yuku.alkitab.model.Book;
import yuku.alkitab.model.Version;
import yuku.alkitab.util.Ari;
import yuku.alkitabintegration.display.Launcher;

import static com.github.florent37.expectanim.core.Expectations.alphaValue;
import static com.github.florent37.expectanim.core.Expectations.leftOfParent;
import static com.github.florent37.expectanim.core.Expectations.scale;
import static com.github.florent37.expectanim.core.Expectations.toRightOf;
import static com.github.florent37.expectanim.core.Expectations.topOfParent;


/**
 * Created by Mr_ZY on 16/11/7.
 */

public class ExitAppActivity extends BaseActivity {

    public static final int SLIDE_UP_DURATION = 1200;

    private View mAdHeaderLayout, mTxtLayout, mTxtLayout2, mAdFlagLayout, mCenterLayout, mRightInLayout;
    private TextView mTitleTv, mMsgTv, mOkBtn, mContinueBtn, mTitleTv2;
    private ImageView mShiningIv, mIcon, mShiningIv2, mIconIv2;
    private View mAdCloseBtn;
    private ProgressBar mProgressBar;

    //    private AdFullScreenStyle mAdLayout;
    private FrameLayout mAdLayoutContainer;

    private ExpectAnim mExpectAnim;
    private ExpectAnim mExpectAnimSignIn;

    private boolean isAnimationEnd = false;
    private boolean mIsAdShown = false;

    public final static int TYPE_SIGN_IN_NOTIFICATION = 1;
    public final static int TYPE_READ_CHAPTER_DONE = 2;
    public final static int TYPE_EXIT_APP = 3;

    private String mReference;
    private int mType, mProgress;

    //book
    private Book mActiveBook;
    private int mLastedAri, mLastBookId, mLastChapter, mLastVerse;
    private Version mCurrentVersion;
    private Handler mHandler;

    public static Intent createIntent(Context context, int type, int progress, String reference) {
        Intent intent = new Intent();
        intent.setClass(context, ExitAppActivity.class);
        intent.putExtra(Constants.KEY_READING_FINISH_PAGE_TYPE, type);
        intent.putExtra(Constants.KEY_READING_PROGRESS, progress);
        intent.putExtra(Constants.KEY_BOOK_REFERENCE, reference);
        return intent;
    }

    public static Intent createIntent(Context context, int type) {
        Intent intent = new Intent();
        intent.setClass(context, ExitAppActivity.class);
        intent.putExtra(Constants.KEY_READING_FINISH_PAGE_TYPE, type);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit_app);

        handleIntent();

        loadAD();

        initView();

        if (mType == TYPE_EXIT_APP) {
            startExitAppAnim();
        } else {
            startAnim();
        }

        if (mType == TYPE_SIGN_IN_NOTIFICATION) {
            setupExpectAnimSignIn();
        } else {
            setupExpectAnim();
        }
    }

    private void initView() {
        mHandler = new Handler();
        mAdHeaderLayout = V.get(this, R.id.ad_header_layout);
        mAdFlagLayout = V.get(this, R.id.ad_flag);
        mTxtLayout = V.get(this, R.id.txt_layout);
        mTxtLayout2 = V.get(this, R.id.txt_layout2);
        mAdCloseBtn = V.get(this, R.id.ad_close);
        mCenterLayout = V.get(this, R.id.center_layout);
        mShiningIv = V.get(this, R.id.shining_light_icon);
        mProgressBar = V.get(this, R.id.pg);
        mIcon = V.get(this, R.id.icon);
        mTitleTv = V.get(this, R.id.title);
        mMsgTv = V.get(this, R.id.msg);
        mAdLayoutContainer = V.get(this, R.id.ad_container);
        mOkBtn = V.get(this, R.id.ok_btn);
        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });
        mAdCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mType == TYPE_READ_CHAPTER_DONE || mType == TYPE_SIGN_IN_NOTIFICATION) {
                    startActivity(RemoveAdsActivity.createIntent(ExitAppActivity.this, Constants.FROM_FINISH_FS));
                }
                exit();
            }
        });

        if (mType == TYPE_SIGN_IN_NOTIFICATION) {
            Utility.recordSigninDays(this);
            mRightInLayout = V.get(this, R.id.right_in_layout);
            mShiningIv2 = V.get(this, R.id.shining_light_icon2);
            mIconIv2 = V.get(this, R.id.icon2);
            mTitleTv2 = V.get(this, R.id.title2);
            mContinueBtn = V.get(this, R.id.continue_read_btn);
            mContinueBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoLastOpenBibleVerse();
                }
            });
            mProgressBar.setVisibility(View.GONE);
            mIcon.setImageResource(R.drawable.ic_calendar);
            mIconIv2.setImageResource(R.drawable.ic_mark);
            int days = Preferences.getInt(getString(R.string.pref_sign_in_days), 0);
            Utility.formatTextStyleWithSizeAndColor(this, mTitleTv, 1.25f, days + "", getString(R.string.read_finish_page_title2, String.valueOf(days)), R.color.white);
            mMsgTv.setText(R.string.read_finish_page_msg2);
            setLastReadChapterInfo();
        } else if (mType == TYPE_READ_CHAPTER_DONE) {
            mIcon.setImageResource(R.drawable.ic_read_chapter_done);
            if (!TextUtils.isEmpty(mReference)) {
                mTitleTv.setText(mReference.replace(' ', '\u00a0'));
            }
            mMsgTv.setText(R.string.read_finish_page_msg3);
            mProgressBar.setProgress(mProgress);
        } else if (mType == TYPE_EXIT_APP) {
            long readingStartTime = Preferences.getLong(getString(R.string.pref_reading_start_time), 0);
            long readingTime = 0;
            if (readingStartTime > 0) {
                readingTime = System.currentTimeMillis() - readingStartTime;
            } else {
                readingTime = 6 * 1000 * 60;
            }
            mTitleTv.setText(getString(R.string.read_finish_page_title1, Utility.getTimeFormatStyleText(this, readingTime)));
            mMsgTv.setText(R.string.read_finish_page_msg1);
        }
    }

    private void setLastReadChapterInfo() {
        //lasted readed
        mLastBookId = Preferences.getInt(Prefkey.lastBookId, 0);
        mLastChapter = Preferences.getInt(Prefkey.lastChapter, 0);
        mLastVerse = Preferences.getInt(Prefkey.lastVerse, 0);
        mLastedAri = Ari.encode(mLastBookId, mLastChapter, mLastVerse);

        if (mLastedAri != 0) {
            if (S.activeVersion == null) {
                S.checkActiveVersion();
            }
            if (S.activeVersion != null) {
                final Book book = S.activeVersion.getBook(Ari.toBook(mLastedAri));
                if (book != null) {
                    mActiveBook = book;
                } else { // can't load last book or bookId 0
                    mActiveBook = S.activeVersion.getFirstBook();
                }
            }
        }
        //current version
        final String lastVersionId = Preferences.getString(Prefkey.lastVersionId);
        MVersion mv = getVersionFromVersionId(lastVersionId);
        if (mv == null) {
            mv = getVersionFromVersionId("preset/en-kjv");
        }
        mCurrentVersion = (mv != null) ? mv.getVersion() : null;
        if (mActiveBook == null) {
            final String reference = mLastBookId + ":" + mLastChapter;
            mTitleTv2.setText(getString(R.string.lasted_read, reference));
        } else {
            final String reference = mActiveBook.reference(mLastChapter);
            mTitleTv2.setText(getString(R.string.lasted_read, reference.replace(' ', '\u00a0')));
        }
    }

    private MVersion getVersionFromVersionId(String versionId) {
        if (versionId == null || MVersionInternal.getVersionInternalId().equals(versionId)) {
            return null; // internal is made the same as null
        }
        // let's look at yes versions
        for (MVersionDb mvDb : S.getDb().listAllVersions()) {
            if (mvDb.getVersionId().equals(versionId)) {
                if (mvDb.hasDataFile()) {
                    return mvDb;
                } else {
                    return null; // this is the one that should have been chosen, but the data file is not available, so let's fallback.
                }
            }
        }
        return null; // not known
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            mType = intent.getIntExtra(Constants.KEY_READING_FINISH_PAGE_TYPE, -1);
            if (mType == -1) {
                finish();
            }
            mProgress = intent.getIntExtra(Constants.KEY_READING_PROGRESS, -1);
            mReference = intent.getStringExtra(Constants.KEY_BOOK_REFERENCE);


            int notificationType = intent.getIntExtra(Constants.KEY_NOTIFICATION_TYPE, 0);
            if (notificationType > 0) {
                Utility.recordReadBibleStartTime(this);
            }
        } else {
            finish();
        }
    }

    private void startExitAppAnim() {
        mIcon.setVisibility(View.VISIBLE);
        mTxtLayout.setVisibility(View.VISIBLE);
        mOkBtn.setVisibility(View.VISIBLE);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.shining_icon_rotate_anim);
        rotation.setRepeatCount(Animation.INFINITE);
        mShiningIv.startAnimation(rotation);
    }

    private void startAnim() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.BounceIn).duration(1000).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mIcon.setVisibility(View.VISIBLE);
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
                }).playOn(mIcon);
            }
        }, 200);
    }

    private void fadeInTxt() {
        YoYo.with(Techniques.FadeIn).duration(100).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mTxtLayout.setVisibility(View.VISIBLE);
                if (mType == TYPE_READ_CHAPTER_DONE) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                startShiningAim(mShiningIv, false);
                if (mType == TYPE_SIGN_IN_NOTIFICATION) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            moveLayoutOutAnim();
                        }
                    }, 1000);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(mTxtLayout);

    }

    private void startShiningAim(View view, boolean isSecondShining) {
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.shining_icon_rotate_anim);
        rotation.setRepeatCount(Animation.INFINITE);
        view.startAnimation(rotation);
    }

    private void moveLayoutOutAnim() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.SlideOutLeft)
                        .duration(300)
                        .interpolate(new AccelerateDecelerateInterpolator())
                        .withListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mCenterLayout.setVisibility(View.GONE);
                                mShiningIv.clearAnimation();
                                moveLayoutRightInAnim();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                            }
                        })
                        .playOn(mCenterLayout);
            }
        }, 100);
    }

    private void moveLayoutRightInAnim() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                YoYo.with(Techniques.SlideInRight)
                        .duration(600)
                        .interpolate(new AccelerateDecelerateInterpolator())
                        .withListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                mRightInLayout.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                startShiningAim(mShiningIv2, true);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                            }
                        })
                        .playOn(mRightInLayout);
            }
        }, 100);
    }

    //////////  ad ////////
    private void loadAD() {
        switch (mType) {
            case TYPE_SIGN_IN_NOTIFICATION:
            case TYPE_EXIT_APP:
                break;
            case TYPE_READ_CHAPTER_DONE:
                break;
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isAnimationEnd) {
                exit();
            } else {
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exitMainPage() {
        EventBus.getDefault().post(new EventExitMainPage());
        Preferences.setLong(getString(R.string.pref_reading_start_time), 0);
        finish();
    }

    private void exit() {
        if (mType == TYPE_SIGN_IN_NOTIFICATION) {
            gotoLastOpenBibleVerse();
        } else if (mType == TYPE_READ_CHAPTER_DONE) {
            finish();
        } else if (mType == TYPE_EXIT_APP) {
            exitMainPage();
        }
    }

    private void gotoLastOpenBibleVerse() {
        Intent intent = Launcher.openAppAtBibleLocationNonVerseSelected(mLastedAri);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mShiningIv != null) {
            mShiningIv.clearAnimation();
        }
    }

    private void setupExpectAnim() {
        mExpectAnim = new ExpectAnim()
                .expect(mShiningIv)
                .toBe(alphaValue(0.0f))
                .expect(mTxtLayout)
                .toBe(topOfParent().withMarginDp(28), toRightOf(mIcon), scale(0.875f, 0.875f))
                .expect(mIcon)
                .toBe(topOfParent().withMarginDp(28), leftOfParent().withMarginDp(42), scale(0.3333333f, 0.3333333f))
                .toAnimation()
                .setDuration(SLIDE_UP_DURATION);
    }

    private void setupExpectAnimSignIn() {
        mExpectAnimSignIn = new ExpectAnim()
                .expect(mShiningIv2)
                .toBe(alphaValue(0.0f))
                .expect(mTxtLayout2)
                .toBe(topOfParent().withMarginDp(28), toRightOf(mIconIv2), scale(0.875f, 0.875f))
                .expect(mIconIv2)
                .toBe(topOfParent().withMarginDp(28), leftOfParent().withMarginDp(42), scale(0.3333333f, 0.3333333f))
                .toAnimation()
                .setDuration(SLIDE_UP_DURATION);
    }

}
