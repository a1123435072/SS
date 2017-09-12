package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

public abstract class Foreground {
	static final Handler handler = new Handler(Looper.getMainLooper());

	public static void run(@NonNull Runnable r) {
		handler.post(r);
	}
}
