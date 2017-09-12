package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import yuku.alkitab.util.Ari;

/**
 * Created by yzq on 16/12/15.
 */

public class VerseItemView extends TextView {
    public static final int INDEX_COLOR = Color.parseColor("#777777");

    private int mStartAri;
    private int mVerseCount;

    private CommonSpanBuilder mSpanBuilder;

    public VerseItemView(Context context) {
        super(context);
        mSpanBuilder = new CommonSpanBuilder();
    }

    public VerseItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mSpanBuilder = new CommonSpanBuilder();
    }

    public void loadVerse(int ari, int verseCount) {
        mStartAri = ari;
        mVerseCount = verseCount;

        mSpanBuilder.clear();

        for (int i = 0; i < mVerseCount; ++i) {
            int lAri = mStartAri + i;
            int verseId = Ari.toVerse(lAri);
            String rawVerseText = S.getVerseByAri(lAri);
            if (TextUtils.isEmpty(rawVerseText)) {
                continue;
            }

            mSpanBuilder.append(" " + String.valueOf(verseId) + " ", new RelativeSizeSpan(0.6f), new ForegroundColorSpan(INDEX_COLOR));
            mSpanBuilder.append(rawVerseText);
            mSpanBuilder.append("\n");
        }

        setText(mSpanBuilder);
    }
}
