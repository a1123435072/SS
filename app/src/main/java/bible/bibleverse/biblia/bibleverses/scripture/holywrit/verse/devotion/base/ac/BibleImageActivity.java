package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fw.basemodules.utils.OmAsyncTask;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.S;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac.base.BaseActivity;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter.FontAdapter;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventExitImageChoosePage;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventVerseOperate;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.ImageUtil;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.util.Utility;
import de.greenrobot.event.EventBus;
import yuku.afw.V;
import yuku.ambilwarna.AmbilWarnaDialog;

public class BibleImageActivity extends BaseActivity {
    public static final String TYPE_LOCAL = "local";
    public static final String TYPE_ALBUM = "album";
    public static final String TYPE_NETWORK = "network";

    private View mTextFontBtnLayout, mTextColorBtnLayout, mTextAlignmentBtnLayout, mTextFontIndicator,
            mTextColorIndicator, mTextAlignmentIndicator, mTextFontControlLayout, mTextColorControlLayout,
            mTextAlignmentControlLayout, mImageEditLayout, mControlLayout, mFunctionContentLayout, mSaveOperationLayout,
            mSaveImageLayout, mCancleBtn, mShareBtn;
    private ImageView mImage, mTextFontIcon, mTextColorIcon, mTextAlignmentIcon, mTextAlignmentControlLeftIcon,
            mTextAlignmentControlMiddleIcon, mTextAlignmentControlRightIcon, mWatermarkImage;
    private ImageButton mBtnWhiteColor, mBtnBlackColor, mBtnMoreColor;
    private int mSelectedIndex, mSelectedAlignmentIndex;
    private SeekBar mTextSizeSeekBar, mTextOpacitySeekBar;
    private TextView mTextSize, mTextOpacity, mBibleText;
    private RecyclerView mTypefaceRecyclerView;
    private ProgressBar mSaveImagePb;

    private FontAdapter mFontAdapter;

    private int mSelectedColor, mSelectedOpacity;
    private float mSelectedTextSize;
    private String mSelectedFont;
    private boolean mSaveDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bible_image);
        initView();

        handleIntent();

        initEventListener();

        initToolBar();
    }

    public void initToolBar() {
        Toolbar topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        topToolBar.setNavigationIcon(R.drawable.ic_back_black);
        setTitle(getString(R.string.images));
    }

    private void initView() {

        //image text edit layout
        mImageEditLayout = findViewById(R.id.image_edit_layout);
        mImage = (ImageView) findViewById(R.id.image);
        mBibleText = (TextView) findViewById(R.id.bible_text);
        mWatermarkImage = (ImageView) findViewById(R.id.watermark);

        mControlLayout = findViewById(R.id.control_layout);
        mFunctionContentLayout = findViewById(R.id.function_content_layout);
        mSaveOperationLayout = findViewById(R.id.save_operation_layout);
        mSaveImageLayout = findViewById(R.id.saving_image_layout);
        mSaveImagePb = (ProgressBar) findViewById(R.id.saving_image_progressbar);
        mCancleBtn = findViewById(R.id.cancle_btn);
        mShareBtn = findViewById(R.id.share_btn);


        //text font control
        mTextFontBtnLayout = findViewById(R.id.text_font_btn_layout);
        mTextFontIcon = (ImageView) findViewById(R.id.iv_text_font);
        mTextFontIndicator = findViewById(R.id.iv_text_font_indicator);
        mTextFontControlLayout = findViewById(R.id.text_font_control_layout_id);
        mTextSizeSeekBar = (SeekBar) findViewById(R.id.sbTextSize);
        mTextSize = (TextView) findViewById(R.id.lTextSize);
        mTypefaceRecyclerView = (RecyclerView) findViewById(R.id.cbTypeface);

        //text color control
        mTextColorBtnLayout = findViewById(R.id.text_color_btn_layout);
        mTextColorIcon = (ImageView) findViewById(R.id.iv_text_color);
        mTextColorIndicator = findViewById(R.id.iv_text_color_indicator);
        mTextColorControlLayout = findViewById(R.id.text_color_control_layout_id);
        mBtnWhiteColor = V.get(this, R.id.btnWhiteColor);
        mBtnBlackColor = V.get(this, R.id.btnBlackColor);
        mBtnMoreColor = V.get(this, R.id.btnMoreColor);
        mTextOpacitySeekBar = (SeekBar) findViewById(R.id.sbTextOpacity);
        mTextOpacity = (TextView) findViewById(R.id.text_opacity);

        //text alignment control
        mTextAlignmentBtnLayout = findViewById(R.id.text_alignment_btn_layout);
        mTextAlignmentIcon = (ImageView) findViewById(R.id.iv_text_alignment);
        mTextAlignmentIndicator = findViewById(R.id.iv_text_alignment_indicator);
        mTextAlignmentControlLayout = findViewById(R.id.text_alignment_control_layout_id);
        findViewById(R.id.text_alignment_control_left_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectAlignment(0);
            }
        });
        findViewById(R.id.text_alignment_control_middle_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectAlignment(1);
            }
        });
        findViewById(R.id.text_alignment_control_right_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectAlignment(2);
            }
        });
        mTextAlignmentControlLeftIcon = (ImageView) findViewById(R.id.text_alignment_control_left_icon);
        mTextAlignmentControlMiddleIcon = (ImageView) findViewById(R.id.text_alignment_control_middle_icon);
        mTextAlignmentControlRightIcon = (ImageView) findViewById(R.id.text_alignment_control_right_icon);

        mTextFontBtnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectBtn(0);
            }
        });
        mTextColorBtnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectBtn(1);
            }
        });
        mTextAlignmentBtnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectBtn(2);
            }
        });

        resizeImg();
    }

    private void resizeImg() {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int statusBarHeight = Utility.getStatusBarHeight(this);
        int width = screenWidth - getResources().getDimensionPixelSize(R.dimen.margin_20) * 2;
        int height = screenHeight - getResources().getDimensionPixelSize(R.dimen.bible_img_operation_layout) - getResources().getDimensionPixelSize(R.dimen.actionbar_height) - statusBarHeight;
        int imgWidth = width > height ? height : width;
        ViewGroup.LayoutParams layoutParams = mImageEditLayout.getLayoutParams();
        layoutParams.width = imgWidth;
        layoutParams.height = imgWidth;
        mImageEditLayout.setLayoutParams(layoutParams);
    }

    public static Intent createIntent(Context context, int ari, int count, String type, String url) {
        Intent intent = new Intent(context, BibleImageActivity.class);
        intent.setClass(context, BibleImageActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("url", url);
        intent.putExtra("ari", ari);
        intent.putExtra("count", count);
        return intent;
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        int ari = intent.getIntExtra("ari", 0);
        int verseCount = intent.getIntExtra("count", 0);
        String imgType = intent.getStringExtra("type");
        String url = intent.getStringExtra("url");

        setBibleText(S.getVerseByAriAndCount(ari, verseCount) + "\n" + S.getVerseReference(ari, verseCount));
        try {
            if (imgType != null && imgType.equals(TYPE_LOCAL)) {
                mImage.setImageResource(Integer.parseInt(url));
            } else if (imgType.equals(TYPE_ALBUM)) {
                Picasso.with(this).load("file://" + url).into(mImage);
            } else if (imgType.equals(TYPE_NETWORK)) {
                Picasso.with(this).load(url).into(mImage);
            }
        } catch (Exception e) {
        }

    }

    private void initEventListener() {
        // text size control
        mTextSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSelectedTextSize = progress * 0.5f + 2.f;
                if (mSelectedTextSize < 8) {
                    mSelectedTextSize = 8;
                }
                updateBibleTextView();
                updateBottomBtnUI();
            }
        });

        // text font control
        mFontAdapter = new FontAdapter(this, new FontAdapter.FontSelectedListener() {
            @Override
            public void OnFontSelected(String fontName) {
                mSelectedFont = fontName;
                updateBibleTextView();
            }
        });
        mTypefaceRecyclerView.setAdapter(mFontAdapter);

        // text color
        mBtnWhiteColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBtnWhiteColor.isSelected()) {
                    mSelectedColor = Color.WHITE;
                    updateBibleTextView();
                    updateBottomBtnUI();
                }
            }
        });
        mBtnBlackColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBtnBlackColor.isSelected()) {
                    mSelectedColor = Color.BLACK;
                    updateBibleTextView();
                    updateBottomBtnUI();
                }
            }
        });
        mBtnMoreColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AmbilWarnaDialog(BibleImageActivity.this, mSelectedColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(final AmbilWarnaDialog dialog) {
                    }

                    @Override
                    public void onOk(final AmbilWarnaDialog dialog, final int color) {
                        mSelectedColor = color;
                        updateBibleTextView();
                        updateBottomBtnUI();
                    }
                }).show();
            }
        });

        // text opacity
        mTextOpacitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSelectedOpacity = progress;
                if (mSelectedOpacity < 5) {
                    mSelectedOpacity = 5;
                }
                updateBibleTextView();
                updateBottomBtnUI();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mSelectedTextSize = getResources().getDimensionPixelSize(R.dimen.image_text_defaut_size);
        mSelectedFont = "DEFAULT";
        mSelectedColor = Color.BLACK;
        mSelectedOpacity = 100;
        mSelectedAlignmentIndex = 1;
        updateBibleTextView();
        updateBottomBtnUI();
    }

    private void updateBibleTextView() {
        mBibleText.setTextSize(mSelectedTextSize);
        mBibleText.setTypeface(FontAdapter.getTypefaceByName(mSelectedFont));
        int alpha = (int) (mSelectedOpacity * 2.55);
        mBibleText.setTextColor(
                Color.argb(alpha,
                        Color.red(mSelectedColor),
                        Color.green(mSelectedColor),
                        Color.blue(mSelectedColor)));
        switch (mSelectedAlignmentIndex) {
            case 0:
                mBibleText.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
                break;
            case 1:
                mBibleText.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                break;
            case 2:
                mBibleText.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
                break;
        }
    }

    private void onSelectBtn(int index) {
        if (mSelectedIndex == index) {
            return;
        }
        mSelectedIndex = index;
        updateBottomBtnUI();
    }

    private void updateBottomBtnUI() {
        mTextFontIndicator.setVisibility(mSelectedIndex == 0 ? View.VISIBLE : View.GONE);
        mTextFontControlLayout.setVisibility(mSelectedIndex == 0 ? View.VISIBLE : View.GONE);
        mTextFontIcon.setImageResource(mSelectedIndex == 0 ? R.drawable.ic_image_text_font_selected : R.drawable.ic_image_text_font_normal);

        mTextColorIndicator.setVisibility(mSelectedIndex == 1 ? View.VISIBLE : View.GONE);
        mTextColorControlLayout.setVisibility(mSelectedIndex == 1 ? View.VISIBLE : View.GONE);
        mTextColorIcon.setImageResource(mSelectedIndex == 1 ? R.drawable.ic_image_text_color_selected : R.drawable.ic_image_text_color_normal);

        mTextAlignmentIndicator.setVisibility(mSelectedIndex == 2 ? View.VISIBLE : View.GONE);
        mTextAlignmentControlLayout.setVisibility(mSelectedIndex == 2 ? View.VISIBLE : View.GONE);
        mTextAlignmentIcon.setImageResource(mSelectedIndex == 2 ? R.drawable.ic_image_text_alignment_selected : R.drawable.ic_image_text_alignment_normal);

        mTextSizeSeekBar.setProgress((int) ((mSelectedTextSize - 2.f) * 2));
        mTextSize.setText(String.format(Locale.US, "%.1f", mSelectedTextSize));
        mFontAdapter.setSelectedFontPosition(mFontAdapter.getPositionByName(mSelectedFont));
        mBtnWhiteColor.setSelected(mSelectedColor == Color.WHITE);
        mBtnBlackColor.setSelected(mSelectedColor == Color.BLACK);
        mTextOpacitySeekBar.setProgress(mSelectedOpacity);
        mTextOpacity.setText(String.format(Locale.US, "%d%%", mSelectedOpacity));
        updateAlignmentUI();
    }


    private void onSelectAlignment(int index) {
        if (mSelectedAlignmentIndex == index) {
            return;
        }
        mSelectedAlignmentIndex = index;
        updateAlignmentUI();
        updateBibleTextView();
    }

    private void updateAlignmentUI() {
        mTextAlignmentControlLeftIcon.setImageResource(mSelectedAlignmentIndex == 0 ? R.drawable.ic_image_text_alignment_left_selected : R.drawable.ic_image_text_alignment_left_normal);
        mTextAlignmentControlMiddleIcon.setImageResource(mSelectedAlignmentIndex == 1 ? R.drawable.ic_image_text_alignment_middle_selected : R.drawable.ic_image_text_alignment_middle_normal);
        mTextAlignmentControlRightIcon.setImageResource(mSelectedAlignmentIndex == 2 ? R.drawable.ic_image_text_alignment_right_selected : R.drawable.ic_image_text_alignment_right_normal);
    }

    MenuItem mImgSaveBtn;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_edit, menu);
        mImgSaveBtn = menu.findItem(R.id.save_btn);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_btn:
                saveImage();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveImage() {
        new SaveImageTask().execute();
    }

    private class SaveImageTask extends OmAsyncTask<Void, Void, String> {
        ProgressDialog pg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pg = new ProgressDialog(BibleImageActivity.this);
            pg.setMessage(getString(R.string.loading));
            pg.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            return ImageUtil.saveBitmapFile(BibleImageActivity.this, mImageEditLayout);
        }

        @Override
        protected void onPostExecute(final String path) {
            if (!TextUtils.isEmpty(path)) {

                Toast.makeText(BibleImageActivity.this, "save to:" + path, Toast.LENGTH_SHORT).show();
                if (pg != null && !isFinishing()) {
                    pg.dismiss();
                }
                showShareImageLayout();
                mShareBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageUtil.shareImg(BibleImageActivity.this, path);
                    }
                });
                mSaveDone = true;
                EventBus.getDefault().post(new EventExitImageChoosePage());
                if (mImgSaveBtn != null) {
                    mImgSaveBtn.setVisible(false);
                }
                EventBus.getDefault().post(new EventVerseOperate(EventVerseOperate.IMAGES));
            } else {
                Toast.makeText(BibleImageActivity.this, "save failure", Toast.LENGTH_SHORT).show();
            }
        }

        private void showShareImageLayout() {
            mSaveImagePb.setProgress(100);
            mControlLayout.setVisibility(View.GONE);
            mSaveOperationLayout.setVisibility(View.VISIBLE);
            mSaveImageLayout.setVisibility(View.GONE);
            mShareBtn.setVisibility(View.VISIBLE);
        }
    }

    private void setBibleText(final String text) {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        final int maxWidth = screenWidth - getResources().getDimensionPixelSize(R.dimen.margin_20) * 4;
        mImageEditLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    mImageEditLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mImageEditLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int height = mImageEditLayout.getHeight() - getResources().getDimensionPixelSize(R.dimen.margin_32) * 2;
                int size = Utility.getFitTextsize(BibleImageActivity.this, text, maxWidth, height);
                mSelectedTextSize = size;
                mBibleText.setText(text);
                updateBibleTextView();
                updateBottomBtnUI();
            }
        });
    }

}
