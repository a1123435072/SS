package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util;

import android.content.Context;
import android.view.View;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.view.Dialog;


/**
 * Created by zhangfei on 12/14/15.
 */
public class DialogUtils {
    public static Dialog showDialog(Context context, String title, String msg, int negativeResId, View.OnClickListener negativeOnClickListener,
                                    int positiveResId, View.OnClickListener positiveOnClickListener) {
        Dialog dialog = new Dialog(context);
        return dialog.setTitle(title)
                .setMessage(msg)
                .setNegativeButton(negativeResId, negativeOnClickListener)
                .setPositiveButton(positiveResId, positiveOnClickListener)
                .setBackground(R.drawable.corners_bg)
                .show();
    }


    public static Dialog showCustomDialog(Context context, View customView, int negativeResId, View.OnClickListener negativeOnClickListener,
                                          int positiveResId, View.OnClickListener positiveOnClickListener) {
        Dialog dialog = new Dialog(context);
        return dialog.setNegativeButton(negativeResId, negativeOnClickListener)
                .setPositiveButton(positiveResId, positiveOnClickListener)
                .setView(customView)
                .show();
    }

}
