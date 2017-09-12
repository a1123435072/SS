package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter.PopularListAdapter;
import yuku.afw.V;

/**
 * Created by yzq on 2017/3/17.
 */

public class ViewHolderPopularHeader extends RecyclerView.ViewHolder {

    public static ViewHolderPopularHeader createViewHolder(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.home_item_popular_header, parent, false);
        return new ViewHolderPopularHeader(view);
    }

    public TextView tvTitle;

    public ViewHolderPopularHeader(View itemView) {
        super(itemView);
        tvTitle = V.get(itemView, R.id.popular_title);
    }

    public void bindView(int type) {
        if (type == PopularListAdapter.ITEM_DEVOTION_HEADER) {
            tvTitle.setText(R.string.popular_devotion);
        } else {
            tvTitle.setText(R.string.popular_prayer);
        }
    }
}
