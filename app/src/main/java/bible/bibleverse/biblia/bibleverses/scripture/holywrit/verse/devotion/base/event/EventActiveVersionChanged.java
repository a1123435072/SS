package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.MVersion;

/**
 * Created by yzq on 16/10/27.
 */

public class EventActiveVersionChanged {
    public MVersion mv;

    public EventActiveVersionChanged(MVersion mv) {
        this.mv = mv;
    }
}
