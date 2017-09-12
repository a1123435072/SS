package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.view;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;


/**
 * Created by KevinZhong on 15/8/14.
 */
public class Dialog {

    public Context mContext;

    private LinearLayout layoutContent;
    private TextView txtTitle;
    private TextView txtSubTitle;
    private TextView txtMessage;

    private LinearLayout buttonGroup;
    private TextView btnNeutral;
    private TextView btnPositive;
    private TextView btnNegative;
    private View divider1;
    private View divider2;

    private android.app.Dialog dialog;
    private LayoutInflater mInflater;
    private int screenWidth;

    public Dialog(Context context) {
        this.mContext = context;
        init();
        dialog = new android.app.Dialog(mContext, R.style.CustomDialogTheme);
        onCreateView();
    }

    private void init() {
        mInflater = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        DisplayMetrics metrics = new DisplayMetrics();
        screenWidth = mContext.getResources().getDimensionPixelSize(R.dimen.dialog_min_width);
        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            screenWidth = metrics.widthPixels;
        }
    }

    public Dialog setBackground(int resId) {
        dialog.findViewById(R.id.root).setBackgroundResource(resId);
        return this;
    }

    public Dialog setTitle(int resId) {
        return setTitle(mContext.getString(resId));
    }

    public Dialog setSubTitle(int resId) {
        return setSubTitle(mContext.getString(resId));
    }

    public Dialog setMessage(int resId) {
        return setMessage(mContext.getString(resId));
    }

    public Dialog setTitle(String title) {
        if (txtTitle != null) {
            txtTitle.setText(title);
        }
        return this;
    }

    public Dialog setSubTitle(String subTitle) {
        if (txtSubTitle != null) {
            txtSubTitle.setText(subTitle);
        }
        return this;
    }

    public Dialog setMessage(String message) {
        if (txtMessage != null) {
            txtMessage.setText(message);
        }
        return this;
    }

    public Dialog setNeutralButton(int resId, View.OnClickListener listener) {
        if (listener == null) {
            return this;
        }
        btnNeutral.setText(this.mContext.getString(resId));
        btnNeutral.setOnClickListener(listener);
        return this;
    }

    public Dialog setPositiveButton(int resId, View.OnClickListener listener) {
        if (listener == null) {
            return this;
        }
        btnPositive.setText(this.mContext.getString(resId));
        btnPositive.setOnClickListener(listener);
        return this;
    }

    public Dialog setNegativeButton(int resId, View.OnClickListener listener) {
        if (listener == null) {
            return this;
        }
        btnNegative.setText(this.mContext.getString(resId));
        btnNegative.setOnClickListener(listener);
        return this;
    }

    protected void onCreateView() {
        View root = mInflater.inflate(R.layout.dialog_base, null);
        //content
        layoutContent = (LinearLayout) root.findViewById(R.id.dialog_base_content_area);
        View content = mInflater.inflate(R.layout.dialog_base_content, null);
        txtTitle = (TextView) content.findViewById(R.id.dialog_base_title);
        txtSubTitle = (TextView) content.findViewById(R.id.sub_title);
        txtMessage = (TextView) content.findViewById(R.id.message);
        layoutContent.addView(content);

        //button area
        buttonGroup = (LinearLayout) root.findViewById(R.id.dialog_base_button_layout);
        btnNeutral = (TextView) root.findViewById(R.id.btn_neutral);
        btnPositive = (TextView) root.findViewById(R.id.btn_positive);
        btnNegative = (TextView) root.findViewById(R.id.btn_negative);
        divider1 = root.findViewById(R.id.divider_1);
        divider2 = root.findViewById(R.id.divider_2);

        dialog.setContentView(root);
    }

    public Dialog setView(View v) {
        layoutContent.removeAllViews();
        layoutContent.addView(v);
        return this;
    }

    public Dialog show() {
        if (txtTitle != null && TextUtils.isEmpty(txtTitle.getText())) {
            txtTitle.setVisibility(View.GONE);
        }

        if (txtSubTitle != null && TextUtils.isEmpty(txtSubTitle.getText())) {
            txtSubTitle.setVisibility(View.GONE);
        }

        int btnCount = 3;
        if (btnNegative != null && TextUtils.isEmpty(btnNegative.getText())) {
            btnNegative.setVisibility(View.GONE);
            divider1.setVisibility(View.GONE);
            btnCount--;
        }

        if (btnNeutral != null && TextUtils.isEmpty(btnNeutral.getText())) {
            btnNeutral.setVisibility(View.GONE);
            divider2.setVisibility(View.GONE);
            btnCount--;
        }

        if (btnPositive != null && TextUtils.isEmpty(btnPositive.getText())) {
            btnPositive.setVisibility(View.GONE);
            btnCount--;
        }

        if (btnCount == 0 && buttonGroup != null) {
            buttonGroup.setVisibility(View.GONE);
        }

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(screenWidth * 4 / 5, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        return this;
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }

    public void setCancelableOnTouchOutside(boolean cancelable) {
        dialog.setCanceledOnTouchOutside(cancelable);
    }

    public void setCancelable(boolean cancelable) {
        dialog.setCancelable(cancelable);
    }

    public android.app.Dialog getOriginalDialog() {
        return dialog;
    }
}
