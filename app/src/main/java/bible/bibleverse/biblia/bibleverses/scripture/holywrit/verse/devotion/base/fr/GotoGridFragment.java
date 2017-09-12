package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fw.basemodules.utils.OmAsyncTask;

import java.util.HashSet;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.SelfAnalyticsHelper;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.UserBehaviorAnalytics;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.GotoActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr.base.BaseGotoFragment;
import yuku.afw.V;

import yuku.alkitab.model.Book;

public class GotoGridFragment extends BaseGotoFragment implements GotoActivity.RefreshListener {
    public static final String TAG = GotoGridFragment.class.getSimpleName();

    private static final String EXTRA_verse = "verse";
    private static final String EXTRA_chapter = "chapter";
    private static final String EXTRA_bookId = "bookId";
    private static final String EXTRA_type = "type";

    public static final int TYPE_CHAPTER = 1;
    public static final int TYPE_VERSE = 2;

    RecyclerView grid;

    int bookId;
    int chapter_1;
    int verse_1;
    int type;

    Book[] books;
    ChapterAdapter chapterAdapter;
    VerseAdapter verseAdapter;

    Book selectedBook;

    public static Bundle createArgs(int bookId, int chapter_1, int verse_1, int type) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_bookId, bookId);
        args.putInt(EXTRA_chapter, chapter_1);
        args.putInt(EXTRA_verse, verse_1);
        args.putInt(EXTRA_type, type);
        return args;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            bookId = args.getInt(EXTRA_bookId, -1);
            chapter_1 = args.getInt(EXTRA_chapter);
            verse_1 = args.getInt(EXTRA_verse);
            type = args.getInt(EXTRA_type);
        }
    }

    GridLayoutManager createLayoutManagerForNumbers() {
        return new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.goto_grid_numeric_num_columns));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View res = inflater.inflate(R.layout.fragment_goto_grid, container, false);
        grid = V.get(res, R.id.grid);
        return res;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        books = S.activeVersion.getConsecutiveBooks();
        selectedBook = getBook(books, bookId);
//        selectedBook=  S.activeVersion.getBook(bookId);
        grid.setLayoutManager(createLayoutManagerForNumbers());
        if (type == TYPE_CHAPTER) {
            grid.setAdapter(chapterAdapter = new ChapterAdapter(selectedBook));
        } else if (type == TYPE_VERSE) {
            grid.setAdapter(verseAdapter = new VerseAdapter(selectedBook, chapter_1));
        }
    }

    class LoadChapterProgressTask extends OmAsyncTask<Integer, Void, HashSet<Integer>> {

        @Override
        protected HashSet<Integer> doInBackground(Integer... integers) {
            if (integers.length > 0) {
                return S.getDb().getCompletedChapterId(S.activeVersionId, integers[0]);
            }
            return S.getDb().getCompletedChapterId(S.activeVersionId, bookId);
        }

        @Override
        protected void onPostExecute(HashSet<Integer> integers) {
            if (chapterAdapter != null) {
                chapterAdapter.setFinishedChapterIds(integers);
                chapterAdapter.notifyDataSetChanged();
            }
        }
    }

    public Book getBook(Book[] books, int bookId) {
        if (books == null)
            return null;
        for (int i = 0; i < books.length; i++) {
            if (books[i].bookId == bookId) {
                return books[i];
            }
        }
        return null;
    }

    public class VH extends RecyclerView.ViewHolder {
        public View view;
        public TextView tv;
        public ImageView icon;

        public VH(final View itemView) {
            super(itemView);
        }
    }

    abstract class GridAdapter extends RecyclerView.Adapter<VH> {
        @Override
        public VH onCreateViewHolder(final ViewGroup parent, final int viewType) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.item_goto_grid_cell, parent, false);
            VH vh = new VH(view);
            vh.view = view;
            vh.tv = (TextView) view.findViewById(R.id.item_chapter);
            vh.icon = (ImageView) view.findViewById(R.id.finished_icon);
            return vh;
        }

        @Override
        public void onBindViewHolder(final VH holder, final int position) {
            holder.tv.setText(textForView(position));
//            lName.setTextColor(textColorForView(position));
        }

        abstract CharSequence textForView(int position);

        int textColorForView(int position) {
            return 0xffffffff;
        }
    }

    class ChapterAdapter extends GridAdapter {
        private Book book;
        private HashSet<Integer> finishedChapterIds;

        public ChapterAdapter(Book book) {
            this.book = book;
            new LoadChapterProgressTask().execute(bookId);
        }

        public void setFinishedChapterIds(HashSet<Integer> chapterIds) {
            this.finishedChapterIds = chapterIds;
        }

        public void setBook(Book book, int chapterId) {
            this.book = book;
            chapter_1 = chapterId;
            new LoadChapterProgressTask().execute(book.bookId);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            if (book != null) {
                return book.chapter_count;
            } else {
                return 0;
            }
        }

        @Override
        public void onBindViewHolder(final VH holder, final int position) {
            super.onBindViewHolder(holder, position); // must call this
            holder.view.setSelected(chapter_1 == position + 1 ? true : false);
            if (finishedChapterIds != null && finishedChapterIds.contains(position + 1)) {
                holder.icon.setVisibility(View.VISIBLE);
                holder.icon.setSelected(chapter_1 == position + 1 ? true : false);
            } else {
                holder.icon.setVisibility(View.GONE);
            }

            holder.tv.setTextColor(ContextCompat.getColor(getActivity(), chapter_1 == position + 1 ? R.color.white : R.color.black_999));
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chapter_1 = position + 1;
                    ((GotoActivity) getActivity()).gotoVerse(book, chapter_1);
                    notifyDataSetChanged();
                    UserBehaviorAnalytics.trackUserBehavior(getActivity(), AnalyticsConstants.P_BOOKSPAGE, AnalyticsConstants.B_SELECTCHAPTER);
                }
            });
        }

        @Override
        CharSequence textForView(int position) {
            return String.valueOf(position + 1);
        }
    }

    class VerseAdapter extends GridAdapter {
        private Book book;
        private int chapter_1;

        public VerseAdapter(Book book, int chapter_1) {
            this.book = book;
            this.chapter_1 = chapter_1;
        }

        public void setVerse(Book book, int chapterId, int verseId) {
            this.book = book;
            this.chapter_1 = chapterId;
            verse_1 = verseId;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            int chapter_0 = chapter_1 - 1;
            return chapter_0 < 0 || chapter_0 >= book.verse_counts.length ? 0 : book.verse_counts[chapter_0];
        }

        @Override
        public void onBindViewHolder(final VH holder, final int position) {
            super.onBindViewHolder(holder, position);
            holder.view.setSelected(verse_1 == position + 1 ? true : false);
            holder.tv.setTextColor(ContextCompat.getColor(getActivity(), verse_1 == position + 1 ? R.color.white : R.color.black_999));

            holder.icon.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verse_1 = position + 1;
//                    ((SelectListener) getActivity()).onActionSelect(book.bookId, mCurrentChapter, verse_1);
                    ((GotoActivity) getActivity()).saveAndQuit(book.bookId, chapter_1, verse_1);
                    UserBehaviorAnalytics.trackUserBehavior(getActivity(), AnalyticsConstants.P_BOOKSPAGE, AnalyticsConstants.B_SELECTVERSE);
                    SelfAnalyticsHelper.sendReadVerseAnalytics(getActivity(), "books", "verse");
                }
            });
        }

        @Override
        CharSequence textForView(int position) {
            return String.valueOf(position + 1);
        }
    }

    @Override
    public void onRefresh(int page, Book book, int chapterId, int verseId) {
        if (page == type) {

        }
    }

    public void refreshChapter(Book book, int chapterId) {
        if (chapterAdapter != null) {
            chapterAdapter.setBook(book, chapterId);
        }
    }

    public void refreshVerse(Book book, int chapterId, int verseId) {
        if (verseAdapter != null) {
            verseAdapter.setVerse(book, chapterId, verseId);
        }
    }
}
