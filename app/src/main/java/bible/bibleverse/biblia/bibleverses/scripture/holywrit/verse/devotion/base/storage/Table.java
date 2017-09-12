package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.storage;

import static bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.storage.Table.Type.blob;
import static bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.storage.Table.Type.integer;
import static bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.storage.Table.Type.text;

public class Table {
	public static final String TAG = Table.class.getSimpleName();

	public enum Type {
		integer,
		real,
		text,
		blob,
	}
	
	public enum SongInfo {
		bookName(text),
		code(text),
		title(text, "collate nocase"),
		title_original(text, "collate nocase"),
		ordering(integer),
		dataFormatVersion(integer), 
		data(blob),
		updateTime(integer),
		;
		
		public final Type type;
		public final String suffix;
		
		SongInfo(Type type) {
			this(type, null);
		}
		
		SongInfo(Type type, String suffix) {
			this.type = type;
			this.suffix = suffix;
		}
		
		public static String tableName() {
			return SongInfo.class.getSimpleName();
		}
	}

	public enum SongBookInfo {
		name(text),
		title(text),
		copyright(text),
		;

		public final Type type;
		public final String suffix;

		SongBookInfo(Type type) {
			this(type, null);
		}

		SongBookInfo(Type type, String suffix) {
			this.type = type;
			this.suffix = suffix;
		}

		public static String tableName() {
			return SongBookInfo.class.getSimpleName();
		}
	}

	public enum PerVersion {
		versionId(text),
		settings(text),
		;

		public final Type type;
		public final String suffix;

		PerVersion(Type type) {
			this(type, null);
		}

		PerVersion(Type type, String suffix) {
			this.type = type;
			this.suffix = suffix;
		}

		public static String tableName() {
			return PerVersion.class.getSimpleName();
		}
	}
}
