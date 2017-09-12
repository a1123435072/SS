package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr.GotoBooksFragment;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr.GotoGridFragment;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr.base.BaseGotoFragment;
import yuku.afw.App;
import yuku.afw.V;
import yuku.alkitab.model.Book;
import yuku.alkitab.util.Ari;
import yuku.alkitabintegration.display.Launcher;


public class GotoActivity extends BaseActivity implements BaseGotoFragment.SelectListener {
    public static final String TAG = GotoActivity.class.getSimpleName();

    private static final String EXTRA_bookId = "bookId";
    private static final String EXTRA_chapter = "chapter";
    private static final String EXTRA_verse = "verse";

    private static final String INSTANCE_STATE_tab = "tab";

    public int mBookId;
    public int mChapter_1;
    public int mVerse_1;
    boolean isBookChange;

    public static class Result {
        public int bookId;
        public int chapter_1;
        public int verse_1;
    }

    public static Intent createIntent(int bookId, int chapter_1, int verse_1) {
        Intent res = new Intent(App.context, GotoActivity.class);
        res.putExtra(EXTRA_bookId, bookId);
        res.putExtra(EXTRA_chapter, chapter_1);
        res.putExtra(EXTRA_verse, verse_1);
        return res;
    }

    public static Result obtainResult(Intent data) {
        Result res = new Result();
        res.bookId = data.getIntExtra(EXTRA_bookId, -1);
        res.chapter_1 = data.getIntExtra(EXTRA_chapter, 0);
        res.verse_1 = data.getIntExtra(EXTRA_verse, 0);
        return res;
    }

    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tablayout;
    View okBtn, cancleBtn;
    GotoPagerAdapter pagerAdapter;

    boolean okToHideKeyboard = false;

    int bookId;
    int chapter_1;
    int verse_1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goto);

        bookId = getIntent().getIntExtra(EXTRA_bookId, -1);
        chapter_1 = getIntent().getIntExtra(EXTRA_chapter, 0);
        verse_1 = getIntent().getIntExtra(EXTRA_verse, 0);

        toolbar = V.get(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_black);
            toolbar.setTitle(R.string.books);
            toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateUp();
                }
            });
        }

        // ViewPager and its adapters use support library fragments, so use getSupportFragmentManager.  兼容低版本
        viewPager = V.get(this, R.id.viewPager);
        viewPager.setAdapter(pagerAdapter = new GotoPagerAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (okToHideKeyboard && position != 0) {
//                    final View editText = V.get(GotoActivity.this, R.id.tDirectReference);
//                    if (editText != null) {
//                        final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                        imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
//                        imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//                    }
                }
            }
        });

        tablayout = V.get(this, R.id.tablayout);
        tablayout.setTabMode(TabLayout.MODE_FIXED);
        tablayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);
        viewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
//                final View editText = V.get(GotoActivity.this, R.id.tDirectReference);
//                if (editText != null) {
//                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
//                }
                okToHideKeyboard = true;
            }
        }, 100);


        okBtn = V.get(this, R.id.ok_btn);
        cancleBtn = V.get(this, R.id.cancle_btn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() == 0) {
                    saveAndQuit(mBookId, 1, 1);
                } else if (viewPager.getCurrentItem() == 1) {
                    saveAndQuit(mBookId, mChapter_1, 1);
                } else if (viewPager.getCurrentItem() == 2) {
                    saveAndQuit(mBookId, mChapter_1, mVerse_1);
                }
            }
        });

        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(final Menu menu) {
////        getMenuInflater().inflate(R.menu.activity_goto, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(final Menu menu) {
////        final MenuItem menuAskForVerse = menu.findItem(R.id.menuAskForVerse);
////        final boolean val = Preferences.getBoolean(Prefkey.gotoAskForVerse, Prefkey.GOTO_ASK_FOR_VERSE_DEFAULT);
////        menuAskForVerse.setChecked(val);
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(final MenuItem item) {
//        if (item.getItemId() == R.id.menuAskForVerse) {
//            final boolean val = Preferences.getBoolean(Prefkey.gotoAskForVerse, Prefkey.GOTO_ASK_FOR_VERSE_DEFAULT);
//            Preferences.setBoolean(Prefkey.gotoAskForVerse, !val);
//            supportInvalidateOptionsMenu();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(INSTANCE_STATE_tab, viewPager.getCurrentItem());
    }

    public class GotoPagerAdapter extends FragmentPagerAdapter {
        final int[] pageTitleResIds = {R.string.books, R.string.pasal_sebelumangka, R.string.ayat_sebelumangka};

        public GotoPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(final int position) {
            final Fragment res;
            if (position == 0) {
                res = Fragment.instantiate(GotoActivity.this, GotoBooksFragment.class.getName(), GotoBooksFragment.createArgs(bookId, chapter_1, verse_1));
            } else if (position == 1) {
                res = Fragment.instantiate(GotoActivity.this, GotoGridFragment.class.getName(), GotoGridFragment.createArgs(bookId, chapter_1, verse_1, GotoGridFragment.TYPE_CHAPTER));
            } else {
                res = Fragment.instantiate(GotoActivity.this, GotoGridFragment.class.getName(), GotoGridFragment.createArgs(bookId, chapter_1, verse_1, GotoGridFragment.TYPE_VERSE));
            }
            return res;
        }

        @Override
        public int getCount() {
            return pageTitleResIds.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(pageTitleResIds[position]);
        }

        public Fragment getFragment(int position) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            return fragmentManager.findFragmentByTag(getFragmentTag(position));
        }

        private String getFragmentTag(int position) {
            return "android:switcher:" + viewPager.getId() + ":" + getItemId(position);
        }
    }

    @Override
    public void onActionSelect(int bookId, int chapter_1, int verse_1) {
        mBookId = bookId;
        mChapter_1 = chapter_1;
        mVerse_1 = verse_1;
    }

    public void saveAndQuit(int bookId, int chapter_1, int verse_1) {
        int ari = Ari.encode(bookId, chapter_1, verse_1);
        startActivity(Launcher.openAppAtBibleLocationWithVerseSelected(ari));
        finish();
    }

    public void gotoChapter(Book book, int chapterId, int verseId, boolean isChange) {
        if (viewPager != null) {
            isBookChange = isChange;
            viewPager.setCurrentItem(1);
            getSupportFragmentManager();
            GotoGridFragment chapterGridFragment = (GotoGridFragment) pagerAdapter.getFragment(1);
            chapterGridFragment.refreshChapter(book, isBookChange ? chapterId : chapter_1);

            GotoGridFragment verseGridFragment = (GotoGridFragment) pagerAdapter.getFragment(2);
            verseGridFragment.refreshVerse(book, isBookChange ? chapterId : chapter_1, isBookChange ? verseId : verse_1);

            mBookId = book.bookId;
            mChapter_1 = isBookChange ? chapterId : chapter_1;
            mVerse_1 = isBookChange ? verseId : verse_1;
        }
    }

    public void gotoVerse(Book book, int chapterId) {
        if (viewPager != null) {
            viewPager.setCurrentItem(2);
            GotoGridFragment gotoGridFragment = (GotoGridFragment) pagerAdapter.getFragment(2);
            gotoGridFragment.refreshVerse(book, chapterId, !isBookChange && chapterId == chapter_1 ? verse_1 : 1);
            mBookId = book.bookId;
            mChapter_1 = chapterId;
            mVerse_1 = isBookChange ? 1 : verse_1;
        }
    }

    public interface RefreshListener {
        public void onRefresh(int page, Book book, int chapterId, int verseId);
    }
}
