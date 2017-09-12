package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by miaohongwei on 2017/8/25.
 */

public class AnalyticsUtils {

    private static String UUID;
    public static String getUUID(Context context){
        if(UUID == null) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            String deviceId = tm == null ? "" : (tm.getDeviceId() == null ? "" : tm.getDeviceId());
            String androidId = android.provider.Settings.System.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

            String serialNo = getDeviceSerial();
            UUID = stringToMD5("" + deviceId + androidId + serialNo);
        }

        return UUID;
    }

    private static String getDeviceSerial() {
        return android.os.Build.SERIAL;
    }

    private static String stringToMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }


}
