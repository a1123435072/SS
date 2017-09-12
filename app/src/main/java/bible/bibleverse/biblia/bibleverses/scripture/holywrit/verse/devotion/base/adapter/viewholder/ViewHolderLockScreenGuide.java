package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import yuku.afw.V;

/**
 * Created by yzq on 2017/2/27.
 */

public class ViewHolderLockScreenGuide extends RecyclerView.ViewHolder {
    public View rootView;
    public View contentLayout;
    public ImageView image;
    public TextView tvTitle, tvSnippet, tvOpen;

    public ViewHolderLockScreenGuide(View itemView) {
        super(itemView);

        rootView = itemView;

        contentLayout = V.get(itemView, R.id.content_layout);

        image = V.get(itemView, R.id.image);
        tvTitle = V.get(itemView, R.id.title);
        tvSnippet = V.get(itemView, R.id.snippet);
        tvOpen = V.get(itemView, R.id.open_button);
    }

    public void bindLockScreenView(final Context context, int imgWidth) {

        int height = imgWidth * 510 / 936;
        Picasso.with(context).load(R.drawable.ic_charge_guide_bg).resize(imgWidth, height).into(image);

        tvTitle.setText(context.getString(R.string.guide_tiitle));
        tvSnippet.setText(context.getString(R.string.finish_ad_page_charge_summary));
    }

}
