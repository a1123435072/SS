package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr.base;

public interface LoadResultListener {
	/**
	 * No matter what result is.
	 */
	void onUpdateUI(int code);


	void failedLoadAtStart(int code);


	void failedLoadNextPage(int code);


	void finishLoad(int code);


	void continueLoad(int code);

}
