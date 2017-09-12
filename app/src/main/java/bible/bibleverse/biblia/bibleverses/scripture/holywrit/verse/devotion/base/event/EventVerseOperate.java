package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event;


/**
 * Created by Mr_ZY on 16/10/27.
 */

public class EventVerseOperate {
    public static final String HIGHLIGHTS = "highlight";
    public static final String NOTE = "note";
    public static final String BOOKMARK = "bookmark";
    public static final String IMAGES = "image";
    public static final String PLANS = "plan";
    public String operateType;

    public EventVerseOperate(String type) {
        operateType = type;
    }
}
