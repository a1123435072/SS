package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.Loader;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.BibleImageActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr.base.BaseGridAdapter;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr.base.BaseGridWithHeaderAndFooterFragment;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.fr.base.CommonListLoader;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.protobuf.ICLProto;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.service.ICLService;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.view.GridViewWithHeaderAndFooter;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.view.ZoomTutorial;
import me.iwf.photopicker.PhotoPicker;


/**
 * Created by yzq on 16/10/28.
 */

public class ImgChooseFragment extends BaseGridWithHeaderAndFooterFragment {

    private ZoomTutorial mZoomTutorial;
    protected ImageListAdapter mListAdapter;

    private LayoutInflater mInflater;
    private static int mPagesCount = 0;
    protected GridViewWithHeaderAndFooter mGridView;
    private RelativeLayout mDetailLayout, mDetailImgLayout, mDetailLayoutBg;
    private ImageView mDetailImage;
    private FrameLayout mRootContainer;
    private ProgressBar mDetailProgressbar;
    private RelativeLayout mActionLayout;
    private ImageView mCancel;
    private ImageView mOk;
    private int mScreenWidth, mGridImgWidth, mAlbumImgWidth;
    private ICLProto.ICL.ICI mCurrentItem;
    private int mAri, mVerseCount;
    private ArrayList<Integer> mLocalImages = new ArrayList<>();
    private boolean mIsImgExpand = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View v = inflater.inflate(R.layout.fragment_img_choose, container, false);
        mRootContainer = (FrameLayout) v.findViewById(R.id.container);
        mGridView = (GridViewWithHeaderAndFooter) v.findViewById(R.id.gridView);
        mDetailLayoutBg = (RelativeLayout) v.findViewById(R.id.detail_layout_bg);
        mDetailLayout = (RelativeLayout) v.findViewById(R.id.detail_layout);
        mDetailImgLayout = (RelativeLayout) v.findViewById(R.id.detail_img_container);
        mDetailImage = (ImageView) v.findViewById(R.id.detail_img);
        mDetailProgressbar = (ProgressBar) v.findViewById(R.id.detail_progressbar);
        mCancel = (ImageView) v.findViewById(R.id.cancel_action);
        mOk = (ImageView) v.findViewById(R.id.ok_action);

        mInflater = inflater;
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handleIntent();

        init();
    }

    private void init() {
        mLocalImages.add(R.drawable.ic_album_entry);
        mLocalImages.add(R.drawable.local_image_1);
        mLocalImages.add(R.drawable.local_image_2);
        mLocalImages.add(R.drawable.local_image_3);
        mLocalImages.add(R.drawable.local_image_4);
        mLocalImages.add(R.drawable.local_image_5);

        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mGridImgWidth = mScreenWidth / 3;
        mAlbumImgWidth = getResources().getDimensionPixelSize(R.dimen.margin_48);
        if (mListAdapter == null) {
            mListAdapter = new ImageListAdapter(getActivity());
        }
        mGridView.setOnScrollListener(mListAdapter);
        List<ICLProto.ICL.ICI> localList = new ArrayList<>();
        for (int i = 0; i < mLocalImages.size(); i++) {
            ICLProto.ICL.ICI ici = new ICLProto.ICL.ICI();
            ici.setThumbnail(String.valueOf(mLocalImages.get(i)));
            ici.setImage(String.valueOf(mLocalImages.get(i)));
            localList.add(ici);
        }
        mListAdapter.addAll(localList);
        mGridView.setAdapter(mListAdapter);


        mDetailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mZoomTutorial != null) {
                    mZoomTutorial.closeZoomAnim();
                }
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mZoomTutorial != null) {
                    mZoomTutorial.closeZoomAnim();
                }
            }
        });

        //detail img size
        RelativeLayout.LayoutParams lay = (RelativeLayout.LayoutParams) mDetailImgLayout.getLayoutParams();
        lay.width = mScreenWidth;
        lay.height = mScreenWidth;
        mDetailImgLayout.setLayoutParams(lay);
    }

    private void handleIntent() {
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            mAri = intent.getIntExtra("ari", 0);
            mVerseCount = intent.getIntExtra("count", 0);
        }
    }

    static class ViewHolder {
        RelativeLayout item;
        ImageView image;
    }

    private class ImageListAdapter extends BaseGridAdapter<ICLProto.ICL.ICI> {

        public ImageListAdapter(Activity context) {
            super(context, new LoadResultListenerImpl());
        }

        @SuppressLint("InlinedApi")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (!isAdded()) {
                return convertView == null ? new View(context) : convertView;
            }

            final ICLProto.ICL.ICI imageItem = getItem(position);
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.image_choose_item_layout, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.item = (RelativeLayout) convertView.findViewById(R.id.item_layout);
                viewHolder.image = (ImageView) convertView.findViewById(R.id.choose_img);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (imageItem.getThumbnail().isEmpty()) {
                return convertView;
            }
            final View finalConvertView = convertView;

            if (!imageItem.getThumbnail().contains("http")) {
                viewHolder.image.setImageResource(Integer.parseInt(imageItem.getThumbnail()));
                viewHolder.item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position == 0) {
                            goAlbum();
                        } else {
                            setViewPagerAndZoom(finalConvertView, imageItem);
                        }
                    }
                });
            } else {
                Picasso.with(context).load(imageItem.getThumbnail())
                        .placeholder(R.drawable.ic_image_choose_default)
                        .error(R.drawable.ic_image_choose_default)
                        .into(viewHolder.image);
                viewHolder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setViewPagerAndZoom(finalConvertView, imageItem);
                    }
                });
            }


            if (convertView == null) {
                convertView = getViewForException();
            }

            return convertView;
        }

        private View getViewForException() {
            return new View(getActivity());
        }

        @Override
        public Loader<List<ICLProto.ICL.ICI>> onCreateLoader(int page, Bundle arg) {
            return getLoader(page, arg);
        }

        @Override
        public void initLoad(int page) {
            if (isAdded()) {
                getLoaderManager().initLoader(page, null, this);
            }
        }

        @Override
        public void restartLoad(int page, Bundle arg) {
            getLoaderManager().restartLoader(page, arg, this);
        }

        @Override
        public int getListPagesCount() {
            return getPagesCount();
        }

        @Override
        public void hideFooterView() {
            if (mListFooterView != null && mListFooterView.getVisibility() == View.VISIBLE) {
                mListFooterView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onRetry() {
        mListAdapter.reload();
    }

    @Override
    protected void onProcessError(int type) {
    }

    protected Loader<List<ICLProto.ICL.ICI>> getLoader(int page, Bundle args) {
        return new ListDataLoader(getActivity(), this, page, args);
    }

    public static class ListDataLoader extends CommonListLoader<ICLProto.ICL.ICI> {
        ImgChooseFragment fragment;
        Context context;
        int page;

        public ListDataLoader(Context ctx, ImgChooseFragment fragment, int page, Bundle args) {
            super(ctx, args);
            this.fragment = fragment;
            this.context = ctx;
            this.page = page;
        }

        @Override
        public List<ICLProto.ICL.ICI> getListData() {
            ICLService service = new ICLService(context);
            ICLProto.ICL imageItemList = service.getData(String.valueOf(page));
            if (imageItemList != null) {
                mPagesCount = imageItemList.getToalPage();
                List<ICLProto.ICL.ICI> list = imageItemList.getImageListList();
                return list;
            } else {
                return null;
            }

        }
    }


    @Override
    protected GridViewWithHeaderAndFooter getGridView() {
        return mGridView;
    }

    /**
     * Get pages total number.
     *
     * @return Integer
     */
    public int getPagesCount() {
        return this.mPagesCount;
    }

    private void goAlbum() {
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setGridColumnCount(3)
                .setShowCamera(false)
                .setPreviewEnabled(false)
                .start(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PhotoPicker.REQUEST_CODE) {
            if (getContext() == null || data == null) {
                return;
            }
            Cursor cursor = null;
            String picturePath = null;
            int orientation = 0;
            picturePath = data.getStringExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            String[] filePathColumn = new String[]{MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.ORIENTATION};
            try {
                cursor = getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, filePathColumn, MediaStore.Images.ImageColumns.DATA + " = '" + picturePath + "'", null, null);
                cursor.moveToFirst();
                orientation = cursor.getInt(cursor.getColumnIndex(filePathColumn[1]));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            startActivity(BibleImageActivity.createIntent(getActivity(), mAri, mVerseCount, BibleImageActivity.TYPE_ALBUM, picturePath));
        }
    }

    public void setViewPagerAndZoom(View v, final ICLProto.ICL.ICI imageItem) {
        if (imageItem.getImage().isEmpty()) return;
        if (imageItem.getThumbnail().contains("http")) {
            Picasso.with(getContext()).load(imageItem.getThumbnail()).into(mDetailImage);
        } else {
            Picasso.with(getContext()).load(Integer.parseInt(imageItem.getThumbnail())).into(mDetailImage);
        }
        mDetailLayoutBg.setVisibility(View.VISIBLE);
        mZoomTutorial = new ZoomTutorial(mRootContainer, mDetailLayout);
        //start animation
        mZoomTutorial.zoomImageFromThumb(v);
        mZoomTutorial.setOnZoomListener(new ZoomTutorial.OnZoomListener() {

            @Override
            public void onThumbed() {
                mDetailLayoutBg.setVisibility(View.GONE);
                mIsImgExpand = false;
            }

            @Override
            public void onExpanded() {
                mCurrentItem = imageItem;
                mIsImgExpand = true;
                Target target = new Target() {

                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        mDetailImage.setImageBitmap(bitmap);
                        mDetailProgressbar.setVisibility(View.GONE);
                        mOk.setClickable(true);
                        mOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(BibleImageActivity.createIntent(getActivity(), mAri, mVerseCount, mCurrentItem.getImage().contains("http") ? BibleImageActivity.TYPE_NETWORK : BibleImageActivity.TYPE_LOCAL, mCurrentItem.getImage()));
                                mDetailLayoutBg.setVisibility(View.GONE);
                            }
                        });
                        mDetailImage.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        mOk.setOnClickListener(null);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        mDetailProgressbar.setVisibility(View.VISIBLE);
                        mOk.setOnClickListener(null);
                    }
                };
                if (imageItem.getImage().contains("http")) {
                    Picasso.with(getContext()).load(imageItem.getImage()).into(target);
                } else {
                    Picasso.with(getContext()).load(Integer.parseInt(imageItem.getImage())).into(target);
                }
            }

        });
    }

    public boolean onKeyPress(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mIsImgExpand && mZoomTutorial != null) {
                mZoomTutorial.closeZoomAnim();
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

}