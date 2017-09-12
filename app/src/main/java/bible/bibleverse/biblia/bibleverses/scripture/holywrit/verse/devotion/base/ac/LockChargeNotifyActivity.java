package bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.ac;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.R;
import bible.bibleverse.biblia.bibleverses.scripture.holywrit.verse.devotion.base.event.EventExitLockChargeReminder;
import de.greenrobot.event.EventBus;

/**
 * Created by Mr_ZY on 16/11/19.
 */

public class LockChargeNotifyActivity extends Activity {

    private ImageView mCheckbox;
    private LinearLayout mCheckboxLayout;
    private boolean mIsChecked = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(R.layout.dialog_charge_reminder);

        mCheckboxLayout = (LinearLayout) findViewById(R.id.checkbox_layout);
        mCheckbox = (ImageView) findViewById(R.id.checkbox);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(getString(R.string.charge_reminder_dialog_msg_part1) + ", " + getString(R.string.charge_reminder_dialog_msg_part2));
        findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.ok_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsChecked) {
                }
                EventBus.getDefault().post(new EventExitLockChargeReminder());
                finish();
            }
        });
        mCheckboxLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsChecked = !mIsChecked;
                mCheckbox.setImageResource(mIsChecked ? R.drawable.ic_charge_guide_checkbox_selected : R.drawable.ic_charge_guide_checkbox_normal);
            }
        });

    }

    @Override
    public void onBackPressed() {
        return;
    }
}
