package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fw.basemodules.utils.OmAsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import at.grabner.circleprogress.CircleProgressView;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.AnalyticsConstants;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.analytics.UserBehaviorAnalytics;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.GotoActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr.base.BaseGotoFragment;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.BookNameSorter;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import yuku.afw.App;
import yuku.afw.V;
import yuku.afw.storage.Preferences;
import yuku.alkitab.model.Book;

public class GotoBooksFragment extends BaseGotoFragment {
    public static final String TAG = GotoBooksFragment.class.getSimpleName();

    private static final String EXTRA_verse = "verse";
    private static final String EXTRA_chapter = "chapter";
    private static final String EXTRA_bookId = "bookId";

    ListView bookLv;
    EditText bookEditText;
    List<Book> currentBookList;

    BookAdapter adapter;

    int bookId;
    int chapter_1;
    int verse_1;

    boolean isChangeBook;

    public static Bundle createArgs(int bookId, int chapter_1, int verse_1) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_bookId, bookId);
        args.putInt(EXTRA_chapter, chapter_1);
        args.putInt(EXTRA_verse, verse_1);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            bookId = args.getInt(EXTRA_bookId, -1);
            chapter_1 = args.getInt(EXTRA_chapter);
            verse_1 = args.getInt(EXTRA_verse);
        }
        currentBookList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View res = inflater.inflate(R.layout.fragment_goto_books, container, false);

        bookEditText = V.get(res, R.id.search_book_et);
        bookLv = V.get(res, R.id.select_books_lv);

        return res;
    }

    private void initSearchView() {
        bookEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                doSearch(bookEditText.getText().toString());
            }
        });

        bookEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (bookEditText != null) {
                        String keyword = bookEditText.getText().toString();
                        doSearch(keyword);
                        Utility.hideSoftKey(getActivity());
                    }
                    return true;
                }
                return false;
            }
        });

    }

    String lastkey = "";

    private void doSearch(String key) {
        if (TextUtils.isEmpty(key)) {
            if (!TextUtils.isEmpty(lastkey)) {
                adapter.setBook(adapter.getAllBooks());
            }
            lastkey = key;
            return;
        }
        if (adapter == null) {
            return;
        }
        List<Book> bookList = (key.length() < lastkey.length()) ? adapter.getAllBooks() : adapter.getCurrentBooks();
        currentBookList.clear();
        if (bookList != null && bookList.size() > 0) {
            for (Book book : bookList) {
                if (book.shortName.contains(key) || book.shortName.toLowerCase().contains(key)) {
                    currentBookList.add(book);
                }
            }
        }
        adapter.setBook(currentBookList);
        lastkey = key;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new LoadBooksReadingProgress().execute();

        initSearchView();
    }

    class LoadBooksReadingProgress extends OmAsyncTask<Void, Void, List<Book>> {

        @Override
        protected List<Book> doInBackground(Void... voids) {

            List<Book> list = new ArrayList<>();
            Book[] booksc = S.activeVersion.getConsecutiveBooks();
            HashMap<Integer, Integer> booksProgress = S.getDb().getBooksProgress(S.activeVersionId);
            for (int i = 0; i < booksc.length; ++i) {
                booksc[i].mChapterFinishCount = booksProgress.containsKey(i) ? booksProgress.get(i) : 0;
            }

            if (Preferences.getBoolean(App.context.getString(R.string.pref_alphabeticBookSort_key), App.context.getResources().getBoolean(R.bool.pref_alphabeticBookSort_default))) {
                Book[] books = BookNameSorter.sortAlphabetically(booksc);
                list.addAll(Arrays.asList(books));
            } else {
                list.addAll(Arrays.asList(booksc.clone()));
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<Book> books) {
            adapter = new BookAdapter(books);
            bookLv.setAdapter(adapter);
        }
    }

    static class ViewHolder {
        TextView nameTv;
        TextView readingProgressTv;
        CircleProgressView readingProgressView;
        ImageView readingFinishIv;
        View readingProgressLayout;
    }

    private class BookAdapter extends BaseAdapter {
        List<Book> booksc_;

        public BookAdapter(List<Book> books) {
            booksc_ = books;
        }

        public List<Book> getAllBooks() {
            Book[] booksc = S.activeVersion.getConsecutiveBooks();
            return Arrays.asList(booksc.clone());
        }

        /**
         * @return 0 when not found (not -1, because we just want to select the first book)
         */
        public int getPositionFromBookId(int pos) {
            if (booksc_ == null || pos > booksc_.size() - 1)
                return 0;
            for (int i = 0; i < booksc_.size(); i++) {
                if (booksc_.get(i).bookId == pos) {
                    return i;
                }
            }
            return 0;
        }

        public List<Book> getCurrentBooks() {
            if (booksc_ == null) {
                Book[] booksc = S.activeVersion.getConsecutiveBooks();
                booksc_ = Arrays.asList(booksc.clone());
            }
            return booksc_;
        }

        public void setBook(List<Book> books) {
            booksc_.clear();
            booksc_.addAll(books);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return booksc_.size();
        }

        @Override
        public Book getItem(int position) {
            return booksc_.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_select_books, parent, false);
                viewHolder.nameTv = (TextView) convertView.findViewById(R.id.book_name);
                viewHolder.readingProgressLayout = convertView.findViewById(R.id.book_reading_progress_layout);
                viewHolder.readingProgressView = (CircleProgressView) convertView.findViewById(R.id.book_reading_progress);
                viewHolder.readingProgressTv = (TextView) convertView.findViewById(R.id.book_reading_progress_value);
                viewHolder.readingFinishIv = (ImageView) convertView.findViewById(R.id.book_reading_iv);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final Book book = getItem(position);
            viewHolder.nameTv.setText(book.shortName);
            if (getPositionFromBookId(bookId) == position) {
                viewHolder.nameTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.accent));
            } else {
                viewHolder.nameTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.black_999));
            }
            if (book.mChapterFinishCount == book.chapter_count && book.chapter_count > 0) {
                viewHolder.readingProgressLayout.setVisibility(View.GONE);
                viewHolder.readingFinishIv.setVisibility(View.VISIBLE);
            } else if (book.mChapterFinishCount > 0 && book.chapter_count > 0) {
                viewHolder.readingProgressLayout.setVisibility(View.VISIBLE);
                viewHolder.readingFinishIv.setVisibility(View.GONE);
                int progress = book.mChapterFinishCount * 100 / book.chapter_count;
                viewHolder.readingProgressView.setValue(progress);
                viewHolder.readingProgressTv.setText(progress + "%");
            } else {
                viewHolder.readingProgressLayout.setVisibility(View.GONE);
                viewHolder.readingFinishIv.setVisibility(View.GONE);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isChangeBook && getPositionFromBookId(bookId) == position) {
                        gotoChapter(book, chapter_1, verse_1, false);
                        isChangeBook = false;
                        ((SelectListener) getActivity()).onActionSelect(book.bookId, chapter_1, verse_1);
                    } else {
                        gotoChapter(book, 1, 1, true);
                        isChangeBook = true;
                    }
                    bookId = book.bookId;
                    notifyDataSetChanged();
                    UserBehaviorAnalytics.trackUserBehavior(getActivity(), AnalyticsConstants.P_BOOKSPAGE, AnalyticsConstants.B_SELECTBOOKS);
                }
            });
            return convertView;
        }

        public void gotoChapter(Book book, int chapterId, int verseId, boolean isChange) {
            ((GotoActivity) getActivity()).gotoChapter(book, chapterId, verseId, isChange);
        }
    }
}
