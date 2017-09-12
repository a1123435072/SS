package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter.viewholder;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.SelfAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.VerseListResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.NewDailyVerseDetailActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.DateTimeUtil;
import yuku.afw.V;

/**
 * Created by yzq on 2017/3/6.
 */

public class ViewHolderRelatedDailyVerse extends RecyclerView.ViewHolder {
    public LinearLayout devotionView;
    ImageView icon;
    TextView name;
    TextView date;
    View adFlag;
    TextView adAction;

    public ViewHolderRelatedDailyVerse(View itemView) {
        super(itemView);

        devotionView = V.get(itemView, R.id.devotionView);
        adFlag = V.get(itemView, R.id.ad_flag);
        adAction = V.get(itemView, R.id.ad_action);
        icon = V.get(itemView, R.id.devotionImg);
        name = V.get(itemView, R.id.devotionTitle);
        date = V.get(itemView, R.id.devotionDate);
    }


    public void bindDailyVerse(final Context context, final VerseListResponse.DataBean.VerseBean verseBean,
                               final boolean useSharedTransfer) {
        adFlag.setVisibility(View.GONE);
        adAction.setVisibility(View.GONE);
        Picasso.with(context).load(verseBean.getImageThumbUrl()).into(icon);
        name.setText(verseBean.getTitle());
        date.setText(DateTimeUtil.getLocaleDateStr4Display(verseBean.getDate()));

        devotionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(
                        NewDailyVerseDetailActivity.createIntent(
                                context,
                                verseBean.getId(),
                                verseBean.getTitle(),
                                verseBean.getImageThumbUrl()));
                SelfAnalyticsHelper.sendVerseReadAnalytics(context, AnalyticsConstants.A_VERSE_CLICK_RELATED);
            }
        });
    }
}
