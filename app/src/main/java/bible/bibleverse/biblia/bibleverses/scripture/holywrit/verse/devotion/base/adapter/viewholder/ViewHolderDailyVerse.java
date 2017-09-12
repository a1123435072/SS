package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import yuku.afw.V;

/**
 * Created by Mr_ZY on 2017/3/17.
 */

public class ViewHolderDailyVerse extends RecyclerView.ViewHolder {

    public static ViewHolderDailyVerse createViewHolder(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.home_item_daily_verse, parent, false);
        return new ViewHolderDailyVerse(view);
    }

    public View rootView, guideArrow, actionShare, actionLike;
    public TextView tvLikeNum, tvShareNum, tvReadAll, tvQuote, tvQuoteRefer;
    public ImageView image, arrow1, arrow2, arrow3;


    public ViewHolderDailyVerse(View itemView) {
        super(itemView);

        rootView = itemView;
        image = V.get(itemView, R.id.image);
        actionShare = V.get(itemView, R.id.action_share);
        actionLike = V.get(itemView, R.id.action_like);
        actionShare.setBackgroundResource(0);
        actionLike.setBackgroundResource(0);
        tvQuote = V.get(itemView, R.id.quote);
        tvQuoteRefer = V.get(itemView, R.id.quote_refer);
        tvLikeNum = V.get(itemView, R.id.like_num);
        tvShareNum = V.get(itemView, R.id.share_num);
        tvReadAll = V.get(itemView, R.id.read_all);
        guideArrow = V.get(itemView, R.id.guide_arrow);
        arrow1 = V.get(itemView, R.id.guide_arrow_1);
        arrow2 = V.get(itemView, R.id.guide_arrow_2);
        arrow3 = V.get(itemView, R.id.guide_arrow_3);
    }
}
