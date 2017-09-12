package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.widget;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.OnSheetDismissedListener;

import java.util.Locale;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.adapter.FontAdapter;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.storage.Prefkey;
import yuku.afw.App;
import yuku.afw.V;
import yuku.afw.storage.Preferences;


public class BottomViewDisplayControl {
    public static final String TAG = BottomViewDisplayControl.class.getSimpleName();

    public interface Listener {
        void onValueChanged();

        void onBottomViewDismiss();
    }

    final Activity activity;
    final BottomSheetLayout parent;
    final Listener listener;
    final View content;

    TextView lTextSize;
    SeekBar sbTextSize;

    RecyclerView cbTypeface;

    ImageButton btnDayMode, btnNightMode;

    FontAdapter typefaceAdapter;
    boolean shown = false;

    public BottomViewDisplayControl(Activity activity, BottomSheetLayout parent, Listener listener) {
        this.activity = activity;
        this.parent = parent;
        this.listener = listener;
        this.content = activity.getLayoutInflater().inflate(R.layout.bottom_sheet_display_control, parent, false);

        this.content.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        }); // prevent click-through

        lTextSize = V.get(content, R.id.lTextSize);
        sbTextSize = V.get(content, R.id.sbTextSize);
        sbTextSize.setOnSeekBarChangeListener(sbTextSize_seekBarChange);

        cbTypeface = V.get(content, R.id.cbTypeface);
        typefaceAdapter = new FontAdapter(activity, new FontAdapter.FontSelectedListener() {
            @Override
            public void OnFontSelected(String fontName) {
                Preferences.setString(Prefkey.jenisHuruf, fontName);
                displayValues();
                BottomViewDisplayControl.this.listener.onValueChanged();
            }
        });
        cbTypeface.setAdapter(typefaceAdapter);

        btnDayMode = V.get(content, R.id.day_mode_btn);
        btnNightMode = V.get(content, R.id.night_mode_btn);
        btnDayMode.setOnClickListener(btnDayModeOnClickListener);
        btnNightMode.setOnClickListener(btnNightModeOnClickListener);

        displayValues();
    }

    public void displayValues() {
        {
            int selectedPosition = typefaceAdapter.getPositionByName(Preferences.getString(Prefkey.jenisHuruf));
            if (selectedPosition >= 0) {
                cbTypeface.scrollToPosition(selectedPosition);
                typefaceAdapter.setSelectedFontPosition(selectedPosition);
                typefaceAdapter.notifyDataSetChanged();
            }
        }

        float textSize = Preferences.getFloat(Prefkey.ukuranHuruf2, (float) App.context.getResources().getInteger(R.integer.pref_ukuranHuruf2_default));
        sbTextSize.setProgress((int) ((textSize - 2.f) * 2));
        displayTextSizeText(textSize);

        boolean isNightMode = Preferences.getBoolean(Prefkey.is_night_mode, false);
        btnNightMode.setSelected(isNightMode);
        btnDayMode.setSelected(isNightMode == false);
    }

    public boolean isShowing() {
        return shown;
    }

    public void show() {
        if (shown) return;

        parent.setIgnoreTouchEventAndBackKeyEvent(false);
        parent.setPeekSheetTranslation(activity.getResources().getDimensionPixelSize(R.dimen.display_control_bottomsheet_peek_height));
        parent.showWithSheetView(content);
        parent.addOnSheetDismissedListener(new OnSheetDismissedListener() {
            @Override
            public void onDismissed(BottomSheetLayout bottomSheetLayout) {
                listener.onBottomViewDismiss();
                parent.removeOnSheetDismissedListener(this);
            }
        });

        shown = true;
    }

    public void hide() {
        if (!shown) return;

        parent.dismissSheet();

        shown = false;
    }

    SeekBar.OnSeekBarChangeListener sbTextSize_seekBarChange = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            float textSize = progress * 0.5f + 2.f;

            Preferences.setFloat(Prefkey.ukuranHuruf2, textSize);
            displayTextSizeText(textSize);
            listener.onValueChanged();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            float textSize = progress * 0.5f + 2.f;
            if (textSize < 8) {
                textSize = 8;
                seekBar.setProgress((int) ((textSize - 2.f) * 2));
            }
            displayTextSizeText(textSize);
        }
    };

    void displayTextSizeText(float textSize) {
        lTextSize.setText(String.format(Locale.US, "%.1f", textSize));
    }

    View.OnClickListener btnDayModeOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (btnDayMode.isSelected()) {
                return;
            } else {
                boolean previousValue = Preferences.getBoolean(Prefkey.is_night_mode, false);
                if (previousValue == false) {
                    return;
                }
                Preferences.setBoolean(Prefkey.is_night_mode, false);
                displayValues();
                listener.onValueChanged();
            }
        }
    };

    View.OnClickListener btnNightModeOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (btnNightMode.isSelected()) {
                return;
            } else {
                boolean previousValue = Preferences.getBoolean(Prefkey.is_night_mode, false);
                if (previousValue == true) {
                    return;
                }
                Preferences.setBoolean(Prefkey.is_night_mode, true);
                displayValues();
                listener.onValueChanged();
            }
        }
    };
}
