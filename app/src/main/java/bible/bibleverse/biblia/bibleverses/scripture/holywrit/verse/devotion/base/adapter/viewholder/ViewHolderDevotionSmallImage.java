package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.SelfAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.DevotionBean;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.DevotionListResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PopularResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.DevotionAllListActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.DevotionDetailWebActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.DevotionSitesGuideActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.DateTimeUtil;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import yuku.afw.V;

/**
 * Created by yzq on 2017/2/27.
 */

public class ViewHolderDevotionSmallImage extends RecyclerView.ViewHolder {

    public static ViewHolderDevotionSmallImage createViewHolder(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.home_item_devotion_small_image, parent, false);
        return new ViewHolderDevotionSmallImage(view);
    }


    public View rootView;
    public View contentLayout, moreLayout, adFlag;
    public ImageView image;
    public TextView tvTitle, tvReadNum, tvSource, tvSnippet;
    public View newFlag;

    private View shadowView, wideSegmentView, narrowSegmentView;

    public ViewHolderDevotionSmallImage(View itemView) {
        super(itemView);

        rootView = itemView;

        contentLayout = V.get(itemView, R.id.content_layout);
        adFlag = V.get(itemView, R.id.ad_flag);
        moreLayout = V.get(itemView, R.id.more_layout);

        image = V.get(itemView, R.id.image);
        tvTitle = V.get(itemView, R.id.title);
        tvReadNum = V.get(itemView, R.id.read_num);
        tvSource = V.get(itemView, R.id.source);
        tvSnippet = V.get(itemView, R.id.snippet);
        newFlag = V.get(itemView, R.id.new_tag_icon);

        shadowView = V.get(itemView, R.id.shadow_layout);
        wideSegmentView = V.get(itemView, R.id.wide_segment);
        narrowSegmentView = V.get(itemView, R.id.narrow_segment);
    }

    public void bindHomeDevotion(
            final Context context,
            final DevotionBean devotion,
            boolean everRead,
            String currentDate,
            boolean isLastOne) {
        adFlag.setVisibility(View.GONE);
        tvReadNum.setVisibility(View.VISIBLE);
        newFlag.setVisibility(View.VISIBLE);

        Picasso.with(context).load(devotion.getImageUrl()).fit().centerCrop().into(image);
        tvTitle.setText(devotion.getTitle());
        tvSource.setText(devotion.getSource());
        tvSnippet.setText(Html.fromHtml(devotion.getContent()));
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
                    SelfAnalyticsHelper.sendHomeActionAnalytics(context, AnalyticsConstants.A_CLICK_CARD_DEVOTION);
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
        newFlag.setVisibility(View.VISIBLE);

        Picasso.with(context).load(devotion.getImageUrl()).fit().centerCrop().into(image);
        tvTitle.setText(devotion.getTitle());
        tvSource.setText(devotion.getSource());
        tvSnippet.setText(Html.fromHtml(devotion.getContent()));
        tvReadNum.setText(String.valueOf(devotion.getView()));
        tvReadNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_hot, 0, 0, 0);
        newFlag.setVisibility(View.GONE);

        contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = DevotionDetailWebActivity.createIntent(context, devotion);
                context.startActivity(intent);
                SelfAnalyticsHelper.sendReadRelatedAnalytics(context, String.valueOf(devotion.getSiteId()), devotion.getId());
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
