package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ReleaseConfig {
	
	public static ReleaseConfig releaseConfig;

	public String channel;
	public boolean isInternal;
	public String downloadBind;
	public String releaseDate;


	public ReleaseConfig(String channel, boolean isInternal, String downloadBind, String releaseDate) {
		this.channel = channel;
		this.isInternal = isInternal;
		this.downloadBind = downloadBind;
		this.releaseDate = releaseDate;
	}

	public static String getChannel(Context ctx) {
		if (releaseConfig == null) {
			initReleaseConfig(ctx);
		}
		return releaseConfig.channel;
	}

	public static boolean initReleaseConfig(Context ctx) {
		InputStream in = null;
		try {
			in = ctx.getAssets().open("info.properties");
			Properties properties = new Properties();
			properties.load(in);

			String channel = properties.getProperty("channel");
			boolean isInternal = Boolean.valueOf(properties.getProperty("isInternal"));
			String downloadBind = properties.getProperty("downloadBind", "");
			String releaseDate = properties.getProperty("releaseDate", "19700101");
			releaseConfig = new ReleaseConfig(channel, isInternal, downloadBind, releaseDate);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
}
