package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fw.basemodules.utils.OmAsyncTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventVerseOperate;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.util.DateTimeUtil;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.ImageUtil;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import de.greenrobot.event.EventBus;
import yuku.afw.V;
import yuku.afw.widget.EasyAdapter;

/**
 * Created by yzq on 16/11/5.
 */

public class BibleImagesListActivity extends BaseActivity {

    private String CREATED_IMAGES_DIR;

    View mRoot;
    View mProgressLayout;
    SwipeRefreshLayout mRefreshLayout;
    ListView mListView;
    View mEmptyLayout;
    TextView mEmptyTitle;
    TextView mEmptyMsg;
    ImageView mEmptyIcon;
    private PopupWindow mPopupWindow;

    private ImageListAdapter mImagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_list);

        initToolbar();

        initView();

        new LoadImageTask().execute();
    }

    private void initToolbar() {
        final Toolbar toolbar = V.get(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_back_black);
    }

    private void initView() {
        mRoot = V.get(this, R.id.root);
        mEmptyLayout = V.get(this, android.R.id.empty);
        mEmptyTitle = V.get(this, R.id.tEmpty);
        mEmptyMsg = V.get(this, R.id.msgEmpty);
        mEmptyIcon = V.get(this, R.id.empty_icon);
        mProgressLayout = V.get(this, R.id.loading_layout);
        mRefreshLayout = V.get(this, R.id.refreshLayout);
        mListView = V.get(this, android.R.id.list);
        CREATED_IMAGES_DIR = ImageUtil.getSaveDirPath(this);
        setTitleAndNothingText();
        mListView.setEmptyView(mEmptyLayout);
        mRefreshLayout.setColorSchemeResources(R.color.theme_color_accent);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
    }

    SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            new LoadImageTask().execute();
        }
    };

    private void setTitleAndNothingText() {
        String title = getString(R.string.images);
        String nothingTitle = getString(R.string.empty_images_title);
        String nothingText = getString(R.string.empty_images_msg);
        int notingIcon = R.drawable.ic_empty_images;

        setTitle(title);
        mEmptyTitle.setText(nothingTitle);
        mEmptyMsg.setText(nothingText);
        mEmptyIcon.setImageResource(notingIcon);
    }

    class LoadImageTask extends OmAsyncTask<Void, Void, Void> {

        private List<File> mCreatedImages = new ArrayList<>();
        private FileFilter mFileFilter = new FileFilter() {
            public boolean accept(File file) {
                String tmp = file.getName().toLowerCase();
                if (tmp.endsWith(".jpg") || tmp.endsWith(".png")) {
                    return true;
                }
                return false;
            }
        };

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            File dir = new File(CREATED_IMAGES_DIR);
            if (dir != null && dir.exists() && dir.isDirectory()) {
                File[] list = dir.listFiles(mFileFilter);
                if (list != null) {
                    for (int i = 0; i < list.length; i++) {
                        File file = list[i];
                        if (file != null && file.exists() && file.isFile()) {
                            mCreatedImages.add(file);
                        }
                    }
                }
            }

            Collections.sort(mCreatedImages, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    return lhs.lastModified() < rhs.lastModified() ? 1 : (lhs.lastModified() == rhs.lastModified() ? 0 : -1);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mImagesAdapter = new ImageListAdapter(mCreatedImages);
            mListView.setAdapter(mImagesAdapter);
            if (mCreatedImages.size() == 0) {
                mEmptyLayout.setVisibility(View.VISIBLE);
                mEmptyTitle.setVisibility(View.VISIBLE);
                mEmptyMsg.setVisibility(View.VISIBLE);
                mEmptyIcon.setVisibility(View.VISIBLE);
            }
            if (mRefreshLayout.isRefreshing()) {
                mRefreshLayout.setRefreshing(false);
            }
            mProgressLayout.setVisibility(View.GONE);
        }
    }

    class ImageListAdapter extends EasyAdapter {
        List<File> mCreatedImages;

        public ImageListAdapter(List<File> images) {
            mCreatedImages = images;
        }

        @Override
        public File getItem(final int position) {
            return mCreatedImages.get(position);
        }

        @Override
        public View newView(final int position, final ViewGroup parent) {
            return getLayoutInflater().inflate(R.layout.item_images, parent, false);
        }

        @Override
        public void bindView(final View view, final int position, final ViewGroup parent) {
            final TextView lDate = V.get(view, R.id.lDate);
            final ImageView lImage = V.get(view, R.id.lImage);
            final LinearLayout moreAction = V.get(view, R.id.action_more);

            moreAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopUpWindow(position, v);
                }
            });
            final File imageFile = getItem(position);
            final long modifyTime = imageFile.lastModified();

            final String createTimeDisplay = DateTimeUtil.getLocaleDateStr4Display(modifyTime);
            lDate.setText(createTimeDisplay);

            Picasso.with(BibleImagesListActivity.this).load(imageFile).into(lImage);
        }

        @Override
        public int getCount() {
            return mCreatedImages.size();
        }

        public void removeFile(int position) {
            mCreatedImages.remove(position);
        }
    }

    private AlertDialog mDeleteDialog;

    private void showPopUpWindow(final int index, View v) {
        final File imageFile = mImagesAdapter.getItem(index);
        int deleteString = R.string.confirm_delete_image;

        mPopupWindow = new PopupWindow(this);

        ArrayList<String> sortList = new ArrayList<String>();
        sortList.add(getString(R.string.menuShare));
        sortList.add(getString(R.string.delete));
        ListView listViewSort = Utility.getPopupWindowList(this, sortList);

        // set on item selected
        final int finalDeleteString = deleteString;
        listViewSort.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                switch (position) {
                    case 0:
                        //share
                        ImageUtil.shareImg(BibleImagesListActivity.this, imageFile.getAbsolutePath());
                        break;
                    case 1:
                        //delete
                        mDeleteDialog = new AlertDialog.Builder(BibleImagesListActivity.this)
                                .setMessage(getString(finalDeleteString))
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        imageFile.delete();
                                        mImagesAdapter.removeFile(index);
                                        mImagesAdapter.notifyDataSetChanged();
                                        EventBus.getDefault().post(new EventVerseOperate(EventVerseOperate.IMAGES));
                                        dialog.dismiss();
                                    }
                                }).create();
                        mDeleteDialog.show();
                        break;
                }
                if (mPopupWindow != null) {
                    mPopupWindow.dismiss();
                }

            }
        });

        // some other visual settings for popup window
        mPopupWindow.setFocusable(true);
        mPopupWindow.setWidth(getResources().getDimensionPixelSize(R.dimen.mark_list_popup_window_width));
        mPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setContentView(listViewSort);

        int windowPos[] = Utility.getPopWindowPosInList(this, v, mRoot);
        int xOff = 10;
        windowPos[0] += xOff;
        mPopupWindow.showAtLocation(v, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
    }

}
