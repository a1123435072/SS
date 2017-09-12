package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event;

/**
 * Created by yzq on 16/10/27.
 */

public class EventEditNoteDone {

    private int type;
    private boolean isCanceled;

    public EventEditNoteDone(int type, boolean isCanceled) {
        this.type = type;
        this.isCanceled = isCanceled;
    }
}
