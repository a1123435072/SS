package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model;

import java.io.Serializable;

/**
 * Created by zhangfei on 11/8/16.
 */
public class VerseItem implements Serializable {

    public int mAri;
    public int mVerseCount;
    public String mDate, mReference;

    public VerseItem(int mAri, int mVerseCount, String mDate, String mReference) {
        this.mAri = mAri;
        this.mVerseCount = mVerseCount;
        this.mDate = mDate;
        this.mReference = mReference;
    }

    public static VerseItem createVerseItem(int ari, int versionCount, String date, String reference) {
        VerseItem verseItem = new VerseItem(ari, versionCount, date, reference);
        return verseItem;
    }

}
