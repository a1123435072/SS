package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.model.MVersion;

/**
 * Created by yzq on 16/11/18.
 */

public class EventNewVersionLoaded {
    public MVersion mv;

    public EventNewVersionLoaded(MVersion mv) {
        this.mv = mv;
    }
}
