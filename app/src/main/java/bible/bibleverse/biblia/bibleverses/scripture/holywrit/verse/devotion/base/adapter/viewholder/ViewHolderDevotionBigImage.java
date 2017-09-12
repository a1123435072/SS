package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.DevotionBean;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.DevotionAllListActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.DevotionDetailWebActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.DevotionSitesGuideActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.DateTimeUtil;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import yuku.afw.V;

/**
 * Created by yzq on 2017/2/27.
 */

public class ViewHolderDevotionBigImage extends RecyclerView.ViewHolder {

    public static ViewHolderDevotionBigImage createViewHolder(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.home_item_devotion_big_image, parent, false);
        return new ViewHolderDevotionBigImage(view);
    }

    public View rootView;
    public View contentLayout, moreLayout, adFlag, sourceLayout;
    public ImageView image, btnPlay;
    public TextView tvTitle, tvReadNum, tvSource;
    public TextView tvSnippet;
    public View newFlag;
    View imageCover;

    private View shadowView, wideSegmentView, narrowSegmentView;

    private boolean mHasRegisteredAd;

    public ViewHolderDevotionBigImage(View itemView) {
        super(itemView);

        rootView = itemView;

        contentLayout = V.get(itemView, R.id.content_layout);
        adFlag = V.get(itemView, R.id.ad_flag);
        moreLayout = V.get(itemView, R.id.more_layout);

        image = V.get(itemView, R.id.image);
        imageCover = V.get(itemView, R.id.image_cover);
        tvTitle = V.get(itemView, R.id.title);
        tvSnippet = V.get(itemView, R.id.snippet);
        tvReadNum = V.get(itemView, R.id.read_num);
        tvSource = V.get(itemView, R.id.source);
        newFlag = V.get(itemView, R.id.new_tag_icon);
        sourceLayout = V.get(itemView, R.id.source_layout);
        btnPlay = V.get(itemView, R.id.play_btn);

        shadowView = V.get(itemView, R.id.shadow_layout);
        wideSegmentView = V.get(itemView, R.id.wide_segment);
        narrowSegmentView = V.get(itemView, R.id.narrow_segment);
    }



    public void bindHomeDevotion(final Context context,
                                 final DevotionBean devotion,
                                 boolean everRead,
                                 String currentDate,
                                 boolean isLastOne) {
        adFlag.setVisibility(View.GONE);
        tvReadNum.setVisibility(View.VISIBLE);
        tvSnippet.setVisibility(View.GONE);
        btnPlay.setVisibility(View.VISIBLE);
        imageCover.setVisibility(View.VISIBLE);

        Picasso.with(context).load(devotion.getImageUrl()).fit().centerCrop().into(image);
        tvTitle.setText(devotion.getTitle());
        sourceLayout.setVisibility(View.VISIBLE);
        tvSource.setText(devotion.getSource());
        tvReadNum.setText(String.valueOf(devotion.getView()));
        if (devotion.getIsHot() == 1) {
            tvReadNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_hot, 0, 0, 0);
        } else {
            tvReadNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_view, 0, 0, 0);
        }

        if (everRead) {
            newFlag.setVisibility(View.INVISIBLE);
            tvTitle.setTextColor(context.getResources().getColor(R.color.black_999999));
            tvSnippet.setTextColor(context.getResources().getColor(R.color.black_999999));
        } else {
            tvTitle.setTextColor(context.getResources().getColor(R.color.black));
            tvSnippet.setTextColor(context.getResources().getColor(R.color.black));
            String yesterday = DateTimeUtil.getDateWithOffset(currentDate, -1);

            if (devotion.getDate().equalsIgnoreCase(currentDate) || devotion.getDate().equalsIgnoreCase(yesterday)) {
                newFlag.setVisibility(View.VISIBLE);
            } else {
                newFlag.setVisibility(View.INVISIBLE);
            }
        }

        // last one
        if (isLastOne) {
            moreLayout.setVisibility(View.VISIBLE);
            moreLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, Utility.isRecommendDevotionSite(context) ? DevotionSitesGuideActivity.class : DevotionAllListActivity.class));
                }
            });
        } else {
            moreLayout.setVisibility(View.GONE);
        }
        narrowSegmentView.setVisibility(View.GONE);
    }

    public void bindPopularDevotion(final Context context, final DevotionBean devotion, boolean isLastOne) {
        adFlag.setVisibility(View.GONE);
        tvReadNum.setVisibility(View.VISIBLE);
        tvSnippet.setVisibility(View.GONE);
        btnPlay.setVisibility(View.VISIBLE);
        imageCover.setVisibility(View.VISIBLE);

        Picasso.with(context).load(devotion.getImageUrl()).fit().centerCrop().into(image);
        tvTitle.setText(devotion.getTitle());
        sourceLayout.setVisibility(View.VISIBLE);
        tvSource.setText(devotion.getSource());
        tvReadNum.setText(String.valueOf(devotion.getView()));
        tvReadNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_hot, 0, 0, 0);
        newFlag.setVisibility(View.GONE);

        contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = DevotionDetailWebActivity.createIntent(context, devotion);
                context.startActivity(intent);
            }
        });
        if (isLastOne) {
            moreLayout.setVisibility(View.VISIBLE);
            moreLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, Utility.isRecommendDevotionSite(context) ? DevotionSitesGuideActivity.class : DevotionAllListActivity.class));
                }
            });
            shadowView.setVisibility(View.VISIBLE);
            wideSegmentView.setVisibility(View.VISIBLE);
            narrowSegmentView.setVisibility(View.GONE);
        } else {
            moreLayout.setVisibility(View.GONE);
            wideSegmentView.setVisibility(View.GONE);
            shadowView.setVisibility(View.GONE);
            narrowSegmentView.setVisibility(View.VISIBLE);
        }
    }
}
