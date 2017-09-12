package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.DevotionBean;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.DevotionListResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PopularResponse;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.api.response.PrayerBean;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.DevotionAllListActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.DevotionDetailWebActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.DevotionSitesGuideActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.PrayerCategoryGridActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.PrayerDetailActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.DateTimeUtil;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import yuku.afw.V;

/**
 * Created by yzq on 2017/2/27.
 */

public class ViewHolderDevotionNoImage extends RecyclerView.ViewHolder {

    public static ViewHolderDevotionNoImage createViewHolder(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.home_item_devotion_no_image, parent, false);
        return new ViewHolderDevotionNoImage(view);
    }

    public View rootView;
    public View contentLayout, moreLayout;
    public TextView tvTitle, tvReadNum, tvSource, tvSnippet, tvMore;
    public View newFlag;

    private View shadowView, wideSegmentView, narrowSegmentView;


    public ViewHolderDevotionNoImage(View itemView) {
        super(itemView);

        rootView = itemView;

        contentLayout = V.get(itemView, R.id.content_layout);
        moreLayout = V.get(itemView, R.id.more_layout);

        tvTitle = V.get(itemView, R.id.title);
        tvReadNum = V.get(itemView, R.id.read_num);
        tvSource = V.get(itemView, R.id.source);
        tvSnippet = V.get(itemView, R.id.snippet);
        newFlag = V.get(itemView, R.id.new_tag_icon);

        shadowView = V.get(itemView, R.id.shadow_layout);
        wideSegmentView = V.get(itemView, R.id.wide_segment);
        narrowSegmentView = V.get(itemView, R.id.narrow_segment);
        tvMore = V.get(itemView, R.id.more_text);
    }

    public void bindHomeDevotion(final Context context,
                                 final DevotionBean devotion,
                                 boolean everRead,
                                 String currentDate,
                                 boolean isLastOne) {

        tvReadNum.setVisibility(View.VISIBLE);

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
                }
            });
        } else {
            moreLayout.setVisibility(View.GONE);
        }

        narrowSegmentView.setVisibility(View.GONE);
    }

    public void bindPopularDevotion(final Context context, final DevotionBean devotion, boolean isLastOne) {
        tvReadNum.setVisibility(View.VISIBLE);

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
            }
        });

        if (isLastOne) {
            tvMore.setText(R.string.more_devotions);
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

    public void bindPopularPrayer(final Context context, final PrayerBean prayer, boolean isLastOne) {
        tvReadNum.setVisibility(View.VISIBLE);

        tvTitle.setText(prayer.getTitle());
        tvSource.setText(prayer.getSource());
        tvSnippet.setText(Html.fromHtml(prayer.getContent()));
        tvReadNum.setText(String.valueOf(prayer.getView()));
        tvReadNum.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_prayer_palm, 0, 0, 0);
        newFlag.setVisibility(View.GONE);


        contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<PrayerBean> prays = new ArrayList<>();
                prays.add(prayer);
                Intent intent = PrayerDetailActivity.createIntentFromList(context, prays, 0, "", 0, prayer.getCateId());
                context.startActivity(intent);
            }
        });

        if (isLastOne) {
            tvMore.setText(R.string.more_prayers);
            moreLayout.setVisibility(View.VISIBLE);
            moreLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, PrayerCategoryGridActivity.class));
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
