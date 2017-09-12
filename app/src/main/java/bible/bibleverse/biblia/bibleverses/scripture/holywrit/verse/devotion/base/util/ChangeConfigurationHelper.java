package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util;

import java.util.concurrent.atomic.AtomicInteger;

public class ChangeConfigurationHelper {
	private static AtomicInteger serialCounter = new AtomicInteger();

	public static int getSerialCounter() {
		return serialCounter.get();
	}

	public static void notifyConfigurationNeedsUpdate() {
		serialCounter.incrementAndGet();
	}
}
