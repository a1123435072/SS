package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model;

import java.util.ArrayList;
import java.util.List;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.protobuf.BPProto;

public class ReadingPlan {

    public static class ReadingPlanInfo {
        public long mDbPlanId;
        public String mCategoryId;
        public String mTitle;
        public String mDescription;
        public String mSmallIconUrl;
        public String mBigImageUrl;
        public int mPlannedDayCount;
        public int mCompletedDayCount;
        public long mStartTime; //starting time of this reading plan in millis
        public long mCompletedTime;
        public String mExtensionData;
        public boolean mIsReminderOpen;
        public int mReminderTime;
        public int mServerQueryId;

        public List<ReadingDayPlanInfo> mDayPlans;
    }

    public static class ReadingDayPlanInfo {
        public long mId;
        public int mPlanId;
        public int mDayIndex;
        public boolean mIsCompleted;
        public List<ReadingDayPlanProgressInfo> mDayPlanProgresses;

        public void updateCompleted() {
            mIsCompleted = true;
            for (int i = 0; i < mDayPlanProgresses.size(); ++i) {
                if (mDayPlanProgresses.get(i).mIsCompleted == 0) {
                    mIsCompleted = false;
                    break;
                }
            }
        }
    }

    public static class ReadingDayPlanProgressInfo {
        public long mId;
        public int mAri;
        public int mVerseCount;
        public int mIsCompleted;
        public String mExtensionData;
    }

    //This item indicates existing completed plans,not the real completed plans.
    public static ReadingPlanInfo createCompletedIndicatorItem() {
        ReadingPlanInfo readingPlan = new ReadingPlanInfo();
        readingPlan.mPlannedDayCount = 1;
        readingPlan.mCompletedDayCount = 1;
        return readingPlan;
    }

    public static ReadingPlanInfo createPlanItemFromBP(BPProto.BP bp) {
        ReadingPlanInfo readingPlanInfo = new ReadingPlanInfo();
        readingPlanInfo.mServerQueryId = bp.getPlanId();
        readingPlanInfo.mCategoryId = String.valueOf(bp.getCategoryId());
        readingPlanInfo.mTitle = bp.getTitle();
        readingPlanInfo.mDescription = bp.getPlanDes();
        readingPlanInfo.mPlannedDayCount = bp.getDaysCount();
        readingPlanInfo.mCompletedDayCount = 0;
        readingPlanInfo.mSmallIconUrl = bp.getIconUrl();
        readingPlanInfo.mBigImageUrl = bp.getImageUrl();
        readingPlanInfo.mCompletedTime = 0;
        readingPlanInfo.mExtensionData = "";

        return readingPlanInfo;
    }

    public static List<ReadingDayPlanInfo> getDayPlanListFromBP(BPProto.BP bp, int dayIndexOffset) {
        List<ReadingDayPlanInfo> dayPlans = new ArrayList<>();
        List<BPProto.BP.SingleDayPlan> list = bp.getPlanDaysListList();
        for (int i = 0; i < list.size(); ++i) {
            ReadingDayPlanInfo dayPlanInfo = createDayPlanFromBPDayPlan(i + dayIndexOffset, list.get(i));
            dayPlans.add(dayPlanInfo);
        }

        return dayPlans;
    }

    public static ReadingPlanInfo createCategoryPlanItemFromBP(BPProto.BP bp) {
        ReadingPlanInfo readingPlanInfo = new ReadingPlanInfo();
        readingPlanInfo.mServerQueryId = bp.getPlanId();
        readingPlanInfo.mCategoryId = String.valueOf(bp.getCategoryId());
        readingPlanInfo.mTitle = bp.getTitle();
        readingPlanInfo.mPlannedDayCount = bp.getDaysCount();
        readingPlanInfo.mSmallIconUrl = bp.getIconUrl();
        readingPlanInfo.mBigImageUrl = bp.getImageUrl();
        return readingPlanInfo;
    }

    public static ReadingDayPlanInfo createDayPlanFromBPDayPlan(int dayIndex, BPProto.BP.SingleDayPlan singleDayPlan) {
        ReadingDayPlanInfo readingDayPlan = new ReadingDayPlanInfo();
        readingDayPlan.mDayIndex = dayIndex;
        readingDayPlan.mDayPlanProgresses = new ArrayList<>();
        List<BPProto.BP.SingleDayPlan.Verse> verseList = singleDayPlan.getVerseList();
        for (int i = 0; i < verseList.size(); ++i) {
            readingDayPlan.mDayPlanProgresses.add(createDayPlanProgressFromBPVerse(verseList.get(i)));
        }

        return readingDayPlan;
    }

    public static ReadingDayPlanProgressInfo createDayPlanProgressFromBPVerse(BPProto.BP.SingleDayPlan.Verse verse) {
        ReadingDayPlanProgressInfo progressInfo = new ReadingDayPlanProgressInfo();
        progressInfo.mAri = verse.getAri();
        progressInfo.mVerseCount = verse.getVerseCount();
        progressInfo.mExtensionData = "";
        progressInfo.mIsCompleted = 0;

        return progressInfo;
    }
}
