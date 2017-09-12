package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventScreenOn;
import de.greenrobot.event.EventBus;


public class ScreenOnReceiver extends BroadcastReceiver {

    public ScreenOnReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            EventBus.getDefault().post(new EventScreenOn());
        }
    }
}
