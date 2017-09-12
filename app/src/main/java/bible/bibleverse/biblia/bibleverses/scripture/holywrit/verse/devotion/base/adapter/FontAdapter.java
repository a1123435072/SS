package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.FontManager;
import yuku.afw.V;

/**
 * Created by yzq on 16/11/5.
 */

class TypefaceItemHolder extends RecyclerView.ViewHolder {
    protected View view;
    protected TextView fontName;

    public TypefaceItemHolder(View view) {
        super(view);
        this.view = view;
        this.fontName = V.get(view, android.R.id.text1);
    }
}

public class FontAdapter extends RecyclerView.Adapter<TypefaceItemHolder> {

    public interface FontSelectedListener {
        void OnFontSelected(String fontName);
    }

    private String[] defaultFontNames = {"Roboto", "Droid Serif", "Droid Mono"};
    private Typeface[] defaultTypes = {Typeface.SANS_SERIF, Typeface.SERIF, Typeface.MONOSPACE};
    private int selectedFontPosition;

    private FontSelectedListener fontSelectedListener;
    private Context context;

    List<FontManager.FontEntry> fontEntries;

    public FontAdapter(Context context, FontSelectedListener listener) {
        this.context = context;
        this.fontSelectedListener = listener;
        selectedFontPosition = 0;
        reload();
    }

    public void setSelectedFontPosition(int position) {
        selectedFontPosition = position;
    }

    @Override
    public TypefaceItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, null);
        TypefaceItemHolder ml = new TypefaceItemHolder(v);
        return ml;
    }

    @Override
    public void onBindViewHolder(TypefaceItemHolder holder, final int position) {

        holder.fontName.setLines(1); // do not wrap long font names
        holder.fontName.setEllipsize(TextUtils.TruncateAt.END);
        if (position == selectedFontPosition) {
            holder.fontName.setTextColor(context.getResources().getColor(R.color.color_font_name_selected));
        } else {
            holder.fontName.setTextColor(context.getResources().getColor(R.color.color_font_name_default));
        }

        if (position < 3) {
            holder.fontName.setText(defaultFontNames[position]);
            holder.fontName.setTypeface(defaultTypes[position]);
        } /*else if (position == getItemCount() - 1) {
                holder.fontName.setText(App.context.getString(R.string.get_more_fonts));
                holder.fontName.setTypeface(Typeface.DEFAULT);
            }*/ else {
            int idx = position - 3;
            holder.fontName.setText(fontEntries.get(idx).name);
            holder.fontName.setTypeface(FontManager.typeface(fontEntries.get(idx).name));
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = getNameByPosition(position);
                if (name == null) {
//                    context.startActivityForResult(FontManagerActivity.createIntent(), reqcodeGetFonts);
                } else {
                    if (fontSelectedListener != null) {
                        fontSelectedListener.OnFontSelected(name);
                    }
                    FontAdapter.this.setSelectedFontPosition(position);
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 3 + fontEntries.size()/* + 1*/;
    }

    public void reload() {
        fontEntries = FontManager.getInstalledFonts();
        notifyDataSetChanged();
    }

    public String getNameByPosition(int position) {
        if (position < 3) {
            return new String[]{"DEFAULT", "SERIF", "MONOSPACE"}[position];
        } else if (position < getItemCount() /*- 1*/) {
            int idx = position - 3;
            return fontEntries.get(idx).name;
        } else {
            return null;
        }
    }

    public int getPositionByName(String name) {
        if (name == null) return -1;
        switch (name) {
            case "DEFAULT":
                return 0;
            case "SERIF":
                return 1;
            case "MONOSPACE":
                return 2;
            default:
                for (int i = 0; i < fontEntries.size(); i++) {
                    if (fontEntries.get(i).name.equals(name)) {
                        return i + 3;
                    }
                }
                break;
        }
        return -1;
    }

    public static Typeface getTypefaceByName(String fontName) {
        if (fontName == null) return Typeface.SANS_SERIF;
        switch (fontName) {
            case "DEFAULT":
                return Typeface.SANS_SERIF;
            case "SERIF":
                return Typeface.SERIF;
            case "MONOSPACE":
                return Typeface.MONOSPACE;
            default:
                return Typeface.SANS_SERIF;
        }
    }
}