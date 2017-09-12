package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;

/**
 * Created by zhangfei on 10/18/16.
 */
public class ImageUtil {

    public static String saveBitmapFile(Context context, View view) {
        Bitmap bitmap = loadBitmapFromView(view, view.getWidth(), view.getHeight());
//        return saveBitmap(context, bitmap);
        return compressAndSaveImage(context, bitmap, getFinalSizeOfBitmap(bitmap.getWidth(), bitmap.getHeight(), 1080));
    }

    private static Bitmap loadBitmapFromView(View v, int width, int height) {
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        Bitmap viewBmp = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        return viewBmp;
    }

    private static String saveBitmap(Context context, Bitmap bm, String savePath) {
        try {
            OutputStream output = new FileOutputStream(savePath);
            bm.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return savePath;
    }

    //compress
    public static String compressAndSaveImage(Context context, Bitmap bitmapOrigin, int targetSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapOrigin.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        String finalPath = getSavePath(context);

        if (baos.toByteArray().length <= targetSize * 1024) {
            return saveBitmap(context, bitmapOrigin, finalPath);
        } else {
            int d = 4;
            int options = 100 - d;
            while (baos.toByteArray().length / 1024 > targetSize) {
                baos.reset();
                bitmapOrigin.compress(Bitmap.CompressFormat.JPEG, options, baos);
                options -= d;
            }
            try {
                FileOutputStream bmpFile = new FileOutputStream(finalPath);
                bitmapOrigin.compress(Bitmap.CompressFormat.JPEG, options, bmpFile);
                bmpFile.flush();
                bmpFile.close();
            } catch (Exception e) {
            }
        }


        return finalPath;
    }

    public static String getSavePath(Context context) {
        Calendar cal = Calendar.getInstance();
        String folderStr = getSaveDirPath(context);
        return folderStr + "/" + cal.getTimeInMillis() + ".png";
    }

    public static String getSaveDirPath(Context context) {
        if (context == null) {
            return "";
        }
        String folderStr = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + context.getString(R.string.app_name);
        File folder = new File(folderStr);
        if (!folder.exists()) {
            folder.mkdir();
        }
        File imgDir = new File(folder.getPath(), Constants.IMAGE_SAVE_PATH);
        if (!imgDir.exists()) {
            imgDir.mkdir();
        }
        return imgDir.getAbsolutePath();
    }

    /**
     * Get the final upload size of Bitmap.
     *
     * @param originWidth
     * @param originHeight
     * @param reqMaxSize
     * @return
     */
    public static int getFinalSizeOfBitmap(int originWidth, int originHeight, int reqMaxSize) {
        int level1 = 128;
        int level2 = 160;
        int level3 = 192;

        int judgeSide = originWidth;
        if (originWidth > (originHeight * 2.5)) {
            judgeSide = originHeight;
        }

        float ratio = (float) Math.max(originWidth, originHeight) / (float) Math.min(originWidth, originHeight);

        if (judgeSide >= reqMaxSize) {
            if (ratio > 2.5) {
                return level3;
            } else if (ratio == 2.5) {
                return level2;
            } else {
                return level1;
            }

        } else {
            if (ratio > 2.5) {
                return level2;
            } else if (ratio == 2.5) {
                return level1;
            } else {
                return level1;
            }

        }
    }

    //load large bitmap
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static String saveToFile(String fileFolderStr, boolean isDir, Bitmap image) {
        File jpgFile;
        if (isDir) {
            File fileFolder = new File(fileFolderStr);
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间
            String filename = format.format(date) + ".jpg";
            if (!fileFolder.exists()) {
                mkdir(fileFolder);
            }
            jpgFile = new File(fileFolder, filename);
        } else {
            jpgFile = new File(fileFolderStr);
            if (!jpgFile.getParentFile().exists()) {
                mkdir(jpgFile.getParentFile());
            }
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(jpgFile);
            image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            return jpgFile.getPath();
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean mkdir(File file) {
        while (!file.getParentFile().exists()) {
            mkdir(file.getParentFile());
        }
        return file.mkdir();
    }

    public static void shareImg(Context context, String path) {
        try {
            if (TextUtils.isEmpty(path)) {
                return;
            }
            File apkFile = new File(path);
            Intent it = new Intent(Intent.ACTION_SEND);
            it.setType("image/*");

            if (apkFile != null) {
                it.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(apkFile));
            }
            Intent chooser = Intent.createChooser(it, context.getResources().getString(R.string.share));
            if (chooser == null) {
                return;
            }
            context.startActivity(chooser);
        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(context, context.getString(R.string.share_failed), Toast.LENGTH_SHORT).show();
        }
    }

    public static void setBackground(Context context, View view, int resId) {
        if (context == null || view == null) {
            return;
        }
        if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(resId == 0 ? null : ContextCompat.getDrawable(context, resId));
        } else {
            view.setBackground(resId == 0 ? null : ContextCompat.getDrawable(context, resId));
        }
    }
}
