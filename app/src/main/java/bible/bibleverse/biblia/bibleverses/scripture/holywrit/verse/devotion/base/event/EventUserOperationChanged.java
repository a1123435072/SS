package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event;

/**
 * Created by yzq on 2017/3/9.
 */

public class EventUserOperationChanged {

    public static final int TYPE_DAILY_VERSE = 1;
    public static final int TYPE_READ_BIBLE_TIME = 2;
    public static final int TYPE_PRAY = 3;

    public int mType;

    public EventUserOperationChanged(int type) {
        mType = type;
    }
}
