package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.helper;

import android.content.Intent;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.fw.basemodules.utils.AndroidUtils;
import com.fw.basemodules.utils.OmAsyncTask;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.NewAnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.NewAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.BibleImagesListActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.DonateActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.FavoriteActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.MarkerListActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.NewMainActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.PlansActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.RemoveAdsActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.SettingsActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.storage.Db;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Constants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.ImageUtil;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.RatingUtil;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import yuku.afw.V;
import yuku.alkitab.model.Marker;

/**
 * Created by yzq on 2017/2/21.
 */

public class LeftDrawerHelper {

    private NewMainActivity mActivity;
    private DrawerLayout mDrawerLayout;


    private View mHighlightsLayout, mBookmarkLayout, mNotesLayout, mImagesLayout, mPlansLayout, mFavoriteLayout;
    private View mDonateLayout, mRemoveAdsLayout;
    private View mSettingLayout, mRatingLayout, mFeedBackLayout;
    private TextView mHighlightsNumTv, mBookmarkNumTv, mNotesNumTv, mImagesNumTv, mPlansNumTv;
    private OmAsyncTask mCountTask;

    public LeftDrawerHelper(NewMainActivity activity, DrawerLayout drawerLayout) {
        this.mActivity = activity;
        this.mDrawerLayout = drawerLayout;
    }

    public void initLeftDrawer() {
        mHighlightsLayout = V.get(mActivity, R.id.highlights_button);
        mBookmarkLayout = V.get(mActivity, R.id.bookmarks_button);
        mNotesLayout = V.get(mActivity, R.id.notes_button);
        mImagesLayout = V.get(mActivity, R.id.images_button);
        mPlansLayout = V.get(mActivity, R.id.plans_button);
        mFavoriteLayout = V.get(mActivity, R.id.fav_button);

        mHighlightsNumTv = V.get(mActivity, R.id.highlights_num);
        mBookmarkNumTv = V.get(mActivity, R.id.bookmarks_num);
        mNotesNumTv = V.get(mActivity, R.id.notes_num);
        mImagesNumTv = V.get(mActivity, R.id.images_num);
        mPlansNumTv = V.get(mActivity, R.id.plans_num);

        mDonateLayout = V.get(mActivity, R.id.donate_button);
        mRemoveAdsLayout = V.get(mActivity, R.id.remove_ads_button);

        mSettingLayout = V.get(mActivity, R.id.settings_button);
        mRatingLayout = V.get(mActivity, R.id.ratings_button);
        mFeedBackLayout = V.get(mActivity, R.id.feedback_button);

        LeftDrawerClickListener listener = new LeftDrawerClickListener();

        mHighlightsLayout.setOnClickListener(listener);
        mBookmarkLayout.setOnClickListener(listener);
        mNotesLayout.setOnClickListener(listener);
        mImagesLayout.setOnClickListener(listener);
        mPlansLayout.setOnClickListener(listener);
        mFavoriteLayout.setOnClickListener(listener);

        mDonateLayout.setOnClickListener(listener);
        mRemoveAdsLayout.setOnClickListener(listener);

        mSettingLayout.setOnClickListener(listener);
        mRatingLayout.setOnClickListener(listener);
        mFeedBackLayout.setOnClickListener(listener);

        mCountTask = new LoadUserOperationCountTask();
        mCountTask.execute();
    }

    private class LeftDrawerClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            final int id = v.getId();
            mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {

                @Override
                public void onDrawerClosed(View drawerView) {
                    switch (id) {
                        case R.id.donate_button:
                            mActivity.startActivity(DonateActivity.createIntent(mActivity, Constants.FROM_LEFT_DRAWER));
                            break;
                        case R.id.remove_ads_button:
                            mActivity.startActivity(RemoveAdsActivity.createIntent(mActivity, Constants.FROM_LEFT_DRAWER));
                            break;
                        case R.id.highlights_button:
                            NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_HOME_MENU_HIGHLIGHTS, "click");
                            mActivity.startActivity(MarkerListActivity.createIntent(mActivity, Marker.Kind.highlight, 0));
                            break;
                        case R.id.bookmarks_button:
                            NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_HOME_MENU_BOOKMARKS, "click");
                            mActivity.startActivity(MarkerListActivity.createIntent(mActivity, Marker.Kind.bookmark, 0));
                            break;
                        case R.id.notes_button:
                            NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_HOME_MENU_NOTES, "click");
                            mActivity.startActivity(MarkerListActivity.createIntent(mActivity, Marker.Kind.note, 0));
                            break;
                        case R.id.images_button:
                            NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_HOME_MENU_IMAGES, "click");
                            mActivity.startActivity(new Intent(mActivity, BibleImagesListActivity.class));
                            break;
                        case R.id.plans_button:
                            NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_HOME_MENU_PLANS, "click");
                            mActivity.startActivity(PlansActivity.createIntent(mActivity, true));
                            break;
                        case R.id.fav_button:
                            NewAnalyticsHelper.getInstance().sendEvent(NewAnalyticsConstants.E_HOME_MENU_FAVORITES, "click");
                            mActivity.startActivity(new Intent(mActivity, FavoriteActivity.class));
                            break;
                        case R.id.settings_button:
                            mActivity.startActivity(SettingsActivity.createIntent(mActivity));
                            break;
                        case R.id.ratings_button:
                            RatingUtil.showRatingDialog(mActivity, true);
                            break;
                        case R.id.feedback_button:
                            Utility.sendEmailToOffical(mActivity,
                                    mActivity.getString(R.string.feedback_email_subject, AndroidUtils.getAppName(mActivity)) + "(" + Build.BRAND + " " + Build.MODEL + "_"
                                            + Build.VERSION.RELEASE + "_" + Utility.getAppVersionName(mActivity)
                                            + ")");
                            break;
                    }
                    mDrawerLayout.removeDrawerListener(this);
                }
            });
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    class LoadUserOperationCountTask extends OmAsyncTask<Object, Void, Void> {
        private int mImageCount = 0;
        private int mHighlightCount = 0;
        private int mBookmarkCount = 0;
        private int mNoteCount = 0;
        private int mPlanCount = 0;

        private FileFilter mFileFilter = new FileFilter() {
            public boolean accept(File file) {
                String tmp = file.getName().toLowerCase();
                if (tmp.endsWith(".jpg") || tmp.endsWith(".png")) {
                    return true;
                }
                return false;
            }
        };

        @Override
        protected void onPreExecute() {
            mImageCount = 0;
        }

        @Override
        protected Void doInBackground(Object... voids) {
            File dir = new File(ImageUtil.getSaveDirPath(mActivity));
            if (dir != null && dir.exists() && dir.isDirectory()) {
                File[] list = dir.listFiles(mFileFilter);
                if (list != null) {
                    for (int i = 0; i < list.length; i++) {
                        File file = list[i];
                        if (file != null && file.exists() && file.isFile()) {
                            ++mImageCount;
                        }
                    }
                }
            }

            List<Marker> highlights = S.getDb().listMarkers(Marker.Kind.highlight, 0, Db.Marker.createTime, false);
            mHighlightCount = highlights.size();

            List<Marker> bookmarks = S.getDb().listMarkers(Marker.Kind.bookmark, 0, Db.Marker.createTime, false);
            mBookmarkCount = bookmarks.size();

            List<Marker> notes = S.getDb().listMarkers(Marker.Kind.note, 0, Db.Marker.createTime, false);
            mNoteCount = notes.size();

            mPlanCount = S.getDb().getAllReadingPlanCount();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mImagesNumTv != null) {
                mImagesNumTv.setText(mImageCount > 0 ? String.valueOf(mImageCount) : "");
            }
            if (mHighlightsNumTv != null) {
                mHighlightsNumTv.setText(mHighlightCount > 0 ? String.valueOf(mHighlightCount) : "");
            }
            if (mBookmarkNumTv != null) {
                mBookmarkNumTv.setText(mBookmarkCount > 0 ? String.valueOf(mBookmarkCount) : "");
            }
            if (mNotesNumTv != null) {
                mNotesNumTv.setText(mNoteCount > 0 ? String.valueOf(mNoteCount) : "");
            }
            if (mPlansNumTv != null) {
                mPlansNumTv.setText(mPlanCount > 0 ? String.valueOf(mPlanCount) : "");
            }
        }
    }

    public void setHighlightNum() {
        List<Marker> highlights = S.getDb().listMarkers(Marker.Kind.highlight, 0, Db.Marker.createTime, false);
        mHighlightsNumTv.setText(highlights.size() > 0 ? String.valueOf(highlights.size()) : "");
    }

    public void setBookmarkNum() {
        List<Marker> bookmarks = S.getDb().listMarkers(Marker.Kind.bookmark, 0, Db.Marker.createTime, false);
        mBookmarkNumTv.setText(bookmarks.size() > 0 ? String.valueOf(bookmarks.size()) : "");
    }

    public void setNotesNum() {
        List<Marker> notes = S.getDb().listMarkers(Marker.Kind.note, 0, Db.Marker.createTime, false);
        mNotesNumTv.setText(notes.size() > 0 ? String.valueOf(notes.size()) : "");
    }

    public void setPlansNum() {
        int planCount = S.getDb().getAllReadingPlanCount();
        mPlansNumTv.setText(planCount > 0 ? String.valueOf(planCount) : "");
    }

    public void refreshAllNum() {
        if (mCountTask != null) {
            mCountTask.cancel(true);
        }
        mCountTask = new LoadUserOperationCountTask();
        mCountTask.execute();
    }

    public void destroy() {
        if (mCountTask != null) {
            mCountTask.cancel(true);
            mCountTask = null;
        }
    }
}
