package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import yuku.afw.V;

/**
 * Created by yzq on 2017/3/17.
 */

public class ViewHolderSeparator extends RecyclerView.ViewHolder {

    public static ViewHolderSeparator createViewHolder(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.home_item_separator, parent, false);
        return new ViewHolderSeparator(view);
    }

    public TextView tvTitle;

    public ViewHolderSeparator(View itemView) {
        super(itemView);
        tvTitle = V.get(itemView, R.id.separator_title);
    }
}
