package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by yzq on 16/9/12.
 */

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals("notification_cancelled")) {
            int id = intent.getIntExtra("notification_id", -1);
            if (id != -1) {
                NotificationBase.setShowingNotificationStatus(context, id, false);
            }
        }
    }
}
