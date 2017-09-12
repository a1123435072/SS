package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.storage;


public interface VerseTextDecoder {
	String[] separateIntoVerses(byte[] ba, boolean lowercased);
	String makeIntoSingleString(byte[] ba, boolean lowercased);
}
